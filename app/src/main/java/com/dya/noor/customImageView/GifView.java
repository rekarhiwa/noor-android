package com.dya.noor.customImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.dya.noor.R;

import java.io.InputStream;


public class GifView extends View {
    private Movie movie;
    private long movieStart;
    private float cornerRadius = 0f;
    private Path clipPath = new Path();
    private RectF viewRect = new RectF();

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifView);
        cornerRadius = a.getDimension(R.styleable.GifView_cornerRadius, 0f);

        int gifResId = a.getResourceId(R.styleable.GifView_gifSrc, -1);
        a.recycle();

        if (gifResId != -1) {
            InputStream is = context.getResources().openRawResource(gifResId);
            movie = Movie.decodeStream(is);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (movie == null) return;

        viewRect.set(0, 0, getWidth(), getHeight());
        clipPath.reset();
        clipPath.addRoundRect(viewRect, cornerRadius, cornerRadius, Path.Direction.CW);
        canvas.clipPath(clipPath);

        long now = SystemClock.uptimeMillis();
        if (movieStart == 0) movieStart = now;

        int relTime = (int)((now - movieStart) % movie.duration());
        movie.setTime(relTime);

        float scaleX = (float) getWidth() / movie.width();
        float scaleY = (float) getHeight() / movie.height();
        canvas.scale(scaleX, scaleY);
        movie.draw(canvas, 0, 0);
        invalidate();
    }
}