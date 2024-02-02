/*    */ package com.google.zxing.client.result;
/*    */ 
/*    */ import com.google.zxing.Result;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
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
/*    */ public final class URIResultParser
/*    */   extends ResultParser
/*    */ {
/* 32 */   private static final Pattern URL_WITH_PROTOCOL_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9+-.]+:");
/* 33 */   private static final Pattern URL_WITHOUT_PROTOCOL_PATTERN = Pattern.compile("([a-zA-Z0-9\\-]+\\.){1,6}[a-zA-Z]{2,}(:\\d{1,5})?(/|\\?|$)");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public URIParsedResult parse(Result result) {
/*    */     String rawText;
/* 43 */     if ((rawText = getMassagedText(result)).startsWith("URL:") || rawText.startsWith("URI:")) {
/* 44 */       return new URIParsedResult(rawText.substring(4).trim(), null);
/*    */     }
/*    */     
/* 47 */     return isBasicallyValidURI(rawText = rawText.trim()) ? new URIParsedResult(rawText, null) : null;
/*    */   }
/*    */   
/*    */   static boolean isBasicallyValidURI(String uri) {
/* 51 */     if (uri.contains(" "))
/*    */     {
/* 53 */       return false;
/*    */     }
/*    */     Matcher m;
/* 56 */     if ((m = URL_WITH_PROTOCOL_PATTERN.matcher(uri)).find() && m.start() == 0) {
/* 57 */       return true;
/*    */     }
/*    */     
/* 60 */     if ((m = URL_WITHOUT_PROTOCOL_PATTERN.matcher(uri)).find() && m.start() == 0) return true;  return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\URIResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */