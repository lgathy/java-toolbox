package com.doctusoft.dataops;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.Map.*;

import static com.google.common.base.Preconditions.checkNotNull;
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
            Entries.forMap(ImmutableMap.of(1, "Alfa", 2, "Beta", 3, "Gamma")),
            Entries.forMap(ImmutableMap.of(1, true, 2, false, 3, true)),
            joinMock
        );
        verifyJoin("Alfa", 1, true);
        verifyJoin("Beta", 2, false);
        verifyJoin("Gamma", 3, true);
        verifyNoMoreInteractions(joinMock);
    }

    @Test
    public void testLeftOuterJoin() throws Exception {

        joinOperator.join(
            Entries.forMap(ImmutableMap.of(1, "Alfa", 2, "Beta", 3, "Gamma")),
            Entries.forMap(ImmutableMap.of(2, false, 3, true)),
            joinMock
        );
        verifyJoin("Alfa", 1, null);
        verifyJoin("Beta", 2, false);
        verifyJoin("Gamma", 3, true);
        verifyNoMoreInteractions(joinMock);
    }

    @Test
    public void testRightOuterJoin() throws Exception {

        joinOperator.join(
            Entries.forMap(ImmutableMap.of(1, "Alfa", 3, "Gamma")),
            Entries.forMap(ImmutableMap.of(1, true, 2, false, 3, false)),
            joinMock
        );
        verifyJoin("Alfa", 1, true);
        verifyJoin(null, 2, false);
        verifyJoin("Gamma", 3, false);
        verifyNoMoreInteractions(joinMock);
    }

    @Test
    public void testFullOuterJoin() throws Exception {

        joinOperator.join(
            Entries.forMap(ImmutableMap.of(2, "Beta", 3, "Gamma")),
            Entries.forMap(ImmutableMap.of(1, true, 3, false)),
            joinMock
        );
        verifyJoin(null, 1, true);
        verifyJoin("Beta", 2, null);
        verifyJoin("Gamma", 3, false);
        verifyNoMoreInteractions(joinMock);
    }

    @Test
    public void testFullOuterJoinWithNullValues() throws Exception {

        joinOperator.join(
            Entries.forEntries(ImmutableList.of(entry(1, (String) null), entry(2, "Beta"), entry(3, "Gamma"))),
            Entries.forEntries(ImmutableList.of(entry(1, true), entry(2, (Boolean) null), entry(3, false))),
            joinMock
        );
        verifyJoin(null, 1, true);
        verifyJoin("Beta", 2, null);
        verifyJoin("Gamma", 3, false);
        verifyNoMoreInteractions(joinMock);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongOrderOnLeft() throws Exception {
        try {
            joinOperator.join(
                Entries.forMap(ImmutableMap.of(1, "Alfa", 3, "Gamma", 2, "Beta")),
                Entries.forMap(ImmutableMap.of(1, true, 2, false, 3, true)),
                joinMock
            );
        } finally {
            verifyJoin("Alfa", 1, true);
            verifyJoin(null, 2, false);
            verifyJoin("Gamma", 3, true);
            verifyNoMoreInteractions(joinMock);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongOrderOnRight() throws Exception {
        try {
            joinOperator.join(
                Entries.forMap(ImmutableMap.of(1, "Alfa", 2, "Beta", 3, "Gamma")),
                Entries.forMap(ImmutableMap.of(2, true, 3, false, 1, true)),
                joinMock
            );
        } finally {
            verifyJoin("Alfa", 1, null);
            verifyJoin("Beta", 2, true);
            verifyJoin("Gamma", 3, false);
            verifyNoMoreInteractions(joinMock);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicationOnLeft() throws Exception {
        try {
            joinOperator.join(
                Entries.forEntries(ImmutableList.of(entry(1, "Alfa"), entry(2, "Beta"), entry(2, "Beta"))),
                Entries.forEntries(ImmutableList.of(entry(1, true), entry(2, false), entry(3, true))),
                joinMock
            );
        } finally {
            verifyJoin("Alfa", 1, true);
            verifyJoin("Beta", 2, false);
            verifyNoMoreInteractions(joinMock);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicationOnRight() throws Exception {
        try {
            joinOperator.join(
                Entries.forEntries(ImmutableList.of(entry(1, "Alfa"), entry(2, "Beta"), entry(3, "Gamma"))),
                Entries.forEntries(ImmutableList.of(entry(1, true), entry(2, false), entry(2, true))),
                joinMock
            );
        } finally {
            verifyJoin("Alfa", 1, true);
            verifyJoin("Beta", 2, false);
            verifyNoMoreInteractions(joinMock);
        }
    }

    private void verifyJoin(String left, Integer key, Boolean right) {
        order.verify(joinMock).accept(left, right, key);
    }

    private static <K, V> Entry<K, V> entry(K key, V value) {
        checkNotNull(key);
        return Maps.immutableEntry(key, value);
    }
    
}
