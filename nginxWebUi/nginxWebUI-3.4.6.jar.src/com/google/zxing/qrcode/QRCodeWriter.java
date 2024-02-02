/*     */ package com.google.zxing.qrcode;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.Writer;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*     */ import com.google.zxing.qrcode.encoder.ByteMatrix;
/*     */ import com.google.zxing.qrcode.encoder.Encoder;
/*     */ import com.google.zxing.qrcode.encoder.QRCode;
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
/*     */ 
/*     */ public final class QRCodeWriter
/*     */   implements Writer
/*     */ {
/*     */   private static final int QUIET_ZONE_SIZE = 4;
/*     */   
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
/*  44 */     return encode(contents, format, width, height, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/*  54 */     if (contents.isEmpty()) {
/*  55 */       throw new IllegalArgumentException("Found empty contents");
/*     */     }
/*     */     
/*  58 */     if (format != BarcodeFormat.QR_CODE) {
/*  59 */       throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
/*     */     }
/*     */     
/*  62 */     if (width < 0 || height < 0) {
/*  63 */       throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' + height);
/*     */     }
/*     */ 
/*     */     
/*  67 */     ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
/*  68 */     int quietZone = 4;
/*  69 */     if (hints != null) {
/*  70 */       if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
/*  71 */         errorCorrectionLevel = ErrorCorrectionLevel.valueOf(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
/*     */       }
/*  73 */       if (hints.containsKey(EncodeHintType.MARGIN)) {
/*  74 */         quietZone = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  79 */     return renderResult(Encoder.encode(contents, errorCorrectionLevel, hints), width, height, quietZone);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
/*     */     ByteMatrix input;
/*  86 */     if ((input = code.getMatrix()) == null) {
/*  87 */       throw new IllegalStateException();
/*     */     }
/*  89 */     int inputWidth = input.getWidth();
/*  90 */     int inputHeight = input.getHeight();
/*  91 */     int qrWidth = inputWidth + (quietZone << 1);
/*  92 */     int qrHeight = inputHeight + (quietZone << 1);
/*  93 */     int outputWidth = Math.max(width, qrWidth);
/*  94 */     int outputHeight = Math.max(height, qrHeight);
/*     */     
/*  96 */     int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     int leftPadding = (outputWidth - inputWidth * multiple) / 2;
/* 102 */     int topPadding = (outputHeight - inputHeight * multiple) / 2;
/*     */     
/* 104 */     BitMatrix output = new BitMatrix(outputWidth, outputHeight);
/*     */     int outputY;
/* 106 */     for (int inputY = 0; inputY < inputHeight; inputY++, outputY += multiple) {
/*     */       int outputX;
/* 108 */       for (int inputX = 0; inputX < inputWidth; inputX++, outputX += multiple) {
/* 109 */         if (input.get(inputX, inputY) == 1) {
/* 110 */           output.setRegion(outputX, outputY, multiple, multiple);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 115 */     return output;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\QRCodeWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */