/*    */ package com.cym.model;
/*    */ 
/*    */ import com.cym.sqlhelper.bean.BaseModel;
/*    */ import com.cym.sqlhelper.config.InitValue;
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
/*    */ public class Upstream
/*    */   extends BaseModel
/*    */ {
/*    */   String name;
/*    */   String tactics;
/*    */   @InitValue("0")
/*    */   Integer proxyType;
/*    */   @InitValue("0")
/*    */   Integer monitor;
/*    */   String descr;
/*    */   @JsonIgnore
/*    */   Long seq;
/*    */   
/*    */   public String getDescr() {
/* 47 */     return this.descr;
/*    */   }
/*    */   
/*    */   public void setDescr(String descr) {
/* 51 */     this.descr = descr;
/*    */   }
/*    */   
/*    */   public Long getSeq() {
/* 55 */     return this.seq;
/*    */   }
/*    */   
/*    */   public void setSeq(Long seq) {
/* 59 */     this.seq = seq;
/*    */   }
/*    */   
/*    */   public Integer getMonitor() {
/* 63 */     return this.monitor;
/*    */   }
/*    */   
/*    */   public void setMonitor(Integer monitor) {
/* 67 */     this.monitor = monitor;
/*    */   }
/*    */   
/*    */   public Integer getProxyType() {
/* 71 */     return this.proxyType;
/*    */   }
/*    */   
/*    */   public void setProxyType(Integer proxyType) {
/* 75 */     this.proxyType = proxyType;
/*    */   }
/*    */   
/*    */   public String getTactics() {
/* 79 */     return this.tactics;
/*    */   }
/*    */   
/*    */   public void setTactics(String tactics) {
/* 83 */     this.tactics = tactics;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 87 */     return this.name;
/*    */   }
/*    */   
/*    */   public void setName(String name) {
/* 91 */     this.name = name;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\model\Upstream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */