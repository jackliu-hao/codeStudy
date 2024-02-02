package org.h2.schema;

import org.h2.engine.SessionLocal;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.Table;
import org.h2.value.Value;

public final class Constant extends SchemaObject {
   private Value value;
   private ValueExpression expression;

   public Constant(Schema var1, int var2, String var3) {
      super(var1, var2, var3, 8);
   }

   public String getCreateSQLForCopy(Table var1, String var2) {
      throw DbException.getInternalError(this.toString());
   }

   public String getCreateSQL() {
      StringBuilder var1 = new StringBuilder("CREATE CONSTANT ");
      this.getSQL(var1, 0).append(" VALUE ");
      return this.value.getSQL(var1, 0).toString();
   }

   public int getType() {
      return 11;
   }

   public void removeChildrenAndResources(SessionLocal var1) {
      this.database.removeMeta(var1, this.getId());
      this.invalidate();
   }

   public void setValue(Value var1) {
      this.value = var1;
      this.expression = ValueExpression.get(var1);
   }

   public ValueExpression getValue() {
      return this.expression;
   }
}
