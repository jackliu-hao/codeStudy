package com.mysql.cj.xdevapi;

import java.util.Map;

public interface Table extends DatabaseObject {
   InsertStatement insert();

   InsertStatement insert(String... var1);

   InsertStatement insert(Map<String, Object> var1);

   SelectStatement select(String... var1);

   UpdateStatement update();

   DeleteStatement delete();

   long count();

   boolean isView();
}
