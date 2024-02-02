package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

public interface WriteListener extends EventListener {
  void onWritePossible() throws IOException;
  
  void onError(Throwable paramThrowable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\WriteListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */