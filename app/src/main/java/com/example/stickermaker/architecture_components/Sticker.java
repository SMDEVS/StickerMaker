package com.example.stickermaker.architecture_components;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sticker_table")
public class Sticker {
    @PrimaryKey(autoGenerate = true)
    private int ID;

    private String sticker_uri;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getSticker_uri() {
        return sticker_uri;
    }

    public void setSticker_uri(String sticker_uri) {
        this.sticker_uri = sticker_uri;
    }
}
