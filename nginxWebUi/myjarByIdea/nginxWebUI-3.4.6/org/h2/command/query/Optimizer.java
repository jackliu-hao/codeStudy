package org.h2.command.query;

import java.util.BitSet;
import java.util.Random;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.table.Plan;
import org.h2.table.PlanItem;
import org.h2.table.TableFilter;
import org.h2.util.Permutations;

class Optimizer {
   private static final int MAX_BRUTE_FORCE_FILTERS = 7;
   private static final int MAX_BRUTE_FORCE = 2000;
   private static final int MAX_GENETIC = 500;
   private long startNs;
   private BitSet switched;
   private final TableFilter[] filters;
   private final Expression condition;
   private final SessionLocal session;
   private Plan bestPlan;
   private TableFilter topFilter;
   private double cost;
   private Random random;
   private final AllColumnsForPlan allColumnsSet;

   Optimizer(TableFilter[] var1, Expression var2, SessionLocal var3) {
      this.filters = var1;
      this.condition = var2;
      this.session = var3;
      this.allColumnsSet = new AllColumnsForPlan(var1);
   }

   private static int getMaxBruteForceFilters(int var0) {
      int var1 = 0;
      int var2 = var0;

      for(int var3 = var0; var2 > 0 && var3 * (var2 * (var2 - 1) / 2) < 2000; ++var1) {
         --var2;
         var3 *= var2;
      }

      return var1;
   }

   private void calculateBestPlan() {
      this.cost = -1.0;
      if (this.filters.length == 1) {
         this.testPlan(this.filters);
      } else {
         this.startNs = System.nanoTime();
         if (this.filters.length <= 7) {
            this.calculateBruteForceAll();
         } else {
            this.calculateBruteForceSome();
            this.random = new Random(0L);
            this.calculateGenetic();
         }
      }

   }

   private void calculateFakePlan() {
      this.cost = -1.0;
      this.bestPlan = new Plan(this.filters, this.filters.length, this.condition);
   }

   private boolean canStop(int var1) {
      return (var1 & 127) == 0 && this.cost >= 0.0 && (double)(System.nanoTime() - this.startNs) > this.cost * 100000.0;
   }

   private void calculateBruteForceAll() {
      TableFilter[] var1 = new TableFilter[this.filters.length];
      Permutations var2 = Permutations.create(this.filters, var1);

      for(int var3 = 0; !this.canStop(var3) && var2.next(); ++var3) {
         this.testPlan(var1);
      }

   }

   private void calculateBruteForceSome() {
      int var1 = getMaxBruteForceFilters(this.filters.length);
      TableFilter[] var2 = new TableFilter[this.filters.length];
      Permutations var3 = Permutations.create(this.filters, var2, var1);

      for(int var4 = 0; !this.canStop(var4) && var3.next(); ++var4) {
         TableFilter[] var5 = this.filters;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            TableFilter var8 = var5[var7];
            var8.setUsed(false);
         }

         int var13;
         for(var13 = 0; var13 < var1; ++var13) {
            var2[var13].setUsed(true);
         }

         for(var13 = var1; var13 < this.filters.length; ++var13) {
            double var14 = -1.0;
            int var15 = -1;

            for(int var9 = 0; var9 < this.filters.length; ++var9) {
               if (!this.filters[var9].isUsed()) {
                  if (var13 == this.filters.length - 1) {
                     var15 = var9;
                     break;
                  }

                  var2[var13] = this.filters[var9];
                  Plan var10 = new Plan(var2, var13 + 1, this.condition);
                  double var11 = var10.calculateCost(this.session, this.allColumnsSet);
                  if (var14 < 0.0 || var11 < var14) {
                     var14 = var11;
                     var15 = var9;
                  }
               }
            }

            this.filters[var15].setUsed(true);
            var2[var13] = this.filters[var15];
         }

         this.testPlan(var2);
      }

   }

   private void calculateGenetic() {
      TableFilter[] var1 = new TableFilter[this.filters.length];
      TableFilter[] var2 = new TableFilter[this.filters.length];

      for(int var3 = 0; var3 < 500 && !this.canStop(var3); ++var3) {
         boolean var4 = (var3 & 127) == 0;
         if (!var4) {
            System.arraycopy(var1, 0, var2, 0, this.filters.length);
            if (!this.shuffleTwo(var2)) {
               var4 = true;
            }
         }

         if (var4) {
            this.switched = new BitSet();
            System.arraycopy(this.filters, 0, var1, 0, this.filters.length);
            this.shuffleAll(var1);
            System.arraycopy(var1, 0, var2, 0, this.filters.length);
         }

         if (this.testPlan(var2)) {
            this.switched = new BitSet();
            System.arraycopy(var2, 0, var1, 0, this.filters.length);
         }
      }

   }

   private boolean testPlan(TableFilter[] var1) {
      Plan var2 = new Plan(var1, var1.length, this.condition);
      double var3 = var2.calculateCost(this.session, this.allColumnsSet);
      if (!(this.cost < 0.0) && !(var3 < this.cost)) {
         return false;
      } else {
         this.cost = var3;
         this.bestPlan = var2;
         return true;
      }
   }

   private void shuffleAll(TableFilter[] var1) {
      for(int var2 = 0; var2 < var1.length - 1; ++var2) {
         int var3 = var2 + this.random.nextInt(var1.length - var2);
         if (var3 != var2) {
            TableFilter var4 = var1[var2];
            var1[var2] = var1[var3];
            var1[var3] = var4;
         }
      }

   }

   private boolean shuffleTwo(TableFilter[] var1) {
      int var2 = 0;
      int var3 = 0;

      int var4;
      for(var4 = 0; var4 < 20; ++var4) {
         var2 = this.random.nextInt(var1.length);
         var3 = this.random.nextInt(var1.length);
         if (var2 != var3) {
            int var5;
            if (var2 < var3) {
               var5 = var2;
               var2 = var3;
               var3 = var5;
            }

            var5 = var2 * var1.length + var3;
            if (!this.switched.get(var5)) {
               this.switched.set(var5);
               break;
            }
         }
      }

      if (var4 == 20) {
         return false;
      } else {
         TableFilter var6 = var1[var2];
         var1[var2] = var1[var3];
         var1[var3] = var6;
         return true;
      }
   }

   void optimize(boolean var1) {
      if (var1) {
         this.calculateFakePlan();
      } else {
         this.calculateBestPlan();
         this.bestPlan.removeUnusableIndexConditions();
      }

      TableFilter[] var2 = this.bestPlan.getFilters();
      this.topFilter = var2[0];

      for(int var3 = 0; var3 < var2.length - 1; ++var3) {
         var2[var3].addJoin(var2[var3 + 1], false, (Expression)null);
      }

      if (!var1) {
         TableFilter[] var8 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            TableFilter var6 = var8[var5];
            PlanItem var7 = this.bestPlan.getItem(var6);
            var6.setPlanItem(var7);
         }

      }
   }

   public TableFilter getTopFilter() {
      return this.topFilter;
   }

   double getCost() {
      return this.cost;
   }
}
