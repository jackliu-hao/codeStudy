package javax.mail.internet;

import java.io.InputStream;

public interface SharedInputStream {
  long getPosition();
  
  InputStream newStream(long paramLong1, long paramLong2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\SharedInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */