/*     */ package com.google.zxing.oned;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.common.BitArray;
/*     */ import com.google.zxing.oned.rss.RSS14Reader;
/*     */ import com.google.zxing.oned.rss.expanded.RSSExpandedReader;
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
/*     */ public final class MultiFormatOneDReader
/*     */   extends OneDReader
/*     */ {
/*     */   private final OneDReader[] readers;
/*     */   
/*     */   public MultiFormatOneDReader(Map<DecodeHintType, ?> hints) {
/*  44 */     Collection<BarcodeFormat> possibleFormats = (hints == null) ? null : (Collection<BarcodeFormat>)hints.get(DecodeHintType.POSSIBLE_FORMATS);
/*     */     
/*  46 */     boolean useCode39CheckDigit = (hints != null && hints.get(DecodeHintType.ASSUME_CODE_39_CHECK_DIGIT) != null);
/*  47 */     Collection<OneDReader> readers = new ArrayList<>();
/*  48 */     if (possibleFormats != null) {
/*  49 */       if (possibleFormats.contains(BarcodeFormat.EAN_13) || possibleFormats
/*  50 */         .contains(BarcodeFormat.UPC_A) || possibleFormats
/*  51 */         .contains(BarcodeFormat.EAN_8) || possibleFormats
/*  52 */         .contains(BarcodeFormat.UPC_E)) {
/*  53 */         readers.add(new MultiFormatUPCEANReader(hints));
/*     */       }
/*  55 */       if (possibleFormats.contains(BarcodeFormat.CODE_39)) {
/*  56 */         readers.add(new Code39Reader(useCode39CheckDigit));
/*     */       }
/*  58 */       if (possibleFormats.contains(BarcodeFormat.CODE_93)) {
/*  59 */         readers.add(new Code93Reader());
/*     */       }
/*  61 */       if (possibleFormats.contains(BarcodeFormat.CODE_128)) {
/*  62 */         readers.add(new Code128Reader());
/*     */       }
/*  64 */       if (possibleFormats.contains(BarcodeFormat.ITF)) {
/*  65 */         readers.add(new ITFReader());
/*     */       }
/*  67 */       if (possibleFormats.contains(BarcodeFormat.CODABAR)) {
/*  68 */         readers.add(new CodaBarReader());
/*     */       }
/*  70 */       if (possibleFormats.contains(BarcodeFormat.RSS_14)) {
/*  71 */         readers.add(new RSS14Reader());
/*     */       }
/*  73 */       if (possibleFormats.contains(BarcodeFormat.RSS_EXPANDED)) {
/*  74 */         readers.add(new RSSExpandedReader());
/*     */       }
/*     */     } 
/*  77 */     if (readers.isEmpty()) {
/*  78 */       readers.add(new MultiFormatUPCEANReader(hints));
/*  79 */       readers.add(new Code39Reader());
/*  80 */       readers.add(new CodaBarReader());
/*  81 */       readers.add(new Code93Reader());
/*  82 */       readers.add(new Code128Reader());
/*  83 */       readers.add(new ITFReader());
/*  84 */       readers.add(new RSS14Reader());
/*  85 */       readers.add(new RSSExpandedReader());
/*     */     } 
/*  87 */     this.readers = readers.<OneDReader>toArray(new OneDReader[readers.size()]);
/*     */   }
/*     */   
/*     */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*     */     OneDReader[] arrayOfOneDReader;
/*     */     int i;
/*     */     byte b;
/*  94 */     for (i = (arrayOfOneDReader = this.readers).length, b = 0; b < i; ) { OneDReader reader = arrayOfOneDReader[b];
/*     */       try {
/*  96 */         return reader.decodeRow(rowNumber, row, hints);
/*  97 */       } catch (ReaderException readerException) {}
/*     */       
/*     */       b++; }
/*     */ 
/*     */     
/* 102 */     throw NotFoundException.getNotFoundInstance();
/*     */   } public void reset() {
/*     */     OneDReader[] arrayOfOneDReader;
/*     */     int i;
/*     */     byte b;
/* 107 */     for (i = (arrayOfOneDReader = this.readers).length, b = 0; b < i; ) { arrayOfOneDReader[b]
/* 108 */         .reset();
/*     */       b++; }
/*     */   
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\MultiFormatOneDReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */