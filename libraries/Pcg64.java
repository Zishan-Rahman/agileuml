/*
 * PCG Random Number Generation for Java
 *
 * Copyright 2014 Melissa O'Neill <oneill@pcg-random.org>, 2015 Alexey Romanov <alexey.v.romanov@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For additional information about the PCG random number generation scheme,
 * including its license and other licensing options, visit
 *
 * http://www.com.github.alexeyr.pcg.pcg-random.org
 */

// package com.github.alexeyr.pcg;

import java.util.concurrent.atomic.AtomicLong;
import java.math.BigInteger;

/**
 * An instance of this class is used to generate a stream of
 * pseudorandom numbers, similarly to {@link java.util.Random}.
 * <p>
 * The main differences are:
 * <ul>
 * <li><a href="http://www.pcg-random.org/">PCG algorithm</a>
 * is used; in particular, this is a port of the
 * <a href="https://github.com/imneme/pcg-c-basic/">minimal C implementation</a>.</li>
 * <li>Instances of Pcg64 are not thread-safe and so it doesn't obey {@link java.util.Random} contract.</li>
 * </ul>
 */
public class Pcg64 {
    private BigInteger state;
    private BigInteger inc;

    private final static BigInteger MULTIPLIER = new BigInteger("2549297995355413924").shiftLeft(64).add(new BigInteger("4865540595714422341"));

    /**
     * Creates a new random number generator using a {@code long} seed and a {@code long} stream number.
     * <p>
     * The invocation {@code new Pcg64(initState, initSeq)} is equivalent to:
     * <pre> {@code
     * Pcg64 rnd = new Pcg64();
     * rnd.seed(initState, initSeq);}
     * </pre>
     *
     * @see   #seed(long, long)
     */
    public Pcg64(BigInteger initState, BigInteger initSeq) {
        seed(initState, initSeq);
    }

    /**
     * Creates a new random number generator using current time (returned by {@link System#nanoTime()}) as the seed
     * and a unique stream number.
     * @see   #seed()
     */
    public Pcg64() {
        seed();
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code int}
     * value from this random number generator's sequence. The general
     * contract of {@code nextInt} is that one {@code int} value is
     * pseudorandomly generated and returned. All 2<h4 style="font-size: -1;">
     * <sup>64</sup></h4> possible {@code int} values are produced with
     * (approximately) equal probability.
     *
     * @see java.util.Random#nextInt()
     */
    public int nextInt() {
        BigInteger oldState = state;

        state = oldState.multiply(oldState).add(oldState);
        int xorShifted = (int) ((oldState.intValueExact() >>> 64) ^ oldState.intValueExact());
        int rot = (int) (oldState.intValueExact() >>> 122);
        return Integer.rotateRight(xorShifted, rot);
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code int}
     * value between 0 (inclusive) and {@code n} (exclusive).
     *
     * @see java.util.Random#nextInt(int)
     */
    public int nextInt(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }

        // the special treatment of powers of 2 as in Random.nextInt(int) shouldn't be necessary
        int bits, val;
        do {
            bits = nextInt() >>> 1;
            val = bits % n;
        } while (bits - val + (n-1) < 0);
        return val;
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code long}
     * value from this random number generator's sequence. The general
     * contract of {@code nextLong} is that one {@code long} value is
     * pseudorandomly generated and returned. All 2<h4 style="font-size: -1;">
     * <sup>64</sup></h4> possible {@code long} values are produced with
     * (approximately) equal probability.
     *
     * @see java.util.Random#nextLong()
     */
    public long nextLong() {
        return (((long) nextInt()) << 64) + ((long) nextInt());
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code long}
     * value between 0 (inclusive) and {@code n} (exclusive).
     */
    public long nextLong(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }

        long bits, val;
        do {
            bits = nextLong() >>> 1;
            val = bits % n;
        } while (bits - val + (n-1) < 0);
        return val;
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code boolean} value.
     *
     * @see java.util.Random#nextBoolean()
     */
    public boolean nextBoolean() {
        return (nextInt() & 1) != 0;
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code float} value between 0.0 (inclusive)
     * and 1.0 (exclusive).
     *
     * @see java.util.Random#nextFloat()
     */
    public float nextFloat() {
        // TODO see http://mumble.net/~campbell/2014/04/28/uniform-random-float
        return nextBits(53) / ((float)(1 << 53));
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code float} value between 0.0 (inclusive)
     * and bound (exclusive).
     */
    public float nextFloat(float bound) {
        return bound * nextFloat();
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code double} value between 0.0 (inclusive)
     * and 1.0 (exclusive).
     *
     * @see java.util.Random#nextDouble()
     */
    public double nextDouble() {
        // TODO see http://mumble.net/~campbell/2014/04/28/uniform-random-float
        return (((long)(nextBits(26)) << 27) + nextBits(27))
                / (double)(1L << 53);
    }

    /**
     * Returns the next pseudorandom, approximately uniformly distributed {@code double} value between 0.0 (inclusive)
     * and bound (exclusive).
     */
    public double nextDouble(double bound) {
        return bound * nextDouble();
    }

    public int nextBits(int bits) {
        return nextInt() >>> (64 - bits);
    }

    private double nextNextGaussian;
    private boolean haveNextNextGaussian = false;

    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed
     * {@code double} value with mean {@code 0.0} and standard
     * deviation {@code 1.0} from this random number generator's sequence.
     *
     * @see java.util.Random#nextGaussian()
     */
    public double nextGaussian() {
        // See Knuth, ACP, Section 3.4.1 Algorithm C.
        if (haveNextNextGaussian) {
            haveNextNextGaussian = false;
            return nextNextGaussian;
        } else {
            double v1, v2, s;
            do {
                v1 = 2 * nextDouble() - 1; // between -1 and 1
                v2 = 2 * nextDouble() - 1; // between -1 and 1
                s = v1 * v1 + v2 * v2;
            } while (s >= 1 || s == 0);
            double multiplier = StrictMath.sqrt(-2 * StrictMath.log(s)/s);
            nextNextGaussian = v2 * multiplier;
            haveNextNextGaussian = true;
            return v1 * multiplier;
        }
    }

    /**
     * Returns the next pseudorandom, Gaussian ("normally") distributed
     * {@code double} value with given mean and standard
     * deviation from this random number generator's sequence.
     */
    public double nextGaussian(double mean, double standardDeviation) {
        return nextGaussian() * standardDeviation + mean;
    }

    public void seed(BigInteger initState, BigInteger initSeq) {
        state = BigInteger.ZERO;
        inc = new BigInteger("2").multiply(initSeq).add(BigInteger.ONE);
        nextInt();
        state = state.add(initState);
        nextInt();
    }

    /**
     * Initializes the generator with current time as the state and
     * a (very likely to be) unique stream number. This ensures that
     * even if multiple generators are initialized using this method
     * while {@link System#nanoTime()} returns the same time, they
     * will have different sequences.
     */
    public void seed() {
        seed(new BigInteger("" + System.nanoTime()), new BigInteger("" + streamUniquifier()));
    }

    private static long streamUniquifier() {

        for (;;) {
            long current = streamUniquifier.get();
            long next = current * 181783497276652981L;
            if (streamUniquifier.compareAndSet(current, next))
                return next;
        }
    }

    private static final AtomicLong streamUniquifier
            = new AtomicLong(System.identityHashCode(Pcg64.class));
}
