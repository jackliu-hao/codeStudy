package javax.servlet.http;

import java.util.Enumeration;
import javax.servlet.ServletContext;

public interface HttpSession {
  long getCreationTime();
  
  String getId();
  
  long getLastAccessedTime();
  
  ServletContext getServletContext();
  
  void setMaxInactiveInterval(int paramInt);
  
  int getMaxInactiveInterval();
  
  @Deprecated
  HttpSessionContext getSessionContext();
  
  Object getAttribute(String paramString);
  
  @Deprecated
  Object getValue(String paramString);
  
  Enumeration<String> getAttributeNames();
  
  @Deprecated
  String[] getValueNames();
  
  void setAttribute(String paramString, Object paramObject);
  
  @Deprecated
  void putValue(String paramString, Object paramObject);
  
  void removeAttribute(String paramString);
  
  @Deprecated
  void removeValue(String paramString);
  
  void invalidate();
  
  boolean isNew();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSession.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */