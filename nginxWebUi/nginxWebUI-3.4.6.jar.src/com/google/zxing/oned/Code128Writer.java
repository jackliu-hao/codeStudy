/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Code128Writer
/*     */   extends OneDimensionalCodeWriter
/*     */ {
/*     */   private static final int CODE_START_B = 104;
/*     */   private static final int CODE_START_C = 105;
/*     */   private static final int CODE_CODE_B = 100;
/*     */   private static final int CODE_CODE_C = 99;
/*     */   private static final int CODE_STOP = 106;
/*     */   private static final char ESCAPE_FNC_1 = 'ñ';
/*     */   private static final char ESCAPE_FNC_2 = 'ò';
/*     */   private static final char ESCAPE_FNC_3 = 'ó';
/*     */   private static final char ESCAPE_FNC_4 = 'ô';
/*     */   private static final int CODE_FNC_1 = 102;
/*     */   private static final int CODE_FNC_2 = 97;
/*     */   private static final int CODE_FNC_3 = 96;
/*     */   private static final int CODE_FNC_4_B = 100;
/*     */   
/*     */   private enum CType
/*     */   {
/*  54 */     UNCODABLE,
/*  55 */     ONE_DIGIT,
/*  56 */     TWO_DIGITS,
/*  57 */     FNC_1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/*  66 */     if (format != BarcodeFormat.CODE_128) {
/*  67 */       throw new IllegalArgumentException("Can only encode CODE_128, but got " + format);
/*     */     }
/*  69 */     return super.encode(contents, format, width, height, hints);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean[] encode(String contents) {
/*     */     int length;
/*  76 */     if ((length = contents.length()) <= 0 || length > 80) {
/*  77 */       throw new IllegalArgumentException("Contents length should be between 1 and 80 characters, but got " + length);
/*     */     }
/*     */ 
/*     */     
/*  81 */     for (int i = 0; i < length; i++) {
/*     */       char c;
/*  83 */       if ((c = contents.charAt(i)) < ' ' || c > '~') {
/*  84 */         switch (c) {
/*     */           case 'ñ':
/*     */           case 'ò':
/*     */           case 'ó':
/*     */           case 'ô':
/*     */             break;
/*     */           default:
/*  91 */             throw new IllegalArgumentException("Bad character in input: " + c);
/*     */         } 
/*     */       
/*     */       }
/*     */     } 
/*  96 */     Collection<int[]> patterns = (Collection)new ArrayList<>();
/*  97 */     int checkSum = 0;
/*  98 */     int checkWeight = 1;
/*  99 */     int codeSet = 0;
/* 100 */     int position = 0;
/*     */     
/* 102 */     while (position < length) {
/*     */       int patternIndex, newCodeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 108 */       if ((newCodeSet = chooseCode(contents, position, codeSet)) == codeSet) {
/*     */ 
/*     */         
/* 111 */         switch (contents.charAt(position)) {
/*     */           case 'ñ':
/* 113 */             patternIndex = 102;
/*     */             break;
/*     */           case 'ò':
/* 116 */             patternIndex = 97;
/*     */             break;
/*     */           case 'ó':
/* 119 */             patternIndex = 96;
/*     */             break;
/*     */           case 'ô':
/* 122 */             patternIndex = 100;
/*     */             break;
/*     */           
/*     */           default:
/* 126 */             if (codeSet == 100) {
/* 127 */               patternIndex = contents.charAt(position) - 32; break;
/*     */             } 
/* 129 */             patternIndex = Integer.parseInt(contents.substring(position, position + 2));
/* 130 */             position++;
/*     */             break;
/*     */         } 
/* 133 */         position++;
/*     */       }
/*     */       else {
/*     */         
/* 137 */         if (codeSet == 0) {
/*     */           
/* 139 */           if (newCodeSet == 100) {
/* 140 */             patternIndex = 104;
/*     */           } else {
/*     */             
/* 143 */             patternIndex = 105;
/*     */           } 
/*     */         } else {
/*     */           
/* 147 */           patternIndex = newCodeSet;
/*     */         } 
/* 149 */         codeSet = newCodeSet;
/*     */       } 
/*     */ 
/*     */       
/* 153 */       patterns.add(Code128Reader.CODE_PATTERNS[patternIndex]);
/*     */ 
/*     */       
/* 156 */       checkSum += patternIndex * checkWeight;
/* 157 */       if (position != 0) {
/* 158 */         checkWeight++;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 163 */     checkSum %= 103;
/* 164 */     patterns.add(Code128Reader.CODE_PATTERNS[checkSum]);
/*     */ 
/*     */     
/* 167 */     patterns.add(Code128Reader.CODE_PATTERNS[106]);
/*     */ 
/*     */     
/* 170 */     int codeWidth = 0;
/* 171 */     for (Iterator<int> iterator = patterns.iterator(); iterator.hasNext();) {
/* 172 */       for (j = (arrayOfInt = (int[])iterator.next()).length, b = 0; b < j; ) { int width = arrayOfInt[b];
/* 173 */         codeWidth += width;
/*     */         
/*     */         b++; }
/*     */     
/*     */     } 
/* 178 */     boolean[] result = new boolean[codeWidth];
/* 179 */     int pos = 0;
/* 180 */     for (int[] pattern : patterns) {
/* 181 */       int j = pos + appendPattern(result, pos, pattern, true);
/*     */     }
/*     */     
/* 184 */     return result;
/*     */   }
/*     */   
/*     */   private static CType findCType(CharSequence value, int start) {
/* 188 */     int last = value.length();
/* 189 */     if (start >= last) {
/* 190 */       return CType.UNCODABLE;
/*     */     }
/*     */     char c;
/* 193 */     if ((c = value.charAt(start)) == 'ñ') {
/* 194 */       return CType.FNC_1;
/*     */     }
/* 196 */     if (c < '0' || c > '9') {
/* 197 */       return CType.UNCODABLE;
/*     */     }
/* 199 */     if (start + 1 >= last) {
/* 200 */       return CType.ONE_DIGIT;
/*     */     }
/*     */     
/* 203 */     if ((c = value.charAt(start + 1)) < '0' || c > '9') {
/* 204 */       return CType.ONE_DIGIT;
/*     */     }
/* 206 */     return CType.TWO_DIGITS;
/*     */   }
/*     */   
/*     */   private static int chooseCode(CharSequence value, int start, int oldCode) {
/*     */     CType lookahead;
/* 211 */     if ((lookahead = findCType(value, start)) == CType.UNCODABLE || lookahead == CType.ONE_DIGIT) {
/* 212 */       return 100;
/*     */     }
/* 214 */     if (oldCode == 99) {
/* 215 */       return oldCode;
/*     */     }
/* 217 */     if (oldCode == 100) {
/* 218 */       if (lookahead == CType.FNC_1) {
/* 219 */         return oldCode;
/*     */       }
/*     */ 
/*     */       
/* 223 */       if ((lookahead = findCType(value, start + 2)) == CType.UNCODABLE || lookahead == CType.ONE_DIGIT) {
/* 224 */         return oldCode;
/*     */       }
/* 226 */       if (lookahead == CType.FNC_1) {
/* 227 */         if (findCType(value, start + 3) == 
/* 228 */           CType.TWO_DIGITS) {
/* 229 */           return 99;
/*     */         }
/* 231 */         return 100;
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 236 */       int index = start + 4;
/* 237 */       while ((lookahead = findCType(value, index)) == CType.TWO_DIGITS) {
/* 238 */         index += 2;
/*     */       }
/* 240 */       if (lookahead == CType.ONE_DIGIT) {
/* 241 */         return 100;
/*     */       }
/* 243 */       return 99;
/*     */     } 
/*     */     
/* 246 */     if (lookahead == CType.FNC_1) {
/* 247 */       lookahead = findCType(value, start + 1);
/*     */     }
/* 249 */     if (lookahead == CType.TWO_DIGITS) {
/* 250 */       return 99;
/*     */     }
/* 252 */     return 100;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\Code128Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */