/*     */ package org.h2.api;
/*     */ 
/*     */ import java.sql.SQLType;
/*     */ import org.h2.value.ExtTypeInfo;
/*     */ import org.h2.value.ExtTypeInfoRow;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Typed;
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
/*     */ public final class H2Type
/*     */   implements SQLType
/*     */ {
/*  24 */   public static final H2Type CHAR = new H2Type(TypeInfo.getTypeInfo(1), "CHARACTER");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  29 */   public static final H2Type VARCHAR = new H2Type(TypeInfo.TYPE_VARCHAR, "CHARACTER VARYING");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final H2Type CLOB = new H2Type(TypeInfo.TYPE_CLOB, "CHARACTER LARGE OBJECT");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   public static final H2Type VARCHAR_IGNORECASE = new H2Type(TypeInfo.TYPE_VARCHAR_IGNORECASE, "VARCHAR_IGNORECASE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static final H2Type BINARY = new H2Type(TypeInfo.getTypeInfo(5), "BINARY");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   public static final H2Type VARBINARY = new H2Type(TypeInfo.TYPE_VARBINARY, "BINARY VARYING");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   public static final H2Type BLOB = new H2Type(TypeInfo.TYPE_BLOB, "BINARY LARGE OBJECT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  63 */   public static final H2Type BOOLEAN = new H2Type(TypeInfo.TYPE_BOOLEAN, "BOOLEAN");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public static final H2Type TINYINT = new H2Type(TypeInfo.TYPE_TINYINT, "TINYINT");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public static final H2Type SMALLINT = new H2Type(TypeInfo.TYPE_SMALLINT, "SMALLINT");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final H2Type INTEGER = new H2Type(TypeInfo.TYPE_INTEGER, "INTEGER");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final H2Type BIGINT = new H2Type(TypeInfo.TYPE_BIGINT, "BIGINT");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  90 */   public static final H2Type NUMERIC = new H2Type(TypeInfo.TYPE_NUMERIC_FLOATING_POINT, "NUMERIC");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   public static final H2Type REAL = new H2Type(TypeInfo.TYPE_REAL, "REAL");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   public static final H2Type DOUBLE_PRECISION = new H2Type(TypeInfo.TYPE_DOUBLE, "DOUBLE PRECISION");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 109 */   public static final H2Type DECFLOAT = new H2Type(TypeInfo.TYPE_DECFLOAT, "DECFLOAT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 116 */   public static final H2Type DATE = new H2Type(TypeInfo.TYPE_DATE, "DATE");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   public static final H2Type TIME = new H2Type(TypeInfo.TYPE_TIME, "TIME");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 126 */   public static final H2Type TIME_WITH_TIME_ZONE = new H2Type(TypeInfo.TYPE_TIME_TZ, "TIME WITH TIME ZONE");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 131 */   public static final H2Type TIMESTAMP = new H2Type(TypeInfo.TYPE_TIMESTAMP, "TIMESTAMP");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   public static final H2Type TIMESTAMP_WITH_TIME_ZONE = new H2Type(TypeInfo.TYPE_TIMESTAMP_TZ, "TIMESTAMP WITH TIME ZONE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 144 */   public static final H2Type INTERVAL_YEAR = new H2Type(TypeInfo.getTypeInfo(22), "INTERVAL_YEAR");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 149 */   public static final H2Type INTERVAL_MONTH = new H2Type(TypeInfo.getTypeInfo(23), "INTERVAL_MONTH");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 155 */   public static final H2Type INTERVAL_DAY = new H2Type(TypeInfo.TYPE_INTERVAL_DAY, "INTERVAL_DAY");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 160 */   public static final H2Type INTERVAL_HOUR = new H2Type(TypeInfo.getTypeInfo(25), "INTERVAL_HOUR");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 165 */   public static final H2Type INTERVAL_MINUTE = new H2Type(TypeInfo.getTypeInfo(26), "INTERVAL_MINUTE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   public static final H2Type INTERVAL_SECOND = new H2Type(TypeInfo.getTypeInfo(27), "INTERVAL_SECOND");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 177 */   public static final H2Type INTERVAL_YEAR_TO_MONTH = new H2Type(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH, "INTERVAL_YEAR_TO_MONTH");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 183 */   public static final H2Type INTERVAL_DAY_TO_HOUR = new H2Type(TypeInfo.getTypeInfo(29), "INTERVAL_DAY_TO_HOUR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 189 */   public static final H2Type INTERVAL_DAY_TO_MINUTE = new H2Type(TypeInfo.getTypeInfo(30), "INTERVAL_DAY_TO_MINUTE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 195 */   public static final H2Type INTERVAL_DAY_TO_SECOND = new H2Type(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND, "INTERVAL_DAY_TO_SECOND");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 201 */   public static final H2Type INTERVAL_HOUR_TO_MINUTE = new H2Type(
/* 202 */       TypeInfo.getTypeInfo(32), "INTERVAL_HOUR_TO_MINUTE");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 207 */   public static final H2Type INTERVAL_HOUR_TO_SECOND = new H2Type(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND, "INTERVAL_HOUR_TO_SECOND");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 213 */   public static final H2Type INTERVAL_MINUTE_TO_SECOND = new H2Type(
/* 214 */       TypeInfo.getTypeInfo(34), "INTERVAL_MINUTE_TO_SECOND");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 221 */   public static final H2Type JAVA_OBJECT = new H2Type(TypeInfo.TYPE_JAVA_OBJECT, "JAVA_OBJECT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 228 */   public static final H2Type ENUM = new H2Type(TypeInfo.TYPE_ENUM_UNDEFINED, "ENUM");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 233 */   public static final H2Type GEOMETRY = new H2Type(TypeInfo.TYPE_GEOMETRY, "GEOMETRY");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 238 */   public static final H2Type JSON = new H2Type(TypeInfo.TYPE_JSON, "JSON");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 243 */   public static final H2Type UUID = new H2Type(TypeInfo.TYPE_UUID, "UUID");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TypeInfo typeInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String field;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static H2Type array(H2Type paramH2Type) {
/* 259 */     return new H2Type(TypeInfo.getTypeInfo(40, -1L, -1, (ExtTypeInfo)paramH2Type.typeInfo), "array(" + paramH2Type.field + ')');
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
/*     */   public static H2Type row(H2Type... paramVarArgs) {
/* 271 */     int i = paramVarArgs.length;
/* 272 */     TypeInfo[] arrayOfTypeInfo = new TypeInfo[i];
/* 273 */     StringBuilder stringBuilder = new StringBuilder("row(");
/* 274 */     for (byte b = 0; b < i; b++) {
/* 275 */       H2Type h2Type = paramVarArgs[b];
/* 276 */       arrayOfTypeInfo[b] = h2Type.typeInfo;
/* 277 */       if (b > 0) {
/* 278 */         stringBuilder.append(", ");
/*     */       }
/* 280 */       stringBuilder.append(h2Type.field);
/*     */     } 
/* 282 */     return new H2Type(TypeInfo.getTypeInfo(41, -1L, -1, (ExtTypeInfo)new ExtTypeInfoRow((Typed[])arrayOfTypeInfo)), stringBuilder
/* 283 */         .append(')').toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private H2Type(TypeInfo paramTypeInfo, String paramString) {
/* 291 */     this.typeInfo = paramTypeInfo;
/* 292 */     this.field = "H2Type." + paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 297 */     return this.typeInfo.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVendor() {
/* 302 */     return "com.h2database";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getVendorTypeNumber() {
/* 313 */     return Integer.valueOf(this.typeInfo.getValueType());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 318 */     return this.field;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\H2Type.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */