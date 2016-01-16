package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class OscilloscopeView extends View
{
    public static OscilloscopeView object;
    private final Paint paint = new Paint();
    private final Path path = new Path();
    private final Paint crosspaint = new Paint();
    private final Path crosspath = new Path();
    // point calibration values
    float _span_vals;
    float _span_disp;
    float _multy;
    private int _screenX;
    private int _screenY;
    private int _x2;
    private int _y2;
    private boolean oneshot;
    private int _stretch = 1;

    public OscilloscopeView(Context context)
    {
        this(context, null);
    }

    /**
     * Constructor for XML designer
     *
     * @param context
     * @param attrs
     */
    public OscilloscopeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setBackgroundColor(Color.RED);
        object = this;

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);

        crosspaint.setColor(Color.YELLOW);
        crosspaint.setStrokeWidth(3f);
        crosspaint.setStyle(Paint.Style.STROKE);

        oneshot = false;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld)
    {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        _screenX = xNew;
        _screenY = yNew;
        _x2 = _screenX / 2;
        _y2 = _screenY / 2;

        // point calibration vals
        _span_vals = (float) (Wave16.MAX_VALUE - Wave16.MIN_VALUE);
        _span_disp = _screenY;
        _multy = _span_disp / _span_vals;

        // set cross
        crosspath.moveTo(_x2, 0);
        crosspath.lineTo(_x2, _screenY);
        crosspath.moveTo(0, _y2);
        crosspath.lineTo(_screenX, _y2);
    }

    private short[] _save;
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
                _save = dat;
                setData(dat);
            }
        });
    }

    private void setData(short[] dat)
    {
        path.reset();
        path.moveTo (0, _y2 + dat[0] * _multy);
        for (int x = 1; x < _screenX; x+= _stretch)
        {
            path.lineTo(x, _y2 + dat[x] * _multy);
        }
        invalidate(); // redraw;
    }

    private void drawCross(Canvas c)
    {
        c.drawPath(crosspath, crosspaint);
    }

    public void setStretch(int x)
    {
        _stretch = x+1;
       setData (_save);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawCross(canvas);
        canvas.drawPath(path, paint);
    }
}