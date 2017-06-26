package com.fernandoferreira.chat.persistence.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by gilson.maciel on 27/04/2015.
 */
public abstract class BaseModel {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true, canBeNull = false)
    protected Long id;

    @DatabaseField(canBeNull = true)
    protected Date createdOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedOn () { return createdOn; }

    public void setCreatedOn(Date createdOn){
        this.createdOn = createdOn;
    }

    public BaseModel(){
        this.createdOn = new java.util.Date();
    }
}
