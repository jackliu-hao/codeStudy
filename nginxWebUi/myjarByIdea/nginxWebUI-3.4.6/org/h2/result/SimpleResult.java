package org.h2.result;

import java.util.ArrayList;
import java.util.Comparator;
import org.h2.engine.Session;
import org.h2.util.Utils;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class SimpleResult implements ResultInterface, ResultTarget {
   private final ArrayList<Column> columns;
   private final ArrayList<Value[]> rows;
   private final String schemaName;
   private final String tableName;
   private int rowId;

   public SimpleResult() {
      this("", "");
   }

   public SimpleResult(String var1, String var2) {
      this.columns = Utils.newSmallArrayList();
      this.rows = new ArrayList();
      this.schemaName = var1;
      this.tableName = var2;
      this.rowId = -1;
   }

   private SimpleResult(ArrayList<Column> var1, ArrayList<Value[]> var2, String var3, String var4) {
      this.columns = var1;
      this.rows = var2;
      this.schemaName = var3;
      this.tableName = var4;
      this.rowId = -1;
   }

   public void addColumn(String var1, String var2, int var3, long var4, int var6) {
      this.addColumn(var1, var2, TypeInfo.getTypeInfo(var3, var4, var6, (ExtTypeInfo)null));
   }

   public void addColumn(String var1, TypeInfo var2) {
      this.addColumn(new Column(var1, var1, var2));
   }

   public void addColumn(String var1, String var2, TypeInfo var3) {
      this.addColumn(new Column(var1, var2, var3));
   }

   void addColumn(Column var1) {
      assert this.rows.isEmpty();

      this.columns.add(var1);
   }

   public void addRow(Value... var1) {
      assert var1.length == this.columns.size();

      this.rows.add(var1);
   }

   public void reset() {
      this.rowId = -1;
   }

   public Value[] currentRow() {
      return (Value[])this.rows.get(this.rowId);
   }

   public boolean next() {
      int var1 = this.rows.size();
      if (this.rowId < var1) {
         return ++this.rowId < var1;
      } else {
         return false;
      }
   }

   public long getRowId() {
      return (long)this.rowId;
   }

   public boolean isAfterLast() {
      return this.rowId >= this.rows.size();
   }

   public int getVisibleColumnCount() {
      return this.columns.size();
   }

   public long getRowCount() {
      return (long)this.rows.size();
   }

   public boolean hasNext() {
      return this.rowId < this.rows.size() - 1;
   }

   public boolean needToClose() {
      return false;
   }

   public void close() {
   }

   public String getAlias(int var1) {
      return ((Column)this.columns.get(var1)).alias;
   }

   public String getSchemaName(int var1) {
      return this.schemaName;
   }

   public String getTableName(int var1) {
      return this.tableName;
   }

   public String getColumnName(int var1) {
      return ((Column)this.columns.get(var1)).columnName;
   }

   public TypeInfo getColumnType(int var1) {
      return ((Column)this.columns.get(var1)).columnType;
   }

   public boolean isIdentity(int var1) {
      return false;
   }

   public int getNullable(int var1) {
      return 2;
   }

   public void setFetchSize(int var1) {
   }

   public int getFetchSize() {
      return 1;
   }

   public boolean isLazy() {
      return false;
   }

   public boolean isClosed() {
      return false;
   }

   public SimpleResult createShallowCopy(Session var1) {
      return new SimpleResult(this.columns, this.rows, this.schemaName, this.tableName);
   }

   public void limitsWereApplied() {
   }

   public void sortRows(Comparator<? super Value[]> var1) {
      this.rows.sort(var1);
   }

   static final class Column {
      final String alias;
      final String columnName;
      final TypeInfo columnType;

      Column(String var1, String var2, TypeInfo var3) {
         if (var1 != null && var2 != null) {
            this.alias = var1;
            this.columnName = var2;
            this.columnType = var3;
         } else {
            throw new NullPointerException();
         }
      }

      public int hashCode() {
         int var2 = 1;
         var2 = 31 * var2 + this.alias.hashCode();
         var2 = 31 * var2 + this.columnName.hashCode();
         return var2;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Column var2 = (Column)var1;
            return this.alias.equals(var2.alias) && this.columnName.equals(var2.columnName);
         } else {
            return false;
         }
      }

      public String toString() {
         return this.alias.equals(this.columnName) ? this.columnName : this.columnName + ' ' + this.alias;
      }
   }
}
