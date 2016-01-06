package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends /*AppCompat*/Activity
{
    /**
     * Sets this App to Fullscreen Landscape
     * This Class MUST be derived from Activity or it will CRASH!
     */
    private void setFullScreenLandscape()
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onCreate(Bundle saved)
    {
        super.onCreate(saved);
        setFullScreenLandscape();
        setContentView(R.layout.activity_main);

        if (saved != null)
        {
            dispose();
        }
        create();    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.game_menu, menu);
        MyApp.kill();
        return true;
    }

    synchronized void create()
    {
        int pos = 0;
        RelativeLayout relview = (RelativeLayout) findViewById(R.id.dialog);
        for (int s=0; s<4; s++)
        {
            MyApp.panels.add (new SynthPanel (relview, pos, this.getApplicationContext()));
            pos += 260;
        }
    }

    synchronized void dispose()
    {
        for (SynthPanel s : MyApp.panels)
        {
            s.dispose();
        }
        MyApp.panels.clear();
    }
}
