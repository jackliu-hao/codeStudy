/*     */ package org.apache.http.impl.entity;
/*     */ 
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.entity.ContentLengthStrategy;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class StrictContentLengthStrategy
/*     */   implements ContentLengthStrategy
/*     */ {
/*  54 */   public static final StrictContentLengthStrategy INSTANCE = new StrictContentLengthStrategy();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int implicitLen;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrictContentLengthStrategy(int implicitLen) {
/*  68 */     this.implicitLen = implicitLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrictContentLengthStrategy() {
/*  76 */     this(-1);
/*     */   }
/*     */ 
/*     */   
/*     */   public long determineLength(HttpMessage message) throws HttpException {
/*  81 */     Args.notNull(message, "HTTP message");
/*     */ 
/*     */ 
/*     */     
/*  85 */     Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
/*  86 */     if (transferEncodingHeader != null) {
/*  87 */       String s = transferEncodingHeader.getValue();
/*  88 */       if ("chunked".equalsIgnoreCase(s)) {
/*  89 */         if (message.getProtocolVersion().lessEquals((ProtocolVersion)HttpVersion.HTTP_1_0)) {
/*  90 */           throw new ProtocolException("Chunked transfer encoding not allowed for " + message.getProtocolVersion());
/*     */         }
/*     */ 
/*     */         
/*  94 */         return -2L;
/*  95 */       }  if ("identity".equalsIgnoreCase(s)) {
/*  96 */         return -1L;
/*     */       }
/*  98 */       throw new ProtocolException("Unsupported transfer encoding: " + s);
/*     */     } 
/*     */ 
/*     */     
/* 102 */     Header contentLengthHeader = message.getFirstHeader("Content-Length");
/* 103 */     if (contentLengthHeader != null) {
/* 104 */       String s = contentLengthHeader.getValue();
/*     */       try {
/* 106 */         long len = Long.parseLong(s);
/* 107 */         if (len < 0L) {
/* 108 */           throw new ProtocolException("Negative content length: " + s);
/*     */         }
/* 110 */         return len;
/* 111 */       } catch (NumberFormatException e) {
/* 112 */         throw new ProtocolException("Invalid content length: " + s);
/*     */       } 
/*     */     } 
/* 115 */     return this.implicitLen;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\entity\StrictContentLengthStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */