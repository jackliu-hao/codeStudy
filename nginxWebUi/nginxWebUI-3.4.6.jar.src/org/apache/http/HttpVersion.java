/*     */ package org.apache.http;
/*     */ 
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public final class HttpVersion
/*     */   extends ProtocolVersion
/*     */ {
/*     */   private static final long serialVersionUID = -5856653513894415344L;
/*     */   public static final String HTTP = "HTTP";
/*  55 */   public static final HttpVersion HTTP_0_9 = new HttpVersion(0, 9);
/*     */ 
/*     */   
/*  58 */   public static final HttpVersion HTTP_1_0 = new HttpVersion(1, 0);
/*     */ 
/*     */   
/*  61 */   public static final HttpVersion HTTP_1_1 = new HttpVersion(1, 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpVersion(int major, int minor) {
/*  73 */     super("HTTP", major, minor);
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
/*     */   public ProtocolVersion forVersion(int major, int minor) {
/*  88 */     if (major == this.major && minor == this.minor) {
/*  89 */       return this;
/*     */     }
/*     */     
/*  92 */     if (major == 1) {
/*  93 */       if (minor == 0) {
/*  94 */         return HTTP_1_0;
/*     */       }
/*  96 */       if (minor == 1) {
/*  97 */         return HTTP_1_1;
/*     */       }
/*     */     } 
/* 100 */     if (major == 0 && minor == 9) {
/* 101 */       return HTTP_0_9;
/*     */     }
/*     */ 
/*     */     
/* 105 */     return new HttpVersion(major, minor);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HttpVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */