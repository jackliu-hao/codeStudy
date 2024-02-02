/*     */ package org.apache.http.impl.entity;
/*     */ 
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HeaderElement;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpMessage;
/*     */ import org.apache.http.ParseException;
/*     */ import org.apache.http.ProtocolException;
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
/*     */ public class LaxContentLengthStrategy
/*     */   implements ContentLengthStrategy
/*     */ {
/*  54 */   public static final LaxContentLengthStrategy INSTANCE = new LaxContentLengthStrategy();
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
/*     */   public LaxContentLengthStrategy(int implicitLen) {
/*  68 */     this.implicitLen = implicitLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LaxContentLengthStrategy() {
/*  76 */     this(-1);
/*     */   }
/*     */ 
/*     */   
/*     */   public long determineLength(HttpMessage message) throws HttpException {
/*  81 */     Args.notNull(message, "HTTP message");
/*     */     
/*  83 */     Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
/*     */ 
/*     */     
/*  86 */     if (transferEncodingHeader != null) {
/*     */       HeaderElement[] encodings;
/*     */       try {
/*  89 */         encodings = transferEncodingHeader.getElements();
/*  90 */       } catch (ParseException px) {
/*  91 */         throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, px);
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  96 */       int len = encodings.length;
/*  97 */       if ("identity".equalsIgnoreCase(transferEncodingHeader.getValue()))
/*  98 */         return -1L; 
/*  99 */       if (len > 0 && "chunked".equalsIgnoreCase(encodings[len - 1].getName()))
/*     */       {
/* 101 */         return -2L;
/*     */       }
/* 103 */       return -1L;
/*     */     } 
/*     */     
/* 106 */     Header contentLengthHeader = message.getFirstHeader("Content-Length");
/* 107 */     if (contentLengthHeader != null) {
/* 108 */       long contentLen = -1L;
/* 109 */       Header[] headers = message.getHeaders("Content-Length");
/* 110 */       for (int i = headers.length - 1; i >= 0; i--) {
/* 111 */         Header header = headers[i];
/*     */         try {
/* 113 */           contentLen = Long.parseLong(header.getValue());
/*     */           break;
/* 115 */         } catch (NumberFormatException ignore) {}
/*     */       } 
/*     */ 
/*     */       
/* 119 */       return (contentLen >= 0L) ? contentLen : -1L;
/*     */     } 
/* 121 */     return this.implicitLen;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\entity\LaxContentLengthStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */