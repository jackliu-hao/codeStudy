/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.h2.command.query.AllColumnsForPlan;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.index.Cursor;
/*     */ import org.h2.index.IndexType;
/*     */ import org.h2.index.SpatialIndex;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStoreException;
/*     */ import org.h2.mvstore.Page;
/*     */ import org.h2.mvstore.rtree.MVRTreeMap;
/*     */ import org.h2.mvstore.rtree.Spatial;
/*     */ import org.h2.mvstore.tx.Transaction;
/*     */ import org.h2.mvstore.tx.TransactionMap;
/*     */ import org.h2.mvstore.tx.VersionedValueType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.result.SortOrder;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.IndexColumn;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableFilter;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueGeometry;
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
/*     */ public class MVSpatialIndex
/*     */   extends MVIndex<Spatial, Value>
/*     */   implements SpatialIndex
/*     */ {
/*     */   final MVTable mvTable;
/*     */   private final TransactionMap<Spatial, Value> dataMap;
/*     */   private final MVRTreeMap<VersionedValue<Value>> spatialMap;
/*     */   
/*     */   public MVSpatialIndex(Database paramDatabase, MVTable paramMVTable, int paramInt1, String paramString, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType) {
/*  74 */     super((Table)paramMVTable, paramInt1, paramString, paramArrayOfIndexColumn, paramInt2, paramIndexType);
/*  75 */     if (paramArrayOfIndexColumn.length != 1) {
/*  76 */       throw DbException.getUnsupportedException("Can only index one column");
/*     */     }
/*     */     
/*  79 */     IndexColumn indexColumn = paramArrayOfIndexColumn[0];
/*  80 */     if ((indexColumn.sortType & 0x1) != 0) {
/*  81 */       throw DbException.getUnsupportedException("Cannot index in descending order");
/*     */     }
/*     */     
/*  84 */     if ((indexColumn.sortType & 0x2) != 0) {
/*  85 */       throw DbException.getUnsupportedException("Nulls first is not supported");
/*     */     }
/*     */     
/*  88 */     if ((indexColumn.sortType & 0x4) != 0) {
/*  89 */       throw DbException.getUnsupportedException("Nulls last is not supported");
/*     */     }
/*     */     
/*  92 */     if (indexColumn.column.getType().getValueType() != 37) {
/*  93 */       throw DbException.getUnsupportedException("Spatial index on non-geometry column, " + indexColumn.column
/*     */           
/*  95 */           .getCreateSQL());
/*     */     }
/*  97 */     this.mvTable = paramMVTable;
/*  98 */     if (!this.database.isStarting()) {
/*  99 */       checkIndexColumnTypes(paramArrayOfIndexColumn);
/*     */     }
/* 101 */     String str = "index." + getId();
/* 102 */     VersionedValueType versionedValueType = new VersionedValueType(NullValueDataType.INSTANCE);
/*     */ 
/*     */     
/* 105 */     MVRTreeMap.Builder builder = (new MVRTreeMap.Builder()).valueType((DataType)versionedValueType);
/* 106 */     this.spatialMap = (MVRTreeMap<VersionedValue<Value>>)paramDatabase.getStore().getMvStore().openMap(str, (MVMap.MapBuilder)builder);
/* 107 */     Transaction transaction = this.mvTable.getTransactionBegin();
/* 108 */     this.dataMap = transaction.openMapX((MVMap)this.spatialMap);
/* 109 */     this.dataMap.map.setVolatile((!paramMVTable.isPersistData() || !paramIndexType.isPersistent()));
/* 110 */     transaction.commit();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addRowsToBuffer(List<Row> paramList, String paramString) {
/* 115 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addBufferedRows(List<String> paramList) {
/* 120 */     throw DbException.getInternalError();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(SessionLocal paramSessionLocal) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(SessionLocal paramSessionLocal, Row paramRow) {
/* 130 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/* 131 */     SpatialKey spatialKey = getKey((SearchRow)paramRow);
/*     */     
/* 133 */     if (spatialKey.isNull()) {
/*     */       return;
/*     */     }
/*     */     
/* 137 */     if (this.uniqueColumnColumn > 0) {
/*     */       
/* 139 */       MVRTreeMap.RTreeCursor rTreeCursor = this.spatialMap.findContainedKeys(spatialKey);
/* 140 */       SpatialKeyIterator spatialKeyIterator = new SpatialKeyIterator(transactionMap, (Iterator<Spatial>)rTreeCursor, false);
/* 141 */       while (spatialKeyIterator.hasNext()) {
/* 142 */         Spatial spatial = spatialKeyIterator.next();
/* 143 */         if (spatial.equalsIgnoringId(spatialKey)) {
/* 144 */           throw getDuplicateKeyException(spatialKey.toString());
/*     */         }
/*     */       } 
/*     */     } 
/*     */     try {
/* 149 */       transactionMap.put(spatialKey, ValueNull.INSTANCE);
/* 150 */     } catch (MVStoreException mVStoreException) {
/* 151 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/* 153 */     if (this.uniqueColumnColumn > 0) {
/*     */       
/* 155 */       MVRTreeMap.RTreeCursor rTreeCursor = this.spatialMap.findContainedKeys(spatialKey);
/* 156 */       SpatialKeyIterator spatialKeyIterator = new SpatialKeyIterator(transactionMap, (Iterator<Spatial>)rTreeCursor, true);
/* 157 */       while (spatialKeyIterator.hasNext()) {
/* 158 */         Spatial spatial = spatialKeyIterator.next();
/* 159 */         if (!spatial.equalsIgnoringId(spatialKey) || 
/* 160 */           transactionMap.isSameTransaction(spatial)) {
/*     */           continue;
/*     */         }
/* 163 */         transactionMap.remove(spatialKey);
/* 164 */         if (transactionMap.getImmediate(spatial) != null)
/*     */         {
/* 166 */           throw getDuplicateKeyException(spatial.toString());
/*     */         }
/* 168 */         throw DbException.get(90131, this.table.getName());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal, Row paramRow) {
/* 176 */     SpatialKey spatialKey = getKey((SearchRow)paramRow);
/*     */     
/* 178 */     if (spatialKey.isNull()) {
/*     */       return;
/*     */     }
/*     */     
/* 182 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/*     */     try {
/* 184 */       Value value = (Value)transactionMap.remove(spatialKey);
/* 185 */       if (value == null) {
/* 186 */         StringBuilder stringBuilder = new StringBuilder();
/* 187 */         getSQL(stringBuilder, 3).append(": ").append(paramRow.getKey());
/* 188 */         throw DbException.get(90112, stringBuilder.toString());
/*     */       } 
/* 190 */     } catch (MVStoreException mVStoreException) {
/* 191 */       throw this.mvTable.convertException(mVStoreException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor find(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/* 197 */     Iterator<Spatial> iterator = this.spatialMap.keyIterator(null);
/* 198 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/* 199 */     SpatialKeyIterator spatialKeyIterator = new SpatialKeyIterator(transactionMap, iterator, false);
/* 200 */     return new MVStoreCursor(paramSessionLocal, spatialKeyIterator, this.mvTable);
/*     */   }
/*     */ 
/*     */   
/*     */   public Cursor findByGeometry(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2, SearchRow paramSearchRow3) {
/* 205 */     if (paramSearchRow3 == null) {
/* 206 */       return find(paramSessionLocal, paramSearchRow1, paramSearchRow2);
/*     */     }
/*     */     
/* 209 */     MVRTreeMap.RTreeCursor rTreeCursor = this.spatialMap.findIntersectingKeys(getKey(paramSearchRow3));
/* 210 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/* 211 */     SpatialKeyIterator spatialKeyIterator = new SpatialKeyIterator(transactionMap, (Iterator<Spatial>)rTreeCursor, false);
/* 212 */     return new MVStoreCursor(paramSessionLocal, spatialKeyIterator, this.mvTable);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getBounds(SessionLocal paramSessionLocal) {
/* 223 */     FindBoundsCursor findBoundsCursor = new FindBoundsCursor(this.spatialMap.getRootPage(), new SpatialKey(0L, new float[0]), paramSessionLocal, getMap(paramSessionLocal), this.columnIds[0]);
/* 224 */     while (findBoundsCursor.hasNext()) {
/* 225 */       findBoundsCursor.next();
/*     */     }
/* 227 */     return findBoundsCursor.getBounds();
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
/*     */   public Value getEstimatedBounds(SessionLocal paramSessionLocal) {
/* 239 */     Page page = this.spatialMap.getRootPage();
/* 240 */     int i = page.getKeyCount();
/* 241 */     if (i > 0) {
/* 242 */       Spatial spatial = (Spatial)page.getKey(0);
/* 243 */       float f1 = spatial.min(0), f2 = spatial.max(0), f3 = spatial.min(1), f4 = spatial.max(1);
/* 244 */       for (byte b = 1; b < i; b++) {
/* 245 */         spatial = (Spatial)page.getKey(b);
/* 246 */         float f5 = spatial.min(0), f6 = spatial.max(0), f7 = spatial.min(1), f8 = spatial.max(1);
/* 247 */         if (f5 < f1) {
/* 248 */           f1 = f5;
/*     */         }
/* 250 */         if (f6 > f2) {
/* 251 */           f2 = f6;
/*     */         }
/* 253 */         if (f7 < f3) {
/* 254 */           f3 = f7;
/*     */         }
/* 256 */         if (f8 > f4) {
/* 257 */           f4 = f8;
/*     */         }
/*     */       } 
/* 260 */       return ValueGeometry.fromEnvelope(new double[] { f1, f2, f3, f4 });
/*     */     } 
/* 262 */     return (Value)ValueNull.INSTANCE;
/*     */   }
/*     */   
/*     */   private SpatialKey getKey(SearchRow paramSearchRow) {
/* 266 */     Value value = paramSearchRow.getValue(this.columnIds[0]);
/*     */     double[] arrayOfDouble;
/* 268 */     if (value == ValueNull.INSTANCE || (arrayOfDouble = value.convertToGeometry(null).getEnvelopeNoCopy()) == null) {
/* 269 */       return new SpatialKey(paramSearchRow.getKey(), new float[0]);
/*     */     }
/* 271 */     return new SpatialKey(paramSearchRow.getKey(), new float[] { (float)arrayOfDouble[0], (float)arrayOfDouble[1], (float)arrayOfDouble[2], (float)arrayOfDouble[3] });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MVTable getTable() {
/* 278 */     return this.mvTable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getCost(SessionLocal paramSessionLocal, int[] paramArrayOfint, TableFilter[] paramArrayOfTableFilter, int paramInt, SortOrder paramSortOrder, AllColumnsForPlan paramAllColumnsForPlan) {
/* 285 */     return getCostRangeIndex(paramArrayOfint, this.columns);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getCostRangeIndex(int[] paramArrayOfint, Column[] paramArrayOfColumn) {
/* 296 */     if (paramArrayOfColumn.length == 0) {
/* 297 */       return Long.MAX_VALUE;
/*     */     }
/* 299 */     for (Column column : paramArrayOfColumn) {
/* 300 */       int i = column.getColumnId();
/* 301 */       int j = paramArrayOfint[i];
/* 302 */       if ((j & 0x10) != 16) {
/* 303 */         return Long.MAX_VALUE;
/*     */       }
/*     */     } 
/* 306 */     return 2L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(SessionLocal paramSessionLocal) {
/* 311 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/* 312 */     if (!transactionMap.isClosed()) {
/* 313 */       Transaction transaction = paramSessionLocal.getTransaction();
/* 314 */       transaction.removeMap(transactionMap);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void truncate(SessionLocal paramSessionLocal) {
/* 320 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/* 321 */     transactionMap.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRebuild() {
/*     */     try {
/* 327 */       return (this.dataMap.sizeAsLongMax() == 0L);
/* 328 */     } catch (MVStoreException mVStoreException) {
/* 329 */       throw DbException.get(90007, mVStoreException, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 335 */     TransactionMap<Spatial, Value> transactionMap = getMap(paramSessionLocal);
/* 336 */     return transactionMap.sizeAsLong();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/*     */     try {
/* 342 */       return this.dataMap.sizeAsLongMax();
/* 343 */     } catch (MVStoreException mVStoreException) {
/* 344 */       throw DbException.get(90007, mVStoreException, new String[0]);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long getDiskSpaceUsed() {
/* 351 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TransactionMap<Spatial, Value> getMap(SessionLocal paramSessionLocal) {
/* 361 */     if (paramSessionLocal == null) {
/* 362 */       return this.dataMap;
/*     */     }
/* 364 */     Transaction transaction = paramSessionLocal.getTransaction();
/* 365 */     return this.dataMap.getInstance(transaction);
/*     */   }
/*     */ 
/*     */   
/*     */   public MVMap<Spatial, VersionedValue<Value>> getMVMap() {
/* 370 */     return this.dataMap.map;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class MVStoreCursor
/*     */     implements Cursor
/*     */   {
/*     */     private final SessionLocal session;
/*     */     
/*     */     private final Iterator<Spatial> it;
/*     */     
/*     */     private final MVTable mvTable;
/*     */     private Spatial current;
/*     */     private SearchRow searchRow;
/*     */     private Row row;
/*     */     
/*     */     MVStoreCursor(SessionLocal param1SessionLocal, Iterator<Spatial> param1Iterator, MVTable param1MVTable) {
/* 387 */       this.session = param1SessionLocal;
/* 388 */       this.it = param1Iterator;
/* 389 */       this.mvTable = param1MVTable;
/*     */     }
/*     */ 
/*     */     
/*     */     public Row get() {
/* 394 */       if (this.row == null) {
/* 395 */         SearchRow searchRow = getSearchRow();
/* 396 */         if (searchRow != null) {
/* 397 */           this.row = this.mvTable.getRow(this.session, searchRow.getKey());
/*     */         }
/*     */       } 
/* 400 */       return this.row;
/*     */     }
/*     */ 
/*     */     
/*     */     public SearchRow getSearchRow() {
/* 405 */       if (this.searchRow == null && 
/* 406 */         this.current != null) {
/* 407 */         this.searchRow = (SearchRow)this.mvTable.getTemplateRow();
/* 408 */         this.searchRow.setKey(this.current.getId());
/*     */       } 
/*     */       
/* 411 */       return this.searchRow;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean next() {
/* 416 */       this.current = this.it.hasNext() ? this.it.next() : null;
/* 417 */       this.searchRow = null;
/* 418 */       this.row = null;
/* 419 */       return (this.current != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean previous() {
/* 424 */       throw DbException.getUnsupportedException("previous");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SpatialKeyIterator
/*     */     implements Iterator<Spatial>
/*     */   {
/*     */     private final TransactionMap<Spatial, Value> map;
/*     */     private final Iterator<Spatial> iterator;
/*     */     private final boolean includeUncommitted;
/*     */     private Spatial current;
/*     */     
/*     */     SpatialKeyIterator(TransactionMap<Spatial, Value> param1TransactionMap, Iterator<Spatial> param1Iterator, boolean param1Boolean) {
/* 438 */       this.map = param1TransactionMap;
/* 439 */       this.iterator = param1Iterator;
/* 440 */       this.includeUncommitted = param1Boolean;
/* 441 */       fetchNext();
/*     */     }
/*     */     
/*     */     private void fetchNext() {
/* 445 */       while (this.iterator.hasNext()) {
/* 446 */         this.current = this.iterator.next();
/* 447 */         if (this.includeUncommitted || this.map.containsKey(this.current)) {
/*     */           return;
/*     */         }
/*     */       } 
/* 451 */       this.current = null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 456 */       return (this.current != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public Spatial next() {
/* 461 */       Spatial spatial = this.current;
/* 462 */       fetchNext();
/* 463 */       return spatial;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private final class FindBoundsCursor
/*     */     extends MVRTreeMap.RTreeCursor<VersionedValue<Value>>
/*     */   {
/*     */     private final SessionLocal session;
/*     */     
/*     */     private final TransactionMap<Spatial, Value> map;
/*     */     private final int columnId;
/*     */     private boolean hasBounds;
/*     */     private float bminxf;
/*     */     private float bmaxxf;
/*     */     private float bminyf;
/*     */     private float bmaxyf;
/*     */     private double bminxd;
/*     */     private double bmaxxd;
/*     */     private double bminyd;
/*     */     private double bmaxyd;
/*     */     
/*     */     FindBoundsCursor(Page<Spatial, VersionedValue<Value>> param1Page, Spatial param1Spatial, SessionLocal param1SessionLocal, TransactionMap<Spatial, Value> param1TransactionMap, int param1Int) {
/* 486 */       super(param1Page, param1Spatial);
/* 487 */       this.session = param1SessionLocal;
/* 488 */       this.map = param1TransactionMap;
/* 489 */       this.columnId = param1Int;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean check(boolean param1Boolean, Spatial param1Spatial1, Spatial param1Spatial2) {
/* 494 */       float f1 = param1Spatial1.min(0), f2 = param1Spatial1.max(0), f3 = param1Spatial1.min(1), f4 = param1Spatial1.max(1);
/* 495 */       if (param1Boolean) {
/* 496 */         if (this.hasBounds) {
/* 497 */           if ((f1 <= this.bminxf || f2 >= this.bmaxxf || f3 <= this.bminyf || f4 >= this.bmaxyf) && this.map
/* 498 */             .containsKey(param1Spatial1)) {
/*     */             
/* 500 */             double[] arrayOfDouble = ((ValueGeometry)MVSpatialIndex.this.mvTable.getRow(this.session, param1Spatial1.getId()).getValue(this.columnId)).getEnvelopeNoCopy();
/* 501 */             double d1 = arrayOfDouble[0], d2 = arrayOfDouble[1], d3 = arrayOfDouble[2], d4 = arrayOfDouble[3];
/* 502 */             if (d1 < this.bminxd) {
/* 503 */               this.bminxf = f1;
/* 504 */               this.bminxd = d1;
/*     */             } 
/* 506 */             if (d2 > this.bmaxxd) {
/* 507 */               this.bmaxxf = f2;
/* 508 */               this.bmaxxd = d2;
/*     */             } 
/* 510 */             if (d3 < this.bminyd) {
/* 511 */               this.bminyf = f3;
/* 512 */               this.bminyd = d3;
/*     */             } 
/* 514 */             if (d4 > this.bmaxyd) {
/* 515 */               this.bmaxyf = f4;
/* 516 */               this.bmaxyd = d4;
/*     */             } 
/*     */           } 
/* 519 */         } else if (this.map.containsKey(param1Spatial1)) {
/* 520 */           this.hasBounds = true;
/*     */           
/* 522 */           double[] arrayOfDouble = ((ValueGeometry)MVSpatialIndex.this.mvTable.getRow(this.session, param1Spatial1.getId()).getValue(this.columnId)).getEnvelopeNoCopy();
/* 523 */           this.bminxf = f1;
/* 524 */           this.bminxd = arrayOfDouble[0];
/* 525 */           this.bmaxxf = f2;
/* 526 */           this.bmaxxd = arrayOfDouble[1];
/* 527 */           this.bminyf = f3;
/* 528 */           this.bminyd = arrayOfDouble[2];
/* 529 */           this.bmaxyf = f4;
/* 530 */           this.bmaxyd = arrayOfDouble[3];
/*     */         } 
/* 532 */       } else if (this.hasBounds) {
/* 533 */         if (f1 <= this.bminxf || f2 >= this.bmaxxf || f3 <= this.bminyf || f4 >= this.bmaxyf) {
/* 534 */           return true;
/*     */         }
/*     */       } else {
/* 537 */         return true;
/*     */       } 
/* 539 */       return false;
/*     */     }
/*     */     
/*     */     Value getBounds() {
/* 543 */       return this.hasBounds ? ValueGeometry.fromEnvelope(new double[] { this.bminxd, this.bmaxxd, this.bminyd, this.bmaxyd }) : (Value)ValueNull.INSTANCE;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVSpatialIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */