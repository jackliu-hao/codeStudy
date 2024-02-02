package com.mysql.cj.xdevapi;

public interface SelectStatement extends Statement<SelectStatement, RowResult> {
   SelectStatement where(String var1);

   SelectStatement groupBy(String... var1);

   SelectStatement having(String var1);

   SelectStatement orderBy(String... var1);

   SelectStatement limit(long var1);

   SelectStatement offset(long var1);

   SelectStatement lockShared();

   SelectStatement lockShared(Statement.LockContention var1);

   SelectStatement lockExclusive();

   SelectStatement lockExclusive(Statement.LockContention var1);

   FilterParams getFilterParams();
}
