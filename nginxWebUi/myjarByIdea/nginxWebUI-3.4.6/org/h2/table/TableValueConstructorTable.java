package org.h2.table;

import java.util.ArrayList;
import org.h2.command.query.TableValueConstructor;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.schema.Schema;

public class TableValueConstructorTable extends VirtualConstructedTable {
   private final ArrayList<ArrayList<Expression>> rows;

   public TableValueConstructorTable(Schema var1, SessionLocal var2, Column[] var3, ArrayList<ArrayList<Expression>> var4) {
      super(var1, 0, "VALUES");
      this.setColumns(var3);
      this.rows = var4;
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return true;
   }

   public long getRowCount(SessionLocal var1) {
      return (long)this.rows.size();
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return (long)this.rows.size();
   }

   public ResultInterface getResult(SessionLocal var1) {
      SimpleResult var2 = new SimpleResult();
      int var3 = this.columns.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Column var5 = this.columns[var4];
         var2.addColumn(var5.getName(), var5.getType());
      }

      TableValueConstructor.getVisibleResult(var1, var2, this.columns, this.rows);
      return var2;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append('(');
      TableValueConstructor.getValuesSQL(var1, var2, this.rows);
      return var1.append(')');
   }

   public boolean isDeterministic() {
      return true;
   }
}
