package org.h2.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.h2.command.CommandInterface;
import org.h2.engine.Constants;
import org.h2.engine.Session;
import org.h2.jdbc.meta.DatabaseMeta;
import org.h2.jdbc.meta.DatabaseMetaLegacy;
import org.h2.message.DbException;
import org.h2.message.Trace;
import org.h2.message.TraceObject;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.value.TypeInfo;
import org.h2.value.ValueInteger;
import org.h2.value.ValueVarchar;

public final class JdbcDatabaseMetaData extends TraceObject implements DatabaseMetaData, JdbcDatabaseMetaDataBackwardsCompat {
   private final JdbcConnection conn;
   private final DatabaseMeta meta;

   JdbcDatabaseMetaData(JdbcConnection var1, Trace var2, int var3) {
      this.setTrace(var2, 2, var3);
      this.conn = var1;
      Session var4 = var1.getSession();
      this.meta = (DatabaseMeta)(var4.isOldInformationSchema() ? new DatabaseMetaLegacy(var4) : var1.getSession().getDatabaseMeta());
   }

   public int getDriverMajorVersion() {
      this.debugCodeCall("getDriverMajorVersion");
      return 2;
   }

   public int getDriverMinorVersion() {
      this.debugCodeCall("getDriverMinorVersion");
      return 1;
   }

   public String getDatabaseProductName() {
      this.debugCodeCall("getDatabaseProductName");
      return "H2";
   }

   public String getDatabaseProductVersion() throws SQLException {
      try {
         this.debugCodeCall("getDatabaseProductVersion");
         return this.meta.getDatabaseProductVersion();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getDriverName() {
      this.debugCodeCall("getDriverName");
      return "H2 JDBC Driver";
   }

   public String getDriverVersion() {
      this.debugCodeCall("getDriverVersion");
      return Constants.FULL_VERSION;
   }

   public ResultSet getTables(String var1, String var2, String var3, String[] var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getTables(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quoteArray(var4) + ')');
         }

         return this.getResultSet(this.meta.getTables(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public ResultSet getColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getColumns(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ')');
         }

         return this.getResultSet(this.meta.getColumns(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public ResultSet getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getIndexInfo(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + var4 + ", " + var5 + ')');
         }

         return this.getResultSet(this.meta.getIndexInfo(var1, var2, var3, var4, var5));
      } catch (Exception var7) {
         throw this.logAndConvert(var7);
      }
   }

   public ResultSet getPrimaryKeys(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getPrimaryKeys(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getPrimaryKeys(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public boolean allProceduresAreCallable() {
      this.debugCodeCall("allProceduresAreCallable");
      return true;
   }

   public boolean allTablesAreSelectable() {
      this.debugCodeCall("allTablesAreSelectable");
      return true;
   }

   public String getURL() throws SQLException {
      try {
         this.debugCodeCall("getURL");
         return this.conn.getURL();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getUserName() throws SQLException {
      try {
         this.debugCodeCall("getUserName");
         return this.conn.getUser();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean isReadOnly() throws SQLException {
      try {
         this.debugCodeCall("isReadOnly");
         return this.conn.isReadOnly();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean nullsAreSortedHigh() throws SQLException {
      try {
         this.debugCodeCall("nullsAreSortedHigh");
         return this.meta.defaultNullOrdering() == DefaultNullOrdering.HIGH;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean nullsAreSortedLow() throws SQLException {
      try {
         this.debugCodeCall("nullsAreSortedLow");
         return this.meta.defaultNullOrdering() == DefaultNullOrdering.LOW;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean nullsAreSortedAtStart() throws SQLException {
      try {
         this.debugCodeCall("nullsAreSortedAtStart");
         return this.meta.defaultNullOrdering() == DefaultNullOrdering.FIRST;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean nullsAreSortedAtEnd() throws SQLException {
      try {
         this.debugCodeCall("nullsAreSortedAtEnd");
         return this.meta.defaultNullOrdering() == DefaultNullOrdering.LAST;
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public Connection getConnection() {
      this.debugCodeCall("getConnection");
      return this.conn;
   }

   public ResultSet getProcedures(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getProcedures(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getProcedures(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getProcedureColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getProcedureColumns(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ')');
         }

         this.checkClosed();
         return this.getResultSet(this.meta.getProcedureColumns(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public ResultSet getSchemas() throws SQLException {
      try {
         this.debugCodeCall("getSchemas");
         return this.getResultSet(this.meta.getSchemas());
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public ResultSet getCatalogs() throws SQLException {
      try {
         this.debugCodeCall("getCatalogs");
         return this.getResultSet(this.meta.getCatalogs());
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public ResultSet getTableTypes() throws SQLException {
      try {
         this.debugCodeCall("getTableTypes");
         return this.getResultSet(this.meta.getTableTypes());
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public ResultSet getColumnPrivileges(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getColumnPrivileges(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ')');
         }

         return this.getResultSet(this.meta.getColumnPrivileges(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public ResultSet getTablePrivileges(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getTablePrivileges(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         this.checkClosed();
         return this.getResultSet(this.meta.getTablePrivileges(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getBestRowIdentifier(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + var4 + ", " + var5 + ')');
         }

         return this.getResultSet(this.meta.getBestRowIdentifier(var1, var2, var3, var4, var5));
      } catch (Exception var7) {
         throw this.logAndConvert(var7);
      }
   }

   public ResultSet getVersionColumns(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getVersionColumns(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getVersionColumns(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getImportedKeys(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getImportedKeys(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getImportedKeys(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getExportedKeys(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getExportedKeys(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getExportedKeys(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getCrossReference(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ", " + quote(var5) + ", " + quote(var6) + ')');
         }

         return this.getResultSet(this.meta.getCrossReference(var1, var2, var3, var4, var5, var6));
      } catch (Exception var8) {
         throw this.logAndConvert(var8);
      }
   }

   public ResultSet getUDTs(String var1, String var2, String var3, int[] var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getUDTs(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quoteIntArray(var4) + ')');
         }

         return this.getResultSet(this.meta.getUDTs(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public ResultSet getTypeInfo() throws SQLException {
      try {
         this.debugCodeCall("getTypeInfo");
         return this.getResultSet(this.meta.getTypeInfo());
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public boolean usesLocalFiles() {
      this.debugCodeCall("usesLocalFiles");
      return true;
   }

   public boolean usesLocalFilePerTable() {
      this.debugCodeCall("usesLocalFilePerTable");
      return false;
   }

   public String getIdentifierQuoteString() {
      this.debugCodeCall("getIdentifierQuoteString");
      return "\"";
   }

   public String getSQLKeywords() throws SQLException {
      try {
         this.debugCodeCall("getSQLKeywords");
         return this.meta.getSQLKeywords();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getNumericFunctions() throws SQLException {
      try {
         this.debugCodeCall("getNumericFunctions");
         return this.meta.getNumericFunctions();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getStringFunctions() throws SQLException {
      try {
         this.debugCodeCall("getStringFunctions");
         return this.meta.getStringFunctions();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getSystemFunctions() throws SQLException {
      try {
         this.debugCodeCall("getSystemFunctions");
         return this.meta.getSystemFunctions();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getTimeDateFunctions() throws SQLException {
      try {
         this.debugCodeCall("getTimeDateFunctions");
         return this.meta.getTimeDateFunctions();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getSearchStringEscape() throws SQLException {
      try {
         this.debugCodeCall("getSearchStringEscape");
         return this.meta.getSearchStringEscape();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public String getExtraNameCharacters() {
      this.debugCodeCall("getExtraNameCharacters");
      return "";
   }

   public boolean supportsAlterTableWithAddColumn() {
      this.debugCodeCall("supportsAlterTableWithAddColumn");
      return true;
   }

   public boolean supportsAlterTableWithDropColumn() {
      this.debugCodeCall("supportsAlterTableWithDropColumn");
      return true;
   }

   public boolean supportsColumnAliasing() {
      this.debugCodeCall("supportsColumnAliasing");
      return true;
   }

   public boolean nullPlusNonNullIsNull() {
      this.debugCodeCall("nullPlusNonNullIsNull");
      return true;
   }

   public boolean supportsConvert() {
      this.debugCodeCall("supportsConvert");
      return true;
   }

   public boolean supportsConvert(int var1, int var2) {
      if (this.isDebugEnabled()) {
         this.debugCode("supportsConvert(" + var1 + ", " + var2 + ')');
      }

      return true;
   }

   public boolean supportsTableCorrelationNames() {
      this.debugCodeCall("supportsTableCorrelationNames");
      return true;
   }

   public boolean supportsDifferentTableCorrelationNames() {
      this.debugCodeCall("supportsDifferentTableCorrelationNames");
      return false;
   }

   public boolean supportsExpressionsInOrderBy() {
      this.debugCodeCall("supportsExpressionsInOrderBy");
      return true;
   }

   public boolean supportsOrderByUnrelated() {
      this.debugCodeCall("supportsOrderByUnrelated");
      return true;
   }

   public boolean supportsGroupBy() {
      this.debugCodeCall("supportsGroupBy");
      return true;
   }

   public boolean supportsGroupByUnrelated() {
      this.debugCodeCall("supportsGroupByUnrelated");
      return true;
   }

   public boolean supportsGroupByBeyondSelect() {
      this.debugCodeCall("supportsGroupByBeyondSelect");
      return true;
   }

   public boolean supportsLikeEscapeClause() {
      this.debugCodeCall("supportsLikeEscapeClause");
      return true;
   }

   public boolean supportsMultipleResultSets() {
      this.debugCodeCall("supportsMultipleResultSets");
      return false;
   }

   public boolean supportsMultipleTransactions() {
      this.debugCodeCall("supportsMultipleTransactions");
      return true;
   }

   public boolean supportsNonNullableColumns() {
      this.debugCodeCall("supportsNonNullableColumns");
      return true;
   }

   public boolean supportsMinimumSQLGrammar() {
      this.debugCodeCall("supportsMinimumSQLGrammar");
      return true;
   }

   public boolean supportsCoreSQLGrammar() {
      this.debugCodeCall("supportsCoreSQLGrammar");
      return true;
   }

   public boolean supportsExtendedSQLGrammar() {
      this.debugCodeCall("supportsExtendedSQLGrammar");
      return false;
   }

   public boolean supportsANSI92EntryLevelSQL() {
      this.debugCodeCall("supportsANSI92EntryLevelSQL");
      return true;
   }

   public boolean supportsANSI92IntermediateSQL() {
      this.debugCodeCall("supportsANSI92IntermediateSQL");
      return false;
   }

   public boolean supportsANSI92FullSQL() {
      this.debugCodeCall("supportsANSI92FullSQL");
      return false;
   }

   public boolean supportsIntegrityEnhancementFacility() {
      this.debugCodeCall("supportsIntegrityEnhancementFacility");
      return true;
   }

   public boolean supportsOuterJoins() {
      this.debugCodeCall("supportsOuterJoins");
      return true;
   }

   public boolean supportsFullOuterJoins() {
      this.debugCodeCall("supportsFullOuterJoins");
      return false;
   }

   public boolean supportsLimitedOuterJoins() {
      this.debugCodeCall("supportsLimitedOuterJoins");
      return true;
   }

   public String getSchemaTerm() {
      this.debugCodeCall("getSchemaTerm");
      return "schema";
   }

   public String getProcedureTerm() {
      this.debugCodeCall("getProcedureTerm");
      return "procedure";
   }

   public String getCatalogTerm() {
      this.debugCodeCall("getCatalogTerm");
      return "catalog";
   }

   public boolean isCatalogAtStart() {
      this.debugCodeCall("isCatalogAtStart");
      return true;
   }

   public String getCatalogSeparator() {
      this.debugCodeCall("getCatalogSeparator");
      return ".";
   }

   public boolean supportsSchemasInDataManipulation() {
      this.debugCodeCall("supportsSchemasInDataManipulation");
      return true;
   }

   public boolean supportsSchemasInProcedureCalls() {
      this.debugCodeCall("supportsSchemasInProcedureCalls");
      return true;
   }

   public boolean supportsSchemasInTableDefinitions() {
      this.debugCodeCall("supportsSchemasInTableDefinitions");
      return true;
   }

   public boolean supportsSchemasInIndexDefinitions() {
      this.debugCodeCall("supportsSchemasInIndexDefinitions");
      return true;
   }

   public boolean supportsSchemasInPrivilegeDefinitions() {
      this.debugCodeCall("supportsSchemasInPrivilegeDefinitions");
      return true;
   }

   public boolean supportsCatalogsInDataManipulation() {
      this.debugCodeCall("supportsCatalogsInDataManipulation");
      return true;
   }

   public boolean supportsCatalogsInProcedureCalls() {
      this.debugCodeCall("supportsCatalogsInProcedureCalls");
      return false;
   }

   public boolean supportsCatalogsInTableDefinitions() {
      this.debugCodeCall("supportsCatalogsInTableDefinitions");
      return true;
   }

   public boolean supportsCatalogsInIndexDefinitions() {
      this.debugCodeCall("supportsCatalogsInIndexDefinitions");
      return true;
   }

   public boolean supportsCatalogsInPrivilegeDefinitions() {
      this.debugCodeCall("supportsCatalogsInPrivilegeDefinitions");
      return true;
   }

   public boolean supportsPositionedDelete() {
      this.debugCodeCall("supportsPositionedDelete");
      return false;
   }

   public boolean supportsPositionedUpdate() {
      this.debugCodeCall("supportsPositionedUpdate");
      return false;
   }

   public boolean supportsSelectForUpdate() {
      this.debugCodeCall("supportsSelectForUpdate");
      return true;
   }

   public boolean supportsStoredProcedures() {
      this.debugCodeCall("supportsStoredProcedures");
      return false;
   }

   public boolean supportsSubqueriesInComparisons() {
      this.debugCodeCall("supportsSubqueriesInComparisons");
      return true;
   }

   public boolean supportsSubqueriesInExists() {
      this.debugCodeCall("supportsSubqueriesInExists");
      return true;
   }

   public boolean supportsSubqueriesInIns() {
      this.debugCodeCall("supportsSubqueriesInIns");
      return true;
   }

   public boolean supportsSubqueriesInQuantifieds() {
      this.debugCodeCall("supportsSubqueriesInQuantifieds");
      return true;
   }

   public boolean supportsCorrelatedSubqueries() {
      this.debugCodeCall("supportsCorrelatedSubqueries");
      return true;
   }

   public boolean supportsUnion() {
      this.debugCodeCall("supportsUnion");
      return true;
   }

   public boolean supportsUnionAll() {
      this.debugCodeCall("supportsUnionAll");
      return true;
   }

   public boolean supportsOpenCursorsAcrossCommit() {
      this.debugCodeCall("supportsOpenCursorsAcrossCommit");
      return false;
   }

   public boolean supportsOpenCursorsAcrossRollback() {
      this.debugCodeCall("supportsOpenCursorsAcrossRollback");
      return false;
   }

   public boolean supportsOpenStatementsAcrossCommit() {
      this.debugCodeCall("supportsOpenStatementsAcrossCommit");
      return true;
   }

   public boolean supportsOpenStatementsAcrossRollback() {
      this.debugCodeCall("supportsOpenStatementsAcrossRollback");
      return true;
   }

   public boolean supportsTransactions() {
      this.debugCodeCall("supportsTransactions");
      return true;
   }

   public boolean supportsTransactionIsolationLevel(int var1) throws SQLException {
      this.debugCodeCall("supportsTransactionIsolationLevel");
      switch (var1) {
         case 1:
         case 2:
         case 4:
         case 6:
         case 8:
            return true;
         case 3:
         case 5:
         case 7:
         default:
            return false;
      }
   }

   public boolean supportsDataDefinitionAndDataManipulationTransactions() {
      this.debugCodeCall("supportsDataDefinitionAndDataManipulationTransactions");
      return false;
   }

   public boolean supportsDataManipulationTransactionsOnly() {
      this.debugCodeCall("supportsDataManipulationTransactionsOnly");
      return true;
   }

   public boolean dataDefinitionCausesTransactionCommit() {
      this.debugCodeCall("dataDefinitionCausesTransactionCommit");
      return true;
   }

   public boolean dataDefinitionIgnoredInTransactions() {
      this.debugCodeCall("dataDefinitionIgnoredInTransactions");
      return false;
   }

   public boolean supportsResultSetType(int var1) {
      this.debugCodeCall("supportsResultSetType", (long)var1);
      return var1 != 1005;
   }

   public boolean supportsResultSetConcurrency(int var1, int var2) {
      if (this.isDebugEnabled()) {
         this.debugCode("supportsResultSetConcurrency(" + var1 + ", " + var2 + ')');
      }

      return var1 != 1005;
   }

   public boolean ownUpdatesAreVisible(int var1) {
      this.debugCodeCall("ownUpdatesAreVisible", (long)var1);
      return true;
   }

   public boolean ownDeletesAreVisible(int var1) {
      this.debugCodeCall("ownDeletesAreVisible", (long)var1);
      return false;
   }

   public boolean ownInsertsAreVisible(int var1) {
      this.debugCodeCall("ownInsertsAreVisible", (long)var1);
      return false;
   }

   public boolean othersUpdatesAreVisible(int var1) {
      this.debugCodeCall("othersUpdatesAreVisible", (long)var1);
      return false;
   }

   public boolean othersDeletesAreVisible(int var1) {
      this.debugCodeCall("othersDeletesAreVisible", (long)var1);
      return false;
   }

   public boolean othersInsertsAreVisible(int var1) {
      this.debugCodeCall("othersInsertsAreVisible", (long)var1);
      return false;
   }

   public boolean updatesAreDetected(int var1) {
      this.debugCodeCall("updatesAreDetected", (long)var1);
      return false;
   }

   public boolean deletesAreDetected(int var1) {
      this.debugCodeCall("deletesAreDetected", (long)var1);
      return false;
   }

   public boolean insertsAreDetected(int var1) {
      this.debugCodeCall("insertsAreDetected", (long)var1);
      return false;
   }

   public boolean supportsBatchUpdates() {
      this.debugCodeCall("supportsBatchUpdates");
      return true;
   }

   public boolean doesMaxRowSizeIncludeBlobs() {
      this.debugCodeCall("doesMaxRowSizeIncludeBlobs");
      return false;
   }

   public int getDefaultTransactionIsolation() {
      this.debugCodeCall("getDefaultTransactionIsolation");
      return 2;
   }

   public boolean supportsMixedCaseIdentifiers() throws SQLException {
      this.debugCodeCall("supportsMixedCaseIdentifiers");
      Session.StaticSettings var1 = this.conn.getStaticSettings();
      return !var1.databaseToUpper && !var1.databaseToLower && !var1.caseInsensitiveIdentifiers;
   }

   public boolean storesUpperCaseIdentifiers() throws SQLException {
      this.debugCodeCall("storesUpperCaseIdentifiers");
      return this.conn.getStaticSettings().databaseToUpper;
   }

   public boolean storesLowerCaseIdentifiers() throws SQLException {
      this.debugCodeCall("storesLowerCaseIdentifiers");
      return this.conn.getStaticSettings().databaseToLower;
   }

   public boolean storesMixedCaseIdentifiers() throws SQLException {
      this.debugCodeCall("storesMixedCaseIdentifiers");
      Session.StaticSettings var1 = this.conn.getStaticSettings();
      return !var1.databaseToUpper && !var1.databaseToLower && var1.caseInsensitiveIdentifiers;
   }

   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
      this.debugCodeCall("supportsMixedCaseQuotedIdentifiers");
      return !this.conn.getStaticSettings().caseInsensitiveIdentifiers;
   }

   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
      this.debugCodeCall("storesUpperCaseQuotedIdentifiers");
      return false;
   }

   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
      this.debugCodeCall("storesLowerCaseQuotedIdentifiers");
      return false;
   }

   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
      this.debugCodeCall("storesMixedCaseQuotedIdentifiers");
      return this.conn.getStaticSettings().caseInsensitiveIdentifiers;
   }

   public int getMaxBinaryLiteralLength() {
      this.debugCodeCall("getMaxBinaryLiteralLength");
      return 0;
   }

   public int getMaxCharLiteralLength() {
      this.debugCodeCall("getMaxCharLiteralLength");
      return 0;
   }

   public int getMaxColumnNameLength() {
      this.debugCodeCall("getMaxColumnNameLength");
      return 0;
   }

   public int getMaxColumnsInGroupBy() {
      this.debugCodeCall("getMaxColumnsInGroupBy");
      return 0;
   }

   public int getMaxColumnsInIndex() {
      this.debugCodeCall("getMaxColumnsInIndex");
      return 0;
   }

   public int getMaxColumnsInOrderBy() {
      this.debugCodeCall("getMaxColumnsInOrderBy");
      return 0;
   }

   public int getMaxColumnsInSelect() {
      this.debugCodeCall("getMaxColumnsInSelect");
      return 0;
   }

   public int getMaxColumnsInTable() {
      this.debugCodeCall("getMaxColumnsInTable");
      return 0;
   }

   public int getMaxConnections() {
      this.debugCodeCall("getMaxConnections");
      return 0;
   }

   public int getMaxCursorNameLength() {
      this.debugCodeCall("getMaxCursorNameLength");
      return 0;
   }

   public int getMaxIndexLength() {
      this.debugCodeCall("getMaxIndexLength");
      return 0;
   }

   public int getMaxSchemaNameLength() {
      this.debugCodeCall("getMaxSchemaNameLength");
      return 0;
   }

   public int getMaxProcedureNameLength() {
      this.debugCodeCall("getMaxProcedureNameLength");
      return 0;
   }

   public int getMaxCatalogNameLength() {
      this.debugCodeCall("getMaxCatalogNameLength");
      return 0;
   }

   public int getMaxRowSize() {
      this.debugCodeCall("getMaxRowSize");
      return 0;
   }

   public int getMaxStatementLength() {
      this.debugCodeCall("getMaxStatementLength");
      return 0;
   }

   public int getMaxStatements() {
      this.debugCodeCall("getMaxStatements");
      return 0;
   }

   public int getMaxTableNameLength() {
      this.debugCodeCall("getMaxTableNameLength");
      return 0;
   }

   public int getMaxTablesInSelect() {
      this.debugCodeCall("getMaxTablesInSelect");
      return 0;
   }

   public int getMaxUserNameLength() {
      this.debugCodeCall("getMaxUserNameLength");
      return 0;
   }

   public boolean supportsSavepoints() {
      this.debugCodeCall("supportsSavepoints");
      return true;
   }

   public boolean supportsNamedParameters() {
      this.debugCodeCall("supportsNamedParameters");
      return false;
   }

   public boolean supportsMultipleOpenResults() {
      this.debugCodeCall("supportsMultipleOpenResults");
      return false;
   }

   public boolean supportsGetGeneratedKeys() {
      this.debugCodeCall("supportsGetGeneratedKeys");
      return true;
   }

   public ResultSet getSuperTypes(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getSuperTypes(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getSuperTypes(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getSuperTables(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getSuperTables(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getSuperTables(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public ResultSet getAttributes(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getAttributes(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ')');
         }

         return this.getResultSet(this.meta.getAttributes(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public boolean supportsResultSetHoldability(int var1) {
      this.debugCodeCall("supportsResultSetHoldability", (long)var1);
      return var1 == 2;
   }

   public int getResultSetHoldability() {
      this.debugCodeCall("getResultSetHoldability");
      return 2;
   }

   public int getDatabaseMajorVersion() throws SQLException {
      try {
         this.debugCodeCall("getDatabaseMajorVersion");
         return this.meta.getDatabaseMajorVersion();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getDatabaseMinorVersion() throws SQLException {
      try {
         this.debugCodeCall("getDatabaseMinorVersion");
         return this.meta.getDatabaseMinorVersion();
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public int getJDBCMajorVersion() {
      this.debugCodeCall("getJDBCMajorVersion");
      return 4;
   }

   public int getJDBCMinorVersion() {
      this.debugCodeCall("getJDBCMinorVersion");
      return 2;
   }

   public int getSQLStateType() {
      this.debugCodeCall("getSQLStateType");
      return 2;
   }

   public boolean locatorsUpdateCopy() {
      this.debugCodeCall("locatorsUpdateCopy");
      return false;
   }

   public boolean supportsStatementPooling() {
      this.debugCodeCall("supportsStatementPooling");
      return false;
   }

   private void checkClosed() {
      this.conn.checkClosed();
   }

   public RowIdLifetime getRowIdLifetime() {
      this.debugCodeCall("getRowIdLifetime");
      return RowIdLifetime.ROWID_UNSUPPORTED;
   }

   public ResultSet getSchemas(String var1, String var2) throws SQLException {
      try {
         this.debugCodeCall("getSchemas(String,String)");
         return this.getResultSet(this.meta.getSchemas(var1, var2));
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public boolean supportsStoredFunctionsUsingCallSyntax() {
      this.debugCodeCall("supportsStoredFunctionsUsingCallSyntax");
      return true;
   }

   public boolean autoCommitFailureClosesAllResultSets() {
      this.debugCodeCall("autoCommitFailureClosesAllResultSets");
      return false;
   }

   public ResultSet getClientInfoProperties() throws SQLException {
      Properties var1 = this.conn.getClientInfo();
      SimpleResult var2 = new SimpleResult();
      var2.addColumn("NAME", TypeInfo.TYPE_VARCHAR);
      var2.addColumn("MAX_LEN", TypeInfo.TYPE_INTEGER);
      var2.addColumn("DEFAULT_VALUE", TypeInfo.TYPE_VARCHAR);
      var2.addColumn("DESCRIPTION", TypeInfo.TYPE_VARCHAR);
      var2.addColumn("VALUE", TypeInfo.TYPE_VARCHAR);
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         var2.addRow(ValueVarchar.get((String)var4.getKey()), ValueInteger.get(Integer.MAX_VALUE), ValueVarchar.EMPTY, ValueVarchar.EMPTY, ValueVarchar.get((String)var4.getValue()));
      }

      int var5 = getNextId(4);
      this.debugCodeAssign("ResultSet", 4, var5, "getClientInfoProperties()");
      return new JdbcResultSet(this.conn, (JdbcStatement)null, (CommandInterface)null, var2, var5, true, false, false);
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      try {
         if (this.isWrapperFor(var1)) {
            return this;
         } else {
            throw DbException.getInvalidValueException("iface", var1);
         }
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1 != null && var1.isAssignableFrom(this.getClass());
   }

   public ResultSet getFunctionColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getFunctionColumns(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ')');
         }

         return this.getResultSet(this.meta.getFunctionColumns(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public ResultSet getFunctions(String var1, String var2, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getFunctions(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ')');
         }

         return this.getResultSet(this.meta.getFunctions(var1, var2, var3));
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public boolean generatedKeyAlwaysReturned() {
      return true;
   }

   public ResultSet getPseudoColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getPseudoColumns(" + quote(var1) + ", " + quote(var2) + ", " + quote(var3) + ", " + quote(var4) + ')');
         }

         return this.getResultSet(this.meta.getPseudoColumns(var1, var2, var3, var4));
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public String toString() {
      return this.getTraceObjectName() + ": " + this.conn;
   }

   private JdbcResultSet getResultSet(ResultInterface var1) {
      return new JdbcResultSet(this.conn, (JdbcStatement)null, (CommandInterface)null, var1, getNextId(4), true, false, false);
   }
}
