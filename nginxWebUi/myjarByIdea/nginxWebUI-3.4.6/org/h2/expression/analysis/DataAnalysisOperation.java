package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionVisitor;
import org.h2.message.DbException;
import org.h2.result.SortOrder;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.Value;
import org.h2.value.ValueInteger;

public abstract class DataAnalysisOperation extends Expression {
   public static final int STAGE_RESET = 0;
   public static final int STAGE_GROUP = 1;
   public static final int STAGE_WINDOW = 2;
   protected final Select select;
   protected Window over;
   protected SortOrder overOrderBySort;
   private int numFrameExpressions;
   private int lastGroupRowId;

   protected static SortOrder createOrder(SessionLocal var0, ArrayList<QueryOrderBy> var1, int var2) {
      int var3 = var1.size();
      int[] var4 = new int[var3];
      int[] var5 = new int[var3];

      for(int var6 = 0; var6 < var3; ++var6) {
         QueryOrderBy var7 = (QueryOrderBy)var1.get(var6);
         var4[var6] = var6 + var2;
         var5[var6] = var7.sortType;
      }

      return new SortOrder(var0, var4, var5, (ArrayList)null);
   }

   protected DataAnalysisOperation(Select var1) {
      this.select = var1;
   }

   public void setOverCondition(Window var1) {
      this.over = var1;
   }

   public abstract boolean isAggregate();

   protected SortOrder getOverOrderBySort() {
      return this.overOrderBySort;
   }

   public final void mapColumns(ColumnResolver var1, int var2, int var3) {
      byte var4;
      if (this.over != null) {
         if (var3 != 0) {
            throw DbException.get(90054, this.getTraceSQL());
         }

         var4 = 1;
      } else {
         if (var3 == 2) {
            throw DbException.get(90054, this.getTraceSQL());
         }

         var4 = 2;
      }

      this.mapColumnsAnalysis(var1, var2, var4);
   }

   protected void mapColumnsAnalysis(ColumnResolver var1, int var2, int var3) {
      if (this.over != null) {
         this.over.mapColumns(var1, var2);
      }

   }

   public Expression optimize(SessionLocal var1) {
      if (this.over != null) {
         this.over.optimize(var1);
         ArrayList var2 = this.over.getOrderBy();
         if (var2 != null) {
            this.overOrderBySort = createOrder(var1, var2, this.getNumExpressions());
         } else if (!this.isAggregate()) {
            this.overOrderBySort = new SortOrder(var1, new int[this.getNumExpressions()]);
         }

         WindowFrame var3 = this.over.getWindowFrame();
         if (var3 != null) {
            int var4 = this.getNumExpressions();
            int var5 = 0;
            if (var2 != null) {
               var5 = var2.size();
               var4 += var5;
            }

            int var6 = 0;
            WindowFrameBound var7 = var3.getStarting();
            if (var7.isParameterized()) {
               this.checkOrderBy(var3.getUnits(), var5);
               if (var7.isVariable()) {
                  var7.setExpressionIndex(var4);
                  ++var6;
               }
            }

            var7 = var3.getFollowing();
            if (var7 != null && var7.isParameterized()) {
               this.checkOrderBy(var3.getUnits(), var5);
               if (var7.isVariable()) {
                  var7.setExpressionIndex(var4 + var6);
                  ++var6;
               }
            }

            this.numFrameExpressions = var6;
         }
      }

      return this;
   }

   private void checkOrderBy(WindowFrameUnits var1, int var2) {
      String var3;
      switch (var1) {
         case RANGE:
            if (var2 != 1) {
               var3 = this.getTraceSQL();
               throw DbException.getSyntaxError(var3, var3.length() - 1, "exactly one sort key is required for RANGE units");
            }
            break;
         case GROUPS:
            if (var2 < 1) {
               var3 = this.getTraceSQL();
               throw DbException.getSyntaxError(var3, var3.length() - 1, "a sort key is required for GROUPS units");
            }
      }

   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      if (this.over != null) {
         this.over.setEvaluatable(var1, var2);
      }

   }

   public final void updateAggregate(SessionLocal var1, int var2) {
      if (var2 == 0) {
         this.updateGroupAggregates(var1, 0);
         this.lastGroupRowId = 0;
      } else {
         boolean var3 = var2 == 2;
         if (var3 != (this.over != null)) {
            if (!var3 && this.select.isWindowQuery()) {
               this.updateGroupAggregates(var1, var2);
            }

         } else {
            SelectGroups var4 = this.select.getGroupDataIfCurrent(var3);
            if (var4 != null) {
               int var5 = var4.getCurrentGroupRowId();
               if (this.lastGroupRowId != var5) {
                  this.lastGroupRowId = var5;
                  if (this.over != null && !this.select.isGroupQuery()) {
                     this.over.updateAggregate(var1, var2);
                  }

                  this.updateAggregate(var1, var4, var5);
               }
            }
         }
      }
   }

   protected abstract void updateAggregate(SessionLocal var1, SelectGroups var2, int var3);

   protected void updateGroupAggregates(SessionLocal var1, int var2) {
      if (this.over != null) {
         this.over.updateAggregate(var1, var2);
      }

   }

   protected abstract int getNumExpressions();

   private int getNumFrameExpressions() {
      return this.numFrameExpressions;
   }

   protected abstract void rememberExpressions(SessionLocal var1, Value[] var2);

   protected Object getWindowData(SessionLocal var1, SelectGroups var2, boolean var3) {
      Value var5 = this.over.getCurrentKey(var1);
      PartitionData var6 = var2.getWindowExprData(this, var5);
      Object var4;
      if (var6 == null) {
         var4 = var3 ? new ArrayList() : this.createAggregateData();
         var2.setWindowExprData(this, var5, new PartitionData(var4));
      } else {
         var4 = var6.getData();
      }

      return var4;
   }

   protected Object getGroupData(SelectGroups var1, boolean var2) {
      Object var3 = var1.getCurrentGroupExprData(this);
      if (var3 == null) {
         if (var2) {
            return null;
         }

         var3 = this.createAggregateData();
         var1.setCurrentGroupExprData(this, var3);
      }

      return var3;
   }

   protected abstract Object createAggregateData();

   public boolean isEverything(ExpressionVisitor var1) {
      if (this.over == null) {
         return true;
      } else {
         switch (var1.getType()) {
            case 0:
            case 1:
            case 2:
            case 8:
            case 11:
               return false;
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            default:
               return true;
         }
      }
   }

   public Value getValue(SessionLocal var1) {
      SelectGroups var2 = this.select.getGroupDataIfCurrent(this.over != null);
      if (var2 == null) {
         throw DbException.get(90054, this.getTraceSQL());
      } else {
         return this.over == null ? this.getAggregatedValue(var1, this.getGroupData(var2, true)) : this.getWindowResult(var1, var2);
      }
   }

   private Value getWindowResult(SessionLocal var1, SelectGroups var2) {
      boolean var5 = this.over.isOrdered();
      Value var6 = this.over.getCurrentKey(var1);
      PartitionData var3 = var2.getWindowExprData(this, var6);
      Object var4;
      if (var3 == null) {
         var4 = var5 ? new ArrayList() : this.createAggregateData();
         var3 = new PartitionData(var4);
         var2.setWindowExprData(this, var6, var3);
      } else {
         var4 = var3.getData();
      }

      Value var7;
      if (!var5 && this.isAggregate()) {
         var7 = var3.getResult();
         if (var7 == null) {
            var7 = this.getAggregatedValue(var1, var4);
            var3.setResult(var7);
         }

         return var7;
      } else {
         var7 = this.getOrderedResult(var1, var2, var3, var4);
         return var7 == null ? this.getAggregatedValue(var1, (Object)null) : var7;
      }
   }

   protected abstract Value getAggregatedValue(SessionLocal var1, Object var2);

   protected void updateOrderedAggregate(SessionLocal var1, SelectGroups var2, int var3, ArrayList<QueryOrderBy> var4) {
      int var5 = this.getNumExpressions();
      int var6 = var4 != null ? var4.size() : 0;
      int var7 = this.getNumFrameExpressions();
      Value[] var8 = new Value[var5 + var6 + var7 + 1];
      this.rememberExpressions(var1, var8);

      for(int var9 = 0; var9 < var6; ++var9) {
         QueryOrderBy var10 = (QueryOrderBy)var4.get(var9);
         var8[var5++] = var10.expression.getValue(var1);
      }

      if (var7 > 0) {
         WindowFrame var11 = this.over.getWindowFrame();
         WindowFrameBound var13 = var11.getStarting();
         if (var13.isVariable()) {
            var8[var5++] = var13.getValue().getValue(var1);
         }

         var13 = var11.getFollowing();
         if (var13 != null && var13.isVariable()) {
            var8[var5++] = var13.getValue().getValue(var1);
         }
      }

      var8[var5] = ValueInteger.get(var3);
      ArrayList var12 = (ArrayList)this.getWindowData(var1, var2, true);
      var12.add(var8);
   }

   private Value getOrderedResult(SessionLocal var1, SelectGroups var2, PartitionData var3, Object var4) {
      HashMap var5 = var3.getOrderedResult();
      if (var5 == null) {
         var5 = new HashMap();
         ArrayList var6 = (ArrayList)var4;
         int var7 = this.getNumExpressions();
         ArrayList var8 = this.over.getOrderBy();
         if (var8 != null) {
            var7 += var8.size();
            var6.sort(this.overOrderBySort);
         }

         var7 += this.getNumFrameExpressions();
         this.getOrderedResultLoop(var1, var5, var6, var7);
         var3.setOrderedResult(var5);
      }

      return (Value)var5.get(var2.getCurrentGroupRowId());
   }

   protected abstract void getOrderedResultLoop(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4);

   protected StringBuilder appendTailConditions(StringBuilder var1, int var2, boolean var3) {
      if (this.over != null) {
         var1.append(' ');
         this.over.getSQL(var1, var2, var3);
      }

      return var1;
   }
}
