package org.xnio;

public interface Pooled<T> extends AutoCloseable {
  void discard();
  
  void free();
  
  T getResource() throws IllegalStateException;
  
  void close();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Pooled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */