/*     */ package org.apache.http.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.apache.http.Consts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class EncodingUtils
/*     */ {
/*     */   public static String getString(byte[] data, int offset, int length, String charset) {
/*  57 */     Args.notNull(data, "Input");
/*  58 */     Args.notEmpty(charset, "Charset");
/*     */     try {
/*  60 */       return new String(data, offset, length, charset);
/*  61 */     } catch (UnsupportedEncodingException e) {
/*  62 */       return new String(data, offset, length);
/*     */     } 
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
/*     */   public static String getString(byte[] data, String charset) {
/*  77 */     Args.notNull(data, "Input");
/*  78 */     return getString(data, 0, data.length, charset);
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
/*     */   public static byte[] getBytes(String data, String charset) {
/*  90 */     Args.notNull(data, "Input");
/*  91 */     Args.notEmpty(charset, "Charset");
/*     */     try {
/*  93 */       return data.getBytes(charset);
/*  94 */     } catch (UnsupportedEncodingException e) {
/*  95 */       return data.getBytes();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] getAsciiBytes(String data) {
/* 106 */     Args.notNull(data, "Input");
/* 107 */     return data.getBytes(Consts.ASCII);
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
/*     */   public static String getAsciiString(byte[] data, int offset, int length) {
/* 121 */     Args.notNull(data, "Input");
/* 122 */     return new String(data, offset, length, Consts.ASCII);
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
/*     */   public static String getAsciiString(byte[] data) {
/* 134 */     Args.notNull(data, "Input");
/* 135 */     return getAsciiString(data, 0, data.length);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\htt\\util\EncodingUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */