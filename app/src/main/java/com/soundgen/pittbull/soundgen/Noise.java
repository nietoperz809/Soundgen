package com.soundgen.pittbull.soundgen;

/**
 * Created by Administrator on 1/18/2016.
 */
public class Noise
{
    private static final int maxKey = 0x1f;
    private static int key;
    private static final int range = 128;
    private static float whiteValues[];
    private static float maxSumEver;

    private static void initPink()
    {
        maxSumEver = 90;
        key = 0;
        whiteValues = new float[6];
        for (int i = 0; i < 6; i++)
        {
            whiteValues[i] = ((float) Math.random() * Long.MAX_VALUE) % (range / 6);
        }
    }

    // return a pink noise value
    private static float pink()
    {
        int last_key = key;
        float sum;

        key++;
        if (key > maxKey)
            key = 0;
        // Exclusive-Or previous value with current value. This gives
        // a list of bits that have changed.
        int diff = last_key ^ key;
        sum = 0;
        for (int i = 0; i < 6; i++)
        {
            // If bit changed get new random number for corresponding
            // white_value
            if ((diff & (1 << i)) != 0)
            {
                whiteValues[i] = ((float) Math.random() * Long.MAX_VALUE) % (range / 6);
            }
            sum += whiteValues[i];
        }
        if (sum > maxSumEver)
            maxSumEver = sum;
        sum = 2f * (sum / maxSumEver) - 1f;
        return sum;
    }

    static public Wave16 whiteNoise (int samplingrate, int samples)
    {
        Wave16 t = new Wave16(samples, samplingrate);
        for (int s = 0; s < t.data.length; s++)
        {
            t.data[s] = Math.random();
        }
        t.data = Wave16.fitValues(t.data);
        return t;
    }


    static public Wave16 pinkNoise(int samplingrate, int samples)
    {
        initPink();
        Wave16 t = new Wave16(samples, samplingrate);
        for (int s = 0; s < t.data.length; s++)
        {
            t.data[s] = pink();
        }
        t.data = Wave16.fitValues(t.data);
        return t;
    }
}
