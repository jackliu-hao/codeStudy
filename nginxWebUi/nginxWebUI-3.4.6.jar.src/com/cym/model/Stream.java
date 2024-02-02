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
/*    */ public class Stream
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String value;
/*    */   @JsonIgnore
/*    */   Long seq;
/*    */   
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 30 */     this.name = name;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 34 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(String value) {
/* 38 */     this.value = value;
/*    */   }
/*    */   
/*    */   public Long getSeq() {
/* 42 */     return this.seq;
/*    */   }
/*    */   
/*    */   public void setSeq(Long seq) {
/* 46 */     this.seq = seq;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Stream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */