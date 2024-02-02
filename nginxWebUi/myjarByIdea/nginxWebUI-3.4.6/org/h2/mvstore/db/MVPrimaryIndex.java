package org.h2.mvstore.db;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.index.SingleRowCursor;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.type.LongDataType;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;
import org.h2.value.VersionedValue;

public class MVPrimaryIndex extends MVIndex<Long, SearchRow> {
   private final MVTable mvTable;
   private final String mapName;
   private final TransactionMap<Long, SearchRow> dataMap;
   private final AtomicLong lastKey = new AtomicLong();
   private int mainIndexColumn = -1;

   public MVPrimaryIndex(Database var1, MVTable var2, int var3, IndexColumn[] var4, IndexType var5) {
      super(var2, var3, var2.getName() + "_DATA", var4, 0, var5);
      this.mvTable = var2;
      RowDataType var6 = var2.getRowFactory().getRowDataType();
      this.mapName = "table." + this.getId();
      Transaction var7 = this.mvTable.getTransactionBegin();
      this.dataMap = var7.openMap(this.mapName, LongDataType.INSTANCE, var6);
      this.dataMap.map.setVolatile(!var2.isPersistData() || !var5.isPersistent());
      if (!var1.isStarting()) {
         this.dataMap.clear();
      }

      var7.commit();
      Long var8 = (Long)this.dataMap.map.lastKey();
      this.lastKey.set(var8 == null ? 0L : var8);
   }

   public String getCreateSQL() {
      return null;
   }

   public String getPlanSQL() {
      return this.table.getSQL(new StringBuilder(), 3).append(".tableScan").toString();
   }

   public void setMainIndexColumn(int var1) {
      this.mainIndexColumn = var1;
   }

   public int getMainIndexColumn() {
      return this.mainIndexColumn;
   }

   public void close(SessionLocal var1) {
   }

   public void add(SessionLocal var1, Row var2) {
      if (this.mainIndexColumn == -1) {
         if (var2.getKey() == 0L) {
            var2.setKey(this.lastKey.incrementAndGet());
         }
      } else {
         long var3 = var2.getValue(this.mainIndexColumn).getLong();
         var2.setKey(var3);
      }

      if (this.mvTable.getContainsLargeObject()) {
         int var10 = 0;

         for(int var4 = var2.getColumnCount(); var10 < var4; ++var10) {
            Value var5 = var2.getValue(var10);
            if (var5 instanceof ValueLob) {
               ValueLob var6 = ((ValueLob)var5).copy(this.database, this.getId());
               var1.removeAtCommitStop(var6);
               if (var5 != var6) {
                  var2.setValue(var10, var6);
               }
            }
         }
      }

      TransactionMap var11 = this.getMap(var1);
      long var12 = var2.getKey();

      try {
         Row var13 = (Row)var11.putIfAbsent(var12, var2);
         if (var13 != null) {
            int var7 = 90131;
            if (var11.getImmediate(var12) != null || var11.getFromSnapshot(var12) != null) {
               var7 = 23505;
            }

            DbException var8 = DbException.get(var7, this.getDuplicatePrimaryKeyMessage(this.mainIndexColumn).append(' ').append(var13).toString());
            var8.setSource(this);
            throw var8;
         }
      } catch (MVStoreException var9) {
         throw this.mvTable.convertException(var9);
      }

      long var14;
      while(var12 > (var14 = this.lastKey.get()) && !this.lastKey.compareAndSet(var14, var12)) {
      }

   }

   public void remove(SessionLocal var1, Row var2) {
      if (this.mvTable.getContainsLargeObject()) {
         int var3 = 0;

         for(int var4 = var2.getColumnCount(); var3 < var4; ++var3) {
            Value var5 = var2.getValue(var3);
            if (var5 instanceof ValueLob) {
               var1.removeAtCommit((ValueLob)var5);
            }
         }
      }

      TransactionMap var7 = this.getMap(var1);

      try {
         Row var8 = (Row)var7.remove(var2.getKey());
         if (var8 == null) {
            StringBuilder var9 = new StringBuilder();
            this.getSQL(var9, 3).append(": ").append(var2.getKey());
            throw DbException.get(90112, var9.toString());
         }
      } catch (MVStoreException var6) {
         throw this.mvTable.convertException(var6);
      }
   }

   public void update(SessionLocal var1, Row var2, Row var3) {
      long var4;
      if (this.mainIndexColumn != -1) {
         var4 = var3.getValue(this.mainIndexColumn).getLong();
         var3.setKey(var4);
      }

      var4 = var2.getKey();

      assert this.mainIndexColumn != -1 || var4 != 0L;

      assert var4 == var3.getKey() : var4 + " != " + var3.getKey();

      if (this.mvTable.getContainsLargeObject()) {
         int var6 = 0;

         for(int var7 = var2.getColumnCount(); var6 < var7; ++var6) {
            Value var8 = var2.getValue(var6);
            Value var9 = var3.getValue(var6);
            if (var8 != var9) {
               if (var8 instanceof ValueLob) {
                  var1.removeAtCommit((ValueLob)var8);
               }

               if (var9 instanceof ValueLob) {
                  ValueLob var10 = ((ValueLob)var9).copy(this.database, this.getId());
                  var1.removeAtCommitStop(var10);
                  if (var9 != var10) {
                     var3.setValue(var6, var10);
                  }
               }
            }
         }
      }

      TransactionMap var12 = this.getMap(var1);

      try {
         Row var13 = (Row)var12.put(var4, var3);
         if (var13 == null) {
            StringBuilder var14 = new StringBuilder();
            this.getSQL(var14, 3).append(": ").append(var4);
            throw DbException.get(90112, var14.toString());
         }
      } catch (MVStoreException var11) {
         throw this.mvTable.convertException(var11);
      }

      if (var3.getKey() > this.lastKey.get()) {
         this.lastKey.set(var3.getKey());
      }

   }

   Row lockRow(SessionLocal var1, Row var2) {
      TransactionMap var3 = this.getMap(var1);
      long var4 = var2.getKey();
      return this.lockRow(var3, var4);
   }

   private Row lockRow(TransactionMap<Long, SearchRow> var1, long var2) {
      try {
         return setRowKey((Row)var1.lock(var2), var2);
      } catch (MVStoreException var5) {
         throw this.mvTable.convertException(var5);
      }
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      long var4 = this.extractPKFromRow(var2, Long.MIN_VALUE);
      long var6 = this.extractPKFromRow(var3, Long.MAX_VALUE);
      return this.find(var1, var4, var6);
   }

   private long extractPKFromRow(SearchRow var1, long var2) {
      long var4;
      if (var1 == null) {
         var4 = var2;
      } else if (this.mainIndexColumn == -1) {
         var4 = var1.getKey();
      } else {
         Value var6 = var1.getValue(this.mainIndexColumn);
         if (var6 == null) {
            var4 = var1.getKey();
         } else if (var6 == ValueNull.INSTANCE) {
            var4 = 0L;
         } else {
            var4 = var6.getLong();
         }
      }

      return var4;
   }

   public MVTable getTable() {
      return this.mvTable;
   }

   public Row getRow(SessionLocal var1, long var2) {
      TransactionMap var4 = this.getMap(var1);
      Row var5 = (Row)var4.getFromSnapshot(var2);
      if (var5 == null) {
         throw DbException.get(90143, this.getTraceSQL(), String.valueOf(var2));
      } else {
         return setRowKey(var5, var2);
      }
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      try {
         return (double)(10L * this.getCostRangeIndex(var2, this.dataMap.sizeAsLongMax(), var3, var4, var5, true, var6));
      } catch (MVStoreException var8) {
         throw DbException.get(90007, var8);
      }
   }

   public int getColumnIndex(Column var1) {
      return -1;
   }

   public boolean isFirstColumn(Column var1) {
      return false;
   }

   public void remove(SessionLocal var1) {
      TransactionMap var2 = this.getMap(var1);
      if (!var2.isClosed()) {
         Transaction var3 = var1.getTransaction();
         var3.removeMap(var2);
      }

   }

   public void truncate(SessionLocal var1) {
      if (this.mvTable.getContainsLargeObject()) {
         this.database.getLobStorage().removeAllForTable(this.table.getId());
      }

      this.getMap(var1).clear();
   }

   public boolean canGetFirstOrLast() {
      return true;
   }

   public Cursor findFirstOrLast(SessionLocal var1, boolean var2) {
      TransactionMap var3 = this.getMap(var1);
      Map.Entry var4 = var2 ? var3.firstEntry() : var3.lastEntry();
      return new SingleRowCursor(var4 != null ? setRowKey((Row)var4.getValue(), (Long)var4.getKey()) : null);
   }

   public boolean needRebuild() {
      return false;
   }

   public long getRowCount(SessionLocal var1) {
      return this.getMap(var1).sizeAsLong();
   }

   public long getRowCountMax() {
      return this.dataMap.sizeAsLongMax();
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return this.getRowCountMax();
   }

   public long getDiskSpaceUsed() {
      return this.dataMap.map.getRootPage().getDiskSpaceUsed();
   }

   public String getMapName() {
      return this.mapName;
   }

   public void addRowsToBuffer(List<Row> var1, String var2) {
      throw new UnsupportedOperationException();
   }

   public void addBufferedRows(List<String> var1) {
      throw new UnsupportedOperationException();
   }

   private Cursor find(SessionLocal var1, Long var2, Long var3) {
      TransactionMap var4 = this.getMap(var1);
      return (Cursor)(var2 != null && var3 != null && var2 == var3 ? new SingleRowCursor(setRowKey((Row)var4.getFromSnapshot(var2), var2)) : new MVStoreCursor(var4.entryIterator(var2, var3)));
   }

   public boolean isRowIdIndex() {
      return true;
   }

   TransactionMap<Long, SearchRow> getMap(SessionLocal var1) {
      if (var1 == null) {
         return this.dataMap;
      } else {
         Transaction var2 = var1.getTransaction();
         return this.dataMap.getInstance(var2);
      }
   }

   public MVMap<Long, VersionedValue<SearchRow>> getMVMap() {
      return this.dataMap.map;
   }

   private static Row setRowKey(Row var0, long var1) {
      if (var0 != null && var0.getKey() == 0L) {
         var0.setKey(var1);
      }

      return var0;
   }

   static final class MVStoreCursor implements Cursor {
      private final TransactionMap.TMIterator<Long, SearchRow, Map.Entry<Long, SearchRow>> it;
      private Map.Entry<Long, SearchRow> current;
      private Row row;

      public MVStoreCursor(TransactionMap.TMIterator<Long, SearchRow, Map.Entry<Long, SearchRow>> var1) {
         this.it = var1;
      }

      public Row get() {
         if (this.row == null && this.current != null) {
            this.row = (Row)this.current.getValue();
            if (this.row.getKey() == 0L) {
               this.row.setKey((Long)this.current.getKey());
            }
         }

         return this.row;
      }

      public SearchRow getSearchRow() {
         return this.get();
      }

      public boolean next() {
         this.current = (Map.Entry)this.it.fetchNext();
         this.row = null;
         return this.current != null;
      }

      public boolean previous() {
         throw DbException.getUnsupportedException("previous");
      }
   }
}
