package com.mysql.cj.xdevapi;

import java.util.Map;

public interface Collection extends DatabaseObject {
  AddStatement add(Map<String, ?> paramMap);
  
  AddStatement add(String... paramVarArgs);
  
  AddStatement add(DbDoc paramDbDoc);
  
  AddStatement add(DbDoc... paramVarArgs);
  
  FindStatement find();
  
  FindStatement find(String paramString);
  
  ModifyStatement modify(String paramString);
  
  RemoveStatement remove(String paramString);
  
  Result createIndex(String paramString, DbDoc paramDbDoc);
  
  Result createIndex(String paramString1, String paramString2);
  
  void dropIndex(String paramString);
  
  long count();
  
  DbDoc newDoc();
  
  Result replaceOne(String paramString, DbDoc paramDbDoc);
  
  Result replaceOne(String paramString1, String paramString2);
  
  Result addOrReplaceOne(String paramString, DbDoc paramDbDoc);
  
  Result addOrReplaceOne(String paramString1, String paramString2);
  
  DbDoc getOne(String paramString);
  
  Result removeOne(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Collection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */