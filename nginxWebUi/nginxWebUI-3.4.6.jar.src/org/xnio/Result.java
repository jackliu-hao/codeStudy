package org.xnio;

import java.io.IOException;

public interface Result<T> {
  boolean setResult(T paramT);
  
  boolean setException(IOException paramIOException);
  
  boolean setCancelled();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Result.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */