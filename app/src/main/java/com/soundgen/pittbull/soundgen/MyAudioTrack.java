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
    /**
     * The player oject
     */
    private AudioTrack my;
    /**
     * fixed sample rate
     */
    private final int _samplerate = 44000;
    /**
     * fixed buffer size
     */
    private final int chunksize = 10000;
    /**
     * scrollbar for single or first sweep frequency
     */
    private final SeekBar _seek;

    private final SeekBar _seekSweep;
    /**
     * scrollbar for second sweep frequency
     */
    private final SeekBar _seek2;
    /**
     * Thread running flag
     */
    private boolean running = true;
    /**
     * Sweep object
     */
    private Wave16 _sweep = new Wave16(0, 0);
    /**
     * current first sweep frequency
     */
    private int sweepmin;
    /**
     * current second sweep frequency
     */
    private int sweepmax;

    private int sweeptime;
    /**
     * current Waveform
     */
    private WaveForm currentWaveForm = WaveForm.OFF;

    public MyAudioTrack(SeekBar seek, SeekBar seek2, SeekBar sweeptime)
    {
        _seek = seek;
        _seek2 = seek2;
        _seekSweep = sweeptime;
        this.start();
    }

    /**
     * Setter for Waveform
     *
     * @param i
     */
    synchronized public void setWaveForm(WaveForm i)
    {
        currentWaveForm = i;
    }

    /**
     * End this thread
     */
    synchronized public void dispose()
    {
        MyApp.Msg("dispose");
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

    /**
     * Make new sweep or get a sweep chunk
     *
     * @param swptype  the sweep type
     * @param startval start offset of next chunk
     * @param min      sweep freq 1
     * @param max      sweep freq 2
     * @return sweep chunk or NULL
     */
    private Wave16 makeSweep(WaveForm swptype, int startval, int min, int max, int time)
    {
        if (_sweep.waveType != swptype || min != sweepmin || max != sweepmax || time != sweeptime)
        {
            double tl = ((double) time) / 10 + 0.1;
            //MyApp.Msg(""+tl+":"+min+":"+max);
            if (swptype == WaveForm.SweepSIN)
                _sweep = Wave16.sweepSine(_samplerate, sweepmin, sweepmax, tl);
            else if (swptype == WaveForm.SweepTRI)
                _sweep = Wave16.sweepTriangle(_samplerate, sweepmin, sweepmax, tl);
            else if (swptype == WaveForm.SweepSQR)
                _sweep = Wave16.sweepSquare(_samplerate, sweepmin, sweepmax, tl);
            else if (swptype == WaveForm.SweepPUL)
                _sweep = Wave16.sweepPulse(_samplerate, sweepmin, sweepmax, tl);
            sweepmin = min;
            sweepmax = max;
            sweeptime = time;
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

    /**
     * Thread function to run the generator
     */
    @Override
    public void run()
    {
        // Init audio track and run
        my = new AudioTrack(AudioManager.STREAM_MUSIC, _samplerate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 4 * chunksize, AudioTrack.MODE_STREAM);
        my.play();

        Wave16 wv = null;
        int startval = 0;

        // run in a loop
        while (running)
        {
            // if Wave is OFF just give away CPU cycles
            if (currentWaveForm == WaveForm.OFF)
            {
                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                    MyApp.Msg("wait interrupted");
                }
                continue;
            }

            // Delay
            try
            {
                sleep(100);
            }
            catch (InterruptedException e)
            {
                MyApp.Msg("wait interrupted");
            }

            int freq = _seek.getProgress();
            int freq2 = _seek2.getProgress();
            int sweeptim = _seekSweep.getProgress();

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
                    wv = makeSweep(currentWaveForm, startval, freq, freq2, sweeptim);
                    break;

                case MollChord:
                    wv = Wave16.sineMoll(_samplerate, chunksize, freq, startval);
                    break;

                case DurChord:
                    wv = Wave16.sineDur(_samplerate, chunksize, freq, startval);
                    break;
            }
            startval += chunksize; // set new chunk offset

            // submit sampling data to player
            if (wv != null)
            {
                short[] arr = wv.toShortArray();
                my.write(arr, 0, arr.length);
                if (OscilloscopeView.object != null)
                    OscilloscopeView.object.setSamples(arr);
            }
        }
    }
}
