package io.github.sianabeeblebrox.rhdl;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A fixed size collection of bits (c) SianaBeeblebrox 2026
 * <br>
 * <br>
 * Note: Index 0 is the least-significant bit, all indexes must be in [0, size)
 * <br>
 * Note: Most operations happen in-place, to avoid this use {@link BitString#clone()} first
 * <br>
 * Note: Most operations are unchecked since the Redstone parser validates preconditions at compile time
 * <br>
 * Note: Unlike {@link java.util.BitSet}, {@link BitString} has an immutable and well defined size that remembers
 * leading zeros
 */
public final class BitString implements Cloneable {
    private final int size;
    private final byte[] bytes;

    private BitString(final int size, final byte[] bytes) {
        this.size = size;
        this.bytes = bytes;
    }

    /**
     * Creates a new bit set of the given {@code size} with all bits set to zero
     * @param size the number of bits
     */
    public BitString(final int size) {
        this(size, new byte[(size + 7)/8]);
    }

    /**
     * Returns the number of bits in @{code this}
     * @return the number of bits in {@code this}
     */
    public int size() {
        return this.size;
    }

    /**
     * Sets the bit at the given non-negative {@code index} to one
     * @param index the index to modify
     */
    public void set(final int index) {
        this.bytes[index/8] |= (1 << (index%8));
    }

    /**
     * Sets the bit at the given non-negative {@code index} to zero
     * @param index the index to modify
     */
    public void unset(final int index) {
        this.bytes[index/8] &= ~(1 << (index%8));
    }

    /**
     * Gets the value of the bit at the given non-negative {@code index} as a boolean
     * @param index the index to get
     */
    public boolean get(final int index) {
        return ((this.bytes[index/8] >> (index%8)) & 1) != 0;
    }

    /**
     * Computes bitwise and in-place with another {@link BitString} of the same size
     * @param other the other {@link BitString}
     * @return {@code this}
     */
    public BitString and(final BitString other) {
        for(int i = 0; i < this.bytes.length; i++) {
            this.bytes[i] &= other.bytes[i];
        }
        return this;
    }

    /**
     * Computes bitwise or in-place with another {@link BitString} of the same size
     * @param other the other {@link BitString}
     * @return {@code this}
     */
    public BitString or(final BitString other) {
        for(int i = 0; i < this.bytes.length; i++) {
            this.bytes[i] |= other.bytes[i];
        }
        return this;
    }

    /**
     * Computes bitwise xor in-place with another {@link BitString} of the same size
     * @param other the other {@link BitString}
     * @return {@code this}
     */
    public BitString xor(final BitString other) {
        for(int i = 0; i < this.bytes.length; i++) {
            this.bytes[i] ^= other.bytes[i];
        }
        return this;
    }

    private void maskOverflow() {
        if (this.size % 8 != 0) {
            this.bytes[this.bytes.length - 1] &= (byte) ((1 << (this.size % 8)) - 1);
        }
    }

    /**
     * Computes bitwise not in-place
     * @return {@code this}
     */
    public BitString not() {
        for(int i = 0; i < this.bytes.length; i++) {
            this.bytes[i] = (byte) (~this.bytes[i] & 0xff);
        }
        this.maskOverflow();
        return this;
    }

    /**
     * Adds one to the least-significant bit and carries in-place (can overflow)
     * @return {@code this}
     */
    public BitString increment() {
        for (int i = 0; i < this.bytes.length; i++) {
            if (++this.bytes[i] != 0) {
                break;
            }
        }
        this.maskOverflow();
        return this;
    }


    /**
     * Subtracts one from the least-significant bit and carries in-place (can overflow)
     * @return {@code this}
     */
    public BitString decrement() {
        for (int i = 0; i < this.bytes.length; i++) {
            if (this.bytes[i]-- != 0) {
                break;
            }
        }
        this.maskOverflow();
        return this;
    }


    /**
     * Merges two {@link BitString}s of any size into a new {@link BitString} (the most-significant bit of the left hand
     * side becomes the most-significant bit of the result, and the least-significant bit of the right hand side becomes
     * the least-significant bit of the result)
     *
     * @param lhs the left {@link BitString}
     * @param rhs the right {@link BitString}
     * @return the new {@link BitString}
     */
    public static BitString concat(final BitString lhs, final BitString rhs) {
        final BitString t = new BitString(lhs.size + rhs.size);
        for(int i = 0; i < rhs.size; i++) if(rhs.get(i)) t.set(i);
        for(int i = 0; i < lhs.size; i++) if(lhs.get(i)) t.set(i + rhs.size);
        return t;
    }

    /**
     * Returns a new {@link BitString} made from a subset of {@code this}
     * <br>
     * <b>Note:</b> the order of {@code begin} and {@code end} are backwards that of methods like
     * {@link String#substring(int, int)} to better match the indexing of bits in strings made from
     * {@link BitString#valueOf(String)}
     *
     * @param end the most-significant-most bit index (exclusive)
     * @param begin the least-significant-most bit index (inclusive)
     * @return a new {@link BitString} made from a subset of {@code this}
     */
    public BitString slice(final int end, final int begin) {
        final BitString t = new BitString(end - begin);
        for(int i = 0; i < t.size; i++) if(this.get(begin + i)) t.set(i);
        return t;
    }

    /**
     * Returns a {@link BitString} of the single bit at the given non-negative {@code index}
     * (like {@link BitString#slice(int, int)} but for a single bit)
     *
     * @param index the index to get
     * @return a new {@link BitString} of the single bit at the given non-negative {@code index}
     */
    public BitString at(final int index) {
        return new BitString(1, new byte[] {this.get(index) ? (byte) 1 : 0});
    }

    public boolean isEmpty() {
        return IntStream.range(0, this.bytes.length).allMatch(i -> this.bytes[i] == 0);
    }

    /**
     * Converts a {@code string} into a {@link BitString}.
     * <br>
     * {@code string} must be a sequence of only {@code '0'} and {@code '1'} with optional {@code '_'} characters to
     * improve readability. A leading {@code "0b"} prefix (case-sensitive) is also allowed.
     * <br>
     * Throws {@link NumberFormatException} if {@code string} is {@code null} or invalid
     *
     * @param string the text to parse
     * @return the parsed {@link BitString}
     */
    public static BitString valueOf(final String string) {
        if(string == null) throw new NumberFormatException("Cannot parse null as bit string");

        final String s = string.replaceAll("^0b|_", "");
        final byte[] bytes = new byte[(s.length() + 7)/8];

        for(int i = 0; i < bytes.length; i++) {
            try {
                bytes[i] = (byte) Integer.parseUnsignedInt("+" + s.substring(Math.max(0, s.length() - (i + 1)*8), s.length() - i*8), 2);
            } catch(final NumberFormatException e) {
                throw new NumberFormatException(String.format("Invalid bit string '%s'", string));
            }
        }

        return new BitString(s.length(), bytes);
    }

    @Override
    public String toString() {
        return "0b" + IntStream.range(0, this.bytes.length)
                .mapToObj(i -> String.format("%8s", Integer.toBinaryString(this.bytes[this.bytes.length - 1 - i] & 0xff)))
                .collect(Collectors.joining())
                .replace(' ', '0')
                .substring((8 - this.size%8)%8)
        ;
    }

    /**
     * Returns {@code true} iff {@code this} and {@code other} are logically equivalent {@link BitString}s
     * @param other the other value to compare with
     * @return {@code true} iff {@code this} and {@code other} are logically equivalent {@link BitString}s
     */
    @Override
    public boolean equals(final @Nullable Object other) {
        return this == other || other instanceof BitString b && this.size == b.size && Arrays.equals(this.bytes, b.bytes);
    }

    @Override
    public int hashCode() {
        return 31*Objects.hash(size) + Arrays.hashCode(bytes);
    }

    /**
     * Creates a copy of {@code this}, modifying the copy does not affect the original or vice versa (until
     * modified, both will {@link BitString#equals(Object)} each other)
     * @return the copy
     */
    @Override
    public BitString clone() {
        return new BitString(this.size, this.bytes.clone());
    }

    /**
     * Returns a bit string of the given size containing all true bits
     * @return a bit string of the given size containing all true bits
     */
    public static BitString one(final int size) {
        return new BitString(size).not();
    }

    /**
     * Returns a bit string containing a single true bit
     * @return a bit string containing a single true bit
     */
    public static BitString one() {
        return BitString.one(1);
    }

    /**
     * Returns a bit string of the given size containing all false bits
     * @return a bit string of the given size containing all false bits
     */
    public static BitString zero(final int size) {
        return new BitString(size);
    }

    /**
     * Returns a bit string containing a single false bit
     * @return a bit string containing a single false bit
     */
    public static BitString zero() {
        return BitString.zero(1);
    }

    /**
     * Returns a bit string with a single true bit corresponding to the ordinal of an enum
     * @param e an enum instance
     * @return a bit string with a single true bit corresponding to the ordinal of an enum
     */
    public static BitString forEnum(final Enum<?> e) {
        final BitString bits = new BitString(e.getClass().getEnumConstants().length);
        bits.set(e.ordinal());
        return bits;
    }
}
