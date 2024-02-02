/*     */ package com.mysql.cj;
/*     */ 
/*     */ import com.mysql.cj.conf.HostInfo;
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.CJOperationNotSupportedException;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*     */ import com.mysql.cj.log.Log;
/*     */ import com.mysql.cj.log.ProfilerEventHandler;
/*     */ import com.mysql.cj.protocol.ResultBuilder;
/*     */ import com.mysql.cj.protocol.ServerSession;
/*     */ import com.mysql.cj.result.Row;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.stream.Collector;
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
/*     */ public interface Session
/*     */ {
/*     */   PropertySet getPropertySet();
/*     */   
/*     */   <M extends com.mysql.cj.protocol.Message> MessageBuilder<M> getMessageBuilder();
/*     */   
/*     */   void changeUser(String paramString1, String paramString2, String paramString3);
/*     */   
/*     */   ExceptionInterceptor getExceptionInterceptor();
/*     */   
/*     */   void setExceptionInterceptor(ExceptionInterceptor paramExceptionInterceptor);
/*     */   
/*     */   void quit();
/*     */   
/*     */   void forceClose();
/*     */   
/*     */   boolean versionMeetsMinimum(int paramInt1, int paramInt2, int paramInt3);
/*     */   
/*     */   long getThreadId();
/*     */   
/*     */   boolean isSetNeededForAutoCommitMode(boolean paramBoolean);
/*     */   
/*     */   Log getLog();
/*     */   
/*     */   ProfilerEventHandler getProfilerEventHandler();
/*     */   
/*     */   HostInfo getHostInfo();
/*     */   
/*     */   String getQueryTimingUnits();
/*     */   
/*     */   ServerSession getServerSession();
/*     */   
/*     */   boolean isSSLEstablished();
/*     */   
/*     */   SocketAddress getRemoteSocketAddress();
/*     */   
/*     */   String getProcessHost();
/*     */   
/*     */   void addListener(SessionEventListener paramSessionEventListener);
/*     */   
/*     */   void removeListener(SessionEventListener paramSessionEventListener);
/*     */   
/*     */   boolean isClosed();
/*     */   
/*     */   String getIdentifierQuoteString();
/*     */   
/*     */   DataStoreMetadata getDataStoreMetadata();
/*     */   
/*     */   default <M extends com.mysql.cj.protocol.Message, R, RES> RES query(M message, Predicate<Row> rowFilter, Function<Row, R> rowMapper, Collector<R, ?, RES> collector) {
/* 185 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
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
/*     */   default <M extends com.mysql.cj.protocol.Message, R extends QueryResult> R query(M message, ResultBuilder<R> resultBuilder) {
/* 202 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
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
/*     */   default <M extends com.mysql.cj.protocol.Message, R extends QueryResult> CompletableFuture<R> queryAsync(M message, ResultBuilder<R> resultBuilder) {
/* 219 */     throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
/*     */   }
/*     */   
/*     */   public static interface SessionEventListener {
/*     */     void handleNormalClose();
/*     */     
/*     */     void handleReconnect();
/*     */     
/*     */     void handleCleanup(Throwable param1Throwable);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */