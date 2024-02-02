package org.h2.expression.aggregate;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.h2.command.query.QueryOrderBy;
import org.h2.command.query.Select;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.expression.Expression;
import org.h2.expression.ExpressionColumn;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ExpressionWithFlags;
import org.h2.expression.ValueExpression;
import org.h2.expression.analysis.Window;
import org.h2.expression.function.BitFunction;
import org.h2.expression.function.JsonConstructorFunction;
import org.h2.index.Cursor;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mvstore.db.MVSpatialIndex;
import org.h2.result.SearchRow;
import org.h2.result.SortOrder;
import org.h2.table.Column;
import org.h2.table.ColumnResolver;
import org.h2.table.Table;
import org.h2.table.TableFilter;
import org.h2.util.StringUtils;
import org.h2.util.json.JsonConstructorUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfo;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;
import org.h2.value.ValueRow;
import org.h2.value.ValueVarchar;

public class Aggregate extends AbstractAggregate implements ExpressionWithFlags {
   private static final int ADDITIONAL_SUM_PRECISION = 10;
   private static final int ADDITIONAL_AVG_SCALE = 10;
   private static final HashMap<String, AggregateType> AGGREGATES = new HashMap(128);
   private final AggregateType aggregateType;
   private ArrayList<QueryOrderBy> orderByList;
   private SortOrder orderBySort;
   private Object extraArguments;
   private int flags;

   public Aggregate(AggregateType var1, Expression[] var2, Select var3, boolean var4) {
      super(var3, var2, var4);
      if (var4 && var1 == AggregateType.COUNT_ALL) {
         throw DbException.getInternalError();
      } else {
         this.aggregateType = var1;
      }
   }

   private static void addAggregate(String var0, AggregateType var1) {
      AGGREGATES.put(var0, var1);
   }

   public static AggregateType getAggregateType(String var0) {
      return (AggregateType)AGGREGATES.get(var0);
   }

   public void setOrderByList(ArrayList<QueryOrderBy> var1) {
      this.orderByList = var1;
   }

   public AggregateType getAggregateType() {
      return this.aggregateType;
   }

   public void setExtraArguments(Object var1) {
      this.extraArguments = var1;
   }

   public Object getExtraArguments() {
      return this.extraArguments;
   }

   public void setFlags(int var1) {
      this.flags = var1;
   }

   public int getFlags() {
      return this.flags;
   }

   private void sortWithOrderBy(Value[] var1) {
      SortOrder var2 = this.orderBySort;
      Arrays.sort(var1, (Comparator)(var2 != null ? (var1x, var2x) -> {
         return var2.compare(((ValueRow)var1x).getList(), ((ValueRow)var2x).getList());
      } : this.select.getSession().getDatabase().getCompareMode()));
   }

   protected void updateAggregate(SessionLocal var1, Object var2) {
      AggregateData var3 = (AggregateData)var2;
      Value var4 = this.args.length == 0 ? null : this.args[0].getValue(var1);
      this.updateData(var1, var3, var4, (Value[])null);
   }

   private void updateData(SessionLocal var1, AggregateData var2, Value var3, Value[] var4) {
      switch (this.aggregateType) {
         case COVAR_POP:
         case COVAR_SAMP:
         case CORR:
         case REGR_SLOPE:
         case REGR_INTERCEPT:
         case REGR_R2:
         case REGR_SXY:
            Value var8;
            if (var3 != ValueNull.INSTANCE && (var8 = this.getSecondValue(var1, var4)) != ValueNull.INSTANCE) {
               ((AggregateDataBinarySet)var2).add(var1, (Value)var3, var8);
               return;
            }

            return;
         case REGR_COUNT:
         case REGR_AVGY:
         case REGR_SYY:
            if (var3 == ValueNull.INSTANCE || this.getSecondValue(var1, var4) == ValueNull.INSTANCE) {
               return;
            }
            break;
         case REGR_AVGX:
         case REGR_SXX:
            if (var3 != ValueNull.INSTANCE && (var3 = this.getSecondValue(var1, var4)) != ValueNull.INSTANCE) {
               break;
            }

            return;
         case LISTAGG:
            if (var3 == ValueNull.INSTANCE) {
               return;
            }

            var3 = this.updateCollecting(var1, ((Value)var3).convertTo(TypeInfo.TYPE_VARCHAR), var4);
            break;
         case ARRAY_AGG:
            var3 = this.updateCollecting(var1, (Value)var3, var4);
            break;
         case RANK:
         case DENSE_RANK:
         case PERCENT_RANK:
         case CUME_DIST:
            int var5 = this.args.length;
            Value[] var9 = new Value[var5];

            int var7;
            for(var7 = 0; var7 < var5; ++var7) {
               var9[var7] = var4 != null ? var4[var7] : this.args[var7].getValue(var1);
            }

            ((AggregateDataCollecting)var2).setSharedArgument(ValueRow.get(var9));
            var9 = new Value[var5];

            for(var7 = 0; var7 < var5; ++var7) {
               var9[var7] = var4 != null ? var4[var5 + var7] : ((QueryOrderBy)this.orderByList.get(var7)).expression.getValue(var1);
            }

            var3 = ValueRow.get(var9);
            break;
         case PERCENTILE_CONT:
         case PERCENTILE_DISC:
            ((AggregateDataCollecting)var2).setSharedArgument((Value)var3);
            var3 = var4 != null ? var4[1] : ((QueryOrderBy)this.orderByList.get(0)).expression.getValue(var1);
            break;
         case MODE:
            var3 = var4 != null ? var4[0] : ((QueryOrderBy)this.orderByList.get(0)).expression.getValue(var1);
            break;
         case JSON_ARRAYAGG:
            var3 = this.updateCollecting(var1, (Value)var3, var4);
            break;
         case JSON_OBJECTAGG:
            Value var6 = this.getSecondValue(var1, var4);
            if (var3 == ValueNull.INSTANCE) {
               throw DbException.getInvalidValueException("JSON_OBJECTAGG key", "NULL");
            }

            var3 = ValueRow.get(new Value[]{(Value)var3, var6});
      }

      var2.add(var1, (Value)var3);
   }

   private Value getSecondValue(SessionLocal var1, Value[] var2) {
      return var2 != null ? var2[1] : this.args[1].getValue(var1);
   }

   protected void updateGroupAggregates(SessionLocal var1, int var2) {
      super.updateGroupAggregates(var1, var2);
      Expression[] var3 = this.args;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Expression var6 = var3[var5];
         var6.updateAggregate(var1, var2);
      }

      if (this.orderByList != null) {
         Iterator var7 = this.orderByList.iterator();

         while(var7.hasNext()) {
            QueryOrderBy var8 = (QueryOrderBy)var7.next();
            var8.expression.updateAggregate(var1, var2);
         }
      }

   }

   private Value updateCollecting(SessionLocal var1, Value var2, Value[] var3) {
      if (this.orderByList != null) {
         int var4 = this.orderByList.size();
         Value[] var5 = new Value[1 + var4];
         var5[0] = (Value)var2;
         if (var3 == null) {
            for(int var6 = 0; var6 < var4; ++var6) {
               QueryOrderBy var7 = (QueryOrderBy)this.orderByList.get(var6);
               var5[var6 + 1] = var7.expression.getValue(var1);
            }
         } else {
            System.arraycopy(var3, 1, var5, 1, var4);
         }

         var2 = ValueRow.get(var5);
      }

      return (Value)var2;
   }

   protected int getNumExpressions() {
      int var1 = this.args.length;
      if (this.orderByList != null) {
         var1 += this.orderByList.size();
      }

      if (this.filterCondition != null) {
         ++var1;
      }

      return var1;
   }

   protected void rememberExpressions(SessionLocal var1, Value[] var2) {
      int var3 = 0;
      Expression[] var4 = this.args;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Expression var7 = var4[var6];
         var2[var3++] = var7.getValue(var1);
      }

      QueryOrderBy var9;
      if (this.orderByList != null) {
         for(Iterator var8 = this.orderByList.iterator(); var8.hasNext(); var2[var3++] = var9.expression.getValue(var1)) {
            var9 = (QueryOrderBy)var8.next();
         }
      }

      if (this.filterCondition != null) {
         var2[var3] = ValueBoolean.get(this.filterCondition.getBooleanValue(var1));
      }

   }

   protected void updateFromExpressions(SessionLocal var1, Object var2, Value[] var3) {
      if (this.filterCondition == null || var3[this.getNumExpressions() - 1].isTrue()) {
         AggregateData var4 = (AggregateData)var2;
         Value var5 = this.args.length == 0 ? null : var3[0];
         this.updateData(var1, var4, var5, var3);
      }

   }

   protected Object createAggregateData() {
      switch (this.aggregateType) {
         case COVAR_POP:
         case COVAR_SAMP:
         case REGR_SXY:
            return new AggregateDataCovar(this.aggregateType);
         case CORR:
         case REGR_SLOPE:
         case REGR_INTERCEPT:
         case REGR_R2:
            return new AggregateDataCorr(this.aggregateType);
         case REGR_COUNT:
         case COUNT_ALL:
            return new AggregateDataCount(true);
         case LISTAGG:
         case ARRAY_AGG:
            return new AggregateDataCollecting(this.distinct, this.orderByList != null, AggregateDataCollecting.NullCollectionMode.USED_OR_IMPOSSIBLE);
         case RANK:
         case DENSE_RANK:
         case PERCENT_RANK:
         case CUME_DIST:
         case PERCENTILE_CONT:
         case PERCENTILE_DISC:
         case MEDIAN:
            break;
         case MODE:
            return new AggregateDataDistinctWithCounts(true, Integer.MAX_VALUE);
         case JSON_ARRAYAGG:
            return new AggregateDataCollecting(this.distinct, this.orderByList != null, (this.flags & 1) != 0 ? AggregateDataCollecting.NullCollectionMode.EXCLUDED : AggregateDataCollecting.NullCollectionMode.USED_OR_IMPOSSIBLE);
         case JSON_OBJECTAGG:
            return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.USED_OR_IMPOSSIBLE);
         case COUNT:
            if (!this.distinct) {
               return new AggregateDataCount(false);
            }
            break;
         case SUM:
         case BIT_XOR_AGG:
         case BIT_XNOR_AGG:
            if (this.distinct) {
               break;
            }
         case MIN:
         case MAX:
         case BIT_AND_AGG:
         case BIT_OR_AGG:
         case BIT_NAND_AGG:
         case BIT_NOR_AGG:
         case ANY:
         case EVERY:
            return new AggregateDataDefault(this.aggregateType, this.type);
         case AVG:
            if (this.distinct) {
               break;
            }
         case REGR_AVGY:
         case REGR_AVGX:
            return new AggregateDataAvg(this.type);
         case STDDEV_POP:
         case STDDEV_SAMP:
         case VAR_POP:
         case VAR_SAMP:
            if (this.distinct) {
               break;
            }
         case REGR_SYY:
         case REGR_SXX:
            return new AggregateDataStdVar(this.aggregateType);
         case HISTOGRAM:
            return new AggregateDataDistinctWithCounts(false, 10000);
         case ENVELOPE:
            return new AggregateDataEnvelope();
         default:
            throw DbException.getInternalError("type=" + this.aggregateType);
      }

      return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.IGNORED);
   }

   public Value getValue(SessionLocal var1) {
      return this.select.isQuickAggregateQuery() ? this.getValueQuick(var1) : super.getValue(var1);
   }

   private Value getValueQuick(SessionLocal var1) {
      switch (this.aggregateType) {
         case PERCENTILE_CONT:
         case PERCENTILE_DISC:
            Value var9 = this.args[0].getValue(var1);
            if (var9 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            } else {
               BigDecimal var10 = var9.getBigDecimal();
               if (var10.signum() >= 0 && var10.compareTo(BigDecimal.ONE) <= 0) {
                  return Percentile.getFromIndex(var1, ((QueryOrderBy)this.orderByList.get(0)).expression, this.type.getValueType(), this.orderByList, var10, this.aggregateType == AggregateType.PERCENTILE_CONT);
               }

               throw DbException.getInvalidValueException(this.aggregateType == AggregateType.PERCENTILE_CONT ? "PERCENTILE_CONT argument" : "PERCENTILE_DISC argument", var10);
            }
         case MODE:
         case JSON_ARRAYAGG:
         case JSON_OBJECTAGG:
         case SUM:
         case BIT_XOR_AGG:
         case BIT_XNOR_AGG:
         case BIT_AND_AGG:
         case BIT_OR_AGG:
         case BIT_NAND_AGG:
         case BIT_NOR_AGG:
         case ANY:
         case EVERY:
         case AVG:
         case STDDEV_POP:
         case STDDEV_SAMP:
         case VAR_POP:
         case VAR_SAMP:
         case HISTOGRAM:
         default:
            throw DbException.getInternalError("type=" + this.aggregateType);
         case COUNT_ALL:
         case COUNT:
            Table var2 = this.select.getTopTableFilter().getTable();
            return ValueBigint.get(var2.getRowCount(var1));
         case MEDIAN:
            return Percentile.getFromIndex(var1, this.args[0], this.type.getValueType(), this.orderByList, Percentile.HALF, true);
         case MIN:
         case MAX:
            boolean var3 = this.aggregateType == AggregateType.MIN;
            Index var4 = this.getMinMaxColumnIndex();
            int var5 = var4.getIndexColumns()[0].sortType;
            if ((var5 & 1) != 0) {
               var3 = !var3;
            }

            Cursor var6 = var4.findFirstOrLast(var1, var3);
            SearchRow var7 = var6.getSearchRow();
            Object var8;
            if (var7 == null) {
               var8 = ValueNull.INSTANCE;
            } else {
               var8 = var7.getValue(var4.getColumns()[0].getColumnId());
            }

            return (Value)var8;
         case ENVELOPE:
            return ((MVSpatialIndex)AggregateDataEnvelope.getGeometryColumnIndex(this.args[0])).getBounds(var1);
      }
   }

   public Value getAggregatedValue(SessionLocal var1, Object var2) {
      AggregateData var3 = (AggregateData)var2;
      if (var3 == null) {
         var3 = (AggregateData)this.createAggregateData();
      }

      AggregateDataCollecting var4;
      ByteArrayOutputStream var5;
      Value[] var6;
      int var7;
      int var8;
      Value var9;
      Value[] var13;
      switch (this.aggregateType) {
         case LISTAGG:
            return this.getListagg(var1, var3);
         case ARRAY_AGG:
            var13 = ((AggregateDataCollecting)var3).getArray();
            if (var13 == null) {
               return ValueNull.INSTANCE;
            }

            if (this.orderByList != null || this.distinct) {
               this.sortWithOrderBy(var13);
            }

            if (this.orderByList != null) {
               for(int var15 = 0; var15 < var13.length; ++var15) {
                  var13[var15] = ((ValueRow)var13[var15]).getList()[0];
               }
            }

            return ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), var13, var1);
         case RANK:
         case DENSE_RANK:
         case PERCENT_RANK:
         case CUME_DIST:
            return this.getHypotheticalSet(var1, var3);
         case PERCENTILE_CONT:
         case PERCENTILE_DISC:
            var4 = (AggregateDataCollecting)var3;
            Value[] var14 = var4.getArray();
            if (var14 == null) {
               return ValueNull.INSTANCE;
            }

            Value var16 = var4.getSharedArgument();
            if (var16 == ValueNull.INSTANCE) {
               return ValueNull.INSTANCE;
            }

            BigDecimal var17 = var16.getBigDecimal();
            if (var17.signum() >= 0 && var17.compareTo(BigDecimal.ONE) <= 0) {
               return Percentile.getValue(var1, var14, this.type.getValueType(), this.orderByList, var17, this.aggregateType == AggregateType.PERCENTILE_CONT);
            }

            throw DbException.getInvalidValueException(this.aggregateType == AggregateType.PERCENTILE_CONT ? "PERCENTILE_CONT argument" : "PERCENTILE_DISC argument", var17);
         case MODE:
            return this.getMode(var1, var3);
         case JSON_ARRAYAGG:
            var13 = ((AggregateDataCollecting)var3).getArray();
            if (var13 == null) {
               return ValueNull.INSTANCE;
            }

            if (this.orderByList != null) {
               this.sortWithOrderBy(var13);
            }

            var5 = new ByteArrayOutputStream();
            var5.write(91);
            var6 = var13;
            var7 = var13.length;

            for(var8 = 0; var8 < var7; ++var8) {
               var9 = var6[var8];
               if (this.orderByList != null) {
                  var9 = ((ValueRow)var9).getList()[0];
               }

               JsonConstructorUtils.jsonArrayAppend(var5, (Value)(var9 != ValueNull.INSTANCE ? var9 : ValueJson.NULL), this.flags);
            }

            var5.write(93);
            return ValueJson.getInternal(var5.toByteArray());
         case JSON_OBJECTAGG:
            var13 = ((AggregateDataCollecting)var3).getArray();
            if (var13 == null) {
               return ValueNull.INSTANCE;
            }

            var5 = new ByteArrayOutputStream();
            var5.write(123);
            var6 = var13;
            var7 = var13.length;

            for(var8 = 0; var8 < var7; ++var8) {
               var9 = var6[var8];
               Value[] var10 = ((ValueRow)var9).getList();
               String var11 = var10[0].getString();
               if (var11 == null) {
                  throw DbException.getInvalidValueException("JSON_OBJECTAGG key", "NULL");
               }

               Object var12 = var10[1];
               if (var12 == ValueNull.INSTANCE) {
                  if ((this.flags & 1) != 0) {
                     continue;
                  }

                  var12 = ValueJson.NULL;
               }

               JsonConstructorUtils.jsonObjectAppend(var5, var11, (Value)var12);
            }

            return JsonConstructorUtils.jsonObjectFinish(var5, this.flags);
         case COUNT_ALL:
         case MIN:
         case MAX:
         case BIT_AND_AGG:
         case BIT_OR_AGG:
         case BIT_NAND_AGG:
         case BIT_NOR_AGG:
         case ANY:
         case EVERY:
         default:
            break;
         case COUNT:
            if (this.distinct) {
               return ValueBigint.get((long)((AggregateDataCollecting)var3).getCount());
            }
            break;
         case MEDIAN:
            var13 = ((AggregateDataCollecting)var3).getArray();
            if (var13 == null) {
               return ValueNull.INSTANCE;
            }

            return Percentile.getValue(var1, var13, this.type.getValueType(), this.orderByList, Percentile.HALF, true);
         case SUM:
         case BIT_XOR_AGG:
         case BIT_XNOR_AGG:
            if (this.distinct) {
               var4 = (AggregateDataCollecting)var3;
               if (var4.getCount() == 0) {
                  return ValueNull.INSTANCE;
               }

               return collect(var1, var4, new AggregateDataDefault(this.aggregateType, this.type));
            }
            break;
         case AVG:
            if (this.distinct) {
               var4 = (AggregateDataCollecting)var3;
               if (var4.getCount() == 0) {
                  return ValueNull.INSTANCE;
               }

               return collect(var1, var4, new AggregateDataAvg(this.type));
            }
            break;
         case STDDEV_POP:
         case STDDEV_SAMP:
         case VAR_POP:
         case VAR_SAMP:
            if (this.distinct) {
               var4 = (AggregateDataCollecting)var3;
               if (var4.getCount() == 0) {
                  return ValueNull.INSTANCE;
               }

               return collect(var1, var4, new AggregateDataStdVar(this.aggregateType));
            }
            break;
         case HISTOGRAM:
            return this.getHistogram(var1, var3);
      }

      return var3.getValue(var1);
   }

   private static Value collect(SessionLocal var0, AggregateDataCollecting var1, AggregateData var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Value var4 = (Value)var3.next();
         var2.add(var0, var4);
      }

      return var2.getValue(var0);
   }

   private Value getHypotheticalSet(SessionLocal var1, AggregateData var2) {
      AggregateDataCollecting var3 = (AggregateDataCollecting)var2;
      Value var4 = var3.getSharedArgument();
      if (var4 == null) {
         switch (this.aggregateType) {
            case RANK:
            case DENSE_RANK:
               return ValueInteger.get(1);
            case PERCENT_RANK:
               return ValueDouble.ZERO;
            case CUME_DIST:
               return ValueDouble.ONE;
            default:
               throw DbException.getUnsupportedException("aggregateType=" + this.aggregateType);
         }
      } else {
         var3.add(var1, var4);
         Value[] var5 = var3.getArray();
         Comparator var6 = this.orderBySort.getRowValueComparator();
         Arrays.sort(var5, var6);
         return this.aggregateType == AggregateType.CUME_DIST ? getCumeDist(var5, var4, var6) : this.getRank(var5, var4, var6);
      }
   }

   private Value getRank(Value[] var1, Value var2, Comparator<Value> var3) {
      int var4 = var1.length;
      int var5 = 0;

      for(int var6 = 0; var6 < var4; ++var6) {
         Value var7 = var1[var6];
         if (var6 == 0) {
            var5 = 1;
         } else if (var3.compare(var1[var6 - 1], var7) != 0) {
            if (this.aggregateType == AggregateType.DENSE_RANK) {
               ++var5;
            } else {
               var5 = var6 + 1;
            }
         }

         Object var8;
         if (this.aggregateType == AggregateType.PERCENT_RANK) {
            int var9 = var5 - 1;
            var8 = var9 == 0 ? ValueDouble.ZERO : ValueDouble.get((double)var9 / (double)(var4 - 1));
         } else {
            var8 = ValueBigint.get((long)var5);
         }

         if (var3.compare(var7, var2) == 0) {
            return (Value)var8;
         }
      }

      throw DbException.getInternalError();
   }

   private static Value getCumeDist(Value[] var0, Value var1, Comparator<Value> var2) {
      int var3 = var0.length;

      int var6;
      for(int var4 = 0; var4 < var3; var4 = var6) {
         Value var5 = var0[var4];

         for(var6 = var4 + 1; var6 < var3 && var2.compare(var5, var0[var6]) == 0; ++var6) {
         }

         ValueDouble var7 = ValueDouble.get((double)var6 / (double)var3);

         for(int var8 = var4; var8 < var6; ++var8) {
            if (var2.compare(var0[var8], var1) == 0) {
               return var7;
            }
         }
      }

      throw DbException.getInternalError();
   }

   private Value getListagg(SessionLocal var1, AggregateData var2) {
      AggregateDataCollecting var3 = (AggregateDataCollecting)var2;
      Value[] var4 = var3.getArray();
      if (var4 == null) {
         return ValueNull.INSTANCE;
      } else if (var4.length == 1) {
         Value var7 = var4[0];
         if (this.orderByList != null) {
            var7 = ((ValueRow)var7).getList()[0];
         }

         return var7.convertTo(2, var1);
      } else {
         if (this.orderByList != null || this.distinct) {
            this.sortWithOrderBy(var4);
         }

         ListaggArguments var5 = (ListaggArguments)this.extraArguments;
         String var6 = var5.getEffectiveSeparator();
         return ValueVarchar.get((var5.getOnOverflowTruncate() ? this.getListaggTruncate(var4, var6, var5.getEffectiveFilter(), var5.isWithoutCount()) : this.getListaggError(var4, var6)).toString(), var1);
      }
   }

   private StringBuilder getListaggError(Value[] var1, String var2) {
      StringBuilder var3 = new StringBuilder(this.getListaggItem(var1[0]));
      int var4 = 1;

      for(int var5 = var1.length; var4 < var5; ++var4) {
         var3.append(var2).append(this.getListaggItem(var1[var4]));
         if (var3.length() > 1048576) {
            throw DbException.getValueTooLongException("CHARACTER VARYING", var3.substring(0, 81), -1L);
         }
      }

      return var3;
   }

   private StringBuilder getListaggTruncate(Value[] var1, String var2, String var3, boolean var4) {
      int var5 = var1.length;
      String[] var6 = new String[var5];
      String var7 = this.getListaggItem(var1[0]);
      var6[0] = var7;
      StringBuilder var8 = new StringBuilder(var7);

      for(int var9 = 1; var9 < var5; ++var9) {
         var8.append(var2).append(var6[var9] = this.getListaggItem(var1[var9]));
         int var10 = var8.length();
         if (var10 > 1048576) {
            while(var9 > 0) {
               var10 -= var6[var9].length();
               var8.setLength(var10);
               var8.append(var3);
               if (!var4) {
                  var8.append('(').append(var5 - var9).append(')');
               }

               if (var8.length() <= 1048576) {
                  return var8;
               }

               var10 -= var2.length();
               --var9;
            }

            var8.setLength(0);
            var8.append(var3).append('(').append(var5).append(')');
            break;
         }
      }

      return var8;
   }

   private String getListaggItem(Value var1) {
      if (this.orderByList != null) {
         var1 = ((ValueRow)var1).getList()[0];
      }

      return var1.getString();
   }

   private Value getHistogram(SessionLocal var1, AggregateData var2) {
      TreeMap var3 = ((AggregateDataDistinctWithCounts)var2).getValues();
      TypeInfo var4 = (TypeInfo)this.type.getExtTypeInfo();
      if (var3 == null) {
         return ValueArray.get(var4, Value.EMPTY_VALUES, var1);
      } else {
         ValueRow[] var5 = new ValueRow[var3.size()];
         int var6 = 0;

         for(Iterator var7 = var3.entrySet().iterator(); var7.hasNext(); ++var6) {
            Map.Entry var8 = (Map.Entry)var7.next();
            LongDataCounter var9 = (LongDataCounter)var8.getValue();
            var5[var6] = ValueRow.get(var4, new Value[]{(Value)var8.getKey(), ValueBigint.get(var9.count)});
         }

         Database var10 = var1.getDatabase();
         CompareMode var11 = var10.getCompareMode();
         Arrays.sort(var5, (var2x, var3x) -> {
            return var2x.getList()[0].compareTo(var3x.getList()[0], var1, var11);
         });
         return ValueArray.get(var4, var5, var1);
      }
   }

   private Value getMode(SessionLocal var1, AggregateData var2) {
      Object var3 = ValueNull.INSTANCE;
      TreeMap var4 = ((AggregateDataDistinctWithCounts)var2).getValues();
      if (var4 == null) {
         return (Value)var3;
      } else {
         long var5 = 0L;
         if (this.orderByList != null) {
            boolean var7 = (((QueryOrderBy)this.orderByList.get(0)).sortType & 1) != 0;
            Iterator var8 = var4.entrySet().iterator();

            while(true) {
               while(var8.hasNext()) {
                  Map.Entry var9 = (Map.Entry)var8.next();
                  long var10 = ((LongDataCounter)var9.getValue()).count;
                  if (var10 > var5) {
                     var3 = (Value)var9.getKey();
                     var5 = var10;
                  } else if (var10 == var5) {
                     Value var12 = (Value)var9.getKey();
                     int var13 = var1.compareTypeSafe((Value)var3, var12);
                     if (var7) {
                        if (var13 >= 0) {
                           continue;
                        }
                     } else if (var13 <= 0) {
                        continue;
                     }

                     var3 = var12;
                  }
               }

               return (Value)var3;
            }
         } else {
            Iterator var14 = var4.entrySet().iterator();

            while(var14.hasNext()) {
               Map.Entry var15 = (Map.Entry)var14.next();
               long var16 = ((LongDataCounter)var15.getValue()).count;
               if (var16 > var5) {
                  var3 = (Value)var15.getKey();
                  var5 = var16;
               }
            }

            return (Value)var3;
         }
      }
   }

   public void mapColumnsAnalysis(ColumnResolver var1, int var2, int var3) {
      if (this.orderByList != null) {
         Iterator var4 = this.orderByList.iterator();

         while(var4.hasNext()) {
            QueryOrderBy var5 = (QueryOrderBy)var4.next();
            var5.expression.mapColumns(var1, var2, var3);
         }
      }

      super.mapColumnsAnalysis(var1, var2, var3);
   }

   public Expression optimize(SessionLocal var1) {
      super.optimize(var1);
      if (this.args.length == 1) {
         this.type = this.args[0].getType();
      }

      if (this.orderByList != null) {
         byte var2;
         switch (this.aggregateType) {
            case LISTAGG:
            case ARRAY_AGG:
            case JSON_ARRAYAGG:
               var2 = 1;
               break;
            default:
               var2 = 0;
         }

         Iterator var3 = this.orderByList.iterator();

         while(true) {
            while(var3.hasNext()) {
               QueryOrderBy var4 = (QueryOrderBy)var3.next();
               Expression var5 = var4.expression.optimize(var1);
               if (var2 != 0 && var5.isConstant()) {
                  var3.remove();
               } else {
                  var4.expression = var5;
               }
            }

            if (this.orderByList.isEmpty()) {
               this.orderByList = null;
            } else {
               this.orderBySort = createOrder(var1, this.orderByList, var2);
            }
            break;
         }
      }

      switch (this.aggregateType) {
         case COVAR_POP:
         case COVAR_SAMP:
         case CORR:
         case REGR_SLOPE:
         case REGR_INTERCEPT:
         case REGR_R2:
         case REGR_SXY:
         case REGR_SYY:
         case REGR_SXX:
         case STDDEV_POP:
         case STDDEV_SAMP:
         case VAR_POP:
         case VAR_SAMP:
            this.type = TypeInfo.TYPE_DOUBLE;
            break;
         case REGR_AVGY:
            if ((this.type = getAvgType(this.args[0].getType())) == null) {
               throw DbException.get(90015, this.getTraceSQL());
            }
            break;
         case REGR_AVGX:
            if ((this.type = getAvgType(this.args[1].getType())) == null) {
               throw DbException.get(90015, this.getTraceSQL());
            }
            break;
         case LISTAGG:
            this.type = TypeInfo.TYPE_VARCHAR;
            break;
         case ARRAY_AGG:
            this.type = TypeInfo.getTypeInfo(40, -1L, 0, this.args[0].getType());
            break;
         case RANK:
         case DENSE_RANK:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         case PERCENT_RANK:
         case CUME_DIST:
            this.type = TypeInfo.TYPE_DOUBLE;
            break;
         case PERCENTILE_CONT:
            this.type = ((QueryOrderBy)this.orderByList.get(0)).expression.getType();
         case MEDIAN:
            switch (this.type.getValueType()) {
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 15:
               case 16:
                  this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT;
                  return this;
               default:
                  return this;
            }
         case PERCENTILE_DISC:
         case MODE:
            this.type = ((QueryOrderBy)this.orderByList.get(0)).expression.getType();
            break;
         case JSON_ARRAYAGG:
         case JSON_OBJECTAGG:
            this.type = TypeInfo.TYPE_JSON;
            break;
         case COUNT:
            if (this.args[0].isConstant()) {
               if (this.args[0].getValue(var1) == ValueNull.INSTANCE) {
                  return ValueExpression.get(ValueBigint.get(0L));
               }

               if (!this.distinct) {
                  Aggregate var7 = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.select, false);
                  var7.setFilterCondition(this.filterCondition);
                  var7.setOverCondition(this.over);
                  return var7.optimize(var1);
               }
            }
         case REGR_COUNT:
         case COUNT_ALL:
            this.type = TypeInfo.TYPE_BIGINT;
            break;
         case SUM:
            if ((this.type = getSumType(this.type)) == null) {
               throw DbException.get(90015, this.getTraceSQL());
            }
            break;
         case BIT_XOR_AGG:
         case BIT_XNOR_AGG:
         case BIT_AND_AGG:
         case BIT_OR_AGG:
         case BIT_NAND_AGG:
         case BIT_NOR_AGG:
            BitFunction.checkArgType(this.args[0]);
         case MIN:
         case MAX:
            break;
         case ANY:
         case EVERY:
            this.type = TypeInfo.TYPE_BOOLEAN;
            break;
         case AVG:
            if ((this.type = getAvgType(this.type)) == null) {
               throw DbException.get(90015, this.getTraceSQL());
            }
            break;
         case HISTOGRAM:
            LinkedHashMap var6 = new LinkedHashMap(4);
            var6.put("VALUE", this.type);
            var6.put("COUNT", TypeInfo.TYPE_BIGINT);
            this.type = TypeInfo.getTypeInfo(40, -1L, 0, TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow(var6)));
            break;
         case ENVELOPE:
            this.type = TypeInfo.TYPE_GEOMETRY;
            break;
         default:
            throw DbException.getInternalError("type=" + this.aggregateType);
      }

      return this;
   }

   private static TypeInfo getSumType(TypeInfo var0) {
      int var1 = var0.getValueType();
      switch (var1) {
         case 8:
         case 9:
         case 10:
         case 11:
            return TypeInfo.TYPE_BIGINT;
         case 12:
            return TypeInfo.getTypeInfo(13, 29L, -1, (ExtTypeInfo)null);
         case 13:
            return TypeInfo.getTypeInfo(13, var0.getPrecision() + 10L, var0.getDeclaredScale(), (ExtTypeInfo)null);
         case 14:
            return TypeInfo.TYPE_DOUBLE;
         case 15:
            return TypeInfo.getTypeInfo(16, 27L, -1, (ExtTypeInfo)null);
         case 16:
            return TypeInfo.getTypeInfo(16, var0.getPrecision() + 10L, -1, (ExtTypeInfo)null);
         default:
            return DataType.isIntervalType(var1) ? TypeInfo.getTypeInfo(var1, 18L, var0.getDeclaredScale(), (ExtTypeInfo)null) : null;
      }
   }

   private static TypeInfo getAvgType(TypeInfo var0) {
      switch (var0.getValueType()) {
         case 9:
         case 10:
         case 11:
         case 14:
            return TypeInfo.TYPE_DOUBLE;
         case 12:
            return TypeInfo.getTypeInfo(13, 29L, 10, (ExtTypeInfo)null);
         case 13:
            int var1 = Math.min(100000 - var0.getScale(), Math.min(100000 - (int)var0.getPrecision(), 10));
            return TypeInfo.getTypeInfo(13, var0.getPrecision() + (long)var1, var0.getScale() + var1, (ExtTypeInfo)null);
         case 15:
            return TypeInfo.getTypeInfo(16, 27L, -1, (ExtTypeInfo)null);
         case 16:
            return TypeInfo.getTypeInfo(16, var0.getPrecision() + 10L, -1, (ExtTypeInfo)null);
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         default:
            return null;
         case 22:
         case 28:
            return TypeInfo.getTypeInfo(28, var0.getDeclaredPrecision(), 0, (ExtTypeInfo)null);
         case 23:
            return TypeInfo.getTypeInfo(23, var0.getDeclaredPrecision(), 0, (ExtTypeInfo)null);
         case 24:
         case 29:
         case 30:
         case 31:
            return TypeInfo.getTypeInfo(31, var0.getDeclaredPrecision(), 9, (ExtTypeInfo)null);
         case 25:
         case 32:
         case 33:
            return TypeInfo.getTypeInfo(33, var0.getDeclaredPrecision(), 9, (ExtTypeInfo)null);
         case 26:
         case 34:
            return TypeInfo.getTypeInfo(34, var0.getDeclaredPrecision(), 9, (ExtTypeInfo)null);
         case 27:
            return TypeInfo.getTypeInfo(27, var0.getDeclaredPrecision(), 9, (ExtTypeInfo)null);
      }
   }

   public void setEvaluatable(TableFilter var1, boolean var2) {
      if (this.orderByList != null) {
         Iterator var3 = this.orderByList.iterator();

         while(var3.hasNext()) {
            QueryOrderBy var4 = (QueryOrderBy)var3.next();
            var4.expression.setEvaluatable(var1, var2);
         }
      }

      super.setEvaluatable(var1, var2);
   }

   public StringBuilder getUnenclosedSQL(StringBuilder var1, int var2) {
      switch (this.aggregateType) {
         case LISTAGG:
            return this.getSQLListagg(var1, var2);
         case ARRAY_AGG:
            return this.getSQLArrayAggregate(var1, var2);
         case RANK:
         case DENSE_RANK:
         case PERCENT_RANK:
         case CUME_DIST:
         case PERCENTILE_CONT:
         case PERCENTILE_DISC:
         case MODE:
         default:
            var1.append(this.aggregateType.name());
            if (this.distinct) {
               var1.append("(DISTINCT ");
            } else {
               var1.append('(');
            }

            writeExpressions(var1, this.args, var2).append(')');
            if (this.orderByList != null) {
               var1.append(" WITHIN GROUP (");
               Window.appendOrderBy(var1, this.orderByList, var2, false);
               var1.append(')');
            }

            return this.appendTailConditions(var1, var2, false);
         case JSON_ARRAYAGG:
            return this.getSQLJsonArrayAggregate(var1, var2);
         case JSON_OBJECTAGG:
            return this.getSQLJsonObjectAggregate(var1, var2);
         case COUNT_ALL:
            return this.appendTailConditions(var1.append("COUNT(*)"), var2, false);
      }
   }

   private StringBuilder getSQLArrayAggregate(StringBuilder var1, int var2) {
      var1.append("ARRAY_AGG(");
      if (this.distinct) {
         var1.append("DISTINCT ");
      }

      this.args[0].getUnenclosedSQL(var1, var2);
      Window.appendOrderBy(var1, this.orderByList, var2, false);
      var1.append(')');
      return this.appendTailConditions(var1, var2, false);
   }

   private StringBuilder getSQLListagg(StringBuilder var1, int var2) {
      var1.append("LISTAGG(");
      if (this.distinct) {
         var1.append("DISTINCT ");
      }

      this.args[0].getUnenclosedSQL(var1, var2);
      ListaggArguments var3 = (ListaggArguments)this.extraArguments;
      String var4 = var3.getSeparator();
      if (var4 != null) {
         StringUtils.quoteStringSQL(var1.append(", "), var4);
      }

      if (var3.getOnOverflowTruncate()) {
         var1.append(" ON OVERFLOW TRUNCATE ");
         var4 = var3.getFilter();
         if (var4 != null) {
            StringUtils.quoteStringSQL(var1, var4).append(' ');
         }

         var1.append(var3.isWithoutCount() ? "WITHOUT" : "WITH").append(" COUNT");
      }

      var1.append(')');
      var1.append(" WITHIN GROUP (");
      Window.appendOrderBy(var1, this.orderByList, var2, true);
      var1.append(')');
      return this.appendTailConditions(var1, var2, false);
   }

   private StringBuilder getSQLJsonObjectAggregate(StringBuilder var1, int var2) {
      var1.append("JSON_OBJECTAGG(");
      this.args[0].getUnenclosedSQL(var1, var2).append(": ");
      this.args[1].getUnenclosedSQL(var1, var2);
      JsonConstructorFunction.getJsonFunctionFlagsSQL(var1, this.flags, false).append(')');
      return this.appendTailConditions(var1, var2, false);
   }

   private StringBuilder getSQLJsonArrayAggregate(StringBuilder var1, int var2) {
      var1.append("JSON_ARRAYAGG(");
      if (this.distinct) {
         var1.append("DISTINCT ");
      }

      this.args[0].getUnenclosedSQL(var1, var2);
      JsonConstructorFunction.getJsonFunctionFlagsSQL(var1, this.flags, true);
      Window.appendOrderBy(var1, this.orderByList, var2, false);
      var1.append(')');
      return this.appendTailConditions(var1, var2, false);
   }

   private Index getMinMaxColumnIndex() {
      Expression var1 = this.args[0];
      if (var1 instanceof ExpressionColumn) {
         ExpressionColumn var2 = (ExpressionColumn)var1;
         Column var3 = var2.getColumn();
         TableFilter var4 = var2.getTableFilter();
         if (var4 != null) {
            Table var5 = var4.getTable();
            return var5.getIndexForColumn(var3, true, false);
         }
      }

      return null;
   }

   public boolean isEverything(ExpressionVisitor var1) {
      if (!super.isEverything(var1)) {
         return false;
      } else if (this.filterCondition != null && !this.filterCondition.isEverything(var1)) {
         return false;
      } else if (var1.getType() == 1) {
         switch (this.aggregateType) {
            case PERCENTILE_CONT:
            case PERCENTILE_DISC:
               return this.args[0].isConstant() && Percentile.getColumnIndex(this.select.getSession().getDatabase(), ((QueryOrderBy)this.orderByList.get(0)).expression) != null;
            case MODE:
            case JSON_ARRAYAGG:
            case JSON_OBJECTAGG:
            case SUM:
            case BIT_XOR_AGG:
            case BIT_XNOR_AGG:
            case BIT_AND_AGG:
            case BIT_OR_AGG:
            case BIT_NAND_AGG:
            case BIT_NOR_AGG:
            case ANY:
            case EVERY:
            case AVG:
            case STDDEV_POP:
            case STDDEV_SAMP:
            case VAR_POP:
            case VAR_SAMP:
            case HISTOGRAM:
            default:
               return false;
            case COUNT:
               if (this.distinct || this.args[0].getNullable() != 0) {
                  return false;
               }
            case COUNT_ALL:
               return var1.getTable().canGetRowCount(this.select.getSession());
            case MEDIAN:
               if (this.distinct) {
                  return false;
               }

               return Percentile.getColumnIndex(this.select.getSession().getDatabase(), this.args[0]) != null;
            case MIN:
            case MAX:
               return this.getMinMaxColumnIndex() != null;
            case ENVELOPE:
               return AggregateDataEnvelope.getGeometryColumnIndex(this.args[0]) != null;
         }
      } else {
         Expression[] var2 = this.args;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Expression var5 = var2[var4];
            if (!var5.isEverything(var1)) {
               return false;
            }
         }

         if (this.orderByList != null) {
            Iterator var6 = this.orderByList.iterator();

            while(var6.hasNext()) {
               QueryOrderBy var7 = (QueryOrderBy)var6.next();
               if (!var7.expression.isEverything(var1)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public int getCost() {
      int var1 = 1;
      Expression[] var2 = this.args;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Expression var5 = var2[var4];
         var1 += var5.getCost();
      }

      QueryOrderBy var7;
      if (this.orderByList != null) {
         for(Iterator var6 = this.orderByList.iterator(); var6.hasNext(); var1 += var7.expression.getCost()) {
            var7 = (QueryOrderBy)var6.next();
         }
      }

      if (this.filterCondition != null) {
         var1 += this.filterCondition.getCost();
      }

      return var1;
   }

   public Select getSelect() {
      return this.select;
   }

   public boolean isDistinct() {
      return this.distinct;
   }

   static {
      addAggregate("COUNT", AggregateType.COUNT);
      addAggregate("SUM", AggregateType.SUM);
      addAggregate("MIN", AggregateType.MIN);
      addAggregate("MAX", AggregateType.MAX);
      addAggregate("AVG", AggregateType.AVG);
      addAggregate("LISTAGG", AggregateType.LISTAGG);
      addAggregate("GROUP_CONCAT", AggregateType.LISTAGG);
      addAggregate("STRING_AGG", AggregateType.LISTAGG);
      addAggregate("STDDEV_SAMP", AggregateType.STDDEV_SAMP);
      addAggregate("STDDEV", AggregateType.STDDEV_SAMP);
      addAggregate("STDDEV_POP", AggregateType.STDDEV_POP);
      addAggregate("STDDEVP", AggregateType.STDDEV_POP);
      addAggregate("VAR_POP", AggregateType.VAR_POP);
      addAggregate("VARP", AggregateType.VAR_POP);
      addAggregate("VAR_SAMP", AggregateType.VAR_SAMP);
      addAggregate("VAR", AggregateType.VAR_SAMP);
      addAggregate("VARIANCE", AggregateType.VAR_SAMP);
      addAggregate("ANY", AggregateType.ANY);
      addAggregate("SOME", AggregateType.ANY);
      addAggregate("BOOL_OR", AggregateType.ANY);
      addAggregate("EVERY", AggregateType.EVERY);
      addAggregate("BOOL_AND", AggregateType.EVERY);
      addAggregate("HISTOGRAM", AggregateType.HISTOGRAM);
      addAggregate("BIT_AND_AGG", AggregateType.BIT_AND_AGG);
      addAggregate("BIT_AND", AggregateType.BIT_AND_AGG);
      addAggregate("BIT_OR_AGG", AggregateType.BIT_OR_AGG);
      addAggregate("BIT_OR", AggregateType.BIT_OR_AGG);
      addAggregate("BIT_XOR_AGG", AggregateType.BIT_XOR_AGG);
      addAggregate("BIT_NAND_AGG", AggregateType.BIT_NAND_AGG);
      addAggregate("BIT_NOR_AGG", AggregateType.BIT_NOR_AGG);
      addAggregate("BIT_XNOR_AGG", AggregateType.BIT_XNOR_AGG);
      addAggregate("COVAR_POP", AggregateType.COVAR_POP);
      addAggregate("COVAR_SAMP", AggregateType.COVAR_SAMP);
      addAggregate("CORR", AggregateType.CORR);
      addAggregate("REGR_SLOPE", AggregateType.REGR_SLOPE);
      addAggregate("REGR_INTERCEPT", AggregateType.REGR_INTERCEPT);
      addAggregate("REGR_COUNT", AggregateType.REGR_COUNT);
      addAggregate("REGR_R2", AggregateType.REGR_R2);
      addAggregate("REGR_AVGX", AggregateType.REGR_AVGX);
      addAggregate("REGR_AVGY", AggregateType.REGR_AVGY);
      addAggregate("REGR_SXX", AggregateType.REGR_SXX);
      addAggregate("REGR_SYY", AggregateType.REGR_SYY);
      addAggregate("REGR_SXY", AggregateType.REGR_SXY);
      addAggregate("RANK", AggregateType.RANK);
      addAggregate("DENSE_RANK", AggregateType.DENSE_RANK);
      addAggregate("PERCENT_RANK", AggregateType.PERCENT_RANK);
      addAggregate("CUME_DIST", AggregateType.CUME_DIST);
      addAggregate("PERCENTILE_CONT", AggregateType.PERCENTILE_CONT);
      addAggregate("PERCENTILE_DISC", AggregateType.PERCENTILE_DISC);
      addAggregate("MEDIAN", AggregateType.MEDIAN);
      addAggregate("ARRAY_AGG", AggregateType.ARRAY_AGG);
      addAggregate("MODE", AggregateType.MODE);
      addAggregate("STATS_MODE", AggregateType.MODE);
      addAggregate("ENVELOPE", AggregateType.ENVELOPE);
      addAggregate("JSON_OBJECTAGG", AggregateType.JSON_OBJECTAGG);
      addAggregate("JSON_ARRAYAGG", AggregateType.JSON_ARRAYAGG);
   }
}
