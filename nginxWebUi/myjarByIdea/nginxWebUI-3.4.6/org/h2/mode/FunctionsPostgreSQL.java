package org.h2.mode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringJoiner;
import org.h2.command.Parser;
import org.h2.engine.Constants;
import org.h2.engine.RightOwner;
import org.h2.engine.SessionLocal;
import org.h2.engine.User;
import org.h2.expression.Expression;
import org.h2.expression.ValueExpression;
import org.h2.expression.function.CurrentGeneralValueSpecification;
import org.h2.index.Index;
import org.h2.message.DbException;
import org.h2.schema.Schema;
import org.h2.server.pg.PgServer;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.StringUtils;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueVarchar;

public final class FunctionsPostgreSQL extends ModeFunction {
   private static final int CURRENT_DATABASE = 3001;
   private static final int CURRTID2 = 3002;
   private static final int FORMAT_TYPE = 3003;
   private static final int HAS_DATABASE_PRIVILEGE = 3004;
   private static final int HAS_SCHEMA_PRIVILEGE = 3005;
   private static final int HAS_TABLE_PRIVILEGE = 3006;
   private static final int LASTVAL = 3007;
   private static final int VERSION = 3008;
   private static final int OBJ_DESCRIPTION = 3009;
   private static final int PG_ENCODING_TO_CHAR = 3010;
   private static final int PG_GET_EXPR = 3011;
   private static final int PG_GET_INDEXDEF = 3012;
   private static final int PG_GET_USERBYID = 3013;
   private static final int PG_POSTMASTER_START_TIME = 3014;
   private static final int PG_RELATION_SIZE = 3015;
   private static final int PG_TABLE_IS_VISIBLE = 3016;
   private static final int SET_CONFIG = 3017;
   private static final int ARRAY_TO_STRING = 3018;
   private static final int PG_STAT_GET_NUMSCANS = 3019;
   private static final int TO_DATE = 3020;
   private static final int TO_TIMESTAMP = 3021;
   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap(32);

   public static FunctionsPostgreSQL getFunction(String var0) {
      FunctionInfo var1 = (FunctionInfo)FUNCTIONS.get(var0);
      return var1 != null ? new FunctionsPostgreSQL(var1) : null;
   }

   private FunctionsPostgreSQL(FunctionInfo var1) {
      super(var1);
   }

   protected void checkParameterCount(int var1) {
      byte var2;
      byte var3;
      switch (this.info.type) {
         case 3004:
         case 3005:
         case 3006:
            var2 = 2;
            var3 = 3;
            break;
         case 3007:
         case 3008:
         case 3010:
         case 3013:
         case 3014:
         case 3016:
         case 3017:
         default:
            throw DbException.getInternalError("type=" + this.info.type);
         case 3009:
         case 3015:
            var2 = 1;
            var3 = 2;
            break;
         case 3011:
         case 3018:
            var2 = 2;
            var3 = 3;
            break;
         case 3012:
            if (var1 != 1 && var1 != 3) {
               throw DbException.get(7001, (String[])(this.info.name, "1, 3"));
            }

            return;
      }

      if (var1 < var2 || var1 > var3) {
         throw DbException.get(7001, (String[])(this.info.name, var2 + ".." + var3));
      }
   }

   public Expression optimize(SessionLocal var1) {
      switch (this.info.type) {
         case 3001:
            return (new CurrentGeneralValueSpecification(0)).optimize(var1);
         default:
            boolean var2 = this.optimizeArguments(var1);
            this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
            return (Expression)(var2 ? ValueExpression.get(this.getValue(var1)) : this);
      }
   }

   public Value getValue(SessionLocal var1) {
      Value[] var2 = this.getArgumentsValues(var1, this.args);
      if (var2 == null) {
         return ValueNull.INSTANCE;
      } else {
         Value var3 = getNullOrValue(var1, this.args, var2, 0);
         Value var4 = getNullOrValue(var1, this.args, var2, 1);
         Value var5 = getNullOrValue(var1, this.args, var2, 2);
         Object var6;
         switch (this.info.type) {
            case 3002:
               var6 = ValueInteger.get(1);
               break;
            case 3003:
               var6 = var3 != ValueNull.INSTANCE ? ValueVarchar.get(PgServer.formatType(var3.getInt())) : ValueNull.INSTANCE;
               break;
            case 3004:
            case 3005:
            case 3006:
            case 3016:
               var6 = ValueBoolean.TRUE;
               break;
            case 3007:
               Value var13 = var1.getLastIdentity();
               if (var13 == ValueNull.INSTANCE) {
                  throw DbException.get(90148, "lastval()");
               }

               var6 = var13.convertToBigint((Object)null);
               break;
            case 3008:
               var6 = ValueVarchar.get("PostgreSQL 8.2.23 server protocol using H2 " + Constants.FULL_VERSION);
               break;
            case 3009:
               var6 = ValueNull.INSTANCE;
               break;
            case 3010:
               var6 = ValueVarchar.get(encodingToChar(var3.getInt()));
               break;
            case 3011:
               var6 = ValueNull.INSTANCE;
               break;
            case 3012:
               var6 = getIndexdef(var1, var3.getInt(), var4, var5);
               break;
            case 3013:
               var6 = ValueVarchar.get(getUserbyid(var1, var3.getInt()));
               break;
            case 3014:
               var6 = var1.getDatabase().getSystemSession().getSessionStart();
               break;
            case 3015:
               var6 = relationSize(var1, var3);
               break;
            case 3017:
               var6 = var4.convertTo(2);
               break;
            case 3018:
               if (var3 != ValueNull.INSTANCE && var4 != ValueNull.INSTANCE) {
                  StringJoiner var7 = new StringJoiner(var4.getString());
                  if (var3.getValueType() != 40) {
                     throw DbException.getInvalidValueException("ARRAY_TO_STRING array", var3);
                  }

                  String var8 = null;
                  if (var5 != null) {
                     var8 = var5.getString();
                  }

                  Value[] var9 = ((ValueArray)var3).getList();
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Value var12 = var9[var11];
                     if (var12 != ValueNull.INSTANCE) {
                        var7.add(var12.getString());
                     } else if (var8 != null) {
                        var7.add(var8);
                     }
                  }

                  var6 = ValueVarchar.get(var7.toString());
               } else {
                  var6 = ValueNull.INSTANCE;
               }
               break;
            case 3019:
               var6 = ValueInteger.get(0);
               break;
            case 3020:
               var6 = ToDateParser.toDate(var1, var3.getString(), var4.getString()).convertToDate(var1);
               break;
            case 3021:
               var6 = ToDateParser.toTimestampTz(var1, var3.getString(), var4.getString());
               break;
            default:
               throw DbException.getInternalError("type=" + this.info.type);
         }

         return (Value)var6;
      }
   }

   private static String encodingToChar(int var0) {
      switch (var0) {
         case 0:
            return "SQL_ASCII";
         case 6:
            return "UTF8";
         case 8:
            return "LATIN1";
         default:
            return var0 < 40 ? "UTF8" : "";
      }
   }

   private static Value getIndexdef(SessionLocal var0, int var1, Value var2, Value var3) {
      Iterator var4 = var0.getDatabase().getAllSchemasNoMeta().iterator();

      Index var7;
      int var8;
      Column[] var9;
      do {
         label34:
         do {
            while(var4.hasNext()) {
               Schema var5 = (Schema)var4.next();
               Iterator var6 = var5.getAllIndexes().iterator();

               while(var6.hasNext()) {
                  var7 = (Index)var6.next();
                  if (var7.getId() == var1) {
                     continue label34;
                  }
               }
            }

            return ValueNull.INSTANCE;
         } while(var7.getTable().isHidden());

         if (var2 == null || (var8 = var2.getInt()) == 0) {
            return ValueVarchar.get(var7.getCreateSQL());
         }
      } while(var8 < 1 || var8 > (var9 = var7.getColumns()).length);

      return ValueVarchar.get(var9[var8 - 1].getName());
   }

   private static String getUserbyid(SessionLocal var0, int var1) {
      User var2 = var0.getUser();
      String var3;
      if (var2.getId() == var1) {
         var3 = var2.getName();
      } else {
         label37: {
            if (var2.isAdmin()) {
               Iterator var4 = var0.getDatabase().getAllUsersAndRoles().iterator();

               while(var4.hasNext()) {
                  RightOwner var5 = (RightOwner)var4.next();
                  if (var5.getId() == var1) {
                     var3 = var5.getName();
                     break label37;
                  }
               }
            }

            return "unknown (OID=" + var1 + ')';
         }
      }

      if (var0.getDatabase().getSettings().databaseToLower) {
         var3 = StringUtils.toLowerEnglish(var3);
      }

      return var3;
   }

   private static Value relationSize(SessionLocal var0, Value var1) {
      if (var1.getValueType() != 11) {
         Table var2 = (new Parser(var0)).parseTableName(var1.getString());
         return ValueBigint.get(var2.getDiskSpaceUsed());
      } else {
         int var3 = var1.getInt();
         Iterator var4 = var0.getDatabase().getAllSchemasNoMeta().iterator();

         while(true) {
            while(var4.hasNext()) {
               Schema var5 = (Schema)var4.next();
               Iterator var6 = var5.getAllTablesAndViews(var0).iterator();

               while(var6.hasNext()) {
                  Table var7 = (Table)var6.next();
                  if (var3 == var7.getId()) {
                     break;
                  }
               }
            }

            return ValueNull.INSTANCE;
         }
      }
   }

   static {
      FUNCTIONS.put("CURRENT_DATABASE", new FunctionInfo("CURRENT_DATABASE", 3001, 0, 2, true, false));
      FUNCTIONS.put("CURRTID2", new FunctionInfo("CURRTID2", 3002, 2, 11, true, false));
      FUNCTIONS.put("FORMAT_TYPE", new FunctionInfo("FORMAT_TYPE", 3003, 2, 2, false, true));
      FUNCTIONS.put("HAS_DATABASE_PRIVILEGE", new FunctionInfo("HAS_DATABASE_PRIVILEGE", 3004, -1, 8, true, false));
      FUNCTIONS.put("HAS_SCHEMA_PRIVILEGE", new FunctionInfo("HAS_SCHEMA_PRIVILEGE", 3005, -1, 8, true, false));
      FUNCTIONS.put("HAS_TABLE_PRIVILEGE", new FunctionInfo("HAS_TABLE_PRIVILEGE", 3006, -1, 8, true, false));
      FUNCTIONS.put("LASTVAL", new FunctionInfo("LASTVAL", 3007, 0, 12, true, false));
      FUNCTIONS.put("VERSION", new FunctionInfo("VERSION", 3008, 0, 2, true, false));
      FUNCTIONS.put("OBJ_DESCRIPTION", new FunctionInfo("OBJ_DESCRIPTION", 3009, -1, 2, true, false));
      FUNCTIONS.put("PG_ENCODING_TO_CHAR", new FunctionInfo("PG_ENCODING_TO_CHAR", 3010, 1, 2, true, true));
      FUNCTIONS.put("PG_GET_EXPR", new FunctionInfo("PG_GET_EXPR", 3011, -1, 2, true, true));
      FUNCTIONS.put("PG_GET_INDEXDEF", new FunctionInfo("PG_GET_INDEXDEF", 3012, -1, 2, true, false));
      FUNCTIONS.put("PG_GET_USERBYID", new FunctionInfo("PG_GET_USERBYID", 3013, 1, 2, true, false));
      FUNCTIONS.put("PG_POSTMASTER_START_TIME", new FunctionInfo("PG_POSTMASTER_START_TIME", 3014, 0, 21, true, false));
      FUNCTIONS.put("PG_RELATION_SIZE", new FunctionInfo("PG_RELATION_SIZE", 3015, -1, 12, true, false));
      FUNCTIONS.put("PG_TABLE_IS_VISIBLE", new FunctionInfo("PG_TABLE_IS_VISIBLE", 3016, 1, 8, true, false));
      FUNCTIONS.put("SET_CONFIG", new FunctionInfo("SET_CONFIG", 3017, 3, 2, true, false));
      FUNCTIONS.put("ARRAY_TO_STRING", new FunctionInfo("ARRAY_TO_STRING", 3018, -1, 2, false, true));
      FUNCTIONS.put("PG_STAT_GET_NUMSCANS", new FunctionInfo("PG_STAT_GET_NUMSCANS", 3019, 1, 11, true, true));
      FUNCTIONS.put("TO_DATE", new FunctionInfo("TO_DATE", 3020, 2, 17, true, true));
      FUNCTIONS.put("TO_TIMESTAMP", new FunctionInfo("TO_TIMESTAMP", 3021, 2, 21, true, true));
   }
}
