/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class BakSub
/*    */   extends BaseModel
/*    */ {
/*    */   String bakId;
/*    */   String name;
/*    */   String content;
/*    */   
/*    */   public String getBakId() {
/* 15 */     return this.bakId;
/*    */   }
/*    */   
/*    */   public void setBakId(String bakId) {
/* 19 */     this.bakId = bakId;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 23 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 27 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getContent() {
/* 31 */     return this.content;
/*    */   }
/*    */   
/*    */   public void setContent(String content) {
/* 35 */     this.content = content;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\BakSub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */