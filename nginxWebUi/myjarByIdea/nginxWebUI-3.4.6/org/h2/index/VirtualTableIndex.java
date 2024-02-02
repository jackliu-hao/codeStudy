package org.h2.index;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.table.IndexColumn;
import org.h2.table.VirtualTable;

public abstract class VirtualTableIndex extends Index {
   protected VirtualTableIndex(VirtualTable var1, String var2, IndexColumn[] var3) {
      super(var1, 0, var2, var3, 0, IndexType.createNonUnique(true));
   }

   public void close(SessionLocal var1) {
   }

   public void add(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public void remove(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public void remove(SessionLocal var1) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public void truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public boolean needRebuild() {
      return false;
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("Virtual table");
   }

   public long getRowCount(SessionLocal var1) {
      return this.table.getRowCount(var1);
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return this.table.getRowCountApproximation(var1);
   }
}
