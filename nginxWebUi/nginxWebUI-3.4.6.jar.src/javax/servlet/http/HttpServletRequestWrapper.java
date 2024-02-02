/*     */ package javax.servlet.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.Principal;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequestWrapper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServletRequestWrapper
/*     */   extends ServletRequestWrapper
/*     */   implements HttpServletRequest
/*     */ {
/*     */   public HttpServletRequestWrapper(HttpServletRequest request) {
/*  47 */     super(request);
/*     */   }
/*     */   
/*     */   private HttpServletRequest _getHttpServletRequest() {
/*  51 */     return (HttpServletRequest)getRequest();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAuthType() {
/*  60 */     return _getHttpServletRequest().getAuthType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cookie[] getCookies() {
/*  69 */     return _getHttpServletRequest().getCookies();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDateHeader(String name) {
/*  78 */     return _getHttpServletRequest().getDateHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeader(String name) {
/*  87 */     return _getHttpServletRequest().getHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration<String> getHeaders(String name) {
/*  96 */     return _getHttpServletRequest().getHeaders(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration<String> getHeaderNames() {
/* 105 */     return _getHttpServletRequest().getHeaderNames();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIntHeader(String name) {
/* 114 */     return _getHttpServletRequest().getIntHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpServletMapping getHttpServletMapping() {
/* 123 */     return _getHttpServletRequest().getHttpServletMapping();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMethod() {
/* 132 */     return _getHttpServletRequest().getMethod();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPathInfo() {
/* 141 */     return _getHttpServletRequest().getPathInfo();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPathTranslated() {
/* 150 */     return _getHttpServletRequest().getPathTranslated();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContextPath() {
/* 159 */     return _getHttpServletRequest().getContextPath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQueryString() {
/* 168 */     return _getHttpServletRequest().getQueryString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRemoteUser() {
/* 177 */     return _getHttpServletRequest().getRemoteUser();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUserInRole(String role) {
/* 186 */     return _getHttpServletRequest().isUserInRole(role);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Principal getUserPrincipal() {
/* 195 */     return _getHttpServletRequest().getUserPrincipal();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestedSessionId() {
/* 204 */     return _getHttpServletRequest().getRequestedSessionId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestURI() {
/* 213 */     return _getHttpServletRequest().getRequestURI();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StringBuffer getRequestURL() {
/* 222 */     return _getHttpServletRequest().getRequestURL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getServletPath() {
/* 231 */     return _getHttpServletRequest().getServletPath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpSession getSession(boolean create) {
/* 240 */     return _getHttpServletRequest().getSession(create);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpSession getSession() {
/* 249 */     return _getHttpServletRequest().getSession();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String changeSessionId() {
/* 260 */     return _getHttpServletRequest().changeSessionId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRequestedSessionIdValid() {
/* 269 */     return _getHttpServletRequest().isRequestedSessionIdValid();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRequestedSessionIdFromCookie() {
/* 278 */     return _getHttpServletRequest().isRequestedSessionIdFromCookie();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRequestedSessionIdFromURL() {
/* 287 */     return _getHttpServletRequest().isRequestedSessionIdFromURL();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isRequestedSessionIdFromUrl() {
/* 300 */     return _getHttpServletRequest().isRequestedSessionIdFromUrl();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
/* 312 */     return _getHttpServletRequest().authenticate(response);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void login(String username, String password) throws ServletException {
/* 324 */     _getHttpServletRequest().login(username, password);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void logout() throws ServletException {
/* 335 */     _getHttpServletRequest().logout();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<Part> getParts() throws IOException, ServletException {
/* 349 */     return _getHttpServletRequest().getParts();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Part getPart(String name) throws IOException, ServletException {
/* 360 */     return _getHttpServletRequest().getPart(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
/* 373 */     return _getHttpServletRequest().upgrade(handlerClass);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PushBuilder newPushBuilder() {
/* 384 */     return _getHttpServletRequest().newPushBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getTrailerFields() {
/* 395 */     return _getHttpServletRequest().getTrailerFields();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isTrailerFieldsReady() {
/* 406 */     return _getHttpServletRequest().isTrailerFieldsReady();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpServletRequestWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */