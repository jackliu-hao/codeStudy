/*      */ package org.h2.mvstore;
/*      */ 
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.h2.util.StringUtils;
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
/*      */ public final class DataUtils
/*      */ {
/*      */   public static final int ERROR_READING_FAILED = 1;
/*      */   public static final int ERROR_WRITING_FAILED = 2;
/*      */   public static final int ERROR_INTERNAL = 3;
/*      */   public static final int ERROR_CLOSED = 4;
/*      */   public static final int ERROR_UNSUPPORTED_FORMAT = 5;
/*      */   public static final int ERROR_FILE_CORRUPT = 6;
/*      */   public static final int ERROR_FILE_LOCKED = 7;
/*      */   public static final int ERROR_SERIALIZATION = 8;
/*      */   public static final int ERROR_CHUNK_NOT_FOUND = 9;
/*      */   public static final int ERROR_BLOCK_NOT_FOUND = 50;
/*      */   public static final int ERROR_TRANSACTION_CORRUPT = 100;
/*      */   public static final int ERROR_TRANSACTION_LOCKED = 101;
/*      */   public static final int ERROR_TOO_MANY_OPEN_TRANSACTIONS = 102;
/*      */   public static final int ERROR_TRANSACTION_ILLEGAL_STATE = 103;
/*      */   public static final int ERROR_TRANSACTION_TOO_BIG = 104;
/*      */   public static final int ERROR_TRANSACTIONS_DEADLOCK = 105;
/*      */   public static final int ERROR_UNKNOWN_DATA_TYPE = 106;
/*      */   public static final int PAGE_TYPE_LEAF = 0;
/*      */   public static final int PAGE_TYPE_NODE = 1;
/*      */   public static final int PAGE_COMPRESSED = 2;
/*      */   public static final int PAGE_COMPRESSED_HIGH = 6;
/*      */   public static final int PAGE_HAS_PAGE_NO = 8;
/*      */   public static final int MAX_VAR_INT_LEN = 5;
/*      */   public static final int MAX_VAR_LONG_LEN = 10;
/*      */   public static final int COMPRESSED_VAR_INT_MAX = 2097151;
/*      */   public static final long COMPRESSED_VAR_LONG_MAX = 562949953421311L;
/*      */   public static final int PAGE_LARGE = 2097152;
/*      */   public static final String META_CHUNK = "chunk.";
/*      */   public static final String META_ROOT = "root.";
/*      */   public static final String META_NAME = "name.";
/*      */   public static final String META_MAP = "map.";
/*      */   
/*      */   public static int getVarIntLen(int paramInt) {
/*  204 */     if ((paramInt & 0xFFFFFF80) == 0)
/*  205 */       return 1; 
/*  206 */     if ((paramInt & 0xFFFFC000) == 0)
/*  207 */       return 2; 
/*  208 */     if ((paramInt & 0xFFE00000) == 0)
/*  209 */       return 3; 
/*  210 */     if ((paramInt & 0xF0000000) == 0) {
/*  211 */       return 4;
/*      */     }
/*  213 */     return 5;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getVarLongLen(long paramLong) {
/*  223 */     byte b = 1;
/*      */     while (true) {
/*  225 */       paramLong >>>= 7L;
/*  226 */       if (paramLong == 0L) {
/*  227 */         return b;
/*      */       }
/*  229 */       b++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int readVarInt(ByteBuffer paramByteBuffer) {
/*  240 */     byte b = paramByteBuffer.get();
/*  241 */     if (b >= 0) {
/*  242 */       return b;
/*      */     }
/*      */     
/*  245 */     return readVarIntRest(paramByteBuffer, b);
/*      */   }
/*      */   
/*      */   private static int readVarIntRest(ByteBuffer paramByteBuffer, int paramInt) {
/*  249 */     int i = paramInt & 0x7F;
/*  250 */     paramInt = paramByteBuffer.get();
/*  251 */     if (paramInt >= 0) {
/*  252 */       return i | paramInt << 7;
/*      */     }
/*  254 */     i |= (paramInt & 0x7F) << 7;
/*  255 */     paramInt = paramByteBuffer.get();
/*  256 */     if (paramInt >= 0) {
/*  257 */       return i | paramInt << 14;
/*      */     }
/*  259 */     i |= (paramInt & 0x7F) << 14;
/*  260 */     paramInt = paramByteBuffer.get();
/*  261 */     if (paramInt >= 0) {
/*  262 */       return i | paramInt << 21;
/*      */     }
/*  264 */     i |= (paramInt & 0x7F) << 21 | paramByteBuffer.get() << 28;
/*  265 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long readVarLong(ByteBuffer paramByteBuffer) {
/*  275 */     long l = paramByteBuffer.get();
/*  276 */     if (l >= 0L) {
/*  277 */       return l;
/*      */     }
/*  279 */     l &= 0x7FL;
/*  280 */     for (byte b = 7; b < 64; b += 7) {
/*  281 */       long l1 = paramByteBuffer.get();
/*  282 */       l |= (l1 & 0x7FL) << b;
/*  283 */       if (l1 >= 0L) {
/*      */         break;
/*      */       }
/*      */     } 
/*  287 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeVarInt(OutputStream paramOutputStream, int paramInt) throws IOException {
/*  298 */     while ((paramInt & 0xFFFFFF80) != 0) {
/*  299 */       paramOutputStream.write((byte)(paramInt | 0x80));
/*  300 */       paramInt >>>= 7;
/*      */     } 
/*  302 */     paramOutputStream.write((byte)paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeVarInt(ByteBuffer paramByteBuffer, int paramInt) {
/*  312 */     while ((paramInt & 0xFFFFFF80) != 0) {
/*  313 */       paramByteBuffer.put((byte)(paramInt | 0x80));
/*  314 */       paramInt >>>= 7;
/*      */     } 
/*  316 */     paramByteBuffer.put((byte)paramInt);
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
/*      */   public static void writeStringData(ByteBuffer paramByteBuffer, String paramString, int paramInt) {
/*  328 */     for (byte b = 0; b < paramInt; b++) {
/*  329 */       char c = paramString.charAt(b);
/*  330 */       if (c < '') {
/*  331 */         paramByteBuffer.put((byte)c);
/*  332 */       } else if (c >= 'ࠀ') {
/*  333 */         paramByteBuffer.put((byte)(0xE0 | c >> 12));
/*  334 */         paramByteBuffer.put((byte)(c >> 6 & 0x3F));
/*  335 */         paramByteBuffer.put((byte)(c & 0x3F));
/*      */       } else {
/*  337 */         paramByteBuffer.put((byte)(0xC0 | c >> 6));
/*  338 */         paramByteBuffer.put((byte)(c & 0x3F));
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
/*      */   public static String readString(ByteBuffer paramByteBuffer) {
/*  350 */     return readString(paramByteBuffer, readVarInt(paramByteBuffer));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readString(ByteBuffer paramByteBuffer, int paramInt) {
/*  361 */     char[] arrayOfChar = new char[paramInt];
/*  362 */     for (byte b = 0; b < paramInt; b++) {
/*  363 */       int i = paramByteBuffer.get() & 0xFF;
/*  364 */       if (i < 128) {
/*  365 */         arrayOfChar[b] = (char)i;
/*  366 */       } else if (i >= 224) {
/*  367 */         arrayOfChar[b] = 
/*  368 */           (char)(((i & 0xF) << 12) + ((paramByteBuffer.get() & 0x3F) << 6) + (paramByteBuffer.get() & 0x3F));
/*      */       } else {
/*  370 */         arrayOfChar[b] = (char)(((i & 0x1F) << 6) + (paramByteBuffer.get() & 0x3F));
/*      */       } 
/*      */     } 
/*  373 */     return new String(arrayOfChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeVarLong(ByteBuffer paramByteBuffer, long paramLong) {
/*  383 */     while ((paramLong & 0xFFFFFFFFFFFFFF80L) != 0L) {
/*  384 */       paramByteBuffer.put((byte)(int)(paramLong | 0x80L));
/*  385 */       paramLong >>>= 7L;
/*      */     } 
/*  387 */     paramByteBuffer.put((byte)(int)paramLong);
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
/*      */   public static void writeVarLong(OutputStream paramOutputStream, long paramLong) throws IOException {
/*  399 */     while ((paramLong & 0xFFFFFFFFFFFFFF80L) != 0L) {
/*  400 */       paramOutputStream.write((byte)(int)(paramLong | 0x80L));
/*  401 */       paramLong >>>= 7L;
/*      */     } 
/*  403 */     paramOutputStream.write((byte)(int)paramLong);
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
/*      */   public static void copyWithGap(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) {
/*  416 */     if (paramInt2 > 0) {
/*  417 */       System.arraycopy(paramObject1, 0, paramObject2, 0, paramInt2);
/*      */     }
/*  419 */     if (paramInt2 < paramInt1) {
/*  420 */       System.arraycopy(paramObject1, paramInt2, paramObject2, paramInt2 + 1, paramInt1 - paramInt2);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyExcept(Object paramObject1, Object paramObject2, int paramInt1, int paramInt2) {
/*  435 */     if (paramInt2 > 0 && paramInt1 > 0) {
/*  436 */       System.arraycopy(paramObject1, 0, paramObject2, 0, paramInt2);
/*      */     }
/*  438 */     if (paramInt2 < paramInt1) {
/*  439 */       System.arraycopy(paramObject1, paramInt2 + 1, paramObject2, paramInt2, paramInt1 - paramInt2 - 1);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static void readFully(FileChannel paramFileChannel, long paramLong, ByteBuffer paramByteBuffer) {
/*      */     try {
/*      */       while (true)
/*  456 */       { int i = paramFileChannel.read(paramByteBuffer, paramLong);
/*  457 */         if (i < 0) {
/*  458 */           throw new EOFException();
/*      */         }
/*  460 */         paramLong += i;
/*  461 */         if (paramByteBuffer.remaining() <= 0)
/*  462 */         { paramByteBuffer.rewind(); return; }  } 
/*  463 */     } catch (IOException iOException) {
/*      */       long l;
/*      */       try {
/*  466 */         l = paramFileChannel.size();
/*  467 */       } catch (IOException iOException1) {
/*  468 */         l = -1L;
/*      */       } 
/*  470 */       throw newMVStoreException(1, "Reading from file {0} failed at {1} (length {2}), read {3}, remaining {4}", new Object[] { paramFileChannel, 
/*      */ 
/*      */ 
/*      */             
/*  474 */             Long.valueOf(paramLong), Long.valueOf(l), Integer.valueOf(paramByteBuffer.position()), Integer.valueOf(paramByteBuffer.remaining()), iOException });
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
/*      */   public static void writeFully(FileChannel paramFileChannel, long paramLong, ByteBuffer paramByteBuffer) {
/*      */     try {
/*  487 */       int i = 0;
/*      */       do {
/*  489 */         int j = paramFileChannel.write(paramByteBuffer, paramLong + i);
/*  490 */         i += j;
/*  491 */       } while (paramByteBuffer.remaining() > 0);
/*  492 */     } catch (IOException iOException) {
/*  493 */       throw newMVStoreException(2, "Writing to {0} failed; length {1} at {2}", new Object[] { paramFileChannel, 
/*      */ 
/*      */             
/*  496 */             Integer.valueOf(paramByteBuffer.remaining()), Long.valueOf(paramLong), iOException });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int encodeLength(int paramInt) {
/*  507 */     if (paramInt <= 32) {
/*  508 */       return 0;
/*      */     }
/*  510 */     int i = Integer.numberOfLeadingZeros(paramInt);
/*  511 */     int j = paramInt << i + 1;
/*  512 */     i += i;
/*  513 */     if ((j & Integer.MIN_VALUE) != 0) {
/*  514 */       i--;
/*      */     }
/*  516 */     if (j << 1 != 0) {
/*  517 */       i--;
/*      */     }
/*  519 */     i = Math.min(31, 52 - i);
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
/*  530 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getPageChunkId(long paramLong) {
/*  540 */     return (int)(paramLong >>> 38L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getPageMapId(long paramLong) {
/*  550 */     return (int)(paramLong >>> 38L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getPageMaxLength(long paramLong) {
/*  560 */     int i = (int)(paramLong >> 1L & 0x1FL);
/*  561 */     return decodePageLength(i);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int decodePageLength(int paramInt) {
/*  572 */     if (paramInt == 31) {
/*  573 */       return 2097152;
/*      */     }
/*  575 */     return 2 + (paramInt & 0x1) << (paramInt >> 1) + 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getPageOffset(long paramLong) {
/*  585 */     return (int)(paramLong >> 6L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getPageType(long paramLong) {
/*  595 */     return (int)paramLong & 0x1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isLeafPosition(long paramLong) {
/*  604 */     return (getPageType(paramLong) == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPageSaved(long paramLong) {
/*  614 */     return ((paramLong & 0xFFFFFFFFFFFFFFFEL) != 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static boolean isPageRemoved(long paramLong) {
/*  625 */     return (paramLong == 1L);
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
/*      */   public static long getPagePos(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  640 */     long l = paramInt1 << 38L;
/*  641 */     l |= paramInt2 << 6L;
/*  642 */     l |= (encodeLength(paramInt3) << 1);
/*  643 */     l |= paramInt4;
/*  644 */     return l;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long getPagePos(int paramInt, long paramLong) {
/*  655 */     return paramLong & 0x3FFFFFFFFFL | paramInt << 38L;
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
/*      */   public static long getTocElement(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
/*  670 */     long l = paramInt1 << 38L;
/*  671 */     l |= paramInt2 << 6L;
/*  672 */     l |= (encodeLength(paramInt3) << 1);
/*  673 */     l |= paramInt4;
/*  674 */     return l;
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
/*      */   public static short getCheckValue(int paramInt) {
/*  686 */     return (short)(paramInt >> 16 ^ paramInt);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static StringBuilder appendMap(StringBuilder paramStringBuilder, HashMap<String, ?> paramHashMap) {
/*  697 */     Object[] arrayOfObject = paramHashMap.keySet().toArray();
/*  698 */     Arrays.sort(arrayOfObject);
/*  699 */     for (Object object1 : arrayOfObject) {
/*  700 */       String str = (String)object1;
/*  701 */       Object object2 = paramHashMap.get(str);
/*  702 */       if (object2 instanceof Long) {
/*  703 */         appendMap(paramStringBuilder, str, ((Long)object2).longValue());
/*  704 */       } else if (object2 instanceof Integer) {
/*  705 */         appendMap(paramStringBuilder, str, ((Integer)object2).intValue());
/*      */       } else {
/*  707 */         appendMap(paramStringBuilder, str, object2.toString());
/*      */       } 
/*      */     } 
/*  710 */     return paramStringBuilder;
/*      */   }
/*      */   
/*      */   private static StringBuilder appendMapKey(StringBuilder paramStringBuilder, String paramString) {
/*  714 */     if (paramStringBuilder.length() > 0) {
/*  715 */       paramStringBuilder.append(',');
/*      */     }
/*  717 */     return paramStringBuilder.append(paramString).append(':');
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
/*      */   public static void appendMap(StringBuilder paramStringBuilder, String paramString1, String paramString2) {
/*  730 */     appendMapKey(paramStringBuilder, paramString1);
/*  731 */     if (paramString2.indexOf(',') < 0 && paramString2.indexOf('"') < 0) {
/*  732 */       paramStringBuilder.append(paramString2);
/*      */     } else {
/*  734 */       paramStringBuilder.append('"'); byte b; int i;
/*  735 */       for (b = 0, i = paramString2.length(); b < i; b++) {
/*  736 */         char c = paramString2.charAt(b);
/*  737 */         if (c == '"') {
/*  738 */           paramStringBuilder.append('\\');
/*      */         }
/*  740 */         paramStringBuilder.append(c);
/*      */       } 
/*  742 */       paramStringBuilder.append('"');
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
/*      */   
/*      */   public static void appendMap(StringBuilder paramStringBuilder, String paramString, long paramLong) {
/*  755 */     appendMapKey(paramStringBuilder, paramString).append(Long.toHexString(paramLong));
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
/*      */   public static void appendMap(StringBuilder paramStringBuilder, String paramString, int paramInt) {
/*  767 */     appendMapKey(paramStringBuilder, paramString).append(Integer.toHexString(paramInt));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int parseMapValue(StringBuilder paramStringBuilder, String paramString, int paramInt1, int paramInt2) {
/*  778 */     while (paramInt1 < paramInt2) {
/*  779 */       char c = paramString.charAt(paramInt1++);
/*  780 */       if (c == ',')
/*      */         break; 
/*  782 */       if (c == '"') {
/*  783 */         while (paramInt1 < paramInt2) {
/*  784 */           c = paramString.charAt(paramInt1++);
/*  785 */           if (c == '\\') {
/*  786 */             if (paramInt1 == paramInt2) {
/*  787 */               throw newMVStoreException(6, "Not a map: {0}", new Object[] { paramString });
/*      */             }
/*  789 */             c = paramString.charAt(paramInt1++);
/*  790 */           } else if (c == '"') {
/*      */             break;
/*      */           } 
/*  793 */           paramStringBuilder.append(c);
/*      */         }  continue;
/*      */       } 
/*  796 */       paramStringBuilder.append(c);
/*      */     } 
/*      */     
/*  799 */     return paramInt1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static HashMap<String, String> parseMap(String paramString) {
/*  810 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  811 */     StringBuilder stringBuilder = new StringBuilder();
/*  812 */     for (int i = 0, j = paramString.length(); i < j; ) {
/*  813 */       int k = i;
/*  814 */       i = paramString.indexOf(':', i);
/*  815 */       if (i < 0) {
/*  816 */         throw newMVStoreException(6, "Not a map: {0}", new Object[] { paramString });
/*      */       }
/*  818 */       String str = paramString.substring(k, i++);
/*  819 */       i = parseMapValue(stringBuilder, paramString, i, j);
/*  820 */       hashMap.put(str, stringBuilder.toString());
/*  821 */       stringBuilder.setLength(0);
/*      */     } 
/*  823 */     return (HashMap)hashMap;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static HashMap<String, String> parseChecksummedMap(byte[] paramArrayOfbyte) {
/*  834 */     byte b = 0; int i = paramArrayOfbyte.length;
/*  835 */     while (b < i && paramArrayOfbyte[b] <= 32) {
/*  836 */       b++;
/*      */     }
/*  838 */     while (b < i && paramArrayOfbyte[i - 1] <= 32) {
/*  839 */       i--;
/*      */     }
/*  841 */     String str = new String(paramArrayOfbyte, b, i - b, StandardCharsets.ISO_8859_1);
/*  842 */     HashMap<Object, Object> hashMap = new HashMap<>();
/*  843 */     StringBuilder stringBuilder = new StringBuilder();
/*  844 */     for (int j = 0, k = str.length(); j < k; ) {
/*  845 */       int m = j;
/*  846 */       j = str.indexOf(':', j);
/*  847 */       if (j < 0)
/*      */       {
/*  849 */         return null;
/*      */       }
/*  851 */       if (j - m == 8 && str.regionMatches(m, "fletcher", 0, 8)) {
/*  852 */         parseMapValue(stringBuilder, str, j + 1, k);
/*  853 */         int n = (int)Long.parseLong(stringBuilder.toString(), 16);
/*  854 */         if (n == getFletcher32(paramArrayOfbyte, b, m - 1)) {
/*  855 */           return (HashMap)hashMap;
/*      */         }
/*      */         
/*  858 */         return null;
/*      */       } 
/*  860 */       String str1 = str.substring(m, j++);
/*  861 */       j = parseMapValue(stringBuilder, str, j, k);
/*  862 */       hashMap.put(str1, stringBuilder.toString());
/*  863 */       stringBuilder.setLength(0);
/*      */     } 
/*      */     
/*  866 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getMapName(String paramString) {
/*  877 */     return getFromMap(paramString, "name");
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
/*      */   public static String getFromMap(String paramString1, String paramString2) {
/*  889 */     int i = paramString2.length();
/*  890 */     for (int j = 0, k = paramString1.length(); j < k; ) {
/*  891 */       int m = j;
/*  892 */       j = paramString1.indexOf(':', j);
/*  893 */       if (j < 0) {
/*  894 */         throw newMVStoreException(6, "Not a map: {0}", new Object[] { paramString1 });
/*      */       }
/*  896 */       if (j++ - m == i && paramString1.regionMatches(m, paramString2, 0, i)) {
/*  897 */         StringBuilder stringBuilder = new StringBuilder();
/*  898 */         parseMapValue(stringBuilder, paramString1, j, k);
/*  899 */         return stringBuilder.toString();
/*      */       } 
/*  901 */       while (j < k) {
/*  902 */         char c = paramString1.charAt(j++);
/*  903 */         if (c == ',')
/*      */           break; 
/*  905 */         if (c == '"') {
/*  906 */           while (j < k) {
/*  907 */             c = paramString1.charAt(j++);
/*  908 */             if (c == '\\') {
/*  909 */               if (j++ == k)
/*  910 */                 throw newMVStoreException(6, "Not a map: {0}", new Object[] { paramString1 });  continue;
/*      */             } 
/*  912 */             if (c == '"') {
/*      */               break;
/*      */             }
/*      */           } 
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  920 */     return null;
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
/*      */   public static int getFletcher32(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
/*  932 */     int i = 65535, j = 65535;
/*  933 */     int k = paramInt1, m = paramInt1 + (paramInt2 & 0xFFFFFFFE);
/*  934 */     while (k < m) {
/*      */       
/*  936 */       for (int n = Math.min(k + 720, m); k < n; ) {
/*  937 */         int i1 = (paramArrayOfbyte[k++] & 0xFF) << 8 | paramArrayOfbyte[k++] & 0xFF;
/*  938 */         j += i += i1;
/*      */       } 
/*  940 */       i = (i & 0xFFFF) + (i >>> 16);
/*  941 */       j = (j & 0xFFFF) + (j >>> 16);
/*      */     } 
/*  943 */     if ((paramInt2 & 0x1) != 0) {
/*      */       
/*  945 */       int n = (paramArrayOfbyte[k] & 0xFF) << 8;
/*  946 */       j += i += n;
/*      */     } 
/*  948 */     i = (i & 0xFFFF) + (i >>> 16);
/*  949 */     j = (j & 0xFFFF) + (j >>> 16);
/*  950 */     return j << 16 | i;
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
/*      */   public static void checkArgument(boolean paramBoolean, String paramString, Object... paramVarArgs) {
/*  963 */     if (!paramBoolean) {
/*  964 */       throw newIllegalArgumentException(paramString, paramVarArgs);
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
/*      */   
/*      */   public static IllegalArgumentException newIllegalArgumentException(String paramString, Object... paramVarArgs) {
/*  977 */     return initCause(new IllegalArgumentException(
/*  978 */           formatMessage(0, paramString, paramVarArgs)), paramVarArgs);
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
/*      */   public static UnsupportedOperationException newUnsupportedOperationException(String paramString) {
/*  990 */     return new UnsupportedOperationException(formatMessage(0, paramString, new Object[0]));
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
/*      */   public static MVStoreException newMVStoreException(int paramInt, String paramString, Object... paramVarArgs) {
/* 1003 */     return initCause(new MVStoreException(paramInt, 
/* 1004 */           formatMessage(paramInt, paramString, paramVarArgs)), paramVarArgs);
/*      */   }
/*      */ 
/*      */   
/*      */   private static <T extends Exception> T initCause(T paramT, Object... paramVarArgs) {
/* 1009 */     int i = paramVarArgs.length;
/* 1010 */     if (i > 0) {
/* 1011 */       Object object = paramVarArgs[i - 1];
/* 1012 */       if (object instanceof Throwable) {
/* 1013 */         paramT.initCause((Throwable)object);
/*      */       }
/*      */     } 
/* 1016 */     return paramT;
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
/*      */   public static String formatMessage(int paramInt, String paramString, Object... paramVarArgs) {
/* 1030 */     paramVarArgs = (Object[])paramVarArgs.clone();
/* 1031 */     for (byte b = 0; b < paramVarArgs.length; b++) {
/* 1032 */       Object object = paramVarArgs[b];
/* 1033 */       if (!(object instanceof Exception)) {
/* 1034 */         String str = (object == null) ? "null" : object.toString();
/* 1035 */         if (str.length() > 1000) {
/* 1036 */           str = str.substring(0, 1000) + "...";
/*      */         }
/* 1038 */         paramVarArgs[b] = str;
/*      */       } 
/*      */     } 
/* 1041 */     return MessageFormat.format(paramString, paramVarArgs) + " [" + '\002' + "." + '\001' + "." + 'Ò' + "/" + paramInt + "]";
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
/*      */   public static long readHexLong(Map<String, ?> paramMap, String paramString, long paramLong) {
/* 1057 */     Object object = paramMap.get(paramString);
/* 1058 */     if (object == null)
/* 1059 */       return paramLong; 
/* 1060 */     if (object instanceof Long) {
/* 1061 */       return ((Long)object).longValue();
/*      */     }
/*      */     try {
/* 1064 */       return parseHexLong((String)object);
/* 1065 */     } catch (NumberFormatException numberFormatException) {
/* 1066 */       throw newMVStoreException(6, "Error parsing the value {0}", new Object[] { object, numberFormatException });
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
/*      */   
/*      */   public static long parseHexLong(String paramString) {
/*      */     try {
/* 1080 */       if (paramString.length() == 16)
/*      */       {
/*      */         
/* 1083 */         return Long.parseLong(paramString.substring(0, 8), 16) << 32L | 
/* 1084 */           Long.parseLong(paramString.substring(8, 16), 16);
/*      */       }
/* 1086 */       return Long.parseLong(paramString, 16);
/* 1087 */     } catch (NumberFormatException numberFormatException) {
/* 1088 */       throw newMVStoreException(6, "Error parsing the value {0}", new Object[] { paramString, numberFormatException });
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static int parseHexInt(String paramString) {
/*      */     try {
/* 1104 */       return (int)Long.parseLong(paramString, 16);
/* 1105 */     } catch (NumberFormatException numberFormatException) {
/* 1106 */       throw newMVStoreException(6, "Error parsing the value {0}", new Object[] { paramString, numberFormatException });
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
/*      */ 
/*      */ 
/*      */   
/*      */   static int readHexInt(Map<String, ?> paramMap, String paramString, int paramInt) {
/* 1121 */     Object object = paramMap.get(paramString);
/* 1122 */     if (object == null)
/* 1123 */       return paramInt; 
/* 1124 */     if (object instanceof Integer) {
/* 1125 */       return ((Integer)object).intValue();
/*      */     }
/*      */     
/*      */     try {
/* 1129 */       return (int)Long.parseLong((String)object, 16);
/* 1130 */     } catch (NumberFormatException numberFormatException) {
/* 1131 */       throw newMVStoreException(6, "Error parsing the value {0}", new Object[] { object, numberFormatException });
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
/*      */   
/*      */   static byte[] parseHexBytes(Map<String, ?> paramMap, String paramString) {
/* 1144 */     Object object = paramMap.get(paramString);
/* 1145 */     if (object == null) {
/* 1146 */       return null;
/*      */     }
/* 1148 */     return StringUtils.convertHexToBytes((String)object);
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
/*      */   static int getConfigParam(Map<String, ?> paramMap, String paramString, int paramInt) {
/* 1160 */     Object object = paramMap.get(paramString);
/* 1161 */     if (object instanceof Number)
/* 1162 */       return ((Number)object).intValue(); 
/* 1163 */     if (object != null) {
/*      */       try {
/* 1165 */         return Integer.decode(object.toString()).intValue();
/* 1166 */       } catch (NumberFormatException numberFormatException) {}
/*      */     }
/*      */ 
/*      */     
/* 1170 */     return paramInt;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static IOException convertToIOException(Throwable paramThrowable) {
/* 1180 */     if (paramThrowable instanceof IOException) {
/* 1181 */       return (IOException)paramThrowable;
/*      */     }
/* 1183 */     if (paramThrowable instanceof org.h2.jdbc.JdbcException && 
/* 1184 */       paramThrowable.getCause() != null) {
/* 1185 */       paramThrowable = paramThrowable.getCause();
/*      */     }
/*      */     
/* 1188 */     return new IOException(paramThrowable.toString(), paramThrowable);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\DataUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */