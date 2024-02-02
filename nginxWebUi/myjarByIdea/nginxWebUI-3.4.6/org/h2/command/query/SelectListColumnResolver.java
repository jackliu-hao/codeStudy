package org.h2.command.query;

import java.util.ArrayList;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class SelectListColumnResolver implements ColumnResolver {
   private final Select select;
   private final Expression[] expressions;
   private final Column[] columns;

   SelectListColumnResolver(Select var1) {
      this.select = var1;
      int var2 = var1.getColumnCount();
      this.columns = new Column[var2];
      this.expressions = new Expression[var2];
      ArrayList var3 = var1.getExpressions();
      SessionLocal var4 = var1.getSession();

      for(int var5 = 0; var5 < var2; ++var5) {
         Expression var6 = (Expression)var3.get(var5);
         this.columns[var5] = new Column(var6.getAlias(var4, var5), TypeInfo.TYPE_NULL, (Table)null, var5);
         this.expressions[var5] = var6.getNonAliasExpression();
      }

   }

   public Column[] getColumns() {
      return this.columns;
   }

   public Column findColumn(String var1) {
      Database var2 = this.select.getSession().getDatabase();
      Column[] var3 = this.columns;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Column var6 = var3[var5];
         if (var2.equalsIdentifiers(var6.getName(), var1)) {
            return var6;
         }
      }

      return null;
   }

   public Select getSelect() {
      return this.select;
   }

   public Value getValue(Column var1) {
      return null;
   }

   public Expression optimize(ExpressionColumn var1, Column var2) {
      return this.expressions[var2.getColumnId()];
   }
}
