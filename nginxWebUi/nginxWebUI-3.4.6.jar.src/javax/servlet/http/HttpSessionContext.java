package javax.servlet.http;

import java.util.Enumeration;

@Deprecated
public interface HttpSessionContext {
  @Deprecated
  HttpSession getSession(String paramString);
  
  @Deprecated
  Enumeration<String> getIds();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */