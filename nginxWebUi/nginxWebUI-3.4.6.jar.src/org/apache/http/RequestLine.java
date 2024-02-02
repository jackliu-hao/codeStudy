package org.apache.http;

public interface RequestLine {
  String getMethod();
  
  ProtocolVersion getProtocolVersion();
  
  String getUri();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\RequestLine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */