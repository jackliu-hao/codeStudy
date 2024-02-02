/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class OperateLog
/*    */   extends BaseModel
/*    */ {
/*    */   String adminName;
/*    */   String beforeConf;
/*    */   String afterConf;
/*    */   
/*    */   public String getAdminName() {
/* 17 */     return this.adminName;
/*    */   }
/*    */   
/*    */   public void setAdminName(String adminName) {
/* 21 */     this.adminName = adminName;
/*    */   }
/*    */   
/*    */   public String getBeforeConf() {
/* 25 */     return this.beforeConf;
/*    */   }
/*    */   
/*    */   public void setBeforeConf(String beforeConf) {
/* 29 */     this.beforeConf = beforeConf;
/*    */   }
/*    */   
/*    */   public String getAfterConf() {
/* 33 */     return this.afterConf;
/*    */   }
/*    */   
/*    */   public void setAfterConf(String afterConf) {
/* 37 */     this.afterConf = afterConf;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\OperateLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */