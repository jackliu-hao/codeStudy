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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\BiDirIntIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */