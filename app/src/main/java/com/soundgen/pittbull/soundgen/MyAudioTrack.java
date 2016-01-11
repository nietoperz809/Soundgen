package com.soundgen.pittbull.soundgen;

/**
 *
 */

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.SeekBar;

public class MyAudioTrack extends Thread
{
    AudioTrack my;
    final int _samplerate = 44000;
    final int chunksize = 10000;
    SeekBar _seek;
    SeekBar _seek2;
    boolean running = true;
    Wave16 _sweep = new Wave16(0, 0);
    int sweepmin;
    int sweepmax;

    WaveForm currentWaveForm = WaveForm.OFF;

    public MyAudioTrack(SeekBar seek, SeekBar seek2)
    {
        _seek = seek;
        _seek2 = seek2;
        this.start();
    }

    synchronized public void setWaveForm(WaveForm i)
    {
        currentWaveForm = i;
    }

    synchronized public void dispose()
    {
        running = false;
        try
        {
            this.join();
        }
        catch (InterruptedException e)
        {
            MyApp.Msg("disposal fail");
        }
        my.stop();
    }

    private Wave16 makeSweep(WaveForm swptype, int startval, int min, int max)
    {
        if (_sweep.waveType != swptype || min != sweepmin || max != sweepmax)
        {
            if (swptype == WaveForm.SweepSIN)
                _sweep = Wave16.sweepSine(_samplerate, sweepmin, sweepmax, 10d);
            else if (swptype == WaveForm.SweepTRI)
                _sweep = Wave16.sweepTriangle(_samplerate, sweepmin, sweepmax, 10d);
            else if (swptype == WaveForm.SweepSQR)
                _sweep = Wave16.sweepSquare(_samplerate, sweepmin, sweepmax, 10d);
            else if (swptype == WaveForm.SweepPUL)
                _sweep = Wave16.sweepPulse(_samplerate, sweepmin, sweepmax, 10d);
            sweepmin = min;
            sweepmax = max;
        }
        else
        {
            int from = startval % _sweep.data.length;
            int to = from + chunksize;
            if (to >= _sweep.data.length)
                to = _sweep.data.length - 1;
            return Wave16.extractSamples(_sweep, from, to);
        }
        return null;
    }

    @Override
    public void run()
    {
        //this.setPriority(MAX_PRIORITY);
        my = new AudioTrack(AudioManager.STREAM_MUSIC, _samplerate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 4 * chunksize, AudioTrack.MODE_STREAM);
        my.play();

        Wave16 wv = null;
        int startval = 0;

        while (running)
        {
            if (currentWaveForm == WaveForm.OFF)
            {
                try
                {
                    sleep(100);
                }
                catch (InterruptedException e)
                {
                    return;
                }
                continue;
            }

            int freq = _seek.getProgress();
            int freq2 = _seek2.getProgress();

            switch (currentWaveForm)
            {
                case Sawtooth:
                    wv = Wave16.curveSawTooth(_samplerate, chunksize, freq, startval);
                    break;

                case Sine:
                    wv = Wave16.curveSine(_samplerate, chunksize, freq, startval);
                    break;

                case Square:
                    wv = Wave16.curveRect(_samplerate, chunksize, freq, startval);
                    break;

                case Pulse:
                    wv = Wave16.curvePulse(_samplerate, chunksize, freq, startval);
                    break;

                case Triangle:
                    wv = Wave16.curveTriangle(_samplerate, chunksize, freq, startval);
                    break;

                case SweepSIN:
                case SweepTRI:
                case SweepSQR:
                case SweepPUL:
                    //case SweepSAW:
                    wv = makeSweep(currentWaveForm, startval, freq, freq2);
                    break;

            }
            startval += chunksize;

            if (wv != null)
            {
                my.write(wv.toShortArray(), 0, wv.data.length);
            }
        }
    }
}
