/*    */ package com.google.zxing.client.result;
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
/*    */ public final class WifiParsedResult
/*    */   extends ParsedResult
/*    */ {
/*    */   private final String ssid;
/*    */   private final String networkEncryption;
/*    */   private final String password;
/*    */   private final boolean hidden;
/*    */   
/*    */   public WifiParsedResult(String networkEncryption, String ssid, String password) {
/* 32 */     this(networkEncryption, ssid, password, false);
/*    */   }
/*    */   
/*    */   public WifiParsedResult(String networkEncryption, String ssid, String password, boolean hidden) {
/* 36 */     super(ParsedResultType.WIFI);
/* 37 */     this.ssid = ssid;
/* 38 */     this.networkEncryption = networkEncryption;
/* 39 */     this.password = password;
/* 40 */     this.hidden = hidden;
/*    */   }
/*    */   
/*    */   public String getSsid() {
/* 44 */     return this.ssid;
/*    */   }
/*    */   
/*    */   public String getNetworkEncryption() {
/* 48 */     return this.networkEncryption;
/*    */   }
/*    */   
/*    */   public String getPassword() {
/* 52 */     return this.password;
/*    */   }
/*    */   
/*    */   public boolean isHidden() {
/* 56 */     return this.hidden;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getDisplayResult() {
/* 61 */     StringBuilder result = new StringBuilder(80);
/* 62 */     maybeAppend(this.ssid, result);
/* 63 */     maybeAppend(this.networkEncryption, result);
/* 64 */     maybeAppend(this.password, result);
/* 65 */     maybeAppend(Boolean.toString(this.hidden), result);
/* 66 */     return result.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\WifiParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */