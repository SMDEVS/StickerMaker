package com.example.stickermaker.architecture_components;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class Sticker_View_Model extends AndroidViewModel {

    private Sticker_Repository repository;
    private LiveData<List<Sticker>> allStickers;
    public Sticker_View_Model(@NonNull Application application) {
        super(application);
        repository=new Sticker_Repository(application);
        allStickers=repository.getAllStickers();
    }
    public void insert(Sticker sticker)
    {
        repository.insert(sticker);
    }
    public void delete(Sticker sticker)
    {
        repository.delete(sticker);
    }

    public LiveData<List<Sticker>> getAllStickers() {
        return allStickers;
    }
}
