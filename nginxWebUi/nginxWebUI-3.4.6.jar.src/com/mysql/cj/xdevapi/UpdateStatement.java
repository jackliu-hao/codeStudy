package com.mysql.cj.xdevapi;

import java.util.Map;

public interface UpdateStatement extends Statement<UpdateStatement, Result> {
  UpdateStatement set(Map<String, Object> paramMap);
  
  UpdateStatement set(String paramString, Object paramObject);
  
  UpdateStatement where(String paramString);
  
  UpdateStatement orderBy(String... paramVarArgs);
  
  UpdateStatement limit(long paramLong);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\UpdateStatement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */