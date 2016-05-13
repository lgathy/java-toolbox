package com.doctusoft.dataops;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;

import static com.doctusoft.dataops.Entries.forEntries;
import static java.util.Arrays.*;
import static java.util.Objects.*;
import static org.mockito.Mockito.*;

public final class TestJoin {
    
    private JoinOperator<Integer> joinOperator;
    private JoinConsumer<String, Boolean, Integer> joinMock;
    private InOrder order;
    
    @Before
    public void setup() {
        joinOperator = JoinOperator.natural();
        joinMock = mock(JoinConsumer.class);
        order = inOrder(joinMock);
    }
    
    @Test
    public void testInnerJoin() throws Exception {
        joinOperator.join(
            numberedEntries("Alfa", "Beta", "Gamma"),
            numberedEntries(true, false, true),
            joinMock
            );
        verifyJoin("Alfa", true, 1);
        verifyJoin("Beta", false, 2);
        verifyJoin("Gamma", true, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test
    public void testLeftOuterJoin() throws Exception {
        joinOperator.join(
            numberedEntries("Alfa", "Beta", "Gamma"),
            forEntries(asList(entry(2, false), entry(3, true))),
            joinMock
            );
        verifyJoin("Alfa", null, 1);
        verifyJoin("Beta", false, 2);
        verifyJoin("Gamma", true, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test
    public void testLeftOuterJoinWithNullValue() throws Exception {
        joinOperator.join(
            numberedEntries("Alfa", "Beta", "Gamma"),
            numberedEntries(null, false, true),
            joinMock
            );
        verifyJoin("Alfa", null, 1);
        verifyJoin("Beta", false, 2);
        verifyJoin("Gamma", true, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test
    public void testRightOuterJoin() throws Exception {
        
        joinOperator.join(
            forEntries(asList(entry(1, "Alfa"), entry(3, "Gamma"))),
            numberedEntries(true, false, false),
            joinMock
            );
        verifyJoin("Alfa", true, 1);
        verifyJoin(null, false, 2);
        verifyJoin("Gamma", false, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test
    public void testRightOuterJoinWithNullValue() throws Exception {
        
        joinOperator.join(
            numberedEntries("Alfa", null, "Gamma"),
            numberedEntries(true, false, false),
            joinMock
            );
        verifyJoin("Alfa", true, 1);
        verifyJoin(null, false, 2);
        verifyJoin("Gamma", false, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test
    public void testFullOuterJoin() throws Exception {
        
        joinOperator.join(
            forEntries(asList(entry(2, "Beta"), entry(3, "Gamma"))),
            forEntries(asList(entry(1, true), entry(3, false))),
            joinMock
            );
        verifyJoin(null, true, 1);
        verifyJoin("Beta", null, 2);
        verifyJoin("Gamma", false, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test
    public void testFullOuterJoinWithNullValues() throws Exception {
        
        joinOperator.join(
            numberedEntries(null, "Beta", "Gamma"),
            numberedEntries(true, null, false),
            joinMock
            );
        verifyJoin(null, true, 1);
        verifyJoin("Beta", null, 2);
        verifyJoin("Gamma", false, 3);
        verifyNoMoreInteractions(joinMock);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWrongOrderOnLeft() throws Exception {
        try {
            joinOperator.join(
                forEntries(asList(entry(1, "Alfa"), entry(3, "Gamma"), entry(2, "Beta"))),
                numberedEntries(true, false, true),
                joinMock
                );
        } finally {
            verifyJoin("Alfa", true, 1);
            verifyJoin(null, false, 2);
            verifyJoin("Gamma", true, 3);
            verifyNoMoreInteractions(joinMock);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWrongOrderOnRight() throws Exception {
        try {
            joinOperator.join(
                numberedEntries("Alfa", "Beta", "Gamma"),
                forEntries(asList(entry(2, true), entry(3, false), entry(1, true))),
                joinMock
                );
        } finally {
            verifyJoin("Alfa", null, 1);
            verifyJoin("Beta", true, 2);
            verifyJoin("Gamma", false, 3);
            verifyNoMoreInteractions(joinMock);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicationOnLeft() throws Exception {
        try {
            joinOperator.join(
                forEntries(asList(entry(1, "Alfa"), entry(2, "Beta"), entry(2, "Beta"))),
                forEntries(asList(entry(1, true), entry(2, false), entry(3, true))),
                joinMock
                );
        } finally {
            verifyJoin("Alfa", true, 1);
            verifyJoin("Beta", false, 2);
            verifyNoMoreInteractions(joinMock);
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicationOnRight() throws Exception {
        try {
            joinOperator.join(
                forEntries(asList(entry(1, "Alfa"), entry(2, "Beta"), entry(3, "Gamma"))),
                forEntries(asList(entry(1, true), entry(2, false), entry(2, true))),
                joinMock
                );
        } finally {
            verifyJoin("Alfa", true, 1);
            verifyJoin("Beta", false, 2);
            verifyNoMoreInteractions(joinMock);
        }
    }
    
    private void verifyJoin(String left, Boolean right, Integer key) {
        order.verify(joinMock).accept(left, right, key);
    }
    
    private static <K, V> Entry<K, V> entry(K key, V value) {
        requireNonNull(key);
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
    
    @SafeVarargs
    private static final <V> Entries<Integer, V> numberedEntries(V... values) {
        return new NumberedEntries<>(asList(values));
    }
    
    private static final class NumberedEntries<V> implements Entries<Integer, V> {
        
        private final Iterator<V> iterator;
        private int index = 1;
        
        NumberedEntries(Iterable<V> values) {
            this.iterator = values.iterator();
        }
        
        @Override
        public boolean next(BiConsumer<Integer, V> action) {
            if (iterator.hasNext()) {
                action.accept(index++, iterator.next());
                return true;
            }
            return false;
        }
    }
    
}
