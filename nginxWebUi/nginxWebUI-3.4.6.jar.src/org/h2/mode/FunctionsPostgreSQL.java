/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.StringJoiner;
/*     */ import org.h2.command.Parser;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Constants;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.expression.function.CurrentGeneralValueSpecification;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.server.pg.PgServer;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueTimestampTimeZone;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FunctionsPostgreSQL
/*     */   extends ModeFunction
/*     */ {
/*     */   private static final int CURRENT_DATABASE = 3001;
/*     */   private static final int CURRTID2 = 3002;
/*     */   private static final int FORMAT_TYPE = 3003;
/*     */   private static final int HAS_DATABASE_PRIVILEGE = 3004;
/*     */   private static final int HAS_SCHEMA_PRIVILEGE = 3005;
/*     */   private static final int HAS_TABLE_PRIVILEGE = 3006;
/*     */   private static final int LASTVAL = 3007;
/*     */   private static final int VERSION = 3008;
/*     */   private static final int OBJ_DESCRIPTION = 3009;
/*     */   private static final int PG_ENCODING_TO_CHAR = 3010;
/*     */   private static final int PG_GET_EXPR = 3011;
/*     */   private static final int PG_GET_INDEXDEF = 3012;
/*     */   private static final int PG_GET_USERBYID = 3013;
/*     */   private static final int PG_POSTMASTER_START_TIME = 3014;
/*     */   private static final int PG_RELATION_SIZE = 3015;
/*     */   private static final int PG_TABLE_IS_VISIBLE = 3016;
/*     */   private static final int SET_CONFIG = 3017;
/*     */   private static final int ARRAY_TO_STRING = 3018;
/*     */   private static final int PG_STAT_GET_NUMSCANS = 3019;
/*     */   private static final int TO_DATE = 3020;
/*     */   private static final int TO_TIMESTAMP = 3021;
/*  84 */   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>(32);
/*     */   
/*     */   static {
/*  87 */     FUNCTIONS.put("CURRENT_DATABASE", new FunctionInfo("CURRENT_DATABASE", 3001, 0, 2, true, false));
/*     */     
/*  89 */     FUNCTIONS.put("CURRTID2", new FunctionInfo("CURRTID2", 3002, 2, 11, true, false));
/*  90 */     FUNCTIONS.put("FORMAT_TYPE", new FunctionInfo("FORMAT_TYPE", 3003, 2, 2, false, true));
/*  91 */     FUNCTIONS.put("HAS_DATABASE_PRIVILEGE", new FunctionInfo("HAS_DATABASE_PRIVILEGE", 3004, -1, 8, true, false));
/*     */     
/*  93 */     FUNCTIONS.put("HAS_SCHEMA_PRIVILEGE", new FunctionInfo("HAS_SCHEMA_PRIVILEGE", 3005, -1, 8, true, false));
/*     */     
/*  95 */     FUNCTIONS.put("HAS_TABLE_PRIVILEGE", new FunctionInfo("HAS_TABLE_PRIVILEGE", 3006, -1, 8, true, false));
/*     */     
/*  97 */     FUNCTIONS.put("LASTVAL", new FunctionInfo("LASTVAL", 3007, 0, 12, true, false));
/*  98 */     FUNCTIONS.put("VERSION", new FunctionInfo("VERSION", 3008, 0, 2, true, false));
/*  99 */     FUNCTIONS.put("OBJ_DESCRIPTION", new FunctionInfo("OBJ_DESCRIPTION", 3009, -1, 2, true, false));
/*     */     
/* 101 */     FUNCTIONS.put("PG_ENCODING_TO_CHAR", new FunctionInfo("PG_ENCODING_TO_CHAR", 3010, 1, 2, true, true));
/*     */     
/* 103 */     FUNCTIONS.put("PG_GET_EXPR", new FunctionInfo("PG_GET_EXPR", 3011, -1, 2, true, true));
/*     */     
/* 105 */     FUNCTIONS.put("PG_GET_INDEXDEF", new FunctionInfo("PG_GET_INDEXDEF", 3012, -1, 2, true, false));
/*     */     
/* 107 */     FUNCTIONS.put("PG_GET_USERBYID", new FunctionInfo("PG_GET_USERBYID", 3013, 1, 2, true, false));
/*     */     
/* 109 */     FUNCTIONS.put("PG_POSTMASTER_START_TIME", new FunctionInfo("PG_POSTMASTER_START_TIME", 3014, 0, 21, true, false));
/*     */ 
/*     */     
/* 112 */     FUNCTIONS.put("PG_RELATION_SIZE", new FunctionInfo("PG_RELATION_SIZE", 3015, -1, 12, true, false));
/*     */     
/* 114 */     FUNCTIONS.put("PG_TABLE_IS_VISIBLE", new FunctionInfo("PG_TABLE_IS_VISIBLE", 3016, 1, 8, true, false));
/*     */     
/* 116 */     FUNCTIONS.put("SET_CONFIG", new FunctionInfo("SET_CONFIG", 3017, 3, 2, true, false));
/* 117 */     FUNCTIONS.put("ARRAY_TO_STRING", new FunctionInfo("ARRAY_TO_STRING", 3018, -1, 2, false, true));
/*     */     
/* 119 */     FUNCTIONS.put("PG_STAT_GET_NUMSCANS", new FunctionInfo("PG_STAT_GET_NUMSCANS", 3019, 1, 11, true, true));
/*     */     
/* 121 */     FUNCTIONS.put("TO_DATE", new FunctionInfo("TO_DATE", 3020, 2, 17, true, true));
/* 122 */     FUNCTIONS.put("TO_TIMESTAMP", new FunctionInfo("TO_TIMESTAMP", 3021, 2, 21, true, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FunctionsPostgreSQL getFunction(String paramString) {
/* 135 */     FunctionInfo functionInfo = FUNCTIONS.get(paramString);
/* 136 */     if (functionInfo != null) {
/* 137 */       return new FunctionsPostgreSQL(functionInfo);
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */   
/*     */   private FunctionsPostgreSQL(FunctionInfo paramFunctionInfo) {
/* 143 */     super(paramFunctionInfo);
/*     */   }
/*     */   
/*     */   protected void checkParameterCount(int paramInt) {
/*     */     byte b1;
/*     */     byte b2;
/* 149 */     switch (this.info.type) {
/*     */       case 3004:
/*     */       case 3005:
/*     */       case 3006:
/* 153 */         b1 = 2;
/* 154 */         b2 = 3;
/*     */         break;
/*     */       case 3009:
/*     */       case 3015:
/* 158 */         b1 = 1;
/* 159 */         b2 = 2;
/*     */         break;
/*     */       case 3012:
/* 162 */         if (paramInt != 1 && paramInt != 3) {
/* 163 */           throw DbException.get(7001, new String[] { this.info.name, "1, 3" });
/*     */         }
/*     */         return;
/*     */       case 3011:
/*     */       case 3018:
/* 168 */         b1 = 2;
/* 169 */         b2 = 3;
/*     */         break;
/*     */       default:
/* 172 */         throw DbException.getInternalError("type=" + this.info.type);
/*     */     } 
/* 174 */     if (paramInt < b1 || paramInt > b2) {
/* 175 */       throw DbException.get(7001, new String[] { this.info.name, b1 + ".." + b2 });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 181 */     switch (this.info.type) {
/*     */       case 3001:
/* 183 */         return (new CurrentGeneralValueSpecification(0))
/* 184 */           .optimize(paramSessionLocal);
/*     */     } 
/* 186 */     boolean bool = optimizeArguments(paramSessionLocal);
/* 187 */     this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
/* 188 */     if (bool) {
/* 189 */       return (Expression)ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/*     */     
/* 192 */     return (Expression)this; } public Value getValue(SessionLocal paramSessionLocal) { ValueInteger valueInteger2; ValueBoolean valueBoolean; Value value8; ValueBigint valueBigint; Value value7; ValueNull valueNull2; Value value6; ValueNull valueNull1; Value value5;
/*     */     ValueTimestampTimeZone valueTimestampTimeZone;
/*     */     Value value4;
/*     */     ValueInteger valueInteger1;
/*     */     ValueDate valueDate;
/* 197 */     Value[] arrayOfValue = getArgumentsValues(paramSessionLocal, this.args);
/* 198 */     if (arrayOfValue == null) {
/* 199 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 201 */     Value value1 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 0);
/* 202 */     Value value2 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 1);
/* 203 */     Value value3 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 2);
/*     */     
/* 205 */     switch (this.info.type)
/*     */     
/*     */     { case 3002:
/* 208 */         valueInteger2 = ValueInteger.get(1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 295 */         return (Value)valueInteger2;case 3003: valueInteger2 = (value1 != ValueNull.INSTANCE) ? (ValueInteger)ValueVarchar.get(PgServer.formatType(value1.getInt())) : (ValueInteger)ValueNull.INSTANCE; return (Value)valueInteger2;case 3004: case 3005: case 3006: case 3016: return (Value)ValueBoolean.TRUE;case 3007: value8 = paramSessionLocal.getLastIdentity(); if (value8 == ValueNull.INSTANCE) throw DbException.get(90148, "lastval()");  return (Value)value8.convertToBigint(null);case 3008: return ValueVarchar.get("PostgreSQL 8.2.23 server protocol using H2 " + Constants.FULL_VERSION);case 3009: return (Value)ValueNull.INSTANCE;case 3010: return ValueVarchar.get(encodingToChar(value1.getInt()));case 3011: return (Value)ValueNull.INSTANCE;case 3012: value5 = getIndexdef(paramSessionLocal, value1.getInt(), value2, value3); return value5;case 3013: value5 = ValueVarchar.get(getUserbyid(paramSessionLocal, value1.getInt())); return value5;case 3014: return (Value)paramSessionLocal.getDatabase().getSystemSession().getSessionStart();case 3015: value4 = relationSize(paramSessionLocal, value1); return value4;case 3017: value4 = value2.convertTo(2); return value4;case 3018: if (value1 == ValueNull.INSTANCE || value2 == ValueNull.INSTANCE) { ValueNull valueNull = ValueNull.INSTANCE; } else { StringJoiner stringJoiner = new StringJoiner(value2.getString()); if (value1.getValueType() != 40) throw DbException.getInvalidValueException("ARRAY_TO_STRING array", value1);  String str = null; if (value3 != null) str = value3.getString();  for (Value value : ((ValueArray)value1).getList()) { if (value != ValueNull.INSTANCE) { stringJoiner.add(value.getString()); } else if (str != null) { stringJoiner.add(str); }  }  value4 = ValueVarchar.get(stringJoiner.toString()); }  return value4;
/*     */       case 3019: return (Value)ValueInteger.get(0);
/*     */       case 3020: return (Value)ToDateParser.toDate(paramSessionLocal, value1.getString(), value2.getString()).convertToDate((CastDataProvider)paramSessionLocal);
/*     */       case 3021:
/* 299 */         return (Value)ToDateParser.toTimestampTz(paramSessionLocal, value1.getString(), value2.getString()); }  throw DbException.getInternalError("type=" + this.info.type); } private static String encodingToChar(int paramInt) { switch (paramInt) {
/*     */       case 0:
/* 301 */         return "SQL_ASCII";
/*     */       case 6:
/* 303 */         return "UTF8";
/*     */       case 8:
/* 305 */         return "LATIN1";
/*     */     } 
/*     */     
/* 308 */     return (paramInt < 40) ? "UTF8" : ""; }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Value getIndexdef(SessionLocal paramSessionLocal, int paramInt, Value paramValue1, Value paramValue2) {
/* 313 */     for (Schema schema : paramSessionLocal.getDatabase().getAllSchemasNoMeta()) {
/* 314 */       for (Index index : schema.getAllIndexes()) {
/* 315 */         if (index.getId() == paramInt && 
/* 316 */           !index.getTable().isHidden()) {
/*     */           int i;
/* 318 */           if (paramValue1 == null || (i = paramValue1.getInt()) == 0) {
/* 319 */             return ValueVarchar.get(index.getCreateSQL());
/*     */           }
/*     */           Column[] arrayOfColumn;
/* 322 */           if (i >= 1 && i <= (arrayOfColumn = index.getColumns()).length) {
/* 323 */             return ValueVarchar.get(arrayOfColumn[i - 1].getName());
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 330 */     return (Value)ValueNull.INSTANCE;
/*     */   }
/*     */   private static String getUserbyid(SessionLocal paramSessionLocal, int paramInt) {
/*     */     String str;
/* 334 */     User user = paramSessionLocal.getUser();
/*     */ 
/*     */     
/* 337 */     if (user.getId() == paramInt)
/* 338 */     { str = user.getName(); }
/*     */     else
/*     */     
/* 341 */     { if (user.isAdmin())
/* 342 */       { Iterator<RightOwner> iterator = paramSessionLocal.getDatabase().getAllUsersAndRoles().iterator(); while (true) { if (iterator.hasNext()) { RightOwner rightOwner = iterator.next();
/* 343 */             if (rightOwner.getId() == paramInt) {
/* 344 */               str = rightOwner.getName();
/*     */             } else {
/*     */               continue;
/*     */             }  }
/*     */           else
/*     */           { break; }
/*     */ 
/*     */           
/* 352 */           if ((paramSessionLocal.getDatabase().getSettings()).databaseToLower) {
/* 353 */             str = StringUtils.toLowerEnglish(str);
/*     */           }
/* 355 */           return str; }  }  return "unknown (OID=" + paramInt + ')'; }  if ((paramSessionLocal.getDatabase().getSettings()).databaseToLower) str = StringUtils.toLowerEnglish(str);  return str;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Value relationSize(SessionLocal paramSessionLocal, Value paramValue) {
/* 360 */     if (paramValue.getValueType() == 11) {
/* 361 */       int i = paramValue.getInt();
/* 362 */       for (Schema schema : paramSessionLocal.getDatabase().getAllSchemasNoMeta()) {
/* 363 */         for (Table table1 : schema.getAllTablesAndViews(paramSessionLocal)) {
/* 364 */           if (i == table1.getId()) {
/* 365 */             Table table2 = table1;
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/* 370 */       return (Value)ValueNull.INSTANCE;
/*     */     } 
/* 372 */     Table table = (new Parser(paramSessionLocal)).parseTableName(paramValue.getString());
/*     */     
/* 374 */     return (Value)ValueBigint.get(table.getDiskSpaceUsed());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionsPostgreSQL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */