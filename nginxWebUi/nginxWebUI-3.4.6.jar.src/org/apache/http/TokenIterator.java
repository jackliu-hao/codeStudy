package org.apache.http;

import java.util.Iterator;

public interface TokenIterator extends Iterator<Object> {
  boolean hasNext();
  
  String nextToken();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\TokenIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */