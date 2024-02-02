package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.HttpRequest;

public interface HttpUriRequest extends HttpRequest {
  String getMethod();
  
  URI getURI();
  
  void abort() throws UnsupportedOperationException;
  
  boolean isAborted();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpUriRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */