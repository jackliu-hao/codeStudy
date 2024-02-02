package org.h2.mvstore.db;

import java.util.List;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.VersionedValue;

public class MVDelegateIndex extends MVIndex<Long, SearchRow> {
   private final MVPrimaryIndex mainIndex;

   public MVDelegateIndex(MVTable var1, int var2, String var3, MVPrimaryIndex var4, IndexType var5) {
      super(var1, var2, var3, IndexColumn.wrap(new Column[]{var1.getColumn(var4.getMainIndexColumn())}), 1, var5);
      this.mainIndex = var4;
      if (var2 < 0) {
         throw DbException.getInternalError(var3);
      }
   }

   public RowFactory getRowFactory() {
      return this.mainIndex.getRowFactory();
   }

   public void addRowsToBuffer(List<Row> var1, String var2) {
      throw DbException.getInternalError();
   }

   public void addBufferedRows(List<String> var1) {
      throw DbException.getInternalError();
   }

   public MVMap<Long, VersionedValue<SearchRow>> getMVMap() {
      return this.mainIndex.getMVMap();
   }

   public void add(SessionLocal var1, Row var2) {
   }

   public Row getRow(SessionLocal var1, long var2) {
      return this.mainIndex.getRow(var1, var2);
   }

   public boolean isRowIdIndex() {
      return true;
   }

   public boolean canGetFirstOrLast() {
      return true;
   }

   public void close(SessionLocal var1) {
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      return this.mainIndex.find(var1, var2, var3);
   }

   public Cursor findFirstOrLast(SessionLocal var1, boolean var2) {
      return this.mainIndex.findFirstOrLast(var1, var2);
   }

   public int getColumnIndex(Column var1) {
      return var1.getColumnId() == this.mainIndex.getMainIndexColumn() ? 0 : -1;
   }

   public boolean isFirstColumn(Column var1) {
      return this.getColumnIndex(var1) == 0;
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return (double)(10L * this.getCostRangeIndex(var2, this.mainIndex.getRowCountApproximation(var1), var3, var4, var5, true, var6));
   }

   public boolean needRebuild() {
      return false;
   }

   public void remove(SessionLocal var1, Row var2) {
   }

   public void update(SessionLocal var1, Row var2, Row var3) {
   }

   public void remove(SessionLocal var1) {
      this.mainIndex.setMainIndexColumn(-1);
   }

   public void truncate(SessionLocal var1) {
   }

   public long getRowCount(SessionLocal var1) {
      return this.mainIndex.getRowCount(var1);
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return this.mainIndex.getRowCountApproximation(var1);
   }
}
