package org.h2.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.Row;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.schema.SchemaObject;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public abstract class Index extends SchemaObject {
   protected IndexColumn[] indexColumns;
   protected Column[] columns;
   protected int[] columnIds;
   protected final int uniqueColumnColumn;
   protected final Table table;
   protected final IndexType indexType;
   private final RowFactory rowFactory;
   private final RowFactory uniqueRowFactory;

   protected static void checkIndexColumnTypes(IndexColumn[] var0) {
      IndexColumn[] var1 = var0;
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         IndexColumn var4 = var1[var3];
         if (!DataType.isIndexable(var4.column.getType())) {
            throw DbException.getUnsupportedException("Index on column: " + var4.column.getCreateSQL());
         }
      }

   }

   protected Index(Table var1, int var2, String var3, IndexColumn[] var4, int var5, IndexType var6) {
      super(var1.getSchema(), var2, var3, 5);
      this.uniqueColumnColumn = var5;
      this.indexType = var6;
      this.table = var1;
      if (var4 != null) {
         this.indexColumns = var4;
         this.columns = new Column[var4.length];
         int var7 = this.columns.length;
         this.columnIds = new int[var7];

         for(int var8 = 0; var8 < var7; ++var8) {
            Column var9 = var4[var8].column;
            this.columns[var8] = var9;
            this.columnIds[var8] = var9.getColumnId();
         }
      }

      RowFactory var11 = this.database.getRowFactory();
      CompareMode var12 = this.database.getCompareMode();
      Column[] var13 = this.table.getColumns();
      this.rowFactory = var11.createRowFactory(this.database, var12, this.database, var13, var6.isScan() ? null : var4, true);
      RowFactory var10;
      if (var5 > 0) {
         if (var4 != null && var5 != var4.length) {
            var10 = var11.createRowFactory(this.database, var12, this.database, var13, (IndexColumn[])Arrays.copyOf(var4, var5), true);
         } else {
            var10 = this.rowFactory;
         }
      } else {
         var10 = null;
      }

      this.uniqueRowFactory = var10;
   }

   public final int getType() {
      return 1;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.table.removeIndex(this);
      this.remove(var1);
      this.database.removeMeta(var1, this.getId());
   }

   public final boolean isHidden() {
      return this.table.isHidden();
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      StringBuilder var3 = new StringBuilder("CREATE ");
      var3.append(this.indexType.getSQL());
      var3.append(' ');
      if (this.table.isHidden()) {
         var3.append("IF NOT EXISTS ");
      }

      var3.append(var2);
      var3.append(" ON ");
      var1.getSQL(var3, 0);
      if (this.comment != null) {
         var3.append(" COMMENT ");
         StringUtils.quoteStringSQL(var3, this.comment);
      }

      return this.getColumnListSQL(var3, 0).toString();
   }

   private StringBuilder getColumnListSQL(StringBuilder var1, int var2) {
      var1.append('(');
      int var3 = this.indexColumns.length;
      if (this.uniqueColumnColumn > 0 && this.uniqueColumnColumn < var3) {
         IndexColumn.writeColumns(var1, this.indexColumns, 0, this.uniqueColumnColumn, var2).append(") INCLUDE(");
         IndexColumn.writeColumns(var1, this.indexColumns, this.uniqueColumnColumn, var3, var2);
      } else {
         IndexColumn.writeColumns(var1, this.indexColumns, 0, var3, var2);
      }

      return var1.append(')');
   }

   public String getCreateSQL() {
      return this.getCreateSQLForCopy(this.table, this.getSQL(0));
   }

   public String getPlanSQL() {
      return this.getSQL(11);
   }

   public abstract void close(SessionLocal var1);

   public abstract void add(SessionLocal var1, Row var2);

   public abstract void remove(SessionLocal var1, Row var2);

   public void update(SessionLocal var1, Row var2, Row var3) {
      this.remove(var1, var2);
      this.add(var1, var3);
   }

   public boolean isFindUsingFullTableScan() {
      return false;
   }

   public abstract Cursor find(SessionLocal var1, SearchRow var2, SearchRow var3);

   public abstract double getCost(SessionLocal var1, int[] var2, TableFilter[] var3, int var4, SortOrder var5, AllColumnsForPlan var6);

   public abstract void remove(SessionLocal var1);

   public abstract void truncate(SessionLocal var1);

   public boolean canGetFirstOrLast() {
      return false;
   }

   public boolean canFindNext() {
      return false;
   }

   public Cursor findNext(SessionLocal var1, SearchRow var2, SearchRow var3) {
      throw DbException.getInternalError(this.toString());
   }

   public Cursor findFirstOrLast(SessionLocal var1, boolean var2) {
      throw DbException.getInternalError(this.toString());
   }

   public abstract boolean needRebuild();

   public abstract long getRowCount(SessionLocal var1);

   public abstract long getRowCountApproximation(SessionLocal var1);

   public long getDiskSpaceUsed() {
      return 0L;
   }

   public final int compareRows(SearchRow var1, SearchRow var2) {
      if (var1 == var2) {
         return 0;
      } else {
         int var3 = 0;

         for(int var4 = this.indexColumns.length; var3 < var4; ++var3) {
            int var5 = this.columnIds[var3];
            Value var6 = var1.getValue(var5);
            Value var7 = var2.getValue(var5);
            if (var6 == null || var7 == null) {
               return 0;
            }

            int var8 = this.compareValues(var6, var7, this.indexColumns[var3].sortType);
            if (var8 != 0) {
               return var8;
            }
         }

         return 0;
      }
   }

   private int compareValues(Value var1, Value var2, int var3) {
      if (var1 == var2) {
         return 0;
      } else {
         boolean var4 = var1 == ValueNull.INSTANCE;
         if (!var4 && var2 != ValueNull.INSTANCE) {
            int var5 = this.table.compareValues(this.database, var1, var2);
            if ((var3 & 1) != 0) {
               var5 = -var5;
            }

            return var5;
         } else {
            return this.table.getDatabase().getDefaultNullOrdering().compareNull(var4, var3);
         }
      }
   }

   public int getColumnIndex(Column var1) {
      int var2 = 0;

      for(int var3 = this.columns.length; var2 < var3; ++var2) {
         if (this.columns[var2].equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   public boolean isFirstColumn(Column var1) {
      return var1.equals(this.columns[0]);
   }

   public final IndexColumn[] getIndexColumns() {
      return this.indexColumns;
   }

   public final Column[] getColumns() {
      return this.columns;
   }

   public final int getUniqueColumnCount() {
      return this.uniqueColumnColumn;
   }

   public final IndexType getIndexType() {
      return this.indexType;
   }

   public Table getTable() {
      return this.table;
   }

   public Row getRow(SessionLocal var1, long var2) {
      throw DbException.getUnsupportedException(this.toString());
   }

   public boolean isRowIdIndex() {
      return false;
   }

   public boolean canScan() {
      return true;
   }

   public DbException getDuplicateKeyException(String var1) {
      StringBuilder var2 = new StringBuilder();
      this.getSQL(var2, 3).append(" ON ");
      this.table.getSQL(var2, 3);
      this.getColumnListSQL(var2, 3);
      if (var1 != null) {
         var2.append(" VALUES ").append(var1);
      }

      DbException var3 = DbException.get(23505, (String)var2.toString());
      var3.setSource(this);
      return var3;
   }

   protected StringBuilder getDuplicatePrimaryKeyMessage(int var1) {
      StringBuilder var2 = new StringBuilder("PRIMARY KEY ON ");
      this.table.getSQL(var2, 3);
      if (var1 >= 0 && var1 < this.indexColumns.length) {
         var2.append('(');
         this.indexColumns[var1].getSQL(var2, 3).append(')');
      }

      return var2;
   }

   protected final long getCostRangeIndex(int[] var1, long var2, TableFilter[] var4, int var5, SortOrder var6, boolean var7, AllColumnsForPlan var8) {
      var2 += 1000L;
      int var9 = 0;
      long var10 = var2;
      boolean var14;
      int var16;
      if (var1 != null) {
         int var12 = 0;
         int var13 = this.columns.length;

         long var18;
         for(var14 = false; var12 < var13; var10 = 2L + Math.max(var2 / var18, 1L)) {
            Column var15 = this.columns[var12++];
            var16 = var15.getColumnId();
            int var17 = var1[var16];
            if ((var17 & 1) != 1) {
               if ((var17 & 6) == 6) {
                  var10 = 2L + var10 / 4L;
                  var14 = true;
               } else if ((var17 & 2) == 2) {
                  var10 = 2L + var10 / 3L;
                  var14 = true;
               } else if ((var17 & 4) == 4) {
                  var10 /= 3L;
                  var14 = true;
               } else if (var17 == 0) {
                  --var12;
               }
               break;
            }

            if (var12 > 0 && var12 == this.uniqueColumnColumn) {
               var10 = 3L;
               break;
            }

            var9 = 100 - (100 - var9) * (100 - var15.getSelectivity()) / 100;
            var18 = var2 * (long)var9 / 100L;
            if (var18 <= 0L) {
               var18 = 1L;
            }
         }

         if (var14) {
            while(var12 < var13 && var1[this.columns[var12].getColumnId()] != 0) {
               ++var12;
               --var10;
            }
         }

         var10 += (long)(var13 - var12);
      }

      long var24 = 0L;
      if (var6 != null) {
         var24 = 100L + var2 / 10L;
      }

      int var19;
      int var22;
      if (var6 != null && !var7) {
         var14 = true;
         int var25 = 0;
         int[] var27 = var6.getSortTypesWithNullOrdering();
         TableFilter var29 = var4 == null ? null : var4[var5];
         int var31 = 0;

         for(var19 = var27.length; var31 < var19 && var31 < this.indexColumns.length; ++var31) {
            Column var20 = var6.getColumn(var31, var29);
            if (var20 == null) {
               var14 = false;
               break;
            }

            IndexColumn var21 = this.indexColumns[var31];
            if (!var20.equals(var21.column)) {
               var14 = false;
               break;
            }

            var22 = var27[var31];
            if (var22 != var21.sortType) {
               var14 = false;
               break;
            }

            ++var25;
         }

         if (var14) {
            var24 = (long)(100 - var25);
         }
      }

      if (!var7 && var8 != null) {
         var14 = false;
         ArrayList var26 = var8.get(this.getTable());
         if (var26 != null) {
            var16 = this.table.getMainIndexColumn();
            Iterator var30 = var26.iterator();

            label101:
            while(true) {
               Column var32;
               do {
                  do {
                     if (!var30.hasNext()) {
                        break label101;
                     }

                     var32 = (Column)var30.next();
                     var19 = var32.getColumnId();
                  } while(var19 == -1);
               } while(var19 == var16);

               Column[] var33 = this.columns;
               int var34 = var33.length;

               for(var22 = 0; var22 < var34; ++var22) {
                  Column var23 = var33[var22];
                  if (var32 == var23) {
                     continue label101;
                  }
               }

               var14 = true;
               break;
            }
         }
      } else {
         var14 = true;
      }

      long var28;
      if (var7) {
         var28 = var10 + var24 + 20L;
      } else if (var14) {
         var28 = var10 + var10 + var24 + 20L;
      } else {
         var28 = var10 + var24 + (long)this.columns.length;
      }

      return var28;
   }

   public final boolean mayHaveNullDuplicates(SearchRow var1) {
      int var2;
      int var3;
      switch (this.database.getMode().uniqueIndexNullsHandling) {
         case ALLOW_DUPLICATES_WITH_ANY_NULL:
            for(var2 = 0; var2 < this.uniqueColumnColumn; ++var2) {
               var3 = this.columnIds[var2];
               if (var1.getValue(var3) == ValueNull.INSTANCE) {
                  return true;
               }
            }

            return false;
         case ALLOW_DUPLICATES_WITH_ALL_NULLS:
            for(var2 = 0; var2 < this.uniqueColumnColumn; ++var2) {
               var3 = this.columnIds[var2];
               if (var1.getValue(var3) != ValueNull.INSTANCE) {
                  return false;
               }
            }

            return true;
         default:
            return false;
      }
   }

   public RowFactory getRowFactory() {
      return this.rowFactory;
   }

   public RowFactory getUniqueRowFactory() {
      return this.uniqueRowFactory;
   }
}
