package org.homonoia.sw.utils;

import com.badlogic.gdx.math.MathUtils;

import java.util.Random;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 16/07/2018
 */
public final class RandomUtils {
    /**
     * Returns a random number between 0 (inclusive) and the specified value (inclusive).
     */
    static public int random(final Random random, int range) {
        return random.nextInt(range + 1);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    static public int random(final Random random, int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value (inclusive).
     */
    static public long random(final Random random, long range) {
        return (long) (random.nextDouble() * range);
    }

    /**
     * Returns a random number between start (inclusive) and end (inclusive).
     */
    static public long random(final Random random, long start, long end) {
        return start + (long) (random.nextDouble() * (end - start));
    }

    /**
     * Returns a random boolean value.
     */
    static public boolean randomBoolean(final Random random) {
        return random.nextBoolean();
    }

    /**
     * Returns true if a random value between 0 and 1 is less than the specified value.
     */
    static public boolean randomBoolean(final Random random, float chance) {
        return MathUtils.random() < chance;
    }

    /**
     * Returns random number between 0.0 (inclusive) and 1.0 (exclusive).
     */
    static public float random(final Random random) {
        return random.nextFloat();
    }

    /**
     * Returns a random number between 0 (inclusive) and the specified value (exclusive).
     */
    static public float random(final Random random, float range) {
        return random.nextFloat() * range;
    }

    /**
     * Returns a random number between start (inclusive) and end (exclusive).
     */
    static public float random(final Random random, float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    /**
     * Returns -1 or 1, randomly.
     */
    static public int randomSign(final Random random) {
        return 1 | (random.nextInt() >> 31);
    }

    /**
     * Returns a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive), where values around zero are
     * more likely.
     * <p>
     * This is an optimized version of {@link #randomTriangular(Random, float, float, float) randomTriangular(-1, 1, 0)}
     */
    public static float randomTriangular(final Random random) {
        return random.nextFloat() - random.nextFloat();
    }

    /**
     * Returns a triangularly distributed random number between {@code -max} (exclusive) and {@code max} (exclusive), where values
     * around zero are more likely.
     * <p>
     * This is an optimized version of {@link #randomTriangular(Random, float, float, float) randomTriangular(-max, max, 0)}
     *
     * @param max the upper limit
     */
    public static float randomTriangular(final Random random, float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    /**
     * Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where the
     * {@code mode} argument defaults to the midpoint between the bounds, giving a symmetric distribution.
     * <p>
     * This method is equivalent of {@link #randomTriangular(Random, float, float, float) randomTriangular(min, max, (min + max) * .5f)}
     *
     * @param min the lower limit
     * @param max the upper limit
     */
    public static float randomTriangular(final Random random, float min, float max) {
        return randomTriangular(random, min, max, (min + max) * 0.5f);
    }

    /**
     * Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where values
     * around {@code mode} are more likely.
     *
     * @param min  the lower limit
     * @param max  the upper limit
     * @param mode the point around which the values are more likely
     */
    public static float randomTriangular(final Random random, float min, float max, float mode) {
        float u = random.nextFloat();
        float d = max - min;
        if (u <= (mode - min) / d) return min + (float) sqrt(u * d * (mode - min));
        return max - (float) sqrt((1 - u) * d * (max - mode));
    }

    /**
     * <p>
     * Generates a sequence of values from a normal distribution, using Box-Muller
     * https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
     * </p>
     *
     * @param random            A (uniform) random number generator
     * @param standardDeviation The standard deviation of the distribution
     * @param mean              The mean of the distribution
     * @return A normally distributed value
     */
    public static float[] normallyDistributedSingles(Random random, float standardDeviation, float mean) {
        double u1 = random.nextDouble(); //these are uniform(0,1) random doubles
        double u2 = random.nextDouble();

        double x1 = sqrt(-2.0 * log(u1));
        double x2 = 2.0 * PI * u2;
        double z1 = x1 * sin(x2); //random normal(0,1)
        double z2 = x1 * cos(x2);
        return new float[]{(float) (z1 * standardDeviation + mean), (float) (z2 * standardDeviation + mean)};
    }

    /**
     * <p>
     * Generates a single value from a normal distribution, using Box-Muller
     * https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
     * </p>
     *
     * @param random            A (uniform) random number generator
     * @param standardDeviation The standard deviation of the distribution
     * @param mean              The mean of the distribution
     * @return A normally distributed value
     */
    public static float normallyDistributedSingle(Random random, float standardDeviation, float mean) {
        // *****************************************************
        // Intentionally duplicated to avoid IEnumerable overhead
        // *****************************************************
        double u1 = random.nextDouble(); //these are uniform(0,1) random doubles
        double u2 = random.nextDouble();

        double x1 = (float) sqrt(-2.0 * log(u1));
        double x2 = (float) (2.0 * PI * u2);
        double z1 = (float) (x1 * sin(x2)); //random normal(0,1)
        return (float) (z1 * standardDeviation + mean);
    }

    /**
     * <p>
     * Generates a sequence of normal values clamped within min and max
     * </p>
     * <p>
     * Originally used inverse phi method, but this method, found here:
     * http://arxiv.org/pdf/0907.4010.pdf
     * is significantly faster
     * </p>
     *
     * @param random            A (uniform) random number generator
     * @param standardDeviation The standard deviation of the distribution
     * @param mean              The mean of the distribution
     * @param min               The minimum allowed value (does not bias)
     * @param max               The max allowed value (does not bias)
     * @return A sequence of samples from a normal distribution, clamped to within min and max in an unbiased manner.
     */
    public static float[] normallyDistributedSingles(Random random, float standardDeviation, float mean, float min, float max) {
        // sharing computation doesn't save us much, it's all lost to IEnumerable overhead
        return new float[]{normallyDistributedSingle(random, standardDeviation, mean, min, max)};
    }

    /**
     * <p>
     * Generates a single normal value clamped within min and max
     * </p>
     * <p>
     * Originally used inverse phi method, but this method, found here:
     * http://arxiv.org/pdf/0907.4010.pdf
     * is significantly faster
     * </p>
     *
     * @param random            A (uniform) random number generator
     * @param standardDeviation The standard deviation of the distribution
     * @param mean              The mean of the distribution
     * @param min               The minimum allowed value (does not bias)
     * @param max               The max allowed value (does not bias)
     * @return A single sample from a normal distribution, clamped to within min and max in an unbiased manner.
     */
    public static float normallyDistributedSingle(Random random, float standardDeviation, float mean, float min, float max) {
        float nMax = (max - mean) / standardDeviation;
        float nMin = (min - mean) / standardDeviation;
        float nRange = nMax - nMin;
        float nMaxSq = nMax * nMax;
        float nMinSq = nMin * nMin;
        float subFrom = nMinSq;
        if (nMin < 0 && 0 < nMax) subFrom = 0;
        else if (nMax < 0) subFrom = nMaxSq;

        float sigma = 0.0f;
        double u;
        float z;
        do {
            z = nRange * random.nextFloat() + nMin; // uniform[normMin, normMax]
            sigma = (float) exp((subFrom - z * z) / 2);
            u = random.nextFloat();
        } while (u > sigma);

        return z * standardDeviation + mean;
    }
}
