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
/*    */ public class CPInt
/*    */   extends CPConstant
/*    */ {
/*    */   private final int theInt;
/*    */   
/*    */   public CPInt(int theInt) {
/* 27 */     this.theInt = theInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object obj) {
/* 32 */     if (this.theInt > ((CPInt)obj).theInt) {
/* 33 */       return 1;
/*    */     }
/* 35 */     if (this.theInt == ((CPInt)obj).theInt) {
/* 36 */       return 0;
/*    */     }
/* 38 */     return -1;
/*    */   }
/*    */   
/*    */   public int getInt() {
/* 42 */     return this.theInt;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\CPInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */