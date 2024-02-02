package com.mysql.cj.xdevapi;

public interface FilterParams {
   Object getCollection();

   Object getOrder();

   void setOrder(String... var1);

   Long getLimit();

   void setLimit(Long var1);

   Long getOffset();

   void setOffset(Long var1);

   boolean supportsOffset();

   Object getCriteria();

   void setCriteria(String var1);

   Object getArgs();

   void addArg(String var1, Object var2);

   void verifyAllArgsBound();

   void clearArgs();

   boolean isRelational();

   void setFields(String... var1);

   Object getFields();

   void setGrouping(String... var1);

   Object getGrouping();

   void setGroupingCriteria(String var1);

   Object getGroupingCriteria();

   RowLock getLock();

   void setLock(RowLock var1);

   RowLockOptions getLockOption();

   void setLockOption(RowLockOptions var1);

   public static enum RowLockOptions {
      NOWAIT(1),
      SKIP_LOCKED(2);

      private int rowLockOption;

      private RowLockOptions(int rowLockOption) {
         this.rowLockOption = rowLockOption;
      }

      public int asNumber() {
         return this.rowLockOption;
      }
   }

   public static enum RowLock {
      SHARED_LOCK(1),
      EXCLUSIVE_LOCK(2);

      private int rowLock;

      private RowLock(int rowLock) {
         this.rowLock = rowLock;
      }

      public int asNumber() {
         return this.rowLock;
      }
   }
}
