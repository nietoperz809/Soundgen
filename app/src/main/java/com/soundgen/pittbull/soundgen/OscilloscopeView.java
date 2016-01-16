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
    public static OscilloscopeView _activeView;
    private final Paint _curvePaint = new Paint();
    private final Path _curvePath = new Path();
    private final Paint _crossPaint = new Paint();
    private final Path _crossPath = new Path();
    // point calibration values
    float _span_vals;
    float _span_disp;
    float _multy;
    private int _screenX;
    private int _screenY;
    private int _x2;
    private int _y2;
    private boolean oneShotFlag;
    private int _stretchFactor = 1;
    private short[] _sampleDataSave;

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
        _activeView = this;

        _curvePaint.setColor(Color.WHITE);
        _curvePaint.setStrokeWidth(5f);
        _curvePaint.setStyle(Paint.Style.STROKE);

        _crossPaint.setColor(Color.YELLOW);
        _crossPaint.setStrokeWidth(3f);
        _crossPaint.setStyle(Paint.Style.STROKE);

        oneShotFlag = false;
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
        _crossPath.moveTo(_x2, 0);
        _crossPath.lineTo(_x2, _screenY);
        _crossPath.moveTo(0, _y2);
        _crossPath.lineTo(_screenX, _y2);
    }

    public void enableSamples()
    {
        oneShotFlag = false;
    }

    public void setSamples(final short[] dat)
    {
        if (oneShotFlag)
        {
            return;
        }
        oneShotFlag = true;

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
                _sampleDataSave = dat;
                viewData(dat);
            }
        });
    }

    private void viewData (short[] dat)
    {
        if (dat == null)
            return;
        _curvePath.reset();
        _curvePath.moveTo(0, _y2 + dat[0] * _multy);
        for (int x = 1; x < _screenX; x++)
        {
            _curvePath.lineTo(x, _y2 + dat[x/_stretchFactor] * _multy);
        }
        invalidate(); // redraw;
    }

    private void drawCross(Canvas c)
    {
        c.drawPath(_crossPath, _crossPaint);
    }

    public void setStretch(int x)
    {
        _stretchFactor = x+1;
       viewData(_sampleDataSave);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawCross(canvas);
        canvas.drawPath(_curvePath, _curvePaint);
    }
}