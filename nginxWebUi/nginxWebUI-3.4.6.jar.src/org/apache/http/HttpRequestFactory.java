package org.apache.http;

public interface HttpRequestFactory {
  HttpRequest newHttpRequest(RequestLine paramRequestLine) throws MethodNotSupportedException;
  
  HttpRequest newHttpRequest(String paramString1, String paramString2) throws MethodNotSupportedException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HttpRequestFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */