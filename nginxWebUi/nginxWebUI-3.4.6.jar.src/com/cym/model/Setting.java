/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ 
/*    */ @Table
/*    */ public class Setting extends BaseModel {
/*    */   String key;
/*    */   
/*    */   public String getKey() {
/* 11 */     return this.key;
/*    */   } String value;
/*    */   public void setKey(String key) {
/* 14 */     this.key = key;
/*    */   }
/*    */   public String getValue() {
/* 17 */     return this.value;
/*    */   }
/*    */   public void setValue(String value) {
/* 20 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Setting.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */