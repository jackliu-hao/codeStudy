/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.exceptions.NumberOutOfRange;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.protocol.ValueDecoder;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MysqlTextValueDecoder
/*     */   implements ValueDecoder
/*     */ {
/*     */   public static final int DATE_BUF_LEN = 10;
/*     */   public static final int TIME_STR_LEN_MIN = 8;
/*     */   public static final int TIME_STR_LEN_MAX_NO_FRAC = 10;
/*     */   public static final int TIME_STR_LEN_MAX_WITH_MICROS = 17;
/*     */   public static final int TIMESTAMP_STR_LEN_NO_FRAC = 19;
/*     */   public static final int TIMESTAMP_STR_LEN_WITH_MICROS = 26;
/*     */   public static final int TIMESTAMP_STR_LEN_WITH_NANOS = 29;
/*  71 */   public static final Pattern TIME_PTRN = Pattern.compile("[-]{0,1}\\d{2,3}:\\d{2}:\\d{2}(\\.\\d{1,9})?");
/*     */   
/*     */   public static final int MAX_SIGNED_LONG_LEN = 20;
/*     */ 
/*     */   
/*     */   public <T> T decodeDate(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*  77 */     return (T)vf.createFromDate(getDate(bytes, offset, length));
/*     */   }
/*     */   
/*     */   public <T> T decodeTime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*  81 */     return (T)vf.createFromTime(getTime(bytes, offset, length, scale));
/*     */   }
/*     */   
/*     */   public <T> T decodeTimestamp(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*  85 */     return (T)vf.createFromTimestamp(getTimestamp(bytes, offset, length, scale));
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeDatetime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*  90 */     return (T)vf.createFromDatetime(getTimestamp(bytes, offset, length, scale));
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*  94 */     return (T)vf.createFromLong(getInt(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public <T> T decodeInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*  98 */     return (T)vf.createFromLong(getInt(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 102 */     return (T)vf.createFromLong(getInt(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public <T> T decodeInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 106 */     return (T)vf.createFromLong(getInt(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public <T> T decodeUInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 110 */     return (T)vf.createFromLong(getLong(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public <T> T decodeInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 114 */     return (T)vf.createFromLong(getInt(bytes, offset, offset + length));
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeUInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 119 */     if (length <= 19 && bytes[offset] >= 48 && bytes[offset] <= 56) {
/* 120 */       return decodeInt8(bytes, offset, length, vf);
/*     */     }
/* 122 */     return (T)vf.createFromBigInteger(getBigInteger(bytes, offset, length));
/*     */   }
/*     */   
/*     */   public <T> T decodeInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 126 */     return (T)vf.createFromLong(getLong(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public <T> T decodeFloat(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 130 */     return decodeDouble(bytes, offset, length, vf);
/*     */   }
/*     */   
/*     */   public <T> T decodeDouble(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 134 */     return (T)vf.createFromDouble(getDouble(bytes, offset, length).doubleValue());
/*     */   }
/*     */   
/*     */   public <T> T decodeDecimal(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 138 */     BigDecimal d = new BigDecimal(StringUtils.toAsciiString(bytes, offset, length));
/* 139 */     return (T)vf.createFromBigDecimal(d);
/*     */   }
/*     */   
/*     */   public <T> T decodeByteArray(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
/* 143 */     return (T)vf.createFromBytes(bytes, offset, length, f);
/*     */   }
/*     */   
/*     */   public <T> T decodeBit(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 147 */     return (T)vf.createFromBit(bytes, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeSet(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
/* 152 */     return decodeByteArray(bytes, offset, length, f, vf);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeYear(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 157 */     return (T)vf.createFromYear(getLong(bytes, offset, offset + length));
/*     */   }
/*     */   
/*     */   public static int getInt(byte[] buf, int offset, int endpos) throws NumberFormatException {
/* 161 */     long l = getLong(buf, offset, endpos);
/* 162 */     if (l < -2147483648L || l > 2147483647L) {
/* 163 */       throw new NumberOutOfRange(Messages.getString("StringUtils.badIntFormat", new Object[] { StringUtils.toString(buf, offset, endpos - offset) }));
/*     */     }
/* 165 */     return (int)l;
/*     */   }
/*     */   
/*     */   public static long getLong(byte[] buf, int offset, int endpos) throws NumberFormatException {
/* 169 */     int base = 10;
/*     */     
/* 171 */     int s = offset;
/*     */ 
/*     */     
/* 174 */     while (s < endpos && Character.isWhitespace((char)buf[s])) {
/* 175 */       s++;
/*     */     }
/*     */     
/* 178 */     if (s == endpos) {
/* 179 */       throw new NumberFormatException(StringUtils.toString(buf));
/*     */     }
/*     */ 
/*     */     
/* 183 */     boolean negative = false;
/*     */     
/* 185 */     if ((char)buf[s] == '-') {
/* 186 */       negative = true;
/* 187 */       s++;
/* 188 */     } else if ((char)buf[s] == '+') {
/* 189 */       s++;
/*     */     } 
/*     */ 
/*     */     
/* 193 */     int save = s;
/*     */     
/* 195 */     long cutoff = Long.MAX_VALUE / base;
/* 196 */     long cutlim = (int)(Long.MAX_VALUE % base);
/*     */     
/* 198 */     if (negative) {
/* 199 */       cutlim++;
/*     */     }
/*     */     
/* 202 */     boolean overflow = false;
/* 203 */     long i = 0L;
/*     */     
/* 205 */     for (; s < endpos; s++) {
/* 206 */       char c = (char)buf[s];
/*     */       
/* 208 */       if (c >= '0' && c <= '9') {
/* 209 */         c = (char)(c - 48);
/* 210 */       } else if (Character.isLetter(c)) {
/* 211 */         c = (char)(Character.toUpperCase(c) - 65 + 10);
/*     */       } else {
/*     */         break;
/*     */       } 
/*     */       
/* 216 */       if (c >= base) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 221 */       if (i > cutoff || (i == cutoff && c > cutlim)) {
/* 222 */         overflow = true;
/*     */       } else {
/* 224 */         i *= base;
/* 225 */         i += c;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 230 */     if (s == save) {
/* 231 */       throw new NumberFormatException(
/* 232 */           Messages.getString("StringUtils.badIntFormat", new Object[] { StringUtils.toString(buf, offset, endpos - offset) }));
/*     */     }
/*     */     
/* 235 */     if (overflow) {
/* 236 */       throw new NumberOutOfRange(Messages.getString("StringUtils.badIntFormat", new Object[] { StringUtils.toString(buf, offset, endpos - offset) }));
/*     */     }
/*     */ 
/*     */     
/* 240 */     return negative ? -i : i;
/*     */   }
/*     */   
/*     */   public static BigInteger getBigInteger(byte[] buf, int offset, int length) throws NumberFormatException {
/* 244 */     BigInteger i = new BigInteger(StringUtils.toAsciiString(buf, offset, length));
/* 245 */     return i;
/*     */   }
/*     */   
/*     */   public static Double getDouble(byte[] bytes, int offset, int length) {
/* 249 */     return Double.valueOf(Double.parseDouble(StringUtils.toAsciiString(bytes, offset, length)));
/*     */   }
/*     */   
/*     */   public static boolean isDate(String s) {
/* 253 */     return (s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-');
/*     */   }
/*     */   
/*     */   public static boolean isTime(String s) {
/* 257 */     Matcher matcher = TIME_PTRN.matcher(s);
/* 258 */     return matcher.matches();
/*     */   }
/*     */   
/*     */   public static boolean isTimestamp(String s) {
/* 262 */     Pattern DATETIME_PTRN = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d{1,9}){0,1}");
/* 263 */     Matcher matcher = DATETIME_PTRN.matcher(s);
/* 264 */     return matcher.matches();
/*     */   }
/*     */   
/*     */   public static InternalDate getDate(byte[] bytes, int offset, int length) {
/* 268 */     if (length != 10) {
/* 269 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "DATE" }));
/*     */     }
/* 271 */     int year = getInt(bytes, offset, offset + 4);
/* 272 */     int month = getInt(bytes, offset + 5, offset + 7);
/* 273 */     int day = getInt(bytes, offset + 8, offset + 10);
/* 274 */     return new InternalDate(year, month, day);
/*     */   }
/*     */   
/*     */   public static InternalTime getTime(byte[] bytes, int offset, int length, int scale) {
/* 278 */     int pos = 0;
/*     */ 
/*     */ 
/*     */     
/* 282 */     if (length < 8 || length > 17) {
/* 283 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIME" }));
/*     */     }
/*     */     
/* 286 */     boolean negative = false;
/*     */     
/* 288 */     if (bytes[offset] == 45) {
/* 289 */       pos++;
/* 290 */       negative = true;
/*     */     } 
/*     */     
/*     */     int segmentLen;
/* 294 */     for (segmentLen = 0; Character.isDigit((char)bytes[offset + pos + segmentLen]); segmentLen++);
/*     */ 
/*     */     
/* 297 */     if (segmentLen == 0 || bytes[offset + pos + segmentLen] != 58) {
/* 298 */       throw new DataReadException(
/* 299 */           Messages.getString("ResultSet.InvalidFormatForType", new Object[] { "TIME", StringUtils.toString(bytes, offset, length) }));
/*     */     }
/* 301 */     int hours = getInt(bytes, offset + pos, offset + pos + segmentLen);
/* 302 */     if (negative) {
/* 303 */       hours *= -1;
/*     */     }
/* 305 */     pos += segmentLen + 1;
/*     */ 
/*     */     
/* 308 */     for (segmentLen = 0; Character.isDigit((char)bytes[offset + pos + segmentLen]); segmentLen++);
/*     */ 
/*     */     
/* 311 */     if (segmentLen != 2 || bytes[offset + pos + segmentLen] != 58) {
/* 312 */       throw new DataReadException(
/* 313 */           Messages.getString("ResultSet.InvalidFormatForType", new Object[] { "TIME", StringUtils.toString(bytes, offset, length) }));
/*     */     }
/* 315 */     int minutes = getInt(bytes, offset + pos, offset + pos + segmentLen);
/* 316 */     pos += segmentLen + 1;
/*     */ 
/*     */     
/* 319 */     for (segmentLen = 0; offset + pos + segmentLen < offset + length && Character.isDigit((char)bytes[offset + pos + segmentLen]); segmentLen++);
/*     */ 
/*     */     
/* 322 */     if (segmentLen != 2) {
/* 323 */       throw new DataReadException(
/* 324 */           Messages.getString("ResultSet.InvalidFormatForType", new Object[] { StringUtils.toString(bytes, offset, length), "TIME" }));
/*     */     }
/* 326 */     int seconds = getInt(bytes, offset + pos, offset + pos + segmentLen);
/* 327 */     pos += segmentLen;
/*     */ 
/*     */     
/* 330 */     int nanos = 0;
/* 331 */     if (length > pos) {
/* 332 */       pos++;
/*     */       
/* 334 */       for (segmentLen = 0; offset + pos + segmentLen < offset + length && Character.isDigit((char)bytes[offset + pos + segmentLen]); segmentLen++);
/*     */ 
/*     */       
/* 337 */       if (segmentLen + pos != length) {
/* 338 */         throw new DataReadException(
/* 339 */             Messages.getString("ResultSet.InvalidFormatForType", new Object[] { StringUtils.toString(bytes, offset, length), "TIME" }));
/*     */       }
/* 341 */       nanos = getInt(bytes, offset + pos, offset + pos + segmentLen);
/*     */ 
/*     */       
/* 344 */       nanos *= (int)Math.pow(10.0D, (9 - segmentLen));
/*     */     } 
/*     */     
/* 347 */     return new InternalTime(hours, minutes, seconds, nanos, scale);
/*     */   }
/*     */   public static InternalTimestamp getTimestamp(byte[] bytes, int offset, int length, int scale) {
/*     */     int nanos;
/* 351 */     if (length < 19 || (length > 26 && length != 29))
/* 352 */       throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[] { Integer.valueOf(length), "TIMESTAMP" })); 
/* 353 */     if (length != 19)
/*     */     {
/* 355 */       if (bytes[offset + 19] != 46 || length < 21) {
/* 356 */         throw new DataReadException(
/* 357 */             Messages.getString("ResultSet.InvalidFormatForType", new Object[] { StringUtils.toString(bytes, offset, length), "TIMESTAMP" }));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 362 */     if (bytes[offset + 4] != 45 || bytes[offset + 7] != 45 || bytes[offset + 10] != 32 || bytes[offset + 13] != 58 || bytes[offset + 16] != 58)
/*     */     {
/* 364 */       throw new DataReadException(
/* 365 */           Messages.getString("ResultSet.InvalidFormatForType", new Object[] { StringUtils.toString(bytes, offset, length), "TIMESTAMP" }));
/*     */     }
/*     */     
/* 368 */     int year = getInt(bytes, offset, offset + 4);
/* 369 */     int month = getInt(bytes, offset + 5, offset + 7);
/* 370 */     int day = getInt(bytes, offset + 8, offset + 10);
/* 371 */     int hours = getInt(bytes, offset + 11, offset + 13);
/* 372 */     int minutes = getInt(bytes, offset + 14, offset + 16);
/* 373 */     int seconds = getInt(bytes, offset + 17, offset + 19);
/*     */ 
/*     */     
/* 376 */     if (length == 29) {
/* 377 */       nanos = getInt(bytes, offset + 20, offset + length);
/*     */     } else {
/* 379 */       nanos = (length == 19) ? 0 : getInt(bytes, offset + 20, offset + length);
/*     */ 
/*     */       
/* 382 */       nanos *= (int)Math.pow(10.0D, (9 - length - 19 - 1));
/*     */     } 
/*     */     
/* 385 */     return new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\MysqlTextValueDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */