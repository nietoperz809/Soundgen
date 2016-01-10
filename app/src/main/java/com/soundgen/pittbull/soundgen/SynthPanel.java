package com.soundgen.pittbull.soundgen;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class SynthPanel extends RelativeLayout implements OnItemSelectedListener, SeekBar.OnSeekBarChangeListener
{
    static WindowManager wm;
    static final DisplayMetrics dm = new DisplayMetrics();
    DigiView freqView;
    Spinner scaleSelect;
    Spinner waveFormSelect;
    Spinner sweepSelect;
    SeekBar freqSlider;
    MyAudioTrack audioTrack;

    public SynthPanel(RelativeLayout parent, int posy, Context context)
    {
        super(context);
        if (wm == null)
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        // Position main panel
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels - 2, 250);
        params.leftMargin = 1;
        params.topMargin = posy;
        parent.addView(this, params);
        this.setBackgroundColor(Color.YELLOW);
        // Frequency View
        params = new RelativeLayout.LayoutParams(160, 80);
        params.leftMargin = 10;
        params.topMargin = 10;
        freqView = new DigiView(context, 5);
        freqView.setBackgroundColor(Color.BLACK);
        this.addView(freqView, params);
        // Scale switcher
        List strings = Arrays.asList(new String[]{"100", "200", "500", "1000", "2000", "5000", "10000"});
        params = new RelativeLayout.LayoutParams(240, 80);
        params.leftMargin = 220;
        params.topMargin = 10;
        scaleSelect = new Spinner(context);
        scaleSelect.setBackgroundColor(Color.GRAY);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scaleSelect.setAdapter(dataAdapter);
        scaleSelect.setOnItemSelectedListener(this);
        this.addView(scaleSelect, params);
        // Waveform switcher
        strings = Arrays.asList(MyAudioTrack.WaveForm.values());
        params = new RelativeLayout.LayoutParams(240, 80);
        params.leftMargin = 470;
        params.topMargin = 10;
        waveFormSelect = new Spinner(context);
        waveFormSelect.setBackgroundColor(Color.GRAY);
        dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waveFormSelect.setAdapter(dataAdapter);
        waveFormSelect.setOnItemSelectedListener(this);
        this.addView(waveFormSelect, params);
        // Sweep switcher
        strings = Arrays.asList(new String[]{"OFF", "0-100", "0-200", "0-500", "0-1000", "0-2000", "0-5000", "0-10000"});
        params = new RelativeLayout.LayoutParams(240, 80);
        params.leftMargin = 750;
        params.topMargin = 10;
        sweepSelect = new Spinner(context);
        sweepSelect.setBackgroundColor(Color.GRAY);
        dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sweepSelect.setAdapter(dataAdapter);
        sweepSelect.setOnItemSelectedListener(this);
        this.addView(sweepSelect, params);
        // SeekBar
        params = new RelativeLayout.LayoutParams(dm.widthPixels - 15, 100);
        params.leftMargin = 5;
        params.topMargin = 100;
        freqSlider = new SeekBar(context);
        freqSlider.setOnSeekBarChangeListener(this);
        this.addView(freqSlider, params);
        // The track
        audioTrack = new MyAudioTrack(freqSlider);
    }

    public void dispose()
    {
        audioTrack.dispose();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (parent.equals(waveFormSelect))
        {
            audioTrack.setWaveForm(MyAudioTrack.WaveForm.values()[position]);
        }
        else if (parent.equals(scaleSelect))
        {
            freqSlider.setMax(Integer.parseInt((String) ((TextView) view).getText()));
        }
        ((TextView) view).setTextColor(Color.WHITE);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (seekBar.equals(freqSlider))
        {
            freqView.setNumber(progress);
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub

    }

}
