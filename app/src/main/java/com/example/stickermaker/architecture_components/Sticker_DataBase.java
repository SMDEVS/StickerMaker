package com.example.stickermaker.architecture_components;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Sticker.class,version = 1)
public abstract class Sticker_DataBase extends RoomDatabase {

    private static Sticker_DataBase instance; // it turns the class into singleton class

    public abstract Sticker_Dao sticker_dao();
     public static synchronized Sticker_DataBase getInstance(Context context){
         if(instance==null)
         {
             instance= Room.databaseBuilder(context.getApplicationContext(),Sticker_DataBase.class,"sticker_database").
                     fallbackToDestructiveMigration().build();
         }
         return instance;
     }
}
