package org.h2.expression.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.command.query.Select;
import org.h2.command.query.SelectGroups;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.message.DbException;
import org.h2.table.ColumnResolver;
import org.h2.table.TableFilter;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDouble;
import org.h2.value.ValueNull;

public class WindowFunction extends DataAnalysisOperation {
   private final WindowFunctionType type;
   private final Expression[] args;
   private boolean fromLast;
   private boolean ignoreNulls;

   public static int getMinArgumentCount(WindowFunctionType var0) {
      switch (var0) {
         case NTILE:
         case LEAD:
         case LAG:
         case FIRST_VALUE:
         case LAST_VALUE:
         case RATIO_TO_REPORT:
            return 1;
         case NTH_VALUE:
            return 2;
         default:
            return 0;
      }
   }

   public static int getMaxArgumentCount(WindowFunctionType var0) {
      switch (var0) {
         case NTILE:
         case FIRST_VALUE:
         case LAST_VALUE:
         case RATIO_TO_REPORT:
            return 1;
         case LEAD:
         case LAG:
            return 3;
         case NTH_VALUE:
            return 2;
         default:
            return 0;
      }
   }

   private static Value getNthValue(Iterator<Value[]> var0, int var1, boolean var2) {
      Object var3 = ValueNull.INSTANCE;
      int var4 = 0;

      while(var0.hasNext()) {
         Value var5 = ((Value[])var0.next())[0];
         if ((!var2 || var5 != ValueNull.INSTANCE) && var4++ == var1) {
            var3 = var5;
            break;
         }
      }

      return (Value)var3;
   }

   public WindowFunction(WindowFunctionType var1, Select var2, Expression[] var3) {
      super(var2);
      this.type = var1;
      this.args = var3;
   }

   public WindowFunctionType getFunctionType() {
      return this.type;
   }

   public void setFromLast(boolean var1) {
      this.fromLast = var1;
   }

   public void setIgnoreNulls(boolean var1) {
      this.ignoreNulls = var1;
   }

   public boolean isAggregate() {
      return false;
   }

   protected void updateAggregate(SessionLocal var1, SelectGroups var2, int var3) {
      this.updateOrderedAggregate(var1, var2, var3, this.over.getOrderBy());
   }

   protected void updateGroupAggregates(SessionLocal var1, int var2) {
      super.updateGroupAggregates(var1, var2);
      if (this.args != null) {
         Expression[] var3 = this.args;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Expression var6 = var3[var5];
            var6.updateAggregate(var1, var2);
         }
      }

   }

   protected int getNumExpressions() {
      return this.args != null ? this.args.length : 0;
   }

   protected void rememberExpressions(SessionLocal var1, Value[] var2) {
      if (this.args != null) {
         int var3 = 0;

         for(int var4 = this.args.length; var3 < var4; ++var3) {
            var2[var3] = this.args[var3].getValue(var1);
         }
      }

   }

   protected Object createAggregateData() {
      throw DbException.getUnsupportedException("Window function");
   }

   protected void getOrderedResultLoop(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4) {
      switch (this.type) {
         case NTILE:
            getNtile(var2, var3, var4);
            break;
         case LEAD:
         case LAG:
            this.getLeadLag(var2, var3, var4, var1);
            break;
         case FIRST_VALUE:
         case LAST_VALUE:
         case NTH_VALUE:
            this.getNth(var1, var2, var3, var4);
            break;
         case RATIO_TO_REPORT:
            getRatioToReport(var2, var3, var4);
            break;
         case ROW_NUMBER:
            int var5 = 0;
            int var6 = var3.size();

            while(var5 < var6) {
               Integer var10001 = ((Value[])var3.get(var5))[var4].getInt();
               ++var5;
               var2.put(var10001, ValueBigint.get((long)var5));
            }

            return;
         case RANK:
         case DENSE_RANK:
         case PERCENT_RANK:
            this.getRank(var2, var3, var4);
            break;
         case CUME_DIST:
            this.getCumeDist(var2, var3, var4);
            break;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

   }

   private void getRank(HashMap<Integer, Value> var1, ArrayList<Value[]> var2, int var3) {
      int var4 = var2.size();
      int var5 = 0;

      for(int var6 = 0; var6 < var4; ++var6) {
         Value[] var7 = (Value[])var2.get(var6);
         if (var6 == 0) {
            var5 = 1;
         } else if (this.getOverOrderBySort().compare((Value[])var2.get(var6 - 1), var7) != 0) {
            if (this.type == WindowFunctionType.DENSE_RANK) {
               ++var5;
            } else {
               var5 = var6 + 1;
            }
         }

         Object var8;
         if (this.type == WindowFunctionType.PERCENT_RANK) {
            int var9 = var5 - 1;
            var8 = var9 == 0 ? ValueDouble.ZERO : ValueDouble.get((double)var9 / (double)(var4 - 1));
         } else {
            var8 = ValueBigint.get((long)var5);
         }

         var1.put(var7[var3].getInt(), var8);
      }

   }

   private void getCumeDist(HashMap<Integer, Value> var1, ArrayList<Value[]> var2, int var3) {
      int var4 = var2.size();

      int var7;
      for(int var5 = 0; var5 < var4; var5 = var7) {
         Value[] var6 = (Value[])var2.get(var5);

         for(var7 = var5 + 1; var7 < var4 && this.overOrderBySort.compare(var6, (Value[])var2.get(var7)) == 0; ++var7) {
         }

         ValueDouble var8 = ValueDouble.get((double)var7 / (double)var4);

         for(int var9 = var5; var9 < var7; ++var9) {
            int var10 = ((Value[])var2.get(var9))[var3].getInt();
            var1.put(var10, var8);
         }
      }

   }

   private static void getNtile(HashMap<Integer, Value> var0, ArrayList<Value[]> var1, int var2) {
      int var3 = var1.size();

      for(int var4 = 0; var4 < var3; ++var4) {
         Value[] var5 = (Value[])var1.get(var4);
         long var6 = var5[0].getLong();
         if (var6 <= 0L) {
            throw DbException.getInvalidValueException("number of tiles", var6);
         }

         long var8 = (long)var3 / var6;
         long var10 = (long)var3 - var8 * var6;
         long var12 = var10 * (var8 + 1L);
         long var14;
         if ((long)var4 >= var12) {
            var14 = ((long)var4 - var12) / var8 + var10 + 1L;
         } else {
            var14 = (long)var4 / (var8 + 1L) + 1L;
         }

         var0.put(((Value[])var1.get(var4))[var2].getInt(), ValueBigint.get(var14));
      }

   }

   private void getLeadLag(HashMap<Integer, Value> var1, ArrayList<Value[]> var2, int var3, SessionLocal var4) {
      int var5 = var2.size();
      int var6 = this.getNumExpressions();
      TypeInfo var7 = this.args[0].getType();

      for(int var8 = 0; var8 < var5; ++var8) {
         Value[] var9 = (Value[])var2.get(var8);
         int var10 = var9[var3].getInt();
         int var11;
         if (var6 >= 2) {
            var11 = var9[1].getInt();
            if (var11 < 0) {
               throw DbException.getInvalidValueException("nth row", var11);
            }
         } else {
            var11 = 1;
         }

         Object var12 = null;
         if (var11 == 0) {
            var12 = ((Value[])var2.get(var8))[0];
         } else {
            int var13;
            if (this.type == WindowFunctionType.LEAD) {
               if (!this.ignoreNulls) {
                  if (var11 <= var5 - var8 - 1) {
                     var12 = ((Value[])var2.get(var8 + var11))[0];
                  }
               } else {
                  for(var13 = var8 + 1; var11 > 0 && var13 < var5; ++var13) {
                     var12 = ((Value[])var2.get(var13))[0];
                     if (var12 != ValueNull.INSTANCE) {
                        --var11;
                     }
                  }

                  if (var11 > 0) {
                     var12 = null;
                  }
               }
            } else if (!this.ignoreNulls) {
               if (var11 <= var8) {
                  var12 = ((Value[])var2.get(var8 - var11))[0];
               }
            } else {
               for(var13 = var8 - 1; var11 > 0 && var13 >= 0; --var13) {
                  var12 = ((Value[])var2.get(var13))[0];
                  if (var12 != ValueNull.INSTANCE) {
                     --var11;
                  }
               }

               if (var11 > 0) {
                  var12 = null;
               }
            }
         }

         if (var12 == null) {
            if (var6 >= 3) {
               var12 = var9[2].convertTo(var7, var4);
            } else {
               var12 = ValueNull.INSTANCE;
            }
         }

         var1.put(var10, var12);
      }

   }

   private void getNth(SessionLocal var1, HashMap<Integer, Value> var2, ArrayList<Value[]> var3, int var4) {
      int var5 = var3.size();

      for(int var6 = 0; var6 < var5; ++var6) {
         Value[] var7 = (Value[])var3.get(var6);
         int var8 = var7[var4].getInt();
         Value var9;
         switch (this.type) {
            case FIRST_VALUE:
               var9 = getNthValue(WindowFrame.iterator(this.over, var1, var3, this.getOverOrderBySort(), var6, false), 0, this.ignoreNulls);
               break;
            case LAST_VALUE:
               var9 = getNthValue(WindowFrame.iterator(this.over, var1, var3, this.getOverOrderBySort(), var6, true), 0, this.ignoreNulls);
               break;
            case RATIO_TO_REPORT:
            default:
               throw DbException.getInternalError("type=" + this.type);
            case NTH_VALUE:
               int var10 = var7[1].getInt();
               if (var10 <= 0) {
                  throw DbException.getInvalidValueException("nth row", var10);
               }

               --var10;
               Iterator var11 = WindowFrame.iterator(this.over, var1, var3, this.getOverOrderBySort(), var6, this.fromLast);
               var9 = getNthValue(var11, var10, this.ignoreNulls);
         }

         var2.put(var8, var9);
      }

   }

   private static void getRatioToReport(HashMap<Integer, Value> var0, ArrayList<Value[]> var1, int var2) {
      int var3 = var1.size();
      Object var4 = null;

      int var5;
      for(var5 = 0; var5 < var3; ++var5) {
         Value var6 = ((Value[])var1.get(var5))[0];
         if (var6 != ValueNull.INSTANCE) {
            if (var4 == null) {
               var4 = var6.convertToDouble();
            } else {
               var4 = ((Value)var4).add(var6.convertToDouble());
            }
         }
      }

      if (var4 != null && ((Value)var4).getSignum() == 0) {
         var4 = null;
      }

      for(var5 = 0; var5 < var3; ++var5) {
         Value[] var8 = (Value[])var1.get(var5);
         Object var7;
         if (var4 == null) {
            var7 = ValueNull.INSTANCE;
         } else {
            var7 = var8[0];
            if (var7 != ValueNull.INSTANCE) {
               var7 = ((Value)var7).convertToDouble().divide((Value)var4, TypeInfo.TYPE_DOUBLE);
            }
         }

         var0.put(var8[var2].getInt(), var7);
      }

   }

   protected Value getAggregatedValue(SessionLocal var1, Object var2) {
      throw DbException.getUnsupportedException("Window function");
   }

   public void mapColumnsAnalysis(ColumnResolver var1, int var2, int var3) {
      if (this.args != null) {
         Expression[] var4 = this.args;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Expression var7 = var4[var6];
            var7.mapColumns(var1, var2, var3);
         }
      }

      super.mapColumnsAnalysis(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      String var2;
      if (this.over.getWindowFrame() != null) {
         switch (this.type) {
            case FIRST_VALUE:
            case LAST_VALUE:
            case NTH_VALUE:
               break;
            case RATIO_TO_REPORT:
            default:
               var2 = this.getTraceSQL();
               throw DbException.getSyntaxError(var2, var2.length() - 1);
         }
      }

      if (this.over.getOrderBy() == null) {
         if (this.type.requiresWindowOrdering()) {
            var2 = this.getTraceSQL();
            throw DbException.getSyntaxError(var2, var2.length() - 1, "ORDER BY");
         }
      } else if (this.type == WindowFunctionType.RATIO_TO_REPORT) {
         var2 = this.getTraceSQL();
         throw DbException.getSyntaxError(var2, var2.length() - 1);
      }

      super.optimize(var1);
      if (this.over.getOrderBy() == null) {
         switch (this.type) {
            case RANK:
            case DENSE_RANK:
               return ValueExpression.get(ValueBigint.get(1L));
            case PERCENT_RANK:
               return ValueExpression.get(ValueDouble.ZERO);
            case CUME_DIST:
               return ValueExpression.get(ValueDouble.ONE);
         }
      }

      if (this.args != null) {
         for(int var3 = 0; var3 < this.args.length; ++var3) {
            this.args[var3] = this.args[var3].optimize(var1);
         }
      }

      return this;
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      if (this.args != null) {
         Expression[] var3 = this.args;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Expression var6 = var3[var5];
            var6.setEvaluatable(var1, var2);
         }
      }

      super.setEvaluatable(var1, var2);
   }

   public TypeInfo getType() {
      switch (this.type) {
         case NTILE:
         case ROW_NUMBER:
         case RANK:
         case DENSE_RANK:
            return TypeInfo.TYPE_BIGINT;
         case LEAD:
         case LAG:
         case FIRST_VALUE:
         case LAST_VALUE:
         case NTH_VALUE:
            return this.args[0].getType();
         case RATIO_TO_REPORT:
         case PERCENT_RANK:
         case CUME_DIST:
            return TypeInfo.TYPE_DOUBLE;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      var1.append(this.type.getSQL()).append('(');
      if (this.args != null) {
         writeExpressions(var1, this.args, var2);
      }

      var1.append(')');
      if (this.fromLast && this.type == WindowFunctionType.NTH_VALUE) {
         var1.append(" FROM LAST");
      }

      if (this.ignoreNulls) {
         switch (this.type) {
            case LEAD:
            case LAG:
            case FIRST_VALUE:
            case LAST_VALUE:
            case NTH_VALUE:
               var1.append(" IGNORE NULLS");
            case RATIO_TO_REPORT:
         }
      }

      return this.appendTailConditions(var1, var2, this.type.requiresWindowOrdering());
   }

   public int getCost() {
      int var1 = 1;
      if (this.args != null) {
         Expression[] var2 = this.args;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Expression var5 = var2[var4];
            var1 += var5.getCost();
         }
      }

      return var1;
   }
}
