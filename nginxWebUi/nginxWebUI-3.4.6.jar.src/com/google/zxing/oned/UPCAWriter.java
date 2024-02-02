/*    */ package com.google.zxing.oned;
/*    */ 
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.EncodeHintType;
/*    */ import com.google.zxing.Writer;
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
/*    */ public final class UPCAWriter
/*    */   implements Writer
/*    */ {
/* 34 */   private final EAN13Writer subWriter = new EAN13Writer();
/*    */ 
/*    */ 
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
/* 39 */     return encode(contents, format, width, height, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/* 48 */     if (format != BarcodeFormat.UPC_A) {
/* 49 */       throw new IllegalArgumentException("Can only encode UPC-A, but got " + format);
/*    */     }
/* 51 */     return this.subWriter.encode(preencode(contents), BarcodeFormat.EAN_13, width, height, hints);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String preencode(String contents) {
/*    */     int length;
/* 60 */     if ((length = contents.length()) == 11) {
/*    */       
/* 62 */       int sum = 0;
/* 63 */       for (int i = 0; i < 11; i++) {
/* 64 */         sum += (contents.charAt(i) - 48) * ((i % 2 == 0) ? 3 : 1);
/*    */       }
/* 66 */       contents = contents + ((1000 - sum) % 10);
/* 67 */     } else if (length != 12) {
/* 68 */       throw new IllegalArgumentException("Requested contents should be 11 or 12 digits long, but got " + contents
/* 69 */           .length());
/*    */     } 
/* 71 */     return "0" + contents;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCAWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */