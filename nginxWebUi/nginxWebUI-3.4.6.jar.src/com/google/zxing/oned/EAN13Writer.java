/*    */ package com.google.zxing.oned;
/*    */ 
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.EncodeHintType;
/*    */ import com.google.zxing.FormatException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class EAN13Writer
/*    */   extends UPCEANWriter
/*    */ {
/*    */   private static final int CODE_WIDTH = 95;
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/* 46 */     if (format != BarcodeFormat.EAN_13) {
/* 47 */       throw new IllegalArgumentException("Can only encode EAN_13, but got " + format);
/*    */     }
/*    */     
/* 50 */     return super.encode(contents, format, width, height, hints);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean[] encode(String contents) {
/* 55 */     if (contents.length() != 13) {
/* 56 */       throw new IllegalArgumentException("Requested contents should be 13 digits long, but got " + contents
/* 57 */           .length());
/*    */     }
/*    */     try {
/* 60 */       if (!UPCEANReader.checkStandardUPCEANChecksum(contents)) {
/* 61 */         throw new IllegalArgumentException("Contents do not pass checksum");
/*    */       }
/* 63 */     } catch (FormatException formatException) {
/* 64 */       throw new IllegalArgumentException("Illegal contents");
/*    */     } 
/*    */     
/* 67 */     int firstDigit = Integer.parseInt(contents.substring(0, 1));
/* 68 */     int parities = EAN13Reader.FIRST_DIGIT_ENCODINGS[firstDigit];
/* 69 */     boolean[] result = new boolean[95];
/*    */ 
/*    */     
/* 72 */     int pos = 0 + appendPattern(result, 0, UPCEANReader.START_END_PATTERN, true);
/*    */     
/*    */     int i;
/* 75 */     for (i = 1; i <= 6; i++) {
/* 76 */       int digit = Integer.parseInt(contents.substring(i, i + 1));
/* 77 */       if ((parities >> 6 - i & 0x1) == 1) {
/* 78 */         digit += 10;
/*    */       }
/* 80 */       pos += appendPattern(result, pos, UPCEANReader.L_AND_G_PATTERNS[digit], false);
/*    */     } 
/*    */     
/* 83 */     pos += appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, false);
/*    */     
/* 85 */     for (i = 7; i <= 12; i++) {
/* 86 */       int digit = Integer.parseInt(contents.substring(i, i + 1));
/* 87 */       pos += appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], true);
/*    */     } 
/* 89 */     appendPattern(result, pos, UPCEANReader.START_END_PATTERN, true);
/*    */     
/* 91 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\EAN13Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */