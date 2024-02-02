/*    */ package org.h2.util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CacheHead
/*    */   extends CacheObject
/*    */ {
/*    */   public boolean canRemove() {
/* 15 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMemory() {
/* 20 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\CacheHead.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */