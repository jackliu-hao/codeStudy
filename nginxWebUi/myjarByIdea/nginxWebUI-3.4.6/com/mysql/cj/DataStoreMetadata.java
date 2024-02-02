package com.mysql.cj;

public interface DataStoreMetadata {
   boolean schemaExists(String var1);

   boolean tableExists(String var1, String var2);

   long getTableRowCount(String var1, String var2);
}
