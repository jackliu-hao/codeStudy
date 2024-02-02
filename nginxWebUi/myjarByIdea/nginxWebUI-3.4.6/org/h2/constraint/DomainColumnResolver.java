package org.h2.constraint;

import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class DomainColumnResolver implements ColumnResolver {
   private final Column column;
   private Value value;
   private String name;

   public DomainColumnResolver(TypeInfo var1) {
      this.column = new Column("VALUE", var1);
   }

   public void setValue(Value var1) {
      this.value = var1;
   }

   public Value getValue(Column var1) {
      return this.value;
   }

   public Column[] getColumns() {
      return new Column[]{this.column};
   }

   public Column findColumn(String var1) {
      return null;
   }

   void setColumnName(String var1) {
      this.name = var1;
   }

   void resetColumnName() {
      this.name = null;
   }

   public String getColumnName() {
      return this.name;
   }

   public TypeInfo getValueType() {
      return this.column.getType();
   }
}
