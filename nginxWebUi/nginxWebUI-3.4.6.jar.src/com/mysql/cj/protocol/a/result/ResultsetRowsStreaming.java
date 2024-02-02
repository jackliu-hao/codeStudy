/*     */ package com.mysql.cj.protocol.a.result;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.exceptions.StreamingNotifiable;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import com.mysql.cj.protocol.ResultsetRow;
/*     */ import com.mysql.cj.protocol.ResultsetRows;
/*     */ import com.mysql.cj.protocol.a.BinaryRowFactory;
/*     */ import com.mysql.cj.protocol.a.NativeMessageBuilder;
/*     */ import com.mysql.cj.protocol.a.NativePacketPayload;
/*     */ import com.mysql.cj.protocol.a.NativeProtocol;
/*     */ import com.mysql.cj.protocol.a.TextRowFactory;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.util.Util;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResultsetRowsStreaming<T extends ProtocolEntity>
/*     */   extends AbstractResultsetRows
/*     */   implements ResultsetRows
/*     */ {
/*     */   private NativeProtocol protocol;
/*     */   private boolean isAfterEnd = false;
/*     */   private boolean noMoreRows = false;
/*     */   private boolean isBinaryEncoded = false;
/*     */   private Row nextRow;
/*     */   private boolean streamerClosed = false;
/*     */   private ExceptionInterceptor exceptionInterceptor;
/*     */   private ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory;
/*  80 */   private NativeMessageBuilder commandBuilder = null;
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
/*     */   public ResultsetRowsStreaming(NativeProtocol io, ColumnDefinition columnDefinition, boolean isBinaryEncoded, ProtocolEntityFactory<T, NativePacketPayload> resultSetFactory) {
/*  96 */     this.protocol = io;
/*  97 */     this.isBinaryEncoded = isBinaryEncoded;
/*  98 */     this.metadata = columnDefinition;
/*  99 */     this.exceptionInterceptor = this.protocol.getExceptionInterceptor();
/* 100 */     this.resultSetFactory = resultSetFactory;
/* 101 */     this.rowFactory = this.isBinaryEncoded ? (ProtocolEntityFactory<ResultsetRow, NativePacketPayload>)new BinaryRowFactory(this.protocol, this.metadata, Resultset.Concurrency.READ_ONLY, true) : (ProtocolEntityFactory<ResultsetRow, NativePacketPayload>)new TextRowFactory(this.protocol, this.metadata, Resultset.Concurrency.READ_ONLY, true);
/*     */     
/* 103 */     this.commandBuilder = new NativeMessageBuilder(this.protocol.getServerSession().supportsQueryAttributes());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 109 */     Object mutex = (this.owner != null && this.owner.getSyncMutex() != null) ? this.owner.getSyncMutex() : this;
/*     */     
/* 111 */     boolean hadMore = false;
/* 112 */     int howMuchMore = 0;
/*     */     
/* 114 */     synchronized (mutex) {
/*     */       
/* 116 */       while (next() != null) {
/* 117 */         hadMore = true;
/* 118 */         howMuchMore++;
/*     */         
/* 120 */         if (howMuchMore % 100 == 0) {
/* 121 */           Thread.yield();
/*     */         }
/*     */       } 
/*     */       
/* 125 */       if (!((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.clobberStreamingResults).getValue()).booleanValue() && ((Integer)this.protocol
/* 126 */         .getPropertySet().getIntegerProperty(PropertyKey.netTimeoutForStreamingResults).getValue()).intValue() > 0) {
/* 127 */         int oldValue = this.protocol.getServerSession().getServerVariable("net_write_timeout", 60);
/*     */         
/* 129 */         this.protocol.clearInputStream();
/*     */         
/*     */         try {
/* 132 */           this.protocol.sendCommand((Message)this.commandBuilder.buildComQuery(this.protocol.getSharedSendPacket(), "SET net_write_timeout=" + oldValue, (String)this.protocol
/* 133 */                 .getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue()), false, 0);
/* 134 */         } catch (Exception ex) {
/* 135 */           throw ExceptionFactory.createException(ex.getMessage(), ex, this.exceptionInterceptor);
/*     */         } 
/*     */       } 
/*     */       
/* 139 */       if (((Boolean)this.protocol.getPropertySet().getBooleanProperty(PropertyKey.useUsageAdvisor).getValue()).booleanValue() && 
/* 140 */         hadMore) {
/* 141 */         this.owner.getSession().getProfilerEventHandler().processEvent((byte)0, this.owner.getSession(), this.owner
/* 142 */             .getOwningQuery(), null, 0L, new Throwable(), 
/* 143 */             Messages.getString("RowDataDynamic.1", (Object[])new String[] { String.valueOf(howMuchMore), this.owner.getPointOfOrigin() }));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 148 */     this.metadata = null;
/* 149 */     this.owner = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 154 */     boolean hasNext = (this.nextRow != null);
/*     */     
/* 156 */     if (!hasNext && !this.streamerClosed) {
/* 157 */       this.protocol.unsetStreamingData(this);
/* 158 */       this.streamerClosed = true;
/*     */     } 
/*     */     
/* 161 */     return hasNext;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 166 */     return this.isAfterEnd;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isBeforeFirst() {
/* 171 */     return (this.currentPositionInFetchedRows < 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 176 */     return this.wasEmpty;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFirst() {
/* 181 */     return (this.currentPositionInFetchedRows == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLast() {
/* 186 */     return (!isBeforeFirst() && !isAfterLast() && this.noMoreRows);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Row next() {
/*     */     try {
/* 193 */       if (!this.noMoreRows) {
/* 194 */         this.nextRow = (Row)this.protocol.read(ResultsetRow.class, this.rowFactory);
/*     */         
/* 196 */         if (this.nextRow == null) {
/* 197 */           this.noMoreRows = true;
/* 198 */           this.isAfterEnd = true;
/*     */           
/* 200 */           if (this.currentPositionInFetchedRows == -1) {
/* 201 */             this.wasEmpty = true;
/*     */           }
/*     */         } 
/*     */       } else {
/* 205 */         this.nextRow = null;
/* 206 */         this.isAfterEnd = true;
/*     */       } 
/*     */       
/* 209 */       if (this.nextRow == null && !this.streamerClosed) {
/* 210 */         if (this.protocol.getServerSession().hasMoreResults()) {
/* 211 */           this.protocol.readNextResultset((ProtocolEntity)this.owner, this.owner.getOwningStatementMaxRows(), true, this.isBinaryEncoded, this.resultSetFactory);
/*     */         } else {
/*     */           
/* 214 */           this.protocol.unsetStreamingData(this);
/* 215 */           this.streamerClosed = true;
/*     */         } 
/*     */       }
/*     */       
/* 219 */       if (this.nextRow != null && 
/* 220 */         this.currentPositionInFetchedRows != Integer.MAX_VALUE) {
/* 221 */         this.currentPositionInFetchedRows++;
/*     */       }
/*     */ 
/*     */       
/* 225 */       return this.nextRow;
/*     */     }
/* 227 */     catch (CJException sqlEx) {
/*     */       
/* 229 */       if (sqlEx instanceof StreamingNotifiable) {
/* 230 */         ((StreamingNotifiable)sqlEx).setWasStreamingResults();
/*     */       }
/*     */ 
/*     */       
/* 234 */       this.noMoreRows = true;
/*     */ 
/*     */       
/* 237 */       throw sqlEx;
/* 238 */     } catch (Exception ex) {
/* 239 */       CJException cjEx = ExceptionFactory.createException(
/* 240 */           Messages.getString("RowDataDynamic.2", (Object[])new String[] { ex.getClass().getName(), ex.getMessage(), Util.stackTraceToString(ex) }), ex, this.exceptionInterceptor);
/*     */ 
/*     */       
/* 243 */       throw cjEx;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getPosition() {
/* 248 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void afterLast() {
/* 252 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void beforeFirst() {
/* 256 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void beforeLast() {
/* 260 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void moveRowRelative(int rows) {
/* 264 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */   
/*     */   public void setCurrentRow(int rowNumber) {
/* 268 */     throw ExceptionFactory.createException(Messages.getString("ResultSet.ForwardOnly"));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\result\ResultsetRowsStreaming.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */