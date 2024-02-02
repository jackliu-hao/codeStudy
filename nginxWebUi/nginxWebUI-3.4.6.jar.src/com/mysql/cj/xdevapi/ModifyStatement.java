package com.mysql.cj.xdevapi;

public interface ModifyStatement extends Statement<ModifyStatement, Result> {
  ModifyStatement sort(String... paramVarArgs);
  
  ModifyStatement limit(long paramLong);
  
  ModifyStatement set(String paramString, Object paramObject);
  
  ModifyStatement change(String paramString, Object paramObject);
  
  ModifyStatement unset(String... paramVarArgs);
  
  ModifyStatement patch(DbDoc paramDbDoc);
  
  ModifyStatement patch(String paramString);
  
  ModifyStatement arrayInsert(String paramString, Object paramObject);
  
  ModifyStatement arrayAppend(String paramString, Object paramObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ModifyStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */