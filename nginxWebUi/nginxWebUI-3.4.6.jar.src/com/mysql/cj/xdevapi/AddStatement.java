package com.mysql.cj.xdevapi;

public interface AddStatement extends Statement<AddStatement, AddResult> {
  AddStatement add(String paramString);
  
  AddStatement add(DbDoc... paramVarArgs);
  
  boolean isUpsert();
  
  AddStatement setUpsert(boolean paramBoolean);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\AddStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */