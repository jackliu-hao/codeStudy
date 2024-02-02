/*     */ package org.apache.http.client.methods;
/*     */ 
/*     */ import java.net.URI;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.message.BasicRequestLine;
/*     */ import org.apache.http.params.HttpProtocolParams;
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
/*     */ public abstract class HttpRequestBase
/*     */   extends AbstractExecutionAwareRequest
/*     */   implements HttpUriRequest, Configurable
/*     */ {
/*     */   private ProtocolVersion version;
/*     */   private URI uri;
/*     */   private RequestConfig config;
/*     */   
/*     */   public abstract String getMethod();
/*     */   
/*     */   public void setProtocolVersion(ProtocolVersion version) {
/*  58 */     this.version = version;
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/*  63 */     return (this.version != null) ? this.version : HttpProtocolParams.getVersion(getParams());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getURI() {
/*  74 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public RequestLine getRequestLine() {
/*  79 */     String method = getMethod();
/*  80 */     ProtocolVersion ver = getProtocolVersion();
/*  81 */     URI uriCopy = getURI();
/*  82 */     String uritext = null;
/*  83 */     if (uriCopy != null) {
/*  84 */       uritext = uriCopy.toASCIIString();
/*     */     }
/*  86 */     if (uritext == null || uritext.isEmpty()) {
/*  87 */       uritext = "/";
/*     */     }
/*  89 */     return (RequestLine)new BasicRequestLine(method, uritext, ver);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestConfig getConfig() {
/*  95 */     return this.config;
/*     */   }
/*     */   
/*     */   public void setConfig(RequestConfig config) {
/*  99 */     this.config = config;
/*     */   }
/*     */   
/*     */   public void setURI(URI uri) {
/* 103 */     this.uri = uri;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void started() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseConnection() {
/* 119 */     reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 124 */     return getMethod() + " " + getURI() + " " + getProtocolVersion();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\methods\HttpRequestBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */