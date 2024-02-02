package com.zaxxer.hikari.pool;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Wrapper;

public final class HikariProxyDatabaseMetaData extends ProxyDatabaseMetaData implements Wrapper, DatabaseMetaData {
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
    try {
      return this.delegate.isWrapperFor(paramClass);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean allProceduresAreCallable() throws SQLException {
    try {
      return this.delegate.allProceduresAreCallable();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean allTablesAreSelectable() throws SQLException {
    try {
      return this.delegate.allTablesAreSelectable();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getURL() throws SQLException {
    try {
      return this.delegate.getURL();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getUserName() throws SQLException {
    try {
      return this.delegate.getUserName();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isReadOnly() throws SQLException {
    try {
      return this.delegate.isReadOnly();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean nullsAreSortedHigh() throws SQLException {
    try {
      return this.delegate.nullsAreSortedHigh();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean nullsAreSortedLow() throws SQLException {
    try {
      return this.delegate.nullsAreSortedLow();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean nullsAreSortedAtStart() throws SQLException {
    try {
      return this.delegate.nullsAreSortedAtStart();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean nullsAreSortedAtEnd() throws SQLException {
    try {
      return this.delegate.nullsAreSortedAtEnd();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getDatabaseProductName() throws SQLException {
    try {
      return this.delegate.getDatabaseProductName();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getDatabaseProductVersion() throws SQLException {
    try {
      return this.delegate.getDatabaseProductVersion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getDriverName() throws SQLException {
    try {
      return this.delegate.getDriverName();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getDriverVersion() throws SQLException {
    try {
      return this.delegate.getDriverVersion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getDriverMajorVersion() {
    return this.delegate.getDriverMajorVersion();
  }
  
  public int getDriverMinorVersion() {
    return this.delegate.getDriverMinorVersion();
  }
  
  public boolean usesLocalFiles() throws SQLException {
    try {
      return this.delegate.usesLocalFiles();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean usesLocalFilePerTable() throws SQLException {
    try {
      return this.delegate.usesLocalFilePerTable();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsMixedCaseIdentifiers() throws SQLException {
    try {
      return this.delegate.supportsMixedCaseIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean storesUpperCaseIdentifiers() throws SQLException {
    try {
      return this.delegate.storesUpperCaseIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean storesLowerCaseIdentifiers() throws SQLException {
    try {
      return this.delegate.storesLowerCaseIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean storesMixedCaseIdentifiers() throws SQLException {
    try {
      return this.delegate.storesMixedCaseIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
    try {
      return this.delegate.supportsMixedCaseQuotedIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
    try {
      return this.delegate.storesUpperCaseQuotedIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
    try {
      return this.delegate.storesLowerCaseQuotedIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
    try {
      return this.delegate.storesMixedCaseQuotedIdentifiers();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getIdentifierQuoteString() throws SQLException {
    try {
      return this.delegate.getIdentifierQuoteString();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getSQLKeywords() throws SQLException {
    try {
      return this.delegate.getSQLKeywords();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getNumericFunctions() throws SQLException {
    try {
      return this.delegate.getNumericFunctions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getStringFunctions() throws SQLException {
    try {
      return this.delegate.getStringFunctions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getSystemFunctions() throws SQLException {
    try {
      return this.delegate.getSystemFunctions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getTimeDateFunctions() throws SQLException {
    try {
      return this.delegate.getTimeDateFunctions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getSearchStringEscape() throws SQLException {
    try {
      return this.delegate.getSearchStringEscape();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getExtraNameCharacters() throws SQLException {
    try {
      return this.delegate.getExtraNameCharacters();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsAlterTableWithAddColumn() throws SQLException {
    try {
      return this.delegate.supportsAlterTableWithAddColumn();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsAlterTableWithDropColumn() throws SQLException {
    try {
      return this.delegate.supportsAlterTableWithDropColumn();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsColumnAliasing() throws SQLException {
    try {
      return this.delegate.supportsColumnAliasing();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean nullPlusNonNullIsNull() throws SQLException {
    try {
      return this.delegate.nullPlusNonNullIsNull();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsConvert() throws SQLException {
    try {
      return this.delegate.supportsConvert();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsConvert(int paramInt1, int paramInt2) throws SQLException {
    try {
      return this.delegate.supportsConvert(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsTableCorrelationNames() throws SQLException {
    try {
      return this.delegate.supportsTableCorrelationNames();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsDifferentTableCorrelationNames() throws SQLException {
    try {
      return this.delegate.supportsDifferentTableCorrelationNames();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsExpressionsInOrderBy() throws SQLException {
    try {
      return this.delegate.supportsExpressionsInOrderBy();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsOrderByUnrelated() throws SQLException {
    try {
      return this.delegate.supportsOrderByUnrelated();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsGroupBy() throws SQLException {
    try {
      return this.delegate.supportsGroupBy();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsGroupByUnrelated() throws SQLException {
    try {
      return this.delegate.supportsGroupByUnrelated();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsGroupByBeyondSelect() throws SQLException {
    try {
      return this.delegate.supportsGroupByBeyondSelect();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsLikeEscapeClause() throws SQLException {
    try {
      return this.delegate.supportsLikeEscapeClause();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsMultipleResultSets() throws SQLException {
    try {
      return this.delegate.supportsMultipleResultSets();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsMultipleTransactions() throws SQLException {
    try {
      return this.delegate.supportsMultipleTransactions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsNonNullableColumns() throws SQLException {
    try {
      return this.delegate.supportsNonNullableColumns();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsMinimumSQLGrammar() throws SQLException {
    try {
      return this.delegate.supportsMinimumSQLGrammar();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCoreSQLGrammar() throws SQLException {
    try {
      return this.delegate.supportsCoreSQLGrammar();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsExtendedSQLGrammar() throws SQLException {
    try {
      return this.delegate.supportsExtendedSQLGrammar();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsANSI92EntryLevelSQL() throws SQLException {
    try {
      return this.delegate.supportsANSI92EntryLevelSQL();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsANSI92IntermediateSQL() throws SQLException {
    try {
      return this.delegate.supportsANSI92IntermediateSQL();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsANSI92FullSQL() throws SQLException {
    try {
      return this.delegate.supportsANSI92FullSQL();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsIntegrityEnhancementFacility() throws SQLException {
    try {
      return this.delegate.supportsIntegrityEnhancementFacility();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsOuterJoins() throws SQLException {
    try {
      return this.delegate.supportsOuterJoins();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsFullOuterJoins() throws SQLException {
    try {
      return this.delegate.supportsFullOuterJoins();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsLimitedOuterJoins() throws SQLException {
    try {
      return this.delegate.supportsLimitedOuterJoins();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getSchemaTerm() throws SQLException {
    try {
      return this.delegate.getSchemaTerm();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getProcedureTerm() throws SQLException {
    try {
      return this.delegate.getProcedureTerm();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getCatalogTerm() throws SQLException {
    try {
      return this.delegate.getCatalogTerm();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean isCatalogAtStart() throws SQLException {
    try {
      return this.delegate.isCatalogAtStart();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public String getCatalogSeparator() throws SQLException {
    try {
      return this.delegate.getCatalogSeparator();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSchemasInDataManipulation() throws SQLException {
    try {
      return this.delegate.supportsSchemasInDataManipulation();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSchemasInProcedureCalls() throws SQLException {
    try {
      return this.delegate.supportsSchemasInProcedureCalls();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSchemasInTableDefinitions() throws SQLException {
    try {
      return this.delegate.supportsSchemasInTableDefinitions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSchemasInIndexDefinitions() throws SQLException {
    try {
      return this.delegate.supportsSchemasInIndexDefinitions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
    try {
      return this.delegate.supportsSchemasInPrivilegeDefinitions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCatalogsInDataManipulation() throws SQLException {
    try {
      return this.delegate.supportsCatalogsInDataManipulation();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCatalogsInProcedureCalls() throws SQLException {
    try {
      return this.delegate.supportsCatalogsInProcedureCalls();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCatalogsInTableDefinitions() throws SQLException {
    try {
      return this.delegate.supportsCatalogsInTableDefinitions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
    try {
      return this.delegate.supportsCatalogsInIndexDefinitions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
    try {
      return this.delegate.supportsCatalogsInPrivilegeDefinitions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsPositionedDelete() throws SQLException {
    try {
      return this.delegate.supportsPositionedDelete();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsPositionedUpdate() throws SQLException {
    try {
      return this.delegate.supportsPositionedUpdate();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSelectForUpdate() throws SQLException {
    try {
      return this.delegate.supportsSelectForUpdate();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsStoredProcedures() throws SQLException {
    try {
      return this.delegate.supportsStoredProcedures();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSubqueriesInComparisons() throws SQLException {
    try {
      return this.delegate.supportsSubqueriesInComparisons();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSubqueriesInExists() throws SQLException {
    try {
      return this.delegate.supportsSubqueriesInExists();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSubqueriesInIns() throws SQLException {
    try {
      return this.delegate.supportsSubqueriesInIns();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSubqueriesInQuantifieds() throws SQLException {
    try {
      return this.delegate.supportsSubqueriesInQuantifieds();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsCorrelatedSubqueries() throws SQLException {
    try {
      return this.delegate.supportsCorrelatedSubqueries();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsUnion() throws SQLException {
    try {
      return this.delegate.supportsUnion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsUnionAll() throws SQLException {
    try {
      return this.delegate.supportsUnionAll();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
    try {
      return this.delegate.supportsOpenCursorsAcrossCommit();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
    try {
      return this.delegate.supportsOpenCursorsAcrossRollback();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
    try {
      return this.delegate.supportsOpenStatementsAcrossCommit();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
    try {
      return this.delegate.supportsOpenStatementsAcrossRollback();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxBinaryLiteralLength() throws SQLException {
    try {
      return this.delegate.getMaxBinaryLiteralLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxCharLiteralLength() throws SQLException {
    try {
      return this.delegate.getMaxCharLiteralLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxColumnNameLength() throws SQLException {
    try {
      return this.delegate.getMaxColumnNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxColumnsInGroupBy() throws SQLException {
    try {
      return this.delegate.getMaxColumnsInGroupBy();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxColumnsInIndex() throws SQLException {
    try {
      return this.delegate.getMaxColumnsInIndex();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxColumnsInOrderBy() throws SQLException {
    try {
      return this.delegate.getMaxColumnsInOrderBy();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxColumnsInSelect() throws SQLException {
    try {
      return this.delegate.getMaxColumnsInSelect();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxColumnsInTable() throws SQLException {
    try {
      return this.delegate.getMaxColumnsInTable();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxConnections() throws SQLException {
    try {
      return this.delegate.getMaxConnections();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxCursorNameLength() throws SQLException {
    try {
      return this.delegate.getMaxCursorNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxIndexLength() throws SQLException {
    try {
      return this.delegate.getMaxIndexLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxSchemaNameLength() throws SQLException {
    try {
      return this.delegate.getMaxSchemaNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxProcedureNameLength() throws SQLException {
    try {
      return this.delegate.getMaxProcedureNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxCatalogNameLength() throws SQLException {
    try {
      return this.delegate.getMaxCatalogNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxRowSize() throws SQLException {
    try {
      return this.delegate.getMaxRowSize();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
    try {
      return this.delegate.doesMaxRowSizeIncludeBlobs();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxStatementLength() throws SQLException {
    try {
      return this.delegate.getMaxStatementLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxStatements() throws SQLException {
    try {
      return this.delegate.getMaxStatements();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxTableNameLength() throws SQLException {
    try {
      return this.delegate.getMaxTableNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxTablesInSelect() throws SQLException {
    try {
      return this.delegate.getMaxTablesInSelect();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getMaxUserNameLength() throws SQLException {
    try {
      return this.delegate.getMaxUserNameLength();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getDefaultTransactionIsolation() throws SQLException {
    try {
      return this.delegate.getDefaultTransactionIsolation();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsTransactions() throws SQLException {
    try {
      return this.delegate.supportsTransactions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsTransactionIsolationLevel(int paramInt) throws SQLException {
    try {
      return this.delegate.supportsTransactionIsolationLevel(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
    try {
      return this.delegate.supportsDataDefinitionAndDataManipulationTransactions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
    try {
      return this.delegate.supportsDataManipulationTransactionsOnly();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
    try {
      return this.delegate.dataDefinitionCausesTransactionCommit();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
    try {
      return this.delegate.dataDefinitionIgnoredInTransactions();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getProcedures(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getProcedures(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    try {
      return super.getProcedureColumns(paramString1, paramString2, paramString3, paramString4);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) throws SQLException {
    try {
      return super.getTables(paramString1, paramString2, paramString3, paramArrayOfString);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getSchemas() throws SQLException {
    try {
      return super.getSchemas();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getCatalogs() throws SQLException {
    try {
      return super.getCatalogs();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getTableTypes() throws SQLException {
    try {
      return super.getTableTypes();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    try {
      return super.getColumns(paramString1, paramString2, paramString3, paramString4);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    try {
      return super.getColumnPrivileges(paramString1, paramString2, paramString3, paramString4);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getTablePrivileges(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getTablePrivileges(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean) throws SQLException {
    try {
      return super.getBestRowIdentifier(paramString1, paramString2, paramString3, paramInt, paramBoolean);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getVersionColumns(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getVersionColumns(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getPrimaryKeys(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getPrimaryKeys(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getImportedKeys(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getImportedKeys(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getExportedKeys(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getExportedKeys(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) throws SQLException {
    try {
      return super.getCrossReference(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getTypeInfo() throws SQLException {
    try {
      return super.getTypeInfo();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2) throws SQLException {
    try {
      return super.getIndexInfo(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsResultSetType(int paramInt) throws SQLException {
    try {
      return this.delegate.supportsResultSetType(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsResultSetConcurrency(int paramInt1, int paramInt2) throws SQLException {
    try {
      return this.delegate.supportsResultSetConcurrency(paramInt1, paramInt2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean ownUpdatesAreVisible(int paramInt) throws SQLException {
    try {
      return this.delegate.ownUpdatesAreVisible(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean ownDeletesAreVisible(int paramInt) throws SQLException {
    try {
      return this.delegate.ownDeletesAreVisible(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean ownInsertsAreVisible(int paramInt) throws SQLException {
    try {
      return this.delegate.ownInsertsAreVisible(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean othersUpdatesAreVisible(int paramInt) throws SQLException {
    try {
      return this.delegate.othersUpdatesAreVisible(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean othersDeletesAreVisible(int paramInt) throws SQLException {
    try {
      return this.delegate.othersDeletesAreVisible(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean othersInsertsAreVisible(int paramInt) throws SQLException {
    try {
      return this.delegate.othersInsertsAreVisible(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean updatesAreDetected(int paramInt) throws SQLException {
    try {
      return this.delegate.updatesAreDetected(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean deletesAreDetected(int paramInt) throws SQLException {
    try {
      return this.delegate.deletesAreDetected(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean insertsAreDetected(int paramInt) throws SQLException {
    try {
      return this.delegate.insertsAreDetected(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsBatchUpdates() throws SQLException {
    try {
      return this.delegate.supportsBatchUpdates();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfint) throws SQLException {
    try {
      return super.getUDTs(paramString1, paramString2, paramString3, paramArrayOfint);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsSavepoints() throws SQLException {
    try {
      return this.delegate.supportsSavepoints();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsNamedParameters() throws SQLException {
    try {
      return this.delegate.supportsNamedParameters();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsMultipleOpenResults() throws SQLException {
    try {
      return this.delegate.supportsMultipleOpenResults();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsGetGeneratedKeys() throws SQLException {
    try {
      return this.delegate.supportsGetGeneratedKeys();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getSuperTypes(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getSuperTypes(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getSuperTables(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getSuperTables(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getAttributes(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    try {
      return super.getAttributes(paramString1, paramString2, paramString3, paramString4);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsResultSetHoldability(int paramInt) throws SQLException {
    try {
      return this.delegate.supportsResultSetHoldability(paramInt);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getResultSetHoldability() throws SQLException {
    try {
      return this.delegate.getResultSetHoldability();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getDatabaseMajorVersion() throws SQLException {
    try {
      return this.delegate.getDatabaseMajorVersion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getDatabaseMinorVersion() throws SQLException {
    try {
      return this.delegate.getDatabaseMinorVersion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getJDBCMajorVersion() throws SQLException {
    try {
      return this.delegate.getJDBCMajorVersion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getJDBCMinorVersion() throws SQLException {
    try {
      return this.delegate.getJDBCMinorVersion();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public int getSQLStateType() throws SQLException {
    try {
      return this.delegate.getSQLStateType();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean locatorsUpdateCopy() throws SQLException {
    try {
      return this.delegate.locatorsUpdateCopy();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsStatementPooling() throws SQLException {
    try {
      return this.delegate.supportsStatementPooling();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public RowIdLifetime getRowIdLifetime() throws SQLException {
    try {
      return this.delegate.getRowIdLifetime();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getSchemas(String paramString1, String paramString2) throws SQLException {
    try {
      return super.getSchemas(paramString1, paramString2);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
    try {
      return this.delegate.supportsStoredFunctionsUsingCallSyntax();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
    try {
      return this.delegate.autoCommitFailureClosesAllResultSets();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getClientInfoProperties() throws SQLException {
    try {
      return super.getClientInfoProperties();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getFunctions(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      return super.getFunctions(paramString1, paramString2, paramString3);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getFunctionColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    try {
      return super.getFunctionColumns(paramString1, paramString2, paramString3, paramString4);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public ResultSet getPseudoColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
    try {
      return super.getPseudoColumns(paramString1, paramString2, paramString3, paramString4);
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean generatedKeyAlwaysReturned() throws SQLException {
    try {
      return this.delegate.generatedKeyAlwaysReturned();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public long getMaxLogicalLobSize() throws SQLException {
    try {
      return this.delegate.getMaxLogicalLobSize();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  public boolean supportsRefCursors() throws SQLException {
    try {
      return this.delegate.supportsRefCursors();
    } catch (SQLException sQLException) {
      throw checkException(sQLException);
    } 
  }
  
  HikariProxyDatabaseMetaData(ProxyConnection paramProxyConnection, DatabaseMetaData paramDatabaseMetaData) {
    super(paramProxyConnection, paramDatabaseMetaData);
  }
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\zaxxer\hikari\pool\HikariProxyDatabaseMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */