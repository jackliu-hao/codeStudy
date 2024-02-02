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
/*    */ 
/*    */ 
/*    */ public final class UPCEWriter
/*    */   extends UPCEANWriter
/*    */ {
/*    */   private static final int CODE_WIDTH = 51;
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/* 43 */     if (format != BarcodeFormat.UPC_E) {
/* 44 */       throw new IllegalArgumentException("Can only encode UPC_E, but got " + format);
/*    */     }
/*    */     
/* 47 */     return super.encode(contents, format, width, height, hints);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean[] encode(String contents) {
/* 52 */     if (contents.length() != 8) {
/* 53 */       throw new IllegalArgumentException("Requested contents should be 8 digits long, but got " + contents
/* 54 */           .length());
/*    */     }
/*    */     
/* 57 */     int checkDigit = Integer.parseInt(contents.substring(7, 8));
/* 58 */     int parities = UPCEReader.CHECK_DIGIT_ENCODINGS[checkDigit];
/* 59 */     boolean[] result = new boolean[51];
/*    */ 
/*    */     
/* 62 */     int pos = 0 + appendPattern(result, 0, UPCEANReader.START_END_PATTERN, true);
/*    */     
/* 64 */     for (int i = 1; i <= 6; i++) {
/* 65 */       int digit = Integer.parseInt(contents.substring(i, i + 1));
/* 66 */       if ((parities >> 6 - i & 0x1) == 1) {
/* 67 */         digit += 10;
/*    */       }
/* 69 */       pos += appendPattern(result, pos, UPCEANReader.L_AND_G_PATTERNS[digit], false);
/*    */     } 
/*    */     
/* 72 */     appendPattern(result, pos, UPCEANReader.END_PATTERN, false);
/*    */     
/* 74 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCEWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */