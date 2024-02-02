/*    */ package com.mysql.cj.protocol;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class InternalDate
/*    */ {
/* 34 */   protected int year = 0;
/* 35 */   protected int month = 0;
/* 36 */   protected int day = 0;
/*    */ 
/*    */ 
/*    */   
/*    */   public InternalDate() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public InternalDate(int year, int month, int day) {
/* 45 */     this.year = year;
/* 46 */     this.month = month;
/* 47 */     this.day = day;
/*    */   }
/*    */   
/*    */   public int getYear() {
/* 51 */     return this.year;
/*    */   }
/*    */   
/*    */   public void setYear(int year) {
/* 55 */     this.year = year;
/*    */   }
/*    */   
/*    */   public int getMonth() {
/* 59 */     return this.month;
/*    */   }
/*    */   
/*    */   public void setMonth(int month) {
/* 63 */     this.month = month;
/*    */   }
/*    */   
/*    */   public int getDay() {
/* 67 */     return this.day;
/*    */   }
/*    */   
/*    */   public void setDay(int day) {
/* 71 */     this.day = day;
/*    */   }
/*    */   
/*    */   public boolean isZero() {
/* 75 */     return (this.year == 0 && this.month == 0 && this.day == 0);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\InternalDate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */