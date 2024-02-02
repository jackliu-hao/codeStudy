package org.h2.expression.analysis;

public enum WindowFrameBoundType {
   UNBOUNDED_PRECEDING("UNBOUNDED PRECEDING"),
   PRECEDING("PRECEDING"),
   CURRENT_ROW("CURRENT ROW"),
   FOLLOWING("FOLLOWING"),
   UNBOUNDED_FOLLOWING("UNBOUNDED FOLLOWING");

   private final String sql;

   private WindowFrameBoundType(String var3) {
      this.sql = var3;
   }

   public String getSQL() {
      return this.sql;
   }
}
