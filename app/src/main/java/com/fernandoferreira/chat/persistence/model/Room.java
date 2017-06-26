package com.fernandoferreira.chat.persistence.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Date;

/**
 * Created by Fernando on 25/06/2017.
 */

@DatabaseTable
public class Room extends BaseModel{
    @DatabaseField(canBeNull = true)
    private String bssid;
    @DatabaseField(canBeNull = true)
    private String name;
    @DatabaseField(canBeNull = true)
    private Boolean online = false;

    public Room(){

    }

    public Room(String _bssid, String _name){
        this.bssid = _bssid;
        this.name = _name;
    }
}