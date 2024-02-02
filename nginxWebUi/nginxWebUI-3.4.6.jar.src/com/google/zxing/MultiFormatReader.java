/*     */ package com.google.zxing;
/*     */ 
/*     */ import com.google.zxing.aztec.AztecReader;
/*     */ import com.google.zxing.datamatrix.DataMatrixReader;
/*     */ import com.google.zxing.maxicode.MaxiCodeReader;
/*     */ import com.google.zxing.oned.MultiFormatOneDReader;
/*     */ import com.google.zxing.pdf417.PDF417Reader;
/*     */ import com.google.zxing.qrcode.QRCodeReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MultiFormatReader
/*     */   implements Reader
/*     */ {
/*     */   private Map<DecodeHintType, ?> hints;
/*     */   private Reader[] readers;
/*     */   
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException {
/*  54 */     setHints(null);
/*  55 */     return decodeInternal(image);
/*     */   }
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
/*     */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  68 */     setHints(hints);
/*  69 */     return decodeInternal(image);
/*     */   }
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
/*     */   public Result decodeWithState(BinaryBitmap image) throws NotFoundException {
/*  82 */     if (this.readers == null) {
/*  83 */       setHints(null);
/*     */     }
/*  85 */     return decodeInternal(image);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHints(Map<DecodeHintType, ?> hints) {
/*  96 */     this.hints = hints;
/*     */     
/*  98 */     boolean tryHarder = (hints != null && hints.containsKey(DecodeHintType.TRY_HARDER));
/*     */ 
/*     */     
/* 101 */     Collection<BarcodeFormat> formats = (hints == null) ? null : (Collection<BarcodeFormat>)hints.get(DecodeHintType.POSSIBLE_FORMATS);
/* 102 */     Collection<Reader> readers = new ArrayList<>();
/* 103 */     if (formats != null) {
/*     */       boolean addOneDReader;
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
/* 117 */       if ((addOneDReader = (formats.contains(BarcodeFormat.UPC_A) || formats.contains(BarcodeFormat.UPC_E) || formats.contains(BarcodeFormat.EAN_13) || formats.contains(BarcodeFormat.EAN_8) || formats.contains(BarcodeFormat.CODABAR) || formats.contains(BarcodeFormat.CODE_39) || formats.contains(BarcodeFormat.CODE_93) || formats.contains(BarcodeFormat.CODE_128) || formats.contains(BarcodeFormat.ITF) || formats.contains(BarcodeFormat.RSS_14) || formats.contains(BarcodeFormat.RSS_EXPANDED))) && !tryHarder) {
/* 118 */         readers.add(new MultiFormatOneDReader(hints));
/*     */       }
/* 120 */       if (formats.contains(BarcodeFormat.QR_CODE)) {
/* 121 */         readers.add(new QRCodeReader());
/*     */       }
/* 123 */       if (formats.contains(BarcodeFormat.DATA_MATRIX)) {
/* 124 */         readers.add(new DataMatrixReader());
/*     */       }
/* 126 */       if (formats.contains(BarcodeFormat.AZTEC)) {
/* 127 */         readers.add(new AztecReader());
/*     */       }
/* 129 */       if (formats.contains(BarcodeFormat.PDF_417)) {
/* 130 */         readers.add(new PDF417Reader());
/*     */       }
/* 132 */       if (formats.contains(BarcodeFormat.MAXICODE)) {
/* 133 */         readers.add(new MaxiCodeReader());
/*     */       }
/*     */       
/* 136 */       if (addOneDReader && tryHarder) {
/* 137 */         readers.add(new MultiFormatOneDReader(hints));
/*     */       }
/*     */     } 
/* 140 */     if (readers.isEmpty()) {
/* 141 */       if (!tryHarder) {
/* 142 */         readers.add(new MultiFormatOneDReader(hints));
/*     */       }
/*     */       
/* 145 */       readers.add(new QRCodeReader());
/* 146 */       readers.add(new DataMatrixReader());
/* 147 */       readers.add(new AztecReader());
/* 148 */       readers.add(new PDF417Reader());
/* 149 */       readers.add(new MaxiCodeReader());
/*     */       
/* 151 */       if (tryHarder) {
/* 152 */         readers.add(new MultiFormatOneDReader(hints));
/*     */       }
/*     */     } 
/* 155 */     this.readers = readers.<Reader>toArray(new Reader[readers.size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 160 */     if (this.readers != null) {
/* 161 */       Reader[] arrayOfReader; int i; byte b; for (i = (arrayOfReader = this.readers).length, b = 0; b < i; ) { arrayOfReader[b]
/* 162 */           .reset();
/*     */         b++; }
/*     */     
/*     */     } 
/*     */   }
/*     */   private Result decodeInternal(BinaryBitmap image) throws NotFoundException {
/* 168 */     if (this.readers != null) {
/* 169 */       Reader[] arrayOfReader; int i; byte b; for (i = (arrayOfReader = this.readers).length, b = 0; b < i; ) { Reader reader = arrayOfReader[b];
/*     */         try {
/* 171 */           return reader.decode(image, this.hints);
/* 172 */         } catch (ReaderException readerException) {}
/*     */         
/*     */         b++; }
/*     */     
/*     */     } 
/* 177 */     throw NotFoundException.getNotFoundInstance();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\MultiFormatReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */