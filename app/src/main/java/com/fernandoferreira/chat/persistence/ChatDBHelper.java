package com.fernandoferreira.chat.persistence;

import android.content.Context;

import com.fernandoferreira.chat.persistence.model.Room;

/**
 * Created by Fernando on 25/06/2017.
 */

public class ChatDBHelper extends AbstractDBHelper {
    private static final String TABLE_NAME = "mydatabase.db";

    public ChatDBHelper(Context context) {
        super(context, TABLE_NAME);
    }

    @Override
    protected Class<?>[] getTableClassList() {
        return new Class<?>[]{Room.class};
    }
}