/*      */ package org.h2.jdbc;
/*      */ 
/*      */ import java.sql.Connection;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.RowIdLifetime;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import org.h2.engine.Constants;
/*      */ import org.h2.engine.Session;
/*      */ import org.h2.jdbc.meta.DatabaseMeta;
/*      */ import org.h2.jdbc.meta.DatabaseMetaLegacy;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.message.Trace;
/*      */ import org.h2.message.TraceObject;
/*      */ import org.h2.mode.DefaultNullOrdering;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.SimpleResult;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueVarchar;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class JdbcDatabaseMetaData
/*      */   extends TraceObject
/*      */   implements DatabaseMetaData, JdbcDatabaseMetaDataBackwardsCompat
/*      */ {
/*      */   private final JdbcConnection conn;
/*      */   private final DatabaseMeta meta;
/*      */   
/*      */   JdbcDatabaseMetaData(JdbcConnection paramJdbcConnection, Trace paramTrace, int paramInt) {
/*   41 */     setTrace(paramTrace, 2, paramInt);
/*   42 */     this.conn = paramJdbcConnection;
/*   43 */     Session session = paramJdbcConnection.getSession();
/*   44 */     this
/*   45 */       .meta = session.isOldInformationSchema() ? (DatabaseMeta)new DatabaseMetaLegacy(session) : paramJdbcConnection.getSession().getDatabaseMeta();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDriverMajorVersion() {
/*   55 */     debugCodeCall("getDriverMajorVersion");
/*   56 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDriverMinorVersion() {
/*   66 */     debugCodeCall("getDriverMinorVersion");
/*   67 */     return 1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDatabaseProductName() {
/*   77 */     debugCodeCall("getDatabaseProductName");
/*      */ 
/*      */     
/*   80 */     return "H2";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDatabaseProductVersion() throws SQLException {
/*      */     try {
/*   91 */       debugCodeCall("getDatabaseProductVersion");
/*   92 */       return this.meta.getDatabaseProductVersion();
/*   93 */     } catch (Exception exception) {
/*   94 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDriverName() {
/*  105 */     debugCodeCall("getDriverName");
/*  106 */     return "H2 JDBC Driver";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDriverVersion() {
/*  117 */     debugCodeCall("getDriverVersion");
/*  118 */     return Constants.FULL_VERSION;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) throws SQLException {
/*      */     try {
/*  152 */       if (isDebugEnabled()) {
/*  153 */         debugCode("getTables(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ", " + 
/*  154 */             quoteArray(paramArrayOfString) + ')');
/*      */       }
/*  156 */       return getResultSet(this.meta.getTables(paramString1, paramString2, paramString3, paramArrayOfString));
/*  157 */     } catch (Exception exception) {
/*  158 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/*      */     try {
/*  208 */       if (isDebugEnabled()) {
/*  209 */         debugCode("getColumns(" + quote(paramString1) + ", " + 
/*  210 */             quote(paramString2) + ", " + 
/*  211 */             quote(paramString3) + ", " + 
/*  212 */             quote(paramString4) + ')');
/*      */       }
/*  214 */       return getResultSet(this.meta.getColumns(paramString1, paramString2, paramString3, paramString4));
/*  215 */     } catch (Exception exception) {
/*  216 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2) throws SQLException {
/*      */     try {
/*  258 */       if (isDebugEnabled()) {
/*  259 */         debugCode("getIndexInfo(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ", " + paramBoolean1 + ", " + paramBoolean2 + ')');
/*      */       }
/*      */       
/*  262 */       return getResultSet(this.meta.getIndexInfo(paramString1, paramString2, paramString3, paramBoolean1, paramBoolean2));
/*  263 */     } catch (Exception exception) {
/*  264 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getPrimaryKeys(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/*  291 */       if (isDebugEnabled()) {
/*  292 */         debugCode("getPrimaryKeys(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ')');
/*      */       }
/*  294 */       return getResultSet(this.meta.getPrimaryKeys(paramString1, paramString2, paramString3));
/*  295 */     } catch (Exception exception) {
/*  296 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean allProceduresAreCallable() {
/*  307 */     debugCodeCall("allProceduresAreCallable");
/*  308 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean allTablesAreSelectable() {
/*  318 */     debugCodeCall("allTablesAreSelectable");
/*  319 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getURL() throws SQLException {
/*      */     try {
/*  330 */       debugCodeCall("getURL");
/*  331 */       return this.conn.getURL();
/*  332 */     } catch (Exception exception) {
/*  333 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getUserName() throws SQLException {
/*      */     try {
/*  346 */       debugCodeCall("getUserName");
/*  347 */       return this.conn.getUser();
/*  348 */     } catch (Exception exception) {
/*  349 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() throws SQLException {
/*      */     try {
/*  361 */       debugCodeCall("isReadOnly");
/*  362 */       return this.conn.isReadOnly();
/*  363 */     } catch (Exception exception) {
/*  364 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedHigh() throws SQLException {
/*      */     try {
/*  377 */       debugCodeCall("nullsAreSortedHigh");
/*  378 */       return (this.meta.defaultNullOrdering() == DefaultNullOrdering.HIGH);
/*  379 */     } catch (Exception exception) {
/*  380 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedLow() throws SQLException {
/*      */     try {
/*  393 */       debugCodeCall("nullsAreSortedLow");
/*  394 */       return (this.meta.defaultNullOrdering() == DefaultNullOrdering.LOW);
/*  395 */     } catch (Exception exception) {
/*  396 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedAtStart() throws SQLException {
/*      */     try {
/*  409 */       debugCodeCall("nullsAreSortedAtStart");
/*  410 */       return (this.meta.defaultNullOrdering() == DefaultNullOrdering.FIRST);
/*  411 */     } catch (Exception exception) {
/*  412 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullsAreSortedAtEnd() throws SQLException {
/*      */     try {
/*  425 */       debugCodeCall("nullsAreSortedAtEnd");
/*  426 */       return (this.meta.defaultNullOrdering() == DefaultNullOrdering.LAST);
/*  427 */     } catch (Exception exception) {
/*  428 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Connection getConnection() {
/*  439 */     debugCodeCall("getConnection");
/*  440 */     return this.conn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getProcedures(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/*  474 */       if (isDebugEnabled()) {
/*  475 */         debugCode("getProcedures(" + 
/*  476 */             quote(paramString1) + ", " + 
/*  477 */             quote(paramString2) + ", " + 
/*  478 */             quote(paramString3) + ')');
/*      */       }
/*  480 */       return getResultSet(this.meta.getProcedures(paramString1, paramString2, paramString3));
/*  481 */     } catch (Exception exception) {
/*  482 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/*      */     try {
/*  532 */       if (isDebugEnabled()) {
/*  533 */         debugCode("getProcedureColumns(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/*  534 */             quote(paramString3) + ", " + quote(paramString4) + ')');
/*      */       }
/*  536 */       checkClosed();
/*  537 */       return getResultSet(this.meta
/*  538 */           .getProcedureColumns(paramString1, paramString2, paramString3, paramString4));
/*  539 */     } catch (Exception exception) {
/*  540 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSchemas() throws SQLException {
/*      */     try {
/*  559 */       debugCodeCall("getSchemas");
/*  560 */       return getResultSet(this.meta.getSchemas());
/*  561 */     } catch (Exception exception) {
/*  562 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getCatalogs() throws SQLException {
/*      */     try {
/*  580 */       debugCodeCall("getCatalogs");
/*  581 */       return getResultSet(this.meta.getCatalogs());
/*  582 */     } catch (Exception exception) {
/*  583 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTableTypes() throws SQLException {
/*      */     try {
/*  600 */       debugCodeCall("getTableTypes");
/*  601 */       return getResultSet(this.meta.getTableTypes());
/*  602 */     } catch (Exception exception) {
/*  603 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/*      */     try {
/*  637 */       if (isDebugEnabled()) {
/*  638 */         debugCode("getColumnPrivileges(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ", " + 
/*  639 */             quote(paramString4) + ')');
/*      */       }
/*  641 */       return getResultSet(this.meta.getColumnPrivileges(paramString1, paramString2, paramString3, paramString4));
/*  642 */     } catch (Exception exception) {
/*  643 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTablePrivileges(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/*  675 */       if (isDebugEnabled()) {
/*  676 */         debugCode("getTablePrivileges(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/*  677 */             quote(paramString3) + ')');
/*      */       }
/*  679 */       checkClosed();
/*  680 */       return getResultSet(this.meta.getTablePrivileges(paramString1, paramString2, paramString3));
/*  681 */     } catch (Exception exception) {
/*  682 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean) throws SQLException {
/*      */     try {
/*  715 */       if (isDebugEnabled()) {
/*  716 */         debugCode("getBestRowIdentifier(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ", " + paramInt + ", " + paramBoolean + ')');
/*      */       }
/*      */       
/*  719 */       return getResultSet(this.meta.getBestRowIdentifier(paramString1, paramString2, paramString3, paramInt, paramBoolean));
/*  720 */     } catch (Exception exception) {
/*  721 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getVersionColumns(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/*  750 */       if (isDebugEnabled()) {
/*  751 */         debugCode("getVersionColumns(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ')');
/*      */       }
/*  753 */       return getResultSet(this.meta.getVersionColumns(paramString1, paramString2, paramString3));
/*  754 */     } catch (Exception exception) {
/*  755 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getImportedKeys(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/*  793 */       if (isDebugEnabled()) {
/*  794 */         debugCode("getImportedKeys(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ')');
/*      */       }
/*  796 */       return getResultSet(this.meta.getImportedKeys(paramString1, paramString2, paramString3));
/*  797 */     } catch (Exception exception) {
/*  798 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getExportedKeys(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/*  836 */       if (isDebugEnabled()) {
/*  837 */         debugCode("getExportedKeys(" + quote(paramString1) + ", " + quote(paramString2) + ", " + quote(paramString3) + ')');
/*      */       }
/*  839 */       return getResultSet(this.meta.getExportedKeys(paramString1, paramString2, paramString3));
/*  840 */     } catch (Exception exception) {
/*  841 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) throws SQLException {
/*      */     try {
/*  886 */       if (isDebugEnabled()) {
/*  887 */         debugCode("getCrossReference(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/*  888 */             quote(paramString3) + ", " + quote(paramString4) + ", " + quote(paramString5) + ", " + 
/*  889 */             quote(paramString6) + ')');
/*      */       }
/*  891 */       return getResultSet(this.meta.getCrossReference(paramString1, paramString2, paramString3, paramString4, paramString5, paramString6));
/*      */     }
/*  893 */     catch (Exception exception) {
/*  894 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfint) throws SQLException {
/*      */     try {
/*  923 */       if (isDebugEnabled()) {
/*  924 */         debugCode("getUDTs(" + 
/*  925 */             quote(paramString1) + ", " + 
/*  926 */             quote(paramString2) + ", " + 
/*  927 */             quote(paramString3) + ", " + 
/*  928 */             quoteIntArray(paramArrayOfint) + ')');
/*      */       }
/*  930 */       return getResultSet(this.meta.getUDTs(paramString1, paramString2, paramString3, paramArrayOfint));
/*  931 */     } catch (Exception exception) {
/*  932 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getTypeInfo() throws SQLException {
/*      */     try {
/*  968 */       debugCodeCall("getTypeInfo");
/*  969 */       return getResultSet(this.meta.getTypeInfo());
/*  970 */     } catch (Exception exception) {
/*  971 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean usesLocalFiles() {
/*  982 */     debugCodeCall("usesLocalFiles");
/*  983 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean usesLocalFilePerTable() {
/*  993 */     debugCodeCall("usesLocalFilePerTable");
/*  994 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getIdentifierQuoteString() {
/* 1004 */     debugCodeCall("getIdentifierQuoteString");
/* 1005 */     return "\"";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSQLKeywords() throws SQLException {
/*      */     try {
/* 1021 */       debugCodeCall("getSQLKeywords");
/* 1022 */       return this.meta.getSQLKeywords();
/* 1023 */     } catch (Exception exception) {
/* 1024 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNumericFunctions() throws SQLException {
/*      */     try {
/* 1036 */       debugCodeCall("getNumericFunctions");
/* 1037 */       return this.meta.getNumericFunctions();
/* 1038 */     } catch (Exception exception) {
/* 1039 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getStringFunctions() throws SQLException {
/*      */     try {
/* 1051 */       debugCodeCall("getStringFunctions");
/* 1052 */       return this.meta.getStringFunctions();
/* 1053 */     } catch (Exception exception) {
/* 1054 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSystemFunctions() throws SQLException {
/*      */     try {
/* 1066 */       debugCodeCall("getSystemFunctions");
/* 1067 */       return this.meta.getSystemFunctions();
/* 1068 */     } catch (Exception exception) {
/* 1069 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getTimeDateFunctions() throws SQLException {
/*      */     try {
/* 1081 */       debugCodeCall("getTimeDateFunctions");
/* 1082 */       return this.meta.getTimeDateFunctions();
/* 1083 */     } catch (Exception exception) {
/* 1084 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSearchStringEscape() throws SQLException {
/*      */     try {
/* 1098 */       debugCodeCall("getSearchStringEscape");
/* 1099 */       return this.meta.getSearchStringEscape();
/* 1100 */     } catch (Exception exception) {
/* 1101 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getExtraNameCharacters() {
/* 1113 */     debugCodeCall("getExtraNameCharacters");
/* 1114 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsAlterTableWithAddColumn() {
/* 1124 */     debugCodeCall("supportsAlterTableWithAddColumn");
/* 1125 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsAlterTableWithDropColumn() {
/* 1135 */     debugCodeCall("supportsAlterTableWithDropColumn");
/* 1136 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsColumnAliasing() {
/* 1146 */     debugCodeCall("supportsColumnAliasing");
/* 1147 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nullPlusNonNullIsNull() {
/* 1157 */     debugCodeCall("nullPlusNonNullIsNull");
/* 1158 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsConvert() {
/* 1168 */     debugCodeCall("supportsConvert");
/* 1169 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsConvert(int paramInt1, int paramInt2) {
/* 1181 */     if (isDebugEnabled()) {
/* 1182 */       debugCode("supportsConvert(" + paramInt1 + ", " + paramInt2 + ')');
/*      */     }
/* 1184 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTableCorrelationNames() {
/* 1194 */     debugCodeCall("supportsTableCorrelationNames");
/* 1195 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsDifferentTableCorrelationNames() {
/* 1206 */     debugCodeCall("supportsDifferentTableCorrelationNames");
/* 1207 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsExpressionsInOrderBy() {
/* 1217 */     debugCodeCall("supportsExpressionsInOrderBy");
/* 1218 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOrderByUnrelated() {
/* 1229 */     debugCodeCall("supportsOrderByUnrelated");
/* 1230 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGroupBy() {
/* 1240 */     debugCodeCall("supportsGroupBy");
/* 1241 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGroupByUnrelated() {
/* 1252 */     debugCodeCall("supportsGroupByUnrelated");
/* 1253 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGroupByBeyondSelect() {
/* 1265 */     debugCodeCall("supportsGroupByBeyondSelect");
/* 1266 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsLikeEscapeClause() {
/* 1276 */     debugCodeCall("supportsLikeEscapeClause");
/* 1277 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMultipleResultSets() {
/* 1287 */     debugCodeCall("supportsMultipleResultSets");
/* 1288 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMultipleTransactions() {
/* 1299 */     debugCodeCall("supportsMultipleTransactions");
/* 1300 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsNonNullableColumns() {
/* 1310 */     debugCodeCall("supportsNonNullableColumns");
/* 1311 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMinimumSQLGrammar() {
/* 1321 */     debugCodeCall("supportsMinimumSQLGrammar");
/* 1322 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCoreSQLGrammar() {
/* 1332 */     debugCodeCall("supportsCoreSQLGrammar");
/* 1333 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsExtendedSQLGrammar() {
/* 1343 */     debugCodeCall("supportsExtendedSQLGrammar");
/* 1344 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsANSI92EntryLevelSQL() {
/* 1354 */     debugCodeCall("supportsANSI92EntryLevelSQL");
/* 1355 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsANSI92IntermediateSQL() {
/* 1365 */     debugCodeCall("supportsANSI92IntermediateSQL");
/* 1366 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsANSI92FullSQL() {
/* 1376 */     debugCodeCall("supportsANSI92FullSQL");
/* 1377 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsIntegrityEnhancementFacility() {
/* 1387 */     debugCodeCall("supportsIntegrityEnhancementFacility");
/* 1388 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOuterJoins() {
/* 1398 */     debugCodeCall("supportsOuterJoins");
/* 1399 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsFullOuterJoins() {
/* 1409 */     debugCodeCall("supportsFullOuterJoins");
/* 1410 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsLimitedOuterJoins() {
/* 1420 */     debugCodeCall("supportsLimitedOuterJoins");
/* 1421 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSchemaTerm() {
/* 1431 */     debugCodeCall("getSchemaTerm");
/* 1432 */     return "schema";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getProcedureTerm() {
/* 1442 */     debugCodeCall("getProcedureTerm");
/* 1443 */     return "procedure";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCatalogTerm() {
/* 1453 */     debugCodeCall("getCatalogTerm");
/* 1454 */     return "catalog";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isCatalogAtStart() {
/* 1464 */     debugCodeCall("isCatalogAtStart");
/* 1465 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getCatalogSeparator() {
/* 1475 */     debugCodeCall("getCatalogSeparator");
/* 1476 */     return ".";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInDataManipulation() {
/* 1486 */     debugCodeCall("supportsSchemasInDataManipulation");
/* 1487 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInProcedureCalls() {
/* 1497 */     debugCodeCall("supportsSchemasInProcedureCalls");
/* 1498 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInTableDefinitions() {
/* 1508 */     debugCodeCall("supportsSchemasInTableDefinitions");
/* 1509 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInIndexDefinitions() {
/* 1519 */     debugCodeCall("supportsSchemasInIndexDefinitions");
/* 1520 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSchemasInPrivilegeDefinitions() {
/* 1530 */     debugCodeCall("supportsSchemasInPrivilegeDefinitions");
/* 1531 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInDataManipulation() {
/* 1541 */     debugCodeCall("supportsCatalogsInDataManipulation");
/* 1542 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInProcedureCalls() {
/* 1552 */     debugCodeCall("supportsCatalogsInProcedureCalls");
/* 1553 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInTableDefinitions() {
/* 1563 */     debugCodeCall("supportsCatalogsInTableDefinitions");
/* 1564 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInIndexDefinitions() {
/* 1574 */     debugCodeCall("supportsCatalogsInIndexDefinitions");
/* 1575 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions() {
/* 1585 */     debugCodeCall("supportsCatalogsInPrivilegeDefinitions");
/* 1586 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsPositionedDelete() {
/* 1596 */     debugCodeCall("supportsPositionedDelete");
/* 1597 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsPositionedUpdate() {
/* 1607 */     debugCodeCall("supportsPositionedUpdate");
/* 1608 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSelectForUpdate() {
/* 1618 */     debugCodeCall("supportsSelectForUpdate");
/* 1619 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsStoredProcedures() {
/* 1629 */     debugCodeCall("supportsStoredProcedures");
/* 1630 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInComparisons() {
/* 1640 */     debugCodeCall("supportsSubqueriesInComparisons");
/* 1641 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInExists() {
/* 1651 */     debugCodeCall("supportsSubqueriesInExists");
/* 1652 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInIns() {
/* 1662 */     debugCodeCall("supportsSubqueriesInIns");
/* 1663 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSubqueriesInQuantifieds() {
/* 1673 */     debugCodeCall("supportsSubqueriesInQuantifieds");
/* 1674 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsCorrelatedSubqueries() {
/* 1684 */     debugCodeCall("supportsCorrelatedSubqueries");
/* 1685 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsUnion() {
/* 1695 */     debugCodeCall("supportsUnion");
/* 1696 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsUnionAll() {
/* 1706 */     debugCodeCall("supportsUnionAll");
/* 1707 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenCursorsAcrossCommit() {
/* 1717 */     debugCodeCall("supportsOpenCursorsAcrossCommit");
/* 1718 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenCursorsAcrossRollback() {
/* 1728 */     debugCodeCall("supportsOpenCursorsAcrossRollback");
/* 1729 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenStatementsAcrossCommit() {
/* 1739 */     debugCodeCall("supportsOpenStatementsAcrossCommit");
/* 1740 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsOpenStatementsAcrossRollback() {
/* 1750 */     debugCodeCall("supportsOpenStatementsAcrossRollback");
/* 1751 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTransactions() {
/* 1761 */     debugCodeCall("supportsTransactions");
/* 1762 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsTransactionIsolationLevel(int paramInt) throws SQLException {
/* 1773 */     debugCodeCall("supportsTransactionIsolationLevel");
/* 1774 */     switch (paramInt) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 6:
/*      */       case 8:
/* 1780 */         return true;
/*      */     } 
/* 1782 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions() {
/* 1794 */     debugCodeCall("supportsDataDefinitionAndDataManipulationTransactions");
/* 1795 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsDataManipulationTransactionsOnly() {
/* 1805 */     debugCodeCall("supportsDataManipulationTransactionsOnly");
/* 1806 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean dataDefinitionCausesTransactionCommit() {
/* 1816 */     debugCodeCall("dataDefinitionCausesTransactionCommit");
/* 1817 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean dataDefinitionIgnoredInTransactions() {
/* 1827 */     debugCodeCall("dataDefinitionIgnoredInTransactions");
/* 1828 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsResultSetType(int paramInt) {
/* 1840 */     debugCodeCall("supportsResultSetType", paramInt);
/* 1841 */     return (paramInt != 1005);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsResultSetConcurrency(int paramInt1, int paramInt2) {
/* 1854 */     if (isDebugEnabled()) {
/* 1855 */       debugCode("supportsResultSetConcurrency(" + paramInt1 + ", " + paramInt2 + ')');
/*      */     }
/* 1857 */     return (paramInt1 != 1005);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ownUpdatesAreVisible(int paramInt) {
/* 1868 */     debugCodeCall("ownUpdatesAreVisible", paramInt);
/* 1869 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ownDeletesAreVisible(int paramInt) {
/* 1880 */     debugCodeCall("ownDeletesAreVisible", paramInt);
/* 1881 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean ownInsertsAreVisible(int paramInt) {
/* 1892 */     debugCodeCall("ownInsertsAreVisible", paramInt);
/* 1893 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean othersUpdatesAreVisible(int paramInt) {
/* 1904 */     debugCodeCall("othersUpdatesAreVisible", paramInt);
/* 1905 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean othersDeletesAreVisible(int paramInt) {
/* 1916 */     debugCodeCall("othersDeletesAreVisible", paramInt);
/* 1917 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean othersInsertsAreVisible(int paramInt) {
/* 1928 */     debugCodeCall("othersInsertsAreVisible", paramInt);
/* 1929 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean updatesAreDetected(int paramInt) {
/* 1940 */     debugCodeCall("updatesAreDetected", paramInt);
/* 1941 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean deletesAreDetected(int paramInt) {
/* 1952 */     debugCodeCall("deletesAreDetected", paramInt);
/* 1953 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean insertsAreDetected(int paramInt) {
/* 1964 */     debugCodeCall("insertsAreDetected", paramInt);
/* 1965 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsBatchUpdates() {
/* 1975 */     debugCodeCall("supportsBatchUpdates");
/* 1976 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean doesMaxRowSizeIncludeBlobs() {
/* 1986 */     debugCodeCall("doesMaxRowSizeIncludeBlobs");
/* 1987 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDefaultTransactionIsolation() {
/* 1997 */     debugCodeCall("getDefaultTransactionIsolation");
/* 1998 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMixedCaseIdentifiers() throws SQLException {
/* 2009 */     debugCodeCall("supportsMixedCaseIdentifiers");
/* 2010 */     Session.StaticSettings staticSettings = this.conn.getStaticSettings();
/* 2011 */     return (!staticSettings.databaseToUpper && !staticSettings.databaseToLower && !staticSettings.caseInsensitiveIdentifiers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesUpperCaseIdentifiers() throws SQLException {
/* 2022 */     debugCodeCall("storesUpperCaseIdentifiers");
/* 2023 */     return (this.conn.getStaticSettings()).databaseToUpper;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesLowerCaseIdentifiers() throws SQLException {
/* 2034 */     debugCodeCall("storesLowerCaseIdentifiers");
/* 2035 */     return (this.conn.getStaticSettings()).databaseToLower;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesMixedCaseIdentifiers() throws SQLException {
/* 2046 */     debugCodeCall("storesMixedCaseIdentifiers");
/* 2047 */     Session.StaticSettings staticSettings = this.conn.getStaticSettings();
/* 2048 */     return (!staticSettings.databaseToUpper && !staticSettings.databaseToLower && staticSettings.caseInsensitiveIdentifiers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
/* 2059 */     debugCodeCall("supportsMixedCaseQuotedIdentifiers");
/* 2060 */     return !(this.conn.getStaticSettings()).caseInsensitiveIdentifiers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
/* 2071 */     debugCodeCall("storesUpperCaseQuotedIdentifiers");
/* 2072 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
/* 2083 */     debugCodeCall("storesLowerCaseQuotedIdentifiers");
/* 2084 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
/* 2095 */     debugCodeCall("storesMixedCaseQuotedIdentifiers");
/* 2096 */     return (this.conn.getStaticSettings()).caseInsensitiveIdentifiers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxBinaryLiteralLength() {
/* 2106 */     debugCodeCall("getMaxBinaryLiteralLength");
/* 2107 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCharLiteralLength() {
/* 2117 */     debugCodeCall("getMaxCharLiteralLength");
/* 2118 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnNameLength() {
/* 2128 */     debugCodeCall("getMaxColumnNameLength");
/* 2129 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInGroupBy() {
/* 2139 */     debugCodeCall("getMaxColumnsInGroupBy");
/* 2140 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInIndex() {
/* 2150 */     debugCodeCall("getMaxColumnsInIndex");
/* 2151 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInOrderBy() {
/* 2161 */     debugCodeCall("getMaxColumnsInOrderBy");
/* 2162 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInSelect() {
/* 2172 */     debugCodeCall("getMaxColumnsInSelect");
/* 2173 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxColumnsInTable() {
/* 2183 */     debugCodeCall("getMaxColumnsInTable");
/* 2184 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxConnections() {
/* 2194 */     debugCodeCall("getMaxConnections");
/* 2195 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCursorNameLength() {
/* 2205 */     debugCodeCall("getMaxCursorNameLength");
/* 2206 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxIndexLength() {
/* 2216 */     debugCodeCall("getMaxIndexLength");
/* 2217 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxSchemaNameLength() {
/* 2227 */     debugCodeCall("getMaxSchemaNameLength");
/* 2228 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxProcedureNameLength() {
/* 2238 */     debugCodeCall("getMaxProcedureNameLength");
/* 2239 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxCatalogNameLength() {
/* 2249 */     debugCodeCall("getMaxCatalogNameLength");
/* 2250 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxRowSize() {
/* 2260 */     debugCodeCall("getMaxRowSize");
/* 2261 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxStatementLength() {
/* 2271 */     debugCodeCall("getMaxStatementLength");
/* 2272 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxStatements() {
/* 2282 */     debugCodeCall("getMaxStatements");
/* 2283 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxTableNameLength() {
/* 2293 */     debugCodeCall("getMaxTableNameLength");
/* 2294 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxTablesInSelect() {
/* 2304 */     debugCodeCall("getMaxTablesInSelect");
/* 2305 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxUserNameLength() {
/* 2315 */     debugCodeCall("getMaxUserNameLength");
/* 2316 */     return 0;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsSavepoints() {
/* 2326 */     debugCodeCall("supportsSavepoints");
/* 2327 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsNamedParameters() {
/* 2337 */     debugCodeCall("supportsNamedParameters");
/* 2338 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsMultipleOpenResults() {
/* 2349 */     debugCodeCall("supportsMultipleOpenResults");
/* 2350 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsGetGeneratedKeys() {
/* 2360 */     debugCodeCall("supportsGetGeneratedKeys");
/* 2361 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSuperTypes(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/* 2370 */       if (isDebugEnabled()) {
/* 2371 */         debugCode("getSuperTypes(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/* 2372 */             quote(paramString3) + ')');
/*      */       }
/* 2374 */       return getResultSet(this.meta.getSuperTypes(paramString1, paramString2, paramString3));
/* 2375 */     } catch (Exception exception) {
/* 2376 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSuperTables(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/* 2401 */       if (isDebugEnabled()) {
/* 2402 */         debugCode("getSuperTables(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/* 2403 */             quote(paramString3) + ')');
/*      */       }
/* 2405 */       return getResultSet(this.meta.getSuperTables(paramString1, paramString2, paramString3));
/* 2406 */     } catch (Exception exception) {
/* 2407 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getAttributes(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/*      */     try {
/* 2418 */       if (isDebugEnabled()) {
/* 2419 */         debugCode("getAttributes(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/* 2420 */             quote(paramString3) + ", " + quote(paramString4) + ')');
/*      */       }
/* 2422 */       return getResultSet(this.meta.getAttributes(paramString1, paramString2, paramString3, paramString4));
/* 2423 */     } catch (Exception exception) {
/* 2424 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsResultSetHoldability(int paramInt) {
/* 2437 */     debugCodeCall("supportsResultSetHoldability", paramInt);
/* 2438 */     return (paramInt == 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getResultSetHoldability() {
/* 2448 */     debugCodeCall("getResultSetHoldability");
/* 2449 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDatabaseMajorVersion() throws SQLException {
/*      */     try {
/* 2460 */       debugCodeCall("getDatabaseMajorVersion");
/* 2461 */       return this.meta.getDatabaseMajorVersion();
/* 2462 */     } catch (Exception exception) {
/* 2463 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDatabaseMinorVersion() throws SQLException {
/*      */     try {
/* 2475 */       debugCodeCall("getDatabaseMinorVersion");
/* 2476 */       return this.meta.getDatabaseMinorVersion();
/* 2477 */     } catch (Exception exception) {
/* 2478 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getJDBCMajorVersion() {
/* 2489 */     debugCodeCall("getJDBCMajorVersion");
/* 2490 */     return 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getJDBCMinorVersion() {
/* 2500 */     debugCodeCall("getJDBCMinorVersion");
/* 2501 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getSQLStateType() {
/* 2511 */     debugCodeCall("getSQLStateType");
/* 2512 */     return 2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean locatorsUpdateCopy() {
/* 2522 */     debugCodeCall("locatorsUpdateCopy");
/* 2523 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsStatementPooling() {
/* 2533 */     debugCodeCall("supportsStatementPooling");
/* 2534 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkClosed() {
/* 2540 */     this.conn.checkClosed();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public RowIdLifetime getRowIdLifetime() {
/* 2550 */     debugCodeCall("getRowIdLifetime");
/* 2551 */     return RowIdLifetime.ROWID_UNSUPPORTED;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSchemas(String paramString1, String paramString2) throws SQLException {
/*      */     try {
/* 2573 */       debugCodeCall("getSchemas(String,String)");
/* 2574 */       return getResultSet(this.meta.getSchemas(paramString1, paramString2));
/* 2575 */     } catch (Exception exception) {
/* 2576 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean supportsStoredFunctionsUsingCallSyntax() {
/* 2588 */     debugCodeCall("supportsStoredFunctionsUsingCallSyntax");
/* 2589 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean autoCommitFailureClosesAllResultSets() {
/* 2600 */     debugCodeCall("autoCommitFailureClosesAllResultSets");
/* 2601 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultSet getClientInfoProperties() throws SQLException {
/* 2606 */     Properties properties = this.conn.getClientInfo();
/* 2607 */     SimpleResult simpleResult = new SimpleResult();
/* 2608 */     simpleResult.addColumn("NAME", TypeInfo.TYPE_VARCHAR);
/* 2609 */     simpleResult.addColumn("MAX_LEN", TypeInfo.TYPE_INTEGER);
/* 2610 */     simpleResult.addColumn("DEFAULT_VALUE", TypeInfo.TYPE_VARCHAR);
/* 2611 */     simpleResult.addColumn("DESCRIPTION", TypeInfo.TYPE_VARCHAR);
/*      */     
/* 2613 */     simpleResult.addColumn("VALUE", TypeInfo.TYPE_VARCHAR);
/* 2614 */     for (Map.Entry<Object, Object> entry : properties.entrySet()) {
/* 2615 */       simpleResult.addRow(new Value[] { ValueVarchar.get((String)entry.getKey()), (Value)ValueInteger.get(2147483647), (Value)ValueVarchar.EMPTY, (Value)ValueVarchar.EMPTY, 
/* 2616 */             ValueVarchar.get((String)entry.getValue()) });
/*      */     } 
/* 2618 */     int i = getNextId(4);
/* 2619 */     debugCodeAssign("ResultSet", 4, i, "getClientInfoProperties()");
/* 2620 */     return new JdbcResultSet(this.conn, null, null, (ResultInterface)simpleResult, i, true, false, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> T unwrap(Class<T> paramClass) throws SQLException {
/*      */     try {
/* 2633 */       if (isWrapperFor(paramClass)) {
/* 2634 */         return (T)this;
/*      */       }
/* 2636 */       throw DbException.getInvalidValueException("iface", paramClass);
/* 2637 */     } catch (Exception exception) {
/* 2638 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWrapperFor(Class<?> paramClass) throws SQLException {
/* 2650 */     return (paramClass != null && paramClass.isAssignableFrom(getClass()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getFunctionColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/*      */     try {
/* 2660 */       if (isDebugEnabled()) {
/* 2661 */         debugCode("getFunctionColumns(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/* 2662 */             quote(paramString3) + ", " + quote(paramString4) + ')');
/*      */       }
/* 2664 */       return getResultSet(this.meta
/* 2665 */           .getFunctionColumns(paramString1, paramString2, paramString3, paramString4));
/* 2666 */     } catch (Exception exception) {
/* 2667 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getFunctions(String paramString1, String paramString2, String paramString3) throws SQLException {
/*      */     try {
/* 2678 */       if (isDebugEnabled()) {
/* 2679 */         debugCode("getFunctions(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/* 2680 */             quote(paramString3) + ')');
/*      */       }
/* 2682 */       return getResultSet(this.meta.getFunctions(paramString1, paramString2, paramString3));
/* 2683 */     } catch (Exception exception) {
/* 2684 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean generatedKeyAlwaysReturned() {
/* 2696 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getPseudoColumns(String paramString1, String paramString2, String paramString3, String paramString4) throws SQLException {
/*      */     try {
/* 2734 */       if (isDebugEnabled()) {
/* 2735 */         debugCode("getPseudoColumns(" + quote(paramString1) + ", " + quote(paramString2) + ", " + 
/* 2736 */             quote(paramString3) + ", " + quote(paramString4) + ')');
/*      */       }
/* 2738 */       return getResultSet(this.meta.getPseudoColumns(paramString1, paramString2, paramString3, paramString4));
/* 2739 */     } catch (Exception exception) {
/* 2740 */       throw logAndConvert(exception);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/* 2749 */     return getTraceObjectName() + ": " + this.conn;
/*      */   }
/*      */   
/*      */   private JdbcResultSet getResultSet(ResultInterface paramResultInterface) {
/* 2753 */     return new JdbcResultSet(this.conn, null, null, paramResultInterface, getNextId(4), true, false, false);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\JdbcDatabaseMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */