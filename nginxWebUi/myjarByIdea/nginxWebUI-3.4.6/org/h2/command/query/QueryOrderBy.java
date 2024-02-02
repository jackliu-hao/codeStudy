package org.h2.command.query;

import org.h2.expression.Expression;
import org.h2.result.SortOrder;

public class QueryOrderBy {
   public Expression expression;
   public Expression columnIndexExpr;
   public int sortType;

   public void getSQL(StringBuilder var1, int var2) {
      (this.expression != null ? this.expression : this.columnIndexExpr).getUnenclosedSQL(var1, var2);
      SortOrder.typeToString(var1, this.sortType);
   }
}
