package com.customview.volumecontrol;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class BrightView extends View {
    private static final String TAG = BrightView.class.getSimpleName();
    public static final float DEFAULT_MAX_SPEED = 300; // Assuming this is km/h and you drive a super-car
    private int angleDifference=30;
    private double startAngle=180;


    float  downDY;
    float brightness;
    // Drawing colors
    Context context;


    private float centerX;
    private float centerY;
    private float radius;
    private int regioonIndex;
    private int regionArea;


    public BrightView(Context context) {
        super(context);
        initDrawingTools(context);
    }

    public BrightView(Context context, AttributeSet attrs) {
        super(context, attrs);
  /*      TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.BrightView,
                0, 0);
        try {



        } finally {
            a.recycle();
        }*/
        initDrawingTools(context);
    }

    private void initDrawingTools(Context context) {
        this.context = context;

        try {
            int brightnessMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }


        } catch (Exception e) {
            // do something useful

        }
        getScreenBrightness((Activity) context);

    }



    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {

        // Setting up the oval area in which the arc will be drawn
        if (width > height) {
            radius = height / 2;
        } else {
            radius = width / 2;
        }

        Log.i("control radius...", "" + radius + "  " + centerX + "," + centerY);


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
        centerX = chosenDimension / 2;
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
        //drawScale(canvas);
        //drawLegend(canvas);
        //drawReading(canvas);
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
        // canvas.drawLine(10, centerY, 10, 0, paint);
        //canvas.drawLine(10, centerY, centerX, centerY, paint);
        double angle = 180;
        for (int i = 0; i < 7; i++) {
            if (i <= regioonIndex) {
                paint.setColor(Color.WHITE);
            } else {
                paint.setColor(Color.GRAY);
            }
            double radian = Math.toRadians(angle);

            float startX = dpToPixel(15) + (float) ((radius / 2) * Math.sin(radian));
            float startY = centerY + (float) ((radius / 2) * Math.cos(radian));
            float stopX = dpToPixel(15) + (float) (radius * Math.sin(radian));
            float stopY = centerY + (float) (radius * Math.cos(radian));
            canvas.drawLine(startX, startY, stopX, stopY, paint);
            angle = angle - 30;

        }
        Paint cpaint = new Paint();
        cpaint.setStyle(Paint.Style.STROKE);
        cpaint.setColor(Color.GRAY);
        cpaint.setAntiAlias(true);
        cpaint.setStrokeWidth(5f);
        float circlradius = radius / 4;
        canvas.drawCircle((circlradius + 5), centerY, circlradius, cpaint);
        RectF rectf = new RectF(5f, centerY - circlradius, circlradius * 2, centerY + circlradius);
        Paint semicirclePaint = new Paint();
        semicirclePaint.setStyle(Paint.Style.FILL);
        semicirclePaint.setColor(Color.GRAY);
        semicirclePaint.setAntiAlias(true);
        canvas.drawArc(rectf, 90, 180, false, semicirclePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        int action = motionEvent.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                clickRegion();
                downDY = motionEvent.getY();
                regioonIndex = ((int) downDY) / regionArea;
                setBrightness(downDY);
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
                    setBrightness(downDY);
                    invalidate();

                } else {
                    //  setBrightness(dy);
                    downDY = dy;
                    regioonIndex = ((int) downDY) / regionArea;
                    setBrightness(downDY);
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

        clickRegion();
    }

    public void setBrightness(float brightness) {


        WindowManager.LayoutParams layout = ((Activity) context).getWindow().getAttributes();
        if (brightness > 255) {
            brightness = 255;
        }
        if (brightness < 1) brightness = 1;
        layout.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        ((Activity) context).getWindow().setAttributes(layout);

    }


    public int getScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = android.provider.Settings.System.getInt(
                    resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("brightness", nowBrightnessValue + "");
        brightness = nowBrightnessValue;
        return nowBrightnessValue;
    }


    public void clickRegion() {
        int height = getHeight();
        regionArea = height / 7;
        int currentbrightness = getScreenBrightness((Activity) context);
        regioonIndex = ((int) currentbrightness) / regionArea;


        Log.i("height", height + "");


    }

    public static void setBrightness(Activity activity, int brightness) {

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        activity.getWindow().setAttributes(lp);

    }

    public void setVolume() {


    }
}
