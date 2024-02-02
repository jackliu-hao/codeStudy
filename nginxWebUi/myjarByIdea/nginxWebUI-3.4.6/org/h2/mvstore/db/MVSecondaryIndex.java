package org.h2.mvstore.db;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.index.SingleRowCursor;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.type.DataType;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.VersionedValue;

public final class MVSecondaryIndex extends MVIndex<SearchRow, Value> {
   private final MVTable mvTable;
   private final TransactionMap<SearchRow, Value> dataMap;

   public MVSecondaryIndex(Database var1, MVTable var2, int var3, String var4, IndexColumn[] var5, int var6, IndexType var7) {
      super(var2, var3, var4, var5, var6, var7);
      this.mvTable = var2;
      if (!this.database.isStarting()) {
         checkIndexColumnTypes(var5);
      }

      String var8 = "index." + this.getId();
      RowDataType var9 = this.getRowFactory().getRowDataType();
      Transaction var10 = this.mvTable.getTransactionBegin();
      this.dataMap = var10.openMap(var8, var9, NullValueDataType.INSTANCE);
      this.dataMap.map.setVolatile(!var2.isPersistData() || !var7.isPersistent());
      if (!var1.isStarting()) {
         this.dataMap.clear();
      }

      var10.commit();
      if (!var9.equals(this.dataMap.getKeyType())) {
         throw DbException.getInternalError("Incompatible key type, expected " + var9 + " but got " + this.dataMap.getKeyType() + " for index " + var4);
      }
   }

   public void addRowsToBuffer(List<Row> var1, String var2) {
      MVMap var3 = this.openMap(var2);
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         Row var5 = (Row)var4.next();
         SearchRow var6 = this.getRowFactory().createRow();
         var6.copyFrom(var5);
         var3.append(var6, ValueNull.INSTANCE);
      }

   }

   public void addBufferedRows(List<String> var1) {
      int var2 = var1.size();
      PriorityQueue var3 = new PriorityQueue(var2, new Source.Comparator(this.getRowFactory().getRowDataType()));
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         Iterator var6 = this.openMap(var5).keyIterator((Object)null);
         if (var6.hasNext()) {
            var3.offer(new Source(var6));
         }
      }

      while(true) {
         boolean var12 = false;

         try {
            var12 = true;
            if (var3.isEmpty()) {
               var12 = false;
               break;
            }

            Source var14 = (Source)var3.poll();
            SearchRow var16 = var14.next();
            if (this.uniqueColumnColumn > 0 && !this.mayHaveNullDuplicates(var16)) {
               this.checkUnique(false, this.dataMap, var16, Long.MIN_VALUE);
            }

            this.dataMap.putCommitted(var16, ValueNull.INSTANCE);
            if (var14.hasNext()) {
               var3.offer(var14);
            }
         } finally {
            if (var12) {
               MVStore var8 = this.database.getStore().getMvStore();
               Iterator var9 = var1.iterator();

               while(var9.hasNext()) {
                  String var10 = (String)var9.next();
                  var8.removeMap(var10);
               }

            }
         }
      }

      MVStore var15 = this.database.getStore().getMvStore();
      Iterator var17 = var1.iterator();

      while(var17.hasNext()) {
         String var18 = (String)var17.next();
         var15.removeMap(var18);
      }

   }

   private MVMap<SearchRow, Value> openMap(String var1) {
      RowDataType var2 = this.getRowFactory().getRowDataType();
      MVMap.Builder var3 = (new MVMap.Builder()).singleWriter().keyType(var2).valueType(NullValueDataType.INSTANCE);
      MVMap var4 = this.database.getStore().getMvStore().openMap(var1, var3);
      if (!var2.equals(var4.getKeyType())) {
         throw DbException.getInternalError("Incompatible key type, expected " + var2 + " but got " + var4.getKeyType() + " for map " + var1);
      } else {
         return var4;
      }
   }

   public void close(SessionLocal var1) {
   }

   public void add(SessionLocal var1, Row var2) {
      TransactionMap var3 = this.getMap(var1);
      SearchRow var4 = this.convertToKey(var2, (Boolean)null);
      boolean var5 = this.uniqueColumnColumn > 0 && !this.mayHaveNullDuplicates(var2);
      if (var5) {
         boolean var6 = !var1.getTransaction().allowNonRepeatableRead();
         this.checkUnique(var6, var3, var2, Long.MIN_VALUE);
      }

      try {
         var3.put(var4, ValueNull.INSTANCE);
      } catch (MVStoreException var7) {
         throw this.mvTable.convertException(var7);
      }

      if (var5) {
         this.checkUnique(false, var3, var2, var2.getKey());
      }

   }

   private void checkUnique(boolean var1, TransactionMap<SearchRow, Value> var2, SearchRow var3, long var4) {
      RowFactory var6 = this.getUniqueRowFactory();
      SearchRow var7 = var6.createRow();
      var7.copyFrom(var3);
      var7.setKey(Long.MIN_VALUE);
      SearchRow var8 = var6.createRow();
      var8.copyFrom(var3);
      var8.setKey(Long.MAX_VALUE);
      TransactionMap.TMIterator var9;
      SearchRow var10;
      if (var1) {
         var9 = var2.keyIterator(var7, var8);

         while((var10 = (SearchRow)var9.fetchNext()) != null) {
            if (var4 != var10.getKey() && !var2.isDeletedByCurrentTransaction(var10)) {
               throw this.getDuplicateKeyException(var10.toString());
            }
         }
      }

      var9 = var2.keyIteratorUncommitted(var7, var8);

      do {
         if ((var10 = (SearchRow)var9.fetchNext()) == null) {
            return;
         }
      } while(var4 == var10.getKey());

      if (var2.getImmediate(var10) != null) {
         throw this.getDuplicateKeyException(var10.toString());
      } else {
         throw DbException.get(90131, this.table.getName());
      }
   }

   public void remove(SessionLocal var1, Row var2) {
      SearchRow var3 = this.convertToKey(var2, (Boolean)null);
      TransactionMap var4 = this.getMap(var1);

      try {
         if (var4.remove(var3) == null) {
            StringBuilder var5 = new StringBuilder();
            this.getSQL(var5, 3).append(": ").append(var2.getKey());
            throw DbException.get(90112, var5.toString());
         }
      } catch (MVStoreException var6) {
         throw this.mvTable.convertException(var6);
      }
   }

   public void update(SessionLocal var1, Row var2, Row var3) {
      SearchRow var4 = this.convertToKey(var2, (Boolean)null);
      SearchRow var5 = this.convertToKey(var3, (Boolean)null);
      if (!this.rowsAreEqual(var4, var5)) {
         super.update(var1, var2, var3);
      }

   }

   private boolean rowsAreEqual(SearchRow var1, SearchRow var2) {
      if (var1 == var2) {
         return true;
      } else {
         int[] var3 = this.columnIds;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var3[var5];
            Value var7 = var1.getValue(var6);
            Value var8 = var2.getValue(var6);
            if (!Objects.equals(var7, var8)) {
               return false;
            }
         }

         return var1.getKey() == var2.getKey();
      }
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      return this.find(var1, var2, false, var3);
   }

   private Cursor find(SessionLocal var1, SearchRow var2, boolean var3, SearchRow var4) {
      SearchRow var5 = this.convertToKey(var2, var3);
      SearchRow var6 = this.convertToKey(var4, Boolean.TRUE);
      return new MVStoreCursor(var1, this.getMap(var1).keyIterator(var5, var6), this.mvTable);
   }

   private SearchRow convertToKey(SearchRow var1, Boolean var2) {
      if (var1 == null) {
         return null;
      } else {
         SearchRow var3 = this.getRowFactory().createRow();
         var3.copyFrom(var1);
         if (var2 != null) {
            var3.setKey(var2 ? Long.MAX_VALUE : Long.MIN_VALUE);
         }

         return var3;
      }
   }

   public MVTable getTable() {
      return this.mvTable;
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      try {
         return (double)(10L * this.getCostRangeIndex(var2, this.dataMap.sizeAsLongMax(), var3, var4, var5, false, var6));
      } catch (MVStoreException var8) {
         throw DbException.get(90007, var8);
      }
   }

   public void remove(SessionLocal var1) {
      TransactionMap var2 = this.getMap(var1);
      if (!var2.isClosed()) {
         Transaction var3 = var1.getTransaction();
         var3.removeMap(var2);
      }

   }

   public void truncate(SessionLocal var1) {
      TransactionMap var2 = this.getMap(var1);
      var2.clear();
   }

   public boolean canGetFirstOrLast() {
      return true;
   }

   public Cursor findFirstOrLast(SessionLocal var1, boolean var2) {
      TransactionMap.TMIterator var3 = this.getMap(var1).keyIterator((Object)null, !var2);

      SearchRow var4;
      do {
         if ((var4 = (SearchRow)var3.fetchNext()) == null) {
            return new SingleRowCursor((Row)null);
         }
      } while(var4.getValue(this.columnIds[0]) == ValueNull.INSTANCE);

      return new SingleRowCursor(this.mvTable.getRow(var1, var4.getKey()));
   }

   public boolean needRebuild() {
      try {
         return this.dataMap.sizeAsLongMax() == 0L;
      } catch (MVStoreException var2) {
         throw DbException.get(90007, var2);
      }
   }

   public long getRowCount(SessionLocal var1) {
      TransactionMap var2 = this.getMap(var1);
      return var2.sizeAsLong();
   }

   public long getRowCountApproximation(SessionLocal var1) {
      try {
         return this.dataMap.sizeAsLongMax();
      } catch (MVStoreException var3) {
         throw DbException.get(90007, var3);
      }
   }

   public long getDiskSpaceUsed() {
      return 0L;
   }

   public boolean canFindNext() {
      return true;
   }

   public Cursor findNext(SessionLocal var1, SearchRow var2, SearchRow var3) {
      return this.find(var1, var2, true, var3);
   }

   private TransactionMap<SearchRow, Value> getMap(SessionLocal var1) {
      if (var1 == null) {
         return this.dataMap;
      } else {
         Transaction var2 = var1.getTransaction();
         return this.dataMap.getInstance(var2);
      }
   }

   public MVMap<SearchRow, VersionedValue<Value>> getMVMap() {
      return this.dataMap.map;
   }

   static final class MVStoreCursor implements Cursor {
      private final SessionLocal session;
      private final TransactionMap.TMIterator<SearchRow, Value, SearchRow> it;
      private final MVTable mvTable;
      private SearchRow current;
      private Row row;

      MVStoreCursor(SessionLocal var1, TransactionMap.TMIterator<SearchRow, Value, SearchRow> var2, MVTable var3) {
         this.session = var1;
         this.it = var2;
         this.mvTable = var3;
      }

      public Row get() {
         if (this.row == null) {
            SearchRow var1 = this.getSearchRow();
            if (var1 != null) {
               this.row = this.mvTable.getRow(this.session, var1.getKey());
            }
         }

         return this.row;
      }

      public SearchRow getSearchRow() {
         return this.current;
      }

      public boolean next() {
         this.current = (SearchRow)this.it.fetchNext();
         this.row = null;
         return this.current != null;
      }

      public boolean previous() {
         throw DbException.getUnsupportedException("previous");
      }
   }

   private static final class Source {
      private final Iterator<SearchRow> iterator;
      SearchRow currentRowData;

      public Source(Iterator<SearchRow> var1) {
         assert var1.hasNext();

         this.iterator = var1;
         this.currentRowData = (SearchRow)var1.next();
      }

      public boolean hasNext() {
         boolean var1 = this.iterator.hasNext();
         if (var1) {
            this.currentRowData = (SearchRow)this.iterator.next();
         }

         return var1;
      }

      public SearchRow next() {
         return this.currentRowData;
      }

      static final class Comparator implements java.util.Comparator<Source> {
         private final DataType<SearchRow> type;

         public Comparator(DataType<SearchRow> var1) {
            this.type = var1;
         }

         public int compare(Source var1, Source var2) {
            return this.type.compare(var1.currentRowData, var2.currentRowData);
         }
      }
   }
}
