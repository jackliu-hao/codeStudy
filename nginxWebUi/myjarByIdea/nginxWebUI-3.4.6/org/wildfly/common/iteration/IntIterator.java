package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

public interface IntIterator {
   boolean hasNext();

   int next() throws NoSuchElementException;

   int peekNext() throws NoSuchElementException;
}
