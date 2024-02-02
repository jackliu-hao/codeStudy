/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitMatrix;
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
/*     */ public class Code93Writer
/*     */   extends OneDimensionalCodeWriter
/*     */ {
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/*  35 */     if (format != BarcodeFormat.CODE_93) {
/*  36 */       throw new IllegalArgumentException("Can only encode CODE_93, but got " + format);
/*     */     }
/*  38 */     return super.encode(contents, format, width, height, hints);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean[] encode(String contents) {
/*     */     int length;
/*  44 */     if ((length = contents.length()) > 80) {
/*  45 */       throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
/*     */     }
/*     */ 
/*     */     
/*  49 */     int[] widths = new int[9];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     boolean[] result = new boolean[(contents.length() + 2 + 2) * 9 + 1];
/*     */ 
/*     */     
/*  57 */     toIntArray(Code93Reader.CHARACTER_ENCODINGS[47], widths);
/*  58 */     int pos = appendPattern(result, 0, widths, true);
/*     */     
/*  60 */     for (int i = 0; i < length; i++) {
/*  61 */       int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(contents.charAt(i));
/*  62 */       toIntArray(Code93Reader.CHARACTER_ENCODINGS[indexInString], widths);
/*  63 */       pos += appendPattern(result, pos, widths, true);
/*     */     } 
/*     */ 
/*     */     
/*  67 */     int check1 = computeChecksumIndex(contents, 20);
/*  68 */     toIntArray(Code93Reader.CHARACTER_ENCODINGS[check1], widths);
/*  69 */     pos += appendPattern(result, pos, widths, true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     int check2 = computeChecksumIndex(contents + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".charAt(check1), 15);
/*  75 */     toIntArray(Code93Reader.CHARACTER_ENCODINGS[check2], widths);
/*  76 */     pos += appendPattern(result, pos, widths, true);
/*     */ 
/*     */     
/*  79 */     toIntArray(Code93Reader.CHARACTER_ENCODINGS[47], widths);
/*  80 */     pos += appendPattern(result, pos, widths, true);
/*     */ 
/*     */     
/*  83 */     result[pos] = true;
/*     */     
/*  85 */     return result;
/*     */   }
/*     */   
/*     */   private static void toIntArray(int a, int[] toReturn) {
/*  89 */     for (int i = 0; i < 9; i++) {
/*  90 */       int temp = a & 1 << 8 - i;
/*  91 */       toReturn[i] = (temp == 0) ? 0 : 1;
/*     */     }  } protected static int appendPattern(boolean[] target, int pos, int[] pattern, boolean startColor) {
/*     */     int[] arrayOfInt;
/*     */     int i;
/*     */     byte b;
/*  96 */     for (i = (arrayOfInt = pattern).length, b = 0; b < i; ) { int bit = arrayOfInt[b];
/*  97 */       target[pos++] = (bit != 0); b++; }
/*     */     
/*  99 */     return 9;
/*     */   }
/*     */   
/*     */   private static int computeChecksumIndex(String contents, int maxWeight) {
/* 103 */     int weight = 1;
/* 104 */     int total = 0;
/*     */     
/* 106 */     for (int i = contents.length() - 1; i >= 0; i--) {
/* 107 */       int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%abcd*".indexOf(contents.charAt(i));
/* 108 */       total += indexInString * weight;
/* 109 */       if (++weight > maxWeight) {
/* 110 */         weight = 1;
/*     */       }
/*     */     } 
/* 113 */     return total % 47;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\Code93Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */