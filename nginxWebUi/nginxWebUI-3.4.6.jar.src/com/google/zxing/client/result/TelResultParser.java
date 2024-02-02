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
/*    */ public final class TelResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public TelParsedResult parse(Result result) {
/*    */     String rawText;
/* 31 */     if (!(rawText = getMassagedText(result)).startsWith("tel:") && !rawText.startsWith("TEL:")) {
/* 32 */       return null;
/*    */     }
/*    */     
/* 35 */     String telURI = rawText.startsWith("TEL:") ? ("tel:" + rawText.substring(4)) : rawText;
/*    */     
/*    */     int queryStart;
/* 38 */     String number = ((queryStart = rawText.indexOf('?', 4)) < 0) ? rawText.substring(4) : rawText.substring(4, queryStart);
/* 39 */     return new TelParsedResult(number, telURI, null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\TelResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */