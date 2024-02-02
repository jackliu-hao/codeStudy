package org.h2.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;
import org.h2.engine.Database;
import org.h2.engine.Session;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVTempResult;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.Utils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueLob;
import org.h2.value.ValueRow;

public class LocalResult implements ResultInterface, ResultTarget {
   private int maxMemoryRows;
   private final SessionLocal session;
   private int visibleColumnCount;
   private int resultColumnCount;
   private Expression[] expressions;
   private boolean forDataChangeDeltaTable;
   private long rowId;
   private long rowCount;
   private ArrayList<Value[]> rows;
   private SortOrder sort;
   private TreeMap<ValueRow, Value[]> distinctRows;
   private Value[] currentRow;
   private long offset;
   private long limit;
   private boolean fetchPercent;
   private SortOrder withTiesSortOrder;
   private boolean limitsWereApplied;
   private ResultExternal external;
   private boolean distinct;
   private int[] distinctIndexes;
   private boolean closed;
   private boolean containsLobs;
   private Boolean containsNull;

   public static LocalResult forTable(SessionLocal var0, Table var1) {
      Column[] var2 = var1.getColumns();
      int var3 = var2.length;
      Expression[] var4 = new Expression[var3 + 1];
      Database var5 = var0.getDatabase();

      for(int var6 = 0; var6 < var3; ++var6) {
         var4[var6] = new ExpressionColumn(var5, var2[var6]);
      }

      Column var7 = var1.getRowIdColumn();
      var4[var3] = var7 != null ? new ExpressionColumn(var5, var7) : new ExpressionColumn(var5, (String)null, var1.getName());
      return new LocalResult(var0, var4, var3, var3 + 1);
   }

   public LocalResult() {
      this((SessionLocal)null);
   }

   private LocalResult(SessionLocal var1) {
      this.limit = -1L;
      this.session = var1;
   }

   public LocalResult(SessionLocal var1, Expression[] var2, int var3, int var4) {
      this.limit = -1L;
      this.session = var1;
      if (var1 == null) {
         this.maxMemoryRows = Integer.MAX_VALUE;
      } else {
         Database var5 = var1.getDatabase();
         if (var5.isPersistent() && !var5.isReadOnly()) {
            this.maxMemoryRows = var1.getDatabase().getMaxMemoryRows();
         } else {
            this.maxMemoryRows = Integer.MAX_VALUE;
         }
      }

      this.rows = Utils.newSmallArrayList();
      this.visibleColumnCount = var3;
      this.resultColumnCount = var4;
      this.rowId = -1L;
      this.expressions = var2;
   }

   public boolean isLazy() {
      return false;
   }

   public void setMaxMemoryRows(int var1) {
      this.maxMemoryRows = var1;
   }

   public void setForDataChangeDeltaTable() {
      this.forDataChangeDeltaTable = true;
   }

   public LocalResult createShallowCopy(Session var1) {
      if (this.external == null && (this.rows == null || (long)this.rows.size() < this.rowCount)) {
         return null;
      } else if (this.containsLobs) {
         return null;
      } else {
         ResultExternal var2 = null;
         if (this.external != null) {
            var2 = this.external.createShallowCopy();
            if (var2 == null) {
               return null;
            }
         }

         LocalResult var3 = new LocalResult((SessionLocal)var1);
         var3.maxMemoryRows = this.maxMemoryRows;
         var3.visibleColumnCount = this.visibleColumnCount;
         var3.resultColumnCount = this.resultColumnCount;
         var3.expressions = this.expressions;
         var3.rowId = -1L;
         var3.rowCount = this.rowCount;
         var3.rows = this.rows;
         var3.sort = this.sort;
         var3.distinctRows = this.distinctRows;
         var3.distinct = this.distinct;
         var3.distinctIndexes = this.distinctIndexes;
         var3.currentRow = null;
         var3.offset = 0L;
         var3.limit = -1L;
         var3.external = var2;
         var3.containsNull = this.containsNull;
         return var3;
      }
   }

   public void setSortOrder(SortOrder var1) {
      this.sort = var1;
   }

   public void setDistinct() {
      assert this.distinctIndexes == null;

      this.distinct = true;
      this.distinctRows = new TreeMap(this.session.getDatabase().getCompareMode());
   }

   public void setDistinct(int[] var1) {
      assert !this.distinct;

      this.distinctIndexes = var1;
      this.distinctRows = new TreeMap(this.session.getDatabase().getCompareMode());
   }

   private boolean isAnyDistinct() {
      return this.distinct || this.distinctIndexes != null;
   }

   public boolean containsDistinct(Value[] var1) {
      assert var1.length == this.visibleColumnCount;

      if (this.external != null) {
         return this.external.contains(var1);
      } else {
         if (this.distinctRows == null) {
            this.distinctRows = new TreeMap(this.session.getDatabase().getCompareMode());
            Iterator var2 = this.rows.iterator();

            while(var2.hasNext()) {
               Value[] var3 = (Value[])var2.next();
               ValueRow var4 = this.getDistinctRow(var3);
               this.distinctRows.put(var4, var4.getList());
            }
         }

         ValueRow var5 = ValueRow.get(var1);
         return this.distinctRows.get(var5) != null;
      }
   }

   public boolean containsNull() {
      Boolean var1 = this.containsNull;
      if (var1 == null) {
         var1 = false;
         this.reset();

         label25:
         while(this.next()) {
            Value[] var2 = this.currentRow;

            for(int var3 = 0; var3 < this.visibleColumnCount; ++var3) {
               if (var2[var3].containsNull()) {
                  var1 = true;
                  break label25;
               }
            }
         }

         this.reset();
         this.containsNull = var1;
      }

      return var1;
   }

   public void removeDistinct(Value[] var1) {
      if (!this.distinct) {
         throw DbException.getInternalError();
      } else {
         assert var1.length == this.visibleColumnCount;

         if (this.distinctRows != null) {
            this.distinctRows.remove(ValueRow.get(var1));
            this.rowCount = (long)this.distinctRows.size();
         } else {
            this.rowCount = (long)this.external.removeRow(var1);
         }

      }
   }

   public void reset() {
      this.rowId = -1L;
      this.currentRow = null;
      if (this.external != null) {
         this.external.reset();
      }

   }

   public Row currentRowForTable() {
      int var1 = this.visibleColumnCount;
      Value[] var2 = this.currentRow;
      Row var3 = this.session.getDatabase().getRowFactory().createRow((Value[])Arrays.copyOf(var2, var1), -1);
      var3.setKey(var2[var1].getLong());
      return var3;
   }

   public Value[] currentRow() {
      return this.currentRow;
   }

   public boolean next() {
      if (!this.closed && this.rowId < this.rowCount) {
         ++this.rowId;
         if (this.rowId < this.rowCount) {
            if (this.external != null) {
               this.currentRow = this.external.next();
            } else {
               this.currentRow = (Value[])this.rows.get((int)this.rowId);
            }

            return true;
         }

         this.currentRow = null;
      }

      return false;
   }

   public long getRowId() {
      return this.rowId;
   }

   public boolean isAfterLast() {
      return this.rowId >= this.rowCount;
   }

   private void cloneLobs(Value[] var1) {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         Value var3 = var1[var2];
         if (var3 instanceof ValueLob) {
            if (this.forDataChangeDeltaTable) {
               this.containsLobs = true;
            } else {
               ValueLob var4 = ((ValueLob)var3).copyToResult();
               if (var4 != var3) {
                  this.containsLobs = true;
                  var1[var2] = this.session.addTemporaryLob(var4);
               }
            }
         }
      }

   }

   private ValueRow getDistinctRow(Value[] var1) {
      if (this.distinctIndexes != null) {
         int var2 = this.distinctIndexes.length;
         Value[] var3 = new Value[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var1[this.distinctIndexes[var4]];
         }

         var1 = var3;
      } else if (var1.length > this.visibleColumnCount) {
         var1 = (Value[])Arrays.copyOf(var1, this.visibleColumnCount);
      }

      return ValueRow.get(var1);
   }

   private void createExternalResult() {
      this.external = MVTempResult.of(this.session.getDatabase(), this.expressions, this.distinct, this.distinctIndexes, this.visibleColumnCount, this.resultColumnCount, this.sort);
   }

   public void addRowForTable(Row var1) {
      int var2 = this.visibleColumnCount;
      Value[] var3 = new Value[var2 + 1];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = var1.getValue(var4);
      }

      var3[var2] = ValueBigint.get(var1.getKey());
      this.addRowInternal(var3);
   }

   public void addRow(Value... var1) {
      assert var1.length == this.resultColumnCount;

      this.cloneLobs(var1);
      this.addRowInternal(var1);
   }

   private void addRowInternal(Value... var1) {
      if (this.isAnyDistinct()) {
         if (this.distinctRows != null) {
            ValueRow var2 = this.getDistinctRow(var1);
            Value[] var3 = (Value[])this.distinctRows.get(var2);
            if (var3 == null || this.sort != null && this.sort.compare(var3, var1) > 0) {
               this.distinctRows.put(var2, var1);
            }

            this.rowCount = (long)this.distinctRows.size();
            if (this.rowCount > (long)this.maxMemoryRows) {
               this.createExternalResult();
               this.rowCount = (long)this.external.addRows(this.distinctRows.values());
               this.distinctRows = null;
            }
         } else {
            this.rowCount = (long)this.external.addRow(var1);
         }
      } else {
         this.rows.add(var1);
         ++this.rowCount;
         if (this.rows.size() > this.maxMemoryRows) {
            this.addRowsToDisk();
         }
      }

   }

   private void addRowsToDisk() {
      if (this.external == null) {
         this.createExternalResult();
      }

      this.rowCount = (long)this.external.addRows(this.rows);
      this.rows.clear();
   }

   public int getVisibleColumnCount() {
      return this.visibleColumnCount;
   }

   public void done() {
      if (this.external != null) {
         this.addRowsToDisk();
      } else {
         if (this.isAnyDistinct()) {
            this.rows = new ArrayList(this.distinctRows.values());
         }

         if (this.sort != null && this.limit != 0L && !this.limitsWereApplied) {
            boolean var1 = this.limit > 0L && this.withTiesSortOrder == null;
            if (this.offset <= 0L && !var1) {
               this.sort.sort(this.rows);
            } else {
               int var2 = this.rows.size();
               if (this.offset < (long)var2) {
                  int var3 = (int)this.offset;
                  if (var1 && this.limit < (long)(var2 - var3)) {
                     var2 = var3 + (int)this.limit;
                  }

                  this.sort.sort(this.rows, var3, var2);
               }
            }
         }
      }

      this.applyOffsetAndLimit();
      this.reset();
   }

   private void applyOffsetAndLimit() {
      if (!this.limitsWereApplied) {
         long var1 = Math.max(this.offset, 0L);
         long var3 = this.limit;
         if ((var1 != 0L || var3 >= 0L || this.fetchPercent) && this.rowCount != 0L) {
            if (this.fetchPercent) {
               if (var3 < 0L || var3 > 100L) {
                  throw DbException.getInvalidValueException("FETCH PERCENT", var3);
               }

               var3 = (var3 * this.rowCount + 99L) / 100L;
            }

            boolean var5 = var1 >= this.rowCount || var3 == 0L;
            if (!var5) {
               long var6 = this.rowCount - var1;
               var3 = var3 < 0L ? var6 : Math.min(var6, var3);
               if (var1 == 0L && var6 <= var3) {
                  return;
               }
            } else {
               var3 = 0L;
            }

            this.distinctRows = null;
            this.rowCount = var3;
            if (this.external == null) {
               if (var5) {
                  this.rows.clear();
                  return;
               }

               int var8 = (int)(var1 + var3);
               if (this.withTiesSortOrder != null) {
                  for(Value[] var7 = (Value[])this.rows.get(var8 - 1); var8 < this.rows.size() && this.withTiesSortOrder.compare(var7, (Value[])this.rows.get(var8)) == 0; ++this.rowCount) {
                     ++var8;
                  }
               }

               if (var1 != 0L || var8 != this.rows.size()) {
                  this.rows = new ArrayList(this.rows.subList((int)var1, var8));
               }
            } else {
               if (var5) {
                  this.external.close();
                  this.external = null;
                  return;
               }

               this.trimExternal(var1, var3);
            }

         }
      }
   }

   private void trimExternal(long var1, long var3) {
      ResultExternal var5 = this.external;
      this.external = null;
      var5.reset();

      while(--var1 >= 0L) {
         var5.next();
      }

      Value[] var6 = null;

      while(--var3 >= 0L) {
         var6 = var5.next();
         this.rows.add(var6);
         if (this.rows.size() > this.maxMemoryRows) {
            this.addRowsToDisk();
         }
      }

      if (this.withTiesSortOrder != null && var6 != null) {
         Value[] var7 = var6;

         while((var6 = var5.next()) != null && this.withTiesSortOrder.compare(var7, var6) == 0) {
            this.rows.add(var6);
            ++this.rowCount;
            if (this.rows.size() > this.maxMemoryRows) {
               this.addRowsToDisk();
            }
         }
      }

      if (this.external != null) {
         this.addRowsToDisk();
      }

      var5.close();
   }

   public long getRowCount() {
      return this.rowCount;
   }

   public void limitsWereApplied() {
      this.limitsWereApplied = true;
   }

   public boolean hasNext() {
      return !this.closed && this.rowId < this.rowCount - 1L;
   }

   public void setLimit(long var1) {
      this.limit = var1;
   }

   public void setFetchPercent(boolean var1) {
      this.fetchPercent = var1;
   }

   public void setWithTies(SortOrder var1) {
      assert this.sort == null || this.sort == var1;

      this.withTiesSortOrder = var1;
   }

   public boolean needToClose() {
      return this.external != null;
   }

   public void close() {
      if (this.external != null) {
         this.external.close();
         this.external = null;
         this.closed = true;
      }

   }

   public String getAlias(int var1) {
      return this.expressions[var1].getAlias(this.session, var1);
   }

   public String getTableName(int var1) {
      return this.expressions[var1].getTableName();
   }

   public String getSchemaName(int var1) {
      return this.expressions[var1].getSchemaName();
   }

   public String getColumnName(int var1) {
      return this.expressions[var1].getColumnName(this.session, var1);
   }

   public TypeInfo getColumnType(int var1) {
      return this.expressions[var1].getType();
   }

   public int getNullable(int var1) {
      return this.expressions[var1].getNullable();
   }

   public boolean isIdentity(int var1) {
      return this.expressions[var1].isIdentity();
   }

   public void setOffset(long var1) {
      this.offset = var1;
   }

   public String toString() {
      return super.toString() + " columns: " + this.visibleColumnCount + " rows: " + this.rowCount + " pos: " + this.rowId;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public int getFetchSize() {
      return 0;
   }

   public void setFetchSize(int var1) {
   }
}
