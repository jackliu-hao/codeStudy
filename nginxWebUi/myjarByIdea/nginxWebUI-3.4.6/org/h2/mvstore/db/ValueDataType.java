package org.h2.mvstore.db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.h2.api.IntervalQualifier;
import org.h2.engine.CastDataProvider;
import org.h2.engine.Database;
import org.h2.message.DbException;
import org.h2.mode.DefaultNullOrdering;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StatefulDataType;
import org.h2.result.RowFactory;
import org.h2.result.SearchRow;
import org.h2.store.DataHandler;
import org.h2.util.Utils;
import org.h2.value.CompareMode;
import org.h2.value.ExtTypeInfoEnum;
import org.h2.value.ExtTypeInfoRow;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueBinary;
import org.h2.value.ValueBlob;
import org.h2.value.ValueBoolean;
import org.h2.value.ValueChar;
import org.h2.value.ValueClob;
import org.h2.value.ValueCollectionBase;
import org.h2.value.ValueDate;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueDouble;
import org.h2.value.ValueGeometry;
import org.h2.value.ValueInteger;
import org.h2.value.ValueInterval;
import org.h2.value.ValueJavaObject;
import org.h2.value.ValueJson;
import org.h2.value.ValueNull;
import org.h2.value.ValueNumeric;
import org.h2.value.ValueReal;
import org.h2.value.ValueRow;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueTinyint;
import org.h2.value.ValueUuid;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;
import org.h2.value.ValueVarcharIgnoreCase;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataInMemory;

public final class ValueDataType extends BasicDataType<Value> implements StatefulDataType<Database> {
   private static final byte NULL = 0;
   private static final byte TINYINT = 2;
   private static final byte SMALLINT = 3;
   private static final byte INTEGER = 4;
   private static final byte BIGINT = 5;
   private static final byte NUMERIC = 6;
   private static final byte DOUBLE = 7;
   private static final byte REAL = 8;
   private static final byte TIME = 9;
   private static final byte DATE = 10;
   private static final byte TIMESTAMP = 11;
   private static final byte VARBINARY = 12;
   private static final byte VARCHAR = 13;
   private static final byte VARCHAR_IGNORECASE = 14;
   private static final byte BLOB = 15;
   private static final byte CLOB = 16;
   private static final byte ARRAY = 17;
   private static final byte JAVA_OBJECT = 19;
   private static final byte UUID = 20;
   private static final byte CHAR = 21;
   private static final byte GEOMETRY = 22;
   private static final byte TIMESTAMP_TZ_OLD = 24;
   private static final byte ENUM = 25;
   private static final byte INTERVAL = 26;
   private static final byte ROW = 27;
   private static final byte INT_0_15 = 32;
   private static final byte BIGINT_0_7 = 48;
   private static final byte NUMERIC_0_1 = 56;
   private static final byte NUMERIC_SMALL_0 = 58;
   private static final byte NUMERIC_SMALL = 59;
   private static final byte DOUBLE_0_1 = 60;
   private static final byte REAL_0_1 = 62;
   private static final byte BOOLEAN_FALSE = 64;
   private static final byte BOOLEAN_TRUE = 65;
   private static final byte INT_NEG = 66;
   private static final byte BIGINT_NEG = 67;
   private static final byte VARCHAR_0_31 = 68;
   private static final int VARBINARY_0_31 = 100;
   private static final int JSON = 134;
   private static final int TIMESTAMP_TZ = 135;
   private static final int TIME_TZ = 136;
   private static final int BINARY = 137;
   private static final int DECFLOAT = 138;
   final DataHandler handler;
   final CastDataProvider provider;
   final CompareMode compareMode;
   final int[] sortTypes;
   private RowFactory rowFactory;
   private static final Factory FACTORY = new Factory();

   public ValueDataType() {
      this((CastDataProvider)null, CompareMode.getInstance((String)null, 0), (DataHandler)null, (int[])null);
   }

   public ValueDataType(Database var1, int[] var2) {
      this(var1, var1.getCompareMode(), var1, var2);
   }

   public ValueDataType(CastDataProvider var1, CompareMode var2, DataHandler var3, int[] var4) {
      this.provider = var1;
      this.compareMode = var2;
      this.handler = var3;
      this.sortTypes = var4;
   }

   public RowFactory getRowFactory() {
      return this.rowFactory;
   }

   public void setRowFactory(RowFactory var1) {
      this.rowFactory = var1;
   }

   public Value[] createStorage(int var1) {
      return new Value[var1];
   }

   public int compare(Value var1, Value var2) {
      if (var1 == var2) {
         return 0;
      } else if (var1 instanceof SearchRow && var2 instanceof SearchRow) {
         return this.compare((SearchRow)var1, (SearchRow)var2);
      } else if (var1 instanceof ValueCollectionBase && var2 instanceof ValueCollectionBase) {
         Value[] var3 = ((ValueCollectionBase)var1).getList();
         Value[] var4 = ((ValueCollectionBase)var2).getList();
         int var5 = var3.length;
         int var6 = var4.length;
         int var7 = Math.min(var5, var6);

         for(int var8 = 0; var8 < var7; ++var8) {
            int var9 = this.sortTypes == null ? 0 : this.sortTypes[var8];
            Value var10 = var3[var8];
            Value var11 = var4[var8];
            if (var10 == null || var11 == null) {
               return this.compareValues(var3[var7 - 1], var4[var7 - 1], 0);
            }

            int var12 = this.compareValues(var10, var11, var9);
            if (var12 != 0) {
               return var12;
            }
         }

         if (var7 < var5) {
            return -1;
         } else if (var7 < var6) {
            return 1;
         } else {
            return 0;
         }
      } else {
         return this.compareValues(var1, var2, 0);
      }
   }

   private int compare(SearchRow var1, SearchRow var2) {
      if (var1 == var2) {
         return 0;
      } else {
         int[] var3 = this.rowFactory.getIndexes();
         int var4;
         int var5;
         if (var3 == null) {
            var4 = var1.getColumnCount();

            assert var4 == var2.getColumnCount() : var4 + " != " + var2.getColumnCount();

            for(var5 = 0; var5 < var4; ++var5) {
               int var11 = this.compareValues(var1.getValue(var5), var2.getValue(var5), this.sortTypes[var5]);
               if (var11 != 0) {
                  return var11;
               }
            }

            return 0;
         } else {
            assert this.sortTypes.length == var3.length;

            for(var4 = 0; var4 < var3.length; ++var4) {
               var5 = var3[var4];
               Value var6 = var1.getValue(var5);
               Value var7 = var2.getValue(var5);
               if (var6 == null || var7 == null) {
                  break;
               }

               int var8 = this.compareValues(var1.getValue(var5), var2.getValue(var5), this.sortTypes[var4]);
               if (var8 != 0) {
                  return var8;
               }
            }

            long var9 = var1.getKey();
            long var10 = var2.getKey();
            return var9 != SearchRow.MATCH_ALL_ROW_KEY && var10 != SearchRow.MATCH_ALL_ROW_KEY ? Long.compare(var9, var10) : 0;
         }
      }
   }

   public int compareValues(Value var1, Value var2, int var3) {
      if (var1 == var2) {
         return 0;
      } else {
         boolean var4 = var1 == ValueNull.INSTANCE;
         if (!var4 && var2 != ValueNull.INSTANCE) {
            int var5 = var1.compareTo(var2, this.provider, this.compareMode);
            if ((var3 & 1) != 0) {
               var5 = -var5;
            }

            return var5;
         } else {
            return DefaultNullOrdering.LOW.compareNull(var4, var3);
         }
      }
   }

   public int getMemory(Value var1) {
      return var1 == null ? 0 : var1.getMemory();
   }

   public Value read(ByteBuffer var1) {
      return this.readValue(var1, (TypeInfo)null);
   }

   public void write(WriteBuffer var1, Value var2) {
      if (var2 == ValueNull.INSTANCE) {
         var1.put((byte)0);
      } else {
         int var3 = var2.getValueType();
         int var7;
         ValueInterval var10;
         int var12;
         byte[] var16;
         LobDataDatabase var23;
         LobData var32;
         switch (var3) {
            case 1:
               writeString(var1.put((byte)21), var2.getString());
               break;
            case 2:
               String var31 = var2.getString();
               var12 = var31.length();
               if (var12 < 32) {
                  var1.put((byte)(68 + var12)).putStringData(var31, var12);
               } else {
                  writeString(var1.put((byte)13), var31);
               }
               break;
            case 3:
               var1.put((byte)16);
               ValueClob var30 = (ValueClob)var2;
               var32 = var30.getLobData();
               if (var32 instanceof LobDataDatabase) {
                  var23 = (LobDataDatabase)var32;
                  var1.putVarInt(-3).putVarInt(var23.getTableId()).putVarLong(var23.getLobId()).putVarLong(var30.octetLength()).putVarLong(var30.charLength());
               } else {
                  var16 = ((LobDataInMemory)var32).getSmall();
                  var1.putVarInt(var16.length).put(var16).putVarLong(var30.charLength());
               }
               break;
            case 4:
               writeString(var1.put((byte)14), var2.getString());
               break;
            case 5:
               writeBinary((byte)-119, var1, var2);
               break;
            case 6:
               byte[] var28 = var2.getBytesNoCopy();
               var12 = var28.length;
               if (var12 < 32) {
                  var1.put((byte)(100 + var12)).put(var28);
               } else {
                  var1.put((byte)12).putVarInt(var12).put(var28);
               }
               break;
            case 7:
               var1.put((byte)15);
               ValueBlob var26 = (ValueBlob)var2;
               var32 = var26.getLobData();
               if (var32 instanceof LobDataDatabase) {
                  var23 = (LobDataDatabase)var32;
                  var1.putVarInt(-3).putVarInt(var23.getTableId()).putVarLong(var23.getLobId()).putVarLong(var26.octetLength());
               } else {
                  var16 = ((LobDataInMemory)var32).getSmall();
                  var1.putVarInt(var16.length).put(var16);
               }
               break;
            case 8:
               var1.put((byte)(var2.getBoolean() ? 65 : 64));
               break;
            case 9:
               var1.put((byte)2).put(var2.getByte());
               break;
            case 10:
               var1.put((byte)3).putShort(var2.getShort());
               break;
            case 11:
            case 36:
               int var24 = var2.getInt();
               if (var24 < 0) {
                  var1.put((byte)66).putVarInt(-var24);
               } else if (var24 < 16) {
                  var1.put((byte)(32 + var24));
               } else {
                  var1.put((byte)(var3 == 11 ? 4 : 25)).putVarInt(var24);
               }
               break;
            case 12:
               writeLong(var1, var2.getLong());
               break;
            case 13:
               BigDecimal var21 = var2.getBigDecimal();
               if (BigDecimal.ZERO.equals(var21)) {
                  var1.put((byte)56);
               } else if (BigDecimal.ONE.equals(var21)) {
                  var1.put((byte)57);
               } else {
                  var12 = var21.scale();
                  BigInteger var20 = var21.unscaledValue();
                  var7 = var20.bitLength();
                  if (var7 <= 63) {
                     if (var12 == 0) {
                        var1.put((byte)58).putVarLong(var20.longValue());
                     } else {
                        var1.put((byte)59).putVarInt(var12).putVarLong(var20.longValue());
                     }
                  } else {
                     byte[] var29 = var20.toByteArray();
                     var1.put((byte)6).putVarInt(var12).putVarInt(var29.length).put(var29);
                  }
               }
               break;
            case 14:
               float var19 = var2.getFloat();
               if (var19 == 1.0F) {
                  var1.put((byte)63);
               } else {
                  var12 = Float.floatToIntBits(var19);
                  if (var12 == 0) {
                     var1.put((byte)62);
                  } else {
                     var1.put((byte)8).putVarInt(Integer.reverse(var12));
                  }
               }
               break;
            case 15:
               double var17 = var2.getDouble();
               if (var17 == 1.0) {
                  var1.put((byte)61);
               } else {
                  long var18 = Double.doubleToLongBits(var17);
                  if (var18 == 0L) {
                     var1.put((byte)60);
                  } else {
                     var1.put((byte)7).putVarLong(Long.reverse(var18));
                  }
               }
               break;
            case 16:
               ValueDecfloat var15 = (ValueDecfloat)var2;
               var1.put((byte)-118);
               if (var15.isFinite()) {
                  BigDecimal var25 = var15.getBigDecimal();
                  var16 = var25.unscaledValue().toByteArray();
                  var1.putVarInt(var25.scale()).putVarInt(var16.length).put(var16);
               } else {
                  byte var27;
                  if (var15 == ValueDecfloat.NEGATIVE_INFINITY) {
                     var27 = -3;
                  } else if (var15 == ValueDecfloat.POSITIVE_INFINITY) {
                     var27 = -2;
                  } else {
                     var27 = -1;
                  }

                  var1.putVarInt(0).putVarInt(var27);
               }
               break;
            case 17:
               var1.put((byte)10).putVarLong(((ValueDate)var2).getDateValue());
               break;
            case 18:
               writeTimestampTime(var1.put((byte)9), ((ValueTime)var2).getNanos());
               break;
            case 19:
               ValueTimeTimeZone var14 = (ValueTimeTimeZone)var2;
               long var22 = var14.getNanos();
               var1.put((byte)-120).putVarInt((int)(var22 / 1000000000L)).putVarInt((int)(var22 % 1000000000L));
               writeTimeZone(var1, var14.getTimeZoneOffsetSeconds());
               break;
            case 20:
               ValueTimestamp var13 = (ValueTimestamp)var2;
               var1.put((byte)11).putVarLong(var13.getDateValue());
               writeTimestampTime(var1, var13.getTimeNanos());
               break;
            case 21:
               ValueTimestampTimeZone var11 = (ValueTimestampTimeZone)var2;
               var1.put((byte)-121).putVarLong(var11.getDateValue());
               writeTimestampTime(var1, var11.getTimeNanos());
               writeTimeZone(var1, var11.getTimeZoneOffsetSeconds());
               break;
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
               var10 = (ValueInterval)var2;
               var12 = var3 - 22;
               if (var10.isNegative()) {
                  var12 = ~var12;
               }

               var1.put((byte)26).put((byte)var12).putVarLong(var10.getLeading());
               break;
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
               var10 = (ValueInterval)var2;
               var12 = var3 - 22;
               if (var10.isNegative()) {
                  var12 = ~var12;
               }

               var1.put((byte)26).put((byte)var12).putVarLong(var10.getLeading()).putVarLong(var10.getRemaining());
               break;
            case 35:
               writeBinary((byte)19, var1, var2);
               break;
            case 37:
               writeBinary((byte)22, var1, var2);
               break;
            case 38:
               writeBinary((byte)-122, var1, var2);
               break;
            case 39:
               ValueUuid var9 = (ValueUuid)var2;
               var1.put((byte)20).putLong(var9.getHigh()).putLong(var9.getLow());
               break;
            case 40:
            case 41:
               Value[] var4 = ((ValueCollectionBase)var2).getList();
               var1.put((byte)(var3 == 40 ? 17 : 27)).putVarInt(var4.length);
               Value[] var5 = var4;
               int var6 = var4.length;

               for(var7 = 0; var7 < var6; ++var7) {
                  Value var8 = var5[var7];
                  this.write(var1, var8);
               }

               return;
            default:
               throw DbException.getInternalError("type=" + var2.getValueType());
         }

      }
   }

   private static void writeBinary(byte var0, WriteBuffer var1, Value var2) {
      byte[] var3 = var2.getBytesNoCopy();
      var1.put(var0).putVarInt(var3.length).put(var3);
   }

   public static void writeLong(WriteBuffer var0, long var1) {
      if (var1 < 0L) {
         var0.put((byte)67).putVarLong(-var1);
      } else if (var1 < 8L) {
         var0.put((byte)((int)(48L + var1)));
      } else {
         var0.put((byte)5).putVarLong(var1);
      }

   }

   private static void writeString(WriteBuffer var0, String var1) {
      int var2 = var1.length();
      var0.putVarInt(var2).putStringData(var1, var2);
   }

   private static void writeTimestampTime(WriteBuffer var0, long var1) {
      long var3 = var1 / 1000000L;
      var0.putVarLong(var3).putVarInt((int)(var1 - var3 * 1000000L));
   }

   private static void writeTimeZone(WriteBuffer var0, int var1) {
      if (var1 % 900 == 0) {
         var0.put((byte)(var1 / 900));
      } else if (var1 > 0) {
         var0.put((byte)127).putVarInt(var1);
      } else {
         var0.put((byte)-128).putVarInt(-var1);
      }

   }

   Value readValue(ByteBuffer var1, TypeInfo var2) {
      int var3 = var1.get() & 255;
      int var4;
      byte[] var11;
      switch (var3) {
         case 0:
            return ValueNull.INSTANCE;
         case 1:
         case 18:
         case 23:
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
         case 40:
         case 41:
         case 42:
         case 43:
         case 44:
         case 45:
         case 46:
         case 47:
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         default:
            if (var3 >= 32 && var3 < 48) {
               var4 = var3 - 32;
               if (var2 != null && var2.getValueType() == 36) {
                  return ((ExtTypeInfoEnum)var2.getExtTypeInfo()).getValue(var4, this.provider);
               }

               return ValueInteger.get(var4);
            } else if (var3 >= 48 && var3 < 56) {
               return ValueBigint.get((long)(var3 - 48));
            } else if (var3 >= 100 && var3 < 132) {
               var4 = var3 - 100;
               var11 = Utils.newBytes(var4);
               var1.get(var11, 0, var4);
               return ValueVarbinary.getNoCopy(var11);
            } else {
               if (var3 >= 68 && var3 < 100) {
                  return ValueVarchar.get(DataUtils.readString(var1, var3 - 68));
               }

               throw DbException.get(90030, "type: " + var3);
            }
         case 2:
            return ValueTinyint.get(var1.get());
         case 3:
            return ValueSmallint.get(var1.getShort());
         case 4:
            return ValueInteger.get(DataUtils.readVarInt(var1));
         case 5:
            return ValueBigint.get(DataUtils.readVarLong(var1));
         case 6:
            var4 = DataUtils.readVarInt(var1);
            return ValueNumeric.get(new BigDecimal(new BigInteger(readVarBytes(var1)), var4));
         case 7:
            return ValueDouble.get(Double.longBitsToDouble(Long.reverse(DataUtils.readVarLong(var1))));
         case 8:
            return ValueReal.get(Float.intBitsToFloat(Integer.reverse(DataUtils.readVarInt(var1))));
         case 9:
            return ValueTime.fromNanos(readTimestampTime(var1));
         case 10:
            return ValueDate.fromDateValue(DataUtils.readVarLong(var1));
         case 11:
            return ValueTimestamp.fromDateValueAndNanos(DataUtils.readVarLong(var1), readTimestampTime(var1));
         case 12:
            return ValueVarbinary.getNoCopy(readVarBytes(var1));
         case 13:
            return ValueVarchar.get(DataUtils.readString(var1));
         case 14:
            return ValueVarcharIgnoreCase.get(DataUtils.readString(var1));
         case 15:
            var4 = DataUtils.readVarInt(var1);
            if (var4 >= 0) {
               var11 = Utils.newBytes(var4);
               var1.get(var11, 0, var4);
               return ValueBlob.createSmall(var11);
            } else {
               if (var4 == -3) {
                  return new ValueBlob(this.readLobDataDatabase(var1), DataUtils.readVarLong(var1));
               }

               throw DbException.get(90030, "lob type: " + var4);
            }
         case 16:
            var4 = DataUtils.readVarInt(var1);
            if (var4 >= 0) {
               var11 = Utils.newBytes(var4);
               var1.get(var11, 0, var4);
               return ValueClob.createSmall(var11, DataUtils.readVarLong(var1));
            } else {
               if (var4 == -3) {
                  return new ValueClob(this.readLobDataDatabase(var1), DataUtils.readVarLong(var1), DataUtils.readVarLong(var1));
               }

               throw DbException.get(90030, "lob type: " + var4);
            }
         case 17:
            if (var2 != null) {
               TypeInfo var12 = (TypeInfo)var2.getExtTypeInfo();
               return ValueArray.get(var12, this.readArrayElements(var1, var12), this.provider);
            }

            return ValueArray.get(this.readArrayElements(var1, (TypeInfo)null), this.provider);
         case 19:
            return ValueJavaObject.getNoCopy(readVarBytes(var1));
         case 20:
            return ValueUuid.get(var1.getLong(), var1.getLong());
         case 21:
            return ValueChar.get(DataUtils.readString(var1));
         case 22:
            return ValueGeometry.get(readVarBytes(var1));
         case 24:
            return ValueTimestampTimeZone.fromDateValueAndNanos(DataUtils.readVarLong(var1), readTimestampTime(var1), DataUtils.readVarInt(var1) * 60);
         case 25:
            var4 = DataUtils.readVarInt(var1);
            if (var2 != null) {
               return ((ExtTypeInfoEnum)var2.getExtTypeInfo()).getValue(var4, this.provider);
            }

            return ValueInteger.get(var4);
         case 26:
            var4 = var1.get();
            boolean var10 = var4 < 0;
            if (var10) {
               var4 = ~var4;
            }

            return ValueInterval.from(IntervalQualifier.valueOf(var4), var10, DataUtils.readVarLong(var1), var4 < 5 ? 0L : DataUtils.readVarLong(var1));
         case 27:
            var4 = DataUtils.readVarInt(var1);
            Value[] var9 = new Value[var4];
            if (var2 != null) {
               ExtTypeInfoRow var14 = (ExtTypeInfoRow)var2.getExtTypeInfo();
               Iterator var15 = var14.getFields().iterator();

               for(int var8 = 0; var8 < var4; ++var8) {
                  var9[var8] = this.readValue(var1, (TypeInfo)((Map.Entry)var15.next()).getValue());
               }

               return ValueRow.get(var2, var9);
            } else {
               TypeInfo[] var13 = this.rowFactory.getColumnTypes();

               for(int var7 = 0; var7 < var4; ++var7) {
                  var9[var7] = this.readValue(var1, var13[var7]);
               }

               return ValueRow.get(var9);
            }
         case 56:
            return ValueNumeric.ZERO;
         case 57:
            return ValueNumeric.ONE;
         case 58:
            return ValueNumeric.get(BigDecimal.valueOf(DataUtils.readVarLong(var1)));
         case 59:
            var4 = DataUtils.readVarInt(var1);
            return ValueNumeric.get(BigDecimal.valueOf(DataUtils.readVarLong(var1), var4));
         case 60:
            return ValueDouble.ZERO;
         case 61:
            return ValueDouble.ONE;
         case 62:
            return ValueReal.ZERO;
         case 63:
            return ValueReal.ONE;
         case 64:
            return ValueBoolean.FALSE;
         case 65:
            return ValueBoolean.TRUE;
         case 66:
            return ValueInteger.get(-DataUtils.readVarInt(var1));
         case 67:
            return ValueBigint.get(-DataUtils.readVarLong(var1));
         case 134:
            return ValueJson.getInternal(readVarBytes(var1));
         case 135:
            return ValueTimestampTimeZone.fromDateValueAndNanos(DataUtils.readVarLong(var1), readTimestampTime(var1), readTimeZone(var1));
         case 136:
            return ValueTimeTimeZone.fromNanos((long)DataUtils.readVarInt(var1) * 1000000000L + (long)DataUtils.readVarInt(var1), readTimeZone(var1));
         case 137:
            return ValueBinary.getNoCopy(readVarBytes(var1));
         case 138:
            var4 = DataUtils.readVarInt(var1);
            int var5 = DataUtils.readVarInt(var1);
            switch (var5) {
               case -3:
                  return ValueDecfloat.NEGATIVE_INFINITY;
               case -2:
                  return ValueDecfloat.POSITIVE_INFINITY;
               case -1:
                  return ValueDecfloat.NAN;
               default:
                  byte[] var6 = Utils.newBytes(var5);
                  var1.get(var6, 0, var5);
                  return ValueDecfloat.get(new BigDecimal(new BigInteger(var6), var4));
            }
      }
   }

   private LobDataDatabase readLobDataDatabase(ByteBuffer var1) {
      int var2 = DataUtils.readVarInt(var1);
      long var3 = DataUtils.readVarLong(var1);
      LobDataDatabase var5 = new LobDataDatabase(this.handler, var2, var3);
      return var5;
   }

   private Value[] readArrayElements(ByteBuffer var1, TypeInfo var2) {
      int var3 = DataUtils.readVarInt(var1);
      Value[] var4 = new Value[var3];

      for(int var5 = 0; var5 < var3; ++var5) {
         var4[var5] = this.readValue(var1, var2);
      }

      return var4;
   }

   private static byte[] readVarBytes(ByteBuffer var0) {
      int var1 = DataUtils.readVarInt(var0);
      byte[] var2 = Utils.newBytes(var1);
      var0.get(var2, 0, var1);
      return var2;
   }

   private static long readTimestampTime(ByteBuffer var0) {
      return DataUtils.readVarLong(var0) * 1000000L + (long)DataUtils.readVarInt(var0);
   }

   private static int readTimeZone(ByteBuffer var0) {
      byte var1 = var0.get();
      if (var1 == 127) {
         return DataUtils.readVarInt(var0);
      } else {
         return var1 == -128 ? -DataUtils.readVarInt(var0) : var1 * 900;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ValueDataType)) {
         return false;
      } else {
         ValueDataType var2 = (ValueDataType)var1;
         if (!this.compareMode.equals(var2.compareMode)) {
            return false;
         } else {
            int[] var3 = this.rowFactory == null ? null : this.rowFactory.getIndexes();
            int[] var4 = var2.rowFactory == null ? null : var2.rowFactory.getIndexes();
            return Arrays.equals(this.sortTypes, var2.sortTypes) && Arrays.equals(var3, var4);
         }
      }
   }

   public int hashCode() {
      int[] var1 = this.rowFactory == null ? null : this.rowFactory.getIndexes();
      return super.hashCode() ^ Arrays.hashCode(var1) ^ this.compareMode.hashCode() ^ Arrays.hashCode(this.sortTypes);
   }

   public void save(WriteBuffer var1, MetaType<Database> var2) {
      writeIntArray(var1, this.sortTypes);
      int var3 = this.rowFactory == null ? 0 : this.rowFactory.getColumnCount();
      var1.putVarInt(var3);
      int[] var4 = this.rowFactory == null ? null : this.rowFactory.getIndexes();
      writeIntArray(var1, var4);
      var1.put((byte)(this.rowFactory != null && !this.rowFactory.getRowDataType().isStoreKeys() ? 0 : 1));
   }

   private static void writeIntArray(WriteBuffer var0, int[] var1) {
      if (var1 == null) {
         var0.putVarInt(0);
      } else {
         var0.putVarInt(var1.length + 1);
         int[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            var0.putVarInt(var5);
         }
      }

   }

   public Factory getFactory() {
      return FACTORY;
   }

   public static final class Factory implements StatefulDataType.Factory<Database> {
      public DataType<?> create(ByteBuffer var1, MetaType<Database> var2, Database var3) {
         int[] var4 = readIntArray(var1);
         int var5 = DataUtils.readVarInt(var1);
         int[] var6 = readIntArray(var1);
         boolean var7 = var1.get() != 0;
         CompareMode var8 = var3 == null ? CompareMode.getInstance((String)null, 0) : var3.getCompareMode();
         if (var3 == null) {
            return new ValueDataType();
         } else if (var4 == null) {
            return new ValueDataType(var3, (int[])null);
         } else {
            RowFactory var9 = RowFactory.getDefaultRowFactory().createRowFactory(var3, var8, var3, var4, var6, (TypeInfo[])null, var5, var7);
            return var9.getRowDataType();
         }
      }

      private static int[] readIntArray(ByteBuffer var0) {
         int var1 = DataUtils.readVarInt(var0) - 1;
         if (var1 < 0) {
            return null;
         } else {
            int[] var2 = new int[var1];

            for(int var3 = 0; var3 < var2.length; ++var3) {
               var2[var3] = DataUtils.readVarInt(var0);
            }

            return var2;
         }
      }
   }
}
