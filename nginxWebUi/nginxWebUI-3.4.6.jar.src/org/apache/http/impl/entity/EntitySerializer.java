/*     */ package org.apache.http.impl.entity;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
/*     */ import org.apache.http.impl.io.ChunkedOutputStream;
/*     */ import org.apache.http.impl.io.ContentLengthOutputStream;
/*     */ import org.apache.http.impl.io.IdentityOutputStream;
/*     */ import org.apache.http.io.SessionOutputBuffer;
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
/*     */ @Deprecated
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class EntitySerializer
/*     */ {
/*     */   private final ContentLengthStrategy lenStrategy;
/*     */   
/*     */   public EntitySerializer(ContentLengthStrategy lenStrategy) {
/*  70 */     this.lenStrategy = (ContentLengthStrategy)Args.notNull(lenStrategy, "Content length strategy");
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
/*     */   protected OutputStream doSerialize(SessionOutputBuffer outbuffer, HttpMessage message) throws HttpException, IOException {
/*  90 */     long len = this.lenStrategy.determineLength(message);
/*  91 */     if (len == -2L)
/*  92 */       return (OutputStream)new ChunkedOutputStream(outbuffer); 
/*  93 */     if (len == -1L) {
/*  94 */       return (OutputStream)new IdentityOutputStream(outbuffer);
/*     */     }
/*  96 */     return (OutputStream)new ContentLengthOutputStream(outbuffer, len);
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
/*     */   public void serialize(SessionOutputBuffer outbuffer, HttpMessage message, HttpEntity entity) throws HttpException, IOException {
/* 114 */     Args.notNull(outbuffer, "Session output buffer");
/* 115 */     Args.notNull(message, "HTTP message");
/* 116 */     Args.notNull(entity, "HTTP entity");
/* 117 */     OutputStream outStream = doSerialize(outbuffer, message);
/* 118 */     entity.writeTo(outStream);
/* 119 */     outStream.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\entity\EntitySerializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */