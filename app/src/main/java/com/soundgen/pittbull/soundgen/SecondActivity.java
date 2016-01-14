package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class SecondActivity extends Activity
{
    public SecondActivity()
    {
        //MyApp.Msg("secondview ctor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //MyApp.Msg("secondview create" + savedInstanceState);
        MyApp.setFullScreenLandscape(this);

        DrawView drawView = new DrawView(this);
        setContentView(drawView);
        //setContentView(R.layout.activity_second);
    }

}
