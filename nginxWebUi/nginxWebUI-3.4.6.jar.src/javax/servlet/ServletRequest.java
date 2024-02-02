package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public interface ServletRequest {
  Object getAttribute(String paramString);
  
  Enumeration<String> getAttributeNames();
  
  String getCharacterEncoding();
  
  void setCharacterEncoding(String paramString) throws UnsupportedEncodingException;
  
  int getContentLength();
  
  long getContentLengthLong();
  
  String getContentType();
  
  ServletInputStream getInputStream() throws IOException;
  
  String getParameter(String paramString);
  
  Enumeration<String> getParameterNames();
  
  String[] getParameterValues(String paramString);
  
  Map<String, String[]> getParameterMap();
  
  String getProtocol();
  
  String getScheme();
  
  String getServerName();
  
  int getServerPort();
  
  BufferedReader getReader() throws IOException;
  
  String getRemoteAddr();
  
  String getRemoteHost();
  
  void setAttribute(String paramString, Object paramObject);
  
  void removeAttribute(String paramString);
  
  Locale getLocale();
  
  Enumeration<Locale> getLocales();
  
  boolean isSecure();
  
  RequestDispatcher getRequestDispatcher(String paramString);
  
  String getRealPath(String paramString);
  
  int getRemotePort();
  
  String getLocalName();
  
  String getLocalAddr();
  
  int getLocalPort();
  
  ServletContext getServletContext();
  
  AsyncContext startAsync() throws IllegalStateException;
  
  AsyncContext startAsync(ServletRequest paramServletRequest, ServletResponse paramServletResponse) throws IllegalStateException;
  
  boolean isAsyncStarted();
  
  boolean isAsyncSupported();
  
  AsyncContext getAsyncContext();
  
  DispatcherType getDispatcherType();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\ServletRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */