/*      */ package org.h2.table;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import org.h2.command.query.AllColumnsForPlan;
/*      */ import org.h2.command.query.Select;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.expression.Expression;
/*      */ import org.h2.expression.condition.ConditionAndOr;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.index.IndexCondition;
/*      */ import org.h2.index.IndexCursor;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.result.Row;
/*      */ import org.h2.result.SearchRow;
/*      */ import org.h2.result.SortOrder;
/*      */ import org.h2.util.ParserUtil;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueSmallint;
/*      */ import org.h2.value.ValueTinyint;
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
/*      */ public class TableFilter
/*      */   implements ColumnResolver
/*      */ {
/*      */   private static final int BEFORE_FIRST = 0;
/*      */   private static final int FOUND = 1;
/*      */   private static final int AFTER_LAST = 2;
/*      */   private static final int NULL_ROW = 3;
/*   55 */   public static final Comparator<TableFilter> ORDER_IN_FROM_COMPARATOR = Comparator.comparing(TableFilter::getOrderInFrom); private static final TableFilterVisitor JOI_VISITOR;
/*      */   protected boolean joinOuterIndirect;
/*      */   private SessionLocal session;
/*      */   
/*      */   static {
/*   60 */     JOI_VISITOR = (paramTableFilter -> paramTableFilter.joinOuterIndirect = true);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private final Table table;
/*      */ 
/*      */   
/*      */   private final Select select;
/*      */ 
/*      */   
/*      */   private String alias;
/*      */ 
/*      */   
/*      */   private Index index;
/*      */ 
/*      */   
/*      */   private final IndexHints indexHints;
/*      */ 
/*      */   
/*      */   private int[] masks;
/*      */ 
/*      */   
/*      */   private int scanCount;
/*      */   
/*      */   private boolean evaluatable;
/*      */   
/*      */   private boolean used;
/*      */   
/*      */   private final IndexCursor cursor;
/*      */   
/*   91 */   private final ArrayList<IndexCondition> indexConditions = Utils.newSmallArrayList();
/*      */ 
/*      */ 
/*      */   
/*      */   private Expression filterCondition;
/*      */ 
/*      */ 
/*      */   
/*      */   private Expression joinCondition;
/*      */ 
/*      */ 
/*      */   
/*      */   private SearchRow currentSearchRow;
/*      */ 
/*      */ 
/*      */   
/*      */   private Row current;
/*      */ 
/*      */ 
/*      */   
/*      */   private int state;
/*      */ 
/*      */ 
/*      */   
/*      */   private TableFilter join;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean joinOuter;
/*      */ 
/*      */ 
/*      */   
/*      */   private TableFilter nestedJoin;
/*      */ 
/*      */ 
/*      */   
/*      */   private LinkedHashMap<Column, Column> commonJoinColumns;
/*      */ 
/*      */ 
/*      */   
/*      */   private TableFilter commonJoinColumnsFilter;
/*      */ 
/*      */ 
/*      */   
/*      */   private ArrayList<Column> commonJoinColumnsToExclude;
/*      */ 
/*      */   
/*      */   private boolean foundOne;
/*      */ 
/*      */   
/*      */   private Expression fullCondition;
/*      */ 
/*      */   
/*      */   private final int hashCode;
/*      */ 
/*      */   
/*      */   private final int orderInFrom;
/*      */ 
/*      */   
/*      */   private LinkedHashMap<Column, String> derivedColumnMap;
/*      */ 
/*      */ 
/*      */   
/*      */   public TableFilter(SessionLocal paramSessionLocal, Table paramTable, String paramString, boolean paramBoolean, Select paramSelect, int paramInt, IndexHints paramIndexHints) {
/*  155 */     this.session = paramSessionLocal;
/*  156 */     this.table = paramTable;
/*  157 */     this.alias = paramString;
/*  158 */     this.select = paramSelect;
/*  159 */     this.cursor = new IndexCursor();
/*  160 */     if (!paramBoolean) {
/*  161 */       paramSessionLocal.getUser().checkTableRight(paramTable, 1);
/*      */     }
/*  163 */     this.hashCode = paramSessionLocal.nextObjectId();
/*  164 */     this.orderInFrom = paramInt;
/*  165 */     this.indexHints = paramIndexHints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getOrderInFrom() {
/*  175 */     return this.orderInFrom;
/*      */   }
/*      */   
/*      */   public IndexCursor getIndexCursor() {
/*  179 */     return this.cursor;
/*      */   }
/*      */ 
/*      */   
/*      */   public Select getSelect() {
/*  184 */     return this.select;
/*      */   }
/*      */   
/*      */   public Table getTable() {
/*  188 */     return this.table;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void lock(SessionLocal paramSessionLocal) {
/*  197 */     this.table.lock(paramSessionLocal, 0);
/*  198 */     if (this.join != null) {
/*  199 */       this.join.lock(paramSessionLocal);
/*      */     }
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
/*      */ 
/*      */   
/*      */   public PlanItem getBestPlanItem(SessionLocal paramSessionLocal, TableFilter[] paramArrayOfTableFilter, int paramInt, AllColumnsForPlan paramAllColumnsForPlan) {
/*  215 */     PlanItem planItem1 = null;
/*  216 */     SortOrder sortOrder = null;
/*  217 */     if (this.select != null) {
/*  218 */       sortOrder = this.select.getSortOrder();
/*      */     }
/*  220 */     if (this.indexConditions.isEmpty()) {
/*  221 */       planItem1 = new PlanItem();
/*  222 */       planItem1.setIndex(this.table.getScanIndex(paramSessionLocal, (int[])null, paramArrayOfTableFilter, paramInt, sortOrder, paramAllColumnsForPlan));
/*      */       
/*  224 */       planItem1.cost = planItem1.getIndex().getCost(paramSessionLocal, null, paramArrayOfTableFilter, paramInt, sortOrder, paramAllColumnsForPlan);
/*      */     } 
/*      */     
/*  227 */     int i = (this.table.getColumns()).length;
/*  228 */     int[] arrayOfInt = new int[i];
/*  229 */     for (IndexCondition indexCondition : this.indexConditions) {
/*  230 */       if (indexCondition.isEvaluatable()) {
/*  231 */         if (indexCondition.isAlwaysFalse()) {
/*  232 */           arrayOfInt = null;
/*      */           break;
/*      */         } 
/*  235 */         int j = indexCondition.getColumn().getColumnId();
/*  236 */         if (j >= 0) {
/*  237 */           arrayOfInt[j] = arrayOfInt[j] | indexCondition.getMask(this.indexConditions);
/*      */         }
/*      */       } 
/*      */     } 
/*  241 */     PlanItem planItem2 = this.table.getBestPlanItem(paramSessionLocal, arrayOfInt, paramArrayOfTableFilter, paramInt, sortOrder, paramAllColumnsForPlan);
/*  242 */     planItem2.setMasks(arrayOfInt);
/*      */ 
/*      */ 
/*      */     
/*  246 */     planItem2.cost -= planItem2.cost * this.indexConditions.size() / 100.0D / (paramInt + 1);
/*      */     
/*  248 */     if (planItem1 != null && planItem1.cost < planItem2.cost) {
/*  249 */       planItem2 = planItem1;
/*      */     }
/*      */     
/*  252 */     if (this.nestedJoin != null) {
/*  253 */       setEvaluatable(true);
/*  254 */       planItem2.setNestedJoinPlan(this.nestedJoin.getBestPlanItem(paramSessionLocal, paramArrayOfTableFilter, paramInt, paramAllColumnsForPlan));
/*      */ 
/*      */       
/*  257 */       planItem2.cost += planItem2.cost * (planItem2.getNestedJoinPlan()).cost;
/*      */     } 
/*  259 */     if (this.join != null) {
/*  260 */       setEvaluatable(true);
/*      */       while (true) {
/*  262 */         paramInt++;
/*  263 */         if (paramArrayOfTableFilter[paramInt] == this.join)
/*  264 */         { planItem2.setJoinPlan(this.join.getBestPlanItem(paramSessionLocal, paramArrayOfTableFilter, paramInt, paramAllColumnsForPlan));
/*      */ 
/*      */           
/*  267 */           planItem2.cost += planItem2.cost * (planItem2.getJoinPlan()).cost; break; } 
/*      */       } 
/*  269 */     }  return planItem2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPlanItem(PlanItem paramPlanItem) {
/*  278 */     if (paramPlanItem == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  283 */     setIndex(paramPlanItem.getIndex());
/*  284 */     this.masks = paramPlanItem.getMasks();
/*  285 */     if (this.nestedJoin != null) {
/*  286 */       if (paramPlanItem.getNestedJoinPlan() != null) {
/*  287 */         this.nestedJoin.setPlanItem(paramPlanItem.getNestedJoinPlan());
/*      */       } else {
/*  289 */         this.nestedJoin.setScanIndexes();
/*      */       } 
/*      */     }
/*  292 */     if (this.join != null) {
/*  293 */       if (paramPlanItem.getJoinPlan() != null) {
/*  294 */         this.join.setPlanItem(paramPlanItem.getJoinPlan());
/*      */       } else {
/*  296 */         this.join.setScanIndexes();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setScanIndexes() {
/*  305 */     if (this.index == null) {
/*  306 */       setIndex(this.table.getScanIndex(this.session));
/*      */     }
/*  308 */     if (this.join != null) {
/*  309 */       this.join.setScanIndexes();
/*      */     }
/*  311 */     if (this.nestedJoin != null) {
/*  312 */       this.nestedJoin.setScanIndexes();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void prepare() {
/*  323 */     for (byte b = 0; b < this.indexConditions.size(); b++) {
/*  324 */       IndexCondition indexCondition = this.indexConditions.get(b);
/*  325 */       if (!indexCondition.isAlwaysFalse()) {
/*  326 */         Column column = indexCondition.getColumn();
/*  327 */         if (column.getColumnId() >= 0 && 
/*  328 */           this.index.getColumnIndex(column) < 0) {
/*  329 */           this.indexConditions.remove(b);
/*  330 */           b--;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  335 */     if (this.nestedJoin != null) {
/*  336 */       if (this.nestedJoin == this) {
/*  337 */         throw DbException.getInternalError("self join");
/*      */       }
/*  339 */       this.nestedJoin.prepare();
/*      */     } 
/*  341 */     if (this.join != null) {
/*  342 */       if (this.join == this) {
/*  343 */         throw DbException.getInternalError("self join");
/*      */       }
/*  345 */       this.join.prepare();
/*      */     } 
/*  347 */     if (this.filterCondition != null) {
/*  348 */       this.filterCondition = this.filterCondition.optimizeCondition(this.session);
/*      */     }
/*  350 */     if (this.joinCondition != null) {
/*  351 */       this.joinCondition = this.joinCondition.optimizeCondition(this.session);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void startQuery(SessionLocal paramSessionLocal) {
/*  361 */     this.session = paramSessionLocal;
/*  362 */     this.scanCount = 0;
/*  363 */     if (this.nestedJoin != null) {
/*  364 */       this.nestedJoin.startQuery(paramSessionLocal);
/*      */     }
/*  366 */     if (this.join != null) {
/*  367 */       this.join.startQuery(paramSessionLocal);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void reset() {
/*  375 */     if (this.nestedJoin != null) {
/*  376 */       this.nestedJoin.reset();
/*      */     }
/*  378 */     if (this.join != null) {
/*  379 */       this.join.reset();
/*      */     }
/*  381 */     this.state = 0;
/*  382 */     this.foundOne = false;
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
/*      */   public boolean next() {
/*      */     // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield state : I
/*      */     //   4: iconst_2
/*      */     //   5: if_icmpne -> 10
/*      */     //   8: iconst_0
/*      */     //   9: ireturn
/*      */     //   10: aload_0
/*      */     //   11: getfield state : I
/*      */     //   14: ifne -> 73
/*      */     //   17: aload_0
/*      */     //   18: getfield cursor : Lorg/h2/index/IndexCursor;
/*      */     //   21: aload_0
/*      */     //   22: getfield session : Lorg/h2/engine/SessionLocal;
/*      */     //   25: aload_0
/*      */     //   26: getfield indexConditions : Ljava/util/ArrayList;
/*      */     //   29: invokevirtual find : (Lorg/h2/engine/SessionLocal;Ljava/util/ArrayList;)V
/*      */     //   32: aload_0
/*      */     //   33: getfield cursor : Lorg/h2/index/IndexCursor;
/*      */     //   36: invokevirtual isAlwaysFalse : ()Z
/*      */     //   39: ifne -> 92
/*      */     //   42: aload_0
/*      */     //   43: getfield nestedJoin : Lorg/h2/table/TableFilter;
/*      */     //   46: ifnull -> 56
/*      */     //   49: aload_0
/*      */     //   50: getfield nestedJoin : Lorg/h2/table/TableFilter;
/*      */     //   53: invokevirtual reset : ()V
/*      */     //   56: aload_0
/*      */     //   57: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   60: ifnull -> 92
/*      */     //   63: aload_0
/*      */     //   64: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   67: invokevirtual reset : ()V
/*      */     //   70: goto -> 92
/*      */     //   73: aload_0
/*      */     //   74: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   77: ifnull -> 92
/*      */     //   80: aload_0
/*      */     //   81: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   84: invokevirtual next : ()Z
/*      */     //   87: ifeq -> 92
/*      */     //   90: iconst_1
/*      */     //   91: ireturn
/*      */     //   92: aload_0
/*      */     //   93: getfield state : I
/*      */     //   96: iconst_3
/*      */     //   97: if_icmpne -> 103
/*      */     //   100: goto -> 358
/*      */     //   103: aload_0
/*      */     //   104: getfield cursor : Lorg/h2/index/IndexCursor;
/*      */     //   107: invokevirtual isAlwaysFalse : ()Z
/*      */     //   110: ifeq -> 121
/*      */     //   113: aload_0
/*      */     //   114: iconst_2
/*      */     //   115: putfield state : I
/*      */     //   118: goto -> 204
/*      */     //   121: aload_0
/*      */     //   122: getfield nestedJoin : Lorg/h2/table/TableFilter;
/*      */     //   125: ifnull -> 143
/*      */     //   128: aload_0
/*      */     //   129: getfield state : I
/*      */     //   132: ifne -> 204
/*      */     //   135: aload_0
/*      */     //   136: iconst_1
/*      */     //   137: putfield state : I
/*      */     //   140: goto -> 204
/*      */     //   143: aload_0
/*      */     //   144: dup
/*      */     //   145: getfield scanCount : I
/*      */     //   148: iconst_1
/*      */     //   149: iadd
/*      */     //   150: dup_x1
/*      */     //   151: putfield scanCount : I
/*      */     //   154: sipush #4095
/*      */     //   157: iand
/*      */     //   158: ifne -> 165
/*      */     //   161: aload_0
/*      */     //   162: invokespecial checkTimeout : ()V
/*      */     //   165: aload_0
/*      */     //   166: getfield cursor : Lorg/h2/index/IndexCursor;
/*      */     //   169: invokevirtual next : ()Z
/*      */     //   172: ifeq -> 199
/*      */     //   175: aload_0
/*      */     //   176: aload_0
/*      */     //   177: getfield cursor : Lorg/h2/index/IndexCursor;
/*      */     //   180: invokevirtual getSearchRow : ()Lorg/h2/result/SearchRow;
/*      */     //   183: putfield currentSearchRow : Lorg/h2/result/SearchRow;
/*      */     //   186: aload_0
/*      */     //   187: aconst_null
/*      */     //   188: putfield current : Lorg/h2/result/Row;
/*      */     //   191: aload_0
/*      */     //   192: iconst_1
/*      */     //   193: putfield state : I
/*      */     //   196: goto -> 204
/*      */     //   199: aload_0
/*      */     //   200: iconst_2
/*      */     //   201: putfield state : I
/*      */     //   204: aload_0
/*      */     //   205: getfield nestedJoin : Lorg/h2/table/TableFilter;
/*      */     //   208: ifnull -> 248
/*      */     //   211: aload_0
/*      */     //   212: getfield state : I
/*      */     //   215: iconst_1
/*      */     //   216: if_icmpne -> 248
/*      */     //   219: aload_0
/*      */     //   220: getfield nestedJoin : Lorg/h2/table/TableFilter;
/*      */     //   223: invokevirtual next : ()Z
/*      */     //   226: ifne -> 248
/*      */     //   229: aload_0
/*      */     //   230: iconst_2
/*      */     //   231: putfield state : I
/*      */     //   234: aload_0
/*      */     //   235: getfield joinOuter : Z
/*      */     //   238: ifeq -> 92
/*      */     //   241: aload_0
/*      */     //   242: getfield foundOne : Z
/*      */     //   245: ifne -> 92
/*      */     //   248: aload_0
/*      */     //   249: getfield state : I
/*      */     //   252: iconst_2
/*      */     //   253: if_icmpne -> 274
/*      */     //   256: aload_0
/*      */     //   257: getfield joinOuter : Z
/*      */     //   260: ifeq -> 358
/*      */     //   263: aload_0
/*      */     //   264: getfield foundOne : Z
/*      */     //   267: ifne -> 358
/*      */     //   270: aload_0
/*      */     //   271: invokevirtual setNullRow : ()V
/*      */     //   274: aload_0
/*      */     //   275: aload_0
/*      */     //   276: getfield filterCondition : Lorg/h2/expression/Expression;
/*      */     //   279: invokevirtual isOk : (Lorg/h2/expression/Expression;)Z
/*      */     //   282: ifne -> 288
/*      */     //   285: goto -> 92
/*      */     //   288: aload_0
/*      */     //   289: aload_0
/*      */     //   290: getfield joinCondition : Lorg/h2/expression/Expression;
/*      */     //   293: invokevirtual isOk : (Lorg/h2/expression/Expression;)Z
/*      */     //   296: istore_1
/*      */     //   297: aload_0
/*      */     //   298: getfield state : I
/*      */     //   301: iconst_1
/*      */     //   302: if_icmpne -> 314
/*      */     //   305: iload_1
/*      */     //   306: ifeq -> 92
/*      */     //   309: aload_0
/*      */     //   310: iconst_1
/*      */     //   311: putfield foundOne : Z
/*      */     //   314: aload_0
/*      */     //   315: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   318: ifnull -> 341
/*      */     //   321: aload_0
/*      */     //   322: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   325: invokevirtual reset : ()V
/*      */     //   328: aload_0
/*      */     //   329: getfield join : Lorg/h2/table/TableFilter;
/*      */     //   332: invokevirtual next : ()Z
/*      */     //   335: ifne -> 341
/*      */     //   338: goto -> 92
/*      */     //   341: aload_0
/*      */     //   342: getfield state : I
/*      */     //   345: iconst_3
/*      */     //   346: if_icmpeq -> 353
/*      */     //   349: iload_1
/*      */     //   350: ifeq -> 355
/*      */     //   353: iconst_1
/*      */     //   354: ireturn
/*      */     //   355: goto -> 92
/*      */     //   358: aload_0
/*      */     //   359: iconst_2
/*      */     //   360: putfield state : I
/*      */     //   363: iconst_0
/*      */     //   364: ireturn
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #391	-> 0
/*      */     //   #392	-> 8
/*      */     //   #393	-> 10
/*      */     //   #394	-> 17
/*      */     //   #395	-> 32
/*      */     //   #396	-> 42
/*      */     //   #397	-> 49
/*      */     //   #399	-> 56
/*      */     //   #400	-> 63
/*      */     //   #406	-> 73
/*      */     //   #407	-> 90
/*      */     //   #412	-> 92
/*      */     //   #413	-> 100
/*      */     //   #415	-> 103
/*      */     //   #416	-> 113
/*      */     //   #417	-> 121
/*      */     //   #418	-> 128
/*      */     //   #419	-> 135
/*      */     //   #422	-> 143
/*      */     //   #423	-> 161
/*      */     //   #425	-> 165
/*      */     //   #426	-> 175
/*      */     //   #427	-> 186
/*      */     //   #428	-> 191
/*      */     //   #430	-> 199
/*      */     //   #433	-> 204
/*      */     //   #434	-> 219
/*      */     //   #435	-> 229
/*      */     //   #436	-> 234
/*      */     //   #444	-> 248
/*      */     //   #445	-> 256
/*      */     //   #446	-> 270
/*      */     //   #451	-> 274
/*      */     //   #452	-> 285
/*      */     //   #454	-> 288
/*      */     //   #455	-> 297
/*      */     //   #456	-> 305
/*      */     //   #457	-> 309
/*      */     //   #462	-> 314
/*      */     //   #463	-> 321
/*      */     //   #464	-> 328
/*      */     //   #465	-> 338
/*      */     //   #469	-> 341
/*      */     //   #470	-> 353
/*      */     //   #472	-> 355
/*      */     //   #473	-> 358
/*      */     //   #474	-> 363
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
/*      */   public boolean isNullRow() {
/*  478 */     return (this.state == 3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void setNullRow() {
/*  485 */     this.state = 3;
/*  486 */     this.current = this.table.getNullRow();
/*  487 */     this.currentSearchRow = (SearchRow)this.current;
/*  488 */     if (this.nestedJoin != null) {
/*  489 */       this.nestedJoin.visit(TableFilter::setNullRow);
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkTimeout() {
/*  494 */     this.session.checkCanceled();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean isOk(Expression paramExpression) {
/*  505 */     return (paramExpression == null || paramExpression.getBooleanValue(this.session));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Row get() {
/*  514 */     if (this.current == null && this.currentSearchRow != null) {
/*  515 */       this.current = this.cursor.get();
/*      */     }
/*  517 */     return this.current;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void set(Row paramRow) {
/*  526 */     this.current = paramRow;
/*  527 */     this.currentSearchRow = (SearchRow)paramRow;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTableAlias() {
/*  538 */     if (this.alias != null) {
/*  539 */       return this.alias;
/*      */     }
/*  541 */     return this.table.getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addIndexCondition(IndexCondition paramIndexCondition) {
/*  550 */     this.indexConditions.add(paramIndexCondition);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addFilterCondition(Expression paramExpression, boolean paramBoolean) {
/*  560 */     if (paramBoolean) {
/*  561 */       if (this.joinCondition == null) {
/*  562 */         this.joinCondition = paramExpression;
/*      */       } else {
/*  564 */         this.joinCondition = (Expression)new ConditionAndOr(0, this.joinCondition, paramExpression);
/*      */       }
/*      */     
/*      */     }
/*  568 */     else if (this.filterCondition == null) {
/*  569 */       this.filterCondition = paramExpression;
/*      */     } else {
/*  571 */       this.filterCondition = (Expression)new ConditionAndOr(0, this.filterCondition, paramExpression);
/*      */     } 
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
/*      */   public void addJoin(TableFilter paramTableFilter, boolean paramBoolean, Expression paramExpression) {
/*  585 */     if (paramExpression != null) {
/*  586 */       paramExpression.mapColumns(this, 0, 0);
/*  587 */       MapColumnsVisitor mapColumnsVisitor = new MapColumnsVisitor(paramExpression);
/*  588 */       visit(mapColumnsVisitor);
/*  589 */       paramTableFilter.visit(mapColumnsVisitor);
/*      */     } 
/*  591 */     if (this.join == null) {
/*  592 */       this.join = paramTableFilter;
/*  593 */       paramTableFilter.joinOuter = paramBoolean;
/*  594 */       if (paramBoolean) {
/*  595 */         paramTableFilter.visit(JOI_VISITOR);
/*      */       }
/*  597 */       if (paramExpression != null) {
/*  598 */         paramTableFilter.mapAndAddFilter(paramExpression);
/*      */       }
/*      */     } else {
/*  601 */       this.join.addJoin(paramTableFilter, paramBoolean, paramExpression);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNestedJoin(TableFilter paramTableFilter) {
/*  611 */     this.nestedJoin = paramTableFilter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapAndAddFilter(Expression paramExpression) {
/*  620 */     paramExpression.mapColumns(this, 0, 0);
/*  621 */     addFilterCondition(paramExpression, true);
/*  622 */     if (this.nestedJoin != null) {
/*  623 */       paramExpression.mapColumns(this.nestedJoin, 0, 0);
/*      */     }
/*  625 */     if (this.join != null) {
/*  626 */       this.join.mapAndAddFilter(paramExpression);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void createIndexConditions() {
/*  634 */     if (this.joinCondition != null) {
/*  635 */       this.joinCondition = this.joinCondition.optimizeCondition(this.session);
/*  636 */       if (this.joinCondition != null) {
/*  637 */         this.joinCondition.createIndexConditions(this.session, this);
/*  638 */         if (this.nestedJoin != null) {
/*  639 */           this.joinCondition.createIndexConditions(this.session, this.nestedJoin);
/*      */         }
/*      */       } 
/*      */     } 
/*  643 */     if (this.join != null) {
/*  644 */       this.join.createIndexConditions();
/*      */     }
/*  646 */     if (this.nestedJoin != null) {
/*  647 */       this.nestedJoin.createIndexConditions();
/*      */     }
/*      */   }
/*      */   
/*      */   public TableFilter getJoin() {
/*  652 */     return this.join;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isJoinOuter() {
/*  661 */     return this.joinOuter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isJoinOuterIndirect() {
/*  671 */     return this.joinOuterIndirect;
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
/*      */   public StringBuilder getPlanSQL(StringBuilder paramStringBuilder, boolean paramBoolean, int paramInt) {
/*  684 */     if (paramBoolean) {
/*  685 */       if (this.joinOuter) {
/*  686 */         paramStringBuilder.append("LEFT OUTER JOIN ");
/*      */       } else {
/*  688 */         paramStringBuilder.append("INNER JOIN ");
/*      */       } 
/*      */     }
/*  691 */     if (this.nestedJoin != null) {
/*  692 */       StringBuilder stringBuilder = new StringBuilder();
/*  693 */       TableFilter tableFilter = this.nestedJoin;
/*      */       while (true) {
/*  695 */         tableFilter.getPlanSQL(stringBuilder, (tableFilter != this.nestedJoin), paramInt).append('\n');
/*  696 */         tableFilter = tableFilter.getJoin();
/*  697 */         if (tableFilter == null)
/*  698 */         { String str = stringBuilder.toString();
/*  699 */           boolean bool = !str.startsWith("(") ? true : false;
/*  700 */           if (bool) {
/*  701 */             paramStringBuilder.append("(\n");
/*      */           }
/*  703 */           StringUtils.indent(paramStringBuilder, str, 4, false);
/*  704 */           if (bool) {
/*  705 */             paramStringBuilder.append(')');
/*      */           }
/*  707 */           if (paramBoolean) {
/*  708 */             paramStringBuilder.append(" ON ");
/*  709 */             if (this.joinCondition == null) {
/*      */ 
/*      */               
/*  712 */               paramStringBuilder.append("1=1");
/*      */             } else {
/*  714 */               this.joinCondition.getUnenclosedSQL(paramStringBuilder, paramInt);
/*      */             } 
/*      */           } 
/*  717 */           return paramStringBuilder; } 
/*      */       } 
/*  719 */     }  if (this.table instanceof TableView && ((TableView)this.table).isRecursive()) {
/*  720 */       this.table.getSchema().getSQL(paramStringBuilder, paramInt).append('.');
/*  721 */       ParserUtil.quoteIdentifier(paramStringBuilder, this.table.getName(), paramInt);
/*      */     } else {
/*  723 */       this.table.getSQL(paramStringBuilder, paramInt);
/*      */     } 
/*  725 */     if (this.table instanceof TableView && ((TableView)this.table).isInvalid()) {
/*  726 */       throw DbException.get(90109, new String[] { this.table.getName(), "not compiled" });
/*      */     }
/*  728 */     if (this.alias != null) {
/*  729 */       paramStringBuilder.append(' ');
/*  730 */       ParserUtil.quoteIdentifier(paramStringBuilder, this.alias, paramInt);
/*  731 */       if (this.derivedColumnMap != null) {
/*  732 */         paramStringBuilder.append('(');
/*  733 */         boolean bool = false;
/*  734 */         for (String str : this.derivedColumnMap.values()) {
/*  735 */           if (bool) {
/*  736 */             paramStringBuilder.append(", ");
/*      */           }
/*  738 */           bool = true;
/*  739 */           ParserUtil.quoteIdentifier(paramStringBuilder, str, paramInt);
/*      */         } 
/*  741 */         paramStringBuilder.append(')');
/*      */       } 
/*      */     } 
/*  744 */     if (this.indexHints != null) {
/*  745 */       paramStringBuilder.append(" USE INDEX (");
/*  746 */       boolean bool = true;
/*  747 */       for (String str : this.indexHints.getAllowedIndexes()) {
/*  748 */         if (!bool) {
/*  749 */           paramStringBuilder.append(", ");
/*      */         } else {
/*  751 */           bool = false;
/*      */         } 
/*  753 */         ParserUtil.quoteIdentifier(paramStringBuilder, str, paramInt);
/*      */       } 
/*  755 */       paramStringBuilder.append(")");
/*      */     } 
/*  757 */     if (this.index != null && (paramInt & 0x8) != 0) {
/*  758 */       paramStringBuilder.append('\n');
/*  759 */       StringBuilder stringBuilder = (new StringBuilder()).append("/* ").append(this.index.getPlanSQL());
/*  760 */       if (!this.indexConditions.isEmpty()) {
/*  761 */         stringBuilder.append(": "); byte b; int i;
/*  762 */         for (b = 0, i = this.indexConditions.size(); b < i; b++) {
/*  763 */           if (b > 0) {
/*  764 */             stringBuilder.append("\n    AND ");
/*      */           }
/*  766 */           stringBuilder.append(((IndexCondition)this.indexConditions.get(b)).getSQL(11));
/*      */         } 
/*      */       } 
/*      */       
/*  770 */       if (stringBuilder.indexOf("\n", 3) >= 0) {
/*  771 */         stringBuilder.append('\n');
/*      */       }
/*  773 */       StringUtils.indent(paramStringBuilder, stringBuilder.append(" */").toString(), 4, false);
/*      */     } 
/*  775 */     if (paramBoolean) {
/*  776 */       paramStringBuilder.append("\n    ON ");
/*  777 */       if (this.joinCondition == null) {
/*      */ 
/*      */         
/*  780 */         paramStringBuilder.append("1=1");
/*      */       } else {
/*  782 */         this.joinCondition.getUnenclosedSQL(paramStringBuilder, paramInt);
/*      */       } 
/*      */     } 
/*  785 */     if ((paramInt & 0x8) != 0) {
/*  786 */       if (this.filterCondition != null) {
/*  787 */         paramStringBuilder.append('\n');
/*  788 */         String str = this.filterCondition.getSQL(11, 2);
/*      */         
/*  790 */         str = "/* WHERE " + str + "\n*/";
/*  791 */         StringUtils.indent(paramStringBuilder, str, 4, false);
/*      */       } 
/*  793 */       if (this.scanCount > 0) {
/*  794 */         paramStringBuilder.append("\n    /* scanCount: ").append(this.scanCount).append(" */");
/*      */       }
/*      */     } 
/*  797 */     return paramStringBuilder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void removeUnusableIndexConditions() {
/*  805 */     for (byte b = 0; b < this.indexConditions.size(); b++) {
/*  806 */       IndexCondition indexCondition = this.indexConditions.get(b);
/*  807 */       if (indexCondition.getMask(this.indexConditions) == 0 || !indexCondition.isEvaluatable()) {
/*  808 */         this.indexConditions.remove(b--);
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public int[] getMasks() {
/*  814 */     return this.masks;
/*      */   }
/*      */   
/*      */   public ArrayList<IndexCondition> getIndexConditions() {
/*  818 */     return this.indexConditions;
/*      */   }
/*      */   
/*      */   public Index getIndex() {
/*  822 */     return this.index;
/*      */   }
/*      */   
/*      */   public void setIndex(Index paramIndex) {
/*  826 */     this.index = paramIndex;
/*  827 */     this.cursor.setIndex(paramIndex);
/*      */   }
/*      */   
/*      */   public void setUsed(boolean paramBoolean) {
/*  831 */     this.used = paramBoolean;
/*      */   }
/*      */   
/*      */   public boolean isUsed() {
/*  835 */     return this.used;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeJoin() {
/*  842 */     this.join = null;
/*      */   }
/*      */   
/*      */   public Expression getJoinCondition() {
/*  846 */     return this.joinCondition;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeJoinCondition() {
/*  853 */     this.joinCondition = null;
/*      */   }
/*      */   
/*      */   public Expression getFilterCondition() {
/*  857 */     return this.filterCondition;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeFilterCondition() {
/*  864 */     this.filterCondition = null;
/*      */   }
/*      */   
/*      */   public void setFullCondition(Expression paramExpression) {
/*  868 */     this.fullCondition = paramExpression;
/*  869 */     if (this.join != null) {
/*  870 */       this.join.setFullCondition(paramExpression);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void optimizeFullCondition() {
/*  879 */     if (!this.joinOuter && this.fullCondition != null) {
/*  880 */       this.fullCondition.addFilterConditions(this);
/*  881 */       if (this.nestedJoin != null) {
/*  882 */         this.nestedJoin.optimizeFullCondition();
/*      */       }
/*  884 */       if (this.join != null) {
/*  885 */         this.join.optimizeFullCondition();
/*      */       }
/*      */     } 
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
/*      */   public void setEvaluatable(TableFilter paramTableFilter, boolean paramBoolean) {
/*  899 */     paramTableFilter.setEvaluatable(paramBoolean);
/*  900 */     if (this.filterCondition != null) {
/*  901 */       this.filterCondition.setEvaluatable(paramTableFilter, paramBoolean);
/*      */     }
/*  903 */     if (this.joinCondition != null) {
/*  904 */       this.joinCondition.setEvaluatable(paramTableFilter, paramBoolean);
/*      */     }
/*  906 */     if (this.nestedJoin != null)
/*      */     {
/*      */       
/*  909 */       if (this == paramTableFilter) {
/*  910 */         this.nestedJoin.setEvaluatable(this.nestedJoin, paramBoolean);
/*      */       }
/*      */     }
/*  913 */     if (this.join != null) {
/*  914 */       this.join.setEvaluatable(paramTableFilter, paramBoolean);
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEvaluatable(boolean paramBoolean) {
/*  919 */     this.evaluatable = paramBoolean;
/*      */   }
/*      */ 
/*      */   
/*      */   public String getSchemaName() {
/*  924 */     if (this.alias == null && !(this.table instanceof VirtualTable)) {
/*  925 */       return this.table.getSchema().getName();
/*      */     }
/*  927 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public Column[] getColumns() {
/*  932 */     return this.table.getColumns();
/*      */   }
/*      */ 
/*      */   
/*      */   public Column findColumn(String paramString) {
/*  937 */     LinkedHashMap<Column, String> linkedHashMap = this.derivedColumnMap;
/*  938 */     if (linkedHashMap != null) {
/*  939 */       Database database = this.session.getDatabase();
/*  940 */       for (Map.Entry<Column, String> entry : this.derivedColumnMap.entrySet()) {
/*  941 */         if (database.equalsIdentifiers((String)entry.getValue(), paramString)) {
/*  942 */           return (Column)entry.getKey();
/*      */         }
/*      */       } 
/*  945 */       return null;
/*      */     } 
/*  947 */     return this.table.findColumn(paramString);
/*      */   }
/*      */ 
/*      */   
/*      */   public String getColumnName(Column paramColumn) {
/*  952 */     LinkedHashMap<Column, String> linkedHashMap = this.derivedColumnMap;
/*  953 */     return (linkedHashMap != null) ? linkedHashMap.get(paramColumn) : paramColumn.getName();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasDerivedColumnList() {
/*  958 */     return (this.derivedColumnMap != null);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getColumn(String paramString, boolean paramBoolean) {
/*  974 */     LinkedHashMap<Column, String> linkedHashMap = this.derivedColumnMap;
/*  975 */     if (linkedHashMap != null) {
/*  976 */       Database database = this.session.getDatabase();
/*  977 */       for (Map.Entry<Column, String> entry : linkedHashMap.entrySet()) {
/*  978 */         if (database.equalsIdentifiers(paramString, (String)entry.getValue())) {
/*  979 */           return (Column)entry.getKey();
/*      */         }
/*      */       } 
/*  982 */       if (paramBoolean) {
/*  983 */         return null;
/*      */       }
/*  985 */       throw DbException.get(42122, paramString);
/*      */     } 
/*      */     
/*  988 */     return this.table.getColumn(paramString, paramBoolean);
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
/*      */   public Column[] getSystemColumns() {
/* 1000 */     if (!(this.session.getDatabase().getMode()).systemColumns) {
/* 1001 */       return null;
/*      */     }
/* 1003 */     return new Column[] { new Column("oid", TypeInfo.TYPE_INTEGER, this.table, 0), new Column("ctid", TypeInfo.TYPE_VARCHAR, this.table, 0) };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Column getRowIdColumn() {
/* 1012 */     return this.table.getRowIdColumn();
/*      */   }
/*      */ 
/*      */   
/*      */   public Value getValue(Column paramColumn) {
/* 1017 */     if (this.currentSearchRow == null) {
/* 1018 */       return null;
/*      */     }
/* 1020 */     int i = paramColumn.getColumnId();
/* 1021 */     if (i == -1) {
/* 1022 */       return (Value)ValueBigint.get(this.currentSearchRow.getKey());
/*      */     }
/* 1024 */     if (this.current == null) {
/* 1025 */       Value value = this.currentSearchRow.getValue(i);
/* 1026 */       if (value != null) {
/* 1027 */         return value;
/*      */       }
/* 1029 */       if (i == paramColumn.getTable().getMainIndexColumn()) {
/* 1030 */         return getDelegatedValue(paramColumn);
/*      */       }
/* 1032 */       this.current = this.cursor.get();
/* 1033 */       if (this.current == null) {
/* 1034 */         return (Value)ValueNull.INSTANCE;
/*      */       }
/*      */     } 
/* 1037 */     return this.current.getValue(i);
/*      */   }
/*      */   
/*      */   private Value getDelegatedValue(Column paramColumn) {
/* 1041 */     long l = this.currentSearchRow.getKey();
/* 1042 */     switch (paramColumn.getType().getValueType()) {
/*      */       case 9:
/* 1044 */         return (Value)ValueTinyint.get((byte)(int)l);
/*      */       case 10:
/* 1046 */         return (Value)ValueSmallint.get((short)(int)l);
/*      */       case 11:
/* 1048 */         return (Value)ValueInteger.get((int)l);
/*      */       case 12:
/* 1050 */         return (Value)ValueBigint.get(l);
/*      */     } 
/* 1052 */     throw DbException.getInternalError();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public TableFilter getTableFilter() {
/* 1058 */     return this;
/*      */   }
/*      */   
/*      */   public void setAlias(String paramString) {
/* 1062 */     this.alias = paramString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDerivedColumns(ArrayList<String> paramArrayList) {
/* 1071 */     Column[] arrayOfColumn = getColumns();
/* 1072 */     int i = arrayOfColumn.length;
/* 1073 */     if (i != paramArrayList.size()) {
/* 1074 */       throw DbException.get(21002);
/*      */     }
/* 1076 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>();
/* 1077 */     for (byte b = 0; b < i; b++) {
/* 1078 */       String str = paramArrayList.get(b);
/* 1079 */       for (byte b1 = 0; b1 < b; b1++) {
/* 1080 */         if (str.equals(paramArrayList.get(b1))) {
/* 1081 */           throw DbException.get(42121, str);
/*      */         }
/*      */       } 
/* 1084 */       linkedHashMap.put(arrayOfColumn[b], str);
/*      */     } 
/* 1086 */     this.derivedColumnMap = (LinkedHashMap)linkedHashMap;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1091 */     return (this.alias != null) ? this.alias : this.table.toString();
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
/*      */ 
/*      */   
/*      */   public void addCommonJoinColumns(Column paramColumn1, Column paramColumn2, TableFilter paramTableFilter) {
/* 1106 */     if (this.commonJoinColumns == null) {
/* 1107 */       this.commonJoinColumns = new LinkedHashMap<>();
/* 1108 */       this.commonJoinColumnsFilter = paramTableFilter;
/*      */     } else {
/* 1110 */       assert this.commonJoinColumnsFilter == paramTableFilter;
/*      */     } 
/* 1112 */     this.commonJoinColumns.put(paramColumn1, paramColumn2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addCommonJoinColumnToExclude(Column paramColumn) {
/* 1122 */     if (this.commonJoinColumnsToExclude == null) {
/* 1123 */       this.commonJoinColumnsToExclude = Utils.newSmallArrayList();
/*      */     }
/* 1125 */     this.commonJoinColumnsToExclude.add(paramColumn);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public LinkedHashMap<Column, Column> getCommonJoinColumns() {
/* 1134 */     return this.commonJoinColumns;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TableFilter getCommonJoinColumnsFilter() {
/* 1143 */     return this.commonJoinColumnsFilter;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCommonJoinColumnToExclude(Column paramColumn) {
/* 1154 */     return (this.commonJoinColumnsToExclude != null && this.commonJoinColumnsToExclude.contains(paramColumn));
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1159 */     return this.hashCode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasInComparisons() {
/* 1168 */     for (IndexCondition indexCondition : this.indexConditions) {
/* 1169 */       int i = indexCondition.getCompareType();
/* 1170 */       if (i == 11 || i == 10) {
/* 1171 */         return true;
/*      */       }
/*      */     } 
/* 1174 */     return false;
/*      */   }
/*      */   
/*      */   public TableFilter getNestedJoin() {
/* 1178 */     return this.nestedJoin;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void visit(TableFilterVisitor paramTableFilterVisitor) {
/* 1187 */     TableFilter tableFilter = this;
/*      */     do {
/* 1189 */       paramTableFilterVisitor.accept(tableFilter);
/* 1190 */       TableFilter tableFilter1 = tableFilter.nestedJoin;
/* 1191 */       if (tableFilter1 != null) {
/* 1192 */         tableFilter1.visit(paramTableFilterVisitor);
/*      */       }
/* 1194 */       tableFilter = tableFilter.join;
/* 1195 */     } while (tableFilter != null);
/*      */   }
/*      */   
/*      */   public boolean isEvaluatable() {
/* 1199 */     return this.evaluatable;
/*      */   }
/*      */   
/*      */   public SessionLocal getSession() {
/* 1203 */     return this.session;
/*      */   }
/*      */   
/*      */   public IndexHints getIndexHints() {
/* 1207 */     return this.indexHints;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isNoFromClauseFilter() {
/* 1217 */     return (this.table instanceof DualTable && this.join == null && this.nestedJoin == null && this.joinCondition == null && this.filterCondition == null);
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
/*      */   private static final class MapColumnsVisitor
/*      */     implements TableFilterVisitor
/*      */   {
/*      */     private final Expression on;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     MapColumnsVisitor(Expression param1Expression) {
/* 1241 */       this.on = param1Expression;
/*      */     }
/*      */ 
/*      */     
/*      */     public void accept(TableFilter param1TableFilter) {
/* 1246 */       this.on.mapColumns(param1TableFilter, 0, 0);
/*      */     }
/*      */   }
/*      */   
/*      */   public static interface TableFilterVisitor {
/*      */     void accept(TableFilter param1TableFilter);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */