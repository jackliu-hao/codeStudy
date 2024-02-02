package org.h2.table;

import org.h2.engine.SessionLocal;
import org.h2.expression.function.table.TableFunction;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;

public class FunctionTable extends VirtualConstructedTable {
   private final TableFunction function;

   public FunctionTable(Schema var1, SessionLocal var2, TableFunction var3) {
      super(var1, 0, var3.getName());
      this.function = var3;
      var3.optimize(var2);
      ResultInterface var4 = var3.getValueTemplate(var2);
      int var5 = var4.getVisibleColumnCount();
      Column[] var6 = new Column[var5];

      for(int var7 = 0; var7 < var5; ++var7) {
         var6[var7] = new Column(var4.getColumnName(var7), var4.getColumnType(var7));
      }

      this.setColumns(var6);
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
      return this.function.getValue(var1);
   }

   public String getSQL(int var1) {
      return this.function.getSQL(var1);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      return var1.append(this.function.getSQL(var2));
   }

   public boolean isDeterministic() {
      return this.function.isDeterministic();
   }
}
