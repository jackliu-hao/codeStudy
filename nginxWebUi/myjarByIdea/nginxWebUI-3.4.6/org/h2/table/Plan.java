package org.h2.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.h2.command.query.AllColumnsForPlan;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.Trace;

public class Plan {
   private final TableFilter[] filters;
   private final HashMap<TableFilter, PlanItem> planItems = new HashMap();
   private final Expression[] allConditions;
   private final TableFilter[] allFilters;

   public Plan(TableFilter[] var1, int var2, Expression var3) {
      this.filters = new TableFilter[var2];
      System.arraycopy(var1, 0, this.filters, 0, var2);
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      if (var3 != null) {
         var4.add(var3);
      }

      for(int var6 = 0; var6 < var2; ++var6) {
         TableFilter var7 = var1[var6];
         var7.visit((var2x) -> {
            var5.add(var2x);
            if (var2x.getJoinCondition() != null) {
               var4.add(var2x.getJoinCondition());
            }

         });
      }

      this.allConditions = (Expression[])var4.toArray(new Expression[0]);
      this.allFilters = (TableFilter[])var5.toArray(new TableFilter[0]);
   }

   public PlanItem getItem(TableFilter var1) {
      return (PlanItem)this.planItems.get(var1);
   }

   public TableFilter[] getFilters() {
      return this.filters;
   }

   public void removeUnusableIndexConditions() {
      for(int var1 = 0; var1 < this.allFilters.length; ++var1) {
         TableFilter var2 = this.allFilters[var1];
         this.setEvaluatable(var2, true);
         if (var1 < this.allFilters.length - 1) {
            var2.optimizeFullCondition();
         }

         var2.removeUnusableIndexConditions();
      }

      TableFilter[] var5 = this.allFilters;
      int var6 = var5.length;

      for(int var3 = 0; var3 < var6; ++var3) {
         TableFilter var4 = var5[var3];
         this.setEvaluatable(var4, false);
      }

   }

   public double calculateCost(SessionLocal var1, AllColumnsForPlan var2) {
      Trace var3 = var1.getTrace();
      if (var3.isDebugEnabled()) {
         var3.debug("Plan       : calculate cost for plan {0}", Arrays.toString(this.allFilters));
      }

      double var4 = 1.0;
      boolean var6 = false;

      for(int var7 = 0; var7 < this.allFilters.length; ++var7) {
         TableFilter var8 = this.allFilters[var7];
         if (var3.isDebugEnabled()) {
            var3.debug("Plan       :   for table filter {0}", var8);
         }

         PlanItem var9 = var8.getBestPlanItem(var1, this.allFilters, var7, var2);
         this.planItems.put(var8, var9);
         if (var3.isDebugEnabled()) {
            var3.debug("Plan       :   best plan item cost {0} index {1}", var9.cost, var9.getIndex().getPlanSQL());
         }

         var4 += var4 * var9.cost;
         this.setEvaluatable(var8, true);
         Expression var10 = var8.getJoinCondition();
         if (var10 != null && !var10.isEverything(ExpressionVisitor.EVALUATABLE_VISITOR)) {
            var6 = true;
            break;
         }
      }

      if (var6) {
         var4 = Double.POSITIVE_INFINITY;
      }

      if (var3.isDebugEnabled()) {
         var1.getTrace().debug("Plan       : plan cost {0}", var4);
      }

      TableFilter[] var11 = this.allFilters;
      int var12 = var11.length;

      for(int var13 = 0; var13 < var12; ++var13) {
         TableFilter var14 = var11[var13];
         this.setEvaluatable(var14, false);
      }

      return var4;
   }

   private void setEvaluatable(TableFilter var1, boolean var2) {
      var1.setEvaluatable(var1, var2);
      Expression[] var3 = this.allConditions;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.setEvaluatable(var1, var2);
      }

   }
}
