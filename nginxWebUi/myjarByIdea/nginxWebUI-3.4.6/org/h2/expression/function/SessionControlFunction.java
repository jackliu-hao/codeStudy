package org.h2.expression.function;

import org.h2.command.Command;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class SessionControlFunction extends Function1 {
   public static final int ABORT_SESSION = 0;
   public static final int CANCEL_SESSION = 1;
   private static final String[] NAMES = new String[]{"ABORT_SESSION", "CANCEL_SESSION"};
   private final int function;

   public SessionControlFunction(Expression var1, int var2) {
      super(var1);
      this.function = var2;
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.arg.getValue(var1);
      if (var2 == ValueNull.INSTANCE) {
         return ValueNull.INSTANCE;
      } else {
         int var3 = var2.getInt();
         var1.getUser().checkAdmin();
         SessionLocal[] var4 = var1.getDatabase().getSessions(false);
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            SessionLocal var7 = var4[var6];
            if (var7.getId() == var3) {
               Command var8 = var7.getCurrentCommand();
               switch (this.function) {
                  case 0:
                     if (var8 != null) {
                        var8.cancel();
                     }

                     var7.close();
                     return ValueBoolean.TRUE;
                  case 1:
                     if (var8 != null) {
                        var8.cancel();
                        return ValueBoolean.TRUE;
                     }

                     return ValueBoolean.FALSE;
                  default:
                     throw DbException.getInternalError("function=" + this.function);
               }
            }
         }

         return ValueBoolean.FALSE;
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.arg = this.arg.optimize(var1);
      this.type = TypeInfo.TYPE_BOOLEAN;
      return this;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
         case 5:
         case 8:
            return false;
         default:
            return super.isEverything(var1);
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
