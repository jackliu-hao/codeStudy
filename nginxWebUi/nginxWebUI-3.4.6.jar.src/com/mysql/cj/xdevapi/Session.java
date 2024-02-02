package com.mysql.cj.xdevapi;

import java.util.List;

public interface Session {
  List<Schema> getSchemas();
  
  Schema getSchema(String paramString);
  
  String getDefaultSchemaName();
  
  Schema getDefaultSchema();
  
  Schema createSchema(String paramString);
  
  Schema createSchema(String paramString, boolean paramBoolean);
  
  void dropSchema(String paramString);
  
  String getUri();
  
  boolean isOpen();
  
  void close();
  
  void startTransaction();
  
  void commit();
  
  void rollback();
  
  String setSavepoint();
  
  String setSavepoint(String paramString);
  
  void rollbackTo(String paramString);
  
  void releaseSavepoint(String paramString);
  
  SqlStatement sql(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Session.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */