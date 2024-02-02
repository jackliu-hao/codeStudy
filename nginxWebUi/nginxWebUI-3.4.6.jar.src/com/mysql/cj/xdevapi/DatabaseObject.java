/*    */ package com.mysql.cj.xdevapi;
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
/*    */ public interface DatabaseObject
/*    */ {
/*    */   Session getSession();
/*    */   
/*    */   Schema getSchema();
/*    */   
/*    */   String getName();
/*    */   
/*    */   DbObjectStatus existsInDatabase();
/*    */   
/*    */   public enum DbObjectType
/*    */   {
/* 41 */     COLLECTION, TABLE, VIEW, COLLECTION_VIEW;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public enum DbObjectStatus
/*    */   {
/* 48 */     EXISTS, NOT_EXISTS, UNKNOWN;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DatabaseObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */