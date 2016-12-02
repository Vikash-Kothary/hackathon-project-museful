package com.vikashkothary.museful;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FeedbackActivity extends AppCompatActivity {

    static int numOfNegative = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        Intent intent = getIntent();
        if (intent.hasExtra("negitiveMsg")) {
            if (intent.getBooleanExtra("negitiveMsg", true)) {
                numOfNegative += 1;
            }
        }
        if (numOfNegative == 7) {
            TextView textView = (TextView) findViewById(R.id.textView_feedback);
            textView.setText(getResources().getString(R.string.help));
        } else if (numOfNegative >= 8) {
            TextView textView = (TextView) findViewById(R.id.textView_feedback);
            textView.setText(getResources().getString(R.string.help2));
        }

        Button call_friend = (Button) findViewById(R.id.button_friend);
        call_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call("07588623602");
            }
        });

        Button call_docter = (Button) findViewById(R.id.button_doctor);
        call_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call("07588623602");
            }
        });
    }

    public void call(String number) {
        String uri = "tel:" + number.trim();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }


}
