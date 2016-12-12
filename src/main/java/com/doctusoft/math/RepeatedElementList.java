package com.doctusoft.math;

import com.doctusoft.annotation.Beta;
import com.doctusoft.java.Failsafe;

import java.util.*;
import java.util.function.*;

import static java.util.Objects.*;

/**
 * A read-only view of {@code n} identical elements in a {@link List} where n must be a positive integer and the
 * {@code element} cannot be {@code null}. All attempts to modify the elements after the instance has been created
 * will result in an {@link UnsupportedOperationException}.
 * <p>Instances of this class may never be {@link #isEmpty() empty}. If 0 as list size should be tolerated upon
 * instance creation, use the {@link #repeat(Object, int)} method.</p>
 *
 * @param <E> the type parameter of the list
 */
@Beta
public final class RepeatedElementList<E> extends AbstractList<E> {
    
    /**
     * @return a read-only {@link List} which has {@code n} identical elements. {@code n} can be {@code null} in which
     * case an {@link Collections#emptyList() empty list} is returned.
     */
    public static <E> List<E> repeat(E element, int nTimes) {
        requireNonNull(element, "element");
        switch (nTimes) {
        case 0:
            return Collections.emptyList();
        case 1:
            return Collections.singletonList(element);
        default:
            return new RepeatedElementList<>(element, nTimes);
        }
    }
    
    private final E element;
    
    private final int size;
    
    /**
     * <b>Important to note</b> that the constructor does not tolerate {@code 0} as {@code size}. If that is needed, use
     * the {@link #repeat(Object, int)} factory method.
     */
    public RepeatedElementList(E element, int size) {
        this.element = requireNonNull(element, "element");
        this.size = size;
        Failsafe.checkArgument(size > 0, "size <= 0");
    }
    
    /**
     * An instance of {@link RepeatedElementList} can never be empty, thus this method will always return {@code false}.
     */
    public boolean isEmpty() { return false; }
    
    public E get(int index) {
        checkBounds(index);
        return element;
    }
    
    private void checkBounds(int index) {
        if (index < 0 || index >= size) {
            throw indexOutOfBounds(index);
        }
    }
    
    private IndexOutOfBoundsException indexOutOfBounds(int index) {
        return new IndexOutOfBoundsException("index=" + index + ", size=" + size);
    }
    
    public int size() { return size; }
    
    @Override public boolean contains(Object obj) {
        return Objects.equals(obj, element);
    }
    
    @Override public void forEach(Consumer<? super E> action) {
        for (int i = 0; i < size; ++i) {
            action.accept(element);
        }
    }
    
    @Override public Iterator<E> iterator() { return listIterator(0); }
    
    @Override public ListIterator<E> listIterator(int index) {
        checkBounds(index);
        
        class Itr implements ListIterator<E> {
            
            private int pos = index;
            
            public boolean hasNext() { return pos < size; }
            
            public boolean hasPrevious() { return pos > 0; }
            
            public E next() {
                if (pos >= size) throw indexOutOfBounds(pos);
                ++pos;
                return element;
            }
            
            public E previous() {
                if (pos <= 0) throw indexOutOfBounds(pos);
                --pos;
                return element;
            }
            
            public int nextIndex() { return pos; }
            
            public int previousIndex() { return pos - 1; }
            
            public void remove() { throw new UnsupportedOperationException(); }
            
            public void set(E e) { throw new UnsupportedOperationException(); }
            
            public void add(E e) { throw new UnsupportedOperationException(); }
        }
        return new Itr();
    }
    
    @Override public void sort(Comparator<? super E> c) {}
    
    @Override public boolean removeIf(Predicate<? super E> filter) { throw new UnsupportedOperationException(); }
    
    @Override public void replaceAll(UnaryOperator<E> operator) { throw new UnsupportedOperationException(); }
    
}
