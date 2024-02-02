package com.mysql.cj.xdevapi;

public interface ModifyStatement extends Statement<ModifyStatement, Result> {
   ModifyStatement sort(String... var1);

   ModifyStatement limit(long var1);

   ModifyStatement set(String var1, Object var2);

   ModifyStatement change(String var1, Object var2);

   ModifyStatement unset(String... var1);

   ModifyStatement patch(DbDoc var1);

   ModifyStatement patch(String var1);

   ModifyStatement arrayInsert(String var1, Object var2);

   ModifyStatement arrayAppend(String var1, Object var2);
}
