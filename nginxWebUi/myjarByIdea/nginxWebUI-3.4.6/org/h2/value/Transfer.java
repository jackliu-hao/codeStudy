package org.h2.value;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.h2.api.IntervalQualifier;
import org.h2.engine.Session;
import org.h2.message.DbException;
import org.h2.security.SHA256;
import org.h2.store.Data;
import org.h2.store.DataReader;
import org.h2.util.Bits;
import org.h2.util.DateTimeUtils;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;

public final class Transfer {
   private static final int BUFFER_SIZE = 65536;
   private static final int LOB_MAGIC = 4660;
   private static final int LOB_MAC_SALT_LENGTH = 16;
   private static final int NULL = 0;
   private static final int BOOLEAN = 1;
   private static final int TINYINT = 2;
   private static final int SMALLINT = 3;
   private static final int INTEGER = 4;
   private static final int BIGINT = 5;
   private static final int NUMERIC = 6;
   private static final int DOUBLE = 7;
   private static final int REAL = 8;
   private static final int TIME = 9;
   private static final int DATE = 10;
   private static final int TIMESTAMP = 11;
   private static final int VARBINARY = 12;
   private static final int VARCHAR = 13;
   private static final int VARCHAR_IGNORECASE = 14;
   private static final int BLOB = 15;
   private static final int CLOB = 16;
   private static final int ARRAY = 17;
   private static final int JAVA_OBJECT = 19;
   private static final int UUID = 20;
   private static final int CHAR = 21;
   private static final int GEOMETRY = 22;
   private static final int TIMESTAMP_TZ = 24;
   private static final int ENUM = 25;
   private static final int INTERVAL = 26;
   private static final int ROW = 27;
   private static final int JSON = 28;
   private static final int TIME_TZ = 29;
   private static final int BINARY = 30;
   private static final int DECFLOAT = 31;
   private static final int[] VALUE_TO_TI = new int[43];
   private static final int[] TI_TO_VALUE = new int[45];
   private Socket socket;
   private DataInputStream in;
   private DataOutputStream out;
   private Session session;
   private boolean ssl;
   private int version;
   private byte[] lobMacSalt;

   private static void addType(int var0, int var1) {
      VALUE_TO_TI[var1 + 1] = var0;
      TI_TO_VALUE[var0 + 1] = var1;
   }

   public Transfer(Session var1, Socket var2) {
      this.session = var1;
      this.socket = var2;
   }

   public synchronized void init() throws IOException {
      if (this.socket != null) {
         this.in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream(), 65536));
         this.out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream(), 65536));
      }

   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public Transfer writeBoolean(boolean var1) throws IOException {
      this.out.writeByte((byte)(var1 ? 1 : 0));
      return this;
   }

   public boolean readBoolean() throws IOException {
      return this.in.readByte() != 0;
   }

   public Transfer writeByte(byte var1) throws IOException {
      this.out.writeByte(var1);
      return this;
   }

   public byte readByte() throws IOException {
      return this.in.readByte();
   }

   private Transfer writeShort(short var1) throws IOException {
      this.out.writeShort(var1);
      return this;
   }

   private short readShort() throws IOException {
      return this.in.readShort();
   }

   public Transfer writeInt(int var1) throws IOException {
      this.out.writeInt(var1);
      return this;
   }

   public int readInt() throws IOException {
      return this.in.readInt();
   }

   public Transfer writeLong(long var1) throws IOException {
      this.out.writeLong(var1);
      return this;
   }

   public long readLong() throws IOException {
      return this.in.readLong();
   }

   private Transfer writeDouble(double var1) throws IOException {
      this.out.writeDouble(var1);
      return this;
   }

   private Transfer writeFloat(float var1) throws IOException {
      this.out.writeFloat(var1);
      return this;
   }

   private double readDouble() throws IOException {
      return this.in.readDouble();
   }

   private float readFloat() throws IOException {
      return this.in.readFloat();
   }

   public Transfer writeString(String var1) throws IOException {
      if (var1 == null) {
         this.out.writeInt(-1);
      } else {
         this.out.writeInt(var1.length());
         this.out.writeChars(var1);
      }

      return this;
   }

   public String readString() throws IOException {
      int var1 = this.in.readInt();
      if (var1 == -1) {
         return null;
      } else {
         StringBuilder var2 = new StringBuilder(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.append(this.in.readChar());
         }

         String var4 = var2.toString();
         var4 = StringUtils.cache(var4);
         return var4;
      }
   }

   public Transfer writeBytes(byte[] var1) throws IOException {
      if (var1 == null) {
         this.writeInt(-1);
      } else {
         this.writeInt(var1.length);
         this.out.write(var1);
      }

      return this;
   }

   public Transfer writeBytes(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      return this;
   }

   public byte[] readBytes() throws IOException {
      int var1 = this.readInt();
      if (var1 == -1) {
         return null;
      } else {
         byte[] var2 = Utils.newBytes(var1);
         this.in.readFully(var2);
         return var2;
      }
   }

   public void readBytes(byte[] var1, int var2, int var3) throws IOException {
      this.in.readFully(var1, var2, var3);
   }

   public synchronized void close() {
      if (this.socket != null) {
         try {
            if (this.out != null) {
               this.out.flush();
            }

            this.socket.close();
         } catch (IOException var5) {
            DbException.traceThrowable(var5);
         } finally {
            this.socket = null;
         }
      }

   }

   public Transfer writeTypeInfo(TypeInfo var1) throws IOException {
      if (this.version >= 20) {
         this.writeTypeInfo20(var1);
      } else {
         this.writeTypeInfo19(var1);
      }

      return this;
   }

   private void writeTypeInfo20(TypeInfo var1) throws IOException {
      int var2 = var1.getValueType();
      this.writeInt(VALUE_TO_TI[var2 + 1]);
      switch (var2) {
         case -1:
         case 0:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 17:
         case 39:
            break;
         case 1:
         case 2:
         case 4:
         case 5:
         case 6:
         case 16:
         case 35:
         case 38:
            this.writeInt((int)var1.getDeclaredPrecision());
            break;
         case 3:
         case 7:
            this.writeLong(var1.getDeclaredPrecision());
            break;
         case 13:
            this.writeInt((int)var1.getDeclaredPrecision());
            this.writeInt(var1.getDeclaredScale());
            this.writeBoolean(var1.getExtTypeInfo() != null);
            break;
         case 14:
         case 15:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 32:
            this.writeBytePrecisionWithDefault(var1.getDeclaredPrecision());
            break;
         case 18:
         case 19:
         case 20:
         case 21:
            this.writeByteScaleWithDefault(var1.getDeclaredScale());
            break;
         case 27:
         case 31:
         case 33:
         case 34:
            this.writeBytePrecisionWithDefault(var1.getDeclaredPrecision());
            this.writeByteScaleWithDefault(var1.getDeclaredScale());
            break;
         case 36:
            this.writeTypeInfoEnum(var1);
            break;
         case 37:
            this.writeTypeInfoGeometry(var1);
            break;
         case 40:
            this.writeInt((int)var1.getDeclaredPrecision());
            this.writeTypeInfo((TypeInfo)var1.getExtTypeInfo());
            break;
         case 41:
            this.writeTypeInfoRow(var1);
            break;
         default:
            throw DbException.getUnsupportedException("value type " + var2);
      }

   }

   private void writeBytePrecisionWithDefault(long var1) throws IOException {
      this.writeByte(var1 >= 0L ? (byte)((int)var1) : -1);
   }

   private void writeByteScaleWithDefault(int var1) throws IOException {
      this.writeByte(var1 >= 0 ? (byte)var1 : -1);
   }

   private void writeTypeInfoEnum(TypeInfo var1) throws IOException {
      ExtTypeInfoEnum var2 = (ExtTypeInfoEnum)var1.getExtTypeInfo();
      if (var2 != null) {
         int var3 = var2.getCount();
         this.writeInt(var3);

         for(int var4 = 0; var4 < var3; ++var4) {
            this.writeString(var2.getEnumerator(var4));
         }
      } else {
         this.writeInt(0);
      }

   }

   private void writeTypeInfoGeometry(TypeInfo var1) throws IOException {
      ExtTypeInfoGeometry var2 = (ExtTypeInfoGeometry)var1.getExtTypeInfo();
      if (var2 == null) {
         this.writeByte((byte)0);
      } else {
         int var3 = var2.getType();
         Integer var4 = var2.getSrid();
         if (var3 == 0) {
            if (var4 == null) {
               this.writeByte((byte)0);
            } else {
               this.writeByte((byte)2);
               this.writeInt(var4);
            }
         } else if (var4 == null) {
            this.writeByte((byte)1);
            this.writeShort((short)var3);
         } else {
            this.writeByte((byte)3);
            this.writeShort((short)var3);
            this.writeInt(var4);
         }
      }

   }

   private void writeTypeInfoRow(TypeInfo var1) throws IOException {
      Set var2 = ((ExtTypeInfoRow)var1.getExtTypeInfo()).getFields();
      this.writeInt(var2.size());
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         this.writeString((String)var4.getKey()).writeTypeInfo((TypeInfo)var4.getValue());
      }

   }

   private void writeTypeInfo19(TypeInfo var1) throws IOException {
      int var2 = var1.getValueType();
      switch (var2) {
         case 5:
            var2 = 6;
            break;
         case 16:
            var2 = 13;
      }

      this.writeInt(VALUE_TO_TI[var2 + 1]).writeLong(var1.getPrecision()).writeInt(var1.getScale());
   }

   public TypeInfo readTypeInfo() throws IOException {
      return this.version >= 20 ? this.readTypeInfo20() : this.readTypeInfo19();
   }

   private TypeInfo readTypeInfo20() throws IOException {
      int var1 = TI_TO_VALUE[this.readInt() + 1];
      long var2 = -1L;
      int var4 = -1;
      Object var5 = null;
      switch (var1) {
         case -1:
         case 0:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 17:
         case 39:
            break;
         case 1:
         case 2:
         case 4:
         case 5:
         case 6:
         case 16:
         case 35:
         case 38:
            var2 = (long)this.readInt();
            break;
         case 3:
         case 7:
            var2 = this.readLong();
            break;
         case 13:
            var2 = (long)this.readInt();
            var4 = this.readInt();
            if (this.readBoolean()) {
               var5 = ExtTypeInfoNumeric.DECIMAL;
            }
            break;
         case 14:
         case 15:
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
         case 28:
         case 29:
         case 30:
         case 32:
            var2 = (long)this.readByte();
            break;
         case 18:
         case 19:
         case 20:
         case 21:
            var4 = this.readByte();
            break;
         case 27:
         case 31:
         case 33:
         case 34:
            var2 = (long)this.readByte();
            var4 = this.readByte();
            break;
         case 36:
            var5 = this.readTypeInfoEnum();
            break;
         case 37:
            var5 = this.readTypeInfoGeometry();
            break;
         case 40:
            var2 = (long)this.readInt();
            var5 = this.readTypeInfo();
            break;
         case 41:
            var5 = this.readTypeInfoRow();
            break;
         default:
            throw DbException.getUnsupportedException("value type " + var1);
      }

      return TypeInfo.getTypeInfo(var1, var2, var4, (ExtTypeInfo)var5);
   }

   private ExtTypeInfo readTypeInfoEnum() throws IOException {
      int var2 = this.readInt();
      ExtTypeInfoEnum var1;
      if (var2 > 0) {
         String[] var3 = new String[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = this.readString();
         }

         var1 = new ExtTypeInfoEnum(var3);
      } else {
         var1 = null;
      }

      return var1;
   }

   private ExtTypeInfo readTypeInfoGeometry() throws IOException {
      byte var2 = this.readByte();
      ExtTypeInfoGeometry var1;
      switch (var2) {
         case 0:
            var1 = null;
            break;
         case 1:
            var1 = new ExtTypeInfoGeometry(this.readShort(), (Integer)null);
            break;
         case 2:
            var1 = new ExtTypeInfoGeometry(0, this.readInt());
            break;
         case 3:
            var1 = new ExtTypeInfoGeometry(this.readShort(), this.readInt());
            break;
         default:
            throw DbException.getUnsupportedException("GEOMETRY type encoding " + var2);
      }

      return var1;
   }

   private ExtTypeInfo readTypeInfoRow() throws IOException {
      LinkedHashMap var1 = new LinkedHashMap();
      int var2 = 0;

      for(int var3 = this.readInt(); var2 < var3; ++var2) {
         String var4 = this.readString();
         if (var1.putIfAbsent(var4, this.readTypeInfo()) != null) {
            throw DbException.get(42121, (String)var4);
         }
      }

      return new ExtTypeInfoRow(var1);
   }

   private TypeInfo readTypeInfo19() throws IOException {
      return TypeInfo.getTypeInfo(TI_TO_VALUE[this.readInt() + 1], this.readLong(), this.readInt(), (ExtTypeInfo)null);
   }

   public void writeValue(Value var1) throws IOException {
      int var2 = var1.getValueType();
      Value[] var4;
      int var5;
      Value[] var6;
      int var7;
      int var8;
      Value var9;
      ValueInterval var12;
      int var14;
      long var18;
      LobData var21;
      LobDataDatabase var22;
      switch (var2) {
         case 0:
            this.writeInt(0);
            break;
         case 1:
            this.writeInt(21);
            this.writeString(var1.getString());
            break;
         case 2:
            this.writeInt(13);
            this.writeString(var1.getString());
            break;
         case 3:
            this.writeInt(16);
            ValueClob var19 = (ValueClob)var1;
            var21 = var19.getLobData();
            var18 = var19.charLength();
            if (var21 instanceof LobDataDatabase) {
               var22 = (LobDataDatabase)var21;
               this.writeLong(-1L);
               this.writeInt(var22.getTableId());
               this.writeLong(var22.getLobId());
               this.writeBytes(this.calculateLobMac(var22.getLobId()));
               if (this.version >= 20) {
                  this.writeLong(var19.octetLength());
               }

               this.writeLong(var18);
            } else {
               if (var18 < 0L) {
                  throw DbException.get(90067, "length=" + var18);
               }

               this.writeLong(var18);
               Reader var24 = var19.getReader();
               Data.copyString(var24, this.out);
               this.writeInt(4660);
            }
            break;
         case 4:
            this.writeInt(14);
            this.writeString(var1.getString());
            break;
         case 5:
            if (this.version >= 20) {
               this.writeInt(30);
               this.writeBytes(var1.getBytesNoCopy());
               break;
            }
         case 6:
            this.writeInt(12);
            this.writeBytes(var1.getBytesNoCopy());
            break;
         case 7:
            this.writeInt(15);
            ValueBlob var17 = (ValueBlob)var1;
            var21 = var17.getLobData();
            var18 = var17.octetLength();
            if (var21 instanceof LobDataDatabase) {
               var22 = (LobDataDatabase)var21;
               this.writeLong(-1L);
               this.writeInt(var22.getTableId());
               this.writeLong(var22.getLobId());
               this.writeBytes(this.calculateLobMac(var22.getLobId()));
               this.writeLong(var18);
            } else {
               if (var18 < 0L) {
                  throw DbException.get(90067, "length=" + var18);
               }

               this.writeLong(var18);
               long var23 = IOUtils.copyAndCloseInput(var17.getInputStream(), this.out);
               if (var23 != var18) {
                  throw DbException.get(90067, "length:" + var18 + " written:" + var23);
               }

               this.writeInt(4660);
            }
            break;
         case 8:
            this.writeInt(1);
            this.writeBoolean(var1.getBoolean());
            break;
         case 9:
            this.writeInt(2);
            this.writeByte(var1.getByte());
            break;
         case 10:
            this.writeInt(3);
            if (this.version >= 20) {
               this.writeShort(var1.getShort());
            } else {
               this.writeInt(var1.getShort());
            }
            break;
         case 11:
            this.writeInt(4);
            this.writeInt(var1.getInt());
            break;
         case 12:
            this.writeInt(5);
            this.writeLong(var1.getLong());
            break;
         case 14:
            this.writeInt(8);
            this.writeFloat(var1.getFloat());
            break;
         case 15:
            this.writeInt(7);
            this.writeDouble(var1.getDouble());
            break;
         case 16:
            if (this.version >= 20) {
               this.writeInt(31);
               this.writeString(var1.getString());
               break;
            }
         case 13:
            this.writeInt(6);
            this.writeString(var1.getString());
            break;
         case 17:
            this.writeInt(10);
            this.writeLong(((ValueDate)var1).getDateValue());
            break;
         case 18:
            this.writeInt(9);
            this.writeLong(((ValueTime)var1).getNanos());
            break;
         case 19:
            ValueTimeTimeZone var16 = (ValueTimeTimeZone)var1;
            if (this.version >= 19) {
               this.writeInt(29);
               this.writeLong(var16.getNanos());
               this.writeInt(var16.getTimeZoneOffsetSeconds());
            } else {
               this.writeInt(9);
               ValueTimestampTimeZone var20 = this.session.isRemote() ? DateTimeUtils.currentTimestamp(DateTimeUtils.getTimeZone()) : this.session.currentTimestamp();
               this.writeLong(DateTimeUtils.normalizeNanosOfDay(var16.getNanos() + (long)(var16.getTimeZoneOffsetSeconds() - var20.getTimeZoneOffsetSeconds()) * 86400000000000L));
            }
            break;
         case 20:
            this.writeInt(11);
            ValueTimestamp var15 = (ValueTimestamp)var1;
            this.writeLong(var15.getDateValue());
            this.writeLong(var15.getTimeNanos());
            break;
         case 21:
            this.writeInt(24);
            ValueTimestampTimeZone var13 = (ValueTimestampTimeZone)var1;
            this.writeLong(var13.getDateValue());
            this.writeLong(var13.getTimeNanos());
            var14 = var13.getTimeZoneOffsetSeconds();
            this.writeInt(this.version >= 19 ? var14 : var14 / 60);
            break;
         case 22:
         case 23:
         case 24:
         case 25:
         case 26:
            if (this.version >= 18) {
               var12 = (ValueInterval)var1;
               var14 = var2 - 22;
               if (var12.isNegative()) {
                  var14 = ~var14;
               }

               this.writeInt(26);
               this.writeByte((byte)var14);
               this.writeLong(var12.getLeading());
            } else {
               this.writeInt(13);
               this.writeString(var1.getString());
            }
            break;
         case 27:
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
            if (this.version >= 18) {
               var12 = (ValueInterval)var1;
               var14 = var2 - 22;
               if (var12.isNegative()) {
                  var14 = ~var14;
               }

               this.writeInt(26);
               this.writeByte((byte)var14);
               this.writeLong(var12.getLeading());
               this.writeLong(var12.getRemaining());
            } else {
               this.writeInt(13);
               this.writeString(var1.getString());
            }
            break;
         case 35:
            this.writeInt(19);
            this.writeBytes(var1.getBytesNoCopy());
            break;
         case 36:
            this.writeInt(25);
            this.writeInt(var1.getInt());
            if (this.version < 20) {
               this.writeString(var1.getString());
            }
            break;
         case 37:
            this.writeInt(22);
            this.writeBytes(var1.getBytesNoCopy());
            break;
         case 38:
            this.writeInt(28);
            this.writeBytes(var1.getBytesNoCopy());
            break;
         case 39:
            this.writeInt(20);
            ValueUuid var11 = (ValueUuid)var1;
            this.writeLong(var11.getHigh());
            this.writeLong(var11.getLow());
            break;
         case 40:
            this.writeInt(17);
            ValueArray var10 = (ValueArray)var1;
            var4 = var10.getList();
            var5 = var4.length;
            this.writeInt(var5);
            var6 = var4;
            var7 = var4.length;

            for(var8 = 0; var8 < var7; ++var8) {
               var9 = var6[var8];
               this.writeValue(var9);
            }

            return;
         case 41:
            this.writeInt(this.version >= 18 ? 27 : 17);
            ValueRow var3 = (ValueRow)var1;
            var4 = var3.getList();
            var5 = var4.length;
            this.writeInt(var5);
            var6 = var4;
            var7 = var4.length;

            for(var8 = 0; var8 < var7; ++var8) {
               var9 = var6[var8];
               this.writeValue(var9);
            }

            return;
         default:
            throw DbException.get(90067, "type=" + var2);
      }

   }

   public Value readValue(TypeInfo var1) throws IOException {
      int var2 = this.readInt();
      int var7;
      byte[] var8;
      long var9;
      int var11;
      long var14;
      int var15;
      long var16;
      switch (var2) {
         case 0:
            return ValueNull.INSTANCE;
         case 1:
            return ValueBoolean.get(this.readBoolean());
         case 2:
            return ValueTinyint.get(this.readByte());
         case 3:
            if (this.version >= 20) {
               return ValueSmallint.get(this.readShort());
            }

            return ValueSmallint.get((short)this.readInt());
         case 4:
            return ValueInteger.get(this.readInt());
         case 5:
            return ValueBigint.get(this.readLong());
         case 6:
            return ValueNumeric.get(new BigDecimal(this.readString()));
         case 7:
            return ValueDouble.get(this.readDouble());
         case 8:
            return ValueReal.get(this.readFloat());
         case 9:
            return ValueTime.fromNanos(this.readLong());
         case 10:
            return ValueDate.fromDateValue(this.readLong());
         case 11:
            return ValueTimestamp.fromDateValueAndNanos(this.readLong(), this.readLong());
         case 12:
            return ValueVarbinary.getNoCopy(this.readBytes());
         case 13:
            return ValueVarchar.get(this.readString());
         case 14:
            return ValueVarcharIgnoreCase.get(this.readString());
         case 15:
            var14 = this.readLong();
            if (var14 == -1L) {
               var5 = this.readInt();
               var16 = this.readLong();
               var8 = this.readBytes();
               var9 = this.readLong();
               return new ValueBlob(new LobDataFetchOnDemand(this.session.getDataHandler(), var5, var16, var8), var9);
            } else {
               ValueBlob var20 = this.session.getDataHandler().getLobStorage().createBlob(this.in, var14);
               var15 = this.readInt();
               if (var15 != 4660) {
                  throw DbException.get(90067, "magic=" + var15);
               }

               return var20;
            }
         case 16:
            var14 = this.readLong();
            if (var14 == -1L) {
               var5 = this.readInt();
               var16 = this.readLong();
               var8 = this.readBytes();
               var9 = this.version >= 20 ? this.readLong() : -1L;
               var14 = this.readLong();
               return new ValueClob(new LobDataFetchOnDemand(this.session.getDataHandler(), var5, var16, var8), var9, var14);
            } else if (var14 < 0L) {
               throw DbException.get(90067, "length=" + var14);
            } else {
               ValueClob var19 = this.session.getDataHandler().getLobStorage().createClob(new DataReader(this.in), var14);
               var15 = this.readInt();
               if (var15 != 4660) {
                  throw DbException.get(90067, "magic=" + var15);
               }

               return var19;
            }
         case 17:
            var11 = this.readInt();
            if (var11 < 0) {
               var11 = ~var11;
               this.readString();
            }

            if (var1 != null) {
               TypeInfo var13 = (TypeInfo)var1.getExtTypeInfo();
               return ValueArray.get(var13, this.readArrayElements(var11, var13), this.session);
            }

            return ValueArray.get(this.readArrayElements(var11, (TypeInfo)null), this.session);
         case 18:
         case 23:
         default:
            throw DbException.get(90067, "type=" + var2);
         case 19:
            return ValueJavaObject.getNoCopy(this.readBytes());
         case 20:
            return ValueUuid.get(this.readLong(), this.readLong());
         case 21:
            return ValueChar.get(this.readString());
         case 22:
            return ValueGeometry.get(this.readBytes());
         case 24:
            var14 = this.readLong();
            long var18 = this.readLong();
            var7 = this.readInt();
            return ValueTimestampTimeZone.fromDateValueAndNanos(var14, var18, this.version >= 19 ? var7 : var7 * 60);
         case 25:
            var11 = this.readInt();
            if (this.version >= 20) {
               return ((ExtTypeInfoEnum)var1.getExtTypeInfo()).getValue(var11, this.session);
            }

            return ValueEnumBase.get(this.readString(), var11);
         case 26:
            var11 = this.readByte();
            boolean var12 = var11 < 0;
            if (var12) {
               var11 = ~var11;
            }

            return ValueInterval.from(IntervalQualifier.valueOf(var11), var12, this.readLong(), var11 < 5 ? 0L : this.readLong());
         case 27:
            var11 = this.readInt();
            Value[] var4 = new Value[var11];
            if (var1 != null) {
               ExtTypeInfoRow var17 = (ExtTypeInfoRow)var1.getExtTypeInfo();
               Iterator var6 = var17.getFields().iterator();

               for(var7 = 0; var7 < var11; ++var7) {
                  var4[var7] = this.readValue((TypeInfo)((Map.Entry)var6.next()).getValue());
               }

               return ValueRow.get(var1, var4);
            } else {
               for(var5 = 0; var5 < var11; ++var5) {
                  var4[var5] = this.readValue((TypeInfo)null);
               }

               return ValueRow.get(var4);
            }
         case 28:
            return ValueJson.fromJson(this.readBytes());
         case 29:
            return ValueTimeTimeZone.fromNanos(this.readLong(), this.readInt());
         case 30:
            return ValueBinary.getNoCopy(this.readBytes());
         case 31:
            switch (this.readString()) {
               case "-Infinity":
                  return ValueDecfloat.NEGATIVE_INFINITY;
               case "Infinity":
                  return ValueDecfloat.POSITIVE_INFINITY;
               case "NaN":
                  return ValueDecfloat.NAN;
               default:
                  return ValueDecfloat.get(new BigDecimal(var3));
            }
      }
   }

   private Value[] readArrayElements(int var1, TypeInfo var2) throws IOException {
      Value[] var3 = new Value[var1];

      for(int var4 = 0; var4 < var1; ++var4) {
         var3[var4] = this.readValue(var2);
      }

      return var3;
   }

   public long readRowCount() throws IOException {
      return this.version >= 20 ? this.readLong() : (long)this.readInt();
   }

   public Transfer writeRowCount(long var1) throws IOException {
      return this.version >= 20 ? this.writeLong(var1) : this.writeInt(var1 < 2147483647L ? (int)var1 : Integer.MAX_VALUE);
   }

   public Socket getSocket() {
      return this.socket;
   }

   public void setSession(Session var1) {
      this.session = var1;
   }

   public void setSSL(boolean var1) {
      this.ssl = var1;
   }

   public Transfer openNewConnection() throws IOException {
      InetAddress var1 = this.socket.getInetAddress();
      int var2 = this.socket.getPort();
      Socket var3 = NetUtils.createSocket(var1, var2, this.ssl);
      Transfer var4 = new Transfer((Session)null, var3);
      var4.setSSL(this.ssl);
      return var4;
   }

   public void setVersion(int var1) {
      this.version = var1;
   }

   public int getVersion() {
      return this.version;
   }

   public synchronized boolean isClosed() {
      return this.socket == null || this.socket.isClosed();
   }

   public void verifyLobMac(byte[] var1, long var2) {
      byte[] var4 = this.calculateLobMac(var2);
      if (!Utils.compareSecure(var1, var4)) {
         throw DbException.get(90067, "Invalid lob hmac; possibly the connection was re-opened internally");
      }
   }

   private byte[] calculateLobMac(long var1) {
      if (this.lobMacSalt == null) {
         this.lobMacSalt = MathUtils.secureRandomBytes(16);
      }

      byte[] var3 = new byte[8];
      Bits.writeLong(var3, 0, var1);
      return SHA256.getHashWithSalt(var3, this.lobMacSalt);
   }

   static {
      addType(-1, -1);
      addType(0, 0);
      addType(1, 8);
      addType(2, 9);
      addType(3, 10);
      addType(4, 11);
      addType(5, 12);
      addType(6, 13);
      addType(7, 15);
      addType(8, 14);
      addType(9, 18);
      addType(10, 17);
      addType(11, 20);
      addType(12, 6);
      addType(13, 2);
      addType(14, 4);
      addType(15, 7);
      addType(16, 3);
      addType(17, 40);
      addType(19, 35);
      addType(20, 39);
      addType(21, 1);
      addType(22, 37);
      addType(24, 21);
      addType(25, 36);
      addType(26, 22);
      addType(27, 23);
      addType(28, 24);
      addType(29, 25);
      addType(30, 26);
      addType(31, 27);
      addType(32, 28);
      addType(33, 29);
      addType(34, 30);
      addType(35, 31);
      addType(36, 32);
      addType(37, 33);
      addType(38, 34);
      addType(39, 41);
      addType(40, 38);
      addType(41, 19);
      addType(42, 5);
      addType(43, 16);
   }
}
