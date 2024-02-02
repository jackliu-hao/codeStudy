/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class Log extends BaseModel {
/*    */   String path;
/*    */   
/*    */   public String getPath() {
/* 11 */     return this.path;
/*    */   }
/*    */   
/*    */   public void setPath(String path) {
/* 15 */     this.path = path;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */