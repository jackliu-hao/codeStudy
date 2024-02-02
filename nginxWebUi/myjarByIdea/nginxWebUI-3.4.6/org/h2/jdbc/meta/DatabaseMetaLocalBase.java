package org.h2.jdbc.meta;

import org.h2.engine.Constants;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.value.TypeInfo;

abstract class DatabaseMetaLocalBase extends DatabaseMeta {
   public final String getDatabaseProductVersion() {
      return Constants.FULL_VERSION;
   }

   public final ResultInterface getVersionColumns(String var1, String var2, String var3) {
      this.checkClosed();
      SimpleResult var4 = new SimpleResult();
      var4.addColumn("SCOPE", TypeInfo.TYPE_SMALLINT);
      var4.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var4.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
      var4.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
      var4.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_SMALLINT);
      var4.addColumn("PSEUDO_COLUMN", TypeInfo.TYPE_SMALLINT);
      return var4;
   }

   public final ResultInterface getUDTs(String var1, String var2, String var3, int[] var4) {
      this.checkClosed();
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("CLASS_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("BASE_TYPE", TypeInfo.TYPE_SMALLINT);
      return var5;
   }

   public final ResultInterface getSuperTypes(String var1, String var2, String var3) {
      this.checkClosed();
      SimpleResult var4 = new SimpleResult();
      var4.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("SUPERTYPE_CAT", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("SUPERTYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("SUPERTYPE_NAME", TypeInfo.TYPE_VARCHAR);
      return var4;
   }

   public final ResultInterface getSuperTables(String var1, String var2, String var3) {
      this.checkClosed();
      SimpleResult var4 = new SimpleResult();
      var4.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("SUPERTABLE_NAME", TypeInfo.TYPE_VARCHAR);
      return var4;
   }

   public final ResultInterface getAttributes(String var1, String var2, String var3, String var4) {
      this.checkClosed();
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("ATTR_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("ATTR_TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("ATTR_SIZE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
      var5.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
      var5.addColumn("NULLABLE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("ATTR_DEF", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
      var5.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
      var5.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SCOPE_CATALOG", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SCOPE_SCHEMA", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SCOPE_TABLE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT);
      return var5;
   }

   public final int getDatabaseMajorVersion() {
      return 2;
   }

   public final int getDatabaseMinorVersion() {
      return 1;
   }

   public final ResultInterface getFunctions(String var1, String var2, String var3) {
      this.checkClosed();
      SimpleResult var4 = new SimpleResult();
      var4.addColumn("FUNCTION_CAT", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("FUNCTION_SCHEM", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("FUNCTION_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("FUNCTION_TYPE", TypeInfo.TYPE_SMALLINT);
      var4.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
      return var4;
   }

   public final ResultInterface getFunctionColumns(String var1, String var2, String var3, String var4) {
      this.checkClosed();
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("FUNCTION_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("FUNCTION_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("FUNCTION_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
      var5.addColumn("LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("SCALE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("RADIX", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
      var5.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
      return var5;
   }

   final SimpleResult getPseudoColumnsResult() {
      this.checkClosed();
      SimpleResult var1 = new SimpleResult();
      var1.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var1.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
      var1.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
      var1.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
      var1.addColumn("COLUMN_USAGE", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
      var1.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
      return var1;
   }

   abstract void checkClosed();
}
