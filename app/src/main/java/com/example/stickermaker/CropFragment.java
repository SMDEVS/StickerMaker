package com.example.stickermaker;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CropFragment extends Fragment implements View.OnClickListener {

 Button circle_crop;
 Button manual_crop;
 Button rect_crop;
 Button auto_crop;
 ProgressDialog progressDialog;
 ImageView imageView;
 Bitmap image_bitmap,temporary_bitmap;
 boolean state;
 Uri photoUri;
    CVManual cvManual;
    CVRect cvRect;
    RelativeLayout rL;

    public CropFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        state=false;
        String myArgs=CropFragmentArgs.fromBundle(getArguments()).getMyArgs();
        photoUri=Uri.parse(myArgs);
        progressDialog=new ProgressDialog(getContext());
        circle_crop=view.findViewById(R.id.circle_crop);
        manual_crop=view.findViewById(R.id.manual_crop);
        rect_crop=view.findViewById(R.id.rectangle_crop);
        auto_crop=view.findViewById(R.id.auto_crop);
        imageView=view.findViewById(R.id.image_view);
        rL=view.findViewById(R.id.custom_view_container);
        try{
            image_bitmap= MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
        }catch(IOException e)
        {
            Log.d("HHH",e.getMessage());
        }
        imageView.setImageBitmap(image_bitmap);

        circle_crop.setOnClickListener(this);
        auto_crop.setOnClickListener(this);
        manual_crop.setOnClickListener(this);
        rect_crop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        switch(view.getId())
        {
            case R.id.manual_crop:
                auto_crop.setVisibility(View.INVISIBLE);auto_crop.setEnabled(false);
                manual_crop.setEnabled(false);
                rect_crop.setVisibility(View.INVISIBLE);rect_crop.setEnabled(false);
                circle_crop.setVisibility(View.INVISIBLE);circle_crop.setEnabled(false);

                imageView.setVisibility(View.GONE);
                cvManual=new CVManual(getContext(),photoUri);
                cvManual.setLayoutParams(params);
                rL.addView(cvManual);


                state=true;
                getActivity().invalidateOptionsMenu();
                break;
            case R.id.rectangle_crop:
                auto_crop.setVisibility(View.INVISIBLE);auto_crop.setEnabled(false);
                manual_crop.setVisibility(View.INVISIBLE);manual_crop.setEnabled(false);
                rect_crop.setEnabled(false);
                circle_crop.setVisibility(View.INVISIBLE);circle_crop.setEnabled(false);

                imageView.setVisibility(View.GONE);
                cvRect=new CVRect(getContext(),photoUri);
                cvRect.setLayoutParams(params);
                rL.addView(cvRect);

                state=true;
                getActivity().invalidateOptionsMenu();
                break;
        }

    }
    public void crop()
    {
        if(manual_crop.getVisibility()==View.VISIBLE)
        {
            temporary_bitmap=cvManual.temporary_bitmap;
            Canvas mCanvas=new Canvas(temporary_bitmap);
            Path clip_path=cvManual.clip_path;
            clip_path.setFillType(Path.FillType.WINDING);
            Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            mCanvas.drawPath(clip_path,paint);
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mCanvas.drawBitmap(cvManual.source_bitmap,0,0,p);
            progressDialog.show();

            Log.e("HHH","thread started");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            temporary_bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            progressDialog.dismiss();
            Bundle bundle=new Bundle();
            bundle.putByteArray("bitmap",byteArray);
            NavHostFragment navHostFragment= (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController=navHostFragment.getNavController();
            navController.navigate(R.id.action_cropFragment_to_editorFragment,bundle);
        }
        else if(rect_crop.getVisibility()==View.VISIBLE)
        {
            temporary_bitmap=cvRect.temporary_bitmap;
            Canvas mCanvas=new Canvas(temporary_bitmap);
            Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            mCanvas.drawRect(cvRect.left,cvRect.top,cvRect.right,cvRect.bottom,paint);
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mCanvas.drawBitmap(cvRect.source_bitmap,0,0,p);
            progressDialog.show();

            Log.e("HHH","thread started");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            temporary_bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            progressDialog.dismiss();
            Bundle bundle=new Bundle();
            bundle.putByteArray("bitmap",byteArray);
            NavHostFragment navHostFragment= (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController=navHostFragment.getNavController();
            navController.navigate(R.id.action_cropFragment_to_editorFragment,bundle);

        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_for_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.next_menu)
        {
            if(manual_crop.getVisibility()==View.VISIBLE || rect_crop.getVisibility()==View.VISIBLE)
            {
              crop();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.getItem(0).setEnabled(state);
    }
}
