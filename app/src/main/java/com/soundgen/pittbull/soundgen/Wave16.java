package com.soundgen.pittbull.soundgen; /**
 * Created by Administrator on 1/6/2016.
 */

/**
 * To change this template use File | Settings | File Templates.
 */
public class Wave16
{

    public WaveForm waveType = WaveForm.OFF;

    /**
     * Data array that holds sampling data
     */
    public double[] data;
    /**
     * Sampling rate
     */
    private final int samplingRate;

    /**
     * Upper level constant
     */
    private static final double MAX_VALUE = Short.MAX_VALUE;

    /**
     * Lower level constant
     */
    private static final double MIN_VALUE = Short.MIN_VALUE;

    /**
     * Math constants
     */
    private static final double PI = Math.PI;
    private static final double PI2 = 2.0 * PI;
    private static final double ASIN1 = Math.asin(1.0);

    /**
     * Builds a new com.soundgen.pittbull.soundgen.Wave16 object
     *
     * @param size Size of array
     * @param rate Sampling rate
     */
    public Wave16(int size, int rate)
    {
        data = new double[size];
        samplingRate = rate;
    }

    private Wave16 (int size, int rate, WaveForm t)
    {
        this (size, rate);
        waveType = t;
    }

    public static Wave16 extractSamples (Wave16 source, int from, int to)
    {
        int len = to - from;
        Wave16 res = new Wave16(len, source.samplingRate);
        System.arraycopy (source.data, from, res.data, 0, len);
        return res;
    }


    /**
     * Local factory function that builds a new SamplingData16 object from this one
     * All samples are empty
     *
     * @return The new object
     */
    private Wave16 createEmptyCopy()
    {
        return new Wave16(data.length, samplingRate);
        //out.setName(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Returns whole array as 'short' values
     *
     * @return The new 'short' array
     */
    public short[] toShortArray()
    {
        short[] res = new short[data.length];
        for (int s = 0; s < data.length; s++)
        {
            res[s] = (short) data[s];
        }
        return res;
    }

    /**
     * Implements the standard toString
     *
     * @return A string describing this object
     */
    @Override
    public String toString()
    {
        return "not supported";
    }

    private static double[] fitValues(double[] in)
    {
        double[] out = new double[in.length];
        Wave16.Wave16AmplitudeInfo am = new Wave16.Wave16AmplitudeInfo();
        am.calc(in);
        double div = am.span / (Wave16.MAX_VALUE - Wave16.MIN_VALUE);
        am.min = am.min / div;
        for (int s = 0; s < in.length; s++)
        {
            out[s] = in[s] / div + Wave16.MIN_VALUE - am.min;
            if (Double.isInfinite(out[s]) || Double.isNaN(out[s]))
            {
                out[s] = 0.0;
            }
        }
        return out;
    }

    private Wave16 deriveAndFitValues()
    {
        double f1;
        double f2;
        Wave16 out = createEmptyCopy();

        for (int s = 0; s < (data.length - 1); s++)
        {
            f1 = data[s];
            f2 = data[s + 1];
            out.data[s] = f2 - f1;
        }
        // Last sample
        out.data[data.length - 1] = out.data[data.length - 2];
        out.data = fitValues(out.data);
        return out;
    }

    //////////////////////////////////////////////////////////////////

    static public Wave16 curvePulse(int samplingrate, int samples, double freq, int startval)
    {
        return curveRect(samplingrate, samples, freq, startval).deriveAndFitValues();
    }

    static public Wave16 curveSawTooth(int samplingrate, int samples, double freq, int startval)
    {
        Wave16 out = new Wave16(samples, samplingrate);

        double d1 = PI / samplingrate * freq;
        //double d2 = samplingrate/freq;
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = MAX_VALUE * Math.asin(Math.sin(startval * d1)) / ASIN1 * Math.pow(-1,
                    Math.floor(0.5 + startval / ((double) samplingrate / freq)));
            startval++;
        }
        out.data = fitValues(out.data);
        return out;
    }

    static public Wave16 curveSine(int samplingrate, int samples, double freq, int startval)
    {
        Wave16 out = new Wave16(samples, samplingrate);

        double d1 = PI2 / samplingrate * freq;
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = MAX_VALUE * Math.sin(startval * d1);
            startval++;
        }
        out.data = fitValues(out.data);
        return out;
    }

    static public Wave16 sweepSine(int samplingrate,
                                   int fstart,
                                   int fend,
                                   int samples)
    {
        Wave16 out = new Wave16(samples, samplingrate, WaveForm.SweepSIN);
        double step = (((double) fend - (double) fstart) / samples / Wave16.PI);
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = Wave16.MAX_VALUE * Math.sin(
                    2 * Wave16.PI * fstart * ((double) x / samplingrate));
            fstart += step;
        }
        return out;
    }

    static public Wave16 sweepSine(int samplingrate,
                                   int fstart,
                                   int fend,
                                   double seconds)
    {
        double time = seconds * samplingrate;
        return sweepSine(samplingrate, fstart, fend, (int)time);
    }

    static public Wave16 sweepTriangle(int samplingrate,
                                       int fstart,
                                       int fend,
                                       int samples)
    {
        Wave16 out = new Wave16(samples, samplingrate, WaveForm.SweepTRI);
        double step = ((double) fend - (double) fstart) / samples / Wave16.PI;
        for (int x = 0; x < samples; x++)
        {
            double c1 = Math.sin(2 * Wave16.PI * fstart * ((double) x / samplingrate));
            out.data[x] = Wave16.MAX_VALUE * Math.asin(c1) / Math.asin(1);
            fstart += step;
        }
        return out;
    }

    static public Wave16 sweepTriangle (int samplingrate,
                                   int fstart,
                                   int fend,
                                   double seconds)
    {
        double time = seconds * samplingrate;
        return sweepTriangle(samplingrate, fstart, fend, (int) time);
    }

    static public Wave16 sweepSquare(int samplingrate,
                                     int fstart,
                                     int fend,
                                     int samples)
    {
        Wave16 out = new Wave16(samples, samplingrate, WaveForm.SweepSQR);
        double step = (((double) fend - (double) fstart) / samples / Wave16.PI);
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = Wave16.MAX_VALUE * Math.signum(Math.sin(2 * Wave16.PI * fstart * ((double) x / samplingrate)));
            fstart += step;
        }
        return out;
    }

    static public Wave16 sweepSquare (int samplingrate,
                                        int fstart,
                                        int fend,
                                        double seconds)
    {
        double time = seconds * samplingrate;
        return sweepSquare(samplingrate, fstart, fend, (int) time);
    }

    static public Wave16 sweepPulse(int samplingrate,
                                    int fstart,
                                    int fend,
                                    int samples)
    {
        Wave16 wv = sweepSquare(samplingrate, fstart, fend, samples).deriveAndFitValues();
        wv.waveType = WaveForm.SweepPUL;
        return wv;
    }

    static public Wave16 sweepPulse (int samplingrate,
                                      int fstart,
                                      int fend,
                                      double seconds)
    {
        double time = seconds * samplingrate;
        return sweepPulse(samplingrate, fstart, fend, (int) time);
    }

    static public Wave16 curveTriangle(int samplingrate, int samples, double freq, int startval)
    {
        Wave16 out = new Wave16(samples, samplingrate);

        double d1 = PI2 / samplingrate * freq;
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = MAX_VALUE * Math.asin(Math.sin(startval * d1)) / ASIN1;
            startval++;
        }
        out.data = fitValues(out.data);
        return out;
    }

    static public Wave16 curveRect(int samplingrate, int samples, double freq, int startval)
    {
        Wave16 out = new Wave16(samples, samplingrate);

        double d1 = Wave16.PI2 / samplingrate * freq;
        for (int x = 0; x < samples; x++)
        {
            out.data[x] = MAX_VALUE * Math.signum(Math.sin(startval * d1));
            startval++;
        }
        out.data = fitValues(out.data);
        return out;
    }


    static class Wave16AmplitudeInfo
    {
        /**
         * Minimum amplitude
         */
        public double min;
        /**
         * Maximum amplitude
         */
        public double max;
        /**
         * Total amplitude span
         */
        public double span;

        Wave16AmplitudeInfo()
        {
        }

        /**
         * Does calculation so that members are valid
         *
         * @param arr Array to be used as base object
         */
        public void calc(double arr[])
        {
            min = Double.MAX_VALUE;
            max = -Double.MAX_VALUE;

            // Find min and max
            for (double anIn : arr)
            {
                // Force forbidden values to zero
                if (Double.isInfinite(anIn) || Double.isNaN(anIn))
                    anIn = 0.0;

                if (anIn < min)
                {
                    min = anIn;
                }
                if (anIn > max)
                {
                    max = anIn;
                }
            }
            span = max - min;
        }

        public String toString()
        {
            return "Min:" + min + " Max:" + max + " Span:" + span;
        }
    }
}

