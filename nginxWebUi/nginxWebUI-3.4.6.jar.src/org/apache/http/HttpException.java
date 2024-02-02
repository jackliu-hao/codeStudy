/*     */ package org.apache.http;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpException
/*     */   extends Exception
/*     */ {
/*     */   private static final int FIRST_VALID_CHAR = 32;
/*     */   private static final long serialVersionUID = -5437299376222011036L;
/*     */   
/*     */   static String clean(String message) {
/*  48 */     char[] chars = message.toCharArray();
/*     */     
/*     */     int i;
/*  51 */     for (i = 0; i < chars.length && 
/*  52 */       chars[i] >= ' '; i++);
/*     */ 
/*     */ 
/*     */     
/*  56 */     if (i == chars.length) {
/*  57 */       return message;
/*     */     }
/*  59 */     StringBuilder builder = new StringBuilder(chars.length * 2);
/*  60 */     for (i = 0; i < chars.length; i++) {
/*  61 */       char ch = chars[i];
/*  62 */       if (ch < ' ') {
/*  63 */         builder.append("[0x");
/*  64 */         String hexString = Integer.toHexString(i);
/*  65 */         if (hexString.length() == 1) {
/*  66 */           builder.append("0");
/*     */         }
/*  68 */         builder.append(hexString);
/*  69 */         builder.append("]");
/*     */       } else {
/*  71 */         builder.append(ch);
/*     */       } 
/*     */     } 
/*  74 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpException() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpException(String message) {
/*  91 */     super(clean(message));
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
/*     */   public HttpException(String message, Throwable cause) {
/* 105 */     super(clean(message));
/* 106 */     initCause(cause);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HttpException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */