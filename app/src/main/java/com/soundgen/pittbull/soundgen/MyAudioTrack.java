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
    boolean running = true;
    public enum WaveForm {OFF, Sine, Sawtooth, Square, Triangle, Pulse,
        SweepSIN, SweepSAW, SweepSQR, SweepTRI, SweepPUL};
    WaveForm currentWaveForm = WaveForm.OFF;

    public MyAudioTrack (SeekBar seek)
    {
        _seek = seek;
        this.start();
    }

    synchronized public void setWaveForm (WaveForm i)
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

    @Override
    public void run()
    {
        //this.setPriority(MAX_PRIORITY);
        my = new AudioTrack (AudioManager.STREAM_MUSIC,
                _samplerate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                4*chunksize,
                //AudioTrack.getMinBufferSize (_samplerate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT),
                AudioTrack.MODE_STREAM);
        my.play();

        Wave16 wv;
        int startval = 0;

        Wave16 sweep = Wave16.sweepSine(_samplerate, 0, 5000, 10d);

        while(running)
        {
            int freq = _seek.getProgress();
            if (freq == 0)
            {
                try
                {
                    sleep (100);
                }
                catch (InterruptedException e)
                {
                    return;
                }
                continue;
            }
            switch (currentWaveForm)
            {
                default:
                case OFF:
                    wv = null;
                    break;

                case Sawtooth:
                    wv = Wave16.curveSawTooth (_samplerate, chunksize, freq, startval);
                    break;

                case Sine:
                    wv = Wave16.curveSine (_samplerate, chunksize, freq, startval);
                    //wv=sweep; // TODO: Experimental
                    break;

                case Square:
                    wv = Wave16.curveRect (_samplerate, chunksize, freq, startval);
                    break;

                case Pulse:
                    wv = Wave16.curvePulse (_samplerate, chunksize, freq, startval);
                    break;

                case Triangle:
                    wv = Wave16.curveTriangle (_samplerate, chunksize, freq, startval);
                    break;

                case SweepSIN:
                    // TODO: add sweepsine code
                    wv = null;
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
