package io.undertow.client;

import java.io.IOException;

public interface ClientCallback<T> {
  void completed(T paramT);
  
  void failed(IOException paramIOException);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ClientCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */