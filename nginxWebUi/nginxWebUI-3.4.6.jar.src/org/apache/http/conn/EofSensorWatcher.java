package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;

public interface EofSensorWatcher {
  boolean eofDetected(InputStream paramInputStream) throws IOException;
  
  boolean streamClosed(InputStream paramInputStream) throws IOException;
  
  boolean streamAbort(InputStream paramInputStream) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\EofSensorWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */