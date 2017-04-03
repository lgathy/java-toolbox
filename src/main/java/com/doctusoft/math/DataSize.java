package com.doctusoft.math;

import com.doctusoft.java.Failsafe;

import java.text.DecimalFormat;

/**
 * Based on <a href="http://programming.guide/java/formatting-byte-size-to-human-readable-format.html"></a>
 */
public enum DataSize {
    
    SI(1000) {
        protected String exponent(int exp) {
            return "kMBTPE".substring(exp - 1, exp);
        }
    },
    BINARY(1 << 10) {
        protected String exponent(int exp) {
            return "KMGTPE".charAt(exp - 1) + "i";
        }
    };
    
    private final int unit;
    
    DataSize(int unit) {
        this.unit = unit;
    }
    
    protected abstract String exponent(int exp);
    
    public String printBase(long size) {
        Failsafe.checkArgument(size >= 0L, () -> "Negative size: " + size);
        if (size < (long) unit) return Long.toString(size);
        int exp = (int) (Math.log(size) / Math.log(unit));
        double base = size / Math.pow(unit, exp);
        return DECIMAL_FORMAT.get().format(base) + exponent(exp);
    }
    
    public String print(long bytes) {
        return printBase(bytes) + "B";
    }
    
    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMAT = ThreadLocal.withInitial(() -> new DecimalFormat("#.#"));
}
