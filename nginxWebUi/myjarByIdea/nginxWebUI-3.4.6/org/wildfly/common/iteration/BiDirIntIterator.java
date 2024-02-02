package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

public interface BiDirIntIterator extends IntIterator {
   boolean hasNext();

   int next() throws NoSuchElementException;

   int peekNext() throws NoSuchElementException;

   boolean hasPrevious();

   int previous() throws NoSuchElementException;

   int peekPrevious() throws NoSuchElementException;
}
