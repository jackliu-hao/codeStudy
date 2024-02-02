/*    */ package cn.hutool.core.img;
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
/*    */ public enum ScaleType
/*    */ {
/* 14 */   DEFAULT(1),
/*    */   
/* 16 */   FAST(2),
/*    */   
/* 18 */   SMOOTH(4),
/*    */   
/* 20 */   REPLICATE(8),
/*    */   
/* 22 */   AREA_AVERAGING(16);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final int value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   ScaleType(int value) {
/* 35 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getValue() {
/* 41 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\ScaleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */