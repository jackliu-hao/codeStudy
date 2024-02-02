/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class Bak
/*    */   extends BaseModel {
/*    */   String time;
/*    */   String content;
/*    */   
/*    */   public String getContent() {
/* 13 */     return this.content;
/*    */   }
/*    */   
/*    */   public void setContent(String content) {
/* 17 */     this.content = content;
/*    */   }
/*    */   
/*    */   public String getTime() {
/* 21 */     return this.time;
/*    */   }
/*    */   
/*    */   public void setTime(String time) {
/* 25 */     this.time = time;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Bak.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */