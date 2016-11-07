package com.doctusoft.math;

import com.doctusoft.annotation.Beta;

import static com.doctusoft.java.Failsafe.staticClassInstantiated;
import static java.lang.Math.abs;

/**
 * Provides specialized implementation for some frequent use-cases of working with decimal interpretation of numbers. 
 * In almost all cases there are more general implementations for these with better flexibility provided but due to that 
 * they also come with larger overhead cost unnecessary in these common use-cases.
 */
@Beta
public final class Decimal {
    
    private Decimal() {
        throw staticClassInstantiated();
    }
    
    public static char getDigit(int index) {
        return DIGITS[index];
    }
    
    public static char getLastDigit(int value) {
        return DIGITS[abs(value % BASE)];
    }
    
    public static String printLast2Digits(int value) {
        return printLast2DigitsInternal(abs(value % BASE_POW_2));
    }
    
    public static String printLast3Digits(int value) {
        return printLast3DigitsInternal(abs(value % BASE_POW_3));
    }
    
    public static String printFixWidth2(int value) {
        return printFixWidth2Internal(abs(value % BASE_POW_2));
    }
    
    public static String printFixWidth3(int value) {
        return printFixWidth3Internal(abs(value % BASE_POW_3));
    }
    
    public static String printFixWidth4(int value) {
        return printFixWidth4Internal(abs(value % BASE_POW_4));
    }
    
    private static String printLast2DigitsInternal(int value) {
        return (value < BASE) ? STR[value] : printFixWidth2Internal(value);
    }
    
    private static String printLast3DigitsInternal(int value) {
        return (value < BASE_POW_2) ? printFixWidth2Internal(value) : printFixWidth3Internal(value);
    }
    
    private static String printFixWidth2Internal(int value) {
        return new String(new char[] { DIGITS[value / BASE], DIGITS[value % BASE] });
    }
    
    private static String printFixWidth3Internal(int value) {
        char[] chars = new char[3];
        chars[2] = DIGITS[value % BASE];
        value /= BASE;
        chars[1] = DIGITS[value % BASE];
        value /= BASE;
        chars[0] = DIGITS[value];
        return new String(chars);
    }
    
    private static String printFixWidth4Internal(int value) {
        char[] chars = new char[4];
        chars[3] = DIGITS[value % BASE];
        value /= BASE;
        chars[2] = DIGITS[value % BASE];
        value /= BASE;
        chars[1] = DIGITS[value % BASE];
        value /= BASE;
        chars[0] = DIGITS[value];
        return new String(chars);
    }
    
    private static final String[] STR = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    
    private static final int BASE = 10;
    
    private static final int BASE_POW_2 = BASE * BASE;
    private static final int BASE_POW_3 = BASE * BASE_POW_2;
    private static final int BASE_POW_4 = BASE * BASE_POW_3;
    
}
