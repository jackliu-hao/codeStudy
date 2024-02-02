/*     */ package org.apache.http.message;
/*     */ 
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.RequestLine;
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
/*     */ public class BasicHttpRequest
/*     */   extends AbstractHttpMessage
/*     */   implements HttpRequest
/*     */ {
/*     */   private final String method;
/*     */   private final String uri;
/*     */   private RequestLine requestline;
/*     */   
/*     */   public BasicHttpRequest(String method, String uri) {
/*  57 */     this.method = (String)Args.notNull(method, "Method name");
/*  58 */     this.uri = (String)Args.notNull(uri, "Request URI");
/*  59 */     this.requestline = null;
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
/*     */   public BasicHttpRequest(String method, String uri, ProtocolVersion ver) {
/*  71 */     this(new BasicRequestLine(method, uri, ver));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicHttpRequest(RequestLine requestline) {
/*  81 */     this.requestline = (RequestLine)Args.notNull(requestline, "Request line");
/*  82 */     this.method = requestline.getMethod();
/*  83 */     this.uri = requestline.getUri();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/*  93 */     return getRequestLine().getProtocolVersion();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestLine getRequestLine() {
/* 103 */     if (this.requestline == null) {
/* 104 */       this.requestline = new BasicRequestLine(this.method, this.uri, (ProtocolVersion)HttpVersion.HTTP_1_1);
/*     */     }
/* 106 */     return this.requestline;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 111 */     return this.method + ' ' + this.uri + ' ' + this.headergroup;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicHttpRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */