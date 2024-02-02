/*     */ package com.google.zxing.datamatrix.encoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class EdifactEncoder
/*     */   implements Encoder
/*     */ {
/*     */   public int getEncodingMode() {
/*  23 */     return 4;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void encode(EncoderContext context) {
/*  29 */     StringBuilder buffer = new StringBuilder();
/*  30 */     while (context.hasMoreCharacters()) {
/*     */       
/*  32 */       encodeChar(context.getCurrentChar(), buffer);
/*  33 */       context.pos++;
/*     */       
/*  35 */       if (buffer.length() >= 
/*  36 */         4) {
/*  37 */         context.writeCodewords(encodeToCodewords(buffer, 0));
/*  38 */         buffer.delete(0, 4);
/*     */         
/*  40 */         if (HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode()) != 
/*  41 */           getEncodingMode()) {
/*  42 */           context.signalEncoderChange(0);
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*  47 */     buffer.append('\037');
/*  48 */     handleEOD(context, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void handleEOD(EncoderContext context, CharSequence buffer) {
/*     */     try {
/*     */       int count;
/*  60 */       if ((count = buffer.length()) == 0) {
/*     */         return;
/*     */       }
/*  63 */       if (count == 1) {
/*     */         
/*  65 */         context.updateSymbolInfo();
/*  66 */         int available = context.getSymbolInfo().getDataCapacity() - context.getCodewordCount();
/*  67 */         if (context.getRemainingCharacters() == 
/*  68 */           0 && available <= 2) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */       
/*  73 */       if (count > 4) {
/*  74 */         throw new IllegalStateException("Count must not exceed 4");
/*     */       }
/*  76 */       int restChars = count - 1;
/*  77 */       String encoded = encodeToCodewords(buffer, 0);
/*     */       
/*  79 */       boolean restInAscii = ((!context.hasMoreCharacters()) && restChars <= 2);
/*     */       
/*  81 */       if (restChars <= 2) {
/*  82 */         context.updateSymbolInfo(context.getCodewordCount() + restChars);
/*  83 */         if (context.getSymbolInfo().getDataCapacity() - context.getCodewordCount() >= 
/*  84 */           3) {
/*  85 */           restInAscii = false;
/*  86 */           context.updateSymbolInfo(context.getCodewordCount() + encoded.length());
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*  91 */       if (restInAscii) {
/*  92 */         context.resetSymbolInfo();
/*  93 */         context.pos -= restChars;
/*     */       } else {
/*  95 */         context.writeCodewords(encoded);
/*     */       }  return;
/*     */     } finally {
/*  98 */       context.signalEncoderChange(0);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void encodeChar(char c, StringBuilder sb) {
/* 103 */     if (c >= ' ' && c <= '?') {
/* 104 */       sb.append(c); return;
/* 105 */     }  if (c >= '@' && c <= '^') {
/* 106 */       sb.append((char)(c - 64)); return;
/*     */     } 
/* 108 */     HighLevelEncoder.illegalCharacter(c);
/*     */   }
/*     */ 
/*     */   
/*     */   private static String encodeToCodewords(CharSequence sb, int startPos) {
/*     */     int len;
/* 114 */     if ((len = sb.length() - startPos) == 0) {
/* 115 */       throw new IllegalStateException("StringBuilder must not be empty");
/*     */     }
/* 117 */     char c1 = sb.charAt(startPos);
/* 118 */     char c2 = (len >= 2) ? sb.charAt(startPos + 1) : Character.MIN_VALUE;
/* 119 */     char c3 = (len >= 3) ? sb.charAt(startPos + 2) : Character.MIN_VALUE;
/* 120 */     char c4 = (len >= 4) ? sb.charAt(startPos + 3) : Character.MIN_VALUE;
/*     */     
/*     */     int v;
/* 123 */     char cw1 = (char)((v = (c1 << 18) + (c2 << 12) + (c3 << 6) + c4) >> 16 & 0xFF);
/* 124 */     char cw2 = (char)(v >> 8 & 0xFF);
/* 125 */     char cw3 = (char)(v & 0xFF);
/*     */     StringBuilder res;
/* 127 */     (res = new StringBuilder(3)).append(cw1);
/* 128 */     if (len >= 2) {
/* 129 */       res.append(cw2);
/*     */     }
/* 131 */     if (len >= 3) {
/* 132 */       res.append(cw3);
/*     */     }
/* 134 */     return res.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\EdifactEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */