/*     */ package javax.servlet.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.servlet.ServletResponseWrapper;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpServletResponseWrapper
/*     */   extends ServletResponseWrapper
/*     */   implements HttpServletResponse
/*     */ {
/*     */   public HttpServletResponseWrapper(HttpServletResponse response) {
/*  48 */     super(response);
/*     */   }
/*     */   
/*     */   private HttpServletResponse _getHttpServletResponse() {
/*  52 */     return (HttpServletResponse)getResponse();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addCookie(Cookie cookie) {
/*  61 */     _getHttpServletResponse().addCookie(cookie);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsHeader(String name) {
/*  70 */     return _getHttpServletResponse().containsHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encodeURL(String url) {
/*  79 */     return _getHttpServletResponse().encodeURL(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encodeRedirectURL(String url) {
/*  88 */     return _getHttpServletResponse().encodeRedirectURL(url);
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
/*     */   public String encodeUrl(String url) {
/* 101 */     return _getHttpServletResponse().encodeUrl(url);
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
/*     */   public String encodeRedirectUrl(String url) {
/* 114 */     return _getHttpServletResponse().encodeRedirectUrl(url);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendError(int sc, String msg) throws IOException {
/* 123 */     _getHttpServletResponse().sendError(sc, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendError(int sc) throws IOException {
/* 132 */     _getHttpServletResponse().sendError(sc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendRedirect(String location) throws IOException {
/* 141 */     _getHttpServletResponse().sendRedirect(location);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDateHeader(String name, long date) {
/* 150 */     _getHttpServletResponse().setDateHeader(name, date);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDateHeader(String name, long date) {
/* 159 */     _getHttpServletResponse().addDateHeader(name, date);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeader(String name, String value) {
/* 168 */     _getHttpServletResponse().setHeader(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) {
/* 177 */     _getHttpServletResponse().addHeader(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIntHeader(String name, int value) {
/* 186 */     _getHttpServletResponse().setIntHeader(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addIntHeader(String name, int value) {
/* 195 */     _getHttpServletResponse().addIntHeader(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatus(int sc) {
/* 204 */     _getHttpServletResponse().setStatus(sc);
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
/*     */   @Deprecated
/*     */   public void setStatus(int sc, String sm) {
/* 219 */     _getHttpServletResponse().setStatus(sc, sm);
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
/*     */   public int getStatus() {
/* 231 */     return _getHttpServletResponse().getStatus();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHeader(String name) {
/* 249 */     return _getHttpServletResponse().getHeader(name);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> getHeaders(String name) {
/* 269 */     return _getHttpServletResponse().getHeaders(name);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> getHeaderNames() {
/* 287 */     return _getHttpServletResponse().getHeaderNames();
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
/*     */   public void setTrailerFields(Supplier<Map<String, String>> supplier) {
/* 301 */     _getHttpServletResponse().setTrailerFields(supplier);
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
/*     */   public Supplier<Map<String, String>> getTrailerFields() {
/* 315 */     return _getHttpServletResponse().getTrailerFields();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpServletResponseWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */