package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface DataSource {
  InputStream getInputStream() throws IOException;
  
  OutputStream getOutputStream() throws IOException;
  
  String getContentType();
  
  String getName();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\DataSource.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */