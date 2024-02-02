/*    */ package com.cym.sqlhelper.bean;
/*    */ 
/*    */ import com.fasterxml.jackson.annotation.JsonIgnore;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BaseModel
/*    */   implements Serializable
/*    */ {
/*    */   String id;
/*    */   @JsonIgnore
/*    */   Long createTime;
/*    */   @JsonIgnore
/*    */   Long updateTime;
/*    */   
/*    */   public String getId() {
/* 18 */     return this.id;
/*    */   }
/*    */   
/*    */   public void setId(String id) {
/* 22 */     this.id = id;
/*    */   }
/*    */   
/*    */   public Long getCreateTime() {
/* 26 */     return this.createTime;
/*    */   }
/*    */   
/*    */   public void setCreateTime(Long createTime) {
/* 30 */     this.createTime = createTime;
/*    */   }
/*    */   
/*    */   public Long getUpdateTime() {
/* 34 */     return this.updateTime;
/*    */   }
/*    */   
/*    */   public void setUpdateTime(Long updateTime) {
/* 38 */     this.updateTime = updateTime;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\bean\BaseModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */