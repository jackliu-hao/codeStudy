/*    */ package io.undertow.server.handlers.proxy.mod_cluster;
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
/*    */ enum MCMPErrorCode
/*    */ {
/* 29 */   CANT_READ_NODE("MEM", "MEM: Can't read node"),
/* 30 */   CANT_UPDATE_NODE("MEM", "MEM: Can't update or insert node"),
/* 31 */   CANT_UPDATE_CONTEXT("MEM", "MEM: Can't update or insert context"),
/* 32 */   NODE_STILL_EXISTS("SYNTAX", "MEM: Old node still exist");
/*    */   
/*    */   private final String type;
/*    */   private final String message;
/*    */   
/*    */   MCMPErrorCode(String type, String message) {
/* 38 */     this.type = type;
/* 39 */     this.message = message;
/*    */   }
/*    */   
/*    */   public String getType() {
/* 43 */     return this.type;
/*    */   }
/*    */   
/*    */   public String getMessage() {
/* 47 */     return this.message;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\proxy\mod_cluster\MCMPErrorCode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */