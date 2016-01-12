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
    private static WindowManager wm;
    private static final DisplayMetrics dm = new DisplayMetrics();
    private final DigiView freqView;
    private final DigiView freqView2;
    private final Spinner scaleSelect;
    private final Spinner waveFormSelect;
    private final SeekBar freqSlider;
    private final SeekBar freqSlider2;
    private final SeekBar sweepInterval;
    private final MyAudioTrack audioTrack;

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

        // Frequency View
        params = new RelativeLayout.LayoutParams(160, 80);
        params.leftMargin = 200;
        params.topMargin = 10;
        freqView2 = new DigiView(context, 5);
        freqView2.setBackgroundColor(Color.BLACK);
        this.addView(freqView2, params);

        // Scale switcher
        List strings = Arrays.asList("100", "200", "500", "1000", "2000", "5000", "10000");
        params = new RelativeLayout.LayoutParams(240, 80);
        params.leftMargin = 420;
        params.topMargin = 10;
        scaleSelect = new Spinner(context);
        scaleSelect.setBackgroundColor(Color.GRAY);
        ArrayAdapter<String> dataAdapter =  new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, strings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scaleSelect.setAdapter(dataAdapter);
        scaleSelect.setOnItemSelectedListener(this);
        this.addView(scaleSelect, params);

        // Waveform switcher
        strings = Arrays.asList(WaveForm.values());
        params = new RelativeLayout.LayoutParams(240, 80);
        params.leftMargin = 670;
        params.topMargin = 10;
        waveFormSelect = new Spinner(context);
        waveFormSelect.setBackgroundColor(Color.GRAY);
        dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, strings);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        waveFormSelect.setAdapter(dataAdapter);
        waveFormSelect.setOnItemSelectedListener(this);
        this.addView(waveFormSelect, params);

        // Sweep interval
        params = new RelativeLayout.LayoutParams(1000, 100); // width, height
        params.leftMargin = 900;
        params.topMargin = 10;
        sweepInterval = new SeekBar(context);
        sweepInterval.setMax(100);
        this.addView(sweepInterval, params);

        // SeekBar
        params = new RelativeLayout.LayoutParams(dm.widthPixels - 15, 100);
        params.leftMargin = 5;
        params.topMargin = 90;
        freqSlider = new SeekBar(context);
        freqSlider.setOnSeekBarChangeListener(this);
        this.addView(freqSlider, params);

        // SeekBar2
        params = new RelativeLayout.LayoutParams(dm.widthPixels - 15, 100);
        params.leftMargin = 5;
        params.topMargin = 160;
        freqSlider2 = new SeekBar(context);
        freqSlider2.setOnSeekBarChangeListener(this);
        this.addView(freqSlider2, params);

        // The track
        audioTrack = new MyAudioTrack (freqSlider, freqSlider2, sweepInterval);
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
            WaveForm wv = WaveForm.values()[position];
            String str = wv.name();
            if (str.startsWith("Sweep"))
            {
                freqSlider2.setEnabled(true);
                freqView2.setEnabled(true);
                sweepInterval.setEnabled(true);
            }
            else
            {
                freqSlider2.setEnabled(false);
                freqView2.setEnabled(false);
                sweepInterval.setEnabled(false);
            }
            audioTrack.setWaveForm(wv);
        }
        else if (parent.equals(scaleSelect))
        {
            int max = Integer.parseInt((String) ((TextView) view).getText());
            freqSlider.setMax(max);
            freqSlider2.setMax(max);
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
        else if (seekBar.equals(freqSlider2))
        {
            freqView2.setNumber(progress);
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
