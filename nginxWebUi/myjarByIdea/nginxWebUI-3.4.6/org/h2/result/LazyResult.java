package org.h2.result;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public abstract class LazyResult extends FetchedResult {
   private final SessionLocal session;
   private final Expression[] expressions;
   private boolean closed;
   private long limit;

   public LazyResult(SessionLocal var1, Expression[] var2) {
      this.session = var1;
      this.expressions = var2;
   }

   public void setLimit(long var1) {
      this.limit = var1;
   }

   public boolean isLazy() {
      return true;
   }

   public void reset() {
      if (this.closed) {
         throw DbException.getInternalError();
      } else {
         this.rowId = -1L;
         this.afterLast = false;
         this.currentRow = null;
         this.nextRow = null;
      }
   }

   public boolean skip() {
      if (!this.closed && !this.afterLast) {
         this.currentRow = null;
         if (this.nextRow != null) {
            this.nextRow = null;
            return true;
         } else if (this.skipNextRow()) {
            return true;
         } else {
            this.afterLast = true;
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean hasNext() {
      if (!this.closed && !this.afterLast) {
         if (this.nextRow == null && (this.limit <= 0L || this.rowId + 1L < this.limit)) {
            this.nextRow = this.fetchNextRow();
         }

         return this.nextRow != null;
      } else {
         return false;
      }
   }

   protected abstract Value[] fetchNextRow();

   protected boolean skipNextRow() {
      return this.fetchNextRow() != null;
   }

   public long getRowCount() {
      throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      this.closed = true;
   }

   public String getAlias(int var1) {
      return this.expressions[var1].getAlias(this.session, var1);
   }

   public String getSchemaName(int var1) {
      return this.expressions[var1].getSchemaName();
   }

   public String getTableName(int var1) {
      return this.expressions[var1].getTableName();
   }

   public String getColumnName(int var1) {
      return this.expressions[var1].getColumnName(this.session, var1);
   }

   public TypeInfo getColumnType(int var1) {
      return this.expressions[var1].getType();
   }

   public boolean isIdentity(int var1) {
      return this.expressions[var1].isIdentity();
   }

   public int getNullable(int var1) {
      return this.expressions[var1].getNullable();
   }

   public void setFetchSize(int var1) {
   }

   public int getFetchSize() {
      return 1;
   }
}
