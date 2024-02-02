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
/*    */ public final class BookmarkDoCoMoResultParser
/*    */   extends AbstractDoCoMoResultParser
/*    */ {
/*    */   public URIParsedResult parse(Result result) {
/*    */     String rawText;
/* 29 */     if (!(rawText = result.getText()).startsWith("MEBKM:")) {
/* 30 */       return null;
/*    */     }
/* 32 */     String title = matchSingleDoCoMoPrefixedField("TITLE:", rawText, true);
/*    */     String[] rawUri;
/* 34 */     if ((rawUri = matchDoCoMoPrefixedField("URL:", rawText, true)) == null) {
/* 35 */       return null;
/*    */     }
/*    */     String uri;
/* 38 */     return URIResultParser.isBasicallyValidURI(uri = rawUri[0]) ? new URIParsedResult(uri, title) : null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\BookmarkDoCoMoResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */