package com.doctusoft.dataops;

import org.junit.Test;

import com.google.common.base.Equivalence;

import static org.junit.Assert.assertEquals;

public final class TestLookbackFilter {

    private static String ALMA = "Alma";
    private String alma = "alma";

    private LookbackFilter<String> filter;

    @Test
    public void testNotTheSame() {
        filter = LookbackFilter.notTheSame();
        test(ALMA, true);
        test(alma, true);
        test(alma, false);
        test(new String(alma), true);
    }

    @Test
    public void testNotEquals() {
        filter = LookbackFilter.notEquals();
        test(ALMA, true);
        test(alma, true);
        test(alma, false);
        test(new String(alma), false);
    }

    @Test
    public void testNotEquivalent() {
        filter = LookbackFilter.notEquivalent(EQUALS_IGNORE_CASE);
        test(ALMA, true);
        test(alma, false);
        test(new String(alma), false);
        test("almafa", true);
        test(alma, true);
    }

    @Test
    public void testMonotone() {
        filter = LookbackFilter.monotone();
        test(ALMA, true);
        test(alma, true);
        test(alma, true);
        test(new String(alma), true);
        test(ALMA, false);
        test(new String(alma), true);
    }

    @Test
    public void testMonotoneIgnoreCase() {
        filter = LookbackFilter.monotone(String.CASE_INSENSITIVE_ORDER);
        test(alma, true);
        test(ALMA, true);
        test(alma, true);
        test(alma, true);
        test(new String(alma), true);
        test("almafa", true);
        test(alma, false);
    }

    @Test
    public void testStrictlyMonotone() {
        filter = LookbackFilter.strictlyMonotone();
        test(ALMA, true);
        test(alma, true);
        test(alma, false);
        test(ALMA, false);
        test(new String(alma), false);
        test("almafa", true);
        test("almafa", false);
        test(alma, false);
    }

    @Test
    public void testStrictlyMonotoneIgnoreCase() {
        filter = LookbackFilter.strictlyMonotone(String.CASE_INSENSITIVE_ORDER);
        test(alma, true);
        test(ALMA, false);
        test(alma, false);
        test(new String(alma), false);
        test("almafa", true);
        test("almafa", false);
        test(alma, false);
    }

    @Test
    public void testNoDuplicates() {
        filter = LookbackFilter.noDuplicates();
        test(ALMA, true);
        test(alma, true);
        test(new String(alma), false);
        test(ALMA, true);
        test("almafa", true);
        test("almafa", false);
        test(ALMA, true);
        test(ALMA, false);
    }

    @Test
    public void testNoDuplicatesIgnoreCase() {
        filter = LookbackFilter.noDuplicates(String.CASE_INSENSITIVE_ORDER);
        test(ALMA, true);
        test(alma, false);
        test("almafa", true);
        test("almafa", false);
        test(alma, true);
        test(ALMA, false);
    }

    private static final Equivalence<String> EQUALS_IGNORE_CASE = new Equivalence<String>() {

        @Override
        protected boolean doEquivalent(String a, String b) {
            return a.equalsIgnoreCase(b);
        }

        @Override
        protected int doHash(String t) {
            return t.toLowerCase().hashCode();
        }
    };

    private void test(String input, boolean expected) {
        assertEquals(expected, filter.apply(input));
    }
    
}
