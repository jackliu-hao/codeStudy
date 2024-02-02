package javax.servlet.http;

import java.io.IOException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;

public interface WebConnection extends AutoCloseable {
  ServletInputStream getInputStream() throws IOException;
  
  ServletOutputStream getOutputStream() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\WebConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */