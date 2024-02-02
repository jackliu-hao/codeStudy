package org.apache.http;

public interface StatusLine {
  ProtocolVersion getProtocolVersion();
  
  int getStatusCode();
  
  String getReasonPhrase();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\StatusLine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */