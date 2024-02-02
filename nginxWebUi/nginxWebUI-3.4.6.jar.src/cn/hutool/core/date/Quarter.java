/*    */ package cn.hutool.core.date;
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
/*    */ public enum Quarter
/*    */ {
/* 17 */   Q1(1),
/*    */   
/* 19 */   Q2(2),
/*    */   
/* 21 */   Q3(3),
/*    */   
/* 23 */   Q4(4);
/*    */   
/*    */   private final int value;
/*    */ 
/*    */   
/*    */   Quarter(int value) {
/* 29 */     this.value = value;
/*    */   }
/*    */   
/*    */   public int getValue() {
/* 33 */     return this.value;
/*    */   }
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
/*    */   public static Quarter of(int intValue) {
/* 48 */     switch (intValue) {
/*    */       case 1:
/* 50 */         return Q1;
/*    */       case 2:
/* 52 */         return Q2;
/*    */       case 3:
/* 54 */         return Q3;
/*    */       case 4:
/* 56 */         return Q4;
/*    */     } 
/* 58 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\Quarter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */