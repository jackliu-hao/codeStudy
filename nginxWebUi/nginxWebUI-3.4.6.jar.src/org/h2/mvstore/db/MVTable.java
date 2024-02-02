/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.h2.command.ddl.CreateTableData;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.constraint.ConstraintReferential;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.mvstore.MVStoreException;
/*     */ import org.h2.mvstore.tx.Transaction;
/*     */ import org.h2.mvstore.tx.TransactionStore;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableBase;
/*     */ import org.h2.table.TableType;
/*     */ import org.h2.util.DebuggingThreadLocal;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.DataType;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MVTable
/*     */   extends TableBase
/*     */ {
/*     */   public static final DebuggingThreadLocal<String> WAITING_FOR_LOCK;
/*     */   public static final DebuggingThreadLocal<ArrayList<String>> EXCLUSIVE_LOCKS;
/*     */   public static final DebuggingThreadLocal<ArrayList<String>> SHARED_LOCKS;
/*     */   private static final String NO_EXTRA_INFO = "";
/*     */   private final boolean containsLargeObject;
/*     */   private volatile SessionLocal lockExclusiveSession;
/*     */   
/*     */   private enum TraceLockEvent
/*     */   {
/*  71 */     TRACE_LOCK_OK("ok"),
/*  72 */     TRACE_LOCK_WAITING_FOR("waiting for"),
/*  73 */     TRACE_LOCK_REQUESTING_FOR("requesting for"),
/*  74 */     TRACE_LOCK_TIMEOUT_AFTER("timeout after "),
/*  75 */     TRACE_LOCK_UNLOCK("unlock"),
/*  76 */     TRACE_LOCK_ADDED_FOR("added for"),
/*  77 */     TRACE_LOCK_ADD_UPGRADED_FOR("add (upgraded) for ");
/*     */     
/*     */     private final String eventText;
/*     */     
/*     */     TraceLockEvent(String param1String1) {
/*  82 */       this.eventText = param1String1;
/*     */     }
/*     */     
/*     */     public String getEventText() {
/*  86 */       return this.eventText;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/*  92 */     if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/*  93 */       WAITING_FOR_LOCK = new DebuggingThreadLocal();
/*  94 */       EXCLUSIVE_LOCKS = new DebuggingThreadLocal();
/*  95 */       SHARED_LOCKS = new DebuggingThreadLocal();
/*     */     } else {
/*  97 */       WAITING_FOR_LOCK = null;
/*  98 */       EXCLUSIVE_LOCKS = null;
/*  99 */       SHARED_LOCKS = null;
/*     */     } 
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
/* 118 */   private final ConcurrentHashMap<SessionLocal, SessionLocal> lockSharedSessions = new ConcurrentHashMap<>();
/*     */   
/*     */   private Column rowIdColumn;
/*     */   
/*     */   private final MVPrimaryIndex primaryIndex;
/* 123 */   private final ArrayList<Index> indexes = Utils.newSmallArrayList();
/* 124 */   private final AtomicLong lastModificationId = new AtomicLong();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 130 */   private final ArrayDeque<SessionLocal> waitingSessions = new ArrayDeque<>();
/*     */   
/*     */   private final Trace traceLock;
/*     */   private final AtomicInteger changesUntilAnalyze;
/*     */   private int nextAnalyze;
/*     */   private final Store store;
/*     */   private final TransactionStore transactionStore;
/*     */   
/*     */   public MVTable(CreateTableData paramCreateTableData, Store paramStore) {
/* 139 */     super(paramCreateTableData);
/* 140 */     this.isHidden = paramCreateTableData.isHidden;
/* 141 */     boolean bool = false;
/* 142 */     for (Column column : getColumns()) {
/* 143 */       if (DataType.isLargeObject(column.getType().getValueType())) {
/* 144 */         bool = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 148 */     this.containsLargeObject = bool;
/* 149 */     this.nextAnalyze = (this.database.getSettings()).analyzeAuto;
/* 150 */     this.changesUntilAnalyze = (this.nextAnalyze <= 0) ? null : new AtomicInteger(this.nextAnalyze);
/* 151 */     this.store = paramStore;
/* 152 */     this.transactionStore = paramStore.getTransactionStore();
/* 153 */     this.traceLock = this.database.getTrace(7);
/*     */     
/* 155 */     this
/* 156 */       .primaryIndex = new MVPrimaryIndex(this.database, this, getId(), IndexColumn.wrap(getColumns()), IndexType.createScan(true));
/* 157 */     this.indexes.add(this.primaryIndex);
/*     */   }
/*     */   
/*     */   public String getMapName() {
/* 161 */     return this.primaryIndex.getMapName();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean lock(SessionLocal paramSessionLocal, int paramInt) {
/* 166 */     if (this.database.getLockMode() == 0) {
/* 167 */       paramSessionLocal.registerTableAsUpdated((Table)this);
/* 168 */       return false;
/*     */     } 
/* 170 */     if (paramInt == 0 && this.lockExclusiveSession == null) {
/* 171 */       return false;
/*     */     }
/* 173 */     if (this.lockExclusiveSession == paramSessionLocal) {
/* 174 */       return true;
/*     */     }
/* 176 */     if (paramInt != 2 && this.lockSharedSessions.containsKey(paramSessionLocal)) {
/* 177 */       return true;
/*     */     }
/* 179 */     synchronized (this) {
/* 180 */       if (paramInt != 2 && this.lockSharedSessions.containsKey(paramSessionLocal)) {
/* 181 */         return true;
/*     */       }
/* 183 */       paramSessionLocal.setWaitForLock((Table)this, Thread.currentThread());
/* 184 */       if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/* 185 */         WAITING_FOR_LOCK.set(getName());
/*     */       }
/* 187 */       this.waitingSessions.addLast(paramSessionLocal);
/*     */       try {
/* 189 */         doLock1(paramSessionLocal, paramInt);
/*     */       } finally {
/* 191 */         paramSessionLocal.setWaitForLock(null, null);
/* 192 */         if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/* 193 */           WAITING_FOR_LOCK.remove();
/*     */         }
/* 195 */         this.waitingSessions.remove(paramSessionLocal);
/*     */       } 
/*     */     } 
/* 198 */     return false;
/*     */   }
/*     */   
/*     */   private void doLock1(SessionLocal paramSessionLocal, int paramInt) {
/* 202 */     traceLock(paramSessionLocal, paramInt, TraceLockEvent.TRACE_LOCK_REQUESTING_FOR, "");
/*     */     
/* 204 */     long l = 0L;
/* 205 */     boolean bool = false;
/*     */     
/*     */     while (true) {
/* 208 */       if (this.waitingSessions.getFirst() == paramSessionLocal && this.lockExclusiveSession == null && 
/* 209 */         doLock2(paramSessionLocal, paramInt)) {
/*     */         return;
/*     */       }
/*     */       
/* 213 */       if (bool) {
/* 214 */         ArrayList<SessionLocal> arrayList = checkDeadlock(paramSessionLocal, (SessionLocal)null, (Set<SessionLocal>)null);
/* 215 */         if (arrayList != null) {
/* 216 */           throw DbException.get(40001, 
/* 217 */               getDeadlockDetails(arrayList, paramInt));
/*     */         }
/*     */       } else {
/*     */         
/* 221 */         bool = true;
/*     */       } 
/* 223 */       long l1 = System.nanoTime();
/* 224 */       if (l == 0L) {
/*     */         
/* 226 */         l = Utils.nanoTimePlusMillis(l1, paramSessionLocal.getLockTimeout());
/* 227 */       } else if (l1 - l >= 0L) {
/* 228 */         traceLock(paramSessionLocal, paramInt, TraceLockEvent.TRACE_LOCK_TIMEOUT_AFTER, 
/* 229 */             Integer.toString(paramSessionLocal.getLockTimeout()));
/* 230 */         throw DbException.get(50200, getName());
/*     */       } 
/*     */       try {
/* 233 */         traceLock(paramSessionLocal, paramInt, TraceLockEvent.TRACE_LOCK_WAITING_FOR, "");
/*     */         
/* 235 */         long l2 = Math.min(100L, (l - l1) / 1000000L);
/* 236 */         if (l2 == 0L) {
/* 237 */           l2 = 1L;
/*     */         }
/* 239 */         wait(l2);
/* 240 */       } catch (InterruptedException interruptedException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean doLock2(SessionLocal paramSessionLocal, int paramInt) {
/*     */     int i;
/* 247 */     switch (paramInt) {
/*     */       case 2:
/* 249 */         i = this.lockSharedSessions.size();
/* 250 */         if (i == 0) {
/* 251 */           traceLock(paramSessionLocal, paramInt, TraceLockEvent.TRACE_LOCK_ADDED_FOR, "");
/* 252 */           paramSessionLocal.registerTableAsLocked((Table)this);
/* 253 */         } else if (i == 1 && this.lockSharedSessions.containsKey(paramSessionLocal)) {
/* 254 */           traceLock(paramSessionLocal, paramInt, TraceLockEvent.TRACE_LOCK_ADD_UPGRADED_FOR, "");
/*     */         } else {
/* 256 */           return false;
/*     */         } 
/* 258 */         this.lockExclusiveSession = paramSessionLocal;
/* 259 */         if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/* 260 */           addLockToDebugList(EXCLUSIVE_LOCKS);
/*     */         }
/*     */         break;
/*     */       case 1:
/* 264 */         if (this.lockSharedSessions.putIfAbsent(paramSessionLocal, paramSessionLocal) == null) {
/* 265 */           traceLock(paramSessionLocal, paramInt, TraceLockEvent.TRACE_LOCK_OK, "");
/* 266 */           paramSessionLocal.registerTableAsLocked((Table)this);
/* 267 */           if (SysProperties.THREAD_DEADLOCK_DETECTOR)
/* 268 */             addLockToDebugList(SHARED_LOCKS); 
/*     */         } 
/*     */         break;
/*     */     } 
/* 272 */     return true;
/*     */   }
/*     */   
/*     */   private void addLockToDebugList(DebuggingThreadLocal<ArrayList<String>> paramDebuggingThreadLocal) {
/* 276 */     ArrayList<String> arrayList = (ArrayList)paramDebuggingThreadLocal.get();
/* 277 */     if (arrayList == null) {
/* 278 */       arrayList = new ArrayList();
/* 279 */       paramDebuggingThreadLocal.set(arrayList);
/*     */     } 
/* 281 */     arrayList.add(getName());
/*     */   }
/*     */   
/*     */   private void traceLock(SessionLocal paramSessionLocal, int paramInt, TraceLockEvent paramTraceLockEvent, String paramString) {
/* 285 */     if (this.traceLock.isDebugEnabled()) {
/* 286 */       this.traceLock.debug("{0} {1} {2} {3} {4}", new Object[] { Integer.valueOf(paramSessionLocal.getId()), 
/* 287 */             lockTypeToString(paramInt), paramTraceLockEvent.getEventText(), 
/* 288 */             getName(), paramString });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void unlock(SessionLocal paramSessionLocal) {
/* 294 */     if (this.database != null) {
/*     */       boolean bool;
/* 296 */       if (this.lockExclusiveSession == paramSessionLocal) {
/* 297 */         bool = true;
/* 298 */         this.lockSharedSessions.remove(paramSessionLocal);
/* 299 */         this.lockExclusiveSession = null;
/* 300 */         if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/* 301 */           ArrayList arrayList = (ArrayList)EXCLUSIVE_LOCKS.get();
/* 302 */           if (arrayList != null) {
/* 303 */             arrayList.remove(getName());
/*     */           }
/*     */         } 
/*     */       } else {
/* 307 */         bool = (this.lockSharedSessions.remove(paramSessionLocal) != null) ? true : false;
/* 308 */         if (SysProperties.THREAD_DEADLOCK_DETECTOR) {
/* 309 */           ArrayList arrayList = (ArrayList)SHARED_LOCKS.get();
/* 310 */           if (arrayList != null) {
/* 311 */             arrayList.remove(getName());
/*     */           }
/*     */         } 
/*     */       } 
/* 315 */       traceLock(paramSessionLocal, bool, TraceLockEvent.TRACE_LOCK_UNLOCK, "");
/* 316 */       if (bool && !this.waitingSessions.isEmpty()) {
/* 317 */         synchronized (this) {
/* 318 */           notifyAll();
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public Row getRow(SessionLocal paramSessionLocal, long paramLong) {
/* 331 */     return this.primaryIndex.getRow(paramSessionLocal, paramLong);
/*     */   }
/*     */ 
/*     */   
/*     */   public Index addIndex(SessionLocal paramSessionLocal, String paramString1, int paramInt1, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType, boolean paramBoolean, String paramString2) {
/*     */     MVSecondaryIndex mVSecondaryIndex;
/* 337 */     paramArrayOfIndexColumn = prepareColumns(this.database, paramArrayOfIndexColumn, paramIndexType);
/* 338 */     boolean bool = (isTemporary() && !isGlobalTemporary()) ? true : false;
/* 339 */     if (!bool) {
/* 340 */       this.database.lockMeta(paramSessionLocal);
/*     */     }
/*     */ 
/*     */     
/* 344 */     byte b = (this.primaryIndex.getMainIndexColumn() != -1) ? -1 : getMainIndexColumn(paramIndexType, paramArrayOfIndexColumn);
/* 345 */     if (this.database.isStarting()) {
/*     */       
/* 347 */       if (this.transactionStore.hasMap("index." + paramInt1))
/*     */       {
/* 349 */         b = -1;
/*     */       }
/* 351 */     } else if (this.primaryIndex.getRowCountMax() != 0L) {
/* 352 */       b = -1;
/*     */     } 
/*     */     
/* 355 */     if (b != -1) {
/* 356 */       this.primaryIndex.setMainIndexColumn(b);
/* 357 */       MVDelegateIndex mVDelegateIndex = new MVDelegateIndex(this, paramInt1, paramString1, this.primaryIndex, paramIndexType);
/*     */     }
/* 359 */     else if (paramIndexType.isSpatial()) {
/* 360 */       MVSpatialIndex mVSpatialIndex = new MVSpatialIndex(paramSessionLocal.getDatabase(), this, paramInt1, paramString1, paramArrayOfIndexColumn, paramInt2, paramIndexType);
/*     */     } else {
/*     */       
/* 363 */       mVSecondaryIndex = new MVSecondaryIndex(paramSessionLocal.getDatabase(), this, paramInt1, paramString1, paramArrayOfIndexColumn, paramInt2, paramIndexType);
/*     */     } 
/*     */     
/* 366 */     if (mVSecondaryIndex.needRebuild()) {
/* 367 */       rebuildIndex(paramSessionLocal, mVSecondaryIndex, paramString1);
/*     */     }
/* 369 */     mVSecondaryIndex.setTemporary(isTemporary());
/* 370 */     if (mVSecondaryIndex.getCreateSQL() != null) {
/* 371 */       mVSecondaryIndex.setComment(paramString2);
/* 372 */       if (bool) {
/* 373 */         paramSessionLocal.addLocalTempTableIndex(mVSecondaryIndex);
/*     */       } else {
/* 375 */         this.database.addSchemaObject(paramSessionLocal, (SchemaObject)mVSecondaryIndex);
/*     */       } 
/*     */     } 
/* 378 */     this.indexes.add(mVSecondaryIndex);
/* 379 */     setModified();
/* 380 */     return mVSecondaryIndex;
/*     */   }
/*     */   
/*     */   private void rebuildIndex(SessionLocal paramSessionLocal, MVIndex<?, ?> paramMVIndex, String paramString) {
/*     */     try {
/* 385 */       if (!paramSessionLocal.getDatabase().isPersistent() || paramMVIndex instanceof MVSpatialIndex) {
/*     */         
/* 387 */         rebuildIndexBuffered(paramSessionLocal, paramMVIndex);
/*     */       } else {
/* 389 */         rebuildIndexBlockMerge(paramSessionLocal, paramMVIndex);
/*     */       } 
/* 391 */     } catch (DbException dbException) {
/* 392 */       getSchema().freeUniqueName(paramString);
/*     */       try {
/* 394 */         paramMVIndex.remove(paramSessionLocal);
/* 395 */       } catch (DbException dbException1) {
/*     */ 
/*     */ 
/*     */         
/* 399 */         this.trace.error((Throwable)dbException1, "could not remove index");
/* 400 */         throw dbException1;
/*     */       } 
/* 402 */       throw dbException;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void rebuildIndexBlockMerge(SessionLocal paramSessionLocal, MVIndex<?, ?> paramMVIndex) {
/* 414 */     Index index = getScanIndex(paramSessionLocal);
/* 415 */     long l1 = index.getRowCount(paramSessionLocal);
/* 416 */     long l2 = l1;
/* 417 */     Cursor cursor = index.find(paramSessionLocal, null, null);
/* 418 */     long l3 = 0L;
/* 419 */     Store store = paramSessionLocal.getDatabase().getStore();
/*     */     
/* 421 */     int i = this.database.getMaxMemoryRows() / 2;
/* 422 */     ArrayList<Row> arrayList = new ArrayList(i);
/* 423 */     String str = getName() + ':' + paramMVIndex.getName();
/* 424 */     ArrayList<String> arrayList1 = Utils.newSmallArrayList();
/* 425 */     while (cursor.next()) {
/* 426 */       Row row = cursor.get();
/* 427 */       arrayList.add(row);
/* 428 */       this.database.setProgress(1, str, l3++, l2);
/* 429 */       if (arrayList.size() >= i) {
/* 430 */         sortRows((ArrayList)arrayList, paramMVIndex);
/* 431 */         String str1 = store.nextTemporaryMapName();
/* 432 */         paramMVIndex.addRowsToBuffer(arrayList, str1);
/* 433 */         arrayList1.add(str1);
/* 434 */         arrayList.clear();
/*     */       } 
/* 436 */       l1--;
/*     */     } 
/* 438 */     sortRows((ArrayList)arrayList, paramMVIndex);
/* 439 */     if (!arrayList1.isEmpty()) {
/* 440 */       String str1 = store.nextTemporaryMapName();
/* 441 */       paramMVIndex.addRowsToBuffer(arrayList, str1);
/* 442 */       arrayList1.add(str1);
/* 443 */       arrayList.clear();
/* 444 */       paramMVIndex.addBufferedRows(arrayList1);
/*     */     } else {
/* 446 */       addRowsToIndex(paramSessionLocal, arrayList, paramMVIndex);
/*     */     } 
/* 448 */     if (l1 != 0L) {
/* 449 */       throw DbException.getInternalError("rowcount remaining=" + l1 + ' ' + getName());
/*     */     }
/*     */   }
/*     */   
/*     */   private void rebuildIndexBuffered(SessionLocal paramSessionLocal, Index paramIndex) {
/* 454 */     Index index = getScanIndex(paramSessionLocal);
/* 455 */     long l1 = index.getRowCount(paramSessionLocal);
/* 456 */     long l2 = l1;
/* 457 */     Cursor cursor = index.find(paramSessionLocal, null, null);
/* 458 */     long l3 = 0L;
/* 459 */     int i = (int)Math.min(l2, this.database.getMaxMemoryRows());
/* 460 */     ArrayList<Row> arrayList = new ArrayList(i);
/* 461 */     String str = getName() + ':' + paramIndex.getName();
/* 462 */     while (cursor.next()) {
/* 463 */       Row row = cursor.get();
/* 464 */       arrayList.add(row);
/* 465 */       this.database.setProgress(1, str, l3++, l2);
/* 466 */       if (arrayList.size() >= i) {
/* 467 */         addRowsToIndex(paramSessionLocal, arrayList, paramIndex);
/*     */       }
/* 469 */       l1--;
/*     */     } 
/* 471 */     addRowsToIndex(paramSessionLocal, arrayList, paramIndex);
/* 472 */     if (l1 != 0L) {
/* 473 */       throw DbException.getInternalError("rowcount remaining=" + l1 + ' ' + getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 479 */     syncLastModificationIdWithDatabase();
/* 480 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 481 */     long l = transaction.setSavepoint();
/*     */     try {
/* 483 */       for (int i = this.indexes.size() - 1; i >= 0; i--) {
/* 484 */         Index index = this.indexes.get(i);
/* 485 */         index.remove(paramSessionLocal, paramRow);
/*     */       } 
/* 487 */     } catch (Throwable throwable) {
/*     */       try {
/* 489 */         transaction.rollbackToSavepoint(l);
/* 490 */       } catch (Throwable throwable1) {
/* 491 */         throwable.addSuppressed(throwable1);
/*     */       } 
/* 493 */       throw DbException.convert(throwable);
/*     */     } 
/* 495 */     analyzeIfRequired(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public long truncate(SessionLocal paramSessionLocal) {
/* 500 */     syncLastModificationIdWithDatabase();
/* 501 */     long l = getRowCountApproximation(paramSessionLocal);
/* 502 */     for (int i = this.indexes.size() - 1; i >= 0; i--) {
/* 503 */       Index index = this.indexes.get(i);
/* 504 */       index.truncate(paramSessionLocal);
/*     */     } 
/* 506 */     if (this.changesUntilAnalyze != null) {
/* 507 */       this.changesUntilAnalyze.set(this.nextAnalyze);
/*     */     }
/* 509 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 514 */     syncLastModificationIdWithDatabase();
/* 515 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 516 */     long l = transaction.setSavepoint();
/*     */     try {
/* 518 */       for (Index index : this.indexes) {
/* 519 */         index.add(paramSessionLocal, paramRow);
/*     */       }
/* 521 */     } catch (Throwable throwable) {
/*     */       try {
/* 523 */         transaction.rollbackToSavepoint(l);
/* 524 */       } catch (Throwable throwable1) {
/* 525 */         throwable.addSuppressed(throwable1);
/*     */       } 
/* 527 */       throw DbException.convert(throwable);
/*     */     } 
/* 529 */     analyzeIfRequired(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateRow(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 534 */     paramRow2.setKey(paramRow1.getKey());
/* 535 */     syncLastModificationIdWithDatabase();
/* 536 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 537 */     long l = transaction.setSavepoint();
/*     */     try {
/* 539 */       for (Index index : this.indexes) {
/* 540 */         index.update(paramSessionLocal, paramRow1, paramRow2);
/*     */       }
/* 542 */     } catch (Throwable throwable) {
/*     */       try {
/* 544 */         transaction.rollbackToSavepoint(l);
/* 545 */       } catch (Throwable throwable1) {
/* 546 */         throwable.addSuppressed(throwable1);
/*     */       } 
/* 548 */       throw DbException.convert(throwable);
/*     */     } 
/* 550 */     analyzeIfRequired(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public Row lockRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 555 */     Row row = this.primaryIndex.lockRow(paramSessionLocal, paramRow);
/* 556 */     if (row == null || !paramRow.hasSharedData(row)) {
/* 557 */       syncLastModificationIdWithDatabase();
/*     */     }
/* 559 */     return row;
/*     */   }
/*     */   
/*     */   private void analyzeIfRequired(SessionLocal paramSessionLocal) {
/* 563 */     if (this.changesUntilAnalyze != null && 
/* 564 */       this.changesUntilAnalyze.decrementAndGet() == 0) {
/* 565 */       if (this.nextAnalyze <= 1073741823) {
/* 566 */         this.nextAnalyze *= 2;
/*     */       }
/* 568 */       this.changesUntilAnalyze.set(this.nextAnalyze);
/* 569 */       paramSessionLocal.markTableForAnalyze((Table)this);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Index getScanIndex(SessionLocal paramSessionLocal) {
/* 576 */     return this.primaryIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<Index> getIndexes() {
/* 581 */     return this.indexes;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxDataModificationId() {
/* 586 */     return this.lastModificationId.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/* 591 */     if (this.containsLargeObject) {
/*     */       
/* 593 */       truncate(paramSessionLocal);
/* 594 */       this.database.getLobStorage().removeAllForTable(getId());
/* 595 */       this.database.lockMeta(paramSessionLocal);
/*     */     } 
/* 597 */     this.database.getStore().removeTable(this);
/* 598 */     super.removeChildrenAndResources(paramSessionLocal);
/*     */     
/* 600 */     while (this.indexes.size() > 1) {
/* 601 */       Index index = this.indexes.get(1);
/* 602 */       index.remove(paramSessionLocal);
/* 603 */       if (index.getName() != null) {
/* 604 */         this.database.removeSchemaObject(paramSessionLocal, (SchemaObject)index);
/*     */       }
/*     */       
/* 607 */       this.indexes.remove(index);
/*     */     } 
/* 609 */     this.primaryIndex.remove(paramSessionLocal);
/* 610 */     this.indexes.clear();
/* 611 */     close(paramSessionLocal);
/* 612 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 617 */     return this.primaryIndex.getRowCount(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 622 */     return this.primaryIndex.getRowCountApproximation(paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getDiskSpaceUsed() {
/* 627 */     return this.primaryIndex.getDiskSpaceUsed();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Transaction getTransactionBegin() {
/* 637 */     return this.transactionStore.begin();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRowLockable() {
/* 642 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void commit() {
/* 650 */     if (this.database != null) {
/* 651 */       syncLastModificationIdWithDatabase();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void syncLastModificationIdWithDatabase() {
/* 663 */     long l2, l1 = this.database.getNextModificationDataId();
/*     */     
/*     */     do {
/* 666 */       l2 = this.lastModificationId.get();
/* 667 */     } while (l1 > l2 && 
/* 668 */       !this.lastModificationId.compareAndSet(l2, l1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DbException convertException(MVStoreException paramMVStoreException) {
/* 678 */     int i = paramMVStoreException.getErrorCode();
/* 679 */     if (i == 101)
/* 680 */       throw DbException.get(90131, paramMVStoreException, new String[] {
/* 681 */             getName()
/*     */           }); 
/* 683 */     if (i == 105)
/* 684 */       throw DbException.get(40001, paramMVStoreException, new String[] {
/* 685 */             getName()
/*     */           }); 
/* 687 */     return this.store.convertMVStoreException(paramMVStoreException);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMainIndexColumn() {
/* 692 */     return this.primaryIndex.getMainIndexColumn();
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
/*     */   private static void addRowsToIndex(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, Index paramIndex) {
/* 707 */     sortRows((ArrayList)paramArrayList, paramIndex);
/* 708 */     for (Row row : paramArrayList) {
/* 709 */       paramIndex.add(paramSessionLocal, row);
/*     */     }
/* 711 */     paramArrayList.clear();
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
/*     */   private static String getDeadlockDetails(ArrayList<SessionLocal> paramArrayList, int paramInt) {
/* 726 */     StringBuilder stringBuilder = new StringBuilder();
/* 727 */     for (SessionLocal sessionLocal : paramArrayList) {
/* 728 */       Table table = sessionLocal.getWaitForLock();
/* 729 */       Thread thread = sessionLocal.getWaitForLockThread();
/* 730 */       stringBuilder.append("\nSession ").append(sessionLocal).append(" on thread ").append(thread.getName())
/* 731 */         .append(" is waiting to lock ").append(table.toString())
/* 732 */         .append(" (").append(lockTypeToString(paramInt)).append(") while locking ");
/* 733 */       boolean bool = false;
/* 734 */       for (Table table1 : sessionLocal.getLocks()) {
/* 735 */         if (bool) {
/* 736 */           stringBuilder.append(", ");
/*     */         }
/* 738 */         bool = true;
/* 739 */         stringBuilder.append(table1.toString());
/* 740 */         if (table1 instanceof MVTable) {
/* 741 */           if (((MVTable)table1).lockExclusiveSession == sessionLocal) {
/* 742 */             stringBuilder.append(" (exclusive)"); continue;
/*     */           } 
/* 744 */           stringBuilder.append(" (shared)");
/*     */         } 
/*     */       } 
/*     */       
/* 748 */       stringBuilder.append('.');
/*     */     } 
/* 750 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   private static String lockTypeToString(int paramInt) {
/* 754 */     return (paramInt == 0) ? "shared read" : ((paramInt == 1) ? "shared write" : "exclusive");
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
/*     */   private static void sortRows(ArrayList<? extends SearchRow> paramArrayList, Index paramIndex) {
/* 767 */     paramArrayList.sort(paramIndex::compareRows);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canDrop() {
/* 772 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 777 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canTruncate() {
/* 782 */     if (getCheckForeignKeyConstraints() && this.database.getReferentialIntegrity()) {
/* 783 */       ArrayList arrayList = getConstraints();
/* 784 */       if (arrayList != null) {
/* 785 */         for (Constraint constraint : arrayList) {
/* 786 */           if (constraint.getConstraintType() != Constraint.Type.REFERENTIAL) {
/*     */             continue;
/*     */           }
/* 789 */           ConstraintReferential constraintReferential = (ConstraintReferential)constraint;
/* 790 */           if (constraintReferential.getRefTable() == this) {
/* 791 */             return false;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 796 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<SessionLocal> checkDeadlock(SessionLocal paramSessionLocal1, SessionLocal paramSessionLocal2, Set<SessionLocal> paramSet) {
/* 802 */     synchronized (getClass()) {
/* 803 */       if (paramSessionLocal2 == null)
/*     */       
/* 805 */       { paramSessionLocal2 = paramSessionLocal1;
/* 806 */         paramSet = new HashSet<>(); }
/* 807 */       else { if (paramSessionLocal2 == paramSessionLocal1)
/*     */         {
/* 809 */           return new ArrayList<>(0); } 
/* 810 */         if (paramSet.contains(paramSessionLocal1))
/*     */         {
/*     */ 
/*     */           
/* 814 */           return null; }  }
/*     */       
/* 816 */       paramSet.add(paramSessionLocal1);
/* 817 */       ArrayList<SessionLocal> arrayList = null;
/* 818 */       for (SessionLocal sessionLocal1 : this.lockSharedSessions.keySet()) {
/* 819 */         if (sessionLocal1 == paramSessionLocal1) {
/*     */           continue;
/*     */         }
/*     */         
/* 823 */         Table table = sessionLocal1.getWaitForLock();
/* 824 */         if (table != null) {
/* 825 */           arrayList = table.checkDeadlock(sessionLocal1, paramSessionLocal2, paramSet);
/* 826 */           if (arrayList != null) {
/* 827 */             arrayList.add(paramSessionLocal1);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 834 */       SessionLocal sessionLocal = this.lockExclusiveSession;
/* 835 */       if (arrayList == null && sessionLocal != null) {
/* 836 */         Table table = sessionLocal.getWaitForLock();
/* 837 */         if (table != null) {
/* 838 */           arrayList = table.checkDeadlock(sessionLocal, paramSessionLocal2, paramSet);
/* 839 */           if (arrayList != null) {
/* 840 */             arrayList.add(paramSessionLocal1);
/*     */           }
/*     */         } 
/*     */       } 
/* 844 */       return arrayList;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkSupportAlter() {}
/*     */ 
/*     */   
/*     */   public boolean getContainsLargeObject() {
/* 854 */     return this.containsLargeObject;
/*     */   }
/*     */ 
/*     */   
/*     */   public Column getRowIdColumn() {
/* 859 */     if (this.rowIdColumn == null) {
/* 860 */       this.rowIdColumn = new Column("_ROWID_", TypeInfo.TYPE_BIGINT, (Table)this, -1);
/* 861 */       this.rowIdColumn.setRowId(true);
/* 862 */       this.rowIdColumn.setNullable(false);
/*     */     } 
/* 864 */     return this.rowIdColumn;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableType getTableType() {
/* 869 */     return TableType.TABLE;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 874 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLockedExclusively() {
/* 879 */     return (this.lockExclusiveSession != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLockedExclusivelyBy(SessionLocal paramSessionLocal) {
/* 884 */     return (this.lockExclusiveSession == paramSessionLocal);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void invalidate() {
/* 889 */     super.invalidate();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 897 */     this.lockExclusiveSession = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 902 */     return getTraceSQL();
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
/*     */   private static IndexColumn[] prepareColumns(Database paramDatabase, IndexColumn[] paramArrayOfIndexColumn, IndexType paramIndexType) {
/* 914 */     if (paramIndexType.isPrimaryKey()) {
/* 915 */       for (IndexColumn indexColumn : paramArrayOfIndexColumn) {
/* 916 */         Column column = indexColumn.column;
/* 917 */         if (column.isNullable()) {
/* 918 */           throw DbException.get(90023, column.getName());
/*     */         }
/*     */       } 
/* 921 */       for (IndexColumn indexColumn : paramArrayOfIndexColumn) {
/* 922 */         indexColumn.column.setPrimaryKey(true);
/*     */       }
/* 924 */     } else if (!paramIndexType.isSpatial()) {
/* 925 */       byte b = 0; int i = paramArrayOfIndexColumn.length;
/* 926 */       while (b < i && ((paramArrayOfIndexColumn[b]).sortType & 0x6) != 0) {
/* 927 */         b++;
/*     */       }
/* 929 */       if (b != i) {
/* 930 */         paramArrayOfIndexColumn = (IndexColumn[])paramArrayOfIndexColumn.clone();
/* 931 */         DefaultNullOrdering defaultNullOrdering = paramDatabase.getDefaultNullOrdering();
/* 932 */         for (; b < i; b++) {
/* 933 */           IndexColumn indexColumn = paramArrayOfIndexColumn[b];
/* 934 */           int j = indexColumn.sortType;
/* 935 */           int k = defaultNullOrdering.addExplicitNullOrdering(j);
/* 936 */           if (k != j) {
/* 937 */             IndexColumn indexColumn1 = new IndexColumn(indexColumn.columnName, k);
/* 938 */             indexColumn1.column = indexColumn.column;
/* 939 */             paramArrayOfIndexColumn[b] = indexColumn1;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 944 */     return paramArrayOfIndexColumn;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */