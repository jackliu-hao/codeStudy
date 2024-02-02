/*    */ package com.cym.sqlhelper.bean;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Page<T>
/*    */ {
/* 14 */   Long count = Long.valueOf(0L);
/*    */ 
/*    */ 
/*    */   
/* 18 */   Integer curr = Integer.valueOf(1);
/*    */ 
/*    */ 
/*    */   
/* 22 */   Integer limit = Integer.valueOf(10);
/*    */ 
/*    */ 
/*    */   
/* 26 */   List records = Collections.emptyList();
/*    */   
/*    */   public List getRecords() {
/* 29 */     return this.records;
/*    */   }
/*    */   
/*    */   public void setRecords(List records) {
/* 33 */     this.records = records;
/*    */   }
/*    */   
/*    */   public Long getCount() {
/* 37 */     return this.count;
/*    */   }
/*    */   
/*    */   public void setCount(Long count) {
/* 41 */     this.count = count;
/*    */   }
/*    */   
/*    */   public Integer getCurr() {
/* 45 */     return this.curr;
/*    */   }
/*    */   
/*    */   public void setCurr(Integer curr) {
/* 49 */     this.curr = curr;
/*    */   }
/*    */   
/*    */   public Integer getLimit() {
/* 53 */     return this.limit;
/*    */   }
/*    */   
/*    */   public void setLimit(Integer limit) {
/* 57 */     this.limit = limit;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\bean\Page.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */