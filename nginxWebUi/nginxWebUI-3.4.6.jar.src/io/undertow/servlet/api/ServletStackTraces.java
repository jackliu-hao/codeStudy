/*    */ package io.undertow.servlet.api;
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
/*    */ public enum ServletStackTraces
/*    */ {
/* 26 */   NONE("none"),
/* 27 */   LOCAL_ONLY("local-only"),
/* 28 */   ALL("all");
/*    */   
/*    */   private final String value;
/*    */   
/*    */   ServletStackTraces(String value) {
/* 33 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 38 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletStackTraces.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */