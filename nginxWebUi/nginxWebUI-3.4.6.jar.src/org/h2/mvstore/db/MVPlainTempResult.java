/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.Cursor;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.LongDataType;
/*     */ import org.h2.result.ResultExternal;
/*     */ import org.h2.result.RowFactory;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.value.Typed;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueRow;
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
/*     */ class MVPlainTempResult
/*     */   extends MVTempResult
/*     */ {
/*     */   private final MVMap<Long, ValueRow> map;
/*     */   private long counter;
/*     */   private Cursor<Long, ValueRow> cursor;
/*     */   
/*     */   private MVPlainTempResult(MVPlainTempResult paramMVPlainTempResult) {
/*  49 */     super(paramMVPlainTempResult);
/*  50 */     this.map = paramMVPlainTempResult.map;
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
/*     */   MVPlainTempResult(Database paramDatabase, Expression[] paramArrayOfExpression, int paramInt1, int paramInt2) {
/*  69 */     super(paramDatabase, paramArrayOfExpression, paramInt1, paramInt2);
/*  70 */     ValueDataType valueDataType = new ValueDataType(paramDatabase, new int[paramInt2]);
/*  71 */     valueDataType.setRowFactory(RowFactory.DefaultRowFactory.INSTANCE.createRowFactory((CastDataProvider)paramDatabase, paramDatabase.getCompareMode(), (DataHandler)paramDatabase, (Typed[])paramArrayOfExpression, null, false));
/*     */ 
/*     */     
/*  74 */     MVMap.Builder builder = (new MVMap.Builder()).keyType((DataType)LongDataType.INSTANCE).valueType((DataType)valueDataType).singleWriter();
/*  75 */     this.map = this.store.openMap("tmp", (MVMap.MapBuilder)builder);
/*     */   }
/*     */ 
/*     */   
/*     */   public int addRow(Value[] paramArrayOfValue) {
/*  80 */     assert this.parent == null;
/*  81 */     this.map.append(Long.valueOf(this.counter++), ValueRow.get(paramArrayOfValue));
/*  82 */     return ++this.rowCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Value[] paramArrayOfValue) {
/*  87 */     throw DbException.getUnsupportedException("contains()");
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized ResultExternal createShallowCopy() {
/*  92 */     if (this.parent != null) {
/*  93 */       return this.parent.createShallowCopy();
/*     */     }
/*  95 */     if (this.closed) {
/*  96 */       return null;
/*     */     }
/*  98 */     this.childCount++;
/*  99 */     return new MVPlainTempResult(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] next() {
/* 104 */     if (this.cursor == null) {
/* 105 */       this.cursor = this.map.cursor(null);
/*     */     }
/* 107 */     if (!this.cursor.hasNext()) {
/* 108 */       return null;
/*     */     }
/* 110 */     this.cursor.next();
/* 111 */     return ((ValueRow)this.cursor.getValue()).getList();
/*     */   }
/*     */ 
/*     */   
/*     */   public int removeRow(Value[] paramArrayOfValue) {
/* 116 */     throw DbException.getUnsupportedException("removeRow()");
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 121 */     this.cursor = null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVPlainTempResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */