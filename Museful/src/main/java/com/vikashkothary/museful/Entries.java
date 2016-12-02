package com.vikashkothary.museful;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Entries {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Entry> ITEMS = new ArrayList<Entry>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Entry> ITEM_MAP = new HashMap<String, Entry>();

    // TODO: remove
    private static final int COUNT = 4;
    static {
        addItem(new Entry("You're awesome"));
    }

    public static void addItem(Entry item) {
        item.id = Integer.toString(ITEM_MAP.size());
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static Entry createDummyEntries(int position) {
        return new Entry(String.valueOf(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static class Entry {
        public String id;
        public final String datetime;
        public final String location;
        public final String message;
        public final Emoji emotion;

        public Entry(String message){
            this.id = id;
            this.datetime = null;
            this.location = null;
            this.message = message;
            this.emotion = Emoji.NONE;
        }

        public Entry(String datetime, String location, String message, Emoji emotion) {
            this.datetime = datetime;
            this.location = location;
            this.message = message;
            this.emotion = emotion;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    public static enum Emoji {
        NONE, HAPPY, SAD
    }
}
