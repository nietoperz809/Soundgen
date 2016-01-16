package com.soundgen.pittbull.soundgen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class SecondActivity extends Activity implements SeekBar.OnSeekBarChangeListener,
        Button.OnClickListener
{
    SeekBar _seek;
    Button _button;
    OscilloscopeView _osc;

    public SecondActivity()
    {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        MyApp.setFullScreenLandscape(this);

        setContentView(R.layout.activity_second);
        _osc = (OscilloscopeView)this.findViewById(R.id.view);
        _seek = (SeekBar) this.findViewById(R.id.seekBar);
        _seek.setOnSeekBarChangeListener(this);
        _button = (Button) this.findViewById(R.id.button);
        _button.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (seekBar.equals(_seek))
        {
            _osc.setStretch(progress);
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


    @Override
    public void onClick(View v)
    {
        _osc.enableSamples();
    }
}
