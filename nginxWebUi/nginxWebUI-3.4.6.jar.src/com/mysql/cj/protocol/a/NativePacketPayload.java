/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.Constants;
/*     */ import com.mysql.cj.Messages;
/*     */ import com.mysql.cj.exceptions.ExceptionFactory;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NativePacketPayload
/*     */   implements Message
/*     */ {
/*     */   static final int NO_LENGTH_LIMIT = -1;
/*     */   public static final long NULL_LENGTH = -1L;
/*     */   public static final short TYPE_ID_ERROR = 255;
/*     */   public static final short TYPE_ID_EOF = 254;
/*     */   public static final short TYPE_ID_AUTH_SWITCH = 254;
/*     */   public static final short TYPE_ID_LOCAL_INFILE = 251;
/*     */   public static final short TYPE_ID_OK = 0;
/*     */   public static final short TYPE_ID_AUTH_MORE_DATA = 1;
/*     */   public static final short TYPE_ID_AUTH_NEXT_FACTOR = 2;
/*  70 */   private int payloadLength = 0;
/*     */   
/*     */   private byte[] byteBuffer;
/*     */   
/*  74 */   private int position = 0;
/*     */   
/*     */   static final int MAX_BYTES_TO_DUMP = 1024;
/*     */   
/*  78 */   private Map<String, Integer> tags = new HashMap<>();
/*     */ 
/*     */   
/*     */   public String toString() {
/*  82 */     int numBytes = (this.position <= this.payloadLength) ? this.position : this.payloadLength;
/*  83 */     int numBytesToDump = (numBytes < 1024) ? numBytes : 1024;
/*     */     
/*  85 */     this.position = 0;
/*  86 */     String dumped = StringUtils.dumpAsHex(readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, numBytesToDump), numBytesToDump);
/*     */     
/*  88 */     if (numBytesToDump < numBytes) {
/*  89 */       return dumped + " ....(packet exceeds max. dump length)";
/*     */     }
/*     */     
/*  92 */     return dumped;
/*     */   }
/*     */   
/*     */   public String toSuperString() {
/*  96 */     return super.toString();
/*     */   }
/*     */   
/*     */   public NativePacketPayload(byte[] buf) {
/* 100 */     this.byteBuffer = buf;
/* 101 */     this.payloadLength = buf.length;
/*     */   }
/*     */   
/*     */   public NativePacketPayload(int size) {
/* 105 */     this.byteBuffer = new byte[size];
/* 106 */     this.payloadLength = size;
/*     */   }
/*     */   
/*     */   public int getCapacity() {
/* 110 */     return this.byteBuffer.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void ensureCapacity(int additionalData) {
/* 121 */     if (this.position + additionalData > this.byteBuffer.length) {
/*     */ 
/*     */ 
/*     */       
/* 125 */       int newLength = (int)(this.byteBuffer.length * 1.25D);
/*     */       
/* 127 */       if (newLength < this.byteBuffer.length + additionalData) {
/* 128 */         newLength = this.byteBuffer.length + (int)(additionalData * 1.25D);
/*     */       }
/*     */       
/* 131 */       if (newLength < this.byteBuffer.length) {
/* 132 */         newLength = this.byteBuffer.length + additionalData;
/*     */       }
/*     */       
/* 135 */       byte[] newBytes = new byte[newLength];
/*     */       
/* 137 */       System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
/* 138 */       this.byteBuffer = newBytes;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteBuffer() {
/* 144 */     return this.byteBuffer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setByteBuffer(byte[] byteBufferToSet) {
/* 154 */     this.byteBuffer = byteBufferToSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPayloadLength() {
/* 164 */     return this.payloadLength;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPayloadLength(int bufLengthToSet) {
/* 175 */     if (bufLengthToSet > this.byteBuffer.length) {
/* 176 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Buffer.0"));
/*     */     }
/* 178 */     this.payloadLength = bufLengthToSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void adjustPayloadLength() {
/* 186 */     if (this.position > this.payloadLength) {
/* 187 */       this.payloadLength = this.position;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPosition() {
/* 193 */     return this.position;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPosition(int positionToSet) {
/* 203 */     this.position = positionToSet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isErrorPacket() {
/* 212 */     return ((this.byteBuffer[0] & 0xFF) == 255);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEOFPacket() {
/* 222 */     return ((this.byteBuffer[0] & 0xFF) == 254 && this.payloadLength <= 5);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isAuthMethodSwitchRequestPacket() {
/* 232 */     return ((this.byteBuffer[0] & 0xFF) == 254);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isOKPacket() {
/* 242 */     return ((this.byteBuffer[0] & 0xFF) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isResultSetOKPacket() {
/* 252 */     return ((this.byteBuffer[0] & 0xFF) == 254 && this.payloadLength > 5 && this.payloadLength < 16777215);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isAuthMoreDataPacket() {
/* 262 */     return ((this.byteBuffer[0] & 0xFF) == 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isAuthNextFactorPacket() {
/* 272 */     return ((this.byteBuffer[0] & 0xFF) == 2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeInteger(NativeConstants.IntegerDataType type, long l) {
/*     */     byte[] b;
/* 285 */     switch (type) {
/*     */       case STRING_FIXED:
/* 287 */         ensureCapacity(1);
/* 288 */         b = this.byteBuffer;
/* 289 */         b[this.position++] = (byte)(int)(l & 0xFFL);
/*     */         break;
/*     */       
/*     */       case STRING_VAR:
/* 293 */         ensureCapacity(2);
/* 294 */         b = this.byteBuffer;
/* 295 */         b[this.position++] = (byte)(int)(l & 0xFFL);
/* 296 */         b[this.position++] = (byte)(int)(l >>> 8L);
/*     */         break;
/*     */       
/*     */       case null:
/* 300 */         ensureCapacity(3);
/* 301 */         b = this.byteBuffer;
/* 302 */         b[this.position++] = (byte)(int)(l & 0xFFL);
/* 303 */         b[this.position++] = (byte)(int)(l >>> 8L);
/* 304 */         b[this.position++] = (byte)(int)(l >>> 16L);
/*     */         break;
/*     */       
/*     */       case null:
/* 308 */         ensureCapacity(4);
/* 309 */         b = this.byteBuffer;
/* 310 */         b[this.position++] = (byte)(int)(l & 0xFFL);
/* 311 */         b[this.position++] = (byte)(int)(l >>> 8L);
/* 312 */         b[this.position++] = (byte)(int)(l >>> 16L);
/* 313 */         b[this.position++] = (byte)(int)(l >>> 24L);
/*     */         break;
/*     */       
/*     */       case null:
/* 317 */         ensureCapacity(6);
/* 318 */         b = this.byteBuffer;
/* 319 */         b[this.position++] = (byte)(int)(l & 0xFFL);
/* 320 */         b[this.position++] = (byte)(int)(l >>> 8L);
/* 321 */         b[this.position++] = (byte)(int)(l >>> 16L);
/* 322 */         b[this.position++] = (byte)(int)(l >>> 24L);
/* 323 */         b[this.position++] = (byte)(int)(l >>> 32L);
/* 324 */         b[this.position++] = (byte)(int)(l >>> 40L);
/*     */         break;
/*     */       
/*     */       case null:
/* 328 */         ensureCapacity(8);
/* 329 */         b = this.byteBuffer;
/* 330 */         b[this.position++] = (byte)(int)(l & 0xFFL);
/* 331 */         b[this.position++] = (byte)(int)(l >>> 8L);
/* 332 */         b[this.position++] = (byte)(int)(l >>> 16L);
/* 333 */         b[this.position++] = (byte)(int)(l >>> 24L);
/* 334 */         b[this.position++] = (byte)(int)(l >>> 32L);
/* 335 */         b[this.position++] = (byte)(int)(l >>> 40L);
/* 336 */         b[this.position++] = (byte)(int)(l >>> 48L);
/* 337 */         b[this.position++] = (byte)(int)(l >>> 56L);
/*     */         break;
/*     */       
/*     */       case null:
/* 341 */         if (l < 251L) {
/* 342 */           ensureCapacity(1);
/* 343 */           writeInteger(NativeConstants.IntegerDataType.INT1, l); break;
/*     */         } 
/* 345 */         if (l < 65536L) {
/* 346 */           ensureCapacity(3);
/* 347 */           writeInteger(NativeConstants.IntegerDataType.INT1, 252L);
/* 348 */           writeInteger(NativeConstants.IntegerDataType.INT2, l); break;
/*     */         } 
/* 350 */         if (l < 16777216L) {
/* 351 */           ensureCapacity(4);
/* 352 */           writeInteger(NativeConstants.IntegerDataType.INT1, 253L);
/* 353 */           writeInteger(NativeConstants.IntegerDataType.INT3, l);
/*     */           break;
/*     */         } 
/* 356 */         ensureCapacity(9);
/* 357 */         writeInteger(NativeConstants.IntegerDataType.INT1, 254L);
/* 358 */         writeInteger(NativeConstants.IntegerDataType.INT8, l);
/*     */         break;
/*     */     } 
/*     */     
/* 362 */     adjustPayloadLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final long readInteger(NativeConstants.IntegerDataType type) {
/*     */     int sw;
/* 373 */     byte[] b = this.byteBuffer;
/* 374 */     switch (type) {
/*     */       case STRING_FIXED:
/* 376 */         return (b[this.position++] & 0xFF);
/*     */       
/*     */       case STRING_VAR:
/* 379 */         return (b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8);
/*     */       
/*     */       case null:
/* 382 */         return (b[this.position++] & 0xFF | (b[this.position++] & 0xFF) << 8 | (b[this.position++] & 0xFF) << 16);
/*     */       
/*     */       case null:
/* 385 */         return b[this.position++] & 0xFFL | (b[this.position++] & 0xFFL) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L;
/*     */ 
/*     */       
/*     */       case null:
/* 389 */         return (b[this.position++] & 0xFF) | (b[this.position++] & 0xFF) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L | (b[this.position++] & 0xFF) << 32L | (b[this.position++] & 0xFF) << 40L;
/*     */ 
/*     */       
/*     */       case null:
/* 393 */         return (b[this.position++] & 0xFF) | (b[this.position++] & 0xFF) << 8L | (b[this.position++] & 0xFF) << 16L | (b[this.position++] & 0xFF) << 24L | (b[this.position++] & 0xFF) << 32L | (b[this.position++] & 0xFF) << 40L | (b[this.position++] & 0xFF) << 48L | (b[this.position++] & 0xFF) << 56L;
/*     */ 
/*     */ 
/*     */       
/*     */       case null:
/* 398 */         sw = b[this.position++] & 0xFF;
/* 399 */         switch (sw) {
/*     */           case 251:
/* 401 */             return -1L;
/*     */           case 252:
/* 403 */             return readInteger(NativeConstants.IntegerDataType.INT2);
/*     */           case 253:
/* 405 */             return readInteger(NativeConstants.IntegerDataType.INT3);
/*     */           case 254:
/* 407 */             return readInteger(NativeConstants.IntegerDataType.INT8);
/*     */         } 
/* 409 */         return sw;
/*     */     } 
/*     */ 
/*     */     
/* 413 */     return (b[this.position++] & 0xFF);
/*     */   }
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
/*     */   public final void writeBytes(NativeConstants.StringSelfDataType type, byte[] b) {
/* 426 */     writeBytes(type, b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void writeBytes(NativeConstants.StringLengthDataType type, byte[] b) {
/* 438 */     writeBytes(type, b, 0, b.length);
/*     */   }
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
/*     */   public void writeBytes(NativeConstants.StringSelfDataType type, byte[] b, int offset, int len) {
/* 455 */     switch (type) {
/*     */       case STRING_FIXED:
/* 457 */         writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, b, offset, len);
/*     */         break;
/*     */       
/*     */       case STRING_VAR:
/* 461 */         ensureCapacity(len + 1);
/* 462 */         writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, b, offset, len);
/* 463 */         this.byteBuffer[this.position++] = 0;
/*     */         break;
/*     */       
/*     */       case null:
/* 467 */         ensureCapacity(len + 9);
/* 468 */         writeInteger(NativeConstants.IntegerDataType.INT_LENENC, len);
/* 469 */         writeBytes(NativeConstants.StringLengthDataType.STRING_FIXED, b, offset, len);
/*     */         break;
/*     */     } 
/*     */     
/* 473 */     adjustPayloadLength();
/*     */   }
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
/*     */   public void writeBytes(NativeConstants.StringLengthDataType type, byte[] b, int offset, int len) {
/* 490 */     switch (type) {
/*     */       case STRING_FIXED:
/*     */       case STRING_VAR:
/* 493 */         ensureCapacity(len);
/* 494 */         System.arraycopy(b, offset, this.byteBuffer, this.position, len);
/* 495 */         this.position += len;
/*     */         break;
/*     */     } 
/*     */     
/* 499 */     adjustPayloadLength();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] readBytes(NativeConstants.StringSelfDataType type) {
/*     */     byte[] b;
/*     */     int i;
/*     */     long l;
/* 512 */     switch (type) {
/*     */       case STRING_VAR:
/* 514 */         i = this.position;
/* 515 */         while (i < this.payloadLength && this.byteBuffer[i] != 0) {
/* 516 */           i++;
/*     */         }
/* 518 */         b = readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, i - this.position);
/* 519 */         this.position++;
/* 520 */         return b;
/*     */       
/*     */       case null:
/* 523 */         l = readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 524 */         return (l == -1L) ? null : ((l == 0L) ? Constants.EMPTY_BYTE_ARRAY : readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, (int)l));
/*     */       
/*     */       case STRING_FIXED:
/* 527 */         return readBytes(NativeConstants.StringLengthDataType.STRING_FIXED, this.payloadLength - this.position);
/*     */     } 
/* 529 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipBytes(NativeConstants.StringSelfDataType type) {
/*     */     long len;
/* 539 */     switch (type) {
/*     */       case STRING_VAR:
/* 541 */         while (this.position < this.payloadLength && this.byteBuffer[this.position] != 0) {
/* 542 */           this.position++;
/*     */         }
/* 544 */         this.position++;
/*     */         break;
/*     */       
/*     */       case null:
/* 548 */         len = readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 549 */         if (len != -1L && len != 0L) {
/* 550 */           this.position += (int)len;
/*     */         }
/*     */         break;
/*     */       
/*     */       case STRING_FIXED:
/* 555 */         this.position = this.payloadLength;
/*     */         break;
/*     */     } 
/*     */   }
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
/*     */   public byte[] readBytes(NativeConstants.StringLengthDataType type, int len) {
/*     */     byte[] b;
/* 571 */     switch (type) {
/*     */       case STRING_FIXED:
/*     */       case STRING_VAR:
/* 574 */         b = new byte[len];
/* 575 */         System.arraycopy(this.byteBuffer, this.position, b, 0, len);
/* 576 */         this.position += len;
/* 577 */         return b;
/*     */     } 
/* 579 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readString(NativeConstants.StringSelfDataType type, String encoding) {
/*     */     int i;
/*     */     long l;
/* 593 */     String res = null;
/* 594 */     switch (type) {
/*     */       case STRING_VAR:
/* 596 */         i = this.position;
/* 597 */         while (i < this.payloadLength && this.byteBuffer[i] != 0) {
/* 598 */           i++;
/*     */         }
/* 600 */         res = readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, i - this.position);
/* 601 */         this.position++;
/*     */         break;
/*     */       
/*     */       case null:
/* 605 */         l = readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 606 */         return (l == -1L) ? null : ((l == 0L) ? "" : readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, (int)l));
/*     */       
/*     */       case STRING_FIXED:
/* 609 */         return readString(NativeConstants.StringLengthDataType.STRING_FIXED, encoding, this.payloadLength - this.position);
/*     */     } 
/*     */     
/* 612 */     return res;
/*     */   }
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
/*     */   public String readString(NativeConstants.StringLengthDataType type, String encoding, int len) {
/* 627 */     String res = null;
/* 628 */     switch (type) {
/*     */       case STRING_FIXED:
/*     */       case STRING_VAR:
/* 631 */         if (this.position + len > this.payloadLength) {
/* 632 */           throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Buffer.1"));
/*     */         }
/*     */         
/* 635 */         res = StringUtils.toString(this.byteBuffer, this.position, len, encoding);
/* 636 */         this.position += len;
/*     */         break;
/*     */     } 
/*     */     
/* 640 */     return res;
/*     */   }
/*     */   
/*     */   public static String extractSqlFromPacket(String possibleSqlQuery, NativePacketPayload packet, int endOfQueryPacketPosition, int maxQuerySizeToLog) {
/* 644 */     String extractedSql = null;
/*     */     
/* 646 */     if (possibleSqlQuery != null) {
/* 647 */       if (possibleSqlQuery.length() > maxQuerySizeToLog) {
/* 648 */         StringBuilder truncatedQueryBuf = new StringBuilder(possibleSqlQuery.substring(0, maxQuerySizeToLog));
/* 649 */         truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
/* 650 */         extractedSql = truncatedQueryBuf.toString();
/*     */       } else {
/* 652 */         extractedSql = possibleSqlQuery;
/*     */       } 
/*     */     }
/*     */     
/* 656 */     if (extractedSql == null) {
/*     */ 
/*     */       
/* 659 */       int extractPosition = endOfQueryPacketPosition;
/*     */       
/* 661 */       boolean truncated = false;
/*     */       
/* 663 */       if (endOfQueryPacketPosition > maxQuerySizeToLog) {
/* 664 */         extractPosition = maxQuerySizeToLog;
/* 665 */         truncated = true;
/*     */       } 
/*     */       
/* 668 */       extractedSql = StringUtils.toString(packet.getByteBuffer(), 1, extractPosition - 1);
/*     */       
/* 670 */       if (truncated) {
/* 671 */         extractedSql = extractedSql + Messages.getString("MysqlIO.25");
/*     */       }
/*     */     } 
/*     */     
/* 675 */     return extractedSql;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setTag(String key) {
/* 687 */     Integer pos = this.tags.put(key, Integer.valueOf(getPosition()));
/* 688 */     return (pos == null) ? -1 : pos.intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTag(String key) {
/* 700 */     Integer pos = this.tags.get(key);
/* 701 */     return (pos == null) ? -1 : pos.intValue();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\NativePacketPayload.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */