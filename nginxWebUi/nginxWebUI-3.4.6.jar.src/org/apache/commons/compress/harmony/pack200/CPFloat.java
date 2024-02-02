/*    */ package org.apache.commons.compress.harmony.pack200;
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
/*    */ public class CPFloat
/*    */   extends CPConstant
/*    */ {
/*    */   private final float theFloat;
/*    */   
/*    */   public CPFloat(float theFloat) {
/* 27 */     this.theFloat = theFloat;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object obj) {
/* 32 */     return Float.compare(this.theFloat, ((CPFloat)obj).theFloat);
/*    */   }
/*    */   
/*    */   public float getFloat() {
/* 36 */     return this.theFloat;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPFloat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */