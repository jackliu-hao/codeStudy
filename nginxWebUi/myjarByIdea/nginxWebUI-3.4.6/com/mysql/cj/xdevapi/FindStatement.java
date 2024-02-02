package com.mysql.cj.xdevapi;

public interface FindStatement extends Statement<FindStatement, DocResult> {
   FindStatement fields(String... var1);

   FindStatement fields(Expression var1);

   FindStatement groupBy(String... var1);

   FindStatement having(String var1);

   FindStatement orderBy(String... var1);

   FindStatement sort(String... var1);

   /** @deprecated */
   @Deprecated
   default FindStatement skip(long limitOffset) {
      return this.offset(limitOffset);
   }

   FindStatement offset(long var1);

   FindStatement limit(long var1);

   FindStatement lockShared();

   FindStatement lockShared(Statement.LockContention var1);

   FindStatement lockExclusive();

   FindStatement lockExclusive(Statement.LockContention var1);
}
