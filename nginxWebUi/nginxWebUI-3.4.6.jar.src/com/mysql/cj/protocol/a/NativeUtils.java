/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NativeUtils
/*     */ {
/*     */   public static byte[] encodeMysqlThreeByteInteger(int i) {
/*  42 */     byte[] b = new byte[3];
/*  43 */     b[0] = (byte)(i & 0xFF);
/*  44 */     b[1] = (byte)(i >>> 8);
/*  45 */     b[2] = (byte)(i >>> 16);
/*  46 */     return b;
/*     */   }
/*     */   
/*     */   public static void encodeMysqlThreeByteInteger(int i, byte[] b, int offset) {
/*  50 */     b[offset++] = (byte)(i & 0xFF);
/*  51 */     b[offset++] = (byte)(i >>> 8);
/*  52 */     b[offset] = (byte)(i >>> 16);
/*     */   }
/*     */   
/*     */   public static int decodeMysqlThreeByteInteger(byte[] b) {
/*  56 */     return decodeMysqlThreeByteInteger(b, 0);
/*     */   }
/*     */   
/*     */   public static int decodeMysqlThreeByteInteger(byte[] b, int offset) {
/*  60 */     return (b[offset + 0] & 0xFF) + ((b[offset + 1] & 0xFF) << 8) + ((b[offset + 2] & 0xFF) << 16);
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
/*     */   public static int getBinaryEncodedLength(int type) {
/*  72 */     switch (type) {
/*     */       case 1:
/*  74 */         return 1;
/*     */       case 2:
/*     */       case 13:
/*  77 */         return 2;
/*     */       case 3:
/*     */       case 4:
/*     */       case 9:
/*  81 */         return 4;
/*     */       case 5:
/*     */       case 8:
/*  84 */         return 8;
/*     */       case 0:
/*     */       case 6:
/*     */       case 7:
/*     */       case 10:
/*     */       case 11:
/*     */       case 12:
/*     */       case 15:
/*     */       case 16:
/*     */       case 245:
/*     */       case 246:
/*     */       case 249:
/*     */       case 250:
/*     */       case 251:
/*     */       case 252:
/*     */       case 253:
/*     */       case 254:
/*     */       case 255:
/* 102 */         return 0;
/*     */     } 
/* 104 */     return -1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativeUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */