package org.h2.jdbc.meta;

import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;

public abstract class DatabaseMeta {
  public abstract DefaultNullOrdering defaultNullOrdering();
  
  public abstract String getDatabaseProductVersion();
  
  public abstract String getSQLKeywords();
  
  public abstract String getNumericFunctions();
  
  public abstract String getStringFunctions();
  
  public abstract String getSystemFunctions();
  
  public abstract String getTimeDateFunctions();
  
  public abstract String getSearchStringEscape();
  
  public abstract ResultInterface getProcedures(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract ResultInterface getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract ResultInterface getSchemas();
  
  public abstract ResultInterface getCatalogs();
  
  public abstract ResultInterface getTableTypes();
  
  public abstract ResultInterface getColumns(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract ResultInterface getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract ResultInterface getTablePrivileges(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean);
  
  public abstract ResultInterface getVersionColumns(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getPrimaryKeys(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getImportedKeys(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getExportedKeys(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6);
  
  public abstract ResultInterface getTypeInfo();
  
  public abstract ResultInterface getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract ResultInterface getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfint);
  
  public abstract ResultInterface getSuperTypes(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getSuperTables(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getAttributes(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract int getDatabaseMajorVersion();
  
  public abstract int getDatabaseMinorVersion();
  
  public abstract ResultInterface getSchemas(String paramString1, String paramString2);
  
  public abstract ResultInterface getFunctions(String paramString1, String paramString2, String paramString3);
  
  public abstract ResultInterface getFunctionColumns(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract ResultInterface getPseudoColumns(String paramString1, String paramString2, String paramString3, String paramString4);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\meta\DatabaseMeta.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */