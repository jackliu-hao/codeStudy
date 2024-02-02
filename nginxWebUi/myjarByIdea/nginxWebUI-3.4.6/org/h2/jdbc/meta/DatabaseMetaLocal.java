package org.h2.jdbc.meta;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.command.dml.Help;
import org.h2.constraint.Constraint;
import org.h2.constraint.ConstraintActionType;
import org.h2.constraint.ConstraintReferential;
import org.h2.constraint.ConstraintUnique;
import org.h2.engine.Database;
import org.h2.engine.DbObject;
import org.h2.engine.Mode;
import org.h2.engine.Right;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.condition.CompareLike;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.result.SortOrder;
import org.h2.schema.FunctionAlias;
import org.h2.schema.Schema;
import org.h2.schema.SchemaObject;
import org.h2.schema.UserDefinedFunction;
import org.h2.table.Column;
import org.h2.table.IndexColumn;
import org.h2.table.Table;
import org.h2.table.TableSynonym;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.DataType;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueToObjectConverter2;
import org.h2.value.ValueVarchar;

public final class DatabaseMetaLocal extends DatabaseMetaLocalBase {
   private static final Value YES = ValueVarchar.get("YES");
   private static final Value NO = ValueVarchar.get("NO");
   private static final ValueSmallint BEST_ROW_SESSION = ValueSmallint.get((short)2);
   private static final ValueSmallint BEST_ROW_NOT_PSEUDO = ValueSmallint.get((short)1);
   private static final ValueInteger COLUMN_NO_NULLS = ValueInteger.get(0);
   private static final ValueSmallint COLUMN_NO_NULLS_SMALL = ValueSmallint.get((short)0);
   private static final ValueInteger COLUMN_NULLABLE = ValueInteger.get(1);
   private static final ValueSmallint COLUMN_NULLABLE_UNKNOWN_SMALL = ValueSmallint.get((short)2);
   private static final ValueSmallint IMPORTED_KEY_CASCADE = ValueSmallint.get((short)0);
   private static final ValueSmallint IMPORTED_KEY_RESTRICT = ValueSmallint.get((short)1);
   private static final ValueSmallint IMPORTED_KEY_DEFAULT = ValueSmallint.get((short)4);
   private static final ValueSmallint IMPORTED_KEY_SET_NULL = ValueSmallint.get((short)2);
   private static final ValueSmallint IMPORTED_KEY_NOT_DEFERRABLE = ValueSmallint.get((short)7);
   private static final ValueSmallint PROCEDURE_COLUMN_IN = ValueSmallint.get((short)1);
   private static final ValueSmallint PROCEDURE_COLUMN_RETURN = ValueSmallint.get((short)5);
   private static final ValueSmallint PROCEDURE_NO_RESULT = ValueSmallint.get((short)1);
   private static final ValueSmallint PROCEDURE_RETURNS_RESULT = ValueSmallint.get((short)2);
   private static final ValueSmallint TABLE_INDEX_HASHED = ValueSmallint.get((short)2);
   private static final ValueSmallint TABLE_INDEX_OTHER = ValueSmallint.get((short)3);
   private static final String[] TABLE_TYPES = new String[]{"BASE TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "SYNONYM", "VIEW"};
   private static final ValueSmallint TYPE_NULLABLE = ValueSmallint.get((short)1);
   private static final ValueSmallint TYPE_SEARCHABLE = ValueSmallint.get((short)3);
   private static final Value NO_USAGE_RESTRICTIONS = ValueVarchar.get("NO_USAGE_RESTRICTIONS");
   private final SessionLocal session;

   public DatabaseMetaLocal(SessionLocal var1) {
      this.session = var1;
   }

   public final DefaultNullOrdering defaultNullOrdering() {
      return this.session.getDatabase().getDefaultNullOrdering();
   }

   public String getSQLKeywords() {
      StringBuilder var1 = (new StringBuilder(103)).append("CURRENT_CATALOG,CURRENT_SCHEMA,GROUPS,IF,ILIKE,KEY,");
      Mode var2 = this.session.getMode();
      if (var2.limit) {
         var1.append("LIMIT,");
      }

      if (var2.minusIsExcept) {
         var1.append("MINUS,");
      }

      var1.append("OFFSET,QUALIFY,REGEXP,ROWNUM,");
      if (var2.topInSelect || var2.topInDML) {
         var1.append("TOP,");
      }

      return var1.append("_ROWID_").toString();
   }

   public String getNumericFunctions() {
      return this.getFunctions("Functions (Numeric)");
   }

   public String getStringFunctions() {
      return this.getFunctions("Functions (String)");
   }

   public String getSystemFunctions() {
      return this.getFunctions("Functions (System)");
   }

   public String getTimeDateFunctions() {
      return this.getFunctions("Functions (Time and Date)");
   }

   private String getFunctions(String var1) {
      this.checkClosed();
      StringBuilder var2 = new StringBuilder();

      try {
         ResultSet var3 = Help.getTable();

         while(var3.next()) {
            if (var3.getString(1).trim().equals(var1)) {
               if (var2.length() != 0) {
                  var2.append(',');
               }

               String var4 = var3.getString(2).trim();
               int var5 = var4.indexOf(32);
               if (var5 >= 0) {
                  StringUtils.trimSubstring(var2, var4, 0, var5);
               } else {
                  var2.append(var4);
               }
            }
         }
      } catch (Exception var6) {
         throw DbException.convert(var6);
      }

      return var2.toString();
   }

   public String getSearchStringEscape() {
      return this.session.getDatabase().getSettings().defaultEscape;
   }

   public ResultInterface getProcedures(String var1, String var2, String var3) {
      this.checkClosed();
      SimpleResult var4 = new SimpleResult();
      var4.addColumn("PROCEDURE_CAT", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("PROCEDURE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("PROCEDURE_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("RESERVED1", TypeInfo.TYPE_NULL);
      var4.addColumn("RESERVED2", TypeInfo.TYPE_NULL);
      var4.addColumn("RESERVED3", TypeInfo.TYPE_NULL);
      var4.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("PROCEDURE_TYPE", TypeInfo.TYPE_SMALLINT);
      var4.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
      if (!this.checkCatalogName(var1)) {
         return var4;
      } else {
         Database var5 = this.session.getDatabase();
         Value var6 = this.getString(var5.getShortName());
         CompareLike var7 = this.getLike(var3);
         Iterator var8 = this.getSchemasForPattern(var2).iterator();

         label65:
         while(var8.hasNext()) {
            Schema var9 = (Schema)var8.next();
            Value var10 = this.getString(var9.getName());
            Iterator var11 = var9.getAllFunctionsAndAggregates().iterator();

            while(true) {
               UserDefinedFunction var12;
               String var13;
               Value var14;
               FunctionAlias.JavaMethod[] var15;
               label51:
               while(true) {
                  while(true) {
                     do {
                        if (!var11.hasNext()) {
                           continue label65;
                        }

                        var12 = (UserDefinedFunction)var11.next();
                        var13 = var12.getName();
                     } while(var7 != null && !var7.test(var13));

                     var14 = this.getString(var13);
                     if (var12 instanceof FunctionAlias) {
                        try {
                           var15 = ((FunctionAlias)var12).getJavaMethods();
                           break label51;
                        } catch (DbException var19) {
                        }
                     } else {
                        this.getProceduresAdd(var4, var6, var10, var14, var12.getComment(), PROCEDURE_RETURNS_RESULT, var14);
                     }
                  }
               }

               for(int var16 = 0; var16 < var15.length; ++var16) {
                  FunctionAlias.JavaMethod var17 = var15[var16];
                  TypeInfo var18 = var17.getDataType();
                  this.getProceduresAdd(var4, var6, var10, var14, var12.getComment(), var18 != null && var18.getValueType() == 0 ? PROCEDURE_NO_RESULT : PROCEDURE_RETURNS_RESULT, this.getString(var13 + '_' + (var16 + 1)));
               }
            }
         }

         var4.sortRows(new SortOrder(this.session, new int[]{1, 2, 8}));
         return var4;
      }
   }

   private void getProceduresAdd(SimpleResult var1, Value var2, Value var3, Value var4, String var5, ValueSmallint var6, Value var7) {
      var1.addRow(var2, var3, var4, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, this.getString(var5), var6, var7);
   }

   public ResultInterface getProcedureColumns(String var1, String var2, String var3, String var4) {
      this.checkClosed();
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("PROCEDURE_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("PROCEDURE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("PROCEDURE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_TYPE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
      var5.addColumn("LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("SCALE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("RADIX", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_DEF", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
      var5.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
      var5.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SPECIFIC_NAME", TypeInfo.TYPE_VARCHAR);
      if (!this.checkCatalogName(var1)) {
         return var5;
      } else {
         Database var6 = this.session.getDatabase();
         Value var7 = this.getString(var6.getShortName());
         CompareLike var8 = this.getLike(var3);
         Iterator var9 = this.getSchemasForPattern(var2).iterator();

         label76:
         while(var9.hasNext()) {
            Schema var10 = (Schema)var9.next();
            Value var11 = this.getString(var10.getName());
            Iterator var12 = var10.getAllFunctionsAndAggregates().iterator();

            while(true) {
               String var14;
               Value var15;
               FunctionAlias.JavaMethod[] var16;
               while(true) {
                  UserDefinedFunction var13;
                  do {
                     do {
                        if (!var12.hasNext()) {
                           continue label76;
                        }

                        var13 = (UserDefinedFunction)var12.next();
                     } while(!(var13 instanceof FunctionAlias));

                     var14 = var13.getName();
                  } while(var8 != null && !var8.test(var14));

                  var15 = this.getString(var14);

                  try {
                     var16 = ((FunctionAlias)var13).getJavaMethods();
                     break;
                  } catch (DbException var27) {
                  }
               }

               int var17 = 0;

               for(int var18 = var16.length; var17 < var18; ++var17) {
                  FunctionAlias.JavaMethod var19 = var16[var17];
                  Value var20 = this.getString(var14 + '_' + (var17 + 1));
                  TypeInfo var21 = var19.getDataType();
                  if (var21 != null && var21.getValueType() != 0) {
                     this.getProcedureColumnAdd(var5, var7, var11, var15, var20, var21, var19.getClass().isPrimitive(), 0);
                  }

                  Class[] var22 = var19.getColumnClasses();
                  int var23 = 1;
                  int var24 = var19.hasConnectionParam() ? 1 : 0;

                  for(int var25 = var22.length; var24 < var25; ++var24) {
                     Class var26 = var22[var24];
                     this.getProcedureColumnAdd(var5, var7, var11, var15, var20, ValueToObjectConverter2.classToType(var26), var26.isPrimitive(), var23);
                     ++var23;
                  }
               }
            }
         }

         var5.sortRows(new SortOrder(this.session, new int[]{1, 2, 19}));
         return var5;
      }
   }

   private void getProcedureColumnAdd(SimpleResult var1, Value var2, Value var3, Value var4, Value var5, TypeInfo var6, boolean var7, int var8) {
      int var9 = var6.getValueType();
      DataType var10 = DataType.getDataType(var9);
      ValueInteger var11 = ValueInteger.get(MathUtils.convertLongToInt(var6.getPrecision()));
      var1.addRow(var2, var3, var4, this.getString(var8 == 0 ? "RESULT" : "P" + var8), var8 == 0 ? PROCEDURE_COLUMN_RETURN : PROCEDURE_COLUMN_IN, ValueInteger.get(DataType.convertTypeToSQLType(var6)), this.getDataTypeName(var6), var11, var11, (Value)(var10.supportsScale ? ValueSmallint.get(MathUtils.convertIntToShort(var10.defaultScale)) : ValueNull.INSTANCE), getRadix(var9, true), var7 ? COLUMN_NO_NULLS_SMALL : COLUMN_NULLABLE_UNKNOWN_SMALL, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, (Value)(!DataType.isBinaryStringType(var9) && !DataType.isCharacterStringType(var9) ? ValueNull.INSTANCE : var11), ValueInteger.get(var8), ValueVarchar.EMPTY, var5);
   }

   public ResultInterface getTables(String var1, String var2, String var3, String[] var4) {
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TABLE_TYPE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SELF_REFERENCING_COL_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("REF_GENERATION", TypeInfo.TYPE_VARCHAR);
      if (!this.checkCatalogName(var1)) {
         return var5;
      } else {
         Database var6 = this.session.getDatabase();
         Value var7 = this.getString(var6.getShortName());
         HashSet var8;
         if (var4 != null) {
            var8 = new HashSet(8);
            String[] var9 = var4;
            int var10 = var4.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               String var12 = var9[var11];
               int var13 = Arrays.binarySearch(TABLE_TYPES, var12);
               if (var13 >= 0) {
                  var8.add(TABLE_TYPES[var13]);
               } else if (var12.equals("TABLE")) {
                  var8.add("BASE TABLE");
               }
            }

            if (var8.isEmpty()) {
               return var5;
            }
         } else {
            var8 = null;
         }

         Iterator var16 = this.getSchemasForPattern(var2).iterator();

         while(var16.hasNext()) {
            Schema var17 = (Schema)var16.next();
            Value var18 = this.getString(var17.getName());
            Iterator var19 = this.getTablesForPattern(var17, var3).iterator();

            while(var19.hasNext()) {
               SchemaObject var20 = (SchemaObject)var19.next();
               Value var14 = this.getString(var20.getName());
               if (var20 instanceof Table) {
                  Table var15 = (Table)var20;
                  if (!var15.isHidden()) {
                     this.getTablesAdd(var5, var7, var18, var14, var15, false, var8);
                  }
               } else {
                  this.getTablesAdd(var5, var7, var18, var14, ((TableSynonym)var20).getSynonymFor(), true, var8);
               }
            }
         }

         var5.sortRows(new SortOrder(this.session, new int[]{3, 1, 2}));
         return var5;
      }
   }

   private void getTablesAdd(SimpleResult var1, Value var2, Value var3, Value var4, Table var5, boolean var6, HashSet<String> var7) {
      String var8 = var6 ? "SYNONYM" : var5.getSQLTableType();
      if (var7 == null || var7.contains(var8)) {
         var1.addRow(var2, var3, var4, this.getString(var8), this.getString(var5.getComment()), ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE);
      }
   }

   public ResultInterface getSchemas() {
      return this.getSchemas((String)null, (String)null);
   }

   public ResultInterface getCatalogs() {
      this.checkClosed();
      SimpleResult var1 = new SimpleResult();
      var1.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var1.addRow(this.getString(this.session.getDatabase().getShortName()));
      return var1;
   }

   public ResultInterface getTableTypes() {
      SimpleResult var1 = new SimpleResult();
      var1.addColumn("TABLE_TYPE", TypeInfo.TYPE_VARCHAR);
      var1.addRow(this.getString("BASE TABLE"));
      var1.addRow(this.getString("GLOBAL TEMPORARY"));
      var1.addRow(this.getString("LOCAL TEMPORARY"));
      var1.addRow(this.getString("SYNONYM"));
      var1.addRow(this.getString("VIEW"));
      return var1;
   }

   public ResultInterface getColumns(String var1, String var2, String var3, String var4) {
      SimpleResult var5 = new SimpleResult();
      var5.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_INTEGER);
      var5.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
      var5.addColumn("NULLABLE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("REMARKS", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("COLUMN_DEF", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var5.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
      var5.addColumn("CHAR_OCTET_LENGTH", TypeInfo.TYPE_INTEGER);
      var5.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_INTEGER);
      var5.addColumn("IS_NULLABLE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SCOPE_CATALOG", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SCOPE_SCHEMA", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SCOPE_TABLE", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("SOURCE_DATA_TYPE", TypeInfo.TYPE_SMALLINT);
      var5.addColumn("IS_AUTOINCREMENT", TypeInfo.TYPE_VARCHAR);
      var5.addColumn("IS_GENERATEDCOLUMN", TypeInfo.TYPE_VARCHAR);
      if (!this.checkCatalogName(var1)) {
         return var5;
      } else {
         Database var6 = this.session.getDatabase();
         Value var7 = this.getString(var6.getShortName());
         CompareLike var8 = this.getLike(var4);
         Iterator var9 = this.getSchemasForPattern(var2).iterator();

         while(var9.hasNext()) {
            Schema var10 = (Schema)var9.next();
            Value var11 = this.getString(var10.getName());
            Iterator var12 = this.getTablesForPattern(var10, var3).iterator();

            while(var12.hasNext()) {
               SchemaObject var13 = (SchemaObject)var12.next();
               Value var14 = this.getString(var13.getName());
               if (var13 instanceof Table) {
                  Table var15 = (Table)var13;
                  if (!var15.isHidden()) {
                     this.getColumnsAdd(var5, var7, var11, var14, var15, var8);
                  }
               } else {
                  TableSynonym var17 = (TableSynonym)var13;
                  Table var16 = var17.getSynonymFor();
                  this.getColumnsAdd(var5, var7, var11, var14, var16, var8);
               }
            }
         }

         var5.sortRows(new SortOrder(this.session, new int[]{1, 2, 16}));
         return var5;
      }
   }

   private void getColumnsAdd(SimpleResult var1, Value var2, Value var3, Value var4, Table var5, CompareLike var6) {
      int var7 = 0;
      Column[] var8 = var5.getColumns();
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Column var11 = var8[var10];
         if (var11.getVisible()) {
            ++var7;
            String var12 = var11.getName();
            if (var6 == null || var6.test(var12)) {
               TypeInfo var13 = var11.getType();
               ValueInteger var14 = ValueInteger.get(MathUtils.convertLongToInt(var13.getPrecision()));
               boolean var15 = var11.isNullable();
               boolean var16 = var11.isGenerated();
               var1.addRow(var2, var3, var4, this.getString(var12), ValueInteger.get(DataType.convertTypeToSQLType(var13)), this.getDataTypeName(var13), var14, ValueNull.INSTANCE, ValueInteger.get(var13.getScale()), getRadix(var13.getValueType(), false), var15 ? COLUMN_NULLABLE : COLUMN_NO_NULLS, this.getString(var11.getComment()), (Value)(var16 ? ValueNull.INSTANCE : this.getString(var11.getDefaultSQL())), ValueNull.INSTANCE, ValueNull.INSTANCE, var14, ValueInteger.get(var7), var15 ? YES : NO, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, ValueNull.INSTANCE, var11.isIdentity() ? YES : NO, var16 ? YES : NO);
            }
         }
      }

   }

   public ResultInterface getColumnPrivileges(String var1, String var2, String var3, String var4) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("table", (Object)null);
      } else {
         this.checkClosed();
         SimpleResult var5 = new SimpleResult();
         var5.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("GRANTOR", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("GRANTEE", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("PRIVILEGE", TypeInfo.TYPE_VARCHAR);
         var5.addColumn("IS_GRANTABLE", TypeInfo.TYPE_VARCHAR);
         if (!this.checkCatalogName(var1)) {
            return var5;
         } else {
            Database var6 = this.session.getDatabase();
            Value var7 = this.getString(var6.getShortName());
            CompareLike var8 = this.getLike(var4);
            Iterator var9 = var6.getAllRights().iterator();

            while(var9.hasNext()) {
               Right var10 = (Right)var9.next();
               DbObject var11 = var10.getGrantedObject();
               if (var11 instanceof Table) {
                  Table var12 = (Table)var11;
                  if (!var12.isHidden()) {
                     String var13 = var12.getName();
                     if (var6.equalsIdentifiers(var3, var13)) {
                        Schema var14 = var12.getSchema();
                        if (this.checkSchema(var2, var14)) {
                           this.addPrivileges(var5, var7, var14.getName(), var13, var10.getGrantee(), var10.getRightMask(), var8, var12.getColumns());
                        }
                     }
                  }
               }
            }

            var5.sortRows(new SortOrder(this.session, new int[]{3, 6}));
            return var5;
         }
      }
   }

   public ResultInterface getTablePrivileges(String var1, String var2, String var3) {
      this.checkClosed();
      SimpleResult var4 = new SimpleResult();
      var4.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("GRANTOR", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("GRANTEE", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("PRIVILEGE", TypeInfo.TYPE_VARCHAR);
      var4.addColumn("IS_GRANTABLE", TypeInfo.TYPE_VARCHAR);
      if (!this.checkCatalogName(var1)) {
         return var4;
      } else {
         Database var5 = this.session.getDatabase();
         Value var6 = this.getString(var5.getShortName());
         CompareLike var7 = this.getLike(var2);
         CompareLike var8 = this.getLike(var3);
         Iterator var9 = var5.getAllRights().iterator();

         while(true) {
            Right var10;
            String var13;
            String var15;
            while(true) {
               Table var12;
               do {
                  do {
                     DbObject var11;
                     do {
                        if (!var9.hasNext()) {
                           var4.sortRows(new SortOrder(this.session, new int[]{1, 2, 5}));
                           return var4;
                        }

                        var10 = (Right)var9.next();
                        var11 = var10.getGrantedObject();
                     } while(!(var11 instanceof Table));

                     var12 = (Table)var11;
                  } while(var12.isHidden());

                  var13 = var12.getName();
               } while(var8 != null && !var8.test(var13));

               Schema var14 = var12.getSchema();
               var15 = var14.getName();
               if (var2 == null) {
                  break;
               }

               if (var2.isEmpty()) {
                  if (var14 != var5.getMainSchema()) {
                     continue;
                  }
               } else if (!var7.test(var15)) {
                  continue;
               }
               break;
            }

            this.addPrivileges(var4, var6, var15, var13, var10.getGrantee(), var10.getRightMask(), (CompareLike)null, (Column[])null);
         }
      }
   }

   private void addPrivileges(SimpleResult var1, Value var2, String var3, String var4, DbObject var5, int var6, CompareLike var7, Column[] var8) {
      Value var9 = this.getString(var3);
      Value var10 = this.getString(var4);
      Value var11 = this.getString(var5.getName());
      boolean var12 = var5.getType() == 2 && ((User)var5).isAdmin();
      if ((var6 & 1) != 0) {
         this.addPrivilege(var1, var2, var9, var10, var11, "SELECT", var12, var7, var8);
      }

      if ((var6 & 4) != 0) {
         this.addPrivilege(var1, var2, var9, var10, var11, "INSERT", var12, var7, var8);
      }

      if ((var6 & 8) != 0) {
         this.addPrivilege(var1, var2, var9, var10, var11, "UPDATE", var12, var7, var8);
      }

      if ((var6 & 2) != 0) {
         this.addPrivilege(var1, var2, var9, var10, var11, "DELETE", var12, var7, var8);
      }

   }

   private void addPrivilege(SimpleResult var1, Value var2, Value var3, Value var4, Value var5, String var6, boolean var7, CompareLike var8, Column[] var9) {
      if (var9 == null) {
         var1.addRow(var2, var3, var4, ValueNull.INSTANCE, var5, this.getString(var6), var7 ? YES : NO);
      } else {
         Column[] var10 = var9;
         int var11 = var9.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            Column var13 = var10[var12];
            String var14 = var13.getName();
            if (var8 == null || var8.test(var14)) {
               var1.addRow(var2, var3, var4, this.getString(var14), ValueNull.INSTANCE, var5, this.getString(var6), var7 ? YES : NO);
            }
         }
      }

   }

   public ResultInterface getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("table", (Object)null);
      } else {
         this.checkClosed();
         SimpleResult var6 = new SimpleResult();
         var6.addColumn("SCOPE", TypeInfo.TYPE_SMALLINT);
         var6.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
         var6.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("COLUMN_SIZE", TypeInfo.TYPE_INTEGER);
         var6.addColumn("BUFFER_LENGTH", TypeInfo.TYPE_INTEGER);
         var6.addColumn("DECIMAL_DIGITS", TypeInfo.TYPE_SMALLINT);
         var6.addColumn("PSEUDO_COLUMN", TypeInfo.TYPE_SMALLINT);
         if (!this.checkCatalogName(var1)) {
            return var6;
         } else {
            Iterator var7 = this.getSchemas(var2).iterator();

            label62:
            while(true) {
               ArrayList var10;
               do {
                  Table var9;
                  do {
                     do {
                        if (!var7.hasNext()) {
                           return var6;
                        }

                        Schema var8 = (Schema)var7.next();
                        var9 = var8.findTableOrView(this.session, var3);
                     } while(var9 == null);
                  } while(var9.isHidden());

                  var10 = var9.getConstraints();
               } while(var10 == null);

               Iterator var11 = var10.iterator();

               while(true) {
                  Constraint var12;
                  do {
                     if (!var11.hasNext()) {
                        continue label62;
                     }

                     var12 = (Constraint)var11.next();
                  } while(var12.getConstraintType() != Constraint.Type.PRIMARY_KEY);

                  IndexColumn[] var13 = ((ConstraintUnique)var12).getColumns();
                  int var14 = 0;

                  for(int var15 = var13.length; var14 < var15; ++var14) {
                     IndexColumn var16 = var13[var14];
                     Column var17 = var16.column;
                     TypeInfo var18 = var17.getType();
                     DataType var19 = DataType.getDataType(var18.getValueType());
                     var6.addRow(BEST_ROW_SESSION, this.getString(var17.getName()), ValueInteger.get(DataType.convertTypeToSQLType(var18)), this.getDataTypeName(var18), ValueInteger.get(MathUtils.convertLongToInt(var18.getPrecision())), ValueNull.INSTANCE, (Value)(var19.supportsScale ? ValueSmallint.get(MathUtils.convertIntToShort(var18.getScale())) : ValueNull.INSTANCE), BEST_ROW_NOT_PSEUDO);
                  }
               }
            }
         }
      }
   }

   private Value getDataTypeName(TypeInfo var1) {
      return this.getString(var1.getDeclaredTypeName());
   }

   public ResultInterface getPrimaryKeys(String var1, String var2, String var3) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("table", (Object)null);
      } else {
         this.checkClosed();
         SimpleResult var4 = new SimpleResult();
         var4.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
         var4.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
         var4.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
         var4.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
         var4.addColumn("KEY_SEQ", TypeInfo.TYPE_SMALLINT);
         var4.addColumn("PK_NAME", TypeInfo.TYPE_VARCHAR);
         if (!this.checkCatalogName(var1)) {
            return var4;
         } else {
            Database var5 = this.session.getDatabase();
            Value var6 = this.getString(var5.getShortName());
            Iterator var7 = this.getSchemas(var2).iterator();

            label57:
            while(true) {
               Schema var8;
               Table var9;
               ArrayList var10;
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           var4.sortRows(new SortOrder(this.session, new int[]{3}));
                           return var4;
                        }

                        var8 = (Schema)var7.next();
                        var9 = var8.findTableOrView(this.session, var3);
                     } while(var9 == null);
                  } while(var9.isHidden());

                  var10 = var9.getConstraints();
               } while(var10 == null);

               Iterator var11 = var10.iterator();

               while(true) {
                  Constraint var12;
                  do {
                     if (!var11.hasNext()) {
                        continue label57;
                     }

                     var12 = (Constraint)var11.next();
                  } while(var12.getConstraintType() != Constraint.Type.PRIMARY_KEY);

                  Value var13 = this.getString(var8.getName());
                  Value var14 = this.getString(var9.getName());
                  Value var15 = this.getString(var12.getName());
                  IndexColumn[] var16 = ((ConstraintUnique)var12).getColumns();
                  int var17 = 0;
                  int var18 = var16.length;

                  while(var17 < var18) {
                     Value[] var10001 = new Value[]{var6, var13, var14, this.getString(var16[var17].column.getName()), null, null};
                     ++var17;
                     var10001[4] = ValueSmallint.get((short)var17);
                     var10001[5] = var15;
                     var4.addRow(var10001);
                  }
               }
            }
         }
      }
   }

   public ResultInterface getImportedKeys(String var1, String var2, String var3) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("table", (Object)null);
      } else {
         SimpleResult var4 = this.initCrossReferenceResult();
         if (!this.checkCatalogName(var1)) {
            return var4;
         } else {
            Database var5 = this.session.getDatabase();
            Value var6 = this.getString(var5.getShortName());
            Iterator var7 = this.getSchemas(var2).iterator();

            while(true) {
               Table var9;
               ArrayList var10;
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           var4.sortRows(new SortOrder(this.session, new int[]{1, 2, 8}));
                           return var4;
                        }

                        Schema var8 = (Schema)var7.next();
                        var9 = var8.findTableOrView(this.session, var3);
                     } while(var9 == null);
                  } while(var9.isHidden());

                  var10 = var9.getConstraints();
               } while(var10 == null);

               Iterator var11 = var10.iterator();

               while(var11.hasNext()) {
                  Constraint var12 = (Constraint)var11.next();
                  if (var12.getConstraintType() == Constraint.Type.REFERENTIAL) {
                     ConstraintReferential var13 = (ConstraintReferential)var12;
                     Table var14 = var13.getTable();
                     if (var14 == var9) {
                        Table var15 = var13.getRefTable();
                        this.addCrossReferenceResult(var4, var6, var15.getSchema().getName(), var15, var14.getSchema().getName(), var14, var13);
                     }
                  }
               }
            }
         }
      }
   }

   public ResultInterface getExportedKeys(String var1, String var2, String var3) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("table", (Object)null);
      } else {
         SimpleResult var4 = this.initCrossReferenceResult();
         if (!this.checkCatalogName(var1)) {
            return var4;
         } else {
            Database var5 = this.session.getDatabase();
            Value var6 = this.getString(var5.getShortName());
            Iterator var7 = this.getSchemas(var2).iterator();

            while(true) {
               Table var9;
               ArrayList var10;
               do {
                  do {
                     do {
                        if (!var7.hasNext()) {
                           var4.sortRows(new SortOrder(this.session, new int[]{5, 6, 8}));
                           return var4;
                        }

                        Schema var8 = (Schema)var7.next();
                        var9 = var8.findTableOrView(this.session, var3);
                     } while(var9 == null);
                  } while(var9.isHidden());

                  var10 = var9.getConstraints();
               } while(var10 == null);

               Iterator var11 = var10.iterator();

               while(var11.hasNext()) {
                  Constraint var12 = (Constraint)var11.next();
                  if (var12.getConstraintType() == Constraint.Type.REFERENTIAL) {
                     ConstraintReferential var13 = (ConstraintReferential)var12;
                     Table var14 = var13.getRefTable();
                     if (var14 == var9) {
                        Table var15 = var13.getTable();
                        this.addCrossReferenceResult(var4, var6, var14.getSchema().getName(), var14, var15.getSchema().getName(), var15, var13);
                     }
                  }
               }
            }
         }
      }
   }

   public ResultInterface getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("primaryTable", (Object)null);
      } else if (var6 == null) {
         throw DbException.getInvalidValueException("foreignTable", (Object)null);
      } else {
         SimpleResult var7 = this.initCrossReferenceResult();
         if (this.checkCatalogName(var1) && this.checkCatalogName(var4)) {
            Database var8 = this.session.getDatabase();
            Value var9 = this.getString(var8.getShortName());
            Iterator var10 = this.getSchemas(var5).iterator();

            while(true) {
               Table var12;
               ArrayList var13;
               do {
                  do {
                     do {
                        if (!var10.hasNext()) {
                           var7.sortRows(new SortOrder(this.session, new int[]{5, 6, 8}));
                           return var7;
                        }

                        Schema var11 = (Schema)var10.next();
                        var12 = var11.findTableOrView(this.session, var6);
                     } while(var12 == null);
                  } while(var12.isHidden());

                  var13 = var12.getConstraints();
               } while(var13 == null);

               Iterator var14 = var13.iterator();

               while(var14.hasNext()) {
                  Constraint var15 = (Constraint)var14.next();
                  if (var15.getConstraintType() == Constraint.Type.REFERENTIAL) {
                     ConstraintReferential var16 = (ConstraintReferential)var15;
                     Table var17 = var16.getTable();
                     if (var17 == var12) {
                        Table var18 = var16.getRefTable();
                        if (var8.equalsIdentifiers(var18.getName(), var3)) {
                           Schema var19 = var18.getSchema();
                           if (this.checkSchema(var2, var19)) {
                              this.addCrossReferenceResult(var7, var9, var19.getName(), var18, var17.getSchema().getName(), var17, var16);
                           }
                        }
                     }
                  }
               }
            }
         } else {
            return var7;
         }
      }
   }

   private SimpleResult initCrossReferenceResult() {
      this.checkClosed();
      SimpleResult var1 = new SimpleResult();
      var1.addColumn("PKTABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("PKTABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("PKTABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("PKCOLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("FKTABLE_CAT", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("FKTABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("FKTABLE_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("FKCOLUMN_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("KEY_SEQ", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("UPDATE_RULE", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("DELETE_RULE", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("FK_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("PK_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("DEFERRABILITY", TypeInfo.TYPE_SMALLINT);
      return var1;
   }

   private void addCrossReferenceResult(SimpleResult var1, Value var2, String var3, Table var4, String var5, Table var6, ConstraintReferential var7) {
      Value var8 = this.getString(var3);
      Value var9 = this.getString(var4.getName());
      Value var10 = this.getString(var5);
      Value var11 = this.getString(var6.getName());
      IndexColumn[] var12 = var7.getRefColumns();
      IndexColumn[] var13 = var7.getColumns();
      ValueSmallint var14 = getRefAction(var7.getUpdateAction());
      ValueSmallint var15 = getRefAction(var7.getDeleteAction());
      Value var16 = this.getString(var7.getName());
      Value var17 = this.getString(var7.getReferencedConstraint().getName());
      int var18 = 0;

      for(int var19 = var13.length; var18 < var19; ++var18) {
         var1.addRow(var2, var8, var9, this.getString(var12[var18].column.getName()), var2, var10, var11, this.getString(var13[var18].column.getName()), ValueSmallint.get((short)(var18 + 1)), var14, var15, var16, var17, IMPORTED_KEY_NOT_DEFERRABLE);
      }

   }

   private static ValueSmallint getRefAction(ConstraintActionType var0) {
      switch (var0) {
         case CASCADE:
            return IMPORTED_KEY_CASCADE;
         case RESTRICT:
            return IMPORTED_KEY_RESTRICT;
         case SET_DEFAULT:
            return IMPORTED_KEY_DEFAULT;
         case SET_NULL:
            return IMPORTED_KEY_SET_NULL;
         default:
            throw DbException.getInternalError("action=" + var0);
      }
   }

   public ResultInterface getTypeInfo() {
      this.checkClosed();
      SimpleResult var1 = new SimpleResult();
      var1.addColumn("TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var1.addColumn("PRECISION", TypeInfo.TYPE_INTEGER);
      var1.addColumn("LITERAL_PREFIX", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("LITERAL_SUFFIX", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("CREATE_PARAMS", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("NULLABLE", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("CASE_SENSITIVE", TypeInfo.TYPE_BOOLEAN);
      var1.addColumn("SEARCHABLE", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("UNSIGNED_ATTRIBUTE", TypeInfo.TYPE_BOOLEAN);
      var1.addColumn("FIXED_PREC_SCALE", TypeInfo.TYPE_BOOLEAN);
      var1.addColumn("AUTO_INCREMENT", TypeInfo.TYPE_BOOLEAN);
      var1.addColumn("LOCAL_TYPE_NAME", TypeInfo.TYPE_VARCHAR);
      var1.addColumn("MINIMUM_SCALE", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("MAXIMUM_SCALE", TypeInfo.TYPE_SMALLINT);
      var1.addColumn("SQL_DATA_TYPE", TypeInfo.TYPE_INTEGER);
      var1.addColumn("SQL_DATETIME_SUB", TypeInfo.TYPE_INTEGER);
      var1.addColumn("NUM_PREC_RADIX", TypeInfo.TYPE_INTEGER);
      int var2 = 1;

      for(byte var3 = 42; var2 < var3; ++var2) {
         DataType var4 = DataType.getDataType(var2);
         Value var5 = this.getString(Value.getTypeName(var4.type));
         var1.addRow(var5, ValueInteger.get(var4.sqlType), ValueInteger.get(MathUtils.convertLongToInt(var4.maxPrecision)), this.getString(var4.prefix), this.getString(var4.suffix), this.getString(var4.params), TYPE_NULLABLE, ValueBoolean.get(var4.caseSensitive), TYPE_SEARCHABLE, ValueBoolean.FALSE, ValueBoolean.get(var4.type == 13), ValueBoolean.FALSE, var5, ValueSmallint.get(MathUtils.convertIntToShort(var4.minScale)), ValueSmallint.get(MathUtils.convertIntToShort(var4.maxScale)), ValueNull.INSTANCE, ValueNull.INSTANCE, getRadix(var4.type, false));
      }

      var1.sortRows(new SortOrder(this.session, new int[]{1}));
      return var1;
   }

   private static Value getRadix(int var0, boolean var1) {
      if (!DataType.isNumericType(var0)) {
         return ValueNull.INSTANCE;
      } else {
         int var2 = var0 != 13 && var0 != 16 ? 2 : 10;
         return (Value)(var1 ? ValueSmallint.get((short)var2) : ValueInteger.get(var2));
      }
   }

   public ResultInterface getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5) {
      if (var3 == null) {
         throw DbException.getInvalidValueException("table", (Object)null);
      } else {
         this.checkClosed();
         SimpleResult var6 = new SimpleResult();
         var6.addColumn("TABLE_CAT", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("NON_UNIQUE", TypeInfo.TYPE_BOOLEAN);
         var6.addColumn("INDEX_QUALIFIER", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("INDEX_NAME", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("TYPE", TypeInfo.TYPE_SMALLINT);
         var6.addColumn("ORDINAL_POSITION", TypeInfo.TYPE_SMALLINT);
         var6.addColumn("COLUMN_NAME", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("ASC_OR_DESC", TypeInfo.TYPE_VARCHAR);
         var6.addColumn("CARDINALITY", TypeInfo.TYPE_BIGINT);
         var6.addColumn("PAGES", TypeInfo.TYPE_BIGINT);
         var6.addColumn("FILTER_CONDITION", TypeInfo.TYPE_VARCHAR);
         if (!this.checkCatalogName(var1)) {
            return var6;
         } else {
            Database var7 = this.session.getDatabase();
            Value var8 = this.getString(var7.getShortName());
            Iterator var9 = this.getSchemas(var2).iterator();

            while(var9.hasNext()) {
               Schema var10 = (Schema)var9.next();
               Table var11 = var10.findTableOrView(this.session, var3);
               if (var11 != null && !var11.isHidden()) {
                  this.getIndexInfo(var8, this.getString(var10.getName()), var11, var4, var5, var6, var7);
               }
            }

            var6.sortRows(new SortOrder(this.session, new int[]{3, 6, 5, 7}));
            return var6;
         }
      }
   }

   private void getIndexInfo(Value var1, Value var2, Table var3, boolean var4, boolean var5, SimpleResult var6, Database var7) {
      ArrayList var8 = var3.getIndexes();
      if (var8 != null) {
         Iterator var9 = var8.iterator();

         while(true) {
            Index var10;
            int var11;
            do {
               do {
                  if (!var9.hasNext()) {
                     return;
                  }

                  var10 = (Index)var9.next();
               } while(var10.getCreateSQL() == null);

               var11 = var10.getUniqueColumnCount();
            } while(var4 && var11 == 0);

            Value var12 = this.getString(var3.getName());
            Value var13 = this.getString(var10.getName());
            IndexColumn[] var14 = var10.getIndexColumns();
            ValueSmallint var15 = var10.getIndexType().isHash() ? TABLE_INDEX_HASHED : TABLE_INDEX_OTHER;
            int var16 = 0;

            for(int var17 = var14.length; var16 < var17; ++var16) {
               IndexColumn var18 = var14[var16];
               boolean var19 = var16 >= var11;
               if (var4 && var19) {
                  break;
               }

               var6.addRow(var1, var2, var12, ValueBoolean.get(var19), var1, var13, var15, ValueSmallint.get((short)(var16 + 1)), this.getString(var18.column.getName()), this.getString((var18.sortType & 1) != 0 ? "D" : "A"), ValueBigint.get(var5 ? var10.getRowCountApproximation(this.session) : var10.getRowCount(this.session)), ValueBigint.get(var10.getDiskSpaceUsed() / (long)var7.getPageSize()), ValueNull.INSTANCE);
            }
         }
      }
   }

   public ResultInterface getSchemas(String var1, String var2) {
      this.checkClosed();
      SimpleResult var3 = new SimpleResult();
      var3.addColumn("TABLE_SCHEM", TypeInfo.TYPE_VARCHAR);
      var3.addColumn("TABLE_CATALOG", TypeInfo.TYPE_VARCHAR);
      if (!this.checkCatalogName(var1)) {
         return var3;
      } else {
         CompareLike var4 = this.getLike(var2);
         Collection var5 = this.session.getDatabase().getAllSchemas();
         Value var6 = this.getString(this.session.getDatabase().getShortName());
         Iterator var7;
         Schema var8;
         if (var4 == null) {
            var7 = var5.iterator();

            while(var7.hasNext()) {
               var8 = (Schema)var7.next();
               var3.addRow(this.getString(var8.getName()), var6);
            }
         } else {
            var7 = var5.iterator();

            while(var7.hasNext()) {
               var8 = (Schema)var7.next();
               String var9 = var8.getName();
               if (var4.test(var9)) {
                  var3.addRow(this.getString(var8.getName()), var6);
               }
            }
         }

         var3.sortRows(new SortOrder(this.session, new int[]{0}));
         return var3;
      }
   }

   public ResultInterface getPseudoColumns(String var1, String var2, String var3, String var4) {
      SimpleResult var5 = this.getPseudoColumnsResult();
      if (!this.checkCatalogName(var1)) {
         return var5;
      } else {
         Database var6 = this.session.getDatabase();
         Value var7 = this.getString(var6.getShortName());
         CompareLike var8 = this.getLike(var4);
         Iterator var9 = this.getSchemasForPattern(var2).iterator();

         while(var9.hasNext()) {
            Schema var10 = (Schema)var9.next();
            Value var11 = this.getString(var10.getName());
            Iterator var12 = this.getTablesForPattern(var10, var3).iterator();

            while(var12.hasNext()) {
               SchemaObject var13 = (SchemaObject)var12.next();
               Value var14 = this.getString(var13.getName());
               if (var13 instanceof Table) {
                  Table var15 = (Table)var13;
                  if (!var15.isHidden()) {
                     this.getPseudoColumnsAdd(var5, var7, var11, var14, var15, var8);
                  }
               } else {
                  TableSynonym var17 = (TableSynonym)var13;
                  Table var16 = var17.getSynonymFor();
                  this.getPseudoColumnsAdd(var5, var7, var11, var14, var16, var8);
               }
            }
         }

         var5.sortRows(new SortOrder(this.session, new int[]{1, 2, 3}));
         return var5;
      }
   }

   private void getPseudoColumnsAdd(SimpleResult var1, Value var2, Value var3, Value var4, Table var5, CompareLike var6) {
      Column var7 = var5.getRowIdColumn();
      if (var7 != null) {
         this.getPseudoColumnsAdd(var1, var2, var3, var4, var6, var7);
      }

      Column[] var8 = var5.getColumns();
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Column var11 = var8[var10];
         if (!var11.getVisible()) {
            this.getPseudoColumnsAdd(var1, var2, var3, var4, var6, var11);
         }
      }

   }

   private void getPseudoColumnsAdd(SimpleResult var1, Value var2, Value var3, Value var4, CompareLike var5, Column var6) {
      String var7 = var6.getName();
      if (var5 == null || var5.test(var7)) {
         TypeInfo var8 = var6.getType();
         ValueInteger var9 = ValueInteger.get(MathUtils.convertLongToInt(var8.getPrecision()));
         var1.addRow(var2, var3, var4, this.getString(var7), ValueInteger.get(DataType.convertTypeToSQLType(var8)), var9, ValueInteger.get(var8.getScale()), getRadix(var8.getValueType(), false), NO_USAGE_RESTRICTIONS, this.getString(var6.getComment()), var9, var6.isNullable() ? YES : NO);
      }
   }

   void checkClosed() {
      if (this.session.isClosed()) {
         throw DbException.get(90121);
      }
   }

   Value getString(String var1) {
      return (Value)(var1 != null ? ValueVarchar.get(var1, this.session) : ValueNull.INSTANCE);
   }

   private boolean checkCatalogName(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         Database var2 = this.session.getDatabase();
         return var2.equalsIdentifiers(var1, var2.getShortName());
      } else {
         return true;
      }
   }

   private Collection<Schema> getSchemas(String var1) {
      Database var2 = this.session.getDatabase();
      if (var1 == null) {
         return var2.getAllSchemas();
      } else if (var1.isEmpty()) {
         return Collections.singleton(var2.getMainSchema());
      } else {
         Schema var3 = var2.findSchema(var1);
         return var3 != null ? Collections.singleton(var3) : Collections.emptySet();
      }
   }

   private Collection<Schema> getSchemasForPattern(String var1) {
      Database var2 = this.session.getDatabase();
      if (var1 == null) {
         return var2.getAllSchemas();
      } else if (var1.isEmpty()) {
         return Collections.singleton(var2.getMainSchema());
      } else {
         ArrayList var3 = Utils.newSmallArrayList();
         CompareLike var4 = this.getLike(var1);
         Iterator var5 = var2.getAllSchemas().iterator();

         while(var5.hasNext()) {
            Schema var6 = (Schema)var5.next();
            if (var4.test(var6.getName())) {
               var3.add(var6);
            }
         }

         return var3;
      }
   }

   private Collection<? extends SchemaObject> getTablesForPattern(Schema var1, String var2) {
      Collection var3 = var1.getAllTablesAndViews(this.session);
      Collection var4 = var1.getAllSynonyms();
      ArrayList var5;
      if (var2 == null) {
         if (var3.isEmpty()) {
            return var4;
         } else if (var4.isEmpty()) {
            return var3;
         } else {
            var5 = new ArrayList(var3.size() + var4.size());
            var5.addAll(var3);
            var5.addAll(var4);
            return var5;
         }
      } else if (var3.isEmpty() && var4.isEmpty()) {
         return Collections.emptySet();
      } else {
         var5 = Utils.newSmallArrayList();
         CompareLike var6 = this.getLike(var2);
         Iterator var7 = var3.iterator();

         while(var7.hasNext()) {
            Table var8 = (Table)var7.next();
            if (var6.test(var8.getName())) {
               var5.add(var8);
            }
         }

         var7 = var4.iterator();

         while(var7.hasNext()) {
            TableSynonym var9 = (TableSynonym)var7.next();
            if (var6.test(var9.getName())) {
               var5.add(var9);
            }
         }

         return var5;
      }
   }

   private boolean checkSchema(String var1, Schema var2) {
      if (var1 == null) {
         return true;
      } else if (var1.isEmpty()) {
         return var2 == this.session.getDatabase().getMainSchema();
      } else {
         return this.session.getDatabase().equalsIdentifiers(var1, var2.getName());
      }
   }

   private CompareLike getLike(String var1) {
      if (var1 == null) {
         return null;
      } else {
         CompareLike var2 = new CompareLike(this.session.getDatabase().getCompareMode(), "\\", (Expression)null, false, false, (Expression)null, (Expression)null, CompareLike.LikeType.LIKE);
         var2.initPattern(var1, '\\');
         return var2;
      }
   }
}
