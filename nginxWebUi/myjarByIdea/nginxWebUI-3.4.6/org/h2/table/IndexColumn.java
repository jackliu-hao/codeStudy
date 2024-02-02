package org.h2.table;

import org.h2.result.SortOrder;
import org.h2.util.ParserUtil;

public class IndexColumn {
   public static final int SQL_NO_ORDER = Integer.MIN_VALUE;
   public final String columnName;
   public Column column;
   public int sortType = 0;

   public static StringBuilder writeColumns(StringBuilder var0, IndexColumn[] var1, int var2) {
      return writeColumns(var0, var1, 0, var1.length, var2);
   }

   public static StringBuilder writeColumns(StringBuilder var0, IndexColumn[] var1, int var2, int var3, int var4) {
      for(int var5 = var2; var5 < var3; ++var5) {
         if (var5 > var2) {
            var0.append(", ");
         }

         var1[var5].getSQL(var0, var4);
      }

      return var0;
   }

   public static StringBuilder writeColumns(StringBuilder var0, IndexColumn[] var1, String var2, String var3, int var4) {
      int var5 = 0;

      for(int var6 = var1.length; var5 < var6; ++var5) {
         if (var5 > 0) {
            var0.append(var2);
         }

         var1[var5].getSQL(var0, var4).append(var3);
      }

      return var0;
   }

   public IndexColumn(String var1) {
      this.columnName = var1;
   }

   public IndexColumn(String var1, int var2) {
      this.columnName = var1;
      this.sortType = var2;
   }

   public IndexColumn(Column var1) {
      this.columnName = null;
      this.column = var1;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if (this.column != null) {
         this.column.getSQL(var1, var2);
      } else {
         ParserUtil.quoteIdentifier(var1, this.columnName, var2);
      }

      if ((var2 & Integer.MIN_VALUE) == 0) {
         SortOrder.typeToString(var1, this.sortType);
      }

      return var1;
   }

   public static IndexColumn[] wrap(Column[] var0) {
      IndexColumn[] var1 = new IndexColumn[var0.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = new IndexColumn(var0[var2]);
      }

      return var1;
   }

   public static void mapColumns(IndexColumn[] var0, Table var1) {
      IndexColumn[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         IndexColumn var5 = var2[var4];
         var5.column = var1.getColumn(var5.columnName);
      }

   }

   public String toString() {
      return this.getSQL(new StringBuilder("IndexColumn "), 3).toString();
   }
}
