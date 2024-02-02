/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.StringEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColognePhonetic
/*     */   implements StringEncoder
/*     */ {
/* 185 */   private static final char[] AEIJOUY = new char[] { 'A', 'E', 'I', 'J', 'O', 'U', 'Y' };
/* 186 */   private static final char[] CSZ = new char[] { 'C', 'S', 'Z' };
/* 187 */   private static final char[] FPVW = new char[] { 'F', 'P', 'V', 'W' };
/* 188 */   private static final char[] GKQ = new char[] { 'G', 'K', 'Q' };
/* 189 */   private static final char[] CKQ = new char[] { 'C', 'K', 'Q' };
/* 190 */   private static final char[] AHKLOQRUX = new char[] { 'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X' };
/* 191 */   private static final char[] SZ = new char[] { 'S', 'Z' };
/* 192 */   private static final char[] AHKOQUX = new char[] { 'A', 'H', 'K', 'O', 'Q', 'U', 'X' };
/* 193 */   private static final char[] DTX = new char[] { 'D', 'T', 'X' };
/*     */ 
/*     */ 
/*     */   
/*     */   private static final char CHAR_IGNORE = '-';
/*     */ 
/*     */ 
/*     */   
/*     */   private abstract class CologneBuffer
/*     */   {
/*     */     protected final char[] data;
/*     */ 
/*     */     
/* 206 */     protected int length = 0;
/*     */     
/*     */     public CologneBuffer(char[] data) {
/* 209 */       this.data = data;
/* 210 */       this.length = data.length;
/*     */     }
/*     */     
/*     */     public CologneBuffer(int buffSize) {
/* 214 */       this.data = new char[buffSize];
/* 215 */       this.length = 0;
/*     */     }
/*     */     
/*     */     protected abstract char[] copyData(int param1Int1, int param1Int2);
/*     */     
/*     */     public int length() {
/* 221 */       return this.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 226 */       return new String(copyData(0, this.length));
/*     */     }
/*     */   }
/*     */   
/*     */   private class CologneOutputBuffer
/*     */     extends CologneBuffer {
/*     */     private char lastCode;
/*     */     
/*     */     public CologneOutputBuffer(int buffSize) {
/* 235 */       super(buffSize);
/* 236 */       this.lastCode = '/';
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void put(char code) {
/* 248 */       if (code != '-' && this.lastCode != code && (code != '0' || this.length == 0)) {
/* 249 */         this.data[this.length] = code;
/* 250 */         this.length++;
/*     */       } 
/* 252 */       this.lastCode = code;
/*     */     }
/*     */ 
/*     */     
/*     */     protected char[] copyData(int start, int length) {
/* 257 */       char[] newData = new char[length];
/* 258 */       System.arraycopy(this.data, start, newData, 0, length);
/* 259 */       return newData;
/*     */     }
/*     */   }
/*     */   
/*     */   private class CologneInputBuffer
/*     */     extends CologneBuffer {
/*     */     public CologneInputBuffer(char[] data) {
/* 266 */       super(data);
/*     */     }
/*     */ 
/*     */     
/*     */     protected char[] copyData(int start, int length) {
/* 271 */       char[] newData = new char[length];
/* 272 */       System.arraycopy(this.data, this.data.length - this.length + start, newData, 0, length);
/* 273 */       return newData;
/*     */     }
/*     */     
/*     */     public char getNextChar() {
/* 277 */       return this.data[getNextPos()];
/*     */     }
/*     */     
/*     */     protected int getNextPos() {
/* 281 */       return this.data.length - this.length;
/*     */     }
/*     */     
/*     */     public char removeNext() {
/* 285 */       char ch = getNextChar();
/* 286 */       this.length--;
/* 287 */       return ch;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean arrayContains(char[] arr, char key) {
/* 295 */     for (char element : arr) {
/* 296 */       if (element == key) {
/* 297 */         return true;
/*     */       }
/*     */     } 
/* 300 */     return false;
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
/*     */   public String colognePhonetic(String text) {
/* 315 */     if (text == null) {
/* 316 */       return null;
/*     */     }
/*     */     
/* 319 */     CologneInputBuffer input = new CologneInputBuffer(preprocess(text));
/* 320 */     CologneOutputBuffer output = new CologneOutputBuffer(input.length() * 2);
/*     */ 
/*     */ 
/*     */     
/* 324 */     char lastChar = '-';
/*     */ 
/*     */     
/* 327 */     while (input.length() > 0) {
/* 328 */       char nextChar, chr = input.removeNext();
/*     */       
/* 330 */       if (input.length() > 0) {
/* 331 */         nextChar = input.getNextChar();
/*     */       } else {
/* 333 */         nextChar = '-';
/*     */       } 
/*     */       
/* 336 */       if (chr < 'A' || chr > 'Z') {
/*     */         continue;
/*     */       }
/*     */       
/* 340 */       if (arrayContains(AEIJOUY, chr)) {
/* 341 */         output.put('0');
/* 342 */       } else if (chr == 'B' || (chr == 'P' && nextChar != 'H')) {
/* 343 */         output.put('1');
/* 344 */       } else if ((chr == 'D' || chr == 'T') && !arrayContains(CSZ, nextChar)) {
/* 345 */         output.put('2');
/* 346 */       } else if (arrayContains(FPVW, chr)) {
/* 347 */         output.put('3');
/* 348 */       } else if (arrayContains(GKQ, chr)) {
/* 349 */         output.put('4');
/* 350 */       } else if (chr == 'X' && !arrayContains(CKQ, lastChar)) {
/* 351 */         output.put('4');
/* 352 */         output.put('8');
/* 353 */       } else if (chr == 'S' || chr == 'Z') {
/* 354 */         output.put('8');
/* 355 */       } else if (chr == 'C') {
/* 356 */         if (output.length() == 0) {
/* 357 */           if (arrayContains(AHKLOQRUX, nextChar)) {
/* 358 */             output.put('4');
/*     */           } else {
/* 360 */             output.put('8');
/*     */           }
/*     */         
/* 363 */         } else if (arrayContains(SZ, lastChar) || !arrayContains(AHKOQUX, nextChar)) {
/* 364 */           output.put('8');
/*     */         } else {
/* 366 */           output.put('4');
/*     */         }
/*     */       
/* 369 */       } else if (arrayContains(DTX, chr)) {
/* 370 */         output.put('8');
/* 371 */       } else if (chr == 'R') {
/* 372 */         output.put('7');
/* 373 */       } else if (chr == 'L') {
/* 374 */         output.put('5');
/* 375 */       } else if (chr == 'M' || chr == 'N') {
/* 376 */         output.put('6');
/* 377 */       } else if (chr == 'H') {
/* 378 */         output.put('-');
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 383 */       lastChar = chr;
/*     */     } 
/* 385 */     return output.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object encode(Object object) throws EncoderException {
/* 390 */     if (!(object instanceof String)) {
/* 391 */       throw new EncoderException("This method's parameter was expected to be of the type " + String.class
/* 392 */           .getName() + ". But actually it was of the type " + object
/*     */           
/* 394 */           .getClass().getName() + ".");
/*     */     }
/*     */     
/* 397 */     return encode((String)object);
/*     */   }
/*     */ 
/*     */   
/*     */   public String encode(String text) {
/* 402 */     return colognePhonetic(text);
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
/*     */   public boolean isEncodeEqual(String text1, String text2) {
/* 414 */     return colognePhonetic(text1).equals(colognePhonetic(text2));
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
/*     */   private char[] preprocess(String text) {
/* 429 */     char[] chrs = text.toUpperCase(Locale.GERMAN).toCharArray();
/*     */     
/* 431 */     for (int index = 0; index < chrs.length; index++) {
/* 432 */       switch (chrs[index]) {
/*     */         case 'Ä':
/* 434 */           chrs[index] = 'A';
/*     */           break;
/*     */         case 'Ü':
/* 437 */           chrs[index] = 'U';
/*     */           break;
/*     */         case 'Ö':
/* 440 */           chrs[index] = 'O';
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     } 
/* 446 */     return chrs;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\ColognePhonetic.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */