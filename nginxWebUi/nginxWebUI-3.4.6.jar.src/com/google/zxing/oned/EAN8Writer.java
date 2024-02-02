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
/*    */ 
/*    */ 
/*    */ public final class EAN8Writer
/*    */   extends UPCEANWriter
/*    */ {
/*    */   private static final int CODE_WIDTH = 67;
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/* 45 */     if (format != BarcodeFormat.EAN_8) {
/* 46 */       throw new IllegalArgumentException("Can only encode EAN_8, but got " + format);
/*    */     }
/*    */ 
/*    */     
/* 50 */     return super.encode(contents, format, width, height, hints);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean[] encode(String contents) {
/* 58 */     if (contents.length() != 8) {
/* 59 */       throw new IllegalArgumentException("Requested contents should be 8 digits long, but got " + contents
/* 60 */           .length());
/*    */     }
/*    */     
/* 63 */     boolean[] result = new boolean[67];
/*    */ 
/*    */     
/* 66 */     int pos = 0 + appendPattern(result, 0, UPCEANReader.START_END_PATTERN, true);
/*    */     int i;
/* 68 */     for (i = 0; i <= 3; i++) {
/* 69 */       int digit = Integer.parseInt(contents.substring(i, i + 1));
/* 70 */       pos += appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], false);
/*    */     } 
/*    */     
/* 73 */     pos += appendPattern(result, pos, UPCEANReader.MIDDLE_PATTERN, false);
/*    */     
/* 75 */     for (i = 4; i <= 7; i++) {
/* 76 */       int digit = Integer.parseInt(contents.substring(i, i + 1));
/* 77 */       pos += appendPattern(result, pos, UPCEANReader.L_PATTERNS[digit], true);
/*    */     } 
/* 79 */     appendPattern(result, pos, UPCEANReader.START_END_PATTERN, true);
/*    */     
/* 81 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\EAN8Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */