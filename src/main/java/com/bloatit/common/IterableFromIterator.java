package com.bloatit.common;

import java.util.Iterator;

public class IterableFromIterator<T> implements Iterable<T> {

    private Iterator<T> it;

    public IterableFromIterator(Iterator<T> it) {
        super();
        this.it = it;
    }

    @Override
    public Iterator<T> iterator() {
        return it;
    }

}
