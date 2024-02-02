package org.h2.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.h2.api.IntervalQualifier;
import org.h2.command.Command;
import org.h2.command.Parser;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintCheck;
import org.h2.constraint.ConstraintDomain;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Constants;
import org.h2.engine.DbObject;
import org.h2.engine.QueryStatisticsData;
import org.h2.engine.Right;
import org.h2.engine.RightOwner;
import org.h2.engine.Role;
import org.h2.engine.SessionLocal;
import org.h2.engine.Setting;
import org.h2.engine.User;
import org.h2.expression.ExpressionVisitor;
import org.h2.expression.ValueExpression;
import org.h2.index.Index;
import org.h2.index.MetaIndex;
import org.h2.message.DbException;
import org.h2.mvstore.FileStore;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.db.Store;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Constant;
import org.h2.schema.Domain;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.store.InDoubtTransaction;
import org.h2.util.DateTimeUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.util.geometry.EWKTUtils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.ExtTypeInfoEnum;
import org.h2.value.ExtTypeInfoGeometry;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueToObjectConverter2;
import org.h2.value.ValueVarchar;

public final class InformationSchemaTable extends MetaTable {
   private static final String CHARACTER_SET_NAME = "Unicode";
   private static final int INFORMATION_SCHEMA_CATALOG_NAME = 0;
   private static final int CHECK_CONSTRAINTS = 1;
   private static final int COLLATIONS = 2;
   private static final int COLUMNS = 3;
   private static final int COLUMN_PRIVILEGES = 4;
   private static final int CONSTRAINT_COLUMN_USAGE = 5;
   private static final int DOMAINS = 6;
   private static final int DOMAIN_CONSTRAINTS = 7;
   private static final int ELEMENT_TYPES = 8;
   private static final int FIELDS = 9;
   private static final int KEY_COLUMN_USAGE = 10;
   private static final int PARAMETERS = 11;
   private static final int REFERENTIAL_CONSTRAINTS = 12;
   private static final int ROUTINES = 13;
   private static final int SCHEMATA = 14;
   private static final int SEQUENCES = 15;
   private static final int TABLES = 16;
   private static final int TABLE_CONSTRAINTS = 17;
   private static final int TABLE_PRIVILEGES = 18;
   private static final int TRIGGERS = 19;
   private static final int VIEWS = 20;
   private static final int CONSTANTS = 21;
   private static final int ENUM_VALUES = 22;
   private static final int INDEXES = 23;
   private static final int INDEX_COLUMNS = 24;
   private static final int IN_DOUBT = 25;
   private static final int LOCKS = 26;
   private static final int QUERY_STATISTICS = 27;
   private static final int RIGHTS = 28;
   private static final int ROLES = 29;
   private static final int SESSIONS = 30;
   private static final int SESSION_STATE = 31;
   private static final int SETTINGS = 32;
   private static final int SYNONYMS = 33;
   private static final int USERS = 34;
   public static final int META_TABLE_TYPE_COUNT = 35;
   private final boolean isView;

   public InformationSchemaTable(Schema var1, int var2, int var3) {
      super(var1, var2, var3);
      String var5 = null;
      boolean var6 = true;
      Column[] var4;
      switch (var3) {
         case 0:
            this.setMetaTableName("INFORMATION_SCHEMA_CATALOG_NAME");
            var6 = false;
            var4 = new Column[]{this.column("CATALOG_NAME")};
            break;
         case 1:
            this.setMetaTableName("CHECK_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("CHECK_CLAUSE")};
            var5 = "CONSTRAINT_NAME";
            break;
         case 2:
            this.setMetaTableName("COLLATIONS");
            var4 = new Column[]{this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("PAD_ATTRIBUTE"), this.column("LANGUAGE_TAG")};
            break;
         case 3:
            this.setMetaTableName("COLUMNS");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("COLUMN_DEFAULT"), this.column("IS_NULLABLE"), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("DOMAIN_CATALOG"), this.column("DOMAIN_SCHEMA"), this.column("DOMAIN_NAME"), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("IS_IDENTITY"), this.column("IDENTITY_GENERATION"), this.column("IDENTITY_START", TypeInfo.TYPE_BIGINT), this.column("IDENTITY_INCREMENT", TypeInfo.TYPE_BIGINT), this.column("IDENTITY_MAXIMUM", TypeInfo.TYPE_BIGINT), this.column("IDENTITY_MINIMUM", TypeInfo.TYPE_BIGINT), this.column("IDENTITY_CYCLE"), this.column("IS_GENERATED"), this.column("GENERATION_EXPRESSION"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), this.column("IDENTITY_BASE", TypeInfo.TYPE_BIGINT), this.column("IDENTITY_CACHE", TypeInfo.TYPE_BIGINT), this.column("COLUMN_ON_UPDATE"), this.column("IS_VISIBLE", TypeInfo.TYPE_BOOLEAN), this.column("DEFAULT_ON_NULL", TypeInfo.TYPE_BOOLEAN), this.column("SELECTIVITY", TypeInfo.TYPE_INTEGER), this.column("REMARKS")};
            var5 = "TABLE_NAME";
            break;
         case 4:
            this.setMetaTableName("COLUMN_PRIVILEGES");
            var4 = new Column[]{this.column("GRANTOR"), this.column("GRANTEE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("PRIVILEGE_TYPE"), this.column("IS_GRANTABLE")};
            var5 = "TABLE_NAME";
            break;
         case 5:
            this.setMetaTableName("CONSTRAINT_COLUMN_USAGE");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME")};
            var5 = "TABLE_NAME";
            break;
         case 6:
            this.setMetaTableName("DOMAINS");
            var4 = new Column[]{this.column("DOMAIN_CATALOG"), this.column("DOMAIN_SCHEMA"), this.column("DOMAIN_NAME"), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DOMAIN_DEFAULT"), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), this.column("DOMAIN_ON_UPDATE"), this.column("PARENT_DOMAIN_CATALOG"), this.column("PARENT_DOMAIN_SCHEMA"), this.column("PARENT_DOMAIN_NAME"), this.column("REMARKS")};
            var5 = "DOMAIN_NAME";
            break;
         case 7:
            this.setMetaTableName("DOMAIN_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("DOMAIN_CATALOG"), this.column("DOMAIN_SCHEMA"), this.column("DOMAIN_NAME"), this.column("IS_DEFERRABLE"), this.column("INITIALLY_DEFERRED"), this.column("REMARKS")};
            var5 = "DOMAIN_NAME";
            break;
         case 8:
            this.setMetaTableName("ELEMENT_TYPES");
            var4 = new Column[]{this.column("OBJECT_CATALOG"), this.column("OBJECT_SCHEMA"), this.column("OBJECT_NAME"), this.column("OBJECT_TYPE"), this.column("COLLECTION_TYPE_IDENTIFIER"), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER)};
            break;
         case 9:
            this.setMetaTableName("FIELDS");
            var4 = new Column[]{this.column("OBJECT_CATALOG"), this.column("OBJECT_SCHEMA"), this.column("OBJECT_NAME"), this.column("OBJECT_TYPE"), this.column("ROW_IDENTIFIER"), this.column("FIELD_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER)};
            break;
         case 10:
            this.setMetaTableName("KEY_COLUMN_USAGE");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("POSITION_IN_UNIQUE_CONSTRAINT", TypeInfo.TYPE_INTEGER)};
            var5 = "TABLE_NAME";
            break;
         case 11:
            this.setMetaTableName("PARAMETERS");
            var4 = new Column[]{this.column("SPECIFIC_CATALOG"), this.column("SPECIFIC_SCHEMA"), this.column("SPECIFIC_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("PARAMETER_MODE"), this.column("IS_RESULT"), this.column("AS_LOCATOR"), this.column("PARAMETER_NAME"), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("PARAMETER_DEFAULT"), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER)};
            break;
         case 12:
            this.setMetaTableName("REFERENTIAL_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("UNIQUE_CONSTRAINT_CATALOG"), this.column("UNIQUE_CONSTRAINT_SCHEMA"), this.column("UNIQUE_CONSTRAINT_NAME"), this.column("MATCH_OPTION"), this.column("UPDATE_RULE"), this.column("DELETE_RULE")};
            var5 = "CONSTRAINT_NAME";
            break;
         case 13:
            this.setMetaTableName("ROUTINES");
            var4 = new Column[]{this.column("SPECIFIC_CATALOG"), this.column("SPECIFIC_SCHEMA"), this.column("SPECIFIC_NAME"), this.column("ROUTINE_CATALOG"), this.column("ROUTINE_SCHEMA"), this.column("ROUTINE_NAME"), this.column("ROUTINE_TYPE"), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("ROUTINE_BODY"), this.column("ROUTINE_DEFINITION"), this.column("EXTERNAL_NAME"), this.column("EXTERNAL_LANGUAGE"), this.column("PARAMETER_STYLE"), this.column("IS_DETERMINISTIC"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), this.column("REMARKS")};
            break;
         case 14:
            this.setMetaTableName("SCHEMATA");
            var4 = new Column[]{this.column("CATALOG_NAME"), this.column("SCHEMA_NAME"), this.column("SCHEMA_OWNER"), this.column("DEFAULT_CHARACTER_SET_CATALOG"), this.column("DEFAULT_CHARACTER_SET_SCHEMA"), this.column("DEFAULT_CHARACTER_SET_NAME"), this.column("SQL_PATH"), this.column("DEFAULT_COLLATION_NAME"), this.column("REMARKS")};
            break;
         case 15:
            this.setMetaTableName("SEQUENCES");
            var4 = new Column[]{this.column("SEQUENCE_CATALOG"), this.column("SEQUENCE_SCHEMA"), this.column("SEQUENCE_NAME"), this.column("DATA_TYPE"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("START_VALUE", TypeInfo.TYPE_BIGINT), this.column("MINIMUM_VALUE", TypeInfo.TYPE_BIGINT), this.column("MAXIMUM_VALUE", TypeInfo.TYPE_BIGINT), this.column("INCREMENT", TypeInfo.TYPE_BIGINT), this.column("CYCLE_OPTION"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("BASE_VALUE", TypeInfo.TYPE_BIGINT), this.column("CACHE", TypeInfo.TYPE_BIGINT), this.column("REMARKS")};
            var5 = "SEQUENCE_NAME";
            break;
         case 16:
            this.setMetaTableName("TABLES");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("TABLE_TYPE"), this.column("IS_INSERTABLE_INTO"), this.column("COMMIT_ACTION"), this.column("STORAGE_TYPE"), this.column("REMARKS"), this.column("LAST_MODIFICATION", TypeInfo.TYPE_BIGINT), this.column("TABLE_CLASS"), this.column("ROW_COUNT_ESTIMATE", TypeInfo.TYPE_BIGINT)};
            var5 = "TABLE_NAME";
            break;
         case 17:
            this.setMetaTableName("TABLE_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("CONSTRAINT_TYPE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("IS_DEFERRABLE"), this.column("INITIALLY_DEFERRED"), this.column("ENFORCED"), this.column("INDEX_CATALOG"), this.column("INDEX_SCHEMA"), this.column("INDEX_NAME"), this.column("REMARKS")};
            var5 = "TABLE_NAME";
            break;
         case 18:
            this.setMetaTableName("TABLE_PRIVILEGES");
            var4 = new Column[]{this.column("GRANTOR"), this.column("GRANTEE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("PRIVILEGE_TYPE"), this.column("IS_GRANTABLE"), this.column("WITH_HIERARCHY")};
            var5 = "TABLE_NAME";
            break;
         case 19:
            this.setMetaTableName("TRIGGERS");
            var4 = new Column[]{this.column("TRIGGER_CATALOG"), this.column("TRIGGER_SCHEMA"), this.column("TRIGGER_NAME"), this.column("EVENT_MANIPULATION"), this.column("EVENT_OBJECT_CATALOG"), this.column("EVENT_OBJECT_SCHEMA"), this.column("EVENT_OBJECT_TABLE"), this.column("ACTION_ORIENTATION"), this.column("ACTION_TIMING"), this.column("IS_ROLLBACK", TypeInfo.TYPE_BOOLEAN), this.column("JAVA_CLASS"), this.column("QUEUE_SIZE", TypeInfo.TYPE_INTEGER), this.column("NO_WAIT", TypeInfo.TYPE_BOOLEAN), this.column("REMARKS")};
            var5 = "EVENT_OBJECT_TABLE";
            break;
         case 20:
            this.setMetaTableName("VIEWS");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("VIEW_DEFINITION"), this.column("CHECK_OPTION"), this.column("IS_UPDATABLE"), this.column("INSERTABLE_INTO"), this.column("IS_TRIGGER_UPDATABLE"), this.column("IS_TRIGGER_DELETABLE"), this.column("IS_TRIGGER_INSERTABLE_INTO"), this.column("STATUS"), this.column("REMARKS")};
            var5 = "TABLE_NAME";
            break;
         case 21:
            this.setMetaTableName("CONSTANTS");
            var6 = false;
            var4 = new Column[]{this.column("CONSTANT_CATALOG"), this.column("CONSTANT_SCHEMA"), this.column("CONSTANT_NAME"), this.column("VALUE_DEFINITION"), this.column("DATA_TYPE"), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_BIGINT), this.column("CHARACTER_SET_CATALOG"), this.column("CHARACTER_SET_SCHEMA"), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_CATALOG"), this.column("COLLATION_SCHEMA"), this.column("COLLATION_NAME"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("MAXIMUM_CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("DTD_IDENTIFIER"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("GEOMETRY_TYPE"), this.column("GEOMETRY_SRID", TypeInfo.TYPE_INTEGER), this.column("REMARKS")};
            var5 = "CONSTANT_NAME";
            break;
         case 22:
            this.setMetaTableName("ENUM_VALUES");
            var6 = false;
            var4 = new Column[]{this.column("OBJECT_CATALOG"), this.column("OBJECT_SCHEMA"), this.column("OBJECT_NAME"), this.column("OBJECT_TYPE"), this.column("ENUM_IDENTIFIER"), this.column("VALUE_NAME"), this.column("VALUE_ORDINAL")};
            break;
         case 23:
            this.setMetaTableName("INDEXES");
            var6 = false;
            var4 = new Column[]{this.column("INDEX_CATALOG"), this.column("INDEX_SCHEMA"), this.column("INDEX_NAME"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("INDEX_TYPE_NAME"), this.column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), this.column("REMARKS"), this.column("INDEX_CLASS")};
            var5 = "TABLE_NAME";
            break;
         case 24:
            this.setMetaTableName("INDEX_COLUMNS");
            var6 = false;
            var4 = new Column[]{this.column("INDEX_CATALOG"), this.column("INDEX_SCHEMA"), this.column("INDEX_NAME"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("ORDERING_SPECIFICATION"), this.column("NULL_ORDERING"), this.column("IS_UNIQUE", TypeInfo.TYPE_BOOLEAN)};
            var5 = "TABLE_NAME";
            break;
         case 25:
            this.setMetaTableName("IN_DOUBT");
            var6 = false;
            var4 = new Column[]{this.column("TRANSACTION_NAME"), this.column("TRANSACTION_STATE")};
            break;
         case 26:
            this.setMetaTableName("LOCKS");
            var6 = false;
            var4 = new Column[]{this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("SESSION_ID", TypeInfo.TYPE_INTEGER), this.column("LOCK_TYPE")};
            break;
         case 27:
            this.setMetaTableName("QUERY_STATISTICS");
            var6 = false;
            var4 = new Column[]{this.column("SQL_STATEMENT"), this.column("EXECUTION_COUNT", TypeInfo.TYPE_INTEGER), this.column("MIN_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("MAX_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("CUMULATIVE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("AVERAGE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("STD_DEV_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("MIN_ROW_COUNT", TypeInfo.TYPE_BIGINT), this.column("MAX_ROW_COUNT", TypeInfo.TYPE_BIGINT), this.column("CUMULATIVE_ROW_COUNT", TypeInfo.TYPE_BIGINT), this.column("AVERAGE_ROW_COUNT", TypeInfo.TYPE_DOUBLE), this.column("STD_DEV_ROW_COUNT", TypeInfo.TYPE_DOUBLE)};
            break;
         case 28:
            this.setMetaTableName("RIGHTS");
            var6 = false;
            var4 = new Column[]{this.column("GRANTEE"), this.column("GRANTEETYPE"), this.column("GRANTEDROLE"), this.column("RIGHTS"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME")};
            var5 = "TABLE_NAME";
            break;
         case 29:
            this.setMetaTableName("ROLES");
            var6 = false;
            var4 = new Column[]{this.column("ROLE_NAME"), this.column("REMARKS")};
            break;
         case 30:
            this.setMetaTableName("SESSIONS");
            var6 = false;
            var4 = new Column[]{this.column("SESSION_ID", TypeInfo.TYPE_INTEGER), this.column("USER_NAME"), this.column("SERVER"), this.column("CLIENT_ADDR"), this.column("CLIENT_INFO"), this.column("SESSION_START", TypeInfo.TYPE_TIMESTAMP_TZ), this.column("ISOLATION_LEVEL"), this.column("EXECUTING_STATEMENT"), this.column("EXECUTING_STATEMENT_START", TypeInfo.TYPE_TIMESTAMP_TZ), this.column("CONTAINS_UNCOMMITTED", TypeInfo.TYPE_BOOLEAN), this.column("SESSION_STATE"), this.column("BLOCKER_ID", TypeInfo.TYPE_INTEGER), this.column("SLEEP_SINCE", TypeInfo.TYPE_TIMESTAMP_TZ)};
            break;
         case 31:
            this.setMetaTableName("SESSION_STATE");
            var6 = false;
            var4 = new Column[]{this.column("STATE_KEY"), this.column("STATE_COMMAND")};
            break;
         case 32:
            this.setMetaTableName("SETTINGS");
            var6 = false;
            var4 = new Column[]{this.column("SETTING_NAME"), this.column("SETTING_VALUE")};
            break;
         case 33:
            this.setMetaTableName("SYNONYMS");
            var6 = false;
            var4 = new Column[]{this.column("SYNONYM_CATALOG"), this.column("SYNONYM_SCHEMA"), this.column("SYNONYM_NAME"), this.column("SYNONYM_FOR"), this.column("SYNONYM_FOR_SCHEMA"), this.column("TYPE_NAME"), this.column("STATUS"), this.column("REMARKS")};
            var5 = "SYNONYM_NAME";
            break;
         case 34:
            this.setMetaTableName("USERS");
            var6 = false;
            var4 = new Column[]{this.column("USER_NAME"), this.column("IS_ADMIN", TypeInfo.TYPE_BOOLEAN), this.column("REMARKS")};
            break;
         default:
            throw DbException.getInternalError("type=" + var3);
      }

      this.setColumns(var4);
      if (var5 == null) {
         this.indexColumn = -1;
         this.metaIndex = null;
      } else {
         this.indexColumn = this.getColumn(this.database.sysIdentifier(var5)).getColumnId();
         IndexColumn[] var7 = IndexColumn.wrap(new Column[]{var4[this.indexColumn]});
         this.metaIndex = new MetaIndex(this, var7, false);
      }

      this.isView = var6;
   }

   public ArrayList<Row> generateRows(SessionLocal var1, SearchRow var2, SearchRow var3) {
      Value var4 = null;
      Value var5 = null;
      if (this.indexColumn >= 0) {
         if (var2 != null) {
            var4 = var2.getValue(this.indexColumn);
         }

         if (var3 != null) {
            var5 = var3.getValue(this.indexColumn);
         }
      }

      ArrayList var6 = Utils.newSmallArrayList();
      String var7 = this.database.getShortName();
      switch (this.type) {
         case 0:
            this.informationSchemaCatalogName(var1, var6, var7);
            break;
         case 1:
            this.checkConstraints(var1, var4, var5, var6, var7);
            break;
         case 2:
            this.collations(var1, var6, var7);
            break;
         case 3:
            this.columns(var1, var4, var5, var6, var7);
            break;
         case 4:
            this.columnPrivileges(var1, var4, var5, var6, var7);
            break;
         case 5:
            this.constraintColumnUsage(var1, var4, var5, var6, var7);
            break;
         case 6:
            this.domains(var1, var4, var5, var6, var7);
            break;
         case 7:
            this.domainConstraints(var1, var4, var5, var6, var7);
            break;
         case 8:
            this.elementTypesFields(var1, var6, var7, 8);
            break;
         case 9:
            this.elementTypesFields(var1, var6, var7, 9);
            break;
         case 10:
            this.keyColumnUsage(var1, var4, var5, var6, var7);
            break;
         case 11:
            this.parameters(var1, var6, var7);
            break;
         case 12:
            this.referentialConstraints(var1, var4, var5, var6, var7);
            break;
         case 13:
            this.routines(var1, var6, var7);
            break;
         case 14:
            this.schemata(var1, var6, var7);
            break;
         case 15:
            this.sequences(var1, var4, var5, var6, var7);
            break;
         case 16:
            this.tables(var1, var4, var5, var6, var7);
            break;
         case 17:
            this.tableConstraints(var1, var4, var5, var6, var7);
            break;
         case 18:
            this.tablePrivileges(var1, var4, var5, var6, var7);
            break;
         case 19:
            this.triggers(var1, var4, var5, var6, var7);
            break;
         case 20:
            this.views(var1, var4, var5, var6, var7);
            break;
         case 21:
            this.constants(var1, var4, var5, var6, var7);
            break;
         case 22:
            this.elementTypesFields(var1, var6, var7, 22);
            break;
         case 23:
            this.indexes(var1, var4, var5, var6, var7, false);
            break;
         case 24:
            this.indexes(var1, var4, var5, var6, var7, true);
            break;
         case 25:
            this.inDoubt(var1, var6);
            break;
         case 26:
            this.locks(var1, var6);
            break;
         case 27:
            this.queryStatistics(var1, var6);
            break;
         case 28:
            this.rights(var1, var4, var5, var6);
            break;
         case 29:
            this.roles(var1, var6);
            break;
         case 30:
            this.sessions(var1, var6);
            break;
         case 31:
            this.sessionState(var1, var6);
            break;
         case 32:
            this.settings(var1, var6);
            break;
         case 33:
            this.synonyms(var1, var6, var7);
            break;
         case 34:
            this.users(var1, var6);
            break;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      return var6;
   }

   private void informationSchemaCatalogName(SessionLocal var1, ArrayList<Row> var2, String var3) {
      this.add(var1, var2, new Object[]{var3});
   }

   private void checkConstraints(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      label34:
      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllConstraints().iterator();

         while(true) {
            Constraint var9;
            while(true) {
               if (!var8.hasNext()) {
                  continue label34;
               }

               var9 = (Constraint)var8.next();
               Constraint.Type var10 = var9.getConstraintType();
               if (var10 == Constraint.Type.CHECK) {
                  ConstraintCheck var11 = (ConstraintCheck)var9;
                  Table var12 = var11.getTable();
                  if (this.hideTable(var12, var1)) {
                     continue;
                  }
               } else if (var10 != Constraint.Type.DOMAIN) {
                  continue;
               }
               break;
            }

            String var13 = var9.getName();
            if (this.checkIndex(var1, var13, var2, var3)) {
               this.checkConstraints(var1, var4, var5, var9, var13);
            }
         }
      }

   }

   private void checkConstraints(SessionLocal var1, ArrayList<Row> var2, String var3, Constraint var4, String var5) {
      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var5, var4.getExpression().getSQL(0, 2)});
   }

   private void collations(SessionLocal var1, ArrayList<Row> var2, String var3) {
      String var4 = this.database.getMainSchema().getName();
      this.collations(var1, var2, var3, var4, "OFF", (String)null);
      Locale[] var5 = CompareMode.getCollationLocales(false);
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Locale var8 = var5[var7];
         this.collations(var1, var2, var3, var4, CompareMode.getName(var8), var8.toLanguageTag());
      }

   }

   private void collations(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, String var6) {
      if ("und".equals(var6)) {
         var6 = null;
      }

      this.add(var1, var2, new Object[]{var3, var4, var5, "NO PAD", var6});
   }

   private void columns(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      String var6 = this.database.getMainSchema().getName();
      String var7 = this.database.getCompareMode().getName();
      Table var11;
      Table var14;
      if (var2 != null && var2.equals(var3)) {
         String var13 = var2.getString();
         if (var13 == null) {
            return;
         }

         Iterator var15 = this.database.getAllSchemas().iterator();

         while(var15.hasNext()) {
            Schema var17 = (Schema)var15.next();
            var11 = var17.getTableOrViewByName(var1, var13);
            if (var11 != null) {
               this.columns(var1, var4, var5, var6, var7, var11, var11.getName());
            }
         }

         var14 = var1.findLocalTempTable(var13);
         if (var14 != null) {
            this.columns(var1, var4, var5, var6, var7, var14, var14.getName());
         }
      } else {
         Iterator var8 = this.database.getAllSchemas().iterator();

         while(var8.hasNext()) {
            Schema var9 = (Schema)var8.next();
            Iterator var10 = var9.getAllTablesAndViews(var1).iterator();

            while(var10.hasNext()) {
               var11 = (Table)var10.next();
               String var12 = var11.getName();
               if (this.checkIndex(var1, var12, var2, var3)) {
                  this.columns(var1, var4, var5, var6, var7, var11, var12);
               }
            }
         }

         var8 = var1.getLocalTempTables().iterator();

         while(var8.hasNext()) {
            var14 = (Table)var8.next();
            String var16 = var14.getName();
            if (this.checkIndex(var1, var16, var2, var3)) {
               this.columns(var1, var4, var5, var6, var7, var14, var16);
            }
         }
      }

   }

   private void columns(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, Table var6, String var7) {
      if (!this.hideTable(var6, var1)) {
         Column[] var8 = var6.getColumns();
         int var9 = 0;
         int var10 = var8.length;

         while(var9 < var10) {
            Column var10008 = var8[var9];
            ++var9;
            this.columns(var1, var2, var3, var4, var5, var6, var7, var10008, var9);
         }

      }
   }

   private void columns(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, Table var6, String var7, Column var8, int var9) {
      TypeInfo var10 = var8.getType();
      DataTypeInformation var11 = InformationSchemaTable.DataTypeInformation.valueOf(var10);
      String var12;
      String var13;
      String var14;
      String var15;
      if (var11.hasCharsetAndCollation) {
         var12 = var3;
         var13 = var4;
         var14 = "Unicode";
         var15 = var5;
      } else {
         var15 = null;
         var14 = null;
         var13 = null;
         var12 = null;
      }

      Domain var16 = var8.getDomain();
      String var17 = null;
      String var18 = null;
      String var19 = null;
      if (var16 != null) {
         var17 = var3;
         var18 = var16.getSchema().getName();
         var19 = var16.getName();
      }

      Sequence var32 = var8.getSequence();
      String var20;
      String var21;
      String var22;
      String var23;
      String var24;
      String var25;
      ValueBigint var26;
      ValueBigint var27;
      ValueBigint var28;
      ValueBigint var29;
      ValueBigint var30;
      ValueBigint var31;
      if (var32 != null) {
         var20 = null;
         var21 = "NEVER";
         var22 = null;
         var23 = "YES";
         var24 = var8.isGeneratedAlways() ? "ALWAYS" : "BY DEFAULT";
         var26 = ValueBigint.get(var32.getStartValue());
         var27 = ValueBigint.get(var32.getIncrement());
         var28 = ValueBigint.get(var32.getMaxValue());
         var29 = ValueBigint.get(var32.getMinValue());
         Sequence.Cycle var33 = var32.getCycle();
         var25 = var33.isCycle() ? "YES" : "NO";
         var30 = var33 != Sequence.Cycle.EXHAUSTED ? ValueBigint.get(var32.getBaseValue()) : null;
         var31 = ValueBigint.get(var32.getCacheSize());
      } else {
         if (var8.isGenerated()) {
            var20 = null;
            var21 = "ALWAYS";
            var22 = var8.getDefaultSQL();
         } else {
            var20 = var8.getDefaultSQL();
            var21 = "NEVER";
            var22 = null;
         }

         var23 = "NO";
         var25 = null;
         var24 = null;
         var31 = null;
         var30 = null;
         var29 = null;
         var28 = null;
         var27 = null;
         var26 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6.getSchema().getName(), var7, var8.getName(), ValueInteger.get(var9), var20, var8.isNullable() ? "YES" : "NO", this.identifier(var11.dataType), var11.characterPrecision, var11.characterPrecision, var11.numericPrecision, var11.numericPrecisionRadix, var11.numericScale, var11.datetimePrecision, var11.intervalType, var11.intervalPrecision, var12, var13, var14, var12, var13, var15, var17, var18, var19, var11.maximumCardinality, Integer.toString(var9), var23, var24, var26, var27, var28, var29, var25, var21, var22, var11.declaredDataType, var11.declaredNumericPrecision, var11.declaredNumericScale, var11.geometryType, var11.geometrySrid, var30, var31, var8.getOnUpdateSQL(), ValueBoolean.get(var8.getVisible()), ValueBoolean.get(var8.isDefaultOnNull()), ValueInteger.get(var8.getSelectivity()), var8.getComment()});
   }

   private void columnPrivileges(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllRights().iterator();

      while(true) {
         Right var7;
         Table var9;
         String var10;
         do {
            do {
               DbObject var8;
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  var7 = (Right)var6.next();
                  var8 = var7.getGrantedObject();
               } while(!(var8 instanceof Table));

               var9 = (Table)var8;
            } while(this.hideTable(var9, var1));

            var10 = var9.getName();
         } while(!this.checkIndex(var1, var10, var2, var3));

         DbObject var11 = var7.getGrantee();
         int var12 = var7.getRightMask();
         Column[] var13 = var9.getColumns();
         int var14 = var13.length;

         for(int var15 = 0; var15 < var14; ++var15) {
            Column var16 = var13[var15];
            this.addPrivileges(var1, var4, var11, var5, var9, var16.getName(), var12);
         }
      }
   }

   private void constraintColumnUsage(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllConstraints().iterator();

         while(var8.hasNext()) {
            Constraint var9 = (Constraint)var8.next();
            this.constraintColumnUsage(var1, var2, var3, var4, var5, var9);
         }
      }

   }

   private void constraintColumnUsage(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5, Constraint var6) {
      Table var7;
      Iterator var8;
      Column var9;
      switch (var6.getConstraintType()) {
         case CHECK:
         case DOMAIN:
            HashSet var11 = new HashSet();
            var6.getExpression().isEverything(ExpressionVisitor.getColumnsVisitor(var11, (Table)null));
            var8 = var11.iterator();

            while(var8.hasNext()) {
               var9 = (Column)var8.next();
               Table var10 = var9.getTable();
               if (this.checkIndex(var1, var10.getName(), var2, var3) && !this.hideTable(var10, var1)) {
                  this.addConstraintColumnUsage(var1, var4, var5, var6, var9);
               }
            }

            return;
         case REFERENTIAL:
            var7 = var6.getRefTable();
            if (this.checkIndex(var1, var7.getName(), var2, var3) && !this.hideTable(var7, var1)) {
               var8 = var6.getReferencedColumns(var7).iterator();

               while(var8.hasNext()) {
                  var9 = (Column)var8.next();
                  this.addConstraintColumnUsage(var1, var4, var5, var6, var9);
               }
            }
         case PRIMARY_KEY:
         case UNIQUE:
            var7 = var6.getTable();
            if (this.checkIndex(var1, var7.getName(), var2, var3) && !this.hideTable(var7, var1)) {
               var8 = var6.getReferencedColumns(var7).iterator();

               while(var8.hasNext()) {
                  var9 = (Column)var8.next();
                  this.addConstraintColumnUsage(var1, var4, var5, var6, var9);
               }
            }
      }

   }

   private void domains(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      String var6 = this.database.getMainSchema().getName();
      String var7 = this.database.getCompareMode().getName();
      Iterator var8 = this.database.getAllSchemas().iterator();

      while(var8.hasNext()) {
         Schema var9 = (Schema)var8.next();
         Iterator var10 = var9.getAllDomains().iterator();

         while(var10.hasNext()) {
            Domain var11 = (Domain)var10.next();
            String var12 = var11.getName();
            if (this.checkIndex(var1, var12, var2, var3)) {
               this.domains(var1, var4, var5, var6, var7, var11, var12);
            }
         }
      }

   }

   private void domains(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, Domain var6, String var7) {
      Domain var8 = var6.getDomain();
      TypeInfo var9 = var6.getDataType();
      DataTypeInformation var10 = InformationSchemaTable.DataTypeInformation.valueOf(var9);
      String var11;
      String var12;
      String var13;
      String var14;
      if (var10.hasCharsetAndCollation) {
         var11 = var3;
         var12 = var4;
         var13 = "Unicode";
         var14 = var5;
      } else {
         var14 = null;
         var13 = null;
         var12 = null;
         var11 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6.getSchema().getName(), var7, var10.dataType, var10.characterPrecision, var10.characterPrecision, var11, var12, var13, var11, var12, var14, var10.numericPrecision, var10.numericPrecisionRadix, var10.numericScale, var10.datetimePrecision, var10.intervalType, var10.intervalPrecision, var6.getDefaultSQL(), var10.maximumCardinality, "TYPE", var10.declaredDataType, var10.declaredNumericPrecision, var10.declaredNumericScale, var10.geometryType, var10.geometrySrid, var6.getOnUpdateSQL(), var8 != null ? var3 : null, var8 != null ? var8.getSchema().getName() : null, var8 != null ? var8.getName() : null, var6.getComment()});
   }

   private void domainConstraints(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllConstraints().iterator();

         while(var8.hasNext()) {
            Constraint var9 = (Constraint)var8.next();
            if (var9.getConstraintType() == Constraint.Type.DOMAIN) {
               ConstraintDomain var10 = (ConstraintDomain)var9;
               Domain var11 = var10.getDomain();
               String var12 = var11.getName();
               if (this.checkIndex(var1, var12, var2, var3)) {
                  this.domainConstraints(var1, var4, var5, var10, var11, var12);
               }
            }
         }
      }

   }

   private void domainConstraints(SessionLocal var1, ArrayList<Row> var2, String var3, ConstraintDomain var4, Domain var5, String var6) {
      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var4.getName(), var3, var5.getSchema().getName(), var6, "NO", "NO", var4.getComment()});
   }

   private void elementTypesFields(SessionLocal var1, ArrayList<Row> var2, String var3, int var4) {
      String var5 = this.database.getMainSchema().getName();
      String var6 = this.database.getCompareMode().getName();
      Iterator var7 = this.database.getAllSchemas().iterator();

      label98:
      while(var7.hasNext()) {
         Schema var8 = (Schema)var7.next();
         String var9 = var8.getName();
         Iterator var10 = var8.getAllTablesAndViews(var1).iterator();

         while(var10.hasNext()) {
            Table var11 = (Table)var10.next();
            this.elementTypesFieldsForTable(var1, var2, var3, var4, var5, var6, var9, var11);
         }

         var10 = var8.getAllDomains().iterator();

         while(var10.hasNext()) {
            Domain var24 = (Domain)var10.next();
            this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var9, var24.getName(), "DOMAIN", "TYPE", var24.getDataType());
         }

         var10 = var8.getAllFunctionsAndAggregates().iterator();

         while(true) {
            String var12;
            FunctionAlias.JavaMethod[] var13;
            while(true) {
               UserDefinedFunction var25;
               do {
                  if (!var10.hasNext()) {
                     var10 = var8.getAllConstants().iterator();

                     while(var10.hasNext()) {
                        Constant var26 = (Constant)var10.next();
                        this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var9, var26.getName(), "CONSTANT", "TYPE", var26.getValue().getType());
                     }
                     continue label98;
                  }

                  var25 = (UserDefinedFunction)var10.next();
               } while(!(var25 instanceof FunctionAlias));

               var12 = var25.getName();

               try {
                  var13 = ((FunctionAlias)var25).getJavaMethods();
                  break;
               } catch (DbException var22) {
               }
            }

            for(int var14 = 0; var14 < var13.length; ++var14) {
               FunctionAlias.JavaMethod var15 = var13[var14];
               TypeInfo var16 = var15.getDataType();
               String var17 = var12 + '_' + (var14 + 1);
               if (var16 != null && var16.getValueType() != 0) {
                  this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var9, var17, "ROUTINE", "RESULT", var16);
               }

               Class[] var18 = var15.getColumnClasses();
               int var19 = 1;
               int var20 = var15.hasConnectionParam() ? 1 : 0;

               for(int var21 = var18.length; var20 < var21; ++var20) {
                  this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var9, var17, "ROUTINE", Integer.toString(var19), ValueToObjectConverter2.classToType(var18[var20]));
                  ++var19;
               }
            }
         }
      }

      var7 = var1.getLocalTempTables().iterator();

      while(var7.hasNext()) {
         Table var23 = (Table)var7.next();
         this.elementTypesFieldsForTable(var1, var2, var3, var4, var5, var6, var23.getSchema().getName(), var23);
      }

   }

   private void elementTypesFieldsForTable(SessionLocal var1, ArrayList<Row> var2, String var3, int var4, String var5, String var6, String var7, Table var8) {
      if (!this.hideTable(var8, var1)) {
         String var9 = var8.getName();
         Column[] var10 = var8.getColumns();

         for(int var11 = 0; var11 < var10.length; ++var11) {
            this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var7, var9, "TABLE", Integer.toString(var11 + 1), var10[var11].getType());
         }

      }
   }

   private void elementTypesFieldsRow(SessionLocal var1, ArrayList<Row> var2, String var3, int var4, String var5, String var6, String var7, String var8, String var9, String var10, TypeInfo var11) {
      switch (var11.getValueType()) {
         case 36:
            if (var4 == 22) {
               this.enumValues(var1, var2, var3, var7, var8, var9, var10, var11);
            }
            break;
         case 40:
            var11 = (TypeInfo)var11.getExtTypeInfo();
            String var18 = var10 + '_';
            if (var4 == 8) {
               this.elementTypes(var1, var2, var3, var5, var6, var7, var8, var9, var10, var18, var11);
            }

            this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var7, var8, var9, var18, var11);
            break;
         case 41:
            ExtTypeInfoRow var12 = (ExtTypeInfoRow)var11.getExtTypeInfo();
            int var13 = 0;

            String var17;
            for(Iterator var14 = var12.getFields().iterator(); var14.hasNext(); this.elementTypesFieldsRow(var1, var2, var3, var4, var5, var6, var7, var8, var9, var17, var11)) {
               Map.Entry var15 = (Map.Entry)var14.next();
               var11 = (TypeInfo)var15.getValue();
               String var16 = (String)var15.getKey();
               StringBuilder var10000 = (new StringBuilder()).append(var10).append('_');
               ++var13;
               var17 = var10000.append(var13).toString();
               if (var4 == 9) {
                  this.fields(var1, var2, var3, var5, var6, var7, var8, var9, var10, var16, var13, var17, var11);
               }
            }
      }

   }

   private void elementTypes(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, TypeInfo var11) {
      DataTypeInformation var12 = InformationSchemaTable.DataTypeInformation.valueOf(var11);
      String var13;
      String var14;
      String var15;
      String var16;
      if (var12.hasCharsetAndCollation) {
         var13 = var3;
         var14 = var4;
         var15 = "Unicode";
         var16 = var5;
      } else {
         var16 = null;
         var15 = null;
         var14 = null;
         var13 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6, var7, var8, var9, var12.dataType, var12.characterPrecision, var12.characterPrecision, var13, var14, var15, var13, var14, var16, var12.numericPrecision, var12.numericPrecisionRadix, var12.numericScale, var12.datetimePrecision, var12.intervalType, var12.intervalPrecision, var12.maximumCardinality, var10, var12.declaredDataType, var12.declaredNumericPrecision, var12.declaredNumericScale, var12.geometryType, var12.geometrySrid});
   }

   private void fields(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, int var11, String var12, TypeInfo var13) {
      DataTypeInformation var14 = InformationSchemaTable.DataTypeInformation.valueOf(var13);
      String var15;
      String var16;
      String var17;
      String var18;
      if (var14.hasCharsetAndCollation) {
         var15 = var3;
         var16 = var4;
         var17 = "Unicode";
         var18 = var5;
      } else {
         var18 = null;
         var17 = null;
         var16 = null;
         var15 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6, var7, var8, var9, var10, ValueInteger.get(var11), var14.dataType, var14.characterPrecision, var14.characterPrecision, var15, var16, var17, var15, var16, var18, var14.numericPrecision, var14.numericPrecisionRadix, var14.numericScale, var14.datetimePrecision, var14.intervalType, var14.intervalPrecision, var14.maximumCardinality, var12, var14.declaredDataType, var14.declaredNumericPrecision, var14.declaredNumericScale, var14.geometryType, var14.geometrySrid});
   }

   private void keyColumnUsage(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllConstraints().iterator();

         while(var8.hasNext()) {
            Constraint var9 = (Constraint)var8.next();
            Constraint.Type var10 = var9.getConstraintType();
            IndexColumn[] var11 = null;
            if (var10 != Constraint.Type.UNIQUE && var10 != Constraint.Type.PRIMARY_KEY) {
               if (var10 == Constraint.Type.REFERENTIAL) {
                  var11 = ((ConstraintReferential)var9).getColumns();
               }
            } else {
               var11 = ((ConstraintUnique)var9).getColumns();
            }

            if (var11 != null) {
               Table var12 = var9.getTable();
               if (!this.hideTable(var12, var1)) {
                  String var13 = var12.getName();
                  if (this.checkIndex(var1, var13, var2, var3)) {
                     this.keyColumnUsage(var1, var4, var5, var9, var10, var11, var12, var13);
                  }
               }
            }
         }
      }

   }

   private void keyColumnUsage(SessionLocal var1, ArrayList<Row> var2, String var3, Constraint var4, Constraint.Type var5, IndexColumn[] var6, Table var7, String var8) {
      ConstraintUnique var9;
      if (var5 == Constraint.Type.REFERENTIAL) {
         var9 = ((ConstraintReferential)var4).getReferencedConstraint();
      } else {
         var9 = null;
      }

      for(int var10 = 0; var10 < var6.length; ++var10) {
         IndexColumn var11 = var6[var10];
         ValueInteger var12 = ValueInteger.get(var10 + 1);
         ValueInteger var13 = null;
         if (var9 != null) {
            Column var14 = ((ConstraintReferential)var4).getRefColumns()[var10].column;
            IndexColumn[] var15 = var9.getColumns();

            for(int var16 = 0; var16 < var15.length; ++var16) {
               if (var15[var16].column.equals(var14)) {
                  var13 = ValueInteger.get(var16 + 1);
                  break;
               }
            }
         }

         this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var4.getName(), var3, var7.getSchema().getName(), var8, var11.columnName, var12, var13});
      }

   }

   private void parameters(SessionLocal var1, ArrayList<Row> var2, String var3) {
      String var4 = this.database.getMainSchema().getName();
      String var5 = this.database.getCompareMode().getName();
      Iterator var6 = this.database.getAllSchemas().iterator();

      label54:
      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllFunctionsAndAggregates().iterator();

         while(true) {
            UserDefinedFunction var9;
            FunctionAlias.JavaMethod[] var10;
            while(true) {
               do {
                  if (!var8.hasNext()) {
                     continue label54;
                  }

                  var9 = (UserDefinedFunction)var8.next();
               } while(!(var9 instanceof FunctionAlias));

               try {
                  var10 = ((FunctionAlias)var9).getJavaMethods();
                  break;
               } catch (DbException var17) {
               }
            }

            for(int var11 = 0; var11 < var10.length; ++var11) {
               FunctionAlias.JavaMethod var12 = var10[var11];
               Class[] var13 = var12.getColumnClasses();
               int var14 = 1;
               int var15 = var12.hasConnectionParam() ? 1 : 0;

               for(int var16 = var13.length; var15 < var16; ++var15) {
                  this.parameters(var1, var2, var3, var4, var5, var7.getName(), var9.getName() + '_' + (var11 + 1), ValueToObjectConverter2.classToType(var13[var15]), var14);
                  ++var14;
               }
            }
         }
      }

   }

   private void parameters(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, String var6, String var7, TypeInfo var8, int var9) {
      DataTypeInformation var10 = InformationSchemaTable.DataTypeInformation.valueOf(var8);
      String var11;
      String var12;
      String var13;
      String var14;
      if (var10.hasCharsetAndCollation) {
         var11 = var3;
         var12 = var4;
         var13 = "Unicode";
         var14 = var5;
      } else {
         var14 = null;
         var13 = null;
         var12 = null;
         var11 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6, var7, ValueInteger.get(var9), "IN", "NO", DataType.isLargeObject(var8.getValueType()) ? "YES" : "NO", "P" + var9, this.identifier(var10.dataType), var10.characterPrecision, var10.characterPrecision, var11, var12, var13, var11, var12, var14, var10.numericPrecision, var10.numericPrecisionRadix, var10.numericScale, var10.datetimePrecision, var10.intervalType, var10.intervalPrecision, var10.maximumCardinality, Integer.toString(var9), var10.declaredDataType, var10.declaredNumericPrecision, var10.declaredNumericScale, null, var10.geometryType, var10.geometrySrid});
   }

   private void referentialConstraints(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllConstraints().iterator();

         while(var8.hasNext()) {
            Constraint var9 = (Constraint)var8.next();
            if (var9.getConstraintType() == Constraint.Type.REFERENTIAL && !this.hideTable(var9.getTable(), var1)) {
               String var10 = var9.getName();
               if (this.checkIndex(var1, var10, var2, var3)) {
                  this.referentialConstraints(var1, var4, var5, (ConstraintReferential)var9, var10);
               }
            }
         }
      }

   }

   private void referentialConstraints(SessionLocal var1, ArrayList<Row> var2, String var3, ConstraintReferential var4, String var5) {
      ConstraintUnique var6 = var4.getReferencedConstraint();
      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var5, var3, var6.getSchema().getName(), var6.getName(), "NONE", var4.getUpdateAction().getSqlName(), var4.getDeleteAction().getSqlName()});
   }

   private void routines(SessionLocal var1, ArrayList<Row> var2, String var3) {
      boolean var4 = var1.getUser().isAdmin();
      String var5 = this.database.getMainSchema().getName();
      String var6 = this.database.getCompareMode().getName();
      Iterator var7 = this.database.getAllSchemas().iterator();

      label59:
      while(var7.hasNext()) {
         Schema var8 = (Schema)var7.next();
         String var9 = var8.getName();
         Iterator var10 = var8.getAllFunctionsAndAggregates().iterator();

         while(true) {
            String var12;
            FunctionAlias var13;
            FunctionAlias.JavaMethod[] var14;
            label45:
            while(true) {
               while(true) {
                  if (!var10.hasNext()) {
                     continue label59;
                  }

                  UserDefinedFunction var11 = (UserDefinedFunction)var10.next();
                  var12 = var11.getName();
                  if (var11 instanceof FunctionAlias) {
                     var13 = (FunctionAlias)var11;

                     try {
                        var14 = var13.getJavaMethods();
                        break label45;
                     } catch (DbException var19) {
                     }
                  } else {
                     this.routines(var1, var2, var3, var5, var6, var9, var12, var12, "AGGREGATE", (String)null, var11.getJavaClassName(), TypeInfo.TYPE_NULL, false, var11.getComment());
                  }
               }
            }

            for(int var15 = 0; var15 < var14.length; ++var15) {
               FunctionAlias.JavaMethod var16 = var14[var15];
               TypeInfo var17 = var16.getDataType();
               String var18;
               if (var17 != null && var17.getValueType() == 0) {
                  var18 = "PROCEDURE";
                  var17 = null;
               } else {
                  var18 = "FUNCTION";
               }

               this.routines(var1, var2, var3, var5, var6, var9, var12, var12 + '_' + (var15 + 1), var18, var4 ? var13.getSource() : null, var13.getJavaClassName() + '.' + var13.getJavaMethodName(), var17, var13.isDeterministic(), var13.getComment());
            }
         }
      }

   }

   private void routines(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11, TypeInfo var12, boolean var13, String var14) {
      DataTypeInformation var15 = var12 != null ? InformationSchemaTable.DataTypeInformation.valueOf(var12) : InformationSchemaTable.DataTypeInformation.NULL;
      String var16;
      String var17;
      String var18;
      String var19;
      if (var15.hasCharsetAndCollation) {
         var16 = var3;
         var17 = var4;
         var18 = "Unicode";
         var19 = var5;
      } else {
         var19 = null;
         var18 = null;
         var17 = null;
         var16 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6, var8, var3, var6, var7, var9, this.identifier(var15.dataType), var15.characterPrecision, var15.characterPrecision, var16, var17, var18, var16, var17, var19, var15.numericPrecision, var15.numericPrecisionRadix, var15.numericScale, var15.datetimePrecision, var15.intervalType, var15.intervalPrecision, var15.maximumCardinality, "RESULT", "EXTERNAL", var10, var11, "JAVA", "GENERAL", var13 ? "YES" : "NO", var15.declaredDataType, var15.declaredNumericPrecision, var15.declaredNumericScale, var15.geometryType, var15.geometrySrid, var14});
   }

   private void schemata(SessionLocal var1, ArrayList<Row> var2, String var3) {
      String var4 = this.database.getMainSchema().getName();
      String var5 = this.database.getCompareMode().getName();
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         this.add(var1, var2, new Object[]{var3, var7.getName(), this.identifier(var7.getOwner().getName()), var3, var4, "Unicode", null, var5, var7.getComment()});
      }

   }

   private void sequences(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllSequences().iterator();

         while(var8.hasNext()) {
            Sequence var9 = (Sequence)var8.next();
            if (!var9.getBelongsToTable()) {
               String var10 = var9.getName();
               if (this.checkIndex(var1, var10, var2, var3)) {
                  this.sequences(var1, var4, var5, var9, var10);
               }
            }
         }
      }

   }

   private void sequences(SessionLocal var1, ArrayList<Row> var2, String var3, Sequence var4, String var5) {
      DataTypeInformation var6 = InformationSchemaTable.DataTypeInformation.valueOf(var4.getDataType());
      Sequence.Cycle var7 = var4.getCycle();
      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var5, var6.dataType, ValueInteger.get(var4.getEffectivePrecision()), var6.numericPrecisionRadix, var6.numericScale, ValueBigint.get(var4.getStartValue()), ValueBigint.get(var4.getMinValue()), ValueBigint.get(var4.getMaxValue()), ValueBigint.get(var4.getIncrement()), var7.isCycle() ? "YES" : "NO", var6.declaredDataType, var6.declaredNumericPrecision, var6.declaredNumericScale, var7 != Sequence.Cycle.EXHAUSTED ? ValueBigint.get(var4.getBaseValue()) : null, ValueBigint.get(var4.getCacheSize()), var4.getComment()});
   }

   private void tables(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllTablesAndViews(var1).iterator();

         while(var8.hasNext()) {
            Table var9 = (Table)var8.next();
            String var10 = var9.getName();
            if (this.checkIndex(var1, var10, var2, var3)) {
               this.tables(var1, var4, var5, var9, var10);
            }
         }
      }

      var6 = var1.getLocalTempTables().iterator();

      while(var6.hasNext()) {
         Table var11 = (Table)var6.next();
         String var12 = var11.getName();
         if (this.checkIndex(var1, var12, var2, var3)) {
            this.tables(var1, var4, var5, var11, var12);
         }
      }

   }

   private void tables(SessionLocal var1, ArrayList<Row> var2, String var3, Table var4, String var5) {
      if (!this.hideTable(var4, var1)) {
         String var6;
         String var7;
         if (var4.isTemporary()) {
            var6 = var4.getOnCommitTruncate() ? "DELETE" : (var4.getOnCommitDrop() ? "DROP" : "PRESERVE");
            var7 = var4.isGlobalTemporary() ? "GLOBAL TEMPORARY" : "LOCAL TEMPORARY";
         } else {
            var6 = null;
            switch (var4.getTableType()) {
               case TABLE_LINK:
                  var7 = "TABLE LINK";
                  break;
               case EXTERNAL_TABLE_ENGINE:
                  var7 = "EXTERNAL";
                  break;
               default:
                  var7 = var4.isPersistIndexes() ? "CACHED" : "MEMORY";
            }
         }

         long var8 = var4.getMaxDataModificationId();
         this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var5, var4.getSQLTableType(), var4.isInsertable() ? "YES" : "NO", var6, var7, var4.getComment(), var8 != Long.MAX_VALUE ? ValueBigint.get(var8) : null, var4.getClass().getName(), ValueBigint.get(var4.getRowCountApproximation(var1))});
      }
   }

   private void tableConstraints(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllConstraints().iterator();

         while(var8.hasNext()) {
            Constraint var9 = (Constraint)var8.next();
            Constraint.Type var10 = var9.getConstraintType();
            if (var10 != Constraint.Type.DOMAIN) {
               Table var11 = var9.getTable();
               if (!this.hideTable(var11, var1)) {
                  String var12 = var11.getName();
                  if (this.checkIndex(var1, var12, var2, var3)) {
                     this.tableConstraints(var1, var4, var5, var9, var10, var11, var12);
                  }
               }
            }
         }
      }

   }

   private void tableConstraints(SessionLocal var1, ArrayList<Row> var2, String var3, Constraint var4, Constraint.Type var5, Table var6, String var7) {
      Index var8 = var4.getIndex();
      boolean var9;
      if (var5 != Constraint.Type.REFERENTIAL) {
         var9 = true;
      } else {
         var9 = this.database.getReferentialIntegrity() && var6.getCheckForeignKeyConstraints() && ((ConstraintReferential)var4).getRefTable().getCheckForeignKeyConstraints();
      }

      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var4.getName(), var5.getSqlName(), var3, var6.getSchema().getName(), var7, "NO", "NO", var9 ? "YES" : "NO", var8 != null ? var3 : null, var8 != null ? var8.getSchema().getName() : null, var8 != null ? var8.getName() : null, var4.getComment()});
   }

   private void tablePrivileges(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllRights().iterator();

      while(var6.hasNext()) {
         Right var7 = (Right)var6.next();
         DbObject var8 = var7.getGrantedObject();
         if (var8 instanceof Table) {
            Table var9 = (Table)var8;
            if (!this.hideTable(var9, var1)) {
               String var10 = var9.getName();
               if (this.checkIndex(var1, var10, var2, var3)) {
                  this.addPrivileges(var1, var4, var7.getGrantee(), var5, var9, (String)null, var7.getRightMask());
               }
            }
         }
      }

   }

   private void triggers(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllTriggers().iterator();

         while(var8.hasNext()) {
            TriggerObject var9 = (TriggerObject)var8.next();
            Table var10 = var9.getTable();
            String var11 = var10.getName();
            if (this.checkIndex(var1, var11, var2, var3)) {
               int var12 = var9.getTypeMask();
               if ((var12 & 1) != 0) {
                  this.triggers(var1, var4, var5, var9, "INSERT", var10, var11);
               }

               if ((var12 & 2) != 0) {
                  this.triggers(var1, var4, var5, var9, "UPDATE", var10, var11);
               }

               if ((var12 & 4) != 0) {
                  this.triggers(var1, var4, var5, var9, "DELETE", var10, var11);
               }

               if ((var12 & 8) != 0) {
                  this.triggers(var1, var4, var5, var9, "SELECT", var10, var11);
               }
            }
         }
      }

   }

   private void triggers(SessionLocal var1, ArrayList<Row> var2, String var3, TriggerObject var4, String var5, Table var6, String var7) {
      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var4.getName(), var5, var3, var6.getSchema().getName(), var7, var4.isRowBased() ? "ROW" : "STATEMENT", var4.isInsteadOf() ? "INSTEAD OF" : (var4.isBefore() ? "BEFORE" : "AFTER"), ValueBoolean.get(var4.isOnRollback()), var4.getTriggerClassName(), ValueInteger.get(var4.getQueueSize()), ValueBoolean.get(var4.isNoWait()), var4.getComment()});
   }

   private void views(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      Iterator var6 = this.database.getAllSchemas().iterator();

      while(var6.hasNext()) {
         Schema var7 = (Schema)var6.next();
         Iterator var8 = var7.getAllTablesAndViews(var1).iterator();

         while(var8.hasNext()) {
            Table var9 = (Table)var8.next();
            if (var9.isView()) {
               String var10 = var9.getName();
               if (this.checkIndex(var1, var10, var2, var3)) {
                  this.views(var1, var4, var5, var9, var10);
               }
            }
         }
      }

      var6 = var1.getLocalTempTables().iterator();

      while(var6.hasNext()) {
         Table var11 = (Table)var6.next();
         if (var11.isView()) {
            String var12 = var11.getName();
            if (this.checkIndex(var1, var12, var2, var3)) {
               this.views(var1, var4, var5, var11, var12);
            }
         }
      }

   }

   private void views(SessionLocal var1, ArrayList<Row> var2, String var3, Table var4, String var5) {
      String var7 = "VALID";
      String var6;
      if (var4 instanceof TableView) {
         TableView var8 = (TableView)var4;
         var6 = var8.getQuery();
         if (var8.isInvalid()) {
            var7 = "INVALID";
         }
      } else {
         var6 = null;
      }

      int var12 = 0;
      ArrayList var9 = var4.getTriggers();
      if (var9 != null) {
         Iterator var10 = var9.iterator();

         while(var10.hasNext()) {
            TriggerObject var11 = (TriggerObject)var10.next();
            if (var11.isInsteadOf()) {
               var12 |= var11.getTypeMask();
            }
         }
      }

      this.add(var1, var2, new Object[]{var3, var4.getSchema().getName(), var5, var6, "NONE", "NO", "NO", (var12 & 2) != 0 ? "YES" : "NO", (var12 & 4) != 0 ? "YES" : "NO", (var12 & 1) != 0 ? "YES" : "NO", var7, var4.getComment()});
   }

   private void constants(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5) {
      String var6 = this.database.getMainSchema().getName();
      String var7 = this.database.getCompareMode().getName();
      Iterator var8 = this.database.getAllSchemas().iterator();

      while(var8.hasNext()) {
         Schema var9 = (Schema)var8.next();
         Iterator var10 = var9.getAllConstants().iterator();

         while(var10.hasNext()) {
            Constant var11 = (Constant)var10.next();
            String var12 = var11.getName();
            if (this.checkIndex(var1, var12, var2, var3)) {
               this.constants(var1, var4, var5, var6, var7, var11, var12);
            }
         }
      }

   }

   private void constants(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, Constant var6, String var7) {
      ValueExpression var8 = var6.getValue();
      TypeInfo var9 = var8.getType();
      DataTypeInformation var10 = InformationSchemaTable.DataTypeInformation.valueOf(var9);
      String var11;
      String var12;
      String var13;
      String var14;
      if (var10.hasCharsetAndCollation) {
         var11 = var3;
         var12 = var4;
         var13 = "Unicode";
         var14 = var5;
      } else {
         var14 = null;
         var13 = null;
         var12 = null;
         var11 = null;
      }

      this.add(var1, var2, new Object[]{var3, var6.getSchema().getName(), var7, var8.getSQL(0), var10.dataType, var10.characterPrecision, var10.characterPrecision, var11, var12, var13, var11, var12, var14, var10.numericPrecision, var10.numericPrecisionRadix, var10.numericScale, var10.datetimePrecision, var10.intervalType, var10.intervalPrecision, var10.maximumCardinality, "TYPE", var10.declaredDataType, var10.declaredNumericPrecision, var10.declaredNumericScale, var10.geometryType, var10.geometrySrid, var6.getComment()});
   }

   private void enumValues(SessionLocal var1, ArrayList<Row> var2, String var3, String var4, String var5, String var6, String var7, TypeInfo var8) {
      ExtTypeInfoEnum var9 = (ExtTypeInfoEnum)var8.getExtTypeInfo();
      if (var9 != null) {
         int var10 = 0;
         int var11 = var1.zeroBasedEnums() ? 0 : 1;

         for(int var12 = var9.getCount(); var10 < var12; ++var11) {
            this.add(var1, var2, new Object[]{var3, var4, var5, var6, var7, var9.getEnumerator(var10), ValueInteger.get(var11)});
            ++var10;
         }

      }
   }

   private void indexes(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4, String var5, boolean var6) {
      Table var10;
      Table var13;
      if (var2 != null && var2.equals(var3)) {
         String var12 = var2.getString();
         if (var12 == null) {
            return;
         }

         Iterator var14 = this.database.getAllSchemas().iterator();

         while(var14.hasNext()) {
            Schema var16 = (Schema)var14.next();
            var10 = var16.getTableOrViewByName(var1, var12);
            if (var10 != null) {
               this.indexes(var1, var4, var5, var6, var10, var10.getName());
            }
         }

         var13 = var1.findLocalTempTable(var12);
         if (var13 != null) {
            this.indexes(var1, var4, var5, var6, var13, var13.getName());
         }
      } else {
         Iterator var7 = this.database.getAllSchemas().iterator();

         while(var7.hasNext()) {
            Schema var8 = (Schema)var7.next();
            Iterator var9 = var8.getAllTablesAndViews(var1).iterator();

            while(var9.hasNext()) {
               var10 = (Table)var9.next();
               String var11 = var10.getName();
               if (this.checkIndex(var1, var11, var2, var3)) {
                  this.indexes(var1, var4, var5, var6, var10, var11);
               }
            }
         }

         var7 = var1.getLocalTempTables().iterator();

         while(var7.hasNext()) {
            var13 = (Table)var7.next();
            String var15 = var13.getName();
            if (this.checkIndex(var1, var15, var2, var3)) {
               this.indexes(var1, var4, var5, var6, var13, var15);
            }
         }
      }

   }

   private void indexes(SessionLocal var1, ArrayList<Row> var2, String var3, boolean var4, Table var5, String var6) {
      if (!this.hideTable(var5, var1)) {
         ArrayList var7 = var5.getIndexes();
         if (var7 != null) {
            Iterator var8 = var7.iterator();

            while(var8.hasNext()) {
               Index var9 = (Index)var8.next();
               if (var9.getCreateSQL() != null) {
                  if (var4) {
                     this.indexColumns(var1, var2, var3, var5, var6, var9);
                  } else {
                     this.indexes(var1, var2, var3, var5, var6, var9);
                  }
               }
            }

         }
      }
   }

   private void indexes(SessionLocal var1, ArrayList<Row> var2, String var3, Table var4, String var5, Index var6) {
      this.add(var1, var2, new Object[]{var3, var6.getSchema().getName(), var6.getName(), var3, var4.getSchema().getName(), var5, var6.getIndexType().getSQL(), ValueBoolean.get(var6.getIndexType().getBelongsToConstraint()), var6.getComment(), var6.getClass().getName()});
   }

   private void indexColumns(SessionLocal var1, ArrayList<Row> var2, String var3, Table var4, String var5, Index var6) {
      IndexColumn[] var7 = var6.getIndexColumns();
      int var8 = var6.getUniqueColumnCount();
      int var9 = 0;
      int var10 = var7.length;

      while(var9 < var10) {
         IndexColumn var11 = var7[var9];
         int var12 = var11.sortType;
         Object[] var10003 = new Object[]{var3, var6.getSchema().getName(), var6.getName(), var3, var4.getSchema().getName(), var5, var11.column.getName(), null, null, null, null};
         ++var9;
         var10003[7] = ValueInteger.get(var9);
         var10003[8] = (var12 & 1) == 0 ? "ASC" : "DESC";
         var10003[9] = (var12 & 2) != 0 ? "FIRST" : ((var12 & 4) != 0 ? "LAST" : null);
         var10003[10] = ValueBoolean.get(var9 <= var8);
         this.add(var1, var2, var10003);
      }

   }

   private void inDoubt(SessionLocal var1, ArrayList<Row> var2) {
      if (var1.getUser().isAdmin()) {
         ArrayList var3 = this.database.getInDoubtTransactions();
         if (var3 != null) {
            Iterator var4 = var3.iterator();

            while(var4.hasNext()) {
               InDoubtTransaction var5 = (InDoubtTransaction)var4.next();
               this.add(var1, var2, new Object[]{var5.getTransactionName(), var5.getStateDescription()});
            }
         }
      }

   }

   private void locks(SessionLocal var1, ArrayList<Row> var2) {
      if (var1.getUser().isAdmin()) {
         SessionLocal[] var3 = this.database.getSessions(false);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            SessionLocal var6 = var3[var5];
            this.locks(var1, var2, var6);
         }
      } else {
         this.locks(var1, var2, var1);
      }

   }

   private void locks(SessionLocal var1, ArrayList<Row> var2, SessionLocal var3) {
      Iterator var4 = var3.getLocks().iterator();

      while(var4.hasNext()) {
         Table var5 = (Table)var4.next();
         this.add(var1, var2, new Object[]{var5.getSchema().getName(), var5.getName(), ValueInteger.get(var3.getId()), var5.isLockedExclusivelyBy(var3) ? "WRITE" : "READ"});
      }

   }

   private void queryStatistics(SessionLocal var1, ArrayList<Row> var2) {
      QueryStatisticsData var3 = this.database.getQueryStatisticsData();
      if (var3 != null) {
         Iterator var4 = var3.getQueries().iterator();

         while(var4.hasNext()) {
            QueryStatisticsData.QueryEntry var5 = (QueryStatisticsData.QueryEntry)var4.next();
            this.add(var1, var2, new Object[]{var5.sqlStatement, ValueInteger.get(var5.count), ValueDouble.get((double)var5.executionTimeMinNanos / 1000000.0), ValueDouble.get((double)var5.executionTimeMaxNanos / 1000000.0), ValueDouble.get((double)var5.executionTimeCumulativeNanos / 1000000.0), ValueDouble.get(var5.executionTimeMeanNanos / 1000000.0), ValueDouble.get(var5.getExecutionTimeStandardDeviation() / 1000000.0), ValueBigint.get(var5.rowCountMin), ValueBigint.get(var5.rowCountMax), ValueBigint.get(var5.rowCountCumulative), ValueDouble.get(var5.rowCountMean), ValueDouble.get(var5.getRowCountStandardDeviation())});
         }
      }

   }

   private void rights(SessionLocal var1, Value var2, Value var3, ArrayList<Row> var4) {
      if (var1.getUser().isAdmin()) {
         Iterator var5 = this.database.getAllRights().iterator();

         while(var5.hasNext()) {
            Right var6 = (Right)var5.next();
            Role var7 = var6.getGrantedRole();
            DbObject var8 = var6.getGrantee();
            String var9 = var8.getType() == 2 ? "USER" : "ROLE";
            if (var7 == null) {
               DbObject var10 = var6.getGrantedObject();
               Schema var11 = null;
               Table var12 = null;
               if (var10 != null) {
                  if (var10 instanceof Schema) {
                     var11 = (Schema)var10;
                  } else if (var10 instanceof Table) {
                     var12 = (Table)var10;
                     var11 = var12.getSchema();
                  }
               }

               String var13 = var12 != null ? var12.getName() : "";
               String var14 = var11 != null ? var11.getName() : "";
               if (this.checkIndex(var1, var13, var2, var3)) {
                  this.add(var1, var4, new Object[]{this.identifier(var8.getName()), var9, null, var6.getRights(), var14, var13});
               }
            } else {
               this.add(var1, var4, new Object[]{this.identifier(var8.getName()), var9, this.identifier(var7.getName()), null, null, null});
            }
         }

      }
   }

   private void roles(SessionLocal var1, ArrayList<Row> var2) {
      boolean var3 = var1.getUser().isAdmin();
      Iterator var4 = this.database.getAllUsersAndRoles().iterator();

      while(true) {
         Role var6;
         do {
            RightOwner var5;
            do {
               if (!var4.hasNext()) {
                  return;
               }

               var5 = (RightOwner)var4.next();
            } while(!(var5 instanceof Role));

            var6 = (Role)var5;
         } while(!var3 && !var1.getUser().isRoleGranted(var6));

         this.add(var1, var2, new Object[]{this.identifier(var6.getName()), var6.getComment()});
      }
   }

   private void sessions(SessionLocal var1, ArrayList<Row> var2) {
      if (var1.getUser().isAdmin()) {
         SessionLocal[] var3 = this.database.getSessions(false);
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            SessionLocal var6 = var3[var5];
            this.sessions(var1, var2, var6);
         }
      } else {
         this.sessions(var1, var2, var1);
      }

   }

   private void sessions(SessionLocal var1, ArrayList<Row> var2, SessionLocal var3) {
      NetworkConnectionInfo var4 = var3.getNetworkConnectionInfo();
      Command var5 = var3.getCurrentCommand();
      int var6 = var3.getBlockingSessionId();
      this.add(var1, var2, new Object[]{ValueInteger.get(var3.getId()), var3.getUser().getName(), var4 == null ? null : var4.getServer(), var4 == null ? null : var4.getClient(), var4 == null ? null : var4.getClientInfo(), var3.getSessionStart(), var1.getIsolationLevel().getSQL(), var5 == null ? null : var5.toString(), var5 == null ? null : var3.getCommandStartOrEnd(), ValueBoolean.get(var3.hasPendingTransaction()), String.valueOf(var3.getState()), var6 == 0 ? null : ValueInteger.get(var6), var3.getState() == SessionLocal.State.SLEEP ? var3.getCommandStartOrEnd() : null});
   }

   private void sessionState(SessionLocal var1, ArrayList<Row> var2) {
      String[] var3 = var1.getVariableNames();
      int var4 = var3.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         Value var7 = var1.getVariable(var6);
         StringBuilder var8 = (new StringBuilder()).append("SET @").append(var6).append(' ');
         var7.getSQL(var8, 0);
         this.add(var1, var2, new Object[]{"@" + var6, var8.toString()});
      }

      Iterator var9 = var1.getLocalTempTables().iterator();

      while(var9.hasNext()) {
         Table var10 = (Table)var9.next();
         this.add(var1, var2, new Object[]{"TABLE " + var10.getName(), var10.getCreateSQL()});
      }

      var3 = var1.getSchemaSearchPath();
      if (var3 != null && var3.length > 0) {
         StringBuilder var11 = new StringBuilder("SET SCHEMA_SEARCH_PATH ");
         var5 = 0;

         for(int var13 = var3.length; var5 < var13; ++var5) {
            if (var5 > 0) {
               var11.append(", ");
            }

            StringUtils.quoteIdentifier(var11, var3[var5]);
         }

         this.add(var1, var2, new Object[]{"SCHEMA_SEARCH_PATH", var11.toString()});
      }

      String var12 = var1.getCurrentSchemaName();
      if (var12 != null) {
         this.add(var1, var2, new Object[]{"SCHEMA", StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), var12).toString()});
      }

      TimeZoneProvider var14 = var1.currentTimeZone();
      if (!var14.equals(DateTimeUtils.getTimeZone())) {
         this.add(var1, var2, new Object[]{"TIME ZONE", StringUtils.quoteStringSQL(new StringBuilder("SET TIME ZONE "), var14.getId()).toString()});
      }

   }

   private void settings(SessionLocal var1, ArrayList<Row> var2) {
      Setting var4;
      String var5;
      for(Iterator var3 = this.database.getAllSettings().iterator(); var3.hasNext(); this.add(var1, var2, new Object[]{this.identifier(var4.getName()), var5})) {
         var4 = (Setting)var3.next();
         var5 = var4.getStringValue();
         if (var5 == null) {
            var5 = Integer.toString(var4.getIntValue());
         }
      }

      this.add(var1, var2, new Object[]{"info.BUILD_ID", "210"});
      this.add(var1, var2, new Object[]{"info.VERSION_MAJOR", "2"});
      this.add(var1, var2, new Object[]{"info.VERSION_MINOR", "1"});
      this.add(var1, var2, new Object[]{"info.VERSION", Constants.FULL_VERSION});
      int var6;
      int var14;
      if (var1.getUser().isAdmin()) {
         String[] var9 = new String[]{"java.runtime.version", "java.vm.name", "java.vendor", "os.name", "os.arch", "os.version", "sun.os.patch.level", "file.separator", "path.separator", "line.separator", "user.country", "user.language", "user.variant", "file.encoding"};
         String[] var11 = var9;
         var14 = var9.length;

         for(var6 = 0; var6 < var14; ++var6) {
            String var7 = var11[var6];
            this.add(var1, var2, new Object[]{"property." + var7, Utils.getProperty(var7, "")});
         }
      }

      this.add(var1, var2, new Object[]{"DEFAULT_NULL_ORDERING", this.database.getDefaultNullOrdering().name()});
      this.add(var1, var2, new Object[]{"EXCLUSIVE", this.database.getExclusiveSession() == null ? "FALSE" : "TRUE"});
      this.add(var1, var2, new Object[]{"MODE", this.database.getMode().getName()});
      this.add(var1, var2, new Object[]{"QUERY_TIMEOUT", Integer.toString(var1.getQueryTimeout())});
      this.add(var1, var2, new Object[]{"TIME ZONE", var1.currentTimeZone().getId()});
      this.add(var1, var2, new Object[]{"TRUNCATE_LARGE_LENGTH", var1.isTruncateLargeLength() ? "TRUE" : "FALSE"});
      this.add(var1, var2, new Object[]{"VARIABLE_BINARY", var1.isVariableBinary() ? "TRUE" : "FALSE"});
      this.add(var1, var2, new Object[]{"OLD_INFORMATION_SCHEMA", var1.isOldInformationSchema() ? "TRUE" : "FALSE"});
      BitSet var10 = var1.getNonKeywords();
      if (var10 != null) {
         this.add(var1, var2, new Object[]{"NON_KEYWORDS", Parser.formatNonKeywords(var10)});
      }

      this.add(var1, var2, new Object[]{"RETENTION_TIME", Integer.toString(this.database.getRetentionTime())});
      Map.Entry[] var12 = this.database.getSettings().getSortedSettings();
      var14 = var12.length;

      for(var6 = 0; var6 < var14; ++var6) {
         Map.Entry var17 = var12[var6];
         this.add(var1, var2, new Object[]{var17.getKey(), var17.getValue()});
      }

      Store var13 = this.database.getStore();
      MVStore var15 = var13.getMvStore();
      FileStore var16 = var15.getFileStore();
      if (var16 != null) {
         this.add(var1, var2, new Object[]{"info.FILE_WRITE", Long.toString(var16.getWriteCount())});
         this.add(var1, var2, new Object[]{"info.FILE_WRITE_BYTES", Long.toString(var16.getWriteBytes())});
         this.add(var1, var2, new Object[]{"info.FILE_READ", Long.toString(var16.getReadCount())});
         this.add(var1, var2, new Object[]{"info.FILE_READ_BYTES", Long.toString(var16.getReadBytes())});
         this.add(var1, var2, new Object[]{"info.UPDATE_FAILURE_PERCENT", String.format(Locale.ENGLISH, "%.2f%%", 100.0 * var15.getUpdateFailureRatio())});
         this.add(var1, var2, new Object[]{"info.FILL_RATE", Integer.toString(var15.getFillRate())});
         this.add(var1, var2, new Object[]{"info.CHUNKS_FILL_RATE", Integer.toString(var15.getChunksFillRate())});
         this.add(var1, var2, new Object[]{"info.CHUNKS_FILL_RATE_RW", Integer.toString(var15.getRewritableChunksFillRate())});

         try {
            this.add(var1, var2, new Object[]{"info.FILE_SIZE", Long.toString(var16.getFile().size())});
         } catch (IOException var8) {
         }

         this.add(var1, var2, new Object[]{"info.CHUNK_COUNT", Long.toString((long)var15.getChunkCount())});
         this.add(var1, var2, new Object[]{"info.PAGE_COUNT", Long.toString((long)var15.getPageCount())});
         this.add(var1, var2, new Object[]{"info.PAGE_COUNT_LIVE", Long.toString((long)var15.getLivePageCount())});
         this.add(var1, var2, new Object[]{"info.PAGE_SIZE", Integer.toString(var15.getPageSplitSize())});
         this.add(var1, var2, new Object[]{"info.CACHE_MAX_SIZE", Integer.toString(var15.getCacheSize())});
         this.add(var1, var2, new Object[]{"info.CACHE_SIZE", Integer.toString(var15.getCacheSizeUsed())});
         this.add(var1, var2, new Object[]{"info.CACHE_HIT_RATIO", Integer.toString(var15.getCacheHitRatio())});
         this.add(var1, var2, new Object[]{"info.TOC_CACHE_HIT_RATIO", Integer.toString(var15.getTocCacheHitRatio())});
         this.add(var1, var2, new Object[]{"info.LEAF_RATIO", Integer.toString(var15.getLeafRatio())});
      }

   }

   private void synonyms(SessionLocal var1, ArrayList<Row> var2, String var3) {
      Iterator var4 = this.database.getAllSynonyms().iterator();

      while(var4.hasNext()) {
         TableSynonym var5 = (TableSynonym)var4.next();
         this.add(var1, var2, new Object[]{var3, var5.getSchema().getName(), var5.getName(), var5.getSynonymForName(), var5.getSynonymForSchema().getName(), "SYNONYM", "VALID", var5.getComment()});
      }

   }

   private void users(SessionLocal var1, ArrayList<Row> var2) {
      User var3 = var1.getUser();
      if (var3.isAdmin()) {
         Iterator var4 = this.database.getAllUsersAndRoles().iterator();

         while(var4.hasNext()) {
            RightOwner var5 = (RightOwner)var4.next();
            if (var5 instanceof User) {
               this.users(var1, var2, (User)var5);
            }
         }
      } else {
         this.users(var1, var2, var3);
      }

   }

   private void users(SessionLocal var1, ArrayList<Row> var2, User var3) {
      this.add(var1, var2, new Object[]{this.identifier(var3.getName()), ValueBoolean.get(var3.isAdmin()), var3.getComment()});
   }

   private void addConstraintColumnUsage(SessionLocal var1, ArrayList<Row> var2, String var3, Constraint var4, Column var5) {
      Table var6 = var5.getTable();
      this.add(var1, var2, new Object[]{var3, var6.getSchema().getName(), var6.getName(), var5.getName(), var3, var4.getSchema().getName(), var4.getName()});
   }

   private void addPrivileges(SessionLocal var1, ArrayList<Row> var2, DbObject var3, String var4, Table var5, String var6, int var7) {
      if ((var7 & 1) != 0) {
         this.addPrivilege(var1, var2, var3, var4, var5, var6, "SELECT");
      }

      if ((var7 & 4) != 0) {
         this.addPrivilege(var1, var2, var3, var4, var5, var6, "INSERT");
      }

      if ((var7 & 8) != 0) {
         this.addPrivilege(var1, var2, var3, var4, var5, var6, "UPDATE");
      }

      if ((var7 & 2) != 0) {
         this.addPrivilege(var1, var2, var3, var4, var5, var6, "DELETE");
      }

   }

   private void addPrivilege(SessionLocal var1, ArrayList<Row> var2, DbObject var3, String var4, Table var5, String var6, String var7) {
      String var8 = "NO";
      if (var3.getType() == 2) {
         User var9 = (User)var3;
         if (var9.isAdmin()) {
            var8 = "YES";
         }
      }

      if (var6 == null) {
         this.add(var1, var2, new Object[]{null, this.identifier(var3.getName()), var4, var5.getSchema().getName(), var5.getName(), var7, var8, "NO"});
      } else {
         this.add(var1, var2, new Object[]{null, this.identifier(var3.getName()), var4, var5.getSchema().getName(), var5.getName(), var6, var7, var8});
      }

   }

   public long getMaxDataModificationId() {
      switch (this.type) {
         case 15:
         case 25:
         case 26:
         case 30:
         case 31:
         case 32:
            return Long.MAX_VALUE;
         case 16:
         case 17:
         case 18:
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
         case 24:
         case 27:
         case 28:
         case 29:
         default:
            return this.database.getModificationDataId();
      }
   }

   public boolean isView() {
      return this.isView;
   }

   public long getRowCount(SessionLocal var1) {
      return this.getRowCount(var1, false);
   }

   public long getRowCountApproximation(SessionLocal var1) {
      return this.getRowCount(var1, true);
   }

   private long getRowCount(SessionLocal var1, boolean var2) {
      long var3;
      Iterator var5;
      RightOwner var6;
      switch (this.type) {
         case 0:
            return 1L;
         case 2:
            Locale[] var8 = CompareMode.getCollationLocales(var2);
            if (var8 != null) {
               return (long)(var8.length + 1);
            }
            break;
         case 14:
            return (long)var1.getDatabase().getAllSchemas().size();
         case 25:
            if (var1.getUser().isAdmin()) {
               ArrayList var7 = var1.getDatabase().getInDoubtTransactions();
               if (var7 != null) {
                  return (long)var7.size();
               }
            }

            return 0L;
         case 29:
            if (var1.getUser().isAdmin()) {
               var3 = 0L;
               var5 = var1.getDatabase().getAllUsersAndRoles().iterator();

               while(var5.hasNext()) {
                  var6 = (RightOwner)var5.next();
                  if (var6 instanceof Role) {
                     ++var3;
                  }
               }

               return var3;
            }
            break;
         case 30:
            if (var1.getUser().isAdmin()) {
               return (long)var1.getDatabase().getSessionCount();
            }

            return 1L;
         case 34:
            if (var1.getUser().isAdmin()) {
               var3 = 0L;
               var5 = var1.getDatabase().getAllUsersAndRoles().iterator();

               while(var5.hasNext()) {
                  var6 = (RightOwner)var5.next();
                  if (var6 instanceof User) {
                     ++var3;
                  }
               }

               return var3;
            }

            return 1L;
      }

      if (var2) {
         return 1000L;
      } else {
         throw DbException.getInternalError(this.toString());
      }
   }

   public boolean canGetRowCount(SessionLocal var1) {
      switch (this.type) {
         case 0:
         case 2:
         case 14:
         case 25:
         case 30:
         case 34:
            return true;
         case 29:
            if (var1.getUser().isAdmin()) {
               return true;
            }
         default:
            return false;
      }
   }

   static final class DataTypeInformation {
      static final DataTypeInformation NULL = new DataTypeInformation((String)null, (Value)null, (Value)null, (Value)null, (Value)null, (Value)null, (Value)null, (Value)null, (Value)null, false, (String)null, (Value)null, (Value)null, (String)null, (Value)null);
      final String dataType;
      final Value characterPrecision;
      final Value numericPrecision;
      final Value numericPrecisionRadix;
      final Value numericScale;
      final Value datetimePrecision;
      final Value intervalPrecision;
      final Value intervalType;
      final Value maximumCardinality;
      final boolean hasCharsetAndCollation;
      final String declaredDataType;
      final Value declaredNumericPrecision;
      final Value declaredNumericScale;
      final String geometryType;
      final Value geometrySrid;

      static DataTypeInformation valueOf(TypeInfo var0) {
         int var1 = var0.getValueType();
         String var2 = Value.getTypeName(var1);
         ValueBigint var3 = null;
         ValueInteger var4 = null;
         ValueInteger var5 = null;
         ValueInteger var6 = null;
         ValueInteger var7 = null;
         ValueInteger var8 = null;
         ValueInteger var9 = null;
         String var10 = null;
         boolean var11 = false;
         String var12 = null;
         ValueInteger var13 = null;
         ValueInteger var14 = null;
         String var15 = null;
         ValueInteger var16 = null;
         switch (var1) {
            case 1:
            case 2:
            case 3:
            case 4:
               var11 = true;
            case 5:
            case 6:
            case 7:
            case 35:
            case 38:
               var3 = ValueBigint.get(var0.getPrecision());
            case 8:
            case 36:
            case 39:
            default:
               break;
            case 9:
            case 10:
            case 11:
            case 12:
               var4 = ValueInteger.get(MathUtils.convertLongToInt(var0.getPrecision()));
               var5 = ValueInteger.get(0);
               var6 = ValueInteger.get(2);
               var12 = var2;
               break;
            case 13:
               var4 = ValueInteger.get(MathUtils.convertLongToInt(var0.getPrecision()));
               var5 = ValueInteger.get(var0.getScale());
               var6 = ValueInteger.get(10);
               var12 = var0.getExtTypeInfo() != null ? "DECIMAL" : "NUMERIC";
               if (var0.getDeclaredPrecision() >= 0L) {
                  var13 = var4;
               }

               if (var0.getDeclaredScale() >= 0) {
                  var14 = var5;
               }
               break;
            case 14:
            case 15:
               var4 = ValueInteger.get(MathUtils.convertLongToInt(var0.getPrecision()));
               var6 = ValueInteger.get(2);
               long var20 = var0.getDeclaredPrecision();
               if (var20 >= 0L) {
                  var12 = "FLOAT";
                  if (var20 > 0L) {
                     var13 = ValueInteger.get((int)var20);
                  }
               } else {
                  var12 = var2;
               }
               break;
            case 16:
               var4 = ValueInteger.get(MathUtils.convertLongToInt(var0.getPrecision()));
               var6 = ValueInteger.get(10);
               var12 = var2;
               if (var0.getDeclaredPrecision() >= 0L) {
                  var13 = var4;
               }
               break;
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
               var10 = IntervalQualifier.valueOf(var1 - 22).toString();
               var2 = "INTERVAL";
               var8 = ValueInteger.get(MathUtils.convertLongToInt(var0.getPrecision()));
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
               var7 = ValueInteger.get(var0.getScale());
               break;
            case 37:
               ExtTypeInfoGeometry var17 = (ExtTypeInfoGeometry)var0.getExtTypeInfo();
               if (var17 != null) {
                  int var18 = var17.getType();
                  if (var18 != 0) {
                     var15 = EWKTUtils.formatGeometryTypeAndDimensionSystem(new StringBuilder(), var18).toString();
                  }

                  Integer var19 = var17.getSrid();
                  if (var19 != null) {
                     var16 = ValueInteger.get(var19);
                  }
               }
               break;
            case 40:
               var9 = ValueInteger.get(MathUtils.convertLongToInt(var0.getPrecision()));
         }

         return new DataTypeInformation(var2, var3, var4, var6, var5, var7, var8, (Value)(var10 != null ? ValueVarchar.get(var10) : ValueNull.INSTANCE), var9, var11, var12, var13, var14, var15, var16);
      }

      private DataTypeInformation(String var1, Value var2, Value var3, Value var4, Value var5, Value var6, Value var7, Value var8, Value var9, boolean var10, String var11, Value var12, Value var13, String var14, Value var15) {
         this.dataType = var1;
         this.characterPrecision = var2;
         this.numericPrecision = var3;
         this.numericPrecisionRadix = var4;
         this.numericScale = var5;
         this.datetimePrecision = var6;
         this.intervalPrecision = var7;
         this.intervalType = var8;
         this.maximumCardinality = var9;
         this.hasCharsetAndCollation = var10;
         this.declaredDataType = var11;
         this.declaredNumericPrecision = var12;
         this.declaredNumericScale = var13;
         this.geometryType = var14;
         this.geometrySrid = var15;
      }
   }
}
