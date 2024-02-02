package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

public interface AsyncListener extends EventListener {
  void onComplete(AsyncEvent paramAsyncEvent) throws IOException;
  
  void onTimeout(AsyncEvent paramAsyncEvent) throws IOException;
  
  void onError(AsyncEvent paramAsyncEvent) throws IOException;
  
  void onStartAsync(AsyncEvent paramAsyncEvent) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\AsyncListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */