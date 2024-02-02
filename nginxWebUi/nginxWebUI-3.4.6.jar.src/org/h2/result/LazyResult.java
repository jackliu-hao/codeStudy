/*     */ package org.h2.result;
/*     */ 
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.value.TypeInfo;
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
/*     */ public abstract class LazyResult
/*     */   extends FetchedResult
/*     */ {
/*     */   private final SessionLocal session;
/*     */   private final Expression[] expressions;
/*     */   private boolean closed;
/*     */   private long limit;
/*     */   
/*     */   public LazyResult(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression) {
/*  27 */     this.session = paramSessionLocal;
/*  28 */     this.expressions = paramArrayOfExpression;
/*     */   }
/*     */   
/*     */   public void setLimit(long paramLong) {
/*  32 */     this.limit = paramLong;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLazy() {
/*  37 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  42 */     if (this.closed) {
/*  43 */       throw DbException.getInternalError();
/*     */     }
/*  45 */     this.rowId = -1L;
/*  46 */     this.afterLast = false;
/*  47 */     this.currentRow = null;
/*  48 */     this.nextRow = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean skip() {
/*  57 */     if (this.closed || this.afterLast) {
/*  58 */       return false;
/*     */     }
/*  60 */     this.currentRow = null;
/*  61 */     if (this.nextRow != null) {
/*  62 */       this.nextRow = null;
/*  63 */       return true;
/*     */     } 
/*  65 */     if (skipNextRow()) {
/*  66 */       return true;
/*     */     }
/*  68 */     this.afterLast = true;
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/*  74 */     if (this.closed || this.afterLast) {
/*  75 */       return false;
/*     */     }
/*  77 */     if (this.nextRow == null && (this.limit <= 0L || this.rowId + 1L < this.limit)) {
/*  78 */       this.nextRow = fetchNextRow();
/*     */     }
/*  80 */     return (this.nextRow != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract Value[] fetchNextRow();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean skipNextRow() {
/*  96 */     return (fetchNextRow() != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount() {
/* 101 */     throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 106 */     return this.closed;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 111 */     this.closed = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlias(int paramInt) {
/* 116 */     return this.expressions[paramInt].getAlias(this.session, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName(int paramInt) {
/* 121 */     return this.expressions[paramInt].getSchemaName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName(int paramInt) {
/* 126 */     return this.expressions[paramInt].getTableName();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int paramInt) {
/* 131 */     return this.expressions[paramInt].getColumnName(this.session, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getColumnType(int paramInt) {
/* 136 */     return this.expressions[paramInt].getType();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity(int paramInt) {
/* 141 */     return this.expressions[paramInt].isIdentity();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable(int paramInt) {
/* 146 */     return this.expressions[paramInt].getNullable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFetchSize(int paramInt) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/* 157 */     return 1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\LazyResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */