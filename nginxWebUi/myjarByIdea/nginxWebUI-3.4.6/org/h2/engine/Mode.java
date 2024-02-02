package org.h2.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.h2.util.StringUtils;
import org.h2.value.DataType;

public class Mode {
   private static final HashMap<String, Mode> MODES = new HashMap();
   public boolean aliasColumnName;
   public boolean convertOnlyToSmallerScale;
   public boolean indexDefinitionInCreateTable;
   public boolean squareBracketQuotedNames;
   public boolean systemColumns;
   public UniqueIndexNullsHandling uniqueIndexNullsHandling;
   public boolean treatEmptyStringsAsNull;
   public boolean sysDummy1;
   public boolean allowPlusForStringConcat;
   public boolean logIsLogBase10;
   public boolean swapLogFunctionParameters;
   public boolean regexpReplaceBackslashReferences;
   public boolean swapConvertFunctionParameters;
   public boolean isolationLevelInSelectOrInsertStatement;
   public boolean onDuplicateKeyUpdate;
   public boolean replaceInto;
   public boolean insertOnConflict;
   public Pattern supportedClientInfoPropertiesRegEx;
   public boolean supportPoundSymbolForColumnNames;
   public boolean allowEmptyInPredicate;
   public CharPadding charPadding;
   public boolean allowDB2TimestampFormat;
   public boolean discardWithTableHints;
   public boolean dateTimeValueWithinTransaction;
   public boolean zeroExLiteralsAreBinaryStrings;
   public boolean allowUnrelatedOrderByExpressionsInDistinctQueries;
   public boolean alterTableExtensionsMySQL;
   public boolean alterTableModifyColumn;
   public boolean truncateTableRestartIdentity;
   public boolean decimalSequences;
   public boolean allowEmptySchemaValuesAsDefaultSchema;
   public boolean allNumericTypesHavePrecision;
   public boolean forBitData;
   public boolean charAndByteLengthUnits;
   public boolean nextvalAndCurrvalPseudoColumns;
   public boolean nextValueReturnsDifferentValues;
   public boolean updateSequenceOnManualIdentityInsertion;
   public boolean takeInsertedIdentity;
   public boolean takeGeneratedSequenceValue;
   public boolean identityColumnsHaveDefaultOnNull;
   public boolean mergeWhere;
   public boolean allowUsingFromClauseInUpdateStatement;
   public boolean createUniqueConstraintForReferencedColumns;
   public ExpressionNames expressionNames;
   public ViewExpressionNames viewExpressionNames;
   public boolean topInSelect;
   public boolean topInDML;
   public boolean limit;
   public boolean minusIsExcept;
   public boolean identityDataType;
   public boolean serialDataTypes;
   public boolean identityClause;
   public boolean autoIncrementClause;
   public Set<String> disallowedTypes;
   public HashMap<String, DataType> typeByNameMap;
   public boolean groupByColumnIndex;
   public boolean numericWithBooleanComparison;
   private final String name;
   private final ModeEnum modeEnum;

   private Mode(ModeEnum var1) {
      this.uniqueIndexNullsHandling = Mode.UniqueIndexNullsHandling.ALLOW_DUPLICATES_WITH_ANY_NULL;
      this.charPadding = Mode.CharPadding.ALWAYS;
      this.expressionNames = Mode.ExpressionNames.OPTIMIZED_SQL;
      this.viewExpressionNames = Mode.ViewExpressionNames.AS_IS;
      this.disallowedTypes = Collections.emptySet();
      this.typeByNameMap = new HashMap();
      this.name = var1.name();
      this.modeEnum = var1;
   }

   private static void add(Mode var0) {
      MODES.put(StringUtils.toUpperEnglish(var0.name), var0);
   }

   public static Mode getInstance(String var0) {
      return (Mode)MODES.get(StringUtils.toUpperEnglish(var0));
   }

   public static Mode getRegular() {
      return getInstance(Mode.ModeEnum.REGULAR.name());
   }

   public String getName() {
      return this.name;
   }

   public ModeEnum getEnum() {
      return this.modeEnum;
   }

   public String toString() {
      return this.name;
   }

   static {
      Mode var0 = new Mode(Mode.ModeEnum.REGULAR);
      var0.allowEmptyInPredicate = true;
      var0.dateTimeValueWithinTransaction = true;
      var0.topInSelect = true;
      var0.limit = true;
      var0.minusIsExcept = true;
      var0.identityDataType = true;
      var0.serialDataTypes = true;
      var0.autoIncrementClause = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.STRICT);
      var0.dateTimeValueWithinTransaction = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.LEGACY);
      var0.allowEmptyInPredicate = true;
      var0.dateTimeValueWithinTransaction = true;
      var0.topInSelect = true;
      var0.limit = true;
      var0.minusIsExcept = true;
      var0.identityDataType = true;
      var0.serialDataTypes = true;
      var0.autoIncrementClause = true;
      var0.identityClause = true;
      var0.updateSequenceOnManualIdentityInsertion = true;
      var0.takeInsertedIdentity = true;
      var0.identityColumnsHaveDefaultOnNull = true;
      var0.nextvalAndCurrvalPseudoColumns = true;
      var0.topInDML = true;
      var0.mergeWhere = true;
      var0.createUniqueConstraintForReferencedColumns = true;
      var0.numericWithBooleanComparison = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.DB2);
      var0.aliasColumnName = true;
      var0.sysDummy1 = true;
      var0.isolationLevelInSelectOrInsertStatement = true;
      var0.supportedClientInfoPropertiesRegEx = Pattern.compile("ApplicationName|ClientAccountingInformation|ClientUser|ClientCorrelationToken");
      var0.allowDB2TimestampFormat = true;
      var0.forBitData = true;
      var0.takeInsertedIdentity = true;
      var0.expressionNames = Mode.ExpressionNames.NUMBER;
      var0.viewExpressionNames = Mode.ViewExpressionNames.EXCEPTION;
      var0.limit = true;
      var0.minusIsExcept = true;
      var0.numericWithBooleanComparison = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.Derby);
      var0.aliasColumnName = true;
      var0.uniqueIndexNullsHandling = Mode.UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES;
      var0.sysDummy1 = true;
      var0.isolationLevelInSelectOrInsertStatement = true;
      var0.supportedClientInfoPropertiesRegEx = null;
      var0.forBitData = true;
      var0.takeInsertedIdentity = true;
      var0.expressionNames = Mode.ExpressionNames.NUMBER;
      var0.viewExpressionNames = Mode.ViewExpressionNames.EXCEPTION;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.HSQLDB);
      var0.allowPlusForStringConcat = true;
      var0.identityColumnsHaveDefaultOnNull = true;
      var0.supportedClientInfoPropertiesRegEx = null;
      var0.expressionNames = Mode.ExpressionNames.C_NUMBER;
      var0.topInSelect = true;
      var0.limit = true;
      var0.minusIsExcept = true;
      var0.numericWithBooleanComparison = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.MSSQLServer);
      var0.aliasColumnName = true;
      var0.squareBracketQuotedNames = true;
      var0.uniqueIndexNullsHandling = Mode.UniqueIndexNullsHandling.FORBID_ANY_DUPLICATES;
      var0.allowPlusForStringConcat = true;
      var0.swapLogFunctionParameters = true;
      var0.swapConvertFunctionParameters = true;
      var0.supportPoundSymbolForColumnNames = true;
      var0.discardWithTableHints = true;
      var0.supportedClientInfoPropertiesRegEx = null;
      var0.zeroExLiteralsAreBinaryStrings = true;
      var0.truncateTableRestartIdentity = true;
      var0.takeInsertedIdentity = true;
      DataType var1 = DataType.createNumeric(19, 4);
      var1.type = 13;
      var1.sqlType = 2;
      var1.specialPrecisionScale = true;
      var0.typeByNameMap.put("MONEY", var1);
      var1 = DataType.createNumeric(10, 4);
      var1.type = 13;
      var1.sqlType = 2;
      var1.specialPrecisionScale = true;
      var0.typeByNameMap.put("SMALLMONEY", var1);
      var0.typeByNameMap.put("UNIQUEIDENTIFIER", DataType.getDataType(39));
      var0.allowEmptySchemaValuesAsDefaultSchema = true;
      var0.expressionNames = Mode.ExpressionNames.EMPTY;
      var0.viewExpressionNames = Mode.ViewExpressionNames.EXCEPTION;
      var0.topInSelect = true;
      var0.topInDML = true;
      var0.identityClause = true;
      var0.numericWithBooleanComparison = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.MariaDB);
      var0.indexDefinitionInCreateTable = true;
      var0.regexpReplaceBackslashReferences = true;
      var0.onDuplicateKeyUpdate = true;
      var0.replaceInto = true;
      var0.charPadding = Mode.CharPadding.NEVER;
      var0.supportedClientInfoPropertiesRegEx = Pattern.compile(".*");
      var0.zeroExLiteralsAreBinaryStrings = true;
      var0.allowUnrelatedOrderByExpressionsInDistinctQueries = true;
      var0.alterTableExtensionsMySQL = true;
      var0.alterTableModifyColumn = true;
      var0.truncateTableRestartIdentity = true;
      var0.allNumericTypesHavePrecision = true;
      var0.nextValueReturnsDifferentValues = true;
      var0.updateSequenceOnManualIdentityInsertion = true;
      var0.takeInsertedIdentity = true;
      var0.identityColumnsHaveDefaultOnNull = true;
      var0.expressionNames = Mode.ExpressionNames.ORIGINAL_SQL;
      var0.viewExpressionNames = Mode.ViewExpressionNames.MYSQL_STYLE;
      var0.limit = true;
      var0.autoIncrementClause = true;
      var0.typeByNameMap.put("YEAR", DataType.getDataType(10));
      var0.groupByColumnIndex = true;
      var0.numericWithBooleanComparison = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.MySQL);
      var0.indexDefinitionInCreateTable = true;
      var0.regexpReplaceBackslashReferences = true;
      var0.onDuplicateKeyUpdate = true;
      var0.replaceInto = true;
      var0.charPadding = Mode.CharPadding.NEVER;
      var0.supportedClientInfoPropertiesRegEx = Pattern.compile(".*");
      var0.zeroExLiteralsAreBinaryStrings = true;
      var0.allowUnrelatedOrderByExpressionsInDistinctQueries = true;
      var0.alterTableExtensionsMySQL = true;
      var0.alterTableModifyColumn = true;
      var0.truncateTableRestartIdentity = true;
      var0.allNumericTypesHavePrecision = true;
      var0.updateSequenceOnManualIdentityInsertion = true;
      var0.takeInsertedIdentity = true;
      var0.identityColumnsHaveDefaultOnNull = true;
      var0.createUniqueConstraintForReferencedColumns = true;
      var0.expressionNames = Mode.ExpressionNames.ORIGINAL_SQL;
      var0.viewExpressionNames = Mode.ViewExpressionNames.MYSQL_STYLE;
      var0.limit = true;
      var0.autoIncrementClause = true;
      var0.typeByNameMap.put("YEAR", DataType.getDataType(10));
      var0.groupByColumnIndex = true;
      var0.numericWithBooleanComparison = true;
      add(var0);
      var0 = new Mode(Mode.ModeEnum.Oracle);
      var0.aliasColumnName = true;
      var0.convertOnlyToSmallerScale = true;
      var0.uniqueIndexNullsHandling = Mode.UniqueIndexNullsHandling.ALLOW_DUPLICATES_WITH_ALL_NULLS;
      var0.treatEmptyStringsAsNull = true;
      var0.regexpReplaceBackslashReferences = true;
      var0.supportPoundSymbolForColumnNames = true;
      var0.supportedClientInfoPropertiesRegEx = Pattern.compile(".*\\..*");
      var0.alterTableModifyColumn = true;
      var0.decimalSequences = true;
      var0.charAndByteLengthUnits = true;
      var0.nextvalAndCurrvalPseudoColumns = true;
      var0.mergeWhere = true;
      var0.minusIsExcept = true;
      var0.expressionNames = Mode.ExpressionNames.ORIGINAL_SQL;
      var0.viewExpressionNames = Mode.ViewExpressionNames.EXCEPTION;
      var0.typeByNameMap.put("BINARY_FLOAT", DataType.getDataType(14));
      var0.typeByNameMap.put("BINARY_DOUBLE", DataType.getDataType(15));
      var1 = DataType.createDate(19, 19, "DATE", false, 0, 0);
      var1.type = 20;
      var1.sqlType = 93;
      var1.specialPrecisionScale = true;
      var0.typeByNameMap.put("DATE", var1);
      add(var0);
      var0 = new Mode(Mode.ModeEnum.PostgreSQL);
      var0.aliasColumnName = true;
      var0.systemColumns = true;
      var0.logIsLogBase10 = true;
      var0.regexpReplaceBackslashReferences = true;
      var0.insertOnConflict = true;
      var0.supportedClientInfoPropertiesRegEx = Pattern.compile("ApplicationName");
      var0.charPadding = Mode.CharPadding.IN_RESULT_SETS;
      var0.nextValueReturnsDifferentValues = true;
      var0.takeGeneratedSequenceValue = true;
      var0.expressionNames = Mode.ExpressionNames.POSTGRESQL_STYLE;
      var0.allowUsingFromClauseInUpdateStatement = true;
      var0.limit = true;
      var0.serialDataTypes = true;
      HashSet var2 = new HashSet();
      var2.add("NUMBER");
      var2.add("TINYINT");
      var2.add("BLOB");
      var2.add("VARCHAR_IGNORECASE");
      var0.disallowedTypes = var2;
      var1 = DataType.getDataType(38);
      var0.typeByNameMap.put("JSONB", var1);
      var1 = DataType.createNumeric(19, 2);
      var1.type = 13;
      var1.sqlType = 2;
      var1.specialPrecisionScale = true;
      var0.typeByNameMap.put("MONEY", var1);
      var1 = DataType.getDataType(11);
      var0.typeByNameMap.put("OID", var1);
      var0.dateTimeValueWithinTransaction = true;
      var0.groupByColumnIndex = true;
      add(var0);
   }

   public static enum CharPadding {
      ALWAYS,
      IN_RESULT_SETS,
      NEVER;
   }

   public static enum ViewExpressionNames {
      AS_IS,
      EXCEPTION,
      MYSQL_STYLE;
   }

   public static enum ExpressionNames {
      OPTIMIZED_SQL,
      ORIGINAL_SQL,
      EMPTY,
      NUMBER,
      C_NUMBER,
      POSTGRESQL_STYLE;
   }

   public static enum UniqueIndexNullsHandling {
      ALLOW_DUPLICATES_WITH_ANY_NULL,
      ALLOW_DUPLICATES_WITH_ALL_NULLS,
      FORBID_ANY_DUPLICATES;
   }

   public static enum ModeEnum {
      REGULAR,
      STRICT,
      LEGACY,
      DB2,
      Derby,
      MariaDB,
      MSSQLServer,
      HSQLDB,
      MySQL,
      Oracle,
      PostgreSQL;
   }
}
