package org.h2.expression.condition;

import java.util.ArrayList;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.Parameter;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.expression.aggregate.Aggregate;
import org.h2.expression.aggregate.AggregateType;
import org.h2.index.IndexCondition;
import org.h2.message.DbException;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public final class Comparison extends Condition {
   public static final int EQUAL = 0;
   public static final int NOT_EQUAL = 1;
   public static final int SMALLER = 2;
   public static final int BIGGER = 3;
   public static final int SMALLER_EQUAL = 4;
   public static final int BIGGER_EQUAL = 5;
   public static final int EQUAL_NULL_SAFE = 6;
   public static final int NOT_EQUAL_NULL_SAFE = 7;
   public static final int SPATIAL_INTERSECTS = 8;
   static final String[] COMPARE_TYPES = new String[]{"=", "<>", "<", ">", "<=", ">=", "IS NOT DISTINCT FROM", "IS DISTINCT FROM", "&&"};
   public static final int FALSE = 9;
   public static final int IN_LIST = 10;
   public static final int IN_QUERY = 11;
   private int compareType;
   private Expression left;
   private Expression right;
   private final boolean whenOperand;

   public Comparison(int var1, Expression var2, Expression var3, boolean var4) {
      this.left = var2;
      this.right = var3;
      this.compareType = var1;
      this.whenOperand = var4;
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      return this.getWhenSQL(this.left.getSQL(var1, var2, 0), var2);
   }

   public StringBuilder getWhenSQL(StringBuilder var1, int var2) {
      var1.append(' ').append(COMPARE_TYPES[this.compareType]).append(' ');
      return this.right.getSQL(var1, var2, this.right instanceof Aggregate && ((Aggregate)this.right).getAggregateType() == AggregateType.ANY ? 1 : 0);
   }

   public Expression optimize(SessionLocal var1) {
      TypeInfo var3;
      label97: {
         this.left = this.left.optimize(var1);
         this.right = this.right.optimize(var1);
         TypeInfo var2 = this.left.getType();
         var3 = this.right.getType();
         if (var1.getMode().numericWithBooleanComparison) {
            switch (this.compareType) {
               case 0:
               case 1:
               case 6:
               case 7:
                  int var4 = var2.getValueType();
                  if (var4 == 8) {
                     if (DataType.isNumericType(var3.getValueType())) {
                        break label97;
                     }
                  } else if (DataType.isNumericType(var4) && var3.getValueType() == 8) {
                     break label97;
                  }
               case 2:
               case 3:
               case 4:
               case 5:
            }
         }

         TypeInfo.checkComparable(var2, var3);
      }

      if (this.whenOperand) {
         return this;
      } else {
         Expression var8;
         if (this.right instanceof ExpressionColumn && (this.left.isConstant() || this.left instanceof Parameter)) {
            var8 = this.left;
            this.left = this.right;
            this.right = var8;
            this.compareType = getReversedCompareType(this.compareType);
         }

         if (this.left instanceof ExpressionColumn) {
            if (this.right.isConstant()) {
               Value var9 = this.right.getValue(var1);
               if (var9 == ValueNull.INSTANCE && (this.compareType & -2) != 6) {
                  return TypedValueExpression.UNKNOWN;
               }

               var3 = this.left.getType();
               TypeInfo var11 = var9.getType();
               int var5 = var11.getValueType();
               if (var5 != var3.getValueType() || var5 >= 40) {
                  TypeInfo var6 = TypeInfo.getHigherType(var3, var11);
                  if (var5 != var6.getValueType() || var5 >= 40) {
                     Column var7 = ((ExpressionColumn)this.left).getColumn();
                     this.right = ValueExpression.get(var9.convertTo(var6, var1, var7));
                  }
               }
            } else if (this.right instanceof Parameter) {
               ((Parameter)this.right).setColumn(((ExpressionColumn)this.left).getColumn());
            }
         }

         if (this.left.isConstant() && this.right.isConstant()) {
            return ValueExpression.getBoolean(this.getValue(var1));
         } else {
            if (this.left.isNullConstant() || this.right.isNullConstant()) {
               if ((this.compareType & -2) != 6) {
                  return TypedValueExpression.UNKNOWN;
               }

               var8 = this.left.isNullConstant() ? this.right : this.left;
               int var10 = var8.getType().getValueType();
               if (var10 != -1 && var10 != 41) {
                  return new NullPredicate(var8, this.compareType == 7, false);
               }
            }

            return this;
         }
      }
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      return (Value)(var2 == ValueNull.INSTANCE && (this.compareType & -2) != 6 ? ValueNull.INSTANCE : compare(var1, var2, this.right.getValue(var1), this.compareType));
   }

   public boolean getWhenValue(SessionLocal var1, Value var2) {
      if (!this.whenOperand) {
         return super.getWhenValue(var1, var2);
      } else {
         return var2 == ValueNull.INSTANCE && (this.compareType & -2) != 6 ? false : compare(var1, var2, this.right.getValue(var1), this.compareType).isTrue();
      }
   }

   static Value compare(SessionLocal var0, Value var1, Value var2, int var3) {
      Object var4;
      int var5;
      switch (var3) {
         case 0:
            var5 = var0.compareWithNull(var1, var2, true);
            if (var5 == 0) {
               var4 = ValueBoolean.TRUE;
            } else if (var5 == Integer.MIN_VALUE) {
               var4 = ValueNull.INSTANCE;
            } else {
               var4 = ValueBoolean.FALSE;
            }
            break;
         case 1:
            var5 = var0.compareWithNull(var1, var2, true);
            if (var5 == 0) {
               var4 = ValueBoolean.FALSE;
            } else if (var5 == Integer.MIN_VALUE) {
               var4 = ValueNull.INSTANCE;
            } else {
               var4 = ValueBoolean.TRUE;
            }
            break;
         case 2:
            var5 = var0.compareWithNull(var1, var2, false);
            if (var5 == Integer.MIN_VALUE) {
               var4 = ValueNull.INSTANCE;
            } else {
               var4 = ValueBoolean.get(var5 < 0);
            }
            break;
         case 3:
            var5 = var0.compareWithNull(var1, var2, false);
            if (var5 > 0) {
               var4 = ValueBoolean.TRUE;
            } else if (var5 == Integer.MIN_VALUE) {
               var4 = ValueNull.INSTANCE;
            } else {
               var4 = ValueBoolean.FALSE;
            }
            break;
         case 4:
            var5 = var0.compareWithNull(var1, var2, false);
            if (var5 == Integer.MIN_VALUE) {
               var4 = ValueNull.INSTANCE;
            } else {
               var4 = ValueBoolean.get(var5 <= 0);
            }
            break;
         case 5:
            var5 = var0.compareWithNull(var1, var2, false);
            if (var5 >= 0) {
               var4 = ValueBoolean.TRUE;
            } else if (var5 == Integer.MIN_VALUE) {
               var4 = ValueNull.INSTANCE;
            } else {
               var4 = ValueBoolean.FALSE;
            }
            break;
         case 6:
            var4 = ValueBoolean.get(var0.areEqual(var1, var2));
            break;
         case 7:
            var4 = ValueBoolean.get(!var0.areEqual(var1, var2));
            break;
         case 8:
            if (var1 != ValueNull.INSTANCE && var2 != ValueNull.INSTANCE) {
               var4 = ValueBoolean.get(var1.convertToGeometry((ExtTypeInfoGeometry)null).intersectsBoundingBox(var2.convertToGeometry((ExtTypeInfoGeometry)null)));
            } else {
               var4 = ValueNull.INSTANCE;
            }
            break;
         default:
            throw DbException.getInternalError("type=" + var3);
      }

      return (Value)var4;
   }

   public boolean isWhenConditionOperand() {
      return this.whenOperand;
   }

   private static int getReversedCompareType(int var0) {
      switch (var0) {
         case 0:
         case 1:
         case 6:
         case 7:
         case 8:
            return var0;
         case 2:
            return 3;
         case 3:
            return 2;
         case 4:
            return 5;
         case 5:
            return 4;
         default:
            throw DbException.getInternalError("type=" + var0);
      }
   }

   public Expression getNotIfPossible(SessionLocal var1) {
      if (this.compareType != 8 && !this.whenOperand) {
         int var2 = this.getNotCompareType();
         return new Comparison(var2, this.left, this.right, false);
      } else {
         return null;
      }
   }

   private int getNotCompareType() {
      switch (this.compareType) {
         case 0:
            return 1;
         case 1:
            return 0;
         case 2:
            return 5;
         case 3:
            return 4;
         case 4:
            return 3;
         case 5:
            return 2;
         case 6:
            return 7;
         case 7:
            return 6;
         default:
            throw DbException.getInternalError("type=" + this.compareType);
      }
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (!this.whenOperand) {
         createIndexConditions(var2, this.left, this.right, this.compareType);
      }

   }

   static void createIndexConditions(TableFilter var0, Expression var1, Expression var2, int var3) {
      if (var0.getTable().isQueryComparable()) {
         ExpressionColumn var4 = null;
         if (var1 instanceof ExpressionColumn) {
            var4 = (ExpressionColumn)var1;
            if (var0 != var4.getTableFilter()) {
               var4 = null;
            }
         }

         ExpressionColumn var5 = null;
         if (var2 instanceof ExpressionColumn) {
            var5 = (ExpressionColumn)var2;
            if (var0 != var5.getTableFilter()) {
               var5 = null;
            }
         }

         if (var4 == null != (var5 == null)) {
            if (var4 == null) {
               if (!var1.isEverything(ExpressionVisitor.getNotFromResolverVisitor(var0))) {
                  return;
               }
            } else if (!var2.isEverything(ExpressionVisitor.getNotFromResolverVisitor(var0))) {
               return;
            }

            switch (var3) {
               case 0:
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
               case 8:
                  TypeInfo var6;
                  if (var4 != null) {
                     var6 = var4.getType();
                     if (TypeInfo.haveSameOrdering(var6, TypeInfo.getHigherType(var6, var2.getType()))) {
                        var0.addIndexCondition(IndexCondition.get(var3, var4, var2));
                     }
                  } else {
                     var6 = var5.getType();
                     if (TypeInfo.haveSameOrdering(var6, TypeInfo.getHigherType(var6, var1.getType()))) {
                        var0.addIndexCondition(IndexCondition.get(getReversedCompareType(var3), var5, var1));
                     }
                  }
               case 1:
               case 7:
                  return;
               default:
                  throw DbException.getInternalError("type=" + var3);
            }
         }
      }
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      if (this.right != null) {
         this.right.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      if (this.right != null) {
         this.right.updateAggregate(var1, var2);
      }

   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      this.right.mapColumns(var1, var2, var3);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.right.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost() + this.right.getCost() + 1;
   }

   Expression getIfEquals(Expression var1) {
      if (this.compareType == 0) {
         String var2 = var1.getSQL(0);
         if (this.left.getSQL(0).equals(var2)) {
            return this.right;
         }

         if (this.right.getSQL(0).equals(var2)) {
            return this.left;
         }
      }

      return null;
   }

   Expression getAdditionalAnd(SessionLocal var1, Comparison var2) {
      if (this.compareType == 0 && var2.compareType == 0 && !this.whenOperand) {
         boolean var3 = this.left.isConstant();
         boolean var4 = this.right.isConstant();
         boolean var5 = var2.left.isConstant();
         boolean var6 = var2.right.isConstant();
         String var7 = this.left.getSQL(0);
         String var8 = var2.left.getSQL(0);
         String var9 = this.right.getSQL(0);
         String var10 = var2.right.getSQL(0);
         if ((!var4 || !var6) && var7.equals(var8)) {
            return new Comparison(0, this.right, var2.right, false);
         }

         if ((!var4 || !var5) && var7.equals(var10)) {
            return new Comparison(0, this.right, var2.left, false);
         }

         if ((!var3 || !var6) && var9.equals(var8)) {
            return new Comparison(0, this.left, var2.right, false);
         }

         if ((!var3 || !var5) && var9.equals(var10)) {
            return new Comparison(0, this.left, var2.left, false);
         }
      }

      return null;
   }

   Expression optimizeOr(SessionLocal var1, Comparison var2) {
      if (this.compareType == 0 && var2.compareType == 0) {
         Expression var3 = var2.left;
         Expression var4 = var2.right;
         String var5 = var3.getSQL(0);
         String var6 = var4.getSQL(0);
         String var7;
         if (this.left.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            var7 = this.left.getSQL(0);
            if (var7.equals(var5)) {
               return getConditionIn(this.left, this.right, var4);
            }

            if (var7.equals(var6)) {
               return getConditionIn(this.left, this.right, var3);
            }
         }

         if (this.right.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            var7 = this.right.getSQL(0);
            if (var7.equals(var5)) {
               return getConditionIn(this.right, this.left, var4);
            }

            if (var7.equals(var6)) {
               return getConditionIn(this.right, this.left, var3);
            }
         }
      }

      return null;
   }

   private static ConditionIn getConditionIn(Expression var0, Expression var1, Expression var2) {
      ArrayList var3 = new ArrayList(2);
      var3.add(var1);
      var3.add(var2);
      return new ConditionIn(var0, false, false, var3);
   }

   public int getSubexpressionCount() {
      return 2;
   }

   public Expression getSubexpression(int var1) {
      switch (var1) {
         case 0:
            return this.left;
         case 1:
            return this.right;
         default:
            throw new IndexOutOfBoundsException();
      }
   }
}
