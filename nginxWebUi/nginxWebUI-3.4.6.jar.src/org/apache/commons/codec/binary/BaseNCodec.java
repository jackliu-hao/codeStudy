/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.apache.commons.codec.BinaryDecoder;
/*     */ import org.apache.commons.codec.BinaryEncoder;
/*     */ import org.apache.commons.codec.DecoderException;
/*     */ import org.apache.commons.codec.EncoderException;
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
/*     */ public abstract class BaseNCodec
/*     */   implements BinaryEncoder, BinaryDecoder
/*     */ {
/*     */   static final int EOF = -1;
/*     */   public static final int MIME_CHUNK_SIZE = 76;
/*     */   public static final int PEM_CHUNK_SIZE = 64;
/*     */   private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
/*     */   private static final int DEFAULT_BUFFER_SIZE = 8192;
/*     */   private static final int MAX_BUFFER_SIZE = 2147483639;
/*     */   protected static final int MASK_8BITS = 255;
/*     */   protected static final byte PAD_DEFAULT = 61;
/*     */   
/*     */   static class Context
/*     */   {
/*     */     int ibitWorkArea;
/*     */     long lbitWorkArea;
/*     */     byte[] buffer;
/*     */     int pos;
/*     */     int readPos;
/*     */     boolean eof;
/*     */     int currentLinePos;
/*     */     int modulus;
/*     */     
/*     */     public String toString() {
/* 102 */       return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", new Object[] {
/* 103 */             getClass().getSimpleName(), Arrays.toString(this.buffer), 
/* 104 */             Integer.valueOf(this.currentLinePos), Boolean.valueOf(this.eof), Integer.valueOf(this.ibitWorkArea), Long.valueOf(this.lbitWorkArea), Integer.valueOf(this.modulus), Integer.valueOf(this.pos), Integer.valueOf(this.readPos)
/*     */           });
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 170 */   protected final byte PAD = 61;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final byte pad;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int unencodedBlockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int encodedBlockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int lineLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int chunkSeparatorLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
/* 203 */     this(unencodedBlockSize, encodedBlockSize, lineLength, chunkSeparatorLength, (byte)61);
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
/*     */   protected BaseNCodec(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength, byte pad) {
/* 217 */     this.unencodedBlockSize = unencodedBlockSize;
/* 218 */     this.encodedBlockSize = encodedBlockSize;
/* 219 */     boolean useChunking = (lineLength > 0 && chunkSeparatorLength > 0);
/* 220 */     this.lineLength = useChunking ? (lineLength / encodedBlockSize * encodedBlockSize) : 0;
/* 221 */     this.chunkSeparatorLength = chunkSeparatorLength;
/*     */     
/* 223 */     this.pad = pad;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasData(Context context) {
/* 233 */     return (context.buffer != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int available(Context context) {
/* 243 */     return (context.buffer != null) ? (context.pos - context.readPos) : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getDefaultBufferSize() {
/* 252 */     return 8192;
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
/*     */   private static byte[] resizeBuffer(Context context, int minCapacity) {
/* 264 */     int oldCapacity = context.buffer.length;
/* 265 */     int newCapacity = oldCapacity * 2;
/* 266 */     if (compareUnsigned(newCapacity, minCapacity) < 0) {
/* 267 */       newCapacity = minCapacity;
/*     */     }
/* 269 */     if (compareUnsigned(newCapacity, 2147483639) > 0) {
/* 270 */       newCapacity = createPositiveCapacity(minCapacity);
/*     */     }
/*     */     
/* 273 */     byte[] b = new byte[newCapacity];
/* 274 */     System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
/* 275 */     context.buffer = b;
/* 276 */     return b;
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
/*     */   private static int compareUnsigned(int x, int y) {
/* 293 */     return Integer.compare(x + Integer.MIN_VALUE, y + Integer.MIN_VALUE);
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
/*     */   private static int createPositiveCapacity(int minCapacity) {
/* 306 */     if (minCapacity < 0)
/*     */     {
/* 308 */       throw new OutOfMemoryError("Unable to allocate array size: " + (minCapacity & 0xFFFFFFFFL));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 318 */     return (minCapacity > 2147483639) ? minCapacity : 2147483639;
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
/*     */   protected byte[] ensureBufferSize(int size, Context context) {
/* 331 */     if (context.buffer == null) {
/* 332 */       context.buffer = new byte[getDefaultBufferSize()];
/* 333 */       context.pos = 0;
/* 334 */       context.readPos = 0;
/*     */ 
/*     */     
/*     */     }
/* 338 */     else if (context.pos + size - context.buffer.length > 0) {
/* 339 */       return resizeBuffer(context, context.pos + size);
/*     */     } 
/* 341 */     return context.buffer;
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
/*     */ 
/*     */ 
/*     */   
/*     */   int readResults(byte[] b, int bPos, int bAvail, Context context) {
/* 361 */     if (context.buffer != null) {
/* 362 */       int len = Math.min(available(context), bAvail);
/* 363 */       System.arraycopy(context.buffer, context.readPos, b, bPos, len);
/* 364 */       context.readPos += len;
/* 365 */       if (context.readPos >= context.pos) {
/* 366 */         context.buffer = null;
/*     */       }
/* 368 */       return len;
/*     */     } 
/* 370 */     return context.eof ? -1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static boolean isWhiteSpace(byte byteToCheck) {
/* 381 */     switch (byteToCheck) {
/*     */       case 9:
/*     */       case 10:
/*     */       case 13:
/*     */       case 32:
/* 386 */         return true;
/*     */     } 
/* 388 */     return false;
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
/*     */   public Object encode(Object obj) throws EncoderException {
/* 404 */     if (!(obj instanceof byte[])) {
/* 405 */       throw new EncoderException("Parameter supplied to Base-N encode is not a byte[]");
/*     */     }
/* 407 */     return encode((byte[])obj);
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
/*     */   public String encodeToString(byte[] pArray) {
/* 419 */     return StringUtils.newStringUtf8(encode(pArray));
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
/*     */   public String encodeAsString(byte[] pArray) {
/* 432 */     return StringUtils.newStringUtf8(encode(pArray));
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
/*     */   public Object decode(Object obj) throws DecoderException {
/* 448 */     if (obj instanceof byte[])
/* 449 */       return decode((byte[])obj); 
/* 450 */     if (obj instanceof String) {
/* 451 */       return decode((String)obj);
/*     */     }
/* 453 */     throw new DecoderException("Parameter supplied to Base-N decode is not a byte[] or a String");
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
/*     */   public byte[] decode(String pArray) {
/* 465 */     return decode(StringUtils.getBytesUtf8(pArray));
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
/*     */   public byte[] decode(byte[] pArray) {
/* 477 */     if (pArray == null || pArray.length == 0) {
/* 478 */       return pArray;
/*     */     }
/* 480 */     Context context = new Context();
/* 481 */     decode(pArray, 0, pArray.length, context);
/* 482 */     decode(pArray, 0, -1, context);
/* 483 */     byte[] result = new byte[context.pos];
/* 484 */     readResults(result, 0, result.length, context);
/* 485 */     return result;
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
/*     */   public byte[] encode(byte[] pArray) {
/* 497 */     if (pArray == null || pArray.length == 0) {
/* 498 */       return pArray;
/*     */     }
/* 500 */     return encode(pArray, 0, pArray.length);
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
/*     */   public byte[] encode(byte[] pArray, int offset, int length) {
/* 517 */     if (pArray == null || pArray.length == 0) {
/* 518 */       return pArray;
/*     */     }
/* 520 */     Context context = new Context();
/* 521 */     encode(pArray, offset, length, context);
/* 522 */     encode(pArray, offset, -1, context);
/* 523 */     byte[] buf = new byte[context.pos - context.readPos];
/* 524 */     readResults(buf, 0, buf.length, context);
/* 525 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void encode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Context paramContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   abstract void decode(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, Context paramContext);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isInAlphabet(byte paramByte);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInAlphabet(byte[] arrayOctet, boolean allowWSPad) {
/* 555 */     for (byte octet : arrayOctet) {
/* 556 */       if (!isInAlphabet(octet) && (!allowWSPad || (octet != this.pad && 
/* 557 */         !isWhiteSpace(octet)))) {
/* 558 */         return false;
/*     */       }
/*     */     } 
/* 561 */     return true;
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
/*     */   public boolean isInAlphabet(String basen) {
/* 574 */     return isInAlphabet(StringUtils.getBytesUtf8(basen), true);
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
/*     */   protected boolean containsAlphabetOrPad(byte[] arrayOctet) {
/* 587 */     if (arrayOctet == null) {
/* 588 */       return false;
/*     */     }
/* 590 */     for (byte element : arrayOctet) {
/* 591 */       if (this.pad == element || isInAlphabet(element)) {
/* 592 */         return true;
/*     */       }
/*     */     } 
/* 595 */     return false;
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
/*     */   public long getEncodedLength(byte[] pArray) {
/* 609 */     long len = ((pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize) * this.encodedBlockSize;
/* 610 */     if (this.lineLength > 0)
/*     */     {
/* 612 */       len += (len + this.lineLength - 1L) / this.lineLength * this.chunkSeparatorLength;
/*     */     }
/* 614 */     return len;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\binary\BaseNCodec.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */