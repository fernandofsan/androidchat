package com.fernandoferreira.chat.persistence.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Fernando on 25/06/2017.
 */

@DatabaseTable
public class RoomMessage extends BaseModel{
    @DatabaseField(canBeNull = false)
    private String text;

    @DatabaseField(canBeNull = false)
    private String userName;

    @DatabaseField(canBeNull = false, foreign = true)
    private Room room;

    public String getText(){
        return this.text;
    }

    public void setText(String _text){
        this.text = _text;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String _userName){
        this.userName = _userName;
    }

    public Room getRoom(){
        return this.room;
    }

    public void setRoom(Room _room){
        this.room = _room;
    }
}