package org.h2.value;

import java.sql.JDBCType;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.h2.api.H2Type;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Mode;
import org.h2.message.DbException;
import org.h2.util.StringUtils;

public class DataType {
   private static final HashMap<String, DataType> TYPES_BY_NAME = new HashMap(128);
   static final DataType[] TYPES_BY_VALUE_TYPE = new DataType[42];
   public int type;
   public int sqlType;
   public long minPrecision;
   public long maxPrecision;
   public int minScale;
   public int maxScale;
   public String prefix;
   public String suffix;
   public String params;
   public boolean caseSensitive;
   public boolean supportsPrecision;
   public boolean supportsScale;
   public long defaultPrecision;
   public int defaultScale;
   public boolean specialPrecisionScale;

   private static void addInterval(int var0) {
      IntervalQualifier var1 = IntervalQualifier.valueOf(var0 - 22);
      String var2 = var1.toString();
      DataType var3 = new DataType();
      var3.prefix = "INTERVAL '";
      var3.suffix = "' " + var2;
      var3.supportsPrecision = true;
      var3.defaultPrecision = 2L;
      var3.minPrecision = 1L;
      var3.maxPrecision = 18L;
      if (var1.hasSeconds()) {
         var3.supportsScale = true;
         var3.defaultScale = 6;
         var3.maxScale = 9;
         var3.params = "PRECISION,SCALE";
      } else {
         var3.params = "PRECISION";
      }

      add(var0, 1111, var3, ("INTERVAL " + var2).intern());
   }

   private static void add(int var0, int var1, DataType var2, String... var3) {
      var2.type = var0;
      var2.sqlType = var1;
      if (TYPES_BY_VALUE_TYPE[var0] == null) {
         TYPES_BY_VALUE_TYPE[var0] = var2;
      }

      String[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         TYPES_BY_NAME.put(var7, var2);
      }

   }

   public static DataType createNumeric(int var0, int var1) {
      DataType var2 = new DataType();
      var2.defaultPrecision = var2.maxPrecision = var2.minPrecision = (long)var0;
      var2.defaultScale = var2.maxScale = var2.minScale = var1;
      return var2;
   }

   public static DataType createDate(int var0, int var1, String var2, boolean var3, int var4, int var5) {
      DataType var6 = new DataType();
      var6.prefix = var2 + " '";
      var6.suffix = "'";
      var6.maxPrecision = (long)var0;
      var6.defaultPrecision = var6.minPrecision = (long)var1;
      if (var3) {
         var6.params = "SCALE";
         var6.supportsScale = true;
         var6.maxScale = var5;
         var6.defaultScale = var4;
      }

      return var6;
   }

   private static DataType createString(boolean var0, boolean var1) {
      return createString(var0, var1, "'", "'");
   }

   private static DataType createBinary(boolean var0) {
      return createString(false, var0, "X'", "'");
   }

   private static DataType createString(boolean var0, boolean var1, String var2, String var3) {
      DataType var4 = new DataType();
      var4.prefix = var2;
      var4.suffix = var3;
      var4.params = "LENGTH";
      var4.caseSensitive = var0;
      var4.supportsPrecision = true;
      var4.minPrecision = 1L;
      var4.maxPrecision = 1048576L;
      var4.defaultPrecision = var1 ? 1L : 1048576L;
      return var4;
   }

   private static DataType createLob(boolean var0) {
      DataType var1 = var0 ? createString(true, false) : createBinary(false);
      var1.maxPrecision = Long.MAX_VALUE;
      var1.defaultPrecision = Long.MAX_VALUE;
      return var1;
   }

   private static DataType createGeometry() {
      DataType var0 = new DataType();
      var0.prefix = "'";
      var0.suffix = "'";
      var0.params = "TYPE,SRID";
      var0.maxPrecision = Long.MAX_VALUE;
      var0.defaultPrecision = Long.MAX_VALUE;
      return var0;
   }

   public static DataType getDataType(int var0) {
      if (var0 == -1) {
         throw DbException.get(50004, (String)"?");
      } else {
         return var0 >= 0 && var0 < 42 ? TYPES_BY_VALUE_TYPE[var0] : TYPES_BY_VALUE_TYPE[0];
      }
   }

   public static int convertTypeToSQLType(TypeInfo var0) {
      int var1 = var0.getValueType();
      switch (var1) {
         case 13:
            return var0.getExtTypeInfo() != null ? 3 : 2;
         case 14:
         case 15:
            if (var0.getDeclaredPrecision() >= 0L) {
               return 6;
            }
         default:
            return getDataType(var1).sqlType;
      }
   }

   public static int convertSQLTypeToValueType(int var0, String var1) {
      switch (var0) {
         case -2:
            if (var1.equalsIgnoreCase("UUID")) {
               return 39;
            }
            break;
         case 1111:
            DataType var2 = (DataType)TYPES_BY_NAME.get(StringUtils.toUpperEnglish(var1));
            if (var2 != null) {
               return var2.type;
            }
      }

      return convertSQLTypeToValueType(var0);
   }

   public static int getValueTypeFromResultSet(ResultSetMetaData var0, int var1) throws SQLException {
      return convertSQLTypeToValueType(var0.getColumnType(var1), var0.getColumnTypeName(var1));
   }

   public static boolean isBinaryColumn(ResultSetMetaData var0, int var1) throws SQLException {
      switch (var0.getColumnType(var1)) {
         case -4:
         case -3:
         case 2000:
         case 2004:
            break;
         case -2:
            if (!var0.getColumnTypeName(var1).equals("UUID")) {
               break;
            }
         default:
            return false;
      }

      return true;
   }

   public static int convertSQLTypeToValueType(SQLType var0) {
      if (var0 instanceof H2Type) {
         return var0.getVendorTypeNumber();
      } else if (var0 instanceof JDBCType) {
         return convertSQLTypeToValueType(var0.getVendorTypeNumber());
      } else {
         throw DbException.get(50004, (String)(var0 == null ? "<null>" : unknownSqlTypeToString(new StringBuilder(), var0).toString()));
      }
   }

   public static int convertSQLTypeToValueType(int var0) {
      switch (var0) {
         case -16:
         case -9:
         case -1:
         case 12:
            return 2;
         case -15:
         case 1:
            return 1;
         case -7:
         case 16:
            return 8;
         case -6:
            return 9;
         case -5:
            return 12;
         case -4:
         case -3:
            return 6;
         case -2:
            return 5;
         case 0:
            return 0;
         case 2:
         case 3:
            return 13;
         case 4:
            return 11;
         case 5:
            return 10;
         case 6:
         case 8:
            return 15;
         case 7:
            return 14;
         case 91:
            return 17;
         case 92:
            return 18;
         case 93:
            return 20;
         case 1111:
            return -1;
         case 2000:
            return 35;
         case 2003:
            return 40;
         case 2004:
            return 7;
         case 2005:
         case 2011:
            return 3;
         case 2013:
            return 19;
         case 2014:
            return 21;
         default:
            throw DbException.get(50004, (String)Integer.toString(var0));
      }
   }

   public static String sqlTypeToString(SQLType var0) {
      if (var0 == null) {
         return "null";
      } else if (var0 instanceof JDBCType) {
         return "JDBCType." + var0.getName();
      } else {
         return var0 instanceof H2Type ? var0.toString() : unknownSqlTypeToString(new StringBuilder("/* "), var0).append(" */ null").toString();
      }
   }

   private static StringBuilder unknownSqlTypeToString(StringBuilder var0, SQLType var1) {
      return var0.append(StringUtils.quoteJavaString(var1.getVendor())).append('/').append(StringUtils.quoteJavaString(var1.getName())).append(" [").append(var1.getVendorTypeNumber()).append(']');
   }

   public static DataType getTypeByName(String var0, Mode var1) {
      DataType var2 = (DataType)var1.typeByNameMap.get(var0);
      if (var2 == null) {
         var2 = (DataType)TYPES_BY_NAME.get(var0);
      }

      return var2;
   }

