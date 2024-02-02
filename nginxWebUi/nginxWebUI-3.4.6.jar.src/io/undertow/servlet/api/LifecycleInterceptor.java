package io.undertow.servlet.api;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

public interface LifecycleInterceptor {
  void init(ServletInfo paramServletInfo, Servlet paramServlet, LifecycleContext paramLifecycleContext) throws ServletException;
  
  void init(FilterInfo paramFilterInfo, Filter paramFilter, LifecycleContext paramLifecycleContext) throws ServletException;
  
  void destroy(ServletInfo paramServletInfo, Servlet paramServlet, LifecycleContext paramLifecycleContext) throws ServletException;
  
  void destroy(FilterInfo paramFilterInfo, Filter paramFilter, LifecycleContext paramLifecycleContext) throws ServletException;
  
  public static interface LifecycleContext {
    void proceed() throws ServletException;
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\LifecycleInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */