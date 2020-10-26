package com.example.stickermaker.architecture_components;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Sticker_Repository {
    private Sticker_Dao sticker_dao;
    private LiveData<List<Sticker>> allStickers;
    public Sticker_Repository(Application application){
        Sticker_DataBase database=Sticker_DataBase.getInstance(application);
        sticker_dao=database.sticker_dao();
        allStickers=sticker_dao.getAllStickers();
    }
   public void insert(Sticker sticker){
       new insertStickerAsyncTask(sticker_dao).execute(sticker);
   }
   public void delete(Sticker sticker){
       new deleteStickerAsyncTask(sticker_dao).execute(sticker);
   }

    public LiveData<List<Sticker>> getAllStickers() {
        return allStickers;
    }

    private static class insertStickerAsyncTask extends AsyncTask<Sticker,Void,Void>{
     private Sticker_Dao sticker_dao;
     private insertStickerAsyncTask(Sticker_Dao sticker_dao)
     {
         this.sticker_dao=sticker_dao;
     }
        @Override
        protected Void doInBackground(Sticker... stickers) {
         sticker_dao.insert(stickers[0]);
            return null;
        }
    }
    private static class deleteStickerAsyncTask extends AsyncTask<Sticker,Void,Void>{
        private Sticker_Dao sticker_dao;
        private deleteStickerAsyncTask(Sticker_Dao sticker_dao)
        {
            this.sticker_dao=sticker_dao;
        }
        @Override
        protected Void doInBackground(Sticker... stickers) {
            sticker_dao.delete(stickers[0]);
            return null;
        }
    }
}
