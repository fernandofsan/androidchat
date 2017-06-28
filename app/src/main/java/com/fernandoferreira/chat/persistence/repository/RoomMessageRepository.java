package com.fernandoferreira.chat.persistence.repository;

import android.content.Context;

import com.fernandoferreira.chat.persistence.ChatDBHelper;
import com.fernandoferreira.chat.persistence.model.Room;
import com.fernandoferreira.chat.persistence.model.RoomMessage;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

/**
 * Created by Fernando on 25/06/2017.
 */

public class RoomMessageRepository<RoomMessage> extends BaseOrmLiteRepository<RoomMessage, Long>{
    public RoomMessageRepository(Context context) {
        super(context, ChatDBHelper.class);
    }

    public long countByRoom(String bssid) {
        long count = 0;
        RoomRepository<Room> repoRooms = new RoomRepository<Room>(context);
        QueryBuilder<Room, Long> roomsByBssid = null;
        try {
            roomsByBssid = repoRooms.getQueryBuilder();
            roomsByBssid.where().eq("bssid", bssid);
            QueryBuilder<RoomMessage, Long> messages = getDao().queryBuilder();
            count = messages.join(roomsByBssid).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}