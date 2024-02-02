/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ResultsetRow;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.protocol.a.BinaryRowFactory;
/*     */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.NativeProtocol;
/*     */ import com.mysql.cj.result.Row;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResultsetRowsCursor
/*     */   extends AbstractResultsetRows
/*     */   implements ResultsetRows
/*     */ {
/*     */   private List<Row> fetchedRows;
/*  62 */   private int currentPositionInEntireResult = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean lastRowFetched = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private NativeProtocol protocol;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean firstFetchCompleted = false;
/*     */ 
/*     */ 
/*     */   
/*  79 */   protected NativeMessageBuilder commandBuilder = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultsetRowsCursor(NativeProtocol ioChannel, ColumnDefinition columnDefinition) {
/*  90 */     this.currentPositionInEntireResult = -1;
/*  91 */     this.metadata = columnDefinition;
/*  92 */     this.protocol = ioChannel;
/*  93 */     this.rowFactory = (ProtocolEntityFactory<ResultsetRow, NativePacketPayload>)new BinaryRowFactory(this.protocol, this.metadata, Resultset.Concurrency.READ_ONLY, false);
/*  94 */     this.commandBuilder = new NativeMessageBuilder(this.protocol.getServerSession().supportsQueryAttributes());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/*  99 */     return (this.lastRowFetched && this.currentPositionInFetchedRows > this.fetchedRows.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBeforeFirst() {
/* 104 */     return (this.currentPositionInEntireResult < 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPosition() {
/* 109 */     return this.currentPositionInEntireResult + 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 114 */     return (isBeforeFirst() && isAfterLast());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirst() {
/* 119 */     return (this.currentPositionInEntireResult == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLast() {
/* 124 */     return (this.lastRowFetched && this.currentPositionInFetchedRows == this.fetchedRows.size() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 130 */     this.metadata = null;
/* 131 */     this.owner = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 137 */     if (this.fetchedRows != null && this.fetchedRows.size() == 0) {
/* 138 */       return false;
/*     */     }
/*     */     
/* 141 */     if (this.owner != null) {
/* 142 */       int maxRows = this.owner.getOwningStatementMaxRows();
/*     */       
/* 144 */       if (maxRows != -1 && this.currentPositionInEntireResult + 1 > maxRows) {
/* 145 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 149 */     if (this.currentPositionInEntireResult != -1) {
/*     */       
/* 151 */       if (this.currentPositionInFetchedRows < this.fetchedRows.size() - 1)
/* 152 */         return true; 
/* 153 */       if (this.currentPositionInFetchedRows == this.fetchedRows.size() && this.lastRowFetched) {
/* 154 */         return false;
/*     */       }
/*     */       
/* 157 */       fetchMoreRows();
/*     */       
/* 159 */       return (this.fetchedRows.size() > 0);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 165 */     fetchMoreRows();
/*     */     
/* 167 */     return (this.fetchedRows.size() > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public Row next() {
/* 172 */     if (this.fetchedRows == null && this.currentPositionInEntireResult != -1) {
/* 173 */       throw ExceptionFactory.createException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), this.protocol
/* 174 */           .getExceptionInterceptor());
/*     */     }
/*     */     
/* 177 */     if (!hasNext()) {
/* 178 */       return null;
/*     */     }
/*     */     
/* 181 */     this.currentPositionInEntireResult++;
/* 182 */     this.currentPositionInFetchedRows++;
/*     */ 
/*     */     
/* 185 */     if (this.fetchedRows != null && this.fetchedRows.size() == 0) {
/* 186 */       return null;
/*     */     }
/*     */     
/* 189 */     if (this.fetchedRows == null || this.currentPositionInFetchedRows > this.fetchedRows.size() - 1) {
/* 190 */       fetchMoreRows();
/* 191 */       this.currentPositionInFetchedRows = 0;
/*     */     } 
/*     */     
/* 194 */     Row row = this.fetchedRows.get(this.currentPositionInFetchedRows);
/*     */     
/* 196 */     row.setMetadata(this.metadata);
/*     */     
/* 198 */     return row;
/*     */   }
/*     */   
/*     */   private void fetchMoreRows() {
/* 202 */     if (this.lastRowFetched) {
/* 203 */       this.fetchedRows = new ArrayList<>(0);
/*     */       
/*     */       return;
/*     */     } 
/* 207 */     synchronized (this.owner.getSyncMutex()) {
/*     */       try {
/* 209 */         boolean oldFirstFetchCompleted = this.firstFetchCompleted;
/*     */         
/* 211 */         if (!this.firstFetchCompleted) {
/* 212 */           this.firstFetchCompleted = true;
/*     */         }
/*     */         
/* 215 */         int numRowsToFetch = this.owner.getOwnerFetchSize();
/*     */         
/* 217 */         if (numRowsToFetch == 0) {
/* 218 */           numRowsToFetch = this.owner.getOwningStatementFetchSize();
/*     */         }
/*     */         
/* 221 */         if (numRowsToFetch == Integer.MIN_VALUE)
/*     */         {
/*     */           
/* 224 */           numRowsToFetch = 1;
/*     */         }
/*     */         
/* 227 */         if (this.fetchedRows == null) {
/* 228 */           this.fetchedRows = new ArrayList<>(numRowsToFetch);
/*     */         } else {
/* 230 */           this.fetchedRows.clear();
/*     */         } 
/*     */ 
/*     */         
/* 234 */         this.protocol.sendCommand((Message)this.commandBuilder
/* 235 */             .buildComStmtFetch(this.protocol.getSharedSendPacket(), this.owner.getOwningStatementServerId(), numRowsToFetch), true, 0);
/*     */ 
/*     */         
/* 238 */         Row row = null;
/*     */         
/* 240 */         while ((row = (Row)this.protocol.read(ResultsetRow.class, this.rowFactory)) != null) {
/* 241 */           this.fetchedRows.add(row);
/*     */         }
/*     */         
/* 244 */         this.currentPositionInFetchedRows = -1;
/*     */         
/* 246 */         if (this.protocol.getServerSession().isLastRowSent()) {
/* 247 */           this.lastRowFetched = true;
/*     */           
/* 249 */           if (!oldFirstFetchCompleted && this.fetchedRows.size() == 0) {
/* 250 */             this.wasEmpty = true;
/*     */           }
/*     */         } 
/* 253 */       } catch (Exception ex) {
/* 254 */         throw ExceptionFactory.createException(ex.getMessage(), ex);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRow(Row row) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void afterLast() {
/* 266 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void beforeFirst() {
/* 270 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void beforeLast() {
/* 274 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void moveRowRelative(int rows) {
/* 278 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void setCurrentRow(int rowNumber) {
/* 282 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\ResultsetRowsCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */