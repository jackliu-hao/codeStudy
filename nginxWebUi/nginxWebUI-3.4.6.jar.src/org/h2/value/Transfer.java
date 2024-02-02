/*      */ package org.h2.value;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.InetAddress;
/*      */ import java.net.Socket;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.h2.api.IntervalQualifier;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.Session;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.security.SHA256;
/*      */ import org.h2.store.Data;
/*      */ import org.h2.store.DataReader;
/*      */ import org.h2.util.Bits;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.IOUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.NetUtils;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.value.lob.LobData;
/*      */ import org.h2.value.lob.LobDataDatabase;
/*      */ import org.h2.value.lob.LobDataFetchOnDemand;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class Transfer
/*      */ {
/*      */   private static final int BUFFER_SIZE = 65536;
/*      */   private static final int LOB_MAGIC = 4660;
/*      */   private static final int LOB_MAC_SALT_LENGTH = 16;
/*      */   private static final int NULL = 0;
/*      */   private static final int BOOLEAN = 1;
/*      */   private static final int TINYINT = 2;
/*      */   private static final int SMALLINT = 3;
/*      */   private static final int INTEGER = 4;
/*      */   private static final int BIGINT = 5;
/*      */   private static final int NUMERIC = 6;
/*      */   private static final int DOUBLE = 7;
/*      */   private static final int REAL = 8;
/*      */   private static final int TIME = 9;
/*      */   private static final int DATE = 10;
/*      */   private static final int TIMESTAMP = 11;
/*      */   private static final int VARBINARY = 12;
/*      */   private static final int VARCHAR = 13;
/*      */   private static final int VARCHAR_IGNORECASE = 14;
/*      */   private static final int BLOB = 15;
/*      */   private static final int CLOB = 16;
/*      */   private static final int ARRAY = 17;
/*      */   private static final int JAVA_OBJECT = 19;
/*      */   private static final int UUID = 20;
/*      */   private static final int CHAR = 21;
/*      */   private static final int GEOMETRY = 22;
/*      */   private static final int TIMESTAMP_TZ = 24;
/*      */   private static final int ENUM = 25;
/*      */   private static final int INTERVAL = 26;
/*      */   private static final int ROW = 27;
/*      */   private static final int JSON = 28;
/*      */   private static final int TIME_TZ = 29;
/*      */   private static final int BINARY = 30;
/*      */   private static final int DECFLOAT = 31;
/*   88 */   private static final int[] VALUE_TO_TI = new int[43];
/*   89 */   private static final int[] TI_TO_VALUE = new int[45]; private Socket socket; private DataInputStream in; private DataOutputStream out; private Session session; private boolean ssl; private int version; private byte[] lobMacSalt;
/*      */   
/*      */   static {
/*   92 */     addType(-1, -1);
/*   93 */     addType(0, 0);
/*   94 */     addType(1, 8);
/*   95 */     addType(2, 9);
/*   96 */     addType(3, 10);
/*   97 */     addType(4, 11);
/*   98 */     addType(5, 12);
/*   99 */     addType(6, 13);
/*  100 */     addType(7, 15);
/*  101 */     addType(8, 14);
/*  102 */     addType(9, 18);
/*  103 */     addType(10, 17);
/*  104 */     addType(11, 20);
/*  105 */     addType(12, 6);
/*  106 */     addType(13, 2);
/*  107 */     addType(14, 4);
/*  108 */     addType(15, 7);
/*  109 */     addType(16, 3);
/*  110 */     addType(17, 40);
/*  111 */     addType(19, 35);
/*  112 */     addType(20, 39);
/*  113 */     addType(21, 1);
/*  114 */     addType(22, 37);
/*  115 */     addType(24, 21);
/*  116 */     addType(25, 36);
/*  117 */     addType(26, 22);
/*  118 */     addType(27, 23);
/*  119 */     addType(28, 24);
/*  120 */     addType(29, 25);
/*  121 */     addType(30, 26);
/*  122 */     addType(31, 27);
/*  123 */     addType(32, 28);
/*  124 */     addType(33, 29);
/*  125 */     addType(34, 30);
/*  126 */     addType(35, 31);
/*  127 */     addType(36, 32);
/*  128 */     addType(37, 33);
/*  129 */     addType(38, 34);
/*  130 */     addType(39, 41);
/*  131 */     addType(40, 38);
/*  132 */     addType(41, 19);
/*  133 */     addType(42, 5);
/*  134 */     addType(43, 16);
/*      */   }
/*      */   
/*      */   private static void addType(int paramInt1, int paramInt2) {
/*  138 */     VALUE_TO_TI[paramInt2 + 1] = paramInt1;
/*  139 */     TI_TO_VALUE[paramInt1 + 1] = paramInt2;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer(Session paramSession, Socket paramSocket) {
/*  157 */     this.session = paramSession;
/*  158 */     this.socket = paramSocket;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void init() throws IOException {
/*  167 */     if (this.socket != null) {
/*  168 */       this
/*      */         
/*  170 */         .in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream(), 65536));
/*  171 */       this
/*      */         
/*  173 */         .out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream(), 65536));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void flush() throws IOException {
/*  182 */     this.out.flush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeBoolean(boolean paramBoolean) throws IOException {
/*  193 */     this.out.writeByte((byte)(paramBoolean ? 1 : 0));
/*  194 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean readBoolean() throws IOException {
/*  204 */     return (this.in.readByte() != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeByte(byte paramByte) throws IOException {
/*  215 */     this.out.writeByte(paramByte);
/*  216 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte readByte() throws IOException {
/*  226 */     return this.in.readByte();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Transfer writeShort(short paramShort) throws IOException {
/*  237 */     this.out.writeShort(paramShort);
/*  238 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private short readShort() throws IOException {
/*  248 */     return this.in.readShort();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeInt(int paramInt) throws IOException {
/*  259 */     this.out.writeInt(paramInt);
/*  260 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int readInt() throws IOException {
/*  270 */     return this.in.readInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeLong(long paramLong) throws IOException {
/*  281 */     this.out.writeLong(paramLong);
/*  282 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long readLong() throws IOException {
/*  292 */     return this.in.readLong();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Transfer writeDouble(double paramDouble) throws IOException {
/*  303 */     this.out.writeDouble(paramDouble);
/*  304 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Transfer writeFloat(float paramFloat) throws IOException {
/*  314 */     this.out.writeFloat(paramFloat);
/*  315 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private double readDouble() throws IOException {
/*  325 */     return this.in.readDouble();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private float readFloat() throws IOException {
/*  335 */     return this.in.readFloat();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeString(String paramString) throws IOException {
/*  346 */     if (paramString == null) {
/*  347 */       this.out.writeInt(-1);
/*      */     } else {
/*  349 */       this.out.writeInt(paramString.length());
/*  350 */       this.out.writeChars(paramString);
/*      */     } 
/*  352 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String readString() throws IOException {
/*  362 */     int i = this.in.readInt();
/*  363 */     if (i == -1) {
/*  364 */       return null;
/*      */     }
/*  366 */     StringBuilder stringBuilder = new StringBuilder(i);
/*  367 */     for (byte b = 0; b < i; b++) {
/*  368 */       stringBuilder.append(this.in.readChar());
/*      */     }
/*  370 */     String str = stringBuilder.toString();
/*  371 */     str = StringUtils.cache(str);
/*  372 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeBytes(byte[] paramArrayOfbyte) throws IOException {
/*  383 */     if (paramArrayOfbyte == null) {
/*  384 */       writeInt(-1);
/*      */     } else {
/*  386 */       writeInt(paramArrayOfbyte.length);
/*  387 */       this.out.write(paramArrayOfbyte);
/*      */     } 
/*  389 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/*  402 */     this.out.write(paramArrayOfbyte, paramInt1, paramInt2);
/*  403 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] readBytes() throws IOException {
/*  413 */     int i = readInt();
/*  414 */     if (i == -1) {
/*  415 */       return null;
/*      */     }
/*  417 */     byte[] arrayOfByte = Utils.newBytes(i);
/*  418 */     this.in.readFully(arrayOfByte);
/*  419 */     return arrayOfByte;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void readBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
/*  431 */     this.in.readFully(paramArrayOfbyte, paramInt1, paramInt2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public synchronized void close() {
/*  438 */     if (this.socket != null) {
/*      */       try {
/*  440 */         if (this.out != null) {
/*  441 */           this.out.flush();
/*      */         }
/*  443 */         this.socket.close();
/*  444 */       } catch (IOException iOException) {
/*  445 */         DbException.traceThrowable(iOException);
/*      */       } finally {
/*  447 */         this.socket = null;
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeTypeInfo(TypeInfo paramTypeInfo) throws IOException {
/*  460 */     if (this.version >= 20) {
/*  461 */       writeTypeInfo20(paramTypeInfo);
/*      */     } else {
/*  463 */       writeTypeInfo19(paramTypeInfo);
/*      */     } 
/*  465 */     return this;
/*      */   }
/*      */   
/*      */   private void writeTypeInfo20(TypeInfo paramTypeInfo) throws IOException {
/*  469 */     int i = paramTypeInfo.getValueType();
/*  470 */     writeInt(VALUE_TO_TI[i + 1]);
/*  471 */     switch (i) {
/*      */       case -1:
/*      */       case 0:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 17:
/*      */       case 39:
/*      */         return;
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 16:
/*      */       case 35:
/*      */       case 38:
/*  490 */         writeInt((int)paramTypeInfo.getDeclaredPrecision());
/*      */       
/*      */       case 3:
/*      */       case 7:
/*  494 */         writeLong(paramTypeInfo.getDeclaredPrecision());
/*      */       
/*      */       case 13:
/*  497 */         writeInt((int)paramTypeInfo.getDeclaredPrecision());
/*  498 */         writeInt(paramTypeInfo.getDeclaredScale());
/*  499 */         writeBoolean((paramTypeInfo.getExtTypeInfo() != null));
/*      */       
/*      */       case 14:
/*      */       case 15:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 32:
/*  512 */         writeBytePrecisionWithDefault(paramTypeInfo.getDeclaredPrecision());
/*      */       
/*      */       case 18:
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*  518 */         writeByteScaleWithDefault(paramTypeInfo.getDeclaredScale());
/*      */       
/*      */       case 27:
/*      */       case 31:
/*      */       case 33:
/*      */       case 34:
/*  524 */         writeBytePrecisionWithDefault(paramTypeInfo.getDeclaredPrecision());
/*  525 */         writeByteScaleWithDefault(paramTypeInfo.getDeclaredScale());
/*      */       
/*      */       case 36:
/*  528 */         writeTypeInfoEnum(paramTypeInfo);
/*      */       
/*      */       case 37:
/*  531 */         writeTypeInfoGeometry(paramTypeInfo);
/*      */       
/*      */       case 40:
/*  534 */         writeInt((int)paramTypeInfo.getDeclaredPrecision());
/*  535 */         writeTypeInfo((TypeInfo)paramTypeInfo.getExtTypeInfo());
/*      */       
/*      */       case 41:
/*  538 */         writeTypeInfoRow(paramTypeInfo);
/*      */     } 
/*      */     
/*  541 */     throw DbException.getUnsupportedException("value type " + i);
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeBytePrecisionWithDefault(long paramLong) throws IOException {
/*  546 */     writeByte((paramLong >= 0L) ? (byte)(int)paramLong : -1);
/*      */   }
/*      */   
/*      */   private void writeByteScaleWithDefault(int paramInt) throws IOException {
/*  550 */     writeByte((paramInt >= 0) ? (byte)paramInt : -1);
/*      */   }
/*      */   
/*      */   private void writeTypeInfoEnum(TypeInfo paramTypeInfo) throws IOException {
/*  554 */     ExtTypeInfoEnum extTypeInfoEnum = (ExtTypeInfoEnum)paramTypeInfo.getExtTypeInfo();
/*  555 */     if (extTypeInfoEnum != null) {
/*  556 */       int i = extTypeInfoEnum.getCount();
/*  557 */       writeInt(i);
/*  558 */       for (byte b = 0; b < i; b++) {
/*  559 */         writeString(extTypeInfoEnum.getEnumerator(b));
/*      */       }
/*      */     } else {
/*  562 */       writeInt(0);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void writeTypeInfoGeometry(TypeInfo paramTypeInfo) throws IOException {
/*  567 */     ExtTypeInfoGeometry extTypeInfoGeometry = (ExtTypeInfoGeometry)paramTypeInfo.getExtTypeInfo();
/*  568 */     if (extTypeInfoGeometry == null) {
/*  569 */       writeByte((byte)0);
/*      */     } else {
/*  571 */       int i = extTypeInfoGeometry.getType();
/*  572 */       Integer integer = extTypeInfoGeometry.getSrid();
/*  573 */       if (i == 0) {
/*  574 */         if (integer == null) {
/*  575 */           writeByte((byte)0);
/*      */         } else {
/*  577 */           writeByte((byte)2);
/*  578 */           writeInt(integer.intValue());
/*      */         }
/*      */       
/*  581 */       } else if (integer == null) {
/*  582 */         writeByte((byte)1);
/*  583 */         writeShort((short)i);
/*      */       } else {
/*  585 */         writeByte((byte)3);
/*  586 */         writeShort((short)i);
/*  587 */         writeInt(integer.intValue());
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void writeTypeInfoRow(TypeInfo paramTypeInfo) throws IOException {
/*  594 */     Set<Map.Entry<String, TypeInfo>> set = ((ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo()).getFields();
/*  595 */     writeInt(set.size());
/*  596 */     for (Map.Entry<String, TypeInfo> entry : set) {
/*  597 */       writeString((String)entry.getKey()).writeTypeInfo((TypeInfo)entry.getValue());
/*      */     }
/*      */   }
/*      */   
/*      */   private void writeTypeInfo19(TypeInfo paramTypeInfo) throws IOException {
/*  602 */     int i = paramTypeInfo.getValueType();
/*  603 */     switch (i) {
/*      */       case 5:
/*  605 */         i = 6;
/*      */         break;
/*      */       case 16:
/*  608 */         i = 13;
/*      */         break;
/*      */     } 
/*  611 */     writeInt(VALUE_TO_TI[i + 1]).writeLong(paramTypeInfo.getPrecision()).writeInt(paramTypeInfo.getScale());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public TypeInfo readTypeInfo() throws IOException {
/*  621 */     if (this.version >= 20) {
/*  622 */       return readTypeInfo20();
/*      */     }
/*  624 */     return readTypeInfo19();
/*      */   }
/*      */ 
/*      */   
/*      */   private TypeInfo readTypeInfo20() throws IOException {
/*  629 */     int i = TI_TO_VALUE[readInt() + 1];
/*  630 */     long l = -1L;
/*  631 */     int j = -1;
/*  632 */     ExtTypeInfo extTypeInfo = null;
/*  633 */     switch (i)
/*      */     
/*      */     { 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case -1:
/*      */       case 0:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 17:
/*      */       case 39:
/*  707 */         return TypeInfo.getTypeInfo(i, l, j, extTypeInfo);case 1: case 2: case 4: case 5: case 6: case 16: case 35: case 38: l = readInt();case 3: case 7: l = readLong();case 13: l = readInt(); j = readInt(); if (readBoolean())
/*      */           extTypeInfo = ExtTypeInfoNumeric.DECIMAL; case 14: case 15: case 22: case 23: case 24: case 25: case 26: case 28: case 29: case 30: case 32: l = readByte();case 18: case 19: case 20: case 21: j = readByte();case 27: case 31: case 33: case 34: l = readByte(); j = readByte();
/*      */       case 36: extTypeInfo = readTypeInfoEnum();
/*      */       case 37: extTypeInfo = readTypeInfoGeometry();
/*      */       case 40: l = readInt(); extTypeInfo = readTypeInfo();
/*  712 */       case 41: extTypeInfo = readTypeInfoRow(); }  throw DbException.getUnsupportedException("value type " + i); } private ExtTypeInfo readTypeInfoEnum() throws IOException { ExtTypeInfo extTypeInfo; int i = readInt();
/*  713 */     if (i > 0) {
/*  714 */       String[] arrayOfString = new String[i];
/*  715 */       for (byte b = 0; b < i; b++) {
/*  716 */         arrayOfString[b] = readString();
/*      */       }
/*  718 */       extTypeInfo = new ExtTypeInfoEnum(arrayOfString);
/*      */     } else {
/*  720 */       extTypeInfo = null;
/*      */     } 
/*  722 */     return extTypeInfo; }
/*      */ 
/*      */   
/*      */   private ExtTypeInfo readTypeInfoGeometry() throws IOException {
/*      */     ExtTypeInfo extTypeInfo;
/*  727 */     byte b = readByte();
/*  728 */     switch (b) {
/*      */       case 0:
/*  730 */         extTypeInfo = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  744 */         return extTypeInfo;case 1: extTypeInfo = new ExtTypeInfoGeometry(readShort(), null); return extTypeInfo;case 2: extTypeInfo = new ExtTypeInfoGeometry(0, Integer.valueOf(readInt())); return extTypeInfo;case 3: extTypeInfo = new ExtTypeInfoGeometry(readShort(), Integer.valueOf(readInt())); return extTypeInfo;
/*      */     } 
/*      */     throw DbException.getUnsupportedException("GEOMETRY type encoding " + b);
/*      */   } private ExtTypeInfo readTypeInfoRow() throws IOException {
/*  748 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>(); byte b; int i;
/*  749 */     for (b = 0, i = readInt(); b < i; b++) {
/*  750 */       String str = readString();
/*  751 */       if (linkedHashMap.putIfAbsent(str, readTypeInfo()) != null) {
/*  752 */         throw DbException.get(42121, str);
/*      */       }
/*      */     } 
/*  755 */     return new ExtTypeInfoRow((LinkedHashMap)linkedHashMap);
/*      */   }
/*      */   
/*      */   private TypeInfo readTypeInfo19() throws IOException {
/*  759 */     return TypeInfo.getTypeInfo(TI_TO_VALUE[readInt() + 1], readLong(), readInt(), null); } public void writeValue(Value paramValue) throws IOException { ValueUuid valueUuid; ValueTimeTimeZone valueTimeTimeZone; ValueTimestamp valueTimestamp;
/*      */     ValueTimestampTimeZone valueTimestampTimeZone;
/*      */     ValueBlob valueBlob;
/*      */     ValueClob valueClob;
/*      */     ValueArray valueArray;
/*      */     ValueRow valueRow;
/*      */     int j;
/*      */     LobData lobData;
/*      */     Value[] arrayOfValue;
/*      */     long l;
/*  769 */     int k, i = paramValue.getValueType();
/*  770 */     switch (i) {
/*      */       case 0:
/*  772 */         writeInt(0);
/*      */         return;
/*      */       case 5:
/*  775 */         if (this.version >= 20) {
/*  776 */           writeInt(30);
/*  777 */           writeBytes(paramValue.getBytesNoCopy());
/*      */           return;
/*      */         } 
/*      */       
/*      */       case 6:
/*  782 */         writeInt(12);
/*  783 */         writeBytes(paramValue.getBytesNoCopy());
/*      */         return;
/*      */       case 35:
/*  786 */         writeInt(19);
/*  787 */         writeBytes(paramValue.getBytesNoCopy());
/*      */         return;
/*      */       case 39:
/*  790 */         writeInt(20);
/*  791 */         valueUuid = (ValueUuid)paramValue;
/*  792 */         writeLong(valueUuid.getHigh());
/*  793 */         writeLong(valueUuid.getLow());
/*      */         return;
/*      */       
/*      */       case 8:
/*  797 */         writeInt(1);
/*  798 */         writeBoolean(paramValue.getBoolean());
/*      */         return;
/*      */       case 9:
/*  801 */         writeInt(2);
/*  802 */         writeByte(paramValue.getByte());
/*      */         return;
/*      */       case 18:
/*  805 */         writeInt(9);
/*  806 */         writeLong(((ValueTime)paramValue).getNanos());
/*      */         return;
/*      */       case 19:
/*  809 */         valueTimeTimeZone = (ValueTimeTimeZone)paramValue;
/*  810 */         if (this.version >= 19) {
/*  811 */           writeInt(29);
/*  812 */           writeLong(valueTimeTimeZone.getNanos());
/*  813 */           writeInt(valueTimeTimeZone.getTimeZoneOffsetSeconds());
/*      */         } else {
/*  815 */           writeInt(9);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  822 */           ValueTimestampTimeZone valueTimestampTimeZone1 = this.session.isRemote() ? DateTimeUtils.currentTimestamp(DateTimeUtils.getTimeZone()) : this.session.currentTimestamp();
/*  823 */           writeLong(DateTimeUtils.normalizeNanosOfDay(valueTimeTimeZone.getNanos() + (valueTimeTimeZone
/*  824 */                 .getTimeZoneOffsetSeconds() - valueTimestampTimeZone1.getTimeZoneOffsetSeconds()) * 86400000000000L));
/*      */         } 
/*      */         return;
/*      */ 
/*      */       
/*      */       case 17:
/*  830 */         writeInt(10);
/*  831 */         writeLong(((ValueDate)paramValue).getDateValue());
/*      */         return;
/*      */       case 20:
/*  834 */         writeInt(11);
/*  835 */         valueTimestamp = (ValueTimestamp)paramValue;
/*  836 */         writeLong(valueTimestamp.getDateValue());
/*  837 */         writeLong(valueTimestamp.getTimeNanos());
/*      */         return;
/*      */       
/*      */       case 21:
/*  841 */         writeInt(24);
/*  842 */         valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/*  843 */         writeLong(valueTimestampTimeZone.getDateValue());
/*  844 */         writeLong(valueTimestampTimeZone.getTimeNanos());
/*  845 */         j = valueTimestampTimeZone.getTimeZoneOffsetSeconds();
/*  846 */         writeInt((this.version >= 19) ? j : (j / 60));
/*      */         return;
/*      */ 
/*      */       
/*      */       case 16:
/*  851 */         if (this.version >= 20) {
/*  852 */           writeInt(31);
/*  853 */           writeString(paramValue.getString());
/*      */           return;
/*      */         } 
/*      */       
/*      */       case 13:
/*  858 */         writeInt(6);
/*  859 */         writeString(paramValue.getString());
/*      */         return;
/*      */       case 15:
/*  862 */         writeInt(7);
/*  863 */         writeDouble(paramValue.getDouble());
/*      */         return;
/*      */       case 14:
/*  866 */         writeInt(8);
/*  867 */         writeFloat(paramValue.getFloat());
/*      */         return;
/*      */       case 11:
/*  870 */         writeInt(4);
/*  871 */         writeInt(paramValue.getInt());
/*      */         return;
/*      */       case 12:
/*  874 */         writeInt(5);
/*  875 */         writeLong(paramValue.getLong());
/*      */         return;
/*      */       case 10:
/*  878 */         writeInt(3);
/*  879 */         if (this.version >= 20) {
/*  880 */           writeShort(paramValue.getShort());
/*      */         } else {
/*  882 */           writeInt(paramValue.getShort());
/*      */         } 
/*      */         return;
/*      */       case 2:
/*  886 */         writeInt(13);
/*  887 */         writeString(paramValue.getString());
/*      */         return;
/*      */       case 4:
/*  890 */         writeInt(14);
/*  891 */         writeString(paramValue.getString());
/*      */         return;
/*      */       case 1:
/*  894 */         writeInt(21);
/*  895 */         writeString(paramValue.getString());
/*      */         return;
/*      */       case 7:
/*  898 */         writeInt(15);
/*  899 */         valueBlob = (ValueBlob)paramValue;
/*  900 */         lobData = valueBlob.getLobData();
/*  901 */         l = valueBlob.octetLength();
/*  902 */         if (lobData instanceof LobDataDatabase) {
/*  903 */           LobDataDatabase lobDataDatabase = (LobDataDatabase)lobData;
/*  904 */           writeLong(-1L);
/*  905 */           writeInt(lobDataDatabase.getTableId());
/*  906 */           writeLong(lobDataDatabase.getLobId());
/*  907 */           writeBytes(calculateLobMac(lobDataDatabase.getLobId()));
/*  908 */           writeLong(l);
/*      */         } else {
/*      */           
/*  911 */           if (l < 0L) {
/*  912 */             throw DbException.get(90067, "length=" + l);
/*      */           }
/*      */           
/*  915 */           writeLong(l);
/*  916 */           long l1 = IOUtils.copyAndCloseInput(valueBlob.getInputStream(), this.out);
/*  917 */           if (l1 != l) {
/*  918 */             throw DbException.get(90067, "length:" + l + " written:" + l1);
/*      */           }
/*      */           
/*  921 */           writeInt(4660);
/*      */         } 
/*      */         return;
/*      */       case 3:
/*  925 */         writeInt(16);
/*  926 */         valueClob = (ValueClob)paramValue;
/*  927 */         lobData = valueClob.getLobData();
/*  928 */         l = valueClob.charLength();
/*  929 */         if (lobData instanceof LobDataDatabase) {
/*  930 */           LobDataDatabase lobDataDatabase = (LobDataDatabase)lobData;
/*  931 */           writeLong(-1L);
/*  932 */           writeInt(lobDataDatabase.getTableId());
/*  933 */           writeLong(lobDataDatabase.getLobId());
/*  934 */           writeBytes(calculateLobMac(lobDataDatabase.getLobId()));
/*  935 */           if (this.version >= 20) {
/*  936 */             writeLong(valueClob.octetLength());
/*      */           }
/*  938 */           writeLong(l);
/*      */         } else {
/*      */           
/*  941 */           if (l < 0L) {
/*  942 */             throw DbException.get(90067, "length=" + l);
/*      */           }
/*      */           
/*  945 */           writeLong(l);
/*  946 */           Reader reader = valueClob.getReader();
/*  947 */           Data.copyString(reader, this.out);
/*  948 */           writeInt(4660);
/*      */         } 
/*      */         return;
/*      */       case 40:
/*  952 */         writeInt(17);
/*  953 */         valueArray = (ValueArray)paramValue;
/*  954 */         arrayOfValue = valueArray.getList();
/*  955 */         k = arrayOfValue.length;
/*  956 */         writeInt(k);
/*  957 */         for (Value value : arrayOfValue) {
/*  958 */           writeValue(value);
/*      */         }
/*      */         return;
/*      */       
/*      */       case 41:
/*  963 */         writeInt((this.version >= 18) ? 27 : 17);
/*  964 */         valueRow = (ValueRow)paramValue;
/*  965 */         arrayOfValue = valueRow.getList();
/*  966 */         k = arrayOfValue.length;
/*  967 */         writeInt(k);
/*  968 */         for (Value value : arrayOfValue) {
/*  969 */           writeValue(value);
/*      */         }
/*      */         return;
/*      */       
/*      */       case 36:
/*  974 */         writeInt(25);
/*  975 */         writeInt(paramValue.getInt());
/*  976 */         if (this.version < 20) {
/*  977 */           writeString(paramValue.getString());
/*      */         }
/*      */         return;
/*      */       
/*      */       case 37:
/*  982 */         writeInt(22);
/*  983 */         writeBytes(paramValue.getBytesNoCopy());
/*      */         return;
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/*  990 */         if (this.version >= 18) {
/*  991 */           ValueInterval valueInterval = (ValueInterval)paramValue;
/*  992 */           int m = i - 22;
/*  993 */           if (valueInterval.isNegative()) {
/*  994 */             m ^= 0xFFFFFFFF;
/*      */           }
/*  996 */           writeInt(26);
/*  997 */           writeByte((byte)m);
/*  998 */           writeLong(valueInterval.getLeading());
/*      */         } else {
/* 1000 */           writeInt(13);
/* 1001 */           writeString(paramValue.getString());
/*      */         } 
/*      */         return;
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/*      */       case 34:
/* 1012 */         if (this.version >= 18) {
/* 1013 */           ValueInterval valueInterval = (ValueInterval)paramValue;
/* 1014 */           int m = i - 22;
/* 1015 */           if (valueInterval.isNegative()) {
/* 1016 */             m ^= 0xFFFFFFFF;
/*      */           }
/* 1018 */           writeInt(26);
/* 1019 */           writeByte((byte)m);
/* 1020 */           writeLong(valueInterval.getLeading());
/* 1021 */           writeLong(valueInterval.getRemaining());
/*      */         } else {
/* 1023 */           writeInt(13);
/* 1024 */           writeString(paramValue.getString());
/*      */         } 
/*      */         return;
/*      */       case 38:
/* 1028 */         writeInt(28);
/* 1029 */         writeBytes(paramValue.getBytesNoCopy());
/*      */         return;
/*      */     } 
/*      */     
/* 1033 */     throw DbException.get(90067, "type=" + i); }
/*      */    public Value readValue(TypeInfo paramTypeInfo) throws IOException {
/*      */     long l2;
/*      */     int k;
/*      */     long l1;
/*      */     int j;
/*      */     String str;
/*      */     Value[] arrayOfValue;
/*      */     boolean bool;
/*      */     long l3;
/*      */     ValueBlob valueBlob;
/*      */     ValueClob valueClob;
/* 1045 */     int m, n, i = readInt();
/* 1046 */     switch (i) {
/*      */       case 0:
/* 1048 */         return ValueNull.INSTANCE;
/*      */       case 12:
/* 1050 */         return ValueVarbinary.getNoCopy(readBytes());
/*      */       case 30:
/* 1052 */         return ValueBinary.getNoCopy(readBytes());
/*      */       case 20:
/* 1054 */         return ValueUuid.get(readLong(), readLong());
/*      */       case 19:
/* 1056 */         return ValueJavaObject.getNoCopy(readBytes());
/*      */       case 1:
/* 1058 */         return ValueBoolean.get(readBoolean());
/*      */       case 2:
/* 1060 */         return ValueTinyint.get(readByte());
/*      */       case 10:
/* 1062 */         return ValueDate.fromDateValue(readLong());
/*      */       case 9:
/* 1064 */         return ValueTime.fromNanos(readLong());
/*      */       case 29:
/* 1066 */         return ValueTimeTimeZone.fromNanos(readLong(), readInt());
/*      */       case 11:
/* 1068 */         return ValueTimestamp.fromDateValueAndNanos(readLong(), readLong());
/*      */       case 24:
/* 1070 */         l2 = readLong(); l3 = readLong();
/* 1071 */         n = readInt();
/* 1072 */         return ValueTimestampTimeZone.fromDateValueAndNanos(l2, l3, (this.version >= 19) ? n : (n * 60));
/*      */ 
/*      */       
/*      */       case 6:
/* 1076 */         return ValueNumeric.get(new BigDecimal(readString()));
/*      */       case 7:
/* 1078 */         return ValueDouble.get(readDouble());
/*      */       case 8:
/* 1080 */         return ValueReal.get(readFloat());
/*      */       case 25:
/* 1082 */         k = readInt();
/* 1083 */         if (this.version >= 20) {
/* 1084 */           return ((ExtTypeInfoEnum)paramTypeInfo.getExtTypeInfo()).getValue(k, (CastDataProvider)this.session);
/*      */         }
/* 1086 */         return ValueEnumBase.get(readString(), k);
/*      */       
/*      */       case 4:
/* 1089 */         return ValueInteger.get(readInt());
/*      */       case 5:
/* 1091 */         return ValueBigint.get(readLong());
/*      */       case 3:
/* 1093 */         if (this.version >= 20) {
/* 1094 */           return ValueSmallint.get(readShort());
/*      */         }
/* 1096 */         return ValueSmallint.get((short)readInt());
/*      */       
/*      */       case 13:
/* 1099 */         return ValueVarchar.get(readString());
/*      */       case 14:
/* 1101 */         return ValueVarcharIgnoreCase.get(readString());
/*      */       case 21:
/* 1103 */         return ValueChar.get(readString());
/*      */       case 15:
/* 1105 */         l1 = readLong();
/* 1106 */         if (l1 == -1L) {
/*      */           
/* 1108 */           int i1 = readInt();
/* 1109 */           long l4 = readLong();
/* 1110 */           byte[] arrayOfByte = readBytes();
/* 1111 */           long l5 = readLong();
/* 1112 */           return new ValueBlob((LobData)new LobDataFetchOnDemand(this.session.getDataHandler(), i1, l4, arrayOfByte), l5);
/*      */         } 
/* 1114 */         valueBlob = this.session.getDataHandler().getLobStorage().createBlob(this.in, l1);
/* 1115 */         m = readInt();
/* 1116 */         if (m != 4660) {
/* 1117 */           throw DbException.get(90067, "magic=" + m);
/*      */         }
/*      */         
/* 1120 */         return valueBlob;
/*      */       
/*      */       case 16:
/* 1123 */         l1 = readLong();
/* 1124 */         if (l1 == -1L) {
/*      */           
/* 1126 */           int i1 = readInt();
/* 1127 */           long l4 = readLong();
/* 1128 */           byte[] arrayOfByte = readBytes();
/* 1129 */           long l5 = (this.version >= 20) ? readLong() : -1L;
/* 1130 */           l1 = readLong();
/* 1131 */           return new ValueClob((LobData)new LobDataFetchOnDemand(this.session.getDataHandler(), i1, l4, arrayOfByte), l5, l1);
/*      */         } 
/*      */         
/* 1134 */         if (l1 < 0L) {
/* 1135 */           throw DbException.get(90067, "length=" + l1);
/*      */         }
/*      */ 
/*      */         
/* 1139 */         valueClob = this.session.getDataHandler().getLobStorage().createClob((Reader)new DataReader(this.in), l1);
/* 1140 */         m = readInt();
/* 1141 */         if (m != 4660) {
/* 1142 */           throw DbException.get(90067, "magic=" + m);
/*      */         }
/*      */         
/* 1145 */         return valueClob;
/*      */       
/*      */       case 17:
/* 1148 */         j = readInt();
/* 1149 */         if (j < 0) {
/*      */           
/* 1151 */           j ^= 0xFFFFFFFF;
/* 1152 */           readString();
/*      */         } 
/* 1154 */         if (paramTypeInfo != null) {
/* 1155 */           TypeInfo typeInfo = (TypeInfo)paramTypeInfo.getExtTypeInfo();
/* 1156 */           return ValueArray.get(typeInfo, readArrayElements(j, typeInfo), (CastDataProvider)this.session);
/*      */         } 
/* 1158 */         return ValueArray.get(readArrayElements(j, null), (CastDataProvider)this.session);
/*      */       
/*      */       case 27:
/* 1161 */         j = readInt();
/* 1162 */         arrayOfValue = new Value[j];
/* 1163 */         if (paramTypeInfo != null) {
/* 1164 */           ExtTypeInfoRow extTypeInfoRow = (ExtTypeInfoRow)paramTypeInfo.getExtTypeInfo();
/* 1165 */           Iterator<Map.Entry<String, TypeInfo>> iterator = extTypeInfoRow.getFields().iterator();
/* 1166 */           for (n = 0; n < j; n++) {
/* 1167 */             arrayOfValue[n] = readValue((TypeInfo)((Map.Entry)iterator.next()).getValue());
/*      */           }
/* 1169 */           return ValueRow.get(paramTypeInfo, arrayOfValue);
/*      */         } 
/* 1171 */         for (null = 0; null < j; null++) {
/* 1172 */           arrayOfValue[null] = readValue(null);
/*      */         }
/* 1174 */         return ValueRow.get(arrayOfValue);
/*      */       
/*      */       case 22:
/* 1177 */         return ValueGeometry.get(readBytes());
/*      */       case 26:
/* 1179 */         j = readByte();
/* 1180 */         bool = (j < 0) ? true : false;
/* 1181 */         if (bool) {
/* 1182 */           j ^= 0xFFFFFFFF;
/*      */         }
/* 1184 */         return ValueInterval.from(IntervalQualifier.valueOf(j), bool, readLong(), (j < 5) ? 0L : 
/* 1185 */             readLong());
/*      */ 
/*      */       
/*      */       case 28:
/* 1189 */         return ValueJson.fromJson(readBytes());
/*      */       case 31:
/* 1191 */         str = readString();
/* 1192 */         switch (str) {
/*      */           case "-Infinity":
/* 1194 */             return ValueDecfloat.NEGATIVE_INFINITY;
/*      */           case "Infinity":
/* 1196 */             return ValueDecfloat.POSITIVE_INFINITY;
/*      */           case "NaN":
/* 1198 */             return ValueDecfloat.NAN;
/*      */         } 
/* 1200 */         return ValueDecfloat.get(new BigDecimal(str));
/*      */     } 
/*      */ 
/*      */     
/* 1204 */     throw DbException.get(90067, "type=" + i);
/*      */   }
/*      */ 
/*      */   
/*      */   private Value[] readArrayElements(int paramInt, TypeInfo paramTypeInfo) throws IOException {
/* 1209 */     Value[] arrayOfValue = new Value[paramInt];
/* 1210 */     for (byte b = 0; b < paramInt; b++) {
/* 1211 */       arrayOfValue[b] = readValue(paramTypeInfo);
/*      */     }
/* 1213 */     return arrayOfValue;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long readRowCount() throws IOException {
/* 1223 */     return (this.version >= 20) ? readLong() : readInt();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer writeRowCount(long paramLong) throws IOException {
/* 1234 */     return (this.version >= 20) ? writeLong(paramLong) : 
/* 1235 */       writeInt((paramLong < 2147483647L) ? (int)paramLong : Integer.MAX_VALUE);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Socket getSocket() {
/* 1244 */     return this.socket;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSession(Session paramSession) {
/* 1253 */     this.session = paramSession;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setSSL(boolean paramBoolean) {
/* 1262 */     this.ssl = paramBoolean;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Transfer openNewConnection() throws IOException {
/* 1272 */     InetAddress inetAddress = this.socket.getInetAddress();
/* 1273 */     int i = this.socket.getPort();
/* 1274 */     Socket socket = NetUtils.createSocket(inetAddress, i, this.ssl);
/* 1275 */     Transfer transfer = new Transfer(null, socket);
/* 1276 */     transfer.setSSL(this.ssl);
/* 1277 */     return transfer;
/*      */   }
/*      */   
/*      */   public void setVersion(int paramInt) {
/* 1281 */     this.version = paramInt;
/*      */   }
/*      */   
/*      */   public int getVersion() {
/* 1285 */     return this.version;
/*      */   }
/*      */   
/*      */   public synchronized boolean isClosed() {
/* 1289 */     return (this.socket == null || this.socket.isClosed());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void verifyLobMac(byte[] paramArrayOfbyte, long paramLong) {
/* 1300 */     byte[] arrayOfByte = calculateLobMac(paramLong);
/* 1301 */     if (!Utils.compareSecure(paramArrayOfbyte, arrayOfByte)) {
/* 1302 */       throw DbException.get(90067, "Invalid lob hmac; possibly the connection was re-opened internally");
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private byte[] calculateLobMac(long paramLong) {
/* 1308 */     if (this.lobMacSalt == null) {
/* 1309 */       this.lobMacSalt = MathUtils.secureRandomBytes(16);
/*      */     }
/* 1311 */     byte[] arrayOfByte = new byte[8];
/* 1312 */     Bits.writeLong(arrayOfByte, 0, paramLong);
/* 1313 */     return SHA256.getHashWithSalt(arrayOfByte, this.lobMacSalt);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\Transfer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */