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
        // y = a * exp(kx) + b

        float a = (float) ((end - start)/(Math.exp(duration * k) - 1));
        float b = start - a;

        return (float) (a * Math.exp(time * k) + b);
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
        // y = a * log(kx + 1) + b

        float a = (float) ((end - start)/(Math.log(k * duration + 1)));
        float b = start;

        return (float) (a * Math.log(k * time + 1) + b);
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
        // y = a * x^(kx) + b

        float a = (float) ((end - start)/(Math.pow(x, k*duration) - 1));
        float b = start - a;

        return (float) (a * Math.pow(x, k * time) + b);
    }
}
