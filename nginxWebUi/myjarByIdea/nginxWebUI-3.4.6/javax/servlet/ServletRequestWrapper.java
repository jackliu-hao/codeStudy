package javax.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class ServletRequestWrapper implements ServletRequest {
   private ServletRequest request;

   public ServletRequestWrapper(ServletRequest request) {
      if (request == null) {
         throw new IllegalArgumentException("Request cannot be null");
      } else {
         this.request = request;
      }
   }

   public ServletRequest getRequest() {
      return this.request;
   }

   public void setRequest(ServletRequest request) {
      if (request == null) {
         throw new IllegalArgumentException("Request cannot be null");
      } else {
         this.request = request;
      }
   }

   public Object getAttribute(String name) {
      return this.request.getAttribute(name);
   }

   public Enumeration<String> getAttributeNames() {
      return this.request.getAttributeNames();
   }

   public String getCharacterEncoding() {
      return this.request.getCharacterEncoding();
   }

   public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
      this.request.setCharacterEncoding(enc);
   }

   public int getContentLength() {
      return this.request.getContentLength();
   }

   public long getContentLengthLong() {
      return this.request.getContentLengthLong();
   }

   public String getContentType() {
      return this.request.getContentType();
   }

   public ServletInputStream getInputStream() throws IOException {
      return this.request.getInputStream();
   }

   public String getParameter(String name) {
      return this.request.getParameter(name);
   }

   public Map<String, String[]> getParameterMap() {
      return this.request.getParameterMap();
   }

   public Enumeration<String> getParameterNames() {
      return this.request.getParameterNames();
   }

   public String[] getParameterValues(String name) {
      return this.request.getParameterValues(name);
   }

   public String getProtocol() {
      return this.request.getProtocol();
   }

   public String getScheme() {
      return this.request.getScheme();
   }

   public String getServerName() {
      return this.request.getServerName();
   }

   public int getServerPort() {
      return this.request.getServerPort();
   }

   public BufferedReader getReader() throws IOException {
      return this.request.getReader();
   }

   public String getRemoteAddr() {
      return this.request.getRemoteAddr();
   }

   public String getRemoteHost() {
      return this.request.getRemoteHost();
   }

   public void setAttribute(String name, Object o) {
      this.request.setAttribute(name, o);
   }

   public void removeAttribute(String name) {
      this.request.removeAttribute(name);
   }

   public Locale getLocale() {
      return this.request.getLocale();
   }

   public Enumeration<Locale> getLocales() {
      return this.request.getLocales();
   }

   public boolean isSecure() {
      return this.request.isSecure();
   }

   public RequestDispatcher getRequestDispatcher(String path) {
      return this.request.getRequestDispatcher(path);
   }

   /** @deprecated */
   @Deprecated
   public String getRealPath(String path) {
      return this.request.getRealPath(path);
   }

   public int getRemotePort() {
      return this.request.getRemotePort();
   }

   public String getLocalName() {
      return this.request.getLocalName();
   }

   public String getLocalAddr() {
      return this.request.getLocalAddr();
   }

   public int getLocalPort() {
      return this.request.getLocalPort();
   }

   public ServletContext getServletContext() {
      return this.request.getServletContext();
   }

   public AsyncContext startAsync() throws IllegalStateException {
      return this.request.startAsync();
   }

   public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
      return this.request.startAsync(servletRequest, servletResponse);
   }

   public boolean isAsyncStarted() {
      return this.request.isAsyncStarted();
   }

   public boolean isAsyncSupported() {
      return this.request.isAsyncSupported();
   }

   public AsyncContext getAsyncContext() {
      return this.request.getAsyncContext();
   }

   public boolean isWrapperFor(ServletRequest wrapped) {
      if (this.request == wrapped) {
         return true;
      } else {
         return this.request instanceof ServletRequestWrapper ? ((ServletRequestWrapper)this.request).isWrapperFor(wrapped) : false;
      }
   }

   public boolean isWrapperFor(Class<?> wrappedType) {
      if (!ServletRequest.class.isAssignableFrom(wrappedType)) {
         throw new IllegalArgumentException("Given class " + wrappedType.getName() + " not a subinterface of " + ServletRequest.class.getName());
      } else if (wrappedType.isAssignableFrom(this.request.getClass())) {
         return true;
      } else {
         return this.request instanceof ServletRequestWrapper ? ((ServletRequestWrapper)this.request).isWrapperFor(wrappedType) : false;
      }
   }

   public DispatcherType getDispatcherType() {
      return this.request.getDispatcherType();
   }
}
