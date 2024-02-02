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
/*    */ public final class WifiResultParser
/*    */   extends ResultParser
/*    */ {
/*    */   public WifiParsedResult parse(Result result) {
/*    */     String rawText;
/* 36 */     if (!(rawText = getMassagedText(result)).startsWith("WIFI:")) {
/* 37 */       return null;
/*    */     }
/*    */     String ssid;
/* 40 */     if ((ssid = matchSinglePrefixedField("S:", rawText, ';', false)) == null || ssid.isEmpty()) {
/* 41 */       return null;
/*    */     }
/* 43 */     String pass = matchSinglePrefixedField("P:", rawText, ';', false);
/*    */     String type;
/* 45 */     if ((type = matchSinglePrefixedField("T:", rawText, ';', false)) == null) {
/* 46 */       type = "nopass";
/*    */     }
/* 48 */     boolean hidden = Boolean.parseBoolean(matchSinglePrefixedField("H:", rawText, ';', false));
/* 49 */     return new WifiParsedResult(type, ssid, pass, hidden);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\WifiResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */