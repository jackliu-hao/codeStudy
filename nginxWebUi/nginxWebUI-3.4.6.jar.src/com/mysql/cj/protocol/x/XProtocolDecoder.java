/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.CodedInputStream;
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.InternalDate;
/*     */ import com.mysql.cj.protocol.InternalTime;
/*     */ import com.mysql.cj.protocol.InternalTimestamp;
/*     */ import com.mysql.cj.protocol.ValueDecoder;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import java.io.IOException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.CharBuffer;
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
/*     */ public class XProtocolDecoder
/*     */   implements ValueDecoder
/*     */ {
/*  50 */   public static XProtocolDecoder instance = new XProtocolDecoder();
/*     */ 
/*     */   
/*     */   public <T> T decodeDate(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*  54 */     return decodeTimestamp(bytes, offset, length, 0, vf);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeTime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*     */     try {
/*  60 */       CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
/*  61 */       boolean negative = (inputStream.readRawByte() > 0);
/*  62 */       int hours = 0;
/*  63 */       int minutes = 0;
/*  64 */       int seconds = 0;
/*     */       
/*  66 */       int nanos = 0;
/*     */       
/*  68 */       if (!inputStream.isAtEnd()) {
/*  69 */         hours = (int)inputStream.readInt64();
/*  70 */         if (!inputStream.isAtEnd()) {
/*  71 */           minutes = (int)inputStream.readInt64();
/*  72 */           if (!inputStream.isAtEnd()) {
/*  73 */             seconds = (int)inputStream.readInt64();
/*  74 */             if (!inputStream.isAtEnd()) {
/*  75 */               nanos = 1000 * (int)inputStream.readInt64();
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/*  81 */       return (T)vf.createFromTime(new InternalTime(negative ? (-1 * hours) : hours, minutes, seconds, nanos, scale));
/*  82 */     } catch (IOException e) {
/*  83 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeTimestamp(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*     */     try {
/*  90 */       CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
/*  91 */       int year = (int)inputStream.readUInt64();
/*  92 */       int month = (int)inputStream.readUInt64();
/*  93 */       int day = (int)inputStream.readUInt64();
/*     */ 
/*     */       
/*  96 */       if (inputStream.getBytesUntilLimit() > 0) {
/*  97 */         int hours = 0;
/*  98 */         int minutes = 0;
/*  99 */         int seconds = 0;
/*     */         
/* 101 */         int nanos = 0;
/*     */         
/* 103 */         if (!inputStream.isAtEnd()) {
/* 104 */           hours = (int)inputStream.readInt64();
/* 105 */           if (!inputStream.isAtEnd()) {
/* 106 */             minutes = (int)inputStream.readInt64();
/* 107 */             if (!inputStream.isAtEnd()) {
/* 108 */               seconds = (int)inputStream.readInt64();
/* 109 */               if (!inputStream.isAtEnd()) {
/* 110 */                 nanos = 1000 * (int)inputStream.readInt64();
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 116 */         return (T)vf.createFromTimestamp(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
/*     */       } 
/* 118 */       return (T)vf.createFromDate(new InternalDate(year, month, day));
/* 119 */     } catch (IOException e) {
/* 120 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeDatetime(byte[] bytes, int offset, int length, int scale, ValueFactory<T> vf) {
/*     */     try {
/* 127 */       CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
/* 128 */       int year = (int)inputStream.readUInt64();
/* 129 */       int month = (int)inputStream.readUInt64();
/* 130 */       int day = (int)inputStream.readUInt64();
/*     */ 
/*     */       
/* 133 */       if (inputStream.getBytesUntilLimit() > 0) {
/* 134 */         int hours = 0;
/* 135 */         int minutes = 0;
/* 136 */         int seconds = 0;
/*     */         
/* 138 */         int nanos = 0;
/*     */         
/* 140 */         if (!inputStream.isAtEnd()) {
/* 141 */           hours = (int)inputStream.readInt64();
/* 142 */           if (!inputStream.isAtEnd()) {
/* 143 */             minutes = (int)inputStream.readInt64();
/* 144 */             if (!inputStream.isAtEnd()) {
/* 145 */               seconds = (int)inputStream.readInt64();
/* 146 */               if (!inputStream.isAtEnd()) {
/* 147 */                 nanos = 1000 * (int)inputStream.readInt64();
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 153 */         return (T)vf.createFromDatetime(new InternalTimestamp(year, month, day, hours, minutes, seconds, nanos, scale));
/*     */       } 
/* 155 */       return (T)vf.createFromDate(new InternalDate(year, month, day));
/* 156 */     } catch (IOException e) {
/* 157 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeUInt1(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 176 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeUInt2(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 188 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeUInt4(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 194 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*     */     try {
/* 200 */       return (T)vf.createFromLong(CodedInputStream.newInstance(bytes, offset, length).readSInt64());
/* 201 */     } catch (IOException e) {
/* 202 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeUInt8(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*     */     try {
/* 212 */       BigInteger v = new BigInteger(ByteBuffer.allocate(9).put((byte)0).putLong(CodedInputStream.newInstance(bytes, offset, length).readUInt64()).array());
/* 213 */       return (T)vf.createFromBigInteger(v);
/* 214 */     } catch (IOException e) {
/* 215 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeFloat(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*     */     try {
/* 222 */       return (T)vf.createFromDouble(CodedInputStream.newInstance(bytes, offset, length).readFloat());
/* 223 */     } catch (IOException e) {
/* 224 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeDouble(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*     */     try {
/* 231 */       return (T)vf.createFromDouble(CodedInputStream.newInstance(bytes, offset, length).readDouble());
/* 232 */     } catch (IOException e) {
/* 233 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeDecimal(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*     */     try {
/* 240 */       CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
/*     */ 
/*     */       
/* 243 */       byte scale = inputStream.readRawByte();
/*     */       
/* 245 */       CharBuffer unscaledString = CharBuffer.allocate(2 * inputStream.getBytesUntilLimit());
/* 246 */       unscaledString.position(1);
/* 247 */       byte sign = 0;
/*     */       
/*     */       while (true) {
/* 250 */         int b = 0xFF & inputStream.readRawByte();
/* 251 */         if (b >> 4 > 9) {
/* 252 */           sign = (byte)(b >> 4);
/*     */           break;
/*     */         } 
/* 255 */         unscaledString.append((char)((b >> 4) + 48));
/* 256 */         if ((b & 0xF) > 9) {
/* 257 */           sign = (byte)(b & 0xF);
/*     */           break;
/*     */         } 
/* 260 */         unscaledString.append((char)((b & 0xF) + 48));
/*     */       } 
/* 262 */       if (inputStream.getBytesUntilLimit() > 0)
/*     */       {
/* 264 */         throw AssertionFailedException.shouldNotHappen("Did not read all bytes while decoding decimal. Bytes left: " + inputStream.getBytesUntilLimit());
/*     */       }
/* 266 */       switch (sign) {
/*     */         case 10:
/*     */         case 12:
/*     */         case 14:
/*     */         case 15:
/* 271 */           unscaledString.put(0, '+');
/*     */           break;
/*     */         case 11:
/*     */         case 13:
/* 275 */           unscaledString.put(0, '-');
/*     */           break;
/*     */       } 
/*     */       
/* 279 */       int characters = unscaledString.position();
/* 280 */       unscaledString.clear();
/* 281 */       BigInteger unscaled = new BigInteger(unscaledString.subSequence(0, characters).toString());
/* 282 */       return (T)vf.createFromBigDecimal(new BigDecimal(unscaled, scale));
/* 283 */     } catch (IOException e) {
/* 284 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeByteArray(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
/*     */     try {
/* 291 */       CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
/*     */       
/* 293 */       int size = inputStream.getBytesUntilLimit();
/* 294 */       size--;
/* 295 */       return (T)vf.createFromBytes(inputStream.readRawBytes(size), 0, size, f);
/* 296 */     } catch (IOException e) {
/* 297 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeBit(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/*     */     try {
/* 306 */       byte[] buf = ByteBuffer.allocate(9).put((byte)0).putLong(CodedInputStream.newInstance(bytes, offset, length).readUInt64()).array();
/* 307 */       return (T)vf.createFromBit(buf, 0, 9);
/* 308 */     } catch (IOException e) {
/* 309 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T decodeSet(byte[] bytes, int offset, int length, Field f, ValueFactory<T> vf) {
/*     */     try {
/* 316 */       CodedInputStream inputStream = CodedInputStream.newInstance(bytes, offset, length);
/* 317 */       StringBuilder vals = new StringBuilder();
/* 318 */       while (inputStream.getBytesUntilLimit() > 0) {
/* 319 */         if (vals.length() > 0) {
/* 320 */           vals.append(",");
/*     */         }
/* 322 */         long valLen = inputStream.readUInt64();
/*     */         
/* 324 */         vals.append(new String(inputStream.readRawBytes((int)valLen)));
/*     */       } 
/*     */       
/* 327 */       byte[] buf = vals.toString().getBytes();
/* 328 */       return (T)vf.createFromBytes(buf, 0, buf.length, f);
/* 329 */     } catch (IOException e) {
/* 330 */       throw new DataReadException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T decodeYear(byte[] bytes, int offset, int length, ValueFactory<T> vf) {
/* 337 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\XProtocolDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */