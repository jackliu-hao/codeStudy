package org.h2.jdbc.meta;

import java.util.ArrayList;
import java.util.Arrays;
import org.h2.command.CommandInterface;
import org.h2.engine.Session;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class DatabaseMetaLegacy extends DatabaseMetaLocalBase {
   private static final Value PERCENT = ValueVarchar.get("%");
   private static final Value BACKSLASH = ValueVarchar.get("\\");
   private static final Value YES = ValueVarchar.get("YES");
   private static final Value NO = ValueVarchar.get("NO");
   private static final Value SCHEMA_MAIN = ValueVarchar.get("PUBLIC");
   private final Session session;

   public DatabaseMetaLegacy(Session var1) {
      this.session = var1;
   }

   public final DefaultNullOrdering defaultNullOrdering() {
      return DefaultNullOrdering.LOW;
   }

   public String getSQLKeywords() {
      return "CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,INTERSECTS,KEY,LIMIT,MINUS,OFFSET,QUALIFY,REGEXP,ROWNUM,SYSDATE,SYSTIME,SYSTIMESTAMP,TODAY,TOP,_ROWID_";
   }

   public String getNumericFunctions() {
      return this.getFunctions("Functions (Numeric)");
   }

   public String getStringFunctions() {
      return this.getFunctions("Functions (String)");
   }

   public String getSystemFunctions() {
      return this.getFunctions("Functions (System)");
   }

   public String getTimeDateFunctions() {
      return this.getFunctions("Functions (Time and Date)");
   }

   private String getFunctions(String var1) {
      String var2 = "SELECT TOPIC FROM INFORMATION_SCHEMA.HELP WHERE SECTION = ?";
      Value[] var3 = new Value[]{this.getString(var1)};
      ResultInterface var4 = this.executeQuery(var2, var3);
      StringBuilder var5 = new StringBuilder();

      while(var4.next()) {
         String var6 = var4.currentRow()[0].getString().trim();
         String[] var7 = StringUtils.arraySplit(var6, ',', true);
         String[] var8 = var7;
         int var9 = var7.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            String var11 = var8[var10];
            if (var5.length() != 0) {
               var5.append(',');
            }

            String var12 = var11.trim();
            int var13 = var12.indexOf(32);
            if (var13 >= 0) {
               StringUtils.trimSubstring(var5, var12, 0, var13);
            } else {
               var5.append(var12);
            }
         }
      }

      return var5.toString();
   }

   public String getSearchStringEscape() {
      return "\\";
   }

   public ResultInterface getProcedures(String var1, String var2, String var3) {
      return this.executeQuery("SELECT ALIAS_CATALOG PROCEDURE_CAT, ALIAS_SCHEMA PROCEDURE_SCHEM, ALIAS_NAME PROCEDURE_NAME, COLUMN_COUNT NUM_INPUT_PARAMS, ZERO() NUM_OUTPUT_PARAMS, ZERO() NUM_RESULT_SETS, REMARKS, RETURNS_RESULT PROCEDURE_TYPE, ALIAS_NAME SPECIFIC_NAME FROM INFORMATION_SCHEMA.FUNCTION_ALIASES WHERE ALIAS_CATALOG LIKE ?1 ESCAPE ?4 AND ALIAS_SCHEMA LIKE ?2 ESCAPE ?4 AND ALIAS_NAME LIKE ?3 ESCAPE ?4 ORDER BY PROCEDURE_SCHEM, PROCEDURE_NAME, NUM_INPUT_PARAMS", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getPattern(var3), BACKSLASH);
   }

   public ResultInterface getProcedureColumns(String var1, String var2, String var3, String var4) {
      return this.executeQuery("SELECT ALIAS_CATALOG PROCEDURE_CAT, ALIAS_SCHEMA PROCEDURE_SCHEM, ALIAS_NAME PROCEDURE_NAME, COLUMN_NAME, COLUMN_TYPE, DATA_TYPE, TYPE_NAME, PRECISION, PRECISION LENGTH, SCALE, RADIX, NULLABLE, REMARKS, COLUMN_DEFAULT COLUMN_DEF, ZERO() SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, ZERO() CHAR_OCTET_LENGTH, POS ORDINAL_POSITION, ?1 IS_NULLABLE, ALIAS_NAME SPECIFIC_NAME FROM INFORMATION_SCHEMA.FUNCTION_COLUMNS WHERE ALIAS_CATALOG LIKE ?2 ESCAPE ?6 AND ALIAS_SCHEMA LIKE ?3 ESCAPE ?6 AND ALIAS_NAME LIKE ?4 ESCAPE ?6 AND COLUMN_NAME LIKE ?5 ESCAPE ?6 ORDER BY PROCEDURE_SCHEM, PROCEDURE_NAME, ORDINAL_POSITION", YES, this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getPattern(var3), this.getPattern(var4), BACKSLASH);
   }

   public ResultInterface getTables(String var1, String var2, String var3, String[] var4) {
      int var5 = var4 != null ? var4.length : 0;
      boolean var6 = var4 == null || Arrays.asList(var4).contains("SYNONYM");
      StringBuilder var7 = new StringBuilder(1008);
      if (var6) {
         var7.append("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_CAT, TYPE_SCHEM, TYPE_NAME, SELF_REFERENCING_COL_NAME, REF_GENERATION, SQL FROM (SELECT SYNONYM_CATALOG TABLE_CAT, SYNONYM_SCHEMA TABLE_SCHEM, SYNONYM_NAME as TABLE_NAME, TYPE_NAME AS TABLE_TYPE, REMARKS, TYPE_NAME TYPE_CAT, TYPE_NAME TYPE_SCHEM, TYPE_NAME AS TYPE_NAME, TYPE_NAME SELF_REFERENCING_COL_NAME, TYPE_NAME REF_GENERATION, NULL AS SQL FROM INFORMATION_SCHEMA.SYNONYMS WHERE SYNONYM_CATALOG LIKE ?1 ESCAPE ?4 AND SYNONYM_SCHEMA LIKE ?2 ESCAPE ?4 AND SYNONYM_NAME LIKE ?3 ESCAPE ?4 UNION ");
      }

      var7.append("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, TABLE_TYPE, REMARKS, TYPE_NAME TYPE_CAT, TYPE_NAME TYPE_SCHEM, TYPE_NAME, TYPE_NAME SELF_REFERENCING_COL_NAME, TYPE_NAME REF_GENERATION, SQL FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME LIKE ?3 ESCAPE ?4");
      if (var5 > 0) {
         var7.append(" AND TABLE_TYPE IN(");

         for(int var8 = 0; var8 < var5; ++var8) {
            if (var8 > 0) {
               var7.append(", ");
            }

            var7.append('?').append(var8 + 5);
         }

         var7.append(')');
      }

      if (var6) {
         var7.append(')');
      }

      Value[] var10 = new Value[var5 + 4];
      var10[0] = this.getCatalogPattern(var1);
      var10[1] = this.getSchemaPattern(var2);
      var10[2] = this.getPattern(var3);
      var10[3] = BACKSLASH;

      for(int var9 = 0; var9 < var5; ++var9) {
         var10[var9 + 4] = this.getString(var4[var9]);
      }

      return this.executeQuery(var7.append(" ORDER BY TABLE_TYPE, TABLE_SCHEM, TABLE_NAME").toString(), var10);
   }

   public ResultInterface getSchemas() {
      return this.executeQuery("SELECT SCHEMA_NAME TABLE_SCHEM, CATALOG_NAME TABLE_CATALOG FROM INFORMATION_SCHEMA.SCHEMATA ORDER BY SCHEMA_NAME");
   }

   public ResultInterface getCatalogs() {
      return this.executeQuery("SELECT CATALOG_NAME TABLE_CAT FROM INFORMATION_SCHEMA.CATALOGS");
   }

   public ResultInterface getTableTypes() {
      return this.executeQuery("SELECT TYPE TABLE_TYPE FROM INFORMATION_SCHEMA.TABLE_TYPES ORDER BY TABLE_TYPE");
   }

   public ResultInterface getColumns(String var1, String var2, String var3, String var4) {
      return this.executeQuery("SELECT TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT, IS_GENERATEDCOLUMN FROM (SELECT s.SYNONYM_CATALOG TABLE_CAT, s.SYNONYM_SCHEMA TABLE_SCHEM, s.SYNONYM_NAME TABLE_NAME, c.COLUMN_NAME, c.DATA_TYPE, c.TYPE_NAME, c.CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, c.CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, c.NUMERIC_SCALE DECIMAL_DIGITS, c.NUMERIC_PRECISION_RADIX NUM_PREC_RADIX, c.NULLABLE, c.REMARKS, c.COLUMN_DEFAULT COLUMN_DEF, c.DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, c.CHARACTER_OCTET_LENGTH CHAR_OCTET_LENGTH, c.ORDINAL_POSITION, c.IS_NULLABLE IS_NULLABLE, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_CATALOG, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_SCHEMA, CAST(c.SOURCE_DATA_TYPE AS VARCHAR) SCOPE_TABLE, c.SOURCE_DATA_TYPE, CASE WHEN c.SEQUENCE_NAME IS NULL THEN CAST(?1 AS VARCHAR) ELSE CAST(?2 AS VARCHAR) END IS_AUTOINCREMENT, CASE WHEN c.IS_COMPUTED THEN CAST(?2 AS VARCHAR) ELSE CAST(?1 AS VARCHAR) END IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS c JOIN INFORMATION_SCHEMA.SYNONYMS s ON s.SYNONYM_FOR = c.TABLE_NAME AND s.SYNONYM_FOR_SCHEMA = c.TABLE_SCHEMA WHERE s.SYNONYM_CATALOG LIKE ?3 ESCAPE ?7 AND s.SYNONYM_SCHEMA LIKE ?4 ESCAPE ?7 AND s.SYNONYM_NAME LIKE ?5 ESCAPE ?7 AND c.COLUMN_NAME LIKE ?6 ESCAPE ?7 UNION SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME, CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, NUMERIC_SCALE DECIMAL_DIGITS, NUMERIC_PRECISION_RADIX NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEFAULT COLUMN_DEF, DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, CHARACTER_OCTET_LENGTH CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE IS_NULLABLE, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_CATALOG, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_SCHEMA, CAST(SOURCE_DATA_TYPE AS VARCHAR) SCOPE_TABLE, SOURCE_DATA_TYPE, CASE WHEN SEQUENCE_NAME IS NULL THEN CAST(?1 AS VARCHAR) ELSE CAST(?2 AS VARCHAR) END IS_AUTOINCREMENT, CASE WHEN IS_COMPUTED THEN CAST(?2 AS VARCHAR) ELSE CAST(?1 AS VARCHAR) END IS_GENERATEDCOLUMN FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_CATALOG LIKE ?3 ESCAPE ?7 AND TABLE_SCHEMA LIKE ?4 ESCAPE ?7 AND TABLE_NAME LIKE ?5 ESCAPE ?7 AND COLUMN_NAME LIKE ?6 ESCAPE ?7) ORDER BY TABLE_SCHEM, TABLE_NAME, ORDINAL_POSITION", NO, YES, this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getPattern(var3), this.getPattern(var4), BACKSLASH);
   }

   public ResultInterface getColumnPrivileges(String var1, String var2, String var3, String var4) {
      return this.executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, GRANTOR, GRANTEE, PRIVILEGE_TYPE PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?5 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?5 AND TABLE_NAME = ?3 AND COLUMN_NAME LIKE ?4 ESCAPE ?5 ORDER BY COLUMN_NAME, PRIVILEGE", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), this.getPattern(var4), BACKSLASH);
   }

   public ResultInterface getTablePrivileges(String var1, String var2, String var3) {
      return this.executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, GRANTOR, GRANTEE, PRIVILEGE_TYPE PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.TABLE_PRIVILEGES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME LIKE ?3 ESCAPE ?4 ORDER BY TABLE_SCHEM, TABLE_NAME, PRIVILEGE", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getPattern(var3), BACKSLASH);
   }

   public ResultInterface getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5) {
      return this.executeQuery("SELECT CAST(?1 AS SMALLINT) SCOPE, C.COLUMN_NAME, C.DATA_TYPE, C.TYPE_NAME, C.CHARACTER_MAXIMUM_LENGTH COLUMN_SIZE, C.CHARACTER_MAXIMUM_LENGTH BUFFER_LENGTH, CAST(C.NUMERIC_SCALE AS SMALLINT) DECIMAL_DIGITS, CAST(?2 AS SMALLINT) PSEUDO_COLUMN FROM INFORMATION_SCHEMA.INDEXES I, INFORMATION_SCHEMA.COLUMNS C WHERE C.TABLE_NAME = I.TABLE_NAME AND C.COLUMN_NAME = I.COLUMN_NAME AND C.TABLE_CATALOG LIKE ?3 ESCAPE ?6 AND C.TABLE_SCHEMA LIKE ?4 ESCAPE ?6 AND C.TABLE_NAME = ?5 AND I.PRIMARY_KEY = TRUE ORDER BY SCOPE", ValueInteger.get(2), ValueInteger.get(1), this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), BACKSLASH);
   }

   public ResultInterface getPrimaryKeys(String var1, String var2, String var3) {
      return this.executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION KEY_SEQ, COALESCE(CONSTRAINT_NAME, INDEX_NAME) PK_NAME FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND TABLE_NAME = ?3 AND PRIMARY_KEY = TRUE ORDER BY COLUMN_NAME", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), BACKSLASH);
   }

   public ResultInterface getImportedKeys(String var1, String var2, String var3) {
      return this.executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE FKTABLE_CATALOG LIKE ?1 ESCAPE ?4 AND FKTABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND FKTABLE_NAME = ?3 ORDER BY PKTABLE_CAT, PKTABLE_SCHEM, PKTABLE_NAME, FK_NAME, KEY_SEQ", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), BACKSLASH);
   }

   public ResultInterface getExportedKeys(String var1, String var2, String var3) {
      return this.executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE PKTABLE_CATALOG LIKE ?1 ESCAPE ?4 AND PKTABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND PKTABLE_NAME = ?3 ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, FK_NAME, KEY_SEQ", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), BACKSLASH);
   }

   public ResultInterface getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6) {
      return this.executeQuery("SELECT PKTABLE_CATALOG PKTABLE_CAT, PKTABLE_SCHEMA PKTABLE_SCHEM, PKTABLE_NAME PKTABLE_NAME, PKCOLUMN_NAME, FKTABLE_CATALOG FKTABLE_CAT, FKTABLE_SCHEMA FKTABLE_SCHEM, FKTABLE_NAME, FKCOLUMN_NAME, ORDINAL_POSITION KEY_SEQ, UPDATE_RULE, DELETE_RULE, FK_NAME, PK_NAME, DEFERRABILITY FROM INFORMATION_SCHEMA.CROSS_REFERENCES WHERE PKTABLE_CATALOG LIKE ?1 ESCAPE ?7 AND PKTABLE_SCHEMA LIKE ?2 ESCAPE ?7 AND PKTABLE_NAME = ?3 AND FKTABLE_CATALOG LIKE ?4 ESCAPE ?7 AND FKTABLE_SCHEMA LIKE ?5 ESCAPE ?7 AND FKTABLE_NAME = ?6 ORDER BY FKTABLE_CAT, FKTABLE_SCHEM, FKTABLE_NAME, FK_NAME, KEY_SEQ", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), this.getCatalogPattern(var4), this.getSchemaPattern(var5), this.getString(var6), BACKSLASH);
   }

   public ResultInterface getTypeInfo() {
      return this.executeQuery("SELECT TYPE_NAME, DATA_TYPE, PRECISION, PREFIX LITERAL_PREFIX, SUFFIX LITERAL_SUFFIX, PARAMS CREATE_PARAMS, NULLABLE, CASE_SENSITIVE, SEARCHABLE, FALSE UNSIGNED_ATTRIBUTE, FALSE FIXED_PREC_SCALE, AUTO_INCREMENT, TYPE_NAME LOCAL_TYPE_NAME, MINIMUM_SCALE, MAXIMUM_SCALE, DATA_TYPE SQL_DATA_TYPE, ZERO() SQL_DATETIME_SUB, RADIX NUM_PREC_RADIX FROM INFORMATION_SCHEMA.TYPE_INFO ORDER BY DATA_TYPE, POS");
   }

   public ResultInterface getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5) {
      String var6 = var4 ? "NON_UNIQUE=FALSE" : "TRUE";
      return this.executeQuery("SELECT TABLE_CATALOG TABLE_CAT, TABLE_SCHEMA TABLE_SCHEM, TABLE_NAME, NON_UNIQUE, TABLE_CATALOG INDEX_QUALIFIER, INDEX_NAME, INDEX_TYPE TYPE, ORDINAL_POSITION, COLUMN_NAME, ASC_OR_DESC, CARDINALITY, PAGES, FILTER_CONDITION, SORT_TYPE FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_CATALOG LIKE ?1 ESCAPE ?4 AND TABLE_SCHEMA LIKE ?2 ESCAPE ?4 AND (" + var6 + ") AND TABLE_NAME = ?3 ORDER BY NON_UNIQUE, TYPE, TABLE_SCHEM, INDEX_NAME, ORDINAL_POSITION", this.getCatalogPattern(var1), this.getSchemaPattern(var2), this.getString(var3), BACKSLASH);
   }

   public ResultInterface getSchemas(String var1, String var2) {
      return this.executeQuery("SELECT SCHEMA_NAME TABLE_SCHEM, CATALOG_NAME TABLE_CATALOG FROM INFORMATION_SCHEMA.SCHEMATA WHERE CATALOG_NAME LIKE ?1 ESCAPE ?3 AND SCHEMA_NAME LIKE ?2 ESCAPE ?3 ORDER BY SCHEMA_NAME", this.getCatalogPattern(var1), this.getSchemaPattern(var2), BACKSLASH);
   }

   public ResultInterface getPseudoColumns(String var1, String var2, String var3, String var4) {
      return this.getPseudoColumnsResult();
   }

   private ResultInterface executeQuery(String var1, Value... var2) {
      this.checkClosed();
      synchronized(this.session) {
         CommandInterface var4 = this.session.prepareCommand(var1, Integer.MAX_VALUE);
         int var5 = var2.length;
         if (var5 > 0) {
            ArrayList var6 = var4.getParameters();

            for(int var7 = 0; var7 < var5; ++var7) {
               ((ParameterInterface)var6.get(var7)).setValue(var2[var7], true);
            }
         }

         ResultInterface var10 = var4.executeQuery(0L, false);
         var4.close();
         return var10;
      }
   }

   void checkClosed() {
      if (this.session.isClosed()) {
         throw DbException.get(90121);
      }
   }

   private Value getString(String var1) {
      return (Value)(var1 != null ? ValueVarchar.get(var1, this.session) : ValueNull.INSTANCE);
   }

   private Value getPattern(String var1) {
      return var1 == null ? PERCENT : this.getString(var1);
   }

   private Value getSchemaPattern(String var1) {
      return var1 == null ? PERCENT : (var1.isEmpty() ? SCHEMA_MAIN : this.getString(var1));
   }

   private Value getCatalogPattern(String var1) {
      return var1 != null && !var1.isEmpty() ? this.getString(var1) : PERCENT;
   }
}
