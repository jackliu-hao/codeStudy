package javax.servlet.http;

import java.util.Set;

public interface PushBuilder {
  PushBuilder method(String paramString);
  
  PushBuilder queryString(String paramString);
  
  PushBuilder sessionId(String paramString);
  
  PushBuilder setHeader(String paramString1, String paramString2);
  
  PushBuilder addHeader(String paramString1, String paramString2);
  
  PushBuilder removeHeader(String paramString);
  
  PushBuilder path(String paramString);
  
  void push();
  
  String getMethod();
  
  String getQueryString();
  
  String getSessionId();
  
  Set<String> getHeaderNames();
  
  String getHeader(String paramString);
  
  String getPath();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\PushBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */