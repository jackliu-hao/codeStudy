package org.h2.mvstore.db;

import java.util.Iterator;
import java.util.List;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.index.IndexType;
import org.h2.index.SpatialIndex;
import org.h2.message.DbException;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStoreException;
import org.h2.mvstore.Page;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.Spatial;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionMap;
import org.h2.mvstore.tx.VersionedValueType;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.TableFilter;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.Value;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueNull;
import org.h2.value.VersionedValue;

public class MVSpatialIndex extends MVIndex<Spatial, Value> implements SpatialIndex {
   final MVTable mvTable;
   private final TransactionMap<Spatial, Value> dataMap;
   private final MVRTreeMap<VersionedValue<Value>> spatialMap;

   public MVSpatialIndex(Database var1, MVTable var2, int var3, String var4, IndexColumn[] var5, int var6, IndexType var7) {
      super(var2, var3, var4, var5, var6, var7);
      if (var5.length != 1) {
         throw DbException.getUnsupportedException("Can only index one column");
      } else {
         IndexColumn var8 = var5[0];
         if ((var8.sortType & 1) != 0) {
            throw DbException.getUnsupportedException("Cannot index in descending order");
         } else if ((var8.sortType & 2) != 0) {
            throw DbException.getUnsupportedException("Nulls first is not supported");
         } else if ((var8.sortType & 4) != 0) {
            throw DbException.getUnsupportedException("Nulls last is not supported");
         } else if (var8.column.getType().getValueType() != 37) {
            throw DbException.getUnsupportedException("Spatial index on non-geometry column, " + var8.column.getCreateSQL());
         } else {
            this.mvTable = var2;
            if (!this.database.isStarting()) {
               checkIndexColumnTypes(var5);
            }

            String var9 = "index." + this.getId();
            VersionedValueType var10 = new VersionedValueType(NullValueDataType.INSTANCE);
            MVRTreeMap.Builder var11 = (new MVRTreeMap.Builder()).valueType(var10);
            this.spatialMap = (MVRTreeMap)var1.getStore().getMvStore().openMap(var9, var11);
            Transaction var12 = this.mvTable.getTransactionBegin();
            this.dataMap = var12.openMapX(this.spatialMap);
            this.dataMap.map.setVolatile(!var2.isPersistData() || !var7.isPersistent());
            var12.commit();
         }
      }
   }

   public void addRowsToBuffer(List<Row> var1, String var2) {
      throw DbException.getInternalError();
   }

   public void addBufferedRows(List<String> var1) {
      throw DbException.getInternalError();
   }

   public void close(SessionLocal var1) {
   }

   public void add(SessionLocal var1, Row var2) {
      TransactionMap var3 = this.getMap(var1);
      SpatialKey var4 = this.getKey(var2);
      if (!var4.isNull()) {
         MVRTreeMap.RTreeCursor var5;
         SpatialKeyIterator var6;
         Spatial var7;
         if (this.uniqueColumnColumn > 0) {
            var5 = this.spatialMap.findContainedKeys(var4);
            var6 = new SpatialKeyIterator(var3, var5, false);

            while(var6.hasNext()) {
               var7 = (Spatial)var6.next();
               if (var7.equalsIgnoringId(var4)) {
                  throw this.getDuplicateKeyException(var4.toString());
               }
            }
         }

         try {
            var3.put(var4, ValueNull.INSTANCE);
         } catch (MVStoreException var8) {
            throw this.mvTable.convertException(var8);
         }

         if (this.uniqueColumnColumn > 0) {
            var5 = this.spatialMap.findContainedKeys(var4);
            var6 = new SpatialKeyIterator(var3, var5, true);

            while(var6.hasNext()) {
               var7 = (Spatial)var6.next();
               if (var7.equalsIgnoringId(var4) && !var3.isSameTransaction(var7)) {
                  var3.remove(var4);
                  if (var3.getImmediate(var7) != null) {
                     throw this.getDuplicateKeyException(var7.toString());
                  }

                  throw DbException.get(90131, this.table.getName());
               }
            }
         }

      }
   }

   public void remove(SessionLocal var1, Row var2) {
      SpatialKey var3 = this.getKey(var2);
      if (!var3.isNull()) {
         TransactionMap var4 = this.getMap(var1);

         try {
            Value var5 = (Value)var4.remove(var3);
            if (var5 == null) {
               StringBuilder var6 = new StringBuilder();
               this.getSQL(var6, 3).append(": ").append(var2.getKey());
               throw DbException.get(90112, var6.toString());
            }
         } catch (MVStoreException var7) {
            throw this.mvTable.convertException(var7);
         }
      }
   }

   public Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3) {
      Iterator var4 = this.spatialMap.keyIterator((Object)null);
      TransactionMap var5 = this.getMap(var1);
      SpatialKeyIterator var6 = new SpatialKeyIterator(var5, var4, false);
      return new MVStoreCursor(var1, var6, this.mvTable);
   }

   public Cursor findByGeometry(SessionLocal var1, SearchRow var2, SearchRow var3, SearchRow var4) {
      if (var4 == null) {
         return this.find(var1, var2, var3);
      } else {
         MVRTreeMap.RTreeCursor var5 = this.spatialMap.findIntersectingKeys(this.getKey(var4));
         TransactionMap var6 = this.getMap(var1);
         SpatialKeyIterator var7 = new SpatialKeyIterator(var6, var5, false);
         return new MVStoreCursor(var1, var7, this.mvTable);
      }
   }

   public Value getBounds(SessionLocal var1) {
      FindBoundsCursor var2 = new FindBoundsCursor(this.spatialMap.getRootPage(), new SpatialKey(0L, new float[0]), var1, this.getMap(var1), this.columnIds[0]);

      while(var2.hasNext()) {
         var2.next();
      }

      return var2.getBounds();
   }

   public Value getEstimatedBounds(SessionLocal var1) {
      Page var2 = this.spatialMap.getRootPage();
      int var3 = var2.getKeyCount();
      if (var3 > 0) {
         Spatial var4 = (Spatial)var2.getKey(0);
         float var5 = var4.min(0);
         float var6 = var4.max(0);
         float var7 = var4.min(1);
         float var8 = var4.max(1);

         for(int var9 = 1; var9 < var3; ++var9) {
            var4 = (Spatial)var2.getKey(var9);
            float var10 = var4.min(0);
            float var11 = var4.max(0);
            float var12 = var4.min(1);
            float var13 = var4.max(1);
            if (var10 < var5) {
               var5 = var10;
            }

            if (var11 > var6) {
               var6 = var11;
            }

            if (var12 < var7) {
               var7 = var12;
            }

            if (var13 > var8) {
               var8 = var13;
            }
         }

         return ValueGeometry.fromEnvelope(new double[]{(double)var5, (double)var6, (double)var7, (double)var8});
      } else {
         return ValueNull.INSTANCE;
      }
   }

   private SpatialKey getKey(SearchRow var1) {
      Value var2 = var1.getValue(this.columnIds[0]);
      double[] var3;
      return var2 != ValueNull.INSTANCE && (var3 = var2.convertToGeometry((ExtTypeInfoGeometry)null).getEnvelopeNoCopy()) != null ? new SpatialKey(var1.getKey(), new float[]{(float)var3[0], (float)var3[1], (float)var3[2], (float)var3[3]}) : new SpatialKey(var1.getKey(), new float[0]);
   }

   public MVTable getTable() {
      return this.mvTable;
   }

   public double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6) {
      return (double)getCostRangeIndex(var2, this.columns);
   }

   public static long getCostRangeIndex(int[] var0, Column[] var1) {
      if (var1.length == 0) {
         return Long.MAX_VALUE;
      } else {
         Column[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Column var5 = var2[var4];
            int var6 = var5.getColumnId();
            int var7 = var0[var6];
            if ((var7 & 16) != 16) {
               return Long.MAX_VALUE;
            }
         }

         return 2L;
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

   private TransactionMap<Spatial, Value> getMap(SessionLocal var1) {
      if (var1 == null) {
         return this.dataMap;
      } else {
         Transaction var2 = var1.getTransaction();
         return this.dataMap.getInstance(var2);
      }
   }

   public MVMap<Spatial, VersionedValue<Value>> getMVMap() {
      return this.dataMap.map;
   }

   private final class FindBoundsCursor extends MVRTreeMap.RTreeCursor<VersionedValue<Value>> {
      private final SessionLocal session;
      private final TransactionMap<Spatial, Value> map;
      private final int columnId;
      private boolean hasBounds;
      private float bminxf;
      private float bmaxxf;
      private float bminyf;
      private float bmaxyf;
      private double bminxd;
      private double bmaxxd;
      private double bminyd;
      private double bmaxyd;

      FindBoundsCursor(Page<Spatial, VersionedValue<Value>> var2, Spatial var3, SessionLocal var4, TransactionMap<Spatial, Value> var5, int var6) {
         super(var2, var3);
         this.session = var4;
         this.map = var5;
         this.columnId = var6;
      }

      protected boolean check(boolean var1, Spatial var2, Spatial var3) {
         float var4 = var2.min(0);
         float var5 = var2.max(0);
         float var6 = var2.min(1);
         float var7 = var2.max(1);
         if (var1) {
            double[] var8;
            if (this.hasBounds) {
               if ((var4 <= this.bminxf || var5 >= this.bmaxxf || var6 <= this.bminyf || var7 >= this.bmaxyf) && this.map.containsKey(var2)) {
                  var8 = ((ValueGeometry)MVSpatialIndex.this.mvTable.getRow(this.session, var2.getId()).getValue(this.columnId)).getEnvelopeNoCopy();
                  double var9 = var8[0];
                  double var11 = var8[1];
                  double var13 = var8[2];
                  double var15 = var8[3];
                  if (var9 < this.bminxd) {
                     this.bminxf = var4;
                     this.bminxd = var9;
                  }

                  if (var11 > this.bmaxxd) {
                     this.bmaxxf = var5;
                     this.bmaxxd = var11;
                  }

                  if (var13 < this.bminyd) {
                     this.bminyf = var6;
                     this.bminyd = var13;
                  }

                  if (var15 > this.bmaxyd) {
                     this.bmaxyf = var7;
                     this.bmaxyd = var15;
                  }
               }
            } else if (this.map.containsKey(var2)) {
               this.hasBounds = true;
               var8 = ((ValueGeometry)MVSpatialIndex.this.mvTable.getRow(this.session, var2.getId()).getValue(this.columnId)).getEnvelopeNoCopy();
               this.bminxf = var4;
               this.bminxd = var8[0];
               this.bmaxxf = var5;
               this.bmaxxd = var8[1];
               this.bminyf = var6;
               this.bminyd = var8[2];
               this.bmaxyf = var7;
               this.bmaxyd = var8[3];
            }
         } else {
            if (!this.hasBounds) {
               return true;
            }

            if (var4 <= this.bminxf || var5 >= this.bmaxxf || var6 <= this.bminyf || var7 >= this.bmaxyf) {
               return true;
            }
         }

         return false;
      }

      Value getBounds() {
         return (Value)(this.hasBounds ? ValueGeometry.fromEnvelope(new double[]{this.bminxd, this.bmaxxd, this.bminyd, this.bmaxyd}) : ValueNull.INSTANCE);
      }
   }

   private static class SpatialKeyIterator implements Iterator<Spatial> {
      private final TransactionMap<Spatial, Value> map;
      private final Iterator<Spatial> iterator;
      private final boolean includeUncommitted;
      private Spatial current;

      SpatialKeyIterator(TransactionMap<Spatial, Value> var1, Iterator<Spatial> var2, boolean var3) {
         this.map = var1;
         this.iterator = var2;
         this.includeUncommitted = var3;
         this.fetchNext();
      }

      private void fetchNext() {
         while(true) {
            if (this.iterator.hasNext()) {
               this.current = (Spatial)this.iterator.next();
               if (!this.includeUncommitted && !this.map.containsKey(this.current)) {
                  continue;
               }

               return;
            }

            this.current = null;
            return;
         }
      }

      public boolean hasNext() {
         return this.current != null;
      }

      public Spatial next() {
         Spatial var1 = this.current;
         this.fetchNext();
         return var1;
      }
   }

   private static class MVStoreCursor implements Cursor {
      private final SessionLocal session;
      private final Iterator<Spatial> it;
      private final MVTable mvTable;
      private Spatial current;
      private SearchRow searchRow;
      private Row row;

      MVStoreCursor(SessionLocal var1, Iterator<Spatial> var2, MVTable var3) {
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
         if (this.searchRow == null && this.current != null) {
            this.searchRow = this.mvTable.getTemplateRow();
            this.searchRow.setKey(this.current.getId());
         }

         return this.searchRow;
      }

      public boolean next() {
         this.current = this.it.hasNext() ? (Spatial)this.it.next() : null;
         this.searchRow = null;
         this.row = null;
         return this.current != null;
      }

      public boolean previous() {
         throw DbException.getUnsupportedException("previous");
      }
   }
}
