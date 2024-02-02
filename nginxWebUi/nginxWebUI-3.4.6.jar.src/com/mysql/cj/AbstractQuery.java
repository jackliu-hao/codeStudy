/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertyKey;
/*     */ import com.mysql.cj.conf.RuntimeProperty;
/*     */ import com.mysql.cj.exceptions.CJException;
/*     */ import com.mysql.cj.exceptions.CJTimeoutException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.OperationCancelledException;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.Resultset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*     */ public abstract class AbstractQuery
/*     */   implements Query
/*     */ {
/*  52 */   static int statementCounter = 1;
/*     */   
/*  54 */   public NativeSession session = null;
/*     */ 
/*     */   
/*     */   protected int statementId;
/*     */ 
/*     */   
/*     */   protected RuntimeProperty<Integer> maxAllowedPacket;
/*     */   
/*  62 */   protected String charEncoding = null;
/*     */ 
/*     */   
/*  65 */   protected Object cancelTimeoutMutex = new Object();
/*     */   
/*  67 */   private Query.CancelStatus cancelStatus = Query.CancelStatus.NOT_CANCELED;
/*     */ 
/*     */   
/*  70 */   protected int timeoutInMillis = 0;
/*     */ 
/*     */   
/*     */   protected List<Object> batchedArgs;
/*     */ 
/*     */   
/*  76 */   protected Resultset.Type resultSetType = Resultset.Type.FORWARD_ONLY;
/*     */ 
/*     */   
/*  79 */   protected int fetchSize = 0;
/*     */ 
/*     */   
/*  82 */   protected final AtomicBoolean statementExecuting = new AtomicBoolean(false);
/*     */ 
/*     */   
/*  85 */   protected String currentDb = null;
/*     */ 
/*     */   
/*     */   protected boolean clearWarningsCalled = false;
/*     */ 
/*     */   
/*  91 */   private long executeTime = -1L;
/*     */   
/*     */   protected QueryAttributesBindings queryAttributesBindings;
/*     */ 
/*     */   
/*     */   public AbstractQuery(NativeSession sess) {
/*  97 */     statementCounter++;
/*  98 */     this.session = sess;
/*  99 */     this.maxAllowedPacket = sess.getPropertySet().getIntegerProperty(PropertyKey.maxAllowedPacket);
/* 100 */     this.charEncoding = (String)sess.getPropertySet().getStringProperty(PropertyKey.characterEncoding).getValue();
/* 101 */     this.queryAttributesBindings = new NativeQueryAttributesBindings();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getId() {
/* 106 */     return this.statementId;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCancelStatus(Query.CancelStatus cs) {
/* 111 */     this.cancelStatus = cs;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getExecuteTime() {
/* 116 */     return this.executeTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setExecuteTime(long executeTime) {
/* 121 */     this.executeTime = executeTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkCancelTimeout() {
/* 126 */     synchronized (this.cancelTimeoutMutex) {
/* 127 */       if (this.cancelStatus != Query.CancelStatus.NOT_CANCELED) {
/* 128 */         CJException cause = (this.cancelStatus == Query.CancelStatus.CANCELED_BY_TIMEOUT) ? (CJException)new CJTimeoutException() : (CJException)new OperationCancelledException();
/* 129 */         resetCancelledState();
/* 130 */         throw cause;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void resetCancelledState() {
/* 136 */     synchronized (this.cancelTimeoutMutex) {
/* 137 */       this.cancelStatus = Query.CancelStatus.NOT_CANCELED;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends Resultset, M extends com.mysql.cj.protocol.Message> ProtocolEntityFactory<T, M> getResultSetFactory() {
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public NativeSession getSession() {
/* 149 */     return this.session;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCancelTimeoutMutex() {
/* 154 */     return this.cancelTimeoutMutex;
/*     */   }
/*     */   
/*     */   public void closeQuery() {
/* 158 */     this.session = null;
/*     */   }
/*     */   
/*     */   public void addBatch(Object batch) {
/* 162 */     if (this.batchedArgs == null) {
/* 163 */       this.batchedArgs = new ArrayList();
/*     */     }
/* 165 */     this.batchedArgs.add(batch);
/*     */   }
/*     */   
/*     */   public List<Object> getBatchedArgs() {
/* 169 */     return (this.batchedArgs == null) ? null : Collections.<Object>unmodifiableList(this.batchedArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearBatchedArgs() {
/* 174 */     if (this.batchedArgs != null) {
/* 175 */       this.batchedArgs.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public QueryAttributesBindings getQueryAttributesBindings() {
/* 181 */     return this.queryAttributesBindings;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResultFetchSize() {
/* 186 */     return this.fetchSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResultFetchSize(int fetchSize) {
/* 191 */     this.fetchSize = fetchSize;
/*     */   }
/*     */   
/*     */   public Resultset.Type getResultType() {
/* 195 */     return this.resultSetType;
/*     */   }
/*     */   
/*     */   public void setResultType(Resultset.Type resultSetType) {
/* 199 */     this.resultSetType = resultSetType;
/*     */   }
/*     */   
/*     */   public int getTimeoutInMillis() {
/* 203 */     return this.timeoutInMillis;
/*     */   }
/*     */   
/*     */   public void setTimeoutInMillis(int timeoutInMillis) {
/* 207 */     this.timeoutInMillis = timeoutInMillis;
/*     */   }
/*     */   
/*     */   public CancelQueryTask startQueryTimer(Query stmtToCancel, int timeout) {
/* 211 */     if (((Boolean)this.session.getPropertySet().getBooleanProperty(PropertyKey.enableQueryTimeouts).getValue()).booleanValue() && timeout != 0) {
/* 212 */       CancelQueryTaskImpl timeoutTask = new CancelQueryTaskImpl(stmtToCancel);
/* 213 */       this.session.getCancelTimer().schedule(timeoutTask, timeout);
/* 214 */       return timeoutTask;
/*     */     } 
/* 216 */     return null;
/*     */   }
/*     */   
/*     */   public void stopQueryTimer(CancelQueryTask timeoutTask, boolean rethrowCancelReason, boolean checkCancelTimeout) {
/* 220 */     if (timeoutTask != null) {
/* 221 */       timeoutTask.cancel();
/*     */       
/* 223 */       if (rethrowCancelReason && timeoutTask.getCaughtWhileCancelling() != null) {
/* 224 */         Throwable t = timeoutTask.getCaughtWhileCancelling();
/* 225 */         throw ExceptionFactory.createException(t.getMessage(), t);
/*     */       } 
/*     */       
/* 228 */       this.session.getCancelTimer().purge();
/*     */       
/* 230 */       if (checkCancelTimeout) {
/* 231 */         checkCancelTimeout();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public AtomicBoolean getStatementExecuting() {
/* 237 */     return this.statementExecuting;
/*     */   }
/*     */   
/*     */   public String getCurrentDatabase() {
/* 241 */     return this.currentDb;
/*     */   }
/*     */   
/*     */   public void setCurrentDatabase(String currentDb) {
/* 245 */     this.currentDb = currentDb;
/*     */   }
/*     */   
/*     */   public boolean isClearWarningsCalled() {
/* 249 */     return this.clearWarningsCalled;
/*     */   }
/*     */   
/*     */   public void setClearWarningsCalled(boolean clearWarningsCalled) {
/* 253 */     this.clearWarningsCalled = clearWarningsCalled;
/*     */   }
/*     */   
/*     */   public void statementBegins() {
/* 257 */     this.clearWarningsCalled = false;
/* 258 */     this.statementExecuting.set(true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\AbstractQuery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */