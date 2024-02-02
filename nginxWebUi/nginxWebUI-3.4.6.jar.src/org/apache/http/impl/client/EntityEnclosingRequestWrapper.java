/*     */ package org.apache.http.impl.client;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.entity.HttpEntityWrapper;
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
/*     */ @Deprecated
/*     */ public class EntityEnclosingRequestWrapper
/*     */   extends RequestWrapper
/*     */   implements HttpEntityEnclosingRequest
/*     */ {
/*     */   private HttpEntity entity;
/*     */   private boolean consumed;
/*     */   
/*     */   public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest request) throws ProtocolException {
/*  63 */     super((HttpRequest)request);
/*  64 */     setEntity(request.getEntity());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpEntity getEntity() {
/*  69 */     return this.entity;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEntity(HttpEntity entity) {
/*  74 */     this.entity = (entity != null) ? (HttpEntity)new EntityWrapper(entity) : null;
/*  75 */     this.consumed = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean expectContinue() {
/*  80 */     Header expect = getFirstHeader("Expect");
/*  81 */     return (expect != null && "100-continue".equalsIgnoreCase(expect.getValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRepeatable() {
/*  86 */     return (this.entity == null || this.entity.isRepeatable() || !this.consumed);
/*     */   }
/*     */   
/*     */   class EntityWrapper
/*     */     extends HttpEntityWrapper {
/*     */     EntityWrapper(HttpEntity entity) {
/*  92 */       super(entity);
/*     */     }
/*     */ 
/*     */     
/*     */     public void consumeContent() throws IOException {
/*  97 */       EntityEnclosingRequestWrapper.this.consumed = true;
/*  98 */       super.consumeContent();
/*     */     }
/*     */ 
/*     */     
/*     */     public InputStream getContent() throws IOException {
/* 103 */       EntityEnclosingRequestWrapper.this.consumed = true;
/* 104 */       return super.getContent();
/*     */     }
/*     */ 
/*     */     
/*     */     public void writeTo(OutputStream outStream) throws IOException {
/* 109 */       EntityEnclosingRequestWrapper.this.consumed = true;
/* 110 */       super.writeTo(outStream);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\EntityEnclosingRequestWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */