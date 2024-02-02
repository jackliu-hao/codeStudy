package org.h2.command.dml;

import java.util.ArrayList;
import org.h2.command.Prepared;
import org.h2.engine.Procedure;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.Parameter;
import org.h2.result.ResultInterface;
import org.h2.util.Utils;

public class ExecuteProcedure extends Prepared {
   private final ArrayList<Expression> expressions = Utils.newSmallArrayList();
   private Procedure procedure;

   public ExecuteProcedure(SessionLocal var1) {
      super(var1);
   }

   public void setProcedure(Procedure var1) {
      this.procedure = var1;
   }

   public void setExpression(int var1, Expression var2) {
      this.expressions.add(var1, var2);
   }

   private void setParameters() {
      Prepared var1 = this.procedure.getPrepared();
      ArrayList var2 = var1.getParameters();

      for(int var3 = 0; var2 != null && var3 < var2.size() && var3 < this.expressions.size(); ++var3) {
         Expression var4 = (Expression)this.expressions.get(var3);
         Parameter var5 = (Parameter)var2.get(var3);
         var5.setValue(var4.getValue(this.session));
      }

   }

   public boolean isQuery() {
      Prepared var1 = this.procedure.getPrepared();
      return var1.isQuery();
   }

   public long update() {
      this.setParameters();
      Prepared var1 = this.procedure.getPrepared();
      return var1.update();
   }

   public ResultInterface query(long var1) {
      this.setParameters();
      Prepared var3 = this.procedure.getPrepared();
      return var3.query(var1);
   }

   public boolean isTransactional() {
      return true;
   }

   public ResultInterface queryMeta() {
      Prepared var1 = this.procedure.getPrepared();
      return var1.queryMeta();
   }

   public int getType() {
      return 59;
   }
}
