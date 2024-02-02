package com.mysql.cj.xdevapi;

import java.util.List;

public interface Session {
   List<Schema> getSchemas();

   Schema getSchema(String var1);

   String getDefaultSchemaName();

   Schema getDefaultSchema();

   Schema createSchema(String var1);

   Schema createSchema(String var1, boolean var2);

   void dropSchema(String var1);

   String getUri();

   boolean isOpen();

   void close();

   void startTransaction();

   void commit();

   void rollback();

   String setSavepoint();

   String setSavepoint(String var1);

   void rollbackTo(String var1);

   void releaseSavepoint(String var1);

   SqlStatement sql(String var1);
}
