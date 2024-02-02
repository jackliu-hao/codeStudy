package org.h2.mvstore.db;

import java.util.Arrays;
import java.util.BitSet;
import org.h2.engine.Database;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.mvstore.Cursor;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.result.ResultExternal;
import org.h2.result.RowFactory;
import org.h2.result.SortOrder;
import org.h2.table.IndexColumn;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;

class MVSortedTempResult extends MVTempResult {
   private final boolean distinct;
   private final int[] distinctIndexes;
   private final int[] indexes;
   private final MVMap<ValueRow, Long> map;
   private MVMap<ValueRow, Value> index;
   private ValueDataType orderedDistinctOnType;
   private Cursor<ValueRow, Long> cursor;
   private Value[] current;
   private long valueCount;

   private MVSortedTempResult(MVSortedTempResult var1) {
      super(var1);
      this.distinct = var1.distinct;
      this.distinctIndexes = var1.distinctIndexes;
      this.indexes = var1.indexes;
      this.map = var1.map;
      this.rowCount = var1.rowCount;
   }

   MVSortedTempResult(Database var1, Expression[] var2, boolean var3, int[] var4, int var5, int var6, SortOrder var7) {
      super(var1, var2, var5, var6);
      this.distinct = var3;
      this.distinctIndexes = var4;
      int[] var8 = new int[var6];
      int[] var9;
      int var11;
      int var13;
      int var14;
      if (var7 != null) {
         var9 = new int[var6];
         int[] var10 = var7.getQueryColumnIndexes();
         var11 = var10.length;
         BitSet var12 = new BitSet();

         for(var13 = 0; var13 < var11; ++var13) {
            var14 = var10[var13];

            assert !var12.get(var14);

            var12.set(var14);
            var9[var13] = var14;
            var8[var13] = var7.getSortTypes()[var13];
         }

         var13 = 0;

         for(var14 = var11; var14 < var6; ++var14) {
            var13 = var12.nextClearBit(var13);
            var9[var14] = var13++;
         }

         var14 = 0;

         while(true) {
            if (var14 >= var6) {
               var9 = null;
               break;
            }

            if (var9[var14] != var14) {
               break;
            }

            ++var14;
         }
      } else {
         var9 = null;
      }

      this.indexes = var9;
      ValueDataType var17 = new ValueDataType(var1, SortOrder.addNullOrdering(var1, var8));
      if (var9 != null) {
         var11 = var9.length;
         TypeInfo[] var19 = new TypeInfo[var11];

         for(var13 = 0; var13 < var11; ++var13) {
            var19[var13] = var2[var9[var13]].getType();
         }

         var17.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(var1, var1.getCompareMode(), var1, var19, (IndexColumn[])null, false));
      } else {
         var17.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(var1, var1.getCompareMode(), var1, var2, (IndexColumn[])null, false));
      }

      MVMap.Builder var18 = (new MVMap.Builder()).keyType(var17).valueType(LongDataType.INSTANCE);
      this.map = this.store.openMap("tmp", var18);
      if (var3 && var6 != var5 || var4 != null) {
         int var20;
         TypeInfo[] var21;
         if (var4 != null) {
            var20 = var4.length;
            var21 = new TypeInfo[var20];

            for(var14 = 0; var14 < var20; ++var14) {
               var21[var14] = var2[var4[var14]].getType();
            }
         } else {
            var20 = var5;
            var21 = new TypeInfo[var5];

            for(var14 = 0; var14 < var20; ++var14) {
               var21[var14] = var2[var14].getType();
            }
         }

         ValueDataType var22 = new ValueDataType(var1, new int[var20]);
         var22.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory(var1, var1.getCompareMode(), var1, var21, (IndexColumn[])null, false));
         Object var15;
         if (var4 != null && var7 != null) {
            var15 = this.orderedDistinctOnType = var17;
         } else {
            var15 = NullValueDataType.INSTANCE;
         }

         MVMap.Builder var16 = (new MVMap.Builder()).keyType(var22).valueType((DataType)var15);
         this.index = this.store.openMap("idx", var16);
      }

   }

   public int addRow(Value[] var1) {
      assert this.parent == null;

      ValueRow var2 = this.getKey(var1);
      if (!this.distinct && this.distinctIndexes == null) {
         Long var8 = (Long)this.map.putIfAbsent(var2, 1L);
         if (var8 != null) {
            this.map.put(var2, var8 + 1L);
         }

         ++this.rowCount;
      } else {
         if (this.distinctIndexes == null) {
            if (this.visibleColumnCount != this.resultColumnCount) {
               ValueRow var7 = ValueRow.get((Value[])Arrays.copyOf(var1, this.visibleColumnCount));
               if (this.index.putIfAbsent(var7, ValueNull.INSTANCE) != null) {
                  return this.rowCount;
               }
            }
         } else {
            int var3 = this.distinctIndexes.length;
            Value[] var4 = new Value[var3];

            for(int var5 = 0; var5 < var3; ++var5) {
               var4[var5] = var1[this.distinctIndexes[var5]];
            }

            ValueRow var9 = ValueRow.get(var4);
            if (this.orderedDistinctOnType == null) {
               if (this.index.putIfAbsent(var9, ValueNull.INSTANCE) != null) {
                  return this.rowCount;
               }
            } else {
               ValueRow var6 = (ValueRow)this.index.get(var9);
               if (var6 == null) {
                  this.index.put(var9, var2);
               } else {
                  if (this.orderedDistinctOnType.compare((Value)var6, (Value)var2) <= 0) {
                     return this.rowCount;
                  }

                  this.map.remove(var6);
                  --this.rowCount;
                  this.index.put(var9, var2);
               }
            }
         }

         if (this.map.putIfAbsent(var2, 1L) == null) {
            ++this.rowCount;
         }
      }

      return this.rowCount;
   }

   public boolean contains(Value[] var1) {
      if (this.parent != null) {
         return this.parent.contains(var1);
      } else {
         assert this.distinct;

         return this.visibleColumnCount != this.resultColumnCount ? this.index.containsKey(ValueRow.get(var1)) : this.map.containsKey(this.getKey(var1));
      }
   }

   public synchronized ResultExternal createShallowCopy() {
      if (this.parent != null) {
         return this.parent.createShallowCopy();
      } else if (this.closed) {
         return null;
      } else {
         ++this.childCount;
         return new MVSortedTempResult(this);
      }
   }

   private ValueRow getKey(Value[] var1) {
      if (this.indexes != null) {
         Value[] var2 = new Value[this.indexes.length];

         for(int var3 = 0; var3 < this.indexes.length; ++var3) {
            var2[var3] = var1[this.indexes[var3]];
         }

         var1 = var2;
      }

      return ValueRow.get(var1);
   }

   private Value[] getValue(Value[] var1) {
      if (this.indexes != null) {
         Value[] var2 = new Value[this.indexes.length];

         for(int var3 = 0; var3 < this.indexes.length; ++var3) {
            var2[this.indexes[var3]] = var1[var3];
         }

         var1 = var2;
      }

      return var1;
   }

   public Value[] next() {
      if (this.cursor == null) {
         this.cursor = this.map.cursor((Object)null);
         this.current = null;
         this.valueCount = 0L;
      }

      if (--this.valueCount > 0L) {
         return this.current;
      } else if (!this.cursor.hasNext()) {
         this.current = null;
         return null;
      } else {
         this.current = this.getValue(((ValueRow)this.cursor.next()).getList());
         this.valueCount = (Long)this.cursor.getValue();
         return this.current;
      }
   }

   public int removeRow(Value[] var1) {
      assert this.parent == null && this.distinct;

      if (this.visibleColumnCount != this.resultColumnCount) {
         throw DbException.getUnsupportedException("removeRow()");
      } else {
         if (this.map.remove(this.getKey(var1)) != null) {
            --this.rowCount;
         }

         return this.rowCount;
      }
   }

   public void reset() {
      this.cursor = null;
      this.current = null;
      this.valueCount = 0L;
   }
}
