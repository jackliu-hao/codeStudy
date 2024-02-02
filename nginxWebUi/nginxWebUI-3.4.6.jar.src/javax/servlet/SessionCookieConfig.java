package javax.servlet;

public interface SessionCookieConfig {
  void setName(String paramString);
  
  String getName();
  
  void setDomain(String paramString);
  
  String getDomain();
  
  void setPath(String paramString);
  
  String getPath();
  
  void setComment(String paramString);
  
  String getComment();
  
  void setHttpOnly(boolean paramBoolean);
  
  boolean isHttpOnly();
  
  void setSecure(boolean paramBoolean);
  
  boolean isSecure();
  
  void setMaxAge(int paramInt);
  
  int getMaxAge();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\SessionCookieConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */