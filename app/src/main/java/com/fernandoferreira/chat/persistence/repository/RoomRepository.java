package com.fernandoferreira.chat.persistence.repository;

import android.content.Context;

import com.fernandoferreira.chat.persistence.ChatDBHelper;

/**
 * Created by Fernando on 25/06/2017.
 */

public class RoomRepository<Room> extends BaseOrmLiteRepository<Room, Long>{
    public RoomRepository(Context context) {
        super(context, ChatDBHelper.class);
    }
}