/*    */ package com.google.zxing.oned;
/*    */ 
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.BinaryBitmap;
/*    */ import com.google.zxing.ChecksumException;
/*    */ import com.google.zxing.DecodeHintType;
/*    */ import com.google.zxing.FormatException;
/*    */ import com.google.zxing.NotFoundException;
/*    */ import com.google.zxing.Result;
/*    */ import com.google.zxing.common.BitArray;
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
/*    */ public final class UPCAReader
/*    */   extends UPCEANReader
/*    */ {
/* 38 */   private final UPCEANReader ean13Reader = new EAN13Reader();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Result decodeRow(int rowNumber, BitArray row, int[] startGuardRange, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
/* 46 */     return maybeReturnResult(this.ean13Reader.decodeRow(rowNumber, row, startGuardRange, hints));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
/* 52 */     return maybeReturnResult(this.ean13Reader.decodeRow(rowNumber, row, hints));
/*    */   }
/*    */ 
/*    */   
/*    */   public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
/* 57 */     return maybeReturnResult(this.ean13Reader.decode(image));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
/* 63 */     return maybeReturnResult(this.ean13Reader.decode(image, hints));
/*    */   }
/*    */ 
/*    */   
/*    */   BarcodeFormat getBarcodeFormat() {
/* 68 */     return BarcodeFormat.UPC_A;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected int decodeMiddle(BitArray row, int[] startRange, StringBuilder resultString) throws NotFoundException {
/* 74 */     return this.ean13Reader.decodeMiddle(row, startRange, resultString);
/*    */   }
/*    */   
/*    */   private static Result maybeReturnResult(Result result) throws FormatException {
/*    */     String text;
/* 79 */     if ((text = result.getText()).charAt(0) == '0') {
/* 80 */       return new Result(text.substring(1), null, result.getResultPoints(), BarcodeFormat.UPC_A);
/*    */     }
/* 82 */     throw FormatException.getFormatInstance();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\oned\UPCAReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */