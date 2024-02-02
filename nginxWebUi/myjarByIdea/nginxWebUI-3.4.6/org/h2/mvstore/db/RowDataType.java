package org.h2.mvstore.db;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Database;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StatefulDataType;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.store.DataHandler;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class RowDataType extends BasicDataType<SearchRow> implements StatefulDataType<Database> {
   private final ValueDataType valueDataType;
   private final int[] sortTypes;
   private final int[] indexes;
   private final int columnCount;
   private final boolean storeKeys;
   private static final Factory FACTORY = new Factory();

   public RowDataType(CastDataProvider var1, CompareMode var2, DataHandler var3, int[] var4, int[] var5, int var6, boolean var7) {
      this.valueDataType = new ValueDataType(var1, var2, var3, var4);
      this.sortTypes = var4;
      this.indexes = var5;
      this.columnCount = var6;
      this.storeKeys = var7;

      assert var5 == null || var4.length == var5.length;

   }

   public int[] getIndexes() {
      return this.indexes;
   }

   public RowFactory getRowFactory() {
      return this.valueDataType.getRowFactory();
   }

   public void setRowFactory(RowFactory var1) {
      this.valueDataType.setRowFactory(var1);
   }

   public int getColumnCount() {
      return this.columnCount;
   }

   public boolean isStoreKeys() {
      return this.storeKeys;
   }

   public SearchRow[] createStorage(int var1) {
      return new SearchRow[var1];
   }

   public int compare(SearchRow var1, SearchRow var2) {
      if (var1 == var2) {
         return 0;
      } else if (this.indexes == null) {
         int var3 = var1.getColumnCount();

         assert var3 == var2.getColumnCount() : var3 + " != " + var2.getColumnCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = this.valueDataType.compareValues(var1.getValue(var4), var2.getValue(var4), this.sortTypes[var4]);
            if (var5 != 0) {
               return var5;
            }
         }

         return 0;
      } else {
         return this.compareSearchRows(var1, var2);
      }
   }

   private int compareSearchRows(SearchRow var1, SearchRow var2) {
      for(int var3 = 0; var3 < this.indexes.length; ++var3) {
         int var4 = this.indexes[var3];
         Value var5 = var1.getValue(var4);
         Value var6 = var2.getValue(var4);
         if (var5 == null || var6 == null) {
            break;
         }

         int var7 = this.valueDataType.compareValues(var5, var6, this.sortTypes[var3]);
         if (var7 != 0) {
            return var7;
         }
      }

      long var8 = var1.getKey();
      long var9 = var2.getKey();
      return var8 != SearchRow.MATCH_ALL_ROW_KEY && var9 != SearchRow.MATCH_ALL_ROW_KEY ? Long.compare(var8, var9) : 0;
   }

   public int binarySearch(SearchRow var1, Object var2, int var3, int var4) {
      return this.binarySearch(var1, (SearchRow[])((SearchRow[])var2), var3, var4);
   }

   public int binarySearch(SearchRow var1, SearchRow[] var2, int var3, int var4) {
      int var5 = 0;
      int var6 = var3 - 1;
      int var7 = var4 - 1;
      if (var7 < 0 || var7 > var6) {
         var7 = var6 >>> 1;
      }

      for(; var5 <= var6; var7 = var5 + var6 >>> 1) {
         int var8 = this.compareSearchRows(var1, var2[var7]);
         if (var8 > 0) {
            var5 = var7 + 1;
         } else {
            if (var8 >= 0) {
               return var7;
            }

            var6 = var7 - 1;
         }
      }

      return -(var5 + 1);
   }

   public int getMemory(SearchRow var1) {
      return var1.getMemory();
   }

   public SearchRow read(ByteBuffer var1) {
      RowFactory var2 = this.valueDataType.getRowFactory();
      SearchRow var3 = var2.createRow();
      if (this.storeKeys) {
         var3.setKey(DataUtils.readVarLong(var1));
      }

      TypeInfo[] var4 = var2.getColumnTypes();
      int var6;
      if (this.indexes == null) {
         int var5 = var3.getColumnCount();

         for(var6 = 0; var6 < var5; ++var6) {
            var3.setValue(var6, this.valueDataType.readValue(var1, var4 != null ? var4[var6] : null));
         }
      } else {
         int[] var9 = this.indexes;
         var6 = var9.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            int var8 = var9[var7];
            var3.setValue(var8, this.valueDataType.readValue(var1, var4 != null ? var4[var8] : null));
         }
      }

      return var3;
   }

   public void write(WriteBuffer var1, SearchRow var2) {
      if (this.storeKeys) {
         var1.putVarLong(var2.getKey());
      }

      int var4;
      if (this.indexes == null) {
         int var3 = var2.getColumnCount();

         for(var4 = 0; var4 < var3; ++var4) {
            this.valueDataType.write(var1, var2.getValue(var4));
         }
      } else {
         int[] var7 = this.indexes;
         var4 = var7.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var7[var5];
            this.valueDataType.write(var1, var2.getValue(var6));
         }
      }

   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && var1.getClass() == RowDataType.class) {
         RowDataType var2 = (RowDataType)var1;
         return this.columnCount == var2.columnCount && Arrays.equals(this.indexes, var2.indexes) && Arrays.equals(this.sortTypes, var2.sortTypes) && this.valueDataType.equals(var2.valueDataType);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = super.hashCode();
      var1 = var1 * 31 + this.columnCount;
      var1 = var1 * 31 + Arrays.hashCode(this.indexes);
      var1 = var1 * 31 + Arrays.hashCode(this.sortTypes);
      var1 = var1 * 31 + this.valueDataType.hashCode();
      return var1;
   }

   public void save(WriteBuffer var1, MetaType<Database> var2) {
      var1.putVarInt(this.columnCount);
      writeIntArray(var1, this.sortTypes);
      writeIntArray(var1, this.indexes);
      var1.put((byte)(this.storeKeys ? 1 : 0));
   }

   private static void writeIntArray(WriteBuffer var0, int[] var1) {
      if (var1 == null) {
         var0.putVarInt(0);
      } else {
         var0.putVarInt(var1.length + 1);
         int[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            var0.putVarInt(var5);
         }
      }

   }

   public Factory getFactory() {
      return FACTORY;
   }

   public static final class Factory implements StatefulDataType.Factory<Database> {
      public RowDataType create(ByteBuffer var1, MetaType<Database> var2, Database var3) {
         int var4 = DataUtils.readVarInt(var1);
         int[] var5 = readIntArray(var1);
         int[] var6 = readIntArray(var1);
         boolean var7 = var1.get() != 0;
         CompareMode var8 = var3 == null ? CompareMode.getInstance((String)null, 0) : var3.getCompareMode();
         RowFactory var9 = RowFactory.getDefaultRowFactory().createRowFactory(var3, var8, var3, var5, var6, (TypeInfo[])null, var4, var7);
         return var9.getRowDataType();
      }

      private static int[] readIntArray(ByteBuffer var0) {
         int var1 = DataUtils.readVarInt(var0) - 1;
         if (var1 < 0) {
            return null;
         } else {
            int[] var2 = new int[var1];

            for(int var3 = 0; var3 < var2.length; ++var3) {
               var2[var3] = DataUtils.readVarInt(var0);
            }

            return var2;
         }
      }
   }
}
