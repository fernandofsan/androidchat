package com.fernandoferreira.chat.entity;

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
public class RoomContent {

    /**
     * An array of sample (dummy) items.
     */
    public List<RoomItem> ITEMS = new ArrayList<RoomItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, RoomItem> ITEM_MAP = new HashMap<String, RoomItem>();

    private static final int COUNT = 25;

//    private static void addItem(RoomItem item) {
//        ITEMS.add(item);
//        ITEM_MAP.put(item.bssid, item);
//    }

    public RoomContent(){
        ITEMS = new ArrayList<RoomItem>();
    }

    public RoomItem createRooomItem(String bssid, String name) {
        return new RoomItem(bssid, name);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class RoomItem {
        public String bssid;
        public String name;

        public RoomItem(String bssid, String name) {
            this.bssid = bssid;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
