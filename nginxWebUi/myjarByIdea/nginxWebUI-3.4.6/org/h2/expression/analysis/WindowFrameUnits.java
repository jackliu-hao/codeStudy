package org.h2.expression.analysis;

public enum WindowFrameUnits {
   ROWS,
   RANGE,
   GROUPS;

   public String getSQL() {
      return this.name();
   }
}
