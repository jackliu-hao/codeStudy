/*    */ package com.mysql.cj.xdevapi;public interface FilterParams { Object getCollection();
/*    */   Object getOrder();
/*    */   void setOrder(String... paramVarArgs);
/*    */   Long getLimit();
/*    */   void setLimit(Long paramLong);
/*    */   
/*    */   Long getOffset();
/*    */   
/*    */   void setOffset(Long paramLong);
/*    */   
/*    */   boolean supportsOffset();
/*    */   
/*    */   Object getCriteria();
/*    */   
/*    */   void setCriteria(String paramString);
/*    */   
/*    */   Object getArgs();
/*    */   
/*    */   void addArg(String paramString, Object paramObject);
/*    */   
/*    */   void verifyAllArgsBound();
/*    */   
/*    */   void clearArgs();
/*    */   
/*    */   boolean isRelational();
/*    */   
/*    */   void setFields(String... paramVarArgs);
/*    */   
/*    */   Object getFields();
/*    */   
/*    */   void setGrouping(String... paramVarArgs);
/*    */   
/*    */   Object getGrouping();
/*    */   
/*    */   void setGroupingCriteria(String paramString);
/*    */   
/*    */   Object getGroupingCriteria();
/*    */   
/*    */   RowLock getLock();
/*    */   
/*    */   void setLock(RowLock paramRowLock);
/*    */   
/*    */   RowLockOptions getLockOption();
/*    */   
/*    */   void setLockOption(RowLockOptions paramRowLockOptions);
/*    */   
/* 47 */   public enum RowLock { SHARED_LOCK(1),
/*    */ 
/*    */ 
/*    */     
/* 51 */     EXCLUSIVE_LOCK(2);
/*    */     
/*    */     private int rowLock;
/*    */     
/*    */     RowLock(int rowLock) {
/* 56 */       this.rowLock = rowLock;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public int asNumber() {
/* 65 */       return this.rowLock;
/*    */     } }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public enum RowLockOptions
/*    */   {
/* 76 */     NOWAIT(1),
/*    */ 
/*    */ 
/*    */     
/* 80 */     SKIP_LOCKED(2);
/*    */     
/*    */     private int rowLockOption;
/*    */     
/*    */     RowLockOptions(int rowLockOption) {
/* 85 */       this.rowLockOption = rowLockOption;
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public int asNumber() {
/* 94 */       return this.rowLockOption;
/*    */     }
/*    */   } }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\FilterParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */