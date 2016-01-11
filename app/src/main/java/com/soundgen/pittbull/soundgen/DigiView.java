package com.soundgen.pittbull.soundgen; /**
 * Created by Administrator on 1/6/2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.TextView;


public class DigiView extends TextView
{
    static private Bitmap[] digits = null;

    final int digit_width = 30; //18;
    final int digit_height = 60; //36;
    int display[];

    private boolean visible = true;

    public DigiView(Context context, int num)
    {
        super(context);
        display = new int[num];

        if (digits == null)
        {
            digits = new Bitmap[12];
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lala);
            bitmap = Bitmap.createScaledBitmap(bitmap, digit_width * 12, digit_height, false);
            for (int s = 0; s < 12; s++)
            {
                digits[s] =
                        Bitmap.createBitmap(bitmap, s * digit_width, 0, digit_width, digit_height);
            }
        }

        setNumber(0);
    }

    public void setNumber(int i)
    {
        String str = "" + i;
        if (str.length() > display.length)
            str = "0";
        while (str.length() < display.length)
        {
            str = "." + str;
        }
        for (int s = 0; s < display.length; s++)
        {
            char c = str.charAt(s);
            if (c >= '0' && c <= '9')
                display[s] = c - '0';
            else if (c == '-')
                display[s] = 11;
            else if (c == '.')
                display[s] = 10;
        }
        invalidate();
    }

    @Override
    public void setEnabled (boolean x)
    {
        visible = x;
        invalidate();
    }

    @Override
    public void onDraw (Canvas c)
    {
        if (!visible)
        {
            return;
        }
        for (int s = 0; s < display.length; s++)
        {
            c.drawBitmap(digits[display[s]], (float) (5.0 + s * digit_width), (float) 8.0, null);
        }
    }
}
