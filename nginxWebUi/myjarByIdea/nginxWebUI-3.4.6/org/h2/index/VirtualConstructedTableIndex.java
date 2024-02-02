package org.h2.index;

import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.FunctionTable;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.table.VirtualConstructedTable;

public class VirtualConstructedTableIndex extends VirtualTableIndex {
   private final VirtualConstructedTable table;

   public VirtualConstructedTableIndex(VirtualConstructedTable var1, IndexColumn[] var2) {
      super(var1, (String)null, var2);
      this.table = var1;
   }

   public boolean isFindUsingFullTableScan() {
      return true;
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      return new VirtualTableCursor(this, var2, var3, this.table.getResult(var1));
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      if (var2 != null) {
         throw DbException.getUnsupportedException("Virtual table");
      } else {
         long var7;
         if (this.table.canGetRowCount(var1)) {
            var7 = this.table.getRowCountApproximation(var1);
         } else {
            var7 = (long)this.database.getSettings().estimatedFunctionTableRows;
         }

         return (double)(var7 * 10L);
      }
   }

   public String getPlanSQL() {
      return this.table instanceof FunctionTable ? "function" : "table scan";
   }

   public boolean canScan() {
      return false;
   }
}
