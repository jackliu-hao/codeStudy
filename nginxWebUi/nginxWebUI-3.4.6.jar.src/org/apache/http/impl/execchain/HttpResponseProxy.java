/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ class HttpResponseProxy
/*     */   implements CloseableHttpResponse
/*     */ {
/*     */   private final HttpResponse original;
/*     */   private final ConnectionHolder connHolder;
/*     */   
/*     */   public HttpResponseProxy(HttpResponse original, ConnectionHolder connHolder) {
/*  54 */     this.original = original;
/*  55 */     this.connHolder = connHolder;
/*  56 */     ResponseEntityProxy.enchance(original, connHolder);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  61 */     if (this.connHolder != null) {
/*  62 */       this.connHolder.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StatusLine getStatusLine() {
/*  68 */     return this.original.getStatusLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusLine(StatusLine statusline) {
/*  73 */     this.original.setStatusLine(statusline);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusLine(ProtocolVersion ver, int code) {
/*  78 */     this.original.setStatusLine(ver, code);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusLine(ProtocolVersion ver, int code, String reason) {
/*  83 */     this.original.setStatusLine(ver, code, reason);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setStatusCode(int code) throws IllegalStateException {
/*  88 */     this.original.setStatusCode(code);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setReasonPhrase(String reason) throws IllegalStateException {
/*  93 */     this.original.setReasonPhrase(reason);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpEntity getEntity() {
/*  98 */     return this.original.getEntity();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEntity(HttpEntity entity) {
/* 103 */     this.original.setEntity(entity);
/*     */   }
/*     */ 
/*     */   
/*     */   public Locale getLocale() {
/* 108 */     return this.original.getLocale();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setLocale(Locale loc) {
/* 113 */     this.original.setLocale(loc);
/*     */   }
/*     */ 
/*     */   
/*     */   public ProtocolVersion getProtocolVersion() {
/* 118 */     return this.original.getProtocolVersion();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsHeader(String name) {
/* 123 */     return this.original.containsHeader(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Header[] getHeaders(String name) {
/* 128 */     return this.original.getHeaders(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getFirstHeader(String name) {
/* 133 */     return this.original.getFirstHeader(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Header getLastHeader(String name) {
/* 138 */     return this.original.getLastHeader(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public Header[] getAllHeaders() {
/* 143 */     return this.original.getAllHeaders();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addHeader(Header header) {
/* 148 */     this.original.addHeader(header);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) {
/* 153 */     this.original.addHeader(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHeader(Header header) {
/* 158 */     this.original.setHeader(header);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHeader(String name, String value) {
/* 163 */     this.original.setHeader(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHeaders(Header[] headers) {
/* 168 */     this.original.setHeaders(headers);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeHeader(Header header) {
/* 173 */     this.original.removeHeader(header);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeHeaders(String name) {
/* 178 */     this.original.removeHeaders(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public HeaderIterator headerIterator() {
/* 183 */     return this.original.headerIterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public HeaderIterator headerIterator(String name) {
/* 188 */     return this.original.headerIterator(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpParams getParams() {
/* 193 */     return this.original.getParams();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setParams(HttpParams params) {
/* 198 */     this.original.setParams(params);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 203 */     StringBuilder sb = new StringBuilder("HttpResponseProxy{");
/* 204 */     sb.append(this.original);
/* 205 */     sb.append('}');
/* 206 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\HttpResponseProxy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */