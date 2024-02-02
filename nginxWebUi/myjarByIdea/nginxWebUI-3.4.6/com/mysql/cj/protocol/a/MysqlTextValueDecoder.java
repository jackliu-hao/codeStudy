package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.exceptions.NumberOutOfRange;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.ValueDecoder;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.ValueFactory;
import com.mysql.cj.util.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MysqlTextValueDecoder implements ValueDecoder {
   public static final int DATE_BUF_LEN = 10;
   public static final int TIME_STR_LEN_MIN = 8;
   public static final int TIME_STR_LEN_MAX_NO_FRAC = 10;
   public static final int TIME_STR_LEN_MAX_WITH_MICROS = 17;
   public static final int TIMESTAMP_STR_LEN_NO_FRAC = 19;
   public static final int TIMESTAMP_STR_LEN_WITH_MICROS = 26;
   public static final int TIMESTAMP_STR_LEN_WITH_NANOS = 29;
   public static final Pattern TIME_PTRN = Pattern.compile("[-]{0,1}\\d{2,3}:\\d{2}:\\d{2}(\\.\\d{1,9})?");
   public static final int MAX_SIGNED_LONG_LEN = 20;

   public <T> T decodeDate(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromDate(getDate(bytes, offset, length));
   }

   public <T> T decodeTime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
      return vf.createFromTime(getTime(bytes, offset, length, scale));
   }

   public <T> T decodeTimestamp(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
      return vf.createFromTimestamp(getTimestamp(bytes, offset, length, scale));
   }

   public <T> T decodeDatetime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
      return vf.createFromDatetime(getTimestamp(bytes, offset, length, scale));
   }

   public <T> T decodeUInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong((long)getInt(bytes, offset, offset + length));
   }

   public <T> T decodeInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong((long)getInt(bytes, offset, offset + length));
   }

   public <T> T decodeUInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong((long)getInt(bytes, offset, offset + length));
   }

   public <T> T decodeInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong((long)getInt(bytes, offset, offset + length));
   }

   public <T> T decodeUInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong(getLong(bytes, offset, offset + length));
   }

   public <T> T decodeInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong((long)getInt(bytes, offset, offset + length));
   }

   public <T> T decodeUInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return length <= 19 && bytes[offset] >= 48 && bytes[offset] <= 56 ? this.decodeInt8(bytes, offset, length, vf) : vf.createFromBigInteger(getBigInteger(bytes, offset, length));
   }

   public <T> T decodeInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromLong(getLong(bytes, offset, offset + length));
   }

   public <T> T decodeFloat(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return this.decodeDouble(bytes, offset, length, vf);
   }

   public <T> T decodeDouble(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromDouble(getDouble(bytes, offset, length));
   }

   public <T> T decodeDecimal(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      BigDecimal d = new BigDecimal(StringUtils.toAsciiString(bytes, offset, length));
      return vf.createFromBigDecimal(d);
   }

   public <T> T decodeByteArray(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
      return vf.createFromBytes(bytes, offset, length, f);
   }

   public <T> T decodeBit(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromBit(bytes, offset, length);
   }

   public <T> T decodeSet(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
      return this.decodeByteArray(bytes, offset, length, f, vf);
   }

   public <T> T decodeYear(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return vf.createFromYear(getLong(bytes, offset, offset + length));
   }

   public static int getInt(byte[] buf, int offset, int endpos) throws NumberFormatException {
      long l = getLong(buf, offset, endpos);
      if (l >= -2147483648L && l <= 2147483647L) {
         return (int)l;
      } else {
         throw new NumberOutOfRange(Messages.getString("StringUtils.badIntFormat", new Object[]{StringUtils.toString(buf, offset, endpos - offset)}));
      }
   }

   public static long getLong(byte[] buf, int offset, int endpos) throws NumberFormatException {
      int base = 10;

      int s;
      for(s = offset; s < endpos && Character.isWhitespace((char)buf[s]); ++s) {
      }

      if (s == endpos) {
         throw new NumberFormatException(StringUtils.toString(buf));
      } else {
         boolean negative = false;
         if ((char)buf[s] == '-') {
            negative = true;
            ++s;
         } else if ((char)buf[s] == '+') {
            ++s;
         }

         int save = s;
         long cutoff = Long.MAX_VALUE / (long)base;
         long cutlim = (long)((int)(Long.MAX_VALUE % (long)base));
         if (negative) {
            ++cutlim;
         }

         boolean overflow = false;

         long i;
         for(i = 0L; s < endpos; ++s) {
            char c = (char)buf[s];
            if (c >= '0' && c <= '9') {
               c = (char)(c - 48);
            } else {
               if (!Character.isLetter(c)) {
                  break;
               }

               c = (char)(Character.toUpperCase(c) - 65 + 10);
            }

            if (c >= base) {
               break;
            }

            if (i <= cutoff && (i != cutoff || (long)c <= cutlim)) {
               i *= (long)base;
               i += (long)c;
            } else {
               overflow = true;
            }
         }

         if (s == save) {
            throw new NumberFormatException(Messages.getString("StringUtils.badIntFormat", new Object[]{StringUtils.toString(buf, offset, endpos - offset)}));
         } else if (overflow) {
            throw new NumberOutOfRange(Messages.getString("StringUtils.badIntFormat", new Object[]{StringUtils.toString(buf, offset, endpos - offset)}));
         } else {
            return negative ? -i : i;
         }
      }
   }

   public static BigInteger getBigInteger(byte[] buf, int offset, int length) throws NumberFormatException {
      BigInteger i = new BigInteger(StringUtils.toAsciiString(buf, offset, length));
      return i;
   }

   public static Double getDouble(byte[] bytes, int offset, int length) {
      return Double.parseDouble(StringUtils.toAsciiString(bytes, offset, length));
   }

   public static boolean isDate(String s) {
      return s.length() == 10 && s.charAt(4) == '-' && s.charAt(7) == '-';
   }

   public static boolean isTime(String s) {
      Matcher matcher = TIME_PTRN.matcher(s);
      return matcher.matches();
   }

   public static boolean isTimestamp(String s) {
      Pattern DATETIME_PTRN = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}(\\.\\d{1,9}){0,1}");
      Matcher matcher = DATETIME_PTRN.matcher(s);
      return matcher.matches();
   }

   public static InternalDate getDate(byte[] bytes, int offset, int length) {
      if (length != 10) {
         throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[]{length, "DATE"}));
      } else {
         int year = getInt(bytes, offset, offset + 4);
         int month = getInt(bytes, offset + 5, offset + 7);
         int day = getInt(bytes, offset + 8, offset + 10);
         return new InternalDate(year, month, day);
      }
   }

   public static InternalTime getTime(byte[] bytes, int offset, int length, int scale) {
      int pos = 0;
      if (length >= 8 && length <= 17) {
         boolean negative = false;
         if (bytes[offset] == 45) {
            ++pos;
            negative = true;
         }

         int segmentLen;
         for(segmentLen = 0; Character.isDigit((char)bytes[offset + pos + segmentLen]); ++segmentLen) {
         }

         if (segmentLen != 0 && bytes[offset + pos + segmentLen] == 58) {
            int hours = getInt(bytes, offset + pos, offset + pos + segmentLen);
            if (negative) {
               hours *= -1;
            }

            pos += segmentLen + 1;

            for(segmentLen = 0; Character.isDigit((char)bytes[offset + pos + segmentLen]); ++segmentLen) {
            }

            if (segmentLen == 2 && bytes[offset + pos + segmentLen] == 58) {
               int minutes = getInt(bytes, offset + pos, offset + pos + segmentLen);
               pos += segmentLen + 1;

               for(segmentLen = 0; offset + pos + segmentLen < offset + length && Character.isDigit((char)bytes[offset + pos + segmentLen]); ++segmentLen) {
               }

               if (segmentLen != 2) {
                  throw new DataReadException(Messages.getString("ResultSet.InvalidFormatForType", new Object[]{StringUtils.toString(bytes, offset, length), "TIME"}));
               } else {
                  int seconds = getInt(bytes, offset + pos, offset + pos + segmentLen);
                  pos += segmentLen;
                  int nanos = 0;
                  if (length > pos) {
                     ++pos;

                     for(segmentLen = 0; offset + pos + segmentLen < offset + length && Character.isDigit((char)bytes[offset + pos + segmentLen]); ++segmentLen) {
                     }

                     if (segmentLen + pos != length) {
                        throw new DataReadException(Messages.getString("ResultSet.InvalidFormatForType", new Object[]{StringUtils.toString(bytes, offset, length), "TIME"}));
                     }

                     nanos = getInt(bytes, offset + pos, offset + pos + segmentLen);
                     nanos *= (int)Math.pow(10.0, (double)(9 - segmentLen));
                  }

                  return new InternalTime(hours, minutes, seconds, nanos, scale);
               }
            } else {
               throw new DataReadException(Messages.getString("ResultSet.InvalidFormatForType", new Object[]{"TIME", StringUtils.toString(bytes, offset, length)}));
            }
         } else {
            throw new DataReadException(Messages.getString("ResultSet.InvalidFormatForType", new Object[]{"TIME", StringUtils.toString(bytes, offset, length)}));
         }
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[]{length, "TIME"}));
      }
   }

   public static InternalTimestamp getTimestamp(byte[] bytes, int offset, int length, int scale) {
      if (length >= 19 && (length <= 26 || length == 29)) {
         if (length != 19 && (bytes[offset + 19] != 46 || length < 21)) {
            throw new DataReadException(Messages.getString("ResultSet.InvalidFormatForType", new Object[]{StringUtils.toString(bytes, offset, length), "TIMESTAMP"}));
         } else if (bytes[offset + 4] == 45 && bytes[offset + 7] == 45 && bytes[offset + 10] == 32 && bytes[offset + 13] == 58 && bytes[offset + 16] == 58) {
            int year = getInt(bytes, offset, offset + 4);
            int month = getInt(bytes, offset + 5, offset + 7);
            int day = getInt(bytes, offset + 8, offset + 10);
            int hours = getInt(bytes, offset + 11, offset + 13);
            int minutes = getInt(bytes, offset + 14, offset + 16);
            int seconds = getInt(bytes, offset + 17, offset + 19);
            int nanos;
            if (length == 29) {
               nanos = getInt(bytes, offset + 20, offset + length);
            } else {
               nanos = length == 19 ? 0 : getInt(bytes, offset + 20, offset + length);
               nanos *= (int)Math.pow(10.0, (double)(9 - (length - 19 - 1)));
            }

            return new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale);
         } else {
            throw new DataReadException(Messages.getString("ResultSet.InvalidFormatForType", new Object[]{StringUtils.toString(bytes, offset, length), "TIMESTAMP"}));
         }
      } else {
         throw new DataReadException(Messages.getString("ResultSet.InvalidLengthForType", new Object[]{length, "TIMESTAMP"}));
      }
   }
}
