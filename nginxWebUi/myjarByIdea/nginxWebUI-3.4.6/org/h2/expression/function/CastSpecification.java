package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.schema.Domain;
import org.h2.table.Column;
import org.h2.util.HasSQL;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class CastSpecification extends Function1 {
   private Domain domain;

   public CastSpecification(Expression var1, Column var2) {
      super(var1);
      this.type = var2.getType();
      this.domain = var2.getDomain();
   }

   public CastSpecification(Expression var1, TypeInfo var2) {
      super(var1);
      this.type = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1).castTo(this.type, var1);
      if (this.domain != null) {
         this.domain.checkConstraints(var1, var2);
      }

      return var2;
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      if (this.arg.isConstant()) {
         Value var2 = this.getValue(var1);
         if (var2 == ValueNull.INSTANCE || canOptimizeCast(this.arg.getType().getValueType(), this.type.getValueType())) {
            return TypedValueExpression.get(var2, this.type);
         }
      }

      return this;
   }

   public boolean isConstant() {
      return this.arg instanceof ValueExpression && canOptimizeCast(this.arg.getType().getValueType(), this.type.getValueType());
   }

   private static boolean canOptimizeCast(int var0, int var1) {
      switch (var0) {
         case 17:
            if (var1 == 21) {
               return false;
            }
            break;
         case 18:
            switch (var1) {
               case 19:
               case 20:
               case 21:
                  return false;
               default:
                  return true;
            }
         case 19:
            switch (var1) {
               case 18:
               case 20:
               case 21:
                  return false;
               case 19:
               default:
                  return true;
            }
         case 20:
            switch (var1) {
               case 19:
               case 21:
                  return false;
               default:
                  return true;
            }
         case 21:
            switch (var1) {
               case 17:
               case 18:
               case 20:
                  return false;
               case 19:
            }
      }

      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append("CAST(");
      this.arg.getUnenclosedSQL(var1, this.arg instanceof ValueExpression ? var2 | 4 : var2).append(" AS ");
      return ((HasSQL)(this.domain != null ? this.domain : this.type)).getSQL(var1, var2).append(')');
   }

   public String getName() {
      return "CAST";
   }
}
