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

   public abstract ResultInterface getProcedures(String var1, String var2, String var3);

   public abstract ResultInterface getProcedureColumns(String var1, String var2, String var3, String var4);

   public abstract ResultInterface getTables(String var1, String var2, String var3, String[] var4);

   public abstract ResultInterface getSchemas();

   public abstract ResultInterface getCatalogs();

   public abstract ResultInterface getTableTypes();

   public abstract ResultInterface getColumns(String var1, String var2, String var3, String var4);

   public abstract ResultInterface getColumnPrivileges(String var1, String var2, String var3, String var4);

   public abstract ResultInterface getTablePrivileges(String var1, String var2, String var3);

   public abstract ResultInterface getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5);

   public abstract ResultInterface getVersionColumns(String var1, String var2, String var3);

   public abstract ResultInterface getPrimaryKeys(String var1, String var2, String var3);

   public abstract ResultInterface getImportedKeys(String var1, String var2, String var3);

   public abstract ResultInterface getExportedKeys(String var1, String var2, String var3);

   public abstract ResultInterface getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6);

   public abstract ResultInterface getTypeInfo();

   public abstract ResultInterface getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5);

   public abstract ResultInterface getUDTs(String var1, String var2, String var3, int[] var4);

   public abstract ResultInterface getSuperTypes(String var1, String var2, String var3);

   public abstract ResultInterface getSuperTables(String var1, String var2, String var3);

   public abstract ResultInterface getAttributes(String var1, String var2, String var3, String var4);

   public abstract int getDatabaseMajorVersion();

   public abstract int getDatabaseMinorVersion();

   public abstract ResultInterface getSchemas(String var1, String var2);

   public abstract ResultInterface getFunctions(String var1, String var2, String var3);

   public abstract ResultInterface getFunctionColumns(String var1, String var2, String var3, String var4);

   public abstract ResultInterface getPseudoColumns(String var1, String var2, String var3, String var4);
}
