package com.soundgen.pittbull.soundgen;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Administrator on 1/7/2016.
 */
public class MyMenu
{
    Menu parent;

    public MyMenu(Menu m)
    {
        parent = m;
    }

    public void addItems(String... items)
    {
        for (String s : items)
        {
            parent.add(Menu.NONE, s.hashCode(), Menu.NONE, s);
        }
    }

    public void setColorsForItem (int id, int back, int fore)
    {
        MenuItem m = parent.findItem(id);
        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(m.toString());
        text.setSpan(new BackgroundColorSpan(back), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(fore), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        m.setTitle(text);
    }

    public void setColorsForItem (String s, int back, int fore)
    {
        int hash = s.hashCode();
        this.setColorsForItem(hash, back, fore);
    }

}
