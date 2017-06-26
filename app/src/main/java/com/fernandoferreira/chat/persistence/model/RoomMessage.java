package com.fernandoferreira.chat.persistence.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Fernando on 25/06/2017.
 */

@DatabaseTable
public class RoomMessage extends BaseModel{
    @DatabaseField(canBeNull = true)
    private String text;

    @DatabaseField(canBeNull = true)
    private String userName;
}