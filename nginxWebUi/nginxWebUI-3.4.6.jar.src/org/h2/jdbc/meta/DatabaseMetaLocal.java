/*      */ package org.h2.jdbc.meta;
/*      */ 
/*      */ import java.sql.ResultSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import org.h2.command.dml.Help;
/*      */ import org.h2.constraint.Constraint;
/*      */ import org.h2.constraint.ConstraintActionType;
/*      */ import org.h2.constraint.ConstraintReferential;
/*      */ import org.h2.constraint.ConstraintUnique;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.DbObject;
/*      */ import org.h2.engine.Mode;
/*      */ import org.h2.engine.Right;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.engine.User;
/*      */ import org.h2.expression.condition.CompareLike;
/*      */ import org.h2.index.Index;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.mode.DefaultNullOrdering;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.result.SimpleResult;
/*      */ import org.h2.result.SortOrder;
/*      */ import org.h2.schema.FunctionAlias;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.schema.SchemaObject;
/*      */ import org.h2.schema.UserDefinedFunction;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.IndexColumn;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.table.TableSynonym;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.DataType;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueBoolean;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueSmallint;
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
/*      */ public final class DatabaseMetaLocal
/*      */   extends DatabaseMetaLocalBase
/*      */ {
/*   63 */   private static final Value YES = ValueVarchar.get("YES");
/*      */   
/*   65 */   private static final Value NO = ValueVarchar.get("NO");
/*      */   
/*   67 */   private static final ValueSmallint BEST_ROW_SESSION = ValueSmallint.get((short)2);
/*      */ 
/*      */   
/*   70 */   private static final ValueSmallint BEST_ROW_NOT_PSEUDO = ValueSmallint.get((short)1);
/*      */   
/*   72 */   private static final ValueInteger COLUMN_NO_NULLS = ValueInteger.get(0);
/*      */ 
/*      */   
/*   75 */   private static final ValueSmallint COLUMN_NO_NULLS_SMALL = ValueSmallint.get((short)0);
/*      */   
/*   77 */   private static final ValueInteger COLUMN_NULLABLE = ValueInteger.get(1);
/*      */ 
/*      */   
/*   80 */   private static final ValueSmallint COLUMN_NULLABLE_UNKNOWN_SMALL = ValueSmallint.get((short)2);
/*      */ 
/*      */   
/*   83 */   private static final ValueSmallint IMPORTED_KEY_CASCADE = ValueSmallint.get((short)0);
/*      */ 
/*      */   
/*   86 */   private static final ValueSmallint IMPORTED_KEY_RESTRICT = ValueSmallint.get((short)1);
/*      */ 
/*      */   
/*   89 */   private static final ValueSmallint IMPORTED_KEY_DEFAULT = ValueSmallint.get((short)4);
/*      */ 
/*      */   
/*   92 */   private static final ValueSmallint IMPORTED_KEY_SET_NULL = ValueSmallint.get((short)2);
/*      */ 
/*      */   
/*   95 */   private static final ValueSmallint IMPORTED_KEY_NOT_DEFERRABLE = ValueSmallint.get((short)7);
/*      */ 
/*      */   
/*   98 */   private static final ValueSmallint PROCEDURE_COLUMN_IN = ValueSmallint.get((short)1);
/*      */ 
/*      */   
/*  101 */   private static final ValueSmallint PROCEDURE_COLUMN_RETURN = ValueSmallint.get((short)5);
/*      */ 
/*      */   
/*  104 */   private static final ValueSmallint PROCEDURE_NO_RESULT = ValueSmallint.get((short)1);
/*      */ 
/*      */   
/*  107 */   private static final ValueSmallint PROCEDURE_RETURNS_RESULT = ValueSmallint.get((short)2);
/*      */   
/*  109 */   private static final ValueSmallint TABLE_INDEX_HASHED = ValueSmallint.get((short)2);
/*      */   
/*  111 */   private static final ValueSmallint TABLE_INDEX_OTHER = ValueSmallint.get((short)3);
/*      */ 
/*      */   
/*  114 */   private static final String[] TABLE_TYPES = new String[] { "BASE TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "SYNONYM", "VIEW" };
/*      */ 
/*      */   
/*  117 */   private static final ValueSmallint TYPE_NULLABLE = ValueSmallint.get((short)1);
/*      */   
/*  119 */   private static final ValueSmallint TYPE_SEARCHABLE = ValueSmallint.get((short)3);
/*      */   
/*  121 */   private static final Value NO_USAGE_RESTRICTIONS = ValueVarchar.get("NO_USAGE_RESTRICTIONS");
/*      */   
/*      */   private final SessionLocal session;
/*      */   
/*      */   public DatabaseMetaLocal(SessionLocal paramSessionLocal) {
/*  126 */     this.session = paramSessionLocal;
/*      */   }
/*      */ 
/*      */   
/*      */   public final DefaultNullOrdering defaultNullOrdering() {
/*  131 */     return this.session.getDatabase().getDefaultNullOrdering();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getSQLKeywords() {
/*  136 */     StringBuilder stringBuilder = (new StringBuilder(103)).append("CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,KEY,");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  142 */     Mode mode = this.session.getMode();
/*  143 */     if (mode.limit) {
/*  144 */       stringBuilder.append("LIMIT,");
/*      */     }
/*  146 */     if (mode.minusIsExcept) {
/*  147 */       stringBuilder.append("MINUS,");
/*      */     }
/*  149 */     stringBuilder.append("OFFSET,QUALIFY,REGEXP,ROWNUM,");
/*      */ 
/*      */ 
/*      */     
/*  153 */     if (mode.topInSelect || mode.topInDML) {
/*  154 */       stringBuilder.append("TOP,");
/*      */     }
/*  156 */     return stringBuilder.append("_ROWID_")
/*  157 */       .toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getNumericFunctions() {
/*  162 */     return getFunctions("Functions (Numeric)");
/*      */   }
/*      */ 
/*      */   
/*      */   public String getStringFunctions() {
/*  167 */     return getFunctions("Functions (String)");
/*      */   }
/*      */ 
/*      */   
/*      */   public String getSystemFunctions() {
/*  172 */     return getFunctions("Functions (System)");
/*      */   }
/*      */ 
/*      */   
/*      */   public String getTimeDateFunctions() {
/*  177 */     return getFunctions("Functions (Time and Date)");
/*      */   }
/*      */   
/*      */   private String getFunctions(String paramString) {
/*  181 */     checkClosed();
/*  182 */     StringBuilder stringBuilder = new StringBuilder();
/*      */     try {
/*  184 */       ResultSet resultSet = Help.getTable();
/*  185 */       while (resultSet.next()) {
/*  186 */         if (resultSet.getString(1).trim().equals(paramString)) {
/*  187 */           if (stringBuilder.length() != 0) {
/*  188 */             stringBuilder.append(',');
/*      */           }
/*  190 */           String str = resultSet.getString(2).trim();
/*  191 */           int i = str.indexOf(' ');
/*  192 */           if (i >= 0) {
/*      */             
/*  194 */             StringUtils.trimSubstring(stringBuilder, str, 0, i); continue;
/*      */           } 
/*  196 */           stringBuilder.append(str);
/*      */         }
/*      */       
/*      */       } 
/*  200 */     } catch (Exception exception) {
/*  201 */       throw DbException.convert(exception);
/*      */     } 
/*  203 */     return stringBuilder.toString();
/*      */   }
/*      */ 
/*      */   
/*      */   public String getSearchStringEscape() {
/*  208 */     return (this.session.getDatabase().getSettings()).defaultEscape;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getProcedures(String paramString1, String paramString2, String paramString3) {
/*  213 */     checkClosed();
/*  214 */     SimpleResult simpleResult = new SimpleResult();
/*  215 */     simpleResult.addColumn("PROCEDURE_CAT", TypeInfo.TYPE_VARCHAR);
/*  216 */     simpleResult.addColumn("PROCEDURE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  217 */     simpleResult.addColumn("PROCEDURE_NAME", TypeInfo.TYPE_VARCHAR);
/*  218 */     simpleResult.addColumn("RESERVED1", TypeInfo.TYPE_NULL);
/*  219 */     simpleResult.addColumn("RESERVED2", TypeInfo.TYPE_NULL);
/*  220 */     simpleResult.addColumn("RESERVED3", TypeInfo.TYPE_NULL);
/*  221 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/*  222 */     simpleResult.addColumn("PROCEDURE_TYPE", TypeInfo.TYPE_SMALLINT);
/*  223 */     simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
/*  224 */     if (!checkCatalogName(paramString1)) {
/*  225 */       return (ResultInterface)simpleResult;
/*      */     }
/*  227 */     Database database = this.session.getDatabase();
/*  228 */     Value value = getString(database.getShortName());
/*  229 */     CompareLike compareLike = getLike(paramString3);
/*  230 */     for (Schema schema : getSchemasForPattern(paramString2)) {
/*  231 */       Value value1 = getString(schema.getName());
/*  232 */       for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
/*  233 */         String str = userDefinedFunction.getName();
/*  234 */         if (compareLike != null && !compareLike.test(str)) {
/*      */           continue;
/*      */         }
/*  237 */         Value value2 = getString(str);
/*  238 */         if (userDefinedFunction instanceof FunctionAlias) {
/*      */           FunctionAlias.JavaMethod[] arrayOfJavaMethod;
/*      */           try {
/*  241 */             arrayOfJavaMethod = ((FunctionAlias)userDefinedFunction).getJavaMethods();
/*  242 */           } catch (DbException dbException) {
/*      */             continue;
/*      */           } 
/*  245 */           for (byte b = 0; b < arrayOfJavaMethod.length; b++) {
/*  246 */             FunctionAlias.JavaMethod javaMethod = arrayOfJavaMethod[b];
/*  247 */             TypeInfo typeInfo = javaMethod.getDataType();
/*  248 */             getProceduresAdd(simpleResult, value, value1, value2, userDefinedFunction
/*  249 */                 .getComment(), (typeInfo == null || typeInfo
/*  250 */                 .getValueType() != 0) ? PROCEDURE_RETURNS_RESULT : PROCEDURE_NO_RESULT, 
/*      */                 
/*  252 */                 getString(str + '_' + (b + 1)));
/*      */           }  continue;
/*      */         } 
/*  255 */         getProceduresAdd(simpleResult, value, value1, value2, userDefinedFunction
/*  256 */             .getComment(), PROCEDURE_RETURNS_RESULT, value2);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  261 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1, 2, 8 }));
/*  262 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void getProceduresAdd(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, String paramString, ValueSmallint paramValueSmallint, Value paramValue4) {
/*  267 */     paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  281 */           getString(paramString), (Value)paramValueSmallint, paramValue4 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/*  291 */     checkClosed();
/*  292 */     SimpleResult simpleResult = new SimpleResult();
/*  293 */     simpleResult.addColumn("PROCEDURE_CAT", TypeInfo.TYPE_VARCHAR);
/*  294 */     simpleResult.addColumn("PROCEDURE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  295 */     simpleResult.addColumn("PROCEDURE_NAME", TypeInfo.TYPE_VARCHAR);
/*  296 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/*  297 */     simpleResult.addColumn("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT);
/*  298 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  299 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  300 */     simpleResult.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
/*  301 */     simpleResult.addColumn("LENGTH", TypeInfo.TYPE_INTEGER);
/*  302 */     simpleResult.addColumn("SCALE", TypeInfo.TYPE_SMALLINT);
/*  303 */     simpleResult.addColumn("RADIX", TypeInfo.TYPE_SMALLINT);
/*  304 */     simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
/*  305 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/*  306 */     simpleResult.addColumn("COLUMN_DEF", TypeInfo.TYPE_VARCHAR);
/*  307 */     simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  308 */     simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
/*  309 */     simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
/*  310 */     simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
/*  311 */     simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
/*  312 */     simpleResult.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
/*  313 */     if (!checkCatalogName(paramString1)) {
/*  314 */       return (ResultInterface)simpleResult;
/*      */     }
/*  316 */     Database database = this.session.getDatabase();
/*  317 */     Value value = getString(database.getShortName());
/*  318 */     CompareLike compareLike = getLike(paramString3);
/*  319 */     for (Schema schema : getSchemasForPattern(paramString2)) {
/*  320 */       Value value1 = getString(schema.getName());
/*  321 */       for (UserDefinedFunction userDefinedFunction : schema.getAllFunctionsAndAggregates()) {
/*  322 */         FunctionAlias.JavaMethod[] arrayOfJavaMethod; if (!(userDefinedFunction instanceof FunctionAlias)) {
/*      */           continue;
/*      */         }
/*  325 */         String str = userDefinedFunction.getName();
/*  326 */         if (compareLike != null && !compareLike.test(str)) {
/*      */           continue;
/*      */         }
/*  329 */         Value value2 = getString(str);
/*      */         
/*      */         try {
/*  332 */           arrayOfJavaMethod = ((FunctionAlias)userDefinedFunction).getJavaMethods();
/*  333 */         } catch (DbException dbException) {
/*      */           continue;
/*      */         }  byte b; int i;
/*  336 */         for (b = 0, i = arrayOfJavaMethod.length; b < i; b++) {
/*  337 */           FunctionAlias.JavaMethod javaMethod = arrayOfJavaMethod[b];
/*  338 */           Value value3 = getString(str + '_' + (b + 1));
/*  339 */           TypeInfo typeInfo = javaMethod.getDataType();
/*  340 */           if (typeInfo != null && typeInfo.getValueType() != 0) {
/*  341 */             getProcedureColumnAdd(simpleResult, value, value1, value2, value3, typeInfo, javaMethod
/*  342 */                 .getClass().isPrimitive(), 0);
/*      */           }
/*  344 */           Class[] arrayOfClass = javaMethod.getColumnClasses(); byte b1, b2; int j;
/*  345 */           for (b1 = 1, b2 = javaMethod.hasConnectionParam() ? 1 : 0, j = arrayOfClass.length; b2 < j; b1++, b2++) {
/*  346 */             Class clazz = arrayOfClass[b2];
/*  347 */             getProcedureColumnAdd(simpleResult, value, value1, value2, value3, 
/*  348 */                 ValueToObjectConverter2.classToType(clazz), clazz.isPrimitive(), b1);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  355 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1, 2, 19 }));
/*  356 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void getProcedureColumnAdd(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, Value paramValue4, TypeInfo paramTypeInfo, boolean paramBoolean, int paramInt) {
/*  361 */     int i = paramTypeInfo.getValueType();
/*  362 */     DataType dataType = DataType.getDataType(i);
/*  363 */     ValueInteger valueInteger = ValueInteger.get(MathUtils.convertLongToInt(paramTypeInfo.getPrecision()));
/*  364 */     paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  372 */           getString((paramInt == 0) ? "RESULT" : ("P" + paramInt)), (paramInt == 0) ? (Value)PROCEDURE_COLUMN_RETURN : (Value)PROCEDURE_COLUMN_IN, 
/*      */ 
/*      */ 
/*      */           
/*  376 */           (Value)ValueInteger.get(DataType.convertTypeToSQLType(paramTypeInfo)), 
/*      */           
/*  378 */           getDataTypeName(paramTypeInfo), (Value)valueInteger, (Value)valueInteger, dataType.supportsScale ? 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  385 */           (Value)ValueSmallint.get(MathUtils.convertIntToShort(dataType.defaultScale)) : (Value)ValueNull.INSTANCE,
/*      */ 
/*      */           
/*  388 */           getRadix(i, true), paramBoolean ? (Value)COLUMN_NO_NULLS_SMALL : (Value)COLUMN_NULLABLE_UNKNOWN_SMALL, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  400 */           DataType.isBinaryStringType(i) || DataType.isCharacterStringType(i)) ? (Value)valueInteger : (Value)ValueNull.INSTANCE, 
/*      */ 
/*      */           
/*  403 */           (Value)ValueInteger.get(paramInt), (Value)ValueVarchar.EMPTY, paramValue4 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
/*      */     HashSet<String> hashSet;
/*  412 */     SimpleResult simpleResult = new SimpleResult();
/*  413 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  414 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  415 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  416 */     simpleResult.addColumn("TABLE_TYPE", TypeInfo.TYPE_VARCHAR);
/*  417 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/*  418 */     simpleResult.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
/*  419 */     simpleResult.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  420 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  421 */     simpleResult.addColumn("SELF_REFERENCING_COL_NAME", TypeInfo.TYPE_VARCHAR);
/*  422 */     simpleResult.addColumn("REF_GENERATION", TypeInfo.TYPE_VARCHAR);
/*  423 */     if (!checkCatalogName(paramString1)) {
/*  424 */       return (ResultInterface)simpleResult;
/*      */     }
/*  426 */     Database database = this.session.getDatabase();
/*  427 */     Value value = getString(database.getShortName());
/*      */     
/*  429 */     if (paramArrayOfString != null) {
/*  430 */       hashSet = new HashSet(8);
/*  431 */       for (String str : paramArrayOfString) {
/*  432 */         int i = Arrays.binarySearch((Object[])TABLE_TYPES, str);
/*  433 */         if (i >= 0) {
/*  434 */           hashSet.add(TABLE_TYPES[i]);
/*  435 */         } else if (str.equals("TABLE")) {
/*  436 */           hashSet.add("BASE TABLE");
/*      */         } 
/*      */       } 
/*  439 */       if (hashSet.isEmpty()) {
/*  440 */         return (ResultInterface)simpleResult;
/*      */       }
/*      */     } else {
/*  443 */       hashSet = null;
/*      */     } 
/*  445 */     for (Schema schema : getSchemasForPattern(paramString2)) {
/*  446 */       Value value1 = getString(schema.getName());
/*  447 */       for (SchemaObject schemaObject : getTablesForPattern(schema, paramString3)) {
/*  448 */         Value value2 = getString(schemaObject.getName());
/*  449 */         if (schemaObject instanceof Table) {
/*  450 */           Table table = (Table)schemaObject;
/*  451 */           if (!table.isHidden())
/*  452 */             getTablesAdd(simpleResult, value, value1, value2, table, false, hashSet); 
/*      */           continue;
/*      */         } 
/*  455 */         getTablesAdd(simpleResult, value, value1, value2, ((TableSynonym)schemaObject).getSynonymFor(), true, hashSet);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  461 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 3, 1, 2 }));
/*  462 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void getTablesAdd(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, Table paramTable, boolean paramBoolean, HashSet<String> paramHashSet) {
/*  467 */     String str = paramBoolean ? "SYNONYM" : paramTable.getSQLTableType();
/*  468 */     if (paramHashSet != null && !paramHashSet.contains(str)) {
/*      */       return;
/*      */     }
/*  471 */     paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  479 */           getString(str), 
/*      */           
/*  481 */           getString(paramTable.getComment()), (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE });
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
/*      */   public ResultInterface getSchemas() {
/*  496 */     return getSchemas(null, null);
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getCatalogs() {
/*  501 */     checkClosed();
/*  502 */     SimpleResult simpleResult = new SimpleResult();
/*  503 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  504 */     simpleResult.addRow(new Value[] { getString(this.session.getDatabase().getShortName()) });
/*  505 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getTableTypes() {
/*  510 */     SimpleResult simpleResult = new SimpleResult();
/*  511 */     simpleResult.addColumn("TABLE_TYPE", TypeInfo.TYPE_VARCHAR);
/*      */     
/*  513 */     simpleResult.addRow(new Value[] { getString("BASE TABLE") });
/*  514 */     simpleResult.addRow(new Value[] { getString("GLOBAL TEMPORARY") });
/*  515 */     simpleResult.addRow(new Value[] { getString("LOCAL TEMPORARY") });
/*  516 */     simpleResult.addRow(new Value[] { getString("SYNONYM") });
/*  517 */     simpleResult.addRow(new Value[] { getString("VIEW") });
/*  518 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/*  524 */     SimpleResult simpleResult = new SimpleResult();
/*  525 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  526 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  527 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  528 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/*  529 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  530 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  531 */     simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
/*  532 */     simpleResult.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
/*  533 */     simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
/*  534 */     simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
/*  535 */     simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_INTEGER);
/*  536 */     simpleResult.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
/*  537 */     simpleResult.addColumn("COLUMN_DEF", TypeInfo.TYPE_VARCHAR);
/*  538 */     simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  539 */     simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
/*  540 */     simpleResult.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
/*  541 */     simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
/*  542 */     simpleResult.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
/*  543 */     simpleResult.addColumn("SCOPE_CATALOG", TypeInfo.TYPE_VARCHAR);
/*  544 */     simpleResult.addColumn("SCOPE_SCHEMA", TypeInfo.TYPE_VARCHAR);
/*  545 */     simpleResult.addColumn("SCOPE_TABLE", TypeInfo.TYPE_VARCHAR);
/*  546 */     simpleResult.addColumn("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT);
/*  547 */     simpleResult.addColumn("IS_AUTOINCREMENT", TypeInfo.TYPE_VARCHAR);
/*  548 */     simpleResult.addColumn("IS_GENERATEDCOLUMN", TypeInfo.TYPE_VARCHAR);
/*  549 */     if (!checkCatalogName(paramString1)) {
/*  550 */       return (ResultInterface)simpleResult;
/*      */     }
/*  552 */     Database database = this.session.getDatabase();
/*  553 */     Value value = getString(database.getShortName());
/*  554 */     CompareLike compareLike = getLike(paramString4);
/*  555 */     for (Schema schema : getSchemasForPattern(paramString2)) {
/*  556 */       Value value1 = getString(schema.getName());
/*  557 */       for (SchemaObject schemaObject : getTablesForPattern(schema, paramString3)) {
/*  558 */         Value value2 = getString(schemaObject.getName());
/*  559 */         if (schemaObject instanceof Table) {
/*  560 */           Table table1 = (Table)schemaObject;
/*  561 */           if (!table1.isHidden())
/*  562 */             getColumnsAdd(simpleResult, value, value1, value2, table1, compareLike); 
/*      */           continue;
/*      */         } 
/*  565 */         TableSynonym tableSynonym = (TableSynonym)schemaObject;
/*  566 */         Table table = tableSynonym.getSynonymFor();
/*  567 */         getColumnsAdd(simpleResult, value, value1, value2, table, compareLike);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  572 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1, 2, 16 }));
/*  573 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void getColumnsAdd(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, Table paramTable, CompareLike paramCompareLike) {
/*  578 */     byte b = 0;
/*  579 */     for (Column column : paramTable.getColumns()) {
/*  580 */       if (column.getVisible()) {
/*      */ 
/*      */         
/*  583 */         b++;
/*  584 */         String str = column.getName();
/*  585 */         if (paramCompareLike == null || paramCompareLike.test(str)) {
/*      */ 
/*      */           
/*  588 */           TypeInfo typeInfo = column.getType();
/*  589 */           ValueInteger valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
/*  590 */           boolean bool1 = column.isNullable(), bool2 = column.isGenerated();
/*  591 */           paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  599 */                 getString(str), 
/*      */                 
/*  601 */                 (Value)ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), 
/*      */                 
/*  603 */                 getDataTypeName(typeInfo), (Value)valueInteger, (Value)ValueNull.INSTANCE, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  609 */                 (Value)ValueInteger.get(typeInfo.getScale()), 
/*      */                 
/*  611 */                 getRadix(typeInfo.getValueType(), false), bool1 ? (Value)COLUMN_NULLABLE : (Value)COLUMN_NO_NULLS, 
/*      */ 
/*      */ 
/*      */                 
/*  615 */                 getString(column.getComment()), bool2 ? (Value)ValueNull.INSTANCE : 
/*      */                 
/*  617 */                 getString(column.getDefaultSQL()), (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)valueInteger, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  625 */                 (Value)ValueInteger.get(b), bool1 ? YES : NO, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  637 */                 column.isIdentity() ? YES : NO, bool2 ? YES : NO });
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4) {
/*  645 */     if (paramString3 == null) {
/*  646 */       throw DbException.getInvalidValueException("table", null);
/*      */     }
/*  648 */     checkClosed();
/*  649 */     SimpleResult simpleResult = new SimpleResult();
/*  650 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  651 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  652 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  653 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/*  654 */     simpleResult.addColumn("GRANTOR", TypeInfo.TYPE_VARCHAR);
/*  655 */     simpleResult.addColumn("GRANTEE", TypeInfo.TYPE_VARCHAR);
/*  656 */     simpleResult.addColumn("PRIVILEGE", TypeInfo.TYPE_VARCHAR);
/*  657 */     simpleResult.addColumn("IS_GRANTABLE", TypeInfo.TYPE_VARCHAR);
/*  658 */     if (!checkCatalogName(paramString1)) {
/*  659 */       return (ResultInterface)simpleResult;
/*      */     }
/*  661 */     Database database = this.session.getDatabase();
/*  662 */     Value value = getString(database.getShortName());
/*  663 */     CompareLike compareLike = getLike(paramString4);
/*  664 */     for (Right right : database.getAllRights()) {
/*  665 */       DbObject dbObject = right.getGrantedObject();
/*  666 */       if (!(dbObject instanceof Table)) {
/*      */         continue;
/*      */       }
/*  669 */       Table table = (Table)dbObject;
/*  670 */       if (table.isHidden()) {
/*      */         continue;
/*      */       }
/*  673 */       String str = table.getName();
/*  674 */       if (!database.equalsIdentifiers(paramString3, str)) {
/*      */         continue;
/*      */       }
/*  677 */       Schema schema = table.getSchema();
/*  678 */       if (!checkSchema(paramString2, schema)) {
/*      */         continue;
/*      */       }
/*  681 */       addPrivileges(simpleResult, value, schema.getName(), str, right.getGrantee(), right.getRightMask(), compareLike, table
/*  682 */           .getColumns());
/*      */     } 
/*      */     
/*  685 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 3, 6 }));
/*  686 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getTablePrivileges(String paramString1, String paramString2, String paramString3) {
/*  691 */     checkClosed();
/*  692 */     SimpleResult simpleResult = new SimpleResult();
/*  693 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  694 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  695 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  696 */     simpleResult.addColumn("GRANTOR", TypeInfo.TYPE_VARCHAR);
/*  697 */     simpleResult.addColumn("GRANTEE", TypeInfo.TYPE_VARCHAR);
/*  698 */     simpleResult.addColumn("PRIVILEGE", TypeInfo.TYPE_VARCHAR);
/*  699 */     simpleResult.addColumn("IS_GRANTABLE", TypeInfo.TYPE_VARCHAR);
/*  700 */     if (!checkCatalogName(paramString1)) {
/*  701 */       return (ResultInterface)simpleResult;
/*      */     }
/*  703 */     Database database = this.session.getDatabase();
/*  704 */     Value value = getString(database.getShortName());
/*  705 */     CompareLike compareLike1 = getLike(paramString2);
/*  706 */     CompareLike compareLike2 = getLike(paramString3);
/*  707 */     for (Right right : database.getAllRights()) {
/*  708 */       DbObject dbObject = right.getGrantedObject();
/*  709 */       if (!(dbObject instanceof Table)) {
/*      */         continue;
/*      */       }
/*  712 */       Table table = (Table)dbObject;
/*  713 */       if (table.isHidden()) {
/*      */         continue;
/*      */       }
/*  716 */       String str1 = table.getName();
/*  717 */       if (compareLike2 != null && !compareLike2.test(str1)) {
/*      */         continue;
/*      */       }
/*  720 */       Schema schema = table.getSchema();
/*  721 */       String str2 = schema.getName();
/*  722 */       if (paramString2 != null && (
/*  723 */         paramString2.isEmpty() ? (
/*  724 */         schema != database.getMainSchema()) : 
/*      */ 
/*      */ 
/*      */         
/*  728 */         !compareLike1.test(str2))) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/*  733 */       addPrivileges(simpleResult, value, str2, str1, right.getGrantee(), right.getRightMask(), null, null);
/*      */     } 
/*      */     
/*  736 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1, 2, 5 }));
/*  737 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void addPrivileges(SimpleResult paramSimpleResult, Value paramValue, String paramString1, String paramString2, DbObject paramDbObject, int paramInt, CompareLike paramCompareLike, Column[] paramArrayOfColumn) {
/*  742 */     Value value1 = getString(paramString1);
/*  743 */     Value value2 = getString(paramString2);
/*  744 */     Value value3 = getString(paramDbObject.getName());
/*  745 */     boolean bool = (paramDbObject.getType() == 2 && ((User)paramDbObject).isAdmin()) ? true : false;
/*  746 */     if ((paramInt & 0x1) != 0) {
/*  747 */       addPrivilege(paramSimpleResult, paramValue, value1, value2, value3, "SELECT", bool, paramCompareLike, paramArrayOfColumn);
/*      */     }
/*      */     
/*  750 */     if ((paramInt & 0x4) != 0) {
/*  751 */       addPrivilege(paramSimpleResult, paramValue, value1, value2, value3, "INSERT", bool, paramCompareLike, paramArrayOfColumn);
/*      */     }
/*      */     
/*  754 */     if ((paramInt & 0x8) != 0) {
/*  755 */       addPrivilege(paramSimpleResult, paramValue, value1, value2, value3, "UPDATE", bool, paramCompareLike, paramArrayOfColumn);
/*      */     }
/*      */     
/*  758 */     if ((paramInt & 0x2) != 0) {
/*  759 */       addPrivilege(paramSimpleResult, paramValue, value1, value2, value3, "DELETE", bool, paramCompareLike, paramArrayOfColumn);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void addPrivilege(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, Value paramValue4, String paramString, boolean paramBoolean, CompareLike paramCompareLike, Column[] paramArrayOfColumn) {
/*  766 */     if (paramArrayOfColumn == null) {
/*  767 */       paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, (Value)ValueNull.INSTANCE, paramValue4, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  779 */             getString(paramString), paramBoolean ? YES : NO });
/*      */     }
/*      */     else {
/*      */       
/*  783 */       for (Column column : paramArrayOfColumn) {
/*  784 */         String str = column.getName();
/*  785 */         if (paramCompareLike == null || paramCompareLike.test(str))
/*      */         {
/*      */           
/*  788 */           paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  796 */                 getString(str), (Value)ValueNull.INSTANCE, paramValue4, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  802 */                 getString(paramString), paramBoolean ? YES : NO });
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean) {
/*  812 */     if (paramString3 == null) {
/*  813 */       throw DbException.getInvalidValueException("table", null);
/*      */     }
/*  815 */     checkClosed();
/*  816 */     SimpleResult simpleResult = new SimpleResult();
/*  817 */     simpleResult.addColumn("SCOPE", TypeInfo.TYPE_SMALLINT);
/*  818 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/*  819 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/*  820 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/*  821 */     simpleResult.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
/*  822 */     simpleResult.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
/*  823 */     simpleResult.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_SMALLINT);
/*  824 */     simpleResult.addColumn("PSEUDO_COLUMN", TypeInfo.TYPE_SMALLINT);
/*  825 */     if (!checkCatalogName(paramString1)) {
/*  826 */       return (ResultInterface)simpleResult;
/*      */     }
/*  828 */     for (Schema schema : getSchemas(paramString2)) {
/*  829 */       Table table = schema.findTableOrView(this.session, paramString3);
/*  830 */       if (table == null || table.isHidden()) {
/*      */         continue;
/*      */       }
/*  833 */       ArrayList arrayList = table.getConstraints();
/*  834 */       if (arrayList == null) {
/*      */         continue;
/*      */       }
/*  837 */       for (Constraint constraint : arrayList) {
/*  838 */         if (constraint.getConstraintType() != Constraint.Type.PRIMARY_KEY) {
/*      */           continue;
/*      */         }
/*  841 */         IndexColumn[] arrayOfIndexColumn = ((ConstraintUnique)constraint).getColumns(); byte b; int i;
/*  842 */         for (b = 0, i = arrayOfIndexColumn.length; b < i; b++) {
/*  843 */           IndexColumn indexColumn = arrayOfIndexColumn[b];
/*  844 */           Column column = indexColumn.column;
/*  845 */           TypeInfo typeInfo = column.getType();
/*  846 */           DataType dataType = DataType.getDataType(typeInfo.getValueType());
/*  847 */           simpleResult.addRow(new Value[] { (Value)BEST_ROW_SESSION, 
/*      */ 
/*      */ 
/*      */                 
/*  851 */                 getString(column.getName()), 
/*      */                 
/*  853 */                 (Value)ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), 
/*      */                 
/*  855 */                 getDataTypeName(typeInfo), 
/*      */                 
/*  857 */                 (Value)ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision())), (Value)ValueNull.INSTANCE, dataType.supportsScale ? 
/*      */ 
/*      */ 
/*      */                 
/*  861 */                 (Value)ValueSmallint.get(MathUtils.convertIntToShort(typeInfo.getScale())) : (Value)ValueNull.INSTANCE, (Value)BEST_ROW_NOT_PSEUDO });
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  869 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */   
/*      */   private Value getDataTypeName(TypeInfo paramTypeInfo) {
/*  873 */     return getString(paramTypeInfo.getDeclaredTypeName());
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getPrimaryKeys(String paramString1, String paramString2, String paramString3) {
/*  878 */     if (paramString3 == null) {
/*  879 */       throw DbException.getInvalidValueException("table", null);
/*      */     }
/*  881 */     checkClosed();
/*  882 */     SimpleResult simpleResult = new SimpleResult();
/*  883 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/*  884 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/*  885 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*  886 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/*  887 */     simpleResult.addColumn("KEY_SEQ", TypeInfo.TYPE_SMALLINT);
/*  888 */     simpleResult.addColumn("PK_NAME", TypeInfo.TYPE_VARCHAR);
/*  889 */     if (!checkCatalogName(paramString1)) {
/*  890 */       return (ResultInterface)simpleResult;
/*      */     }
/*  892 */     Database database = this.session.getDatabase();
/*  893 */     Value value = getString(database.getShortName());
/*  894 */     for (Schema schema : getSchemas(paramString2)) {
/*  895 */       Table table = schema.findTableOrView(this.session, paramString3);
/*  896 */       if (table == null || table.isHidden()) {
/*      */         continue;
/*      */       }
/*  899 */       ArrayList arrayList = table.getConstraints();
/*  900 */       if (arrayList == null) {
/*      */         continue;
/*      */       }
/*  903 */       for (Constraint constraint : arrayList) {
/*  904 */         if (constraint.getConstraintType() != Constraint.Type.PRIMARY_KEY) {
/*      */           continue;
/*      */         }
/*  907 */         Value value1 = getString(schema.getName());
/*  908 */         Value value2 = getString(table.getName());
/*  909 */         Value value3 = getString(constraint.getName());
/*  910 */         IndexColumn[] arrayOfIndexColumn = ((ConstraintUnique)constraint).getColumns(); byte b; int i;
/*  911 */         for (b = 0, i = arrayOfIndexColumn.length; b < i;) {
/*  912 */           simpleResult.addRow(new Value[] { value, value1, value2, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/*  920 */                 getString((arrayOfIndexColumn[b]).column.getName()), 
/*      */                 
/*  922 */                 (Value)ValueSmallint.get((short)++b), value3 });
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  929 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 3 }));
/*  930 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getImportedKeys(String paramString1, String paramString2, String paramString3) {
/*  935 */     if (paramString3 == null) {
/*  936 */       throw DbException.getInvalidValueException("table", null);
/*      */     }
/*  938 */     SimpleResult simpleResult = initCrossReferenceResult();
/*  939 */     if (!checkCatalogName(paramString1)) {
/*  940 */       return (ResultInterface)simpleResult;
/*      */     }
/*  942 */     Database database = this.session.getDatabase();
/*  943 */     Value value = getString(database.getShortName());
/*  944 */     for (Schema schema : getSchemas(paramString2)) {
/*  945 */       Table table = schema.findTableOrView(this.session, paramString3);
/*  946 */       if (table == null || table.isHidden()) {
/*      */         continue;
/*      */       }
/*  949 */       ArrayList arrayList = table.getConstraints();
/*  950 */       if (arrayList == null) {
/*      */         continue;
/*      */       }
/*  953 */       for (Constraint constraint : arrayList) {
/*  954 */         if (constraint.getConstraintType() != Constraint.Type.REFERENTIAL) {
/*      */           continue;
/*      */         }
/*  957 */         ConstraintReferential constraintReferential = (ConstraintReferential)constraint;
/*  958 */         Table table1 = constraintReferential.getTable();
/*  959 */         if (table1 != table) {
/*      */           continue;
/*      */         }
/*  962 */         Table table2 = constraintReferential.getRefTable();
/*  963 */         addCrossReferenceResult(simpleResult, value, table2.getSchema().getName(), table2, table1
/*  964 */             .getSchema().getName(), table1, constraintReferential);
/*      */       } 
/*      */     } 
/*      */     
/*  968 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1, 2, 8 }));
/*  969 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   public ResultInterface getExportedKeys(String paramString1, String paramString2, String paramString3) {
/*  974 */     if (paramString3 == null) {
/*  975 */       throw DbException.getInvalidValueException("table", null);
/*      */     }
/*  977 */     SimpleResult simpleResult = initCrossReferenceResult();
/*  978 */     if (!checkCatalogName(paramString1)) {
/*  979 */       return (ResultInterface)simpleResult;
/*      */     }
/*  981 */     Database database = this.session.getDatabase();
/*  982 */     Value value = getString(database.getShortName());
/*  983 */     for (Schema schema : getSchemas(paramString2)) {
/*  984 */       Table table = schema.findTableOrView(this.session, paramString3);
/*  985 */       if (table == null || table.isHidden()) {
/*      */         continue;
/*      */       }
/*  988 */       ArrayList arrayList = table.getConstraints();
/*  989 */       if (arrayList == null) {
/*      */         continue;
/*      */       }
/*  992 */       for (Constraint constraint : arrayList) {
/*  993 */         if (constraint.getConstraintType() != Constraint.Type.REFERENTIAL) {
/*      */           continue;
/*      */         }
/*  996 */         ConstraintReferential constraintReferential = (ConstraintReferential)constraint;
/*  997 */         Table table1 = constraintReferential.getRefTable();
/*  998 */         if (table1 != table) {
/*      */           continue;
/*      */         }
/* 1001 */         Table table2 = constraintReferential.getTable();
/* 1002 */         addCrossReferenceResult(simpleResult, value, table1.getSchema().getName(), table1, table2
/* 1003 */             .getSchema().getName(), table2, constraintReferential);
/*      */       } 
/*      */     } 
/*      */     
/* 1007 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 5, 6, 8 }));
/* 1008 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) {
/* 1014 */     if (paramString3 == null) {
/* 1015 */       throw DbException.getInvalidValueException("primaryTable", null);
/*      */     }
/* 1017 */     if (paramString6 == null) {
/* 1018 */       throw DbException.getInvalidValueException("foreignTable", null);
/*      */     }
/* 1020 */     SimpleResult simpleResult = initCrossReferenceResult();
/* 1021 */     if (!checkCatalogName(paramString1) || !checkCatalogName(paramString4)) {
/* 1022 */       return (ResultInterface)simpleResult;
/*      */     }
/* 1024 */     Database database = this.session.getDatabase();
/* 1025 */     Value value = getString(database.getShortName());
/* 1026 */     for (Schema schema : getSchemas(paramString5)) {
/* 1027 */       Table table = schema.findTableOrView(this.session, paramString6);
/* 1028 */       if (table == null || table.isHidden()) {
/*      */         continue;
/*      */       }
/* 1031 */       ArrayList arrayList = table.getConstraints();
/* 1032 */       if (arrayList == null) {
/*      */         continue;
/*      */       }
/* 1035 */       for (Constraint constraint : arrayList) {
/* 1036 */         if (constraint.getConstraintType() != Constraint.Type.REFERENTIAL) {
/*      */           continue;
/*      */         }
/* 1039 */         ConstraintReferential constraintReferential = (ConstraintReferential)constraint;
/* 1040 */         Table table1 = constraintReferential.getTable();
/* 1041 */         if (table1 != table) {
/*      */           continue;
/*      */         }
/* 1044 */         Table table2 = constraintReferential.getRefTable();
/* 1045 */         if (!database.equalsIdentifiers(table2.getName(), paramString3)) {
/*      */           continue;
/*      */         }
/* 1048 */         Schema schema1 = table2.getSchema();
/* 1049 */         if (!checkSchema(paramString2, schema1)) {
/*      */           continue;
/*      */         }
/* 1052 */         addCrossReferenceResult(simpleResult, value, schema1.getName(), table2, table1
/* 1053 */             .getSchema().getName(), table1, constraintReferential);
/*      */       } 
/*      */     } 
/*      */     
/* 1057 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 5, 6, 8 }));
/* 1058 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */   
/*      */   private SimpleResult initCrossReferenceResult() {
/* 1062 */     checkClosed();
/* 1063 */     SimpleResult simpleResult = new SimpleResult();
/* 1064 */     simpleResult.addColumn("PKTABLE_CAT", TypeInfo.TYPE_VARCHAR);
/* 1065 */     simpleResult.addColumn("PKTABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 1066 */     simpleResult.addColumn("PKTABLE_NAME", TypeInfo.TYPE_VARCHAR);
/* 1067 */     simpleResult.addColumn("PKCOLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/* 1068 */     simpleResult.addColumn("FKTABLE_CAT", TypeInfo.TYPE_VARCHAR);
/* 1069 */     simpleResult.addColumn("FKTABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 1070 */     simpleResult.addColumn("FKTABLE_NAME", TypeInfo.TYPE_VARCHAR);
/* 1071 */     simpleResult.addColumn("FKCOLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/* 1072 */     simpleResult.addColumn("KEY_SEQ", TypeInfo.TYPE_SMALLINT);
/* 1073 */     simpleResult.addColumn("UPDATE_RULE", TypeInfo.TYPE_SMALLINT);
/* 1074 */     simpleResult.addColumn("DELETE_RULE", TypeInfo.TYPE_SMALLINT);
/* 1075 */     simpleResult.addColumn("FK_NAME", TypeInfo.TYPE_VARCHAR);
/* 1076 */     simpleResult.addColumn("PK_NAME", TypeInfo.TYPE_VARCHAR);
/* 1077 */     simpleResult.addColumn("DEFERRABILITY", TypeInfo.TYPE_SMALLINT);
/* 1078 */     return simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void addCrossReferenceResult(SimpleResult paramSimpleResult, Value paramValue, String paramString1, Table paramTable1, String paramString2, Table paramTable2, ConstraintReferential paramConstraintReferential) {
/* 1083 */     Value value1 = getString(paramString1);
/* 1084 */     Value value2 = getString(paramTable1.getName());
/* 1085 */     Value value3 = getString(paramString2);
/* 1086 */     Value value4 = getString(paramTable2.getName());
/* 1087 */     IndexColumn[] arrayOfIndexColumn1 = paramConstraintReferential.getRefColumns();
/* 1088 */     IndexColumn[] arrayOfIndexColumn2 = paramConstraintReferential.getColumns();
/* 1089 */     ValueSmallint valueSmallint1 = getRefAction(paramConstraintReferential.getUpdateAction());
/* 1090 */     ValueSmallint valueSmallint2 = getRefAction(paramConstraintReferential.getDeleteAction());
/* 1091 */     Value value5 = getString(paramConstraintReferential.getName());
/* 1092 */     Value value6 = getString(paramConstraintReferential.getReferencedConstraint().getName()); byte b; int i;
/* 1093 */     for (b = 0, i = arrayOfIndexColumn2.length; b < i; b++) {
/* 1094 */       paramSimpleResult.addRow(new Value[] { paramValue, value1, value2, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1102 */             getString((arrayOfIndexColumn1[b]).column.getName()), paramValue, value3, value4, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1110 */             getString((arrayOfIndexColumn2[b]).column.getName()), 
/*      */             
/* 1112 */             (Value)ValueSmallint.get((short)(b + 1)), (Value)valueSmallint1, (Value)valueSmallint2, value5, value6, (Value)IMPORTED_KEY_NOT_DEFERRABLE });
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
/*      */   private static ValueSmallint getRefAction(ConstraintActionType paramConstraintActionType) {
/* 1127 */     switch (paramConstraintActionType) {
/*      */       case CASCADE:
/* 1129 */         return IMPORTED_KEY_CASCADE;
/*      */       case RESTRICT:
/* 1131 */         return IMPORTED_KEY_RESTRICT;
/*      */       case SET_DEFAULT:
/* 1133 */         return IMPORTED_KEY_DEFAULT;
/*      */       case SET_NULL:
/* 1135 */         return IMPORTED_KEY_SET_NULL;
/*      */     } 
/* 1137 */     throw DbException.getInternalError("action=" + paramConstraintActionType);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getTypeInfo() {
/* 1143 */     checkClosed();
/* 1144 */     SimpleResult simpleResult = new SimpleResult();
/* 1145 */     simpleResult.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/* 1146 */     simpleResult.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
/* 1147 */     simpleResult.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
/* 1148 */     simpleResult.addColumn("LITERAL_PREFIX", TypeInfo.TYPE_VARCHAR);
/* 1149 */     simpleResult.addColumn("LITERAL_SUFFIX", TypeInfo.TYPE_VARCHAR);
/* 1150 */     simpleResult.addColumn("CREATE_PARAMS", TypeInfo.TYPE_VARCHAR);
/* 1151 */     simpleResult.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
/* 1152 */     simpleResult.addColumn("CASE_SENSITIVE", TypeInfo.TYPE_BOOLEAN);
/* 1153 */     simpleResult.addColumn("SEARCHABLE", TypeInfo.TYPE_SMALLINT);
/* 1154 */     simpleResult.addColumn("UNSIGNED_ATTRIBUTE", TypeInfo.TYPE_BOOLEAN);
/* 1155 */     simpleResult.addColumn("FIXED_PREC_SCALE", TypeInfo.TYPE_BOOLEAN);
/* 1156 */     simpleResult.addColumn("AUTO_INCREMENT", TypeInfo.TYPE_BOOLEAN);
/* 1157 */     simpleResult.addColumn("LOCAL_TYPE_NAME", TypeInfo.TYPE_VARCHAR);
/* 1158 */     simpleResult.addColumn("MINIMUM_SCALE", TypeInfo.TYPE_SMALLINT);
/* 1159 */     simpleResult.addColumn("MAXIMUM_SCALE", TypeInfo.TYPE_SMALLINT);
/* 1160 */     simpleResult.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
/* 1161 */     simpleResult.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
/* 1162 */     simpleResult.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
/* 1163 */     for (byte b1 = 1, b2 = 42; b1 < b2; b1++) {
/* 1164 */       DataType dataType = DataType.getDataType(b1);
/* 1165 */       Value value = getString(Value.getTypeName(dataType.type));
/* 1166 */       simpleResult.addRow(new Value[] { value, 
/*      */ 
/*      */ 
/*      */             
/* 1170 */             (Value)ValueInteger.get(dataType.sqlType), 
/*      */             
/* 1172 */             (Value)ValueInteger.get(MathUtils.convertLongToInt(dataType.maxPrecision)), 
/*      */             
/* 1174 */             getString(dataType.prefix), 
/*      */             
/* 1176 */             getString(dataType.suffix), 
/*      */             
/* 1178 */             getString(dataType.params), (Value)TYPE_NULLABLE, 
/*      */ 
/*      */ 
/*      */             
/* 1182 */             (Value)ValueBoolean.get(dataType.caseSensitive), (Value)TYPE_SEARCHABLE, (Value)ValueBoolean.FALSE,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1188 */             (Value)ValueBoolean.get((dataType.type == 13)), (Value)ValueBoolean.FALSE, value, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1194 */             (Value)ValueSmallint.get(MathUtils.convertIntToShort(dataType.minScale)), 
/*      */             
/* 1196 */             (Value)ValueSmallint.get(MathUtils.convertIntToShort(dataType.maxScale)), (Value)ValueNull.INSTANCE, (Value)ValueNull.INSTANCE, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1202 */             getRadix(dataType.type, false) });
/*      */     } 
/*      */     
/* 1205 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1 }));
/* 1206 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */   
/*      */   private static Value getRadix(int paramInt, boolean paramBoolean) {
/* 1210 */     if (DataType.isNumericType(paramInt)) {
/* 1211 */       byte b = (paramInt == 13 || paramInt == 16) ? 10 : 2;
/* 1212 */       return paramBoolean ? (Value)ValueSmallint.get((short)b) : (Value)ValueInteger.get(b);
/*      */     } 
/* 1214 */     return (Value)ValueNull.INSTANCE;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2) {
/* 1220 */     if (paramString3 == null) {
/* 1221 */       throw DbException.getInvalidValueException("table", null);
/*      */     }
/* 1223 */     checkClosed();
/* 1224 */     SimpleResult simpleResult = new SimpleResult();
/* 1225 */     simpleResult.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
/* 1226 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 1227 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/* 1228 */     simpleResult.addColumn("NON_UNIQUE", TypeInfo.TYPE_BOOLEAN);
/* 1229 */     simpleResult.addColumn("INDEX_QUALIFIER", TypeInfo.TYPE_VARCHAR);
/* 1230 */     simpleResult.addColumn("INDEX_NAME", TypeInfo.TYPE_VARCHAR);
/* 1231 */     simpleResult.addColumn("TYPE", TypeInfo.TYPE_SMALLINT);
/* 1232 */     simpleResult.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT);
/* 1233 */     simpleResult.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
/* 1234 */     simpleResult.addColumn("ASC_OR_DESC", TypeInfo.TYPE_VARCHAR);
/* 1235 */     simpleResult.addColumn("CARDINALITY", TypeInfo.TYPE_BIGINT);
/* 1236 */     simpleResult.addColumn("PAGES", TypeInfo.TYPE_BIGINT);
/* 1237 */     simpleResult.addColumn("FILTER_CONDITION", TypeInfo.TYPE_VARCHAR);
/* 1238 */     if (!checkCatalogName(paramString1)) {
/* 1239 */       return (ResultInterface)simpleResult;
/*      */     }
/* 1241 */     Database database = this.session.getDatabase();
/* 1242 */     Value value = getString(database.getShortName());
/* 1243 */     for (Schema schema : getSchemas(paramString2)) {
/* 1244 */       Table table = schema.findTableOrView(this.session, paramString3);
/* 1245 */       if (table == null || table.isHidden()) {
/*      */         continue;
/*      */       }
/* 1248 */       getIndexInfo(value, getString(schema.getName()), table, paramBoolean1, paramBoolean2, simpleResult, database);
/*      */     } 
/*      */     
/* 1251 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 3, 6, 5, 7 }));
/* 1252 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void getIndexInfo(Value paramValue1, Value paramValue2, Table paramTable, boolean paramBoolean1, boolean paramBoolean2, SimpleResult paramSimpleResult, Database paramDatabase) {
/* 1257 */     ArrayList arrayList = paramTable.getIndexes();
/* 1258 */     if (arrayList != null) {
/* 1259 */       for (Index index : arrayList) {
/* 1260 */         if (index.getCreateSQL() == null) {
/*      */           continue;
/*      */         }
/* 1263 */         int i = index.getUniqueColumnCount();
/* 1264 */         if (paramBoolean1 && i == 0) {
/*      */           continue;
/*      */         }
/* 1267 */         Value value1 = getString(paramTable.getName());
/* 1268 */         Value value2 = getString(index.getName());
/* 1269 */         IndexColumn[] arrayOfIndexColumn = index.getIndexColumns();
/* 1270 */         ValueSmallint valueSmallint = index.getIndexType().isHash() ? TABLE_INDEX_HASHED : TABLE_INDEX_OTHER; byte b; int j;
/* 1271 */         for (b = 0, j = arrayOfIndexColumn.length; b < j; b++) {
/* 1272 */           IndexColumn indexColumn = arrayOfIndexColumn[b];
/* 1273 */           boolean bool = (b >= i) ? true : false;
/* 1274 */           if (paramBoolean1 && bool) {
/*      */             break;
/*      */           }
/* 1277 */           paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, value1, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1285 */                 (Value)ValueBoolean.get(bool), paramValue1, value2, (Value)valueSmallint, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1293 */                 (Value)ValueSmallint.get((short)(b + 1)), 
/*      */                 
/* 1295 */                 getString(indexColumn.column.getName()), 
/*      */                 
/* 1297 */                 getString(((indexColumn.sortType & 0x1) != 0) ? "D" : "A"),
/*      */                 
/* 1299 */                 (Value)ValueBigint.get(paramBoolean2 ? index
/* 1300 */                   .getRowCountApproximation(this.session) : index
/* 1301 */                   .getRowCount(this.session)), 
/*      */                 
/* 1303 */                 (Value)ValueBigint.get(index.getDiskSpaceUsed() / paramDatabase.getPageSize()), (Value)ValueNull.INSTANCE });
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getSchemas(String paramString1, String paramString2) {
/* 1313 */     checkClosed();
/* 1314 */     SimpleResult simpleResult = new SimpleResult();
/* 1315 */     simpleResult.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
/* 1316 */     simpleResult.addColumn("TABLE_CATALOG", TypeInfo.TYPE_VARCHAR);
/* 1317 */     if (!checkCatalogName(paramString1)) {
/* 1318 */       return (ResultInterface)simpleResult;
/*      */     }
/* 1320 */     CompareLike compareLike = getLike(paramString2);
/* 1321 */     Collection collection = this.session.getDatabase().getAllSchemas();
/* 1322 */     Value value = getString(this.session.getDatabase().getShortName());
/* 1323 */     if (compareLike == null) {
/* 1324 */       for (Schema schema : collection) {
/* 1325 */         simpleResult.addRow(new Value[] { getString(schema.getName()), value });
/*      */       } 
/*      */     } else {
/* 1328 */       for (Schema schema : collection) {
/* 1329 */         String str = schema.getName();
/* 1330 */         if (compareLike.test(str)) {
/* 1331 */           simpleResult.addRow(new Value[] { getString(schema.getName()), value });
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1336 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 0 }));
/* 1337 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ResultInterface getPseudoColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 1343 */     SimpleResult simpleResult = getPseudoColumnsResult();
/* 1344 */     if (!checkCatalogName(paramString1)) {
/* 1345 */       return (ResultInterface)simpleResult;
/*      */     }
/* 1347 */     Database database = this.session.getDatabase();
/* 1348 */     Value value = getString(database.getShortName());
/* 1349 */     CompareLike compareLike = getLike(paramString4);
/* 1350 */     for (Schema schema : getSchemasForPattern(paramString2)) {
/* 1351 */       Value value1 = getString(schema.getName());
/* 1352 */       for (SchemaObject schemaObject : getTablesForPattern(schema, paramString3)) {
/* 1353 */         Value value2 = getString(schemaObject.getName());
/* 1354 */         if (schemaObject instanceof Table) {
/* 1355 */           Table table1 = (Table)schemaObject;
/* 1356 */           if (!table1.isHidden())
/* 1357 */             getPseudoColumnsAdd(simpleResult, value, value1, value2, table1, compareLike); 
/*      */           continue;
/*      */         } 
/* 1360 */         TableSynonym tableSynonym = (TableSynonym)schemaObject;
/* 1361 */         Table table = tableSynonym.getSynonymFor();
/* 1362 */         getPseudoColumnsAdd(simpleResult, value, value1, value2, table, compareLike);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1367 */     simpleResult.sortRows((Comparator)new SortOrder(this.session, new int[] { 1, 2, 3 }));
/* 1368 */     return (ResultInterface)simpleResult;
/*      */   }
/*      */ 
/*      */   
/*      */   private void getPseudoColumnsAdd(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, Table paramTable, CompareLike paramCompareLike) {
/* 1373 */     Column column = paramTable.getRowIdColumn();
/* 1374 */     if (column != null) {
/* 1375 */       getPseudoColumnsAdd(paramSimpleResult, paramValue1, paramValue2, paramValue3, paramCompareLike, column);
/*      */     }
/* 1377 */     for (Column column1 : paramTable.getColumns()) {
/* 1378 */       if (!column1.getVisible()) {
/* 1379 */         getPseudoColumnsAdd(paramSimpleResult, paramValue1, paramValue2, paramValue3, paramCompareLike, column1);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void getPseudoColumnsAdd(SimpleResult paramSimpleResult, Value paramValue1, Value paramValue2, Value paramValue3, CompareLike paramCompareLike, Column paramColumn) {
/* 1386 */     String str = paramColumn.getName();
/* 1387 */     if (paramCompareLike != null && !paramCompareLike.test(str)) {
/*      */       return;
/*      */     }
/* 1390 */     TypeInfo typeInfo = paramColumn.getType();
/* 1391 */     ValueInteger valueInteger = ValueInteger.get(MathUtils.convertLongToInt(typeInfo.getPrecision()));
/* 1392 */     paramSimpleResult.addRow(new Value[] { paramValue1, paramValue2, paramValue3, 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1400 */           getString(str), 
/*      */           
/* 1402 */           (Value)ValueInteger.get(DataType.convertTypeToSQLType(typeInfo)), (Value)valueInteger, 
/*      */ 
/*      */ 
/*      */           
/* 1406 */           (Value)ValueInteger.get(typeInfo.getScale()), 
/*      */           
/* 1408 */           getRadix(typeInfo.getValueType(), false), NO_USAGE_RESTRICTIONS, 
/*      */ 
/*      */ 
/*      */           
/* 1412 */           getString(paramColumn.getComment()), (Value)valueInteger, 
/*      */ 
/*      */ 
/*      */           
/* 1416 */           paramColumn.isNullable() ? YES : NO });
/*      */   }
/*      */ 
/*      */   
/*      */   void checkClosed() {
/* 1421 */     if (this.session.isClosed()) {
/* 1422 */       throw DbException.get(90121);
/*      */     }
/*      */   }
/*      */   
/*      */   Value getString(String paramString) {
/* 1427 */     return (paramString != null) ? ValueVarchar.get(paramString, (CastDataProvider)this.session) : (Value)ValueNull.INSTANCE;
/*      */   }
/*      */   
/*      */   private boolean checkCatalogName(String paramString) {
/* 1431 */     if (paramString != null && !paramString.isEmpty()) {
/* 1432 */       Database database = this.session.getDatabase();
/* 1433 */       return database.equalsIdentifiers(paramString, database.getShortName());
/*      */     } 
/* 1435 */     return true;
/*      */   }
/*      */   
/*      */   private Collection<Schema> getSchemas(String paramString) {
/* 1439 */     Database database = this.session.getDatabase();
/* 1440 */     if (paramString == null)
/* 1441 */       return database.getAllSchemas(); 
/* 1442 */     if (paramString.isEmpty()) {
/* 1443 */       return Collections.singleton(database.getMainSchema());
/*      */     }
/* 1445 */     Schema schema = database.findSchema(paramString);
/* 1446 */     if (schema != null) {
/* 1447 */       return Collections.singleton(schema);
/*      */     }
/* 1449 */     return Collections.emptySet();
/*      */   }
/*      */ 
/*      */   
/*      */   private Collection<Schema> getSchemasForPattern(String paramString) {
/* 1454 */     Database database = this.session.getDatabase();
/* 1455 */     if (paramString == null)
/* 1456 */       return database.getAllSchemas(); 
/* 1457 */     if (paramString.isEmpty()) {
/* 1458 */       return Collections.singleton(database.getMainSchema());
/*      */     }
/* 1460 */     ArrayList<Schema> arrayList = Utils.newSmallArrayList();
/* 1461 */     CompareLike compareLike = getLike(paramString);
/* 1462 */     for (Schema schema : database.getAllSchemas()) {
/* 1463 */       if (compareLike.test(schema.getName())) {
/* 1464 */         arrayList.add(schema);
/*      */       }
/*      */     } 
/* 1467 */     return arrayList;
/*      */   }
/*      */ 
/*      */   
/*      */   private Collection<? extends SchemaObject> getTablesForPattern(Schema paramSchema, String paramString) {
/* 1472 */     Collection<? extends SchemaObject> collection1 = paramSchema.getAllTablesAndViews(this.session);
/* 1473 */     Collection<? extends SchemaObject> collection2 = paramSchema.getAllSynonyms();
/* 1474 */     if (paramString == null) {
/* 1475 */       if (collection1.isEmpty())
/* 1476 */         return collection2; 
/* 1477 */       if (collection2.isEmpty()) {
/* 1478 */         return collection1;
/*      */       }
/* 1480 */       ArrayList<SchemaObject> arrayList1 = new ArrayList(collection1.size() + collection2.size());
/* 1481 */       arrayList1.addAll(collection1);
/* 1482 */       arrayList1.addAll(collection2);
/* 1483 */       return arrayList1;
/* 1484 */     }  if (collection1.isEmpty() && collection2.isEmpty()) {
/* 1485 */       return Collections.emptySet();
/*      */     }
/* 1487 */     ArrayList<Table> arrayList = Utils.newSmallArrayList();
/* 1488 */     CompareLike compareLike = getLike(paramString);
/* 1489 */     for (Table table : collection1) {
/* 1490 */       if (compareLike.test(table.getName())) {
/* 1491 */         arrayList.add(table);
/*      */       }
/*      */     } 
/* 1494 */     for (TableSynonym tableSynonym : collection2) {
/* 1495 */       if (compareLike.test(tableSynonym.getName())) {
/* 1496 */         arrayList.add(tableSynonym);
/*      */       }
/*      */     } 
/* 1499 */     return (Collection)arrayList;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean checkSchema(String paramString, Schema paramSchema) {
/* 1504 */     if (paramString == null)
/* 1505 */       return true; 
/* 1506 */     if (paramString.isEmpty()) {
/* 1507 */       return (paramSchema == this.session.getDatabase().getMainSchema());
/*      */     }
/* 1509 */     return this.session.getDatabase().equalsIdentifiers(paramString, paramSchema.getName());
/*      */   }
/*      */ 
/*      */   
/*      */   private CompareLike getLike(String paramString) {
/* 1514 */     if (paramString == null) {
/* 1515 */       return null;
/*      */     }
/* 1517 */     CompareLike compareLike = new CompareLike(this.session.getDatabase().getCompareMode(), "\\", null, false, false, null, null, CompareLike.LikeType.LIKE);
/*      */     
/* 1519 */     compareLike.initPattern(paramString, Character.valueOf('\\'));
/* 1520 */     return compareLike;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\meta\DatabaseMetaLocal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */