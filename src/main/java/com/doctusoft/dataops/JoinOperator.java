package com.doctusoft.dataops;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

public final class JoinOperator<K> {
    
    private final Comparator<? super K> keyOrder;
    
    private JoinOperator(Comparator<? super K> keyOrder) {
        this.keyOrder = requireNonNull(keyOrder);
    }
    
    public <L, R> void join(Entries<K, L> leftEntries, Entries<K, R> rightEntries, JoinConsumer<L, R, K> consumer) {
        Side<L> left = new Side<>(leftEntries);
        Side<R> right = new Side<>(rightEntries);
        K actualKey = null;
        while (left.hasNext() || right.hasNext()) {
            int c = compareNullsLast(left.nextKey, right.nextKey);
            if (c <= 0) {
                actualKey = left.nextKey;
                left.forward();
            }
            if (c >= 0) {
                actualKey = right.nextKey;
                right.forward();
            }
            consumer.accept(left.actualValue, right.actualValue, actualKey);
            left.actualValue = null;
            right.actualValue = null;
        }
    }
    
    public <L, G extends Collection<L>, R> void joinGroupLeft(Entries<K, L> leftEntries,
        Supplier<? extends G> leftCollectionSupplier, Entries<K, R> rightEntries, JoinConsumer<G, R, K> consumer)
    {
        join(new EntryGroups<>(leftEntries, leftCollectionSupplier, this::keyEquals), rightEntries, consumer);
    }
    
    public <L, R, G extends Collection<R>> void joinGroupRight(Entries<K, L> leftEntries, Entries<K, R> rightEntries,
        Supplier<? extends G> rightCollectionSupplier, JoinConsumer<L, G, K> consumer)
    {
        join(leftEntries, new EntryGroups<>(rightEntries, rightCollectionSupplier, this::keyEquals), consumer);
    }
    
    public <L, GL extends Collection<L>, R, GR extends Collection<R>> void joinGroupBoth(
        Entries<K, L> leftEntries, Supplier<? extends GL> leftCollectionSupplier,
        Entries<K, R> rightEntries, Supplier<? extends GR> rightCollectionSupplier,
        JoinConsumer<GL, GR, K> consumer)
    {
        join(
            new EntryGroups<>(leftEntries, leftCollectionSupplier, this::keyEquals),
            new EntryGroups<>(rightEntries, rightCollectionSupplier, this::keyEquals),
            consumer);
    }
    
    private boolean keyEquals(K left, K right) {
        return left == right || keyOrder.compare(left, right) == 0;
    }
    
    private int compareNullsLast(K left, K right) {
        if (left == right) {
            return 0;
        }
        if (left == null) {
            return 1;
        }
        if (right == null) {
            return -1;
        }
        return keyOrder.compare(left, right);
    }
    
    public static <K> JoinOperator<K> from(Comparator<K> comparator) {
        return new JoinOperator<>(comparator);
    }
    
    public static <K extends Comparable<? super K>> JoinOperator<K> natural() {
        return NATURAL_OPERATOR;
    }
    
    public static <K extends Comparable<? super K>> JoinOperator<K> forClass(Class<K> keyClass) {
        return NATURAL_OPERATOR;
    }
    
    @SuppressWarnings("rawtypes")
    private static final JoinOperator NATURAL_OPERATOR = new JoinOperator<>(Comparator.naturalOrder());
    
    private final class Side<V> {
        
        private final Entries<K, V> entries;
        private final LookbackFilter<K> keyOrderValidator;
        
        private K nextKey;
        private V nextValue;
        private V actualValue;
        
        private Side(Entries<K, V> entries) {
            this.entries = requireNonNull(entries);
            this.keyOrderValidator = LookbackFilter.strictlyMonotone(keyOrder);
            forward();
        }
        
        public boolean hasNext() {
            return nextKey != null;
        }
        
        public void forward() {
            validateKeyOrder();
            if (!entries.next(this::acceptNext)) {
                store(null, null);
            }
        }
        
        private void acceptNext(K key, V value) {
            store(key, value);
        }
        
        private void validateKeyOrder() {
            if (nextKey != null && !keyOrderValidator.test(nextKey)) {
                K lastKey = keyOrderValidator.getLastAccepted();
                throw new IllegalArgumentException("keyOrder violated: " + lastKey + ", " + nextKey);
            }
        }
        
        private void store(K key, V value) {
            actualValue = nextValue;
            nextValue = value;
            nextKey = key;
        }
        
    }
    
}
