package com.mysql.cj;

public interface CancelQueryTask {
  boolean cancel();
  
  Throwable getCaughtWhileCancelling();
  
  void setCaughtWhileCancelling(Throwable paramThrowable);
  
  Query getQueryToCancel();
  
  void setQueryToCancel(Query paramQuery);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\CancelQueryTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */