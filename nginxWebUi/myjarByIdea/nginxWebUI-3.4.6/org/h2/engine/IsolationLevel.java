package org.h2.engine;

import org.h2.message.DbException;

public enum IsolationLevel {
   READ_UNCOMMITTED(1, 0),
   READ_COMMITTED(2, 3),
   REPEATABLE_READ(4, 1),
   SNAPSHOT(6, 1),
   SERIALIZABLE(8, 1);

   private final String sql = this.name().replace('_', ' ').intern();
   private final int jdbc;
   private final int lockMode;

   public static IsolationLevel fromJdbc(int var0) {
      switch (var0) {
         case 1:
            return READ_UNCOMMITTED;
         case 2:
            return READ_COMMITTED;
         case 3:
         case 5:
         case 7:
         default:
            throw DbException.getInvalidValueException("isolation level", var0);
         case 4:
            return REPEATABLE_READ;
         case 6:
            return SNAPSHOT;
         case 8:
            return SERIALIZABLE;
      }
   }

   public static IsolationLevel fromLockMode(int var0) {
      switch (var0) {
         case 0:
            return READ_UNCOMMITTED;
         case 1:
         case 2:
            return SERIALIZABLE;
         case 3:
         default:
            return READ_COMMITTED;
      }
   }

   public static IsolationLevel fromSql(String var0) {
      switch (var0) {
         case "READ UNCOMMITTED":
            return READ_UNCOMMITTED;
         case "READ COMMITTED":
            return READ_COMMITTED;
         case "REPEATABLE READ":
            return REPEATABLE_READ;
         case "SNAPSHOT":
            return SNAPSHOT;
         case "SERIALIZABLE":
            return SERIALIZABLE;
         default:
            throw DbException.getInvalidValueException("isolation level", var0);
      }
   }

   private IsolationLevel(int var3, int var4) {
      this.jdbc = var3;
      this.lockMode = var4;
   }

   public String getSQL() {
      return this.sql;
   }

   public int getJdbc() {
      return this.jdbc;
   }

   public int getLockMode() {
      return this.lockMode;
   }

   public boolean allowNonRepeatableRead() {
      return this.ordinal() < REPEATABLE_READ.ordinal();
   }
}
