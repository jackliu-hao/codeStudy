package org.h2.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.h2.command.query.Query;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.table.Column;
import org.h2.table.TableType;
import org.h2.value.Value;

public class IndexCondition {
   public static final int EQUALITY = 1;
   public static final int START = 2;
   public static final int END = 4;
   public static final int RANGE = 6;
   public static final int ALWAYS_FALSE = 8;
   public static final int SPATIAL_INTERSECTS = 16;
   private final Column column;
   private final int compareType;
   private final Expression expression;
   private List<Expression> expressionList;
   private Query expressionQuery;

   private IndexCondition(int var1, ExpressionColumn var2, Expression var3) {
      this.compareType = var1;
      this.column = var2 == null ? null : var2.getColumn();
      this.expression = var3;
   }

   public static IndexCondition get(int var0, ExpressionColumn var1, Expression var2) {
      return new IndexCondition(var0, var1, var2);
   }

   public static IndexCondition getInList(ExpressionColumn var0, List<Expression> var1) {
      IndexCondition var2 = new IndexCondition(10, var0, (Expression)null);
      var2.expressionList = var1;
      return var2;
   }

   public static IndexCondition getInQuery(ExpressionColumn var0, Query var1) {
      assert var1.isRandomAccessResult();

      IndexCondition var2 = new IndexCondition(11, var0, (Expression)null);
      var2.expressionQuery = var1;
      return var2;
   }

   public Value getCurrentValue(SessionLocal var1) {
      return this.expression.getValue(var1);
   }

   public Value[] getCurrentValueList(SessionLocal var1) {
      TreeSet var2 = new TreeSet(var1.getDatabase().getCompareMode());
      Iterator var3 = this.expressionList.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         Value var5 = var4.getValue(var1);
         var5 = this.column.convert(var1, var5);
         var2.add(var5);
      }

      Value[] var6 = (Value[])var2.toArray(new Value[var2.size()]);
      Arrays.sort(var6, var1.getDatabase().getCompareMode());
      return var6;
   }

   public ResultInterface getCurrentResult() {
      return this.expressionQuery.query(0L);
   }

   public String getSQL(int var1) {
      if (this.compareType == 9) {
         return "FALSE";
      } else {
         StringBuilder var2 = new StringBuilder();
         this.column.getSQL(var2, var1);
         switch (this.compareType) {
            case 0:
               var2.append(" = ");
               break;
            case 1:
            case 7:
            case 9:
            default:
               throw DbException.getInternalError("type=" + this.compareType);
            case 2:
               var2.append(" < ");
               break;
            case 3:
               var2.append(" > ");
               break;
            case 4:
               var2.append(" <= ");
               break;
            case 5:
               var2.append(" >= ");
               break;
            case 6:
               var2.append(!this.expression.isNullConstant() && (this.column.getType().getValueType() != 8 || !this.expression.isConstant()) ? " IS NOT DISTINCT FROM " : " IS ");
               break;
            case 8:
               var2.append(" && ");
               break;
            case 10:
               Expression.writeExpressions(var2.append(" IN("), this.expressionList, var1).append(')');
               break;
            case 11:
               var2.append(" IN(");
               var2.append(this.expressionQuery.getPlanSQL(var1));
               var2.append(')');
         }

         if (this.expression != null) {
            this.expression.getSQL(var2, var1, 0);
         }

         return var2.toString();
      }
   }

   public int getMask(ArrayList<IndexCondition> var1) {
      switch (this.compareType) {
         case 0:
         case 6:
            return 1;
         case 1:
         case 7:
         default:
            throw DbException.getInternalError("type=" + this.compareType);
         case 2:
         case 4:
            return 4;
         case 3:
         case 5:
            return 2;
         case 8:
            return 16;
         case 9:
            return 8;
         case 10:
         case 11:
            return var1.size() > 1 && TableType.TABLE != this.column.getTable().getTableType() ? 0 : 1;
      }
   }

   public boolean isAlwaysFalse() {
      return this.compareType == 9;
   }

   public boolean isStart() {
      switch (this.compareType) {
         case 0:
         case 3:
         case 5:
         case 6:
            return true;
         case 1:
         case 2:
         case 4:
         default:
            return false;
      }
   }

   public boolean isEnd() {
      switch (this.compareType) {
         case 0:
         case 2:
         case 4:
         case 6:
            return true;
         case 1:
         case 3:
         case 5:
         default:
            return false;
      }
   }

   public boolean isSpatialIntersects() {
      switch (this.compareType) {
         case 8:
            return true;
         default:
            return false;
      }
   }

   public int getCompareType() {
      return this.compareType;
   }

   public Column getColumn() {
      return this.column;
   }

   public Expression getExpression() {
      return this.expression;
   }

   public List<Expression> getExpressionList() {
      return this.expressionList;
   }

   public Query getExpressionQuery() {
      return this.expressionQuery;
   }

   public boolean isEvaluatable() {
      if (this.expression != null) {
         return this.expression.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
      } else if (this.expressionList != null) {
         Iterator var1 = this.expressionList.iterator();

         Expression var2;
         do {
            if (!var1.hasNext()) {
               return true;
            }

            var2 = (Expression)var1.next();
         } while(var2.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR));

         return false;
      } else {
         return this.expressionQuery.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR);
      }
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder("column=")).append(this.column).append(", compareType=");
      return compareTypeToString(var1, this.compareType).append(", expression=").append(this.expression).append(", expressionList=").append(this.expressionList).append(", expressionQuery=").append(this.expressionQuery).toString();
   }

   private static StringBuilder compareTypeToString(StringBuilder var0, int var1) {
      boolean var2 = false;
      if ((var1 & 1) == 1) {
         var2 = true;
         var0.append("EQUALITY");
      }

      if ((var1 & 2) == 2) {
         if (var2) {
            var0.append(", ");
         }

         var2 = true;
         var0.append("START");
      }

      if ((var1 & 4) == 4) {
         if (var2) {
            var0.append(", ");
         }

         var2 = true;
         var0.append("END");
      }

      if ((var1 & 8) == 8) {
         if (var2) {
            var0.append(", ");
         }

         var2 = true;
         var0.append("ALWAYS_FALSE");
      }

      if ((var1 & 16) == 16) {
         if (var2) {
            var0.append(", ");
         }

         var0.append("SPATIAL_INTERSECTS");
      }

      return var0;
   }
}
