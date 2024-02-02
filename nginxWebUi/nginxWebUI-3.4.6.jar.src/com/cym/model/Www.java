/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class Www
/*    */   extends BaseModel {
/*    */   String dir;
/*    */   
/*    */   public String getDir() {
/* 12 */     return this.dir;
/*    */   }
/*    */   
/*    */   public void setDir(String dir) {
/* 16 */     this.dir = dir;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Www.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */