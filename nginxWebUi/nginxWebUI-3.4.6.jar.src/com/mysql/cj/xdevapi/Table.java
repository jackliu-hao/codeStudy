package com.mysql.cj.xdevapi;

import java.util.Map;

public interface Table extends DatabaseObject {
  InsertStatement insert();
  
  InsertStatement insert(String... paramVarArgs);
  
  InsertStatement insert(Map<String, Object> paramMap);
  
  SelectStatement select(String... paramVarArgs);
  
  UpdateStatement update();
  
  DeleteStatement delete();
  
  long count();
  
  boolean isView();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Table.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */