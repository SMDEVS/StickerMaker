package com.example.stickermaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class CVManual extends View {
    Context context;
    Bitmap source_bitmap,temporary_bitmap;
    Paint paint;
    Uri photoUri;
    float mX,mY;
    Path clip_path;
    Canvas mCanvas;
    ArrayList<CropModel> arrayList;
    private static final float TOUCH_TOLERANCE = 4;
    boolean isEnabled;

    public CVManual(Context context,Uri photoUri) {
        super(context);
        this.context=context;
        this.photoUri=photoUri;
        init();
    }

    public CVManual(Context context, @Nullable AttributeSet attrs, Uri photoUri) {
        super(context, attrs);
        this.context=context;
        this.photoUri=photoUri;
        init();
    }

    public CVManual(Context context, @Nullable AttributeSet attrs, int defStyleAttr,Uri photoUri) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.photoUri=photoUri;
        init();
    }


    public void init()
    {
        isEnabled=true;
        paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{15.0f,15.0f},0));
        clip_path=new Path();
        arrayList=new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        try {
            source_bitmap= MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
        } catch (IOException e) {
            Log.d("HHH",e.getMessage());
        }
        source_bitmap=Bitmap.createScaledBitmap(source_bitmap,w,h,false);
        temporary_bitmap=Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(temporary_bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawBitmap(source_bitmap, 0, 0, null);
        canvas.drawPath(clip_path, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if(isEnabled)
        {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    clip_path.moveTo(x, y);
                    mX = x;
                    mY = y;
                    arrayList.add(new CropModel(x, y));
                    return true;
                case MotionEvent.ACTION_MOVE:
                    arrayList.add(new CropModel(x, y));
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        clip_path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                        //mCanvas.drawPath(clip_path, paint);
                        invalidate();
                    }
                    mX = x;
                    mY = y;
                    return true;
                case MotionEvent.ACTION_UP:
                    clip_path.lineTo(arrayList.get(0).getX(), arrayList.get(0).getY());
                    //mCanvas.drawPath(clip_path,paint);
                    invalidate();
                    crop();
                    return true;

            }
        }
        return false;

    }
    public void crop()
    {
        clip_path.close();
        clip_path.setFillType(Path.FillType.INVERSE_WINDING);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setAlpha(100);
        invalidate();
        isEnabled=false;
    }
}
