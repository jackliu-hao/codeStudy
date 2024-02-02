/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.index.SingleRowCursor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStoreException;
/*     */ import org.h2.mvstore.tx.Transaction;
/*     */ import org.h2.mvstore.tx.TransactionMap;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.LongDataType;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.VersionedValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MVPrimaryIndex
/*     */   extends MVIndex<Long, SearchRow>
/*     */ {
/*     */   private final MVTable mvTable;
/*     */   private final String mapName;
/*     */   private final TransactionMap<Long, SearchRow> dataMap;
/*  44 */   private final AtomicLong lastKey = new AtomicLong();
/*  45 */   private int mainIndexColumn = -1;
/*     */   
/*     */   public MVPrimaryIndex(Database paramDatabase, MVTable paramMVTable, int paramInt, IndexColumn[] paramArrayOfIndexColumn, IndexType paramIndexType) {
/*  48 */     super((Table)paramMVTable, paramInt, paramMVTable.getName() + "_DATA", paramArrayOfIndexColumn, 0, paramIndexType);
/*  49 */     this.mvTable = paramMVTable;
/*  50 */     RowDataType rowDataType = paramMVTable.getRowFactory().getRowDataType();
/*  51 */     this.mapName = "table." + getId();
/*  52 */     Transaction transaction = this.mvTable.getTransactionBegin();
/*  53 */     this.dataMap = transaction.openMap(this.mapName, (DataType)LongDataType.INSTANCE, (DataType)rowDataType);
/*  54 */     this.dataMap.map.setVolatile((!paramMVTable.isPersistData() || !paramIndexType.isPersistent()));
/*  55 */     if (!paramDatabase.isStarting()) {
/*  56 */       this.dataMap.clear();
/*     */     }
/*  58 */     transaction.commit();
/*  59 */     Long long_ = (Long)this.dataMap.map.lastKey();
/*  60 */     this.lastKey.set((long_ == null) ? 0L : long_.longValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  65 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPlanSQL() {
/*  70 */     return this.table.getSQL(new StringBuilder(), 3).append(".tableScan").toString();
/*     */   }
/*     */   
/*     */   public void setMainIndexColumn(int paramInt) {
/*  74 */     this.mainIndexColumn = paramInt;
/*     */   }
/*     */   
/*     */   public int getMainIndexColumn() {
/*  78 */     return this.mainIndexColumn;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/*  88 */     if (this.mainIndexColumn == -1) {
/*  89 */       if (paramRow.getKey() == 0L) {
/*  90 */         paramRow.setKey(this.lastKey.incrementAndGet());
/*     */       }
/*     */     } else {
/*  93 */       long l = paramRow.getValue(this.mainIndexColumn).getLong();
/*  94 */       paramRow.setKey(l);
/*     */     } 
/*     */     
/*  97 */     if (this.mvTable.getContainsLargeObject()) {
/*  98 */       byte b; int i; for (b = 0, i = paramRow.getColumnCount(); b < i; b++) {
/*  99 */         Value value = paramRow.getValue(b);
/* 100 */         if (value instanceof ValueLob) {
/* 101 */           ValueLob valueLob = ((ValueLob)value).copy((DataHandler)this.database, getId());
/* 102 */           paramSessionLocal.removeAtCommitStop(valueLob);
/* 103 */           if (value != valueLob) {
/* 104 */             paramRow.setValue(b, (Value)valueLob);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 110 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/* 111 */     long l1 = paramRow.getKey();
/*     */     try {
/* 113 */       Row row = (Row)transactionMap.putIfAbsent(Long.valueOf(l1), paramRow);
/* 114 */       if (row != null) {
/* 115 */         int i = 90131;
/* 116 */         if (transactionMap.getImmediate(Long.valueOf(l1)) != null || transactionMap.getFromSnapshot(Long.valueOf(l1)) != null)
/*     */         {
/* 118 */           i = 23505;
/*     */         }
/* 120 */         DbException dbException = DbException.get(i, 
/* 121 */             getDuplicatePrimaryKeyMessage(this.mainIndexColumn).append(' ').append(row).toString());
/* 122 */         dbException.setSource(this);
/* 123 */         throw dbException;
/*     */       } 
/* 125 */     } catch (MVStoreException mVStoreException) {
/* 126 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */     long l2;
/*     */     do {
/*     */     
/* 131 */     } while (l1 > (l2 = this.lastKey.get()) && 
/* 132 */       !this.lastKey.compareAndSet(l2, l1));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/* 138 */     if (this.mvTable.getContainsLargeObject()) {
/* 139 */       byte b; int i; for (b = 0, i = paramRow.getColumnCount(); b < i; b++) {
/* 140 */         Value value = paramRow.getValue(b);
/* 141 */         if (value instanceof ValueLob) {
/* 142 */           paramSessionLocal.removeAtCommit((ValueLob)value);
/*     */         }
/*     */       } 
/*     */     } 
/* 146 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/*     */     try {
/* 148 */       Row row = (Row)transactionMap.remove(Long.valueOf(paramRow.getKey()));
/* 149 */       if (row == null) {
/* 150 */         StringBuilder stringBuilder = new StringBuilder();
/* 151 */         getSQL(stringBuilder, 3).append(": ").append(paramRow.getKey());
/* 152 */         throw DbException.get(90112, stringBuilder.toString());
/*     */       } 
/* 154 */     } catch (MVStoreException mVStoreException) {
/* 155 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 161 */     if (this.mainIndexColumn != -1) {
/* 162 */       long l1 = paramRow2.getValue(this.mainIndexColumn).getLong();
/* 163 */       paramRow2.setKey(l1);
/*     */     } 
/* 165 */     long l = paramRow1.getKey();
/* 166 */     assert this.mainIndexColumn != -1 || l != 0L;
/* 167 */     assert l == paramRow2.getKey() : l + " != " + paramRow2.getKey();
/* 168 */     if (this.mvTable.getContainsLargeObject()) {
/* 169 */       byte b; int i; for (b = 0, i = paramRow1.getColumnCount(); b < i; b++) {
/* 170 */         Value value1 = paramRow1.getValue(b);
/* 171 */         Value value2 = paramRow2.getValue(b);
/* 172 */         if (value1 != value2) {
/* 173 */           if (value1 instanceof ValueLob) {
/* 174 */             paramSessionLocal.removeAtCommit((ValueLob)value1);
/*     */           }
/* 176 */           if (value2 instanceof ValueLob) {
/* 177 */             ValueLob valueLob = ((ValueLob)value2).copy((DataHandler)this.database, getId());
/* 178 */             paramSessionLocal.removeAtCommitStop(valueLob);
/* 179 */             if (value2 != valueLob) {
/* 180 */               paramRow2.setValue(b, (Value)valueLob);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 187 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/*     */     try {
/* 189 */       Row row = (Row)transactionMap.put(Long.valueOf(l), paramRow2);
/* 190 */       if (row == null) {
/* 191 */         StringBuilder stringBuilder = new StringBuilder();
/* 192 */         getSQL(stringBuilder, 3).append(": ").append(l);
/* 193 */         throw DbException.get(90112, stringBuilder.toString());
/*     */       } 
/* 195 */     } catch (MVStoreException mVStoreException) {
/* 196 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 202 */     if (paramRow2.getKey() > this.lastKey.get()) {
/* 203 */       this.lastKey.set(paramRow2.getKey());
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
/*     */   Row lockRow(SessionLocal paramSessionLocal, Row paramRow) {
/* 215 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/* 216 */     long l = paramRow.getKey();
/* 217 */     return lockRow(transactionMap, l);
/*     */   }
/*     */   
/*     */   private Row lockRow(TransactionMap<Long, SearchRow> paramTransactionMap, long paramLong) {
/*     */     try {
/* 222 */       return setRowKey((Row)paramTransactionMap.lock(Long.valueOf(paramLong)), paramLong);
/* 223 */     } catch (MVStoreException mVStoreException) {
/* 224 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 230 */     long l1 = extractPKFromRow(paramSearchRow1, Long.MIN_VALUE);
/* 231 */     long l2 = extractPKFromRow(paramSearchRow2, Long.MAX_VALUE);
/* 232 */     return find(paramSessionLocal, Long.valueOf(l1), Long.valueOf(l2));
/*     */   }
/*     */   
/*     */   private long extractPKFromRow(SearchRow paramSearchRow, long paramLong) {
/*     */     long l;
/* 237 */     if (paramSearchRow == null) {
/* 238 */       l = paramLong;
/* 239 */     } else if (this.mainIndexColumn == -1) {
/* 240 */       l = paramSearchRow.getKey();
/*     */     } else {
/* 242 */       Value value = paramSearchRow.getValue(this.mainIndexColumn);
/* 243 */       if (value == null) {
/* 244 */         l = paramSearchRow.getKey();
/* 245 */       } else if (value == ValueNull.INSTANCE) {
/* 246 */         l = 0L;
/*     */       } else {
/* 248 */         l = value.getLong();
/*     */       } 
/*     */     } 
/* 251 */     return l;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MVTable getTable() {
/* 257 */     return this.mvTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public Row getRow(SessionLocal paramSessionLocal, long paramLong) {
/* 262 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/* 263 */     Row row = (Row)transactionMap.getFromSnapshot(Long.valueOf(paramLong));
/* 264 */     if (row == null) {
/* 265 */       throw DbException.get(90143, new String[] { getTraceSQL(), String.valueOf(paramLong) });
/*     */     }
/* 267 */     return setRowKey(row, paramLong);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*     */     try {
/* 275 */       return (10L * getCostRangeIndex(paramArrayOfint, this.dataMap.sizeAsLongMax(), paramArrayOfTableFilter, paramInt, paramSortOrder, true, paramAllColumnsForPlan));
/*     */     }
/* 277 */     catch (MVStoreException mVStoreException) {
/* 278 */       throw DbException.get(90007, mVStoreException, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getColumnIndex(Column paramColumn) {
/* 285 */     return -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirstColumn(Column paramColumn) {
/* 290 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {
/* 295 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/* 296 */     if (!transactionMap.isClosed()) {
/* 297 */       Transaction transaction = paramSessionLocal.getTransaction();
/* 298 */       transaction.removeMap(transactionMap);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {
/* 304 */     if (this.mvTable.getContainsLargeObject()) {
/* 305 */       this.database.getLobStorage().removeAllForTable(this.table.getId());
/*     */     }
/* 307 */     getMap(paramSessionLocal).clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetFirstOrLast() {
/* 312 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findFirstOrLast(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 317 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/* 318 */     Map.Entry entry = paramBoolean ? transactionMap.firstEntry() : transactionMap.lastEntry();
/* 319 */     return (Cursor)new SingleRowCursor((entry != null) ? setRowKey((Row)entry.getValue(), ((Long)entry.getKey()).longValue()) : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/* 324 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 329 */     return getMap(paramSessionLocal).sizeAsLong();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRowCountMax() {
/* 338 */     return this.dataMap.sizeAsLongMax();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 343 */     return getRowCountMax();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getDiskSpaceUsed() {
/* 348 */     return this.dataMap.map.getRootPage().getDiskSpaceUsed();
/*     */   }
/*     */   
/*     */   public String getMapName() {
/* 352 */     return this.mapName;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRowsToBuffer(List<Row> paramList, String paramString) {
/* 357 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBufferedRows(List<String> paramList) {
/* 362 */     throw new UnsupportedOperationException();
/*     */   }
/*     */   
/*     */   private Cursor find(SessionLocal paramSessionLocal, Long paramLong1, Long paramLong2) {
/* 366 */     TransactionMap<Long, SearchRow> transactionMap = getMap(paramSessionLocal);
/* 367 */     if (paramLong1 != null && paramLong2 != null && paramLong1.longValue() == paramLong2.longValue()) {
/* 368 */       return (Cursor)new SingleRowCursor(setRowKey((Row)transactionMap.getFromSnapshot(paramLong1), paramLong1.longValue()));
/*     */     }
/* 370 */     return new MVStoreCursor(transactionMap.entryIterator(paramLong1, paramLong2));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRowIdIndex() {
/* 375 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TransactionMap<Long, SearchRow> getMap(SessionLocal paramSessionLocal) {
/* 385 */     if (paramSessionLocal == null) {
/* 386 */       return this.dataMap;
/*     */     }
/* 388 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 389 */     return this.dataMap.getInstance(transaction);
/*     */   }
/*     */ 
/*     */   
/*     */   public MVMap<Long, VersionedValue<SearchRow>> getMVMap() {
/* 394 */     return this.dataMap.map;
/*     */   }
/*     */   
/*     */   private static Row setRowKey(Row paramRow, long paramLong) {
/* 398 */     if (paramRow != null && paramRow.getKey() == 0L) {
/* 399 */       paramRow.setKey(paramLong);
/*     */     }
/* 401 */     return paramRow;
/*     */   }
/*     */ 
/*     */   
/*     */   static final class MVStoreCursor
/*     */     implements Cursor
/*     */   {
/*     */     private final TransactionMap.TMIterator<Long, SearchRow, Map.Entry<Long, SearchRow>> it;
/*     */     
/*     */     private Map.Entry<Long, SearchRow> current;
/*     */     private Row row;
/*     */     
/*     */     public MVStoreCursor(TransactionMap.TMIterator<Long, SearchRow, Map.Entry<Long, SearchRow>> param1TMIterator) {
/* 414 */       this.it = param1TMIterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public Row get() {
/* 419 */       if (this.row == null && 
/* 420 */         this.current != null) {
/* 421 */         this.row = (Row)this.current.getValue();
/* 422 */         if (this.row.getKey() == 0L) {
/* 423 */           this.row.setKey(((Long)this.current.getKey()).longValue());
/*     */         }
/*     */       } 
/*     */       
/* 427 */       return this.row;
/*     */     }
/*     */ 
/*     */     
/*     */     public SearchRow getSearchRow() {
/* 432 */       return (SearchRow)get();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean next() {
/* 437 */       this.current = (Map.Entry<Long, SearchRow>)this.it.fetchNext();
/* 438 */       this.row = null;
/* 439 */       return (this.current != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean previous() {
/* 444 */       throw DbException.getUnsupportedException("previous");
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVPrimaryIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */