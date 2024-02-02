package org.wildfly.common.iteration;

import java.util.NoSuchElementException;

public interface IntIterator {
  boolean hasNext();
  
  int next() throws NoSuchElementException;
  
  int peekNext() throws NoSuchElementException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\IntIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */