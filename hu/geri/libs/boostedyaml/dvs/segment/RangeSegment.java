/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.dvs.segment;

import hu.geri.libs.boostedyaml.dvs.segment.Segment;
import java.util.Arrays;

public class RangeSegment
implements Segment {
    private final int start;
    private final int end;
    private final int step;
    private final int minStringLength;
    private final int maxStringLength;
    private final int fill;
    private final int length;

    public RangeSegment(int start, int end, int step, int fill) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.fill = fill;
        if (step == 0) {
            throw new IllegalArgumentException("Step cannot be zero!");
        }
        if (start < end && step < 0 || start > end && step > 0) {
            throw new IllegalArgumentException(String.format("Invalid step for the given range! start=%d end=%d step=%d", start, end, step));
        }
        if (start == end) {
            throw new IllegalArgumentException(String.format("Parameters define an empty range, start=end! start=%d end=%d", start, end));
        }
        this.length = (int)Math.ceil((double)Math.abs(start - end) / (double)Math.abs(step));
        int last = start + step * (this.length - 1);
        if (start < 0 || end < 0 && last < 0) {
            throw new IllegalArgumentException(String.format("Range contains negative integers! start=%d end=%d step=%d", start, end, step));
        }
        if (fill > 0 && !this.validateFill(fill, Math.max(start, last))) {
            throw new IllegalArgumentException(String.format("Some integer from the range exceeds maximum length defined by the filling parameter! start=%d end=%d last=%d fill=%d", start, end, last, fill));
        }
        int n = fill > 0 ? fill : (this.maxStringLength = this.countDigits(step > 0 ? end : start));
        this.minStringLength = fill > 0 ? fill : this.countDigits(step > 0 ? start : end);
    }

    @Deprecated
    public RangeSegment(int start, int end) {
        this(start, end, start < end ? 1 : -1, 0);
    }

    @Deprecated
    public RangeSegment(int start, int end, int step) {
        this(start, end, step, 0);
    }

    private boolean validateFill(int fill, int maxValue) {
        int maxFillValue = 9;
        for (int i = 0; i < fill; ++i) {
            if (maxFillValue >= maxValue) {
                return true;
            }
            maxFillValue *= 10;
            maxFillValue += 9;
        }
        return false;
    }

    @Override
    public int parse(String versionId, int index) {
        int digit;
        if (this.fill > 0) {
            if (this.fill > versionId.length() - index) {
                return -1;
            }
            try {
                return this.getRangeIndex(Integer.parseInt(versionId.substring(index, this.fill)));
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }
        if (versionId.length() <= index) {
            return -1;
        }
        int value = 0;
        int digits = 0;
        for (int i = 0; i < this.maxStringLength && i < versionId.length() - index && (i != 1 || value != 0 || digits != 1) && (digit = Character.digit(versionId.charAt(index + i), 10)) != -1; value += digit, ++digits, ++i) {
            value *= 10;
        }
        if (digits == 0) {
            return -1;
        }
        if (value == 0) {
            return this.getRangeIndex(0);
        }
        while (value > 0 && digits >= this.minStringLength) {
            int rangeIndex = this.getRangeIndex(value);
            if (rangeIndex != -1) {
                return rangeIndex;
            }
            value /= 10;
            --digits;
        }
        return -1;
    }

    private int countDigits(int value) {
        if (value == 0) {
            return 1;
        }
        int digits = 0;
        while (value > 0) {
            value /= 10;
            ++digits;
        }
        return digits;
    }

    private int getRangeIndex(int value) {
        if (this.step > 0 ? this.start > value || this.end <= value : this.start < value || this.end >= value) {
            return -1;
        }
        int diff = Math.abs(value - this.start);
        if (value >= 0 && diff % this.step == 0) {
            return diff / Math.abs(this.step);
        }
        return -1;
    }

    @Override
    public String getElement(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds! i=%d length=%d", index, this.length));
        }
        String value = Integer.toString(this.start + this.step * index, 10);
        if (this.fill <= 0 || value.length() == this.fill) {
            return value;
        }
        char[] fill = new char[this.fill - value.length()];
        Arrays.fill(fill, '0');
        return new StringBuilder(value).insert(0, fill).toString();
    }

    @Override
    public int getElementLength(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException(String.format("Index out of bounds! i=%d length=%d", index, this.length));
        }
        return this.fill > 0 ? this.fill : this.countDigits(this.start + this.step * index);
    }

    @Override
    public int length() {
        return this.length;
    }

    public String toString() {
        return "RangeSegment{start=" + this.start + ", end=" + this.end + ", step=" + this.step + ", fill=" + this.fill + ", length=" + this.length + '}';
    }
}

