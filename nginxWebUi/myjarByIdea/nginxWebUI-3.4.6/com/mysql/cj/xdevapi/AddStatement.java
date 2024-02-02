package com.mysql.cj.xdevapi;

public interface AddStatement extends Statement<AddStatement, AddResult> {
   AddStatement add(String var1);

   AddStatement add(DbDoc... var1);

   boolean isUpsert();

   AddStatement setUpsert(boolean var1);
}
