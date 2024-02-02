package org.h2.index;

import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.DualTable;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;

public class DualIndex extends VirtualTableIndex {
   public DualIndex(DualTable var1) {
      super(var1, "DUAL_INDEX", new IndexColumn[0]);
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      return new DualCursor();
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return 1.0;
   }

   public String getCreateSQL() {
      return null;
   }

   public boolean canGetFirstOrLast() {
      return true;
   }

   public Cursor findFirstOrLast(SessionLocal var1, boolean var2) {
      return new SingleRowCursor(Row.get(Value.EMPTY_VALUES, 1));
   }

   public String getPlanSQL() {
      return "dual index";
   }
}
