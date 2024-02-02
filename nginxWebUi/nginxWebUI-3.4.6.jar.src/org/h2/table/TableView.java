/*     */ package org.h2.table;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.command.ddl.CreateTableData;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.index.ViewIndex;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TableView
/*     */   extends Table
/*     */ {
/*     */   private static final long ROW_COUNT_APPROXIMATION = 100L;
/*     */   private String querySQL;
/*     */   private ArrayList<Table> tables;
/*     */   private Column[] columnTemplates;
/*     */   private Query viewQuery;
/*     */   private ViewIndex index;
/*     */   private boolean allowRecursive;
/*     */   private DbException createException;
/*     */   private long lastModificationCheck;
/*     */   private long maxDataModificationId;
/*     */   private User owner;
/*     */   private Query topQuery;
/*     */   private ResultInterface recursiveResult;
/*     */   private boolean isRecursiveQueryDetected;
/*     */   private boolean isTableExpression;
/*     */   
/*     */   public TableView(Schema paramSchema, int paramInt, String paramString1, String paramString2, ArrayList<Parameter> paramArrayList, Column[] paramArrayOfColumn, SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {
/*  66 */     super(paramSchema, paramInt, paramString1, false, true);
/*  67 */     setTemporary(paramBoolean4);
/*  68 */     init(paramString2, paramArrayList, paramArrayOfColumn, paramSessionLocal, paramBoolean1, paramBoolean2, paramBoolean3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void replace(String paramString, Column[] paramArrayOfColumn, SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/*  84 */     String str = this.querySQL;
/*  85 */     Column[] arrayOfColumn = this.columnTemplates;
/*  86 */     boolean bool = this.allowRecursive;
/*  87 */     init(paramString, (ArrayList<Parameter>)null, paramArrayOfColumn, paramSessionLocal, paramBoolean1, paramBoolean3, this.isTableExpression);
/*  88 */     DbException dbException = recompile(paramSessionLocal, paramBoolean2, true);
/*  89 */     if (dbException != null) {
/*  90 */       init(str, (ArrayList<Parameter>)null, arrayOfColumn, paramSessionLocal, bool, paramBoolean3, this.isTableExpression);
/*     */       
/*  92 */       recompile(paramSessionLocal, true, false);
/*  93 */       throw dbException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void init(String paramString, ArrayList<Parameter> paramArrayList, Column[] paramArrayOfColumn, SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
/* 100 */     this.querySQL = paramString;
/* 101 */     this.columnTemplates = paramArrayOfColumn;
/* 102 */     this.allowRecursive = paramBoolean1;
/* 103 */     this.isRecursiveQueryDetected = false;
/* 104 */     this.isTableExpression = paramBoolean3;
/* 105 */     this.index = new ViewIndex(this, paramString, paramArrayList, paramBoolean1);
/* 106 */     initColumnsAndTables(paramSessionLocal, paramBoolean2);
/*     */   }
/*     */   
/*     */   private Query compileViewQuery(SessionLocal paramSessionLocal, String paramString, boolean paramBoolean) {
/*     */     Prepared prepared;
/* 111 */     paramSessionLocal.setParsingCreateView(true);
/*     */     try {
/* 113 */       prepared = paramSessionLocal.prepare(paramString, false, paramBoolean);
/*     */     } finally {
/* 115 */       paramSessionLocal.setParsingCreateView(false);
/*     */     } 
/* 117 */     if (!(prepared instanceof Query)) {
/* 118 */       throw DbException.getSyntaxError(paramString, 0);
/*     */     }
/* 120 */     Query query = (Query)prepared;
/*     */     
/* 122 */     if (this.isTableExpression && this.allowRecursive) {
/* 123 */       query.setNeverLazy(true);
/*     */     }
/* 125 */     return query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized DbException recompile(SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2) {
/*     */     try {
/* 140 */       compileViewQuery(paramSessionLocal, this.querySQL, false);
/* 141 */     } catch (DbException dbException) {
/* 142 */       if (!paramBoolean1) {
/* 143 */         return dbException;
/*     */       }
/*     */     } 
/* 146 */     ArrayList<TableView> arrayList = new ArrayList<>(getDependentViews());
/* 147 */     initColumnsAndTables(paramSessionLocal, false);
/* 148 */     for (TableView tableView : arrayList) {
/* 149 */       DbException dbException = tableView.recompile(paramSessionLocal, paramBoolean1, false);
/* 150 */       if (dbException != null && !paramBoolean1) {
/* 151 */         return dbException;
/*     */       }
/*     */     } 
/* 154 */     if (paramBoolean2) {
/* 155 */       clearIndexCaches(this.database);
/*     */     }
/* 157 */     return paramBoolean1 ? null : this.createException;
/*     */   }
/*     */   
/*     */   private void initColumnsAndTables(SessionLocal paramSessionLocal, boolean paramBoolean) {
/*     */     Column[] arrayOfColumn;
/* 162 */     removeCurrentViewFromOtherTables();
/* 163 */     setTableExpression(this.isTableExpression);
/*     */     try {
/* 165 */       Query query = compileViewQuery(paramSessionLocal, this.querySQL, paramBoolean);
/* 166 */       this.querySQL = query.getPlanSQL(0);
/* 167 */       this.tables = new ArrayList<>(query.getTables());
/* 168 */       ArrayList<Expression> arrayList = query.getExpressions();
/* 169 */       int i = query.getColumnCount();
/* 170 */       ArrayList<Column> arrayList1 = new ArrayList(i);
/* 171 */       for (byte b = 0; b < i; b++) {
/* 172 */         Expression expression = arrayList.get(b);
/* 173 */         String str = null;
/* 174 */         TypeInfo typeInfo = TypeInfo.TYPE_UNKNOWN;
/* 175 */         if (this.columnTemplates != null && this.columnTemplates.length > b) {
/* 176 */           str = this.columnTemplates[b].getName();
/* 177 */           typeInfo = this.columnTemplates[b].getType();
/*     */         } 
/* 179 */         if (str == null) {
/* 180 */           str = expression.getColumnNameForView(paramSessionLocal, b);
/*     */         }
/* 182 */         if (typeInfo.getValueType() == -1) {
/* 183 */           typeInfo = expression.getType();
/*     */         }
/* 185 */         arrayList1.add(new Column(str, typeInfo, this, b));
/*     */       } 
/* 187 */       arrayOfColumn = arrayList1.<Column>toArray(new Column[0]);
/* 188 */       this.createException = null;
/* 189 */       this.viewQuery = query;
/* 190 */     } catch (DbException dbException) {
/* 191 */       if (dbException.getErrorCode() == 90156) {
/* 192 */         throw dbException;
/*     */       }
/* 194 */       dbException.addSQL(getCreateSQL());
/* 195 */       this.createException = dbException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 201 */       if (isRecursiveQueryExceptionDetected(this.createException)) {
/* 202 */         this.isRecursiveQueryDetected = true;
/*     */       }
/* 204 */       this.tables = Utils.newSmallArrayList();
/* 205 */       arrayOfColumn = new Column[0];
/* 206 */       if (this.allowRecursive && this.columnTemplates != null) {
/* 207 */         arrayOfColumn = new Column[this.columnTemplates.length];
/* 208 */         for (byte b = 0; b < this.columnTemplates.length; b++) {
/* 209 */           arrayOfColumn[b] = this.columnTemplates[b].getClone();
/*     */         }
/* 211 */         this.index.setRecursive(true);
/* 212 */         this.createException = null;
/*     */       } 
/*     */     } 
/* 215 */     setColumns(arrayOfColumn);
/* 216 */     if (getId() != 0) {
/* 217 */       addDependentViewToTables();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isView() {
/* 223 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInvalid() {
/* 232 */     return (this.createException != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PlanItem getBestPlanItem(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 239 */     CacheKey cacheKey = new CacheKey(paramArrayOfint, this);
/* 240 */     Map<CacheKey, ViewIndex> map = paramSessionLocal.getViewIndexCache((this.topQuery != null));
/* 241 */     ViewIndex viewIndex = (ViewIndex)map.get(cacheKey);
/* 242 */     if (viewIndex == null || viewIndex.isExpired()) {
/* 243 */       viewIndex = new ViewIndex(this, this.index, paramSessionLocal, paramArrayOfint, paramArrayOfTableFilter, paramInt, paramSortOrder);
/* 244 */       map.put(cacheKey, viewIndex);
/*     */     } 
/* 246 */     PlanItem planItem = new PlanItem();
/* 247 */     planItem.cost = viewIndex.getCost(paramSessionLocal, paramArrayOfint, paramArrayOfTableFilter, paramInt, paramSortOrder, paramAllColumnsForPlan);
/* 248 */     planItem.setIndex((Index)viewIndex);
/* 249 */     return planItem;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQueryComparable() {
/* 254 */     if (!super.isQueryComparable()) {
/* 255 */       return false;
/*     */     }
/* 257 */     for (Table table : this.tables) {
/* 258 */       if (!table.isQueryComparable()) {
/* 259 */         return false;
/*     */       }
/*     */     } 
/* 262 */     if (this.topQuery != null && 
/* 263 */       !this.topQuery.isEverything(ExpressionVisitor.QUERY_COMPARABLE_VISITOR)) {
/* 264 */       return false;
/*     */     }
/* 266 */     return true;
/*     */   }
/*     */   
/*     */   public Query getTopQuery() {
/* 270 */     return this.topQuery;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/* 275 */     return getSQL(new StringBuilder("DROP VIEW IF EXISTS "), 0).append(" CASCADE").toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQLForCopy(Table paramTable, String paramString) {
/* 280 */     return getCreateSQL(false, true, paramString);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/* 286 */     return getCreateSQL(false, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCreateSQL(boolean paramBoolean1, boolean paramBoolean2) {
/* 297 */     return getCreateSQL(paramBoolean1, paramBoolean2, getSQL(0));
/*     */   }
/*     */   
/*     */   private String getCreateSQL(boolean paramBoolean1, boolean paramBoolean2, String paramString) {
/* 301 */     StringBuilder stringBuilder = new StringBuilder("CREATE ");
/* 302 */     if (paramBoolean1) {
/* 303 */       stringBuilder.append("OR REPLACE ");
/*     */     }
/* 305 */     if (paramBoolean2) {
/* 306 */       stringBuilder.append("FORCE ");
/*     */     }
/* 308 */     stringBuilder.append("VIEW ");
/* 309 */     if (this.isTableExpression) {
/* 310 */       stringBuilder.append("TABLE_EXPRESSION ");
/*     */     }
/* 312 */     stringBuilder.append(paramString);
/* 313 */     if (this.comment != null) {
/* 314 */       stringBuilder.append(" COMMENT ");
/* 315 */       StringUtils.quoteStringSQL(stringBuilder, this.comment);
/*     */     } 
/* 317 */     if (this.columns != null && this.columns.length > 0) {
/* 318 */       stringBuilder.append('(');
/* 319 */       Column.writeColumns(stringBuilder, this.columns, 0);
/* 320 */       stringBuilder.append(')');
/* 321 */     } else if (this.columnTemplates != null) {
/* 322 */       stringBuilder.append('(');
/* 323 */       Column.writeColumns(stringBuilder, this.columnTemplates, 0);
/* 324 */       stringBuilder.append(')');
/*     */     } 
/* 326 */     return stringBuilder.append(" AS\n").append(this.querySQL).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Index addIndex(SessionLocal paramSessionLocal, String paramString1, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType, boolean paramBoolean, String paramString2) {
/* 337 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInsertable() {
/* 342 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 347 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 352 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkSupportAlter() {
/* 357 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public long truncate(SessionLocal paramSessionLocal) {
/* 362 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 367 */     throw DbException.getInternalError(toString());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 373 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canDrop() {
/* 378 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableType getTableType() {
/* 383 */     return TableType.VIEW;
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 388 */     removeCurrentViewFromOtherTables();
/* 389 */     super.removeChildrenAndResources(paramSessionLocal);
/* 390 */     this.database.removeMeta(paramSessionLocal, getId());
/* 391 */     this.querySQL = null;
/* 392 */     this.index = null;
/* 393 */     clearIndexCaches(this.database);
/* 394 */     invalidate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void clearIndexCaches(Database paramDatabase) {
/* 403 */     for (SessionLocal sessionLocal : paramDatabase.getSessions(true)) {
/* 404 */       sessionLocal.clearViewIndexCache();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 410 */     if (isTemporary() && this.querySQL != null) {
/* 411 */       paramStringBuilder.append("(\n");
/* 412 */       return StringUtils.indent(paramStringBuilder, this.querySQL, 4, true).append(')');
/*     */     } 
/* 414 */     return super.getSQL(paramStringBuilder, paramInt);
/*     */   }
/*     */   
/*     */   public String getQuery() {
/* 418 */     return this.querySQL;
/*     */   }
/*     */ 
/*     */   
/*     */   public Index getScanIndex(SessionLocal paramSessionLocal) {
/* 423 */     return getBestPlanItem(paramSessionLocal, (int[])null, (TableFilter[])null, -1, (SortOrder)null, (AllColumnsForPlan)null).getIndex();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Index getScanIndex(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 430 */     if (this.createException != null) {
/* 431 */       String str = this.createException.getMessage();
/* 432 */       throw DbException.get(90109, this.createException, new String[] { getTraceSQL(), str });
/*     */     } 
/* 434 */     PlanItem planItem = getBestPlanItem(paramSessionLocal, paramArrayOfint, paramArrayOfTableFilter, paramInt, paramSortOrder, paramAllColumnsForPlan);
/* 435 */     return planItem.getIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canReference() {
/* 440 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<Index> getIndexes() {
/* 445 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxDataModificationId() {
/* 450 */     if (this.createException != null) {
/* 451 */       return Long.MAX_VALUE;
/*     */     }
/* 453 */     if (this.viewQuery == null) {
/* 454 */       return Long.MAX_VALUE;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 459 */     long l = this.database.getModificationDataId();
/* 460 */     if (l > this.lastModificationCheck && this.maxDataModificationId <= l) {
/* 461 */       this.maxDataModificationId = this.viewQuery.getMaxDataModificationId();
/* 462 */       this.lastModificationCheck = l;
/*     */     } 
/* 464 */     return this.maxDataModificationId;
/*     */   }
/*     */   
/*     */   private void removeCurrentViewFromOtherTables() {
/* 468 */     if (this.tables != null) {
/* 469 */       for (Table table : this.tables) {
/* 470 */         table.removeDependentView(this);
/*     */       }
/* 472 */       this.tables.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addDependentViewToTables() {
/* 477 */     for (Table table : this.tables) {
/* 478 */       table.addDependentView(this);
/*     */     }
/*     */   }
/*     */   
/*     */   private void setOwner(User paramUser) {
/* 483 */     this.owner = paramUser;
/*     */   }
/*     */   
/*     */   public User getOwner() {
/* 487 */     return this.owner;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TableView createTempView(SessionLocal paramSessionLocal, User paramUser, String paramString, Column[] paramArrayOfColumn, Query paramQuery1, Query paramQuery2) {
/* 503 */     Schema schema = paramSessionLocal.getDatabase().getMainSchema();
/* 504 */     String str = paramQuery1.getPlanSQL(0);
/*     */     
/* 506 */     TableView tableView = new TableView(schema, 0, paramString, str, paramQuery1.getParameters(), paramArrayOfColumn, paramSessionLocal, false, true, false, true);
/*     */ 
/*     */     
/* 509 */     if (tableView.createException != null) {
/* 510 */       throw tableView.createException;
/*     */     }
/* 512 */     tableView.setTopQuery(paramQuery2);
/* 513 */     tableView.setOwner(paramUser);
/* 514 */     tableView.setTemporary(true);
/* 515 */     return tableView;
/*     */   }
/*     */   
/*     */   private void setTopQuery(Query paramQuery) {
/* 519 */     this.topQuery = paramQuery;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 524 */     return 100L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getParameterOffset(ArrayList<Parameter> paramArrayList) {
/* 534 */     int i = (this.topQuery == null) ? -1 : getMaxParameterIndex(this.topQuery.getParameters());
/* 535 */     if (paramArrayList != null) {
/* 536 */       i = Math.max(i, getMaxParameterIndex(paramArrayList));
/*     */     }
/* 538 */     return i + 1;
/*     */   }
/*     */   
/*     */   private static int getMaxParameterIndex(ArrayList<Parameter> paramArrayList) {
/* 542 */     int i = -1;
/* 543 */     for (Parameter parameter : paramArrayList) {
/* 544 */       if (parameter != null) {
/* 545 */         i = Math.max(i, parameter.getIndex());
/*     */       }
/*     */     } 
/* 548 */     return i;
/*     */   }
/*     */   
/*     */   public boolean isRecursive() {
/* 552 */     return this.allowRecursive;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 557 */     if (this.allowRecursive || this.viewQuery == null) {
/* 558 */       return false;
/*     */     }
/* 560 */     return this.viewQuery.isEverything(ExpressionVisitor.DETERMINISTIC_VISITOR);
/*     */   }
/*     */   
/*     */   public void setRecursiveResult(ResultInterface paramResultInterface) {
/* 564 */     if (this.recursiveResult != null) {
/* 565 */       this.recursiveResult.close();
/*     */     }
/* 567 */     this.recursiveResult = paramResultInterface;
/*     */   }
/*     */   
/*     */   public ResultInterface getRecursiveResult() {
/* 571 */     return this.recursiveResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addDependencies(HashSet<DbObject> paramHashSet) {
/* 576 */     super.addDependencies(paramHashSet);
/* 577 */     if (this.tables != null) {
/* 578 */       for (Table table : this.tables) {
/* 579 */         if (TableType.VIEW != table.getTableType()) {
/* 580 */           table.addDependencies(paramHashSet);
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class CacheKey
/*     */   {
/*     */     private final int[] masks;
/*     */     
/*     */     private final TableView view;
/*     */ 
/*     */     
/*     */     CacheKey(int[] param1ArrayOfint, TableView param1TableView) {
/* 595 */       this.masks = param1ArrayOfint;
/* 596 */       this.view = param1TableView;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 602 */       int i = 1;
/* 603 */       i = 31 * i + Arrays.hashCode(this.masks);
/* 604 */       i = 31 * i + this.view.hashCode();
/* 605 */       return i;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object param1Object) {
/* 610 */       if (this == param1Object) {
/* 611 */         return true;
/*     */       }
/* 613 */       if (param1Object == null) {
/* 614 */         return false;
/*     */       }
/* 616 */       if (getClass() != param1Object.getClass()) {
/* 617 */         return false;
/*     */       }
/* 619 */       CacheKey cacheKey = (CacheKey)param1Object;
/* 620 */       if (this.view != cacheKey.view) {
/* 621 */         return false;
/*     */       }
/* 623 */       return Arrays.equals(this.masks, cacheKey.masks);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRecursiveQueryDetected() {
/* 633 */     return this.isRecursiveQueryDetected;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isRecursiveQueryExceptionDetected(DbException paramDbException) {
/* 640 */     if (paramDbException == null) {
/* 641 */       return false;
/*     */     }
/* 643 */     int i = paramDbException.getErrorCode();
/* 644 */     if (i != 42102 && i != 42104 && i != 42103)
/*     */     {
/*     */ 
/*     */       
/* 648 */       return false;
/*     */     }
/* 650 */     return paramDbException.getMessage().contains("\"" + getName() + "\"");
/*     */   }
/*     */   
/*     */   public List<Table> getTables() {
/* 654 */     return this.tables;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TableView createTableViewMaybeRecursive(Schema paramSchema, int paramInt, String paramString1, String paramString2, ArrayList<Parameter> paramArrayList, Column[] paramArrayOfColumn, SessionLocal paramSessionLocal, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Database paramDatabase) {
/*     */     List<Column> list;
/* 678 */     Table table = createShadowTableForRecursiveTableExpression(paramBoolean3, paramSessionLocal, paramString1, paramSchema, 
/* 679 */         Arrays.asList(paramArrayOfColumn), paramDatabase);
/*     */ 
/*     */     
/* 682 */     String[] arrayOfString = new String[1];
/* 683 */     ArrayList<String> arrayList = new ArrayList();
/* 684 */     for (Column column : paramArrayOfColumn) {
/* 685 */       arrayList.add(column.getName());
/*     */     }
/*     */     
/*     */     try {
/* 689 */       Prepared prepared = paramSessionLocal.prepare(paramString2, false, false);
/* 690 */       if (!paramBoolean3) {
/* 691 */         prepared.setSession(paramSessionLocal);
/*     */       }
/* 693 */       list = createQueryColumnTemplateList(arrayList.<String>toArray(new String[1]), (Query)prepared, arrayOfString);
/*     */     }
/*     */     finally {
/*     */       
/* 697 */       destroyShadowTableForRecursiveExpression(paramBoolean3, paramSessionLocal, table);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 702 */     TableView tableView = new TableView(paramSchema, paramInt, paramString1, paramString2, paramArrayList, list.<Column>toArray(paramArrayOfColumn), paramSessionLocal, true, paramBoolean1, paramBoolean2, paramBoolean3);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 707 */     if (!tableView.isRecursiveQueryDetected()) {
/* 708 */       if (!paramBoolean3) {
/* 709 */         paramDatabase.addSchemaObject(paramSessionLocal, tableView);
/* 710 */         tableView.lock(paramSessionLocal, 2);
/* 711 */         paramSessionLocal.getDatabase().removeSchemaObject(paramSessionLocal, tableView);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 716 */         tableView.removeChildrenAndResources(paramSessionLocal);
/*     */       } else {
/* 718 */         paramSessionLocal.removeLocalTempTable(tableView);
/*     */       } 
/* 720 */       tableView = new TableView(paramSchema, paramInt, paramString1, paramString2, paramArrayList, paramArrayOfColumn, paramSessionLocal, false, paramBoolean1, paramBoolean2, paramBoolean3);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 725 */     return tableView;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Column> createQueryColumnTemplateList(String[] paramArrayOfString1, Query paramQuery, String[] paramArrayOfString2) {
/* 743 */     ArrayList<Column> arrayList = new ArrayList();
/* 744 */     paramQuery.prepare();
/*     */ 
/*     */     
/* 747 */     paramArrayOfString2[0] = StringUtils.cache(paramQuery.getPlanSQL(8));
/* 748 */     SessionLocal sessionLocal = paramQuery.getSession();
/* 749 */     ArrayList<Expression> arrayList1 = paramQuery.getExpressions();
/* 750 */     for (byte b = 0; b < arrayList1.size(); b++) {
/* 751 */       Expression expression = arrayList1.get(b);
/*     */ 
/*     */ 
/*     */       
/* 755 */       String str = (paramArrayOfString1 != null && paramArrayOfString1.length > b) ? paramArrayOfString1[b] : expression.getColumnNameForView(sessionLocal, b);
/* 756 */       arrayList.add(new Column(str, expression.getType()));
/*     */     } 
/*     */     
/* 759 */     return arrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Table createShadowTableForRecursiveTableExpression(boolean paramBoolean, SessionLocal paramSessionLocal, String paramString, Schema paramSchema, List<Column> paramList, Database paramDatabase) {
/* 777 */     CreateTableData createTableData = new CreateTableData();
/* 778 */     createTableData.id = paramDatabase.allocateObjectId();
/* 779 */     createTableData.columns = new ArrayList<>(paramList);
/* 780 */     createTableData.tableName = paramString;
/* 781 */     createTableData.temporary = paramBoolean;
/* 782 */     createTableData.persistData = true;
/* 783 */     createTableData.persistIndexes = !paramBoolean;
/* 784 */     createTableData.session = paramSessionLocal;
/*     */ 
/*     */     
/* 787 */     Table table = paramSchema.createTable(createTableData);
/*     */     
/* 789 */     if (!paramBoolean) {
/*     */       
/* 791 */       paramDatabase.unlockMeta(paramSessionLocal);
/* 792 */       synchronized (paramSessionLocal) {
/* 793 */         paramDatabase.addSchemaObject(paramSessionLocal, table);
/*     */       } 
/*     */     } else {
/* 796 */       paramSessionLocal.addLocalTempTable(table);
/*     */     } 
/* 798 */     return table;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void destroyShadowTableForRecursiveExpression(boolean paramBoolean, SessionLocal paramSessionLocal, Table paramTable) {
/* 810 */     if (paramTable != null) {
/* 811 */       if (!paramBoolean) {
/* 812 */         paramTable.lock(paramSessionLocal, 2);
/* 813 */         paramSessionLocal.getDatabase().removeSchemaObject(paramSessionLocal, paramTable);
/*     */       } else {
/*     */         
/* 816 */         paramSessionLocal.removeLocalTempTable(paramTable);
/*     */       } 
/*     */ 
/*     */       
/* 820 */       paramSessionLocal.getDatabase().unlockMeta(paramSessionLocal);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\TableView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */