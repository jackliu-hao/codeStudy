/*    */ package com.google.zxing.client.result;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public final class SMSTOMMSTOResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public SMSParsedResult parse(Result result) {
/*    */     String rawText;
/* 36 */     if (!(rawText = getMassagedText(result)).startsWith("smsto:") && !rawText.startsWith("SMSTO:") && 
/* 37 */       !rawText.startsWith("mmsto:") && !rawText.startsWith("MMSTO:")) {
/* 38 */       return null;
/*    */     }
/*    */ 
/*    */     
/* 42 */     String number = rawText.substring(6);
/* 43 */     String body = null;
/*    */     int bodyStart;
/* 45 */     if ((bodyStart = number.indexOf(':')) >= 0) {
/* 46 */       body = number.substring(bodyStart + 1);
/* 47 */       number = number.substring(0, bodyStart);
/*    */     } 
/* 49 */     return new SMSParsedResult(number, null, null, body);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\SMSTOMMSTOResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */