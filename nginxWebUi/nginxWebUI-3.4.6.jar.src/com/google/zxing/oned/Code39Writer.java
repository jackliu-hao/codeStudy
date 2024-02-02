/*    */ package com.google.zxing.oned;
/*    */ 
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.EncodeHintType;
/*    */ import com.google.zxing.WriterException;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Code39Writer
/*    */   extends OneDimensionalCodeWriter
/*    */ {
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/* 39 */     if (format != BarcodeFormat.CODE_39) {
/* 40 */       throw new IllegalArgumentException("Can only encode CODE_39, but got " + format);
/*    */     }
/* 42 */     return super.encode(contents, format, width, height, hints);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean[] encode(String contents) {
/*    */     int length;
/* 48 */     if ((length = contents.length()) > 80) {
/* 49 */       throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
/*    */     }
/*    */ 
/*    */     
/* 53 */     int[] widths = new int[9];
/* 54 */     int codeWidth = length + 25;
/* 55 */     for (int i = 0; i < length; i++) {
/*    */       int indexInString;
/* 57 */       if ((indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(i))) < 0) {
/* 58 */         throw new IllegalArgumentException("Bad contents: " + contents);
/*    */       }
/* 60 */       toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths); int[] arrayOfInt; byte b;
/* 61 */       for (arrayOfInt = widths, b = 0; b < 9; ) { int width = arrayOfInt[b];
/* 62 */         codeWidth += width; b++; }
/*    */     
/*    */     } 
/* 65 */     boolean[] result = new boolean[codeWidth];
/* 66 */     toIntArray(Code39Reader.ASTERISK_ENCODING, widths);
/* 67 */     int pos = appendPattern(result, 0, widths, true);
/* 68 */     int[] narrowWhite = { 1 };
/* 69 */     pos += appendPattern(result, pos, narrowWhite, false);
/*    */     
/* 71 */     for (int j = 0; j < length; j++) {
/* 72 */       int indexInString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. *$/+%".indexOf(contents.charAt(j));
/* 73 */       toIntArray(Code39Reader.CHARACTER_ENCODINGS[indexInString], widths);
/*    */       
/* 75 */       pos = (pos = pos + appendPattern(result, pos, widths, true)) + appendPattern(result, pos, narrowWhite, false);
/*    */     } 
/* 77 */     toIntArray(Code39Reader.ASTERISK_ENCODING, widths);
/* 78 */     appendPattern(result, pos, widths, true);
/* 79 */     return result;
/*    */   }
/*    */   
/*    */   private static void toIntArray(int a, int[] toReturn) {
/* 83 */     for (int i = 0; i < 9; i++) {
/* 84 */       int temp = a & 1 << 8 - i;
/* 85 */       toReturn[i] = (temp == 0) ? 1 : 2;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\Code39Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */