package org.h2.expression.function;

import java.util.regex.Pattern;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public final class SignalFunction extends Function2 {
   private static final Pattern SIGNAL_PATTERN = Pattern.compile("[0-9A-Z]{5}");

   public SignalFunction(Expression var1, Expression var2) {
      super(var1, var2);
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      String var4 = var2.getString();
      if (!var4.startsWith("00") && SIGNAL_PATTERN.matcher(var4).matches()) {
         throw DbException.fromUser(var4, var3.getString());
      } else {
         throw DbException.getInvalidValueException("SQLSTATE", var4);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      this.type = TypeInfo.TYPE_NULL;
      return this;
   }

   public String getName() {
      return "SIGNAL";
   }
}
