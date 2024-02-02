/*     */ package com.google.zxing.datamatrix.encoder;
/*     */ 
/*     */ import com.google.zxing.Dimension;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HighLevelEncoder
/*     */ {
/*     */   private static final char PAD = '';
/*     */   static final char LATCH_TO_C40 = 'æ';
/*     */   static final char LATCH_TO_BASE256 = 'ç';
/*     */   static final char UPPER_SHIFT = 'ë';
/*     */   private static final char MACRO_05 = 'ì';
/*     */   private static final char MACRO_06 = 'í';
/*     */   static final char LATCH_TO_ANSIX12 = 'î';
/*     */   static final char LATCH_TO_TEXT = 'ï';
/*     */   static final char LATCH_TO_EDIFACT = 'ð';
/*     */   static final char C40_UNLATCH = 'þ';
/*     */   static final char X12_UNLATCH = 'þ';
/*     */   private static final String MACRO_05_HEADER = "[)>\03605\035";
/*     */   private static final String MACRO_06_HEADER = "[)>\03606\035";
/*     */   private static final String MACRO_TRAILER = "\036\004";
/*     */   static final int ASCII_ENCODATION = 0;
/*     */   static final int C40_ENCODATION = 1;
/*     */   static final int TEXT_ENCODATION = 2;
/*     */   static final int X12_ENCODATION = 3;
/*     */   static final int EDIFACT_ENCODATION = 4;
/*     */   static final int BASE256_ENCODATION = 5;
/*     */   
/*     */   private static char randomize253State(char ch, int codewordPosition) {
/* 129 */     int pseudoRandom = codewordPosition * 149 % 253 + 1;
/*     */     int tempVariable;
/* 131 */     return (char)(((tempVariable = ch + pseudoRandom) <= 254) ? tempVariable : (tempVariable - 254));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String encodeHighLevel(String msg) {
/* 142 */     return encodeHighLevel(msg, SymbolShapeHint.FORCE_NONE, null, null);
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
/*     */   public static String encodeHighLevel(String msg, SymbolShapeHint shape, Dimension minSize, Dimension maxSize) {
/* 161 */     Encoder[] encoders = { new ASCIIEncoder(), new C40Encoder(), new TextEncoder(), new X12Encoder(), new EdifactEncoder(), new Base256Encoder() };
/*     */ 
/*     */     
/*     */     EncoderContext context;
/*     */ 
/*     */     
/* 167 */     (context = new EncoderContext(msg)).setSymbolShape(shape);
/* 168 */     context.setSizeConstraints(minSize, maxSize);
/*     */     
/* 170 */     if (msg.startsWith("[)>\03605\035") && msg.endsWith("\036\004")) {
/* 171 */       context.writeCodeword('ì');
/* 172 */       context.setSkipAtEnd(2);
/* 173 */       context.pos += 7;
/* 174 */     } else if (msg.startsWith("[)>\03606\035") && msg.endsWith("\036\004")) {
/* 175 */       context.writeCodeword('í');
/* 176 */       context.setSkipAtEnd(2);
/* 177 */       context.pos += 7;
/*     */     } 
/*     */     
/* 180 */     int encodingMode = 0;
/* 181 */     while (context.hasMoreCharacters()) {
/* 182 */       encoders[encodingMode].encode(context);
/* 183 */       if (context.getNewEncoding() >= 0) {
/* 184 */         encodingMode = context.getNewEncoding();
/* 185 */         context.resetEncoderSignal();
/*     */       } 
/*     */     } 
/* 188 */     int len = context.getCodewordCount();
/* 189 */     context.updateSymbolInfo();
/* 190 */     int capacity = context.getSymbolInfo().getDataCapacity();
/* 191 */     if (len < capacity && 
/* 192 */       encodingMode != 0 && encodingMode != 5) {
/* 193 */       context.writeCodeword('þ');
/*     */     }
/*     */     
/*     */     StringBuilder codewords;
/*     */     
/* 198 */     if ((codewords = context.getCodewords()).length() < capacity) {
/* 199 */       codewords.append('');
/*     */     }
/* 201 */     while (codewords.length() < capacity) {
/* 202 */       codewords.append(randomize253State('', codewords.length() + 1));
/*     */     }
/*     */     
/* 205 */     return context.getCodewords().toString();
/*     */   }
/*     */   static int lookAheadTest(CharSequence msg, int startpos, int currentMode) {
/*     */     float[] charCounts;
/* 209 */     if (startpos >= msg.length()) {
/* 210 */       return currentMode;
/*     */     }
/*     */ 
/*     */     
/* 214 */     if (currentMode == 0) {
/* 215 */       charCounts = new float[] { 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.25F };
/*     */     } else {
/*     */       
/* 218 */       (charCounts = new float[] { 1.0F, 2.0F, 2.0F, 2.0F, 2.0F, 2.25F })[currentMode] = 0.0F;
/*     */     } 
/*     */     
/* 221 */     int charsProcessed = 0;
/*     */     
/*     */     while (true) {
/* 224 */       if (startpos + charsProcessed == msg.length()) {
/*     */         
/* 226 */         byte[] mins = new byte[6];
/* 227 */         int[] intCharCounts = new int[6];
/* 228 */         int min = findMinimums(charCounts, intCharCounts, 2147483647, mins);
/* 229 */         int minCount = getMinimumCount(mins);
/*     */         
/* 231 */         if (intCharCounts[0] == min) {
/* 232 */           return 0;
/*     */         }
/* 234 */         if (minCount == 1 && mins[5] > 0) {
/* 235 */           return 5;
/*     */         }
/* 237 */         if (minCount == 1 && mins[4] > 0) {
/* 238 */           return 4;
/*     */         }
/* 240 */         if (minCount == 1 && mins[2] > 0) {
/* 241 */           return 2;
/*     */         }
/* 243 */         if (minCount == 1 && mins[3] > 0) {
/* 244 */           return 3;
/*     */         }
/* 246 */         return 1;
/*     */       } 
/*     */       
/* 249 */       char c = msg.charAt(startpos + charsProcessed);
/* 250 */       charsProcessed++;
/*     */ 
/*     */       
/* 253 */       if (isDigit(c)) {
/* 254 */         charCounts[0] = charCounts[0] + 0.5F;
/* 255 */       } else if (isExtendedASCII(c)) {
/* 256 */         charCounts[0] = (float)Math.ceil(charCounts[0]);
/* 257 */         charCounts[0] = charCounts[0] + 2.0F;
/*     */       } else {
/* 259 */         charCounts[0] = (float)Math.ceil(charCounts[0]);
/* 260 */         charCounts[0] = charCounts[0] + 1.0F;
/*     */       } 
/*     */ 
/*     */       
/* 264 */       if (isNativeC40(c)) {
/* 265 */         charCounts[1] = charCounts[1] + 0.6666667F;
/* 266 */       } else if (isExtendedASCII(c)) {
/* 267 */         charCounts[1] = charCounts[1] + 2.6666667F;
/*     */       } else {
/* 269 */         charCounts[1] = charCounts[1] + 1.3333334F;
/*     */       } 
/*     */ 
/*     */       
/* 273 */       if (isNativeText(c)) {
/* 274 */         charCounts[2] = charCounts[2] + 0.6666667F;
/* 275 */       } else if (isExtendedASCII(c)) {
/* 276 */         charCounts[2] = charCounts[2] + 2.6666667F;
/*     */       } else {
/* 278 */         charCounts[2] = charCounts[2] + 1.3333334F;
/*     */       } 
/*     */ 
/*     */       
/* 282 */       if (isNativeX12(c)) {
/* 283 */         charCounts[3] = charCounts[3] + 0.6666667F;
/* 284 */       } else if (isExtendedASCII(c)) {
/* 285 */         charCounts[3] = charCounts[3] + 4.3333335F;
/*     */       } else {
/* 287 */         charCounts[3] = charCounts[3] + 3.3333333F;
/*     */       } 
/*     */ 
/*     */       
/* 291 */       if (isNativeEDIFACT(c)) {
/* 292 */         charCounts[4] = charCounts[4] + 0.75F;
/* 293 */       } else if (isExtendedASCII(c)) {
/* 294 */         charCounts[4] = charCounts[4] + 4.25F;
/*     */       } else {
/* 296 */         charCounts[4] = charCounts[4] + 3.25F;
/*     */       } 
/*     */ 
/*     */       
/* 300 */       if (isSpecialB256(c)) {
/* 301 */         charCounts[5] = charCounts[5] + 4.0F;
/*     */       } else {
/* 303 */         charCounts[5] = charCounts[5] + 1.0F;
/*     */       } 
/*     */ 
/*     */       
/* 307 */       if (charsProcessed >= 4) {
/* 308 */         int[] intCharCounts = new int[6];
/* 309 */         byte[] mins = new byte[6];
/* 310 */         findMinimums(charCounts, intCharCounts, 2147483647, mins);
/* 311 */         int minCount = getMinimumCount(mins);
/*     */         
/* 313 */         if (intCharCounts[0] < intCharCounts[5] && intCharCounts[0] < intCharCounts[1] && intCharCounts[0] < intCharCounts[2] && intCharCounts[0] < intCharCounts[3] && intCharCounts[0] < intCharCounts[4])
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 318 */           return 0;
/*     */         }
/* 320 */         if (intCharCounts[5] < intCharCounts[0] || mins[1] + mins[2] + mins[3] + mins[4] == 0)
/*     */         {
/* 322 */           return 5;
/*     */         }
/* 324 */         if (minCount == 1 && mins[4] > 0) {
/* 325 */           return 4;
/*     */         }
/* 327 */         if (minCount == 1 && mins[2] > 0) {
/* 328 */           return 2;
/*     */         }
/* 330 */         if (minCount == 1 && mins[3] > 0) {
/* 331 */           return 3;
/*     */         }
/* 333 */         if (intCharCounts[1] + 1 < intCharCounts[0] && intCharCounts[1] + 1 < intCharCounts[5] && intCharCounts[1] + 1 < intCharCounts[4] && intCharCounts[1] + 1 < intCharCounts[2]) {
/*     */ 
/*     */ 
/*     */           
/* 337 */           if (intCharCounts[1] < intCharCounts[3]) {
/* 338 */             return 1;
/*     */           }
/* 340 */           if (intCharCounts[1] == intCharCounts[3]) {
/* 341 */             int p = startpos + charsProcessed + 1;
/* 342 */             while (p < msg.length()) {
/*     */               char tc;
/* 344 */               if (isX12TermSep(tc = msg.charAt(p))) {
/* 345 */                 return 3;
/*     */               }
/* 347 */               if (isNativeX12(tc))
/*     */               {
/*     */                 
/* 350 */                 p++; } 
/*     */             } 
/* 352 */             return 1;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int findMinimums(float[] charCounts, int[] intCharCounts, int min, byte[] mins) {
/* 360 */     Arrays.fill(mins, (byte)0);
/* 361 */     for (int i = 0; i < 6; i++) {
/* 362 */       intCharCounts[i] = (int)Math.ceil(charCounts[i]);
/* 363 */       int current = intCharCounts[i];
/* 364 */       if (min > current) {
/* 365 */         min = current;
/* 366 */         Arrays.fill(mins, (byte)0);
/*     */       } 
/* 368 */       if (min == current) {
/* 369 */         mins[i] = (byte)(mins[i] + 1);
/*     */       }
/*     */     } 
/*     */     
/* 373 */     return min;
/*     */   }
/*     */   
/*     */   private static int getMinimumCount(byte[] mins) {
/* 377 */     int minCount = 0;
/* 378 */     for (int i = 0; i < 6; i++) {
/* 379 */       minCount += mins[i];
/*     */     }
/* 381 */     return minCount;
/*     */   }
/*     */   
/*     */   static boolean isDigit(char ch) {
/* 385 */     return (ch >= '0' && ch <= '9');
/*     */   }
/*     */   
/*     */   static boolean isExtendedASCII(char ch) {
/* 389 */     return (ch >= '' && ch <= 'ÿ');
/*     */   }
/*     */   
/*     */   private static boolean isNativeC40(char ch) {
/* 393 */     return (ch == ' ' || (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z'));
/*     */   }
/*     */   
/*     */   private static boolean isNativeText(char ch) {
/* 397 */     return (ch == ' ' || (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z'));
/*     */   }
/*     */   
/*     */   private static boolean isNativeX12(char ch) {
/* 401 */     return (isX12TermSep(ch) || ch == ' ' || (ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z'));
/*     */   }
/*     */   
/*     */   private static boolean isX12TermSep(char ch) {
/* 405 */     return (ch == '\r' || ch == '*' || ch == '>');
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isNativeEDIFACT(char ch) {
/* 411 */     return (ch >= ' ' && ch <= '^');
/*     */   }
/*     */   
/*     */   private static boolean isSpecialB256(char ch) {
/* 415 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
/* 426 */     int count = 0;
/* 427 */     int len = msg.length();
/* 428 */     int idx = startpos;
/* 429 */     if (startpos < len) {
/* 430 */       char ch = msg.charAt(startpos);
/* 431 */       while (isDigit(ch) && idx < len) {
/* 432 */         count++;
/* 433 */         idx++;
/* 434 */         if (idx < len) {
/* 435 */           ch = msg.charAt(idx);
/*     */         }
/*     */       } 
/*     */     } 
/* 439 */     return count;
/*     */   }
/*     */   
/*     */   static void illegalCharacter(char c) {
/* 443 */     String hex = Integer.toHexString(c);
/* 444 */     hex = "0000".substring(0, 4 - hex.length()) + hex;
/* 445 */     throw new IllegalArgumentException("Illegal character: " + c + " (0x" + hex + ')');
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\HighLevelEncoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */