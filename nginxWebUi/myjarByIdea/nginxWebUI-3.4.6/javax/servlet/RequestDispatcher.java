package javax.servlet;

import java.io.IOException;

public interface RequestDispatcher {
   String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
   String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
   String FORWARD_MAPPING = "javax.servlet.forward.mapping";
   String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
   String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
   String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";
   String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
   String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
   String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
   String INCLUDE_MAPPING = "javax.servlet.include.mapping";
   String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
   String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
   String ERROR_EXCEPTION = "javax.servlet.error.exception";
   String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
   String ERROR_MESSAGE = "javax.servlet.error.message";
   String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
   String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
   String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

   void forward(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;

   void include(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;
}
