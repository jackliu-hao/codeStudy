/*     */ package org.apache.http.message;
/*     */ 
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderIterator;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.params.BasicHttpParams;
/*     */ import org.apache.http.params.HttpParams;
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
/*     */ public abstract class AbstractHttpMessage
/*     */   implements HttpMessage
/*     */ {
/*     */   protected HeaderGroup headergroup;
/*     */   @Deprecated
/*     */   protected HttpParams params;
/*     */   
/*     */   @Deprecated
/*     */   protected AbstractHttpMessage(HttpParams params) {
/*  59 */     this.headergroup = new HeaderGroup();
/*  60 */     this.params = params;
/*     */   }
/*     */   
/*     */   protected AbstractHttpMessage() {
/*  64 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsHeader(String name) {
/*  70 */     return this.headergroup.containsHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Header[] getHeaders(String name) {
/*  76 */     return this.headergroup.getHeaders(name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Header getFirstHeader(String name) {
/*  82 */     return this.headergroup.getFirstHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Header getLastHeader(String name) {
/*  88 */     return this.headergroup.getLastHeader(name);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Header[] getAllHeaders() {
/*  94 */     return this.headergroup.getAllHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeader(Header header) {
/* 100 */     this.headergroup.addHeader(header);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) {
/* 106 */     Args.notNull(name, "Header name");
/* 107 */     this.headergroup.addHeader(new BasicHeader(name, value));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeader(Header header) {
/* 113 */     this.headergroup.updateHeader(header);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeader(String name, String value) {
/* 119 */     Args.notNull(name, "Header name");
/* 120 */     this.headergroup.updateHeader(new BasicHeader(name, value));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeaders(Header[] headers) {
/* 126 */     this.headergroup.setHeaders(headers);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHeader(Header header) {
/* 132 */     this.headergroup.removeHeader(header);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHeaders(String name) {
/* 138 */     if (name == null) {
/*     */       return;
/*     */     }
/* 141 */     for (HeaderIterator i = this.headergroup.iterator(); i.hasNext(); ) {
/* 142 */       Header header = i.nextHeader();
/* 143 */       if (name.equalsIgnoreCase(header.getName())) {
/* 144 */         i.remove();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderIterator headerIterator() {
/* 152 */     return this.headergroup.iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderIterator headerIterator(String name) {
/* 158 */     return this.headergroup.iterator(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpParams getParams() {
/* 167 */     if (this.params == null) {
/* 168 */       this.params = (HttpParams)new BasicHttpParams();
/*     */     }
/* 170 */     return this.params;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setParams(HttpParams params) {
/* 179 */     this.params = (HttpParams)Args.notNull(params, "HTTP parameters");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\AbstractHttpMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */