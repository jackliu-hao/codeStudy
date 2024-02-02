/*      */ package com.mysql.cj.jdbc;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.MysqlType;
/*      */ import com.mysql.cj.ServerVersion;
/*      */ import com.mysql.cj.conf.PropertyDefinitions;
/*      */ import com.mysql.cj.conf.PropertyKey;
/*      */ import com.mysql.cj.exceptions.CJException;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLError;
/*      */ import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
/*      */ import com.mysql.cj.jdbc.result.ResultSetFactory;
/*      */ import com.mysql.cj.jdbc.result.ResultSetInternalMethods;
/*      */ import com.mysql.cj.util.LRUCache;
/*      */ import com.mysql.cj.util.StringUtils;
/*      */ import java.sql.PreparedStatement;
/*      */ import java.sql.ResultSet;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class DatabaseMetaDataUsingInfoSchema
/*      */   extends DatabaseMetaData
/*      */ {
/*   56 */   private static Map<ServerVersion, String> keywordsCache = Collections.synchronizedMap((Map<ServerVersion, String>)new LRUCache(10));
/*      */   
/*      */   protected enum FunctionConstant
/*      */   {
/*   60 */     FUNCTION_COLUMN_UNKNOWN, FUNCTION_COLUMN_IN, FUNCTION_COLUMN_INOUT, FUNCTION_COLUMN_OUT, FUNCTION_COLUMN_RETURN, FUNCTION_COLUMN_RESULT,
/*      */     
/*   62 */     FUNCTION_NO_NULLS, FUNCTION_NULLABLE, FUNCTION_NULLABLE_UNKNOWN;
/*      */   }
/*      */   
/*      */   protected DatabaseMetaDataUsingInfoSchema(JdbcConnection connToSet, String databaseToSet, ResultSetFactory resultSetFactory) throws SQLException {
/*   66 */     super(connToSet, databaseToSet, resultSetFactory);
/*      */   }
/*      */   
/*      */   protected ResultSet executeMetadataQuery(PreparedStatement pStmt) throws SQLException {
/*   70 */     ResultSet rs = pStmt.executeQuery();
/*   71 */     ((ResultSetInternalMethods)rs).setOwningStatement(null);
/*      */     
/*   73 */     return rs;
/*      */   }
/*      */   
/*      */   public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
/*      */     
/*   78 */     try { String db = getDatabase(catalog, schema);
/*      */       
/*   80 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */ 
/*      */       
/*   83 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT TABLE_CATALOG, TABLE_SCHEMA," : "SELECT TABLE_SCHEMA, NULL,");
/*   84 */       sqlBuf.append(" TABLE_NAME, COLUMN_NAME, NULL AS GRANTOR, GRANTEE, PRIVILEGE_TYPE AS PRIVILEGE, IS_GRANTABLE FROM INFORMATION_SCHEMA.COLUMN_PRIVILEGES WHERE");
/*      */       
/*   86 */       if (db != null) {
/*   87 */         sqlBuf.append(" TABLE_SCHEMA=? AND");
/*      */       }
/*      */       
/*   90 */       sqlBuf.append(" TABLE_NAME =?");
/*   91 */       if (columnNamePattern != null) {
/*   92 */         sqlBuf.append(" AND COLUMN_NAME LIKE ?");
/*      */       }
/*   94 */       sqlBuf.append(" ORDER BY COLUMN_NAME, PRIVILEGE_TYPE");
/*      */       
/*   96 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*   99 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  100 */         int nextId = 1;
/*  101 */         pStmt.setString(nextId++, db);
/*  102 */         pStmt.setString(nextId++, table);
/*  103 */         if (columnNamePattern != null) {
/*  104 */           pStmt.setString(nextId, columnNamePattern);
/*      */         }
/*      */         
/*  107 */         ResultSet rs = executeMetadataQuery(pStmt);
/*  108 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(getColumnPrivilegesFields());
/*      */         
/*  110 */         return rs;
/*      */       } finally {
/*  112 */         if (pStmt != null)
/*  113 */           pStmt.close(); 
/*      */       }  }
/*  115 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getColumns(String catalog, String schemaPattern, String tableName, String columnNamePattern) throws SQLException {
/*      */     
/*  120 */     try { String db = getDatabase(catalog, schemaPattern);
/*      */       
/*  122 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */ 
/*      */       
/*  125 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT TABLE_CATALOG, TABLE_SCHEMA," : "SELECT TABLE_SCHEMA, NULL,");
/*  126 */       sqlBuf.append(" TABLE_NAME, COLUMN_NAME,");
/*      */       
/*  128 */       appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE", "COLUMN_TYPE");
/*  129 */       sqlBuf.append(" AS DATA_TYPE, ");
/*      */       
/*  131 */       sqlBuf.append("UPPER(CASE");
/*  132 */       if (this.tinyInt1isBit) {
/*  133 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TINYINT' THEN CASE");
/*  134 */         sqlBuf.append(" WHEN LOCATE('ZEROFILL', UPPER(COLUMN_TYPE)) = 0 AND LOCATE('UNSIGNED', UPPER(COLUMN_TYPE)) = 0 AND LOCATE('(1)', COLUMN_TYPE) != 0 THEN ");
/*      */         
/*  136 */         sqlBuf.append(this.transformedBitIsBoolean ? "'BOOLEAN'" : "'BIT'");
/*  137 */         sqlBuf.append(" WHEN LOCATE('UNSIGNED', UPPER(COLUMN_TYPE)) != 0 AND LOCATE('UNSIGNED', UPPER(DATA_TYPE)) = 0 THEN 'TINYINT UNSIGNED'");
/*  138 */         sqlBuf.append(" ELSE DATA_TYPE END ");
/*      */       } 
/*  140 */       sqlBuf.append(" WHEN LOCATE('UNSIGNED', UPPER(COLUMN_TYPE)) != 0 AND LOCATE('UNSIGNED', UPPER(DATA_TYPE)) = 0 AND LOCATE('SET', UPPER(DATA_TYPE)) <> 1 AND LOCATE('ENUM', UPPER(DATA_TYPE)) <> 1 THEN CONCAT(DATA_TYPE, ' UNSIGNED')");
/*      */ 
/*      */ 
/*      */       
/*  144 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POINT' THEN 'GEOMETRY'");
/*  145 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='LINESTRING' THEN 'GEOMETRY'");
/*  146 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POLYGON' THEN 'GEOMETRY'");
/*  147 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOINT' THEN 'GEOMETRY'");
/*  148 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTILINESTRING' THEN 'GEOMETRY'");
/*  149 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOLYGON' THEN 'GEOMETRY'");
/*  150 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMETRYCOLLECTION' THEN 'GEOMETRY'");
/*  151 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMCOLLECTION' THEN 'GEOMETRY'");
/*      */       
/*  153 */       sqlBuf.append(" ELSE UPPER(DATA_TYPE) END) AS TYPE_NAME,");
/*      */       
/*  155 */       sqlBuf.append("UPPER(CASE");
/*  156 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='DATE' THEN 10");
/*  157 */       if (this.conn.getServerVersion().meetsMinimum(ServerVersion.parseVersion("5.6.4"))) {
/*  158 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TIME'");
/*  159 */         sqlBuf.append("  THEN 8+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*  160 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='DATETIME' OR");
/*  161 */         sqlBuf.append("  UPPER(DATA_TYPE)='TIMESTAMP'");
/*  162 */         sqlBuf.append("  THEN 19+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*      */       } else {
/*  164 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TIME' THEN 8");
/*  165 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='DATETIME' OR");
/*  166 */         sqlBuf.append("  UPPER(DATA_TYPE)='TIMESTAMP'");
/*  167 */         sqlBuf.append("  THEN 19");
/*      */       } 
/*      */       
/*  170 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='YEAR' THEN 4");
/*  171 */       if (this.tinyInt1isBit && !this.transformedBitIsBoolean) {
/*  172 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TINYINT' AND LOCATE('ZEROFILL', UPPER(COLUMN_TYPE)) = 0 AND LOCATE('UNSIGNED', UPPER(COLUMN_TYPE)) = 0 AND LOCATE('(1)', COLUMN_TYPE) != 0 THEN 1");
/*      */       }
/*      */ 
/*      */       
/*  176 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MEDIUMINT' AND LOCATE('UNSIGNED', UPPER(COLUMN_TYPE)) != 0 THEN 8");
/*  177 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='JSON' THEN 1073741824");
/*      */ 
/*      */       
/*  180 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMETRY' THEN 65535");
/*  181 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POINT' THEN 65535");
/*  182 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='LINESTRING' THEN 65535");
/*  183 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POLYGON' THEN 65535");
/*  184 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOINT' THEN 65535");
/*  185 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTILINESTRING' THEN 65535");
/*  186 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOLYGON' THEN 65535");
/*  187 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMETRYCOLLECTION' THEN 65535");
/*  188 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMCOLLECTION' THEN 65535");
/*      */       
/*  190 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION");
/*  191 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH > ");
/*  192 */       sqlBuf.append(2147483647);
/*  193 */       sqlBuf.append(" THEN ");
/*  194 */       sqlBuf.append(2147483647);
/*  195 */       sqlBuf.append(" ELSE CHARACTER_MAXIMUM_LENGTH");
/*  196 */       sqlBuf.append(" END) AS COLUMN_SIZE,");
/*      */       
/*  198 */       sqlBuf.append(maxBufferSize);
/*  199 */       sqlBuf.append(" AS BUFFER_LENGTH,");
/*      */       
/*  201 */       sqlBuf.append("UPPER(CASE");
/*  202 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='DECIMAL' THEN NUMERIC_SCALE");
/*  203 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='FLOAT' OR UPPER(DATA_TYPE)='DOUBLE' THEN");
/*  204 */       sqlBuf.append(" CASE WHEN NUMERIC_SCALE IS NULL THEN 0");
/*  205 */       sqlBuf.append(" ELSE NUMERIC_SCALE END");
/*  206 */       sqlBuf.append(" ELSE NULL END) AS DECIMAL_DIGITS,");
/*      */       
/*  208 */       sqlBuf.append("10 AS NUM_PREC_RADIX,");
/*      */       
/*  210 */       sqlBuf.append("CASE");
/*  211 */       sqlBuf.append(" WHEN IS_NULLABLE='NO' THEN ");
/*  212 */       sqlBuf.append(0);
/*  213 */       sqlBuf.append(" ELSE CASE WHEN IS_NULLABLE='YES' THEN ");
/*  214 */       sqlBuf.append(1);
/*  215 */       sqlBuf.append(" ELSE ");
/*  216 */       sqlBuf.append(2);
/*  217 */       sqlBuf.append(" END END AS NULLABLE,");
/*      */       
/*  219 */       sqlBuf.append("COLUMN_COMMENT AS REMARKS,");
/*  220 */       sqlBuf.append("COLUMN_DEFAULT AS COLUMN_DEF,");
/*  221 */       sqlBuf.append("0 AS SQL_DATA_TYPE,");
/*  222 */       sqlBuf.append("0 AS SQL_DATETIME_SUB,");
/*      */       
/*  224 */       sqlBuf.append("CASE WHEN CHARACTER_OCTET_LENGTH > ");
/*  225 */       sqlBuf.append(2147483647);
/*  226 */       sqlBuf.append(" THEN ");
/*  227 */       sqlBuf.append(2147483647);
/*  228 */       sqlBuf.append(" ELSE CHARACTER_OCTET_LENGTH END AS CHAR_OCTET_LENGTH,");
/*      */       
/*  230 */       sqlBuf.append("ORDINAL_POSITION, IS_NULLABLE, NULL AS SCOPE_CATALOG, NULL AS SCOPE_SCHEMA, NULL AS SCOPE_TABLE, NULL AS SOURCE_DATA_TYPE,");
/*  231 */       sqlBuf.append("IF (EXTRA LIKE '%auto_increment%','YES','NO') AS IS_AUTOINCREMENT, ");
/*  232 */       sqlBuf.append("IF (EXTRA LIKE '%GENERATED%','YES','NO') AS IS_GENERATEDCOLUMN ");
/*      */       
/*  234 */       sqlBuf.append("FROM INFORMATION_SCHEMA.COLUMNS");
/*      */       
/*  236 */       StringBuilder conditionBuf = new StringBuilder();
/*      */       
/*  238 */       if (db != null) {
/*  239 */         conditionBuf.append(("information_schema".equalsIgnoreCase(db) || "performance_schema".equalsIgnoreCase(db) || !StringUtils.hasWildcards(db) || this.databaseTerm
/*  240 */             .getValue() == PropertyDefinitions.DatabaseTerm.CATALOG) ? " TABLE_SCHEMA = ?" : " TABLE_SCHEMA LIKE ?");
/*      */       }
/*  242 */       if (tableName != null) {
/*  243 */         if (conditionBuf.length() > 0) {
/*  244 */           conditionBuf.append(" AND");
/*      */         }
/*  246 */         conditionBuf.append(StringUtils.hasWildcards(tableName) ? " TABLE_NAME LIKE ?" : " TABLE_NAME = ?");
/*      */       } 
/*  248 */       if (columnNamePattern != null) {
/*  249 */         if (conditionBuf.length() > 0) {
/*  250 */           conditionBuf.append(" AND");
/*      */         }
/*  252 */         conditionBuf.append(StringUtils.hasWildcards(columnNamePattern) ? " COLUMN_NAME LIKE ?" : " COLUMN_NAME = ?");
/*      */       } 
/*      */       
/*  255 */       if (conditionBuf.length() > 0) {
/*  256 */         sqlBuf.append(" WHERE");
/*      */       }
/*  258 */       sqlBuf.append(conditionBuf);
/*  259 */       sqlBuf.append(" ORDER BY TABLE_SCHEMA, TABLE_NAME, ORDINAL_POSITION");
/*      */       
/*  261 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  264 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */         
/*  266 */         int nextId = 1;
/*  267 */         if (db != null) {
/*  268 */           pStmt.setString(nextId++, db);
/*      */         }
/*  270 */         if (tableName != null) {
/*  271 */           pStmt.setString(nextId++, tableName);
/*      */         }
/*  273 */         if (columnNamePattern != null) {
/*  274 */           pStmt.setString(nextId, columnNamePattern);
/*      */         }
/*      */         
/*  277 */         ResultSet rs = executeMetadataQuery(pStmt);
/*      */         
/*  279 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createColumnsFields());
/*  280 */         return rs;
/*      */       } finally {
/*  282 */         if (pStmt != null)
/*  283 */           pStmt.close(); 
/*      */       }  }
/*  285 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable) throws SQLException {
/*      */     
/*  291 */     try { if (primaryTable == null) {
/*  292 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/*  293 */             getExceptionInterceptor());
/*      */       }
/*      */       
/*  296 */       String primaryDb = getDatabase(primaryCatalog, primarySchema);
/*  297 */       String foreignDb = getDatabase(foreignCatalog, foreignSchema);
/*      */       
/*  299 */       primaryDb = this.pedantic ? primaryDb : StringUtils.unQuoteIdentifier(primaryDb, this.quotedId);
/*  300 */       foreignDb = this.pedantic ? foreignDb : StringUtils.unQuoteIdentifier(foreignDb, this.quotedId);
/*      */       
/*  302 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT DISTINCT A.CONSTRAINT_CATALOG AS PKTABLE_CAT, A.REFERENCED_TABLE_SCHEMA AS PKTABLE_SCHEM," : "SELECT DISTINCT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,");
/*      */ 
/*      */       
/*  305 */       sqlBuf.append(" A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,");
/*  306 */       sqlBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " A.TABLE_CATALOG AS FKTABLE_CAT, A.TABLE_SCHEMA AS FKTABLE_SCHEM," : " A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM,");
/*      */       
/*  308 */       sqlBuf.append(" A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ,");
/*  309 */       sqlBuf.append(generateUpdateRuleClause());
/*  310 */       sqlBuf.append(" AS UPDATE_RULE,");
/*  311 */       sqlBuf.append(generateDeleteRuleClause());
/*  312 */       sqlBuf.append(" AS DELETE_RULE, A.CONSTRAINT_NAME AS FK_NAME, TC.CONSTRAINT_NAME AS PK_NAME,");
/*  313 */       sqlBuf.append(7);
/*  314 */       sqlBuf.append(" AS DEFERRABILITY FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE A");
/*  315 */       sqlBuf.append(" JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) ");
/*  316 */       sqlBuf.append(generateOptionalRefContraintsJoin());
/*  317 */       sqlBuf.append(" LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC ON (A.REFERENCED_TABLE_SCHEMA = TC.TABLE_SCHEMA");
/*  318 */       sqlBuf.append("  AND A.REFERENCED_TABLE_NAME = TC.TABLE_NAME");
/*  319 */       sqlBuf.append("  AND TC.CONSTRAINT_TYPE IN ('UNIQUE', 'PRIMARY KEY'))");
/*  320 */       sqlBuf.append("WHERE B.CONSTRAINT_TYPE = 'FOREIGN KEY'");
/*  321 */       if (primaryDb != null) {
/*  322 */         sqlBuf.append(" AND A.REFERENCED_TABLE_SCHEMA=?");
/*      */       }
/*  324 */       sqlBuf.append(" AND A.REFERENCED_TABLE_NAME=?");
/*  325 */       if (foreignDb != null) {
/*  326 */         sqlBuf.append(" AND A.TABLE_SCHEMA = ?");
/*      */       }
/*  328 */       sqlBuf.append(" AND A.TABLE_NAME=?");
/*  329 */       sqlBuf.append(" ORDER BY FKTABLE_NAME, FKTABLE_NAME, KEY_SEQ");
/*      */       
/*  331 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  334 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  335 */         int nextId = 1;
/*  336 */         if (primaryDb != null) {
/*  337 */           pStmt.setString(nextId++, primaryDb);
/*      */         }
/*  339 */         pStmt.setString(nextId++, primaryTable);
/*  340 */         if (foreignDb != null) {
/*  341 */           pStmt.setString(nextId++, foreignDb);
/*      */         }
/*  343 */         pStmt.setString(nextId, foreignTable);
/*      */         
/*  345 */         ResultSet rs = executeMetadataQuery(pStmt);
/*  346 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createFkMetadataFields());
/*      */         
/*  348 */         return rs;
/*      */       } finally {
/*  350 */         if (pStmt != null)
/*  351 */           pStmt.close(); 
/*      */       }  }
/*  353 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
/*      */     
/*  358 */     try { if (table == null) {
/*  359 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/*  360 */             getExceptionInterceptor());
/*      */       }
/*      */       
/*  363 */       String db = getDatabase(catalog, schema);
/*      */       
/*  365 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */       
/*  367 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT DISTINCT A.CONSTRAINT_CATALOG AS PKTABLE_CAT, A.REFERENCED_TABLE_SCHEMA AS PKTABLE_SCHEM," : "SELECT DISTINCT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,");
/*      */ 
/*      */       
/*  370 */       sqlBuf.append(" A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,");
/*  371 */       sqlBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " A.TABLE_CATALOG AS FKTABLE_CAT, A.TABLE_SCHEMA AS FKTABLE_SCHEM," : " A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM,");
/*      */       
/*  373 */       sqlBuf.append(" A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ,");
/*  374 */       sqlBuf.append(generateUpdateRuleClause());
/*  375 */       sqlBuf.append(" AS UPDATE_RULE,");
/*  376 */       sqlBuf.append(generateDeleteRuleClause());
/*  377 */       sqlBuf.append(" AS DELETE_RULE, A.CONSTRAINT_NAME AS FK_NAME, TC.CONSTRAINT_NAME AS PK_NAME,");
/*  378 */       sqlBuf.append(7);
/*  379 */       sqlBuf.append(" AS DEFERRABILITY FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE A");
/*  380 */       sqlBuf.append(" JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (TABLE_SCHEMA, TABLE_NAME, CONSTRAINT_NAME) ");
/*  381 */       sqlBuf.append(generateOptionalRefContraintsJoin());
/*  382 */       sqlBuf.append(" LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS TC ON (A.REFERENCED_TABLE_SCHEMA = TC.TABLE_SCHEMA");
/*  383 */       sqlBuf.append("  AND A.REFERENCED_TABLE_NAME = TC.TABLE_NAME");
/*  384 */       sqlBuf.append("  AND TC.CONSTRAINT_TYPE IN ('UNIQUE', 'PRIMARY KEY'))");
/*  385 */       sqlBuf.append(" WHERE B.CONSTRAINT_TYPE = 'FOREIGN KEY'");
/*  386 */       if (db != null) {
/*  387 */         sqlBuf.append(" AND A.REFERENCED_TABLE_SCHEMA = ?");
/*      */       }
/*  389 */       sqlBuf.append(" AND A.REFERENCED_TABLE_NAME=?");
/*  390 */       sqlBuf.append(" ORDER BY FKTABLE_NAME, FKTABLE_NAME, KEY_SEQ");
/*      */       
/*  392 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  395 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  396 */         int nextId = 1;
/*      */         
/*  398 */         if (db != null) {
/*  399 */           pStmt.setString(nextId++, db);
/*      */         }
/*  401 */         pStmt.setString(nextId, table);
/*      */         
/*  403 */         ResultSet rs = executeMetadataQuery(pStmt);
/*      */         
/*  405 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createFkMetadataFields());
/*      */         
/*  407 */         return rs;
/*      */       } finally {
/*  409 */         if (pStmt != null)
/*  410 */           pStmt.close(); 
/*      */       }  }
/*  412 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   private String generateOptionalRefContraintsJoin() {
/*  417 */     return "JOIN INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS R ON (R.CONSTRAINT_NAME = B.CONSTRAINT_NAME AND R.TABLE_NAME = B.TABLE_NAME AND R.CONSTRAINT_SCHEMA = B.TABLE_SCHEMA) ";
/*      */   }
/*      */ 
/*      */   
/*      */   private String generateDeleteRuleClause() {
/*  422 */     return "CASE WHEN R.DELETE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.DELETE_RULE='SET NULL' THEN " + 
/*  423 */       String.valueOf(2) + " WHEN R.DELETE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.DELETE_RULE='RESTRICT' THEN " + 
/*  424 */       String.valueOf(1) + " WHEN R.DELETE_RULE='NO ACTION' THEN " + 
/*  425 */       String.valueOf(1) + " ELSE " + String.valueOf(1) + " END ";
/*      */   }
/*      */   
/*      */   private String generateUpdateRuleClause() {
/*  429 */     return "CASE WHEN R.UPDATE_RULE='CASCADE' THEN " + String.valueOf(0) + " WHEN R.UPDATE_RULE='SET NULL' THEN " + 
/*  430 */       String.valueOf(2) + " WHEN R.UPDATE_RULE='SET DEFAULT' THEN " + String.valueOf(4) + " WHEN R.UPDATE_RULE='RESTRICT' THEN " + 
/*  431 */       String.valueOf(1) + " WHEN R.UPDATE_RULE='NO ACTION' THEN " + 
/*  432 */       String.valueOf(1) + " ELSE " + String.valueOf(1) + " END ";
/*      */   }
/*      */   
/*      */   public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
/*      */     
/*  437 */     try { if (table == null) {
/*  438 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/*  439 */             getExceptionInterceptor());
/*      */       }
/*      */       
/*  442 */       String db = getDatabase(catalog, schema);
/*      */       
/*  444 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */       
/*  446 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT DISTINCT A.CONSTRAINT_CATALOG AS PKTABLE_CAT, A.REFERENCED_TABLE_SCHEMA AS PKTABLE_SCHEM," : "SELECT DISTINCT A.REFERENCED_TABLE_SCHEMA AS PKTABLE_CAT,NULL AS PKTABLE_SCHEM,");
/*      */ 
/*      */       
/*  449 */       sqlBuf.append(" A.REFERENCED_TABLE_NAME AS PKTABLE_NAME, A.REFERENCED_COLUMN_NAME AS PKCOLUMN_NAME,");
/*  450 */       sqlBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " A.TABLE_CATALOG AS FKTABLE_CAT, A.TABLE_SCHEMA AS FKTABLE_SCHEM," : " A.TABLE_SCHEMA AS FKTABLE_CAT, NULL AS FKTABLE_SCHEM,");
/*      */       
/*  452 */       sqlBuf.append(" A.TABLE_NAME AS FKTABLE_NAME, A.COLUMN_NAME AS FKCOLUMN_NAME, A.ORDINAL_POSITION AS KEY_SEQ,");
/*  453 */       sqlBuf.append(generateUpdateRuleClause());
/*  454 */       sqlBuf.append(" AS UPDATE_RULE,");
/*  455 */       sqlBuf.append(generateDeleteRuleClause());
/*  456 */       sqlBuf.append(" AS DELETE_RULE, A.CONSTRAINT_NAME AS FK_NAME, R.UNIQUE_CONSTRAINT_NAME AS PK_NAME,");
/*  457 */       sqlBuf.append(7);
/*  458 */       sqlBuf.append(" AS DEFERRABILITY FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE A");
/*  459 */       sqlBuf.append(" JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS B USING (CONSTRAINT_NAME, TABLE_NAME) ");
/*  460 */       sqlBuf.append(generateOptionalRefContraintsJoin());
/*  461 */       sqlBuf.append("WHERE B.CONSTRAINT_TYPE = 'FOREIGN KEY'");
/*  462 */       if (db != null) {
/*  463 */         sqlBuf.append(" AND A.TABLE_SCHEMA = ?");
/*      */       }
/*  465 */       sqlBuf.append(" AND A.TABLE_NAME=?");
/*  466 */       sqlBuf.append(" AND A.REFERENCED_TABLE_SCHEMA IS NOT NULL");
/*  467 */       sqlBuf.append(" ORDER BY A.REFERENCED_TABLE_SCHEMA, A.REFERENCED_TABLE_NAME, A.ORDINAL_POSITION");
/*      */       
/*  469 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  472 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  473 */         int nextId = 1;
/*  474 */         if (db != null) {
/*  475 */           pStmt.setString(nextId++, db);
/*      */         }
/*  477 */         pStmt.setString(nextId, table);
/*      */         
/*  479 */         ResultSet rs = executeMetadataQuery(pStmt);
/*      */         
/*  481 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createFkMetadataFields());
/*      */         
/*  483 */         return rs;
/*      */       } finally {
/*  485 */         if (pStmt != null)
/*  486 */           pStmt.close(); 
/*      */       }  }
/*  488 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
/*      */     
/*  493 */     try { String db = getDatabase(catalog, schema);
/*      */       
/*  495 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */ 
/*      */       
/*  498 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT TABLE_CATALOG AS TABLE_CAT, TABLE_SCHEMA AS TABLE_SCHEM," : "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,");
/*      */       
/*  500 */       sqlBuf.append(" TABLE_NAME, NON_UNIQUE, NULL AS INDEX_QUALIFIER, INDEX_NAME,");
/*  501 */       sqlBuf.append(3);
/*  502 */       sqlBuf.append(" AS TYPE, SEQ_IN_INDEX AS ORDINAL_POSITION, COLUMN_NAME,");
/*  503 */       sqlBuf.append("COLLATION AS ASC_OR_DESC, CARDINALITY, 0 AS PAGES, NULL AS FILTER_CONDITION FROM INFORMATION_SCHEMA.STATISTICS WHERE");
/*  504 */       if (db != null) {
/*  505 */         sqlBuf.append(" TABLE_SCHEMA = ? AND");
/*      */       }
/*  507 */       sqlBuf.append(" TABLE_NAME = ?");
/*      */       
/*  509 */       if (unique) {
/*  510 */         sqlBuf.append(" AND NON_UNIQUE=0 ");
/*      */       }
/*  512 */       sqlBuf.append("ORDER BY NON_UNIQUE, INDEX_NAME, SEQ_IN_INDEX");
/*      */       
/*  514 */       PreparedStatement pStmt = null;
/*      */ 
/*      */       
/*      */       try {
/*  518 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  519 */         int nextId = 1;
/*  520 */         if (db != null) {
/*  521 */           pStmt.setString(nextId++, db);
/*      */         }
/*  523 */         pStmt.setString(nextId, table);
/*      */         
/*  525 */         ResultSet rs = executeMetadataQuery(pStmt);
/*      */         
/*  527 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createIndexInfoFields());
/*      */         
/*  529 */         return rs;
/*      */       } finally {
/*  531 */         if (pStmt != null)
/*  532 */           pStmt.close(); 
/*      */       }  }
/*  534 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
/*      */     
/*  539 */     try { if (table == null) {
/*  540 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/*  541 */             getExceptionInterceptor());
/*      */       }
/*      */       
/*  544 */       String db = getDatabase(catalog, schema);
/*      */       
/*  546 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */ 
/*      */       
/*  549 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT TABLE_CATALOG AS TABLE_CAT, TABLE_SCHEMA AS TABLE_SCHEM," : "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,");
/*      */       
/*  551 */       sqlBuf.append(" TABLE_NAME, COLUMN_NAME, SEQ_IN_INDEX AS KEY_SEQ, 'PRIMARY' AS PK_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE");
/*  552 */       if (db != null) {
/*  553 */         sqlBuf.append(" TABLE_SCHEMA = ? AND");
/*      */       }
/*  555 */       sqlBuf.append(" TABLE_NAME = ?");
/*  556 */       sqlBuf.append(" AND INDEX_NAME='PRIMARY' ORDER BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX");
/*      */       
/*  558 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  561 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  562 */         int nextId = 1;
/*  563 */         if (db != null) {
/*  564 */           pStmt.setString(nextId++, db);
/*      */         }
/*  566 */         pStmt.setString(nextId, table);
/*      */         
/*  568 */         ResultSet rs = executeMetadataQuery(pStmt);
/*  569 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(getPrimaryKeysFields());
/*      */         
/*  571 */         return rs;
/*      */       } finally {
/*  573 */         if (pStmt != null)
/*  574 */           pStmt.close(); 
/*      */       }  }
/*  576 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
/*      */     
/*  582 */     try { String db = getDatabase(catalog, schemaPattern);
/*      */       
/*  584 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */ 
/*      */       
/*  587 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT ROUTINE_CATALOG AS PROCEDURE_CAT, ROUTINE_SCHEMA AS PROCEDURE_SCHEM," : "SELECT ROUTINE_SCHEMA AS PROCEDURE_CAT, NULL AS PROCEDURE_SCHEM,");
/*      */       
/*  589 */       sqlBuf.append(" ROUTINE_NAME AS PROCEDURE_NAME, NULL AS RESERVED_1, NULL AS RESERVED_2, NULL AS RESERVED_3, ROUTINE_COMMENT AS REMARKS, CASE WHEN ROUTINE_TYPE = 'PROCEDURE' THEN ");
/*      */       
/*  591 */       sqlBuf.append(1);
/*  592 */       sqlBuf.append(" WHEN ROUTINE_TYPE='FUNCTION' THEN ");
/*  593 */       sqlBuf.append(2);
/*  594 */       sqlBuf.append(" ELSE ");
/*  595 */       sqlBuf.append(0);
/*  596 */       sqlBuf.append(" END AS PROCEDURE_TYPE, ROUTINE_NAME AS SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES");
/*      */       
/*  598 */       StringBuilder conditionBuf = new StringBuilder();
/*  599 */       if (!((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.getProceduresReturnsFunctions).getValue()).booleanValue()) {
/*  600 */         conditionBuf.append(" ROUTINE_TYPE = 'PROCEDURE'");
/*      */       }
/*  602 */       if (db != null) {
/*  603 */         if (conditionBuf.length() > 0) {
/*  604 */           conditionBuf.append(" AND");
/*      */         }
/*  606 */         conditionBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " ROUTINE_SCHEMA LIKE ?" : " ROUTINE_SCHEMA = ?");
/*      */       } 
/*  608 */       if (procedureNamePattern != null) {
/*  609 */         if (conditionBuf.length() > 0) {
/*  610 */           conditionBuf.append(" AND");
/*      */         }
/*  612 */         conditionBuf.append(" ROUTINE_NAME LIKE ?");
/*      */       } 
/*      */       
/*  615 */       if (conditionBuf.length() > 0) {
/*  616 */         sqlBuf.append(" WHERE");
/*  617 */         sqlBuf.append(conditionBuf);
/*      */       } 
/*  619 */       sqlBuf.append(" ORDER BY ROUTINE_SCHEMA, ROUTINE_NAME, ROUTINE_TYPE");
/*      */       
/*  621 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  624 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  625 */         int nextId = 1;
/*  626 */         if (db != null) {
/*  627 */           pStmt.setString(nextId++, db);
/*      */         }
/*  629 */         if (procedureNamePattern != null) {
/*  630 */           pStmt.setString(nextId, procedureNamePattern);
/*      */         }
/*      */         
/*  633 */         ResultSet rs = executeMetadataQuery(pStmt);
/*  634 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createFieldMetadataForGetProcedures());
/*      */         
/*  636 */         return rs;
/*      */       } finally {
/*  638 */         if (pStmt != null)
/*  639 */           pStmt.close(); 
/*      */       }  }
/*  641 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
/*      */     
/*  647 */     try { String db = getDatabase(catalog, schemaPattern);
/*      */       
/*  649 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*  650 */       boolean supportsFractSeconds = this.conn.getServerVersion().meetsMinimum(ServerVersion.parseVersion("5.6.4"));
/*      */ 
/*      */       
/*  653 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT SPECIFIC_CATALOG AS PROCEDURE_CAT, SPECIFIC_SCHEMA AS `PROCEDURE_SCHEM`," : "SELECT SPECIFIC_SCHEMA AS PROCEDURE_CAT, NULL AS `PROCEDURE_SCHEM`,");
/*      */       
/*  655 */       sqlBuf.append(" SPECIFIC_NAME AS `PROCEDURE_NAME`, IFNULL(PARAMETER_NAME, '') AS `COLUMN_NAME`,");
/*  656 */       sqlBuf.append(" CASE WHEN PARAMETER_MODE = 'IN' THEN ");
/*  657 */       sqlBuf.append(1);
/*  658 */       sqlBuf.append(" WHEN PARAMETER_MODE = 'OUT' THEN ");
/*  659 */       sqlBuf.append(4);
/*  660 */       sqlBuf.append(" WHEN PARAMETER_MODE = 'INOUT' THEN ");
/*  661 */       sqlBuf.append(2);
/*  662 */       sqlBuf.append(" WHEN ORDINAL_POSITION = 0 THEN ");
/*  663 */       sqlBuf.append(5);
/*  664 */       sqlBuf.append(" ELSE ");
/*  665 */       sqlBuf.append(0);
/*  666 */       sqlBuf.append(" END AS `COLUMN_TYPE`, ");
/*  667 */       appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE", "DTD_IDENTIFIER");
/*  668 */       sqlBuf.append(" AS `DATA_TYPE`, ");
/*      */       
/*  670 */       sqlBuf.append("UPPER(CASE");
/*  671 */       if (this.tinyInt1isBit) {
/*  672 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TINYINT' THEN CASE");
/*  673 */         sqlBuf.append(" WHEN LOCATE('ZEROFILL', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('(1)', DTD_IDENTIFIER) != 0 THEN ");
/*      */         
/*  675 */         sqlBuf.append(this.transformedBitIsBoolean ? "'BOOLEAN'" : "'BIT'");
/*  676 */         sqlBuf.append(" WHEN LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 AND LOCATE('UNSIGNED', UPPER(DATA_TYPE)) = 0 THEN 'TINYINT UNSIGNED'");
/*  677 */         sqlBuf.append(" ELSE DATA_TYPE END ");
/*      */       } 
/*  679 */       sqlBuf.append(" WHEN LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 AND LOCATE('UNSIGNED', UPPER(DATA_TYPE)) = 0 AND LOCATE('SET', UPPER(DATA_TYPE)) <> 1 AND LOCATE('ENUM', UPPER(DATA_TYPE)) <> 1 THEN CONCAT(DATA_TYPE, ' UNSIGNED')");
/*      */ 
/*      */ 
/*      */       
/*  683 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POINT' THEN 'GEOMETRY'");
/*  684 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='LINESTRING' THEN 'GEOMETRY'");
/*  685 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POLYGON' THEN 'GEOMETRY'");
/*  686 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOINT' THEN 'GEOMETRY'");
/*  687 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTILINESTRING' THEN 'GEOMETRY'");
/*  688 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOLYGON' THEN 'GEOMETRY'");
/*  689 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMETRYCOLLECTION' THEN 'GEOMETRY'");
/*  690 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMCOLLECTION' THEN 'GEOMETRY'");
/*      */       
/*  692 */       sqlBuf.append(" ELSE UPPER(DATA_TYPE) END) AS TYPE_NAME,");
/*      */ 
/*      */       
/*  695 */       sqlBuf.append(" CASE WHEN LCASE(DATA_TYPE)='date' THEN 0");
/*  696 */       if (supportsFractSeconds) {
/*  697 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' OR LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN DATETIME_PRECISION");
/*      */       } else {
/*  699 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' OR LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN 0");
/*      */       } 
/*  701 */       if (this.tinyInt1isBit && !this.transformedBitIsBoolean) {
/*  702 */         sqlBuf.append(" WHEN (UPPER(DATA_TYPE)='TINYINT' AND LOCATE('ZEROFILL', UPPER(DTD_IDENTIFIER)) = 0) AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('(1)', DTD_IDENTIFIER) != 0 THEN 1");
/*      */       }
/*      */ 
/*      */       
/*  706 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MEDIUMINT' AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 THEN 8");
/*  707 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='JSON' THEN 1073741824");
/*  708 */       sqlBuf.append(" ELSE NUMERIC_PRECISION END AS `PRECISION`,");
/*      */ 
/*      */       
/*  711 */       sqlBuf.append(" CASE WHEN LCASE(DATA_TYPE)='date' THEN 10");
/*  712 */       if (supportsFractSeconds) {
/*  713 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' THEN 8+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*  714 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp'");
/*  715 */         sqlBuf.append("  THEN 19+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*      */       } else {
/*  717 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' THEN 8");
/*  718 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN 19");
/*      */       } 
/*  720 */       if (this.tinyInt1isBit && !this.transformedBitIsBoolean) {
/*  721 */         sqlBuf.append(" WHEN (UPPER(DATA_TYPE)='TINYINT' OR UPPER(DATA_TYPE)='TINYINT UNSIGNED') AND LOCATE('ZEROFILL', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('(1)', DTD_IDENTIFIER) != 0 THEN 1");
/*      */       }
/*      */ 
/*      */       
/*  725 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MEDIUMINT' AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 THEN 8");
/*  726 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='JSON' THEN 1073741824");
/*  727 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION");
/*  728 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH > ");
/*  729 */       sqlBuf.append(2147483647);
/*  730 */       sqlBuf.append(" THEN ");
/*  731 */       sqlBuf.append(2147483647);
/*  732 */       sqlBuf.append(" ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH,");
/*      */       
/*  734 */       sqlBuf.append("NUMERIC_SCALE AS `SCALE`, ");
/*  735 */       sqlBuf.append("10 AS RADIX,");
/*  736 */       sqlBuf.append(1);
/*  737 */       sqlBuf.append(" AS `NULLABLE`, NULL AS `REMARKS`, NULL AS `COLUMN_DEF`, NULL AS `SQL_DATA_TYPE`, NULL AS `SQL_DATETIME_SUB`,");
/*      */       
/*  739 */       sqlBuf.append(" CASE WHEN CHARACTER_OCTET_LENGTH > ");
/*  740 */       sqlBuf.append(2147483647);
/*  741 */       sqlBuf.append(" THEN ");
/*  742 */       sqlBuf.append(2147483647);
/*  743 */       sqlBuf.append(" ELSE CHARACTER_OCTET_LENGTH END AS `CHAR_OCTET_LENGTH`,");
/*      */       
/*  745 */       sqlBuf.append(" ORDINAL_POSITION, 'YES' AS `IS_NULLABLE`, SPECIFIC_NAME");
/*  746 */       sqlBuf.append(" FROM INFORMATION_SCHEMA.PARAMETERS");
/*      */       
/*  748 */       StringBuilder conditionBuf = new StringBuilder();
/*  749 */       if (!((Boolean)this.conn.getPropertySet().getBooleanProperty(PropertyKey.getProceduresReturnsFunctions).getValue()).booleanValue()) {
/*  750 */         conditionBuf.append(" ROUTINE_TYPE = 'PROCEDURE'");
/*      */       }
/*  752 */       if (db != null) {
/*  753 */         if (conditionBuf.length() > 0) {
/*  754 */           conditionBuf.append(" AND");
/*      */         }
/*  756 */         conditionBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " SPECIFIC_SCHEMA LIKE ?" : " SPECIFIC_SCHEMA = ?");
/*      */       } 
/*  758 */       if (procedureNamePattern != null) {
/*  759 */         if (conditionBuf.length() > 0) {
/*  760 */           conditionBuf.append(" AND");
/*      */         }
/*  762 */         conditionBuf.append(" SPECIFIC_NAME LIKE ?");
/*      */       } 
/*  764 */       if (columnNamePattern != null) {
/*  765 */         if (conditionBuf.length() > 0) {
/*  766 */           conditionBuf.append(" AND");
/*      */         }
/*  768 */         conditionBuf.append(" (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL)");
/*      */       } 
/*      */       
/*  771 */       if (conditionBuf.length() > 0) {
/*  772 */         sqlBuf.append(" WHERE");
/*  773 */         sqlBuf.append(conditionBuf);
/*      */       } 
/*  775 */       sqlBuf.append(" ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ROUTINE_TYPE, ORDINAL_POSITION");
/*      */       
/*  777 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  780 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */         
/*  782 */         int nextId = 1;
/*  783 */         if (db != null) {
/*  784 */           pStmt.setString(nextId++, db);
/*      */         }
/*  786 */         if (procedureNamePattern != null) {
/*  787 */           pStmt.setString(nextId++, procedureNamePattern);
/*      */         }
/*  789 */         if (columnNamePattern != null) {
/*  790 */           pStmt.setString(nextId, columnNamePattern);
/*      */         }
/*      */         
/*  793 */         ResultSet rs = executeMetadataQuery(pStmt);
/*  794 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createProcedureColumnsFields());
/*      */         
/*  796 */         return rs;
/*      */       } finally {
/*  798 */         if (pStmt != null)
/*  799 */           pStmt.close(); 
/*      */       }  }
/*  801 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
/*      */     
/*  806 */     try { String db = getDatabase(catalog, schemaPattern);
/*      */       
/*  808 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */       
/*  810 */       if (tableNamePattern != null) {
/*  811 */         List<String> parseList = StringUtils.splitDBdotName(tableNamePattern, db, this.quotedId, this.session.getServerSession().isNoBackslashEscapesSet());
/*      */         
/*  813 */         if (parseList.size() == 2) {
/*  814 */           tableNamePattern = parseList.get(1);
/*      */         }
/*      */       } 
/*      */       
/*  818 */       PreparedStatement pStmt = null;
/*      */ 
/*      */       
/*  821 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT TABLE_CATALOG AS TABLE_CAT, TABLE_SCHEMA AS TABLE_SCHEM," : "SELECT TABLE_SCHEMA AS TABLE_CAT, NULL AS TABLE_SCHEM,");
/*      */       
/*  823 */       sqlBuf.append(" TABLE_NAME, CASE WHEN TABLE_TYPE='BASE TABLE' THEN CASE WHEN TABLE_SCHEMA = 'mysql' OR TABLE_SCHEMA = 'performance_schema' THEN 'SYSTEM TABLE' ");
/*      */       
/*  825 */       sqlBuf.append("ELSE 'TABLE' END WHEN TABLE_TYPE='TEMPORARY' THEN 'LOCAL_TEMPORARY' ELSE TABLE_TYPE END AS TABLE_TYPE, ");
/*  826 */       sqlBuf.append("TABLE_COMMENT AS REMARKS, NULL AS TYPE_CAT, NULL AS TYPE_SCHEM, NULL AS TYPE_NAME, NULL AS SELF_REFERENCING_COL_NAME, ");
/*  827 */       sqlBuf.append("NULL AS REF_GENERATION FROM INFORMATION_SCHEMA.TABLES");
/*      */       
/*  829 */       if (db != null || tableNamePattern != null) {
/*  830 */         sqlBuf.append(" WHERE");
/*      */       }
/*      */       
/*  833 */       if (db != null) {
/*  834 */         sqlBuf.append(("information_schema".equalsIgnoreCase(db) || "performance_schema".equalsIgnoreCase(db) || !StringUtils.hasWildcards(db) || this.databaseTerm
/*  835 */             .getValue() == PropertyDefinitions.DatabaseTerm.CATALOG) ? " TABLE_SCHEMA = ?" : " TABLE_SCHEMA LIKE ?");
/*      */       }
/*      */       
/*  838 */       if (tableNamePattern != null) {
/*  839 */         if (db != null) {
/*  840 */           sqlBuf.append(" AND");
/*      */         }
/*  842 */         sqlBuf.append(StringUtils.hasWildcards(tableNamePattern) ? " TABLE_NAME LIKE ?" : " TABLE_NAME = ?");
/*      */       } 
/*  844 */       if (types != null && types.length > 0) {
/*  845 */         sqlBuf.append(" HAVING TABLE_TYPE IN (?,?,?,?,?)");
/*      */       }
/*  847 */       sqlBuf.append(" ORDER BY TABLE_TYPE, TABLE_SCHEMA, TABLE_NAME");
/*      */       try {
/*  849 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*      */         
/*  851 */         int nextId = 1;
/*  852 */         if (db != null) {
/*  853 */           pStmt.setString(nextId++, (db != null) ? db : "%");
/*      */         }
/*  855 */         if (tableNamePattern != null) {
/*  856 */           pStmt.setString(nextId++, tableNamePattern);
/*      */         }
/*      */         
/*  859 */         if (types != null && types.length > 0) {
/*  860 */           int i; for (i = 0; i < 5; i++) {
/*  861 */             pStmt.setNull(nextId + i, MysqlType.VARCHAR.getJdbcType());
/*      */           }
/*  863 */           for (i = 0; i < types.length; i++) {
/*  864 */             DatabaseMetaData.TableType tableType = DatabaseMetaData.TableType.getTableTypeEqualTo(types[i]);
/*  865 */             if (tableType != DatabaseMetaData.TableType.UNKNOWN) {
/*  866 */               pStmt.setString(nextId++, tableType.getName());
/*      */             }
/*      */           } 
/*      */         } 
/*      */         
/*  871 */         ResultSet rs = executeMetadataQuery(pStmt);
/*      */         
/*  873 */         ((ResultSetInternalMethods)rs).setColumnDefinition(createTablesFields());
/*      */         
/*  875 */         return rs;
/*      */       } finally {
/*  877 */         if (pStmt != null)
/*  878 */           pStmt.close(); 
/*      */       }  }
/*  880 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
/*      */     
/*  885 */     try { if (table == null) {
/*  886 */         throw SQLError.createSQLException(Messages.getString("DatabaseMetaData.2"), "S1009", 
/*  887 */             getExceptionInterceptor());
/*      */       }
/*      */       
/*  890 */       String db = getDatabase(catalog, schema);
/*      */       
/*  892 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */       
/*  894 */       StringBuilder sqlBuf = new StringBuilder("SELECT NULL AS SCOPE, COLUMN_NAME, ");
/*  895 */       appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE", "COLUMN_TYPE");
/*  896 */       sqlBuf.append(" AS DATA_TYPE, UPPER(COLUMN_TYPE) AS TYPE_NAME,");
/*  897 */       sqlBuf.append(" CASE WHEN LCASE(DATA_TYPE)='date' THEN 10");
/*  898 */       if (this.conn.getServerVersion().meetsMinimum(ServerVersion.parseVersion("5.6.4"))) {
/*  899 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time'");
/*  900 */         sqlBuf.append("  THEN 8+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*  901 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp'");
/*  902 */         sqlBuf.append("  THEN 19+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*      */       } else {
/*  904 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' THEN 8");
/*  905 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN 19");
/*      */       } 
/*  907 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION WHEN CHARACTER_MAXIMUM_LENGTH > ");
/*  908 */       sqlBuf.append(2147483647);
/*  909 */       sqlBuf.append(" THEN ");
/*  910 */       sqlBuf.append(2147483647);
/*  911 */       sqlBuf.append(" ELSE CHARACTER_MAXIMUM_LENGTH END AS COLUMN_SIZE, ");
/*  912 */       sqlBuf.append(maxBufferSize);
/*  913 */       sqlBuf.append(" AS BUFFER_LENGTH,NUMERIC_SCALE AS DECIMAL_DIGITS, ");
/*  914 */       sqlBuf.append(Integer.toString(1));
/*  915 */       sqlBuf.append(" AS PSEUDO_COLUMN FROM INFORMATION_SCHEMA.COLUMNS WHERE");
/*  916 */       if (db != null) {
/*  917 */         sqlBuf.append(" TABLE_SCHEMA = ? AND");
/*      */       }
/*  919 */       sqlBuf.append(" TABLE_NAME = ?");
/*  920 */       sqlBuf.append(" AND EXTRA LIKE '%on update CURRENT_TIMESTAMP%'");
/*      */       
/*  922 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/*  925 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/*  926 */         int nextId = 1;
/*  927 */         if (db != null) {
/*  928 */           pStmt.setString(nextId++, db);
/*      */         }
/*  930 */         pStmt.setString(nextId, table);
/*      */         
/*  932 */         ResultSet rs = executeMetadataQuery(pStmt);
/*  933 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(getVersionColumnsFields());
/*      */         
/*  935 */         return rs;
/*      */       } finally {
/*  937 */         if (pStmt != null)
/*  938 */           pStmt.close(); 
/*      */       }  }
/*  940 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
/*      */     
/*  946 */     try { String db = getDatabase(catalog, schemaPattern);
/*      */       
/*  948 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*  949 */       boolean supportsFractSeconds = this.conn.getServerVersion().meetsMinimum(ServerVersion.parseVersion("5.6.4"));
/*      */ 
/*      */       
/*  952 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT SPECIFIC_CATALOG AS FUNCTION_CAT, SPECIFIC_SCHEMA AS `FUNCTION_SCHEM`," : "SELECT SPECIFIC_SCHEMA AS FUNCTION_CAT, NULL AS `FUNCTION_SCHEM`,");
/*      */       
/*  954 */       sqlBuf.append(" SPECIFIC_NAME AS `FUNCTION_NAME`, IFNULL(PARAMETER_NAME, '') AS `COLUMN_NAME`, CASE WHEN PARAMETER_MODE = 'IN' THEN ");
/*  955 */       sqlBuf.append(getFunctionConstant(FunctionConstant.FUNCTION_COLUMN_IN));
/*  956 */       sqlBuf.append(" WHEN PARAMETER_MODE = 'OUT' THEN ");
/*  957 */       sqlBuf.append(getFunctionConstant(FunctionConstant.FUNCTION_COLUMN_OUT));
/*  958 */       sqlBuf.append(" WHEN PARAMETER_MODE = 'INOUT' THEN ");
/*  959 */       sqlBuf.append(getFunctionConstant(FunctionConstant.FUNCTION_COLUMN_INOUT));
/*  960 */       sqlBuf.append(" WHEN ORDINAL_POSITION = 0 THEN ");
/*  961 */       sqlBuf.append(getFunctionConstant(FunctionConstant.FUNCTION_COLUMN_RETURN));
/*  962 */       sqlBuf.append(" ELSE ");
/*  963 */       sqlBuf.append(getFunctionConstant(FunctionConstant.FUNCTION_COLUMN_UNKNOWN));
/*  964 */       sqlBuf.append(" END AS `COLUMN_TYPE`, ");
/*  965 */       appendJdbcTypeMappingQuery(sqlBuf, "DATA_TYPE", "DTD_IDENTIFIER");
/*  966 */       sqlBuf.append(" AS `DATA_TYPE`, ");
/*  967 */       sqlBuf.append("UPPER(CASE");
/*  968 */       if (this.tinyInt1isBit) {
/*  969 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TINYINT' THEN CASE");
/*  970 */         sqlBuf.append(" WHEN LOCATE('ZEROFILL', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('(1)', DTD_IDENTIFIER) != 0 THEN ");
/*      */         
/*  972 */         sqlBuf.append(this.transformedBitIsBoolean ? "'BOOLEAN'" : "'BIT'");
/*  973 */         sqlBuf.append(" WHEN LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 AND LOCATE('UNSIGNED', UPPER(DATA_TYPE)) = 0 THEN 'TINYINT UNSIGNED'");
/*  974 */         sqlBuf.append(" ELSE DATA_TYPE END ");
/*      */       } 
/*  976 */       sqlBuf.append(" WHEN LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 AND LOCATE('UNSIGNED', UPPER(DATA_TYPE)) = 0 AND LOCATE('SET', UPPER(DATA_TYPE)) <> 1 AND LOCATE('ENUM', UPPER(DATA_TYPE)) <> 1 THEN CONCAT(DATA_TYPE, ' UNSIGNED')");
/*      */ 
/*      */ 
/*      */       
/*  980 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POINT' THEN 'GEOMETRY'");
/*  981 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='LINESTRING' THEN 'GEOMETRY'");
/*  982 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='POLYGON' THEN 'GEOMETRY'");
/*  983 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOINT' THEN 'GEOMETRY'");
/*  984 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTILINESTRING' THEN 'GEOMETRY'");
/*  985 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOLYGON' THEN 'GEOMETRY'");
/*  986 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMETRYCOLLECTION' THEN 'GEOMETRY'");
/*  987 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='GEOMCOLLECTION' THEN 'GEOMETRY'");
/*      */       
/*  989 */       sqlBuf.append(" ELSE UPPER(DATA_TYPE) END) AS TYPE_NAME,");
/*      */ 
/*      */       
/*  992 */       sqlBuf.append(" CASE WHEN LCASE(DATA_TYPE)='date' THEN 0");
/*  993 */       if (supportsFractSeconds) {
/*  994 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' OR LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN DATETIME_PRECISION");
/*      */       } else {
/*  996 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' OR LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN 0");
/*      */       } 
/*  998 */       if (this.tinyInt1isBit && !this.transformedBitIsBoolean) {
/*  999 */         sqlBuf.append(" WHEN UPPER(DATA_TYPE)='TINYINT' AND LOCATE('ZEROFILL', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('(1)', DTD_IDENTIFIER) != 0 THEN 1");
/*      */       }
/*      */ 
/*      */       
/* 1003 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MEDIUMINT' AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 THEN 8");
/* 1004 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='JSON' THEN 1073741824");
/* 1005 */       sqlBuf.append(" ELSE NUMERIC_PRECISION END AS `PRECISION`,");
/*      */ 
/*      */       
/* 1008 */       sqlBuf.append(" CASE WHEN LCASE(DATA_TYPE)='date' THEN 10");
/* 1009 */       if (supportsFractSeconds) {
/* 1010 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' THEN 8+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/* 1011 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp'");
/* 1012 */         sqlBuf.append("  THEN 19+(CASE WHEN DATETIME_PRECISION>0 THEN DATETIME_PRECISION+1 ELSE DATETIME_PRECISION END)");
/*      */       } else {
/* 1014 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='time' THEN 8");
/* 1015 */         sqlBuf.append(" WHEN LCASE(DATA_TYPE)='datetime' OR LCASE(DATA_TYPE)='timestamp' THEN 19");
/*      */       } 
/* 1017 */       if (this.tinyInt1isBit && !this.transformedBitIsBoolean) {
/* 1018 */         sqlBuf.append(" WHEN (UPPER(DATA_TYPE)='TINYINT' OR UPPER(DATA_TYPE)='TINYINT UNSIGNED') AND LOCATE('ZEROFILL', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) = 0 AND LOCATE('(1)', DTD_IDENTIFIER) != 0 THEN 1");
/*      */       }
/*      */ 
/*      */       
/* 1022 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='MEDIUMINT' AND LOCATE('UNSIGNED', UPPER(DTD_IDENTIFIER)) != 0 THEN 8");
/* 1023 */       sqlBuf.append(" WHEN UPPER(DATA_TYPE)='JSON' THEN 1073741824");
/* 1024 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN NUMERIC_PRECISION");
/* 1025 */       sqlBuf.append(" WHEN CHARACTER_MAXIMUM_LENGTH > 2147483647 THEN 2147483647");
/* 1026 */       sqlBuf.append(" ELSE CHARACTER_MAXIMUM_LENGTH END AS LENGTH, ");
/*      */       
/* 1028 */       sqlBuf.append("NUMERIC_SCALE AS `SCALE`, 10 AS RADIX, ");
/* 1029 */       sqlBuf.append(getFunctionConstant(FunctionConstant.FUNCTION_NULLABLE));
/* 1030 */       sqlBuf.append(" AS `NULLABLE`,  NULL AS `REMARKS`,");
/*      */       
/* 1032 */       sqlBuf.append(" CASE WHEN CHARACTER_OCTET_LENGTH > ");
/* 1033 */       sqlBuf.append(2147483647);
/* 1034 */       sqlBuf.append(" THEN ");
/* 1035 */       sqlBuf.append(2147483647);
/* 1036 */       sqlBuf.append(" ELSE CHARACTER_OCTET_LENGTH END AS `CHAR_OCTET_LENGTH`,");
/*      */       
/* 1038 */       sqlBuf.append(" ORDINAL_POSITION, 'YES' AS `IS_NULLABLE`,");
/* 1039 */       sqlBuf.append(" SPECIFIC_NAME FROM INFORMATION_SCHEMA.PARAMETERS WHERE");
/*      */       
/* 1041 */       StringBuilder conditionBuf = new StringBuilder();
/* 1042 */       if (db != null) {
/* 1043 */         conditionBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " SPECIFIC_SCHEMA LIKE ?" : " SPECIFIC_SCHEMA = ?");
/*      */       }
/*      */       
/* 1046 */       if (functionNamePattern != null) {
/* 1047 */         if (conditionBuf.length() > 0) {
/* 1048 */           conditionBuf.append(" AND");
/*      */         }
/* 1050 */         conditionBuf.append(" SPECIFIC_NAME LIKE ?");
/*      */       } 
/*      */       
/* 1053 */       if (columnNamePattern != null) {
/* 1054 */         if (conditionBuf.length() > 0) {
/* 1055 */           conditionBuf.append(" AND");
/*      */         }
/* 1057 */         conditionBuf.append(" (PARAMETER_NAME LIKE ? OR PARAMETER_NAME IS NULL)");
/*      */       } 
/*      */       
/* 1060 */       if (conditionBuf.length() > 0) {
/* 1061 */         conditionBuf.append(" AND");
/*      */       }
/* 1063 */       conditionBuf.append(" ROUTINE_TYPE='FUNCTION'");
/*      */       
/* 1065 */       sqlBuf.append(conditionBuf);
/* 1066 */       sqlBuf.append(" ORDER BY SPECIFIC_SCHEMA, SPECIFIC_NAME, ORDINAL_POSITION");
/*      */       
/* 1068 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/* 1071 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/* 1072 */         int nextId = 1;
/* 1073 */         if (db != null) {
/* 1074 */           pStmt.setString(nextId++, db);
/*      */         }
/* 1076 */         if (functionNamePattern != null) {
/* 1077 */           pStmt.setString(nextId++, functionNamePattern);
/*      */         }
/* 1079 */         if (columnNamePattern != null) {
/* 1080 */           pStmt.setString(nextId, columnNamePattern);
/*      */         }
/*      */         
/* 1083 */         ResultSet rs = executeMetadataQuery(pStmt);
/* 1084 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(createFunctionColumnsFields());
/*      */         
/* 1086 */         return rs;
/*      */       } finally {
/* 1088 */         if (pStmt != null)
/* 1089 */           pStmt.close(); 
/*      */       }  }
/* 1091 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected int getFunctionConstant(FunctionConstant constant) {
/* 1103 */     switch (constant) {
/*      */       case TINYINT:
/* 1105 */         return 1;
/*      */       case TINYINT_UNSIGNED:
/* 1107 */         return 2;
/*      */       case null:
/* 1109 */         return 3;
/*      */       case null:
/* 1111 */         return 4;
/*      */       case null:
/* 1113 */         return 5;
/*      */       case null:
/* 1115 */         return 0;
/*      */       case null:
/* 1117 */         return 0;
/*      */       case null:
/* 1119 */         return 1;
/*      */       case null:
/* 1121 */         return 2;
/*      */     } 
/* 1123 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
/*      */     
/* 1130 */     try { String db = getDatabase(catalog, schemaPattern);
/*      */       
/* 1132 */       db = this.pedantic ? db : StringUtils.unQuoteIdentifier(db, this.quotedId);
/*      */ 
/*      */       
/* 1135 */       StringBuilder sqlBuf = new StringBuilder((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? "SELECT ROUTINE_CATALOG AS FUNCTION_CAT, ROUTINE_SCHEMA AS FUNCTION_SCHEM," : "SELECT ROUTINE_SCHEMA AS FUNCTION_CAT, NULL AS FUNCTION_SCHEM,");
/*      */       
/* 1137 */       sqlBuf.append(" ROUTINE_NAME AS FUNCTION_NAME, ROUTINE_COMMENT AS REMARKS, ");
/* 1138 */       sqlBuf.append(1);
/* 1139 */       sqlBuf.append(" AS FUNCTION_TYPE, ROUTINE_NAME AS SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES");
/* 1140 */       sqlBuf.append(" WHERE ROUTINE_TYPE LIKE 'FUNCTION'");
/* 1141 */       if (db != null) {
/* 1142 */         sqlBuf.append((this.databaseTerm.getValue() == PropertyDefinitions.DatabaseTerm.SCHEMA) ? " AND ROUTINE_SCHEMA LIKE ?" : " AND ROUTINE_SCHEMA = ?");
/*      */       }
/* 1144 */       if (functionNamePattern != null) {
/* 1145 */         sqlBuf.append(" AND ROUTINE_NAME LIKE ?");
/*      */       }
/*      */       
/* 1148 */       sqlBuf.append(" ORDER BY FUNCTION_CAT, FUNCTION_SCHEM, FUNCTION_NAME, SPECIFIC_NAME");
/*      */       
/* 1150 */       PreparedStatement pStmt = null;
/*      */       
/*      */       try {
/* 1153 */         pStmt = prepareMetaDataSafeStatement(sqlBuf.toString());
/* 1154 */         int nextId = 1;
/* 1155 */         if (db != null) {
/* 1156 */           pStmt.setString(nextId++, db);
/*      */         }
/* 1158 */         if (functionNamePattern != null) {
/* 1159 */           pStmt.setString(nextId, functionNamePattern);
/*      */         }
/*      */         
/* 1162 */         ResultSet rs = executeMetadataQuery(pStmt);
/* 1163 */         ((ResultSetInternalMethods)rs).getColumnDefinition().setFields(getFunctionsFields());
/*      */         
/* 1165 */         return rs;
/*      */       } finally {
/* 1167 */         if (pStmt != null)
/* 1168 */           pStmt.close(); 
/*      */       }  }
/* 1170 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   public String getSQLKeywords() throws SQLException {
/*      */     
/* 1175 */     try { if (!this.conn.getServerVersion().meetsMinimum(ServerVersion.parseVersion("8.0.11"))) {
/* 1176 */         return super.getSQLKeywords();
/*      */       }
/*      */       
/* 1179 */       String keywords = keywordsCache.get(this.conn.getServerVersion());
/* 1180 */       if (keywords != null) {
/* 1181 */         return keywords;
/*      */       }
/*      */       
/* 1184 */       synchronized (keywordsCache)
/*      */       
/* 1186 */       { keywords = keywordsCache.get(this.conn.getServerVersion());
/* 1187 */         if (keywords != null) {
/* 1188 */           return keywords;
/*      */         }
/*      */         
/* 1191 */         List<String> keywordsFromServer = new ArrayList<>();
/* 1192 */         Statement stmt = this.conn.getMetadataSafeStatement();
/* 1193 */         ResultSet rs = stmt.executeQuery("SELECT WORD FROM INFORMATION_SCHEMA.KEYWORDS WHERE RESERVED=1 ORDER BY WORD");
/* 1194 */         while (rs.next()) {
/* 1195 */           keywordsFromServer.add(rs.getString(1));
/*      */         }
/* 1197 */         stmt.close();
/*      */         
/* 1199 */         keywordsFromServer.removeAll(SQL2003_KEYWORDS);
/* 1200 */         keywords = String.join(",", (Iterable)keywordsFromServer);
/*      */         
/* 1202 */         keywordsCache.put(this.conn.getServerVersion(), keywords);
/* 1203 */         return keywords; }  }
/* 1204 */     catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   private final void appendJdbcTypeMappingQuery(StringBuilder buf, String mysqlTypeColumnName, String fullMysqlTypeColumnName) {
/* 1209 */     buf.append("CASE ");
/* 1210 */     for (MysqlType mysqlType : MysqlType.values()) {
/*      */       
/* 1212 */       buf.append(" WHEN UPPER(");
/* 1213 */       buf.append(mysqlTypeColumnName);
/* 1214 */       buf.append(")='");
/* 1215 */       buf.append(mysqlType.getName());
/* 1216 */       buf.append("' THEN ");
/*      */       
/* 1218 */       switch (mysqlType) {
/*      */         case TINYINT:
/*      */         case TINYINT_UNSIGNED:
/* 1221 */           if (this.tinyInt1isBit) {
/* 1222 */             buf.append("CASE");
/* 1223 */             buf.append(" WHEN LOCATE('ZEROFILL', UPPER(");
/* 1224 */             buf.append(fullMysqlTypeColumnName);
/* 1225 */             buf.append(")) = 0 AND LOCATE('UNSIGNED', UPPER(");
/* 1226 */             buf.append(fullMysqlTypeColumnName);
/* 1227 */             buf.append(")) = 0 AND LOCATE('(1)', ");
/* 1228 */             buf.append(fullMysqlTypeColumnName);
/* 1229 */             buf.append(") != 0 THEN ");
/* 1230 */             buf.append(this.transformedBitIsBoolean ? "16" : "-7");
/* 1231 */             buf.append(" ELSE -6 END "); break;
/*      */           } 
/* 1233 */           buf.append(mysqlType.getJdbcType());
/*      */           break;
/*      */ 
/*      */         
/*      */         default:
/* 1238 */           buf.append(mysqlType.getJdbcType());
/*      */           break;
/*      */       } 
/*      */     } 
/* 1242 */     buf.append(" WHEN UPPER(DATA_TYPE)='POINT' THEN -2");
/* 1243 */     buf.append(" WHEN UPPER(DATA_TYPE)='LINESTRING' THEN -2");
/* 1244 */     buf.append(" WHEN UPPER(DATA_TYPE)='POLYGON' THEN -2");
/* 1245 */     buf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOINT' THEN -2");
/* 1246 */     buf.append(" WHEN UPPER(DATA_TYPE)='MULTILINESTRING' THEN -2");
/* 1247 */     buf.append(" WHEN UPPER(DATA_TYPE)='MULTIPOLYGON' THEN -2");
/* 1248 */     buf.append(" WHEN UPPER(DATA_TYPE)='GEOMETRYCOLLECTION' THEN -2");
/* 1249 */     buf.append(" WHEN UPPER(DATA_TYPE)='GEOMCOLLECTION' THEN -2");
/*      */     
/* 1251 */     buf.append(" ELSE 1111");
/* 1252 */     buf.append(" END ");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultSet getSchemas() throws SQLException {
/*      */     
/* 1259 */     try { return super.getSchemas(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
/*      */     
/* 1265 */     try { return super.getSchemas(catalog, schemaPattern); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getCatalogs() throws SQLException {
/*      */     
/* 1271 */     try { return super.getCatalogs(); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
/*      */     
/* 1277 */     try { return super.getTablePrivileges(catalog, schemaPattern, tableNamePattern); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */   
/*      */   public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
/*      */     
/* 1283 */     try { return super.getBestRowIdentifier(catalog, schema, table, scope, nullable); } catch (CJException cJException) { throw SQLExceptionsMapping.translateException(cJException, getExceptionInterceptor()); }
/*      */   
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\DatabaseMetaDataUsingInfoSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */