package org.h2.mvstore.type;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.util.Utils;

public class ObjectDataType extends BasicDataType<Object> {
   static final int TYPE_NULL = 0;
   static final int TYPE_BOOLEAN = 1;
   static final int TYPE_BYTE = 2;
   static final int TYPE_SHORT = 3;
   static final int TYPE_INT = 4;
   static final int TYPE_LONG = 5;
   static final int TYPE_BIG_INTEGER = 6;
   static final int TYPE_FLOAT = 7;
   static final int TYPE_DOUBLE = 8;
   static final int TYPE_BIG_DECIMAL = 9;
   static final int TYPE_CHAR = 10;
   static final int TYPE_STRING = 11;
   static final int TYPE_UUID = 12;
   static final int TYPE_DATE = 13;
   static final int TYPE_ARRAY = 14;
   static final int TYPE_SERIALIZED_OBJECT = 19;
   static final int TAG_BOOLEAN_TRUE = 32;
   static final int TAG_INTEGER_NEGATIVE = 33;
   static final int TAG_INTEGER_FIXED = 34;
   static final int TAG_LONG_NEGATIVE = 35;
   static final int TAG_LONG_FIXED = 36;
   static final int TAG_BIG_INTEGER_0 = 37;
   static final int TAG_BIG_INTEGER_1 = 38;
   static final int TAG_BIG_INTEGER_SMALL = 39;
   static final int TAG_FLOAT_0 = 40;
   static final int TAG_FLOAT_1 = 41;
   static final int TAG_FLOAT_FIXED = 42;
   static final int TAG_DOUBLE_0 = 43;
   static final int TAG_DOUBLE_1 = 44;
   static final int TAG_DOUBLE_FIXED = 45;
   static final int TAG_BIG_DECIMAL_0 = 46;
   static final int TAG_BIG_DECIMAL_1 = 47;
   static final int TAG_BIG_DECIMAL_SMALL = 48;
   static final int TAG_BIG_DECIMAL_SMALL_SCALED = 49;
   static final int TAG_INTEGER_0_15 = 64;
   static final int TAG_LONG_0_7 = 80;
   static final int TAG_STRING_0_15 = 88;
   static final int TAG_BYTE_ARRAY_0_15 = 104;
   static final int FLOAT_ZERO_BITS = Float.floatToIntBits(0.0F);
   static final int FLOAT_ONE_BITS = Float.floatToIntBits(1.0F);
   static final long DOUBLE_ZERO_BITS = Double.doubleToLongBits(0.0);
   static final long DOUBLE_ONE_BITS = Double.doubleToLongBits(1.0);
   static final Class<?>[] COMMON_CLASSES;
   private AutoDetectDataType<Object> last = this.selectDataType(0);

   public Object[] createStorage(int var1) {
      return new Object[var1];
   }

   public int compare(Object var1, Object var2) {
      int var3 = getTypeId(var1);
      int var4 = var3 - getTypeId(var2);
      return var4 == 0 ? this.newType(var3).compare(var1, var2) : Integer.signum(var4);
   }

   public int getMemory(Object var1) {
      return this.switchType(var1).getMemory(var1);
   }

   public void write(WriteBuffer var1, Object var2) {
      this.switchType(var2).write(var1, var2);
   }

   private AutoDetectDataType<Object> newType(int var1) {
      return var1 == this.last.typeId ? this.last : this.selectDataType(var1);
   }

   private AutoDetectDataType selectDataType(int var1) {
      switch (var1) {
         case 0:
            return ObjectDataType.NullType.INSTANCE;
         case 1:
            return ObjectDataType.BooleanType.INSTANCE;
         case 2:
            return ObjectDataType.ByteType.INSTANCE;
         case 3:
            return ObjectDataType.ShortType.INSTANCE;
         case 4:
            return ObjectDataType.IntegerType.INSTANCE;
         case 5:
            return ObjectDataType.LongType.INSTANCE;
         case 6:
            return ObjectDataType.BigIntegerType.INSTANCE;
         case 7:
            return ObjectDataType.FloatType.INSTANCE;
         case 8:
            return ObjectDataType.DoubleType.INSTANCE;
         case 9:
            return ObjectDataType.BigDecimalType.INSTANCE;
         case 10:
            return ObjectDataType.CharacterType.INSTANCE;
         case 11:
            return ObjectDataType.StringType.INSTANCE;
         case 12:
            return ObjectDataType.UUIDType.INSTANCE;
         case 13:
            return ObjectDataType.DateType.INSTANCE;
         case 14:
            return new ObjectArrayType();
         case 15:
         case 16:
         case 17:
         case 18:
         default:
            throw DataUtils.newMVStoreException(3, "Unsupported type {0}", var1);
         case 19:
            return new SerializedObjectType(this);
      }
   }

   public Object read(ByteBuffer var1) {
      byte var2 = var1.get();
      byte var3;
      if (var2 <= 19) {
         var3 = var2;
      } else {
         switch (var2) {
            case 32:
               var3 = 1;
               break;
            case 33:
            case 34:
               var3 = 4;
               break;
            case 35:
            case 36:
               var3 = 5;
               break;
            case 37:
            case 38:
            case 39:
               var3 = 6;
               break;
            case 40:
            case 41:
            case 42:
               var3 = 7;
               break;
            case 43:
            case 44:
            case 45:
               var3 = 8;
               break;
            case 46:
            case 47:
            case 48:
            case 49:
               var3 = 9;
               break;
            default:
               if (var2 >= 64 && var2 <= 79) {
                  var3 = 4;
               } else if (var2 >= 88 && var2 <= 103) {
                  var3 = 11;
               } else if (var2 >= 80 && var2 <= 87) {
                  var3 = 5;
               } else {
                  if (var2 < 104 || var2 > 119) {
                     throw DataUtils.newMVStoreException(6, "Unknown tag {0}", Integer.valueOf(var2));
                  }

                  var3 = 14;
               }
         }
      }

      AutoDetectDataType var4 = this.last;
      if (var3 != var4.typeId) {
         this.last = var4 = this.newType(var3);
      }

      return var4.read(var1, var2);
   }

   private static int getTypeId(Object var0) {
      if (var0 instanceof Integer) {
         return 4;
      } else if (var0 instanceof String) {
         return 11;
      } else if (var0 instanceof Long) {
         return 5;
      } else if (var0 instanceof Double) {
         return 8;
      } else if (var0 instanceof Float) {
         return 7;
      } else if (var0 instanceof Boolean) {
         return 1;
      } else if (var0 instanceof UUID) {
         return 12;
      } else if (var0 instanceof Byte) {
         return 2;
      } else if (var0 instanceof Short) {
         return 3;
      } else if (var0 instanceof Character) {
         return 10;
      } else if (var0 == null) {
         return 0;
      } else if (isDate(var0)) {
         return 13;
      } else if (isBigInteger(var0)) {
         return 6;
      } else if (isBigDecimal(var0)) {
         return 9;
      } else {
         return var0.getClass().isArray() ? 14 : 19;
      }
   }

   AutoDetectDataType<Object> switchType(Object var1) {
      int var2 = getTypeId(var1);
      AutoDetectDataType var3 = this.last;
      if (var2 != var3.typeId) {
         this.last = var3 = this.newType(var2);
      }

      return var3;
   }

   static boolean isBigInteger(Object var0) {
      return var0 != null && var0.getClass() == BigInteger.class;
   }

   static boolean isBigDecimal(Object var0) {
      return var0 != null && var0.getClass() == BigDecimal.class;
   }

   static boolean isDate(Object var0) {
      return var0 != null && var0.getClass() == Date.class;
   }

   static boolean isArray(Object var0) {
      return var0 != null && var0.getClass().isArray();
   }

   public static byte[] serialize(Object var0) {
      try {
         ByteArrayOutputStream var1 = new ByteArrayOutputStream();
         ObjectOutputStream var2 = new ObjectOutputStream(var1);
         var2.writeObject(var0);
         return var1.toByteArray();
      } catch (Throwable var3) {
         throw DataUtils.newIllegalArgumentException("Could not serialize {0}", var0, var3);
      }
   }

   public static Object deserialize(byte[] var0) {
      try {
         ByteArrayInputStream var1 = new ByteArrayInputStream(var0);
         ObjectInputStream var2 = new ObjectInputStream(var1);
         return var2.readObject();
      } catch (Throwable var3) {
         throw DataUtils.newIllegalArgumentException("Could not deserialize {0}", Arrays.toString(var0), var3);
      }
   }

   public static int compareNotNull(byte[] var0, byte[] var1) {
      if (var0 == var1) {
         return 0;
      } else {
         int var2 = Math.min(var0.length, var1.length);

         for(int var3 = 0; var3 < var2; ++var3) {
            int var4 = var0[var3] & 255;
            int var5 = var1[var3] & 255;
            if (var4 != var5) {
               return var4 > var5 ? 1 : -1;
            }
         }

         return Integer.signum(var0.length - var1.length);
      }
   }

   static {
      COMMON_CLASSES = new Class[]{Boolean.TYPE, Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Object.class, Boolean.class, Byte.class, Short.class, Character.class, Integer.class, Long.class, BigInteger.class, Float.class, Double.class, BigDecimal.class, String.class, UUID.class, Date.class};
   }

   static class SerializedObjectType extends AutoDetectDataType<Object> {
      private int averageSize = 10000;

      SerializedObjectType(ObjectDataType var1) {
         super(var1, 19);
      }

      public Object[] createStorage(int var1) {
         return new Object[var1];
      }

      public int compare(Object var1, Object var2) {
         if (var1 == var2) {
            return 0;
         } else {
            DataType var3 = this.getType(var1);
            DataType var4 = this.getType(var2);
            if (var3 == this && var4 == this) {
               if (var1 instanceof Comparable && var1.getClass().isAssignableFrom(var2.getClass())) {
                  return ((Comparable)var1).compareTo(var2);
               } else if (var2 instanceof Comparable && var2.getClass().isAssignableFrom(var1.getClass())) {
                  return -((Comparable)var2).compareTo(var1);
               } else {
                  byte[] var5 = ObjectDataType.serialize(var1);
                  byte[] var6 = ObjectDataType.serialize(var2);
                  return ObjectDataType.compareNotNull(var5, var6);
               }
            } else {
               return var3 == var4 ? var3.compare(var1, var2) : super.compare(var1, var2);
            }
         }
      }

      public int getMemory(Object var1) {
         DataType var2 = this.getType(var1);
         return var2 == this ? this.averageSize : var2.getMemory(var1);
      }

      public void write(WriteBuffer var1, Object var2) {
         DataType var3 = this.getType(var2);
         if (var3 != this) {
            var3.write(var1, var2);
         } else {
            byte[] var4 = ObjectDataType.serialize(var2);
            int var5 = var4.length * 2;
            this.averageSize = (int)(((long)var5 + 15L * (long)this.averageSize) / 16L);
            var1.put((byte)19).putVarInt(var4.length).put(var4);
         }
      }

      public Object read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Object read(ByteBuffer var1, int var2) {
         int var3 = DataUtils.readVarInt(var1);
         byte[] var4 = Utils.newBytes(var3);
         int var5 = var4.length * 2;
         this.averageSize = (int)(((long)var5 + 15L * (long)this.averageSize) / 16L);
         var1.get(var4);
         return ObjectDataType.deserialize(var4);
      }
   }

   static class ObjectArrayType extends AutoDetectDataType<Object> {
      private final ObjectDataType elementType = new ObjectDataType();

      ObjectArrayType() {
         super(14);
      }

      public Object[] createStorage(int var1) {
         return new Object[var1];
      }

      public int getMemory(Object var1) {
         if (!ObjectDataType.isArray(var1)) {
            return super.getMemory(var1);
         } else {
            int var2 = 64;
            Class var3 = var1.getClass().getComponentType();
            if (var3.isPrimitive()) {
               int var4 = Array.getLength(var1);
               if (var3 != Boolean.TYPE && var3 != Byte.TYPE) {
                  if (var3 != Character.TYPE && var3 != Short.TYPE) {
                     if (var3 != Integer.TYPE && var3 != Float.TYPE) {
                        if (var3 == Double.TYPE || var3 == Long.TYPE) {
                           var2 += var4 * 8;
                        }
                     } else {
                        var2 += var4 * 4;
                     }
                  } else {
                     var2 += var4 * 2;
                  }
               } else {
                  var2 += var4;
               }
            } else {
               Object[] var8 = (Object[])((Object[])var1);
               int var5 = var8.length;

               for(int var6 = 0; var6 < var5; ++var6) {
                  Object var7 = var8[var6];
                  if (var7 != null) {
                     var2 += this.elementType.getMemory(var7);
                  }
               }
            }

            return var2 * 2;
         }
      }

      public int compare(Object var1, Object var2) {
         if (ObjectDataType.isArray(var1) && ObjectDataType.isArray(var2)) {
            if (var1 == var2) {
               return 0;
            } else {
               Class var3 = var1.getClass().getComponentType();
               Class var4 = var2.getClass().getComponentType();
               if (var3 != var4) {
                  Integer var14 = ObjectDataType.Holder.getCommonClassId(var3);
                  Integer var15 = ObjectDataType.Holder.getCommonClassId(var4);
                  if (var14 != null) {
                     return var15 != null ? var14.compareTo(var15) : -1;
                  } else {
                     return var15 != null ? 1 : var3.getName().compareTo(var4.getName());
                  }
               } else {
                  int var5 = Array.getLength(var1);
                  int var6 = Array.getLength(var2);
                  int var7 = Math.min(var5, var6);
                  int var10;
                  int var11;
                  if (var3.isPrimitive()) {
                     if (var3 == Byte.TYPE) {
                        byte[] var16 = (byte[])((byte[])var1);
                        byte[] var19 = (byte[])((byte[])var2);
                        return ObjectDataType.compareNotNull(var16, var19);
                     }

                     for(int var8 = 0; var8 < var7; ++var8) {
                        int var9;
                        if (var3 == Boolean.TYPE) {
                           var9 = Integer.signum((((boolean[])((boolean[])var1))[var8] ? 1 : 0) - (((boolean[])((boolean[])var2))[var8] ? 1 : 0));
                        } else if (var3 == Character.TYPE) {
                           var9 = Integer.signum(((char[])((char[])var1))[var8] - ((char[])((char[])var2))[var8]);
                        } else if (var3 == Short.TYPE) {
                           var9 = Integer.signum(((short[])((short[])var1))[var8] - ((short[])((short[])var2))[var8]);
                        } else if (var3 == Integer.TYPE) {
                           var10 = ((int[])((int[])var1))[var8];
                           var11 = ((int[])((int[])var2))[var8];
                           var9 = Integer.compare(var10, var11);
                        } else if (var3 == Float.TYPE) {
                           var9 = Float.compare(((float[])((float[])var1))[var8], ((float[])((float[])var2))[var8]);
                        } else if (var3 == Double.TYPE) {
                           var9 = Double.compare(((double[])((double[])var1))[var8], ((double[])((double[])var2))[var8]);
                        } else {
                           long var18 = ((long[])((long[])var1))[var8];
                           long var12 = ((long[])((long[])var2))[var8];
                           var9 = Long.compare(var18, var12);
                        }

                        if (var9 != 0) {
                           return var9;
                        }
                     }
                  } else {
                     Object[] var17 = (Object[])((Object[])var1);
                     Object[] var20 = (Object[])((Object[])var2);

                     for(var10 = 0; var10 < var7; ++var10) {
                        var11 = this.elementType.compare(var17[var10], var20[var10]);
                        if (var11 != 0) {
                           return var11;
                        }
                     }
                  }

                  return Integer.compare(var5, var6);
               }
            }
         } else {
            return super.compare(var1, var2);
         }
      }

      public void write(WriteBuffer var1, Object var2) {
         if (!ObjectDataType.isArray(var2)) {
            super.write(var1, var2);
         } else {
            Class var3 = var2.getClass().getComponentType();
            Integer var4 = ObjectDataType.Holder.getCommonClassId(var3);
            int var6;
            if (var4 != null) {
               if (var3.isPrimitive()) {
                  if (var3 == Byte.TYPE) {
                     byte[] var11 = (byte[])((byte[])var2);
                     var6 = var11.length;
                     if (var6 <= 15) {
                        var1.put((byte)(104 + var6));
                     } else {
                        var1.put((byte)14).put((byte)var4).putVarInt(var6);
                     }

                     var1.put(var11);
                     return;
                  }

                  int var5 = Array.getLength(var2);
                  var1.put((byte)14).put((byte)var4).putVarInt(var5);

                  for(var6 = 0; var6 < var5; ++var6) {
                     if (var3 == Boolean.TYPE) {
                        var1.put((byte)(((boolean[])((boolean[])var2))[var6] ? 1 : 0));
                     } else if (var3 == Character.TYPE) {
                        var1.putChar(((char[])((char[])var2))[var6]);
                     } else if (var3 == Short.TYPE) {
                        var1.putShort(((short[])((short[])var2))[var6]);
                     } else if (var3 == Integer.TYPE) {
                        var1.putInt(((int[])((int[])var2))[var6]);
                     } else if (var3 == Float.TYPE) {
                        var1.putFloat(((float[])((float[])var2))[var6]);
                     } else if (var3 == Double.TYPE) {
                        var1.putDouble(((double[])((double[])var2))[var6]);
                     } else {
                        var1.putLong(((long[])((long[])var2))[var6]);
                     }
                  }

                  return;
               }

               var1.put((byte)14).put((byte)var4);
            } else {
               var1.put((byte)14).put((byte)-1);
               String var12 = var3.getName();
               StringDataType.INSTANCE.write(var1, var12);
            }

            Object[] var13 = (Object[])((Object[])var2);
            var6 = var13.length;
            var1.putVarInt(var6);
            Object[] var7 = var13;
            int var8 = var13.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               Object var10 = var7[var9];
               this.elementType.write(var1, var10);
            }

         }
      }

      public Object read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Object read(ByteBuffer var1, int var2) {
         if (var2 != 14) {
            int var12 = var2 - 104;
            byte[] var11 = Utils.newBytes(var12);
            var1.get(var11);
            return var11;
         } else {
            byte var3 = var1.get();
            Class var4;
            if (var3 == -1) {
               String var6 = StringDataType.INSTANCE.read(var1);

               try {
                  var4 = Class.forName(var6);
               } catch (Exception var10) {
                  throw DataUtils.newMVStoreException(8, "Could not get class {0}", var6, var10);
               }
            } else {
               var4 = ObjectDataType.COMMON_CLASSES[var3];
            }

            int var13 = DataUtils.readVarInt(var1);

            Object var5;
            try {
               var5 = Array.newInstance(var4, var13);
            } catch (Exception var9) {
               throw DataUtils.newMVStoreException(8, "Could not create array of type {0} length {1}", var4, var13, var9);
            }

            if (var4.isPrimitive()) {
               for(int var7 = 0; var7 < var13; ++var7) {
                  if (var4 == Boolean.TYPE) {
                     ((boolean[])((boolean[])var5))[var7] = var1.get() == 1;
                  } else if (var4 == Byte.TYPE) {
                     ((byte[])((byte[])var5))[var7] = var1.get();
                  } else if (var4 == Character.TYPE) {
                     ((char[])((char[])var5))[var7] = var1.getChar();
                  } else if (var4 == Short.TYPE) {
                     ((short[])((short[])var5))[var7] = var1.getShort();
                  } else if (var4 == Integer.TYPE) {
                     ((int[])((int[])var5))[var7] = var1.getInt();
                  } else if (var4 == Float.TYPE) {
                     ((float[])((float[])var5))[var7] = var1.getFloat();
                  } else if (var4 == Double.TYPE) {
                     ((double[])((double[])var5))[var7] = var1.getDouble();
                  } else {
                     ((long[])((long[])var5))[var7] = var1.getLong();
                  }
               }
            } else {
               Object[] var14 = (Object[])((Object[])var5);

               for(int var8 = 0; var8 < var13; ++var8) {
                  var14[var8] = this.elementType.read(var1);
               }
            }

            return var5;
         }
      }
   }

   static class DateType extends AutoDetectDataType<Date> {
      static final DateType INSTANCE = new DateType();

      private DateType() {
         super(13);
      }

      public Date[] createStorage(int var1) {
         return new Date[var1];
      }

      public int getMemory(Date var1) {
         return 40;
      }

      public int compare(Date var1, Date var2) {
         return var1.compareTo(var2);
      }

      public void write(WriteBuffer var1, Date var2) {
         var1.put((byte)13);
         var1.putLong(var2.getTime());
      }

      public Date read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Date read(ByteBuffer var1, int var2) {
         long var3 = var1.getLong();
         return new Date(var3);
      }
   }

   static class UUIDType extends AutoDetectDataType<UUID> {
      static final UUIDType INSTANCE = new UUIDType();

      private UUIDType() {
         super(12);
      }

      public UUID[] createStorage(int var1) {
         return new UUID[var1];
      }

      public int getMemory(UUID var1) {
         return 40;
      }

      public int compare(UUID var1, UUID var2) {
         return var1.compareTo(var2);
      }

      public void write(WriteBuffer var1, UUID var2) {
         var1.put((byte)12);
         var1.putLong(var2.getMostSignificantBits());
         var1.putLong(var2.getLeastSignificantBits());
      }

      public UUID read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public UUID read(ByteBuffer var1, int var2) {
         long var3 = var1.getLong();
         long var5 = var1.getLong();
         return new UUID(var3, var5);
      }
   }

   static class StringType extends AutoDetectDataType<String> {
      static final StringType INSTANCE = new StringType();

      private StringType() {
         super(11);
      }

      public String[] createStorage(int var1) {
         return new String[var1];
      }

      public int getMemory(String var1) {
         return 24 + 2 * var1.length();
      }

      public int compare(String var1, String var2) {
         return var1.compareTo(var2);
      }

      public void write(WriteBuffer var1, String var2) {
         int var3 = var2.length();
         if (var3 <= 15) {
            var1.put((byte)(88 + var3));
         } else {
            var1.put((byte)11).putVarInt(var3);
         }

         var1.putStringData(var2, var3);
      }

      public String read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public String read(ByteBuffer var1, int var2) {
         int var3;
         if (var2 == 11) {
            var3 = DataUtils.readVarInt(var1);
         } else {
            var3 = var2 - 88;
         }

         return DataUtils.readString(var1, var3);
      }
   }

   static class BigDecimalType extends AutoDetectDataType<BigDecimal> {
      static final BigDecimalType INSTANCE = new BigDecimalType();

      private BigDecimalType() {
         super(9);
      }

      public BigDecimal[] createStorage(int var1) {
         return new BigDecimal[var1];
      }

      public int compare(BigDecimal var1, BigDecimal var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(BigDecimal var1) {
         return 150;
      }

      public void write(WriteBuffer var1, BigDecimal var2) {
         if (BigDecimal.ZERO.equals(var2)) {
            var1.put((byte)46);
         } else if (BigDecimal.ONE.equals(var2)) {
            var1.put((byte)47);
         } else {
            int var3 = var2.scale();
            BigInteger var4 = var2.unscaledValue();
            int var5 = var4.bitLength();
            if (var5 < 64) {
               if (var3 == 0) {
                  var1.put((byte)48);
               } else {
                  var1.put((byte)49).putVarInt(var3);
               }

               var1.putVarLong(var4.longValue());
            } else {
               byte[] var6 = var4.toByteArray();
               var1.put((byte)9).putVarInt(var3).putVarInt(var6.length).put(var6);
            }
         }

      }

      public BigDecimal read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public BigDecimal read(ByteBuffer var1, int var2) {
         int var3;
         switch (var2) {
            case 46:
               return BigDecimal.ZERO;
            case 47:
               return BigDecimal.ONE;
            case 48:
               return BigDecimal.valueOf(DataUtils.readVarLong(var1));
            case 49:
               var3 = DataUtils.readVarInt(var1);
               return BigDecimal.valueOf(DataUtils.readVarLong(var1), var3);
            default:
               var3 = DataUtils.readVarInt(var1);
               int var4 = DataUtils.readVarInt(var1);
               byte[] var5 = Utils.newBytes(var4);
               var1.get(var5);
               BigInteger var6 = new BigInteger(var5);
               return new BigDecimal(var6, var3);
         }
      }
   }

   static class BigIntegerType extends AutoDetectDataType<BigInteger> {
      static final BigIntegerType INSTANCE = new BigIntegerType();

      private BigIntegerType() {
         super(6);
      }

      public BigInteger[] createStorage(int var1) {
         return new BigInteger[var1];
      }

      public int compare(BigInteger var1, BigInteger var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(BigInteger var1) {
         return 100;
      }

      public void write(WriteBuffer var1, BigInteger var2) {
         if (BigInteger.ZERO.equals(var2)) {
            var1.put((byte)37);
         } else if (BigInteger.ONE.equals(var2)) {
            var1.put((byte)38);
         } else {
            int var3 = var2.bitLength();
            if (var3 <= 63) {
               var1.put((byte)39).putVarLong(var2.longValue());
            } else {
               byte[] var4 = var2.toByteArray();
               var1.put((byte)6).putVarInt(var4.length).put(var4);
            }
         }

      }

      public BigInteger read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public BigInteger read(ByteBuffer var1, int var2) {
         switch (var2) {
            case 37:
               return BigInteger.ZERO;
            case 38:
               return BigInteger.ONE;
            case 39:
               return BigInteger.valueOf(DataUtils.readVarLong(var1));
            default:
               int var3 = DataUtils.readVarInt(var1);
               byte[] var4 = Utils.newBytes(var3);
               var1.get(var4);
               return new BigInteger(var4);
         }
      }
   }

   static class DoubleType extends AutoDetectDataType<Double> {
      static final DoubleType INSTANCE = new DoubleType();

      private DoubleType() {
         super(8);
      }

      public Double[] createStorage(int var1) {
         return new Double[var1];
      }

      public int compare(Double var1, Double var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Double var1) {
         return 30;
      }

      public void write(WriteBuffer var1, Double var2) {
         double var3 = var2;
         long var5 = Double.doubleToLongBits(var3);
         if (var5 == ObjectDataType.DOUBLE_ZERO_BITS) {
            var1.put((byte)43);
         } else if (var5 == ObjectDataType.DOUBLE_ONE_BITS) {
            var1.put((byte)44);
         } else {
            long var7 = Long.reverse(var5);
            if (var7 >= 0L && var7 <= 562949953421311L) {
               var1.put((byte)8);
               var1.putVarLong(var7);
            } else {
               var1.put((byte)45);
               var1.putDouble(var3);
            }
         }

      }

      public Double read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Double read(ByteBuffer var1, int var2) {
         switch (var2) {
            case 43:
               return 0.0;
            case 44:
               return 1.0;
            case 45:
               return var1.getDouble();
            default:
               return Double.longBitsToDouble(Long.reverse(DataUtils.readVarLong(var1)));
         }
      }
   }

   static class FloatType extends AutoDetectDataType<Float> {
      static final FloatType INSTANCE = new FloatType();

      private FloatType() {
         super(7);
      }

      public Float[] createStorage(int var1) {
         return new Float[var1];
      }

      public int compare(Float var1, Float var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Float var1) {
         return 24;
      }

      public void write(WriteBuffer var1, Float var2) {
         float var3 = var2;
         int var4 = Float.floatToIntBits(var3);
         if (var4 == ObjectDataType.FLOAT_ZERO_BITS) {
            var1.put((byte)40);
         } else if (var4 == ObjectDataType.FLOAT_ONE_BITS) {
            var1.put((byte)41);
         } else {
            int var5 = Integer.reverse(var4);
            if (var5 >= 0 && var5 <= 2097151) {
               var1.put((byte)7).putVarInt(var5);
            } else {
               var1.put((byte)42).putFloat(var3);
            }
         }

      }

      public Float read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Float read(ByteBuffer var1, int var2) {
         switch (var2) {
            case 40:
               return 0.0F;
            case 41:
               return 1.0F;
            case 42:
               return var1.getFloat();
            default:
               return Float.intBitsToFloat(Integer.reverse(DataUtils.readVarInt(var1)));
         }
      }
   }

   static class LongType extends AutoDetectDataType<Long> {
      static final LongType INSTANCE = new LongType();

      private LongType() {
         super(5);
      }

      public Long[] createStorage(int var1) {
         return new Long[var1];
      }

      public int compare(Long var1, Long var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Long var1) {
         return 30;
      }

      public void write(WriteBuffer var1, Long var2) {
         long var3 = var2;
         if (var3 < 0L) {
            if (-var3 >= 0L && -var3 <= 562949953421311L) {
               var1.put((byte)35);
               var1.putVarLong(-var3);
            } else {
               var1.put((byte)36);
               var1.putLong(var3);
            }
         } else if (var3 <= 7L) {
            var1.put((byte)((int)(80L + var3)));
         } else if (var3 <= 562949953421311L) {
            var1.put((byte)5);
            var1.putVarLong(var3);
         } else {
            var1.put((byte)36);
            var1.putLong(var3);
         }

      }

      public Long read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Long read(ByteBuffer var1, int var2) {
         switch (var2) {
            case 5:
               return DataUtils.readVarLong(var1);
            case 35:
               return -DataUtils.readVarLong(var1);
            case 36:
               return var1.getLong();
            default:
               return (long)(var2 - 80);
         }
      }
   }

   static class IntegerType extends AutoDetectDataType<Integer> {
      static final IntegerType INSTANCE = new IntegerType();

      private IntegerType() {
         super(4);
      }

      public Integer[] createStorage(int var1) {
         return new Integer[var1];
      }

      public int compare(Integer var1, Integer var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Integer var1) {
         return 24;
      }

      public void write(WriteBuffer var1, Integer var2) {
         int var3 = var2;
         if (var3 < 0) {
            if (-var3 >= 0 && -var3 <= 2097151) {
               var1.put((byte)33).putVarInt(-var3);
            } else {
               var1.put((byte)34).putInt(var3);
            }
         } else if (var3 <= 15) {
            var1.put((byte)(64 + var3));
         } else if (var3 <= 2097151) {
            var1.put((byte)4).putVarInt(var3);
         } else {
            var1.put((byte)34).putInt(var3);
         }

      }

      public Integer read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Integer read(ByteBuffer var1, int var2) {
         switch (var2) {
            case 4:
               return DataUtils.readVarInt(var1);
            case 33:
               return -DataUtils.readVarInt(var1);
            case 34:
               return var1.getInt();
            default:
               return var2 - 64;
         }
      }
   }

   static class ShortType extends AutoDetectDataType<Short> {
      static final ShortType INSTANCE = new ShortType();

      private ShortType() {
         super(3);
      }

      public Short[] createStorage(int var1) {
         return new Short[var1];
      }

      public int compare(Short var1, Short var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Short var1) {
         return 24;
      }

      public void write(WriteBuffer var1, Short var2) {
         var1.put((byte)3);
         var1.putShort(var2);
      }

      public Short read(ByteBuffer var1) {
         return this.read(var1, var1.get());
      }

      public Short read(ByteBuffer var1, int var2) {
         return var1.getShort();
      }
   }

   static class CharacterType extends AutoDetectDataType<Character> {
      static final CharacterType INSTANCE = new CharacterType();

      private CharacterType() {
         super(10);
      }

      public Character[] createStorage(int var1) {
         return new Character[var1];
      }

      public int compare(Character var1, Character var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Character var1) {
         return 24;
      }

      public void write(WriteBuffer var1, Character var2) {
         var1.put((byte)10);
         var1.putChar(var2);
      }

      public Character read(ByteBuffer var1) {
         return var1.getChar();
      }

      public Character read(ByteBuffer var1, int var2) {
         return var1.getChar();
      }
   }

   static class ByteType extends AutoDetectDataType<Byte> {
      static final ByteType INSTANCE = new ByteType();

      private ByteType() {
         super(2);
      }

      public Byte[] createStorage(int var1) {
         return new Byte[var1];
      }

      public int compare(Byte var1, Byte var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Byte var1) {
         return 1;
      }

      public void write(WriteBuffer var1, Byte var2) {
         var1.put((byte)2);
         var1.put(var2);
      }

      public Byte read(ByteBuffer var1) {
         return var1.get();
      }

      public Object read(ByteBuffer var1, int var2) {
         return var1.get();
      }
   }

   static class BooleanType extends AutoDetectDataType<Boolean> {
      static final BooleanType INSTANCE = new BooleanType();

      private BooleanType() {
         super(1);
      }

      public Boolean[] createStorage(int var1) {
         return new Boolean[var1];
      }

      public int compare(Boolean var1, Boolean var2) {
         return var1.compareTo(var2);
      }

      public int getMemory(Boolean var1) {
         return 0;
      }

      public void write(WriteBuffer var1, Boolean var2) {
         int var3 = var2 ? 32 : 1;
         var1.put((byte)var3);
      }

      public Boolean read(ByteBuffer var1) {
         return var1.get() == 32 ? Boolean.TRUE : Boolean.FALSE;
      }

      public Boolean read(ByteBuffer var1, int var2) {
         return var2 == 1 ? Boolean.FALSE : Boolean.TRUE;
      }
   }

   static class NullType extends AutoDetectDataType<Object> {
      static final NullType INSTANCE = new NullType();

      private NullType() {
         super(0);
      }

      public Object[] createStorage(int var1) {
         return null;
      }

      public int compare(Object var1, Object var2) {
         return 0;
      }

      public int getMemory(Object var1) {
         return 0;
      }

      public void write(WriteBuffer var1, Object var2) {
         var1.put((byte)0);
      }

      public Object read(ByteBuffer var1) {
         return null;
      }

      public Object read(ByteBuffer var1, int var2) {
         return null;
      }
   }

   abstract static class AutoDetectDataType<T> extends BasicDataType<T> {
      private final ObjectDataType base;
      final int typeId;

      AutoDetectDataType(int var1) {
         this.base = null;
         this.typeId = var1;
      }

      AutoDetectDataType(ObjectDataType var1, int var2) {
         this.base = var1;
         this.typeId = var2;
      }

      public int getMemory(T var1) {
         return this.getType(var1).getMemory(var1);
      }

      public void write(WriteBuffer var1, T var2) {
         this.getType(var2).write(var1, var2);
      }

      DataType<Object> getType(Object var1) {
         return this.base.switchType(var1);
      }

      abstract Object read(ByteBuffer var1, int var2);
   }

   private static class Holder {
      private static final HashMap<Class<?>, Integer> COMMON_CLASSES_MAP = new HashMap(32);

      static Integer getCommonClassId(Class<?> var0) {
         return (Integer)COMMON_CLASSES_MAP.get(var0);
      }

      static {
         int var0 = 0;

         for(int var1 = ObjectDataType.COMMON_CLASSES.length; var0 < var1; ++var0) {
            COMMON_CLASSES_MAP.put(ObjectDataType.COMMON_CLASSES[var0], var0);
         }

      }
   }
}
