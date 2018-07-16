package org.homonoia.sw.utils;

import com.badlogic.gdx.math.MathUtils;

import java.util.Random;

/**
 * Copyright (c) 2015-2018 Homonoia Studios.
 *
 * @author alexparlett
 * @since 16/07/2018
 */
public final class RandomUtils {
    /** Returns a random number between 0 (inclusive) and the specified value (inclusive). */
    static public int random (final Random random, int range) {
        return random.nextInt(range + 1);
    }

    /** Returns a random number between start (inclusive) and end (inclusive). */
    static public int random (final Random random, int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    /** Returns a random number between 0 (inclusive) and the specified value (inclusive). */
    static public long random (final Random random, long range) {
        return (long)(random.nextDouble() * range);
    }

    /** Returns a random number between start (inclusive) and end (inclusive). */
    static public long random (final Random random, long start, long end) {
        return start + (long)(random.nextDouble() * (end - start));
    }

    /** Returns a random boolean value. */
    static public boolean randomBoolean (final Random random) {
        return random.nextBoolean();
    }

    /** Returns true if a random value between 0 and 1 is less than the specified value. */
    static public boolean randomBoolean (final Random random, float chance) {
        return MathUtils.random() < chance;
    }

    /** Returns random number between 0.0 (inclusive) and 1.0 (exclusive). */
    static public float random (final Random random) {
        return random.nextFloat();
    }

    /** Returns a random number between 0 (inclusive) and the specified value (exclusive). */
    static public float random (final Random random, float range) {
        return random.nextFloat() * range;
    }

    /** Returns a random number between start (inclusive) and end (exclusive). */
    static public float random (final Random random, float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    /** Returns -1 or 1, randomly. */
    static public int randomSign (final Random random) {
        return 1 | (random.nextInt() >> 31);
    }

    /** Returns a triangularly distributed random number between -1.0 (exclusive) and 1.0 (exclusive), where values around zero are
     * more likely.
     * <p>
     * This is an optimized version of {@link #randomTriangular(Random, float, float, float) randomTriangular(-1, 1, 0)} */
    public static float randomTriangular (final Random random) {
        return random.nextFloat() - random.nextFloat();
    }

    /** Returns a triangularly distributed random number between {@code -max} (exclusive) and {@code max} (exclusive), where values
     * around zero are more likely.
     * <p>
     * This is an optimized version of {@link #randomTriangular(Random, float, float, float) randomTriangular(-max, max, 0)}
     * @param max the upper limit */
    public static float randomTriangular (final Random random, float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    /** Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where the
     * {@code mode} argument defaults to the midpoint between the bounds, giving a symmetric distribution.
     * <p>
     * This method is equivalent of {@link #randomTriangular(Random, float, float, float) randomTriangular(min, max, (min + max) * .5f)}
     * @param min the lower limit
     * @param max the upper limit */
    public static float randomTriangular (final Random random, float min, float max) {
        return randomTriangular(random, min, max, (min + max) * 0.5f);
    }

    /** Returns a triangularly distributed random number between {@code min} (inclusive) and {@code max} (exclusive), where values
     * around {@code mode} are more likely.
     * @param min the lower limit
     * @param max the upper limit
     * @param mode the point around which the values are more likely */
    public static float randomTriangular (final Random random, float min, float max, float mode) {
        float u = random.nextFloat();
        float d = max - min;
        if (u <= (mode - min) / d) return min + (float)Math.sqrt(u * d * (mode - min));
        return max - (float)Math.sqrt((1 - u) * d * (max - mode));
    }
}
