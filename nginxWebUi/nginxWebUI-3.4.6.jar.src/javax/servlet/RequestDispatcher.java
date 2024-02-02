package javax.servlet;

import java.io.IOException;

public interface RequestDispatcher {
  public static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
  
  public static final String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
  
  public static final String FORWARD_MAPPING = "javax.servlet.forward.mapping";
  
  public static final String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
  
  public static final String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
  
  public static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";
  
  public static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
  
  public static final String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
  
  public static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
  
  public static final String INCLUDE_MAPPING = "javax.servlet.include.mapping";
  
  public static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
  
  public static final String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
  
  public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
  
  public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
  
  public static final String ERROR_MESSAGE = "javax.servlet.error.message";
  
  public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
  
  public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
  
  public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
  
  void forward(ServletRequest paramServletRequest, ServletResponse paramServletResponse) throws ServletException, IOException;
  
  void include(ServletRequest paramServletRequest, ServletResponse paramServletResponse) throws ServletException, IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\RequestDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */