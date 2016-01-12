package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle saved)
    {
        super.onCreate(saved);
        MyApp.setFullScreenLandscape(this);
        setContentView(R.layout.activity_main);

        if (saved != null)
        {
            //dispose();
        }
        create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(Menu.NONE, 1, Menu.NONE, "");
        MenuItem m = menu.findItem(1);
        //Spanned text = Html.fromHtml("<b><u><font color='#ff3824'>Kill App</font>");

        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(" Exit ");
        text.setSpan(new BackgroundColorSpan(Color.BLACK), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setSpan(new ForegroundColorSpan(Color.CYAN), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        m.setTitle (text);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case 1:
                MyApp.kill();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Back button disabled
     */
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }


    private synchronized void create()
    {
        int pos = 0;
        RelativeLayout relview = (RelativeLayout) findViewById(R.id.dialog);
        for (int s = 0; s < 4; s++)
        {
            MyApp.panels.add(new SynthPanel(relview, pos, this.getApplicationContext()));
            pos += 260;
        }
    }

    private synchronized void dispose()
    {
        for (SynthPanel s : MyApp.panels)
        {
            s.dispose();
        }
        MyApp.panels.clear();
    }
}
