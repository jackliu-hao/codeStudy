/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class Template
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String def;
/*    */   
/*    */   public String getName() {
/* 14 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 18 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getDef() {
/* 22 */     return this.def;
/*    */   }
/*    */   
/*    */   public void setDef(String def) {
/* 26 */     this.def = def;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Template.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */