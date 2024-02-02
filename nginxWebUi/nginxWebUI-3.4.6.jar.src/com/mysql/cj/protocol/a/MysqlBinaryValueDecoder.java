/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.protocol.ValueDecoder;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ public class MysqlBinaryValueDecoder
/*     */   implements ValueDecoder
/*     */ {
/*     */   public <T> T decodeTimestamp(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*  51 */     if (length == 0)
/*  52 */       return (T)vf.createFromTimestamp(new InternalTimestamp()); 
/*  53 */     if (length != 4 && length != 11 && length != 7)
/*     */     {
/*     */       
/*  56 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIMESTAMP" }));
/*     */     }
/*     */     
/*  59 */     int year = 0;
/*  60 */     int month = 0;
/*  61 */     int day = 0;
/*     */     
/*  63 */     int hours = 0;
/*  64 */     int minutes = 0;
/*  65 */     int seconds = 0;
/*     */     
/*  67 */     int nanos = 0;
/*     */     
/*  69 */     year = bytes[offset + 0] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
/*  70 */     month = bytes[offset + 2];
/*  71 */     day = bytes[offset + 3];
/*     */     
/*  73 */     if (length > 4) {
/*  74 */       hours = bytes[offset + 4];
/*  75 */       minutes = bytes[offset + 5];
/*  76 */       seconds = bytes[offset + 6];
/*     */     } 
/*     */     
/*  79 */     if (length > 7)
/*     */     {
/*  81 */       nanos = 1000 * (bytes[offset + 7] & 0xFF | (bytes[offset + 8] & 0xFF) << 8 | (bytes[offset + 9] & 0xFF) << 16 | (bytes[offset + 10] & 0xFF) << 24);
/*     */     }
/*     */ 
/*     */     
/*  85 */     return (T)vf.createFromTimestamp(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
/*     */   }
/*     */   
/*     */   public <T> T decodeDatetime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*  89 */     if (length == 0)
/*  90 */       return (T)vf.createFromTimestamp(new InternalTimestamp()); 
/*  91 */     if (length != 4 && length != 11 && length != 7)
/*     */     {
/*     */       
/*  94 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIMESTAMP" }));
/*     */     }
/*     */     
/*  97 */     int year = 0;
/*  98 */     int month = 0;
/*  99 */     int day = 0;
/*     */     
/* 101 */     int hours = 0;
/* 102 */     int minutes = 0;
/* 103 */     int seconds = 0;
/*     */     
/* 105 */     int nanos = 0;
/*     */     
/* 107 */     year = bytes[offset + 0] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
/* 108 */     month = bytes[offset + 2];
/* 109 */     day = bytes[offset + 3];
/*     */     
/* 111 */     if (length > 4) {
/* 112 */       hours = bytes[offset + 4];
/* 113 */       minutes = bytes[offset + 5];
/* 114 */       seconds = bytes[offset + 6];
/*     */     } 
/*     */     
/* 117 */     if (length > 7)
/*     */     {
/* 119 */       nanos = 1000 * (bytes[offset + 7] & 0xFF | (bytes[offset + 8] & 0xFF) << 8 | (bytes[offset + 9] & 0xFF) << 16 | (bytes[offset + 10] & 0xFF) << 24);
/*     */     }
/*     */ 
/*     */     
/* 123 */     return (T)vf.createFromDatetime(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
/*     */   }
/*     */   
/*     */   public <T> T decodeTime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/* 127 */     if (length == 0)
/* 128 */       return (T)vf.createFromTime(new InternalTime()); 
/* 129 */     if (length != 12 && length != 8) {
/* 130 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIME" }));
/*     */     }
/*     */     
/* 133 */     int days = 0;
/* 134 */     int hours = 0;
/* 135 */     int minutes = 0;
/* 136 */     int seconds = 0;
/* 137 */     int nanos = 0;
/*     */     
/* 139 */     boolean negative = (bytes[offset] == 1);
/*     */     
/* 141 */     days = bytes[offset + 1] & 0xFF | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF) << 16 | (bytes[offset + 4] & 0xFF) << 24;
/* 142 */     hours = bytes[offset + 5];
/* 143 */     minutes = bytes[offset + 6];
/* 144 */     seconds = bytes[offset + 7];
/*     */     
/* 146 */     if (negative) {
/* 147 */       days *= -1;
/*     */     }
/*     */     
/* 150 */     if (length > 8)
/*     */     {
/* 152 */       nanos = 1000 * (bytes[offset + 8] & 0xFF | (bytes[offset + 9] & 0xFF) << 8 | (bytes[offset + 10] & 0xFF) << 16 | (bytes[offset + 11] & 0xFF) << 24);
/*     */     }
/*     */ 
/*     */     
/* 156 */     return (T)vf.createFromTime(new InternalTime(days * 24 + hours, minutes, seconds, nanos, scale));
/*     */   }
/*     */   
/*     */   public <T> T decodeDate(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 160 */     if (length == 0)
/* 161 */       return (T)vf.createFromDate(new InternalDate()); 
/* 162 */     if (length != 4) {
/* 163 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "DATE" }));
/*     */     }
/* 165 */     int year = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
/* 166 */     int month = bytes[offset + 2];
/* 167 */     int day = bytes[offset + 3];
/* 168 */     return (T)vf.createFromDate(new InternalDate(year, month, day));
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 172 */     if (length != 1) {
/* 173 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "BYTE" }));
/*     */     }
/* 175 */     return (T)vf.createFromLong((bytes[offset] & 0xFF));
/*     */   }
/*     */   
/*     */   public <T> T decodeInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 179 */     if (length != 1) {
/* 180 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "BYTE" }));
/*     */     }
/* 182 */     return (T)vf.createFromLong(bytes[offset]);
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 186 */     if (length != 2) {
/* 187 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "SHORT" }));
/*     */     }
/* 189 */     int asInt = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8;
/* 190 */     return (T)vf.createFromLong(asInt);
/*     */   }
/*     */   
/*     */   public <T> T decodeInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 194 */     if (length != 2) {
/* 195 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "SHORT" }));
/*     */     }
/* 197 */     short asShort = (short)(bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8);
/* 198 */     return (T)vf.createFromLong(asShort);
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 202 */     if (length != 4) {
/* 203 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "INT" }));
/*     */     }
/* 205 */     long asLong = (bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16) | (bytes[offset + 3] & 0xFF) << 24L;
/*     */     
/* 207 */     return (T)vf.createFromLong(asLong);
/*     */   }
/*     */   
/*     */   public <T> T decodeInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 211 */     if (length != 4) {
/* 212 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "SHORT" }));
/*     */     }
/* 214 */     int asInt = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | (bytes[offset + 3] & 0xFF) << 24;
/* 215 */     return (T)vf.createFromLong(asInt);
/*     */   }
/*     */   
/*     */   public <T> T decodeInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 219 */     if (length != 8) {
/* 220 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "LONG" }));
/*     */     }
/* 222 */     long asLong = (bytes[offset] & 0xFF) | (bytes[offset + 1] & 0xFF) << 8L | (bytes[offset + 2] & 0xFF) << 16L | (bytes[offset + 3] & 0xFF) << 24L | (bytes[offset + 4] & 0xFF) << 32L | (bytes[offset + 5] & 0xFF) << 40L | (bytes[offset + 6] & 0xFF) << 48L | (bytes[offset + 7] & 0xFF) << 56L;
/*     */ 
/*     */     
/* 225 */     return (T)vf.createFromLong(asLong);
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 229 */     if (length != 8) {
/* 230 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "LONG" }));
/*     */     }
/*     */ 
/*     */     
/* 234 */     if ((bytes[offset + 7] & 0x80) == 0) {
/* 235 */       return decodeInt8(bytes, offset, length, vf);
/*     */     }
/*     */ 
/*     */     
/* 239 */     byte[] bigEndian = { 0, bytes[offset + 7], bytes[offset + 6], bytes[offset + 5], bytes[offset + 4], bytes[offset + 3], bytes[offset + 2], bytes[offset + 1], bytes[offset] };
/*     */     
/* 241 */     BigInteger bigInt = new BigInteger(bigEndian);
/* 242 */     return (T)vf.createFromBigInteger(bigInt);
/*     */   }
/*     */   
/*     */   public <T> T decodeFloat(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 246 */     if (length != 4) {
/* 247 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "FLOAT" }));
/*     */     }
/* 249 */     int asInt = bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8 | (bytes[offset + 2] & 0xFF) << 16 | (bytes[offset + 3] & 0xFF) << 24;
/* 250 */     return (T)vf.createFromDouble(Float.intBitsToFloat(asInt));
/*     */   }
/*     */   
/*     */   public <T> T decodeDouble(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 254 */     if (length != 8) {
/* 255 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "DOUBLE" }));
/*     */     }
/* 257 */     long valueAsLong = (bytes[offset + 0] & 0xFF) | (bytes[offset + 1] & 0xFF) << 8L | (bytes[offset + 2] & 0xFF) << 16L | (bytes[offset + 3] & 0xFF) << 24L | (bytes[offset + 4] & 0xFF) << 32L | (bytes[offset + 5] & 0xFF) << 40L | (bytes[offset + 6] & 0xFF) << 48L | (bytes[offset + 7] & 0xFF) << 56L;
/*     */ 
/*     */     
/* 260 */     return (T)vf.createFromDouble(Double.longBitsToDouble(valueAsLong));
/*     */   }
/*     */   
/*     */   public <T> T decodeDecimal(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 264 */     BigDecimal d = new BigDecimal(StringUtils.toAsciiString(bytes, offset, length));
/* 265 */     return (T)vf.createFromBigDecimal(d);
/*     */   }
/*     */   
/*     */   public <T> T decodeByteArray(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
/* 269 */     return (T)vf.createFromBytes(bytes, offset, length, f);
/*     */   }
/*     */   
/*     */   public <T> T decodeBit(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 273 */     return (T)vf.createFromBit(bytes, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeSet(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
/* 278 */     return decodeByteArray(bytes, offset, length, f, vf);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeYear(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 283 */     if (length != 2) {
/* 284 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "YEAR" }));
/*     */     }
/* 286 */     short asShort = (short)(bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8);
/* 287 */     return (T)vf.createFromYear(asShort);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\MysqlBinaryValueDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */