package com.dya.noor.customImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.dya.noor.R;
import java.util.Random;

public class WaveSeekBar extends View {

    private Paint progressPaint;
    private Paint backgroundPaint;

    private int max = 100;
    private int progress = 0;

    private int[] waveHeights;
    private int barWidth = 8;
    private int barGap = 4;
    private float cornerRadius = 4f;

    private OnWaveSeekBarChangeListener listener;

    public interface OnWaveSeekBarChangeListener {
        void onProgressChanged(WaveSeekBar waveSeekBar, int progress, boolean fromUser);
    }

    public WaveSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public WaveSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int progressColor = Color.parseColor("#FF4081");
        int backgroundColor = Color.parseColor("#E0E0E0");

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveSeekBar);
            try {
                progressColor = a.getColor(R.styleable.WaveSeekBar_waveProgressColor, progressColor);
                backgroundColor = a.getColor(R.styleable.WaveSeekBar_waveBackgroundColor, backgroundColor);
                barWidth = a.getDimensionPixelSize(R.styleable.WaveSeekBar_waveBarWidth, barWidth);
                barGap = a.getDimensionPixelSize(R.styleable.WaveSeekBar_waveBarGap, barGap);
                cornerRadius = a.getDimension(R.styleable.WaveSeekBar_waveCornerRadius, cornerRadius);
            } finally {
                a.recycle();
            }
        }

        progressPaint = new Paint();
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
    }

    // CRITICAL FIX: Move the wave generation out to its own method so we can call it anytime
    private void generateWavebars() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        int totalBarSpace = barWidth + barGap;
        int numBars = w / totalBarSpace;

        if (numBars <= 0) numBars = 1;

        waveHeights = new int[numBars];
        Random random = new Random();
        for (int i = 0; i < numBars; i++) {
            int minHeight = (int) (h * 0.15);
            int maxHeight = (int) (h * 0.90);
            waveHeights[i] = random.nextInt((maxHeight - minHeight) + 1) + minHeight;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        generateWavebars(); // Generate when screen size is ready
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (waveHeights == null || waveHeights.length == 0 || max <= 0) return;

        int viewHeight = getHeight();
        int totalBars = waveHeights.length;

        double progressRatio = (double) progress / (double) max;
        int progressThresholdBar = (int) (progressRatio * totalBars);

        for (int i = 0; i < totalBars; i++) {
            float left = i * (barWidth + barGap);
            float right = left + barWidth;

            float top = (viewHeight - waveHeights[i]) / 2f;
            float bottom = top + waveHeights[i];

            if (i < progressThresholdBar) {
                canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, progressPaint);
            } else {
                canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, backgroundPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float touchX = event.getX();
                float width = getWidth();

                if (touchX < 0) touchX = 0;
                if (touchX > width) touchX = width;

                double ratio = (double) touchX / (double) width;
                int newProgress = (int) (ratio * max);

                setProgress(newProgress, true);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setProgress(int progress) {
        setProgress(progress, false);
    }

    private void setProgress(int progress, boolean fromUser) {
        this.progress = Math.max(0, Math.min(progress, max));
        invalidate();

        if (listener != null) {
            listener.onProgressChanged(this, this.progress, fromUser);
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public void setMax(int max) {
        if (max <= 0) return;
        this.max = max;
        invalidate();
    }

    public void setOnWaveSeekBarChangeListener(OnWaveSeekBarChangeListener listener) {
        this.listener = listener;
    }

    public void setBarWidth(int widthInPixels) {
        this.barWidth = widthInPixels;
        generateWavebars(); // 🔥 RECALCULATE BARS RIGHT AWAY FOR THE NEW DENSITY
        requestLayout();
        invalidate();
    }

    public void setBarGap(int gapInPixels) {
        this.barGap = gapInPixels;
        generateWavebars(); // 🔥 RECALCULATE BARS RIGHT AWAY FOR THE NEW DENSITY
        requestLayout();
        invalidate();
    }

    public void setCornerRadius(float radius) {
        this.cornerRadius = radius;
        invalidate();
    }
}