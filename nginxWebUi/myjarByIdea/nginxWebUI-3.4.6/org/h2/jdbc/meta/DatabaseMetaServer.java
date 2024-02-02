package org.h2.jdbc.meta;

import org.h2.engine.SessionLocal;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.result.SimpleResult;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class DatabaseMetaServer {
   public static ResultInterface process(SessionLocal var0, int var1, Value[] var2) {
      DatabaseMeta var3 = var0.getDatabaseMeta();
      switch (var1) {
         case 0:
            return result(var3.defaultNullOrdering().ordinal());
         case 1:
            return result(var0, var3.getDatabaseProductVersion());
         case 2:
            return result(var0, var3.getSQLKeywords());
         case 3:
            return result(var0, var3.getNumericFunctions());
         case 4:
            return result(var0, var3.getStringFunctions());
         case 5:
            return result(var0, var3.getSystemFunctions());
         case 6:
            return result(var0, var3.getTimeDateFunctions());
         case 7:
            return result(var0, var3.getSearchStringEscape());
         case 8:
            return var3.getProcedures(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 9:
            return var3.getProcedureColumns(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString());
         case 10:
            return var3.getTables(var2[0].getString(), var2[1].getString(), var2[2].getString(), toStringArray(var2[3]));
         case 11:
            return var3.getSchemas();
         case 12:
            return var3.getCatalogs();
         case 13:
            return var3.getTableTypes();
         case 14:
            return var3.getColumns(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString());
         case 15:
            return var3.getColumnPrivileges(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString());
         case 16:
            return var3.getTablePrivileges(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 17:
            return var3.getBestRowIdentifier(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getInt(), var2[4].getBoolean());
         case 18:
            return var3.getVersionColumns(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 19:
            return var3.getPrimaryKeys(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 20:
            return var3.getImportedKeys(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 21:
            return var3.getExportedKeys(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 22:
            return var3.getCrossReference(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString(), var2[4].getString(), var2[5].getString());
         case 23:
            return var3.getTypeInfo();
         case 24:
            return var3.getIndexInfo(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getBoolean(), var2[4].getBoolean());
         case 25:
            return var3.getUDTs(var2[0].getString(), var2[1].getString(), var2[2].getString(), toIntArray(var2[3]));
         case 26:
            return var3.getSuperTypes(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 27:
            return var3.getSuperTables(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 28:
            return var3.getAttributes(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString());
         case 29:
            return result(var3.getDatabaseMajorVersion());
         case 30:
            return result(var3.getDatabaseMinorVersion());
         case 31:
            return var3.getSchemas(var2[0].getString(), var2[1].getString());
         case 32:
            return var3.getFunctions(var2[0].getString(), var2[1].getString(), var2[2].getString());
         case 33:
            return var3.getFunctionColumns(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString());
         case 34:
            return var3.getPseudoColumns(var2[0].getString(), var2[1].getString(), var2[2].getString(), var2[3].getString());
         default:
            throw DbException.getUnsupportedException("META " + var1);
      }
   }

   private static String[] toStringArray(Value var0) {
      if (var0 == ValueNull.INSTANCE) {
         return null;
      } else {
         Value[] var1 = ((ValueArray)var0).getList();
         int var2 = var1.length;
         String[] var3 = new String[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var1[var4].getString();
         }

         return var3;
      }
   }

   private static int[] toIntArray(Value var0) {
      if (var0 == ValueNull.INSTANCE) {
         return null;
      } else {
         Value[] var1 = ((ValueArray)var0).getList();
         int var2 = var1.length;
         int[] var3 = new int[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var1[var4].getInt();
         }

         return var3;
      }
   }

   private static ResultInterface result(int var0) {
      return result(ValueInteger.get(var0));
   }

   private static ResultInterface result(SessionLocal var0, String var1) {
      return result(ValueVarchar.get(var1, var0));
   }

   private static ResultInterface result(Value var0) {
      SimpleResult var1 = new SimpleResult();
      var1.addColumn("RESULT", var0.getType());
      var1.addRow(var0);
      return var1;
   }

   private DatabaseMetaServer() {
   }
}
