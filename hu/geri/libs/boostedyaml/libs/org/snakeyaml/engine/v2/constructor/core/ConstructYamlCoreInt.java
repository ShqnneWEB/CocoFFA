/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.core;

import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.constructor.ConstructScalar;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import hu.geri.libs.boostedyaml.libs.org.snakeyaml.engine.v2.nodes.Node;
import java.math.BigInteger;

public class ConstructYamlCoreInt
extends ConstructScalar {
    private static final int[][] RADIX_MAX;

    private static int maxLen(int max, int radix) {
        return Integer.toString(max, radix).length();
    }

    private static int maxLen(long max, int radix) {
        return Long.toString(max, radix).length();
    }

    protected static Number createLongOrBigInteger(String number, int radix) {
        try {
            return Long.valueOf(number, radix);
        } catch (NumberFormatException e1) {
            return new BigInteger(number, radix);
        }
    }

    @Override
    public Object construct(Node node) {
        String value = this.constructScalar(node);
        if (value.isEmpty()) {
            throw new ConstructorException("while constructing an int", node.getStartMark(), "found empty value", node.getStartMark());
        }
        return this.createIntNumber(value);
    }

    public Object createIntNumber(String value) {
        int base;
        int sign = 1;
        char first = value.charAt(0);
        if (first == '-') {
            sign = -1;
            value = value.substring(1);
        } else if (first == '+') {
            value = value.substring(1);
        }
        if ("0".equals(value)) {
            return 0;
        }
        if (value.startsWith("0x")) {
            value = value.substring(2);
            base = 16;
        } else if (value.startsWith("0o")) {
            value = value.substring(2);
            base = 8;
        } else {
            return this.createNumber(sign, value, 10);
        }
        return this.createNumber(sign, value, base);
    }

    private Number createNumber(int sign, String number, int radix) {
        Number result;
        int[] maxArr;
        int len;
        int n = len = number != null ? number.length() : 0;
        if (sign < 0) {
            number = "-" + number;
        }
        int[] nArray = maxArr = radix < RADIX_MAX.length ? RADIX_MAX[radix] : null;
        if (maxArr != null) {
            boolean gtInt;
            boolean bl = gtInt = len > maxArr[0];
            if (gtInt) {
                if (len > maxArr[1]) {
                    return new BigInteger(number, radix);
                }
                return ConstructYamlCoreInt.createLongOrBigInteger(number, radix);
            }
        }
        try {
            result = Integer.valueOf(number, radix);
        } catch (NumberFormatException e) {
            result = ConstructYamlCoreInt.createLongOrBigInteger(number, radix);
        }
        return result;
    }

    static {
        int[] radixList;
        RADIX_MAX = new int[17][2];
        for (int radix : radixList = new int[]{8, 10, 16}) {
            ConstructYamlCoreInt.RADIX_MAX[radix] = new int[]{ConstructYamlCoreInt.maxLen(Integer.MAX_VALUE, radix), ConstructYamlCoreInt.maxLen(Long.MAX_VALUE, radix)};
        }
    }
}

