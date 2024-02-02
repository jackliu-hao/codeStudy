/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.PriorityQueue;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.index.SingleRowCursor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.MVStoreException;
/*     */ import org.h2.mvstore.tx.Transaction;
/*     */ import org.h2.mvstore.tx.TransactionMap;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.RowFactory;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.VersionedValue;
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
/*     */ public final class MVSecondaryIndex
/*     */   extends MVIndex<SearchRow, Value>
/*     */ {
/*     */   private final MVTable mvTable;
/*     */   private final TransactionMap<SearchRow, Value> dataMap;
/*     */   
/*     */   public MVSecondaryIndex(Database paramDatabase, MVTable paramMVTable, int paramInt1, String paramString, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType) {
/*  51 */     super((Table)paramMVTable, paramInt1, paramString, paramArrayOfIndexColumn, paramInt2, paramIndexType);
/*  52 */     this.mvTable = paramMVTable;
/*  53 */     if (!this.database.isStarting()) {
/*  54 */       checkIndexColumnTypes(paramArrayOfIndexColumn);
/*     */     }
/*  56 */     String str = "index." + getId();
/*  57 */     RowDataType rowDataType = getRowFactory().getRowDataType();
/*  58 */     Transaction transaction = this.mvTable.getTransactionBegin();
/*  59 */     this.dataMap = transaction.openMap(str, (DataType)rowDataType, NullValueDataType.INSTANCE);
/*  60 */     this.dataMap.map.setVolatile((!paramMVTable.isPersistData() || !paramIndexType.isPersistent()));
/*  61 */     if (!paramDatabase.isStarting()) {
/*  62 */       this.dataMap.clear();
/*     */     }
/*  64 */     transaction.commit();
/*  65 */     if (!rowDataType.equals(this.dataMap.getKeyType())) {
/*  66 */       throw DbException.getInternalError("Incompatible key type, expected " + rowDataType + " but got " + this.dataMap
/*     */           
/*  68 */           .getKeyType() + " for index " + paramString);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRowsToBuffer(List<Row> paramList, String paramString) {
/*  74 */     MVMap<SearchRow, Value> mVMap = openMap(paramString);
/*  75 */     for (Row row : paramList) {
/*  76 */       SearchRow searchRow = getRowFactory().createRow();
/*  77 */       searchRow.copyFrom((SearchRow)row);
/*  78 */       mVMap.append(searchRow, ValueNull.INSTANCE);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Source
/*     */   {
/*     */     private final Iterator<SearchRow> iterator;
/*     */     SearchRow currentRowData;
/*     */     
/*     */     public Source(Iterator<SearchRow> param1Iterator) {
/*  89 */       assert param1Iterator.hasNext();
/*  90 */       this.iterator = param1Iterator;
/*  91 */       this.currentRowData = param1Iterator.next();
/*     */     }
/*     */     
/*     */     public boolean hasNext() {
/*  95 */       boolean bool = this.iterator.hasNext();
/*  96 */       if (bool) {
/*  97 */         this.currentRowData = this.iterator.next();
/*     */       }
/*  99 */       return bool;
/*     */     }
/*     */     
/*     */     public SearchRow next() {
/* 103 */       return this.currentRowData;
/*     */     }
/*     */     
/*     */     static final class Comparator
/*     */       implements java.util.Comparator<Source> {
/*     */       private final DataType<SearchRow> type;
/*     */       
/*     */       public Comparator(DataType<SearchRow> param2DataType) {
/* 111 */         this.type = param2DataType;
/*     */       }
/*     */       
/*     */       public int compare(MVSecondaryIndex.Source param2Source1, MVSecondaryIndex.Source param2Source2)
/*     */       {
/* 116 */         return this.type.compare(param2Source1.currentRowData, param2Source2.currentRowData); } } } static final class Comparator implements java.util.Comparator<Source> { public int compare(MVSecondaryIndex.Source param1Source1, MVSecondaryIndex.Source param1Source2) { return this.type.compare(param1Source1.currentRowData, param1Source2.currentRowData); }
/*     */     
/*     */     private final DataType<SearchRow> type;
/*     */     public Comparator(DataType<SearchRow> param1DataType) {
/*     */       this.type = param1DataType;
/*     */     } }
/*     */   public void addBufferedRows(List<String> paramList) {
/* 123 */     int i = paramList.size();
/*     */     
/* 125 */     PriorityQueue<Source> priorityQueue = new PriorityQueue(i, new Source.Comparator((DataType<SearchRow>)getRowFactory().getRowDataType()));
/* 126 */     for (String str : paramList) {
/* 127 */       Iterator<SearchRow> iterator = openMap(str).keyIterator(null);
/* 128 */       if (iterator.hasNext()) {
/* 129 */         priorityQueue.offer(new Source(iterator));
/*     */       }
/*     */     } 
/*     */     
/*     */     try {
/* 134 */       while (!priorityQueue.isEmpty()) {
/* 135 */         Source source = priorityQueue.poll();
/* 136 */         SearchRow searchRow = source.next();
/*     */         
/* 138 */         if (this.uniqueColumnColumn > 0 && !mayHaveNullDuplicates(searchRow)) {
/* 139 */           checkUnique(false, this.dataMap, searchRow, Long.MIN_VALUE);
/*     */         }
/*     */         
/* 142 */         this.dataMap.putCommitted(searchRow, ValueNull.INSTANCE);
/*     */         
/* 144 */         if (source.hasNext()) {
/* 145 */           priorityQueue.offer(source);
/*     */         }
/*     */       } 
/*     */     } finally {
/* 149 */       MVStore mVStore = this.database.getStore().getMvStore();
/* 150 */       for (String str : paramList) {
/* 151 */         mVStore.removeMap(str);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private MVMap<SearchRow, Value> openMap(String paramString) {
/* 157 */     RowDataType rowDataType = getRowFactory().getRowDataType();
/*     */ 
/*     */ 
/*     */     
/* 161 */     MVMap.Builder builder = (new MVMap.Builder()).singleWriter().keyType((DataType)rowDataType).valueType(NullValueDataType.INSTANCE);
/*     */     
/* 163 */     MVMap<SearchRow, Value> mVMap = this.database.getStore().getMvStore().openMap(paramString, (MVMap.MapBuilder)builder);
/* 164 */     if (!rowDataType.equals(mVMap.getKeyType())) {
/* 165 */       throw DbException.getInternalError("Incompatible key type, expected " + rowDataType + " but got " + mVMap
/*     */           
/* 167 */           .getKeyType() + " for map " + paramString);
/*     */     }
/* 169 */     return mVMap;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/* 179 */     TransactionMap<SearchRow, Value> transactionMap = getMap(paramSessionLocal);
/* 180 */     SearchRow searchRow = convertToKey((SearchRow)paramRow, (Boolean)null);
/* 181 */     boolean bool = (this.uniqueColumnColumn > 0 && !mayHaveNullDuplicates((SearchRow)paramRow)) ? true : false;
/* 182 */     if (bool) {
/* 183 */       boolean bool1 = !paramSessionLocal.getTransaction().allowNonRepeatableRead() ? true : false;
/* 184 */       checkUnique(bool1, transactionMap, (SearchRow)paramRow, Long.MIN_VALUE);
/*     */     } 
/*     */     
/*     */     try {
/* 188 */       transactionMap.put(searchRow, ValueNull.INSTANCE);
/* 189 */     } catch (MVStoreException mVStoreException) {
/* 190 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */     
/* 193 */     if (bool) {
/* 194 */       checkUnique(false, transactionMap, (SearchRow)paramRow, paramRow.getKey());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void checkUnique(boolean paramBoolean, TransactionMap<SearchRow, Value> paramTransactionMap, SearchRow paramSearchRow, long paramLong) {
/* 200 */     RowFactory rowFactory = getUniqueRowFactory();
/* 201 */     SearchRow searchRow1 = rowFactory.createRow();
/* 202 */     searchRow1.copyFrom(paramSearchRow);
/* 203 */     searchRow1.setKey(Long.MIN_VALUE);
/* 204 */     SearchRow searchRow2 = rowFactory.createRow();
/* 205 */     searchRow2.copyFrom(paramSearchRow);
/* 206 */     searchRow2.setKey(Long.MAX_VALUE);
/* 207 */     if (paramBoolean) {
/*     */ 
/*     */ 
/*     */       
/* 211 */       TransactionMap.TMIterator tMIterator1 = paramTransactionMap.keyIterator(searchRow1, searchRow2); SearchRow searchRow;
/* 212 */       while ((searchRow = (SearchRow)tMIterator1.fetchNext()) != null) {
/* 213 */         if (paramLong != searchRow.getKey() && !paramTransactionMap.isDeletedByCurrentTransaction(searchRow)) {
/* 214 */           throw getDuplicateKeyException(searchRow.toString());
/*     */         }
/*     */       } 
/*     */     } 
/* 218 */     TransactionMap.TMIterator tMIterator = paramTransactionMap.keyIteratorUncommitted(searchRow1, searchRow2); SearchRow searchRow3;
/* 219 */     while ((searchRow3 = (SearchRow)tMIterator.fetchNext()) != null) {
/* 220 */       if (paramLong != searchRow3.getKey()) {
/* 221 */         if (paramTransactionMap.getImmediate(searchRow3) != null)
/*     */         {
/* 223 */           throw getDuplicateKeyException(searchRow3.toString());
/*     */         }
/* 225 */         throw DbException.get(90131, this.table.getName());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/* 232 */     SearchRow searchRow = convertToKey((SearchRow)paramRow, (Boolean)null);
/* 233 */     TransactionMap<SearchRow, Value> transactionMap = getMap(paramSessionLocal);
/*     */     try {
/* 235 */       if (transactionMap.remove(searchRow) == null) {
/* 236 */         StringBuilder stringBuilder = new StringBuilder();
/* 237 */         getSQL(stringBuilder, 3).append(": ").append(paramRow.getKey());
/* 238 */         throw DbException.get(90112, stringBuilder.toString());
/*     */       } 
/* 240 */     } catch (MVStoreException mVStoreException) {
/* 241 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(SessionLocal paramSessionLocal, Row paramRow1, Row paramRow2) {
/* 247 */     SearchRow searchRow1 = convertToKey((SearchRow)paramRow1, (Boolean)null);
/* 248 */     SearchRow searchRow2 = convertToKey((SearchRow)paramRow2, (Boolean)null);
/* 249 */     if (!rowsAreEqual(searchRow1, searchRow2)) {
/* 250 */       super.update(paramSessionLocal, paramRow1, paramRow2);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean rowsAreEqual(SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 255 */     if (paramSearchRow1 == paramSearchRow2) {
/* 256 */       return true;
/*     */     }
/* 258 */     for (int i : this.columnIds) {
/* 259 */       Value value1 = paramSearchRow1.getValue(i);
/* 260 */       Value value2 = paramSearchRow2.getValue(i);
/* 261 */       if (!Objects.equals(value1, value2)) {
/* 262 */         return false;
/*     */       }
/*     */     } 
/* 265 */     return (paramSearchRow1.getKey() == paramSearchRow2.getKey());
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 270 */     return find(paramSessionLocal, paramSearchRow1, false, paramSearchRow2);
/*     */   }
/*     */   
/*     */   private Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, boolean paramBoolean, SearchRow paramSearchRow2) {
/* 274 */     SearchRow searchRow1 = convertToKey(paramSearchRow1, Boolean.valueOf(paramBoolean));
/* 275 */     SearchRow searchRow2 = convertToKey(paramSearchRow2, Boolean.TRUE);
/* 276 */     return new MVStoreCursor(paramSessionLocal, getMap(paramSessionLocal).keyIterator(searchRow1, searchRow2), this.mvTable);
/*     */   }
/*     */   
/*     */   private SearchRow convertToKey(SearchRow paramSearchRow, Boolean paramBoolean) {
/* 280 */     if (paramSearchRow == null) {
/* 281 */       return null;
/*     */     }
/*     */     
/* 284 */     SearchRow searchRow = getRowFactory().createRow();
/* 285 */     searchRow.copyFrom(paramSearchRow);
/* 286 */     if (paramBoolean != null) {
/* 287 */       searchRow.setKey(paramBoolean.booleanValue() ? Long.MAX_VALUE : Long.MIN_VALUE);
/*     */     }
/* 289 */     return searchRow;
/*     */   }
/*     */ 
/*     */   
/*     */   public MVTable getTable() {
/* 294 */     return this.mvTable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/*     */     try {
/* 302 */       return (10L * getCostRangeIndex(paramArrayOfint, this.dataMap.sizeAsLongMax(), paramArrayOfTableFilter, paramInt, paramSortOrder, false, paramAllColumnsForPlan));
/*     */     }
/* 304 */     catch (MVStoreException mVStoreException) {
/* 305 */       throw DbException.get(90007, mVStoreException, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {
/* 311 */     TransactionMap<SearchRow, Value> transactionMap = getMap(paramSessionLocal);
/* 312 */     if (!transactionMap.isClosed()) {
/* 313 */       Transaction transaction = paramSessionLocal.getTransaction();
/* 314 */       transaction.removeMap(transactionMap);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {
/* 320 */     TransactionMap<SearchRow, Value> transactionMap = getMap(paramSessionLocal);
/* 321 */     transactionMap.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canGetFirstOrLast() {
/* 326 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findFirstOrLast(SessionLocal paramSessionLocal, boolean paramBoolean) {
/* 331 */     TransactionMap.TMIterator tMIterator = getMap(paramSessionLocal).keyIterator(null, !paramBoolean); SearchRow searchRow;
/* 332 */     while ((searchRow = (SearchRow)tMIterator.fetchNext()) != null) {
/* 333 */       if (searchRow.getValue(this.columnIds[0]) != ValueNull.INSTANCE) {
/* 334 */         return (Cursor)new SingleRowCursor(this.mvTable.getRow(paramSessionLocal, searchRow.getKey()));
/*     */       }
/*     */     } 
/* 337 */     return (Cursor)new SingleRowCursor(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/*     */     try {
/* 343 */       return (this.dataMap.sizeAsLongMax() == 0L);
/* 344 */     } catch (MVStoreException mVStoreException) {
/* 345 */       throw DbException.get(90007, mVStoreException, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 351 */     TransactionMap<SearchRow, Value> transactionMap = getMap(paramSessionLocal);
/* 352 */     return transactionMap.sizeAsLong();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/*     */     try {
/* 358 */       return this.dataMap.sizeAsLongMax();
/* 359 */     } catch (MVStoreException mVStoreException) {
/* 360 */       throw DbException.get(90007, mVStoreException, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDiskSpaceUsed() {
/* 367 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canFindNext() {
/* 372 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findNext(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 377 */     return find(paramSessionLocal, paramSearchRow1, true, paramSearchRow2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TransactionMap<SearchRow, Value> getMap(SessionLocal paramSessionLocal) {
/* 387 */     if (paramSessionLocal == null) {
/* 388 */       return this.dataMap;
/*     */     }
/* 390 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 391 */     return this.dataMap.getInstance(transaction);
/*     */   }
/*     */ 
/*     */   
/*     */   public MVMap<SearchRow, VersionedValue<Value>> getMVMap() {
/* 396 */     return this.dataMap.map;
/*     */   }
/*     */ 
/*     */   
/*     */   static final class MVStoreCursor
/*     */     implements Cursor
/*     */   {
/*     */     private final SessionLocal session;
/*     */     
/*     */     private final TransactionMap.TMIterator<SearchRow, Value, SearchRow> it;
/*     */     private final MVTable mvTable;
/*     */     private SearchRow current;
/*     */     private Row row;
/*     */     
/*     */     MVStoreCursor(SessionLocal param1SessionLocal, TransactionMap.TMIterator<SearchRow, Value, SearchRow> param1TMIterator, MVTable param1MVTable) {
/* 411 */       this.session = param1SessionLocal;
/* 412 */       this.it = param1TMIterator;
/* 413 */       this.mvTable = param1MVTable;
/*     */     }
/*     */ 
/*     */     
/*     */     public Row get() {
/* 418 */       if (this.row == null) {
/* 419 */         SearchRow searchRow = getSearchRow();
/* 420 */         if (searchRow != null) {
/* 421 */           this.row = this.mvTable.getRow(this.session, searchRow.getKey());
/*     */         }
/*     */       } 
/* 424 */       return this.row;
/*     */     }
/*     */ 
/*     */     
/*     */     public SearchRow getSearchRow() {
/* 429 */       return this.current;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean next() {
/* 434 */       this.current = (SearchRow)this.it.fetchNext();
/* 435 */       this.row = null;
/* 436 */       return (this.current != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean previous() {
/* 441 */       throw DbException.getUnsupportedException("previous");
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVSecondaryIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */