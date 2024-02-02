/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.BarcodeFormat;
/*    */ import com.google.zxing.Result;
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
/*    */ public final class ISBNResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public ISBNParsedResult parse(Result result) {
/* 34 */     if (result.getBarcodeFormat() != 
/* 35 */       BarcodeFormat.EAN_13) {
/* 36 */       return null;
/*    */     }
/*    */     String rawText;
/* 39 */     if ((rawText = getMassagedText(result)).length() != 
/* 40 */       13) {
/* 41 */       return null;
/*    */     }
/* 43 */     if (!rawText.startsWith("978") && !rawText.startsWith("979")) {
/* 44 */       return null;
/*    */     }
/*    */     
/* 47 */     return new ISBNParsedResult(rawText);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\ISBNResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */