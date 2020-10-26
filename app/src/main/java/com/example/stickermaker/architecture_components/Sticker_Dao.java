package com.example.stickermaker.architecture_components;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Sticker_Dao {
    @Insert
    void insert(Sticker sticker);
    @Delete
    void delete(Sticker sticker);

    @Query("SELECT * FROM sticker_table ORDER BY ID DESC")
    LiveData<List<Sticker>> getAllStickers();
}
