/*    */ package com.google.zxing.multi.qrcode.detector;
/*    */ 
/*    */ import com.google.zxing.DecodeHintType;
/*    */ import com.google.zxing.NotFoundException;
/*    */ import com.google.zxing.ReaderException;
/*    */ import com.google.zxing.ResultPointCallback;
/*    */ import com.google.zxing.common.BitMatrix;
/*    */ import com.google.zxing.common.DetectorResult;
/*    */ import com.google.zxing.qrcode.detector.Detector;
/*    */ import com.google.zxing.qrcode.detector.FinderPatternInfo;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public final class MultiDetector
/*    */   extends Detector
/*    */ {
/* 41 */   private static final DetectorResult[] EMPTY_DETECTOR_RESULTS = new DetectorResult[0];
/*    */   
/*    */   public MultiDetector(BitMatrix image) {
/* 44 */     super(image);
/*    */   }
/*    */   
/*    */   public DetectorResult[] detectMulti(Map<DecodeHintType, ?> hints) throws NotFoundException {
/* 48 */     BitMatrix image = getImage();
/*    */     
/* 50 */     ResultPointCallback resultPointCallback = (hints == null) ? null : (ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
/*    */     
/*    */     FinderPatternInfo[] infos;
/*    */     
/* 54 */     if ((infos = (new MultiFinderPatternFinder(image, resultPointCallback)).findMulti(hints)).length == 0) {
/* 55 */       throw NotFoundException.getNotFoundInstance();
/*    */     }
/*    */     
/* 58 */     List<DetectorResult> result = new ArrayList<>(); FinderPatternInfo[] arrayOfFinderPatternInfo1; int i; byte b;
/* 59 */     for (i = (arrayOfFinderPatternInfo1 = infos).length, b = 0; b < i; ) { FinderPatternInfo info = arrayOfFinderPatternInfo1[b];
/*    */       try {
/* 61 */         result.add(processFinderPatternInfo(info));
/* 62 */       } catch (ReaderException readerException) {}
/*    */       
/*    */       b++; }
/*    */     
/* 66 */     if (result.isEmpty()) {
/* 67 */       return EMPTY_DETECTOR_RESULTS;
/*    */     }
/* 69 */     return result.<DetectorResult>toArray(new DetectorResult[result.size()]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\multi\qrcode\detector\MultiDetector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */