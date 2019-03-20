package com.example.euxcet.arphone_android;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnScaleGestureListener,
        OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private boolean FIRST_TIME = true;
    private float initScale = 1.0f;

    private static final float SCALE_MAX = 4.0f;
    private static final float SCALE_MIN = 1.0f;

    private final float[] matrixValues = new float[9];

    private ScaleGestureDetector scaleGestureDetector = null;
    private final Matrix scaleMatrix = new Matrix();

    private Net net = Net.getInstance();

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);
        this.setOnTouchListener(this);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (getDrawable() == null) return true;
        float scale = getScale();
        float factor = detector.getScaleFactor();
        factor = Math.min(Math.max(factor, initScale * SCALE_MIN / scale), initScale * SCALE_MAX / scale);

        scaleMatrix.postScale(factor, factor, getWidth() / 2, getHeight() / 2);
        setImageMatrix(scaleMatrix);

        net.sendEnlarge(getWidth() / 2, getHeight() / 2);
        net.sendScale(getScale() / initScale);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event);
    }


    public final float getScale() {
        scaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (!FIRST_TIME) return;
        initScale = 1.0f * getWidth() / getDrawableWidth();
        scaleMatrix.postTranslate((getWidth() - getDrawableWidth()) / 2, (getHeight() - getDrawableHeight()) / 2);
        scaleMatrix.postScale(initScale, initScale, getWidth() / 2, getHeight() / 2);
        setImageMatrix(scaleMatrix);
        FIRST_TIME = false;
    }

    private int getDrawableWidth() {
        return getDrawable().getIntrinsicWidth();
    }

    private int getDrawableHeight() {
        return getDrawable().getIntrinsicHeight();
    }
}
