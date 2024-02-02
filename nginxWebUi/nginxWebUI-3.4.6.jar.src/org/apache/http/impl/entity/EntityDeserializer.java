/*     */ package org.apache.http.impl.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.entity.BasicHttpEntity;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.io.ChunkedInputStream;
/*     */ import org.apache.http.impl.io.ContentLengthInputStream;
/*     */ import org.apache.http.impl.io.IdentityInputStream;
/*     */ import org.apache.http.io.SessionInputBuffer;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class EntityDeserializer
/*     */ {
/*     */   private final ContentLengthStrategy lenStrategy;
/*     */   
/*     */   public EntityDeserializer(ContentLengthStrategy lenStrategy) {
/*  73 */     this.lenStrategy = (ContentLengthStrategy)Args.notNull(lenStrategy, "Content length strategy");
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
/*     */   
/*     */   protected BasicHttpEntity doDeserialize(SessionInputBuffer inBuffer, HttpMessage message) throws HttpException, IOException {
/*  94 */     BasicHttpEntity entity = new BasicHttpEntity();
/*     */     
/*  96 */     long len = this.lenStrategy.determineLength(message);
/*  97 */     if (len == -2L) {
/*  98 */       entity.setChunked(true);
/*  99 */       entity.setContentLength(-1L);
/* 100 */       entity.setContent((InputStream)new ChunkedInputStream(inBuffer));
/* 101 */     } else if (len == -1L) {
/* 102 */       entity.setChunked(false);
/* 103 */       entity.setContentLength(-1L);
/* 104 */       entity.setContent((InputStream)new IdentityInputStream(inBuffer));
/*     */     } else {
/* 106 */       entity.setChunked(false);
/* 107 */       entity.setContentLength(len);
/* 108 */       entity.setContent((InputStream)new ContentLengthInputStream(inBuffer, len));
/*     */     } 
/*     */     
/* 111 */     Header contentTypeHeader = message.getFirstHeader("Content-Type");
/* 112 */     if (contentTypeHeader != null) {
/* 113 */       entity.setContentType(contentTypeHeader);
/*     */     }
/* 115 */     Header contentEncodingHeader = message.getFirstHeader("Content-Encoding");
/* 116 */     if (contentEncodingHeader != null) {
/* 117 */       entity.setContentEncoding(contentEncodingHeader);
/*     */     }
/* 119 */     return entity;
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
/*     */   public HttpEntity deserialize(SessionInputBuffer inBuffer, HttpMessage message) throws HttpException, IOException {
/* 139 */     Args.notNull(inBuffer, "Session input buffer");
/* 140 */     Args.notNull(message, "HTTP message");
/* 141 */     return (HttpEntity)doDeserialize(inBuffer, message);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\entity\EntityDeserializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */