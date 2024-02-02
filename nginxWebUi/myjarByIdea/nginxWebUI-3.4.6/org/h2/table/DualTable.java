package org.h2.table;

import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.DualIndex;
import org.h2.index.Index;

public class DualTable extends VirtualTable {
   public static final String NAME = "DUAL";

   public DualTable(Database var1) {
      super(var1.getMainSchema(), 0, "DUAL");
      this.setColumns(new Column[0]);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append("DUAL");
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return true;
   }

   public long getRowCount(SessionLocal var1) {
      return 1L;
   }

   public TableType getTableType() {
      return TableType.SYSTEM_TABLE;
   }

   public Index getScanIndex(SessionLocal var1) {
      return new DualIndex(this);
   }

   public long getMaxDataModificationId() {
      return 0L;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 1L;
   }

   public boolean isDeterministic() {
      return true;
   }
}
