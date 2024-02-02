/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class Credit
/*    */   extends BaseModel
/*    */ {
/*    */   String key;
/*    */   String adminId;
/*    */   
/*    */   public String getAdminId() {
/* 16 */     return this.adminId;
/*    */   }
/*    */   
/*    */   public void setAdminId(String adminId) {
/* 20 */     this.adminId = adminId;
/*    */   }
/*    */   
/*    */   public String getKey() {
/* 24 */     return this.key;
/*    */   }
/*    */   
/*    */   public void setKey(String key) {
/* 28 */     this.key = key;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Credit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */