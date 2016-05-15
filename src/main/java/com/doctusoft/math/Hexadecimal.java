package com.doctusoft.math;

import com.doctusoft.annotation.Beta;
import com.doctusoft.java.Failsafe;

@Beta
public final class Hexadecimal {
    
    private Hexadecimal() {
        throw Failsafe.staticClassInstantiated();
    }
    
    public static void appendFixWidth32bit(StringBuilder buf, int unsignedInt) {
        int ln = buf.length();
        buf.append("00000000");
        for (int i = ln + 7, base = unsignedInt, mod; i >= ln && base != 0; --i) {
            mod = base & MASK;
            buf.setCharAt(i, DIGITS[mod]);
            base >>>= BITS;
        }
    }
    
    public static String printFixWidth32bit(int unsignedInt) {
        StringBuilder buf = new StringBuilder(8);
        appendFixWidth32bit(buf, unsignedInt);
        return buf.toString();
    }
    
    public static void appendFixWidth64bit(StringBuilder buf, long unsignedLong) {
        int ln = buf.length();
        buf.append("0000000000000000");
        long base = unsignedLong;
        long mod;
        for (int i = ln + 15; i >= ln && base != 0; --i) {
            mod = base & LMASK;
            buf.setCharAt(i, DIGITS[(int) mod]);
            base >>>= BITS;
        }
    }
    
    public static String printFixWidth64bit(long unsignedLong) {
        StringBuilder buf = new StringBuilder(16);
        appendFixWidth64bit(buf, unsignedLong);
        return buf.toString();
    }
    
    public static char getDigit(int index) {
        return DIGITS[index];
    }
    
    public static char getLastDigit(int value) {
        return DIGITS[value & MASK];
    }
    
    public static String printLast2Digits(int value) {
        return printLast2DigitsInternal(value & BMASK);
    }
    
    public static String printFixWidth2(int value) {
        return printFixWidth2Internal(value & BMASK);
    }
    
    public static final char[] createDigits() {
        return new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    private static String printLast2DigitsInternal(int value) {
        return (value < BASE) ? STR[value] : printFixWidth2Internal(value);
    }
    
    private static String printFixWidth2Internal(int value) {
        return new String(new char[] { DIGITS[value >>> BITS], DIGITS[value & MASK] });
    }
    
    private static final int BITS = 4;
    
    private static final int BASE = 16;
    
    private static final int MASK = BASE - 1;
    
    private static final int BMASK = 0x000000ff;
    
    private static final long LMASK = MASK;
    
    private static final char[] DIGITS = createDigits();
    
    private static final String[] STR = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

}
