/*     */ package org.apache.http.protocol;
/*     */ 
/*     */ import org.apache.http.HttpConnection;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.util.Args;
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
/*     */ public class HttpCoreContext
/*     */   implements HttpContext
/*     */ {
/*     */   public static final String HTTP_CONNECTION = "http.connection";
/*     */   public static final String HTTP_REQUEST = "http.request";
/*     */   public static final String HTTP_RESPONSE = "http.response";
/*     */   public static final String HTTP_TARGET_HOST = "http.target_host";
/*     */   public static final String HTTP_REQ_SENT = "http.request_sent";
/*     */   private final HttpContext context;
/*     */   
/*     */   public static HttpCoreContext create() {
/*  76 */     return new HttpCoreContext(new BasicHttpContext());
/*     */   }
/*     */   
/*     */   public static HttpCoreContext adapt(HttpContext context) {
/*  80 */     Args.notNull(context, "HTTP context");
/*  81 */     return (context instanceof HttpCoreContext) ? (HttpCoreContext)context : new HttpCoreContext(context);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpCoreContext(HttpContext context) {
/*  90 */     this.context = context;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpCoreContext() {
/*  95 */     this.context = new BasicHttpContext();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getAttribute(String id) {
/* 100 */     return this.context.getAttribute(id);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setAttribute(String id, Object obj) {
/* 105 */     this.context.setAttribute(id, obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object removeAttribute(String id) {
/* 110 */     return this.context.removeAttribute(id);
/*     */   }
/*     */   
/*     */   public <T> T getAttribute(String attribname, Class<T> clazz) {
/* 114 */     Args.notNull(clazz, "Attribute class");
/* 115 */     Object obj = getAttribute(attribname);
/* 116 */     if (obj == null) {
/* 117 */       return null;
/*     */     }
/* 119 */     return clazz.cast(obj);
/*     */   }
/*     */   
/*     */   public <T extends HttpConnection> T getConnection(Class<T> clazz) {
/* 123 */     return (T)getAttribute("http.connection", clazz);
/*     */   }
/*     */   
/*     */   public HttpConnection getConnection() {
/* 127 */     return getAttribute("http.connection", HttpConnection.class);
/*     */   }
/*     */   
/*     */   public HttpRequest getRequest() {
/* 131 */     return getAttribute("http.request", HttpRequest.class);
/*     */   }
/*     */   
/*     */   public boolean isRequestSent() {
/* 135 */     Boolean b = getAttribute("http.request_sent", Boolean.class);
/* 136 */     return (b != null && b.booleanValue());
/*     */   }
/*     */   
/*     */   public HttpResponse getResponse() {
/* 140 */     return getAttribute("http.response", HttpResponse.class);
/*     */   }
/*     */   
/*     */   public void setTargetHost(HttpHost host) {
/* 144 */     setAttribute("http.target_host", host);
/*     */   }
/*     */   
/*     */   public HttpHost getTargetHost() {
/* 148 */     return getAttribute("http.target_host", HttpHost.class);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\HttpCoreContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */