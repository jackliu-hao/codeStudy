/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.Table;
/*    */ import com.fasterxml.jackson.annotation.JsonIgnore;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class Basic
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String value;
/*    */   @JsonIgnore
/*    */   Long seq;
/*    */   
/*    */   public Basic() {}
/*    */   
/*    */   public Basic(String name, String value, Long seq) {
/* 28 */     this.name = name;
/* 29 */     this.value = value;
/* 30 */     this.seq = seq;
/*    */   }
/*    */   
/*    */   public Long getSeq() {
/* 34 */     return this.seq;
/*    */   }
/*    */   
/*    */   public void setSeq(Long seq) {
/* 38 */     this.seq = seq;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 42 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 46 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 50 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 54 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Basic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */