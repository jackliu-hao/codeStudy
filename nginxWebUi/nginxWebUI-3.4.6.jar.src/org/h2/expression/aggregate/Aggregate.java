/*      */ package org.h2.expression.aggregate;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ import org.h2.command.query.QueryOrderBy;
/*      */ import org.h2.command.query.Select;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.ExpressionColumn;
/*      */ import org.h2.expression.ExpressionVisitor;
/*      */ import org.h2.expression.ExpressionWithFlags;
/*      */ import org.h2.expression.ValueExpression;
/*      */ import org.h2.expression.analysis.Window;
/*      */ import org.h2.expression.function.BitFunction;
/*      */ import org.h2.expression.function.JsonConstructorFunction;
/*      */ import org.h2.index.Cursor;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.mvstore.db.MVSpatialIndex;
/*      */ import org.h2.result.SearchRow;
/*      */ import org.h2.result.SortOrder;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.ColumnResolver;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.table.TableFilter;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.json.JsonConstructorUtils;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.ExtTypeInfo;
/*      */ import org.h2.value.ExtTypeInfoRow;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueArray;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueBoolean;
/*      */ import org.h2.value.ValueDouble;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueJson;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueRow;
/*      */ import org.h2.value.ValueVarchar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Aggregate
/*      */   extends AbstractAggregate
/*      */   implements ExpressionWithFlags
/*      */ {
/*      */   private static final int ADDITIONAL_SUM_PRECISION = 10;
/*      */   private static final int ADDITIONAL_AVG_SCALE = 10;
/*   78 */   private static final HashMap<String, AggregateType> AGGREGATES = new HashMap<>(128);
/*      */ 
/*      */ 
/*      */   
/*      */   private final AggregateType aggregateType;
/*      */ 
/*      */ 
/*      */   
/*      */   private ArrayList<QueryOrderBy> orderByList;
/*      */ 
/*      */ 
/*      */   
/*      */   private SortOrder orderBySort;
/*      */ 
/*      */ 
/*      */   
/*      */   private Object extraArguments;
/*      */ 
/*      */   
/*      */   private int flags;
/*      */ 
/*      */ 
/*      */   
/*      */   public Aggregate(AggregateType paramAggregateType, Expression[] paramArrayOfExpression, Select paramSelect, boolean paramBoolean) {
/*  102 */     super(paramSelect, paramArrayOfExpression, paramBoolean);
/*  103 */     if (paramBoolean && paramAggregateType == AggregateType.COUNT_ALL) {
/*  104 */       throw DbException.getInternalError();
/*      */     }
/*  106 */     this.aggregateType = paramAggregateType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*  113 */     addAggregate("COUNT", AggregateType.COUNT);
/*  114 */     addAggregate("SUM", AggregateType.SUM);
/*  115 */     addAggregate("MIN", AggregateType.MIN);
/*  116 */     addAggregate("MAX", AggregateType.MAX);
/*  117 */     addAggregate("AVG", AggregateType.AVG);
/*  118 */     addAggregate("LISTAGG", AggregateType.LISTAGG);
/*      */     
/*  120 */     addAggregate("GROUP_CONCAT", AggregateType.LISTAGG);
/*      */     
/*  122 */     addAggregate("STRING_AGG", AggregateType.LISTAGG);
/*  123 */     addAggregate("STDDEV_SAMP", AggregateType.STDDEV_SAMP);
/*  124 */     addAggregate("STDDEV", AggregateType.STDDEV_SAMP);
/*  125 */     addAggregate("STDDEV_POP", AggregateType.STDDEV_POP);
/*  126 */     addAggregate("STDDEVP", AggregateType.STDDEV_POP);
/*  127 */     addAggregate("VAR_POP", AggregateType.VAR_POP);
/*  128 */     addAggregate("VARP", AggregateType.VAR_POP);
/*  129 */     addAggregate("VAR_SAMP", AggregateType.VAR_SAMP);
/*  130 */     addAggregate("VAR", AggregateType.VAR_SAMP);
/*  131 */     addAggregate("VARIANCE", AggregateType.VAR_SAMP);
/*  132 */     addAggregate("ANY", AggregateType.ANY);
/*  133 */     addAggregate("SOME", AggregateType.ANY);
/*      */     
/*  135 */     addAggregate("BOOL_OR", AggregateType.ANY);
/*  136 */     addAggregate("EVERY", AggregateType.EVERY);
/*      */     
/*  138 */     addAggregate("BOOL_AND", AggregateType.EVERY);
/*  139 */     addAggregate("HISTOGRAM", AggregateType.HISTOGRAM);
/*  140 */     addAggregate("BIT_AND_AGG", AggregateType.BIT_AND_AGG);
/*  141 */     addAggregate("BIT_AND", AggregateType.BIT_AND_AGG);
/*  142 */     addAggregate("BIT_OR_AGG", AggregateType.BIT_OR_AGG);
/*  143 */     addAggregate("BIT_OR", AggregateType.BIT_OR_AGG);
/*  144 */     addAggregate("BIT_XOR_AGG", AggregateType.BIT_XOR_AGG);
/*  145 */     addAggregate("BIT_NAND_AGG", AggregateType.BIT_NAND_AGG);
/*  146 */     addAggregate("BIT_NOR_AGG", AggregateType.BIT_NOR_AGG);
/*  147 */     addAggregate("BIT_XNOR_AGG", AggregateType.BIT_XNOR_AGG);
/*      */     
/*  149 */     addAggregate("COVAR_POP", AggregateType.COVAR_POP);
/*  150 */     addAggregate("COVAR_SAMP", AggregateType.COVAR_SAMP);
/*  151 */     addAggregate("CORR", AggregateType.CORR);
/*  152 */     addAggregate("REGR_SLOPE", AggregateType.REGR_SLOPE);
/*  153 */     addAggregate("REGR_INTERCEPT", AggregateType.REGR_INTERCEPT);
/*  154 */     addAggregate("REGR_COUNT", AggregateType.REGR_COUNT);
/*  155 */     addAggregate("REGR_R2", AggregateType.REGR_R2);
/*  156 */     addAggregate("REGR_AVGX", AggregateType.REGR_AVGX);
/*  157 */     addAggregate("REGR_AVGY", AggregateType.REGR_AVGY);
/*  158 */     addAggregate("REGR_SXX", AggregateType.REGR_SXX);
/*  159 */     addAggregate("REGR_SYY", AggregateType.REGR_SYY);
/*  160 */     addAggregate("REGR_SXY", AggregateType.REGR_SXY);
/*      */     
/*  162 */     addAggregate("RANK", AggregateType.RANK);
/*  163 */     addAggregate("DENSE_RANK", AggregateType.DENSE_RANK);
/*  164 */     addAggregate("PERCENT_RANK", AggregateType.PERCENT_RANK);
/*  165 */     addAggregate("CUME_DIST", AggregateType.CUME_DIST);
/*      */     
/*  167 */     addAggregate("PERCENTILE_CONT", AggregateType.PERCENTILE_CONT);
/*  168 */     addAggregate("PERCENTILE_DISC", AggregateType.PERCENTILE_DISC);
/*  169 */     addAggregate("MEDIAN", AggregateType.MEDIAN);
/*      */     
/*  171 */     addAggregate("ARRAY_AGG", AggregateType.ARRAY_AGG);
/*  172 */     addAggregate("MODE", AggregateType.MODE);
/*      */     
/*  174 */     addAggregate("STATS_MODE", AggregateType.MODE);
/*  175 */     addAggregate("ENVELOPE", AggregateType.ENVELOPE);
/*      */     
/*  177 */     addAggregate("JSON_OBJECTAGG", AggregateType.JSON_OBJECTAGG);
/*  178 */     addAggregate("JSON_ARRAYAGG", AggregateType.JSON_ARRAYAGG);
/*      */   }
/*      */   
/*      */   private static void addAggregate(String paramString, AggregateType paramAggregateType) {
/*  182 */     AGGREGATES.put(paramString, paramAggregateType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static AggregateType getAggregateType(String paramString) {
/*  195 */     return AGGREGATES.get(paramString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOrderByList(ArrayList<QueryOrderBy> paramArrayList) {
/*  205 */     this.orderByList = paramArrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public AggregateType getAggregateType() {
/*  214 */     return this.aggregateType;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setExtraArguments(Object paramObject) {
/*  223 */     this.extraArguments = paramObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getExtraArguments() {
/*  232 */     return this.extraArguments;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setFlags(int paramInt) {
/*  237 */     this.flags = paramInt;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getFlags() {
/*  242 */     return this.flags;
/*      */   }
/*      */   
/*      */   private void sortWithOrderBy(Value[] paramArrayOfValue) {
/*  246 */     SortOrder sortOrder = this.orderBySort;
/*  247 */     Arrays.sort(paramArrayOfValue, (sortOrder != null) ? ((paramValue1, paramValue2) -> paramSortOrder.compare(((ValueRow)paramValue1).getList(), ((ValueRow)paramValue2).getList())) : (Comparator<? super Value>)this.select
/*      */ 
/*      */         
/*  250 */         .getSession().getDatabase().getCompareMode());
/*      */   }
/*      */ 
/*      */   
/*      */   protected void updateAggregate(SessionLocal paramSessionLocal, Object paramObject) {
/*  255 */     AggregateData aggregateData = (AggregateData)paramObject;
/*  256 */     Value value = (this.args.length == 0) ? null : this.args[0].getValue(paramSessionLocal);
/*  257 */     updateData(paramSessionLocal, aggregateData, value, (Value[])null); } private void updateData(SessionLocal paramSessionLocal, AggregateData paramAggregateData, Value paramValue, Value[] paramArrayOfValue) { ValueRow valueRow2; Value value1; ValueRow valueRow1; Value value3;
/*      */     int i;
/*      */     Value value2, arrayOfValue[], value4;
/*      */     byte b;
/*  261 */     switch (this.aggregateType) {
/*      */       
/*      */       case COVAR_POP:
/*      */       case COVAR_SAMP:
/*      */       case CORR:
/*      */       case REGR_SLOPE:
/*      */       case REGR_INTERCEPT:
/*      */       case REGR_R2:
/*      */       case REGR_SXY:
/*  270 */         if (paramValue == ValueNull.INSTANCE || (value3 = getSecondValue(paramSessionLocal, paramArrayOfValue)) == ValueNull.INSTANCE) {
/*      */           return;
/*      */         }
/*  273 */         ((AggregateDataBinarySet)paramAggregateData).add(paramSessionLocal, paramValue, value3);
/*      */         return;
/*      */       
/*      */       case REGR_COUNT:
/*      */       case REGR_AVGY:
/*      */       case REGR_SYY:
/*  279 */         if (paramValue == ValueNull.INSTANCE || getSecondValue(paramSessionLocal, paramArrayOfValue) == ValueNull.INSTANCE) {
/*      */           return;
/*      */         }
/*      */         break;
/*      */       case REGR_AVGX:
/*      */       case REGR_SXX:
/*  285 */         if (paramValue == ValueNull.INSTANCE || (paramValue = getSecondValue(paramSessionLocal, paramArrayOfValue)) == ValueNull.INSTANCE) {
/*      */           return;
/*      */         }
/*      */         break;
/*      */       case LISTAGG:
/*  290 */         if (paramValue == ValueNull.INSTANCE) {
/*      */           return;
/*      */         }
/*  293 */         paramValue = updateCollecting(paramSessionLocal, paramValue.convertTo(TypeInfo.TYPE_VARCHAR), paramArrayOfValue);
/*      */         break;
/*      */       case ARRAY_AGG:
/*  296 */         paramValue = updateCollecting(paramSessionLocal, paramValue, paramArrayOfValue);
/*      */         break;
/*      */       case RANK:
/*      */       case DENSE_RANK:
/*      */       case PERCENT_RANK:
/*      */       case CUME_DIST:
/*  302 */         i = this.args.length;
/*  303 */         arrayOfValue = new Value[i];
/*  304 */         for (b = 0; b < i; b++) {
/*  305 */           arrayOfValue[b] = (paramArrayOfValue != null) ? paramArrayOfValue[b] : this.args[b].getValue(paramSessionLocal);
/*      */         }
/*  307 */         ((AggregateDataCollecting)paramAggregateData).setSharedArgument((Value)ValueRow.get(arrayOfValue));
/*  308 */         arrayOfValue = new Value[i];
/*  309 */         for (b = 0; b < i; b++) {
/*  310 */           arrayOfValue[b] = (paramArrayOfValue != null) ? paramArrayOfValue[i + b] : ((QueryOrderBy)this.orderByList.get(b)).expression.getValue(paramSessionLocal);
/*      */         }
/*  312 */         valueRow2 = ValueRow.get(arrayOfValue);
/*      */         break;
/*      */       
/*      */       case PERCENTILE_CONT:
/*      */       case PERCENTILE_DISC:
/*  317 */         ((AggregateDataCollecting)paramAggregateData).setSharedArgument((Value)valueRow2);
/*  318 */         value1 = (paramArrayOfValue != null) ? paramArrayOfValue[1] : ((QueryOrderBy)this.orderByList.get(0)).expression.getValue(paramSessionLocal);
/*      */         break;
/*      */       case MODE:
/*  321 */         value1 = (paramArrayOfValue != null) ? paramArrayOfValue[0] : ((QueryOrderBy)this.orderByList.get(0)).expression.getValue(paramSessionLocal);
/*      */         break;
/*      */       case JSON_ARRAYAGG:
/*  324 */         value1 = updateCollecting(paramSessionLocal, value1, paramArrayOfValue);
/*      */         break;
/*      */       case JSON_OBJECTAGG:
/*  327 */         value2 = value1;
/*  328 */         value4 = getSecondValue(paramSessionLocal, paramArrayOfValue);
/*  329 */         if (value2 == ValueNull.INSTANCE) {
/*  330 */           throw DbException.getInvalidValueException("JSON_OBJECTAGG key", "NULL");
/*      */         }
/*  332 */         valueRow1 = ValueRow.get(new Value[] { value2, value4 });
/*      */         break;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  338 */     paramAggregateData.add(paramSessionLocal, (Value)valueRow1); }
/*      */ 
/*      */   
/*      */   private Value getSecondValue(SessionLocal paramSessionLocal, Value[] paramArrayOfValue) {
/*  342 */     return (paramArrayOfValue != null) ? paramArrayOfValue[1] : this.args[1].getValue(paramSessionLocal);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void updateGroupAggregates(SessionLocal paramSessionLocal, int paramInt) {
/*  347 */     super.updateGroupAggregates(paramSessionLocal, paramInt);
/*  348 */     for (Expression expression : this.args) {
/*  349 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*      */     }
/*  351 */     if (this.orderByList != null)
/*  352 */       for (QueryOrderBy queryOrderBy : this.orderByList) {
/*  353 */         queryOrderBy.expression.updateAggregate(paramSessionLocal, paramInt);
/*      */       } 
/*      */   }
/*      */   
/*      */   private Value updateCollecting(SessionLocal paramSessionLocal, Value paramValue, Value[] paramArrayOfValue) {
/*      */     ValueRow valueRow;
/*  359 */     if (this.orderByList != null) {
/*  360 */       int i = this.orderByList.size();
/*  361 */       Value[] arrayOfValue = new Value[1 + i];
/*  362 */       arrayOfValue[0] = paramValue;
/*  363 */       if (paramArrayOfValue == null) {
/*  364 */         for (byte b = 0; b < i; b++) {
/*  365 */           QueryOrderBy queryOrderBy = this.orderByList.get(b);
/*  366 */           arrayOfValue[b + 1] = queryOrderBy.expression.getValue(paramSessionLocal);
/*      */         } 
/*      */       } else {
/*  369 */         System.arraycopy(paramArrayOfValue, 1, arrayOfValue, 1, i);
/*      */       } 
/*  371 */       valueRow = ValueRow.get(arrayOfValue);
/*      */     } 
/*  373 */     return (Value)valueRow;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int getNumExpressions() {
/*  378 */     int i = this.args.length;
/*  379 */     if (this.orderByList != null) {
/*  380 */       i += this.orderByList.size();
/*      */     }
/*  382 */     if (this.filterCondition != null) {
/*  383 */       i++;
/*      */     }
/*  385 */     return i;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void rememberExpressions(SessionLocal paramSessionLocal, Value[] paramArrayOfValue) {
/*  390 */     byte b = 0;
/*  391 */     for (Expression expression : this.args) {
/*  392 */       paramArrayOfValue[b++] = expression.getValue(paramSessionLocal);
/*      */     }
/*  394 */     if (this.orderByList != null) {
/*  395 */       for (QueryOrderBy queryOrderBy : this.orderByList) {
/*  396 */         paramArrayOfValue[b++] = queryOrderBy.expression.getValue(paramSessionLocal);
/*      */       }
/*      */     }
/*  399 */     if (this.filterCondition != null) {
/*  400 */       paramArrayOfValue[b] = (Value)ValueBoolean.get(this.filterCondition.getBooleanValue(paramSessionLocal));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   protected void updateFromExpressions(SessionLocal paramSessionLocal, Object paramObject, Value[] paramArrayOfValue) {
/*  406 */     if (this.filterCondition == null || paramArrayOfValue[getNumExpressions() - 1].isTrue()) {
/*  407 */       AggregateData aggregateData = (AggregateData)paramObject;
/*  408 */       Value value = (this.args.length == 0) ? null : paramArrayOfValue[0];
/*  409 */       updateData(paramSessionLocal, aggregateData, value, paramArrayOfValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected Object createAggregateData() {
/*  415 */     switch (this.aggregateType)
/*      */     { case REGR_COUNT:
/*      */       case COUNT_ALL:
/*  418 */         return new AggregateDataCount(true);
/*      */       case COUNT:
/*  420 */         if (!this.distinct) {
/*  421 */           return new AggregateDataCount(false);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case RANK:
/*      */       case DENSE_RANK:
/*      */       case PERCENT_RANK:
/*      */       case CUME_DIST:
/*      */       case PERCENTILE_CONT:
/*      */       case PERCENTILE_DISC:
/*      */       case MEDIAN:
/*  495 */         return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.IGNORED);case SUM: case BIT_XOR_AGG: case BIT_XNOR_AGG: if (this.distinct) return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.IGNORED); case MIN: case MAX: case BIT_AND_AGG: case BIT_OR_AGG: case BIT_NAND_AGG: case BIT_NOR_AGG: case ANY: case EVERY: return new AggregateDataDefault(this.aggregateType, this.type);case AVG: if (this.distinct) return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.IGNORED); case REGR_AVGY: case REGR_AVGX: return new AggregateDataAvg(this.type);case STDDEV_POP: case STDDEV_SAMP: case VAR_POP: case VAR_SAMP: if (this.distinct) return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.IGNORED); case REGR_SYY: case REGR_SXX: return new AggregateDataStdVar(this.aggregateType);case HISTOGRAM: return new AggregateDataDistinctWithCounts(false, 10000);case COVAR_POP: case COVAR_SAMP: case REGR_SXY: return new AggregateDataCovar(this.aggregateType);case CORR: case REGR_SLOPE: case REGR_INTERCEPT: case REGR_R2: return new AggregateDataCorr(this.aggregateType);
/*      */       case LISTAGG: case ARRAY_AGG: return new AggregateDataCollecting(this.distinct, (this.orderByList != null), AggregateDataCollecting.NullCollectionMode.USED_OR_IMPOSSIBLE);
/*      */       case MODE: return new AggregateDataDistinctWithCounts(true, 2147483647);
/*      */       case ENVELOPE: return new AggregateDataEnvelope();
/*      */       case JSON_ARRAYAGG: return new AggregateDataCollecting(this.distinct, (this.orderByList != null), ((this.flags & 0x1) != 0) ? AggregateDataCollecting.NullCollectionMode.EXCLUDED : AggregateDataCollecting.NullCollectionMode.USED_OR_IMPOSSIBLE);
/*  500 */       case JSON_OBJECTAGG: return new AggregateDataCollecting(this.distinct, false, AggregateDataCollecting.NullCollectionMode.USED_OR_IMPOSSIBLE); }  throw DbException.getInternalError("type=" + this.aggregateType); } public Value getValue(SessionLocal paramSessionLocal) { return this.select.isQuickAggregateQuery() ? getValueQuick(paramSessionLocal) : super.getValue(paramSessionLocal); } private Value getValueQuick(SessionLocal paramSessionLocal) { Table table; boolean bool; Value value1; Index index; BigDecimal bigDecimal; int i;
/*      */     Cursor cursor;
/*      */     SearchRow searchRow;
/*      */     Value value2;
/*  504 */     switch (this.aggregateType) {
/*      */       case COUNT_ALL:
/*      */       case COUNT:
/*  507 */         table = this.select.getTopTableFilter().getTable();
/*  508 */         return (Value)ValueBigint.get(table.getRowCount(paramSessionLocal));
/*      */       case MIN:
/*      */       case MAX:
/*  511 */         bool = (this.aggregateType == AggregateType.MIN) ? true : false;
/*  512 */         index = getMinMaxColumnIndex();
/*  513 */         i = (index.getIndexColumns()[0]).sortType;
/*  514 */         if ((i & 0x1) != 0) {
/*  515 */           bool = !bool ? true : false;
/*      */         }
/*  517 */         cursor = index.findFirstOrLast(paramSessionLocal, bool);
/*  518 */         searchRow = cursor.getSearchRow();
/*      */         
/*  520 */         if (searchRow == null) {
/*  521 */           ValueNull valueNull = ValueNull.INSTANCE;
/*      */         } else {
/*  523 */           value2 = searchRow.getValue(index.getColumns()[0].getColumnId());
/*      */         } 
/*  525 */         return value2;
/*      */       
/*      */       case PERCENTILE_CONT:
/*      */       case PERCENTILE_DISC:
/*  529 */         value1 = this.args[0].getValue(paramSessionLocal);
/*  530 */         if (value1 == ValueNull.INSTANCE) {
/*  531 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  533 */         bigDecimal = value1.getBigDecimal();
/*  534 */         if (bigDecimal.signum() >= 0 && bigDecimal.compareTo(BigDecimal.ONE) <= 0) {
/*  535 */           return Percentile.getFromIndex(paramSessionLocal, ((QueryOrderBy)this.orderByList.get(0)).expression, this.type.getValueType(), this.orderByList, bigDecimal, (this.aggregateType == AggregateType.PERCENTILE_CONT));
/*      */         }
/*      */         
/*  538 */         throw DbException.getInvalidValueException((this.aggregateType == AggregateType.PERCENTILE_CONT) ? "PERCENTILE_CONT argument" : "PERCENTILE_DISC argument", bigDecimal);
/*      */ 
/*      */ 
/*      */       
/*      */       case MEDIAN:
/*  543 */         return Percentile.getFromIndex(paramSessionLocal, this.args[0], this.type.getValueType(), this.orderByList, Percentile.HALF, true);
/*      */       case ENVELOPE:
/*  545 */         return ((MVSpatialIndex)AggregateDataEnvelope.getGeometryColumnIndex(this.args[0])).getBounds(paramSessionLocal);
/*      */     } 
/*  547 */     throw DbException.getInternalError("type=" + this.aggregateType); } public Value getAggregatedValue(SessionLocal paramSessionLocal, Object paramObject) { Value[] arrayOfValue2;
/*      */     AggregateDataCollecting aggregateDataCollecting;
/*      */     Value[] arrayOfValue1, arrayOfValue3;
/*      */     ByteArrayOutputStream byteArrayOutputStream;
/*      */     Value value;
/*      */     BigDecimal bigDecimal;
/*  553 */     AggregateData aggregateData = (AggregateData)paramObject;
/*  554 */     if (aggregateData == null) {
/*  555 */       aggregateData = (AggregateData)createAggregateData();
/*      */     }
/*  557 */     switch (this.aggregateType) {
/*      */       case COUNT:
/*  559 */         if (this.distinct) {
/*  560 */           return (Value)ValueBigint.get(((AggregateDataCollecting)aggregateData).getCount());
/*      */         }
/*      */         break;
/*      */       case SUM:
/*      */       case BIT_XOR_AGG:
/*      */       case BIT_XNOR_AGG:
/*  566 */         if (this.distinct) {
/*  567 */           AggregateDataCollecting aggregateDataCollecting1 = (AggregateDataCollecting)aggregateData;
/*  568 */           if (aggregateDataCollecting1.getCount() == 0) {
/*  569 */             return (Value)ValueNull.INSTANCE;
/*      */           }
/*  571 */           return collect(paramSessionLocal, aggregateDataCollecting1, new AggregateDataDefault(this.aggregateType, this.type));
/*      */         } 
/*      */         break;
/*      */       case AVG:
/*  575 */         if (this.distinct) {
/*  576 */           AggregateDataCollecting aggregateDataCollecting1 = (AggregateDataCollecting)aggregateData;
/*  577 */           if (aggregateDataCollecting1.getCount() == 0) {
/*  578 */             return (Value)ValueNull.INSTANCE;
/*      */           }
/*  580 */           return collect(paramSessionLocal, aggregateDataCollecting1, new AggregateDataAvg(this.type));
/*      */         } 
/*      */         break;
/*      */       case STDDEV_POP:
/*      */       case STDDEV_SAMP:
/*      */       case VAR_POP:
/*      */       case VAR_SAMP:
/*  587 */         if (this.distinct) {
/*  588 */           AggregateDataCollecting aggregateDataCollecting1 = (AggregateDataCollecting)aggregateData;
/*  589 */           if (aggregateDataCollecting1.getCount() == 0) {
/*  590 */             return (Value)ValueNull.INSTANCE;
/*      */           }
/*  592 */           return collect(paramSessionLocal, aggregateDataCollecting1, new AggregateDataStdVar(this.aggregateType));
/*      */         } 
/*      */         break;
/*      */       case HISTOGRAM:
/*  596 */         return getHistogram(paramSessionLocal, aggregateData);
/*      */       case LISTAGG:
/*  598 */         return getListagg(paramSessionLocal, aggregateData);
/*      */       case ARRAY_AGG:
/*  600 */         arrayOfValue2 = ((AggregateDataCollecting)aggregateData).getArray();
/*  601 */         if (arrayOfValue2 == null) {
/*  602 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  604 */         if (this.orderByList != null || this.distinct) {
/*  605 */           sortWithOrderBy(arrayOfValue2);
/*      */         }
/*  607 */         if (this.orderByList != null) {
/*  608 */           for (byte b = 0; b < arrayOfValue2.length; b++) {
/*  609 */             arrayOfValue2[b] = ((ValueRow)arrayOfValue2[b]).getList()[0];
/*      */           }
/*      */         }
/*  612 */         return (Value)ValueArray.get((TypeInfo)this.type.getExtTypeInfo(), arrayOfValue2, (CastDataProvider)paramSessionLocal);
/*      */       
/*      */       case RANK:
/*      */       case DENSE_RANK:
/*      */       case PERCENT_RANK:
/*      */       case CUME_DIST:
/*  618 */         return getHypotheticalSet(paramSessionLocal, aggregateData);
/*      */       case PERCENTILE_CONT:
/*      */       case PERCENTILE_DISC:
/*  621 */         aggregateDataCollecting = (AggregateDataCollecting)aggregateData;
/*  622 */         arrayOfValue3 = aggregateDataCollecting.getArray();
/*  623 */         if (arrayOfValue3 == null) {
/*  624 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  626 */         value = aggregateDataCollecting.getSharedArgument();
/*  627 */         if (value == ValueNull.INSTANCE) {
/*  628 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  630 */         bigDecimal = value.getBigDecimal();
/*  631 */         if (bigDecimal.signum() >= 0 && bigDecimal.compareTo(BigDecimal.ONE) <= 0) {
/*  632 */           return Percentile.getValue(paramSessionLocal, arrayOfValue3, this.type.getValueType(), this.orderByList, bigDecimal, (this.aggregateType == AggregateType.PERCENTILE_CONT));
/*      */         }
/*      */         
/*  635 */         throw DbException.getInvalidValueException((this.aggregateType == AggregateType.PERCENTILE_CONT) ? "PERCENTILE_CONT argument" : "PERCENTILE_DISC argument", bigDecimal);
/*      */ 
/*      */ 
/*      */       
/*      */       case MEDIAN:
/*  640 */         arrayOfValue1 = ((AggregateDataCollecting)aggregateData).getArray();
/*  641 */         if (arrayOfValue1 == null) {
/*  642 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  644 */         return Percentile.getValue(paramSessionLocal, arrayOfValue1, this.type.getValueType(), this.orderByList, Percentile.HALF, true);
/*      */       
/*      */       case MODE:
/*  647 */         return getMode(paramSessionLocal, aggregateData);
/*      */       case JSON_ARRAYAGG:
/*  649 */         arrayOfValue1 = ((AggregateDataCollecting)aggregateData).getArray();
/*  650 */         if (arrayOfValue1 == null) {
/*  651 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  653 */         if (this.orderByList != null) {
/*  654 */           sortWithOrderBy(arrayOfValue1);
/*      */         }
/*  656 */         byteArrayOutputStream = new ByteArrayOutputStream();
/*  657 */         byteArrayOutputStream.write(91);
/*  658 */         for (Value value1 : arrayOfValue1) {
/*  659 */           if (this.orderByList != null) {
/*  660 */             value1 = ((ValueRow)value1).getList()[0];
/*      */           }
/*  662 */           JsonConstructorUtils.jsonArrayAppend(byteArrayOutputStream, (value1 != ValueNull.INSTANCE) ? value1 : (Value)ValueJson.NULL, this.flags);
/*      */         } 
/*  664 */         byteArrayOutputStream.write(93);
/*  665 */         return (Value)ValueJson.getInternal(byteArrayOutputStream.toByteArray());
/*      */       
/*      */       case JSON_OBJECTAGG:
/*  668 */         arrayOfValue1 = ((AggregateDataCollecting)aggregateData).getArray();
/*  669 */         if (arrayOfValue1 == null) {
/*  670 */           return (Value)ValueNull.INSTANCE;
/*      */         }
/*  672 */         byteArrayOutputStream = new ByteArrayOutputStream();
/*  673 */         byteArrayOutputStream.write(123);
/*  674 */         for (Value value1 : arrayOfValue1) {
/*  675 */           ValueJson valueJson; Value[] arrayOfValue = ((ValueRow)value1).getList();
/*  676 */           String str = arrayOfValue[0].getString();
/*  677 */           if (str == null) {
/*  678 */             throw DbException.getInvalidValueException("JSON_OBJECTAGG key", "NULL");
/*      */           }
/*  680 */           Value value2 = arrayOfValue[1];
/*  681 */           if (value2 == ValueNull.INSTANCE) {
/*  682 */             if ((this.flags & 0x1) != 0) {
/*      */               continue;
/*      */             }
/*  685 */             valueJson = ValueJson.NULL;
/*      */           } 
/*  687 */           JsonConstructorUtils.jsonObjectAppend(byteArrayOutputStream, str, (Value)valueJson); continue;
/*      */         } 
/*  689 */         return JsonConstructorUtils.jsonObjectFinish(byteArrayOutputStream, this.flags);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  694 */     return aggregateData.getValue(paramSessionLocal); }
/*      */ 
/*      */   
/*      */   private static Value collect(SessionLocal paramSessionLocal, AggregateDataCollecting paramAggregateDataCollecting, AggregateData paramAggregateData) {
/*  698 */     for (Value value : paramAggregateDataCollecting) {
/*  699 */       paramAggregateData.add(paramSessionLocal, value);
/*      */     }
/*  701 */     return paramAggregateData.getValue(paramSessionLocal);
/*      */   }
/*      */   
/*      */   private Value getHypotheticalSet(SessionLocal paramSessionLocal, AggregateData paramAggregateData) {
/*  705 */     AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting)paramAggregateData;
/*  706 */     Value value = aggregateDataCollecting.getSharedArgument();
/*  707 */     if (value == null) {
/*  708 */       switch (this.aggregateType) {
/*      */         case RANK:
/*      */         case DENSE_RANK:
/*  711 */           return (Value)ValueInteger.get(1);
/*      */         case PERCENT_RANK:
/*  713 */           return (Value)ValueDouble.ZERO;
/*      */         case CUME_DIST:
/*  715 */           return (Value)ValueDouble.ONE;
/*      */       } 
/*  717 */       throw DbException.getUnsupportedException("aggregateType=" + this.aggregateType);
/*      */     } 
/*      */     
/*  720 */     aggregateDataCollecting.add(paramSessionLocal, value);
/*  721 */     Value[] arrayOfValue = aggregateDataCollecting.getArray();
/*  722 */     Comparator<? super Value> comparator = this.orderBySort.getRowValueComparator();
/*  723 */     Arrays.sort(arrayOfValue, comparator);
/*  724 */     return (this.aggregateType == AggregateType.CUME_DIST) ? getCumeDist(arrayOfValue, value, (Comparator)comparator) : getRank(arrayOfValue, value, (Comparator)comparator);
/*      */   }
/*      */   
/*      */   private Value getRank(Value[] paramArrayOfValue, Value paramValue, Comparator<Value> paramComparator) {
/*  728 */     int i = paramArrayOfValue.length;
/*  729 */     int j = 0;
/*  730 */     for (byte b = 0; b < i; b++) {
/*  731 */       ValueBigint valueBigint; Value value = paramArrayOfValue[b];
/*  732 */       if (b == 0) {
/*  733 */         j = 1;
/*  734 */       } else if (paramComparator.compare(paramArrayOfValue[b - 1], value) != 0) {
/*  735 */         if (this.aggregateType == AggregateType.DENSE_RANK) {
/*  736 */           j++;
/*      */         } else {
/*  738 */           j = b + 1;
/*      */         } 
/*      */       } 
/*      */       
/*  742 */       if (this.aggregateType == AggregateType.PERCENT_RANK) {
/*  743 */         int k = j - 1;
/*  744 */         ValueDouble valueDouble = (k == 0) ? ValueDouble.ZERO : ValueDouble.get(k / (i - 1));
/*      */       } else {
/*  746 */         valueBigint = ValueBigint.get(j);
/*      */       } 
/*  748 */       if (paramComparator.compare(value, paramValue) == 0) {
/*  749 */         return (Value)valueBigint;
/*      */       }
/*      */     } 
/*  752 */     throw DbException.getInternalError();
/*      */   }
/*      */   
/*      */   private static Value getCumeDist(Value[] paramArrayOfValue, Value paramValue, Comparator<Value> paramComparator) {
/*  756 */     int i = paramArrayOfValue.length; int j;
/*  757 */     for (j = 0; j < i; ) {
/*  758 */       Value value = paramArrayOfValue[j];
/*  759 */       int k = j + 1;
/*  760 */       while (k < i && paramComparator.compare(value, paramArrayOfValue[k]) == 0) {
/*  761 */         k++;
/*      */       }
/*  763 */       ValueDouble valueDouble = ValueDouble.get(k / i);
/*  764 */       for (int m = j; m < k; m++) {
/*  765 */         if (paramComparator.compare(paramArrayOfValue[m], paramValue) == 0) {
/*  766 */           return (Value)valueDouble;
/*      */         }
/*      */       } 
/*  769 */       j = k;
/*      */     } 
/*  771 */     throw DbException.getInternalError();
/*      */   }
/*      */   
/*      */   private Value getListagg(SessionLocal paramSessionLocal, AggregateData paramAggregateData) {
/*  775 */     AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting)paramAggregateData;
/*  776 */     Value[] arrayOfValue = aggregateDataCollecting.getArray();
/*  777 */     if (arrayOfValue == null) {
/*  778 */       return (Value)ValueNull.INSTANCE;
/*      */     }
/*  780 */     if (arrayOfValue.length == 1) {
/*  781 */       Value value = arrayOfValue[0];
/*  782 */       if (this.orderByList != null) {
/*  783 */         value = ((ValueRow)value).getList()[0];
/*      */       }
/*  785 */       return value.convertTo(2, (CastDataProvider)paramSessionLocal);
/*      */     } 
/*  787 */     if (this.orderByList != null || this.distinct) {
/*  788 */       sortWithOrderBy(arrayOfValue);
/*      */     }
/*  790 */     ListaggArguments listaggArguments = (ListaggArguments)this.extraArguments;
/*  791 */     String str = listaggArguments.getEffectiveSeparator();
/*  792 */     return 
/*  793 */       ValueVarchar.get((listaggArguments.getOnOverflowTruncate() ? 
/*  794 */         getListaggTruncate(arrayOfValue, str, listaggArguments.getEffectiveFilter(), listaggArguments
/*  795 */           .isWithoutCount()) : 
/*  796 */         getListaggError(arrayOfValue, str)).toString(), (CastDataProvider)paramSessionLocal);
/*      */   }
/*      */   
/*      */   private StringBuilder getListaggError(Value[] paramArrayOfValue, String paramString) {
/*  800 */     StringBuilder stringBuilder = new StringBuilder(getListaggItem(paramArrayOfValue[0])); byte b; int i;
/*  801 */     for (b = 1, i = paramArrayOfValue.length; b < i; b++) {
/*  802 */       stringBuilder.append(paramString).append(getListaggItem(paramArrayOfValue[b]));
/*  803 */       if (stringBuilder.length() > 1048576) {
/*  804 */         throw DbException.getValueTooLongException("CHARACTER VARYING", stringBuilder.substring(0, 81), -1L);
/*      */       }
/*      */     } 
/*  807 */     return stringBuilder;
/*      */   }
/*      */ 
/*      */   
/*      */   private StringBuilder getListaggTruncate(Value[] paramArrayOfValue, String paramString1, String paramString2, boolean paramBoolean) {
/*  812 */     int i = paramArrayOfValue.length;
/*  813 */     String[] arrayOfString = new String[i];
/*  814 */     String str = getListaggItem(paramArrayOfValue[0]);
/*  815 */     arrayOfString[0] = str;
/*  816 */     StringBuilder stringBuilder = new StringBuilder(str);
/*  817 */     for (byte b = 1; b < i; b++) {
/*  818 */       arrayOfString[b] = str = getListaggItem(paramArrayOfValue[b]); stringBuilder.append(paramString1).append(str = getListaggItem(paramArrayOfValue[b]));
/*  819 */       int j = stringBuilder.length();
/*  820 */       if (j > 1048576) { while (true) {
/*  821 */           if (b > 0) {
/*  822 */             j -= arrayOfString[b].length();
/*  823 */             stringBuilder.setLength(j);
/*  824 */             stringBuilder.append(paramString2);
/*  825 */             if (!paramBoolean) {
/*  826 */               stringBuilder.append('(').append(i - b).append(')');
/*      */             }
/*  828 */             if (stringBuilder.length() <= 1048576) {
/*      */               break;
/*      */             }
/*  831 */             j -= paramString1.length(); b--; continue;
/*      */           } 
/*  833 */           stringBuilder.setLength(0);
/*  834 */           stringBuilder.append(paramString2).append('(').append(i).append(')'); break;
/*      */         }  break; }
/*      */     
/*      */     } 
/*  838 */     return stringBuilder;
/*      */   }
/*      */   
/*      */   private String getListaggItem(Value paramValue) {
/*  842 */     if (this.orderByList != null) {
/*  843 */       paramValue = ((ValueRow)paramValue).getList()[0];
/*      */     }
/*  845 */     return paramValue.getString();
/*      */   }
/*      */   
/*      */   private Value getHistogram(SessionLocal paramSessionLocal, AggregateData paramAggregateData) {
/*  849 */     TreeMap<Value, LongDataCounter> treeMap = ((AggregateDataDistinctWithCounts)paramAggregateData).getValues();
/*  850 */     TypeInfo typeInfo = (TypeInfo)this.type.getExtTypeInfo();
/*  851 */     if (treeMap == null) {
/*  852 */       return (Value)ValueArray.get(typeInfo, Value.EMPTY_VALUES, (CastDataProvider)paramSessionLocal);
/*      */     }
/*  854 */     ValueRow[] arrayOfValueRow = new ValueRow[treeMap.size()];
/*  855 */     byte b = 0;
/*  856 */     for (Map.Entry<Value, LongDataCounter> entry : treeMap.entrySet()) {
/*  857 */       LongDataCounter longDataCounter = (LongDataCounter)entry.getValue();
/*  858 */       arrayOfValueRow[b] = ValueRow.get(typeInfo, new Value[] { (Value)entry.getKey(), (Value)ValueBigint.get(longDataCounter.count) });
/*  859 */       b++;
/*      */     } 
/*  861 */     Database database = paramSessionLocal.getDatabase();
/*  862 */     CompareMode compareMode = database.getCompareMode();
/*  863 */     Arrays.sort(arrayOfValueRow, (paramValueRow1, paramValueRow2) -> paramValueRow1.getList()[0].compareTo(paramValueRow2.getList()[0], (CastDataProvider)paramSessionLocal, paramCompareMode));
/*  864 */     return (Value)ValueArray.get(typeInfo, (Value[])arrayOfValueRow, (CastDataProvider)paramSessionLocal);
/*      */   }
/*      */   private Value getMode(SessionLocal paramSessionLocal, AggregateData paramAggregateData) {
/*      */     Value value;
/*  868 */     ValueNull valueNull = ValueNull.INSTANCE;
/*  869 */     TreeMap<Value, LongDataCounter> treeMap = ((AggregateDataDistinctWithCounts)paramAggregateData).getValues();
/*  870 */     if (treeMap == null) {
/*  871 */       return (Value)valueNull;
/*      */     }
/*  873 */     long l = 0L;
/*  874 */     if (this.orderByList != null) {
/*  875 */       boolean bool = ((((QueryOrderBy)this.orderByList.get(0)).sortType & 0x1) != 0) ? true : false;
/*  876 */       for (Map.Entry<Value, LongDataCounter> entry : treeMap.entrySet()) {
/*  877 */         long l1 = ((LongDataCounter)entry.getValue()).count;
/*  878 */         if (l1 > l) {
/*  879 */           value = (Value)entry.getKey();
/*  880 */           l = l1; continue;
/*  881 */         }  if (l1 == l) {
/*  882 */           Value value1 = (Value)entry.getKey();
/*  883 */           int i = paramSessionLocal.compareTypeSafe(value, value1);
/*  884 */           if (bool ? (
/*  885 */             i >= 0) : (
/*      */ 
/*      */             
/*  888 */             i <= 0)) {
/*      */             continue;
/*      */           }
/*  891 */           value = value1;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  895 */       for (Map.Entry<Value, LongDataCounter> entry : treeMap.entrySet()) {
/*  896 */         long l1 = ((LongDataCounter)entry.getValue()).count;
/*  897 */         if (l1 > l) {
/*  898 */           value = (Value)entry.getKey();
/*  899 */           l = l1;
/*      */         } 
/*      */       } 
/*      */     } 
/*  903 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public void mapColumnsAnalysis(ColumnResolver paramColumnResolver, int paramInt1, int paramInt2) {
/*  908 */     if (this.orderByList != null) {
/*  909 */       for (QueryOrderBy queryOrderBy : this.orderByList) {
/*  910 */         queryOrderBy.expression.mapColumns(paramColumnResolver, paramInt1, paramInt2);
/*      */       }
/*      */     }
/*  913 */     super.mapColumnsAnalysis(paramColumnResolver, paramInt1, paramInt2);
/*      */   }
/*      */   
/*      */   public Expression optimize(SessionLocal paramSessionLocal) {
/*      */     LinkedHashMap<Object, Object> linkedHashMap;
/*  918 */     super.optimize(paramSessionLocal);
/*  919 */     if (this.args.length == 1) {
/*  920 */       this.type = this.args[0].getType();
/*      */     }
/*  922 */     if (this.orderByList != null) {
/*      */       boolean bool;
/*  924 */       switch (this.aggregateType) {
/*      */         case LISTAGG:
/*      */         case ARRAY_AGG:
/*      */         case JSON_ARRAYAGG:
/*  928 */           bool = true;
/*      */           break;
/*      */         default:
/*  931 */           bool = false; break;
/*      */       } 
/*  933 */       for (Iterator<QueryOrderBy> iterator = this.orderByList.iterator(); iterator.hasNext(); ) {
/*  934 */         QueryOrderBy queryOrderBy = iterator.next();
/*  935 */         Expression expression = queryOrderBy.expression.optimize(paramSessionLocal);
/*  936 */         if (bool && expression.isConstant()) {
/*  937 */           iterator.remove(); continue;
/*      */         } 
/*  939 */         queryOrderBy.expression = expression;
/*      */       } 
/*      */       
/*  942 */       if (this.orderByList.isEmpty()) {
/*  943 */         this.orderByList = null;
/*      */       } else {
/*  945 */         this.orderBySort = createOrder(paramSessionLocal, this.orderByList, bool);
/*      */       } 
/*      */     } 
/*  948 */     switch (this.aggregateType)
/*      */     { case LISTAGG:
/*  950 */         this.type = TypeInfo.TYPE_VARCHAR;
/*      */       
/*      */       case COUNT:
/*  953 */         if (this.args[0].isConstant()) {
/*  954 */           if (this.args[0].getValue(paramSessionLocal) == ValueNull.INSTANCE) {
/*  955 */             return (Expression)ValueExpression.get((Value)ValueBigint.get(0L));
/*      */           }
/*  957 */           if (!this.distinct) {
/*  958 */             Aggregate aggregate = new Aggregate(AggregateType.COUNT_ALL, new Expression[0], this.select, false);
/*  959 */             aggregate.setFilterCondition(this.filterCondition);
/*  960 */             aggregate.setOverCondition(this.over);
/*  961 */             return aggregate.optimize(paramSessionLocal);
/*      */           } 
/*      */         } 
/*      */       
/*      */       case REGR_COUNT:
/*      */       case COUNT_ALL:
/*  967 */         this.type = TypeInfo.TYPE_BIGINT;
/*      */       
/*      */       case HISTOGRAM:
/*  970 */         linkedHashMap = new LinkedHashMap<>(4);
/*  971 */         linkedHashMap.put("VALUE", this.type);
/*  972 */         linkedHashMap.put("COUNT", TypeInfo.TYPE_BIGINT);
/*  973 */         this.type = TypeInfo.getTypeInfo(40, -1L, 0, 
/*  974 */             (ExtTypeInfo)TypeInfo.getTypeInfo(41, -1L, -1, (ExtTypeInfo)new ExtTypeInfoRow(linkedHashMap)));
/*      */ 
/*      */       
/*      */       case SUM:
/*  978 */         if ((this.type = getSumType(this.type)) == null) {
/*  979 */           throw DbException.get(90015, getTraceSQL());
/*      */         }
/*      */       
/*      */       case AVG:
/*  983 */         if ((this.type = getAvgType(this.type)) == null) {
/*  984 */           throw DbException.get(90015, getTraceSQL());
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case MIN:
/*      */       case MAX:
/* 1069 */         return (Expression)this;case COVAR_POP: case COVAR_SAMP: case CORR: case REGR_SLOPE: case REGR_INTERCEPT: case REGR_R2: case REGR_SXY: case REGR_SYY: case REGR_SXX: case STDDEV_POP: case STDDEV_SAMP: case VAR_POP: case VAR_SAMP: this.type = TypeInfo.TYPE_DOUBLE;case REGR_AVGX: if ((this.type = getAvgType(this.args[1].getType())) == null)
/*      */           throw DbException.get(90015, getTraceSQL()); case REGR_AVGY: if ((this.type = getAvgType(this.args[0].getType())) == null)
/*      */           throw DbException.get(90015, getTraceSQL()); case RANK: case DENSE_RANK: this.type = TypeInfo.TYPE_BIGINT;case PERCENT_RANK: case CUME_DIST: this.type = TypeInfo.TYPE_DOUBLE;case PERCENTILE_CONT: this.type = ((QueryOrderBy)this.orderByList.get(0)).expression.getType();case MEDIAN: switch (this.type.getValueType()) { case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16: this.type = TypeInfo.TYPE_NUMERIC_FLOATING_POINT; break; } case PERCENTILE_DISC: case MODE: this.type = ((QueryOrderBy)this.orderByList.get(0)).expression.getType();case ANY: case EVERY: this.type = TypeInfo.TYPE_BOOLEAN;case BIT_XOR_AGG: case BIT_XNOR_AGG: case BIT_AND_AGG: case BIT_OR_AGG: case BIT_NAND_AGG: case BIT_NOR_AGG: BitFunction.checkArgType(this.args[0]);case ARRAY_AGG: this.type = TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)this.args[0].getType());
/*      */       case ENVELOPE: this.type = TypeInfo.TYPE_GEOMETRY;
/* 1073 */       case JSON_ARRAYAGG: case JSON_OBJECTAGG: this.type = TypeInfo.TYPE_JSON; }  throw DbException.getInternalError("type=" + this.aggregateType); } private static TypeInfo getSumType(TypeInfo paramTypeInfo) { int i = paramTypeInfo.getValueType();
/* 1074 */     switch (i) {
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/* 1079 */         return TypeInfo.TYPE_BIGINT;
/*      */       case 12:
/* 1081 */         return TypeInfo.getTypeInfo(13, 29L, -1, null);
/*      */       
/*      */       case 13:
/* 1084 */         return TypeInfo.getTypeInfo(13, paramTypeInfo.getPrecision() + 10L, paramTypeInfo
/* 1085 */             .getDeclaredScale(), null);
/*      */       case 14:
/* 1087 */         return TypeInfo.TYPE_DOUBLE;
/*      */       case 15:
/* 1089 */         return TypeInfo.getTypeInfo(16, 27L, -1, null);
/*      */       
/*      */       case 16:
/* 1092 */         return TypeInfo.getTypeInfo(16, paramTypeInfo.getPrecision() + 10L, -1, null);
/*      */     } 
/* 1094 */     if (DataType.isIntervalType(i)) {
/* 1095 */       return TypeInfo.getTypeInfo(i, 18L, paramTypeInfo.getDeclaredScale(), null);
/*      */     }
/* 1097 */     return null; }
/*      */ 
/*      */   
/*      */   private static TypeInfo getAvgType(TypeInfo paramTypeInfo) {
/*      */     int i;
/* 1102 */     switch (paramTypeInfo.getValueType()) {
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 14:
/* 1107 */         return TypeInfo.TYPE_DOUBLE;
/*      */       case 12:
/* 1109 */         return TypeInfo.getTypeInfo(13, 29L, 10, null);
/*      */       
/*      */       case 13:
/* 1112 */         i = Math.min(100000 - paramTypeInfo.getScale(), 
/* 1113 */             Math.min(100000 - (int)paramTypeInfo.getPrecision(), 10));
/* 1114 */         return TypeInfo.getTypeInfo(13, paramTypeInfo.getPrecision() + i, paramTypeInfo
/* 1115 */             .getScale() + i, null);
/*      */       
/*      */       case 15:
/* 1118 */         return TypeInfo.getTypeInfo(16, 27L, -1, null);
/*      */       
/*      */       case 16:
/* 1121 */         return TypeInfo.getTypeInfo(16, paramTypeInfo.getPrecision() + 10L, -1, null);
/*      */       case 22:
/*      */       case 28:
/* 1124 */         return TypeInfo.getTypeInfo(28, paramTypeInfo.getDeclaredPrecision(), 0, null);
/*      */       case 23:
/* 1126 */         return TypeInfo.getTypeInfo(23, paramTypeInfo.getDeclaredPrecision(), 0, null);
/*      */       case 24:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/* 1131 */         return TypeInfo.getTypeInfo(31, paramTypeInfo.getDeclaredPrecision(), 9, null);
/*      */       
/*      */       case 25:
/*      */       case 32:
/*      */       case 33:
/* 1136 */         return TypeInfo.getTypeInfo(33, paramTypeInfo.getDeclaredPrecision(), 9, null);
/*      */       
/*      */       case 26:
/*      */       case 34:
/* 1140 */         return TypeInfo.getTypeInfo(34, paramTypeInfo.getDeclaredPrecision(), 9, null);
/*      */       
/*      */       case 27:
/* 1143 */         return TypeInfo.getTypeInfo(27, paramTypeInfo.getDeclaredPrecision(), 9, null);
/*      */     } 
/*      */     
/* 1146 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/* 1152 */     if (this.orderByList != null) {
/* 1153 */       for (QueryOrderBy queryOrderBy : this.orderByList) {
/* 1154 */         queryOrderBy.expression.setEvaluatable(paramTableFilter, paramBoolean);
/*      */       }
/*      */     }
/* 1157 */     super.setEvaluatable(paramTableFilter, paramBoolean);
/*      */   }
/*      */ 
/*      */   
/*      */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 1162 */     switch (this.aggregateType) {
/*      */       case COUNT_ALL:
/* 1164 */         return appendTailConditions(paramStringBuilder.append("COUNT(*)"), paramInt, false);
/*      */       case LISTAGG:
/* 1166 */         return getSQLListagg(paramStringBuilder, paramInt);
/*      */       case ARRAY_AGG:
/* 1168 */         return getSQLArrayAggregate(paramStringBuilder, paramInt);
/*      */       case JSON_OBJECTAGG:
/* 1170 */         return getSQLJsonObjectAggregate(paramStringBuilder, paramInt);
/*      */       case JSON_ARRAYAGG:
/* 1172 */         return getSQLJsonArrayAggregate(paramStringBuilder, paramInt);
/*      */     } 
/*      */     
/* 1175 */     paramStringBuilder.append(this.aggregateType.name());
/* 1176 */     if (this.distinct) {
/* 1177 */       paramStringBuilder.append("(DISTINCT ");
/*      */     } else {
/* 1179 */       paramStringBuilder.append('(');
/*      */     } 
/* 1181 */     writeExpressions(paramStringBuilder, this.args, paramInt).append(')');
/* 1182 */     if (this.orderByList != null) {
/* 1183 */       paramStringBuilder.append(" WITHIN GROUP (");
/* 1184 */       Window.appendOrderBy(paramStringBuilder, this.orderByList, paramInt, false);
/* 1185 */       paramStringBuilder.append(')');
/*      */     } 
/* 1187 */     return appendTailConditions(paramStringBuilder, paramInt, false);
/*      */   }
/*      */   
/*      */   private StringBuilder getSQLArrayAggregate(StringBuilder paramStringBuilder, int paramInt) {
/* 1191 */     paramStringBuilder.append("ARRAY_AGG(");
/* 1192 */     if (this.distinct) {
/* 1193 */       paramStringBuilder.append("DISTINCT ");
/*      */     }
/* 1195 */     this.args[0].getUnenclosedSQL(paramStringBuilder, paramInt);
/* 1196 */     Window.appendOrderBy(paramStringBuilder, this.orderByList, paramInt, false);
/* 1197 */     paramStringBuilder.append(')');
/* 1198 */     return appendTailConditions(paramStringBuilder, paramInt, false);
/*      */   }
/*      */   
/*      */   private StringBuilder getSQLListagg(StringBuilder paramStringBuilder, int paramInt) {
/* 1202 */     paramStringBuilder.append("LISTAGG(");
/* 1203 */     if (this.distinct) {
/* 1204 */       paramStringBuilder.append("DISTINCT ");
/*      */     }
/* 1206 */     this.args[0].getUnenclosedSQL(paramStringBuilder, paramInt);
/* 1207 */     ListaggArguments listaggArguments = (ListaggArguments)this.extraArguments;
/* 1208 */     String str = listaggArguments.getSeparator();
/* 1209 */     if (str != null) {
/* 1210 */       StringUtils.quoteStringSQL(paramStringBuilder.append(", "), str);
/*      */     }
/* 1212 */     if (listaggArguments.getOnOverflowTruncate()) {
/* 1213 */       paramStringBuilder.append(" ON OVERFLOW TRUNCATE ");
/* 1214 */       str = listaggArguments.getFilter();
/* 1215 */       if (str != null) {
/* 1216 */         StringUtils.quoteStringSQL(paramStringBuilder, str).append(' ');
/*      */       }
/* 1218 */       paramStringBuilder.append(listaggArguments.isWithoutCount() ? "WITHOUT" : "WITH").append(" COUNT");
/*      */     } 
/* 1220 */     paramStringBuilder.append(')');
/* 1221 */     paramStringBuilder.append(" WITHIN GROUP (");
/* 1222 */     Window.appendOrderBy(paramStringBuilder, this.orderByList, paramInt, true);
/* 1223 */     paramStringBuilder.append(')');
/* 1224 */     return appendTailConditions(paramStringBuilder, paramInt, false);
/*      */   }
/*      */   
/*      */   private StringBuilder getSQLJsonObjectAggregate(StringBuilder paramStringBuilder, int paramInt) {
/* 1228 */     paramStringBuilder.append("JSON_OBJECTAGG(");
/* 1229 */     this.args[0].getUnenclosedSQL(paramStringBuilder, paramInt).append(": ");
/* 1230 */     this.args[1].getUnenclosedSQL(paramStringBuilder, paramInt);
/* 1231 */     JsonConstructorFunction.getJsonFunctionFlagsSQL(paramStringBuilder, this.flags, false).append(')');
/* 1232 */     return appendTailConditions(paramStringBuilder, paramInt, false);
/*      */   }
/*      */   
/*      */   private StringBuilder getSQLJsonArrayAggregate(StringBuilder paramStringBuilder, int paramInt) {
/* 1236 */     paramStringBuilder.append("JSON_ARRAYAGG(");
/* 1237 */     if (this.distinct) {
/* 1238 */       paramStringBuilder.append("DISTINCT ");
/*      */     }
/* 1240 */     this.args[0].getUnenclosedSQL(paramStringBuilder, paramInt);
/* 1241 */     JsonConstructorFunction.getJsonFunctionFlagsSQL(paramStringBuilder, this.flags, true);
/* 1242 */     Window.appendOrderBy(paramStringBuilder, this.orderByList, paramInt, false);
/* 1243 */     paramStringBuilder.append(')');
/* 1244 */     return appendTailConditions(paramStringBuilder, paramInt, false);
/*      */   }
/*      */   
/*      */   private Index getMinMaxColumnIndex() {
/* 1248 */     Expression expression = this.args[0];
/* 1249 */     if (expression instanceof ExpressionColumn) {
/* 1250 */       ExpressionColumn expressionColumn = (ExpressionColumn)expression;
/* 1251 */       Column column = expressionColumn.getColumn();
/* 1252 */       TableFilter tableFilter = expressionColumn.getTableFilter();
/* 1253 */       if (tableFilter != null) {
/* 1254 */         Table table = tableFilter.getTable();
/* 1255 */         return table.getIndexForColumn(column, true, false);
/*      */       } 
/*      */     } 
/* 1258 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 1263 */     if (!super.isEverything(paramExpressionVisitor)) {
/* 1264 */       return false;
/*      */     }
/* 1266 */     if (this.filterCondition != null && !this.filterCondition.isEverything(paramExpressionVisitor)) {
/* 1267 */       return false;
/*      */     }
/* 1269 */     if (paramExpressionVisitor.getType() == 1) {
/* 1270 */       switch (this.aggregateType) {
/*      */         case COUNT:
/* 1272 */           if (this.distinct || this.args[0].getNullable() != 0) {
/* 1273 */             return false;
/*      */           }
/*      */         
/*      */         case COUNT_ALL:
/* 1277 */           return paramExpressionVisitor.getTable().canGetRowCount(this.select.getSession());
/*      */         case MIN:
/*      */         case MAX:
/* 1280 */           return (getMinMaxColumnIndex() != null);
/*      */         case PERCENTILE_CONT:
/*      */         case PERCENTILE_DISC:
/* 1283 */           return (this.args[0].isConstant() && Percentile.getColumnIndex(this.select.getSession().getDatabase(), ((QueryOrderBy)this.orderByList
/* 1284 */               .get(0)).expression) != null);
/*      */         case MEDIAN:
/* 1286 */           if (this.distinct) {
/* 1287 */             return false;
/*      */           }
/* 1289 */           return (Percentile.getColumnIndex(this.select.getSession().getDatabase(), this.args[0]) != null);
/*      */         case ENVELOPE:
/* 1291 */           return (AggregateDataEnvelope.getGeometryColumnIndex(this.args[0]) != null);
/*      */       } 
/* 1293 */       return false;
/*      */     } 
/*      */     
/* 1296 */     for (Expression expression : this.args) {
/* 1297 */       if (!expression.isEverything(paramExpressionVisitor)) {
/* 1298 */         return false;
/*      */       }
/*      */     } 
/* 1301 */     if (this.orderByList != null) {
/* 1302 */       for (QueryOrderBy queryOrderBy : this.orderByList) {
/* 1303 */         if (!queryOrderBy.expression.isEverything(paramExpressionVisitor)) {
/* 1304 */           return false;
/*      */         }
/*      */       } 
/*      */     }
/* 1308 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getCost() {
/* 1313 */     int i = 1;
/* 1314 */     for (Expression expression : this.args) {
/* 1315 */       i += expression.getCost();
/*      */     }
/* 1317 */     if (this.orderByList != null) {
/* 1318 */       for (QueryOrderBy queryOrderBy : this.orderByList) {
/* 1319 */         i += queryOrderBy.expression.getCost();
/*      */       }
/*      */     }
/* 1322 */     if (this.filterCondition != null) {
/* 1323 */       i += this.filterCondition.getCost();
/*      */     }
/* 1325 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Select getSelect() {
/* 1333 */     return this.select;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDistinct() {
/* 1342 */     return this.distinct;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\Aggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */