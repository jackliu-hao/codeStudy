/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ValueExpression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.DateTimeUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueDate;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueTimestamp;
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
/*     */ public final class FunctionsMySQL
/*     */   extends ModeFunction
/*     */ {
/*     */   private static final int UNIX_TIMESTAMP = 1001;
/*     */   private static final int FROM_UNIXTIME = 1002;
/*     */   private static final int DATE = 1003;
/*     */   private static final int LAST_INSERT_ID = 1004;
/*  39 */   private static final HashMap<String, FunctionInfo> FUNCTIONS = new HashMap<>(); private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
/*     */   
/*     */   static {
/*  42 */     FUNCTIONS.put("UNIX_TIMESTAMP", new FunctionInfo("UNIX_TIMESTAMP", 1001, -1, 11, false, false));
/*     */     
/*  44 */     FUNCTIONS.put("FROM_UNIXTIME", new FunctionInfo("FROM_UNIXTIME", 1002, -1, 2, false, true));
/*     */     
/*  46 */     FUNCTIONS.put("DATE", new FunctionInfo("DATE", 1003, 1, 17, false, true));
/*  47 */     FUNCTIONS.put("LAST_INSERT_ID", new FunctionInfo("LAST_INSERT_ID", 1004, -1, 12, false, false));
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
/*     */ 
/*     */ 
/*     */   
/*  62 */   private static final String[] FORMAT_REPLACE = new String[] { "%a", "EEE", "%b", "MMM", "%c", "MM", "%d", "dd", "%e", "d", "%H", "HH", "%h", "hh", "%I", "hh", "%i", "mm", "%j", "DDD", "%k", "H", "%l", "h", "%M", "MMMM", "%m", "MM", "%p", "a", "%r", "hh:mm:ss a", "%S", "ss", "%s", "ss", "%T", "HH:mm:ss", "%W", "EEEE", "%w", "F", "%Y", "yyyy", "%y", "yy", "%%", "%" };
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
/*     */   public static int unixTimestamp(SessionLocal paramSessionLocal, Value paramValue) {
/*     */     long l;
/* 100 */     if (paramValue instanceof ValueTimestampTimeZone) {
/* 101 */       ValueTimestampTimeZone valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/* 102 */       long l1 = valueTimestampTimeZone.getTimeNanos();
/*     */       
/* 104 */       l = DateTimeUtils.absoluteDayFromDateValue(valueTimestampTimeZone.getDateValue()) * 86400L + l1 / 1000000000L - valueTimestampTimeZone.getTimeZoneOffsetSeconds();
/*     */     } else {
/* 106 */       ValueTimestamp valueTimestamp = (ValueTimestamp)paramValue.convertTo(TypeInfo.TYPE_TIMESTAMP, (CastDataProvider)paramSessionLocal);
/* 107 */       long l1 = valueTimestamp.getTimeNanos();
/* 108 */       l = paramSessionLocal.currentTimeZone().getEpochSecondsFromLocal(valueTimestamp.getDateValue(), l1);
/*     */     } 
/* 110 */     return (int)l;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String fromUnixTime(int paramInt) {
/* 121 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
/*     */     
/* 123 */     return simpleDateFormat.format(new Date(paramInt * 1000L));
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
/*     */   public static String fromUnixTime(int paramInt, String paramString) {
/* 135 */     paramString = convertToSimpleDateFormat(paramString);
/* 136 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paramString, Locale.ENGLISH);
/* 137 */     return simpleDateFormat.format(new Date(paramInt * 1000L));
/*     */   }
/*     */   
/*     */   private static String convertToSimpleDateFormat(String paramString) {
/* 141 */     String[] arrayOfString = FORMAT_REPLACE;
/* 142 */     for (byte b = 0; b < arrayOfString.length; b += 2) {
/* 143 */       paramString = StringUtils.replaceAll(paramString, arrayOfString[b], arrayOfString[b + 1]);
/*     */     }
/* 145 */     return paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FunctionsMySQL getFunction(String paramString) {
/* 156 */     FunctionInfo functionInfo = FUNCTIONS.get(paramString);
/* 157 */     return (functionInfo != null) ? new FunctionsMySQL(functionInfo) : null;
/*     */   }
/*     */   
/*     */   FunctionsMySQL(FunctionInfo paramFunctionInfo) {
/* 161 */     super(paramFunctionInfo);
/*     */   }
/*     */   
/*     */   protected void checkParameterCount(int paramInt) {
/*     */     byte b1;
/*     */     byte b2;
/* 167 */     switch (this.info.type) {
/*     */       case 1001:
/* 169 */         b1 = 0;
/* 170 */         b2 = 1;
/*     */         break;
/*     */       case 1002:
/* 173 */         b1 = 1;
/* 174 */         b2 = 2;
/*     */         break;
/*     */       case 1003:
/* 177 */         b1 = 1;
/* 178 */         b2 = 1;
/*     */         break;
/*     */       case 1004:
/* 181 */         b1 = 0;
/* 182 */         b2 = 1;
/*     */         break;
/*     */       default:
/* 185 */         throw DbException.getInternalError("type=" + this.info.type);
/*     */     } 
/* 187 */     if (paramInt < b1 || paramInt > b2) {
/* 188 */       throw DbException.get(7001, new String[] { this.info.name, b1 + ".." + b2 });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 194 */     boolean bool = optimizeArguments(paramSessionLocal);
/* 195 */     this.type = TypeInfo.getTypeInfo(this.info.returnDataType);
/* 196 */     if (bool) {
/* 197 */       return (Expression)ValueExpression.get(getValue(paramSessionLocal));
/*     */     }
/* 199 */     return (Expression)this; } public Value getValue(SessionLocal paramSessionLocal) { ValueInteger valueInteger;
/*     */     Value value3;
/*     */     ValueNull valueNull;
/*     */     ValueDate valueDate;
/*     */     ValueBigint valueBigint;
/* 204 */     Value[] arrayOfValue = new Value[this.args.length];
/* 205 */     Value value1 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 0);
/* 206 */     Value value2 = getNullOrValue(paramSessionLocal, this.args, arrayOfValue, 1);
/*     */     
/* 208 */     switch (this.info.type) {
/*     */       case 1001:
/* 210 */         return (Value)ValueInteger.get(unixTimestamp(paramSessionLocal, (value1 == null) ? (Value)paramSessionLocal.currentTimestamp() : value1));
/*     */       
/*     */       case 1002:
/* 213 */         value3 = ValueVarchar.get((value2 == null) ? 
/* 214 */             fromUnixTime(value1.getInt()) : fromUnixTime(value1.getInt(), value2.getString()));
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
/* 255 */         return value3;case 1003: switch (value1.getValueType()) { case 0: case 17: value3 = value1; return value3;default: try { value1 = value1.convertTo(TypeInfo.TYPE_TIMESTAMP, (CastDataProvider)paramSessionLocal); break; } catch (DbException dbException) { valueNull = ValueNull.INSTANCE; }  return (Value)valueNull;case 20: case 21: break; }  return (Value)value1.convertToDate((CastDataProvider)paramSessionLocal);case 1004: if (this.args.length == 0) { Value value = paramSessionLocal.getLastIdentity(); if (value == ValueNull.INSTANCE) { valueBigint = ValueBigint.get(0L); } else { valueBigint = valueBigint.convertToBigint(null); }  } else { Value value = value1; if (value == ValueNull.INSTANCE) { paramSessionLocal.setLastIdentity((Value)ValueNull.INSTANCE); } else { paramSessionLocal.setLastIdentity((Value)(valueBigint = value.convertToBigint(null))); }  }  return (Value)valueBigint;
/*     */     } 
/*     */     throw DbException.getInternalError("type=" + this.info.type); }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\FunctionsMySQL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */