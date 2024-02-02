/*    */ package com.google.zxing.aztec;
/*    */ 
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.EncodeHintType;
/*    */ import com.google.zxing.Writer;
/*    */ import com.google.zxing.aztec.encoder.AztecCode;
/*    */ import com.google.zxing.aztec.encoder.Encoder;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import java.nio.charset.Charset;
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
/*    */ public final class AztecWriter
/*    */   implements Writer
/*    */ {
/* 34 */   private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
/*    */ 
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) {
/* 38 */     return encode(contents, format, width, height, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) {
/* 43 */     Charset charset = DEFAULT_CHARSET;
/* 44 */     int eccPercent = 33;
/* 45 */     int layers = 0;
/* 46 */     if (hints != null) {
/* 47 */       if (hints.containsKey(EncodeHintType.CHARACTER_SET)) {
/* 48 */         charset = Charset.forName(hints.get(EncodeHintType.CHARACTER_SET).toString());
/*    */       }
/* 50 */       if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
/* 51 */         eccPercent = Integer.parseInt(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
/*    */       }
/* 53 */       if (hints.containsKey(EncodeHintType.AZTEC_LAYERS)) {
/* 54 */         layers = Integer.parseInt(hints.get(EncodeHintType.AZTEC_LAYERS).toString());
/*    */       }
/*    */     } 
/* 57 */     return encode(contents, format, width, height, charset, eccPercent, layers);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Charset charset, int eccPercent, int layers) {
/* 63 */     if (format != BarcodeFormat.AZTEC) {
/* 64 */       throw new IllegalArgumentException("Can only encode AZTEC, but got " + format);
/*    */     }
/*    */     
/* 67 */     return renderResult(Encoder.encode(contents.getBytes(charset), eccPercent, layers), width, height);
/*    */   }
/*    */   
/*    */   private static BitMatrix renderResult(AztecCode code, int width, int height) {
/*    */     BitMatrix input;
/* 72 */     if ((input = code.getMatrix()) == null) {
/* 73 */       throw new IllegalStateException();
/*    */     }
/* 75 */     int inputWidth = input.getWidth();
/* 76 */     int inputHeight = input.getHeight();
/* 77 */     int outputWidth = Math.max(width, inputWidth);
/* 78 */     int outputHeight = Math.max(height, inputHeight);
/*    */     
/* 80 */     int multiple = Math.min(outputWidth / inputWidth, outputHeight / inputHeight);
/* 81 */     int leftPadding = (outputWidth - inputWidth * multiple) / 2;
/* 82 */     int topPadding = (outputHeight - inputHeight * multiple) / 2;
/*    */     
/* 84 */     BitMatrix output = new BitMatrix(outputWidth, outputHeight);
/*    */     int outputY;
/* 86 */     for (int inputY = 0; inputY < inputHeight; inputY++, outputY += multiple) {
/*    */       int outputX;
/* 88 */       for (int inputX = 0; inputX < inputWidth; inputX++, outputX += multiple) {
/* 89 */         if (input.get(inputX, inputY)) {
/* 90 */           output.setRegion(outputX, outputY, multiple, multiple);
/*    */         }
/*    */       } 
/*    */     } 
/* 94 */     return output;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\AztecWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */