/*    */ package com.mysql.cj;public interface Query { int getId();
/*    */   void setCancelStatus(CancelStatus paramCancelStatus);
/*    */   void checkCancelTimeout();
/*    */   <T extends Resultset, M extends com.mysql.cj.protocol.Message> ProtocolEntityFactory<T, M> getResultSetFactory();
/*    */   Session getSession();
/*    */   Object getCancelTimeoutMutex();
/*    */   void resetCancelledState();
/*    */   void closeQuery();
/*    */   void addBatch(Object paramObject);
/*    */   List<Object> getBatchedArgs();
/*    */   void clearBatchedArgs();
/*    */   QueryAttributesBindings getQueryAttributesBindings();
/*    */   int getResultFetchSize();
/*    */   void setResultFetchSize(int paramInt);
/*    */   Resultset.Type getResultType();
/*    */   void setResultType(Resultset.Type paramType);
/*    */   
/*    */   int getTimeoutInMillis();
/*    */   
/*    */   void setTimeoutInMillis(int paramInt);
/*    */   
/*    */   void setExecuteTime(long paramLong);
/*    */   
/*    */   long getExecuteTime();
/*    */   
/*    */   CancelQueryTask startQueryTimer(Query paramQuery, int paramInt);
/*    */   
/*    */   AtomicBoolean getStatementExecuting();
/*    */   
/*    */   String getCurrentDatabase();
/*    */   
/*    */   void setCurrentDatabase(String paramString);
/*    */   
/*    */   boolean isClearWarningsCalled();
/*    */   
/*    */   void setClearWarningsCalled(boolean paramBoolean);
/*    */   
/*    */   void statementBegins();
/*    */   
/*    */   void stopQueryTimer(CancelQueryTask paramCancelQueryTask, boolean paramBoolean1, boolean paramBoolean2);
/*    */   
/* 42 */   public enum CancelStatus { NOT_CANCELED, CANCELED_BY_USER, CANCELED_BY_TIMEOUT; }
/*    */    }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\Query.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */