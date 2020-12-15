package com.example.stickermaker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stickermaker.architecture_components.Sticker;
import com.example.stickermaker.architecture_components.Sticker_View_Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class StickersFragment extends Fragment {
     private Sticker_View_Model sticker_view_model;
     private static final int PERMISSION_REQUEST_CODE=1;
     private static final int PICK_PHOTO_CODE=100;
     RecyclerView recyclerView;
     Adapter recyclerAdapter;
     FloatingActionButton button;

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
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=(RecyclerView)view.findViewById(R.id.sticker_recycler_view);
        button=(FloatingActionButton)view.findViewById(R.id.addSticker);
        recyclerAdapter=new Adapter();
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        sticker_view_model = new ViewModelProvider(this).get(Sticker_View_Model.class);
        //sticker_view_model=ViewModelProviders.of(this).get(Sticker_View_Model.class);
        //sticker_view_model=new ViewModelProvider(this,
        // new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(Sticker_View_Model.class);

        sticker_view_model.getAllStickers().observe(getViewLifecycleOwner(), new Observer<List<Sticker>>() {
            @Override
            public void onChanged(List<Sticker> stickers) {
               recyclerAdapter.update(stickers);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    Galley_To_Crop();
                }else
                {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                }
            }
        });
    }
    public void Galley_To_Crop()
    {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null)
        {
            startActivityForResult(intent,PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    Galley_To_Crop();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null && requestCode==PICK_PHOTO_CODE)
        {
            Uri photoUri=data.getData();
            Navigate_to_Crop_Fragment(photoUri);
        }
    }
    public void Navigate_to_Crop_Fragment(Uri uri)
    {
        StickersFragmentDirections.ActionStickersFragmentToCropFragment action=
                StickersFragmentDirections.actionStickersFragmentToCropFragment(uri.toString());
        NavHostFragment navHostFragment= (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController=navHostFragment.getNavController();
        navController.navigate(action);
    }
}
