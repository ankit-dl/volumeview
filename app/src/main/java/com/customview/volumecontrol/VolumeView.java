package com.customview.volumecontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VolumeView extends View {
    private static final String TAG = VolumeView.class.getSimpleName();
    public static final float DEFAULT_MAX_SPEED = 300; // Assuming this is km/h and you drive a super-car

    // Speedometer internal state
    private float mMaxSpeed;
    private float mCurrentSpeed;
    float downDX, downDY;
    float volmeValue;
    // Drawing colors
    Context context;

    private float centerX;
    private float centerY;
    private float radius;
    private int regioonIndex;
    private int regionArea;


    public VolumeView(Context context) {
        super(context);
        initDrawingTools(context);
    }

    public VolumeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    /*    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BrightView,
                0, 0);
        try {
            mMaxSpeed = a.getFloat(R.styleable.BrightView_maxSpeed, DEFAULT_MAX_SPEED);
            mCurrentSpeed = a.getFloat(R.styleable.BrightView_currentSpeed, 0);


        } finally {
            a.recycle();
        }*/
        initDrawingTools(context);
    }

    private void initDrawingTools(Context context) {
        this.context = context;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        float currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        setVolume(currentVolume);
        regioonIndex= (int) (currentVolume/2);


    }


    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {

        // Setting up the oval area in which the arc will be drawn
        if (width > height) {
            radius = height / 2;
        } else {
            radius = width / 2;
        }

        Log.i("control radius...bright", "" + radius + "  " + centerX + "," + centerY);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		Log.d(TAG, "Width spec: " + MeasureSpec.toString(widthMeasureSpec));
//		Log.d(TAG, "Height spec: " + MeasureSpec.toString(heightMeasureSpec));

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int chosenWidth = chooseDimension(widthMode, widthSize);
        int chosenHeight = chooseDimension(heightMode, heightSize);

        int chosenDimension = Math.min(chosenWidth, chosenHeight);
        centerX = chosenDimension;
        centerY = chosenDimension / 2;
        setMeasuredDimension(chosenDimension, chosenDimension);
    }

    private int chooseDimension(int mode, int size) {
        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            return size;
        } else { // (mode == MeasureSpec.UNSPECIFIED)
            return getPreferredSize();
        }
    }

    // in case there is no size specified
    private int getPreferredSize() {
        return 300;
    }

    @Override
    public void onDraw(Canvas canvas) {

        drawScaleBackground(canvas);

    }

    /**
     * Draws the segments in their OFF state
     *
     * @param canvas
     */
    private void drawScaleBackground(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10f);
        paint.setAntiAlias(true);

        double angle = 180;
        for (int i = 0; i < 7; i++) {
            if (i <= regioonIndex) {
                paint.setColor(Color.WHITE);
            } else {
                paint.setColor(Color.GRAY);
            }
            double radian = Math.toRadians(angle);

            float startX = (float) ((radius / 2) * Math.sin(radian)) + (getWidth() - dpToPixel(15));
            float startY = centerY + (float) ((radius / 2) * Math.cos(radian));
            float stopX = (float) (radius * Math.sin(radian)) + (getWidth() - dpToPixel(15));
            float stopY = centerY + (float) (radius * Math.cos(radian));
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            angle = angle + 30;

        }
        Paint iconPaint = new Paint();
        iconPaint.setColor(Color.GRAY);
        iconPaint.setStyle(Paint.Style.STROKE);
        iconPaint.setStrokeWidth(5f);
        iconPaint.setAntiAlias(true);
        Path path=new Path();
        path.moveTo(getWidth()-20,centerY-50);
        path.lineTo(getWidth()-20,centerY+50);
        path.lineTo(getWidth()-50,centerY+20);
        path.lineTo(getWidth()-80,centerY+20);
        path.lineTo(getWidth()-80,centerY-10);
        path.lineTo(getWidth()-50,centerY-10);
        path.close();



        canvas.drawPath(path,iconPaint);



    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        int action = motionEvent.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {

                downDY = motionEvent.getY();
                regioonIndex = ((int) downDY) / regionArea;
                setVolume(regioonIndex);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_MOVE: {


                final float dy = motionEvent.getY();
                Log.i("move", dy + "");
                if (dy >= downDY) {
                /* Move down */

                    //setBrightness(dy);
                    downDY = dy;
                    regioonIndex = ((int) downDY) / regionArea;
                    setVolume(regioonIndex);
                    invalidate();

                } else {
                    //  setBrightness(dy);
                    downDY = dy;
                    regioonIndex = ((int) downDY) / regionArea;
                    setVolume(regioonIndex);
                    invalidate();
                }
                /* Move up */
                downDY = dy;
            }


            break;
        }
       /* boolean eventConsumed = gesture.onTouchEvent(event);
        if (eventConsumed) return true;
        else return false;*/
        return true;
    }

    private int dpToPixel(int dp) {
        float density = getResources().getDisplayMetrics().density;
        float px = dp * density;
        return (int) px;


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        regionArea = getHeight() / 7;

    }


    public void setVolume(float value) {
        try {

            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

            int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            Log.i("volume", currentVolume + " / " + maxVolume);
            float percent = value/10f;
            int seventyVolume = (int) (maxVolume * percent);
            audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
