package org.h2.expression.function;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.TypedValueExpression;
import org.h2.message.DbException;
import org.h2.tools.CompressTool;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueVarbinary;

public final class CompressFunction extends Function1_2 {
   public static final int COMPRESS = 0;
   public static final int EXPAND = 1;
   private static final String[] NAMES = new String[]{"COMPRESS", "EXPAND"};
   private final int function;

   public CompressFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      ValueVarbinary var4;
      switch (this.function) {
         case 0:
            var4 = ValueVarbinary.getNoCopy(CompressTool.getInstance().compress(var2.getBytesNoCopy(), var3 != null ? var3.getString() : null));
            break;
         case 1:
            var4 = ValueVarbinary.getNoCopy(CompressTool.getInstance().expand(var2.getBytesNoCopy()));
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return var4;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      this.type = TypeInfo.TYPE_VARBINARY;
      return (Expression)(!this.left.isConstant() || this.right != null && !this.right.isConstant() ? this : TypedValueExpression.getTypedIfNull(this.getValue(var1), this.type));
   }

   public String getName() {
      return NAMES[this.function];
   }
}
