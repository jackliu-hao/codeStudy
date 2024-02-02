/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class Group
/*    */   extends BaseModel {
/*    */   String parentId;
/*    */   String name;
/*    */   
/*    */   public String getParentId() {
/* 13 */     return this.parentId;
/*    */   }
/*    */   
/*    */   public void setParentId(String parentId) {
/* 17 */     this.parentId = parentId;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 21 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 25 */     this.name = name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Group.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */