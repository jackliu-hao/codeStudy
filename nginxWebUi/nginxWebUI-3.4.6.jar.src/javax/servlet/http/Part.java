package javax.servlet.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface Part {
  InputStream getInputStream() throws IOException;
  
  String getContentType();
  
  String getName();
  
  String getSubmittedFileName();
  
  long getSize();
  
  void write(String paramString) throws IOException;
  
  void delete() throws IOException;
  
  String getHeader(String paramString);
  
  Collection<String> getHeaders(String paramString);
  
  Collection<String> getHeaderNames();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\Part.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */