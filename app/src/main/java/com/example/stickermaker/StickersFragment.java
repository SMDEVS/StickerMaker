package com.example.stickermaker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.stickermaker.architecture_components.Sticker;
import com.example.stickermaker.architecture_components.Sticker_View_Model;

import java.util.ArrayList;
import java.util.List;

public class StickersFragment extends Fragment {
     private Sticker_View_Model sticker_view_model;
     RecyclerView recyclerView;
     Adapter recyclerAdapter;

    public StickersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stickers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView)view.findViewById(R.id.sticker_recycler_view);
        recyclerAdapter=new Adapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        sticker_view_model=new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(Sticker_View_Model.class);

        sticker_view_model.getAllStickers().observe(getViewLifecycleOwner(), new Observer<List<Sticker>>() {
            @Override
            public void onChanged(List<Sticker> stickers) {
               recyclerAdapter.update(stickers);
            }
        });
    }
}
