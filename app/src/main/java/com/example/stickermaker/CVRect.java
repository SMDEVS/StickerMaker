package com.example.stickermaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;

public class CVRect extends View {
    Context context;
    Bitmap source_bitmap,temporary_bitmap;
    Paint borderPaint,cornerPaint;
    Uri photoUri;
    Canvas mCanvas;
    boolean isEnabled;
    float left,top,right,bottom;
    RectF rectF;
    float touchOffset_x,touchOffset_y;
    private float cornerThickness,borderThickness,cornerLength,cornerRadius;
    private int AREA_TOUCHED,MIN_WIDTH_AND_HEIGHT,MAX_WIDTH,MAX_HEIGHT;
    float width,height;

    public CVRect(Context context,Uri photoUri) {
        super(context);
        this.context=context;
        this.photoUri=photoUri;
        init();
    }

    public CVRect(Context context, @Nullable AttributeSet attrs, Uri photoUri) {
        super(context, attrs);
        this.context=context;
        this.photoUri=photoUri;
        init();
    }

    public CVRect(Context context, @Nullable AttributeSet attrs, int defStyleAttr,Uri photoUri) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.photoUri=photoUri;
        init();
    }


    public void init()
    {
        isEnabled=true;
        rectF=new RectF();
        touchOffset_x=0;
        touchOffset_y=0;
        AREA_TOUCHED=-1;
        cornerThickness=context.getResources().getDimension(R.dimen.corner_thickness);
        borderThickness=context.getResources().getDimension(R.dimen.border_thickness);
        cornerLength=context.getResources().getDimension(R.dimen.corner_length);
        cornerRadius=context.getResources().getDimension(R.dimen.target_radius);
        borderPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        cornerPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        cornerPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderThickness);
        cornerPaint.setStrokeWidth(cornerThickness);
        borderPaint.setColor(context.getResources().getColor(R.color.border));
        cornerPaint.setColor(context.getResources().getColor(R.color.corner));


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

        left=w/2-w/4;
        top=h/2-h/4;
        right=w/2+w/4;
        bottom=h/2+h/4;
        MIN_WIDTH_AND_HEIGHT=60;
        MAX_WIDTH=w;
        MAX_HEIGHT=h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(source_bitmap, 0, 0, null);
        drawBoundary(canvas);
        drawCorners(canvas);
    }
    public void drawBoundary(Canvas canvas)
    {
        canvas.drawRect(left,top,right,bottom,borderPaint);
    }
    public void drawCorners(Canvas canvas)
    {
        final float lateralOffset=(cornerThickness-borderThickness)/2f;
        final float startOffset=cornerThickness-borderThickness/2f;
        //top left corner: left side
        canvas.drawLine(left-lateralOffset,top-startOffset,left-lateralOffset,top+cornerLength,cornerPaint);
        //top left corner: top side
        canvas.drawLine(left-startOffset,top-lateralOffset,left+cornerLength,top-lateralOffset,cornerPaint);

        // Top-right corner: right side
        canvas.drawLine(right + lateralOffset, top - startOffset, right + lateralOffset, top + cornerLength, cornerPaint);
        // Top-right corner: top side
        canvas.drawLine(right + startOffset, top - lateralOffset, right - cornerLength, top - lateralOffset, cornerPaint);

        // Bottom-left corner: left side
        canvas.drawLine(left - lateralOffset, bottom + startOffset, left - lateralOffset, bottom - cornerLength, cornerPaint);
        // Bottom-left corner: bottom side
        canvas.drawLine(left - startOffset, bottom + lateralOffset, left + cornerLength, bottom + lateralOffset, cornerPaint);

        // Bottom-right corner: right side
        canvas.drawLine(right + lateralOffset, bottom + startOffset, right + lateralOffset, bottom - cornerLength, cornerPaint);
        // Bottom-right corner: bottom side
        canvas.drawLine(right + startOffset, bottom + lateralOffset, right - cornerLength, bottom + lateralOffset, cornerPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        if(isEnabled)
        {

            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    touchDown(x,y);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    touchMove(x,y);
                    return true;
                case MotionEvent.ACTION_UP:
                    AREA_TOUCHED=-1;
                    return true;
            }
        }
        return false;
    }
    public void touchDown(float x,float y)
    {
        float closestDistance=Float.POSITIVE_INFINITY;
        float distance[]=new float[4];
        distance[0]=calculateDistanceBetweenPoints(x,y,left,top);
        distance[1]=calculateDistanceBetweenPoints(x,y,right,top);
        distance[2]=calculateDistanceBetweenPoints(x,y,left,bottom);
        distance[3]=calculateDistanceBetweenPoints(x,y,right,bottom);
        int i_min=0;
        for(int i=0;i<4;i++)
        {
            if(distance[i]<closestDistance) {
                closestDistance = distance[i];
                i_min = i;
            }
        }
        if(closestDistance<=cornerRadius)
            AREA_TOUCHED=i_min;
        else if(x >= left && x <= right && y >= top && y <= bottom)
            AREA_TOUCHED=4;
        calculateOffset(x,y);
    }
    public void touchMove(float x,float y)
    {
        switch(AREA_TOUCHED)
        {
            case 0: left=x-touchOffset_x;
                top=y-touchOffset_y;
                if(left<0)
                    left=0;
                if(top<0)
                    top=0;
                if(right-left<MIN_WIDTH_AND_HEIGHT)
                    left=right-MIN_WIDTH_AND_HEIGHT;
                if(bottom-top<MIN_WIDTH_AND_HEIGHT)
                    top=bottom-MIN_WIDTH_AND_HEIGHT;
                invalidate();
                break;
            case 1: right=x-touchOffset_x;
                top=y-touchOffset_y;
                if(right>MAX_WIDTH)
                    right=MAX_WIDTH;
                if(top<0)
                    top=0;
                if(right-left<MIN_WIDTH_AND_HEIGHT)
                    right=left+MIN_WIDTH_AND_HEIGHT;
                if(bottom-top<MIN_WIDTH_AND_HEIGHT)
                    top=bottom-MIN_WIDTH_AND_HEIGHT;
                invalidate();
                break;
            case 2: left=x-touchOffset_x;
                bottom=y-touchOffset_y;
                if(left<0)
                    left=0;
                if(bottom>MAX_HEIGHT)
                    bottom=MAX_HEIGHT;
                if(right-left<MIN_WIDTH_AND_HEIGHT)
                    left=right-MIN_WIDTH_AND_HEIGHT;
                if(bottom-top<MIN_WIDTH_AND_HEIGHT)
                    bottom=top+MIN_WIDTH_AND_HEIGHT;
                invalidate();
                break;
            case 3: right=x-touchOffset_x;
                bottom=y-touchOffset_y;
                if(right>MAX_WIDTH)
                    right=MAX_WIDTH;
                if(bottom>MAX_HEIGHT)
                    bottom=MAX_HEIGHT;
                if(right-left<MIN_WIDTH_AND_HEIGHT)
                    right=left+MIN_WIDTH_AND_HEIGHT;
                if(bottom-top<MIN_WIDTH_AND_HEIGHT)
                    bottom=top+MIN_WIDTH_AND_HEIGHT;
                invalidate();
                break;
            case 4: float centre_x=x-touchOffset_x;
                float centre_y=y-touchOffset_y;

                left = centre_x - width / 2;
                right = centre_x + width / 2;
                top = centre_y - height / 2;
                bottom = centre_y + height / 2;

                //if(left>0 && right<MAX_WIDTH && top>0 && bottom<MAX_HEIGHT)
                invalidate();
                break;
        }
    }
    public float calculateDistanceBetweenPoints(float x1,float y1,float x2,float y2)
    {
        float s1=x1-x2;
        float s2=y1-y2;
        return (float)Math.sqrt(s1*s1+s2*s2);
    }
    public void calculateOffset(float x,float y)
    {
        switch(AREA_TOUCHED)
        {
            case 0:touchOffset_x=x-left;
                touchOffset_y=y-top;
                break;
            case 1:touchOffset_x=x-right;
                touchOffset_y=y-top;
                break;
            case 2:touchOffset_x=x-left;
                touchOffset_y=y-bottom;
                break;
            case 3: touchOffset_x=x-right;
                touchOffset_y=y-bottom;
                break;
            case 4: float centre_x=(left+right)/2;
                float centre_y=(top+bottom)/2;
                touchOffset_x=x-centre_x;
                touchOffset_y=y-centre_y;
                width=right-left;
                height=bottom-top;
                break;
        }
    }
}
