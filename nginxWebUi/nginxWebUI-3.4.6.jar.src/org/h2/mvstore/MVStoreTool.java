/*     */ package org.h2.mvstore;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.h2.compress.CompressDeflate;
/*     */ import org.h2.compress.CompressLZF;
/*     */ import org.h2.compress.Compressor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.type.BasicDataType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.StringDataType;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MVStoreTool
/*     */ {
/*     */   public static void main(String... paramVarArgs) {
/*  55 */     for (byte b = 0; b < paramVarArgs.length; b++) {
/*  56 */       if ("-dump".equals(paramVarArgs[b])) {
/*  57 */         String str = paramVarArgs[++b];
/*  58 */         dump(str, new PrintWriter(System.out), true);
/*  59 */       } else if ("-info".equals(paramVarArgs[b])) {
/*  60 */         String str = paramVarArgs[++b];
/*  61 */         info(str, new PrintWriter(System.out));
/*  62 */       } else if ("-compact".equals(paramVarArgs[b])) {
/*  63 */         String str = paramVarArgs[++b];
/*  64 */         compact(str, false);
/*  65 */       } else if ("-compress".equals(paramVarArgs[b])) {
/*  66 */         String str = paramVarArgs[++b];
/*  67 */         compact(str, true);
/*  68 */       } else if ("-rollback".equals(paramVarArgs[b])) {
/*  69 */         String str = paramVarArgs[++b];
/*  70 */         long l = Long.decode(paramVarArgs[++b]).longValue();
/*  71 */         rollback(str, l, new PrintWriter(System.out));
/*  72 */       } else if ("-repair".equals(paramVarArgs[b])) {
/*  73 */         String str = paramVarArgs[++b];
/*  74 */         repair(str);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void dump(String paramString, boolean paramBoolean) {
/*  86 */     dump(paramString, new PrintWriter(System.out), paramBoolean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void info(String paramString) {
/*  95 */     info(paramString, new PrintWriter(System.out));
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
/*     */   public static void dump(String paramString, Writer paramWriter, boolean paramBoolean) {
/* 107 */     PrintWriter printWriter = new PrintWriter(paramWriter, true);
/* 108 */     if (!FilePath.get(paramString).exists()) {
/* 109 */       printWriter.println("File not found: " + paramString);
/*     */       return;
/*     */     } 
/* 112 */     long l1 = FileUtils.size(paramString);
/* 113 */     printWriter.printf("File %s, %d bytes, %d MB\n", new Object[] { paramString, Long.valueOf(l1), Long.valueOf(l1 / 1024L / 1024L) });
/* 114 */     char c = 'က';
/* 115 */     TreeMap<Object, Object> treeMap = new TreeMap<>();
/*     */     
/* 117 */     long l2 = 0L;
/* 118 */     try (FileChannel null = FilePath.get(paramString).open("r")) {
/* 119 */       long l3 = fileChannel.size();
/* 120 */       int i = Long.toHexString(l3).length();
/* 121 */       ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
/* 122 */       long l4 = 0L;
/* 123 */       for (long l5 = 0L; l5 < l3; ) {
/* 124 */         Chunk chunk; byteBuffer.rewind();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         try {
/* 130 */           DataUtils.readFully(fileChannel, l5, byteBuffer);
/* 131 */         } catch (MVStoreException mVStoreException) {
/* 132 */           l5 += c;
/* 133 */           printWriter.printf("ERROR illegal position %d%n", new Object[] { Long.valueOf(l5) });
/*     */           continue;
/*     */         } 
/* 136 */         byteBuffer.rewind();
/* 137 */         byte b = byteBuffer.get();
/* 138 */         if (b == 72) {
/* 139 */           String str = (new String(byteBuffer.array(), StandardCharsets.ISO_8859_1)).trim();
/* 140 */           printWriter.printf("%0" + i + "x fileHeader %s%n", new Object[] {
/* 141 */                 Long.valueOf(l5), str });
/* 142 */           l5 += c;
/*     */           continue;
/*     */         } 
/* 145 */         if (b != 99) {
/* 146 */           l5 += c;
/*     */           continue;
/*     */         } 
/* 149 */         byteBuffer.position(0);
/*     */         
/*     */         try {
/* 152 */           chunk = Chunk.readChunkHeader(byteBuffer, l5);
/* 153 */         } catch (MVStoreException mVStoreException) {
/* 154 */           l5 += c;
/*     */           continue;
/*     */         } 
/* 157 */         if (chunk.len <= 0) {
/*     */           
/* 159 */           l5 += c;
/*     */           continue;
/*     */         } 
/* 162 */         int j = chunk.len * 4096;
/* 163 */         printWriter.printf("%n%0" + i + "x chunkHeader %s%n", new Object[] {
/* 164 */               Long.valueOf(l5), chunk.toString() });
/* 165 */         ByteBuffer byteBuffer1 = ByteBuffer.allocate(j);
/* 166 */         DataUtils.readFully(fileChannel, l5, byteBuffer1);
/* 167 */         int k = byteBuffer.position();
/* 168 */         l5 += j;
/* 169 */         int m = chunk.pageCount;
/* 170 */         l4 += chunk.pageCount;
/* 171 */         TreeMap<Object, Object> treeMap1 = new TreeMap<>();
/*     */         
/* 173 */         int n = 0;
/* 174 */         while (m > 0) {
/* 175 */           int i2 = k;
/*     */           try {
/* 177 */             byteBuffer1.position(k);
/* 178 */           } catch (IllegalArgumentException illegalArgumentException) {
/*     */             
/* 180 */             printWriter.printf("ERROR illegal position %d%n", new Object[] { Integer.valueOf(k) });
/*     */             break;
/*     */           } 
/* 183 */           int i3 = byteBuffer1.getInt();
/*     */           
/* 185 */           byteBuffer1.getShort();
/* 186 */           DataUtils.readVarInt(byteBuffer1);
/* 187 */           int i4 = DataUtils.readVarInt(byteBuffer1);
/* 188 */           int i5 = DataUtils.readVarInt(byteBuffer1);
/* 189 */           byte b1 = byteBuffer1.get();
/* 190 */           boolean bool1 = ((b1 & 0x2) != 0) ? true : false;
/* 191 */           boolean bool2 = ((b1 & 0x1) != 0) ? true : false;
/* 192 */           if (paramBoolean) {
/* 193 */             printWriter.printf("+%0" + i + "x %s, map %x, %d entries, %d bytes, maxLen %x%n", new Object[] {
/*     */ 
/*     */                   
/* 196 */                   Integer.valueOf(k), (bool2 ? "node" : "leaf") + (bool1 ? " compressed" : ""), 
/*     */ 
/*     */                   
/* 199 */                   Integer.valueOf(i4), 
/* 200 */                   Integer.valueOf(bool2 ? (i5 + 1) : i5), 
/* 201 */                   Integer.valueOf(i3), 
/* 202 */                   Integer.valueOf(DataUtils.getPageMaxLength(DataUtils.getPagePos(0, 0, i3, 0)))
/*     */                 });
/*     */           }
/* 205 */           k += i3;
/* 206 */           Integer integer = (Integer)treeMap1.get(Integer.valueOf(i4));
/* 207 */           if (integer == null) {
/* 208 */             integer = Integer.valueOf(0);
/*     */           }
/* 210 */           treeMap1.put(Integer.valueOf(i4), Integer.valueOf(integer.intValue() + i3));
/* 211 */           Long long_ = (Long)treeMap.get(Integer.valueOf(i4));
/* 212 */           if (long_ == null) {
/* 213 */             long_ = Long.valueOf(0L);
/*     */           }
/* 215 */           treeMap.put(Integer.valueOf(i4), Long.valueOf(long_.longValue() + i3));
/* 216 */           n += i3;
/* 217 */           l2 += i3;
/* 218 */           m--;
/* 219 */           long[] arrayOfLong1 = null;
/* 220 */           long[] arrayOfLong2 = null;
/* 221 */           if (bool2) {
/* 222 */             arrayOfLong1 = new long[i5 + 1]; byte b2;
/* 223 */             for (b2 = 0; b2 <= i5; b2++) {
/* 224 */               arrayOfLong1[b2] = byteBuffer1.getLong();
/*     */             }
/* 226 */             arrayOfLong2 = new long[i5 + 1];
/* 227 */             for (b2 = 0; b2 <= i5; b2++) {
/* 228 */               long l = DataUtils.readVarLong(byteBuffer1);
/* 229 */               arrayOfLong2[b2] = l;
/*     */             } 
/*     */           } 
/* 232 */           String[] arrayOfString = new String[i5];
/* 233 */           if (i4 == 0 && paramBoolean) {
/*     */             ByteBuffer byteBuffer2;
/* 235 */             if (bool1) {
/* 236 */               boolean bool = ((b1 & 0x6) != 6) ? true : false;
/* 237 */               Compressor compressor = getCompressor(bool);
/* 238 */               int i6 = DataUtils.readVarInt(byteBuffer1);
/* 239 */               int i7 = i3 + i2 - byteBuffer1.position();
/* 240 */               byte[] arrayOfByte = Utils.newBytes(i7);
/* 241 */               byteBuffer1.get(arrayOfByte);
/* 242 */               int i8 = i7 + i6;
/* 243 */               byteBuffer2 = ByteBuffer.allocate(i8);
/* 244 */               compressor.expand(arrayOfByte, 0, i7, byteBuffer2.array(), 0, i8);
/*     */             } else {
/* 246 */               byteBuffer2 = byteBuffer1;
/*     */             }  byte b2;
/* 248 */             for (b2 = 0; b2 < i5; b2++) {
/* 249 */               String str = StringDataType.INSTANCE.read(byteBuffer2);
/* 250 */               arrayOfString[b2] = str;
/*     */             } 
/* 252 */             if (bool2) {
/*     */               
/* 254 */               for (b2 = 0; b2 < i5; b2++) {
/* 255 */                 long l6 = arrayOfLong1[b2];
/* 256 */                 printWriter.printf("    %d children < %s @ chunk %x +%0" + i + "x%n", new Object[] {
/*     */ 
/*     */                       
/* 259 */                       Long.valueOf(arrayOfLong2[b2]), arrayOfString[b2], 
/*     */                       
/* 261 */                       Integer.valueOf(DataUtils.getPageChunkId(l6)), 
/* 262 */                       Integer.valueOf(DataUtils.getPageOffset(l6)) });
/*     */               } 
/* 264 */               long l = arrayOfLong1[i5];
/* 265 */               printWriter.printf("    %d children >= %s @ chunk %x +%0" + i + "x%n", new Object[] {
/*     */                     
/* 267 */                     Long.valueOf(arrayOfLong2[i5]), (arrayOfString.length >= i5) ? null : arrayOfString[i5], 
/*     */                     
/* 269 */                     Integer.valueOf(DataUtils.getPageChunkId(l)), 
/* 270 */                     Integer.valueOf(DataUtils.getPageOffset(l)) });
/*     */               continue;
/*     */             } 
/* 273 */             String[] arrayOfString1 = new String[i5]; byte b3;
/* 274 */             for (b3 = 0; b3 < i5; b3++) {
/* 275 */               String str = StringDataType.INSTANCE.read(byteBuffer2);
/* 276 */               arrayOfString1[b3] = str;
/*     */             } 
/* 278 */             for (b3 = 0; b3 < i5; b3++) {
/* 279 */               printWriter.println("    " + arrayOfString[b3] + " = " + arrayOfString1[b3]);
/*     */             }
/*     */             
/*     */             continue;
/*     */           } 
/* 284 */           if (bool2 && paramBoolean) {
/* 285 */             for (byte b2 = 0; b2 <= i5; b2++) {
/* 286 */               long l = arrayOfLong1[b2];
/* 287 */               printWriter.printf("    %d children @ chunk %x +%0" + i + "x%n", new Object[] {
/*     */                     
/* 289 */                     Long.valueOf(arrayOfLong2[b2]), 
/* 290 */                     Integer.valueOf(DataUtils.getPageChunkId(l)), 
/* 291 */                     Integer.valueOf(DataUtils.getPageOffset(l))
/*     */                   });
/*     */             } 
/*     */           }
/*     */         } 
/* 296 */         n = Math.max(1, n);
/* 297 */         for (Integer integer : treeMap1.keySet()) {
/* 298 */           int i2 = 100 * ((Integer)treeMap1.get(integer)).intValue() / n;
/* 299 */           printWriter.printf("map %x: %d bytes, %d%%%n", new Object[] { integer, treeMap1.get(integer), Integer.valueOf(i2) });
/*     */         } 
/* 301 */         int i1 = byteBuffer1.limit() - 128;
/*     */         try {
/* 303 */           byteBuffer1.position(i1);
/* 304 */           printWriter.printf("+%0" + i + "x chunkFooter %s%n", new Object[] {
/*     */                 
/* 306 */                 Integer.valueOf(i1), (new String(byteBuffer1
/* 307 */                   .array(), byteBuffer1.position(), 128, StandardCharsets.ISO_8859_1))
/* 308 */                 .trim() });
/* 309 */         } catch (IllegalArgumentException illegalArgumentException) {
/*     */           
/* 311 */           printWriter.printf("ERROR illegal footer position %d%n", new Object[] { Integer.valueOf(i1) });
/*     */         } 
/*     */       } 
/* 314 */       printWriter.printf("%n%0" + i + "x eof%n", new Object[] { Long.valueOf(l3) });
/* 315 */       printWriter.printf("\n", new Object[0]);
/* 316 */       l4 = Math.max(1L, l4);
/* 317 */       printWriter.printf("page size total: %d bytes, page count: %d, average page size: %d bytes\n", new Object[] {
/* 318 */             Long.valueOf(l2), Long.valueOf(l4), Long.valueOf(l2 / l4) });
/* 319 */       l2 = Math.max(1L, l2);
/* 320 */       for (Integer integer : treeMap.keySet()) {
/* 321 */         int j = (int)(100L * ((Long)treeMap.get(integer)).longValue() / l2);
/* 322 */         printWriter.printf("map %x: %d bytes, %d%%%n", new Object[] { integer, treeMap.get(integer), Integer.valueOf(j) });
/*     */       } 
/* 324 */     } catch (IOException iOException) {
/* 325 */       printWriter.println("ERROR: " + iOException);
/* 326 */       iOException.printStackTrace(printWriter);
/*     */     } 
/*     */     
/* 329 */     printWriter.flush();
/*     */   }
/*     */   
/*     */   private static Compressor getCompressor(boolean paramBoolean) {
/* 333 */     return paramBoolean ? (Compressor)new CompressLZF() : (Compressor)new CompressDeflate();
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
/*     */   public static String info(String paramString, Writer paramWriter) {
/* 345 */     PrintWriter printWriter = new PrintWriter(paramWriter, true);
/* 346 */     if (!FilePath.get(paramString).exists()) {
/* 347 */       printWriter.println("File not found: " + paramString);
/* 348 */       return "File not found: " + paramString;
/*     */     } 
/* 350 */     long l = FileUtils.size(paramString);
/*     */ 
/*     */     
/* 353 */     try (MVStore null = (new MVStore.Builder()).fileName(paramString).recoveryMode().readOnly().open()) {
/* 354 */       MVMap<String, String> mVMap = mVStore.getLayoutMap();
/* 355 */       Map<String, Object> map = mVStore.getStoreHeader();
/* 356 */       long l1 = DataUtils.readHexLong(map, "created", 0L);
/* 357 */       TreeMap<Object, Object> treeMap = new TreeMap<>();
/* 358 */       long l2 = 0L;
/* 359 */       long l3 = 0L;
/* 360 */       long l4 = 0L;
/* 361 */       long l5 = 0L;
/* 362 */       for (Map.Entry<String, String> entry : mVMap.entrySet()) {
/* 363 */         String str = (String)entry.getKey();
/* 364 */         if (str.startsWith("chunk.")) {
/* 365 */           Chunk chunk = Chunk.fromString((String)entry.getValue());
/* 366 */           treeMap.put(Integer.valueOf(chunk.id), chunk);
/* 367 */           l2 += (chunk.len * 4096);
/* 368 */           l3 += chunk.maxLen;
/* 369 */           l4 += chunk.maxLenLive;
/* 370 */           if (chunk.maxLenLive > 0L) {
/* 371 */             l5 += chunk.maxLen;
/*     */           }
/*     */         } 
/*     */       } 
/* 375 */       printWriter.printf("Created: %s\n", new Object[] { formatTimestamp(l1, l1) });
/* 376 */       printWriter.printf("Last modified: %s\n", new Object[] {
/* 377 */             formatTimestamp(FileUtils.lastModified(paramString), l1) });
/* 378 */       printWriter.printf("File length: %d\n", new Object[] { Long.valueOf(l) });
/* 379 */       printWriter.printf("The last chunk is not listed\n", new Object[0]);
/* 380 */       printWriter.printf("Chunk length: %d\n", new Object[] { Long.valueOf(l2) });
/* 381 */       printWriter.printf("Chunk count: %d\n", new Object[] { Integer.valueOf(treeMap.size()) });
/* 382 */       printWriter.printf("Used space: %d%%\n", new Object[] { Integer.valueOf(getPercent(l2, l)) });
/* 383 */       printWriter.printf("Chunk fill rate: %d%%\n", new Object[] { Integer.valueOf((l3 == 0L) ? 100 : 
/* 384 */               getPercent(l4, l3)) });
/* 385 */       printWriter.printf("Chunk fill rate excluding empty chunks: %d%%\n", new Object[] {
/* 386 */             Integer.valueOf((l5 == 0L) ? 100 : 
/* 387 */               getPercent(l4, l5)) });
/* 388 */       for (Map.Entry<Object, Object> entry : treeMap.entrySet()) {
/* 389 */         Chunk chunk = (Chunk)entry.getValue();
/* 390 */         long l6 = l1 + chunk.time;
/* 391 */         printWriter.printf("  Chunk %d: %s, %d%% used, %d blocks", new Object[] {
/* 392 */               Integer.valueOf(chunk.id), formatTimestamp(l6, l1), 
/* 393 */               Integer.valueOf(getPercent(chunk.maxLenLive, chunk.maxLen)), 
/* 394 */               Integer.valueOf(chunk.len)
/*     */             });
/* 396 */         if (chunk.maxLenLive == 0L)
/* 397 */           printWriter.printf(", unused: %s", new Object[] {
/* 398 */                 formatTimestamp(l1 + chunk.unused, l1)
/*     */               }); 
/* 400 */         printWriter.printf("\n", new Object[0]);
/*     */       } 
/* 402 */       printWriter.printf("\n", new Object[0]);
/* 403 */     } catch (Exception exception) {
/* 404 */       printWriter.println("ERROR: " + exception);
/* 405 */       exception.printStackTrace(printWriter);
/* 406 */       return exception.getMessage();
/*     */     } 
/* 408 */     printWriter.flush();
/* 409 */     return null;
/*     */   }
/*     */   
/*     */   private static String formatTimestamp(long paramLong1, long paramLong2) {
/* 413 */     String str1 = (new Timestamp(paramLong1)).toString();
/* 414 */     String str2 = str1.substring(0, 19);
/* 415 */     str2 = str2 + " (+" + ((paramLong1 - paramLong2) / 1000L) + " s)";
/* 416 */     return str2;
/*     */   }
/*     */   
/*     */   private static int getPercent(long paramLong1, long paramLong2) {
/* 420 */     if (paramLong1 == 0L)
/* 421 */       return 0; 
/* 422 */     if (paramLong1 == paramLong2) {
/* 423 */       return 100;
/*     */     }
/* 425 */     return (int)(1L + 98L * paramLong1 / Math.max(1L, paramLong2));
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
/*     */   public static void compact(String paramString, boolean paramBoolean) {
/* 440 */     String str = paramString + ".tempFile";
/* 441 */     FileUtils.delete(str);
/* 442 */     compact(paramString, str, paramBoolean);
/*     */     try {
/* 444 */       FileUtils.moveAtomicReplace(str, paramString);
/* 445 */     } catch (DbException dbException) {
/* 446 */       String str1 = paramString + ".newFile";
/* 447 */       FileUtils.delete(str1);
/* 448 */       FileUtils.move(str, str1);
/* 449 */       FileUtils.delete(paramString);
/* 450 */       FileUtils.move(str1, paramString);
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
/*     */   public static void compactCleanUp(String paramString) {
/* 463 */     String str1 = paramString + ".tempFile";
/* 464 */     if (FileUtils.exists(str1)) {
/* 465 */       FileUtils.delete(str1);
/*     */     }
/* 467 */     String str2 = paramString + ".newFile";
/* 468 */     if (FileUtils.exists(str2)) {
/* 469 */       if (FileUtils.exists(paramString)) {
/* 470 */         FileUtils.delete(str2);
/*     */       } else {
/* 472 */         FileUtils.move(str2, paramString);
/*     */       } 
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
/*     */   public static void compact(String paramString1, String paramString2, boolean paramBoolean) {
/* 486 */     try (MVStore null = (new MVStore.Builder()).fileName(paramString1).readOnly().open()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 492 */       FileUtils.delete(paramString2);
/*     */       
/* 494 */       MVStore.Builder builder = (new MVStore.Builder()).fileName(paramString2);
/* 495 */       if (paramBoolean) {
/* 496 */         builder.compress();
/*     */       }
/* 498 */       try (MVStore null = builder.open()) {
/* 499 */         compact(mVStore, mVStore1);
/*     */       } 
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
/*     */   public static void compact(MVStore paramMVStore1, MVStore paramMVStore2) {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: invokevirtual getAutoCommitDelay : ()I
/*     */     //   4: istore_2
/*     */     //   5: aload_1
/*     */     //   6: invokevirtual getReuseSpace : ()Z
/*     */     //   9: istore_3
/*     */     //   10: aload_1
/*     */     //   11: iconst_0
/*     */     //   12: invokevirtual setReuseSpace : (Z)V
/*     */     //   15: aload_1
/*     */     //   16: iconst_0
/*     */     //   17: invokevirtual setAutoCommitDelay : (I)V
/*     */     //   20: aload_0
/*     */     //   21: invokevirtual getMetaMap : ()Lorg/h2/mvstore/MVMap;
/*     */     //   24: astore #4
/*     */     //   26: aload_1
/*     */     //   27: invokevirtual getMetaMap : ()Lorg/h2/mvstore/MVMap;
/*     */     //   30: astore #5
/*     */     //   32: aload #4
/*     */     //   34: invokevirtual entrySet : ()Ljava/util/Set;
/*     */     //   37: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   42: astore #6
/*     */     //   44: aload #6
/*     */     //   46: invokeinterface hasNext : ()Z
/*     */     //   51: ifeq -> 122
/*     */     //   54: aload #6
/*     */     //   56: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   61: checkcast java/util/Map$Entry
/*     */     //   64: astore #7
/*     */     //   66: aload #7
/*     */     //   68: invokeinterface getKey : ()Ljava/lang/Object;
/*     */     //   73: checkcast java/lang/String
/*     */     //   76: astore #8
/*     */     //   78: aload #8
/*     */     //   80: ldc 'map.'
/*     */     //   82: invokevirtual startsWith : (Ljava/lang/String;)Z
/*     */     //   85: ifeq -> 91
/*     */     //   88: goto -> 119
/*     */     //   91: aload #8
/*     */     //   93: ldc 'name.'
/*     */     //   95: invokevirtual startsWith : (Ljava/lang/String;)Z
/*     */     //   98: ifeq -> 104
/*     */     //   101: goto -> 119
/*     */     //   104: aload #5
/*     */     //   106: aload #8
/*     */     //   108: aload #7
/*     */     //   110: invokeinterface getValue : ()Ljava/lang/Object;
/*     */     //   115: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   118: pop
/*     */     //   119: goto -> 44
/*     */     //   122: aload_0
/*     */     //   123: invokevirtual getMapNames : ()Ljava/util/Set;
/*     */     //   126: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   131: astore #6
/*     */     //   133: aload #6
/*     */     //   135: invokeinterface hasNext : ()Z
/*     */     //   140: ifeq -> 233
/*     */     //   143: aload #6
/*     */     //   145: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   150: checkcast java/lang/String
/*     */     //   153: astore #7
/*     */     //   155: invokestatic getGenericMapBuilder : ()Lorg/h2/mvstore/MVMap$Builder;
/*     */     //   158: astore #8
/*     */     //   160: aload #7
/*     */     //   162: ldc 'undoLog'
/*     */     //   164: invokevirtual startsWith : (Ljava/lang/String;)Z
/*     */     //   167: ifeq -> 176
/*     */     //   170: aload #8
/*     */     //   172: invokevirtual singleWriter : ()Lorg/h2/mvstore/MVMap$Builder;
/*     */     //   175: pop
/*     */     //   176: aload_0
/*     */     //   177: aload #7
/*     */     //   179: aload #8
/*     */     //   181: invokevirtual openMap : (Ljava/lang/String;Lorg/h2/mvstore/MVMap$MapBuilder;)Lorg/h2/mvstore/MVMap;
/*     */     //   184: astore #9
/*     */     //   186: aload_1
/*     */     //   187: aload #7
/*     */     //   189: aload #8
/*     */     //   191: invokevirtual openMap : (Ljava/lang/String;Lorg/h2/mvstore/MVMap$MapBuilder;)Lorg/h2/mvstore/MVMap;
/*     */     //   194: astore #10
/*     */     //   196: aload #10
/*     */     //   198: aload #9
/*     */     //   200: invokevirtual copyFrom : (Lorg/h2/mvstore/MVMap;)V
/*     */     //   203: aload #5
/*     */     //   205: aload #10
/*     */     //   207: invokevirtual getId : ()I
/*     */     //   210: invokestatic getMapKey : (I)Ljava/lang/String;
/*     */     //   213: aload #4
/*     */     //   215: aload #9
/*     */     //   217: invokevirtual getId : ()I
/*     */     //   220: invokestatic getMapKey : (I)Ljava/lang/String;
/*     */     //   223: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   226: invokevirtual put : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   229: pop
/*     */     //   230: goto -> 133
/*     */     //   233: aload_1
/*     */     //   234: invokevirtual commit : ()J
/*     */     //   237: pop2
/*     */     //   238: aload_1
/*     */     //   239: iload_2
/*     */     //   240: invokevirtual setAutoCommitDelay : (I)V
/*     */     //   243: aload_1
/*     */     //   244: iload_3
/*     */     //   245: invokevirtual setReuseSpace : (Z)V
/*     */     //   248: goto -> 266
/*     */     //   251: astore #11
/*     */     //   253: aload_1
/*     */     //   254: iload_2
/*     */     //   255: invokevirtual setAutoCommitDelay : (I)V
/*     */     //   258: aload_1
/*     */     //   259: iload_3
/*     */     //   260: invokevirtual setReuseSpace : (Z)V
/*     */     //   263: aload #11
/*     */     //   265: athrow
/*     */     //   266: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #511	-> 0
/*     */     //   #512	-> 5
/*     */     //   #514	-> 10
/*     */     //   #515	-> 15
/*     */     //   #516	-> 20
/*     */     //   #517	-> 26
/*     */     //   #518	-> 32
/*     */     //   #519	-> 66
/*     */     //   #520	-> 78
/*     */     //   #522	-> 91
/*     */     //   #525	-> 104
/*     */     //   #527	-> 119
/*     */     //   #534	-> 122
/*     */     //   #535	-> 155
/*     */     //   #542	-> 160
/*     */     //   #543	-> 170
/*     */     //   #545	-> 176
/*     */     //   #546	-> 186
/*     */     //   #547	-> 196
/*     */     //   #548	-> 203
/*     */     //   #549	-> 230
/*     */     //   #552	-> 233
/*     */     //   #554	-> 238
/*     */     //   #555	-> 243
/*     */     //   #556	-> 248
/*     */     //   #554	-> 251
/*     */     //   #555	-> 258
/*     */     //   #556	-> 263
/*     */     //   #557	-> 266
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   10	238	251	finally
/*     */     //   251	253	251	finally
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
/*     */   public static void repair(String paramString) {
/* 565 */     PrintWriter printWriter = new PrintWriter(System.out);
/* 566 */     long l = Long.MAX_VALUE;
/* 567 */     OutputStream outputStream = new OutputStream()
/*     */       {
/*     */         public void write(int param1Int) {}
/*     */       };
/*     */ 
/*     */     
/* 573 */     while (l >= 0L) {
/* 574 */       printWriter.println((l == Long.MAX_VALUE) ? "Trying latest version" : ("Trying version " + l));
/*     */       
/* 576 */       printWriter.flush();
/* 577 */       l = rollback(paramString, l, new PrintWriter(outputStream));
/*     */       try {
/* 579 */         String str = info(paramString + ".temp", new PrintWriter(outputStream));
/* 580 */         if (str == null) {
/* 581 */           FilePath.get(paramString).moveTo(FilePath.get(paramString + ".back"), true);
/* 582 */           FilePath.get(paramString + ".temp").moveTo(FilePath.get(paramString), true);
/* 583 */           printWriter.println("Success");
/*     */           break;
/*     */         } 
/* 586 */         printWriter.println("    ... failed: " + str);
/* 587 */       } catch (Exception exception) {
/* 588 */         printWriter.println("Fail: " + exception.getMessage());
/* 589 */         printWriter.flush();
/*     */       } 
/* 591 */       l--;
/*     */     } 
/* 593 */     printWriter.flush();
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
/*     */   public static long rollback(String paramString, long paramLong, Writer paramWriter) {
/* 606 */     long l = -1L;
/* 607 */     PrintWriter printWriter = new PrintWriter(paramWriter, true);
/* 608 */     if (!FilePath.get(paramString).exists()) {
/* 609 */       printWriter.println("File not found: " + paramString);
/* 610 */       return l;
/*     */     } 
/* 612 */     FileChannel fileChannel1 = null;
/* 613 */     FileChannel fileChannel2 = null;
/* 614 */     char c = 'က';
/*     */     try {
/* 616 */       fileChannel1 = FilePath.get(paramString).open("r");
/* 617 */       FilePath.get(paramString + ".temp").delete();
/* 618 */       fileChannel2 = FilePath.get(paramString + ".temp").open("rw");
/* 619 */       long l1 = fileChannel1.size();
/* 620 */       ByteBuffer byteBuffer1 = ByteBuffer.allocate(4096);
/* 621 */       Chunk chunk = null; long l2;
/* 622 */       for (l2 = 0L; l2 < l1; ) {
/* 623 */         Chunk chunk1; byteBuffer1.rewind();
/* 624 */         DataUtils.readFully(fileChannel1, l2, byteBuffer1);
/* 625 */         byteBuffer1.rewind();
/* 626 */         byte b = byteBuffer1.get();
/* 627 */         if (b == 72) {
/* 628 */           byteBuffer1.rewind();
/* 629 */           fileChannel2.write(byteBuffer1, l2);
/* 630 */           l2 += c;
/*     */           continue;
/*     */         } 
/* 633 */         if (b != 99) {
/* 634 */           l2 += c;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         try {
/* 639 */           chunk1 = Chunk.readChunkHeader(byteBuffer1, l2);
/* 640 */         } catch (MVStoreException mVStoreException) {
/* 641 */           l2 += c;
/*     */           continue;
/*     */         } 
/* 644 */         if (chunk1.len <= 0) {
/*     */           
/* 646 */           l2 += c;
/*     */           continue;
/*     */         } 
/* 649 */         int j = chunk1.len * 4096;
/* 650 */         ByteBuffer byteBuffer = ByteBuffer.allocate(j);
/* 651 */         DataUtils.readFully(fileChannel1, l2, byteBuffer);
/* 652 */         if (chunk1.version > paramLong) {
/*     */           
/* 654 */           l2 += j;
/*     */           continue;
/*     */         } 
/* 657 */         byteBuffer.rewind();
/* 658 */         fileChannel2.write(byteBuffer, l2);
/* 659 */         if (chunk == null || chunk1.version > chunk.version) {
/* 660 */           chunk = chunk1;
/* 661 */           l = chunk1.version;
/*     */         } 
/* 663 */         l2 += j;
/*     */       } 
/* 665 */       int i = chunk.len * 4096;
/* 666 */       ByteBuffer byteBuffer2 = ByteBuffer.allocate(i);
/* 667 */       DataUtils.readFully(fileChannel1, chunk.block * 4096L, byteBuffer2);
/* 668 */       byteBuffer2.rewind();
/* 669 */       fileChannel2.write(byteBuffer2, l1);
/* 670 */     } catch (IOException iOException) {
/* 671 */       printWriter.println("ERROR: " + iOException);
/* 672 */       iOException.printStackTrace(printWriter);
/*     */     } finally {
/* 674 */       if (fileChannel1 != null) {
/*     */         try {
/* 676 */           fileChannel1.close();
/* 677 */         } catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */       
/* 681 */       if (fileChannel2 != null) {
/*     */         try {
/* 683 */           fileChannel2.close();
/* 684 */         } catch (IOException iOException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 689 */     printWriter.flush();
/* 690 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   static MVMap.Builder<Object, Object> getGenericMapBuilder() {
/* 695 */     return (new MVMap.Builder<>())
/* 696 */       .keyType((DataType<? super Object>)GenericDataType.INSTANCE)
/* 697 */       .valueType((DataType<? super Object>)GenericDataType.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class GenericDataType
/*     */     extends BasicDataType<byte[]>
/*     */   {
/* 705 */     static GenericDataType INSTANCE = new GenericDataType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isMemoryEstimationAllowed() {
/* 711 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMemory(byte[] param1ArrayOfbyte) {
/* 716 */       return (param1ArrayOfbyte == null) ? 0 : (param1ArrayOfbyte.length * 8);
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[][] createStorage(int param1Int) {
/* 721 */       return new byte[param1Int][];
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(WriteBuffer param1WriteBuffer, byte[] param1ArrayOfbyte) {
/* 726 */       if (param1ArrayOfbyte != null) {
/* 727 */         param1WriteBuffer.put(param1ArrayOfbyte);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public byte[] read(ByteBuffer param1ByteBuffer) {
/* 733 */       int i = param1ByteBuffer.remaining();
/* 734 */       if (i == 0) {
/* 735 */         return null;
/*     */       }
/* 737 */       byte[] arrayOfByte = new byte[i];
/* 738 */       param1ByteBuffer.get(arrayOfByte);
/* 739 */       return arrayOfByte;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\MVStoreTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */