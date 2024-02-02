package org.h2.expression.aggregate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.analysis.DataAnalysisOperation;
import org.h2.expression.analysis.WindowFrame;
import org.h2.expression.analysis.WindowFrameBound;
import org.h2.expression.analysis.WindowFrameBoundType;
import org.h2.expression.analysis.WindowFrameExclusion;
import org.h2.expression.analysis.WindowFrameUnits;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public abstract class AbstractAggregate extends DataAnalysisOperation {
   protected final boolean distinct;
   protected final Expression[] args;
   protected Expression filterCondition;
   protected TypeInfo type;

   AbstractAggregate(Select var1, Expression[] var2, boolean var3) {
      super(var1);
      this.args = var2;
      this.distinct = var3;
   }

   public final boolean isAggregate() {
      return true;
   }

   public void setFilterCondition(Expression var1) {
      this.filterCondition = var1;
   }

   public TypeInfo getType() {
      return this.type;
   }

   public void mapColumnsAnalysis(ColumnResolver var1, int var2, int var3) {
      Expression[] var4 = this.args;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Expression var7 = var4[var6];
         var7.mapColumns(var1, var2, var3);
      }

      if (this.filterCondition != null) {
         this.filterCondition.mapColumns(var1, var2, var3);
      }

      super.mapColumnsAnalysis(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      for(int var2 = 0; var2 < this.args.length; ++var2) {
         this.args[var2] = this.args[var2].optimize(var1);
      }

      if (this.filterCondition != null) {
         this.filterCondition = this.filterCondition.optimizeCondition(var1);
      }

      return super.optimize(var1);
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.setEvaluatable(var1, var2);
      }

      if (this.filterCondition != null) {
         this.filterCondition.setEvaluatable(var1, var2);
      }

      super.setEvaluatable(var1, var2);
   }

   protected void getOrderedResultLoop(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4) {
      WindowFrame var5 = this.over.getWindowFrame();
      boolean var6 = var5 == null || var5.getUnits() != WindowFrameUnits.ROWS && var5.getExclusion().isGroupOrNoOthers();
      if (var5 == null) {
         this.aggregateFastPartition(var1, var2, var3, var4, var6);
      } else {
         boolean var7 = var5.isVariableBounds();
         if (var7) {
            var7 = checkVariableBounds(var5, var3);
         }

         if (var7) {
            var6 = false;
         } else if (var5.getExclusion() == WindowFrameExclusion.EXCLUDE_NO_OTHERS) {
            WindowFrameBound var8 = var5.getFollowing();
            boolean var9 = var8 != null && var8.getType() == WindowFrameBoundType.UNBOUNDED_FOLLOWING;
            if (var5.getStarting().getType() == WindowFrameBoundType.UNBOUNDED_PRECEDING) {
               if (var9) {
                  this.aggregateWholePartition(var1, var2, var3, var4);
               } else {
                  this.aggregateFastPartition(var1, var2, var3, var4, var6);
               }

               return;
            }

            if (var9) {
               this.aggregateFastPartitionInReverse(var1, var2, var3, var4, var6);
               return;
            }
         }

         int var12 = var3.size();

         Value var14;
         for(int var13 = 0; var13 < var12; var13 = this.processGroup(var2, var14, var3, var4, var13, var12, var6)) {
            Object var10 = this.createAggregateData();
            Iterator var11 = WindowFrame.iterator(this.over, var1, var3, this.getOverOrderBySort(), var13, false);

            while(var11.hasNext()) {
               this.updateFromExpressions(var1, var10, (Value[])var11.next());
            }

            var14 = this.getAggregatedValue(var1, var10);
         }

      }
   }

   private static boolean checkVariableBounds(WindowFrame var0, ArrayList<Value[]> var1) {
      int var2 = var1.size();
      WindowFrameBound var3 = var0.getStarting();
      int var4;
      Value var5;
      int var6;
      if (var3.isVariable()) {
         var4 = var3.getExpressionIndex();
         var5 = ((Value[])var1.get(0))[var4];

         for(var6 = 1; var6 < var2; ++var6) {
            if (!var5.equals(((Value[])var1.get(var6))[var4])) {
               return true;
            }
         }
      }

      var3 = var0.getFollowing();
      if (var3 != null && var3.isVariable()) {
         var4 = var3.getExpressionIndex();
         var5 = ((Value[])var1.get(0))[var4];

         for(var6 = 1; var6 < var2; ++var6) {
            if (!var5.equals(((Value[])var1.get(var6))[var4])) {
               return true;
            }
         }
      }

      return false;
   }

   private void aggregateFastPartition(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4, boolean var5) {
      Object var6 = this.createAggregateData();
      int var7 = var3.size();
      int var8 = -1;
      Value var9 = null;

      for(int var10 = 0; var10 < var7; var10 = this.processGroup(var2, var9, var3, var4, var10, var7, var5)) {
         int var11 = WindowFrame.getEndIndex(this.over, var1, var3, this.getOverOrderBySort(), var10);

         assert var11 >= var8;

         if (var11 <= var8) {
            if (var9 == null) {
               var9 = this.getAggregatedValue(var1, var6);
            }
         } else {
            for(int var12 = var8 + 1; var12 <= var11; ++var12) {
               this.updateFromExpressions(var1, var6, (Value[])var3.get(var12));
            }

            var8 = var11;
            var9 = this.getAggregatedValue(var1, var6);
         }
      }

   }

   private void aggregateFastPartitionInReverse(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4, boolean var5) {
      Object var6 = this.createAggregateData();
      int var7 = var3.size();
      Value var8 = null;
      int var9 = var7 - 1;

      while(var9 >= 0) {
         int var10 = this.over.getWindowFrame().getStartIndex(var1, var3, this.getOverOrderBySort(), var9);

         assert var10 <= var7;

         if (var10 >= var7) {
            if (var8 == null) {
               var8 = this.getAggregatedValue(var1, var6);
            }
         } else {
            for(int var11 = var7 - 1; var11 >= var10; --var11) {
               this.updateFromExpressions(var1, var6, (Value[])var3.get(var11));
            }

            var7 = var10;
            var8 = this.getAggregatedValue(var1, var6);
         }

         Value[] var13 = (Value[])var3.get(var9);
         Value[] var12 = var13;

         while(true) {
            var2.put(var12[var4].getInt(), var8);
            --var9;
            if (var9 < 0 || !var5 || this.overOrderBySort.compare(var13, var12 = (Value[])var3.get(var9)) != 0) {
               break;
            }
         }
      }

   }

   private int processGroup(HashMap<Integer, Value> var1, Value var2, ArrayList<Value[]> var3, int var4, int var5, int var6, boolean var7) {
      Value[] var8 = (Value[])var3.get(var5);
      Value[] var9 = var8;

      do {
         var1.put(var9[var4].getInt(), var2);
         ++var5;
      } while(var5 < var6 && var7 && this.overOrderBySort.compare(var8, var9 = (Value[])var3.get(var5)) == 0);

      return var5;
   }

   private void aggregateWholePartition(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4) {
      Object var5 = this.createAggregateData();
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         Value[] var7 = (Value[])var6.next();
         this.updateFromExpressions(var1, var5, var7);
      }

      Value var9 = this.getAggregatedValue(var1, var5);
      Iterator var10 = var3.iterator();

      while(var10.hasNext()) {
         Value[] var8 = (Value[])var10.next();
         var2.put(var8[var4].getInt(), var9);
      }

   }

   protected abstract void updateFromExpressions(SessionLocal var1, Object var2, Value[] var3);

   protected void updateAggregate(SessionLocal var1, SelectGroups var2, int var3) {
      if (this.filterCondition != null && !this.filterCondition.getBooleanValue(var1)) {
         if (this.over != null && this.over.isOrdered()) {
            this.updateOrderedAggregate(var1, var2, var3, this.over.getOrderBy());
         }
      } else if (this.over != null) {
         if (this.over.isOrdered()) {
            this.updateOrderedAggregate(var1, var2, var3, this.over.getOrderBy());
         } else {
            this.updateAggregate(var1, this.getWindowData(var1, var2, false));
         }
      } else {
         this.updateAggregate(var1, this.getGroupData(var2, false));
      }

   }

   protected abstract void updateAggregate(SessionLocal var1, Object var2);

   protected void updateGroupAggregates(SessionLocal var1, int var2) {
      if (this.filterCondition != null) {
         this.filterCondition.updateAggregate(var1, var2);
      }

      super.updateGroupAggregates(var1, var2);
   }

   protected StringBuilder appendTailConditions(StringBuilder var1, int var2, boolean var3) {
      if (this.filterCondition != null) {
         var1.append(" FILTER (WHERE ");
         this.filterCondition.getUnenclosedSQL(var1, var2).append(')');
      }

      return super.appendTailConditions(var1, var2, var3);
   }

   public int getSubexpressionCount() {
      return this.args.length;
   }

   public Expression getSubexpression(int var1) {
      return this.args[var1];
   }
}
