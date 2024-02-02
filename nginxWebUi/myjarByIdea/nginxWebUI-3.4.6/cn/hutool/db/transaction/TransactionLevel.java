package cn.hutool.db.transaction;

public enum TransactionLevel {
   NONE(0),
   READ_UNCOMMITTED(1),
   READ_COMMITTED(2),
   REPEATABLE_READ(4),
   SERIALIZABLE(8);

   private final int level;

   private TransactionLevel(int level) {
      this.level = level;
   }

   public int getLevel() {
      return this.level;
   }
}
