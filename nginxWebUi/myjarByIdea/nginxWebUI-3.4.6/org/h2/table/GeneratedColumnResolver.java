package org.h2.table;

import java.util.HashMap;
import org.h2.result.Row;
import org.h2.value.Value;
import org.h2.value.ValueBigint;

class GeneratedColumnResolver implements ColumnResolver {
   private final Table table;
   private Column[] columns;
   private HashMap<String, Column> columnMap;
   private Row current;

   GeneratedColumnResolver(Table var1) {
      this.table = var1;
   }

   void set(Row var1) {
      this.current = var1;
   }

   public Column[] getColumns() {
      Column[] var1 = this.columns;
      if (var1 == null) {
         this.columns = var1 = this.createColumns();
      }

      return var1;
   }

   private Column[] createColumns() {
      Column[] var1 = this.table.getColumns();
      int var2 = var1.length;
      int var3 = var2;

      for(int var4 = 0; var4 < var2; ++var4) {
         if (var1[var4].isGenerated()) {
            --var3;
         }
      }

      Column[] var8 = new Column[var3];
      int var5 = 0;

      for(int var6 = 0; var5 < var2; ++var5) {
         Column var7 = var1[var5];
         if (!var7.isGenerated()) {
            var8[var6++] = var7;
         }
      }

      return var8;
   }

   public Column findColumn(String var1) {
      HashMap var2 = this.columnMap;
      if (var2 == null) {
         var2 = this.table.getDatabase().newStringMap();
         Column[] var3 = this.getColumns();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Column var6 = var3[var5];
            var2.put(var6.getName(), var6);
         }

         this.columnMap = var2;
      }

      return (Column)var2.get(var1);
   }

   public Value getValue(Column var1) {
      int var2 = var1.getColumnId();
      return (Value)(var2 == -1 ? ValueBigint.get(this.current.getKey()) : this.current.getValue(var2));
   }

   public Column getRowIdColumn() {
      return this.table.getRowIdColumn();
   }
}
