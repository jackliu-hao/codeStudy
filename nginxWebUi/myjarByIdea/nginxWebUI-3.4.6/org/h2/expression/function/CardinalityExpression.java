package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.util.MathUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;

public final class CardinalityExpression extends Function1 {
   private final boolean max;

   public CardinalityExpression(Expression var1, boolean var2) {
      super(var1);
      this.max = var2;
   }

   public Value getValue(SessionLocal var1) {
      int var2;
      if (this.max) {
         TypeInfo var3 = this.arg.getType();
         if (var3.getValueType() != 40) {
            throw DbException.getInvalidValueException("array", this.arg.getValue(var1).getTraceSQL());
         }

         var2 = MathUtils.convertLongToInt(var3.getPrecision());
      } else {
         Value var4 = this.arg.getValue(var1);
         if (var4 == ValueNull.INSTANCE) {
            return ValueNull.INSTANCE;
         }

         if (var4.getValueType() != 40) {
            throw DbException.getInvalidValueException("array", var4.getTraceSQL());
         }

         var2 = ((ValueArray)var4).getList().length;
      }

      return ValueInteger.get(var2);
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      this.type = TypeInfo.TYPE_INTEGER;
      return (Expression)(this.arg.isConstant() ? TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type) : this);
   }

   public String getName() {
      return this.max ? "ARRAY_MAX_CARDINALITY" : "CARDINALITY";
   }
}
