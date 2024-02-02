package org.apache.http.io;

public interface HttpTransportMetrics {
  long getBytesTransferred();
  
  void reset();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\io\HttpTransportMetrics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */