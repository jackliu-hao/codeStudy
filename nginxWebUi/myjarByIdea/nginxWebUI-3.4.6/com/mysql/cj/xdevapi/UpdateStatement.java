package com.mysql.cj.xdevapi;

import java.util.Map;

public interface UpdateStatement extends Statement<UpdateStatement, Result> {
   UpdateStatement set(Map<String, Object> var1);

   UpdateStatement set(String var1, Object var2);

   UpdateStatement where(String var1);

   UpdateStatement orderBy(String... var1);

   UpdateStatement limit(long var1);
}
