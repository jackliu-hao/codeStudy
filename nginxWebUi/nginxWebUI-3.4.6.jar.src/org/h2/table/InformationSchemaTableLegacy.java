/*      */ package org.h2.table;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.sql.ResultSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.BitSet;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.h2.command.Command;
/*      */ import org.h2.command.Parser;
/*      */ import org.h2.command.dml.Help;
/*      */ import org.h2.constraint.Constraint;
/*      */ import org.h2.constraint.ConstraintActionType;
/*      */ import org.h2.constraint.ConstraintCheck;
/*      */ import org.h2.constraint.ConstraintDomain;
/*      */ import org.h2.constraint.ConstraintReferential;
/*      */ import org.h2.constraint.ConstraintUnique;
/*      */ import org.h2.engine.Constants;
/*      */ import org.h2.engine.DbObject;
/*      */ import org.h2.engine.QueryStatisticsData;
/*      */ import org.h2.engine.Right;
/*      */ import org.h2.engine.RightOwner;
/*      */ import org.h2.engine.Role;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.engine.Setting;
/*      */ import org.h2.engine.User;
/*      */ import org.h2.expression.ExpressionVisitor;
/*      */ import org.h2.expression.ValueExpression;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.index.MetaIndex;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.mvstore.FileStore;
/*      */ import org.h2.mvstore.MVStore;
/*      */ import org.h2.mvstore.db.Store;
/*      */ import org.h2.result.Row;
/*      */ import org.h2.result.SearchRow;
/*      */ import org.h2.schema.Constant;
/*      */ import org.h2.schema.Domain;
/*      */ import org.h2.schema.FunctionAlias;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.schema.SchemaObject;
/*      */ import org.h2.schema.Sequence;
/*      */ import org.h2.schema.TriggerObject;
/*      */ import org.h2.schema.UserDefinedFunction;
/*      */ import org.h2.store.InDoubtTransaction;
/*      */ import org.h2.tools.Csv;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.NetworkConnectionInfo;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueBoolean;
/*      */ import org.h2.value.ValueDouble;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueSmallint;
/*      */ import org.h2.value.ValueToObjectConverter2;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class InformationSchemaTableLegacy
/*      */   extends MetaTable
/*      */ {
/*      */   private static final String CHARACTER_SET_NAME = "Unicode";
/*      */   private static final int TABLES = 0;
/*      */   private static final int COLUMNS = 1;
/*      */   private static final int INDEXES = 2;
/*      */   private static final int TABLE_TYPES = 3;
/*      */   private static final int TYPE_INFO = 4;
/*      */   private static final int CATALOGS = 5;
/*      */   private static final int SETTINGS = 6;
/*      */   private static final int HELP = 7;
/*      */   private static final int SEQUENCES = 8;
/*      */   private static final int USERS = 9;
/*      */   private static final int ROLES = 10;
/*      */   private static final int RIGHTS = 11;
/*      */   private static final int FUNCTION_ALIASES = 12;
/*      */   private static final int SCHEMATA = 13;
/*      */   private static final int TABLE_PRIVILEGES = 14;
/*      */   private static final int COLUMN_PRIVILEGES = 15;
/*      */   private static final int COLLATIONS = 16;
/*      */   private static final int VIEWS = 17;
/*      */   private static final int IN_DOUBT = 18;
/*      */   private static final int CROSS_REFERENCES = 19;
/*      */   private static final int FUNCTION_COLUMNS = 20;
/*      */   private static final int CONSTRAINTS = 21;
/*      */   private static final int CONSTANTS = 22;
/*      */   private static final int DOMAINS = 23;
/*      */   private static final int TRIGGERS = 24;
/*      */   private static final int SESSIONS = 25;
/*      */   private static final int LOCKS = 26;
/*      */   private static final int SESSION_STATE = 27;
/*      */   private static final int QUERY_STATISTICS = 28;
/*      */   private static final int SYNONYMS = 29;
/*      */   private static final int TABLE_CONSTRAINTS = 30;
/*      */   private static final int DOMAIN_CONSTRAINTS = 31;
/*      */   private static final int KEY_COLUMN_USAGE = 32;
/*      */   private static final int REFERENTIAL_CONSTRAINTS = 33;
/*      */   private static final int CHECK_CONSTRAINTS = 34;
/*      */   private static final int CONSTRAINT_COLUMN_USAGE = 35;
/*      */   public static final int META_TABLE_TYPE_COUNT = 36;
/*      */   
/*      */   public InformationSchemaTableLegacy(Schema paramSchema, int paramInt1, int paramInt2) {
/*  141 */     super(paramSchema, paramInt1, paramInt2);
/*      */     Column[] arrayOfColumn;
/*  143 */     String str = null;
/*  144 */     switch (paramInt2) {
/*      */       case 0:
/*  146 */         setMetaTableName("TABLES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  160 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("TABLE_TYPE"), column("STORAGE_TYPE"), column("SQL"), column("REMARKS"), column("LAST_MODIFICATION", TypeInfo.TYPE_BIGINT), column("ID", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("TABLE_CLASS"), column("ROW_COUNT_ESTIMATE", TypeInfo.TYPE_BIGINT) };
/*      */         
/*  162 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 1:
/*  165 */         setMetaTableName("COLUMNS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  202 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("COLUMN_DEFAULT"), column("IS_NULLABLE"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_INTEGER), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("CHARACTER_SET_NAME"), column("COLLATION_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("IS_GENERATED"), column("GENERATION_EXPRESSION"), column("TYPE_NAME"), column("NULLABLE", TypeInfo.TYPE_INTEGER), column("IS_COMPUTED", TypeInfo.TYPE_BOOLEAN), column("SELECTIVITY", TypeInfo.TYPE_INTEGER), column("SEQUENCE_NAME"), column("REMARKS"), column("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT), column("COLUMN_TYPE"), column("COLUMN_ON_UPDATE"), column("IS_VISIBLE"), column("CHECK_CONSTRAINT") };
/*      */         
/*  204 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 2:
/*  207 */         setMetaTableName("INDEXES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  229 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("NON_UNIQUE", TypeInfo.TYPE_BOOLEAN), column("INDEX_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT), column("COLUMN_NAME"), column("CARDINALITY", TypeInfo.TYPE_INTEGER), column("PRIMARY_KEY", TypeInfo.TYPE_BOOLEAN), column("INDEX_TYPE_NAME"), column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), column("INDEX_TYPE", TypeInfo.TYPE_SMALLINT), column("ASC_OR_DESC"), column("PAGES", TypeInfo.TYPE_INTEGER), column("FILTER_CONDITION"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER), column("SORT_TYPE", TypeInfo.TYPE_INTEGER), column("CONSTRAINT_NAME"), column("INDEX_CLASS") };
/*      */         
/*  231 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 3:
/*  234 */         setMetaTableName("TABLE_TYPES");
/*      */         
/*  236 */         arrayOfColumn = new Column[] { column("TYPE") };
/*      */         break;
/*      */       
/*      */       case 4:
/*  240 */         setMetaTableName("TYPE_INFO");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  255 */         arrayOfColumn = new Column[] { column("TYPE_NAME"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("PRECISION", TypeInfo.TYPE_INTEGER), column("PREFIX"), column("SUFFIX"), column("PARAMS"), column("AUTO_INCREMENT", TypeInfo.TYPE_BOOLEAN), column("MINIMUM_SCALE", TypeInfo.TYPE_SMALLINT), column("MAXIMUM_SCALE", TypeInfo.TYPE_SMALLINT), column("RADIX", TypeInfo.TYPE_INTEGER), column("POS", TypeInfo.TYPE_INTEGER), column("CASE_SENSITIVE", TypeInfo.TYPE_BOOLEAN), column("NULLABLE", TypeInfo.TYPE_SMALLINT), column("SEARCHABLE", TypeInfo.TYPE_SMALLINT) };
/*      */         break;
/*      */       
/*      */       case 5:
/*  259 */         setMetaTableName("CATALOGS");
/*      */         
/*  261 */         arrayOfColumn = new Column[] { column("CATALOG_NAME") };
/*      */         break;
/*      */       
/*      */       case 6:
/*  265 */         setMetaTableName("SETTINGS");
/*      */ 
/*      */         
/*  268 */         arrayOfColumn = new Column[] { column("NAME"), column("VALUE") };
/*      */         break;
/*      */       
/*      */       case 7:
/*  272 */         setMetaTableName("HELP");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  278 */         arrayOfColumn = new Column[] { column("ID", TypeInfo.TYPE_INTEGER), column("SECTION"), column("TOPIC"), column("SYNTAX"), column("TEXT") };
/*      */         break;
/*      */       
/*      */       case 8:
/*  282 */         setMetaTableName("SEQUENCES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  307 */         arrayOfColumn = new Column[] { column("SEQUENCE_CATALOG"), column("SEQUENCE_SCHEMA"), column("SEQUENCE_NAME"), column("DATA_TYPE"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("START_VALUE", TypeInfo.TYPE_BIGINT), column("MINIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("MAXIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("INCREMENT", TypeInfo.TYPE_BIGINT), column("CYCLE_OPTION"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("CURRENT_VALUE", TypeInfo.TYPE_BIGINT), column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("CACHE", TypeInfo.TYPE_BIGINT), column("ID", TypeInfo.TYPE_INTEGER), column("MIN_VALUE", TypeInfo.TYPE_BIGINT), column("MAX_VALUE", TypeInfo.TYPE_BIGINT), column("IS_CYCLE", TypeInfo.TYPE_BOOLEAN) };
/*      */         break;
/*      */       
/*      */       case 9:
/*  311 */         setMetaTableName("USERS");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  316 */         arrayOfColumn = new Column[] { column("NAME"), column("ADMIN"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 10:
/*  320 */         setMetaTableName("ROLES");
/*      */ 
/*      */ 
/*      */         
/*  324 */         arrayOfColumn = new Column[] { column("NAME"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 11:
/*  328 */         setMetaTableName("RIGHTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  336 */         arrayOfColumn = new Column[] { column("GRANTEE"), column("GRANTEETYPE"), column("GRANTEDROLE"), column("RIGHTS"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         
/*  338 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 12:
/*  341 */         setMetaTableName("FUNCTION_ALIASES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  354 */         arrayOfColumn = new Column[] { column("ALIAS_CATALOG"), column("ALIAS_SCHEMA"), column("ALIAS_NAME"), column("JAVA_CLASS"), column("JAVA_METHOD"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("COLUMN_COUNT", TypeInfo.TYPE_INTEGER), column("RETURNS_RESULT", TypeInfo.TYPE_SMALLINT), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER), column("SOURCE") };
/*      */         break;
/*      */       
/*      */       case 20:
/*  358 */         setMetaTableName("FUNCTION_COLUMNS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  376 */         arrayOfColumn = new Column[] { column("ALIAS_CATALOG"), column("ALIAS_SCHEMA"), column("ALIAS_NAME"), column("JAVA_CLASS"), column("JAVA_METHOD"), column("COLUMN_COUNT", TypeInfo.TYPE_INTEGER), column("POS", TypeInfo.TYPE_INTEGER), column("COLUMN_NAME"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("PRECISION", TypeInfo.TYPE_INTEGER), column("SCALE", TypeInfo.TYPE_SMALLINT), column("RADIX", TypeInfo.TYPE_SMALLINT), column("NULLABLE", TypeInfo.TYPE_SMALLINT), column("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT), column("REMARKS"), column("COLUMN_DEFAULT") };
/*      */         break;
/*      */       
/*      */       case 13:
/*  380 */         setMetaTableName("SCHEMATA");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  389 */         arrayOfColumn = new Column[] { column("CATALOG_NAME"), column("SCHEMA_NAME"), column("SCHEMA_OWNER"), column("DEFAULT_CHARACTER_SET_NAME"), column("DEFAULT_COLLATION_NAME"), column("IS_DEFAULT", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 14:
/*  393 */         setMetaTableName("TABLE_PRIVILEGES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  401 */         arrayOfColumn = new Column[] { column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE") };
/*      */         
/*  403 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 15:
/*  406 */         setMetaTableName("COLUMN_PRIVILEGES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  415 */         arrayOfColumn = new Column[] { column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE") };
/*      */         
/*  417 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 16:
/*  420 */         setMetaTableName("COLLATIONS");
/*      */ 
/*      */         
/*  423 */         arrayOfColumn = new Column[] { column("NAME"), column("KEY") };
/*      */         break;
/*      */       
/*      */       case 17:
/*  427 */         setMetaTableName("VIEWS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  437 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("VIEW_DEFINITION"), column("CHECK_OPTION"), column("IS_UPDATABLE"), column("STATUS"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         
/*  439 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 18:
/*  442 */         setMetaTableName("IN_DOUBT");
/*      */ 
/*      */         
/*  445 */         arrayOfColumn = new Column[] { column("TRANSACTION"), column("STATE") };
/*      */         break;
/*      */       
/*      */       case 19:
/*  449 */         setMetaTableName("CROSS_REFERENCES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  464 */         arrayOfColumn = new Column[] { column("PKTABLE_CATALOG"), column("PKTABLE_SCHEMA"), column("PKTABLE_NAME"), column("PKCOLUMN_NAME"), column("FKTABLE_CATALOG"), column("FKTABLE_SCHEMA"), column("FKTABLE_NAME"), column("FKCOLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT), column("UPDATE_RULE", TypeInfo.TYPE_SMALLINT), column("DELETE_RULE", TypeInfo.TYPE_SMALLINT), column("FK_NAME"), column("PK_NAME"), column("DEFERRABILITY", TypeInfo.TYPE_SMALLINT) };
/*      */         
/*  466 */         str = "PKTABLE_NAME";
/*      */         break;
/*      */       case 21:
/*  469 */         setMetaTableName("CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  483 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CONSTRAINT_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("UNIQUE_INDEX_NAME"), column("CHECK_EXPRESSION"), column("COLUMN_LIST"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         
/*  485 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 22:
/*  488 */         setMetaTableName("CONSTANTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  496 */         arrayOfColumn = new Column[] { column("CONSTANT_CATALOG"), column("CONSTANT_SCHEMA"), column("CONSTANT_NAME"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 23:
/*  500 */         setMetaTableName("DOMAINS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  521 */         arrayOfColumn = new Column[] { column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("DOMAIN_DEFAULT"), column("DOMAIN_ON_UPDATE"), column("DATA_TYPE", TypeInfo.TYPE_INTEGER), column("PRECISION", TypeInfo.TYPE_INTEGER), column("SCALE", TypeInfo.TYPE_INTEGER), column("TYPE_NAME"), column("PARENT_DOMAIN_CATALOG"), column("PARENT_DOMAIN_SCHEMA"), column("PARENT_DOMAIN_NAME"), column("SELECTIVITY", TypeInfo.TYPE_INTEGER), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER), column("COLUMN_DEFAULT"), column("IS_NULLABLE"), column("CHECK_CONSTRAINT") };
/*      */         break;
/*      */       
/*      */       case 24:
/*  525 */         setMetaTableName("TRIGGERS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  540 */         arrayOfColumn = new Column[] { column("TRIGGER_CATALOG"), column("TRIGGER_SCHEMA"), column("TRIGGER_NAME"), column("TRIGGER_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("BEFORE", TypeInfo.TYPE_BOOLEAN), column("JAVA_CLASS"), column("QUEUE_SIZE", TypeInfo.TYPE_INTEGER), column("NO_WAIT", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 25:
/*  544 */         setMetaTableName("SESSIONS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  558 */         arrayOfColumn = new Column[] { column("ID", TypeInfo.TYPE_INTEGER), column("USER_NAME"), column("SERVER"), column("CLIENT_ADDR"), column("CLIENT_INFO"), column("SESSION_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("ISOLATION_LEVEL"), column("STATEMENT"), column("STATEMENT_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("CONTAINS_UNCOMMITTED", TypeInfo.TYPE_BOOLEAN), column("STATE"), column("BLOCKER_ID", TypeInfo.TYPE_INTEGER), column("SLEEP_SINCE", TypeInfo.TYPE_TIMESTAMP_TZ) };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 26:
/*  563 */         setMetaTableName("LOCKS");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  568 */         arrayOfColumn = new Column[] { column("TABLE_SCHEMA"), column("TABLE_NAME"), column("SESSION_ID", TypeInfo.TYPE_INTEGER), column("LOCK_TYPE") };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 27:
/*  573 */         setMetaTableName("SESSION_STATE");
/*      */ 
/*      */         
/*  576 */         arrayOfColumn = new Column[] { column("KEY"), column("SQL") };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 28:
/*  581 */         setMetaTableName("QUERY_STATISTICS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  594 */         arrayOfColumn = new Column[] { column("SQL_STATEMENT"), column("EXECUTION_COUNT", TypeInfo.TYPE_INTEGER), column("MIN_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MAX_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("CUMULATIVE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("AVERAGE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("STD_DEV_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MIN_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("MAX_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("CUMULATIVE_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("AVERAGE_ROW_COUNT", TypeInfo.TYPE_DOUBLE), column("STD_DEV_ROW_COUNT", TypeInfo.TYPE_DOUBLE) };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 29:
/*  599 */         setMetaTableName("SYNONYMS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  609 */         arrayOfColumn = new Column[] { column("SYNONYM_CATALOG"), column("SYNONYM_SCHEMA"), column("SYNONYM_NAME"), column("SYNONYM_FOR"), column("SYNONYM_FOR_SCHEMA"), column("TYPE_NAME"), column("STATUS"), column("REMARKS"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         
/*  611 */         str = "SYNONYM_NAME";
/*      */         break;
/*      */       
/*      */       case 30:
/*  615 */         setMetaTableName("TABLE_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  628 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CONSTRAINT_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         
/*  630 */         str = "TABLE_NAME";
/*      */         break;
/*      */       
/*      */       case 31:
/*  634 */         setMetaTableName("DOMAIN_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  646 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("REMARKS"), column("SQL"), column("ID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 32:
/*  651 */         setMetaTableName("KEY_COLUMN_USAGE");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  664 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("POSITION_IN_UNIQUE_CONSTRAINT", TypeInfo.TYPE_INTEGER), column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME") };
/*      */         
/*  666 */         str = "TABLE_NAME";
/*      */         break;
/*      */       
/*      */       case 33:
/*  670 */         setMetaTableName("REFERENTIAL_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  680 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("UNIQUE_CONSTRAINT_CATALOG"), column("UNIQUE_CONSTRAINT_SCHEMA"), column("UNIQUE_CONSTRAINT_NAME"), column("MATCH_OPTION"), column("UPDATE_RULE"), column("DELETE_RULE") };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 34:
/*  685 */         setMetaTableName("CHECK_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  690 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CHECK_CLAUSE") };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 35:
/*  695 */         setMetaTableName("CONSTRAINT_COLUMN_USAGE");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  703 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME") };
/*      */         
/*  705 */         str = "TABLE_NAME";
/*      */         break;
/*      */       
/*      */       default:
/*  709 */         throw DbException.getInternalError("type=" + paramInt2);
/*      */     } 
/*  711 */     setColumns(arrayOfColumn);
/*      */     
/*  713 */     if (str == null) {
/*  714 */       this.indexColumn = -1;
/*  715 */       this.metaIndex = null;
/*      */     } else {
/*  717 */       this.indexColumn = getColumn(this.database.sysIdentifier(str)).getColumnId();
/*  718 */       IndexColumn[] arrayOfIndexColumn = IndexColumn.wrap(new Column[] { arrayOfColumn[this.indexColumn] });
/*      */       
/*  720 */       this.metaIndex = new MetaIndex(this, arrayOfIndexColumn, false);
/*      */     } 
/*      */   }
/*      */   
/*      */   private static String replaceNullWithEmpty(String paramString) {
/*  725 */     return (paramString == null) ? "" : paramString; } public ArrayList<Row> generateRows(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) { ArrayList<Table> arrayList2; byte b1; BitSet bitSet; String str2; ArrayList arrayList1; String[] arrayOfString; QueryStatisticsData queryStatisticsData; byte b2; Store store;
/*      */     String str3;
/*      */     MVStore mVStore;
/*      */     TimeZoneProvider timeZoneProvider;
/*      */     FileStore fileStore;
/*  730 */     Value value1 = null, value2 = null;
/*      */     
/*  732 */     if (this.indexColumn >= 0) {
/*  733 */       if (paramSearchRow1 != null) {
/*  734 */         value1 = paramSearchRow1.getValue(this.indexColumn);
/*      */       }
/*  736 */       if (paramSearchRow2 != null) {
/*  737 */         value2 = paramSearchRow2.getValue(this.indexColumn);
/*      */       }
/*      */     } 
/*      */     
/*  741 */     ArrayList<Row> arrayList = Utils.newSmallArrayList();
/*  742 */     String str1 = this.database.getShortName();
/*  743 */     boolean bool = paramSessionLocal.getUser().isAdmin();
/*  744 */     switch (this.type) {
/*      */       case 0:
/*  746 */         for (Table table : getAllTables(paramSessionLocal)) {
/*  747 */           String str5, str4 = table.getName();
/*  748 */           if (!checkIndex(paramSessionLocal, str4, value1, value2)) {
/*      */             continue;
/*      */           }
/*  751 */           if (hideTable(table, paramSessionLocal)) {
/*      */             continue;
/*      */           }
/*      */           
/*  755 */           if (table.isTemporary()) {
/*  756 */             if (table.isGlobalTemporary()) {
/*  757 */               str5 = "GLOBAL TEMPORARY";
/*      */             } else {
/*  759 */               str5 = "LOCAL TEMPORARY";
/*      */             } 
/*      */           } else {
/*  762 */             str5 = table.isPersistIndexes() ? "CACHED" : "MEMORY";
/*      */           } 
/*      */           
/*  765 */           String str6 = table.getCreateSQL();
/*  766 */           if (!bool && 
/*  767 */             str6 != null && str6.contains("--hide--"))
/*      */           {
/*  769 */             str6 = "-";
/*      */           }
/*      */           
/*  772 */           add(paramSessionLocal, arrayList, new Object[] { str1, table
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  777 */                 .getSchema().getName(), str4, table
/*      */ 
/*      */ 
/*      */                 
/*  781 */                 .getTableType().toString(), str5, str6, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  787 */                 replaceNullWithEmpty(table.getComment()), 
/*      */                 
/*  789 */                 ValueBigint.get(table.getMaxDataModificationId()), 
/*      */                 
/*  791 */                 ValueInteger.get(table.getId()), null, table
/*      */ 
/*      */ 
/*      */                 
/*  795 */                 .getClass().getName(), 
/*      */                 
/*  797 */                 ValueBigint.get(table.getRowCountApproximation(paramSessionLocal)) });
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2358 */         return arrayList;case 1: if (value1 != null && value1.equals(value2)) { String str = value1.getString(); if (str == null) return arrayList;  arrayList2 = getTablesByName(paramSessionLocal, str); } else { arrayList2 = getAllTables(paramSessionLocal); }  for (Table table : arrayList2) { String str4 = table.getName(); if (!checkIndex(paramSessionLocal, str4, value1, value2)) continue;  if (hideTable(table, paramSessionLocal)) continue;  Column[] arrayOfColumn = table.getColumns(); String str5 = this.database.getCompareMode().getName(); for (byte b = 0; b < arrayOfColumn.length; b++) { boolean bool1; Column column = arrayOfColumn[b]; Domain domain = column.getDomain(); TypeInfo typeInfo = column.getType(); ValueInteger valueInteger1 = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision())); ValueInteger valueInteger2 = ValueInteger.get(typeInfo.getScale()); Sequence sequence = column.getSequence(); int i = typeInfo.getValueType(); switch (i) { case 17: case 18: case 19: case 20: case 21: case 27: case 31: case 33: case 34: bool1 = true; break;default: bool1 = false; break; }  boolean bool2 = column.isGenerated(); boolean bool3 = DataType.isIntervalType(i); String str = column.getCreateSQLWithoutName(); add(paramSessionLocal, arrayList, new Object[] { str1, table.getSchema().getName(), str4, column.getName(), ValueInteger.get(b + 1), bool2 ? null : column.getDefaultSQL(), column.isNullable() ? "YES" : "NO", ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), valueInteger1, valueInteger1, valueInteger1, ValueInteger.get(10), valueInteger2, bool1 ? valueInteger2 : null, bool3 ? str.substring(9) : null, bool3 ? valueInteger1 : null, "Unicode", str5, (domain != null) ? str1 : null, (domain != null) ? domain.getSchema().getName() : null, (domain != null) ? domain.getName() : null, bool2 ? "ALWAYS" : "NEVER", bool2 ? column.getDefaultSQL() : null, identifier(bool3 ? "INTERVAL" : typeInfo.getDeclaredTypeName()), ValueInteger.get(column.isNullable() ? 1 : 0), ValueBoolean.get(bool2), ValueInteger.get(column.getSelectivity()), (sequence == null) ? null : sequence.getName(), replaceNullWithEmpty(column.getComment()), null, str, column.getOnUpdateSQL(), ValueBoolean.get(column.getVisible()), null }); }  }  return arrayList;case 2: if (value1 != null && value1.equals(value2)) { String str = value1.getString(); if (str == null) return arrayList;  arrayList2 = getTablesByName(paramSessionLocal, str); } else { arrayList2 = getAllTables(paramSessionLocal); }  for (Table table : arrayList2) { String str = table.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  if (hideTable(table, paramSessionLocal)) continue;  ArrayList<Index> arrayList3 = table.getIndexes(); ArrayList<Constraint> arrayList4 = table.getConstraints(); for (byte b = 0; arrayList3 != null && b < arrayList3.size(); b++) { Index index = arrayList3.get(b); if (index.getCreateSQL() != null) { String str4 = null; for (byte b3 = 0; arrayList4 != null && b3 < arrayList4.size(); b3++) { Constraint constraint = arrayList4.get(b3); if (constraint.usesIndex(index)) if (index.getIndexType().isPrimaryKey()) { if (constraint.getConstraintType() == Constraint.Type.PRIMARY_KEY) str4 = constraint.getName();  } else { str4 = constraint.getName(); }   }  IndexColumn[] arrayOfIndexColumn = index.getIndexColumns(); int i = index.getUniqueColumnCount(); String str5 = index.getClass().getName(); for (byte b4 = 0; b4 < arrayOfIndexColumn.length; b4++) { IndexColumn indexColumn = arrayOfIndexColumn[b4]; Column column = indexColumn.column; add(paramSessionLocal, arrayList, new Object[] { str1, table.getSchema().getName(), str, ValueBoolean.get((b4 >= i)), index.getName(), ValueSmallint.get((short)(b4 + 1)), column.getName(), ValueInteger.get(0), ValueBoolean.get(index.getIndexType().isPrimaryKey()), index.getIndexType().getSQL(), ValueBoolean.get(index.getIndexType().getBelongsToConstraint()), ValueSmallint.get((short)3), ((indexColumn.sortType & 0x1) != 0) ? "D" : "A", ValueInteger.get(0), "", replaceNullWithEmpty(index.getComment()), index.getCreateSQL(), ValueInteger.get(index.getId()), ValueInteger.get(indexColumn.sortType), str4, str5 }); }  }  }  }  return arrayList;case 3: add(paramSessionLocal, arrayList, new Object[] { TableType.TABLE.toString() }); add(paramSessionLocal, arrayList, new Object[] { TableType.TABLE_LINK.toString() }); add(paramSessionLocal, arrayList, new Object[] { TableType.SYSTEM_TABLE.toString() }); add(paramSessionLocal, arrayList, new Object[] { TableType.VIEW.toString() }); add(paramSessionLocal, arrayList, new Object[] { TableType.EXTERNAL_TABLE_ENGINE.toString() }); return arrayList;case 4: for (b1 = 1, b2 = 42; b1 < b2; b1++) { DataType dataType = DataType.getDataType(b1); add(paramSessionLocal, arrayList, new Object[] { Value.getTypeName(dataType.type), ValueInteger.get(dataType.sqlType), ValueInteger.get(MathUtils.convertLongToInt(dataType.maxPrecision)), dataType.prefix, dataType.suffix, dataType.params, ValueBoolean.FALSE, ValueSmallint.get(MathUtils.convertIntToShort(dataType.minScale)), ValueSmallint.get(MathUtils.convertIntToShort(dataType.maxScale)), DataType.isNumericType(b1) ? ValueInteger.get(10) : null, ValueInteger.get(dataType.type), ValueBoolean.get(dataType.caseSensitive), ValueSmallint.get((short)1), ValueSmallint.get((short)3) }); }  return arrayList;case 5: add(paramSessionLocal, arrayList, new Object[] { str1 }); return arrayList;case 6: for (Setting setting : this.database.getAllSettings()) { String str = setting.getStringValue(); if (str == null) str = Integer.toString(setting.getIntValue());  add(paramSessionLocal, arrayList, new Object[] { identifier(setting.getName()), str }); }  add(paramSessionLocal, arrayList, new Object[] { "info.BUILD_ID", "210" }); add(paramSessionLocal, arrayList, new Object[] { "info.VERSION_MAJOR", "2" }); add(paramSessionLocal, arrayList, new Object[] { "info.VERSION_MINOR", "1" }); add(paramSessionLocal, arrayList, new Object[] { "info.VERSION", Constants.FULL_VERSION }); if (bool) { String[] arrayOfString1 = { "java.runtime.version", "java.vm.name", "java.vendor", "os.name", "os.arch", "os.version", "sun.os.patch.level", "file.separator", "path.separator", "line.separator", "user.country", "user.language", "user.variant", "file.encoding" }; for (String str : arrayOfString1) { add(paramSessionLocal, arrayList, new Object[] { "property." + str, Utils.getProperty(str, "") }); }  }  add(paramSessionLocal, arrayList, new Object[] { "DEFAULT_NULL_ORDERING", this.database.getDefaultNullOrdering().name() }); add(paramSessionLocal, arrayList, new Object[] { "EXCLUSIVE", (this.database.getExclusiveSession() == null) ? "FALSE" : "TRUE" }); add(paramSessionLocal, arrayList, new Object[] { "MODE", this.database.getMode().getName() }); add(paramSessionLocal, arrayList, new Object[] { "QUERY_TIMEOUT", Integer.toString(paramSessionLocal.getQueryTimeout()) }); add(paramSessionLocal, arrayList, new Object[] { "TIME ZONE", paramSessionLocal.currentTimeZone().getId() }); add(paramSessionLocal, arrayList, new Object[] { "TRUNCATE_LARGE_LENGTH", paramSessionLocal.isTruncateLargeLength() ? "TRUE" : "FALSE" }); add(paramSessionLocal, arrayList, new Object[] { "VARIABLE_BINARY", paramSessionLocal.isVariableBinary() ? "TRUE" : "FALSE" }); add(paramSessionLocal, arrayList, new Object[] { "OLD_INFORMATION_SCHEMA", paramSessionLocal.isOldInformationSchema() ? "TRUE" : "FALSE" }); bitSet = paramSessionLocal.getNonKeywords(); if (bitSet != null) add(paramSessionLocal, arrayList, new Object[] { "NON_KEYWORDS", Parser.formatNonKeywords(bitSet) });  add(paramSessionLocal, arrayList, new Object[] { "RETENTION_TIME", Integer.toString(this.database.getRetentionTime()) }); for (Map.Entry entry : this.database.getSettings().getSortedSettings()) { add(paramSessionLocal, arrayList, new Object[] { entry.getKey(), entry.getValue() }); }  store = this.database.getStore(); mVStore = store.getMvStore(); fileStore = mVStore.getFileStore(); if (fileStore != null) { add(paramSessionLocal, arrayList, new Object[] { "info.FILE_WRITE", Long.toString(fileStore.getWriteCount()) }); add(paramSessionLocal, arrayList, new Object[] { "info.FILE_WRITE_BYTES", Long.toString(fileStore.getWriteBytes()) }); add(paramSessionLocal, arrayList, new Object[] { "info.FILE_READ", Long.toString(fileStore.getReadCount()) }); add(paramSessionLocal, arrayList, new Object[] { "info.FILE_READ_BYTES", Long.toString(fileStore.getReadBytes()) }); add(paramSessionLocal, arrayList, new Object[] { "info.UPDATE_FAILURE_PERCENT", String.format(Locale.ENGLISH, "%.2f%%", new Object[] { Double.valueOf(100.0D * mVStore.getUpdateFailureRatio()) }) }); add(paramSessionLocal, arrayList, new Object[] { "info.FILL_RATE", Integer.toString(mVStore.getFillRate()) }); add(paramSessionLocal, arrayList, new Object[] { "info.CHUNKS_FILL_RATE", Integer.toString(mVStore.getChunksFillRate()) }); add(paramSessionLocal, arrayList, new Object[] { "info.CHUNKS_FILL_RATE_RW", Integer.toString(mVStore.getRewritableChunksFillRate()) }); try { add(paramSessionLocal, arrayList, new Object[] { "info.FILE_SIZE", Long.toString(fileStore.getFile().size()) }); } catch (IOException iOException) {} add(paramSessionLocal, arrayList, new Object[] { "info.CHUNK_COUNT", Long.toString(mVStore.getChunkCount()) }); add(paramSessionLocal, arrayList, new Object[] { "info.PAGE_COUNT", Long.toString(mVStore.getPageCount()) }); add(paramSessionLocal, arrayList, new Object[] { "info.PAGE_COUNT_LIVE", Long.toString(mVStore.getLivePageCount()) }); add(paramSessionLocal, arrayList, new Object[] { "info.PAGE_SIZE", Integer.toString(mVStore.getPageSplitSize()) }); add(paramSessionLocal, arrayList, new Object[] { "info.CACHE_MAX_SIZE", Integer.toString(mVStore.getCacheSize()) }); add(paramSessionLocal, arrayList, new Object[] { "info.CACHE_SIZE", Integer.toString(mVStore.getCacheSizeUsed()) }); add(paramSessionLocal, arrayList, new Object[] { "info.CACHE_HIT_RATIO", Integer.toString(mVStore.getCacheHitRatio()) }); add(paramSessionLocal, arrayList, new Object[] { "info.TOC_CACHE_HIT_RATIO", Integer.toString(mVStore.getTocCacheHitRatio()) }); add(paramSessionLocal, arrayList, new Object[] { "info.LEAF_RATIO", Integer.toString(mVStore.getLeafRatio()) }); }  return arrayList;case 7: str2 = "/org/h2/res/help.csv"; try { byte[] arrayOfByte = Utils.getResource(str2); InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(arrayOfByte)); Csv csv = new Csv(); csv.setLineCommentCharacter('#'); ResultSet resultSet = csv.read(inputStreamReader, null); int i = resultSet.getMetaData().getColumnCount() - 1; String[] arrayOfString1 = new String[5]; for (byte b = 0; resultSet.next(); b++) { for (byte b3 = 0; b3 < i; b3++) { String str = resultSet.getString(1 + b3); switch (b3) { case 2: str = Help.stripAnnotationsFromSyntax(str); break;case 3: str = Help.processHelpText(str); break; }  arrayOfString1[b3] = str.trim(); }  add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(b), arrayOfString1[0], arrayOfString1[1], arrayOfString1[2], arrayOfString1[3] }); }  } catch (Exception exception) { throw DbException.convert(exception); }  return arrayList;case 8: for (SchemaObject schemaObject : getAllSchemaObjects(3)) { Sequence sequence = (Sequence)schemaObject; TypeInfo typeInfo = sequence.getDataType(); String str = Value.getTypeName(typeInfo.getValueType()); ValueInteger valueInteger = ValueInteger.get(typeInfo.getScale()); add(paramSessionLocal, arrayList, new Object[] { str1, sequence.getSchema().getName(), sequence.getName(), str, ValueInteger.get(sequence.getEffectivePrecision()), ValueInteger.get(10), valueInteger, ValueBigint.get(sequence.getStartValue()), ValueBigint.get(sequence.getMinValue()), ValueBigint.get(sequence.getMaxValue()), ValueBigint.get(sequence.getIncrement()), sequence.getCycle().isCycle() ? "YES" : "NO", str, ValueInteger.get((int)typeInfo.getPrecision()), valueInteger, ValueBigint.get(sequence.getCurrentValue()), ValueBoolean.get(sequence.getBelongsToTable()), replaceNullWithEmpty(sequence.getComment()), ValueBigint.get(sequence.getCacheSize()), ValueInteger.get(sequence.getId()), ValueBigint.get(sequence.getMinValue()), ValueBigint.get(sequence.getMaxValue()), ValueBoolean.get(sequence.getCycle().isCycle()) }); }  return arrayList;case 9: for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) { if (rightOwner instanceof User) { User user = (User)rightOwner; if (bool || paramSessionLocal.getUser() == user) add(paramSessionLocal, arrayList, new Object[] { identifier(user.getName()), String.valueOf(user.isAdmin()), replaceNullWithEmpty(user.getComment()), ValueInteger.get(user.getId()) });  }  }  return arrayList;case 10: for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) { if (rightOwner instanceof Role) { Role role = (Role)rightOwner; if (bool || paramSessionLocal.getUser().isRoleGranted(role)) add(paramSessionLocal, arrayList, new Object[] { identifier(role.getName()), replaceNullWithEmpty(role.getComment()), ValueInteger.get(role.getId()) });  }  }  return arrayList;case 11: if (bool) for (Right right : this.database.getAllRights()) { Role role = right.getGrantedRole(); DbObject dbObject = right.getGrantee(); String str = (dbObject.getType() == 2) ? "USER" : "ROLE"; if (role == null) { DbObject dbObject1 = right.getGrantedObject(); Schema schema = null; Table table = null; if (dbObject1 != null) if (dbObject1 instanceof Schema) { schema = (Schema)dbObject1; } else if (dbObject1 instanceof Table) { table = (Table)dbObject1; schema = table.getSchema(); }   String str4 = (table != null) ? table.getName() : ""; String str5 = (schema != null) ? schema.getName() : ""; if (!checkIndex(paramSessionLocal, str4, value1, value2)) continue;  add(paramSessionLocal, arrayList, new Object[] { identifier(dbObject.getName()), str, "", right.getRights(), str5, str4, ValueInteger.get(right.getId()) }); continue; }  add(paramSessionLocal, arrayList, new Object[] { identifier(dbObject.getName()), str, identifier(role.getName()), "", "", "", ValueInteger.get(right.getId()) }); }   return arrayList;case 12: for (Schema schema : this.database.getAllSchemas()) { for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) { if (userDefinedFunction instanceof FunctionAlias) { FunctionAlias.JavaMethod[] arrayOfJavaMethod; FunctionAlias functionAlias = (FunctionAlias)userDefinedFunction; try { arrayOfJavaMethod = functionAlias.getJavaMethods(); } catch (DbException dbException) { continue; }  for (FunctionAlias.JavaMethod javaMethod : arrayOfJavaMethod) { TypeInfo typeInfo = javaMethod.getDataType(); if (typeInfo == null) typeInfo = TypeInfo.TYPE_NULL;  add(paramSessionLocal, arrayList, new Object[] { str1, functionAlias.getSchema().getName(), functionAlias.getName(), functionAlias.getJavaClassName(), functionAlias.getJavaMethodName(), ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), typeInfo.getDeclaredTypeName(), ValueInteger.get(javaMethod.getParameterCount()), ValueSmallint.get((typeInfo.getValueType() == 0) ? 1 : 2), replaceNullWithEmpty(functionAlias.getComment()), ValueInteger.get(functionAlias.getId()), functionAlias.getSource() }); }  continue; }  add(paramSessionLocal, arrayList, new Object[] { str1, this.database.getMainSchema().getName(), userDefinedFunction.getName(), userDefinedFunction.getJavaClassName(), "", ValueInteger.get(0), "NULL", ValueInteger.get(1), ValueSmallint.get((short)2), replaceNullWithEmpty(userDefinedFunction.getComment()), ValueInteger.get(userDefinedFunction.getId()), "" }); }  }  return arrayList;case 20: for (Schema schema : this.database.getAllSchemas()) { for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) { if (userDefinedFunction instanceof FunctionAlias) { FunctionAlias.JavaMethod[] arrayOfJavaMethod; FunctionAlias functionAlias = (FunctionAlias)userDefinedFunction; try { arrayOfJavaMethod = functionAlias.getJavaMethods(); } catch (DbException dbException) { continue; }  for (FunctionAlias.JavaMethod javaMethod : arrayOfJavaMethod) { TypeInfo typeInfo = javaMethod.getDataType(); if (typeInfo != null && typeInfo.getValueType() != 0) { DataType dataType = DataType.getDataType(typeInfo.getValueType()); add(paramSessionLocal, arrayList, new Object[] { str1, functionAlias.getSchema().getName(), functionAlias.getName(), functionAlias.getJavaClassName(), functionAlias.getJavaMethodName(), ValueInteger.get(javaMethod.getParameterCount()), ValueInteger.get(0), "P0", ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), typeInfo.getDeclaredTypeName(), ValueInteger.get(MathUtils.convertLongToInt(dataType.defaultPrecision)), ValueSmallint.get(MathUtils.convertIntToShort(dataType.defaultScale)), ValueSmallint.get((short)10), ValueSmallint.get((short)2), ValueSmallint.get((short)5), "", null }); }  Class[] arrayOfClass = javaMethod.getColumnClasses(); for (byte b = 0; b < arrayOfClass.length; b++) { if (!javaMethod.hasConnectionParam() || b != 0) { Class clazz = arrayOfClass[b]; TypeInfo typeInfo1 = ValueToObjectConverter2.classToType(clazz); DataType dataType = DataType.getDataType(typeInfo1.getValueType()); add(paramSessionLocal, arrayList, new Object[] { str1, functionAlias.getSchema().getName(), functionAlias.getName(), functionAlias.getJavaClassName(), functionAlias.getJavaMethodName(), ValueInteger.get(javaMethod.getParameterCount()), ValueInteger.get(b + (javaMethod.hasConnectionParam() ? 0 : 1)), "P" + (b + 1), ValueInteger.get(DataType.convertTypeToSQLType(typeInfo1)), typeInfo1.getDeclaredTypeName(), ValueInteger.get(MathUtils.convertLongToInt(dataType.defaultPrecision)), ValueSmallint.get(MathUtils.convertIntToShort(dataType.defaultScale)), ValueSmallint.get((short)10), ValueSmallint.get(clazz.isPrimitive() ? 0 : 1), ValueSmallint.get((short)1), "", null }); }  }  }  }  }  }  return arrayList;case 13: str2 = this.database.getCompareMode().getName(); for (Schema schema : this.database.getAllSchemas()) { add(paramSessionLocal, arrayList, new Object[] { str1, schema.getName(), identifier(schema.getOwner().getName()), "Unicode", str2, ValueBoolean.get((schema.getId() == 0)), replaceNullWithEmpty(schema.getComment()), ValueInteger.get(schema.getId()) }); }  return arrayList;case 14: for (Right right : this.database.getAllRights()) { DbObject dbObject = right.getGrantedObject(); if (!(dbObject instanceof Table)) continue;  Table table = (Table)dbObject; if (hideTable(table, paramSessionLocal)) continue;  String str = table.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  addPrivileges(paramSessionLocal, arrayList, right.getGrantee(), str1, table, (String)null, right.getRightMask()); }  return arrayList;case 15: for (Right right : this.database.getAllRights()) { DbObject dbObject1 = right.getGrantedObject(); if (!(dbObject1 instanceof Table)) continue;  Table table = (Table)dbObject1; if (hideTable(table, paramSessionLocal)) continue;  String str = table.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  DbObject dbObject2 = right.getGrantee(); int i = right.getRightMask(); for (Column column : table.getColumns()) addPrivileges(paramSessionLocal, arrayList, dbObject2, str1, table, column.getName(), i);  }  return arrayList;case 16: for (Locale locale : CompareMode.getCollationLocales(false)) { add(paramSessionLocal, arrayList, new Object[] { CompareMode.getName(locale), locale.toString() }); }  return arrayList;case 17: for (Table table : getAllTables(paramSessionLocal)) { if (table.getTableType() != TableType.VIEW) continue;  String str = table.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  TableView tableView = (TableView)table; add(paramSessionLocal, arrayList, new Object[] { str1, table.getSchema().getName(), str, table.getCreateSQL(), "NONE", "NO", tableView.isInvalid() ? "INVALID" : "VALID", replaceNullWithEmpty(tableView.getComment()), ValueInteger.get(tableView.getId()) }); }  return arrayList;case 18: arrayList1 = this.database.getInDoubtTransactions(); if (arrayList1 != null && bool) for (InDoubtTransaction inDoubtTransaction : arrayList1) { add(paramSessionLocal, arrayList, new Object[] { inDoubtTransaction.getTransactionName(), inDoubtTransaction.getStateDescription() }); }   return arrayList;case 19: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { Constraint constraint = (Constraint)schemaObject; if (constraint.getConstraintType() != Constraint.Type.REFERENTIAL) continue;  ConstraintReferential constraintReferential = (ConstraintReferential)constraint; IndexColumn[] arrayOfIndexColumn1 = constraintReferential.getColumns(); IndexColumn[] arrayOfIndexColumn2 = constraintReferential.getRefColumns(); Table table1 = constraintReferential.getTable(); Table table2 = constraintReferential.getRefTable(); String str = table2.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  ValueSmallint valueSmallint1 = ValueSmallint.get(getRefAction(constraintReferential.getUpdateAction())); ValueSmallint valueSmallint2 = ValueSmallint.get(getRefAction(constraintReferential.getDeleteAction())); for (byte b = 0; b < arrayOfIndexColumn1.length; b++) { add(paramSessionLocal, arrayList, new Object[] { str1, table2.getSchema().getName(), table2.getName(), (arrayOfIndexColumn2[b]).column.getName(), str1, table1.getSchema().getName(), table1.getName(), (arrayOfIndexColumn1[b]).column.getName(), ValueSmallint.get((short)(b + 1)), valueSmallint1, valueSmallint2, constraintReferential.getName(), constraintReferential.getReferencedConstraint().getName(), ValueSmallint.get((short)7) }); }  }  return arrayList;case 21: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { Constraint constraint = (Constraint)schemaObject; Constraint.Type type = constraint.getConstraintType(); String str4 = null; IndexColumn[] arrayOfIndexColumn = null; Table table = constraint.getTable(); if (hideTable(table, paramSessionLocal)) continue;  Index index = constraint.getIndex(); String str5 = null; if (index != null) str5 = index.getName();  String str6 = table.getName(); if (!checkIndex(paramSessionLocal, str6, value1, value2)) continue;  if (type == Constraint.Type.CHECK) { str4 = ((ConstraintCheck)constraint).getExpression().getSQL(0); } else if (type == Constraint.Type.UNIQUE || type == Constraint.Type.PRIMARY_KEY) { arrayOfIndexColumn = ((ConstraintUnique)constraint).getColumns(); } else if (type == Constraint.Type.REFERENTIAL) { arrayOfIndexColumn = ((ConstraintReferential)constraint).getColumns(); }  String str7 = null; if (arrayOfIndexColumn != null) { StringBuilder stringBuilder = new StringBuilder(); byte b; int i; for (b = 0, i = arrayOfIndexColumn.length; b < i; b++) { if (b > 0) stringBuilder.append(',');  stringBuilder.append((arrayOfIndexColumn[b]).column.getName()); }  str7 = stringBuilder.toString(); }  add(paramSessionLocal, arrayList, new Object[] { str1, constraint.getSchema().getName(), constraint.getName(), (type == Constraint.Type.PRIMARY_KEY) ? type.getSqlName() : type.name(), str1, table.getSchema().getName(), str6, str5, str4, str7, replaceNullWithEmpty(constraint.getComment()), constraint.getCreateSQL(), ValueInteger.get(constraint.getId()) }); }  return arrayList;case 22: for (SchemaObject schemaObject : getAllSchemaObjects(11)) { Constant constant = (Constant)schemaObject; ValueExpression valueExpression = constant.getValue(); add(paramSessionLocal, arrayList, new Object[] { str1, constant.getSchema().getName(), constant.getName(), ValueInteger.get(DataType.convertTypeToSQLType(valueExpression.getType())), replaceNullWithEmpty(constant.getComment()), valueExpression.getSQL(0), ValueInteger.get(constant.getId()) }); }  return arrayList;case 23: for (SchemaObject schemaObject : getAllSchemaObjects(12)) { Domain domain1 = (Domain)schemaObject; Domain domain2 = domain1.getDomain(); TypeInfo typeInfo = domain1.getDataType(); add(paramSessionLocal, arrayList, new Object[] { str1, domain1.getSchema().getName(), domain1.getName(), domain1.getDefaultSQL(), domain1.getOnUpdateSQL(), ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision())), ValueInteger.get(typeInfo.getScale()), typeInfo.getDeclaredTypeName(), (domain2 != null) ? str1 : null, (domain2 != null) ? domain2.getSchema().getName() : null, (domain2 != null) ? domain2.getName() : null, ValueInteger.get(50), replaceNullWithEmpty(domain1.getComment()), domain1.getCreateSQL(), ValueInteger.get(domain1.getId()), domain1.getDefaultSQL(), "YES", null }); }  return arrayList;case 24: for (SchemaObject schemaObject : getAllSchemaObjects(4)) { TriggerObject triggerObject = (TriggerObject)schemaObject; Table table = triggerObject.getTable(); add(paramSessionLocal, arrayList, new Object[] { str1, triggerObject.getSchema().getName(), triggerObject.getName(), triggerObject.getTypeNameList(new StringBuilder()).toString(), str1, table.getSchema().getName(), table.getName(), ValueBoolean.get(triggerObject.isBefore()), triggerObject.getTriggerClassName(), ValueInteger.get(triggerObject.getQueueSize()), ValueBoolean.get(triggerObject.isNoWait()), replaceNullWithEmpty(triggerObject.getComment()), triggerObject.getCreateSQL(), ValueInteger.get(triggerObject.getId()) }); }  return arrayList;case 25: for (SessionLocal sessionLocal : this.database.getSessions(false)) { if (bool || sessionLocal == paramSessionLocal) { NetworkConnectionInfo networkConnectionInfo = sessionLocal.getNetworkConnectionInfo(); Command command = sessionLocal.getCurrentCommand(); int i = sessionLocal.getBlockingSessionId(); add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(sessionLocal.getId()), sessionLocal.getUser().getName(), (networkConnectionInfo == null) ? null : networkConnectionInfo.getServer(), (networkConnectionInfo == null) ? null : networkConnectionInfo.getClient(), (networkConnectionInfo == null) ? null : networkConnectionInfo.getClientInfo(), sessionLocal.getSessionStart(), paramSessionLocal.getIsolationLevel().getSQL(), (command == null) ? null : command.toString(), (command == null) ? null : sessionLocal.getCommandStartOrEnd(), ValueBoolean.get(sessionLocal.hasPendingTransaction()), String.valueOf(sessionLocal.getState()), (i == 0) ? null : ValueInteger.get(i), (sessionLocal.getState() == SessionLocal.State.SLEEP) ? sessionLocal.getCommandStartOrEnd() : null }); }  }  return arrayList;case 26: for (SessionLocal sessionLocal : this.database.getSessions(false)) { if (bool || sessionLocal == paramSessionLocal) for (Table table : sessionLocal.getLocks()) { add(paramSessionLocal, arrayList, new Object[] { table.getSchema().getName(), table.getName(), ValueInteger.get(sessionLocal.getId()), table.isLockedExclusivelyBy(sessionLocal) ? "WRITE" : "READ" }); }   }  return arrayList;case 27: for (String str : paramSessionLocal.getVariableNames()) { Value value = paramSessionLocal.getVariable(str); StringBuilder stringBuilder = (new StringBuilder()).append("SET @").append(str).append(' '); value.getSQL(stringBuilder, 0); add(paramSessionLocal, arrayList, new Object[] { "@" + str, stringBuilder.toString() }); }  for (Table table : paramSessionLocal.getLocalTempTables()) { add(paramSessionLocal, arrayList, new Object[] { "TABLE " + table.getName(), table.getCreateSQL() }); }  arrayOfString = paramSessionLocal.getSchemaSearchPath(); if (arrayOfString != null && arrayOfString.length > 0) { StringBuilder stringBuilder = new StringBuilder("SET SCHEMA_SEARCH_PATH "); byte b; int i; for (b = 0, i = arrayOfString.length; b < i; b++) { if (b > 0) stringBuilder.append(", ");  StringUtils.quoteIdentifier(stringBuilder, arrayOfString[b]); }  add(paramSessionLocal, arrayList, new Object[] { "SCHEMA_SEARCH_PATH", stringBuilder.toString() }); }  str3 = paramSessionLocal.getCurrentSchemaName(); if (str3 != null) add(paramSessionLocal, arrayList, new Object[] { "SCHEMA", StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), str3).toString() });  timeZoneProvider = paramSessionLocal.currentTimeZone(); if (!timeZoneProvider.equals(DateTimeUtils.getTimeZone())) add(paramSessionLocal, arrayList, new Object[] { "TIME ZONE", StringUtils.quoteStringSQL(new StringBuilder("SET TIME ZONE "), timeZoneProvider.getId()).toString() });  return arrayList;case 28: queryStatisticsData = this.database.getQueryStatisticsData(); if (queryStatisticsData != null) for (QueryStatisticsData.QueryEntry queryEntry : queryStatisticsData.getQueries()) { add(paramSessionLocal, arrayList, new Object[] { queryEntry.sqlStatement, ValueInteger.get(queryEntry.count), ValueDouble.get(queryEntry.executionTimeMinNanos / 1000000.0D), ValueDouble.get(queryEntry.executionTimeMaxNanos / 1000000.0D), ValueDouble.get(queryEntry.executionTimeCumulativeNanos / 1000000.0D), ValueDouble.get(queryEntry.executionTimeMeanNanos / 1000000.0D), ValueDouble.get(queryEntry.getExecutionTimeStandardDeviation() / 1000000.0D), ValueBigint.get(queryEntry.rowCountMin), ValueBigint.get(queryEntry.rowCountMax), ValueBigint.get(queryEntry.rowCountCumulative), ValueDouble.get(queryEntry.rowCountMean), ValueDouble.get(queryEntry.getRowCountStandardDeviation()) }); }   return arrayList;case 29: for (TableSynonym tableSynonym : this.database.getAllSynonyms()) { add(paramSessionLocal, arrayList, new Object[] { str1, tableSynonym.getSchema().getName(), tableSynonym.getName(), tableSynonym.getSynonymForName(), tableSynonym.getSynonymForSchema().getName(), "SYNONYM", "VALID", replaceNullWithEmpty(tableSynonym.getComment()), ValueInteger.get(tableSynonym.getId()) }); }  return arrayList;case 30: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { Constraint constraint = (Constraint)schemaObject; Constraint.Type type = constraint.getConstraintType(); if (type == Constraint.Type.DOMAIN) continue;  Table table = constraint.getTable(); if (hideTable(table, paramSessionLocal)) continue;  String str = table.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  add(paramSessionLocal, arrayList, new Object[] { str1, constraint.getSchema().getName(), constraint.getName(), type.getSqlName(), str1, table.getSchema().getName(), str, "NO", "NO", replaceNullWithEmpty(constraint.getComment()), constraint.getCreateSQL(), ValueInteger.get(constraint.getId()) }); }  return arrayList;case 31: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { if (((Constraint)schemaObject).getConstraintType() != Constraint.Type.DOMAIN) continue;  ConstraintDomain constraintDomain = (ConstraintDomain)schemaObject; Domain domain = constraintDomain.getDomain(); add(paramSessionLocal, arrayList, new Object[] { str1, constraintDomain.getSchema().getName(), constraintDomain.getName(), str1, domain.getSchema().getName(), domain.getName(), "NO", "NO", replaceNullWithEmpty(constraintDomain.getComment()), constraintDomain.getCreateSQL(), ValueInteger.get(constraintDomain.getId()) }); }  return arrayList;case 32: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { ConstraintUnique constraintUnique; Constraint constraint = (Constraint)schemaObject; Constraint.Type type = constraint.getConstraintType(); IndexColumn[] arrayOfIndexColumn = null; if (type == Constraint.Type.UNIQUE || type == Constraint.Type.PRIMARY_KEY) { arrayOfIndexColumn = ((ConstraintUnique)constraint).getColumns(); } else if (type == Constraint.Type.REFERENTIAL) { arrayOfIndexColumn = ((ConstraintReferential)constraint).getColumns(); }  if (arrayOfIndexColumn == null) continue;  Table table = constraint.getTable(); if (hideTable(table, paramSessionLocal)) continue;  String str = table.getName(); if (!checkIndex(paramSessionLocal, str, value1, value2)) continue;  if (type == Constraint.Type.REFERENTIAL) { constraintUnique = ((ConstraintReferential)constraint).getReferencedConstraint(); } else { constraintUnique = null; }  Index index = constraint.getIndex(); for (byte b = 0; b < arrayOfIndexColumn.length; b++) { IndexColumn indexColumn = arrayOfIndexColumn[b]; ValueInteger valueInteger1 = ValueInteger.get(b + 1); ValueInteger valueInteger2 = null; if (constraintUnique != null) { Column column = (((ConstraintReferential)constraint).getRefColumns()[b]).column; IndexColumn[] arrayOfIndexColumn1 = constraintUnique.getColumns(); for (byte b3 = 0; b3 < arrayOfIndexColumn1.length; b3++) { if ((arrayOfIndexColumn1[b3]).column.equals(column)) { valueInteger2 = ValueInteger.get(b3 + 1); break; }  }  }  add(paramSessionLocal, arrayList, new Object[] { str1, constraint.getSchema().getName(), constraint.getName(), str1, table.getSchema().getName(), str, indexColumn.columnName, valueInteger1, valueInteger2, (index != null) ? str1 : null, (index != null) ? index.getSchema().getName() : null, (index != null) ? index.getName() : null }); }  }  return arrayList;case 33: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { if (((Constraint)schemaObject).getConstraintType() != Constraint.Type.REFERENTIAL) continue;  ConstraintReferential constraintReferential = (ConstraintReferential)schemaObject; Table table = constraintReferential.getTable(); if (hideTable(table, paramSessionLocal)) continue;  ConstraintUnique constraintUnique = constraintReferential.getReferencedConstraint(); add(paramSessionLocal, arrayList, new Object[] { str1, constraintReferential.getSchema().getName(), constraintReferential.getName(), str1, constraintUnique.getSchema().getName(), constraintUnique.getName(), "NONE", constraintReferential.getUpdateAction().getSqlName(), constraintReferential.getDeleteAction().getSqlName() }); }  return arrayList;case 34: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { Constraint constraint = (Constraint)schemaObject; Constraint.Type type = constraint.getConstraintType(); if (type == Constraint.Type.CHECK) { ConstraintCheck constraintCheck = (ConstraintCheck)schemaObject; Table table = constraintCheck.getTable(); if (hideTable(table, paramSessionLocal)) continue;  } else if (type != Constraint.Type.DOMAIN) { continue; }  add(paramSessionLocal, arrayList, new Object[] { str1, schemaObject.getSchema().getName(), schemaObject.getName(), constraint.getExpression().getSQL(0, 2) }); }  return arrayList;case 35: for (SchemaObject schemaObject : getAllSchemaObjects(5)) { HashSet hashSet; Table table; Constraint constraint = (Constraint)schemaObject; switch (constraint.getConstraintType()) { case CASCADE: case RESTRICT: hashSet = new HashSet(); constraint.getExpression().isEverything(ExpressionVisitor.getColumnsVisitor(hashSet, null)); for (Column column : hashSet) { Table table1 = column.getTable(); if (checkIndex(paramSessionLocal, table1.getName(), value1, value2) && !hideTable(table1, paramSessionLocal)) addConstraintColumnUsage(paramSessionLocal, arrayList, str1, constraint, column);  } case SET_DEFAULT: table = constraint.getRefTable(); if (checkIndex(paramSessionLocal, table.getName(), value1, value2) && !hideTable(table, paramSessionLocal)) for (Column column : constraint.getReferencedColumns(table)) addConstraintColumnUsage(paramSessionLocal, arrayList, str1, constraint, column);  case SET_NULL: case null: table = constraint.getTable(); if (checkIndex(paramSessionLocal, table.getName(), value1, value2) && !hideTable(table, paramSessionLocal)) for (Column column : constraint.getReferencedColumns(table)) addConstraintColumnUsage(paramSessionLocal, arrayList, str1, constraint, column);   }  }  return arrayList;
/*      */     } 
/*      */     throw DbException.getInternalError("type=" + this.type); }
/*      */    private static short getRefAction(ConstraintActionType paramConstraintActionType) {
/* 2362 */     switch (paramConstraintActionType) {
/*      */       case CASCADE:
/* 2364 */         return 0;
/*      */       case RESTRICT:
/* 2366 */         return 1;
/*      */       case SET_DEFAULT:
/* 2368 */         return 4;
/*      */       case SET_NULL:
/* 2370 */         return 2;
/*      */     } 
/* 2372 */     throw DbException.getInternalError("action=" + paramConstraintActionType);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addConstraintColumnUsage(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString, Constraint paramConstraint, Column paramColumn) {
/* 2378 */     Table table = paramColumn.getTable();
/* 2379 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString, table
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2384 */           .getSchema().getName(), table
/*      */           
/* 2386 */           .getName(), paramColumn
/*      */           
/* 2388 */           .getName(), paramString, paramConstraint
/*      */ 
/*      */ 
/*      */           
/* 2392 */           .getSchema().getName(), paramConstraint
/*      */           
/* 2394 */           .getName() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addPrivileges(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, DbObject paramDbObject, String paramString1, Table paramTable, String paramString2, int paramInt) {
/* 2400 */     if ((paramInt & 0x1) != 0) {
/* 2401 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "SELECT");
/*      */     }
/* 2403 */     if ((paramInt & 0x4) != 0) {
/* 2404 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "INSERT");
/*      */     }
/* 2406 */     if ((paramInt & 0x8) != 0) {
/* 2407 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "UPDATE");
/*      */     }
/* 2409 */     if ((paramInt & 0x2) != 0) {
/* 2410 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "DELETE");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void addPrivilege(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, DbObject paramDbObject, String paramString1, Table paramTable, String paramString2, String paramString3) {
/* 2416 */     String str = "NO";
/* 2417 */     if (paramDbObject.getType() == 2) {
/* 2418 */       User user = (User)paramDbObject;
/* 2419 */       if (user.isAdmin())
/*      */       {
/* 2421 */         str = "YES";
/*      */       }
/*      */     } 
/* 2424 */     if (paramString2 == null) {
/* 2425 */       add(paramSessionLocal, paramArrayList, new Object[] { null, 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2430 */             identifier(paramDbObject.getName()), paramString1, paramTable
/*      */ 
/*      */ 
/*      */             
/* 2434 */             .getSchema().getName(), paramTable
/*      */             
/* 2436 */             .getName(), paramString3, str });
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 2443 */       add(paramSessionLocal, paramArrayList, new Object[] { null, 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2448 */             identifier(paramDbObject.getName()), paramString1, paramTable
/*      */ 
/*      */ 
/*      */             
/* 2452 */             .getSchema().getName(), paramTable
/*      */             
/* 2454 */             .getName(), paramString2, paramString3, str });
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
/*      */   private ArrayList<SchemaObject> getAllSchemaObjects(int paramInt) {
/* 2466 */     ArrayList<SchemaObject> arrayList = new ArrayList();
/* 2467 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2468 */       schema.getAll(paramInt, arrayList);
/*      */     }
/* 2470 */     return arrayList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ArrayList<Table> getAllTables(SessionLocal paramSessionLocal) {
/* 2481 */     ArrayList<Table> arrayList = new ArrayList();
/* 2482 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2483 */       arrayList.addAll(schema.getAllTablesAndViews(paramSessionLocal));
/*      */     }
/* 2485 */     arrayList.addAll(paramSessionLocal.getLocalTempTables());
/* 2486 */     return arrayList;
/*      */   }
/*      */ 
/*      */   
/*      */   private ArrayList<Table> getTablesByName(SessionLocal paramSessionLocal, String paramString) {
/* 2491 */     ArrayList<Table> arrayList = new ArrayList(1);
/* 2492 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2493 */       Table table1 = schema.getTableOrViewByName(paramSessionLocal, paramString);
/* 2494 */       if (table1 != null) {
/* 2495 */         arrayList.add(table1);
/*      */       }
/*      */     } 
/* 2498 */     Table table = paramSessionLocal.findLocalTempTable(paramString);
/* 2499 */     if (table != null) {
/* 2500 */       arrayList.add(table);
/*      */     }
/* 2502 */     return arrayList;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getMaxDataModificationId() {
/* 2507 */     switch (this.type) {
/*      */       case 6:
/*      */       case 8:
/*      */       case 18:
/*      */       case 25:
/*      */       case 26:
/*      */       case 27:
/* 2514 */         return Long.MAX_VALUE;
/*      */     } 
/* 2516 */     return this.database.getModificationDataId();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\InformationSchemaTableLegacy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */