package com.fernandoferreira.chat.persistence.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Fernando on 25/06/2017.
 */

@DatabaseTable
public class Room extends BaseModel{
    @DatabaseField(canBeNull = true)
    private String bssid;
    @DatabaseField(canBeNull = true)
    private Integer name;
}