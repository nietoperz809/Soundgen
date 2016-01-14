package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class DrawView extends View
{
    Paint paint = new Paint();
    Path path = new Path();
    Paint crosspaint = new Paint();
    Path crosspath = new Path();
    private int _screenX = MyApp.getScreenWidth();
    private int _screenY = MyApp.getScreenHeight();
    private int _x2 = _screenX / 2;
    private int _y2 = _screenY / 2;

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
    }

    public DrawView(Context context)
    {
        super(context);
        setBackgroundColor(Color.RED);
        object = this;
    }


    public void setSamples(final short[] dat)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                setData(dat);
            }
        });
    }

    private Activity getActivity()
    {
        Context context = getContext();
        while (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void setData(short[] dat)
    {
//        MyApp.Msg("setData");
//        path.reset();
//        path.moveTo(0, _y2);
//        path.lineTo(300,100);
//        invalidate();
            path.reset();
            int step = _screenX / 256;
            path.moveTo(0, _y2);
            for (int x = step; x < _screenX; x += step)
            {
                path.lineTo(x, 250+ dat[x]/64);
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