/*    */ package org.h2.util;
/*    */ 
/*    */ import org.h2.message.DbException;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CacheObject
/*    */   implements Comparable<CacheObject>
/*    */ {
/*    */   public CacheObject cachePrevious;
/*    */   public CacheObject cacheNext;
/*    */   public CacheObject cacheChained;
/*    */   private int pos;
/*    */   private boolean changed;
/*    */   
/*    */   public void setPos(int paramInt) {
/* 51 */     if (this.cachePrevious != null || this.cacheNext != null || this.cacheChained != null) {
/* 52 */       throw DbException.getInternalError("setPos too late");
/*    */     }
/* 54 */     this.pos = paramInt;
/*    */   }
/*    */   
/*    */   public int getPos() {
/* 58 */     return this.pos;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isChanged() {
/* 68 */     return this.changed;
/*    */   }
/*    */   
/*    */   public void setChanged(boolean paramBoolean) {
/* 72 */     this.changed = paramBoolean;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(CacheObject paramCacheObject) {
/* 77 */     return Integer.compare(getPos(), paramCacheObject.getPos());
/*    */   }
/*    */   
/*    */   public boolean isStream() {
/* 81 */     return false;
/*    */   }
/*    */   
/*    */   public abstract boolean canRemove();
/*    */   
/*    */   public abstract int getMemory();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\CacheObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */