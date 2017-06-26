package com.fernandoferreira.chat.persistence.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Fernando on 25/06/2017.
 */

@DatabaseTable
public class Room extends BaseModel{
    @DatabaseField(canBeNull = false)
    private String bssid;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(persisted = false)
    private Boolean online = false;

    //lasted messages
    @DatabaseField(canBeNull = true)
    private String latestMessageText;

    @DatabaseField(canBeNull = true)
    private String latestMessageUserName;

    @DatabaseField(canBeNull = true)
    private Date latestMessageDate;

    public Room(){

    }

    public Room(String _bssid, String _name){
        this.bssid = _bssid;
        this.name = _name;
    }

    public String getName(){
        return this.name;
    }

    public String getBssid(){
        return this.bssid;
    }

    public void setLatestMessage(String text, String userName, Date date){
        this.latestMessageText = text;
        this.latestMessageUserName = userName;
        this.latestMessageDate = date;
    }

    public String getLatestMessage(){
        return this.latestMessageText;
    }

}