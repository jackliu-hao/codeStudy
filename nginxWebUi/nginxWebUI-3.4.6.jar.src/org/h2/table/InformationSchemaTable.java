/*      */ package org.h2.table;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.BitSet;
/*      */ import java.util.HashSet;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import org.h2.api.IntervalQualifier;
/*      */ import org.h2.command.Command;
/*      */ import org.h2.command.Parser;
/*      */ import org.h2.constraint.Constraint;
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
/*      */ import org.h2.schema.Sequence;
/*      */ import org.h2.schema.TriggerObject;
/*      */ import org.h2.schema.UserDefinedFunction;
/*      */ import org.h2.store.InDoubtTransaction;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.NetworkConnectionInfo;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.util.geometry.EWKTUtils;
/*      */ import org.h2.value.CompareMode;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.ExtTypeInfoEnum;
/*      */ import org.h2.value.ExtTypeInfoGeometry;
/*      */ import org.h2.value.ExtTypeInfoRow;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueBoolean;
/*      */ import org.h2.value.ValueDouble;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueToObjectConverter2;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class InformationSchemaTable
/*      */   extends MetaTable
/*      */ {
/*      */   private static final String CHARACTER_SET_NAME = "Unicode";
/*      */   private static final int INFORMATION_SCHEMA_CATALOG_NAME = 0;
/*      */   private static final int CHECK_CONSTRAINTS = 1;
/*      */   private static final int COLLATIONS = 2;
/*      */   private static final int COLUMNS = 3;
/*      */   private static final int COLUMN_PRIVILEGES = 4;
/*      */   private static final int CONSTRAINT_COLUMN_USAGE = 5;
/*      */   private static final int DOMAINS = 6;
/*      */   private static final int DOMAIN_CONSTRAINTS = 7;
/*      */   private static final int ELEMENT_TYPES = 8;
/*      */   private static final int FIELDS = 9;
/*      */   private static final int KEY_COLUMN_USAGE = 10;
/*      */   private static final int PARAMETERS = 11;
/*      */   private static final int REFERENTIAL_CONSTRAINTS = 12;
/*      */   private static final int ROUTINES = 13;
/*      */   private static final int SCHEMATA = 14;
/*      */   private static final int SEQUENCES = 15;
/*      */   private static final int TABLES = 16;
/*      */   private static final int TABLE_CONSTRAINTS = 17;
/*      */   private static final int TABLE_PRIVILEGES = 18;
/*      */   private static final int TRIGGERS = 19;
/*      */   private static final int VIEWS = 20;
/*      */   private static final int CONSTANTS = 21;
/*      */   private static final int ENUM_VALUES = 22;
/*      */   private static final int INDEXES = 23;
/*      */   private static final int INDEX_COLUMNS = 24;
/*      */   private static final int IN_DOUBT = 25;
/*      */   private static final int LOCKS = 26;
/*      */   private static final int QUERY_STATISTICS = 27;
/*      */   private static final int RIGHTS = 28;
/*      */   private static final int ROLES = 29;
/*      */   private static final int SESSIONS = 30;
/*      */   private static final int SESSION_STATE = 31;
/*      */   private static final int SETTINGS = 32;
/*      */   private static final int SYNONYMS = 33;
/*      */   private static final int USERS = 34;
/*      */   public static final int META_TABLE_TYPE_COUNT = 35;
/*      */   private final boolean isView;
/*      */   
/*      */   public InformationSchemaTable(Schema paramSchema, int paramInt1, int paramInt2) {
/*  177 */     super(paramSchema, paramInt1, paramInt2);
/*      */     Column[] arrayOfColumn;
/*  179 */     String str = null;
/*  180 */     boolean bool = true;
/*  181 */     switch (paramInt2) {
/*      */       
/*      */       case 0:
/*  184 */         setMetaTableName("INFORMATION_SCHEMA_CATALOG_NAME");
/*  185 */         bool = false;
/*      */         
/*  187 */         arrayOfColumn = new Column[] { column("CATALOG_NAME") };
/*      */         break;
/*      */ 
/*      */       
/*      */       case 1:
/*  192 */         setMetaTableName("CHECK_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  197 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CHECK_CLAUSE") };
/*      */         
/*  199 */         str = "CONSTRAINT_NAME";
/*      */         break;
/*      */       case 2:
/*  202 */         setMetaTableName("COLLATIONS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  209 */         arrayOfColumn = new Column[] { column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("PAD_ATTRIBUTE"), column("LANGUAGE_TAG") };
/*      */         break;
/*      */       
/*      */       case 3:
/*  213 */         setMetaTableName("COLUMNS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  263 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("COLUMN_DEFAULT"), column("IS_NULLABLE"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("IS_IDENTITY"), column("IDENTITY_GENERATION"), column("IDENTITY_START", TypeInfo.TYPE_BIGINT), column("IDENTITY_INCREMENT", TypeInfo.TYPE_BIGINT), column("IDENTITY_MAXIMUM", TypeInfo.TYPE_BIGINT), column("IDENTITY_MINIMUM", TypeInfo.TYPE_BIGINT), column("IDENTITY_CYCLE"), column("IS_GENERATED"), column("GENERATION_EXPRESSION"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("IDENTITY_BASE", TypeInfo.TYPE_BIGINT), column("IDENTITY_CACHE", TypeInfo.TYPE_BIGINT), column("COLUMN_ON_UPDATE"), column("IS_VISIBLE", TypeInfo.TYPE_BOOLEAN), column("DEFAULT_ON_NULL", TypeInfo.TYPE_BOOLEAN), column("SELECTIVITY", TypeInfo.TYPE_INTEGER), column("REMARKS") };
/*      */         
/*  265 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 4:
/*  268 */         setMetaTableName("COLUMN_PRIVILEGES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  277 */         arrayOfColumn = new Column[] { column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE") };
/*      */         
/*  279 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 5:
/*  282 */         setMetaTableName("CONSTRAINT_COLUMN_USAGE");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  290 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME") };
/*      */         
/*  292 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 6:
/*  295 */         setMetaTableName("DOMAINS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  328 */         arrayOfColumn = new Column[] { column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("DOMAIN_DEFAULT"), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("DOMAIN_ON_UPDATE"), column("PARENT_DOMAIN_CATALOG"), column("PARENT_DOMAIN_SCHEMA"), column("PARENT_DOMAIN_NAME"), column("REMARKS") };
/*      */         
/*  330 */         str = "DOMAIN_NAME";
/*      */         break;
/*      */       case 7:
/*  333 */         setMetaTableName("DOMAIN_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  344 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("DOMAIN_CATALOG"), column("DOMAIN_SCHEMA"), column("DOMAIN_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("REMARKS") };
/*      */         
/*  346 */         str = "DOMAIN_NAME";
/*      */         break;
/*      */       case 8:
/*  349 */         setMetaTableName("ELEMENT_TYPES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  378 */         arrayOfColumn = new Column[] { column("OBJECT_CATALOG"), column("OBJECT_SCHEMA"), column("OBJECT_NAME"), column("OBJECT_TYPE"), column("COLLECTION_TYPE_IDENTIFIER"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 9:
/*  382 */         setMetaTableName("FIELDS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  413 */         arrayOfColumn = new Column[] { column("OBJECT_CATALOG"), column("OBJECT_SCHEMA"), column("OBJECT_NAME"), column("OBJECT_TYPE"), column("ROW_IDENTIFIER"), column("FIELD_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 10:
/*  417 */         setMetaTableName("KEY_COLUMN_USAGE");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  427 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("POSITION_IN_UNIQUE_CONSTRAINT", TypeInfo.TYPE_INTEGER) };
/*      */         
/*  429 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 11:
/*  432 */         setMetaTableName("PARAMETERS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  465 */         arrayOfColumn = new Column[] { column("SPECIFIC_CATALOG"), column("SPECIFIC_SCHEMA"), column("SPECIFIC_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("PARAMETER_MODE"), column("IS_RESULT"), column("AS_LOCATOR"), column("PARAMETER_NAME"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("PARAMETER_DEFAULT"), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER) };
/*      */         break;
/*      */       
/*      */       case 12:
/*  469 */         setMetaTableName("REFERENTIAL_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  479 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("UNIQUE_CONSTRAINT_CATALOG"), column("UNIQUE_CONSTRAINT_SCHEMA"), column("UNIQUE_CONSTRAINT_NAME"), column("MATCH_OPTION"), column("UPDATE_RULE"), column("DELETE_RULE") };
/*      */         
/*  481 */         str = "CONSTRAINT_NAME";
/*      */         break;
/*      */       case 13:
/*  484 */         setMetaTableName("ROUTINES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  522 */         arrayOfColumn = new Column[] { column("SPECIFIC_CATALOG"), column("SPECIFIC_SCHEMA"), column("SPECIFIC_NAME"), column("ROUTINE_CATALOG"), column("ROUTINE_SCHEMA"), column("ROUTINE_NAME"), column("ROUTINE_TYPE"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("ROUTINE_BODY"), column("ROUTINE_DEFINITION"), column("EXTERNAL_NAME"), column("EXTERNAL_LANGUAGE"), column("PARAMETER_STYLE"), column("IS_DETERMINISTIC"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("REMARKS") };
/*      */         break;
/*      */       
/*      */       case 14:
/*  526 */         setMetaTableName("SCHEMATA");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  537 */         arrayOfColumn = new Column[] { column("CATALOG_NAME"), column("SCHEMA_NAME"), column("SCHEMA_OWNER"), column("DEFAULT_CHARACTER_SET_CATALOG"), column("DEFAULT_CHARACTER_SET_SCHEMA"), column("DEFAULT_CHARACTER_SET_NAME"), column("SQL_PATH"), column("DEFAULT_COLLATION_NAME"), column("REMARKS") };
/*      */         break;
/*      */       
/*      */       case 15:
/*  541 */         setMetaTableName("SEQUENCES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  561 */         arrayOfColumn = new Column[] { column("SEQUENCE_CATALOG"), column("SEQUENCE_SCHEMA"), column("SEQUENCE_NAME"), column("DATA_TYPE"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("START_VALUE", TypeInfo.TYPE_BIGINT), column("MINIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("MAXIMUM_VALUE", TypeInfo.TYPE_BIGINT), column("INCREMENT", TypeInfo.TYPE_BIGINT), column("CYCLE_OPTION"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("BASE_VALUE", TypeInfo.TYPE_BIGINT), column("CACHE", TypeInfo.TYPE_BIGINT), column("REMARKS") };
/*      */         
/*  563 */         str = "SEQUENCE_NAME";
/*      */         break;
/*      */       case 16:
/*  566 */         setMetaTableName("TABLES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  579 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("TABLE_TYPE"), column("IS_INSERTABLE_INTO"), column("COMMIT_ACTION"), column("STORAGE_TYPE"), column("REMARKS"), column("LAST_MODIFICATION", TypeInfo.TYPE_BIGINT), column("TABLE_CLASS"), column("ROW_COUNT_ESTIMATE", TypeInfo.TYPE_BIGINT) };
/*      */         
/*  581 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 17:
/*  584 */         setMetaTableName("TABLE_CONSTRAINTS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  600 */         arrayOfColumn = new Column[] { column("CONSTRAINT_CATALOG"), column("CONSTRAINT_SCHEMA"), column("CONSTRAINT_NAME"), column("CONSTRAINT_TYPE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("IS_DEFERRABLE"), column("INITIALLY_DEFERRED"), column("ENFORCED"), column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME"), column("REMARKS") };
/*      */         
/*  602 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 18:
/*  605 */         setMetaTableName("TABLE_PRIVILEGES");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  614 */         arrayOfColumn = new Column[] { column("GRANTOR"), column("GRANTEE"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("PRIVILEGE_TYPE"), column("IS_GRANTABLE"), column("WITH_HIERARCHY") };
/*      */         
/*  616 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 19:
/*  619 */         setMetaTableName("TRIGGERS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  635 */         arrayOfColumn = new Column[] { column("TRIGGER_CATALOG"), column("TRIGGER_SCHEMA"), column("TRIGGER_NAME"), column("EVENT_MANIPULATION"), column("EVENT_OBJECT_CATALOG"), column("EVENT_OBJECT_SCHEMA"), column("EVENT_OBJECT_TABLE"), column("ACTION_ORIENTATION"), column("ACTION_TIMING"), column("IS_ROLLBACK", TypeInfo.TYPE_BOOLEAN), column("JAVA_CLASS"), column("QUEUE_SIZE", TypeInfo.TYPE_INTEGER), column("NO_WAIT", TypeInfo.TYPE_BOOLEAN), column("REMARKS") };
/*      */         
/*  637 */         str = "EVENT_OBJECT_TABLE";
/*      */         break;
/*      */       case 20:
/*  640 */         setMetaTableName("VIEWS");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  654 */         arrayOfColumn = new Column[] { column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("VIEW_DEFINITION"), column("CHECK_OPTION"), column("IS_UPDATABLE"), column("INSERTABLE_INTO"), column("IS_TRIGGER_UPDATABLE"), column("IS_TRIGGER_DELETABLE"), column("IS_TRIGGER_INSERTABLE_INTO"), column("STATUS"), column("REMARKS") };
/*      */         
/*  656 */         str = "TABLE_NAME";
/*      */         break;
/*      */       
/*      */       case 21:
/*  660 */         setMetaTableName("CONSTANTS");
/*  661 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  689 */         arrayOfColumn = new Column[] { column("CONSTANT_CATALOG"), column("CONSTANT_SCHEMA"), column("CONSTANT_NAME"), column("VALUE_DEFINITION"), column("DATA_TYPE"), column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), column("CHARACTER_SET_CATALOG"), column("CHARACTER_SET_SCHEMA"), column("CHARACTER_SET_NAME"), column("COLLATION_CATALOG"), column("COLLATION_SCHEMA"), column("COLLATION_NAME"), column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), column("INTERVAL_TYPE"), column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), column("DTD_IDENTIFIER"), column("DECLARED_DATA_TYPE"), column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), column("GEOMETRY_TYPE"), column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), column("REMARKS") };
/*      */         
/*  691 */         str = "CONSTANT_NAME";
/*      */         break;
/*      */       case 22:
/*  694 */         setMetaTableName("ENUM_VALUES");
/*  695 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  703 */         arrayOfColumn = new Column[] { column("OBJECT_CATALOG"), column("OBJECT_SCHEMA"), column("OBJECT_NAME"), column("OBJECT_TYPE"), column("ENUM_IDENTIFIER"), column("VALUE_NAME"), column("VALUE_ORDINAL") };
/*      */         break;
/*      */       
/*      */       case 23:
/*  707 */         setMetaTableName("INDEXES");
/*  708 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  719 */         arrayOfColumn = new Column[] { column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("INDEX_TYPE_NAME"), column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), column("REMARKS"), column("INDEX_CLASS") };
/*      */         
/*  721 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 24:
/*  724 */         setMetaTableName("INDEX_COLUMNS");
/*  725 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  737 */         arrayOfColumn = new Column[] { column("INDEX_CATALOG"), column("INDEX_SCHEMA"), column("INDEX_NAME"), column("TABLE_CATALOG"), column("TABLE_SCHEMA"), column("TABLE_NAME"), column("COLUMN_NAME"), column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), column("ORDERING_SPECIFICATION"), column("NULL_ORDERING"), column("IS_UNIQUE", TypeInfo.TYPE_BOOLEAN) };
/*      */         
/*  739 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 25:
/*  742 */         setMetaTableName("IN_DOUBT");
/*  743 */         bool = false;
/*      */ 
/*      */         
/*  746 */         arrayOfColumn = new Column[] { column("TRANSACTION_NAME"), column("TRANSACTION_STATE") };
/*      */         break;
/*      */       
/*      */       case 26:
/*  750 */         setMetaTableName("LOCKS");
/*  751 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  756 */         arrayOfColumn = new Column[] { column("TABLE_SCHEMA"), column("TABLE_NAME"), column("SESSION_ID", TypeInfo.TYPE_INTEGER), column("LOCK_TYPE") };
/*      */         break;
/*      */       
/*      */       case 27:
/*  760 */         setMetaTableName("QUERY_STATISTICS");
/*  761 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  774 */         arrayOfColumn = new Column[] { column("SQL_STATEMENT"), column("EXECUTION_COUNT", TypeInfo.TYPE_INTEGER), column("MIN_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MAX_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("CUMULATIVE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("AVERAGE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("STD_DEV_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), column("MIN_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("MAX_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("CUMULATIVE_ROW_COUNT", TypeInfo.TYPE_BIGINT), column("AVERAGE_ROW_COUNT", TypeInfo.TYPE_DOUBLE), column("STD_DEV_ROW_COUNT", TypeInfo.TYPE_DOUBLE) };
/*      */         break;
/*      */       
/*      */       case 28:
/*  778 */         setMetaTableName("RIGHTS");
/*  779 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  786 */         arrayOfColumn = new Column[] { column("GRANTEE"), column("GRANTEETYPE"), column("GRANTEDROLE"), column("RIGHTS"), column("TABLE_SCHEMA"), column("TABLE_NAME") };
/*      */         
/*  788 */         str = "TABLE_NAME";
/*      */         break;
/*      */       case 29:
/*  791 */         setMetaTableName("ROLES");
/*  792 */         bool = false;
/*      */ 
/*      */         
/*  795 */         arrayOfColumn = new Column[] { column("ROLE_NAME"), column("REMARKS") };
/*      */         break;
/*      */       
/*      */       case 30:
/*  799 */         setMetaTableName("SESSIONS");
/*  800 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  814 */         arrayOfColumn = new Column[] { column("SESSION_ID", TypeInfo.TYPE_INTEGER), column("USER_NAME"), column("SERVER"), column("CLIENT_ADDR"), column("CLIENT_INFO"), column("SESSION_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("ISOLATION_LEVEL"), column("EXECUTING_STATEMENT"), column("EXECUTING_STATEMENT_START", TypeInfo.TYPE_TIMESTAMP_TZ), column("CONTAINS_UNCOMMITTED", TypeInfo.TYPE_BOOLEAN), column("SESSION_STATE"), column("BLOCKER_ID", TypeInfo.TYPE_INTEGER), column("SLEEP_SINCE", TypeInfo.TYPE_TIMESTAMP_TZ) };
/*      */         break;
/*      */       
/*      */       case 31:
/*  818 */         setMetaTableName("SESSION_STATE");
/*  819 */         bool = false;
/*      */ 
/*      */         
/*  822 */         arrayOfColumn = new Column[] { column("STATE_KEY"), column("STATE_COMMAND") };
/*      */         break;
/*      */       
/*      */       case 32:
/*  826 */         setMetaTableName("SETTINGS");
/*  827 */         bool = false;
/*      */ 
/*      */         
/*  830 */         arrayOfColumn = new Column[] { column("SETTING_NAME"), column("SETTING_VALUE") };
/*      */         break;
/*      */       
/*      */       case 33:
/*  834 */         setMetaTableName("SYNONYMS");
/*  835 */         bool = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  844 */         arrayOfColumn = new Column[] { column("SYNONYM_CATALOG"), column("SYNONYM_SCHEMA"), column("SYNONYM_NAME"), column("SYNONYM_FOR"), column("SYNONYM_FOR_SCHEMA"), column("TYPE_NAME"), column("STATUS"), column("REMARKS") };
/*      */         
/*  846 */         str = "SYNONYM_NAME";
/*      */         break;
/*      */       case 34:
/*  849 */         setMetaTableName("USERS");
/*  850 */         bool = false;
/*      */ 
/*      */ 
/*      */         
/*  854 */         arrayOfColumn = new Column[] { column("USER_NAME"), column("IS_ADMIN", TypeInfo.TYPE_BOOLEAN), column("REMARKS") };
/*      */         break;
/*      */       
/*      */       default:
/*  858 */         throw DbException.getInternalError("type=" + paramInt2);
/*      */     } 
/*  860 */     setColumns(arrayOfColumn);
/*      */     
/*  862 */     if (str == null) {
/*  863 */       this.indexColumn = -1;
/*  864 */       this.metaIndex = null;
/*      */     } else {
/*  866 */       this.indexColumn = getColumn(this.database.sysIdentifier(str)).getColumnId();
/*  867 */       IndexColumn[] arrayOfIndexColumn = IndexColumn.wrap(new Column[] { arrayOfColumn[this.indexColumn] });
/*  868 */       this.metaIndex = new MetaIndex(this, arrayOfIndexColumn, false);
/*      */     } 
/*  870 */     this.isView = bool;
/*      */   }
/*      */ 
/*      */   
/*      */   public ArrayList<Row> generateRows(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) {
/*  875 */     Value value1 = null, value2 = null;
/*  876 */     if (this.indexColumn >= 0) {
/*  877 */       if (paramSearchRow1 != null) {
/*  878 */         value1 = paramSearchRow1.getValue(this.indexColumn);
/*      */       }
/*  880 */       if (paramSearchRow2 != null) {
/*  881 */         value2 = paramSearchRow2.getValue(this.indexColumn);
/*      */       }
/*      */     } 
/*  884 */     ArrayList<Row> arrayList = Utils.newSmallArrayList();
/*  885 */     String str = this.database.getShortName();
/*  886 */     switch (this.type) {
/*      */       
/*      */       case 0:
/*  889 */         informationSchemaCatalogName(paramSessionLocal, arrayList, str);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  998 */         return arrayList;case 1: checkConstraints(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 2: collations(paramSessionLocal, arrayList, str); return arrayList;case 3: columns(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 4: columnPrivileges(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 5: constraintColumnUsage(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 6: domains(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 7: domainConstraints(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 8: elementTypesFields(paramSessionLocal, arrayList, str, 8); return arrayList;case 9: elementTypesFields(paramSessionLocal, arrayList, str, 9); return arrayList;case 10: keyColumnUsage(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 11: parameters(paramSessionLocal, arrayList, str); return arrayList;case 12: referentialConstraints(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 13: routines(paramSessionLocal, arrayList, str); return arrayList;case 14: schemata(paramSessionLocal, arrayList, str); return arrayList;case 15: sequences(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 16: tables(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 17: tableConstraints(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 18: tablePrivileges(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 19: triggers(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 20: views(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 21: constants(paramSessionLocal, value1, value2, arrayList, str); return arrayList;case 22: elementTypesFields(paramSessionLocal, arrayList, str, 22); return arrayList;case 23: indexes(paramSessionLocal, value1, value2, arrayList, str, false); return arrayList;case 24: indexes(paramSessionLocal, value1, value2, arrayList, str, true); return arrayList;case 25: inDoubt(paramSessionLocal, arrayList); return arrayList;case 26: locks(paramSessionLocal, arrayList); return arrayList;case 27: queryStatistics(paramSessionLocal, arrayList); return arrayList;case 28: rights(paramSessionLocal, value1, value2, arrayList); return arrayList;case 29: roles(paramSessionLocal, arrayList); return arrayList;case 30: sessions(paramSessionLocal, arrayList); return arrayList;case 31: sessionState(paramSessionLocal, arrayList); return arrayList;case 32: settings(paramSessionLocal, arrayList); return arrayList;case 33: synonyms(paramSessionLocal, arrayList, str); return arrayList;case 34: users(paramSessionLocal, arrayList); return arrayList;
/*      */     } 
/*      */     throw DbException.getInternalError("type=" + this.type);
/*      */   } private void informationSchemaCatalogName(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString) {
/* 1002 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkConstraints(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1009 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1010 */       for (Constraint constraint : schema.getAllConstraints()) {
/* 1011 */         Constraint.Type type = constraint.getConstraintType();
/* 1012 */         if (type == Constraint.Type.CHECK) {
/* 1013 */           ConstraintCheck constraintCheck = (ConstraintCheck)constraint;
/* 1014 */           Table table = constraintCheck.getTable();
/* 1015 */           if (hideTable(table, paramSessionLocal)) {
/*      */             continue;
/*      */           }
/* 1018 */         } else if (type != Constraint.Type.DOMAIN) {
/*      */           continue;
/*      */         } 
/* 1021 */         String str = constraint.getName();
/* 1022 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 1025 */         checkConstraints(paramSessionLocal, paramArrayList, paramString, constraint, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkConstraints(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Constraint paramConstraint, String paramString2) {
/* 1032 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramConstraint
/*      */ 
/*      */ 
/*      */           
/* 1036 */           .getSchema().getName(), paramString2, paramConstraint
/*      */ 
/*      */ 
/*      */           
/* 1040 */           .getExpression().getSQL(0, 2) });
/*      */   }
/*      */ 
/*      */   
/*      */   private void collations(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString) {
/* 1045 */     String str = this.database.getMainSchema().getName();
/* 1046 */     collations(paramSessionLocal, paramArrayList, paramString, str, "OFF", (String)null);
/* 1047 */     for (Locale locale : CompareMode.getCollationLocales(false)) {
/* 1048 */       collations(paramSessionLocal, paramArrayList, paramString, str, CompareMode.getName(locale), locale.toLanguageTag());
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void collations(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, String paramString4) {
/* 1054 */     if ("und".equals(paramString4)) {
/* 1055 */       paramString4 = null;
/*      */     }
/* 1057 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramString2, paramString3, "NO PAD", paramString4 });
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
/*      */   private void columns(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1073 */     String str1 = this.database.getMainSchema().getName();
/* 1074 */     String str2 = this.database.getCompareMode().getName();
/* 1075 */     if (paramValue1 != null && paramValue1.equals(paramValue2)) {
/* 1076 */       String str = paramValue1.getString();
/* 1077 */       if (str == null) {
/*      */         return;
/*      */       }
/* 1080 */       for (Schema schema : this.database.getAllSchemas()) {
/* 1081 */         Table table1 = schema.getTableOrViewByName(paramSessionLocal, str);
/* 1082 */         if (table1 != null) {
/* 1083 */           columns(paramSessionLocal, paramArrayList, paramString, str1, str2, table1, table1.getName());
/*      */         }
/*      */       } 
/* 1086 */       Table table = paramSessionLocal.findLocalTempTable(str);
/* 1087 */       if (table != null) {
/* 1088 */         columns(paramSessionLocal, paramArrayList, paramString, str1, str2, table, table.getName());
/*      */       }
/*      */     } else {
/* 1091 */       for (Schema schema : this.database.getAllSchemas()) {
/* 1092 */         for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) {
/* 1093 */           String str = table.getName();
/* 1094 */           if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/* 1095 */             columns(paramSessionLocal, paramArrayList, paramString, str1, str2, table, str);
/*      */           }
/*      */         } 
/*      */       } 
/* 1099 */       for (Table table : paramSessionLocal.getLocalTempTables()) {
/* 1100 */         String str = table.getName();
/* 1101 */         if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/* 1102 */           columns(paramSessionLocal, paramArrayList, paramString, str1, str2, table, str);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void columns(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, Table paramTable, String paramString4) {
/* 1110 */     if (hideTable(paramTable, paramSessionLocal)) {
/*      */       return;
/*      */     }
/* 1113 */     Column[] arrayOfColumn = paramTable.getColumns(); byte b; int i;
/* 1114 */     for (b = 0, i = arrayOfColumn.length; b < i;)
/* 1115 */       columns(paramSessionLocal, paramArrayList, paramString1, paramString2, paramString3, paramTable, paramString4, arrayOfColumn[b], ++b);  } private void columns(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, Table paramTable, String paramString4, Column paramColumn, int paramInt) {
/*      */     Object object1, object2, object3, object4;
/*      */     String str4, str5;
/*      */     Object object5;
/*      */     String str6;
/*      */     Object object6, object7, object8, object9, object10, object11, object12, object13;
/* 1121 */     TypeInfo typeInfo = paramColumn.getType();
/* 1122 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(typeInfo);
/*      */     
/* 1124 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 1125 */       object1 = paramString1;
/* 1126 */       object2 = paramString2;
/* 1127 */       object3 = "Unicode";
/* 1128 */       object4 = paramString3;
/*      */     } else {
/* 1130 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 1132 */     Domain domain = paramColumn.getDomain();
/* 1133 */     String str1 = null, str2 = null, str3 = null;
/* 1134 */     if (domain != null) {
/* 1135 */       str1 = paramString1;
/* 1136 */       str2 = domain.getSchema().getName();
/* 1137 */       str3 = domain.getName();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1142 */     Sequence sequence = paramColumn.getSequence();
/* 1143 */     if (sequence != null) {
/* 1144 */       str4 = null;
/* 1145 */       str5 = "NEVER";
/* 1146 */       object5 = null;
/* 1147 */       str6 = "YES";
/* 1148 */       object6 = paramColumn.isGeneratedAlways() ? "ALWAYS" : "BY DEFAULT";
/* 1149 */       object8 = ValueBigint.get(sequence.getStartValue());
/* 1150 */       object9 = ValueBigint.get(sequence.getIncrement());
/* 1151 */       object10 = ValueBigint.get(sequence.getMaxValue());
/* 1152 */       object11 = ValueBigint.get(sequence.getMinValue());
/* 1153 */       Sequence.Cycle cycle = sequence.getCycle();
/* 1154 */       object7 = cycle.isCycle() ? "YES" : "NO";
/* 1155 */       object12 = (cycle != Sequence.Cycle.EXHAUSTED) ? ValueBigint.get(sequence.getBaseValue()) : null;
/* 1156 */       object13 = ValueBigint.get(sequence.getCacheSize());
/*      */     } else {
/* 1158 */       if (paramColumn.isGenerated()) {
/* 1159 */         str4 = null;
/* 1160 */         str5 = "ALWAYS";
/* 1161 */         object5 = paramColumn.getDefaultSQL();
/*      */       } else {
/* 1163 */         str4 = paramColumn.getDefaultSQL();
/* 1164 */         str5 = "NEVER";
/* 1165 */         object5 = null;
/*      */       } 
/* 1167 */       str6 = "NO";
/* 1168 */       object6 = object7 = null;
/* 1169 */       object8 = object9 = object10 = object11 = object12 = object13 = null;
/*      */     } 
/*      */     
/* 1172 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramTable
/*      */ 
/*      */ 
/*      */           
/* 1176 */           .getSchema().getName(), paramString4, paramColumn
/*      */ 
/*      */ 
/*      */           
/* 1180 */           .getName(), 
/*      */           
/* 1182 */           ValueInteger.get(paramInt), str4, 
/*      */ 
/*      */ 
/*      */           
/* 1186 */           paramColumn.isNullable() ? "YES" : "NO", 
/*      */           
/* 1188 */           identifier(dataTypeInformation.dataType), dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, object1, object2, object3, object1, object2, object4, str1, str2, str3, dataTypeInformation.maximumCardinality, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1226 */           Integer.toString(paramInt), str6, object6, object8, object9, object10, object11, object7, str5, object5, dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid, object12, object13, paramColumn
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1261 */           .getOnUpdateSQL(), 
/*      */           
/* 1263 */           ValueBoolean.get(paramColumn.getVisible()), 
/*      */           
/* 1265 */           ValueBoolean.get(paramColumn.isDefaultOnNull()), 
/*      */           
/* 1267 */           ValueInteger.get(paramColumn.getSelectivity()), paramColumn
/*      */           
/* 1269 */           .getComment() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void columnPrivileges(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1275 */     for (Right right : this.database.getAllRights()) {
/* 1276 */       DbObject dbObject1 = right.getGrantedObject();
/* 1277 */       if (!(dbObject1 instanceof Table)) {
/*      */         continue;
/*      */       }
/* 1280 */       Table table = (Table)dbObject1;
/* 1281 */       if (hideTable(table, paramSessionLocal)) {
/*      */         continue;
/*      */       }
/* 1284 */       String str = table.getName();
/* 1285 */       if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */         continue;
/*      */       }
/* 1288 */       DbObject dbObject2 = right.getGrantee();
/* 1289 */       int i = right.getRightMask();
/* 1290 */       for (Column column : table.getColumns()) {
/* 1291 */         addPrivileges(paramSessionLocal, paramArrayList, dbObject2, paramString, table, column.getName(), i);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void constraintColumnUsage(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1298 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1299 */       for (Constraint constraint : schema.getAllConstraints())
/* 1300 */         constraintColumnUsage(paramSessionLocal, paramValue1, paramValue2, paramArrayList, paramString, constraint); 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void constraintColumnUsage(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString, Constraint paramConstraint) {
/*      */     HashSet hashSet;
/*      */     Table table;
/* 1307 */     switch (paramConstraint.getConstraintType()) {
/*      */       case TABLE_LINK:
/*      */       case EXTERNAL_TABLE_ENGINE:
/* 1310 */         hashSet = new HashSet();
/* 1311 */         paramConstraint.getExpression().isEverything(ExpressionVisitor.getColumnsVisitor(hashSet, null));
/* 1312 */         for (Column column : hashSet) {
/* 1313 */           Table table1 = column.getTable();
/* 1314 */           if (checkIndex(paramSessionLocal, table1.getName(), paramValue1, paramValue2) && !hideTable(table1, paramSessionLocal)) {
/* 1315 */             addConstraintColumnUsage(paramSessionLocal, paramArrayList, paramString, paramConstraint, column);
/*      */           }
/*      */         } 
/*      */         break;
/*      */       
/*      */       case null:
/* 1321 */         table = paramConstraint.getRefTable();
/* 1322 */         if (checkIndex(paramSessionLocal, table.getName(), paramValue1, paramValue2) && !hideTable(table, paramSessionLocal)) {
/* 1323 */           for (Column column : paramConstraint.getReferencedColumns(table)) {
/* 1324 */             addConstraintColumnUsage(paramSessionLocal, paramArrayList, paramString, paramConstraint, column);
/*      */           }
/*      */         }
/*      */ 
/*      */       
/*      */       case null:
/*      */       case null:
/* 1331 */         table = paramConstraint.getTable();
/* 1332 */         if (checkIndex(paramSessionLocal, table.getName(), paramValue1, paramValue2) && !hideTable(table, paramSessionLocal)) {
/* 1333 */           for (Column column : paramConstraint.getReferencedColumns(table)) {
/* 1334 */             addConstraintColumnUsage(paramSessionLocal, paramArrayList, paramString, paramConstraint, column);
/*      */           }
/*      */         }
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void domains(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1342 */     String str1 = this.database.getMainSchema().getName();
/* 1343 */     String str2 = this.database.getCompareMode().getName();
/* 1344 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1345 */       for (Domain domain : schema.getAllDomains()) {
/* 1346 */         String str = domain.getName();
/* 1347 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 1350 */         domains(paramSessionLocal, paramArrayList, paramString, str1, str2, domain, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void domains(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, Domain paramDomain, String paramString4) {
/*      */     Object object1, object2, object3, object4;
/* 1357 */     Domain domain = paramDomain.getDomain();
/* 1358 */     TypeInfo typeInfo = paramDomain.getDataType();
/* 1359 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(typeInfo);
/*      */     
/* 1361 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 1362 */       object1 = paramString1;
/* 1363 */       object2 = paramString2;
/* 1364 */       object3 = "Unicode";
/* 1365 */       object4 = paramString3;
/*      */     } else {
/* 1367 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 1369 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramDomain
/*      */ 
/*      */ 
/*      */           
/* 1373 */           .getSchema().getName(), paramString4, dataTypeInformation.dataType, dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, object1, object2, object3, object1, object2, object4, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, paramDomain
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1407 */           .getDefaultSQL(), dataTypeInformation.maximumCardinality, "TYPE", dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid, paramDomain
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1424 */           .getOnUpdateSQL(), (domain != null) ? paramString1 : null, (domain != null) ? domain
/*      */ 
/*      */ 
/*      */           
/* 1428 */           .getSchema().getName() : null, (domain != null) ? domain
/*      */           
/* 1430 */           .getName() : null, paramDomain
/*      */           
/* 1432 */           .getComment() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void domainConstraints(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1438 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1439 */       for (Constraint constraint : schema.getAllConstraints()) {
/* 1440 */         if (constraint.getConstraintType() != Constraint.Type.DOMAIN) {
/*      */           continue;
/*      */         }
/* 1443 */         ConstraintDomain constraintDomain = (ConstraintDomain)constraint;
/* 1444 */         Domain domain = constraintDomain.getDomain();
/* 1445 */         String str = domain.getName();
/* 1446 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 1449 */         domainConstraints(paramSessionLocal, paramArrayList, paramString, constraintDomain, domain, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void domainConstraints(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, ConstraintDomain paramConstraintDomain, Domain paramDomain, String paramString2) {
/* 1456 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramConstraintDomain
/*      */ 
/*      */ 
/*      */           
/* 1460 */           .getSchema().getName(), paramConstraintDomain
/*      */           
/* 1462 */           .getName(), paramString1, paramDomain
/*      */ 
/*      */ 
/*      */           
/* 1466 */           .getSchema().getName(), paramString2, "NO", "NO", paramConstraintDomain
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1475 */           .getComment() });
/*      */   }
/*      */ 
/*      */   
/*      */   private void elementTypesFields(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString, int paramInt) {
/* 1480 */     String str1 = this.database.getMainSchema().getName();
/* 1481 */     String str2 = this.database.getCompareMode().getName();
/* 1482 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1483 */       String str = schema.getName();
/* 1484 */       for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) {
/* 1485 */         elementTypesFieldsForTable(paramSessionLocal, paramArrayList, paramString, paramInt, str1, str2, str, table);
/*      */       }
/*      */       
/* 1488 */       for (Domain domain : schema.getAllDomains()) {
/* 1489 */         elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString, paramInt, str1, str2, str, domain
/* 1490 */             .getName(), "DOMAIN", "TYPE", domain.getDataType());
/*      */       }
/* 1492 */       for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
/* 1493 */         if (userDefinedFunction instanceof FunctionAlias) {
/* 1494 */           FunctionAlias.JavaMethod[] arrayOfJavaMethod; String str3 = userDefinedFunction.getName();
/*      */           
/*      */           try {
/* 1497 */             arrayOfJavaMethod = ((FunctionAlias)userDefinedFunction).getJavaMethods();
/* 1498 */           } catch (DbException dbException) {
/*      */             continue;
/*      */           } 
/* 1501 */           for (byte b = 0; b < arrayOfJavaMethod.length; b++) {
/* 1502 */             FunctionAlias.JavaMethod javaMethod = arrayOfJavaMethod[b];
/* 1503 */             TypeInfo typeInfo = javaMethod.getDataType();
/* 1504 */             String str4 = str3 + '_' + (b + 1);
/* 1505 */             if (typeInfo != null && typeInfo.getValueType() != 0) {
/* 1506 */               elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString, paramInt, str1, str2, str, str4, "ROUTINE", "RESULT", typeInfo);
/*      */             }
/*      */             
/* 1509 */             Class[] arrayOfClass = javaMethod.getColumnClasses();
/* 1510 */             byte b1 = 1, b2 = javaMethod.hasConnectionParam() ? 1 : 0;
/* 1511 */             for (int i = arrayOfClass.length; b2 < i; b1++, b2++) {
/* 1512 */               elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString, paramInt, str1, str2, str, str4, "ROUTINE", 
/* 1513 */                   Integer.toString(b1), 
/* 1514 */                   ValueToObjectConverter2.classToType(arrayOfClass[b2]));
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/* 1519 */       for (Constant constant : schema.getAllConstants()) {
/* 1520 */         elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString, paramInt, str1, str2, str, constant
/* 1521 */             .getName(), "CONSTANT", "TYPE", constant.getValue().getType());
/*      */       }
/*      */     } 
/* 1524 */     for (Table table : paramSessionLocal.getLocalTempTables()) {
/* 1525 */       elementTypesFieldsForTable(paramSessionLocal, paramArrayList, paramString, paramInt, str1, str2, table
/* 1526 */           .getSchema().getName(), table);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void elementTypesFieldsForTable(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, int paramInt, String paramString2, String paramString3, String paramString4, Table paramTable) {
/* 1533 */     if (hideTable(paramTable, paramSessionLocal)) {
/*      */       return;
/*      */     }
/* 1536 */     String str = paramTable.getName();
/* 1537 */     Column[] arrayOfColumn = paramTable.getColumns();
/* 1538 */     for (byte b = 0; b < arrayOfColumn.length; b++)
/* 1539 */       elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString1, paramInt, paramString2, paramString3, paramString4, str, "TABLE", 
/* 1540 */           Integer.toString(b + 1), arrayOfColumn[b].getType()); 
/*      */   }
/*      */   
/*      */   private void elementTypesFieldsRow(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, int paramInt, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, TypeInfo paramTypeInfo) {
/*      */     String str;
/*      */     ExtTypeInfoRow extTypeInfoRow;
/*      */     byte b;
/* 1547 */     switch (paramTypeInfo.getValueType()) {
/*      */       case 36:
/* 1549 */         if (paramInt == 22) {
/* 1550 */           enumValues(paramSessionLocal, paramArrayList, paramString1, paramString4, paramString5, paramString6, paramString7, paramTypeInfo);
/*      */         }
/*      */         break;
/*      */       case 40:
/* 1554 */         paramTypeInfo = (TypeInfo)paramTypeInfo.getExtTypeInfo();
/* 1555 */         str = paramString7 + '_';
/* 1556 */         if (paramInt == 8) {
/* 1557 */           elementTypes(paramSessionLocal, paramArrayList, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, str, paramTypeInfo);
/*      */         }
/*      */         
/* 1560 */         elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString1, paramInt, paramString2, paramString3, paramString4, paramString5, paramString6, str, paramTypeInfo);
/*      */         break;
/*      */ 
/*      */       
/*      */       case 41:
/* 1565 */         extTypeInfoRow = (ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo();
/* 1566 */         b = 0;
/* 1567 */         for (Map.Entry entry : extTypeInfoRow.getFields()) {
/* 1568 */           paramTypeInfo = (TypeInfo)entry.getValue();
/* 1569 */           String str1 = (String)entry.getKey();
/* 1570 */           String str2 = paramString7 + '_' + ++b;
/* 1571 */           if (paramInt == 9) {
/* 1572 */             fields(paramSessionLocal, paramArrayList, paramString1, paramString2, paramString3, paramString4, paramString5, paramString6, paramString7, str1, b, str2, paramTypeInfo);
/*      */           }
/*      */           
/* 1575 */           elementTypesFieldsRow(paramSessionLocal, paramArrayList, paramString1, paramInt, paramString2, paramString3, paramString4, paramString5, paramString6, str2, paramTypeInfo);
/*      */         } 
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void elementTypes(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, TypeInfo paramTypeInfo) {
/*      */     Object object1, object2, object3, object4;
/* 1585 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(paramTypeInfo);
/*      */     
/* 1587 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 1588 */       object1 = paramString1;
/* 1589 */       object2 = paramString2;
/* 1590 */       object3 = "Unicode";
/* 1591 */       object4 = paramString3;
/*      */     } else {
/* 1593 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 1595 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramString4, paramString5, paramString6, paramString7, dataTypeInformation.dataType, dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, object1, object2, object3, object1, object2, object4, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, dataTypeInformation.maximumCardinality, paramString8, dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fields(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, int paramInt, String paramString9, TypeInfo paramTypeInfo) {
/*      */     Object object1, object2, object3, object4;
/* 1657 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(paramTypeInfo);
/*      */     
/* 1659 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 1660 */       object1 = paramString1;
/* 1661 */       object2 = paramString2;
/* 1662 */       object3 = "Unicode";
/* 1663 */       object4 = paramString3;
/*      */     } else {
/* 1665 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 1667 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramString4, paramString5, paramString6, paramString7, paramString8, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1681 */           ValueInteger.get(paramInt), dataTypeInformation.dataType, dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, object1, object2, object3, object1, object2, object4, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, dataTypeInformation.maximumCardinality, paramString9, dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid });
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
/*      */ 
/*      */ 
/*      */   
/*      */   private void keyColumnUsage(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1732 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1733 */       for (Constraint constraint : schema.getAllConstraints()) {
/* 1734 */         Constraint.Type type = constraint.getConstraintType();
/* 1735 */         IndexColumn[] arrayOfIndexColumn = null;
/* 1736 */         if (type == Constraint.Type.UNIQUE || type == Constraint.Type.PRIMARY_KEY) {
/* 1737 */           arrayOfIndexColumn = ((ConstraintUnique)constraint).getColumns();
/* 1738 */         } else if (type == Constraint.Type.REFERENTIAL) {
/* 1739 */           arrayOfIndexColumn = ((ConstraintReferential)constraint).getColumns();
/*      */         } 
/* 1741 */         if (arrayOfIndexColumn == null) {
/*      */           continue;
/*      */         }
/* 1744 */         Table table = constraint.getTable();
/* 1745 */         if (hideTable(table, paramSessionLocal)) {
/*      */           continue;
/*      */         }
/* 1748 */         String str = table.getName();
/* 1749 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 1752 */         keyColumnUsage(paramSessionLocal, paramArrayList, paramString, constraint, type, arrayOfIndexColumn, table, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void keyColumnUsage(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Constraint paramConstraint, Constraint.Type paramType, IndexColumn[] paramArrayOfIndexColumn, Table paramTable, String paramString2) {
/*      */     ConstraintUnique constraintUnique;
/* 1760 */     if (paramType == Constraint.Type.REFERENTIAL) {
/* 1761 */       constraintUnique = ((ConstraintReferential)paramConstraint).getReferencedConstraint();
/*      */     } else {
/* 1763 */       constraintUnique = null;
/*      */     } 
/* 1765 */     for (byte b = 0; b < paramArrayOfIndexColumn.length; b++) {
/* 1766 */       IndexColumn indexColumn = paramArrayOfIndexColumn[b];
/* 1767 */       ValueInteger valueInteger1 = ValueInteger.get(b + 1);
/* 1768 */       ValueInteger valueInteger2 = null;
/* 1769 */       if (constraintUnique != null) {
/* 1770 */         Column column = (((ConstraintReferential)paramConstraint).getRefColumns()[b]).column;
/* 1771 */         IndexColumn[] arrayOfIndexColumn = constraintUnique.getColumns();
/* 1772 */         for (byte b1 = 0; b1 < arrayOfIndexColumn.length; b1++) {
/* 1773 */           if ((arrayOfIndexColumn[b1]).column.equals(column)) {
/* 1774 */             valueInteger2 = ValueInteger.get(b1 + 1);
/*      */             break;
/*      */           } 
/*      */         } 
/*      */       } 
/* 1779 */       add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramConstraint
/*      */ 
/*      */ 
/*      */             
/* 1783 */             .getSchema().getName(), paramConstraint
/*      */             
/* 1785 */             .getName(), paramString1, paramTable
/*      */ 
/*      */ 
/*      */             
/* 1789 */             .getSchema().getName(), paramString2, indexColumn.columnName, valueInteger1, valueInteger2 });
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
/*      */   private void parameters(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString) {
/* 1803 */     String str1 = this.database.getMainSchema().getName();
/* 1804 */     String str2 = this.database.getCompareMode().getName();
/* 1805 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1806 */       for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
/* 1807 */         if (userDefinedFunction instanceof FunctionAlias) {
/*      */           FunctionAlias.JavaMethod[] arrayOfJavaMethod;
/*      */           try {
/* 1810 */             arrayOfJavaMethod = ((FunctionAlias)userDefinedFunction).getJavaMethods();
/* 1811 */           } catch (DbException dbException) {
/*      */             continue;
/*      */           } 
/* 1814 */           for (byte b = 0; b < arrayOfJavaMethod.length; b++) {
/* 1815 */             FunctionAlias.JavaMethod javaMethod = arrayOfJavaMethod[b];
/* 1816 */             Class[] arrayOfClass = javaMethod.getColumnClasses();
/* 1817 */             byte b1 = 1, b2 = javaMethod.hasConnectionParam() ? 1 : 0;
/* 1818 */             for (int i = arrayOfClass.length; b2 < i; b1++, b2++) {
/* 1819 */               parameters(paramSessionLocal, paramArrayList, paramString, str1, str2, schema.getName(), userDefinedFunction
/* 1820 */                   .getName() + '_' + (b + 1), 
/* 1821 */                   ValueToObjectConverter2.classToType(arrayOfClass[b2]), b1);
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void parameters(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, TypeInfo paramTypeInfo, int paramInt) {
/*      */     Object object1, object2, object3, object4;
/* 1831 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(paramTypeInfo);
/*      */     
/* 1833 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 1834 */       object1 = paramString1;
/* 1835 */       object2 = paramString2;
/* 1836 */       object3 = "Unicode";
/* 1837 */       object4 = paramString3;
/*      */     } else {
/* 1839 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 1841 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramString4, paramString5, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1849 */           ValueInteger.get(paramInt), "IN", "NO", 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1855 */           DataType.isLargeObject(paramTypeInfo.getValueType()) ? "YES" : "NO", "P" + paramInt, 
/*      */ 
/*      */ 
/*      */           
/* 1859 */           identifier(dataTypeInformation.dataType), dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, object1, object2, object3, object1, object2, object4, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, dataTypeInformation.maximumCardinality, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1891 */           Integer.toString(paramInt), dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, null, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid });
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
/*      */   private void referentialConstraints(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 1910 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1911 */       for (Constraint constraint : schema.getAllConstraints()) {
/* 1912 */         if (constraint.getConstraintType() != Constraint.Type.REFERENTIAL) {
/*      */           continue;
/*      */         }
/* 1915 */         if (hideTable(constraint.getTable(), paramSessionLocal)) {
/*      */           continue;
/*      */         }
/* 1918 */         String str = constraint.getName();
/* 1919 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 1922 */         referentialConstraints(paramSessionLocal, paramArrayList, paramString, (ConstraintReferential)constraint, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void referentialConstraints(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, ConstraintReferential paramConstraintReferential, String paramString2) {
/* 1929 */     ConstraintUnique constraintUnique = paramConstraintReferential.getReferencedConstraint();
/* 1930 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramConstraintReferential
/*      */ 
/*      */ 
/*      */           
/* 1934 */           .getSchema().getName(), paramString2, paramString1, constraintUnique
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1940 */           .getSchema().getName(), constraintUnique
/*      */           
/* 1942 */           .getName(), "NONE", paramConstraintReferential
/*      */ 
/*      */ 
/*      */           
/* 1946 */           .getUpdateAction().getSqlName(), paramConstraintReferential
/*      */           
/* 1948 */           .getDeleteAction().getSqlName() });
/*      */   }
/*      */ 
/*      */   
/*      */   private void routines(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString) {
/* 1953 */     boolean bool = paramSessionLocal.getUser().isAdmin();
/* 1954 */     String str1 = this.database.getMainSchema().getName();
/* 1955 */     String str2 = this.database.getCompareMode().getName();
/* 1956 */     for (Schema schema : this.database.getAllSchemas()) {
/* 1957 */       String str = schema.getName();
/* 1958 */       for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
/* 1959 */         String str3 = userDefinedFunction.getName();
/* 1960 */         if (userDefinedFunction instanceof FunctionAlias) {
/* 1961 */           FunctionAlias.JavaMethod[] arrayOfJavaMethod; FunctionAlias functionAlias = (FunctionAlias)userDefinedFunction;
/*      */           
/*      */           try {
/* 1964 */             arrayOfJavaMethod = functionAlias.getJavaMethods();
/* 1965 */           } catch (DbException dbException) {
/*      */             continue;
/*      */           } 
/* 1968 */           for (byte b = 0; b < arrayOfJavaMethod.length; b++) {
/* 1969 */             String str4; FunctionAlias.JavaMethod javaMethod = arrayOfJavaMethod[b];
/* 1970 */             TypeInfo typeInfo = javaMethod.getDataType();
/*      */             
/* 1972 */             if (typeInfo != null && typeInfo.getValueType() == 0) {
/* 1973 */               str4 = "PROCEDURE";
/* 1974 */               typeInfo = null;
/*      */             } else {
/* 1976 */               str4 = "FUNCTION";
/*      */             } 
/* 1978 */             routines(paramSessionLocal, paramArrayList, paramString, str1, str2, str, str3, str3 + '_' + (b + 1), str4, bool ? functionAlias
/* 1979 */                 .getSource() : null, functionAlias
/* 1980 */                 .getJavaClassName() + '.' + functionAlias.getJavaMethodName(), typeInfo, functionAlias
/* 1981 */                 .isDeterministic(), functionAlias.getComment());
/*      */           }  continue;
/*      */         } 
/* 1984 */         routines(paramSessionLocal, paramArrayList, paramString, str1, str2, str, str3, str3, "AGGREGATE", (String)null, userDefinedFunction
/* 1985 */             .getJavaClassName(), TypeInfo.TYPE_NULL, false, userDefinedFunction
/* 1986 */             .getComment());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void routines(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7, String paramString8, String paramString9, TypeInfo paramTypeInfo, boolean paramBoolean, String paramString10) {
/*      */     Object object1, object2, object3, object4;
/* 1995 */     DataTypeInformation dataTypeInformation = (paramTypeInfo != null) ? DataTypeInformation.valueOf(paramTypeInfo) : DataTypeInformation.NULL;
/*      */     
/* 1997 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 1998 */       object1 = paramString1;
/* 1999 */       object2 = paramString2;
/* 2000 */       object3 = "Unicode";
/* 2001 */       object4 = paramString3;
/*      */     } else {
/* 2003 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 2005 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramString4, paramString6, paramString1, paramString4, paramString5, paramString7, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2021 */           identifier(dataTypeInformation.dataType), dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, object1, object2, object3, object1, object2, object4, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, dataTypeInformation.maximumCardinality, "RESULT", "EXTERNAL", paramString8, paramString9, "JAVA", "GENERAL", paramBoolean ? "YES" : "NO", dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid, paramString10 });
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void schemata(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString) {
/* 2083 */     String str1 = this.database.getMainSchema().getName();
/* 2084 */     String str2 = this.database.getCompareMode().getName();
/* 2085 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2086 */       add(paramSessionLocal, paramArrayList, new Object[] { paramString, schema
/*      */ 
/*      */ 
/*      */             
/* 2090 */             .getName(), 
/*      */             
/* 2092 */             identifier(schema.getOwner().getName()), paramString, str1, "Unicode", null, str2, schema
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2105 */             .getComment() });
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void sequences(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2111 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2112 */       for (Sequence sequence : schema.getAllSequences()) {
/* 2113 */         if (sequence.getBelongsToTable()) {
/*      */           continue;
/*      */         }
/* 2116 */         String str = sequence.getName();
/* 2117 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 2120 */         sequences(paramSessionLocal, paramArrayList, paramString, sequence, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void sequences(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Sequence paramSequence, String paramString2) {
/* 2127 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(paramSequence.getDataType());
/* 2128 */     Sequence.Cycle cycle = paramSequence.getCycle();
/* 2129 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramSequence
/*      */ 
/*      */ 
/*      */           
/* 2133 */           .getSchema().getName(), paramString2, dataTypeInformation.dataType, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2139 */           ValueInteger.get(paramSequence.getEffectivePrecision()), dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2145 */           ValueBigint.get(paramSequence.getStartValue()), 
/*      */           
/* 2147 */           ValueBigint.get(paramSequence.getMinValue()), 
/*      */           
/* 2149 */           ValueBigint.get(paramSequence.getMaxValue()),
/*      */           
/* 2151 */           ValueBigint.get(paramSequence.getIncrement()), 
/*      */           
/* 2153 */           cycle.isCycle() ? "YES" : "NO", dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, (cycle != Sequence.Cycle.EXHAUSTED) ? 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2162 */           ValueBigint.get(paramSequence.getBaseValue()) : null, 
/*      */           
/* 2164 */           ValueBigint.get(paramSequence.getCacheSize()), paramSequence
/*      */           
/* 2166 */           .getComment() });
/*      */   }
/*      */ 
/*      */   
/*      */   private void tables(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2171 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2172 */       for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) {
/* 2173 */         String str = table.getName();
/* 2174 */         if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/* 2175 */           tables(paramSessionLocal, paramArrayList, paramString, table, str);
/*      */         }
/*      */       } 
/*      */     } 
/* 2179 */     for (Table table : paramSessionLocal.getLocalTempTables()) {
/* 2180 */       String str = table.getName();
/* 2181 */       if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2))
/* 2182 */         tables(paramSessionLocal, paramArrayList, paramString, table, str); 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void tables(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Table paramTable, String paramString2) {
/*      */     Object object;
/*      */     String str;
/* 2189 */     if (hideTable(paramTable, paramSessionLocal)) {
/*      */       return;
/*      */     }
/*      */     
/* 2193 */     if (paramTable.isTemporary()) {
/* 2194 */       object = paramTable.getOnCommitTruncate() ? "DELETE" : (paramTable.getOnCommitDrop() ? "DROP" : "PRESERVE");
/* 2195 */       str = paramTable.isGlobalTemporary() ? "GLOBAL TEMPORARY" : "LOCAL TEMPORARY";
/*      */     } else {
/* 2197 */       object = null;
/* 2198 */       switch (paramTable.getTableType()) {
/*      */         case TABLE_LINK:
/* 2200 */           str = "TABLE LINK";
/*      */           break;
/*      */         case EXTERNAL_TABLE_ENGINE:
/* 2203 */           str = "EXTERNAL";
/*      */           break;
/*      */         default:
/* 2206 */           str = paramTable.isPersistIndexes() ? "CACHED" : "MEMORY";
/*      */           break;
/*      */       } 
/*      */     } 
/* 2210 */     long l = paramTable.getMaxDataModificationId();
/* 2211 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramTable
/*      */ 
/*      */ 
/*      */           
/* 2215 */           .getSchema().getName(), paramString2, paramTable
/*      */ 
/*      */ 
/*      */           
/* 2219 */           .getSQLTableType(), 
/*      */           
/* 2221 */           paramTable.isInsertable() ? "YES" : "NO", object, str, paramTable
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2228 */           .getComment(), (l != Long.MAX_VALUE) ? 
/*      */           
/* 2230 */           ValueBigint.get(l) : null, paramTable
/*      */           
/* 2232 */           .getClass().getName(),
/*      */           
/* 2234 */           ValueBigint.get(paramTable.getRowCountApproximation(paramSessionLocal)) });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void tableConstraints(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2240 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2241 */       for (Constraint constraint : schema.getAllConstraints()) {
/* 2242 */         Constraint.Type type = constraint.getConstraintType();
/* 2243 */         if (type == Constraint.Type.DOMAIN) {
/*      */           continue;
/*      */         }
/* 2246 */         Table table = constraint.getTable();
/* 2247 */         if (hideTable(table, paramSessionLocal)) {
/*      */           continue;
/*      */         }
/* 2250 */         String str = table.getName();
/* 2251 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 2254 */         tableConstraints(paramSessionLocal, paramArrayList, paramString, constraint, type, table, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void tableConstraints(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Constraint paramConstraint, Constraint.Type paramType, Table paramTable, String paramString2) {
/*      */     boolean bool;
/* 2261 */     Index index = paramConstraint.getIndex();
/*      */     
/* 2263 */     if (paramType != Constraint.Type.REFERENTIAL) {
/* 2264 */       bool = true;
/*      */     } else {
/*      */       
/* 2267 */       bool = (this.database.getReferentialIntegrity() && paramTable.getCheckForeignKeyConstraints() && ((ConstraintReferential)paramConstraint).getRefTable().getCheckForeignKeyConstraints()) ? true : false;
/*      */     } 
/* 2269 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramConstraint
/*      */ 
/*      */ 
/*      */           
/* 2273 */           .getSchema().getName(), paramConstraint
/*      */           
/* 2275 */           .getName(), paramType
/*      */           
/* 2277 */           .getSqlName(), paramString1, paramTable
/*      */ 
/*      */ 
/*      */           
/* 2281 */           .getSchema().getName(), paramString2, "NO", "NO", bool ? "YES" : "NO", (index != null) ? paramString1 : null, (index != null) ? index
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2294 */           .getSchema().getName() : null, (index != null) ? index
/*      */           
/* 2296 */           .getName() : null, paramConstraint
/*      */           
/* 2298 */           .getComment() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void tablePrivileges(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2304 */     for (Right right : this.database.getAllRights()) {
/* 2305 */       DbObject dbObject = right.getGrantedObject();
/* 2306 */       if (!(dbObject instanceof Table)) {
/*      */         continue;
/*      */       }
/* 2309 */       Table table = (Table)dbObject;
/* 2310 */       if (hideTable(table, paramSessionLocal)) {
/*      */         continue;
/*      */       }
/* 2313 */       String str = table.getName();
/* 2314 */       if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */         continue;
/*      */       }
/* 2317 */       addPrivileges(paramSessionLocal, paramArrayList, right.getGrantee(), paramString, table, (String)null, right.getRightMask());
/*      */     } 
/*      */   }
/*      */   
/*      */   private void triggers(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2322 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2323 */       for (TriggerObject triggerObject : schema.getAllTriggers()) {
/* 2324 */         Table table = triggerObject.getTable();
/* 2325 */         String str = table.getName();
/* 2326 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 2329 */         int i = triggerObject.getTypeMask();
/* 2330 */         if ((i & 0x1) != 0) {
/* 2331 */           triggers(paramSessionLocal, paramArrayList, paramString, triggerObject, "INSERT", table, str);
/*      */         }
/* 2333 */         if ((i & 0x2) != 0) {
/* 2334 */           triggers(paramSessionLocal, paramArrayList, paramString, triggerObject, "UPDATE", table, str);
/*      */         }
/* 2336 */         if ((i & 0x4) != 0) {
/* 2337 */           triggers(paramSessionLocal, paramArrayList, paramString, triggerObject, "DELETE", table, str);
/*      */         }
/* 2339 */         if ((i & 0x8) != 0) {
/* 2340 */           triggers(paramSessionLocal, paramArrayList, paramString, triggerObject, "SELECT", table, str);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void triggers(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, TriggerObject paramTriggerObject, String paramString2, Table paramTable, String paramString3) {
/* 2348 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramTriggerObject
/*      */ 
/*      */ 
/*      */           
/* 2352 */           .getSchema().getName(), paramTriggerObject
/*      */           
/* 2354 */           .getName(), paramString2, paramString1, paramTable
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2360 */           .getSchema().getName(), paramString3, 
/*      */ 
/*      */ 
/*      */           
/* 2364 */           paramTriggerObject.isRowBased() ? "ROW" : "STATEMENT", 
/*      */           
/* 2366 */           paramTriggerObject.isInsteadOf() ? "INSTEAD OF" : (paramTriggerObject.isBefore() ? "BEFORE" : "AFTER"), 
/*      */ 
/*      */           
/* 2369 */           ValueBoolean.get(paramTriggerObject.isOnRollback()), paramTriggerObject
/*      */           
/* 2371 */           .getTriggerClassName(), 
/*      */           
/* 2373 */           ValueInteger.get(paramTriggerObject.getQueueSize()), 
/*      */           
/* 2375 */           ValueBoolean.get(paramTriggerObject.isNoWait()), paramTriggerObject
/*      */           
/* 2377 */           .getComment() });
/*      */   }
/*      */ 
/*      */   
/*      */   private void views(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2382 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2383 */       for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) {
/* 2384 */         if (table.isView()) {
/* 2385 */           String str = table.getName();
/* 2386 */           if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/* 2387 */             views(paramSessionLocal, paramArrayList, paramString, table, str);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } 
/* 2392 */     for (Table table : paramSessionLocal.getLocalTempTables()) {
/* 2393 */       if (table.isView()) {
/* 2394 */         String str = table.getName();
/* 2395 */         if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2))
/* 2396 */           views(paramSessionLocal, paramArrayList, paramString, table, str); 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void views(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Table paramTable, String paramString2) {
/*      */     Object object;
/* 2403 */     String str = "VALID";
/* 2404 */     if (paramTable instanceof TableView) {
/* 2405 */       TableView tableView = (TableView)paramTable;
/* 2406 */       object = tableView.getQuery();
/* 2407 */       if (tableView.isInvalid()) {
/* 2408 */         str = "INVALID";
/*      */       }
/*      */     } else {
/* 2411 */       object = null;
/*      */     } 
/* 2413 */     int i = 0;
/* 2414 */     ArrayList<TriggerObject> arrayList = paramTable.getTriggers();
/* 2415 */     if (arrayList != null) {
/* 2416 */       for (TriggerObject triggerObject : arrayList) {
/* 2417 */         if (triggerObject.isInsteadOf()) {
/* 2418 */           i |= triggerObject.getTypeMask();
/*      */         }
/*      */       } 
/*      */     }
/* 2422 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramTable
/*      */ 
/*      */ 
/*      */           
/* 2426 */           .getSchema().getName(), paramString2, object, "NONE", "NO", "NO", ((i & 0x2) != 0) ? "YES" : "NO", ((i & 0x4) != 0) ? "YES" : "NO", ((i & 0x1) != 0) ? "YES" : "NO", str, paramTable
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2447 */           .getComment() });
/*      */   }
/*      */ 
/*      */   
/*      */   private void constants(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString) {
/* 2452 */     String str1 = this.database.getMainSchema().getName();
/* 2453 */     String str2 = this.database.getCompareMode().getName();
/* 2454 */     for (Schema schema : this.database.getAllSchemas()) {
/* 2455 */       for (Constant constant : schema.getAllConstants()) {
/* 2456 */         String str = constant.getName();
/* 2457 */         if (!checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 2460 */         constants(paramSessionLocal, paramArrayList, paramString, str1, str2, constant, str);
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void constants(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, Constant paramConstant, String paramString4) {
/*      */     Object object1, object2, object3, object4;
/* 2467 */     ValueExpression valueExpression = paramConstant.getValue();
/* 2468 */     TypeInfo typeInfo = valueExpression.getType();
/* 2469 */     DataTypeInformation dataTypeInformation = DataTypeInformation.valueOf(typeInfo);
/*      */     
/* 2471 */     if (dataTypeInformation.hasCharsetAndCollation) {
/* 2472 */       object1 = paramString1;
/* 2473 */       object2 = paramString2;
/* 2474 */       object3 = "Unicode";
/* 2475 */       object4 = paramString3;
/*      */     } else {
/* 2477 */       object1 = object2 = object3 = object4 = null;
/*      */     } 
/* 2479 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramConstant
/*      */ 
/*      */ 
/*      */           
/* 2483 */           .getSchema().getName(), paramString4, valueExpression
/*      */ 
/*      */ 
/*      */           
/* 2487 */           .getSQL(0), dataTypeInformation.dataType, dataTypeInformation.characterPrecision, dataTypeInformation.characterPrecision, object1, object2, object3, object1, object2, object4, dataTypeInformation.numericPrecision, dataTypeInformation.numericPrecisionRadix, dataTypeInformation.numericScale, dataTypeInformation.datetimePrecision, dataTypeInformation.intervalType, dataTypeInformation.intervalPrecision, dataTypeInformation.maximumCardinality, "TYPE", dataTypeInformation.declaredDataType, dataTypeInformation.declaredNumericPrecision, dataTypeInformation.declaredNumericScale, dataTypeInformation.geometryType, dataTypeInformation.geometrySrid, paramConstant
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2533 */           .getComment() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void enumValues(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, TypeInfo paramTypeInfo) {
/* 2539 */     ExtTypeInfoEnum extTypeInfoEnum = (ExtTypeInfoEnum)paramTypeInfo.getExtTypeInfo();
/* 2540 */     if (extTypeInfoEnum == null)
/*      */       return;  byte b1, b2;
/*      */     int i;
/* 2543 */     for (b1 = 0, b2 = paramSessionLocal.zeroBasedEnums() ? 0 : 1, i = extTypeInfoEnum.getCount(); b1 < i; b1++, b2++) {
/* 2544 */       add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramString2, paramString3, paramString4, paramString5, extTypeInfoEnum
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2556 */             .getEnumerator(b1), 
/*      */             
/* 2558 */             ValueInteger.get(b2) });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void indexes(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList, String paramString, boolean paramBoolean) {
/* 2565 */     if (paramValue1 != null && paramValue1.equals(paramValue2)) {
/* 2566 */       String str = paramValue1.getString();
/* 2567 */       if (str == null) {
/*      */         return;
/*      */       }
/* 2570 */       for (Schema schema : this.database.getAllSchemas()) {
/* 2571 */         Table table1 = schema.getTableOrViewByName(paramSessionLocal, str);
/* 2572 */         if (table1 != null) {
/* 2573 */           indexes(paramSessionLocal, paramArrayList, paramString, paramBoolean, table1, table1.getName());
/*      */         }
/*      */       } 
/* 2576 */       Table table = paramSessionLocal.findLocalTempTable(str);
/* 2577 */       if (table != null) {
/* 2578 */         indexes(paramSessionLocal, paramArrayList, paramString, paramBoolean, table, table.getName());
/*      */       }
/*      */     } else {
/* 2581 */       for (Schema schema : this.database.getAllSchemas()) {
/* 2582 */         for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) {
/* 2583 */           String str = table.getName();
/* 2584 */           if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/* 2585 */             indexes(paramSessionLocal, paramArrayList, paramString, paramBoolean, table, str);
/*      */           }
/*      */         } 
/*      */       } 
/* 2589 */       for (Table table : paramSessionLocal.getLocalTempTables()) {
/* 2590 */         String str = table.getName();
/* 2591 */         if (checkIndex(paramSessionLocal, str, paramValue1, paramValue2)) {
/* 2592 */           indexes(paramSessionLocal, paramArrayList, paramString, paramBoolean, table, str);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void indexes(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, boolean paramBoolean, Table paramTable, String paramString2) {
/* 2600 */     if (hideTable(paramTable, paramSessionLocal)) {
/*      */       return;
/*      */     }
/* 2603 */     ArrayList<Index> arrayList = paramTable.getIndexes();
/* 2604 */     if (arrayList == null) {
/*      */       return;
/*      */     }
/* 2607 */     for (Index index : arrayList) {
/* 2608 */       if (index.getCreateSQL() == null) {
/*      */         continue;
/*      */       }
/* 2611 */       if (paramBoolean) {
/* 2612 */         indexColumns(paramSessionLocal, paramArrayList, paramString1, paramTable, paramString2, index); continue;
/*      */       } 
/* 2614 */       indexes(paramSessionLocal, paramArrayList, paramString1, paramTable, paramString2, index);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void indexes(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Table paramTable, String paramString2, Index paramIndex) {
/* 2621 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramIndex
/*      */ 
/*      */ 
/*      */           
/* 2625 */           .getSchema().getName(), paramIndex
/*      */           
/* 2627 */           .getName(), paramString1, paramTable
/*      */ 
/*      */ 
/*      */           
/* 2631 */           .getSchema().getName(), paramString2, paramIndex
/*      */ 
/*      */ 
/*      */           
/* 2635 */           .getIndexType().getSQL(), 
/*      */           
/* 2637 */           ValueBoolean.get(paramIndex.getIndexType().getBelongsToConstraint()), paramIndex
/*      */           
/* 2639 */           .getComment(), paramIndex
/*      */           
/* 2641 */           .getClass().getName() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void indexColumns(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString1, Table paramTable, String paramString2, Index paramIndex) {
/* 2647 */     IndexColumn[] arrayOfIndexColumn = paramIndex.getIndexColumns();
/* 2648 */     int i = paramIndex.getUniqueColumnCount(); byte b; int j;
/* 2649 */     for (b = 0, j = arrayOfIndexColumn.length; b < j; ) {
/* 2650 */       IndexColumn indexColumn = arrayOfIndexColumn[b];
/* 2651 */       int k = indexColumn.sortType;
/* 2652 */       add(paramSessionLocal, paramArrayList, new Object[] { paramString1, paramIndex
/*      */ 
/*      */ 
/*      */             
/* 2656 */             .getSchema().getName(), paramIndex
/*      */             
/* 2658 */             .getName(), paramString1, paramTable
/*      */ 
/*      */ 
/*      */             
/* 2662 */             .getSchema().getName(), paramString2, indexColumn.column
/*      */ 
/*      */ 
/*      */             
/* 2666 */             .getName(), 
/*      */             
/* 2668 */             ValueInteger.get(++b), ((k & 0x1) == 0) ? "ASC" : "DESC", ((k & 0x2) != 0) ? "FIRST" : (((k & 0x4) != 0) ? "LAST" : null),
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2675 */             ValueBoolean.get((b <= i)) });
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void inDoubt(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2681 */     if (paramSessionLocal.getUser().isAdmin()) {
/* 2682 */       ArrayList arrayList = this.database.getInDoubtTransactions();
/* 2683 */       if (arrayList != null) {
/* 2684 */         for (InDoubtTransaction inDoubtTransaction : arrayList) {
/* 2685 */           add(paramSessionLocal, paramArrayList, new Object[] { inDoubtTransaction
/*      */                 
/* 2687 */                 .getTransactionName(), inDoubtTransaction
/*      */                 
/* 2689 */                 .getStateDescription() });
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void locks(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2697 */     if (paramSessionLocal.getUser().isAdmin()) {
/* 2698 */       for (SessionLocal sessionLocal : this.database.getSessions(false)) {
/* 2699 */         locks(paramSessionLocal, paramArrayList, sessionLocal);
/*      */       }
/*      */     } else {
/* 2702 */       locks(paramSessionLocal, paramArrayList, paramSessionLocal);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void locks(SessionLocal paramSessionLocal1, ArrayList<Row> paramArrayList, SessionLocal paramSessionLocal2) {
/* 2707 */     for (Table table : paramSessionLocal2.getLocks()) {
/* 2708 */       add(paramSessionLocal1, paramArrayList, new Object[] { table
/*      */             
/* 2710 */             .getSchema().getName(), table
/*      */             
/* 2712 */             .getName(), 
/*      */             
/* 2714 */             ValueInteger.get(paramSessionLocal2.getId()), 
/*      */             
/* 2716 */             table.isLockedExclusivelyBy(paramSessionLocal2) ? "WRITE" : "READ" });
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void queryStatistics(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2722 */     QueryStatisticsData queryStatisticsData = this.database.getQueryStatisticsData();
/* 2723 */     if (queryStatisticsData != null) {
/* 2724 */       for (QueryStatisticsData.QueryEntry queryEntry : queryStatisticsData.getQueries()) {
/* 2725 */         add(paramSessionLocal, paramArrayList, new Object[] { queryEntry.sqlStatement, 
/*      */ 
/*      */ 
/*      */               
/* 2729 */               ValueInteger.get(queryEntry.count), 
/*      */               
/* 2731 */               ValueDouble.get(queryEntry.executionTimeMinNanos / 1000000.0D), 
/*      */               
/* 2733 */               ValueDouble.get(queryEntry.executionTimeMaxNanos / 1000000.0D), 
/*      */               
/* 2735 */               ValueDouble.get(queryEntry.executionTimeCumulativeNanos / 1000000.0D), 
/*      */               
/* 2737 */               ValueDouble.get(queryEntry.executionTimeMeanNanos / 1000000.0D), 
/*      */               
/* 2739 */               ValueDouble.get(queryEntry.getExecutionTimeStandardDeviation() / 1000000.0D), 
/*      */               
/* 2741 */               ValueBigint.get(queryEntry.rowCountMin), 
/*      */               
/* 2743 */               ValueBigint.get(queryEntry.rowCountMax), 
/*      */               
/* 2745 */               ValueBigint.get(queryEntry.rowCountCumulative),
/*      */               
/* 2747 */               ValueDouble.get(queryEntry.rowCountMean), 
/*      */               
/* 2749 */               ValueDouble.get(queryEntry.getRowCountStandardDeviation()) });
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void rights(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, ArrayList<Row> paramArrayList) {
/* 2756 */     if (!paramSessionLocal.getUser().isAdmin()) {
/*      */       return;
/*      */     }
/* 2759 */     for (Right right : this.database.getAllRights()) {
/* 2760 */       Role role = right.getGrantedRole();
/* 2761 */       DbObject dbObject = right.getGrantee();
/* 2762 */       String str = (dbObject.getType() == 2) ? "USER" : "ROLE";
/* 2763 */       if (role == null) {
/* 2764 */         DbObject dbObject1 = right.getGrantedObject();
/* 2765 */         Schema schema = null;
/* 2766 */         Table table = null;
/* 2767 */         if (dbObject1 != null) {
/* 2768 */           if (dbObject1 instanceof Schema) {
/* 2769 */             schema = (Schema)dbObject1;
/* 2770 */           } else if (dbObject1 instanceof Table) {
/* 2771 */             table = (Table)dbObject1;
/* 2772 */             schema = table.getSchema();
/*      */           } 
/*      */         }
/* 2775 */         String str1 = (table != null) ? table.getName() : "";
/* 2776 */         String str2 = (schema != null) ? schema.getName() : "";
/* 2777 */         if (!checkIndex(paramSessionLocal, str1, paramValue1, paramValue2)) {
/*      */           continue;
/*      */         }
/* 2780 */         add(paramSessionLocal, paramArrayList, new Object[] {
/*      */               
/* 2782 */               identifier(dbObject.getName()), str, null, right
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2788 */               .getRights(), str2, str1
/*      */             });
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2795 */       add(paramSessionLocal, paramArrayList, new Object[] {
/*      */             
/* 2797 */             identifier(dbObject.getName()), str, 
/*      */ 
/*      */ 
/*      */             
/* 2801 */             identifier(role.getName()), null, null, null
/*      */           });
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
/*      */   private void roles(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2814 */     boolean bool = paramSessionLocal.getUser().isAdmin();
/* 2815 */     for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) {
/* 2816 */       if (rightOwner instanceof Role) {
/* 2817 */         Role role = (Role)rightOwner;
/* 2818 */         if (bool || paramSessionLocal.getUser().isRoleGranted(role)) {
/* 2819 */           add(paramSessionLocal, paramArrayList, new Object[] {
/*      */                 
/* 2821 */                 identifier(role.getName()), role
/*      */                 
/* 2823 */                 .getComment()
/*      */               });
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sessions(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2831 */     if (paramSessionLocal.getUser().isAdmin()) {
/* 2832 */       for (SessionLocal sessionLocal : this.database.getSessions(false)) {
/* 2833 */         sessions(paramSessionLocal, paramArrayList, sessionLocal);
/*      */       }
/*      */     } else {
/* 2836 */       sessions(paramSessionLocal, paramArrayList, paramSessionLocal);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sessions(SessionLocal paramSessionLocal1, ArrayList<Row> paramArrayList, SessionLocal paramSessionLocal2) {
/* 2841 */     NetworkConnectionInfo networkConnectionInfo = paramSessionLocal2.getNetworkConnectionInfo();
/* 2842 */     Command command = paramSessionLocal2.getCurrentCommand();
/* 2843 */     int i = paramSessionLocal2.getBlockingSessionId();
/* 2844 */     add(paramSessionLocal1, paramArrayList, new Object[] {
/*      */           
/* 2846 */           ValueInteger.get(paramSessionLocal2.getId()), paramSessionLocal2
/*      */           
/* 2848 */           .getUser().getName(), (networkConnectionInfo == null) ? null : networkConnectionInfo
/*      */           
/* 2850 */           .getServer(), (networkConnectionInfo == null) ? null : networkConnectionInfo
/*      */           
/* 2852 */           .getClient(), (networkConnectionInfo == null) ? null : networkConnectionInfo
/*      */           
/* 2854 */           .getClientInfo(), paramSessionLocal2
/*      */           
/* 2856 */           .getSessionStart(), paramSessionLocal1
/*      */           
/* 2858 */           .getIsolationLevel().getSQL(), (command == null) ? null : command
/*      */           
/* 2860 */           .toString(), (command == null) ? null : paramSessionLocal2
/*      */           
/* 2862 */           .getCommandStartOrEnd(), 
/*      */           
/* 2864 */           ValueBoolean.get(paramSessionLocal2.hasPendingTransaction()),
/*      */           
/* 2866 */           String.valueOf(paramSessionLocal2.getState()), (i == 0) ? null : 
/*      */           
/* 2868 */           ValueInteger.get(i), 
/*      */           
/* 2870 */           (paramSessionLocal2.getState() == SessionLocal.State.SLEEP) ? paramSessionLocal2.getCommandStartOrEnd() : null
/*      */         });
/*      */   }
/*      */   
/*      */   private void sessionState(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2875 */     for (String str1 : paramSessionLocal.getVariableNames()) {
/* 2876 */       Value value = paramSessionLocal.getVariable(str1);
/* 2877 */       StringBuilder stringBuilder = (new StringBuilder()).append("SET @").append(str1).append(' ');
/* 2878 */       value.getSQL(stringBuilder, 0);
/* 2879 */       add(paramSessionLocal, paramArrayList, new Object[] { "@" + str1, stringBuilder
/*      */ 
/*      */ 
/*      */             
/* 2883 */             .toString() });
/*      */     } 
/*      */     
/* 2886 */     for (Table table : paramSessionLocal.getLocalTempTables()) {
/* 2887 */       add(paramSessionLocal, paramArrayList, new Object[] { "TABLE " + table
/*      */             
/* 2889 */             .getName(), table
/*      */             
/* 2891 */             .getCreateSQL() });
/*      */     } 
/*      */     
/* 2894 */     String[] arrayOfString = paramSessionLocal.getSchemaSearchPath();
/* 2895 */     if (arrayOfString != null && arrayOfString.length > 0) {
/* 2896 */       StringBuilder stringBuilder = new StringBuilder("SET SCHEMA_SEARCH_PATH "); byte b; int i;
/* 2897 */       for (b = 0, i = arrayOfString.length; b < i; b++) {
/* 2898 */         if (b > 0) {
/* 2899 */           stringBuilder.append(", ");
/*      */         }
/* 2901 */         StringUtils.quoteIdentifier(stringBuilder, arrayOfString[b]);
/*      */       } 
/* 2903 */       add(paramSessionLocal, paramArrayList, new Object[] { "SCHEMA_SEARCH_PATH", stringBuilder
/*      */ 
/*      */ 
/*      */             
/* 2907 */             .toString() });
/*      */     } 
/*      */     
/* 2910 */     String str = paramSessionLocal.getCurrentSchemaName();
/* 2911 */     if (str != null) {
/* 2912 */       add(paramSessionLocal, paramArrayList, new Object[] { "SCHEMA", 
/*      */ 
/*      */ 
/*      */             
/* 2916 */             StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), str).toString() });
/*      */     }
/*      */     
/* 2919 */     TimeZoneProvider timeZoneProvider = paramSessionLocal.currentTimeZone();
/* 2920 */     if (!timeZoneProvider.equals(DateTimeUtils.getTimeZone())) {
/* 2921 */       add(paramSessionLocal, paramArrayList, new Object[] { "TIME ZONE", 
/*      */ 
/*      */ 
/*      */             
/* 2925 */             StringUtils.quoteStringSQL(new StringBuilder("SET TIME ZONE "), timeZoneProvider.getId())
/* 2926 */             .toString() });
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void settings(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 2932 */     for (Setting setting : this.database.getAllSettings()) {
/* 2933 */       String str = setting.getStringValue();
/* 2934 */       if (str == null) {
/* 2935 */         str = Integer.toString(setting.getIntValue());
/*      */       }
/* 2937 */       add(paramSessionLocal, paramArrayList, new Object[] { identifier(setting.getName()), str });
/*      */     } 
/* 2939 */     add(paramSessionLocal, paramArrayList, new Object[] { "info.BUILD_ID", "210" });
/* 2940 */     add(paramSessionLocal, paramArrayList, new Object[] { "info.VERSION_MAJOR", "2" });
/* 2941 */     add(paramSessionLocal, paramArrayList, new Object[] { "info.VERSION_MINOR", "1" });
/* 2942 */     add(paramSessionLocal, paramArrayList, new Object[] { "info.VERSION", Constants.FULL_VERSION });
/* 2943 */     if (paramSessionLocal.getUser().isAdmin()) {
/* 2944 */       String[] arrayOfString = { "java.runtime.version", "java.vm.name", "java.vendor", "os.name", "os.arch", "os.version", "sun.os.patch.level", "file.separator", "path.separator", "line.separator", "user.country", "user.language", "user.variant", "file.encoding" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2950 */       for (String str : arrayOfString) {
/* 2951 */         add(paramSessionLocal, paramArrayList, new Object[] { "property." + str, Utils.getProperty(str, "") });
/*      */       } 
/*      */     } 
/* 2954 */     add(paramSessionLocal, paramArrayList, new Object[] { "DEFAULT_NULL_ORDERING", this.database.getDefaultNullOrdering().name() });
/* 2955 */     add(paramSessionLocal, paramArrayList, new Object[] { "EXCLUSIVE", (this.database.getExclusiveSession() == null) ? "FALSE" : "TRUE" });
/* 2956 */     add(paramSessionLocal, paramArrayList, new Object[] { "MODE", this.database.getMode().getName() });
/* 2957 */     add(paramSessionLocal, paramArrayList, new Object[] { "QUERY_TIMEOUT", Integer.toString(paramSessionLocal.getQueryTimeout()) });
/* 2958 */     add(paramSessionLocal, paramArrayList, new Object[] { "TIME ZONE", paramSessionLocal.currentTimeZone().getId() });
/* 2959 */     add(paramSessionLocal, paramArrayList, new Object[] { "TRUNCATE_LARGE_LENGTH", paramSessionLocal.isTruncateLargeLength() ? "TRUE" : "FALSE" });
/* 2960 */     add(paramSessionLocal, paramArrayList, new Object[] { "VARIABLE_BINARY", paramSessionLocal.isVariableBinary() ? "TRUE" : "FALSE" });
/* 2961 */     add(paramSessionLocal, paramArrayList, new Object[] { "OLD_INFORMATION_SCHEMA", paramSessionLocal.isOldInformationSchema() ? "TRUE" : "FALSE" });
/* 2962 */     BitSet bitSet = paramSessionLocal.getNonKeywords();
/* 2963 */     if (bitSet != null) {
/* 2964 */       add(paramSessionLocal, paramArrayList, new Object[] { "NON_KEYWORDS", Parser.formatNonKeywords(bitSet) });
/*      */     }
/* 2966 */     add(paramSessionLocal, paramArrayList, new Object[] { "RETENTION_TIME", Integer.toString(this.database.getRetentionTime()) });
/*      */     
/* 2968 */     for (Map.Entry entry : this.database.getSettings().getSortedSettings()) {
/* 2969 */       add(paramSessionLocal, paramArrayList, new Object[] { entry.getKey(), entry.getValue() });
/*      */     } 
/* 2971 */     Store store = this.database.getStore();
/* 2972 */     MVStore mVStore = store.getMvStore();
/* 2973 */     FileStore fileStore = mVStore.getFileStore();
/* 2974 */     if (fileStore != null) {
/* 2975 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.FILE_WRITE", 
/* 2976 */             Long.toString(fileStore.getWriteCount()) });
/* 2977 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.FILE_WRITE_BYTES", 
/* 2978 */             Long.toString(fileStore.getWriteBytes()) });
/* 2979 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.FILE_READ", 
/* 2980 */             Long.toString(fileStore.getReadCount()) });
/* 2981 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.FILE_READ_BYTES", 
/* 2982 */             Long.toString(fileStore.getReadBytes()) });
/* 2983 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.UPDATE_FAILURE_PERCENT", 
/*      */             
/* 2985 */             String.format(Locale.ENGLISH, "%.2f%%", new Object[] { Double.valueOf(100.0D * mVStore.getUpdateFailureRatio()) }) });
/* 2986 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.FILL_RATE", 
/* 2987 */             Integer.toString(mVStore.getFillRate()) });
/* 2988 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.CHUNKS_FILL_RATE", 
/* 2989 */             Integer.toString(mVStore.getChunksFillRate()) });
/* 2990 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.CHUNKS_FILL_RATE_RW", 
/* 2991 */             Integer.toString(mVStore.getRewritableChunksFillRate()) });
/*      */       try {
/* 2993 */         add(paramSessionLocal, paramArrayList, new Object[] { "info.FILE_SIZE", 
/* 2994 */               Long.toString(fileStore.getFile().size()) });
/* 2995 */       } catch (IOException iOException) {}
/* 2996 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.CHUNK_COUNT", 
/* 2997 */             Long.toString(mVStore.getChunkCount()) });
/* 2998 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.PAGE_COUNT", 
/* 2999 */             Long.toString(mVStore.getPageCount()) });
/* 3000 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.PAGE_COUNT_LIVE", 
/* 3001 */             Long.toString(mVStore.getLivePageCount()) });
/* 3002 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.PAGE_SIZE", 
/* 3003 */             Integer.toString(mVStore.getPageSplitSize()) });
/* 3004 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.CACHE_MAX_SIZE", 
/* 3005 */             Integer.toString(mVStore.getCacheSize()) });
/* 3006 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.CACHE_SIZE", 
/* 3007 */             Integer.toString(mVStore.getCacheSizeUsed()) });
/* 3008 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.CACHE_HIT_RATIO", 
/* 3009 */             Integer.toString(mVStore.getCacheHitRatio()) });
/* 3010 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.TOC_CACHE_HIT_RATIO", 
/* 3011 */             Integer.toString(mVStore.getTocCacheHitRatio()) });
/* 3012 */       add(paramSessionLocal, paramArrayList, new Object[] { "info.LEAF_RATIO", 
/* 3013 */             Integer.toString(mVStore.getLeafRatio()) });
/*      */     } 
/*      */   }
/*      */   
/*      */   private void synonyms(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString) {
/* 3018 */     for (TableSynonym tableSynonym : this.database.getAllSynonyms()) {
/* 3019 */       add(paramSessionLocal, paramArrayList, new Object[] { paramString, tableSynonym
/*      */ 
/*      */ 
/*      */             
/* 3023 */             .getSchema().getName(), tableSynonym
/*      */             
/* 3025 */             .getName(), tableSynonym
/*      */             
/* 3027 */             .getSynonymForName(), tableSynonym
/*      */             
/* 3029 */             .getSynonymForSchema().getName(), "SYNONYM", "VALID", tableSynonym
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 3035 */             .getComment() });
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void users(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 3041 */     User user = paramSessionLocal.getUser();
/* 3042 */     if (user.isAdmin()) {
/* 3043 */       for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) {
/* 3044 */         if (rightOwner instanceof User) {
/* 3045 */           users(paramSessionLocal, paramArrayList, (User)rightOwner);
/*      */         }
/*      */       } 
/*      */     } else {
/* 3049 */       users(paramSessionLocal, paramArrayList, user);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void users(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, User paramUser) {
/* 3054 */     add(paramSessionLocal, paramArrayList, new Object[] {
/*      */           
/* 3056 */           identifier(paramUser.getName()), 
/*      */           
/* 3058 */           ValueBoolean.get(paramUser.isAdmin()), paramUser
/*      */           
/* 3060 */           .getComment()
/*      */         });
/*      */   }
/*      */ 
/*      */   
/*      */   private void addConstraintColumnUsage(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, String paramString, Constraint paramConstraint, Column paramColumn) {
/* 3066 */     Table table = paramColumn.getTable();
/* 3067 */     add(paramSessionLocal, paramArrayList, new Object[] { paramString, table
/*      */ 
/*      */ 
/*      */           
/* 3071 */           .getSchema().getName(), table
/*      */           
/* 3073 */           .getName(), paramColumn
/*      */           
/* 3075 */           .getName(), paramString, paramConstraint
/*      */ 
/*      */ 
/*      */           
/* 3079 */           .getSchema().getName(), paramConstraint
/*      */           
/* 3081 */           .getName() });
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addPrivileges(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, DbObject paramDbObject, String paramString1, Table paramTable, String paramString2, int paramInt) {
/* 3087 */     if ((paramInt & 0x1) != 0) {
/* 3088 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "SELECT");
/*      */     }
/* 3090 */     if ((paramInt & 0x4) != 0) {
/* 3091 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "INSERT");
/*      */     }
/* 3093 */     if ((paramInt & 0x8) != 0) {
/* 3094 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "UPDATE");
/*      */     }
/* 3096 */     if ((paramInt & 0x2) != 0) {
/* 3097 */       addPrivilege(paramSessionLocal, paramArrayList, paramDbObject, paramString1, paramTable, paramString2, "DELETE");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void addPrivilege(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, DbObject paramDbObject, String paramString1, Table paramTable, String paramString2, String paramString3) {
/* 3103 */     String str = "NO";
/* 3104 */     if (paramDbObject.getType() == 2) {
/* 3105 */       User user = (User)paramDbObject;
/* 3106 */       if (user.isAdmin())
/*      */       {
/* 3108 */         str = "YES";
/*      */       }
/*      */     } 
/* 3111 */     if (paramString2 == null) {
/* 3112 */       add(paramSessionLocal, paramArrayList, new Object[] { null, 
/*      */ 
/*      */ 
/*      */             
/* 3116 */             identifier(paramDbObject.getName()), paramString1, paramTable
/*      */ 
/*      */ 
/*      */             
/* 3120 */             .getSchema().getName(), paramTable
/*      */             
/* 3122 */             .getName(), paramString3, str, "NO" });
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */ 
/*      */       
/* 3131 */       add(paramSessionLocal, paramArrayList, new Object[] { null, 
/*      */ 
/*      */ 
/*      */             
/* 3135 */             identifier(paramDbObject.getName()), paramString1, paramTable
/*      */ 
/*      */ 
/*      */             
/* 3139 */             .getSchema().getName(), paramTable
/*      */             
/* 3141 */             .getName(), paramString2, paramString3, str });
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
/*      */   public long getMaxDataModificationId() {
/* 3154 */     switch (this.type) {
/*      */       case 15:
/*      */       case 25:
/*      */       case 26:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/* 3161 */         return Long.MAX_VALUE;
/*      */     } 
/* 3163 */     return this.database.getModificationDataId();
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isView() {
/* 3168 */     return this.isView;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getRowCount(SessionLocal paramSessionLocal) {
/* 3173 */     return getRowCount(paramSessionLocal, false);
/*      */   }
/*      */ 
/*      */   
/*      */   public long getRowCountApproximation(SessionLocal paramSessionLocal) {
/* 3178 */     return getRowCount(paramSessionLocal, true);
/*      */   }
/*      */   private long getRowCount(SessionLocal paramSessionLocal, boolean paramBoolean) {
/*      */     Locale[] arrayOfLocale;
/* 3182 */     switch (this.type) {
/*      */       case 0:
/* 3184 */         return 1L;
/*      */       case 2:
/* 3186 */         arrayOfLocale = CompareMode.getCollationLocales(paramBoolean);
/* 3187 */         if (arrayOfLocale != null) {
/* 3188 */           return (arrayOfLocale.length + 1);
/*      */         }
/*      */         break;
/*      */       
/*      */       case 14:
/* 3193 */         return paramSessionLocal.getDatabase().getAllSchemas().size();
/*      */       case 25:
/* 3195 */         if (paramSessionLocal.getUser().isAdmin()) {
/* 3196 */           ArrayList arrayList = paramSessionLocal.getDatabase().getInDoubtTransactions();
/* 3197 */           if (arrayList != null) {
/* 3198 */             return arrayList.size();
/*      */           }
/*      */         } 
/* 3201 */         return 0L;
/*      */       case 29:
/* 3203 */         if (paramSessionLocal.getUser().isAdmin()) {
/* 3204 */           long l = 0L;
/* 3205 */           for (RightOwner rightOwner : paramSessionLocal.getDatabase().getAllUsersAndRoles()) {
/* 3206 */             if (rightOwner instanceof Role) {
/* 3207 */               l++;
/*      */             }
/*      */           } 
/* 3210 */           return l;
/*      */         } 
/*      */         break;
/*      */       case 30:
/* 3214 */         if (paramSessionLocal.getUser().isAdmin()) {
/* 3215 */           return paramSessionLocal.getDatabase().getSessionCount();
/*      */         }
/* 3217 */         return 1L;
/*      */       
/*      */       case 34:
/* 3220 */         if (paramSessionLocal.getUser().isAdmin()) {
/* 3221 */           long l = 0L;
/* 3222 */           for (RightOwner rightOwner : paramSessionLocal.getDatabase().getAllUsersAndRoles()) {
/* 3223 */             if (rightOwner instanceof User) {
/* 3224 */               l++;
/*      */             }
/*      */           } 
/* 3227 */           return l;
/*      */         } 
/* 3229 */         return 1L;
/*      */     } 
/*      */     
/* 3232 */     if (paramBoolean) {
/* 3233 */       return 1000L;
/*      */     }
/* 3235 */     throw DbException.getInternalError(toString());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canGetRowCount(SessionLocal paramSessionLocal) {
/* 3240 */     switch (this.type) {
/*      */       case 0:
/*      */       case 2:
/*      */       case 14:
/*      */       case 25:
/*      */       case 30:
/*      */       case 34:
/* 3247 */         return true;
/*      */       case 29:
/* 3249 */         if (paramSessionLocal.getUser().isAdmin()) {
/* 3250 */           return true;
/*      */         }
/*      */         break;
/*      */     } 
/* 3254 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static final class DataTypeInformation
/*      */   {
/* 3262 */     static final DataTypeInformation NULL = new DataTypeInformation(null, null, null, null, null, null, null, null, null, false, null, null, null, null, null);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final String dataType;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value characterPrecision;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value numericPrecision;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value numericPrecisionRadix;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value numericScale;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value datetimePrecision;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value intervalPrecision;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     final Value intervalType;
/*      */ 
/*      */ 
/*      */     
/*      */     final Value maximumCardinality;
/*      */ 
/*      */ 
/*      */     
/*      */     final boolean hasCharsetAndCollation;
/*      */ 
/*      */ 
/*      */     
/*      */     final String declaredDataType;
/*      */ 
/*      */ 
/*      */     
/*      */     final Value declaredNumericPrecision;
/*      */ 
/*      */ 
/*      */     
/*      */     final Value declaredNumericScale;
/*      */ 
/*      */ 
/*      */     
/*      */     final String geometryType;
/*      */ 
/*      */ 
/*      */     
/*      */     final Value geometrySrid;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static DataTypeInformation valueOf(TypeInfo param1TypeInfo) {
/*      */       long l;
/*      */       ExtTypeInfoGeometry extTypeInfoGeometry;
/* 3338 */       int i = param1TypeInfo.getValueType();
/* 3339 */       String str1 = Value.getTypeName(i);
/* 3340 */       ValueBigint valueBigint = null;
/* 3341 */       ValueInteger valueInteger1 = null, valueInteger2 = null, valueInteger3 = null;
/* 3342 */       ValueInteger valueInteger4 = null, valueInteger5 = null, valueInteger6 = null;
/* 3343 */       String str2 = null;
/* 3344 */       boolean bool = false;
/* 3345 */       String str3 = null;
/* 3346 */       ValueInteger valueInteger7 = null, valueInteger8 = null;
/* 3347 */       String str4 = null;
/* 3348 */       ValueInteger valueInteger9 = null;
/* 3349 */       switch (i) {
/*      */         case 1:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/* 3354 */           bool = true;
/*      */         
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 35:
/*      */         case 38:
/* 3361 */           valueBigint = ValueBigint.get(param1TypeInfo.getPrecision());
/*      */           break;
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/* 3367 */           valueInteger1 = ValueInteger.get(MathUtils.convertLongToInt(param1TypeInfo.getPrecision()));
/* 3368 */           valueInteger2 = ValueInteger.get(0);
/* 3369 */           valueInteger3 = ValueInteger.get(2);
/* 3370 */           str3 = str1;
/*      */           break;
/*      */         case 13:
/* 3373 */           valueInteger1 = ValueInteger.get(MathUtils.convertLongToInt(param1TypeInfo.getPrecision()));
/* 3374 */           valueInteger2 = ValueInteger.get(param1TypeInfo.getScale());
/* 3375 */           valueInteger3 = ValueInteger.get(10);
/* 3376 */           str3 = (param1TypeInfo.getExtTypeInfo() != null) ? "DECIMAL" : "NUMERIC";
/* 3377 */           if (param1TypeInfo.getDeclaredPrecision() >= 0L) {
/* 3378 */             valueInteger7 = valueInteger1;
/*      */           }
/* 3380 */           if (param1TypeInfo.getDeclaredScale() >= 0) {
/* 3381 */             valueInteger8 = valueInteger2;
/*      */           }
/*      */           break;
/*      */         
/*      */         case 14:
/*      */         case 15:
/* 3387 */           valueInteger1 = ValueInteger.get(MathUtils.convertLongToInt(param1TypeInfo.getPrecision()));
/* 3388 */           valueInteger3 = ValueInteger.get(2);
/* 3389 */           l = param1TypeInfo.getDeclaredPrecision();
/* 3390 */           if (l >= 0L) {
/* 3391 */             str3 = "FLOAT";
/* 3392 */             if (l > 0L)
/* 3393 */               valueInteger7 = ValueInteger.get((int)l); 
/*      */             break;
/*      */           } 
/* 3396 */           str3 = str1;
/*      */           break;
/*      */ 
/*      */         
/*      */         case 16:
/* 3401 */           valueInteger1 = ValueInteger.get(MathUtils.convertLongToInt(param1TypeInfo.getPrecision()));
/* 3402 */           valueInteger3 = ValueInteger.get(10);
/* 3403 */           str3 = str1;
/* 3404 */           if (param1TypeInfo.getDeclaredPrecision() >= 0L) {
/* 3405 */             valueInteger7 = valueInteger1;
/*      */           }
/*      */           break;
/*      */         case 22:
/*      */         case 23:
/*      */         case 24:
/*      */         case 25:
/*      */         case 26:
/*      */         case 27:
/*      */         case 28:
/*      */         case 29:
/*      */         case 30:
/*      */         case 31:
/*      */         case 32:
/*      */         case 33:
/*      */         case 34:
/* 3421 */           str2 = IntervalQualifier.valueOf(i - 22).toString();
/* 3422 */           str1 = "INTERVAL";
/* 3423 */           valueInteger5 = ValueInteger.get(MathUtils.convertLongToInt(param1TypeInfo.getPrecision()));
/*      */         
/*      */         case 17:
/*      */         case 18:
/*      */         case 19:
/*      */         case 20:
/*      */         case 21:
/* 3430 */           valueInteger4 = ValueInteger.get(param1TypeInfo.getScale());
/*      */           break;
/*      */         case 37:
/* 3433 */           extTypeInfoGeometry = (ExtTypeInfoGeometry)param1TypeInfo.getExtTypeInfo();
/* 3434 */           if (extTypeInfoGeometry != null) {
/* 3435 */             int j = extTypeInfoGeometry.getType();
/* 3436 */             if (j != 0)
/*      */             {
/* 3438 */               str4 = EWKTUtils.formatGeometryTypeAndDimensionSystem(new StringBuilder(), j).toString();
/*      */             }
/* 3440 */             Integer integer = extTypeInfoGeometry.getSrid();
/* 3441 */             if (integer != null) {
/* 3442 */               valueInteger9 = ValueInteger.get(integer.intValue());
/*      */             }
/*      */           } 
/*      */           break;
/*      */         
/*      */         case 40:
/* 3448 */           valueInteger6 = ValueInteger.get(MathUtils.convertLongToInt(param1TypeInfo.getPrecision())); break;
/*      */       } 
/* 3450 */       return new DataTypeInformation(str1, (Value)valueBigint, (Value)valueInteger1, (Value)valueInteger3, (Value)valueInteger2, (Value)valueInteger4, (Value)valueInteger5, (str2 != null) ? 
/*      */           
/* 3452 */           ValueVarchar.get(str2) : (Value)ValueNull.INSTANCE, (Value)valueInteger6, bool, str3, (Value)valueInteger7, (Value)valueInteger8, str4, (Value)valueInteger9);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private DataTypeInformation(String param1String1, Value param1Value1, Value param1Value2, Value param1Value3, Value param1Value4, Value param1Value5, Value param1Value6, Value param1Value7, Value param1Value8, boolean param1Boolean, String param1String2, Value param1Value9, Value param1Value10, String param1String3, Value param1Value11) {
/* 3461 */       this.dataType = param1String1;
/* 3462 */       this.characterPrecision = param1Value1;
/* 3463 */       this.numericPrecision = param1Value2;
/* 3464 */       this.numericPrecisionRadix = param1Value3;
/* 3465 */       this.numericScale = param1Value4;
/* 3466 */       this.datetimePrecision = param1Value5;
/* 3467 */       this.intervalPrecision = param1Value6;
/* 3468 */       this.intervalType = param1Value7;
/* 3469 */       this.maximumCardinality = param1Value8;
/* 3470 */       this.hasCharsetAndCollation = param1Boolean;
/* 3471 */       this.declaredDataType = param1String2;
/* 3472 */       this.declaredNumericPrecision = param1Value9;
/* 3473 */       this.declaredNumericScale = param1Value10;
/* 3474 */       this.geometryType = param1String3;
/* 3475 */       this.geometrySrid = param1Value11;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\table\InformationSchemaTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */