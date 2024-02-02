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
/*    */ public class DatabaseObjectDescription
/*    */ {
/*    */   private String objectName;
/*    */   private DatabaseObject.DbObjectType objectType;
/*    */   
/*    */   public DatabaseObjectDescription(String name, String type) {
/* 48 */     this.objectName = name;
/* 49 */     this.objectType = DatabaseObject.DbObjectType.valueOf(type);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getObjectName() {
/* 58 */     return this.objectName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DatabaseObject.DbObjectType getObjectType() {
/* 67 */     return this.objectType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DatabaseObjectDescription.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */