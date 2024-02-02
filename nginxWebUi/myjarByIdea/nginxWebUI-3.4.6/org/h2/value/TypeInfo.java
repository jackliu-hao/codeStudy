package org.h2.value;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.h2.api.IntervalQualifier;
import org.h2.message.DbException;

public class TypeInfo extends ExtTypeInfo implements Typed {
   public static final TypeInfo TYPE_UNKNOWN;
   public static final TypeInfo TYPE_NULL;
   public static final TypeInfo TYPE_CHAR;
   public static final TypeInfo TYPE_VARCHAR;
   public static final TypeInfo TYPE_VARCHAR_IGNORECASE;
   public static final TypeInfo TYPE_CLOB;
   public static final TypeInfo TYPE_BINARY;
   public static final TypeInfo TYPE_VARBINARY;
   public static final TypeInfo TYPE_BLOB;
   public static final TypeInfo TYPE_BOOLEAN;
   public static final TypeInfo TYPE_TINYINT;
   public static final TypeInfo TYPE_SMALLINT;
   public static final TypeInfo TYPE_INTEGER;
   public static final TypeInfo TYPE_BIGINT;
   public static final TypeInfo TYPE_NUMERIC_SCALE_0;
   public static final TypeInfo TYPE_NUMERIC_BIGINT;
   public static final TypeInfo TYPE_NUMERIC_FLOATING_POINT;
   public static final TypeInfo TYPE_REAL;
   public static final TypeInfo TYPE_DOUBLE;
   public static final TypeInfo TYPE_DECFLOAT;
   public static final TypeInfo TYPE_DECFLOAT_BIGINT;
   public static final TypeInfo TYPE_DATE;
   public static final TypeInfo TYPE_TIME;
   public static final TypeInfo TYPE_TIME_TZ;
   public static final TypeInfo TYPE_TIMESTAMP;
   public static final TypeInfo TYPE_TIMESTAMP_TZ;
   public static final TypeInfo TYPE_INTERVAL_DAY;
   public static final TypeInfo TYPE_INTERVAL_YEAR_TO_MONTH;
   public static final TypeInfo TYPE_INTERVAL_DAY_TO_SECOND;
   public static final TypeInfo TYPE_INTERVAL_HOUR_TO_SECOND;
   public static final TypeInfo TYPE_JAVA_OBJECT;
   public static final TypeInfo TYPE_ENUM_UNDEFINED;
   public static final TypeInfo TYPE_GEOMETRY;
   public static final TypeInfo TYPE_JSON;
   public static final TypeInfo TYPE_UUID;
   public static final TypeInfo TYPE_ARRAY_UNKNOWN;
   public static final TypeInfo TYPE_ROW_EMPTY;
   private static final TypeInfo[] TYPE_INFOS_BY_VALUE_TYPE;
   private final int valueType;
   private final long precision;
   private final int scale;
   private final ExtTypeInfo extTypeInfo;

   public static TypeInfo getTypeInfo(int var0) {
      if (var0 == -1) {
         throw DbException.get(50004, (String)"?");
      } else {
         if (var0 >= 0 && var0 < 42) {
            TypeInfo var1 = TYPE_INFOS_BY_VALUE_TYPE[var0];
            if (var1 != null) {
               return var1;
            }
         }

         return TYPE_NULL;
      }
   }

   public static TypeInfo getTypeInfo(int var0, long var1, int var3, ExtTypeInfo var4) {
      switch (var0) {
         case -1:
            return TYPE_UNKNOWN;
         case 0:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 17:
         case 39:
            return TYPE_INFOS_BY_VALUE_TYPE[var0];
         case 1:
            if (var1 < 1L) {
               return TYPE_CHAR;
            }

            if (var1 > 1048576L) {
               var1 = 1048576L;
            }

            return new TypeInfo(1, var1);
         case 2:
            if (var1 < 1L || var1 >= 1048576L) {
               if (var1 != 0L) {
                  return TYPE_VARCHAR;
               }

               var1 = 1L;
            }

            return new TypeInfo(2, var1);
         case 3:
            if (var1 < 1L) {
               return TYPE_CLOB;
            }

            return new TypeInfo(3, var1);
         case 4:
            if (var1 < 1L || var1 >= 1048576L) {
               if (var1 != 0L) {
                  return TYPE_VARCHAR_IGNORECASE;
               }

               var1 = 1L;
            }

            return new TypeInfo(4, var1);
         case 5:
            if (var1 < 1L) {
               return TYPE_BINARY;
            }

            if (var1 > 1048576L) {
               var1 = 1048576L;
            }

            return new TypeInfo(5, var1);
         case 6:
            if (var1 < 1L || var1 >= 1048576L) {
               if (var1 != 0L) {
                  return TYPE_VARBINARY;
               }

               var1 = 1L;
            }

            return new TypeInfo(6, var1);
         case 7:
            if (var1 < 1L) {
               return TYPE_BLOB;
            }

            return new TypeInfo(7, var1);
         case 13:
            if (var1 < 1L) {
               var1 = -1L;
            } else if (var1 > 100000L) {
               var1 = 100000L;
            }

            if (var3 < 0) {
               var3 = -1;
            } else if (var3 > 100000) {
               var3 = 100000;
            }

            return new TypeInfo(13, var1, var3, var4 instanceof ExtTypeInfoNumeric ? var4 : null);
         case 14:
            if (var1 >= 1L && var1 <= 24L) {
               return new TypeInfo(14, var1, -1, var4);
            }

            return TYPE_REAL;
         case 15:
            if (var1 != 0L && (var1 < 25L || var1 > 53L)) {
               return TYPE_DOUBLE;
            }

            return new TypeInfo(15, var1, -1, var4);
         case 16:
            if (var1 < 1L) {
               var1 = -1L;
            } else if (var1 >= 100000L) {
               return TYPE_DECFLOAT;
            }

            return new TypeInfo(16, var1, -1, (ExtTypeInfo)null);
         case 18:
            if (var3 < 0) {
               var3 = -1;
            } else if (var3 >= 9) {
               return TYPE_TIME;
            }

            return new TypeInfo(18, var3);
         case 19:
            if (var3 < 0) {
               var3 = -1;
            } else if (var3 >= 9) {
               return TYPE_TIME_TZ;
            }

            return new TypeInfo(19, var3);
         case 20:
            if (var3 < 0) {
               var3 = -1;
            } else if (var3 >= 9) {
               return TYPE_TIMESTAMP;
            }

            return new TypeInfo(20, var3);
         case 21:
            if (var3 < 0) {
               var3 = -1;
            } else if (var3 >= 9) {
               return TYPE_TIMESTAMP_TZ;
            }

            return new TypeInfo(21, var3);
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 32:
            if (var1 < 1L) {
               var1 = -1L;
            } else if (var1 > 18L) {
               var1 = 18L;
            }

            return new TypeInfo(var0, var1);
         case 27:
         case 31:
         case 33:
         case 34:
            if (var1 < 1L) {
               var1 = -1L;
            } else if (var1 > 18L) {
               var1 = 18L;
            }

            if (var3 < 0) {
               var3 = -1;
            } else if (var3 > 9) {
               var3 = 9;
            }

            return new TypeInfo(var0, var1, var3, (ExtTypeInfo)null);
         case 35:
            if (var1 < 1L) {
               return TYPE_JAVA_OBJECT;
            }

            if (var1 > 1048576L) {
               var1 = 1048576L;
            }

            return new TypeInfo(35, var1);
         case 36:
            if (var4 instanceof ExtTypeInfoEnum) {
               return ((ExtTypeInfoEnum)var4).getType();
            }

            return TYPE_ENUM_UNDEFINED;
         case 37:
            if (var4 instanceof ExtTypeInfoGeometry) {
               return new TypeInfo(37, -1L, -1, var4);
            }

            return TYPE_GEOMETRY;
         case 38:
            if (var1 < 1L) {
               return TYPE_JSON;
            }

            if (var1 > 1048576L) {
               var1 = 1048576L;
            }

            return new TypeInfo(38, var1);
         case 40:
            if (!(var4 instanceof TypeInfo)) {
               throw new IllegalArgumentException();
            }

            if (var1 < 0L || var1 >= 65536L) {
               var1 = -1L;
            }

            return new TypeInfo(40, var1, -1, var4);
         case 41:
            if (!(var4 instanceof ExtTypeInfoRow)) {
               throw new IllegalArgumentException();
            }

            return new TypeInfo(41, -1L, -1, var4);
         default:
            return TYPE_NULL;
      }
   }

   public static TypeInfo getHigherType(Typed[] var0) {
      int var1 = var0.length;
      TypeInfo var2;
      if (var1 == 0) {
         var2 = TYPE_NULL;
      } else {
         var2 = var0[0].getType();
         boolean var3 = false;
         boolean var4 = false;
         switch (var2.getValueType()) {
            case -1:
               var3 = true;
               break;
            case 0:
               var4 = true;
         }

         for(int var5 = 1; var5 < var1; ++var5) {
            TypeInfo var6 = var0[var5].getType();
            switch (var6.getValueType()) {
               case -1:
                  var3 = true;
                  break;
               case 0:
                  var4 = true;
                  break;
               default:
                  var2 = getHigherType(var2, var6);
            }
         }

         if (var2.getValueType() <= 0 && var3) {
            throw DbException.get(50004, (String)(var4 ? "NULL, ?" : "?"));
         }
      }

      return var2;
   }

   public static TypeInfo getHigherType(TypeInfo var0, TypeInfo var1) {
      int var2 = var0.getValueType();
      int var3 = var1.getValueType();
      int var4;
      if (var2 == var3) {
         if (var2 == -1) {
            throw DbException.get(50004, (String)"?, ?");
         }

         var4 = var2;
      } else {
         if (var2 < var3) {
            int var5 = var2;
            var2 = var3;
            var3 = var5;
            TypeInfo var6 = var0;
            var0 = var1;
            var1 = var6;
         }

         if (var2 == -1) {
            if (var3 == 0) {
               throw DbException.get(50004, (String)"?, NULL");
            }

            return var1;
         }

         if (var3 == -1) {
            if (var2 == 0) {
               throw DbException.get(50004, (String)"NULL, ?");
            }

            return var0;
         }

         if (var3 == 0) {
            return var0;
         }

         var4 = Value.getHigherOrderKnown(var2, var3);
      }

      long var14;
      switch (var4) {
         case 13:
            var0 = var0.toNumericType();
            var1 = var1.toNumericType();
            long var7 = var0.getPrecision();
            long var9 = var1.getPrecision();
            int var11 = var0.getScale();
            int var12 = var1.getScale();
            int var13;
            if (var11 < var12) {
               var7 += (long)(var12 - var11);
               var13 = var12;
            } else {
               var9 += (long)(var11 - var12);
               var13 = var11;
            }

            return getTypeInfo(13, Math.max(var7, var9), var13, (ExtTypeInfo)null);
         case 14:
         case 15:
            var14 = -1L;
            break;
         case 40:
            return getHigherArray(var0, var1, dimensions(var0), dimensions(var1));
         case 41:
            return getHigherRow(var0, var1);
         default:
            var14 = Math.max(var0.getPrecision(), var1.getPrecision());
      }

      ExtTypeInfo var15 = var0.extTypeInfo;
      return getTypeInfo(var4, var14, Math.max(var0.getScale(), var1.getScale()), var4 == var2 && var15 != null ? var15 : (var4 == var3 ? var1.extTypeInfo : null));
   }

   private static int dimensions(TypeInfo var0) {
      int var1;
      for(var1 = 0; var0.getValueType() == 40; ++var1) {
         var0 = (TypeInfo)var0.extTypeInfo;
      }

      return var1;
   }

   private static TypeInfo getHigherArray(TypeInfo var0, TypeInfo var1, int var2, int var3) {
      long var4;
      if (var2 > var3) {
         --var2;
         var4 = Math.max(var0.getPrecision(), 1L);
         var0 = (TypeInfo)var0.extTypeInfo;
      } else if (var2 < var3) {
         --var3;
         var4 = Math.max(1L, var1.getPrecision());
         var1 = (TypeInfo)var1.extTypeInfo;
      } else {
         if (var2 <= 0) {
            return getHigherType(var0, var1);
         }

         --var2;
         --var3;
         var4 = Math.max(var0.getPrecision(), var1.getPrecision());
         var0 = (TypeInfo)var0.extTypeInfo;
         var1 = (TypeInfo)var1.extTypeInfo;
      }

      return getTypeInfo(40, var4, 0, getHigherArray(var0, var1, var2, var3));
   }

   private static TypeInfo getHigherRow(TypeInfo var0, TypeInfo var1) {
      if (var0.getValueType() != 41) {
         var0 = typeToRow(var0);
      }

      if (var1.getValueType() != 41) {
         var1 = typeToRow(var1);
      }

      ExtTypeInfoRow var2 = (ExtTypeInfoRow)var0.getExtTypeInfo();
      ExtTypeInfoRow var3 = (ExtTypeInfoRow)var1.getExtTypeInfo();
      if (var2.equals(var3)) {
         return var0;
      } else {
         Set var4 = var2.getFields();
         Set var5 = var3.getFields();
         int var6 = var4.size();
         if (var5.size() != var6) {
            throw DbException.get(21002);
         } else {
            LinkedHashMap var7 = new LinkedHashMap((int)Math.ceil((double)var6 / 0.75));
            Iterator var8 = var4.iterator();
            Iterator var9 = var5.iterator();

            while(var8.hasNext()) {
               Map.Entry var10 = (Map.Entry)var8.next();
               var7.put(var10.getKey(), getHigherType((TypeInfo)var10.getValue(), (TypeInfo)((Map.Entry)var9.next()).getValue()));
            }

            return getTypeInfo(41, 0L, 0, new ExtTypeInfoRow(var7));
         }
      }
   }

   private static TypeInfo typeToRow(TypeInfo var0) {
      LinkedHashMap var1 = new LinkedHashMap(2);
      var1.put("C1", var0);
      return getTypeInfo(41, 0L, 0, new ExtTypeInfoRow(var1));
   }

   public static boolean areSameTypes(TypeInfo var0, TypeInfo var1) {
      while(true) {
         int var2 = var0.getValueType();
         if (var2 != var1.getValueType()) {
            return false;
         }

         ExtTypeInfo var3 = var0.getExtTypeInfo();
         ExtTypeInfo var4 = var1.getExtTypeInfo();
         if (var2 != 40) {
            return Objects.equals(var3, var4);
         }

         var0 = (TypeInfo)var3;
         var1 = (TypeInfo)var4;
      }
   }

   public static void checkComparable(TypeInfo var0, TypeInfo var1) {
      if (!areComparable(var0, var1)) {
         throw DbException.get(90110, var0.getTraceSQL(), var1.getTraceSQL());
      }
   }

   private static boolean areComparable(TypeInfo var0, TypeInfo var1) {
      int var2 = (var0 = var0.unwrapRow()).getValueType();
      int var3 = (var1 = var1.unwrapRow()).getValueType();
      if (var2 > var3) {
         int var4 = var2;
         var2 = var3;
         var3 = var4;
         TypeInfo var5 = var0;
         var0 = var1;
         var1 = var5;
      }

      if (var2 <= 0) {
         return true;
      } else if (var2 == var3) {
         switch (var2) {
            case 40:
               return areComparable((TypeInfo)var0.getExtTypeInfo(), (TypeInfo)var1.getExtTypeInfo());
            case 41:
               Set var10 = ((ExtTypeInfoRow)var0.getExtTypeInfo()).getFields();
               Set var12 = ((ExtTypeInfoRow)var1.getExtTypeInfo()).getFields();
               int var6 = var10.size();
               if (var12.size() != var6) {
                  return false;
               } else {
                  Iterator var7 = var10.iterator();
                  Iterator var8 = var12.iterator();

                  while(var7.hasNext()) {
                     if (!areComparable((TypeInfo)((Map.Entry)var7.next()).getValue(), (TypeInfo)((Map.Entry)var8.next()).getValue())) {
                        return false;
                     }
                  }
               }
            default:
               return true;
         }
      } else {
         byte var9 = Value.GROUPS[var2];
         byte var11 = Value.GROUPS[var3];
         if (var9 == var11) {
            switch (var9) {
               case 5:
                  return var2 != 17 || var3 != 18 && var3 != 19;
               case 6:
               case 7:
               default:
                  return true;
               case 8:
               case 9:
                  return false;
            }
         } else {
            switch (var9) {
               case 1:
                  switch (var11) {
                     case 4:
                     case 5:
                     case 6:
                     case 7:
                        return true;
                     case 8:
                        switch (var3) {
                           case 36:
                           case 37:
                           case 38:
                           case 39:
                              return true;
                           default:
                              return false;
                        }
                     default:
                        return false;
                  }
               case 2:
                  switch (var3) {
                     case 35:
                     case 37:
                     case 38:
                     case 39:
                        return true;
                     case 36:
                     default:
                        return false;
                  }
               default:
                  return false;
            }
         }
      }
   }

   public static boolean haveSameOrdering(TypeInfo var0, TypeInfo var1) {
      int var2 = (var0 = var0.unwrapRow()).getValueType();
      int var3 = (var1 = var1.unwrapRow()).getValueType();
      if (var2 > var3) {
         int var4 = var2;
         var2 = var3;
         var3 = var4;
         TypeInfo var5 = var0;
         var0 = var1;
         var1 = var5;
      }

      if (var2 <= 0) {
         return true;
      } else if (var2 == var3) {
         switch (var2) {
            case 40:
               return haveSameOrdering((TypeInfo)var0.getExtTypeInfo(), (TypeInfo)var1.getExtTypeInfo());
            case 41:
               Set var10 = ((ExtTypeInfoRow)var0.getExtTypeInfo()).getFields();
               Set var12 = ((ExtTypeInfoRow)var1.getExtTypeInfo()).getFields();
               int var6 = var10.size();
               if (var12.size() != var6) {
                  return false;
               } else {
                  Iterator var7 = var10.iterator();
                  Iterator var8 = var12.iterator();

                  while(var7.hasNext()) {
                     if (!haveSameOrdering((TypeInfo)((Map.Entry)var7.next()).getValue(), (TypeInfo)((Map.Entry)var8.next()).getValue())) {
                        return false;
                     }
                  }
               }
            default:
               return true;
         }
      } else {
         byte var9 = Value.GROUPS[var2];
         byte var11 = Value.GROUPS[var3];
         if (var9 == var11) {
            switch (var9) {
               case 1:
                  return var2 == 4 == (var3 == 4);
               case 2:
               case 3:
               case 4:
               case 6:
               case 7:
               default:
                  return true;
               case 5:
                  switch (var2) {
                     case 17:
                        return var3 == 20 || var3 == 21;
                     case 18:
                     case 19:
                        return var3 == 18 || var3 == 19;
                     default:
                        return true;
                  }
               case 8:
               case 9:
                  return false;
            }
         } else if (var9 == 2) {
            switch (var3) {
               case 35:
               case 37:
               case 38:
               case 39:
                  return true;
               case 36:
               default:
                  return false;
            }
         } else {
            return false;
         }
      }
   }

   private TypeInfo(int var1) {
      this.valueType = var1;
      this.precision = -1L;
      this.scale = -1;
      this.extTypeInfo = null;
   }

   private TypeInfo(int var1, long var2) {
      this.valueType = var1;
      this.precision = var2;
      this.scale = -1;
      this.extTypeInfo = null;
   }

   private TypeInfo(int var1, int var2) {
      this.valueType = var1;
      this.precision = -1L;
      this.scale = var2;
      this.extTypeInfo = null;
   }

   public TypeInfo(int var1, long var2, int var4, ExtTypeInfo var5) {
      this.valueType = var1;
      this.precision = var2;
      this.scale = var4;
      this.extTypeInfo = var5;
   }

   public TypeInfo getType() {
      return this;
   }

   public int getValueType() {
      return this.valueType;
   }

   public long getPrecision() {
      int var1;
      switch (this.valueType) {
         case -1:
            return -1L;
         case 0:
            return 1L;
         case 1:
         case 5:
            return this.precision >= 0L ? this.precision : 1L;
         case 2:
         case 4:
         case 6:
         case 35:
         case 36:
         case 37:
         case 38:
            return this.precision >= 0L ? this.precision : 1048576L;
         case 3:
         case 7:
            return this.precision >= 0L ? this.precision : Long.MAX_VALUE;
         case 8:
            return 1L;
         case 9:
            return 8L;
         case 10:
            return 16L;
         case 11:
            return 32L;
         case 12:
            return 64L;
         case 13:
            return this.precision >= 0L ? this.precision : 100000L;
         case 14:
            return 24L;
         case 15:
            return 53L;
         case 16:
            return this.precision >= 0L ? this.precision : 100000L;
         case 17:
            return 10L;
         case 18:
            var1 = this.scale >= 0 ? this.scale : 0;
            return var1 == 0 ? 8L : (long)(9 + var1);
         case 19:
            var1 = this.scale >= 0 ? this.scale : 0;
            return var1 == 0 ? 14L : (long)(15 + var1);
         case 20:
            var1 = this.scale >= 0 ? this.scale : 6;
            return var1 == 0 ? 19L : (long)(20 + var1);
         case 21:
            var1 = this.scale >= 0 ? this.scale : 6;
            return var1 == 0 ? 25L : (long)(26 + var1);
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
            return this.precision >= 0L ? this.precision : 2L;
         case 39:
            return 16L;
         case 40:
            return this.precision >= 0L ? this.precision : 65536L;
         case 41:
            return 2147483647L;
         default:
            return this.precision;
      }
   }

   public long getDeclaredPrecision() {
      return this.precision;
   }

   public int getScale() {
      switch (this.valueType) {
         case -1:
            return -1;
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 14:
         case 15:
         case 16:
         case 17:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 32:
         case 35:
         case 36:
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
            return 0;
         case 13:
            return this.scale >= 0 ? this.scale : 0;
         case 18:
         case 19:
            return this.scale >= 0 ? this.scale : 0;
         case 20:
         case 21:
            return this.scale >= 0 ? this.scale : 6;
         case 27:
         case 31:
         case 33:
         case 34:
            return this.scale >= 0 ? this.scale : 6;
         default:
            return this.scale;
      }
   }

   public int getDeclaredScale() {
      return this.scale;
   }

   public int getDisplaySize() {
      int var1;
      switch (this.valueType) {
         case -1:
         default:
            return -1;
         case 0:
            return 4;
         case 1:
            return this.precision >= 0L ? (int)this.precision : 1;
         case 2:
         case 4:
         case 38:
            return this.precision >= 0L ? (int)this.precision : 1048576;
         case 3:
            return this.precision >= 0L && this.precision <= 2147483647L ? (int)this.precision : Integer.MAX_VALUE;
         case 5:
            return this.precision >= 0L ? (int)this.precision * 2 : 2;
         case 6:
         case 35:
            return this.precision >= 0L ? (int)this.precision * 2 : 2097152;
         case 7:
            return this.precision >= 0L && this.precision <= 1073741823L ? (int)this.precision * 2 : Integer.MAX_VALUE;
         case 8:
            return 5;
         case 9:
            return 4;
         case 10:
            return 6;
         case 11:
            return 11;
         case 12:
            return 20;
         case 13:
            return this.precision >= 0L ? (int)this.precision + 2 : 100002;
         case 14:
            return 15;
         case 15:
            return 24;
         case 16:
            return this.precision >= 0L ? (int)this.precision + 12 : 100012;
         case 17:
            return 10;
         case 18:
            var1 = this.scale >= 0 ? this.scale : 0;
            return var1 == 0 ? 8 : 9 + var1;
         case 19:
            var1 = this.scale >= 0 ? this.scale : 0;
            return var1 == 0 ? 14 : 15 + var1;
         case 20:
            var1 = this.scale >= 0 ? this.scale : 6;
            return var1 == 0 ? 19 : 20 + var1;
         case 21:
            var1 = this.scale >= 0 ? this.scale : 6;
            return var1 == 0 ? 25 : 26 + var1;
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
            return ValueInterval.getDisplaySize(this.valueType, this.precision >= 0L ? (int)this.precision : 2, this.scale >= 0 ? this.scale : 6);
         case 36:
            return this.extTypeInfo != null ? (int)this.precision : 1048576;
         case 37:
         case 40:
         case 41:
            return Integer.MAX_VALUE;
         case 39:
            return 36;
      }
   }

   public ExtTypeInfo getExtTypeInfo() {
      return this.extTypeInfo;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      switch (this.valueType) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 35:
         case 38:
            var1.append(Value.getTypeName(this.valueType));
            if (this.precision >= 0L) {
               var1.append('(').append(this.precision).append(')');
            }
            break;
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 17:
         case 39:
         default:
            var1.append(Value.getTypeName(this.valueType));
            break;
         case 13:
            if (this.extTypeInfo != null) {
               this.extTypeInfo.getSQL(var1, var2);
            } else {
               var1.append("NUMERIC");
            }

            boolean var3 = this.precision >= 0L;
            boolean var4 = this.scale >= 0;
            if (var3 || var4) {
               var1.append('(').append(var3 ? this.precision : 100000L);
               if (var4) {
                  var1.append(", ").append(this.scale);
               }

               var1.append(')');
            }
            break;
         case 14:
         case 15:
            if (this.precision < 0L) {
               var1.append(Value.getTypeName(this.valueType));
            } else {
               var1.append("FLOAT");
               if (this.precision > 0L) {
                  var1.append('(').append(this.precision).append(')');
               }
            }
            break;
         case 16:
            var1.append("DECFLOAT");
            if (this.precision >= 0L) {
               var1.append('(').append(this.precision).append(')');
            }
            break;
         case 18:
         case 19:
            var1.append("TIME");
            if (this.scale >= 0) {
               var1.append('(').append(this.scale).append(')');
            }

            if (this.valueType == 19) {
               var1.append(" WITH TIME ZONE");
            }
            break;
         case 20:
         case 21:
            var1.append("TIMESTAMP");
            if (this.scale >= 0) {
               var1.append('(').append(this.scale).append(')');
            }

            if (this.valueType == 21) {
               var1.append(" WITH TIME ZONE");
            }
            break;
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
            IntervalQualifier.valueOf(this.valueType - 22).getTypeName(var1, (int)this.precision, this.scale, false);
            break;
         case 36:
            this.extTypeInfo.getSQL(var1.append("ENUM"), var2);
            break;
         case 37:
            var1.append("GEOMETRY");
            if (this.extTypeInfo != null) {
               this.extTypeInfo.getSQL(var1, var2);
            }
            break;
         case 40:
            if (this.extTypeInfo != null) {
               this.extTypeInfo.getSQL(var1, var2).append(' ');
            }

            var1.append("ARRAY");
            if (this.precision >= 0L) {
               var1.append('[').append(this.precision).append(']');
            }
            break;
         case 41:
            var1.append("ROW");
            if (this.extTypeInfo != null) {
               this.extTypeInfo.getSQL(var1, var2);
            }
      }

      return var1;
   }

   public int hashCode() {
      int var1 = 1;
      var1 = 31 * var1 + this.valueType;
      var1 = 31 * var1 + (int)(this.precision ^ this.precision >>> 32);
      var1 = 31 * var1 + this.scale;
      var1 = 31 * var1 + (this.extTypeInfo == null ? 0 : this.extTypeInfo.hashCode());
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && var1.getClass() == TypeInfo.class) {
         TypeInfo var2 = (TypeInfo)var1;
         return this.valueType == var2.valueType && this.precision == var2.precision && this.scale == var2.scale && Objects.equals(this.extTypeInfo, var2.extTypeInfo);
      } else {
         return false;
      }
   }

   public TypeInfo toNumericType() {
      switch (this.valueType) {
         case 8:
         case 9:
         case 10:
         case 11:
            return getTypeInfo(13, this.getDecimalPrecision(), 0, (ExtTypeInfo)null);
         case 12:
            return TYPE_NUMERIC_BIGINT;
         case 13:
            return this;
         case 14:
            return getTypeInfo(13, 85L, 46, (ExtTypeInfo)null);
         case 15:
            return getTypeInfo(13, 634L, 325, (ExtTypeInfo)null);
         default:
            return TYPE_NUMERIC_FLOATING_POINT;
      }
   }

   public TypeInfo toDecfloatType() {
      switch (this.valueType) {
         case 8:
         case 9:
         case 10:
         case 11:
            return getTypeInfo(16, this.getDecimalPrecision(), 0, (ExtTypeInfo)null);
         case 12:
            return TYPE_DECFLOAT_BIGINT;
         case 13:
            return getTypeInfo(16, this.getPrecision(), 0, (ExtTypeInfo)null);
         case 14:
            return getTypeInfo(16, 7L, 0, (ExtTypeInfo)null);
         case 15:
            return getTypeInfo(16, 7L, 0, (ExtTypeInfo)null);
         case 16:
            return this;
         default:
            return TYPE_DECFLOAT;
      }
   }

   public TypeInfo unwrapRow() {
      if (this.valueType == 41) {
         Set var1 = ((ExtTypeInfoRow)this.extTypeInfo).getFields();
         if (var1.size() == 1) {
            return ((TypeInfo)((Map.Entry)var1.iterator().next()).getValue()).unwrapRow();
         }
      }

      return this;
   }

   public long getDecimalPrecision() {
      switch (this.valueType) {
         case 9:
            return 3L;
         case 10:
            return 5L;
         case 11:
            return 10L;
         case 12:
            return 19L;
         case 13:
         default:
            return this.precision;
         case 14:
            return 7L;
         case 15:
            return 17L;
      }
   }

   public String getDeclaredTypeName() {
      switch (this.valueType) {
         case 13:
            return this.extTypeInfo != null ? "DECIMAL" : "NUMERIC";
         case 14:
         case 15:
            if (this.extTypeInfo != null) {
               return "FLOAT";
            }
         default:
            return Value.getTypeName(this.valueType);
         case 36:
         case 37:
         case 41:
            return this.getSQL(0);
         case 40:
            TypeInfo var1 = (TypeInfo)this.extTypeInfo;
            return var1.getSQL(new StringBuilder(), 0).append(" ARRAY").toString();
      }
   }

   static {
      TypeInfo[] var0 = new TypeInfo[42];
      TYPE_UNKNOWN = new TypeInfo(-1);
      var0[0] = TYPE_NULL = new TypeInfo(0);
      var0[1] = TYPE_CHAR = new TypeInfo(1, -1L);
      var0[2] = TYPE_VARCHAR = new TypeInfo(2);
      var0[3] = TYPE_CLOB = new TypeInfo(3);
      var0[4] = TYPE_VARCHAR_IGNORECASE = new TypeInfo(4);
      var0[5] = TYPE_BINARY = new TypeInfo(5, -1L);
      var0[6] = TYPE_VARBINARY = new TypeInfo(6);
      var0[7] = TYPE_BLOB = new TypeInfo(7);
      var0[8] = TYPE_BOOLEAN = new TypeInfo(8);
      var0[9] = TYPE_TINYINT = new TypeInfo(9);
      var0[10] = TYPE_SMALLINT = new TypeInfo(10);
      var0[11] = TYPE_INTEGER = new TypeInfo(11);
      var0[12] = TYPE_BIGINT = new TypeInfo(12);
      TYPE_NUMERIC_SCALE_0 = new TypeInfo(13, 100000L, 0, (ExtTypeInfo)null);
      TYPE_NUMERIC_BIGINT = new TypeInfo(13, 19L, 0, (ExtTypeInfo)null);
      var0[13] = TYPE_NUMERIC_FLOATING_POINT = new TypeInfo(13, 100000L, 50000, (ExtTypeInfo)null);
      var0[14] = TYPE_REAL = new TypeInfo(14);
      var0[15] = TYPE_DOUBLE = new TypeInfo(15);
      var0[16] = TYPE_DECFLOAT = new TypeInfo(16);
      TYPE_DECFLOAT_BIGINT = new TypeInfo(16, 19L);
      var0[17] = TYPE_DATE = new TypeInfo(17);
      var0[18] = TYPE_TIME = new TypeInfo(18, 9);
      var0[19] = TYPE_TIME_TZ = new TypeInfo(19, 9);
      var0[20] = TYPE_TIMESTAMP = new TypeInfo(20, 9);
      var0[21] = TYPE_TIMESTAMP_TZ = new TypeInfo(21, 9);

      for(int var1 = 22; var1 <= 34; ++var1) {
         var0[var1] = new TypeInfo(var1, 18L, IntervalQualifier.valueOf(var1 - 22).hasSeconds() ? 9 : -1, (ExtTypeInfo)null);
      }

      TYPE_INTERVAL_DAY = var0[24];
      TYPE_INTERVAL_YEAR_TO_MONTH = var0[28];
      TYPE_INTERVAL_DAY_TO_SECOND = var0[31];
      TYPE_INTERVAL_HOUR_TO_SECOND = var0[33];
      var0[35] = TYPE_JAVA_OBJECT = new TypeInfo(35);
      var0[36] = TYPE_ENUM_UNDEFINED = new TypeInfo(36);
      var0[37] = TYPE_GEOMETRY = new TypeInfo(37);
      var0[38] = TYPE_JSON = new TypeInfo(38);
      var0[39] = TYPE_UUID = new TypeInfo(39);
      var0[40] = TYPE_ARRAY_UNKNOWN = new TypeInfo(40);
      var0[41] = TYPE_ROW_EMPTY = new TypeInfo(41, -1L, -1, new ExtTypeInfoRow(new LinkedHashMap()));
      TYPE_INFOS_BY_VALUE_TYPE = var0;
   }
}
