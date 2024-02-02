/*     */ package org.h2.result;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.TreeMap;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.db.MVTempResult;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueRow;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalResult
/*     */   implements ResultInterface, ResultTarget
/*     */ {
/*     */   private int maxMemoryRows;
/*     */   private final SessionLocal session;
/*     */   private int visibleColumnCount;
/*     */   private int resultColumnCount;
/*     */   private Expression[] expressions;
/*     */   private boolean forDataChangeDeltaTable;
/*     */   private long rowId;
/*     */   private long rowCount;
/*     */   private ArrayList<Value[]> rows;
/*     */   private SortOrder sort;
/*     */   private TreeMap<ValueRow, Value[]> distinctRows;
/*     */   private Value[] currentRow;
/*     */   private long offset;
/*     */   
/*     */   public static LocalResult forTable(SessionLocal paramSessionLocal, Table paramTable) {
/*  47 */     Column[] arrayOfColumn = paramTable.getColumns();
/*  48 */     int i = arrayOfColumn.length;
/*  49 */     Expression[] arrayOfExpression = new Expression[i + 1];
/*  50 */     Database database = paramSessionLocal.getDatabase();
/*  51 */     for (byte b = 0; b < i; b++) {
/*  52 */       arrayOfExpression[b] = (Expression)new ExpressionColumn(database, arrayOfColumn[b]);
/*     */     }
/*  54 */     Column column = paramTable.getRowIdColumn();
/*  55 */     arrayOfExpression[i] = (column != null) ? (Expression)new ExpressionColumn(database, column) : (Expression)new ExpressionColumn(database, null, paramTable
/*  56 */         .getName());
/*  57 */     return new LocalResult(paramSessionLocal, arrayOfExpression, i, i + 1);
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
/*  74 */   private long limit = -1L;
/*     */   
/*     */   private boolean fetchPercent;
/*     */   
/*     */   private SortOrder withTiesSortOrder;
/*     */   
/*     */   private boolean limitsWereApplied;
/*     */   private ResultExternal external;
/*     */   private boolean distinct;
/*     */   private int[] distinctIndexes;
/*     */   private boolean closed;
/*     */   private boolean containsLobs;
/*     */   private Boolean containsNull;
/*     */   
/*     */   public LocalResult() {
/*  89 */     this(null);
/*     */   }
/*     */   
/*     */   private LocalResult(SessionLocal paramSessionLocal) {
/*  93 */     this.session = paramSessionLocal;
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
/*     */   public LocalResult(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression, int paramInt1, int paramInt2) {
/* 110 */     this.session = paramSessionLocal;
/* 111 */     if (paramSessionLocal == null) {
/* 112 */       this.maxMemoryRows = Integer.MAX_VALUE;
/*     */     } else {
/* 114 */       Database database = paramSessionLocal.getDatabase();
/* 115 */       if (database.isPersistent() && !database.isReadOnly()) {
/* 116 */         this.maxMemoryRows = paramSessionLocal.getDatabase().getMaxMemoryRows();
/*     */       } else {
/* 118 */         this.maxMemoryRows = Integer.MAX_VALUE;
/*     */       } 
/*     */     } 
/* 121 */     this.rows = Utils.newSmallArrayList();
/* 122 */     this.visibleColumnCount = paramInt1;
/* 123 */     this.resultColumnCount = paramInt2;
/* 124 */     this.rowId = -1L;
/* 125 */     this.expressions = paramArrayOfExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLazy() {
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxMemoryRows(int paramInt) {
/* 141 */     this.maxMemoryRows = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setForDataChangeDeltaTable() {
/* 148 */     this.forDataChangeDeltaTable = true;
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
/*     */   public LocalResult createShallowCopy(Session paramSession) {
/* 160 */     if (this.external == null && (this.rows == null || this.rows.size() < this.rowCount)) {
/* 161 */       return null;
/*     */     }
/* 163 */     if (this.containsLobs) {
/* 164 */       return null;
/*     */     }
/* 166 */     ResultExternal resultExternal = null;
/* 167 */     if (this.external != null) {
/* 168 */       resultExternal = this.external.createShallowCopy();
/* 169 */       if (resultExternal == null) {
/* 170 */         return null;
/*     */       }
/*     */     } 
/* 173 */     LocalResult localResult = new LocalResult((SessionLocal)paramSession);
/* 174 */     localResult.maxMemoryRows = this.maxMemoryRows;
/* 175 */     localResult.visibleColumnCount = this.visibleColumnCount;
/* 176 */     localResult.resultColumnCount = this.resultColumnCount;
/* 177 */     localResult.expressions = this.expressions;
/* 178 */     localResult.rowId = -1L;
/* 179 */     localResult.rowCount = this.rowCount;
/* 180 */     localResult.rows = this.rows;
/* 181 */     localResult.sort = this.sort;
/* 182 */     localResult.distinctRows = this.distinctRows;
/* 183 */     localResult.distinct = this.distinct;
/* 184 */     localResult.distinctIndexes = this.distinctIndexes;
/* 185 */     localResult.currentRow = null;
/* 186 */     localResult.offset = 0L;
/* 187 */     localResult.limit = -1L;
/* 188 */     localResult.external = resultExternal;
/* 189 */     localResult.containsNull = this.containsNull;
/* 190 */     return localResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSortOrder(SortOrder paramSortOrder) {
/* 200 */     this.sort = paramSortOrder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistinct() {
/* 207 */     assert this.distinctIndexes == null;
/* 208 */     this.distinct = true;
/* 209 */     this.distinctRows = (TreeMap)new TreeMap<>((Comparator<? super ValueRow>)this.session.getDatabase().getCompareMode());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDistinct(int[] paramArrayOfint) {
/* 218 */     assert !this.distinct;
/* 219 */     this.distinctIndexes = paramArrayOfint;
/* 220 */     this.distinctRows = (TreeMap)new TreeMap<>((Comparator<? super ValueRow>)this.session.getDatabase().getCompareMode());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isAnyDistinct() {
/* 227 */     return (this.distinct || this.distinctIndexes != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsDistinct(Value[] paramArrayOfValue) {
/* 237 */     assert paramArrayOfValue.length == this.visibleColumnCount;
/* 238 */     if (this.external != null) {
/* 239 */       return this.external.contains(paramArrayOfValue);
/*     */     }
/* 241 */     if (this.distinctRows == null) {
/* 242 */       this.distinctRows = (TreeMap)new TreeMap<>((Comparator<? super ValueRow>)this.session.getDatabase().getCompareMode());
/* 243 */       for (Value[] arrayOfValue : this.rows) {
/* 244 */         ValueRow valueRow1 = getDistinctRow(arrayOfValue);
/* 245 */         this.distinctRows.put(valueRow1, valueRow1.getList());
/*     */       } 
/*     */     } 
/* 248 */     ValueRow valueRow = ValueRow.get(paramArrayOfValue);
/* 249 */     return (this.distinctRows.get(valueRow) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean containsNull() {
/* 259 */     Boolean bool = this.containsNull;
/* 260 */     if (bool == null) {
/* 261 */       bool = Boolean.valueOf(false);
/* 262 */       reset();
/* 263 */       label17: while (next()) {
/* 264 */         Value[] arrayOfValue = this.currentRow;
/* 265 */         for (byte b = 0; b < this.visibleColumnCount; b++) {
/* 266 */           if (arrayOfValue[b].containsNull()) {
/* 267 */             bool = Boolean.valueOf(true);
/*     */             break label17;
/*     */           } 
/*     */         } 
/*     */       } 
/* 272 */       reset();
/* 273 */       this.containsNull = bool;
/*     */     } 
/* 275 */     return bool.booleanValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeDistinct(Value[] paramArrayOfValue) {
/* 284 */     if (!this.distinct) {
/* 285 */       throw DbException.getInternalError();
/*     */     }
/* 287 */     assert paramArrayOfValue.length == this.visibleColumnCount;
/* 288 */     if (this.distinctRows != null) {
/* 289 */       this.distinctRows.remove(ValueRow.get(paramArrayOfValue));
/* 290 */       this.rowCount = this.distinctRows.size();
/*     */     } else {
/* 292 */       this.rowCount = this.external.removeRow(paramArrayOfValue);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 298 */     this.rowId = -1L;
/* 299 */     this.currentRow = null;
/* 300 */     if (this.external != null) {
/* 301 */       this.external.reset();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Row currentRowForTable() {
/* 310 */     int i = this.visibleColumnCount;
/* 311 */     Value[] arrayOfValue = this.currentRow;
/*     */     
/* 313 */     Row row = this.session.getDatabase().getRowFactory().createRow(Arrays.<Value>copyOf(arrayOfValue, i), -1);
/* 314 */     row.setKey(arrayOfValue[i].getLong());
/* 315 */     return row;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] currentRow() {
/* 320 */     return this.currentRow;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean next() {
/* 325 */     if (!this.closed && this.rowId < this.rowCount) {
/* 326 */       this.rowId++;
/* 327 */       if (this.rowId < this.rowCount) {
/* 328 */         if (this.external != null) {
/* 329 */           this.currentRow = this.external.next();
/*     */         } else {
/* 331 */           this.currentRow = this.rows.get((int)this.rowId);
/*     */         } 
/* 333 */         return true;
/*     */       } 
/* 335 */       this.currentRow = null;
/*     */     } 
/* 337 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowId() {
/* 342 */     return this.rowId;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 347 */     return (this.rowId >= this.rowCount);
/*     */   }
/*     */   
/*     */   private void cloneLobs(Value[] paramArrayOfValue) {
/* 351 */     for (byte b = 0; b < paramArrayOfValue.length; b++) {
/* 352 */       Value value = paramArrayOfValue[b];
/* 353 */       if (value instanceof ValueLob) {
/* 354 */         if (this.forDataChangeDeltaTable) {
/* 355 */           this.containsLobs = true;
/*     */         } else {
/* 357 */           ValueLob valueLob = ((ValueLob)value).copyToResult();
/* 358 */           if (valueLob != value) {
/* 359 */             this.containsLobs = true;
/* 360 */             paramArrayOfValue[b] = (Value)this.session.addTemporaryLob(valueLob);
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private ValueRow getDistinctRow(Value[] paramArrayOfValue) {
/* 368 */     if (this.distinctIndexes != null) {
/* 369 */       int i = this.distinctIndexes.length;
/* 370 */       Value[] arrayOfValue = new Value[i];
/* 371 */       for (byte b = 0; b < i; b++) {
/* 372 */         arrayOfValue[b] = paramArrayOfValue[this.distinctIndexes[b]];
/*     */       }
/* 374 */       paramArrayOfValue = arrayOfValue;
/* 375 */     } else if (paramArrayOfValue.length > this.visibleColumnCount) {
/* 376 */       paramArrayOfValue = Arrays.<Value>copyOf(paramArrayOfValue, this.visibleColumnCount);
/*     */     } 
/* 378 */     return ValueRow.get(paramArrayOfValue);
/*     */   }
/*     */   
/*     */   private void createExternalResult() {
/* 382 */     this.external = MVTempResult.of(this.session.getDatabase(), this.expressions, this.distinct, this.distinctIndexes, this.visibleColumnCount, this.resultColumnCount, this.sort);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRowForTable(Row paramRow) {
/* 392 */     int i = this.visibleColumnCount;
/* 393 */     Value[] arrayOfValue = new Value[i + 1];
/* 394 */     for (byte b = 0; b < i; b++) {
/* 395 */       arrayOfValue[b] = paramRow.getValue(b);
/*     */     }
/* 397 */     arrayOfValue[i] = (Value)ValueBigint.get(paramRow.getKey());
/* 398 */     addRowInternal(arrayOfValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRow(Value... paramVarArgs) {
/* 408 */     assert paramVarArgs.length == this.resultColumnCount;
/* 409 */     cloneLobs(paramVarArgs);
/* 410 */     addRowInternal(paramVarArgs);
/*     */   }
/*     */   
/*     */   private void addRowInternal(Value... paramVarArgs) {
/* 414 */     if (isAnyDistinct()) {
/* 415 */       if (this.distinctRows != null) {
/* 416 */         ValueRow valueRow = getDistinctRow(paramVarArgs);
/* 417 */         Value[] arrayOfValue = this.distinctRows.get(valueRow);
/* 418 */         if (arrayOfValue == null || (this.sort != null && this.sort.compare(arrayOfValue, paramVarArgs) > 0)) {
/* 419 */           this.distinctRows.put(valueRow, paramVarArgs);
/*     */         }
/* 421 */         this.rowCount = this.distinctRows.size();
/* 422 */         if (this.rowCount > this.maxMemoryRows) {
/* 423 */           createExternalResult();
/* 424 */           this.rowCount = this.external.addRows((Collection)this.distinctRows.values());
/* 425 */           this.distinctRows = null;
/*     */         } 
/*     */       } else {
/* 428 */         this.rowCount = this.external.addRow(paramVarArgs);
/*     */       } 
/*     */     } else {
/* 431 */       this.rows.add(paramVarArgs);
/* 432 */       this.rowCount++;
/* 433 */       if (this.rows.size() > this.maxMemoryRows) {
/* 434 */         addRowsToDisk();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addRowsToDisk() {
/* 440 */     if (this.external == null) {
/* 441 */       createExternalResult();
/*     */     }
/* 443 */     this.rowCount = this.external.addRows((Collection<Value[]>)this.rows);
/* 444 */     this.rows.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVisibleColumnCount() {
/* 449 */     return this.visibleColumnCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void done() {
/* 456 */     if (this.external != null) {
/* 457 */       addRowsToDisk();
/*     */     } else {
/* 459 */       if (isAnyDistinct()) {
/* 460 */         this.rows = (ArrayList)new ArrayList<>(this.distinctRows.values());
/*     */       }
/* 462 */       if (this.sort != null && this.limit != 0L && !this.limitsWereApplied) {
/* 463 */         boolean bool = (this.limit > 0L && this.withTiesSortOrder == null) ? true : false;
/* 464 */         if (this.offset > 0L || bool) {
/* 465 */           int i = this.rows.size();
/* 466 */           if (this.offset < i) {
/* 467 */             int j = (int)this.offset;
/* 468 */             if (bool && this.limit < (i - j)) {
/* 469 */               i = j + (int)this.limit;
/*     */             }
/* 471 */             this.sort.sort(this.rows, j, i);
/*     */           } 
/*     */         } else {
/* 474 */           this.sort.sort(this.rows);
/*     */         } 
/*     */       } 
/*     */     } 
/* 478 */     applyOffsetAndLimit();
/* 479 */     reset();
/*     */   }
/*     */   
/*     */   private void applyOffsetAndLimit() {
/* 483 */     if (this.limitsWereApplied) {
/*     */       return;
/*     */     }
/* 486 */     long l1 = Math.max(this.offset, 0L);
/* 487 */     long l2 = this.limit;
/* 488 */     if ((l1 == 0L && l2 < 0L && !this.fetchPercent) || this.rowCount == 0L) {
/*     */       return;
/*     */     }
/* 491 */     if (this.fetchPercent) {
/* 492 */       if (l2 < 0L || l2 > 100L) {
/* 493 */         throw DbException.getInvalidValueException("FETCH PERCENT", Long.valueOf(l2));
/*     */       }
/*     */       
/* 496 */       l2 = (l2 * this.rowCount + 99L) / 100L;
/*     */     } 
/* 498 */     boolean bool = (l1 >= this.rowCount || l2 == 0L) ? true : false;
/* 499 */     if (!bool) {
/* 500 */       long l = this.rowCount - l1;
/* 501 */       l2 = (l2 < 0L) ? l : Math.min(l, l2);
/* 502 */       if (l1 == 0L && l <= l2) {
/*     */         return;
/*     */       }
/*     */     } else {
/* 506 */       l2 = 0L;
/*     */     } 
/* 508 */     this.distinctRows = null;
/* 509 */     this.rowCount = l2;
/* 510 */     if (this.external == null) {
/* 511 */       if (bool) {
/* 512 */         this.rows.clear();
/*     */         return;
/*     */       } 
/* 515 */       int i = (int)(l1 + l2);
/* 516 */       if (this.withTiesSortOrder != null) {
/* 517 */         Value[] arrayOfValue = this.rows.get(i - 1);
/* 518 */         while (i < this.rows.size() && this.withTiesSortOrder.compare(arrayOfValue, this.rows.get(i)) == 0) {
/* 519 */           i++;
/* 520 */           this.rowCount++;
/*     */         } 
/*     */       } 
/* 523 */       if (l1 != 0L || i != this.rows.size())
/*     */       {
/* 525 */         this.rows = (ArrayList)new ArrayList<>(this.rows.subList((int)l1, i));
/*     */       }
/*     */     } else {
/* 528 */       if (bool) {
/* 529 */         this.external.close();
/* 530 */         this.external = null;
/*     */         return;
/*     */       } 
/* 533 */       trimExternal(l1, l2);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void trimExternal(long paramLong1, long paramLong2) {
/* 538 */     ResultExternal resultExternal = this.external;
/* 539 */     this.external = null;
/* 540 */     resultExternal.reset();
/* 541 */     while (--paramLong1 >= 0L) {
/* 542 */       resultExternal.next();
/*     */     }
/* 544 */     Value[] arrayOfValue = null;
/* 545 */     while (--paramLong2 >= 0L) {
/* 546 */       arrayOfValue = resultExternal.next();
/* 547 */       this.rows.add(arrayOfValue);
/* 548 */       if (this.rows.size() > this.maxMemoryRows) {
/* 549 */         addRowsToDisk();
/*     */       }
/*     */     } 
/* 552 */     if (this.withTiesSortOrder != null && arrayOfValue != null) {
/* 553 */       Value[] arrayOfValue1 = arrayOfValue;
/* 554 */       while ((arrayOfValue = resultExternal.next()) != null && this.withTiesSortOrder.compare(arrayOfValue1, arrayOfValue) == 0) {
/* 555 */         this.rows.add(arrayOfValue);
/* 556 */         this.rowCount++;
/* 557 */         if (this.rows.size() > this.maxMemoryRows) {
/* 558 */           addRowsToDisk();
/*     */         }
/*     */       } 
/*     */     } 
/* 562 */     if (this.external != null) {
/* 563 */       addRowsToDisk();
/*     */     }
/* 565 */     resultExternal.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount() {
/* 570 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public void limitsWereApplied() {
/* 575 */     this.limitsWereApplied = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 580 */     return (!this.closed && this.rowId < this.rowCount - 1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLimit(long paramLong) {
/* 589 */     this.limit = paramLong;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFetchPercent(boolean paramBoolean) {
/* 596 */     this.fetchPercent = paramBoolean;
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
/*     */   public void setWithTies(SortOrder paramSortOrder) {
/* 608 */     assert this.sort == null || this.sort == paramSortOrder;
/* 609 */     this.withTiesSortOrder = paramSortOrder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needToClose() {
/* 614 */     return (this.external != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 619 */     if (this.external != null) {
/* 620 */       this.external.close();
/* 621 */       this.external = null;
/* 622 */       this.closed = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlias(int paramInt) {
/* 628 */     return this.expressions[paramInt].getAlias(this.session, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName(int paramInt) {
/* 633 */     return this.expressions[paramInt].getTableName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName(int paramInt) {
/* 638 */     return this.expressions[paramInt].getSchemaName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int paramInt) {
/* 643 */     return this.expressions[paramInt].getColumnName(this.session, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getColumnType(int paramInt) {
/* 648 */     return this.expressions[paramInt].getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable(int paramInt) {
/* 653 */     return this.expressions[paramInt].getNullable();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity(int paramInt) {
/* 658 */     return this.expressions[paramInt].isIdentity();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setOffset(long paramLong) {
/* 667 */     this.offset = paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 672 */     return super.toString() + " columns: " + this.visibleColumnCount + " rows: " + this.rowCount + " pos: " + this.rowId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 683 */     return this.closed;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/* 688 */     return 0;
/*     */   }
/*     */   
/*     */   public void setFetchSize(int paramInt) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\LocalResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */