package org.h2.util;

public interface HasSQL {
   int QUOTE_ONLY_WHEN_REQUIRED = 1;
   int REPLACE_LOBS_FOR_TRACE = 2;
   int NO_CASTS = 4;
   int ADD_PLAN_INFORMATION = 8;
   int DEFAULT_SQL_FLAGS = 0;
   int TRACE_SQL_FLAGS = 3;

   default String getTraceSQL() {
      return this.getSQL(3);
   }

   default String getSQL(int var1) {
      return this.getSQL(new StringBuilder(), var1).toString();
   }

   StringBuilder getSQL(StringBuilder var1, int var2);
}
