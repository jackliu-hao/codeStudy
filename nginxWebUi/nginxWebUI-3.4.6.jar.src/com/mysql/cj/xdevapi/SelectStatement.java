package com.mysql.cj.xdevapi;

public interface SelectStatement extends Statement<SelectStatement, RowResult> {
  SelectStatement where(String paramString);
  
  SelectStatement groupBy(String... paramVarArgs);
  
  SelectStatement having(String paramString);
  
  SelectStatement orderBy(String... paramVarArgs);
  
  SelectStatement limit(long paramLong);
  
  SelectStatement offset(long paramLong);
  
  SelectStatement lockShared();
  
  SelectStatement lockShared(Statement.LockContention paramLockContention);
  
  SelectStatement lockExclusive();
  
  SelectStatement lockExclusive(Statement.LockContention paramLockContention);
  
  FilterParams getFilterParams();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\SelectStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */