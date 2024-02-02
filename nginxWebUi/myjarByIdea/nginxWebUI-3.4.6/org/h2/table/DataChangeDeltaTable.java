package org.h2.table;

import org.h2.command.dml.DataChangeStatement;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.result.LocalResult;
import org.h2.result.ResultInterface;
import org.h2.result.ResultTarget;
import org.h2.result.Row;
import org.h2.schema.Schema;

public class DataChangeDeltaTable extends VirtualConstructedTable {
   private final DataChangeStatement statement;
   private final ResultOption resultOption;
   private final Expression[] expressions;

   public static void collectInsertedFinalRow(SessionLocal var0, Table var1, ResultTarget var2, ResultOption var3, Row var4) {
      if (var0.getMode().takeInsertedIdentity) {
         Column var5 = var1.getIdentityColumn();
         if (var5 != null) {
            var0.setLastIdentity(var4.getValue(var5.getColumnId()));
         }
      }

      if (var3 == DataChangeDeltaTable.ResultOption.FINAL) {
         var2.addRow(var4.getValueList());
      }

   }

   public DataChangeDeltaTable(Schema var1, SessionLocal var2, DataChangeStatement var3, ResultOption var4) {
      super(var1, 0, var3.getStatementName());
      this.statement = var3;
      this.resultOption = var4;
      Table var5 = var3.getTable();
      Column[] var6 = var5.getColumns();
      int var7 = var6.length;
      Column[] var8 = new Column[var7];

      for(int var9 = 0; var9 < var7; ++var9) {
         var8[var9] = var6[var9].getClone();
      }

      this.setColumns(var8);
      Expression[] var12 = new Expression[var7];
      String var10 = this.getName();

      for(int var11 = 0; var11 < var7; ++var11) {
         var12[var11] = new ExpressionColumn(this.database, (String)null, var10, var8[var11].getName());
      }

      this.expressions = var12;
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return false;
   }

   public long getRowCount(SessionLocal var1) {
      return Long.MAX_VALUE;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return Long.MAX_VALUE;
   }

   public ResultInterface getResult(SessionLocal var1) {
      this.statement.prepare();
      int var2 = this.expressions.length;
      LocalResult var3 = new LocalResult(var1, this.expressions, var2, var2);
      var3.setForDataChangeDeltaTable();
      this.statement.update(var3, this.resultOption);
      return var3;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append(this.resultOption.name()).append(" TABLE (").append(this.statement.getSQL()).append(')');
   }

   public boolean isDeterministic() {
      return false;
   }

   public static enum ResultOption {
      OLD,
      NEW,
      FINAL;
   }
}
