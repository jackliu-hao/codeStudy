package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

public interface ReadListener extends EventListener {
  void onDataAvailable() throws IOException;
  
  void onAllDataRead() throws IOException;
  
  void onError(Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ReadListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */