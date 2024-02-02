package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.util.StringUtils;
import org.h2.value.ExtTypeInfo;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarchar;

public final class TrimFunction extends Function1_2 {
   public static final int LEADING = 1;
   public static final int TRAILING = 2;
   private int flags;

   public TrimFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.flags = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      return ValueVarchar.get(StringUtils.trim(var2.getString(), (this.flags & 1) != 0, (this.flags & 2) != 0, var3 != null ? var3.getString() : " "), var1);
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      this.type = TypeInfo.getTypeInfo(2, this.left.getType().getPrecision(), 0, (ExtTypeInfo)null);
      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.getName()).append('(');
      boolean var3 = false;
      switch (this.flags) {
         case 1:
            var1.append("LEADING ");
            var3 = true;
            break;
         case 2:
            var1.append("TRAILING ");
            var3 = true;
      }

      if (this.right != null) {
         this.right.getUnenclosedSQL(var1, var2);
         var3 = true;
      }

      if (var3) {
         var1.append(" FROM ");
      }

      return this.left.getUnenclosedSQL(var1, var2).append(')');
   }

   public String getName() {
      return "TRIM";
   }
}
