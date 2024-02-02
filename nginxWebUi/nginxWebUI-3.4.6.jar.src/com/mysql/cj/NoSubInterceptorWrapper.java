/*    */ package com.mysql.cj;
/*    */ 
/*    */ import com.mysql.cj.interceptors.QueryInterceptor;
/*    */ import com.mysql.cj.log.Log;
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.protocol.Resultset;
/*    */ import com.mysql.cj.protocol.ServerSession;
/*    */ import java.util.Properties;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoSubInterceptorWrapper
/*    */   implements QueryInterceptor
/*    */ {
/*    */   private final QueryInterceptor underlyingInterceptor;
/*    */   
/*    */   public NoSubInterceptorWrapper(QueryInterceptor underlyingInterceptor) {
/* 49 */     if (underlyingInterceptor == null) {
/* 50 */       throw new RuntimeException(Messages.getString("NoSubInterceptorWrapper.0"));
/*    */     }
/*    */     
/* 53 */     this.underlyingInterceptor = underlyingInterceptor;
/*    */   }
/*    */   
/*    */   public void destroy() {
/* 57 */     this.underlyingInterceptor.destroy();
/*    */   }
/*    */   
/*    */   public boolean executeTopLevelOnly() {
/* 61 */     return this.underlyingInterceptor.executeTopLevelOnly();
/*    */   }
/*    */   
/*    */   public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
/* 65 */     this.underlyingInterceptor.init(conn, props, log);
/* 66 */     return this;
/*    */   }
/*    */   
/*    */   public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
/* 70 */     this.underlyingInterceptor.postProcess(sql, interceptedQuery, (Resultset)originalResultSet, serverSession);
/*    */     
/* 72 */     return null;
/*    */   }
/*    */   
/*    */   public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
/* 76 */     this.underlyingInterceptor.preProcess(sql, interceptedQuery);
/*    */     
/* 78 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public <M extends Message> M preProcess(M queryPacket) {
/* 83 */     this.underlyingInterceptor.preProcess((Message)queryPacket);
/*    */     
/* 85 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public <M extends Message> M postProcess(M queryPacket, M originalResponsePacket) {
/* 90 */     this.underlyingInterceptor.postProcess((Message)queryPacket, (Message)originalResponsePacket);
/*    */     
/* 92 */     return null;
/*    */   }
/*    */   
/*    */   public QueryInterceptor getUnderlyingInterceptor() {
/* 96 */     return this.underlyingInterceptor;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\NoSubInterceptorWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */