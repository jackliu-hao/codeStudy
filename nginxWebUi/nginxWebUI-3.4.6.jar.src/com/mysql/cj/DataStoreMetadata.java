package com.mysql.cj;

public interface DataStoreMetadata {
  boolean schemaExists(String paramString);
  
  boolean tableExists(String paramString1, String paramString2);
  
  long getTableRowCount(String paramString1, String paramString2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\DataStoreMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */