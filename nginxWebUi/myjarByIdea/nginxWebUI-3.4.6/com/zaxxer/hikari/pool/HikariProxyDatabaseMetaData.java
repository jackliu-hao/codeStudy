package com.zaxxer.hikari.pool;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Wrapper;

public final class HikariProxyDatabaseMetaData extends ProxyDatabaseMetaData implements Wrapper, DatabaseMetaData {
   public boolean isWrapperFor(Class var1) throws SQLException {
      try {
         return super.delegate.isWrapperFor(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean allProceduresAreCallable() throws SQLException {
      try {
         return super.delegate.allProceduresAreCallable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean allTablesAreSelectable() throws SQLException {
      try {
         return super.delegate.allTablesAreSelectable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getURL() throws SQLException {
      try {
         return super.delegate.getURL();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getUserName() throws SQLException {
      try {
         return super.delegate.getUserName();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isReadOnly() throws SQLException {
      try {
         return super.delegate.isReadOnly();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean nullsAreSortedHigh() throws SQLException {
      try {
         return super.delegate.nullsAreSortedHigh();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean nullsAreSortedLow() throws SQLException {
      try {
         return super.delegate.nullsAreSortedLow();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean nullsAreSortedAtStart() throws SQLException {
      try {
         return super.delegate.nullsAreSortedAtStart();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean nullsAreSortedAtEnd() throws SQLException {
      try {
         return super.delegate.nullsAreSortedAtEnd();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getDatabaseProductName() throws SQLException {
      try {
         return super.delegate.getDatabaseProductName();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getDatabaseProductVersion() throws SQLException {
      try {
         return super.delegate.getDatabaseProductVersion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getDriverName() throws SQLException {
      try {
         return super.delegate.getDriverName();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getDriverVersion() throws SQLException {
      try {
         return super.delegate.getDriverVersion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getDriverMajorVersion() {
      return ((DatabaseMetaData)super.delegate).getDriverMajorVersion();
   }

   public int getDriverMinorVersion() {
      return ((DatabaseMetaData)super.delegate).getDriverMinorVersion();
   }

   public boolean usesLocalFiles() throws SQLException {
      try {
         return super.delegate.usesLocalFiles();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean usesLocalFilePerTable() throws SQLException {
      try {
         return super.delegate.usesLocalFilePerTable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsMixedCaseIdentifiers() throws SQLException {
      try {
         return super.delegate.supportsMixedCaseIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean storesUpperCaseIdentifiers() throws SQLException {
      try {
         return super.delegate.storesUpperCaseIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean storesLowerCaseIdentifiers() throws SQLException {
      try {
         return super.delegate.storesLowerCaseIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean storesMixedCaseIdentifiers() throws SQLException {
      try {
         return super.delegate.storesMixedCaseIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
      try {
         return super.delegate.supportsMixedCaseQuotedIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
      try {
         return super.delegate.storesUpperCaseQuotedIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
      try {
         return super.delegate.storesLowerCaseQuotedIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
      try {
         return super.delegate.storesMixedCaseQuotedIdentifiers();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getIdentifierQuoteString() throws SQLException {
      try {
         return super.delegate.getIdentifierQuoteString();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getSQLKeywords() throws SQLException {
      try {
         return super.delegate.getSQLKeywords();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getNumericFunctions() throws SQLException {
      try {
         return super.delegate.getNumericFunctions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getStringFunctions() throws SQLException {
      try {
         return super.delegate.getStringFunctions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getSystemFunctions() throws SQLException {
      try {
         return super.delegate.getSystemFunctions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getTimeDateFunctions() throws SQLException {
      try {
         return super.delegate.getTimeDateFunctions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getSearchStringEscape() throws SQLException {
      try {
         return super.delegate.getSearchStringEscape();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getExtraNameCharacters() throws SQLException {
      try {
         return super.delegate.getExtraNameCharacters();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsAlterTableWithAddColumn() throws SQLException {
      try {
         return super.delegate.supportsAlterTableWithAddColumn();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsAlterTableWithDropColumn() throws SQLException {
      try {
         return super.delegate.supportsAlterTableWithDropColumn();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsColumnAliasing() throws SQLException {
      try {
         return super.delegate.supportsColumnAliasing();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean nullPlusNonNullIsNull() throws SQLException {
      try {
         return super.delegate.nullPlusNonNullIsNull();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsConvert() throws SQLException {
      try {
         return super.delegate.supportsConvert();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsConvert(int var1, int var2) throws SQLException {
      try {
         return super.delegate.supportsConvert(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean supportsTableCorrelationNames() throws SQLException {
      try {
         return super.delegate.supportsTableCorrelationNames();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsDifferentTableCorrelationNames() throws SQLException {
      try {
         return super.delegate.supportsDifferentTableCorrelationNames();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsExpressionsInOrderBy() throws SQLException {
      try {
         return super.delegate.supportsExpressionsInOrderBy();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsOrderByUnrelated() throws SQLException {
      try {
         return super.delegate.supportsOrderByUnrelated();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsGroupBy() throws SQLException {
      try {
         return super.delegate.supportsGroupBy();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsGroupByUnrelated() throws SQLException {
      try {
         return super.delegate.supportsGroupByUnrelated();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsGroupByBeyondSelect() throws SQLException {
      try {
         return super.delegate.supportsGroupByBeyondSelect();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsLikeEscapeClause() throws SQLException {
      try {
         return super.delegate.supportsLikeEscapeClause();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsMultipleResultSets() throws SQLException {
      try {
         return super.delegate.supportsMultipleResultSets();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsMultipleTransactions() throws SQLException {
      try {
         return super.delegate.supportsMultipleTransactions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsNonNullableColumns() throws SQLException {
      try {
         return super.delegate.supportsNonNullableColumns();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsMinimumSQLGrammar() throws SQLException {
      try {
         return super.delegate.supportsMinimumSQLGrammar();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCoreSQLGrammar() throws SQLException {
      try {
         return super.delegate.supportsCoreSQLGrammar();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsExtendedSQLGrammar() throws SQLException {
      try {
         return super.delegate.supportsExtendedSQLGrammar();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsANSI92EntryLevelSQL() throws SQLException {
      try {
         return super.delegate.supportsANSI92EntryLevelSQL();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsANSI92IntermediateSQL() throws SQLException {
      try {
         return super.delegate.supportsANSI92IntermediateSQL();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsANSI92FullSQL() throws SQLException {
      try {
         return super.delegate.supportsANSI92FullSQL();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsIntegrityEnhancementFacility() throws SQLException {
      try {
         return super.delegate.supportsIntegrityEnhancementFacility();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsOuterJoins() throws SQLException {
      try {
         return super.delegate.supportsOuterJoins();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsFullOuterJoins() throws SQLException {
      try {
         return super.delegate.supportsFullOuterJoins();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsLimitedOuterJoins() throws SQLException {
      try {
         return super.delegate.supportsLimitedOuterJoins();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getSchemaTerm() throws SQLException {
      try {
         return super.delegate.getSchemaTerm();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getProcedureTerm() throws SQLException {
      try {
         return super.delegate.getProcedureTerm();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getCatalogTerm() throws SQLException {
      try {
         return super.delegate.getCatalogTerm();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean isCatalogAtStart() throws SQLException {
      try {
         return super.delegate.isCatalogAtStart();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public String getCatalogSeparator() throws SQLException {
      try {
         return super.delegate.getCatalogSeparator();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSchemasInDataManipulation() throws SQLException {
      try {
         return super.delegate.supportsSchemasInDataManipulation();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSchemasInProcedureCalls() throws SQLException {
      try {
         return super.delegate.supportsSchemasInProcedureCalls();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSchemasInTableDefinitions() throws SQLException {
      try {
         return super.delegate.supportsSchemasInTableDefinitions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSchemasInIndexDefinitions() throws SQLException {
      try {
         return super.delegate.supportsSchemasInIndexDefinitions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
      try {
         return super.delegate.supportsSchemasInPrivilegeDefinitions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCatalogsInDataManipulation() throws SQLException {
      try {
         return super.delegate.supportsCatalogsInDataManipulation();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCatalogsInProcedureCalls() throws SQLException {
      try {
         return super.delegate.supportsCatalogsInProcedureCalls();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCatalogsInTableDefinitions() throws SQLException {
      try {
         return super.delegate.supportsCatalogsInTableDefinitions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
      try {
         return super.delegate.supportsCatalogsInIndexDefinitions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
      try {
         return super.delegate.supportsCatalogsInPrivilegeDefinitions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsPositionedDelete() throws SQLException {
      try {
         return super.delegate.supportsPositionedDelete();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsPositionedUpdate() throws SQLException {
      try {
         return super.delegate.supportsPositionedUpdate();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSelectForUpdate() throws SQLException {
      try {
         return super.delegate.supportsSelectForUpdate();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsStoredProcedures() throws SQLException {
      try {
         return super.delegate.supportsStoredProcedures();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSubqueriesInComparisons() throws SQLException {
      try {
         return super.delegate.supportsSubqueriesInComparisons();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSubqueriesInExists() throws SQLException {
      try {
         return super.delegate.supportsSubqueriesInExists();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSubqueriesInIns() throws SQLException {
      try {
         return super.delegate.supportsSubqueriesInIns();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsSubqueriesInQuantifieds() throws SQLException {
      try {
         return super.delegate.supportsSubqueriesInQuantifieds();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsCorrelatedSubqueries() throws SQLException {
      try {
         return super.delegate.supportsCorrelatedSubqueries();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsUnion() throws SQLException {
      try {
         return super.delegate.supportsUnion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsUnionAll() throws SQLException {
      try {
         return super.delegate.supportsUnionAll();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
      try {
         return super.delegate.supportsOpenCursorsAcrossCommit();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
      try {
         return super.delegate.supportsOpenCursorsAcrossRollback();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
      try {
         return super.delegate.supportsOpenStatementsAcrossCommit();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
      try {
         return super.delegate.supportsOpenStatementsAcrossRollback();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxBinaryLiteralLength() throws SQLException {
      try {
         return super.delegate.getMaxBinaryLiteralLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxCharLiteralLength() throws SQLException {
      try {
         return super.delegate.getMaxCharLiteralLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxColumnNameLength() throws SQLException {
      try {
         return super.delegate.getMaxColumnNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxColumnsInGroupBy() throws SQLException {
      try {
         return super.delegate.getMaxColumnsInGroupBy();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxColumnsInIndex() throws SQLException {
      try {
         return super.delegate.getMaxColumnsInIndex();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxColumnsInOrderBy() throws SQLException {
      try {
         return super.delegate.getMaxColumnsInOrderBy();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxColumnsInSelect() throws SQLException {
      try {
         return super.delegate.getMaxColumnsInSelect();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxColumnsInTable() throws SQLException {
      try {
         return super.delegate.getMaxColumnsInTable();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxConnections() throws SQLException {
      try {
         return super.delegate.getMaxConnections();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxCursorNameLength() throws SQLException {
      try {
         return super.delegate.getMaxCursorNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxIndexLength() throws SQLException {
      try {
         return super.delegate.getMaxIndexLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxSchemaNameLength() throws SQLException {
      try {
         return super.delegate.getMaxSchemaNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxProcedureNameLength() throws SQLException {
      try {
         return super.delegate.getMaxProcedureNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxCatalogNameLength() throws SQLException {
      try {
         return super.delegate.getMaxCatalogNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxRowSize() throws SQLException {
      try {
         return super.delegate.getMaxRowSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
      try {
         return super.delegate.doesMaxRowSizeIncludeBlobs();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxStatementLength() throws SQLException {
      try {
         return super.delegate.getMaxStatementLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxStatements() throws SQLException {
      try {
         return super.delegate.getMaxStatements();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxTableNameLength() throws SQLException {
      try {
         return super.delegate.getMaxTableNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxTablesInSelect() throws SQLException {
      try {
         return super.delegate.getMaxTablesInSelect();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getMaxUserNameLength() throws SQLException {
      try {
         return super.delegate.getMaxUserNameLength();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getDefaultTransactionIsolation() throws SQLException {
      try {
         return super.delegate.getDefaultTransactionIsolation();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsTransactions() throws SQLException {
      try {
         return super.delegate.supportsTransactions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsTransactionIsolationLevel(int var1) throws SQLException {
      try {
         return super.delegate.supportsTransactionIsolationLevel(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
      try {
         return super.delegate.supportsDataDefinitionAndDataManipulationTransactions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
      try {
         return super.delegate.supportsDataManipulationTransactionsOnly();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
      try {
         return super.delegate.dataDefinitionCausesTransactionCommit();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
      try {
         return super.delegate.dataDefinitionIgnoredInTransactions();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getProcedures(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getProcedures(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getProcedureColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         return super.getProcedureColumns(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public ResultSet getTables(String var1, String var2, String var3, String[] var4) throws SQLException {
      try {
         return super.getTables(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public ResultSet getSchemas() throws SQLException {
      try {
         return super.getSchemas();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getCatalogs() throws SQLException {
      try {
         return super.getCatalogs();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getTableTypes() throws SQLException {
      try {
         return super.getTableTypes();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         return super.getColumns(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public ResultSet getColumnPrivileges(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         return super.getColumnPrivileges(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public ResultSet getTablePrivileges(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getTablePrivileges(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5) throws SQLException {
      try {
         return super.getBestRowIdentifier(var1, var2, var3, var4, var5);
      } catch (SQLException var7) {
         throw this.checkException(var7);
      }
   }

   public ResultSet getVersionColumns(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getVersionColumns(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getPrimaryKeys(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getPrimaryKeys(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getImportedKeys(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getImportedKeys(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getExportedKeys(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getExportedKeys(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6) throws SQLException {
      try {
         return super.getCrossReference(var1, var2, var3, var4, var5, var6);
      } catch (SQLException var8) {
         throw this.checkException(var8);
      }
   }

   public ResultSet getTypeInfo() throws SQLException {
      try {
         return super.getTypeInfo();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5) throws SQLException {
      try {
         return super.getIndexInfo(var1, var2, var3, var4, var5);
      } catch (SQLException var7) {
         throw this.checkException(var7);
      }
   }

   public boolean supportsResultSetType(int var1) throws SQLException {
      try {
         return super.delegate.supportsResultSetType(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean supportsResultSetConcurrency(int var1, int var2) throws SQLException {
      try {
         return super.delegate.supportsResultSetConcurrency(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean ownUpdatesAreVisible(int var1) throws SQLException {
      try {
         return super.delegate.ownUpdatesAreVisible(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean ownDeletesAreVisible(int var1) throws SQLException {
      try {
         return super.delegate.ownDeletesAreVisible(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean ownInsertsAreVisible(int var1) throws SQLException {
      try {
         return super.delegate.ownInsertsAreVisible(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean othersUpdatesAreVisible(int var1) throws SQLException {
      try {
         return super.delegate.othersUpdatesAreVisible(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean othersDeletesAreVisible(int var1) throws SQLException {
      try {
         return super.delegate.othersDeletesAreVisible(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean othersInsertsAreVisible(int var1) throws SQLException {
      try {
         return super.delegate.othersInsertsAreVisible(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean updatesAreDetected(int var1) throws SQLException {
      try {
         return super.delegate.updatesAreDetected(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean deletesAreDetected(int var1) throws SQLException {
      try {
         return super.delegate.deletesAreDetected(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean insertsAreDetected(int var1) throws SQLException {
      try {
         return super.delegate.insertsAreDetected(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public boolean supportsBatchUpdates() throws SQLException {
      try {
         return super.delegate.supportsBatchUpdates();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getUDTs(String var1, String var2, String var3, int[] var4) throws SQLException {
      try {
         return super.getUDTs(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public boolean supportsSavepoints() throws SQLException {
      try {
         return super.delegate.supportsSavepoints();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsNamedParameters() throws SQLException {
      try {
         return super.delegate.supportsNamedParameters();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsMultipleOpenResults() throws SQLException {
      try {
         return super.delegate.supportsMultipleOpenResults();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsGetGeneratedKeys() throws SQLException {
      try {
         return super.delegate.supportsGetGeneratedKeys();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getSuperTypes(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getSuperTypes(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getSuperTables(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getSuperTables(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getAttributes(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         return super.getAttributes(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public boolean supportsResultSetHoldability(int var1) throws SQLException {
      try {
         return super.delegate.supportsResultSetHoldability(var1);
      } catch (SQLException var3) {
         throw this.checkException(var3);
      }
   }

   public int getResultSetHoldability() throws SQLException {
      try {
         return super.delegate.getResultSetHoldability();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getDatabaseMajorVersion() throws SQLException {
      try {
         return super.delegate.getDatabaseMajorVersion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getDatabaseMinorVersion() throws SQLException {
      try {
         return super.delegate.getDatabaseMinorVersion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getJDBCMajorVersion() throws SQLException {
      try {
         return super.delegate.getJDBCMajorVersion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getJDBCMinorVersion() throws SQLException {
      try {
         return super.delegate.getJDBCMinorVersion();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public int getSQLStateType() throws SQLException {
      try {
         return super.delegate.getSQLStateType();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean locatorsUpdateCopy() throws SQLException {
      try {
         return super.delegate.locatorsUpdateCopy();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsStatementPooling() throws SQLException {
      try {
         return super.delegate.supportsStatementPooling();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public RowIdLifetime getRowIdLifetime() throws SQLException {
      try {
         return super.delegate.getRowIdLifetime();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getSchemas(String var1, String var2) throws SQLException {
      try {
         return super.getSchemas(var1, var2);
      } catch (SQLException var4) {
         throw this.checkException(var4);
      }
   }

   public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
      try {
         return super.delegate.supportsStoredFunctionsUsingCallSyntax();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
      try {
         return super.delegate.autoCommitFailureClosesAllResultSets();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getClientInfoProperties() throws SQLException {
      try {
         return super.getClientInfoProperties();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public ResultSet getFunctions(String var1, String var2, String var3) throws SQLException {
      try {
         return super.getFunctions(var1, var2, var3);
      } catch (SQLException var5) {
         throw this.checkException(var5);
      }
   }

   public ResultSet getFunctionColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         return super.getFunctionColumns(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public ResultSet getPseudoColumns(String var1, String var2, String var3, String var4) throws SQLException {
      try {
         return super.getPseudoColumns(var1, var2, var3, var4);
      } catch (SQLException var6) {
         throw this.checkException(var6);
      }
   }

   public boolean generatedKeyAlwaysReturned() throws SQLException {
      try {
         return super.delegate.generatedKeyAlwaysReturned();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public long getMaxLogicalLobSize() throws SQLException {
      try {
         return super.delegate.getMaxLogicalLobSize();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   public boolean supportsRefCursors() throws SQLException {
      try {
         return super.delegate.supportsRefCursors();
      } catch (SQLException var2) {
         throw this.checkException(var2);
      }
   }

   HikariProxyDatabaseMetaData(ProxyConnection var1, DatabaseMetaData var2) {
      super(var1, var2);
   }
}
