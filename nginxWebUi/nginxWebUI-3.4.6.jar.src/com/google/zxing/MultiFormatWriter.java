/*     */ package com.google.zxing;
/*     */ 
/*     */ import com.google.zxing.aztec.AztecWriter;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.datamatrix.DataMatrixWriter;
/*     */ import com.google.zxing.oned.CodaBarWriter;
/*     */ import com.google.zxing.oned.Code128Writer;
/*     */ import com.google.zxing.oned.Code39Writer;
/*     */ import com.google.zxing.oned.Code93Writer;
/*     */ import com.google.zxing.oned.EAN13Writer;
/*     */ import com.google.zxing.oned.EAN8Writer;
/*     */ import com.google.zxing.oned.ITFWriter;
/*     */ import com.google.zxing.oned.UPCAWriter;
/*     */ import com.google.zxing.oned.UPCEWriter;
/*     */ import com.google.zxing.pdf417.PDF417Writer;
/*     */ import com.google.zxing.qrcode.QRCodeWriter;
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
/*     */ 
/*     */ 
/*     */ public final class MultiFormatWriter
/*     */   implements Writer
/*     */ {
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
/*  49 */     return encode(contents, format, width, height, null); } public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException { EAN8Writer eAN8Writer; UPCEWriter uPCEWriter; EAN13Writer eAN13Writer; UPCAWriter uPCAWriter;
/*     */     QRCodeWriter qRCodeWriter;
/*     */     Code39Writer code39Writer;
/*     */     Code93Writer code93Writer;
/*     */     Code128Writer code128Writer;
/*     */     ITFWriter iTFWriter;
/*     */     PDF417Writer pDF417Writer;
/*     */     CodaBarWriter codaBarWriter;
/*     */     DataMatrixWriter dataMatrixWriter;
/*     */     AztecWriter aztecWriter;
/*  59 */     switch (format) {
/*     */       case EAN_8:
/*  61 */         eAN8Writer = new EAN8Writer();
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
/* 102 */         return eAN8Writer.encode(contents, format, width, height, hints);case UPC_E: uPCEWriter = new UPCEWriter(); return uPCEWriter.encode(contents, format, width, height, hints);case EAN_13: eAN13Writer = new EAN13Writer(); return eAN13Writer.encode(contents, format, width, height, hints);case UPC_A: uPCAWriter = new UPCAWriter(); return uPCAWriter.encode(contents, format, width, height, hints);case QR_CODE: qRCodeWriter = new QRCodeWriter(); return qRCodeWriter.encode(contents, format, width, height, hints);case CODE_39: code39Writer = new Code39Writer(); return code39Writer.encode(contents, format, width, height, hints);case CODE_93: code93Writer = new Code93Writer(); return code93Writer.encode(contents, format, width, height, hints);case CODE_128: code128Writer = new Code128Writer(); return code128Writer.encode(contents, format, width, height, hints);case ITF: iTFWriter = new ITFWriter(); return iTFWriter.encode(contents, format, width, height, hints);case PDF_417: pDF417Writer = new PDF417Writer(); return pDF417Writer.encode(contents, format, width, height, hints);case CODABAR: codaBarWriter = new CodaBarWriter(); return codaBarWriter.encode(contents, format, width, height, hints);case DATA_MATRIX: dataMatrixWriter = new DataMatrixWriter(); return dataMatrixWriter.encode(contents, format, width, height, hints);case AZTEC: aztecWriter = new AztecWriter(); return aztecWriter.encode(contents, format, width, height, hints);
/*     */     } 
/*     */     throw new IllegalArgumentException("No encoder available for format " + format); }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\MultiFormatWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */