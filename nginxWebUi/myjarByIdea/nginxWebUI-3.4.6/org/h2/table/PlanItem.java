package org.h2.table;

import org.h2.index.Index;

public class PlanItem {
   double cost;
   private int[] masks;
   private Index index;
   private PlanItem joinPlan;
   private PlanItem nestedJoinPlan;

   void setMasks(int[] var1) {
      this.masks = var1;
   }

   int[] getMasks() {
      return this.masks;
   }

   void setIndex(Index var1) {
      this.index = var1;
   }

   public Index getIndex() {
      return this.index;
   }

   PlanItem getJoinPlan() {
      return this.joinPlan;
   }

   PlanItem getNestedJoinPlan() {
      return this.nestedJoinPlan;
   }

   void setJoinPlan(PlanItem var1) {
      this.joinPlan = var1;
   }

   void setNestedJoinPlan(PlanItem var1) {
      this.nestedJoinPlan = var1;
   }
}
