package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;

public class ExecuteImmediate extends Prepared {
   private Expression statement;

   public ExecuteImmediate(SessionLocal var1, Expression var2) {
      super(var1);
      this.statement = var2.optimize(var1);
   }

   public long update() {
      String var1 = this.statement.getValue(this.session).getString();
      if (var1 == null) {
         throw DbException.getInvalidValueException("SQL command", (Object)null);
      } else {
         Prepared var2 = this.session.prepare(var1);
         if (var2.isQuery()) {
            throw DbException.get(42001, (String[])(var1, "<not a query>"));
         } else {
            return var2.update();
         }
      }
   }

   public boolean isTransactional() {
      return true;
   }

   public int getType() {
      return 91;
   }

   public ResultInterface queryMeta() {
      return null;
   }
}
