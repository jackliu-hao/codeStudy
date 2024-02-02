/*     */ package org.h2.result;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionRemote;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.value.Transfer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ResultRemote
/*     */   extends FetchedResult
/*     */ {
/*     */   private int fetchSize;
/*     */   private SessionRemote session;
/*     */   private Transfer transfer;
/*     */   private int id;
/*     */   private final ResultColumn[] columns;
/*     */   private long rowCount;
/*     */   private long rowOffset;
/*     */   private ArrayList<Value[]> result;
/*     */   private final Trace trace;
/*     */   
/*     */   public ResultRemote(SessionRemote paramSessionRemote, Transfer paramTransfer, int paramInt1, int paramInt2, int paramInt3) throws IOException {
/*  39 */     this.session = paramSessionRemote;
/*  40 */     this.trace = paramSessionRemote.getTrace();
/*  41 */     this.transfer = paramTransfer;
/*  42 */     this.id = paramInt1;
/*  43 */     this.columns = new ResultColumn[paramInt2];
/*  44 */     this.rowCount = paramTransfer.readRowCount();
/*  45 */     for (byte b = 0; b < paramInt2; b++) {
/*  46 */       this.columns[b] = new ResultColumn(paramTransfer);
/*     */     }
/*  48 */     this.rowId = -1L;
/*  49 */     this.fetchSize = paramInt3;
/*  50 */     if (this.rowCount >= 0L) {
/*  51 */       paramInt3 = (int)Math.min(this.rowCount, paramInt3);
/*  52 */       this.result = (ArrayList)new ArrayList<>(paramInt3);
/*     */     } else {
/*  54 */       this.result = (ArrayList)new ArrayList<>();
/*     */     } 
/*  56 */     synchronized (paramSessionRemote) {
/*     */       try {
/*  58 */         if (fetchRows(paramInt3)) {
/*  59 */           this.rowCount = this.result.size();
/*     */         }
/*  61 */       } catch (IOException iOException) {
/*  62 */         throw DbException.convertIOException(iOException, null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLazy() {
/*  69 */     return (this.rowCount < 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlias(int paramInt) {
/*  74 */     return (this.columns[paramInt]).alias;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName(int paramInt) {
/*  79 */     return (this.columns[paramInt]).schemaName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName(int paramInt) {
/*  84 */     return (this.columns[paramInt]).tableName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int paramInt) {
/*  89 */     return (this.columns[paramInt]).columnName;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getColumnType(int paramInt) {
/*  94 */     return (this.columns[paramInt]).columnType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity(int paramInt) {
/*  99 */     return (this.columns[paramInt]).identity;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable(int paramInt) {
/* 104 */     return (this.columns[paramInt]).nullable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/* 109 */     if (this.rowCount < 0L || this.rowOffset > 0L) {
/* 110 */       throw DbException.get(90128);
/*     */     }
/* 112 */     this.rowId = -1L;
/* 113 */     this.currentRow = null;
/* 114 */     this.nextRow = null;
/* 115 */     this.afterLast = false;
/* 116 */     if (this.session == null) {
/*     */       return;
/*     */     }
/* 119 */     synchronized (this.session) {
/* 120 */       this.session.checkClosed();
/*     */       try {
/* 122 */         this.session.traceOperation("RESULT_RESET", this.id);
/* 123 */         this.transfer.writeInt(6).writeInt(this.id).flush();
/* 124 */       } catch (IOException iOException) {
/* 125 */         throw DbException.convertIOException(iOException, null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVisibleColumnCount() {
/* 132 */     return this.columns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount() {
/* 137 */     if (this.rowCount < 0L) {
/* 138 */       throw DbException.getUnsupportedException("Row count is unknown for lazy result.");
/*     */     }
/* 140 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 145 */     if (this.afterLast) {
/* 146 */       return false;
/*     */     }
/* 148 */     if (this.nextRow == null && (
/* 149 */       this.rowCount < 0L || this.rowId < this.rowCount - 1L)) {
/* 150 */       long l = this.rowId + 1L;
/* 151 */       if (this.session != null) {
/* 152 */         remapIfOld();
/* 153 */         if (l - this.rowOffset >= this.result.size()) {
/* 154 */           fetchAdditionalRows();
/*     */         }
/*     */       } 
/* 157 */       int i = (int)(l - this.rowOffset);
/* 158 */       this.nextRow = (i < this.result.size()) ? this.result.get(i) : null;
/*     */     } 
/*     */     
/* 161 */     return (this.nextRow != null);
/*     */   }
/*     */   
/*     */   private void sendClose() {
/* 165 */     if (this.session == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 170 */       synchronized (this.session) {
/* 171 */         this.session.traceOperation("RESULT_CLOSE", this.id);
/* 172 */         this.transfer.writeInt(7).writeInt(this.id);
/*     */       } 
/* 174 */     } catch (IOException iOException) {
/* 175 */       this.trace.error(iOException, "close");
/*     */     } finally {
/* 177 */       this.transfer = null;
/* 178 */       this.session = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 184 */     this.result = null;
/* 185 */     sendClose();
/*     */   }
/*     */   
/*     */   private void remapIfOld() {
/*     */     try {
/* 190 */       if (this.id <= this.session.getCurrentId() - SysProperties.SERVER_CACHED_OBJECTS / 2)
/*     */       {
/* 192 */         int i = this.session.getNextId();
/* 193 */         this.session.traceOperation("CHANGE_ID", this.id);
/* 194 */         this.transfer.writeInt(9).writeInt(this.id).writeInt(i);
/* 195 */         this.id = i;
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 200 */     catch (IOException iOException) {
/* 201 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void fetchAdditionalRows() {
/* 206 */     synchronized (this.session) {
/* 207 */       this.session.checkClosed();
/*     */       try {
/* 209 */         this.rowOffset += this.result.size();
/* 210 */         this.result.clear();
/* 211 */         int i = this.fetchSize;
/* 212 */         if (this.rowCount >= 0L) {
/* 213 */           i = (int)Math.min(i, this.rowCount - this.rowOffset);
/* 214 */         } else if (i == Integer.MAX_VALUE) {
/* 215 */           i = SysProperties.SERVER_RESULT_SET_FETCH_SIZE;
/*     */         } 
/* 217 */         this.session.traceOperation("RESULT_FETCH_ROWS", this.id);
/* 218 */         this.transfer.writeInt(5).writeInt(this.id).writeInt(i);
/* 219 */         this.session.done(this.transfer);
/* 220 */         fetchRows(i);
/* 221 */       } catch (IOException iOException) {
/* 222 */         throw DbException.convertIOException(iOException, null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean fetchRows(int paramInt) throws IOException {
/* 228 */     int i = this.columns.length;
/* 229 */     for (byte b = 0; b < paramInt; b++) {
/* 230 */       Value[] arrayOfValue; byte b1; switch (this.transfer.readByte()) {
/*     */         case 1:
/* 232 */           arrayOfValue = new Value[i];
/* 233 */           for (b1 = 0; b1 < i; b1++) {
/* 234 */             arrayOfValue[b1] = this.transfer.readValue((this.columns[b1]).columnType);
/*     */           }
/* 236 */           this.result.add(arrayOfValue);
/*     */           break;
/*     */         
/*     */         case 0:
/* 240 */           sendClose();
/* 241 */           return true;
/*     */         case -1:
/* 243 */           throw SessionRemote.readException(this.transfer);
/*     */         default:
/* 245 */           throw DbException.getInternalError();
/*     */       } 
/*     */     } 
/* 248 */     if (this.rowCount >= 0L && this.rowOffset + this.result.size() >= this.rowCount) {
/* 249 */       sendClose();
/*     */     }
/* 251 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 256 */     return "columns: " + this.columns.length + ((this.rowCount < 0L) ? " lazy" : (" rows: " + this.rowCount)) + " pos: " + this.rowId;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/* 261 */     return this.fetchSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFetchSize(int paramInt) {
/* 266 */     this.fetchSize = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 271 */     return (this.result == null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultRemote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */