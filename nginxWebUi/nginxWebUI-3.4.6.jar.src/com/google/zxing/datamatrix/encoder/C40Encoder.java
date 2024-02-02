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
/*     */ class C40Encoder
/*     */   implements Encoder
/*     */ {
/*     */   public int getEncodingMode() {
/*  23 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void encode(EncoderContext context) {
/*  29 */     StringBuilder buffer = new StringBuilder();
/*  30 */     while (context.hasMoreCharacters()) {
/*  31 */       char c = context.getCurrentChar();
/*  32 */       context.pos++;
/*     */       
/*  34 */       int lastCharSize = encodeChar(c, buffer);
/*     */       
/*  36 */       int unwritten = buffer.length() / 3 << 1;
/*     */       
/*  38 */       int curCodewordCount = context.getCodewordCount() + unwritten;
/*  39 */       context.updateSymbolInfo(curCodewordCount);
/*  40 */       int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;
/*     */       
/*  42 */       if (!context.hasMoreCharacters()) {
/*     */         
/*  44 */         StringBuilder removed = new StringBuilder();
/*  45 */         if (buffer.length() % 3 == 2 && (
/*  46 */           available < 2 || available > 2)) {
/*  47 */           lastCharSize = backtrackOneCharacter(context, buffer, removed, lastCharSize);
/*     */         }
/*     */ 
/*     */         
/*  51 */         while (buffer.length() % 3 == 1 && ((lastCharSize <= 3 && available != 1) || lastCharSize > 3))
/*     */         {
/*  53 */           lastCharSize = backtrackOneCharacter(context, buffer, removed, lastCharSize);
/*     */         }
/*     */         break;
/*     */       } 
/*     */       int newMode;
/*  58 */       if (buffer.length() % 
/*  59 */         3 == 0 && (
/*     */         
/*  61 */         newMode = HighLevelEncoder.lookAheadTest(context.getMessage(), context.pos, getEncodingMode())) != getEncodingMode()) {
/*  62 */         context.signalEncoderChange(newMode);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/*  67 */     handleEOD(context, buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   private int backtrackOneCharacter(EncoderContext context, StringBuilder buffer, StringBuilder removed, int lastCharSize) {
/*  72 */     int count = buffer.length();
/*  73 */     buffer.delete(count - lastCharSize, count);
/*  74 */     context.pos--;
/*  75 */     char c = context.getCurrentChar();
/*  76 */     lastCharSize = encodeChar(c, removed);
/*  77 */     context.resetSymbolInfo();
/*  78 */     return lastCharSize;
/*     */   }
/*     */   
/*     */   static void writeNextTriplet(EncoderContext context, StringBuilder buffer) {
/*  82 */     context.writeCodewords(encodeToCodewords(buffer, 0));
/*  83 */     buffer.delete(0, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void handleEOD(EncoderContext context, StringBuilder buffer) {
/*  93 */     int unwritten = buffer.length() / 3 << 1;
/*  94 */     int rest = buffer.length() % 3;
/*     */     
/*  96 */     int curCodewordCount = context.getCodewordCount() + unwritten;
/*  97 */     context.updateSymbolInfo(curCodewordCount);
/*  98 */     int available = context.getSymbolInfo().getDataCapacity() - curCodewordCount;
/*     */     
/* 100 */     if (rest == 2) {
/* 101 */       buffer.append(false);
/* 102 */       while (buffer.length() >= 3) {
/* 103 */         writeNextTriplet(context, buffer);
/*     */       }
/* 105 */       if (context.hasMoreCharacters()) {
/* 106 */         context.writeCodeword('þ');
/*     */       }
/* 108 */     } else if (available == 1 && rest == 1) {
/* 109 */       while (buffer.length() >= 3) {
/* 110 */         writeNextTriplet(context, buffer);
/*     */       }
/* 112 */       if (context.hasMoreCharacters()) {
/* 113 */         context.writeCodeword('þ');
/*     */       }
/*     */       
/* 116 */       context.pos--;
/* 117 */     } else if (rest == 0) {
/* 118 */       while (buffer.length() >= 3) {
/* 119 */         writeNextTriplet(context, buffer);
/*     */       }
/* 121 */       if (available > 0 || context.hasMoreCharacters()) {
/* 122 */         context.writeCodeword('þ');
/*     */       }
/*     */     } else {
/* 125 */       throw new IllegalStateException("Unexpected case. Please report!");
/*     */     } 
/* 127 */     context.signalEncoderChange(0);
/*     */   }
/*     */   
/*     */   int encodeChar(char c, StringBuilder sb) {
/* 131 */     if (c == ' ') {
/* 132 */       sb.append('\003');
/* 133 */       return 1;
/* 134 */     }  if (c >= '0' && c <= '9') {
/* 135 */       sb.append((char)(c - 48 + 4));
/* 136 */       return 1;
/* 137 */     }  if (c >= 'A' && c <= 'Z') {
/* 138 */       sb.append((char)(c - 65 + 14));
/* 139 */       return 1;
/* 140 */     }  if (c >= '\000' && c <= '\037') {
/* 141 */       sb.append(false);
/* 142 */       sb.append(c);
/* 143 */       return 2;
/* 144 */     }  if (c >= '!' && c <= '/') {
/* 145 */       sb.append('\001');
/* 146 */       sb.append((char)(c - 33));
/* 147 */       return 2;
/* 148 */     }  if (c >= ':' && c <= '@') {
/* 149 */       sb.append('\001');
/* 150 */       sb.append((char)(c - 58 + 15));
/* 151 */       return 2;
/* 152 */     }  if (c >= '[' && c <= '_') {
/* 153 */       sb.append('\001');
/* 154 */       sb.append((char)(c - 91 + 22));
/* 155 */       return 2;
/* 156 */     }  if (c >= '`' && c <= '') {
/* 157 */       sb.append('\002');
/* 158 */       sb.append((char)(c - 96));
/* 159 */       return 2;
/* 160 */     }  if (c >= '') {
/* 161 */       sb.append("\001\036");
/*     */       
/* 163 */       return 2 + encodeChar((char)(c - 128), sb);
/*     */     } 
/*     */     
/* 166 */     throw new IllegalArgumentException("Illegal character: " + c);
/*     */   }
/*     */ 
/*     */   
/*     */   private static String encodeToCodewords(CharSequence sb, int startPos) {
/* 171 */     char c1 = sb.charAt(startPos);
/* 172 */     char c2 = sb.charAt(startPos + 1);
/* 173 */     char c3 = sb.charAt(startPos + 2);
/*     */     int v;
/* 175 */     char cw1 = (char)((v = c1 * 1600 + c2 * 40 + c3 + 1) / 256);
/* 176 */     char cw2 = (char)(v % 256);
/* 177 */     return new String(new char[] { cw1, cw2 });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\C40Encoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */