package javax.servlet;

import java.util.Enumeration;

public interface FilterConfig {
  String getFilterName();
  
  ServletContext getServletContext();
  
  String getInitParameter(String paramString);
  
  Enumeration<String> getInitParameterNames();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\FilterConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */