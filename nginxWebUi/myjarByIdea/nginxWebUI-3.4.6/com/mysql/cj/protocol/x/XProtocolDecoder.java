package com.mysql.cj.protocol.x;

import com.google.protobuf.CodedInputStream;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.DataReadException;
import com.mysql.cj.protocol.InternalDate;
import com.mysql.cj.protocol.InternalTime;
import com.mysql.cj.protocol.InternalTimestamp;
import com.mysql.cj.protocol.ValueDecoder;
import com.mysql.cj.result.Field;
import com.mysql.cj.result.ValueFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class XProtocolDecoder implements ValueDecoder {
   public static XProtocolDecoder instance = new XProtocolDecoder();

   public <T> T decodeDate(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return this.decodeTimestamp(bytes, offset, length, 0, vf);
   }

   public <T> T decodeTime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
      try {
         CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
         boolean negative = inputStream.readRawByte() > 0;
         int hours = 0;
         int minutes = 0;
         int seconds = 0;
         int nanos = 0;
         if (!inputStream.isAtEnd()) {
            hours = (int)inputStream.readInt64();
            if (!inputStream.isAtEnd()) {
               minutes = (int)inputStream.readInt64();
               if (!inputStream.isAtEnd()) {
                  seconds = (int)inputStream.readInt64();
                  if (!inputStream.isAtEnd()) {
                     nanos = 1000 * (int)inputStream.readInt64();
                  }
               }
            }
         }

         return vf.createFromTime(new InternalTime(negative ? -1 * hours : hours, minutes, seconds, nanos, scale));
      } catch (IOException var12) {
         throw new DataReadException(var12);
      }
   }

   public <T> T decodeTimestamp(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
      try {
         CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
         int year = (int)inputStream.readUInt64();
         int month = (int)inputStream.readUInt64();
         int day = (int)inputStream.readUInt64();
         if (inputStream.getBytesUntilLimit() > 0) {
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            int nanos = 0;
            if (!inputStream.isAtEnd()) {
               hours = (int)inputStream.readInt64();
               if (!inputStream.isAtEnd()) {
                  minutes = (int)inputStream.readInt64();
                  if (!inputStream.isAtEnd()) {
                     seconds = (int)inputStream.readInt64();
                     if (!inputStream.isAtEnd()) {
                        nanos = 1000 * (int)inputStream.readInt64();
                     }
                  }
               }
            }

            return vf.createFromTimestamp(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
         } else {
            return vf.createFromDate(new InternalDate(year, month, day));
         }
      } catch (IOException var14) {
         throw new DataReadException(var14);
      }
   }

   public <T> T decodeDatetime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
      try {
         CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
         int year = (int)inputStream.readUInt64();
         int month = (int)inputStream.readUInt64();
         int day = (int)inputStream.readUInt64();
         if (inputStream.getBytesUntilLimit() > 0) {
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            int nanos = 0;
            if (!inputStream.isAtEnd()) {
               hours = (int)inputStream.readInt64();
               if (!inputStream.isAtEnd()) {
                  minutes = (int)inputStream.readInt64();
                  if (!inputStream.isAtEnd()) {
                     seconds = (int)inputStream.readInt64();
                     if (!inputStream.isAtEnd()) {
                        nanos = 1000 * (int)inputStream.readInt64();
                     }
                  }
               }
            }

            return vf.createFromDatetime(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
         } else {
            return vf.createFromDate(new InternalDate(year, month, day));
         }
      } catch (IOException var14) {
         throw new DataReadException(var14);
      }
   }

   public <T> T decodeInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }

   public <T> T decodeUInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }

   public <T> T decodeInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }

   public <T> T decodeUInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }

   public <T> T decodeInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }

   public <T> T decodeUInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }

   public <T> T decodeInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      try {
         return vf.createFromLong(CodedInputStream.newInstance(bytes, offset, length).readSInt64());
      } catch (IOException var6) {
         throw new DataReadException(var6);
      }
   }

   public <T> T decodeUInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      try {
         BigInteger v = new BigInteger(ByteBuffer.allocate(9).put((byte)0).putLong(CodedInputStream.newInstance(bytes, offset, length).readUInt64()).array());
         return vf.createFromBigInteger(v);
      } catch (IOException var6) {
         throw new DataReadException(var6);
      }
   }

   public <T> T decodeFloat(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      try {
         return vf.createFromDouble((double)CodedInputStream.newInstance(bytes, offset, length).readFloat());
      } catch (IOException var6) {
         throw new DataReadException(var6);
      }
   }

   public <T> T decodeDouble(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      try {
         return vf.createFromDouble(CodedInputStream.newInstance(bytes, offset, length).readDouble());
      } catch (IOException var6) {
         throw new DataReadException(var6);
      }
   }

   public <T> T decodeDecimal(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      try {
         CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
         byte scale = inputStream.readRawByte();
         CharBuffer unscaledString = CharBuffer.allocate(2 * inputStream.getBytesUntilLimit());
         unscaledString.position(1);
         byte sign = false;

         int characters;
         byte sign;
         while(true) {
            characters = 255 & inputStream.readRawByte();
            if (characters >> 4 > 9) {
               sign = (byte)(characters >> 4);
               break;
            }

            unscaledString.append((char)((characters >> 4) + 48));
            if ((characters & 15) > 9) {
               sign = (byte)(characters & 15);
               break;
            }

            unscaledString.append((char)((characters & 15) + 48));
         }

         if (inputStream.getBytesUntilLimit() > 0) {
            throw AssertionFailedException.shouldNotHappen("Did not read all bytes while decoding decimal. Bytes left: " + inputStream.getBytesUntilLimit());
         } else {
            switch (sign) {
               case 10:
               case 12:
               case 14:
               case 15:
                  unscaledString.put(0, '+');
                  break;
               case 11:
               case 13:
                  unscaledString.put(0, '-');
            }

            characters = unscaledString.position();
            unscaledString.clear();
            BigInteger unscaled = new BigInteger(unscaledString.subSequence(0, characters).toString());
            return vf.createFromBigDecimal(new BigDecimal(unscaled, scale));
         }
      } catch (IOException var11) {
         throw new DataReadException(var11);
      }
   }

   public <T> T decodeByteArray(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
      try {
         CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
         int size = inputStream.getBytesUntilLimit();
         --size;
         return vf.createFromBytes(inputStream.readRawBytes(size), 0, size, f);
      } catch (IOException var8) {
         throw new DataReadException(var8);
      }
   }

   public <T> T decodeBit(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      try {
         byte[] buf = ByteBuffer.allocate(9).put((byte)0).putLong(CodedInputStream.newInstance(bytes, offset, length).readUInt64()).array();
         return vf.createFromBit(buf, 0, 9);
      } catch (IOException var6) {
         throw new DataReadException(var6);
      }
   }

   public <T> T decodeSet(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
      try {
         CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
         StringBuilder vals = new StringBuilder();

         while(inputStream.getBytesUntilLimit() > 0) {
            if (vals.length() > 0) {
               vals.append(",");
            }

            long valLen = inputStream.readUInt64();
            vals.append(new String(inputStream.readRawBytes((int)valLen)));
         }

         byte[] buf = vals.toString().getBytes();
         return vf.createFromBytes(buf, 0, buf.length, f);
      } catch (IOException var10) {
         throw new DataReadException(var10);
      }
   }

   public <T> T decodeYear(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
      return null;
   }
}
