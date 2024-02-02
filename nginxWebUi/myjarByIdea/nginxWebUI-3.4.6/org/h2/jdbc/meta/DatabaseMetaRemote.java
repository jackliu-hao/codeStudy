package org.h2.jdbc.meta;

import java.io.IOException;
import java.util.ArrayList;
import org.h2.engine.SessionRemote;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.result.ResultInterface;
import org.h2.result.ResultRemote;
import org.h2.value.Transfer;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public class DatabaseMetaRemote extends DatabaseMeta {
   static final int DEFAULT_NULL_ORDERING = 0;
   static final int GET_DATABASE_PRODUCT_VERSION = 1;
   static final int GET_SQL_KEYWORDS = 2;
   static final int GET_NUMERIC_FUNCTIONS = 3;
   static final int GET_STRING_FUNCTIONS = 4;
   static final int GET_SYSTEM_FUNCTIONS = 5;
   static final int GET_TIME_DATE_FUNCTIONS = 6;
   static final int GET_SEARCH_STRING_ESCAPE = 7;
   static final int GET_PROCEDURES_3 = 8;
   static final int GET_PROCEDURE_COLUMNS_4 = 9;
   static final int GET_TABLES_4 = 10;
   static final int GET_SCHEMAS = 11;
   static final int GET_CATALOGS = 12;
   static final int GET_TABLE_TYPES = 13;
   static final int GET_COLUMNS_4 = 14;
   static final int GET_COLUMN_PRIVILEGES_4 = 15;
   static final int GET_TABLE_PRIVILEGES_3 = 16;
   static final int GET_BEST_ROW_IDENTIFIER_5 = 17;
   static final int GET_VERSION_COLUMNS_3 = 18;
   static final int GET_PRIMARY_KEYS_3 = 19;
   static final int GET_IMPORTED_KEYS_3 = 20;
   static final int GET_EXPORTED_KEYS_3 = 21;
   static final int GET_CROSS_REFERENCE_6 = 22;
   static final int GET_TYPE_INFO = 23;
   static final int GET_INDEX_INFO_5 = 24;
   static final int GET_UDTS_4 = 25;
   static final int GET_SUPER_TYPES_3 = 26;
   static final int GET_SUPER_TABLES_3 = 27;
   static final int GET_ATTRIBUTES_4 = 28;
   static final int GET_DATABASE_MAJOR_VERSION = 29;
   static final int GET_DATABASE_MINOR_VERSION = 30;
   static final int GET_SCHEMAS_2 = 31;
   static final int GET_FUNCTIONS_3 = 32;
   static final int GET_FUNCTION_COLUMNS_4 = 33;
   static final int GET_PSEUDO_COLUMNS_4 = 34;
   private final SessionRemote session;
   private final ArrayList<Transfer> transferList;

   public DatabaseMetaRemote(SessionRemote var1, ArrayList<Transfer> var2) {
      this.session = var1;
      this.transferList = var2;
   }

   public DefaultNullOrdering defaultNullOrdering() {
      ResultInterface var1 = this.executeQuery(0);
      var1.next();
      return DefaultNullOrdering.valueOf(var1.currentRow()[0].getInt());
   }

   public String getDatabaseProductVersion() {
      ResultInterface var1 = this.executeQuery(1);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public String getSQLKeywords() {
      ResultInterface var1 = this.executeQuery(2);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public String getNumericFunctions() {
      ResultInterface var1 = this.executeQuery(3);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public String getStringFunctions() {
      ResultInterface var1 = this.executeQuery(4);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public String getSystemFunctions() {
      ResultInterface var1 = this.executeQuery(5);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public String getTimeDateFunctions() {
      ResultInterface var1 = this.executeQuery(6);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public String getSearchStringEscape() {
      ResultInterface var1 = this.executeQuery(7);
      var1.next();
      return var1.currentRow()[0].getString();
   }

   public ResultInterface getProcedures(String var1, String var2, String var3) {
      return this.executeQuery(8, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getProcedureColumns(String var1, String var2, String var3, String var4) {
      return this.executeQuery(9, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4));
   }

   public ResultInterface getTables(String var1, String var2, String var3, String[] var4) {
      return this.executeQuery(10, this.getString(var1), this.getString(var2), this.getString(var3), this.getStringArray(var4));
   }

   public ResultInterface getSchemas() {
      return this.executeQuery(11);
   }

   public ResultInterface getCatalogs() {
      return this.executeQuery(12);
   }

   public ResultInterface getTableTypes() {
      return this.executeQuery(13);
   }

   public ResultInterface getColumns(String var1, String var2, String var3, String var4) {
      return this.executeQuery(14, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4));
   }

   public ResultInterface getColumnPrivileges(String var1, String var2, String var3, String var4) {
      return this.executeQuery(15, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4));
   }

   public ResultInterface getTablePrivileges(String var1, String var2, String var3) {
      return this.executeQuery(16, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5) {
      return this.executeQuery(17, this.getString(var1), this.getString(var2), this.getString(var3), ValueInteger.get(var4), ValueBoolean.get(var5));
   }

   public ResultInterface getVersionColumns(String var1, String var2, String var3) {
      return this.executeQuery(18, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getPrimaryKeys(String var1, String var2, String var3) {
      return this.executeQuery(19, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getImportedKeys(String var1, String var2, String var3) {
      return this.executeQuery(20, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getExportedKeys(String var1, String var2, String var3) {
      return this.executeQuery(21, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6) {
      return this.executeQuery(22, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4), this.getString(var5), this.getString(var6));
   }

   public ResultInterface getTypeInfo() {
      return this.executeQuery(23);
   }

   public ResultInterface getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5) {
      return this.executeQuery(24, this.getString(var1), this.getString(var2), this.getString(var3), ValueBoolean.get(var4), ValueBoolean.get(var5));
   }

   public ResultInterface getUDTs(String var1, String var2, String var3, int[] var4) {
      return this.executeQuery(25, this.getString(var1), this.getString(var2), this.getString(var3), this.getIntArray(var4));
   }

   public ResultInterface getSuperTypes(String var1, String var2, String var3) {
      return this.executeQuery(26, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getSuperTables(String var1, String var2, String var3) {
      return this.executeQuery(27, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getAttributes(String var1, String var2, String var3, String var4) {
      return this.executeQuery(28, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4));
   }

   public int getDatabaseMajorVersion() {
      ResultInterface var1 = this.executeQuery(29);
      var1.next();
      return var1.currentRow()[0].getInt();
   }

   public int getDatabaseMinorVersion() {
      ResultInterface var1 = this.executeQuery(30);
      var1.next();
      return var1.currentRow()[0].getInt();
   }

   public ResultInterface getSchemas(String var1, String var2) {
      return this.executeQuery(31, this.getString(var1), this.getString(var2));
   }

   public ResultInterface getFunctions(String var1, String var2, String var3) {
      return this.executeQuery(32, this.getString(var1), this.getString(var2), this.getString(var3));
   }

   public ResultInterface getFunctionColumns(String var1, String var2, String var3, String var4) {
      return this.executeQuery(33, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4));
   }

   public ResultInterface getPseudoColumns(String var1, String var2, String var3, String var4) {
      return this.executeQuery(34, this.getString(var1), this.getString(var2), this.getString(var3), this.getString(var4));
   }

   private ResultInterface executeQuery(int var1, Value... var2) {
      if (this.session.isClosed()) {
         throw DbException.get(90121);
      } else {
         synchronized(this.session) {
            int var4 = this.session.getNextId();
            int var5 = 0;
            int var6 = 0;

            while(var5 < this.transferList.size()) {
               Transfer var7 = (Transfer)this.transferList.get(var5);

               try {
                  this.session.traceOperation("GET_META", var4);
                  int var8 = var2.length;
                  var7.writeInt(19).writeInt(var1).writeInt(var8);

                  int var9;
                  for(var9 = 0; var9 < var8; ++var9) {
                     var7.writeValue(var2[var9]);
                  }

                  this.session.done(var7);
                  var9 = var7.readInt();
                  ResultRemote var10000 = new ResultRemote(this.session, var7, var4, var9, Integer.MAX_VALUE);
                  return var10000;
               } catch (IOException var11) {
                  int var10002 = var5--;
                  ++var6;
                  this.session.removeServer(var11, var10002, var6);
                  ++var5;
               }
            }

            return null;
         }
      }
   }

   private Value getIntArray(int[] var1) {
      if (var1 == null) {
         return ValueNull.INSTANCE;
      } else {
         int var2 = var1.length;
         Value[] var3 = new Value[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = ValueInteger.get(var1[var4]);
         }

         return ValueArray.get(TypeInfo.TYPE_INTEGER, var3, this.session);
      }
   }

   private Value getStringArray(String[] var1) {
      if (var1 == null) {
         return ValueNull.INSTANCE;
      } else {
         int var2 = var1.length;
         Value[] var3 = new Value[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = this.getString(var1[var4]);
         }

         return ValueArray.get(TypeInfo.TYPE_VARCHAR, var3, this.session);
      }
   }

   private Value getString(String var1) {
      return (Value)(var1 != null ? ValueVarchar.get(var1, this.session) : ValueNull.INSTANCE);
   }
}
