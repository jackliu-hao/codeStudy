package org.h2.table;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.index.Index;
import org.h2.index.RangeIndex;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.value.TypeInfo;

public class RangeTable extends VirtualTable {
   public static final String NAME = "SYSTEM_RANGE";
   public static final String ALIAS = "GENERATE_SERIES";
   private Expression min;
   private Expression max;
   private Expression step;
   private boolean optimized;
   private final RangeIndex index;

   public RangeTable(Schema var1, Expression var2, Expression var3) {
      super(var1, 0, "SYSTEM_RANGE");
      this.min = var2;
      this.max = var3;
      Column[] var4 = new Column[]{new Column("X", TypeInfo.TYPE_BIGINT)};
      this.setColumns(var4);
      this.index = new RangeIndex(this, IndexColumn.wrap(var4));
   }

   public RangeTable(Schema var1, Expression var2, Expression var3, Expression var4) {
      this(var1, var2, var3);
      this.step = var4;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append("SYSTEM_RANGE").append('(');
      this.min.getUnenclosedSQL(var1, var2).append(", ");
      this.max.getUnenclosedSQL(var1, var2);
      if (this.step != null) {
         this.step.getUnenclosedSQL(var1.append(", "), var2);
      }

      return var1.append(')');
   }

   public boolean canGetRowCount(SessionLocal var1) {
      return true;
   }

   public long getRowCount(SessionLocal var1) {
      long var2 = this.getStep(var1);
      if (var2 == 0L) {
         throw DbException.get(90142);
      } else {
         long var4 = this.getMax(var1) - this.getMin(var1);
         if (var2 > 0L) {
            if (var4 < 0L) {
               return 0L;
            }
         } else if (var4 > 0L) {
            return 0L;
         }

         return var4 / var2 + 1L;
      }
   }

   public TableType getTableType() {
      return TableType.SYSTEM_TABLE;
   }

   public Index getScanIndex(SessionLocal var1) {
      return this.index;
   }

   public ArrayList<Index> getIndexes() {
      ArrayList var1 = new ArrayList(2);
      var1.add(this.index);
      var1.add(this.index);
      return var1;
   }

   public long getMin(SessionLocal var1) {
      this.optimize(var1);
      return this.min.getValue(var1).getLong();
   }

   public long getMax(SessionLocal var1) {
      this.optimize(var1);
      return this.max.getValue(var1).getLong();
   }

   public long getStep(SessionLocal var1) {
      this.optimize(var1);
      return this.step == null ? 1L : this.step.getValue(var1).getLong();
   }

   private void optimize(SessionLocal var1) {
      if (!this.optimized) {
         this.min = this.min.optimize(var1);
         this.max = this.max.optimize(var1);
         if (this.step != null) {
            this.step = this.step.optimize(var1);
         }

         this.optimized = true;
      }

   }

   public long getMaxDataModificationId() {
      return 0L;
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return 100L;
   }

   public boolean isDeterministic() {
      return true;
   }
}
