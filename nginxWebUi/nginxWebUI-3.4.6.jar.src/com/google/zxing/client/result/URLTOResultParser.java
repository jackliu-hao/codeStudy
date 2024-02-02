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
/*    */ public final class URLTOResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public URIParsedResult parse(Result result) {
/*    */     String rawText;
/* 33 */     if (!(rawText = getMassagedText(result)).startsWith("urlto:") && !rawText.startsWith("URLTO:")) {
/* 34 */       return null;
/*    */     }
/*    */     int titleEnd;
/* 37 */     if ((titleEnd = rawText.indexOf(':', 6)) < 0) {
/* 38 */       return null;
/*    */     }
/* 40 */     String title = (titleEnd <= 6) ? null : rawText.substring(6, titleEnd);
/* 41 */     String uri = rawText.substring(titleEnd + 1);
/* 42 */     return new URIParsedResult(uri, title);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\URLTOResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */