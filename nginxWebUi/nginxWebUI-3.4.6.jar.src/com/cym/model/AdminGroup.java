/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class AdminGroup
/*    */   extends BaseModel
/*    */ {
/*    */   String adminId;
/*    */   String groupId;
/*    */   
/*    */   public String getAdminId() {
/* 16 */     return this.adminId;
/*    */   }
/*    */   public void setAdminId(String adminId) {
/* 19 */     this.adminId = adminId;
/*    */   }
/*    */   public String getGroupId() {
/* 22 */     return this.groupId;
/*    */   }
/*    */   public void setGroupId(String groupId) {
/* 25 */     this.groupId = groupId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\AdminGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */