package org.h2.api;

import java.sql.SQLType;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;

public final class H2Type implements SQLType {
   public static final H2Type CHAR = new H2Type(TypeInfo.getTypeInfo(1), "CHARACTER");
   public static final H2Type VARCHAR;
   public static final H2Type CLOB;
   public static final H2Type VARCHAR_IGNORECASE;
   public static final H2Type BINARY;
   public static final H2Type VARBINARY;
   public static final H2Type BLOB;
   public static final H2Type BOOLEAN;
   public static final H2Type TINYINT;
   public static final H2Type SMALLINT;
   public static final H2Type INTEGER;
   public static final H2Type BIGINT;
   public static final H2Type NUMERIC;
   public static final H2Type REAL;
   public static final H2Type DOUBLE_PRECISION;
   public static final H2Type DECFLOAT;
   public static final H2Type DATE;
   public static final H2Type TIME;
   public static final H2Type TIME_WITH_TIME_ZONE;
   public static final H2Type TIMESTAMP;
   public static final H2Type TIMESTAMP_WITH_TIME_ZONE;
   public static final H2Type INTERVAL_YEAR;
   public static final H2Type INTERVAL_MONTH;
   public static final H2Type INTERVAL_DAY;
   public static final H2Type INTERVAL_HOUR;
   public static final H2Type INTERVAL_MINUTE;
   public static final H2Type INTERVAL_SECOND;
   public static final H2Type INTERVAL_YEAR_TO_MONTH;
   public static final H2Type INTERVAL_DAY_TO_HOUR;
   public static final H2Type INTERVAL_DAY_TO_MINUTE;
   public static final H2Type INTERVAL_DAY_TO_SECOND;
   public static final H2Type INTERVAL_HOUR_TO_MINUTE;
   public static final H2Type INTERVAL_HOUR_TO_SECOND;
   public static final H2Type INTERVAL_MINUTE_TO_SECOND;
   public static final H2Type JAVA_OBJECT;
   public static final H2Type ENUM;
   public static final H2Type GEOMETRY;
   public static final H2Type JSON;
   public static final H2Type UUID;
   private TypeInfo typeInfo;
   private String field;

   public static H2Type array(H2Type var0) {
      return new H2Type(TypeInfo.getTypeInfo(40, -1L, -1, var0.typeInfo), "array(" + var0.field + ')');
   }

   public static H2Type row(H2Type... var0) {
      int var1 = var0.length;
      TypeInfo[] var2 = new TypeInfo[var1];
      StringBuilder var3 = new StringBuilder("row(");

      for(int var4 = 0; var4 < var1; ++var4) {
         H2Type var5 = var0[var4];
         var2[var4] = var5.typeInfo;
         if (var4 > 0) {
            var3.append(", ");
         }

         var3.append(var5.field);
      }

      return new H2Type(TypeInfo.getTypeInfo(41, -1L, -1, new ExtTypeInfoRow(var2)), var3.append(')').toString());
   }

   private H2Type(TypeInfo var1, String var2) {
      this.typeInfo = var1;
      this.field = "H2Type." + var2;
   }

   public String getName() {
      return this.typeInfo.toString();
   }

   public String getVendor() {
      return "com.h2database";
   }

   public Integer getVendorTypeNumber() {
      return this.typeInfo.getValueType();
   }

   public String toString() {
      return this.field;
   }

   static {
      VARCHAR = new H2Type(TypeInfo.TYPE_VARCHAR, "CHARACTER VARYING");
      CLOB = new H2Type(TypeInfo.TYPE_CLOB, "CHARACTER LARGE OBJECT");
      VARCHAR_IGNORECASE = new H2Type(TypeInfo.TYPE_VARCHAR_IGNORECASE, "VARCHAR_IGNORECASE");
      BINARY = new H2Type(TypeInfo.getTypeInfo(5), "BINARY");
      VARBINARY = new H2Type(TypeInfo.TYPE_VARBINARY, "BINARY VARYING");
      BLOB = new H2Type(TypeInfo.TYPE_BLOB, "BINARY LARGE OBJECT");
      BOOLEAN = new H2Type(TypeInfo.TYPE_BOOLEAN, "BOOLEAN");
      TINYINT = new H2Type(TypeInfo.TYPE_TINYINT, "TINYINT");
      SMALLINT = new H2Type(TypeInfo.TYPE_SMALLINT, "SMALLINT");
      INTEGER = new H2Type(TypeInfo.TYPE_INTEGER, "INTEGER");
      BIGINT = new H2Type(TypeInfo.TYPE_BIGINT, "BIGINT");
      NUMERIC = new H2Type(TypeInfo.TYPE_NUMERIC_FLOATING_POINT, "NUMERIC");
      REAL = new H2Type(TypeInfo.TYPE_REAL, "REAL");
      DOUBLE_PRECISION = new H2Type(TypeInfo.TYPE_DOUBLE, "DOUBLE PRECISION");
      DECFLOAT = new H2Type(TypeInfo.TYPE_DECFLOAT, "DECFLOAT");
      DATE = new H2Type(TypeInfo.TYPE_DATE, "DATE");
      TIME = new H2Type(TypeInfo.TYPE_TIME, "TIME");
      TIME_WITH_TIME_ZONE = new H2Type(TypeInfo.TYPE_TIME_TZ, "TIME WITH TIME ZONE");
      TIMESTAMP = new H2Type(TypeInfo.TYPE_TIMESTAMP, "TIMESTAMP");
      TIMESTAMP_WITH_TIME_ZONE = new H2Type(TypeInfo.TYPE_TIMESTAMP_TZ, "TIMESTAMP WITH TIME ZONE");
      INTERVAL_YEAR = new H2Type(TypeInfo.getTypeInfo(22), "INTERVAL_YEAR");
      INTERVAL_MONTH = new H2Type(TypeInfo.getTypeInfo(23), "INTERVAL_MONTH");
      INTERVAL_DAY = new H2Type(TypeInfo.TYPE_INTERVAL_DAY, "INTERVAL_DAY");
      INTERVAL_HOUR = new H2Type(TypeInfo.getTypeInfo(25), "INTERVAL_HOUR");
      INTERVAL_MINUTE = new H2Type(TypeInfo.getTypeInfo(26), "INTERVAL_MINUTE");
      INTERVAL_SECOND = new H2Type(TypeInfo.getTypeInfo(27), "INTERVAL_SECOND");
      INTERVAL_YEAR_TO_MONTH = new H2Type(TypeInfo.TYPE_INTERVAL_YEAR_TO_MONTH, "INTERVAL_YEAR_TO_MONTH");
      INTERVAL_DAY_TO_HOUR = new H2Type(TypeInfo.getTypeInfo(29), "INTERVAL_DAY_TO_HOUR");
      INTERVAL_DAY_TO_MINUTE = new H2Type(TypeInfo.getTypeInfo(30), "INTERVAL_DAY_TO_MINUTE");
      INTERVAL_DAY_TO_SECOND = new H2Type(TypeInfo.TYPE_INTERVAL_DAY_TO_SECOND, "INTERVAL_DAY_TO_SECOND");
      INTERVAL_HOUR_TO_MINUTE = new H2Type(TypeInfo.getTypeInfo(32), "INTERVAL_HOUR_TO_MINUTE");
      INTERVAL_HOUR_TO_SECOND = new H2Type(TypeInfo.TYPE_INTERVAL_HOUR_TO_SECOND, "INTERVAL_HOUR_TO_SECOND");
      INTERVAL_MINUTE_TO_SECOND = new H2Type(TypeInfo.getTypeInfo(34), "INTERVAL_MINUTE_TO_SECOND");
      JAVA_OBJECT = new H2Type(TypeInfo.TYPE_JAVA_OBJECT, "JAVA_OBJECT");
      ENUM = new H2Type(TypeInfo.TYPE_ENUM_UNDEFINED, "ENUM");
      GEOMETRY = new H2Type(TypeInfo.TYPE_GEOMETRY, "GEOMETRY");
      JSON = new H2Type(TypeInfo.TYPE_JSON, "JSON");
      UUID = new H2Type(TypeInfo.TYPE_UUID, "UUID");
   }
}
