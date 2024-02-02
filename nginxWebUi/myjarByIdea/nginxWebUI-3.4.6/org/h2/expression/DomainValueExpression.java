package org.h2.expression;

import org.h2.constraint.DomainColumnResolver;
import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.util.ParserUtil;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class DomainValueExpression extends Operation0 {
   private DomainColumnResolver columnResolver;

   public Value getValue(SessionLocal var1) {
      return this.columnResolver.getValue((Column)null);
   }

   public TypeInfo getType() {
      return this.columnResolver.getValueType();
   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      if (var1 instanceof DomainColumnResolver) {
         this.columnResolver = (DomainColumnResolver)var1;
      }

   }

   public Expression optimize(SessionLocal var1) {
      if (this.columnResolver == null) {
         throw DbException.get(42122, (String)"VALUE");
      } else {
         return this;
      }
   }

   public boolean isValueSet() {
      return this.columnResolver.getValue((Column)null) != null;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      if (this.columnResolver != null) {
         String var3 = this.columnResolver.getColumnName();
         if (var3 != null) {
            return ParserUtil.quoteIdentifier(var1, var3, var2);
         }
      }

      return var1.append("VALUE");
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return true;
   }

   public int getCost() {
      return 1;
   }
}
