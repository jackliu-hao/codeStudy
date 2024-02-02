/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.common.BitArray;
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
/*     */ public final class MultiFormatUPCEANReader
/*     */   extends OneDReader
/*     */ {
/*     */   private final UPCEANReader[] readers;
/*     */   
/*     */   public MultiFormatUPCEANReader(Map<DecodeHintType, ?> hints) {
/*  45 */     Collection<BarcodeFormat> possibleFormats = (hints == null) ? null : (Collection<BarcodeFormat>)hints.get(DecodeHintType.POSSIBLE_FORMATS);
/*  46 */     Collection<UPCEANReader> readers = new ArrayList<>();
/*  47 */     if (possibleFormats != null) {
/*  48 */       if (possibleFormats.contains(BarcodeFormat.EAN_13)) {
/*  49 */         readers.add(new EAN13Reader());
/*  50 */       } else if (possibleFormats.contains(BarcodeFormat.UPC_A)) {
/*  51 */         readers.add(new UPCAReader());
/*     */       } 
/*  53 */       if (possibleFormats.contains(BarcodeFormat.EAN_8)) {
/*  54 */         readers.add(new EAN8Reader());
/*     */       }
/*  56 */       if (possibleFormats.contains(BarcodeFormat.UPC_E)) {
/*  57 */         readers.add(new UPCEReader());
/*     */       }
/*     */     } 
/*  60 */     if (readers.isEmpty()) {
/*  61 */       readers.add(new EAN13Reader());
/*     */       
/*  63 */       readers.add(new EAN8Reader());
/*  64 */       readers.add(new UPCEReader());
/*     */     } 
/*  66 */     this.readers = readers.<UPCEANReader>toArray(new UPCEANReader[readers.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  74 */     int[] startGuardPattern = UPCEANReader.findStartGuardPattern(row); UPCEANReader[] arrayOfUPCEANReader; int i; byte b;
/*  75 */     for (i = (arrayOfUPCEANReader = this.readers).length, b = 0; b < i; ) { Result result; UPCEANReader reader = arrayOfUPCEANReader[b];
/*     */       
/*     */       try {
/*  78 */         result = reader.decodeRow(rowNumber, row, startGuardPattern, hints);
/*  79 */       } catch (ReaderException readerException) {}
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
/*  96 */       boolean ean13MayBeUPCA = (result.getBarcodeFormat() == BarcodeFormat.EAN_13 && result.getText().charAt(0) == '0');
/*     */       
/*     */       Collection<BarcodeFormat> possibleFormats;
/*     */       
/* 100 */       boolean canReturnUPCA = ((possibleFormats = (Collection<BarcodeFormat>)((hints == null) ? null : hints.get(DecodeHintType.POSSIBLE_FORMATS))) == null || possibleFormats.contains(BarcodeFormat.UPC_A));
/*     */       
/* 102 */       if (ean13MayBeUPCA && canReturnUPCA) {
/*     */         Result resultUPCA;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 108 */         (resultUPCA = new Result(result.getText().substring(1), result.getRawBytes(), result.getResultPoints(), BarcodeFormat.UPC_A)).putAllMetadata(result.getResultMetadata());
/* 109 */         return resultUPCA;
/*     */       } 
/* 111 */       return result; }
/*     */ 
/*     */     
/* 114 */     throw NotFoundException.getNotFoundInstance();
/*     */   } public void reset() {
/*     */     UPCEANReader[] arrayOfUPCEANReader;
/*     */     int i;
/*     */     byte b;
/* 119 */     for (i = (arrayOfUPCEANReader = this.readers).length, b = 0; b < i; ) { arrayOfUPCEANReader[b]
/* 120 */         .reset();
/*     */       b++; }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\MultiFormatUPCEANReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */