package org.h2.command.dml;

import org.h2.command.Prepared;
import org.h2.engine.SessionLocal;
import org.h2.expression.Alias;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.function.table.TableFunction;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.value.Value;

public class Call extends Prepared {
   private Expression expression;
   private TableFunction tableFunction;
   private Expression[] expressions;

   public Call(SessionLocal var1) {
      super(var1);
   }

   public ResultInterface queryMeta() {
      int var1 = this.expressions.length;
      LocalResult var2 = new LocalResult(this.session, this.expressions, var1, var1);
      var2.done();
      return var2;
   }

   public long update() {
      if (this.tableFunction != null) {
         return super.update();
      } else {
         Value var1 = this.expression.getValue(this.session);
         int var2 = var1.getValueType();
         switch (var2) {
            case -1:
            case 0:
               return 0L;
            default:
               return (long)var1.getInt();
         }
      }
   }

   public ResultInterface query(long var1) {
      this.setCurrentRowNumber(1L);
      if (this.tableFunction != null) {
         return this.tableFunction.getValue(this.session);
      } else {
         LocalResult var3 = new LocalResult(this.session, this.expressions, 1, 1);
         var3.addRow(this.expression.getValue(this.session));
         var3.done();
         return var3;
      }
   }

   public void prepare() {
      if (this.tableFunction != null) {
         this.prepareAlways = true;
         this.tableFunction.optimize(this.session);
         ResultInterface var1 = this.tableFunction.getValueTemplate(this.session);
         int var2 = var1.getVisibleColumnCount();
         this.expressions = new Expression[var2];

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = var1.getColumnName(var3);
            String var5 = var1.getAlias(var3);
            Object var6 = new ExpressionColumn(this.session.getDatabase(), new Column(var4, var1.getColumnType(var3)));
            if (!var5.equals(var4)) {
               var6 = new Alias((Expression)var6, var5, false);
            }

            this.expressions[var3] = (Expression)var6;
         }
      } else {
         this.expressions = new Expression[]{this.expression = this.expression.optimize(this.session)};
      }

   }

   public void setExpression(Expression var1) {
      this.expression = var1;
   }

   public void setTableFunction(TableFunction var1) {
      this.tableFunction = var1;
   }

   public boolean isQuery() {
      return true;
   }

   public boolean isTransactional() {
      return true;
   }

   public boolean isReadOnly() {
      return this.tableFunction == null && this.expression.isEverything(ExpressionVisitor.READONLY_VISITOR);
   }

   public int getType() {
      return 57;
   }

   public boolean isCacheable() {
      return this.tableFunction == null;
   }
}
