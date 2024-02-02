package org.h2.expression.condition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueNull;

public class ConditionAndOrN extends Condition {
   private final int andOrType;
   private final List<Expression> expressions;
   private List<Expression> added;
   private static final Comparator<Expression> COMPARE_BY_COST = new Comparator<Expression>() {
      public int compare(Expression var1, Expression var2) {
         return var1.getCost() - var2.getCost();
      }
   };

   public ConditionAndOrN(int var1, Expression var2, Expression var3, Expression var4) {
      this.andOrType = var1;
      this.expressions = new ArrayList(3);
      this.expressions.add(var2);
      this.expressions.add(var3);
      this.expressions.add(var4);
   }

   public ConditionAndOrN(int var1, List<Expression> var2) {
      this.andOrType = var1;
      this.expressions = var2;
   }

   int getAndOrType() {
      return this.andOrType;
   }

   void addFirst(Expression var1) {
      this.expressions.add(0, var1);
   }

   public boolean needParentheses() {
      return true;
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      Iterator var3 = this.expressions.iterator();
      ((Expression)var3.next()).getSQL(var1, var2, 0);

      for(; var3.hasNext(); ((Expression)var3.next()).getSQL(var1, var2, 0)) {
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
      }

      return var1;
   }

   public void createIndexConditions(SessionLocal var1, TableFilter var2) {
      if (this.andOrType == 0) {
         Iterator var3 = this.expressions.iterator();

         Expression var4;
         while(var3.hasNext()) {
            var4 = (Expression)var3.next();
            var4.createIndexConditions(var1, var2);
         }

         if (this.added != null) {
            var3 = this.added.iterator();

            while(var3.hasNext()) {
               var4 = (Expression)var3.next();
               var4.createIndexConditions(var1, var2);
            }
         }
      }

   }

   public Expression getNotIfPossible(SessionLocal var1) {
      ArrayList var2 = new ArrayList(this.expressions.size());

      Object var5;
      for(Iterator var3 = this.expressions.iterator(); var3.hasNext(); var2.add(var5)) {
         Expression var4 = (Expression)var3.next();
         var5 = var4.getNotIfPossible(var1);
         if (var5 == null) {
            var5 = new ConditionNot(var4);
         }
      }

      int var6 = this.andOrType == 0 ? 1 : 0;
      return new ConditionAndOrN(var6, var2);
   }

   public Value getValue(SessionLocal var1) {
      boolean var2 = false;
      Iterator var3;
      Expression var4;
      Value var5;
      switch (this.andOrType) {
         case 0:
            var3 = this.expressions.iterator();

            while(var3.hasNext()) {
               var4 = (Expression)var3.next();
               var5 = var4.getValue(var1);
               if (var5 == ValueNull.INSTANCE) {
                  var2 = true;
               } else if (!var5.getBoolean()) {
                  return ValueBoolean.FALSE;
               }
            }

            return (Value)(var2 ? ValueNull.INSTANCE : ValueBoolean.TRUE);
         case 1:
            var3 = this.expressions.iterator();

            while(var3.hasNext()) {
               var4 = (Expression)var3.next();
               var5 = var4.getValue(var1);
               if (var5 == ValueNull.INSTANCE) {
                  var2 = true;
               } else if (var5.getBoolean()) {
                  return ValueBoolean.TRUE;
               }
            }

            return (Value)(var2 ? ValueNull.INSTANCE : ValueBoolean.FALSE);
         default:
            throw DbException.getInternalError("type=" + this.andOrType);
      }
   }

   public Expression optimize(SessionLocal var1) {
      int var2;
      for(var2 = 0; var2 < this.expressions.size(); ++var2) {
         this.expressions.set(var2, ((Expression)this.expressions.get(var2)).optimize(var1));
      }

      Collections.sort(this.expressions, COMPARE_BY_COST);
      this.optimizeMerge(0);
      var2 = 1;

      while(true) {
         while(var2 < this.expressions.size()) {
            Expression var3 = (Expression)this.expressions.get(var2 - 1);
            Expression var4 = (Expression)this.expressions.get(var2);
            Expression var5;
            switch (this.andOrType) {
               case 0:
                  if (var1.getDatabase().getSettings().optimizeTwoEquals && var3 instanceof Comparison && var4 instanceof Comparison) {
                     var5 = ((Comparison)var3).getAdditionalAnd(var1, (Comparison)var4);
                     if (var5 != null) {
                        if (this.added == null) {
                           this.added = new ArrayList();
                        }

                        this.added.add(var5.optimize(var1));
                     }
                  }
                  break;
               case 1:
                  if (var1.getDatabase().getSettings().optimizeOr) {
                     label112: {
                        if (var3 instanceof Comparison && var4 instanceof Comparison) {
                           var5 = ((Comparison)var3).optimizeOr(var1, (Comparison)var4);
                        } else if (var3 instanceof ConditionIn && var4 instanceof Comparison) {
                           var5 = ((ConditionIn)var3).getAdditional((Comparison)var4);
                        } else if (var4 instanceof ConditionIn && var3 instanceof Comparison) {
                           var5 = ((ConditionIn)var4).getAdditional((Comparison)var3);
                        } else if (var3 instanceof ConditionInConstantSet && var4 instanceof Comparison) {
                           var5 = ((ConditionInConstantSet)var3).getAdditional(var1, (Comparison)var4);
                        } else if (var4 instanceof ConditionInConstantSet && var3 instanceof Comparison) {
                           var5 = ((ConditionInConstantSet)var4).getAdditional(var1, (Comparison)var3);
                        } else {
                           if (!(var3 instanceof ConditionAndOr) || !(var4 instanceof ConditionAndOr)) {
                              break label112;
                           }

                           var5 = ConditionAndOr.optimizeConditionAndOr((ConditionAndOr)var3, (ConditionAndOr)var4);
                        }

                        if (var5 != null) {
                           this.expressions.remove(var2);
                           this.expressions.set(var2 - 1, var5.optimize(var1));
                           continue;
                        }
                     }
                  }
            }

            var5 = ConditionAndOr.optimizeIfConstant(var1, this.andOrType, var3, var4);
            if (var5 != null) {
               this.expressions.remove(var2);
               this.expressions.set(var2 - 1, var5);
            } else if (!this.optimizeMerge(var2)) {
               ++var2;
            }
         }

         Collections.sort(this.expressions, COMPARE_BY_COST);
         if (this.expressions.size() == 1) {
            return Condition.castToBoolean(var1, (Expression)this.expressions.get(0));
         }

         return this;
      }
   }

   private boolean optimizeMerge(int var1) {
      Expression var2 = (Expression)this.expressions.get(var1);
      if (var2 instanceof ConditionAndOrN) {
         ConditionAndOrN var3 = (ConditionAndOrN)var2;
         if (this.andOrType == var3.andOrType) {
            this.expressions.remove(var1);
            this.expressions.addAll(var1, var3.expressions);
            return true;
         }
      } else if (var2 instanceof ConditionAndOr) {
         ConditionAndOr var4 = (ConditionAndOr)var2;
         if (this.andOrType == var4.getAndOrType()) {
            this.expressions.set(var1, var4.getSubexpression(0));
            this.expressions.add(var1 + 1, var4.getSubexpression(1));
            return true;
         }
      }

      return false;
   }

   public void addFilterConditions(TableFilter var1) {
      if (this.andOrType == 0) {
         Iterator var2 = this.expressions.iterator();

         while(var2.hasNext()) {
            Expression var3 = (Expression)var2.next();
            var3.addFilterConditions(var1);
         }
      } else {
         super.addFilterConditions(var1);
      }

   }

   public void mapColumns(ColumnResolver var1, int var2, int var3) {
      Iterator var4 = this.expressions.iterator();

      while(var4.hasNext()) {
         Expression var5 = (Expression)var4.next();
         var5.mapColumns(var1, var2, var3);
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Iterator var3 = this.expressions.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.setEvaluatable(var1, var2);
      }

   }

   public void updateAggregate(SessionLocal var1, int var2) {
      Iterator var3 = this.expressions.iterator();

      while(var3.hasNext()) {
         Expression var4 = (Expression)var3.next();
         var4.updateAggregate(var1, var2);
      }

   }

   public boolean isEverything(ExpressionVisitor var1) {
      Iterator var2 = this.expressions.iterator();

      Expression var3;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         var3 = (Expression)var2.next();
      } while(var3.isEverything(var1));

      return false;
   }

   public int getCost() {
      int var1 = 0;

      Expression var3;
      for(Iterator var2 = this.expressions.iterator(); var2.hasNext(); var1 += var3.getCost()) {
         var3 = (Expression)var2.next();
      }

      return var1;
   }

   public int getSubexpressionCount() {
      return this.expressions.size();
   }

   public Expression getSubexpression(int var1) {
      return (Expression)this.expressions.get(var1);
   }
}
