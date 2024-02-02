/*    */ package com.mysql.cj.protocol.x;
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
/*    */ 
/*    */ 
/*    */ public enum XpluginStatementCommand
/*    */ {
/* 33 */   XPLUGIN_STMT_CREATE_COLLECTION("create_collection"), XPLUGIN_STMT_CREATE_COLLECTION_INDEX("create_collection_index"),
/* 34 */   XPLUGIN_STMT_DROP_COLLECTION("drop_collection"), XPLUGIN_STMT_DROP_COLLECTION_INDEX("drop_collection_index"),
/* 35 */   XPLUGIN_STMT_MODIFY_COLLECTION_OPTIONS("modify_collection_options"), XPLUGIN_STMT_PING("ping"), XPLUGIN_STMT_LIST_OBJECTS("list_objects"),
/* 36 */   XPLUGIN_STMT_ENABLE_NOTICES("enable_notices"), XPLUGIN_STMT_DISABLE_NOTICES("disable_notices"), XPLUGIN_STMT_LIST_NOTICES("list_notices");
/*    */   
/*    */   public String commandName;
/*    */ 
/*    */   
/*    */   XpluginStatementCommand(String commandName) {
/* 42 */     this.commandName = commandName;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XpluginStatementCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */