package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

public class SecondActivity extends Activity implements SeekBar.OnSeekBarChangeListener
{
    SeekBar _seek;

    public SecondActivity()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        MyApp.setFullScreenLandscape(this);

        setContentView(R.layout.activity_second);
        _seek = (SeekBar) this.findViewById(R.id.seekBar);
        _seek.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (seekBar.equals(_seek))
        {
            //MyApp.Msg("seek "+progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }


}
