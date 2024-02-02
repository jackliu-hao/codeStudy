/*    */ package cn.hutool.core.lang.hash;
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
/*    */ public class Number128
/*    */   extends Number
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private long lowValue;
/*    */   private long highValue;
/*    */   
/*    */   public Number128(long lowValue, long highValue) {
/* 22 */     this.lowValue = lowValue;
/* 23 */     this.highValue = highValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getLowValue() {
/* 32 */     return this.lowValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setLowValue(long lowValue) {
/* 41 */     this.lowValue = lowValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long getHighValue() {
/* 50 */     return this.highValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setHighValue(long hiValue) {
/* 59 */     this.highValue = hiValue;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public long[] getLongArray() {
/* 68 */     return new long[] { this.lowValue, this.highValue };
/*    */   }
/*    */ 
/*    */   
/*    */   public int intValue() {
/* 73 */     return (int)longValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public long longValue() {
/* 78 */     return this.lowValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public float floatValue() {
/* 83 */     return (float)longValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public double doubleValue() {
/* 88 */     return longValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\hash\Number128.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */