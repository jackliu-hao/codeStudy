package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.table.DataChangeDeltaTable;
import org.h2.table.Table;

public abstract class DataChangeStatement extends Prepared {
   protected DataChangeStatement(SessionLocal var1) {
      super(var1);
   }

   public abstract String getStatementName();

   public abstract Table getTable();

   public final boolean isTransactional() {
      return true;
   }

   public final ResultInterface queryMeta() {
      return null;
   }

   public boolean isCacheable() {
      return true;
   }

   public final long update() {
      return this.update((ResultTarget)null, (DataChangeDeltaTable.ResultOption)null);
   }

   public abstract long update(ResultTarget var1, DataChangeDeltaTable.ResultOption var2);
}
