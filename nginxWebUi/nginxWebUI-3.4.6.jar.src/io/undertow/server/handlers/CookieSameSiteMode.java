/*    */ package io.undertow.server.handlers;
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
/*    */ public enum CookieSameSiteMode
/*    */ {
/* 28 */   STRICT("Strict"),
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 34 */   LAX("Lax"),
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 39 */   NONE("None");
/*    */   static {
/* 41 */     SAMESITE_MODES = values();
/*    */   }
/*    */ 
/*    */   
/*    */   private static final CookieSameSiteMode[] SAMESITE_MODES;
/*    */   private final String label;
/*    */   
/*    */   CookieSameSiteMode(String label) {
/* 49 */     this.label = label;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String lookupModeString(String mode) {
/* 59 */     for (CookieSameSiteMode m : SAMESITE_MODES) {
/* 60 */       if (m.name().equalsIgnoreCase(mode)) {
/* 61 */         return m.toString();
/*    */       }
/*    */     } 
/* 64 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 69 */     return this.label;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\CookieSameSiteMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */