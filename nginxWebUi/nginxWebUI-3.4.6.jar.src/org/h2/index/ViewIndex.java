/*     */ package org.h2.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.command.query.Query;
/*     */ import org.h2.command.query.SelectUnion;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Parameter;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.table.TableView;
/*     */ import org.h2.util.IntArray;
/*     */ import org.h2.value.Value;
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
/*     */ public class ViewIndex
/*     */   extends Index
/*     */   implements SpatialIndex
/*     */ {
/*  39 */   private static final long MAX_AGE_NANOS = TimeUnit.MILLISECONDS
/*  40 */     .toNanos(10000L);
/*     */ 
/*     */   
/*     */   private final TableView view;
/*     */ 
/*     */   
/*     */   private final String querySQL;
/*     */ 
/*     */   
/*     */   private final ArrayList<Parameter> originalParameters;
/*     */ 
/*     */   
/*     */   private boolean recursive;
/*     */ 
/*     */   
/*     */   private final int[] indexMasks;
/*     */   
/*     */   private Query query;
/*     */   
/*     */   private final SessionLocal createSession;
/*     */   
/*     */   private final long evaluatedAt;
/*     */ 
/*     */   
/*     */   public ViewIndex(TableView paramTableView, String paramString, ArrayList<Parameter> paramArrayList, boolean paramBoolean) {
/*  65 */     super((Table)paramTableView, 0, null, null, 0, IndexType.createNonUnique(false));
/*  66 */     this.view = paramTableView;
/*  67 */     this.querySQL = paramString;
/*  68 */     this.originalParameters = paramArrayList;
/*  69 */     this.recursive = paramBoolean;
/*  70 */     this.columns = new Column[0];
/*  71 */     this.createSession = null;
/*  72 */     this.indexMasks = null;
/*     */ 
/*     */     
/*  75 */     this.evaluatedAt = Long.MIN_VALUE;
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
/*     */   public ViewIndex(TableView paramTableView, ViewIndex paramViewIndex, SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder) {
/*  92 */     super((Table)paramTableView, 0, null, null, 0, IndexType.createNonUnique(false));
/*  93 */     this.view = paramTableView;
/*  94 */     this.querySQL = paramViewIndex.querySQL;
/*  95 */     this.originalParameters = paramViewIndex.originalParameters;
/*  96 */     this.recursive = paramViewIndex.recursive;
/*  97 */     this.indexMasks = paramArrayOfint;
/*  98 */     this.createSession = paramSessionLocal;
/*  99 */     this.columns = new Column[0];
/* 100 */     if (!this.recursive) {
/* 101 */       this.query = getQuery(paramSessionLocal, paramArrayOfint);
/*     */     }
/* 103 */     if (this.recursive || paramTableView.getTopQuery() != null) {
/* 104 */       this.evaluatedAt = Long.MAX_VALUE;
/*     */     } else {
/* 106 */       long l = System.nanoTime();
/* 107 */       if (l == Long.MAX_VALUE) {
/* 108 */         l++;
/*     */       }
/* 110 */       this.evaluatedAt = l;
/*     */     } 
/*     */   }
/*     */   
/*     */   public SessionLocal getSession() {
/* 115 */     return this.createSession;
/*     */   }
/*     */   
/*     */   public boolean isExpired() {
/* 119 */     assert this.evaluatedAt != Long.MIN_VALUE : "must not be called for main index of TableView";
/* 120 */     return (!this.recursive && this.view.getTopQuery() == null && 
/* 121 */       System.nanoTime() - this.evaluatedAt > MAX_AGE_NANOS);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL() {
/* 126 */     return (this.query == null) ? null : this.query.getPlanSQL(11);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/* 136 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/* 141 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 148 */     return this.recursive ? 1000.0D : this.query.getCost();
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 153 */     return find(paramSessionLocal, paramSearchRow1, paramSearchRow2, (SearchRow)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findByGeometry(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2, SearchRow paramSearchRow3) {
/* 158 */     return find(paramSessionLocal, paramSearchRow1, paramSearchRow2, paramSearchRow3);
/*     */   }
/*     */   
/*     */   private Cursor findRecursive(SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 162 */     assert this.recursive;
/* 163 */     ResultInterface resultInterface1 = this.view.getRecursiveResult();
/* 164 */     if (resultInterface1 != null) {
/* 165 */       resultInterface1.reset();
/* 166 */       return new ViewCursor(this, resultInterface1, paramSearchRow1, paramSearchRow2);
/*     */     } 
/* 168 */     if (this.query == null) {
/* 169 */       Parser parser = new Parser(this.createSession);
/* 170 */       parser.setRightsChecked(true);
/* 171 */       parser.setSuppliedParameters(this.originalParameters);
/* 172 */       this.query = (Query)parser.prepare(this.querySQL);
/* 173 */       this.query.setNeverLazy(true);
/*     */     } 
/* 175 */     if (!this.query.isUnion()) {
/* 176 */       throw DbException.get(42001, "recursive queries without UNION");
/*     */     }
/*     */     
/* 179 */     SelectUnion selectUnion = (SelectUnion)this.query;
/* 180 */     Query query1 = selectUnion.getLeft();
/* 181 */     query1.setNeverLazy(true);
/*     */     
/* 183 */     query1.disableCache();
/* 184 */     ResultInterface resultInterface2 = query1.query(0L);
/* 185 */     LocalResult localResult = selectUnion.getEmptyResult();
/*     */ 
/*     */     
/* 188 */     localResult.setMaxMemoryRows(2147483647);
/* 189 */     while (resultInterface2.next()) {
/* 190 */       Value[] arrayOfValue = resultInterface2.currentRow();
/* 191 */       localResult.addRow(arrayOfValue);
/*     */     } 
/* 193 */     Query query2 = selectUnion.getRight();
/* 194 */     query2.setNeverLazy(true);
/* 195 */     resultInterface2.reset();
/* 196 */     this.view.setRecursiveResult(resultInterface2);
/*     */     
/* 198 */     query2.disableCache();
/*     */     while (true) {
/* 200 */       resultInterface2 = query2.query(0L);
/* 201 */       if (!resultInterface2.hasNext()) {
/*     */         break;
/*     */       }
/* 204 */       while (resultInterface2.next()) {
/* 205 */         Value[] arrayOfValue = resultInterface2.currentRow();
/* 206 */         localResult.addRow(arrayOfValue);
/*     */       } 
/* 208 */       resultInterface2.reset();
/* 209 */       this.view.setRecursiveResult(resultInterface2);
/*     */     } 
/* 211 */     this.view.setRecursiveResult(null);
/* 212 */     localResult.done();
/* 213 */     return new ViewCursor(this, (ResultInterface)localResult, paramSearchRow1, paramSearchRow2);
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
/*     */   public void setupQueryParameters(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2, SearchRow paramSearchRow3) {
/*     */     byte b1;
/* 226 */     ArrayList<Parameter> arrayList = this.query.getParameters();
/* 227 */     if (this.originalParameters != null) {
/* 228 */       for (Parameter parameter : this.originalParameters) {
/* 229 */         if (parameter != null) {
/* 230 */           int j = parameter.getIndex();
/* 231 */           Value value = parameter.getValue(paramSessionLocal);
/* 232 */           setParameter(arrayList, j, value);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 237 */     if (paramSearchRow1 != null) {
/* 238 */       b1 = paramSearchRow1.getColumnCount();
/* 239 */     } else if (paramSearchRow2 != null) {
/* 240 */       b1 = paramSearchRow2.getColumnCount();
/* 241 */     } else if (paramSearchRow3 != null) {
/* 242 */       b1 = paramSearchRow3.getColumnCount();
/*     */     } else {
/* 244 */       b1 = 0;
/*     */     } 
/* 246 */     int i = this.view.getParameterOffset(this.originalParameters);
/* 247 */     for (byte b2 = 0; b2 < b1; b2++) {
/* 248 */       int j = this.indexMasks[b2];
/* 249 */       if ((j & 0x1) != 0) {
/* 250 */         setParameter(arrayList, i++, paramSearchRow1.getValue(b2));
/*     */       }
/* 252 */       if ((j & 0x2) != 0) {
/* 253 */         setParameter(arrayList, i++, paramSearchRow1.getValue(b2));
/*     */       }
/* 255 */       if ((j & 0x4) != 0) {
/* 256 */         setParameter(arrayList, i++, paramSearchRow2.getValue(b2));
/*     */       }
/* 258 */       if ((j & 0x10) != 0) {
/* 259 */         setParameter(arrayList, i++, paramSearchRow3.getValue(b2));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2, SearchRow paramSearchRow3) {
/* 266 */     if (this.recursive) {
/* 267 */       return findRecursive(paramSearchRow1, paramSearchRow2);
/*     */     }
/* 269 */     setupQueryParameters(paramSessionLocal, paramSearchRow1, paramSearchRow2, paramSearchRow3);
/* 270 */     ResultInterface resultInterface = this.query.query(0L);
/* 271 */     return new ViewCursor(this, resultInterface, paramSearchRow1, paramSearchRow2);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setParameter(ArrayList<Parameter> paramArrayList, int paramInt, Value paramValue) {
/* 276 */     if (paramInt >= paramArrayList.size()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 281 */     Parameter parameter = paramArrayList.get(paramInt);
/* 282 */     parameter.setValue(paramValue);
/*     */   }
/*     */   
/*     */   public Query getQuery() {
/* 286 */     return this.query;
/*     */   }
/*     */   
/*     */   private Query getQuery(SessionLocal paramSessionLocal, int[] paramArrayOfint) {
/* 290 */     Query query = (Query)paramSessionLocal.prepare(this.querySQL, true, true);
/* 291 */     if (paramArrayOfint == null) {
/* 292 */       return query;
/*     */     }
/* 294 */     if (!query.allowGlobalConditions()) {
/* 295 */       return query;
/*     */     }
/* 297 */     int i = this.view.getParameterOffset(this.originalParameters);
/*     */ 
/*     */ 
/*     */     
/* 301 */     IntArray intArray = new IntArray();
/* 302 */     byte b1 = 0; int j;
/* 303 */     for (j = 0; j < paramArrayOfint.length; j++) {
/* 304 */       int k = paramArrayOfint[j];
/* 305 */       if (k != 0) {
/*     */ 
/*     */         
/* 308 */         b1++;
/*     */ 
/*     */ 
/*     */         
/* 312 */         int m = Integer.bitCount(k);
/* 313 */         for (byte b = 0; b < m; b++)
/* 314 */           intArray.add(j); 
/*     */       } 
/*     */     } 
/* 317 */     j = intArray.size();
/* 318 */     ArrayList<Column> arrayList = new ArrayList(j); byte b2;
/* 319 */     for (b2 = 0; b2 < j; ) {
/* 320 */       int k = intArray.get(b2);
/* 321 */       arrayList.add(this.table.getColumn(k));
/* 322 */       int m = paramArrayOfint[k];
/* 323 */       if ((m & 0x1) != 0) {
/* 324 */         Parameter parameter = new Parameter(i + b2);
/* 325 */         query.addGlobalCondition(parameter, k, 6);
/* 326 */         b2++;
/*     */       } 
/* 328 */       if ((m & 0x2) != 0) {
/* 329 */         Parameter parameter = new Parameter(i + b2);
/* 330 */         query.addGlobalCondition(parameter, k, 5);
/* 331 */         b2++;
/*     */       } 
/* 333 */       if ((m & 0x4) != 0) {
/* 334 */         Parameter parameter = new Parameter(i + b2);
/* 335 */         query.addGlobalCondition(parameter, k, 4);
/* 336 */         b2++;
/*     */       } 
/* 338 */       if ((m & 0x10) != 0) {
/* 339 */         Parameter parameter = new Parameter(i + b2);
/* 340 */         query.addGlobalCondition(parameter, k, 8);
/* 341 */         b2++;
/*     */       } 
/*     */     } 
/* 344 */     this.columns = arrayList.<Column>toArray(new Column[0]);
/*     */ 
/*     */     
/* 347 */     this.indexColumns = new IndexColumn[b1];
/* 348 */     this.columnIds = new int[b1]; byte b3;
/* 349 */     for (b2 = 0, b3 = 0; b2 < 2; b2++) {
/* 350 */       for (byte b = 0; b < paramArrayOfint.length; b++) {
/* 351 */         int k = paramArrayOfint[b];
/* 352 */         if (k != 0)
/*     */         {
/*     */           
/* 355 */           if ((b2 == 0) ? ((
/* 356 */             k & 0x1) == 0) : ((
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 361 */             k & 0x1) != 0)) {
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 366 */             Column column = this.table.getColumn(b);
/* 367 */             this.indexColumns[b3] = new IndexColumn(column);
/* 368 */             this.columnIds[b3] = column.getColumnId();
/* 369 */             b3++;
/*     */           }  } 
/*     */       } 
/*     */     } 
/* 373 */     String str = query.getPlanSQL(0);
/* 374 */     query = (Query)paramSessionLocal.prepare(str, true, true);
/* 375 */     return query;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {
/* 380 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {
/* 385 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkRename() {
/* 390 */     throw DbException.getUnsupportedException("VIEW");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/* 395 */     return false;
/*     */   }
/*     */   
/*     */   public void setRecursive(boolean paramBoolean) {
/* 399 */     this.recursive = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 404 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 409 */     return 0L;
/*     */   }
/*     */   
/*     */   public boolean isRecursive() {
/* 413 */     return this.recursive;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\ViewIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */