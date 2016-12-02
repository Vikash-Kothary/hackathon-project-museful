package com.vikashkothary.museful;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitive.textanalytics.model.request.RequestDoc;
import com.microsoft.cognitive.textanalytics.model.request.RequestDocIncludeLanguage;
import com.microsoft.cognitive.textanalytics.model.request.keyphrases_sentiment.TextRequest;
import com.microsoft.cognitive.textanalytics.model.request.language.LanguageRequest;
import com.microsoft.cognitive.textanalytics.model.response.keyphrases.KeyPhrasesResponse;
import com.microsoft.cognitive.textanalytics.model.response.language.LanguageResponse;
import com.microsoft.cognitive.textanalytics.model.response.sentiment.SentimentResponse;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceCall;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceCallback;
import com.microsoft.cognitive.textanalytics.retrofit.ServiceRequestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NewEventActivity extends AppCompatActivity  implements View.OnClickListener {

    private String mSubscriptionKey;

    private static final String TAG = MainActivity.class.getSimpleName();

    // UI
    private TextInputEditText mTextInput;
    private ProgressDialog mProgressDialog;
//    private ImageButton mClearButton;

    // Network request
    private ServiceRequestClient mRequest;
    private RequestDoc mDocument;
    private LanguageRequest mLanguageRequest;       // request for language detection
    private RequestDocIncludeLanguage mDocIncludeLanguage;
    private TextRequest mTextIncludeLanguageRequest;               // request for key phrases and sentiment analysis


    private ServiceCall mLanguageServiceCall;
    private ServiceCallback mLanguageCallback;

    private ServiceCall mKeyPhrasesCall;
    private ServiceCallback mKeyPhrasesCallback;

    private ServiceCall mSentimentCall;
    private ServiceCallback mSentimentCallback;
    private static String keyPhrasesString;
    private static String sentimentString;

    static int numOfNegative = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_event, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(Utils.hasText(this, mTextInput)){
//            getKeyPhrases();
            getSentimentScore();

            if (numOfNegative == 0){
                Entries.addItem(new Entries.Entry(mTextInput.getText().toString()));
                finish();
            }else if (numOfNegative == 7) {
                Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                startActivity(intent);
            }else{
                finish();
            }
            numOfNegative += 1;
//            Log.e("Test", sentimentString);
//            boolean negitiveMsg = Double.parseDouble()<0.5;
//            intent.putExtra("negitiveMsg", negitiveMsg);
//            if(!negitiveMsg){
//                Entries.addItem(new Entries.Entry(mTextInput.getText().toString()));
//            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);


        mTextInput = (TextInputEditText) findViewById(R.id.text_input);
//        mClearButton = (ImageButton) findViewById(R.id.clear_all);

        mTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (mTextInput.getText().toString().isEmpty()) {
//                    mClearButton.setVisibility(View.GONE);
//                } else {
//                    mClearButton.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Get input text string
                String textInputString = mTextInput.getText().toString().trim();

                // Request: text without language
                mDocument = new RequestDoc();
                mDocument.setId("1");
                mDocument.setText(textInputString);
                List<RequestDoc> documents = new ArrayList<>();
                documents.add(mDocument);
                mLanguageRequest = new LanguageRequest(documents);

                // Request: text with language hard-coded to "en" for demo purpose, not production quality
                mDocIncludeLanguage = new RequestDocIncludeLanguage();
                mDocIncludeLanguage.setId("1");
                mDocIncludeLanguage.setLanguage("en");
                mDocIncludeLanguage.setText(textInputString);
                List<RequestDocIncludeLanguage> textDocs = new ArrayList<>();
                textDocs.add(mDocIncludeLanguage);
                mTextIncludeLanguageRequest = new TextRequest(textDocs);

            }
        });

        mSubscriptionKey = Utils.getAPiKey(this); // get API key from either strings.xml or SharedPreferences

        // Set OnClick listeners
//        mClearButton.setOnClickListener(this);

        // Request for network calls
        mRequest = new ServiceRequestClient(mSubscriptionKey);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel all network calls
        if (mLanguageServiceCall != null && !mLanguageServiceCall.isCancelled()) {
            mLanguageServiceCall.cancel();
        }
        if (mKeyPhrasesCall != null && !mKeyPhrasesCall.isCancelled()) {
            mKeyPhrasesCall.cancel();
        }
        if (mSentimentCall != null && !mSentimentCall.isCancelled()) {
            mSentimentCall.cancel();
        }
        // Dismiss dialog
//        dismissProgressDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.clear_all:
//                clearText();
//                break;

        }
    }

    private void clearText() {
        mTextInput.setText("");
    }

//    private void showProgressDialog() {
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        mProgressDialog.setTitle(getString(R.string.progress_bar_title));
//        mProgressDialog.show();
//    }
//
//    private void dismissProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }

    private void getKeyPhrases() {
//        showProgressDialog();

        mKeyPhrasesCallback = new ServiceCallback(mRequest.getRetrofit()) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                KeyPhrasesResponse keyPhrasesResponse = (KeyPhrasesResponse) response.body();
                if (response != null && response.isSuccessful()) {
                    List<String> keyPhrasesStringList = keyPhrasesResponse.getDocuments().get(0).getKeyPhrases();
                     keyPhrasesString = keyPhrasesStringList.get(0);
                    for (int i = 1; i < keyPhrasesStringList.size(); i++) {
                        keyPhrasesString += ", " + keyPhrasesStringList.get(i);
                    }
                }
//                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
//                dismissProgressDialog();
            }
        };

        try {
            mKeyPhrasesCall = mRequest.getKeyPhrasesAsync(mTextIncludeLanguageRequest, mKeyPhrasesCallback);
        } catch (IllegalArgumentException e) {
//            dismissProgressDialog();
            Toast.makeText(NewEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getSentimentScore() {
//        showProgressDialog();

        mSentimentCallback = new ServiceCallback(mRequest.getRetrofit()) {
            @Override
            public void onResponse(Call call, Response response) {
                super.onResponse(call, response);
                SentimentResponse sentimentResponse = (SentimentResponse) response.body();
                if (response != null && response.isSuccessful()) {
                    sentimentString = sentimentResponse.getDocuments().get(0).getScore().toString();
                    Log.e("Test", sentimentString);
                }
//                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
//                dismissProgressDialog();
            }
        };

        try {
            mSentimentCall = mRequest.getSentimentAsync(mTextIncludeLanguageRequest, mSentimentCallback);
        } catch (IllegalArgumentException e) {
//            dismissProgressDialog();
            Toast.makeText(NewEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
