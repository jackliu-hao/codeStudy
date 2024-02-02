package javax.servlet.http;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequestWrapper;

public class HttpServletRequestWrapper extends ServletRequestWrapper implements HttpServletRequest {
   public HttpServletRequestWrapper(HttpServletRequest request) {
      super(request);
   }

   private HttpServletRequest _getHttpServletRequest() {
      return (HttpServletRequest)super.getRequest();
   }

   public String getAuthType() {
      return this._getHttpServletRequest().getAuthType();
   }

   public Cookie[] getCookies() {
      return this._getHttpServletRequest().getCookies();
   }

   public long getDateHeader(String name) {
      return this._getHttpServletRequest().getDateHeader(name);
   }

   public String getHeader(String name) {
      return this._getHttpServletRequest().getHeader(name);
   }

   public Enumeration<String> getHeaders(String name) {
      return this._getHttpServletRequest().getHeaders(name);
   }

   public Enumeration<String> getHeaderNames() {
      return this._getHttpServletRequest().getHeaderNames();
   }

   public int getIntHeader(String name) {
      return this._getHttpServletRequest().getIntHeader(name);
   }

   public HttpServletMapping getHttpServletMapping() {
      return this._getHttpServletRequest().getHttpServletMapping();
   }

   public String getMethod() {
      return this._getHttpServletRequest().getMethod();
   }

   public String getPathInfo() {
      return this._getHttpServletRequest().getPathInfo();
   }

   public String getPathTranslated() {
      return this._getHttpServletRequest().getPathTranslated();
   }

   public String getContextPath() {
      return this._getHttpServletRequest().getContextPath();
   }

   public String getQueryString() {
      return this._getHttpServletRequest().getQueryString();
   }

   public String getRemoteUser() {
      return this._getHttpServletRequest().getRemoteUser();
   }

   public boolean isUserInRole(String role) {
      return this._getHttpServletRequest().isUserInRole(role);
   }

   public Principal getUserPrincipal() {
      return this._getHttpServletRequest().getUserPrincipal();
   }

   public String getRequestedSessionId() {
      return this._getHttpServletRequest().getRequestedSessionId();
   }

   public String getRequestURI() {
      return this._getHttpServletRequest().getRequestURI();
   }

   public StringBuffer getRequestURL() {
      return this._getHttpServletRequest().getRequestURL();
   }

   public String getServletPath() {
      return this._getHttpServletRequest().getServletPath();
   }

   public HttpSession getSession(boolean create) {
      return this._getHttpServletRequest().getSession(create);
   }

   public HttpSession getSession() {
      return this._getHttpServletRequest().getSession();
   }

   public String changeSessionId() {
      return this._getHttpServletRequest().changeSessionId();
   }

   public boolean isRequestedSessionIdValid() {
      return this._getHttpServletRequest().isRequestedSessionIdValid();
   }

   public boolean isRequestedSessionIdFromCookie() {
      return this._getHttpServletRequest().isRequestedSessionIdFromCookie();
   }

   public boolean isRequestedSessionIdFromURL() {
      return this._getHttpServletRequest().isRequestedSessionIdFromURL();
   }

   /** @deprecated */
   @Deprecated
   public boolean isRequestedSessionIdFromUrl() {
      return this._getHttpServletRequest().isRequestedSessionIdFromUrl();
   }

   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
      return this._getHttpServletRequest().authenticate(response);
   }

   public void login(String username, String password) throws ServletException {
      this._getHttpServletRequest().login(username, password);
   }

   public void logout() throws ServletException {
      this._getHttpServletRequest().logout();
   }

   public Collection<Part> getParts() throws IOException, ServletException {
      return this._getHttpServletRequest().getParts();
   }

   public Part getPart(String name) throws IOException, ServletException {
      return this._getHttpServletRequest().getPart(name);
   }

   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
      return this._getHttpServletRequest().upgrade(handlerClass);
   }

   public PushBuilder newPushBuilder() {
      return this._getHttpServletRequest().newPushBuilder();
   }

   public Map<String, String> getTrailerFields() {
      return this._getHttpServletRequest().getTrailerFields();
   }

   public boolean isTrailerFieldsReady() {
      return this._getHttpServletRequest().isTrailerFieldsReady();
   }
}
