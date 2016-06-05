package org.cora.maths.utils;

/**
 * Created by ronan-j on 06/06/16.
 */
public class Interpolation
{
    /**
     * Compute linear interpolation value
     * @param start start value
     * @param end end value
     * @param duration full animation duration
     * @param time current time
     * @return computed value
     */
    public static float linearInterpolation(float start, float end, float duration, float time)
    {
        // y = ax + b

        float b = start;
        float a = (end - start) / duration;
        return a * time + b;
    }

    /**
     * Compute exponentiel interpolation value
     * @param start start value
     * @param end end value
     * @param duration full animation duration
     * @param time current time
     * @param k base of exp
     * @return computed value
     */
    public static float expInterpolation(float start, float end, float duration, float time, float k)
    {
        // y = a * exp(bkx)

        float a = start;
        float b = (float) (Math.log(end/a) / (k * duration));

        return (float) (a * Math.exp(b * k * time));
    }

    /**
     * Compute logarithmic interpolation value
     * @param start start value
     * @param end end value
     * @param duration full animation duration
     * @param time current time
     * @param k base of log
     * @return computed value
     */
    public static float logInterpolation(float start, float end, float duration, float time, float k)
    {
        // y = a + b * log(kx)

        float r = 1/k;
        start += r;
        duration += r;
        time += r;

        float a = start;
        float b = (float) ((end - a) / Math.log(k * duration + 1));

        return (float) ( a + b * Math.log(k * time));
    }

    /**
     * Compute exponentiel interpolation value
     * @param start start value
     * @param end end value
     * @param duration full animation duration
     * @param time current time
     * @param x x pow value
     * @param k base of pow
     * @return computed value
     */
    public static float powInterpolation(float start, float end, float duration, float time, float x, float k)
    {
        // y = a * x^(bkx)

        float a = start;
        float b = (float) (Math.log(end/a) / (k * duration * Math.log(x)));

        return (float) (a * Math.pow(x, b * k * time));
    }
}
