package org.h2.table;

import org.h2.command.query.Select;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.value.Value;

public interface ColumnResolver {
   default String getTableAlias() {
      return null;
   }

   Column[] getColumns();

   Column findColumn(String var1);

   default String getColumnName(Column var1) {
      return var1.getName();
   }

   default boolean hasDerivedColumnList() {
      return false;
   }

   default Column[] getSystemColumns() {
      return null;
   }

   default Column getRowIdColumn() {
      return null;
   }

   default String getSchemaName() {
      return null;
   }

   Value getValue(Column var1);

   default TableFilter getTableFilter() {
      return null;
   }

   default Select getSelect() {
      return null;
   }

   default Expression optimize(ExpressionColumn var1, Column var2) {
      return var1;
   }
}
