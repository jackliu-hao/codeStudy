package cn.hutool.db.meta;

public enum TableType {
   TABLE("TABLE"),
   VIEW("VIEW"),
   SYSTEM_TABLE("SYSTEM TABLE"),
   GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
   LOCAL_TEMPORARY("LOCAL TEMPORARY"),
   ALIAS("ALIAS"),
   SYNONYM("SYNONYM");

   private final String value;

   private TableType(String value) {
      this.value = value;
   }

   public String value() {
      return this.value;
   }

   public String toString() {
      return this.value();
   }
}
