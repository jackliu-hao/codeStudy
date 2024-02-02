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
/*    */ public final class ITFWriter
/*    */   extends OneDimensionalCodeWriter
/*    */ {
/* 33 */   private static final int[] START_PATTERN = new int[] { 1, 1, 1, 1 };
/* 34 */   private static final int[] END_PATTERN = new int[] { 3, 1, 1 };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/* 42 */     if (format != BarcodeFormat.ITF) {
/* 43 */       throw new IllegalArgumentException("Can only encode ITF, but got " + format);
/*    */     }
/*    */     
/* 46 */     return super.encode(contents, format, width, height, hints);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean[] encode(String contents) {
/*    */     int length;
/* 52 */     if ((length = contents.length()) % 2 != 0) {
/* 53 */       throw new IllegalArgumentException("The length of the input should be even");
/*    */     }
/* 55 */     if (length > 80) {
/* 56 */       throw new IllegalArgumentException("Requested contents should be less than 80 digits long, but got " + length);
/*    */     }
/*    */     
/*    */     boolean[] result;
/* 60 */     int pos = appendPattern(result = new boolean[9 + length * 9], 0, START_PATTERN, true);
/* 61 */     for (int i = 0; i < length; i += 2) {
/* 62 */       int one = Character.digit(contents.charAt(i), 10);
/* 63 */       int two = Character.digit(contents.charAt(i + 1), 10);
/* 64 */       int[] encoding = new int[18];
/* 65 */       for (int j = 0; j < 5; j++) {
/* 66 */         encoding[2 * j] = ITFReader.PATTERNS[one][j];
/* 67 */         encoding[2 * j + 1] = ITFReader.PATTERNS[two][j];
/*    */       } 
/* 69 */       pos += appendPattern(result, pos, encoding, true);
/*    */     } 
/* 71 */     appendPattern(result, pos, END_PATTERN, true);
/*    */     
/* 73 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\ITFWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */