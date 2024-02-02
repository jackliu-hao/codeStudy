package com.mysql.cj.xdevapi;

import java.util.Map;

public interface Collection extends DatabaseObject {
   AddStatement add(Map<String, ?> var1);

   AddStatement add(String... var1);

   AddStatement add(DbDoc var1);

   AddStatement add(DbDoc... var1);

   FindStatement find();

   FindStatement find(String var1);

   ModifyStatement modify(String var1);

   RemoveStatement remove(String var1);

   Result createIndex(String var1, DbDoc var2);

   Result createIndex(String var1, String var2);

   void dropIndex(String var1);

   long count();

   DbDoc newDoc();

   Result replaceOne(String var1, DbDoc var2);

   Result replaceOne(String var1, String var2);

   Result addOrReplaceOne(String var1, DbDoc var2);

   Result addOrReplaceOne(String var1, String var2);

   DbDoc getOne(String var1);

   Result removeOne(String var1);
}
