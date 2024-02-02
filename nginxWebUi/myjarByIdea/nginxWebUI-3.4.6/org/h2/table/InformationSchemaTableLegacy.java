package org.h2.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.h2.command.Command;
import org.h2.command.Parser;
import org.h2.command.dml.Help;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
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
import org.h2.schema.SchemaObject;
import org.h2.schema.Sequence;
import org.h2.schema.TriggerObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.store.InDoubtTransaction;
import org.h2.tools.Csv;
import org.h2.util.DateTimeUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueToObjectConverter2;

public final class InformationSchemaTableLegacy extends MetaTable {
   private static final String CHARACTER_SET_NAME = "Unicode";
   private static final int TABLES = 0;
   private static final int COLUMNS = 1;
   private static final int INDEXES = 2;
   private static final int TABLE_TYPES = 3;
   private static final int TYPE_INFO = 4;
   private static final int CATALOGS = 5;
   private static final int SETTINGS = 6;
   private static final int HELP = 7;
   private static final int SEQUENCES = 8;
   private static final int USERS = 9;
   private static final int ROLES = 10;
   private static final int RIGHTS = 11;
   private static final int FUNCTION_ALIASES = 12;
   private static final int SCHEMATA = 13;
   private static final int TABLE_PRIVILEGES = 14;
   private static final int COLUMN_PRIVILEGES = 15;
   private static final int COLLATIONS = 16;
   private static final int VIEWS = 17;
   private static final int IN_DOUBT = 18;
   private static final int CROSS_REFERENCES = 19;
   private static final int FUNCTION_COLUMNS = 20;
   private static final int CONSTRAINTS = 21;
   private static final int CONSTANTS = 22;
   private static final int DOMAINS = 23;
   private static final int TRIGGERS = 24;
   private static final int SESSIONS = 25;
   private static final int LOCKS = 26;
   private static final int SESSION_STATE = 27;
   private static final int QUERY_STATISTICS = 28;
   private static final int SYNONYMS = 29;
   private static final int TABLE_CONSTRAINTS = 30;
   private static final int DOMAIN_CONSTRAINTS = 31;
   private static final int KEY_COLUMN_USAGE = 32;
   private static final int REFERENTIAL_CONSTRAINTS = 33;
   private static final int CHECK_CONSTRAINTS = 34;
   private static final int CONSTRAINT_COLUMN_USAGE = 35;
   public static final int META_TABLE_TYPE_COUNT = 36;

   public InformationSchemaTableLegacy(Schema var1, int var2, int var3) {
      super(var1, var2, var3);
      String var5 = null;
      Column[] var4;
      switch (var3) {
         case 0:
            this.setMetaTableName("TABLES");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("TABLE_TYPE"), this.column("STORAGE_TYPE"), this.column("SQL"), this.column("REMARKS"), this.column("LAST_MODIFICATION", TypeInfo.TYPE_BIGINT), this.column("ID", TypeInfo.TYPE_INTEGER), this.column("TYPE_NAME"), this.column("TABLE_CLASS"), this.column("ROW_COUNT_ESTIMATE", TypeInfo.TYPE_BIGINT)};
            var5 = "TABLE_NAME";
            break;
         case 1:
            this.setMetaTableName("COLUMNS");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("COLUMN_DEFAULT"), this.column("IS_NULLABLE"), this.column("DATA_TYPE", TypeInfo.TYPE_INTEGER), this.column("CHARACTER_MAXIMUM_LENGTH", TypeInfo.TYPE_INTEGER), this.column("CHARACTER_OCTET_LENGTH", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("DATETIME_PRECISION", TypeInfo.TYPE_INTEGER), this.column("INTERVAL_TYPE"), this.column("INTERVAL_PRECISION", TypeInfo.TYPE_INTEGER), this.column("CHARACTER_SET_NAME"), this.column("COLLATION_NAME"), this.column("DOMAIN_CATALOG"), this.column("DOMAIN_SCHEMA"), this.column("DOMAIN_NAME"), this.column("IS_GENERATED"), this.column("GENERATION_EXPRESSION"), this.column("TYPE_NAME"), this.column("NULLABLE", TypeInfo.TYPE_INTEGER), this.column("IS_COMPUTED", TypeInfo.TYPE_BOOLEAN), this.column("SELECTIVITY", TypeInfo.TYPE_INTEGER), this.column("SEQUENCE_NAME"), this.column("REMARKS"), this.column("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT), this.column("COLUMN_TYPE"), this.column("COLUMN_ON_UPDATE"), this.column("IS_VISIBLE"), this.column("CHECK_CONSTRAINT")};
            var5 = "TABLE_NAME";
            break;
         case 2:
            this.setMetaTableName("INDEXES");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("NON_UNIQUE", TypeInfo.TYPE_BOOLEAN), this.column("INDEX_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT), this.column("COLUMN_NAME"), this.column("CARDINALITY", TypeInfo.TYPE_INTEGER), this.column("PRIMARY_KEY", TypeInfo.TYPE_BOOLEAN), this.column("INDEX_TYPE_NAME"), this.column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), this.column("INDEX_TYPE", TypeInfo.TYPE_SMALLINT), this.column("ASC_OR_DESC"), this.column("PAGES", TypeInfo.TYPE_INTEGER), this.column("FILTER_CONDITION"), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER), this.column("SORT_TYPE", TypeInfo.TYPE_INTEGER), this.column("CONSTRAINT_NAME"), this.column("INDEX_CLASS")};
            var5 = "TABLE_NAME";
            break;
         case 3:
            this.setMetaTableName("TABLE_TYPES");
            var4 = new Column[]{this.column("TYPE")};
            break;
         case 4:
            this.setMetaTableName("TYPE_INFO");
            var4 = new Column[]{this.column("TYPE_NAME"), this.column("DATA_TYPE", TypeInfo.TYPE_INTEGER), this.column("PRECISION", TypeInfo.TYPE_INTEGER), this.column("PREFIX"), this.column("SUFFIX"), this.column("PARAMS"), this.column("AUTO_INCREMENT", TypeInfo.TYPE_BOOLEAN), this.column("MINIMUM_SCALE", TypeInfo.TYPE_SMALLINT), this.column("MAXIMUM_SCALE", TypeInfo.TYPE_SMALLINT), this.column("RADIX", TypeInfo.TYPE_INTEGER), this.column("POS", TypeInfo.TYPE_INTEGER), this.column("CASE_SENSITIVE", TypeInfo.TYPE_BOOLEAN), this.column("NULLABLE", TypeInfo.TYPE_SMALLINT), this.column("SEARCHABLE", TypeInfo.TYPE_SMALLINT)};
            break;
         case 5:
            this.setMetaTableName("CATALOGS");
            var4 = new Column[]{this.column("CATALOG_NAME")};
            break;
         case 6:
            this.setMetaTableName("SETTINGS");
            var4 = new Column[]{this.column("NAME"), this.column("VALUE")};
            break;
         case 7:
            this.setMetaTableName("HELP");
            var4 = new Column[]{this.column("ID", TypeInfo.TYPE_INTEGER), this.column("SECTION"), this.column("TOPIC"), this.column("SYNTAX"), this.column("TEXT")};
            break;
         case 8:
            this.setMetaTableName("SEQUENCES");
            var4 = new Column[]{this.column("SEQUENCE_CATALOG"), this.column("SEQUENCE_SCHEMA"), this.column("SEQUENCE_NAME"), this.column("DATA_TYPE"), this.column("NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_PRECISION_RADIX", TypeInfo.TYPE_INTEGER), this.column("NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("START_VALUE", TypeInfo.TYPE_BIGINT), this.column("MINIMUM_VALUE", TypeInfo.TYPE_BIGINT), this.column("MAXIMUM_VALUE", TypeInfo.TYPE_BIGINT), this.column("INCREMENT", TypeInfo.TYPE_BIGINT), this.column("CYCLE_OPTION"), this.column("DECLARED_DATA_TYPE"), this.column("DECLARED_NUMERIC_PRECISION", TypeInfo.TYPE_INTEGER), this.column("DECLARED_NUMERIC_SCALE", TypeInfo.TYPE_INTEGER), this.column("CURRENT_VALUE", TypeInfo.TYPE_BIGINT), this.column("IS_GENERATED", TypeInfo.TYPE_BOOLEAN), this.column("REMARKS"), this.column("CACHE", TypeInfo.TYPE_BIGINT), this.column("ID", TypeInfo.TYPE_INTEGER), this.column("MIN_VALUE", TypeInfo.TYPE_BIGINT), this.column("MAX_VALUE", TypeInfo.TYPE_BIGINT), this.column("IS_CYCLE", TypeInfo.TYPE_BOOLEAN)};
            break;
         case 9:
            this.setMetaTableName("USERS");
            var4 = new Column[]{this.column("NAME"), this.column("ADMIN"), this.column("REMARKS"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            break;
         case 10:
            this.setMetaTableName("ROLES");
            var4 = new Column[]{this.column("NAME"), this.column("REMARKS"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            break;
         case 11:
            this.setMetaTableName("RIGHTS");
            var4 = new Column[]{this.column("GRANTEE"), this.column("GRANTEETYPE"), this.column("GRANTEDROLE"), this.column("RIGHTS"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            var5 = "TABLE_NAME";
            break;
         case 12:
            this.setMetaTableName("FUNCTION_ALIASES");
            var4 = new Column[]{this.column("ALIAS_CATALOG"), this.column("ALIAS_SCHEMA"), this.column("ALIAS_NAME"), this.column("JAVA_CLASS"), this.column("JAVA_METHOD"), this.column("DATA_TYPE", TypeInfo.TYPE_INTEGER), this.column("TYPE_NAME"), this.column("COLUMN_COUNT", TypeInfo.TYPE_INTEGER), this.column("RETURNS_RESULT", TypeInfo.TYPE_SMALLINT), this.column("REMARKS"), this.column("ID", TypeInfo.TYPE_INTEGER), this.column("SOURCE")};
            break;
         case 13:
            this.setMetaTableName("SCHEMATA");
            var4 = new Column[]{this.column("CATALOG_NAME"), this.column("SCHEMA_NAME"), this.column("SCHEMA_OWNER"), this.column("DEFAULT_CHARACTER_SET_NAME"), this.column("DEFAULT_COLLATION_NAME"), this.column("IS_DEFAULT", TypeInfo.TYPE_BOOLEAN), this.column("REMARKS"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            break;
         case 14:
            this.setMetaTableName("TABLE_PRIVILEGES");
            var4 = new Column[]{this.column("GRANTOR"), this.column("GRANTEE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("PRIVILEGE_TYPE"), this.column("IS_GRANTABLE")};
            var5 = "TABLE_NAME";
            break;
         case 15:
            this.setMetaTableName("COLUMN_PRIVILEGES");
            var4 = new Column[]{this.column("GRANTOR"), this.column("GRANTEE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("PRIVILEGE_TYPE"), this.column("IS_GRANTABLE")};
            var5 = "TABLE_NAME";
            break;
         case 16:
            this.setMetaTableName("COLLATIONS");
            var4 = new Column[]{this.column("NAME"), this.column("KEY")};
            break;
         case 17:
            this.setMetaTableName("VIEWS");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("VIEW_DEFINITION"), this.column("CHECK_OPTION"), this.column("IS_UPDATABLE"), this.column("STATUS"), this.column("REMARKS"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            var5 = "TABLE_NAME";
            break;
         case 18:
            this.setMetaTableName("IN_DOUBT");
            var4 = new Column[]{this.column("TRANSACTION"), this.column("STATE")};
            break;
         case 19:
            this.setMetaTableName("CROSS_REFERENCES");
            var4 = new Column[]{this.column("PKTABLE_CATALOG"), this.column("PKTABLE_SCHEMA"), this.column("PKTABLE_NAME"), this.column("PKCOLUMN_NAME"), this.column("FKTABLE_CATALOG"), this.column("FKTABLE_SCHEMA"), this.column("FKTABLE_NAME"), this.column("FKCOLUMN_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT), this.column("UPDATE_RULE", TypeInfo.TYPE_SMALLINT), this.column("DELETE_RULE", TypeInfo.TYPE_SMALLINT), this.column("FK_NAME"), this.column("PK_NAME"), this.column("DEFERRABILITY", TypeInfo.TYPE_SMALLINT)};
            var5 = "PKTABLE_NAME";
            break;
         case 20:
            this.setMetaTableName("FUNCTION_COLUMNS");
            var4 = new Column[]{this.column("ALIAS_CATALOG"), this.column("ALIAS_SCHEMA"), this.column("ALIAS_NAME"), this.column("JAVA_CLASS"), this.column("JAVA_METHOD"), this.column("COLUMN_COUNT", TypeInfo.TYPE_INTEGER), this.column("POS", TypeInfo.TYPE_INTEGER), this.column("COLUMN_NAME"), this.column("DATA_TYPE", TypeInfo.TYPE_INTEGER), this.column("TYPE_NAME"), this.column("PRECISION", TypeInfo.TYPE_INTEGER), this.column("SCALE", TypeInfo.TYPE_SMALLINT), this.column("RADIX", TypeInfo.TYPE_SMALLINT), this.column("NULLABLE", TypeInfo.TYPE_SMALLINT), this.column("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT), this.column("REMARKS"), this.column("COLUMN_DEFAULT")};
            break;
         case 21:
            this.setMetaTableName("CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("CONSTRAINT_TYPE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("UNIQUE_INDEX_NAME"), this.column("CHECK_EXPRESSION"), this.column("COLUMN_LIST"), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            var5 = "TABLE_NAME";
            break;
         case 22:
            this.setMetaTableName("CONSTANTS");
            var4 = new Column[]{this.column("CONSTANT_CATALOG"), this.column("CONSTANT_SCHEMA"), this.column("CONSTANT_NAME"), this.column("DATA_TYPE", TypeInfo.TYPE_INTEGER), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            break;
         case 23:
            this.setMetaTableName("DOMAINS");
            var4 = new Column[]{this.column("DOMAIN_CATALOG"), this.column("DOMAIN_SCHEMA"), this.column("DOMAIN_NAME"), this.column("DOMAIN_DEFAULT"), this.column("DOMAIN_ON_UPDATE"), this.column("DATA_TYPE", TypeInfo.TYPE_INTEGER), this.column("PRECISION", TypeInfo.TYPE_INTEGER), this.column("SCALE", TypeInfo.TYPE_INTEGER), this.column("TYPE_NAME"), this.column("PARENT_DOMAIN_CATALOG"), this.column("PARENT_DOMAIN_SCHEMA"), this.column("PARENT_DOMAIN_NAME"), this.column("SELECTIVITY", TypeInfo.TYPE_INTEGER), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER), this.column("COLUMN_DEFAULT"), this.column("IS_NULLABLE"), this.column("CHECK_CONSTRAINT")};
            break;
         case 24:
            this.setMetaTableName("TRIGGERS");
            var4 = new Column[]{this.column("TRIGGER_CATALOG"), this.column("TRIGGER_SCHEMA"), this.column("TRIGGER_NAME"), this.column("TRIGGER_TYPE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("BEFORE", TypeInfo.TYPE_BOOLEAN), this.column("JAVA_CLASS"), this.column("QUEUE_SIZE", TypeInfo.TYPE_INTEGER), this.column("NO_WAIT", TypeInfo.TYPE_BOOLEAN), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            break;
         case 25:
            this.setMetaTableName("SESSIONS");
            var4 = new Column[]{this.column("ID", TypeInfo.TYPE_INTEGER), this.column("USER_NAME"), this.column("SERVER"), this.column("CLIENT_ADDR"), this.column("CLIENT_INFO"), this.column("SESSION_START", TypeInfo.TYPE_TIMESTAMP_TZ), this.column("ISOLATION_LEVEL"), this.column("STATEMENT"), this.column("STATEMENT_START", TypeInfo.TYPE_TIMESTAMP_TZ), this.column("CONTAINS_UNCOMMITTED", TypeInfo.TYPE_BOOLEAN), this.column("STATE"), this.column("BLOCKER_ID", TypeInfo.TYPE_INTEGER), this.column("SLEEP_SINCE", TypeInfo.TYPE_TIMESTAMP_TZ)};
            break;
         case 26:
            this.setMetaTableName("LOCKS");
            var4 = new Column[]{this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("SESSION_ID", TypeInfo.TYPE_INTEGER), this.column("LOCK_TYPE")};
            break;
         case 27:
            this.setMetaTableName("SESSION_STATE");
            var4 = new Column[]{this.column("KEY"), this.column("SQL")};
            break;
         case 28:
            this.setMetaTableName("QUERY_STATISTICS");
            var4 = new Column[]{this.column("SQL_STATEMENT"), this.column("EXECUTION_COUNT", TypeInfo.TYPE_INTEGER), this.column("MIN_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("MAX_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("CUMULATIVE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("AVERAGE_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("STD_DEV_EXECUTION_TIME", TypeInfo.TYPE_DOUBLE), this.column("MIN_ROW_COUNT", TypeInfo.TYPE_BIGINT), this.column("MAX_ROW_COUNT", TypeInfo.TYPE_BIGINT), this.column("CUMULATIVE_ROW_COUNT", TypeInfo.TYPE_BIGINT), this.column("AVERAGE_ROW_COUNT", TypeInfo.TYPE_DOUBLE), this.column("STD_DEV_ROW_COUNT", TypeInfo.TYPE_DOUBLE)};
            break;
         case 29:
            this.setMetaTableName("SYNONYMS");
            var4 = new Column[]{this.column("SYNONYM_CATALOG"), this.column("SYNONYM_SCHEMA"), this.column("SYNONYM_NAME"), this.column("SYNONYM_FOR"), this.column("SYNONYM_FOR_SCHEMA"), this.column("TYPE_NAME"), this.column("STATUS"), this.column("REMARKS"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            var5 = "SYNONYM_NAME";
            break;
         case 30:
            this.setMetaTableName("TABLE_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("CONSTRAINT_TYPE"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("IS_DEFERRABLE"), this.column("INITIALLY_DEFERRED"), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            var5 = "TABLE_NAME";
            break;
         case 31:
            this.setMetaTableName("DOMAIN_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("DOMAIN_CATALOG"), this.column("DOMAIN_SCHEMA"), this.column("DOMAIN_NAME"), this.column("IS_DEFERRABLE"), this.column("INITIALLY_DEFERRED"), this.column("REMARKS"), this.column("SQL"), this.column("ID", TypeInfo.TYPE_INTEGER)};
            break;
         case 32:
            this.setMetaTableName("KEY_COLUMN_USAGE");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER), this.column("POSITION_IN_UNIQUE_CONSTRAINT", TypeInfo.TYPE_INTEGER), this.column("INDEX_CATALOG"), this.column("INDEX_SCHEMA"), this.column("INDEX_NAME")};
            var5 = "TABLE_NAME";
            break;
         case 33:
            this.setMetaTableName("REFERENTIAL_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("UNIQUE_CONSTRAINT_CATALOG"), this.column("UNIQUE_CONSTRAINT_SCHEMA"), this.column("UNIQUE_CONSTRAINT_NAME"), this.column("MATCH_OPTION"), this.column("UPDATE_RULE"), this.column("DELETE_RULE")};
            break;
         case 34:
            this.setMetaTableName("CHECK_CONSTRAINTS");
            var4 = new Column[]{this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME"), this.column("CHECK_CLAUSE")};
            break;
         case 35:
            this.setMetaTableName("CONSTRAINT_COLUMN_USAGE");
            var4 = new Column[]{this.column("TABLE_CATALOG"), this.column("TABLE_SCHEMA"), this.column("TABLE_NAME"), this.column("COLUMN_NAME"), this.column("CONSTRAINT_CATALOG"), this.column("CONSTRAINT_SCHEMA"), this.column("CONSTRAINT_NAME")};
            var5 = "TABLE_NAME";
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
         IndexColumn[] var6 = IndexColumn.wrap(new Column[]{var4[this.indexColumn]});
         this.metaIndex = new MetaIndex(this, var6, false);
      }

   }

   private static String replaceNullWithEmpty(String var0) {
      return var0 == null ? "" : var0;
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
      boolean var8 = var1.getUser().isAdmin();
      Iterator var9;
      SchemaObject var10;
      Constraint var11;
      Table var12;
      Iterator var13;
      Table var15;
      int var18;
      ValueInteger var20;
      String[] var32;
      Iterator var35;
      SessionLocal[] var37;
      int var38;
      Constraint.Type var40;
      Table var42;
      int var43;
      String var45;
      Domain var46;
      String var49;
      IndexColumn[] var50;
      Table var52;
      int var53;
      ArrayList var54;
      SessionLocal var55;
      int var57;
      String var58;
      Schema var60;
      FunctionAlias.JavaMethod[] var65;
      IndexColumn[] var69;
      String var70;
      Index var72;
      String var75;
      UserDefinedFunction var76;
      int var77;
      Right var78;
      Iterator var79;
      String var80;
      Table var82;
      FunctionAlias.JavaMethod[] var88;
      int var89;
      String var90;
      String var91;
      FunctionAlias var92;
      DbObject var93;
      FunctionAlias.JavaMethod var95;
      RightOwner var96;
      DbObject var99;
      TypeInfo var110;
      Role var118;
      int var130;
      Table var151;
      switch (this.type) {
         case 0:
            var9 = this.getAllTables(var1).iterator();

            while(var9.hasNext()) {
               var42 = (Table)var9.next();
               var90 = var42.getName();
               if (this.checkIndex(var1, var90, var4, var5) && !this.hideTable(var42, var1)) {
                  if (var42.isTemporary()) {
                     if (var42.isGlobalTemporary()) {
                        var49 = "GLOBAL TEMPORARY";
                     } else {
                        var49 = "LOCAL TEMPORARY";
                     }
                  } else {
                     var49 = var42.isPersistIndexes() ? "CACHED" : "MEMORY";
                  }

                  var80 = var42.getCreateSQL();
                  if (!var8 && var80 != null && var80.contains("--hide--")) {
                     var80 = "-";
                  }

                  this.add(var1, var6, new Object[]{var7, var42.getSchema().getName(), var90, var42.getTableType().toString(), var49, var80, replaceNullWithEmpty(var42.getComment()), ValueBigint.get(var42.getMaxDataModificationId()), ValueInteger.get(var42.getId()), null, var42.getClass().getName(), ValueBigint.get(var42.getRowCountApproximation(var1))});
               }
            }

            return var6;
         case 1:
            if (var4 != null && var4.equals(var5)) {
               var45 = var4.getString();
               if (var45 == null) {
                  break;
               }

               var54 = this.getTablesByName(var1, var45);
            } else {
               var54 = this.getAllTables(var1);
            }

            var35 = var54.iterator();

            while(true) {
               do {
                  do {
                     if (!var35.hasNext()) {
                        return var6;
                     }

                     var151 = (Table)var35.next();
                     var49 = var151.getName();
                  } while(!this.checkIndex(var1, var49, var4, var5));
               } while(this.hideTable(var151, var1));

               Column[] var133 = var151.getColumns();
               var58 = this.database.getCompareMode().getName();

               for(var57 = 0; var57 < var133.length; ++var57) {
                  Column var114 = var133[var57];
                  Domain var134 = var114.getDomain();
                  TypeInfo var142 = var114.getType();
                  ValueInteger var139 = ValueInteger.get(MathUtils.convertLongToInt(var142.getPrecision()));
                  var20 = ValueInteger.get(var142.getScale());
                  Sequence var146 = var114.getSequence();
                  int var147 = var142.getValueType();
                  boolean var143;
                  switch (var147) {
                     case 17:
                     case 18:
                     case 19:
                     case 20:
                     case 21:
                     case 27:
                     case 31:
                     case 33:
                     case 34:
                        var143 = true;
                        break;
                     case 22:
                     case 23:
                     case 24:
                     case 25:
                     case 26:
                     case 28:
                     case 29:
                     case 30:
                     case 32:
                     default:
                        var143 = false;
                  }

                  boolean var152 = var114.isGenerated();
                  boolean var25 = DataType.isIntervalType(var147);
                  String var26 = var114.getCreateSQLWithoutName();
                  this.add(var1, var6, new Object[]{var7, var151.getSchema().getName(), var49, var114.getName(), ValueInteger.get(var57 + 1), var152 ? null : var114.getDefaultSQL(), var114.isNullable() ? "YES" : "NO", ValueInteger.get(DataType.convertTypeToSQLType(var142)), var139, var139, var139, ValueInteger.get(10), var20, var143 ? var20 : null, var25 ? var26.substring(9) : null, var25 ? var139 : null, "Unicode", var58, var134 != null ? var7 : null, var134 != null ? var134.getSchema().getName() : null, var134 != null ? var134.getName() : null, var152 ? "ALWAYS" : "NEVER", var152 ? var114.getDefaultSQL() : null, this.identifier(var25 ? "INTERVAL" : var142.getDeclaredTypeName()), ValueInteger.get(var114.isNullable() ? 1 : 0), ValueBoolean.get(var152), ValueInteger.get(var114.getSelectivity()), var146 == null ? null : var146.getName(), replaceNullWithEmpty(var114.getComment()), null, var26, var114.getOnUpdateSQL(), ValueBoolean.get(var114.getVisible()), null});
               }
            }
         case 2:
            if (var4 != null && var4.equals(var5)) {
               var45 = var4.getString();
               if (var45 == null) {
                  break;
               }

               var54 = this.getTablesByName(var1, var45);
            } else {
               var54 = this.getAllTables(var1);
            }

            var35 = var54.iterator();

            while(true) {
               do {
                  do {
                     if (!var35.hasNext()) {
                        return var6;
                     }

                     var151 = (Table)var35.next();
                     var49 = var151.getName();
                  } while(!this.checkIndex(var1, var49, var4, var5));
               } while(this.hideTable(var151, var1));

               ArrayList var128 = var151.getIndexes();
               ArrayList var113 = var151.getConstraints();

               for(var57 = 0; var128 != null && var57 < var128.size(); ++var57) {
                  var72 = (Index)var128.get(var57);
                  if (var72.getCreateSQL() != null) {
                     var75 = null;

                     for(var18 = 0; var113 != null && var18 < var113.size(); ++var18) {
                        Constraint var136 = (Constraint)var113.get(var18);
                        if (var136.usesIndex(var72)) {
                           if (var72.getIndexType().isPrimaryKey()) {
                              if (var136.getConstraintType() == Constraint.Type.PRIMARY_KEY) {
                                 var75 = var136.getName();
                              }
                           } else {
                              var75 = var136.getName();
                           }
                        }
                     }

                     IndexColumn[] var140 = var72.getIndexColumns();
                     int var137 = var72.getUniqueColumnCount();
                     String var131 = var72.getClass().getName();

                     for(var130 = 0; var130 < var140.length; ++var130) {
                        IndexColumn var141 = var140[var130];
                        Column var145 = var141.column;
                        this.add(var1, var6, new Object[]{var7, var151.getSchema().getName(), var49, ValueBoolean.get(var130 >= var137), var72.getName(), ValueSmallint.get((short)(var130 + 1)), var145.getName(), ValueInteger.get(0), ValueBoolean.get(var72.getIndexType().isPrimaryKey()), var72.getIndexType().getSQL(), ValueBoolean.get(var72.getIndexType().getBelongsToConstraint()), ValueSmallint.get((short)3), (var141.sortType & 1) != 0 ? "D" : "A", ValueInteger.get(0), "", replaceNullWithEmpty(var72.getComment()), var72.getCreateSQL(), ValueInteger.get(var72.getId()), ValueInteger.get(var141.sortType), var75, var131});
                     }
                  }
               }
            }
         case 3:
            this.add(var1, var6, new Object[]{TableType.TABLE.toString()});
            this.add(var1, var6, new Object[]{TableType.TABLE_LINK.toString()});
            this.add(var1, var6, new Object[]{TableType.SYSTEM_TABLE.toString()});
            this.add(var1, var6, new Object[]{TableType.VIEW.toString()});
            this.add(var1, var6, new Object[]{TableType.EXTERNAL_TABLE_ENGINE.toString()});
            break;
         case 4:
            int var105 = 1;

            for(byte var126 = 42; var105 < var126; ++var105) {
               DataType var149 = DataType.getDataType(var105);
               this.add(var1, var6, new Object[]{Value.getTypeName(var149.type), ValueInteger.get(var149.sqlType), ValueInteger.get(MathUtils.convertLongToInt(var149.maxPrecision)), var149.prefix, var149.suffix, var149.params, ValueBoolean.FALSE, ValueSmallint.get(MathUtils.convertIntToShort(var149.minScale)), ValueSmallint.get(MathUtils.convertIntToShort(var149.maxScale)), DataType.isNumericType(var105) ? ValueInteger.get(10) : null, ValueInteger.get(var149.type), ValueBoolean.get(var149.caseSensitive), ValueSmallint.get((short)1), ValueSmallint.get((short)3)});
            }

            return var6;
         case 5:
            this.add(var1, var6, new Object[]{var7});
            break;
         case 6:
            Setting var111;
            for(var9 = this.database.getAllSettings().iterator(); var9.hasNext(); this.add(var1, var6, new Object[]{this.identifier(var111.getName()), var90})) {
               var111 = (Setting)var9.next();
               var90 = var111.getStringValue();
               if (var90 == null) {
                  var90 = Integer.toString(var111.getIntValue());
               }
            }

            this.add(var1, var6, new Object[]{"info.BUILD_ID", "210"});
            this.add(var1, var6, new Object[]{"info.VERSION_MAJOR", "2"});
            this.add(var1, var6, new Object[]{"info.VERSION_MINOR", "1"});
            this.add(var1, var6, new Object[]{"info.VERSION", Constants.FULL_VERSION});
            if (var8) {
               var32 = new String[]{"java.runtime.version", "java.vm.name", "java.vendor", "os.name", "os.arch", "os.version", "sun.os.patch.level", "file.separator", "path.separator", "line.separator", "user.country", "user.language", "user.variant", "file.encoding"};
               String[] var116 = var32;
               var43 = var32.length;

               for(var53 = 0; var53 < var43; ++var53) {
                  var80 = var116[var53];
                  this.add(var1, var6, new Object[]{"property." + var80, Utils.getProperty(var80, "")});
               }
            }

            this.add(var1, var6, new Object[]{"DEFAULT_NULL_ORDERING", this.database.getDefaultNullOrdering().name()});
            this.add(var1, var6, new Object[]{"EXCLUSIVE", this.database.getExclusiveSession() == null ? "FALSE" : "TRUE"});
            this.add(var1, var6, new Object[]{"MODE", this.database.getMode().getName()});
            this.add(var1, var6, new Object[]{"QUERY_TIMEOUT", Integer.toString(var1.getQueryTimeout())});
            this.add(var1, var6, new Object[]{"TIME ZONE", var1.currentTimeZone().getId()});
            this.add(var1, var6, new Object[]{"TRUNCATE_LARGE_LENGTH", var1.isTruncateLargeLength() ? "TRUE" : "FALSE"});
            this.add(var1, var6, new Object[]{"VARIABLE_BINARY", var1.isVariableBinary() ? "TRUE" : "FALSE"});
            this.add(var1, var6, new Object[]{"OLD_INFORMATION_SCHEMA", var1.isOldInformationSchema() ? "TRUE" : "FALSE"});
            BitSet var103 = var1.getNonKeywords();
            if (var103 != null) {
               this.add(var1, var6, new Object[]{"NON_KEYWORDS", Parser.formatNonKeywords(var103)});
            }

            this.add(var1, var6, new Object[]{"RETENTION_TIME", Integer.toString(this.database.getRetentionTime())});
            Map.Entry[] var121 = this.database.getSettings().getSortedSettings();
            var43 = var121.length;

            for(var53 = 0; var53 < var43; ++var53) {
               Map.Entry var125 = var121[var53];
               this.add(var1, var6, new Object[]{var125.getKey(), var125.getValue()});
            }

            Store var124 = this.database.getStore();
            MVStore var148 = var124.getMvStore();
            FileStore var129 = var148.getFileStore();
            if (var129 != null) {
               this.add(var1, var6, new Object[]{"info.FILE_WRITE", Long.toString(var129.getWriteCount())});
               this.add(var1, var6, new Object[]{"info.FILE_WRITE_BYTES", Long.toString(var129.getWriteBytes())});
               this.add(var1, var6, new Object[]{"info.FILE_READ", Long.toString(var129.getReadCount())});
               this.add(var1, var6, new Object[]{"info.FILE_READ_BYTES", Long.toString(var129.getReadBytes())});
               this.add(var1, var6, new Object[]{"info.UPDATE_FAILURE_PERCENT", String.format(Locale.ENGLISH, "%.2f%%", 100.0 * var148.getUpdateFailureRatio())});
               this.add(var1, var6, new Object[]{"info.FILL_RATE", Integer.toString(var148.getFillRate())});
               this.add(var1, var6, new Object[]{"info.CHUNKS_FILL_RATE", Integer.toString(var148.getChunksFillRate())});
               this.add(var1, var6, new Object[]{"info.CHUNKS_FILL_RATE_RW", Integer.toString(var148.getRewritableChunksFillRate())});

               try {
                  this.add(var1, var6, new Object[]{"info.FILE_SIZE", Long.toString(var129.getFile().size())});
               } catch (IOException var27) {
               }

               this.add(var1, var6, new Object[]{"info.CHUNK_COUNT", Long.toString((long)var148.getChunkCount())});
               this.add(var1, var6, new Object[]{"info.PAGE_COUNT", Long.toString((long)var148.getPageCount())});
               this.add(var1, var6, new Object[]{"info.PAGE_COUNT_LIVE", Long.toString((long)var148.getLivePageCount())});
               this.add(var1, var6, new Object[]{"info.PAGE_SIZE", Integer.toString(var148.getPageSplitSize())});
               this.add(var1, var6, new Object[]{"info.CACHE_MAX_SIZE", Integer.toString(var148.getCacheSize())});
               this.add(var1, var6, new Object[]{"info.CACHE_SIZE", Integer.toString(var148.getCacheSizeUsed())});
               this.add(var1, var6, new Object[]{"info.CACHE_HIT_RATIO", Integer.toString(var148.getCacheHitRatio())});
               this.add(var1, var6, new Object[]{"info.TOC_CACHE_HIT_RATIO", Integer.toString(var148.getTocCacheHitRatio())});
               this.add(var1, var6, new Object[]{"info.LEAF_RATIO", Integer.toString(var148.getLeafRatio())});
            }
            break;
         case 7:
            var70 = "/org/h2/res/help.csv";

            try {
               byte[] var106 = Utils.getResource(var70);
               InputStreamReader var132 = new InputStreamReader(new ByteArrayInputStream(var106));
               Csv var104 = new Csv();
               var104.setLineCommentCharacter('#');
               ResultSet var117 = var104.read(var132, (String[])null);
               int var108 = var117.getMetaData().getColumnCount() - 1;
               String[] var94 = new String[5];

               for(var77 = 0; var117.next(); ++var77) {
                  for(var89 = 0; var89 < var108; ++var89) {
                     var91 = var117.getString(1 + var89);
                     switch (var89) {
                        case 2:
                           var91 = Help.stripAnnotationsFromSyntax(var91);
                           break;
                        case 3:
                           var91 = Help.processHelpText(var91);
                     }

                     var94[var89] = var91.trim();
                  }

                  this.add(var1, var6, new Object[]{ValueInteger.get(var77), var94[0], var94[1], var94[2], var94[3]});
               }

               return var6;
            } catch (Exception var30) {
               throw DbException.convert(var30);
            }
         case 8:
            var9 = this.getAllSchemaObjects(3).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               Sequence var127 = (Sequence)var10;
               TypeInfo var101 = var127.getDataType();
               var80 = Value.getTypeName(var101.getValueType());
               ValueInteger var102 = ValueInteger.get(var101.getScale());
               this.add(var1, var6, new Object[]{var7, var127.getSchema().getName(), var127.getName(), var80, ValueInteger.get(var127.getEffectivePrecision()), ValueInteger.get(10), var102, ValueBigint.get(var127.getStartValue()), ValueBigint.get(var127.getMinValue()), ValueBigint.get(var127.getMaxValue()), ValueBigint.get(var127.getIncrement()), var127.getCycle().isCycle() ? "YES" : "NO", var80, ValueInteger.get((int)var101.getPrecision()), var102, ValueBigint.get(var127.getCurrentValue()), ValueBoolean.get(var127.getBelongsToTable()), replaceNullWithEmpty(var127.getComment()), ValueBigint.get(var127.getCacheSize()), ValueInteger.get(var127.getId()), ValueBigint.get(var127.getMinValue()), ValueBigint.get(var127.getMaxValue()), ValueBoolean.get(var127.getCycle().isCycle())});
            }

            return var6;
         case 9:
            var9 = this.database.getAllUsersAndRoles().iterator();

            while(true) {
               User var123;
               do {
                  do {
                     if (!var9.hasNext()) {
                        return var6;
                     }

                     var96 = (RightOwner)var9.next();
                  } while(!(var96 instanceof User));

                  var123 = (User)var96;
               } while(!var8 && var1.getUser() != var123);

               this.add(var1, var6, new Object[]{this.identifier(var123.getName()), String.valueOf(var123.isAdmin()), replaceNullWithEmpty(var123.getComment()), ValueInteger.get(var123.getId())});
            }
         case 10:
            var9 = this.database.getAllUsersAndRoles().iterator();

            while(true) {
               do {
                  do {
                     if (!var9.hasNext()) {
                        return var6;
                     }

                     var96 = (RightOwner)var9.next();
                  } while(!(var96 instanceof Role));

                  var118 = (Role)var96;
               } while(!var8 && !var1.getUser().isRoleGranted(var118));

               this.add(var1, var6, new Object[]{this.identifier(var118.getName()), replaceNullWithEmpty(var118.getComment()), ValueInteger.get(var118.getId())});
            }
         case 11:
            if (var8) {
               var9 = this.database.getAllRights().iterator();

               while(var9.hasNext()) {
                  var78 = (Right)var9.next();
                  var118 = var78.getGrantedRole();
                  DbObject var100 = var78.getGrantee();
                  var80 = var100.getType() == 2 ? "USER" : "ROLE";
                  if (var118 == null) {
                     var93 = var78.getGrantedObject();
                     Schema var83 = null;
                     var82 = null;
                     if (var93 != null) {
                        if (var93 instanceof Schema) {
                           var83 = (Schema)var93;
                        } else if (var93 instanceof Table) {
                           var82 = (Table)var93;
                           var83 = var82.getSchema();
                        }
                     }

                     var75 = var82 != null ? var82.getName() : "";
                     var91 = var83 != null ? var83.getName() : "";
                     if (this.checkIndex(var1, var75, var4, var5)) {
                        this.add(var1, var6, new Object[]{this.identifier(var100.getName()), var80, "", var78.getRights(), var91, var75, ValueInteger.get(var78.getId())});
                     }
                  } else {
                     this.add(var1, var6, new Object[]{this.identifier(var100.getName()), var80, this.identifier(var118.getName()), "", "", "", ValueInteger.get(var78.getId())});
                  }
               }
            }
            break;
         case 12:
            var9 = this.database.getAllSchemas().iterator();

            label1065:
            while(var9.hasNext()) {
               var60 = (Schema)var9.next();
               var79 = var60.getAllFunctionsAndAggregates().iterator();

               while(true) {
                  label1056:
                  while(true) {
                     while(true) {
                        if (!var79.hasNext()) {
                           continue label1065;
                        }

                        var76 = (UserDefinedFunction)var79.next();
                        if (var76 instanceof FunctionAlias) {
                           var92 = (FunctionAlias)var76;

                           try {
                              var88 = var92.getJavaMethods();
                              break label1056;
                           } catch (DbException var29) {
                           }
                        } else {
                           this.add(var1, var6, new Object[]{var7, this.database.getMainSchema().getName(), var76.getName(), var76.getJavaClassName(), "", ValueInteger.get(0), "NULL", ValueInteger.get(1), ValueSmallint.get((short)2), replaceNullWithEmpty(var76.getComment()), ValueInteger.get(var76.getId()), ""});
                        }
                     }
                  }

                  var65 = var88;
                  var77 = var88.length;

                  for(var89 = 0; var89 < var77; ++var89) {
                     var95 = var65[var89];
                     var110 = var95.getDataType();
                     if (var110 == null) {
                        var110 = TypeInfo.TYPE_NULL;
                     }

                     this.add(var1, var6, new Object[]{var7, var92.getSchema().getName(), var92.getName(), var92.getJavaClassName(), var92.getJavaMethodName(), ValueInteger.get(DataType.convertTypeToSQLType(var110)), var110.getDeclaredTypeName(), ValueInteger.get(var95.getParameterCount()), ValueSmallint.get((short)(var110.getValueType() == 0 ? 1 : 2)), replaceNullWithEmpty(var92.getComment()), ValueInteger.get(var92.getId()), var92.getSource()});
                  }
               }
            }

            return var6;
         case 13:
            var70 = this.database.getCompareMode().getName();
            var35 = this.database.getAllSchemas().iterator();

            while(var35.hasNext()) {
               Schema var109 = (Schema)var35.next();
               this.add(var1, var6, new Object[]{var7, var109.getName(), this.identifier(var109.getOwner().getName()), "Unicode", var70, ValueBoolean.get(var109.getId() == 0), replaceNullWithEmpty(var109.getComment()), ValueInteger.get(var109.getId())});
            }

            return var6;
         case 14:
            var9 = this.database.getAllRights().iterator();

            while(var9.hasNext()) {
               var78 = (Right)var9.next();
               var99 = var78.getGrantedObject();
               if (var99 instanceof Table) {
                  var12 = (Table)var99;
                  if (!this.hideTable(var12, var1)) {
                     var80 = var12.getName();
                     if (this.checkIndex(var1, var80, var4, var5)) {
                        this.addPrivileges(var1, var6, var78.getGrantee(), var7, var12, (String)null, var78.getRightMask());
                     }
                  }
               }
            }

            return var6;
         case 15:
            var9 = this.database.getAllRights().iterator();

            while(true) {
               do {
                  do {
                     do {
                        if (!var9.hasNext()) {
                           return var6;
                        }

                        var78 = (Right)var9.next();
                        var99 = var78.getGrantedObject();
                     } while(!(var99 instanceof Table));

                     var12 = (Table)var99;
                  } while(this.hideTable(var12, var1));

                  var80 = var12.getName();
               } while(!this.checkIndex(var1, var80, var4, var5));

               var93 = var78.getGrantee();
               var57 = var78.getRightMask();
               Column[] var85 = var12.getColumns();
               var89 = var85.length;

               for(var18 = 0; var18 < var89; ++var18) {
                  Column var119 = var85[var18];
                  this.addPrivileges(var1, var6, var93, var7, var12, var119.getName(), var57);
               }
            }
         case 16:
            Locale[] var59 = CompareMode.getCollationLocales(false);
            var38 = var59.length;

            for(var43 = 0; var43 < var38; ++var43) {
               Locale var86 = var59[var43];
               this.add(var1, var6, new Object[]{CompareMode.getName(var86), var86.toString()});
            }

            return var6;
         case 17:
            var9 = this.getAllTables(var1).iterator();

            while(var9.hasNext()) {
               var42 = (Table)var9.next();
               if (var42.getTableType() == TableType.VIEW) {
                  var90 = var42.getName();
                  if (this.checkIndex(var1, var90, var4, var5)) {
                     TableView var84 = (TableView)var42;
                     this.add(var1, var6, new Object[]{var7, var42.getSchema().getName(), var90, var42.getCreateSQL(), "NONE", "NO", var84.isInvalid() ? "INVALID" : "VALID", replaceNullWithEmpty(var84.getComment()), ValueInteger.get(var84.getId())});
                  }
               }
            }

            return var6;
         case 18:
            var54 = this.database.getInDoubtTransactions();
            if (var54 != null && var8) {
               var35 = var54.iterator();

               while(var35.hasNext()) {
                  InDoubtTransaction var87 = (InDoubtTransaction)var35.next();
                  this.add(var1, var6, new Object[]{var87.getTransactionName(), var87.getStateDescription()});
               }
            }
            break;
         case 19:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(true) {
               ConstraintReferential var81;
               do {
                  do {
                     if (!var9.hasNext()) {
                        return var6;
                     }

                     var10 = (SchemaObject)var9.next();
                     var11 = (Constraint)var10;
                  } while(var11.getConstraintType() != Constraint.Type.REFERENTIAL);

                  var81 = (ConstraintReferential)var11;
                  var50 = var81.getColumns();
                  var69 = var81.getRefColumns();
                  var15 = var81.getTable();
                  var82 = var81.getRefTable();
                  var75 = var82.getName();
               } while(!this.checkIndex(var1, var75, var4, var5));

               ValueSmallint var97 = ValueSmallint.get(getRefAction(var81.getUpdateAction()));
               ValueSmallint var112 = ValueSmallint.get(getRefAction(var81.getDeleteAction()));

               for(int var122 = 0; var122 < var50.length; ++var122) {
                  this.add(var1, var6, new Object[]{var7, var82.getSchema().getName(), var82.getName(), var69[var122].column.getName(), var7, var15.getSchema().getName(), var15.getName(), var50[var122].column.getName(), ValueSmallint.get((short)(var122 + 1)), var97, var112, var81.getName(), var81.getReferencedConstraint().getName(), ValueSmallint.get((short)7)});
               }
            }
         case 20:
            var9 = this.database.getAllSchemas().iterator();

            label1040:
            while(var9.hasNext()) {
               var60 = (Schema)var9.next();
               var79 = var60.getAllFunctionsAndAggregates().iterator();

               while(true) {
                  while(true) {
                     do {
                        if (!var79.hasNext()) {
                           continue label1040;
                        }

                        var76 = (UserDefinedFunction)var79.next();
                     } while(!(var76 instanceof FunctionAlias));

                     var92 = (FunctionAlias)var76;

                     try {
                        var88 = var92.getJavaMethods();
                        break;
                     } catch (DbException var28) {
                     }
                  }

                  var65 = var88;
                  var77 = var88.length;

                  for(var89 = 0; var89 < var77; ++var89) {
                     var95 = var65[var89];
                     var110 = var95.getDataType();
                     if (var110 != null && var110.getValueType() != 0) {
                        DataType var115 = DataType.getDataType(var110.getValueType());
                        this.add(var1, var6, new Object[]{var7, var92.getSchema().getName(), var92.getName(), var92.getJavaClassName(), var92.getJavaMethodName(), ValueInteger.get(var95.getParameterCount()), ValueInteger.get(0), "P0", ValueInteger.get(DataType.convertTypeToSQLType(var110)), var110.getDeclaredTypeName(), ValueInteger.get(MathUtils.convertLongToInt(var115.defaultPrecision)), ValueSmallint.get(MathUtils.convertIntToShort(var115.defaultScale)), ValueSmallint.get((short)10), ValueSmallint.get((short)2), ValueSmallint.get((short)5), "", null});
                     }

                     Class[] var120 = var95.getColumnClasses();

                     for(var130 = 0; var130 < var120.length; ++var130) {
                        if (!var95.hasConnectionParam() || var130 != 0) {
                           Class var138 = var120[var130];
                           TypeInfo var144 = ValueToObjectConverter2.classToType(var138);
                           DataType var150 = DataType.getDataType(var144.getValueType());
                           this.add(var1, var6, new Object[]{var7, var92.getSchema().getName(), var92.getName(), var92.getJavaClassName(), var92.getJavaMethodName(), ValueInteger.get(var95.getParameterCount()), ValueInteger.get(var130 + (var95.hasConnectionParam() ? 0 : 1)), "P" + (var130 + 1), ValueInteger.get(DataType.convertTypeToSQLType(var144)), var144.getDeclaredTypeName(), ValueInteger.get(MathUtils.convertLongToInt(var150.defaultPrecision)), ValueSmallint.get(MathUtils.convertIntToShort(var150.defaultScale)), ValueSmallint.get((short)10), ValueSmallint.get((short)(var138.isPrimitive() ? 0 : 1)), ValueSmallint.get((short)1), "", null});
                        }
                     }
                  }
               }
            }

            return var6;
         case 21:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(true) {
               do {
                  do {
                     if (!var9.hasNext()) {
                        return var6;
                     }

                     var10 = (SchemaObject)var9.next();
                     var11 = (Constraint)var10;
                     var40 = var11.getConstraintType();
                     var80 = null;
                     var69 = null;
                     var15 = var11.getTable();
                  } while(this.hideTable(var15, var1));

                  var72 = var11.getIndex();
                  var75 = null;
                  if (var72 != null) {
                     var75 = var72.getName();
                  }

                  var91 = var15.getName();
               } while(!this.checkIndex(var1, var91, var4, var5));

               if (var40 == Constraint.Type.CHECK) {
                  var80 = ((ConstraintCheck)var11).getExpression().getSQL(0);
               } else if (var40 != Constraint.Type.UNIQUE && var40 != Constraint.Type.PRIMARY_KEY) {
                  if (var40 == Constraint.Type.REFERENTIAL) {
                     var69 = ((ConstraintReferential)var11).getColumns();
                  }
               } else {
                  var69 = ((ConstraintUnique)var11).getColumns();
               }

               String var98 = null;
               if (var69 != null) {
                  StringBuilder var107 = new StringBuilder();
                  var130 = 0;

                  for(int var135 = var69.length; var130 < var135; ++var130) {
                     if (var130 > 0) {
                        var107.append(',');
                     }

                     var107.append(var69[var130].column.getName());
                  }

                  var98 = var107.toString();
               }

               this.add(var1, var6, new Object[]{var7, var11.getSchema().getName(), var11.getName(), var40 == Constraint.Type.PRIMARY_KEY ? var40.getSqlName() : var40.name(), var7, var15.getSchema().getName(), var91, var75, var80, var98, replaceNullWithEmpty(var11.getComment()), var11.getCreateSQL(), ValueInteger.get(var11.getId())});
            }
         case 22:
            var9 = this.getAllSchemaObjects(11).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               Constant var71 = (Constant)var10;
               ValueExpression var66 = var71.getValue();
               this.add(var1, var6, new Object[]{var7, var71.getSchema().getName(), var71.getName(), ValueInteger.get(DataType.convertTypeToSQLType(var66.getType())), replaceNullWithEmpty(var71.getComment()), var66.getSQL(0), ValueInteger.get(var71.getId())});
            }

            return var6;
         case 23:
            var9 = this.getAllSchemaObjects(12).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               Domain var67 = (Domain)var10;
               var46 = var67.getDomain();
               TypeInfo var74 = var67.getDataType();
               this.add(var1, var6, new Object[]{var7, var67.getSchema().getName(), var67.getName(), var67.getDefaultSQL(), var67.getOnUpdateSQL(), ValueInteger.get(DataType.convertTypeToSQLType(var74)), ValueInteger.get(MathUtils.convertLongToInt(var74.getPrecision())), ValueInteger.get(var74.getScale()), var74.getDeclaredTypeName(), var46 != null ? var7 : null, var46 != null ? var46.getSchema().getName() : null, var46 != null ? var46.getName() : null, ValueInteger.get(50), replaceNullWithEmpty(var67.getComment()), var67.getCreateSQL(), ValueInteger.get(var67.getId()), var67.getDefaultSQL(), "YES", null});
            }

            return var6;
         case 24:
            var9 = this.getAllSchemaObjects(4).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               TriggerObject var63 = (TriggerObject)var10;
               var12 = var63.getTable();
               this.add(var1, var6, new Object[]{var7, var63.getSchema().getName(), var63.getName(), var63.getTypeNameList(new StringBuilder()).toString(), var7, var12.getSchema().getName(), var12.getName(), ValueBoolean.get(var63.isBefore()), var63.getTriggerClassName(), ValueInteger.get(var63.getQueueSize()), ValueBoolean.get(var63.isNoWait()), replaceNullWithEmpty(var63.getComment()), var63.getCreateSQL(), ValueInteger.get(var63.getId())});
            }

            return var6;
         case 25:
            var37 = this.database.getSessions(false);
            var38 = var37.length;

            for(var43 = 0; var43 < var38; ++var43) {
               var55 = var37[var43];
               if (var8 || var55 == var1) {
                  NetworkConnectionInfo var73 = var55.getNetworkConnectionInfo();
                  Command var68 = var55.getCurrentCommand();
                  var57 = var55.getBlockingSessionId();
                  this.add(var1, var6, new Object[]{ValueInteger.get(var55.getId()), var55.getUser().getName(), var73 == null ? null : var73.getServer(), var73 == null ? null : var73.getClient(), var73 == null ? null : var73.getClientInfo(), var55.getSessionStart(), var1.getIsolationLevel().getSQL(), var68 == null ? null : var68.toString(), var68 == null ? null : var55.getCommandStartOrEnd(), ValueBoolean.get(var55.hasPendingTransaction()), String.valueOf(var55.getState()), var57 == 0 ? null : ValueInteger.get(var57), var55.getState() == SessionLocal.State.SLEEP ? var55.getCommandStartOrEnd() : null});
               }
            }

            return var6;
         case 26:
            var37 = this.database.getSessions(false);
            var38 = var37.length;

            for(var43 = 0; var43 < var38; ++var43) {
               var55 = var37[var43];
               if (var8 || var55 == var1) {
                  var13 = var55.getLocks().iterator();

                  while(var13.hasNext()) {
                     var52 = (Table)var13.next();
                     this.add(var1, var6, new Object[]{var52.getSchema().getName(), var52.getName(), ValueInteger.get(var55.getId()), var52.isLockedExclusivelyBy(var55) ? "WRITE" : "READ"});
                  }
               }
            }

            return var6;
         case 27:
            var32 = var1.getVariableNames();
            var38 = var32.length;

            for(var43 = 0; var43 < var38; ++var43) {
               var49 = var32[var43];
               Value var64 = var1.getVariable(var49);
               StringBuilder var61 = (new StringBuilder()).append("SET @").append(var49).append(' ');
               var64.getSQL(var61, 0);
               this.add(var1, var6, new Object[]{"@" + var49, var61.toString()});
            }

            var9 = var1.getLocalTempTables().iterator();

            while(var9.hasNext()) {
               var42 = (Table)var9.next();
               this.add(var1, var6, new Object[]{"TABLE " + var42.getName(), var42.getCreateSQL()});
            }

            var32 = var1.getSchemaSearchPath();
            if (var32 != null && var32.length > 0) {
               StringBuilder var44 = new StringBuilder("SET SCHEMA_SEARCH_PATH ");
               var43 = 0;

               for(var53 = var32.length; var43 < var53; ++var43) {
                  if (var43 > 0) {
                     var44.append(", ");
                  }

                  StringUtils.quoteIdentifier(var44, var32[var43]);
               }

               this.add(var1, var6, new Object[]{"SCHEMA_SEARCH_PATH", var44.toString()});
            }

            var45 = var1.getCurrentSchemaName();
            if (var45 != null) {
               this.add(var1, var6, new Object[]{"SCHEMA", StringUtils.quoteIdentifier(new StringBuilder("SET SCHEMA "), var45).toString()});
            }

            TimeZoneProvider var51 = var1.currentTimeZone();
            if (!var51.equals(DateTimeUtils.getTimeZone())) {
               this.add(var1, var6, new Object[]{"TIME ZONE", StringUtils.quoteStringSQL(new StringBuilder("SET TIME ZONE "), var51.getId()).toString()});
            }
            break;
         case 28:
            QueryStatisticsData var31 = this.database.getQueryStatisticsData();
            if (var31 != null) {
               var35 = var31.getQueries().iterator();

               while(var35.hasNext()) {
                  QueryStatisticsData.QueryEntry var41 = (QueryStatisticsData.QueryEntry)var35.next();
                  this.add(var1, var6, new Object[]{var41.sqlStatement, ValueInteger.get(var41.count), ValueDouble.get((double)var41.executionTimeMinNanos / 1000000.0), ValueDouble.get((double)var41.executionTimeMaxNanos / 1000000.0), ValueDouble.get((double)var41.executionTimeCumulativeNanos / 1000000.0), ValueDouble.get(var41.executionTimeMeanNanos / 1000000.0), ValueDouble.get(var41.getExecutionTimeStandardDeviation() / 1000000.0), ValueBigint.get(var41.rowCountMin), ValueBigint.get(var41.rowCountMax), ValueBigint.get(var41.rowCountCumulative), ValueDouble.get(var41.rowCountMean), ValueDouble.get(var41.getRowCountStandardDeviation())});
               }
            }
            break;
         case 29:
            var9 = this.database.getAllSynonyms().iterator();

            while(var9.hasNext()) {
               TableSynonym var34 = (TableSynonym)var9.next();
               this.add(var1, var6, new Object[]{var7, var34.getSchema().getName(), var34.getName(), var34.getSynonymForName(), var34.getSynonymForSchema().getName(), "SYNONYM", "VALID", replaceNullWithEmpty(var34.getComment()), ValueInteger.get(var34.getId())});
            }

            return var6;
         case 30:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               var11 = (Constraint)var10;
               var40 = var11.getConstraintType();
               if (var40 != Constraint.Type.DOMAIN) {
                  Table var62 = var11.getTable();
                  if (!this.hideTable(var62, var1)) {
                     var58 = var62.getName();
                     if (this.checkIndex(var1, var58, var4, var5)) {
                        this.add(var1, var6, new Object[]{var7, var11.getSchema().getName(), var11.getName(), var40.getSqlName(), var7, var62.getSchema().getName(), var58, "NO", "NO", replaceNullWithEmpty(var11.getComment()), var11.getCreateSQL(), ValueInteger.get(var11.getId())});
                     }
                  }
               }
            }

            return var6;
         case 31:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               if (((Constraint)var10).getConstraintType() == Constraint.Type.DOMAIN) {
                  ConstraintDomain var36 = (ConstraintDomain)var10;
                  var46 = var36.getDomain();
                  this.add(var1, var6, new Object[]{var7, var36.getSchema().getName(), var36.getName(), var7, var46.getSchema().getName(), var46.getName(), "NO", "NO", replaceNullWithEmpty(var36.getComment()), var36.getCreateSQL(), ValueInteger.get(var36.getId())});
               }
            }

            return var6;
         case 32:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(true) {
               String var56;
               do {
                  do {
                     do {
                        if (!var9.hasNext()) {
                           return var6;
                        }

                        var10 = (SchemaObject)var9.next();
                        var11 = (Constraint)var10;
                        var40 = var11.getConstraintType();
                        var50 = null;
                        if (var40 != Constraint.Type.UNIQUE && var40 != Constraint.Type.PRIMARY_KEY) {
                           if (var40 == Constraint.Type.REFERENTIAL) {
                              var50 = ((ConstraintReferential)var11).getColumns();
                           }
                        } else {
                           var50 = ((ConstraintUnique)var11).getColumns();
                        }
                     } while(var50 == null);

                     var52 = var11.getTable();
                  } while(this.hideTable(var52, var1));

                  var56 = var52.getName();
               } while(!this.checkIndex(var1, var56, var4, var5));

               ConstraintUnique var16;
               if (var40 == Constraint.Type.REFERENTIAL) {
                  var16 = ((ConstraintReferential)var11).getReferencedConstraint();
               } else {
                  var16 = null;
               }

               Index var17 = var11.getIndex();

               for(var18 = 0; var18 < var50.length; ++var18) {
                  IndexColumn var19 = var50[var18];
                  var20 = ValueInteger.get(var18 + 1);
                  ValueInteger var21 = null;
                  if (var16 != null) {
                     Column var22 = ((ConstraintReferential)var11).getRefColumns()[var18].column;
                     IndexColumn[] var23 = var16.getColumns();

                     for(int var24 = 0; var24 < var23.length; ++var24) {
                        if (var23[var24].column.equals(var22)) {
                           var21 = ValueInteger.get(var24 + 1);
                           break;
                        }
                     }
                  }

                  this.add(var1, var6, new Object[]{var7, var11.getSchema().getName(), var11.getName(), var7, var52.getSchema().getName(), var56, var19.columnName, var20, var21, var17 != null ? var7 : null, var17 != null ? var17.getSchema().getName() : null, var17 != null ? var17.getName() : null});
               }
            }
         case 33:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(var9.hasNext()) {
               var10 = (SchemaObject)var9.next();
               if (((Constraint)var10).getConstraintType() == Constraint.Type.REFERENTIAL) {
                  ConstraintReferential var33 = (ConstraintReferential)var10;
                  var12 = var33.getTable();
                  if (!this.hideTable(var12, var1)) {
                     ConstraintUnique var48 = var33.getReferencedConstraint();
                     this.add(var1, var6, new Object[]{var7, var33.getSchema().getName(), var33.getName(), var7, var48.getSchema().getName(), var48.getName(), "NONE", var33.getUpdateAction().getSqlName(), var33.getDeleteAction().getSqlName()});
                  }
               }
            }

            return var6;
         case 34:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(true) {
               while(true) {
                  if (!var9.hasNext()) {
                     return var6;
                  }

                  var10 = (SchemaObject)var9.next();
                  var11 = (Constraint)var10;
                  var40 = var11.getConstraintType();
                  if (var40 == Constraint.Type.CHECK) {
                     ConstraintCheck var47 = (ConstraintCheck)var10;
                     var52 = var47.getTable();
                     if (this.hideTable(var52, var1)) {
                        continue;
                     }
                  } else if (var40 != Constraint.Type.DOMAIN) {
                     continue;
                  }
                  break;
               }

               this.add(var1, var6, new Object[]{var7, var10.getSchema().getName(), var10.getName(), var11.getExpression().getSQL(0, 2)});
            }
         case 35:
            var9 = this.getAllSchemaObjects(5).iterator();

            while(true) {
               Column var14;
               do {
                  do {
                     label778:
                     while(true) {
                        if (!var9.hasNext()) {
                           return var6;
                        }

                        var10 = (SchemaObject)var9.next();
                        var11 = (Constraint)var10;
                        switch (var11.getConstraintType()) {
                           case CHECK:
                           case DOMAIN:
                              HashSet var39 = new HashSet();
                              var11.getExpression().isEverything(ExpressionVisitor.getColumnsVisitor(var39, (Table)null));
                              var13 = var39.iterator();

                              while(true) {
                                 if (!var13.hasNext()) {
                                    continue label778;
                                 }

                                 var14 = (Column)var13.next();
                                 var15 = var14.getTable();
                                 if (this.checkIndex(var1, var15.getName(), var4, var5) && !this.hideTable(var15, var1)) {
                                    this.addConstraintColumnUsage(var1, var6, var7, var11, var14);
                                 }
                              }
                           case REFERENTIAL:
                              var12 = var11.getRefTable();
                              if (this.checkIndex(var1, var12.getName(), var4, var5) && !this.hideTable(var12, var1)) {
                                 var13 = var11.getReferencedColumns(var12).iterator();

                                 while(var13.hasNext()) {
                                    var14 = (Column)var13.next();
                                    this.addConstraintColumnUsage(var1, var6, var7, var11, var14);
                                 }
                              }
                           case PRIMARY_KEY:
                           case UNIQUE:
                              break label778;
                        }
                     }

                     var12 = var11.getTable();
                  } while(!this.checkIndex(var1, var12.getName(), var4, var5));
               } while(this.hideTable(var12, var1));

               var13 = var11.getReferencedColumns(var12).iterator();

               while(var13.hasNext()) {
                  var14 = (Column)var13.next();
                  this.addConstraintColumnUsage(var1, var6, var7, var11, var14);
               }
            }
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      return var6;
   }

   private static short getRefAction(ConstraintActionType var0) {
      switch (var0) {
         case CASCADE:
            return 0;
         case RESTRICT:
            return 1;
         case SET_DEFAULT:
            return 4;
         case SET_NULL:
            return 2;
         default:
            throw DbException.getInternalError("action=" + var0);
      }
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
         this.add(var1, var2, new Object[]{null, this.identifier(var3.getName()), var4, var5.getSchema().getName(), var5.getName(), var7, var8});
      } else {
         this.add(var1, var2, new Object[]{null, this.identifier(var3.getName()), var4, var5.getSchema().getName(), var5.getName(), var6, var7, var8});
      }

   }

   private ArrayList<SchemaObject> getAllSchemaObjects(int var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.database.getAllSchemas().iterator();

      while(var3.hasNext()) {
         Schema var4 = (Schema)var3.next();
         var4.getAll(var1, var2);
      }

      return var2;
   }

   private ArrayList<Table> getAllTables(SessionLocal var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = this.database.getAllSchemas().iterator();

      while(var3.hasNext()) {
         Schema var4 = (Schema)var3.next();
         var2.addAll(var4.getAllTablesAndViews(var1));
      }

      var2.addAll(var1.getLocalTempTables());
      return var2;
   }

   private ArrayList<Table> getTablesByName(SessionLocal var1, String var2) {
      ArrayList var3 = new ArrayList(1);
      Iterator var4 = this.database.getAllSchemas().iterator();

      while(var4.hasNext()) {
         Schema var5 = (Schema)var4.next();
         Table var6 = var5.getTableOrViewByName(var1, var2);
         if (var6 != null) {
            var3.add(var6);
         }
      }

      Table var7 = var1.findLocalTempTable(var2);
      if (var7 != null) {
         var3.add(var7);
      }

      return var3;
   }

   public long getMaxDataModificationId() {
      switch (this.type) {
         case 6:
         case 8:
         case 18:
         case 25:
         case 26:
         case 27:
            return Long.MAX_VALUE;
         default:
            return this.database.getModificationDataId();
      }
   }
}
