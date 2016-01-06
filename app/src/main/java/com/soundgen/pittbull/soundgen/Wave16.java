package com.soundgen.pittbull.soundgen; /**
 * Created by Administrator on 1/6/2016.
 */

/**
 * To change this template use File | Settings | File Templates.
 */
public class Wave16
{
    /**
     *
     */
    private static final long serialVersionUID = 3070090589210322951L;

    /**
     * Data array that holds sampling data
     */
    public double[] data;
    /**
     * Sampling rate
     */
    public int samplingRate;

    /**
     * Optional name of this wave
     */
    public String name = "unnamed";

    /**
     * Upper level constant
     */
    public static final double MAX_VALUE = Short.MAX_VALUE;

    /**
     * Lower level constant
     */
    public static final double MIN_VALUE = Short.MIN_VALUE;

    /**
     * Math constants
     */
    public static final double PI = Math.PI;
    public static final double PI2 = 2.0 * PI;
    public static final double ASIN1 = Math.asin(1.0);

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
        //name = Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Local factory function that builds a new SamplingData16 object from this one
     * All samples are empty
     *
     * @return The new object
     */
    public Wave16 createEmptyCopy()
    {
        return new Wave16(data.length, samplingRate);
        //out.setName(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /**
     * Gives an optional name
     *
     * @param n The name
     * @return This com.soundgen.pittbull.soundgen.Wave16 object
     */
    public Wave16 setName(String n)
    {
        name = n;
        return this;
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

    public static double[] fitValues(double[] in)
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

    public Wave16 deriveAndFitValues()
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
            out.data[x] = MAX_VALUE * Math.asin(Math.sin(startval * d1)) / ASIN1 * Math.pow(-1, Math.floor(0.5 + startval / ((double) samplingrate / freq)));
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

    static public Wave16 curveTriangle(int samplingrate, int samples, double freq, int startval)
    {
        Wave16 out = new Wave16(samples, samplingrate);

        double d1 = 2 * PI / samplingrate * freq;
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

