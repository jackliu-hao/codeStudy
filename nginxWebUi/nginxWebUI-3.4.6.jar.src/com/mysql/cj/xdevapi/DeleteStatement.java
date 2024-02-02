package com.mysql.cj.xdevapi;

public interface DeleteStatement extends Statement<DeleteStatement, Result> {
  DeleteStatement where(String paramString);
  
  DeleteStatement orderBy(String... paramVarArgs);
  
  DeleteStatement limit(long paramLong);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DeleteStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */