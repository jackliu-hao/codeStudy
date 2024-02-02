package org.h2.expression.analysis;

public enum WindowFunctionType {
   ROW_NUMBER,
   RANK,
   DENSE_RANK,
   PERCENT_RANK,
   CUME_DIST,
   NTILE,
   LEAD,
   LAG,
   FIRST_VALUE,
   LAST_VALUE,
   NTH_VALUE,
   RATIO_TO_REPORT;

   public static WindowFunctionType get(String var0) {
      switch (var0) {
         case "ROW_NUMBER":
            return ROW_NUMBER;
         case "RANK":
            return RANK;
         case "DENSE_RANK":
            return DENSE_RANK;
         case "PERCENT_RANK":
            return PERCENT_RANK;
         case "CUME_DIST":
            return CUME_DIST;
         case "NTILE":
            return NTILE;
         case "LEAD":
            return LEAD;
         case "LAG":
            return LAG;
         case "FIRST_VALUE":
            return FIRST_VALUE;
         case "LAST_VALUE":
            return LAST_VALUE;
         case "NTH_VALUE":
            return NTH_VALUE;
         case "RATIO_TO_REPORT":
            return RATIO_TO_REPORT;
         default:
            return null;
      }
   }

   public String getSQL() {
      return this.name();
   }

   public boolean requiresWindowOrdering() {
      switch (this) {
         case RANK:
         case DENSE_RANK:
         case NTILE:
         case LEAD:
         case LAG:
            return true;
         default:
            return false;
      }
   }
}
