package org.h2.index;

import java.util.ArrayList;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.MetaTable;
import org.h2.table.TableFilter;

public class MetaIndex extends Index {
   private final MetaTable meta;
   private final boolean scan;

   public MetaIndex(MetaTable var1, IndexColumn[] var2, boolean var3) {
      super(var1, 0, (String)null, var2, 0, IndexType.createNonUnique(true));
      this.meta = var1;
      this.scan = var3;
   }

   public void close(SessionLocal var1) {
   }

   public void add(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("META");
   }

   public void remove(SessionLocal var1, Row var2) {
      throw DbException.getUnsupportedException("META");
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      ArrayList var4 = this.meta.generateRows(var1, var2, var3);
      return new MetaCursor(var4);
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return this.scan ? 10000.0 : (double)this.getCostRangeIndex(var2, 1000L, var3, var4, var5, false, var6);
   }

   public void truncate(SessionLocal var1) {
      throw DbException.getUnsupportedException("META");
   }

   public void remove(SessionLocal var1) {
      throw DbException.getUnsupportedException("META");
   }

   public int getColumnIndex(Column var1) {
      return this.scan ? -1 : super.getColumnIndex(var1);
   }

   public boolean isFirstColumn(Column var1) {
      return this.scan ? false : super.isFirstColumn(var1);
   }

   public void checkRename() {
      throw DbException.getUnsupportedException("META");
   }

   public boolean needRebuild() {
      return false;
   }

   public String getCreateSQL() {
      return null;
   }

   public long getRowCount(SessionLocal var1) {
      return 1000L;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 1000L;
   }

   public long getDiskSpaceUsed() {
      return this.meta.getDiskSpaceUsed();
   }

   public String getPlanSQL() {
      return "meta";
   }
}
