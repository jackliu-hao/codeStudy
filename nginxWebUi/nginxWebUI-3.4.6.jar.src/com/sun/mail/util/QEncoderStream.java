/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QEncoderStream
/*     */   extends QPEncoderStream
/*     */ {
/*     */   private String specials;
/*  55 */   private static String WORD_SPECIALS = "=_?\"#$%&'(),.:;<>@[\\]^`{|}~";
/*  56 */   private static String TEXT_SPECIALS = "=_?";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public QEncoderStream(OutputStream out, boolean encodingWord) {
/*  65 */     super(out, 2147483647);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     this.specials = encodingWord ? WORD_SPECIALS : TEXT_SPECIALS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(int c) throws IOException {
/*  81 */     c &= 0xFF;
/*  82 */     if (c == 32) {
/*  83 */       output(95, false);
/*  84 */     } else if (c < 32 || c >= 127 || this.specials.indexOf(c) >= 0) {
/*     */       
/*  86 */       output(c, true);
/*     */     } else {
/*  88 */       output(c, false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int encodedLength(byte[] b, boolean encodingWord) {
/*  95 */     int len = 0;
/*  96 */     String specials = encodingWord ? WORD_SPECIALS : TEXT_SPECIALS;
/*  97 */     for (int i = 0; i < b.length; i++) {
/*  98 */       int c = b[i] & 0xFF;
/*  99 */       if (c < 32 || c >= 127 || specials.indexOf(c) >= 0) {
/*     */         
/* 101 */         len += 3;
/*     */       } else {
/* 103 */         len++;
/*     */       } 
/* 105 */     }  return len;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\QEncoderStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */