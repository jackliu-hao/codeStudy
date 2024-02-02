package org.h2.result;

import org.h2.engine.CastDataProvider;
import org.h2.mvstore.db.RowDataType;
import org.h2.store.DataHandler;
import org.h2.table.IndexColumn;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Typed;
import org.h2.value.Value;

public abstract class RowFactory {
   public static DefaultRowFactory getDefaultRowFactory() {
      return RowFactory.DefaultRowFactory.INSTANCE;
   }

   public static RowFactory getRowFactory() {
      return RowFactory.Holder.EFFECTIVE;
   }

   public RowFactory createRowFactory(CastDataProvider var1, CompareMode var2, DataHandler var3, Typed[] var4, IndexColumn[] var5, boolean var6) {
      return this;
   }

   public abstract Row createRow(Value[] var1, int var2);

   public abstract SearchRow createRow();

   public abstract RowDataType getRowDataType();

   public abstract int[] getIndexes();

   public abstract TypeInfo[] getColumnTypes();

   public abstract int getColumnCount();

   public abstract boolean getStoreKeys();

   public static final class DefaultRowFactory extends RowFactory {
      private final RowDataType dataType;
      private final int columnCount;
      private final int[] indexes;
      private TypeInfo[] columnTypes;
      private final int[] map;
      public static final DefaultRowFactory INSTANCE = new DefaultRowFactory();

      DefaultRowFactory() {
         this(new RowDataType((CastDataProvider)null, CompareMode.getInstance((String)null, 0), (DataHandler)null, (int[])null, (int[])null, 0, true), 0, (int[])null, (TypeInfo[])null);
      }

      private DefaultRowFactory(RowDataType var1, int var2, int[] var3, TypeInfo[] var4) {
         this.dataType = var1;
         this.columnCount = var2;
         this.indexes = var3;
         if (var3 == null) {
            this.map = null;
         } else {
            this.map = new int[var2];
            int var5 = 0;

            int var10001;
            for(int var6 = var3.length; var5 < var6; this.map[var10001] = var5) {
               var10001 = var3[var5];
               ++var5;
            }
         }

         this.columnTypes = var4;
      }

      public RowFactory createRowFactory(CastDataProvider var1, CompareMode var2, DataHandler var3, Typed[] var4, IndexColumn[] var5, boolean var6) {
         int[] var7 = null;
         int[] var8 = null;
         TypeInfo[] var9 = null;
         int var10 = 0;
         if (var4 != null) {
            var10 = var4.length;
            int var11;
            if (var5 == null) {
               var8 = new int[var10];

               for(var11 = 0; var11 < var10; ++var11) {
                  var8[var11] = 0;
               }
            } else {
               var11 = var5.length;
               var7 = new int[var11];
               var8 = new int[var11];

               for(int var12 = 0; var12 < var11; ++var12) {
                  IndexColumn var13 = var5[var12];
                  var7[var12] = var13.column.getColumnId();
                  var8[var12] = var13.sortType;
               }
            }

            var9 = new TypeInfo[var10];

            for(var11 = 0; var11 < var10; ++var11) {
               var9[var11] = var4[var11].getType();
            }
         }

         return this.createRowFactory(var1, var2, var3, var8, var7, var9, var10, var6);
      }

      public RowFactory createRowFactory(CastDataProvider var1, CompareMode var2, DataHandler var3, int[] var4, int[] var5, TypeInfo[] var6, int var7, boolean var8) {
         RowDataType var9 = new RowDataType(var1, var2, var3, var4, var5, var7, var8);
         DefaultRowFactory var10 = new DefaultRowFactory(var9, var7, var5, var6);
         var9.setRowFactory(var10);
         return var10;
      }

      public Row createRow(Value[] var1, int var2) {
         return new DefaultRow(var1, var2);
      }

      public SearchRow createRow() {
         if (this.indexes == null) {
            return new DefaultRow(this.columnCount);
         } else {
            return (SearchRow)(this.indexes.length == 1 ? new SimpleRowValue(this.columnCount, this.indexes[0]) : new Sparse(this.columnCount, this.indexes.length, this.map));
         }
      }

      public RowDataType getRowDataType() {
         return this.dataType;
      }

      public int[] getIndexes() {
         return this.indexes;
      }

      public TypeInfo[] getColumnTypes() {
         return this.columnTypes;
      }

      public int getColumnCount() {
         return this.columnCount;
      }

      public boolean getStoreKeys() {
         return this.dataType.isStoreKeys();
      }
   }

   private static final class Holder {
      static final RowFactory EFFECTIVE;

      static {
         EFFECTIVE = RowFactory.DefaultRowFactory.INSTANCE;
      }
   }
}
