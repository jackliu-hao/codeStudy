package org.h2.expression.condition;

import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.TypedValueExpression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public class ConditionAndOr extends Condition {
   public static final int AND = 0;
   public static final int OR = 1;
   private final int andOrType;
   private Expression left;
   private Expression right;
   private Expression added;

   public ConditionAndOr(int var1, Expression var2, Expression var3) {
      if (var2 != null && var3 != null) {
         this.andOrType = var1;
         this.left = var2;
         this.right = var3;
      } else {
         throw DbException.getInternalError(var2 + " " + var3);
      }
   }

   int getAndOrType() {
      return this.andOrType;
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      this.left.getSQL(var1, var2, 0);
      switch (this.andOrType) {
         case 0:
            var1.append("\n    AND ");
            break;
         case 1:
            var1.append("\n    OR ");
            break;
         default:
            throw DbException.getInternalError("andOrType=" + this.andOrType);
      }

      return this.right.getSQL(var1, var2, 0);
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (this.andOrType == 0) {
         this.left.createIndexConditions(var1, var2);
         this.right.createIndexConditions(var1, var2);
         if (this.added != null) {
            this.added.createIndexConditions(var1, var2);
         }
      }

   }

   public Expression getNotIfPossible(SessionLocal var1) {
      Object var2 = this.left.getNotIfPossible(var1);
      if (var2 == null) {
         var2 = new ConditionNot(this.left);
      }

      Object var3 = this.right.getNotIfPossible(var1);
      if (var3 == null) {
         var3 = new ConditionNot(this.right);
      }

      int var4 = this.andOrType == 0 ? 1 : 0;
      return new ConditionAndOr(var4, (Expression)var2, (Expression)var3);
   }

   public Value getValue(SessionLocal var1) {
      Value var2 = this.left.getValue(var1);
      Value var3;
      switch (this.andOrType) {
         case 0:
            if (!var2.isFalse() && !(var3 = this.right.getValue(var1)).isFalse()) {
               if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
                  return ValueBoolean.TRUE;
               }

               return ValueNull.INSTANCE;
            }

            return ValueBoolean.FALSE;
         case 1:
            if (!var2.isTrue() && !(var3 = this.right.getValue(var1)).isTrue()) {
               if (var2 != ValueNull.INSTANCE && var3 != ValueNull.INSTANCE) {
                  return ValueBoolean.FALSE;
               }

               return ValueNull.INSTANCE;
            }

            return ValueBoolean.TRUE;
         default:
            throw DbException.getInternalError("type=" + this.andOrType);
      }
   }

   public Expression optimize(SessionLocal var1) {
      this.left = this.left.optimize(var1);
      this.right = this.right.optimize(var1);
      int var2 = this.left.getCost();
      int var3 = this.right.getCost();
      Expression var4;
      if (var3 < var2) {
         var4 = this.left;
         this.left = this.right;
         this.right = var4;
      }

      switch (this.andOrType) {
         case 0:
            if (var1.getDatabase().getSettings().optimizeTwoEquals && this.left instanceof Comparison && this.right instanceof Comparison) {
               var4 = ((Comparison)this.left).getAdditionalAnd(var1, (Comparison)this.right);
               if (var4 != null) {
                  this.added = var4.optimize(var1);
               }
            }
            break;
         case 1:
            if (var1.getDatabase().getSettings().optimizeOr) {
               label93: {
                  if (this.left instanceof Comparison && this.right instanceof Comparison) {
                     var4 = ((Comparison)this.left).optimizeOr(var1, (Comparison)this.right);
                  } else if (this.left instanceof ConditionIn && this.right instanceof Comparison) {
                     var4 = ((ConditionIn)this.left).getAdditional((Comparison)this.right);
                  } else if (this.right instanceof ConditionIn && this.left instanceof Comparison) {
                     var4 = ((ConditionIn)this.right).getAdditional((Comparison)this.left);
                  } else if (this.left instanceof ConditionInConstantSet && this.right instanceof Comparison) {
                     var4 = ((ConditionInConstantSet)this.left).getAdditional(var1, (Comparison)this.right);
                  } else if (this.right instanceof ConditionInConstantSet && this.left instanceof Comparison) {
                     var4 = ((ConditionInConstantSet)this.right).getAdditional(var1, (Comparison)this.left);
                  } else {
                     if (!(this.left instanceof ConditionAndOr) || !(this.right instanceof ConditionAndOr)) {
                        break label93;
                     }

                     var4 = optimizeConditionAndOr((ConditionAndOr)this.left, (ConditionAndOr)this.right);
                  }

                  if (var4 != null) {
                     return var4.optimize(var1);
                  }
               }
            }
      }

      var4 = optimizeIfConstant(var1, this.andOrType, this.left, this.right);
      if (var4 == null) {
         return optimizeN(this);
      } else {
         return var4 instanceof ConditionAndOr ? optimizeN((ConditionAndOr)var4) : var4;
      }
   }

   private static Expression optimizeN(ConditionAndOr var0) {
      if (var0.right instanceof ConditionAndOr) {
         ConditionAndOr var1 = (ConditionAndOr)var0.right;
         if (var1.andOrType == var0.andOrType) {
            return new ConditionAndOrN(var0.andOrType, var0.left, var1.left, var1.right);
         }
      }

      if (var0.right instanceof ConditionAndOrN) {
         ConditionAndOrN var2 = (ConditionAndOrN)var0.right;
         if (var2.getAndOrType() == var0.andOrType) {
            var2.addFirst(var0.left);
            return var2;
         }
      }

      return var0;
   }

   static Expression optimizeIfConstant(SessionLocal var0, int var1, Expression var2, Expression var3) {
      if (!var2.isConstant()) {
         return !var3.isConstant() ? null : optimizeConstant(var0, var1, var3.getValue(var0), var2);
      } else {
         Value var4 = var2.getValue(var0);
         if (!var3.isConstant()) {
            return optimizeConstant(var0, var1, var4, var3);
         } else {
            Value var5 = var3.getValue(var0);
            switch (var1) {
               case 0:
                  if (!var4.isFalse() && !var5.isFalse()) {
                     if (var4 != ValueNull.INSTANCE && var5 != ValueNull.INSTANCE) {
                        return ValueExpression.TRUE;
                     }

                     return TypedValueExpression.UNKNOWN;
                  }

                  return ValueExpression.FALSE;
               case 1:
                  if (!var4.isTrue() && !var5.isTrue()) {
                     if (var4 != ValueNull.INSTANCE && var5 != ValueNull.INSTANCE) {
                        return ValueExpression.FALSE;
                     }

                     return TypedValueExpression.UNKNOWN;
                  }

                  return ValueExpression.TRUE;
               default:
                  throw DbException.getInternalError("type=" + var1);
            }
         }
      }
   }

   private static Expression optimizeConstant(SessionLocal var0, int var1, Value var2, Expression var3) {
      if (var2 != ValueNull.INSTANCE) {
         switch (var1) {
            case 0:
               return (Expression)(var2.getBoolean() ? castToBoolean(var0, var3) : ValueExpression.FALSE);
            case 1:
               return (Expression)(var2.getBoolean() ? ValueExpression.TRUE : castToBoolean(var0, var3));
            default:
               throw DbException.getInternalError("type=" + var1);
         }
      } else {
         return null;
      }
   }

   public void addFilterConditions(TableFilter var1) {
      if (this.andOrType == 0) {
         this.left.addFilterConditions(var1);
         this.right.addFilterConditions(var1);
      } else {
         super.addFilterConditions(var1);
      }

   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      this.left.mapColumns(var1, var2, var3);
      this.right.mapColumns(var1, var2, var3);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      this.left.setEvaluatable(var1, var2);
      this.right.setEvaluatable(var1, var2);
   }

   public void updateAggregate(SessionLocal var1, int var2) {
      this.left.updateAggregate(var1, var2);
      this.right.updateAggregate(var1, var2);
   }

   public boolean isEverything(ExpressionVisitor var1) {
      return this.left.isEverything(var1) && this.right.isEverything(var1);
   }

   public int getCost() {
      return this.left.getCost() + this.right.getCost();
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

   static Expression optimizeConditionAndOr(ConditionAndOr var0, ConditionAndOr var1) {
      if (var0.andOrType == 0 && var1.andOrType == 0) {
         Expression var2 = var0.getSubexpression(0);
         Expression var3 = var0.getSubexpression(1);
         Expression var4 = var1.getSubexpression(0);
         Expression var5 = var1.getSubexpression(1);
         String var6 = var4.getSQL(0);
         String var7 = var5.getSQL(0);
         String var8;
         if (var2.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            var8 = var2.getSQL(0);
            if (var8.equals(var6)) {
               return new ConditionAndOr(0, var2, new ConditionAndOr(1, var3, var5));
            }

            if (var8.equals(var7)) {
               return new ConditionAndOr(0, var2, new ConditionAndOr(1, var3, var4));
            }
         }

         if (var3.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR)) {
            var8 = var3.getSQL(0);
            if (var8.equals(var6)) {
               return new ConditionAndOr(0, var3, new ConditionAndOr(1, var2, var5));
            }

            if (var8.equals(var7)) {
               return new ConditionAndOr(0, var3, new ConditionAndOr(1, var2, var4));
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
