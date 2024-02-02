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
/*    */ 
/*    */ public abstract class ConstantPoolEntry
/*    */ {
/* 24 */   private int index = -1;
/*    */   
/*    */   public int getIndex() {
/* 27 */     return this.index;
/*    */   }
/*    */   
/*    */   public void setIndex(int index) {
/* 31 */     this.index = index;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\ConstantPoolEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */