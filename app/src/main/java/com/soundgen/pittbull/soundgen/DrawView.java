package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawView extends View
{
    private final Paint paint = new Paint();
    private final Path path = new Path();
    private final Paint crosspaint = new Paint();
    private final Path crosspath = new Path();
    private final int _screenX = MyApp.getScreenWidth();
    private final int _screenY = MyApp.getScreenHeight();
    private final int _x2 = _screenX / 2;
    private final int _y2 = _screenY / 2;

    // point calibration vals
    float _span_vals = (float) (Wave16.MAX_VALUE - Wave16.MIN_VALUE);
    float _span_disp = _screenY;
    float _multy = _span_disp/_span_vals;

    private boolean oneshot;

    public static DrawView object;

    // Initializer
    {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);

        crosspaint.setColor(Color.YELLOW);
        crosspaint.setStrokeWidth(3f);
        crosspaint.setStyle(Paint.Style.STROKE);

        crosspath.moveTo(_x2, 0);
        crosspath.lineTo(_x2, _screenY);
        crosspath.moveTo(0, _y2);
        crosspath.lineTo(_screenX, _y2);

        oneshot = false;
    }

    public DrawView(Context context)
    {
        super(context);
        setBackgroundColor(Color.RED);
        object = this;
    }


    public void setSamples(final short[] dat)
    {
        if (oneshot)
        {
            return;
        }
        oneshot = true;

        Activity act = MyApp.getActivity(this);
        if (act == null)
        {
            return;
        }
        act.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                setData(dat);
            }
        });
    }

    private void setData(short[] dat)
    {
        //float step = (float)_screenX / dat.length;
        path.reset();
        path.moveTo (0, _y2 + dat[0] * _multy);
        for (int x = 1; x < _screenX; x++)
        {
            path.lineTo(x, _y2 + dat[x] * _multy);
        }
        invalidate(); // redraw;
    }

    private void drawCross(Canvas c)
    {
        c.drawPath(crosspath, crosspaint);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawCross(canvas);
        canvas.drawPath(path, paint);
    }
}