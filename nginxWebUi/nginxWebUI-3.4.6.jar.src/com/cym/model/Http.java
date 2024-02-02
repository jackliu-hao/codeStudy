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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Table
/*    */ public class Http
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String value;
/*    */   String unit;
/*    */   @JsonIgnore
/*    */   Long seq;
/*    */   
/*    */   public Http() {}
/*    */   
/*    */   public Http(String name, String value, Long seq) {
/* 33 */     this.name = name;
/* 34 */     this.value = value;
/* 35 */     this.seq = seq;
/*    */   }
/*    */   
/*    */   public Long getSeq() {
/* 39 */     return this.seq;
/*    */   }
/*    */   
/*    */   public void setSeq(Long seq) {
/* 43 */     this.seq = seq;
/*    */   }
/*    */   
/*    */   public String getUnit() {
/* 47 */     return this.unit;
/*    */   }
/*    */   
/*    */   public void setUnit(String unit) {
/* 51 */     this.unit = unit;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 55 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 59 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 63 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 67 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Http.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */