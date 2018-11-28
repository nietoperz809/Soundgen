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
    static private final int _samplerate = 44000;
    /**
     * fixed buffer size
     */
    static private final int chunksize = 10000;
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
    private WaveFormType currentWaveForm = WaveFormType.OFF;

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
    synchronized public void setWaveForm(WaveFormType i)
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
    private Wave16 makeSweep(WaveFormType swptype, int startval, int min, int max, int time)
    {
        if (_sweep.waveType != swptype || min != sweepmin || max != sweepmax || time != sweeptime)
        {
            double tl = ((double) time) / 10 + 0.1;
            //MyApp.Msg(""+tl+":"+min+":"+max);
            if (swptype == WaveFormType.SweepSIN)
                _sweep = WaveForms.sweepSine(_samplerate, sweepmin, sweepmax, tl);
            else if (swptype == WaveFormType.SweepTRI)
                _sweep = WaveForms.sweepTriangle(_samplerate, sweepmin, sweepmax, tl);
            else if (swptype == WaveFormType.SweepSQR)
                _sweep = WaveForms.sweepSquare(_samplerate, sweepmin, sweepmax, tl);
            else if (swptype == WaveFormType.SweepPUL)
                _sweep = WaveForms.sweepPulse(_samplerate, sweepmin, sweepmax, tl);
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
            int sleeptime;
            if (currentWaveForm == WaveFormType.OFF)
                sleeptime = 1000;
            else
                sleeptime = 100;

            // Delay
            try
            {
                sleep(sleeptime);
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
                case OFF:
                    continue;

                case Sawtooth:
                    wv = WaveForms.curveSawTooth(_samplerate, chunksize, freq, startval);
                    break;

                case Sine:
                    wv = WaveForms.curveSine(_samplerate, chunksize, freq, startval);
                    break;

                case Square:
                    wv = WaveForms.curveRect(_samplerate, chunksize, freq, startval);
                    break;

                case Pulse:
                    wv = WaveForms.curvePulse(_samplerate, chunksize, freq, startval);
                    break;

                case Triangle:
                    wv = WaveForms.curveTriangle(_samplerate, chunksize, freq, startval);
                    break;

                case SweepSIN:
                case SweepTRI:
                case SweepSQR:
                case SweepPUL:
                    //case SweepSAW:
                    wv = makeSweep(currentWaveForm, startval, freq, freq2, sweeptim);
                    break;

                case MollChord:
                    wv = WaveForms.sineMoll(_samplerate, chunksize, freq, startval);
                    break;

                case DurChord:
                    wv = WaveForms.sineDur(_samplerate, chunksize, freq, startval);
                    break;

                case PinkNoise:
                    wv = Noise.pinkNoise(_samplerate, chunksize);
                    break;

                case WhiteNoise:
                    wv = Noise.whiteNoise(_samplerate, chunksize);
                    break;
            }
            startval += chunksize; // set new chunk offset

            // submit sampling data to player
            if (wv != null)
            {
                short[] arr = wv.toShortArray();
                my.write(arr, 0, arr.length);
                if (OscilloscopeView._activeView != null)
                    OscilloscopeView._activeView.setSamples(arr);
            }
        }
    }
}
