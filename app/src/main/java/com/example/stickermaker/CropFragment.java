package com.example.stickermaker;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CropFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
 ImageView imageView;
 Bitmap source_bitmap;
 Bitmap temporary_bitmap;
 Bitmap altered_bitmap;
 Canvas canvas;
 Paint paint;
 Path clip_path;
    float mX;
    float mY;
    float small_x,small_y,large_x,large_y;
 Button circle_crop;
 Button manual_crop;
 Button rect_crop;
 Button auto_crop;
 Button okay;
 ProgressDialog progressDialog;
 ArrayList<CropModel> arrayList;
    int c_y;
    int c_x;
    int y_dist;
    int x_dist;
    private static final float TOUCH_TOLERANCE = 4;
    Region r_rect,r_circle;
    Rect r;
    public CropFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String myArgs=CropFragmentArgs.fromBundle(getArguments()).getMyArgs();
        arrayList=new ArrayList<>();
        imageView=view.findViewById(R.id.image_view);
        progressDialog=new ProgressDialog(getContext());
        circle_crop=view.findViewById(R.id.circle_crop);
        manual_crop=view.findViewById(R.id.manual_crop);
        rect_crop=view.findViewById(R.id.rectangle_crop);
        auto_crop=view.findViewById(R.id.auto_crop);
        okay=view.findViewById(R.id.ok_button);

        Glide.with(getContext()).asBitmap().load(Uri.parse(myArgs)).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                source_bitmap=resource;
                imageView.setImageBitmap(source_bitmap);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        imageView.setOnTouchListener(this);
        manual_crop.setOnClickListener(this);
        rect_crop.setOnClickListener(this);
        okay.setOnClickListener(this);
    }




    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
       if(manual_crop.getVisibility()==View.VISIBLE)
       {
           float x=motionEvent.getX();
           float y=motionEvent.getY();
           switch ((motionEvent.getAction()))
           {
               case MotionEvent.ACTION_DOWN:
                   clip_path.moveTo(x, y);
                   mX=x;mY=y;
                   arrayList.add(new CropModel(x,y));
                   break;
               case MotionEvent.ACTION_MOVE:
                   arrayList.add(new CropModel(x, y));
                   float dx = Math.abs(x - mX);
                   float dy = Math.abs(y - mY);
                   if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                       clip_path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                       canvas.drawPath(clip_path, paint);
                       imageView.invalidate();
                   }
                   mX = x;
                   mY = y;
                   break;
                case MotionEvent.ACTION_UP:
                       clip_path.lineTo(arrayList.get(0).getX(),arrayList.get(0).getY());
                       canvas.drawPath(clip_path,paint);
                       imageView.invalidate();
                       crop();
           }
           return true;
       }else if(rect_crop.getVisibility()==View.VISIBLE)
       {
           float x=motionEvent.getX();
           float y=motionEvent.getY();
           switch(motionEvent.getAction())
           {
               case MotionEvent.ACTION_DOWN:
                  mX=x;
                  mY=y;
                  break;
               case MotionEvent.ACTION_MOVE:
                   if(r_rect.contains((int)mX,(int)mY))
                   {
                       c_x=(int)x;
                       c_y=(int)y;
                       draw();
                       mX=x;
                       mY=y;
                   }
                   break;
           }
           return true;
       }
       else
           return false;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.manual_crop:
                okay.setVisibility(View.VISIBLE);okay.setEnabled(true);
                auto_crop.setVisibility(View.INVISIBLE);auto_crop.setEnabled(false);
                manual_crop.setEnabled(false);
                rect_crop.setVisibility(View.INVISIBLE);rect_crop.setEnabled(false);
                circle_crop.setVisibility(View.INVISIBLE);circle_crop.setEnabled(false);

                initCanvas();
                clip_path = new Path();
                break;
            case R.id.rectangle_crop:
                okay.setVisibility(View.VISIBLE);okay.setEnabled(true);
                auto_crop.setVisibility(View.INVISIBLE);auto_crop.setEnabled(false);
                manual_crop.setVisibility(View.INVISIBLE);manual_crop.setEnabled(false);
                rect_crop.setEnabled(false);
                circle_crop.setVisibility(View.INVISIBLE);circle_crop.setEnabled(false);
                init_canvas_rect();
                break;
            case R.id.ok_button:
                if(manual_crop.getVisibility()==View.VISIBLE)
                save();
                break;
        }

    }
    public void initCanvas()
    {
        altered_bitmap=Bitmap.createBitmap(source_bitmap.getWidth(),source_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas=new Canvas(altered_bitmap);
        paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{15.0f,15.0f},0));
        canvas.drawBitmap(source_bitmap,0,0,null);
        imageView.setImageBitmap(altered_bitmap);
    }public void crop()
    {
        clip_path.close();
        clip_path.setFillType(Path.FillType.INVERSE_WINDING);
        temporary_bitmap = altered_bitmap;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setAlpha(100);
        canvas.drawPath(clip_path, paint);
        canvas.drawBitmap(temporary_bitmap, 0, 0, paint);
    }
    public void save()
    {
        if(clip_path != null) {
            final int color = 0xff424242;
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPath(clip_path, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            canvas.drawBitmap(altered_bitmap, 0, 0, paint);
            altered_bitmap = Bitmap.createBitmap(altered_bitmap);

        }else
            {
            altered_bitmap = source_bitmap;
            }
        progressDialog.show();

        Thread mThread = new Thread() {
            @Override
            public void run() {

                Bitmap bitmap = altered_bitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
                byte[] byteArray = stream.toByteArray();
                progressDialog.dismiss();
                Bundle bundle=new Bundle();
                bundle.putByteArray("bitmap",byteArray);
                NavHostFragment navHostFragment= (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                NavController navController=navHostFragment.getNavController();
               navController.navigate(R.id.action_cropFragment_to_editorFragment,bundle);
            }
        };
        mThread.start();

    }
    public void init_canvas_rect()
    {
        altered_bitmap=Bitmap.createBitmap(source_bitmap.getWidth(),source_bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas=new Canvas(altered_bitmap);
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(10);
        paint.setARGB(60,0,30,0);
        canvas.drawBitmap(source_bitmap,0,0,null);
        imageView.setImageBitmap(altered_bitmap);
         c_y=altered_bitmap.getHeight()/2;
         c_x=altered_bitmap.getWidth()/2;
         y_dist=altered_bitmap.getHeight()/4;
         x_dist=altered_bitmap.getWidth()/4;
         r=new Rect();
        r_rect=new Region();
        draw();
    }
    public void draw()
    {
        int a0_x,a0_y,a4_x,a4_y;
        a0_x= Math.max((c_x - x_dist), 0);
        a0_y= Math.max((c_y - y_dist), 0);
        a4_x= Math.min((c_x + x_dist), altered_bitmap.getWidth());
        a4_y= Math.min((c_y + y_dist), altered_bitmap.getHeight());
        int radius=Math.min(y_dist,x_dist)/8;
        r.left=a0_x;
        r.top=a0_y;
        r.right=a4_x;
        r.bottom=a4_y;
        r_rect.set(r);
        //r_circle=new Region(a4_x-radius,a4_y-radius,a4_x+radius,a4_y+radius);
        canvas.drawRect(r,paint);
        //canvas.drawCircle(a4_x,a4_y,radius,paint);
        imageView.invalidate();

    }
}
