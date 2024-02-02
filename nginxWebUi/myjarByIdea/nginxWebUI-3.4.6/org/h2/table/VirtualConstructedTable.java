package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.index.Index;
import org.h2.index.VirtualConstructedTableIndex;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;

public abstract class VirtualConstructedTable extends VirtualTable {
   protected VirtualConstructedTable(Schema var1, int var2, String var3) {
      super(var1, var2, var3);
   }

   public abstract ResultInterface getResult(SessionLocal var1);

   public Index getScanIndex(SessionLocal var1) {
      return new VirtualConstructedTableIndex(this, IndexColumn.wrap(this.columns));
   }

   public long getMaxDataModificationId() {
      return Long.MAX_VALUE;
   }
}
