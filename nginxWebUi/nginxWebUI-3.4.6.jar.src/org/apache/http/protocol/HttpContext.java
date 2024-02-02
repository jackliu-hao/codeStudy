package org.apache.http.protocol;

public interface HttpContext {
  public static final String RESERVED_PREFIX = "http.";
  
  Object getAttribute(String paramString);
  
  void setAttribute(String paramString, Object paramObject);
  
  Object removeAttribute(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\HttpContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */