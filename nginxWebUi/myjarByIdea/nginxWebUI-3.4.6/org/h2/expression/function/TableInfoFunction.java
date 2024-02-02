package org.h2.expression.function;

import java.util.ArrayList;
import org.h2.command.Parser;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVSpatialIndex;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueNull;

public final class TableInfoFunction extends Function1_2 {
   public static final int DISK_SPACE_USED = 0;
   public static final int ESTIMATED_ENVELOPE = 1;
   private static final String[] NAMES = new String[]{"DISK_SPACE_USED", "ESTIMATED_ENVELOPE"};
   private final int function;

   public TableInfoFunction(Expression var1, Expression var2, int var3) {
      super(var1, var2);
      this.function = var3;
   }

   public Value getValue(SessionLocal var1, Value var2, Value var3) {
      Table var4 = (new Parser(var1)).parseTableName(var2.getString());
      Object var10;
      switch (this.function) {
         case 0:
            var10 = ValueBigint.get(var4.getDiskSpaceUsed());
            break;
         case 1:
            Column var5 = var4.getColumn(var3.getString());
            ArrayList var6 = var4.getIndexes();
            if (var6 != null) {
               int var7 = 1;

               for(int var8 = var6.size(); var7 < var8; ++var7) {
                  Index var9 = (Index)var6.get(var7);
                  if (var9 instanceof MVSpatialIndex && var9.isFirstColumn(var5)) {
                     var10 = ((MVSpatialIndex)var9).getEstimatedBounds(var1);
                     return (Value)var10;
                  }
               }
            }

            var10 = ValueNull.INSTANCE;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return (Value)var10;
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      if (this.right != null) {
         this.right = this.right.optimize(var1);
      }

      switch (this.function) {
         case 0:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         case 1:
            this.type = TypeInfo.TYPE_GEOMETRY;
            break;
         default:
            throw DbException.getInternalError("function=" + this.function);
      }

      return this;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      switch (var1.getType()) {
         case 2:
            return false;
         default:
            return super.isEverything(var1);
      }
   }

   public String getName() {
      return NAMES[this.function];
   }
}