   public static boolean isIndexable(TypeInfo var0) {
      switch (var0.getValueType()) {
         case -1:
         case 0:
         case 3:
         case 7:
            return false;
         case 40:
            return isIndexable((TypeInfo)var0.getExtTypeInfo());
         case 41:
            ExtTypeInfoRow var1 = (ExtTypeInfoRow)var0.getExtTypeInfo();
            Iterator var2 = var1.getFields().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               if (!isIndexable((TypeInfo)var3.getValue())) {
                  return false;
               }
            }
         default:
            return true;
      }
   }

   public static boolean areStableComparable(TypeInfo var0, TypeInfo var1) {
      int var2 = var0.getValueType();
      int var3 = var1.getValueType();
      switch (var2) {
         case -1:
         case 0:
         case 3:
         case 7:
         case 41:
            return false;
         case 1:
         case 2:
         case 4:
         case 5:
         case 6:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         default:
            switch (var3) {
               case -1:
               case 0:
               case 3:
               case 7:
               case 41:
                  return false;
               default:
                  return true;
            }
         case 17:
         case 20:
            return var3 == 17 || var3 == 20;
         case 18:
         case 19:
         case 21:
            return var2 == var3;
         case 40:
            if (var3 == 40) {
               return areStableComparable((TypeInfo)var0.getExtTypeInfo(), (TypeInfo)var1.getExtTypeInfo());
            } else {
               return false;
            }
      }
   }

   public static boolean isDateTimeType(int var0) {
      return var0 >= 17 && var0 <= 21;
   }

   public static boolean isIntervalType(int var0) {
      return var0 >= 22 && var0 <= 34;
   }

   public static boolean isYearMonthIntervalType(int var0) {
      return var0 == 22 || var0 == 23 || var0 == 28;
   }

   public static boolean isLargeObject(int var0) {
      return var0 == 7 || var0 == 3;
   }

   public static boolean isNumericType(int var0) {
      return var0 >= 9 && var0 <= 16;
   }

   public static boolean isBinaryStringType(int var0) {
      return var0 >= 5 && var0 <= 7;
   }

   public static boolean isCharacterStringType(int var0) {
      return var0 >= 1 && var0 <= 4;
   }

   public static boolean isStringType(int var0) {
      return var0 == 2 || var0 == 1 || var0 == 4;
   }

   public static boolean isBinaryStringOrSpecialBinaryType(int var0) {
      switch (var0) {
         case 5:
         case 6:
         case 7:
         case 35:
         case 37:
         case 38:
         case 39:
            return true;
         default:
            return false;
      }
   }

   public static boolean hasTotalOrdering(int var0) {
      switch (var0) {
         case 5:
         case 6:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 14:
         case 15:
         case 17:
         case 18:
         case 20:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 36:
         case 37:
         case 39:
            return true;
         case 7:
         case 13:
         case 16:
         case 19:
         case 21:
         case 38:
         default:
            return false;
      }
   }

   public static long addPrecision(long var0, long var2) {
      long var4 = var0 + var2;
      return (var0 | var2 | var4) < 0L ? Long.MAX_VALUE : var4;
   }

   public static Object getDefaultForPrimitiveType(Class<?> var0) {
      if (var0 == Boolean.TYPE) {
         return Boolean.FALSE;
      } else if (var0 == Byte.TYPE) {
         return 0;
      } else if (var0 == Character.TYPE) {
         return '\u0000';
      } else if (var0 == Short.TYPE) {
         return Short.valueOf((short)0);
      } else if (var0 == Integer.TYPE) {
         return 0;
      } else if (var0 == Long.TYPE) {
         return 0L;
      } else if (var0 == Float.TYPE) {
         return 0.0F;
      } else if (var0 == Double.TYPE) {
         return 0.0;
      } else {
         throw DbException.getInternalError("primitive=" + var0.toString());
      }
   }

   static {
      DataType var0 = new DataType();
      var0.defaultPrecision = var0.maxPrecision = var0.minPrecision = 1L;
      add(0, 0, var0, "NULL");
      add(1, 1, createString(true, true), "CHARACTER", "CHAR", "NCHAR", "NATIONAL CHARACTER", "NATIONAL CHAR");
      add(2, 12, createString(true, false), "CHARACTER VARYING", "VARCHAR", "CHAR VARYING", "NCHAR VARYING", "NATIONAL CHARACTER VARYING", "NATIONAL CHAR VARYING", "VARCHAR2", "NVARCHAR", "NVARCHAR2", "VARCHAR_CASESENSITIVE", "TID", "LONGVARCHAR", "LONGNVARCHAR");
      add(3, 2005, createLob(true), "CHARACTER LARGE OBJECT", "CLOB", "CHAR LARGE OBJECT", "TINYTEXT", "TEXT", "MEDIUMTEXT", "LONGTEXT", "NTEXT", "NCLOB", "NCHAR LARGE OBJECT", "NATIONAL CHARACTER LARGE OBJECT");
      add(4, 12, createString(false, false), "VARCHAR_IGNORECASE");
      add(5, -2, createBinary(true), "BINARY");
      add(6, -3, createBinary(false), "BINARY VARYING", "VARBINARY", "RAW", "BYTEA", "LONG RAW", "LONGVARBINARY");
      add(7, 2004, createLob(false), "BINARY LARGE OBJECT", "BLOB", "TINYBLOB", "MEDIUMBLOB", "LONGBLOB", "IMAGE");
      add(8, 16, createNumeric(1, 0), "BOOLEAN", "BIT", "BOOL");
      add(9, -6, createNumeric(8, 0), "TINYINT");
      add(10, 5, createNumeric(16, 0), "SMALLINT", "INT2");
      add(11, 4, createNumeric(32, 0), "INTEGER", "INT", "MEDIUMINT", "INT4", "SIGNED");
      add(12, -5, createNumeric(64, 0), "BIGINT", "INT8", "LONG");
      var0 = new DataType();
      var0.minPrecision = 1L;
      var0.defaultPrecision = var0.maxPrecision = 100000L;
      var0.defaultScale = 0;
      var0.maxScale = 100000;
      var0.minScale = 0;
      var0.params = "PRECISION,SCALE";
      var0.supportsPrecision = true;
      var0.supportsScale = true;
      add(13, 2, var0, "NUMERIC", "DECIMAL", "DEC");
      add(14, 7, createNumeric(24, 0), "REAL", "FLOAT4");
      add(15, 8, createNumeric(53, 0), "DOUBLE PRECISION", "DOUBLE", "FLOAT8");
      add(15, 6, createNumeric(53, 0), "FLOAT");
      var0 = new DataType();
      var0.minPrecision = 1L;
      var0.defaultPrecision = var0.maxPrecision = 100000L;
      var0.params = "PRECISION";
      var0.supportsPrecision = true;
      add(16, 2, var0, "DECFLOAT");
      add(17, 91, createDate(10, 10, "DATE", false, 0, 0), "DATE");
      add(18, 92, createDate(18, 8, "TIME", true, 0, 9), "TIME", "TIME WITHOUT TIME ZONE");
      add(19, 2013, createDate(24, 14, "TIME WITH TIME ZONE", true, 0, 9), "TIME WITH TIME ZONE");
      add(20, 93, createDate(29, 26, "TIMESTAMP", true, 6, 9), "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE", "DATETIME", "DATETIME2", "SMALLDATETIME");
      add(21, 2014, createDate(35, 32, "TIMESTAMP WITH TIME ZONE", true, 6, 9), "TIMESTAMP WITH TIME ZONE");

      for(int var1 = 22; var1 <= 34; ++var1) {
         addInterval(var1);
      }

      add(35, 2000, createBinary(false), "JAVA_OBJECT", "OBJECT", "OTHER");
      var0 = createString(false, false);
      var0.supportsPrecision = false;
      var0.params = "ELEMENT [,...]";
      add(36, 1111, var0, "ENUM");
      add(37, 1111, createGeometry(), "GEOMETRY");
      add(38, 1111, createString(true, false, "JSON '", "'"), "JSON");
      var0 = new DataType();
      var0.prefix = var0.suffix = "'";
      var0.defaultPrecision = var0.maxPrecision = var0.minPrecision = 16L;
      add(39, -2, var0, "UUID");
      var0 = new DataType();
      var0.prefix = "ARRAY[";
      var0.suffix = "]";
      var0.params = "CARDINALITY";
      var0.supportsPrecision = true;
      var0.defaultPrecision = var0.maxPrecision = 65536L;
      add(40, 2003, var0, "ARRAY");
      var0 = new DataType();
      var0.prefix = "ROW(";
      var0.suffix = ")";
      var0.params = "NAME DATA_TYPE [,...]";
      add(41, 1111, var0, "ROW");
   }
}
