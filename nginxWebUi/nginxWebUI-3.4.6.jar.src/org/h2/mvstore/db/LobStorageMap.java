/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.MVStore;
/*     */ import org.h2.mvstore.StreamStore;
/*     */ import org.h2.mvstore.WriteBuffer;
/*     */ import org.h2.mvstore.tx.TransactionStore;
/*     */ import org.h2.mvstore.type.BasicDataType;
/*     */ import org.h2.mvstore.type.ByteArrayDataType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.LongDataType;
/*     */ import org.h2.store.CountingReaderInputStream;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.LobStorageInterface;
/*     */ import org.h2.store.RangeInputStream;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBlob;
/*     */ import org.h2.value.ValueClob;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.lob.LobData;
/*     */ import org.h2.value.lob.LobDataDatabase;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LobStorageMap
/*     */   implements LobStorageInterface
/*     */ {
/*     */   private static final boolean TRACE = false;
/*     */   private final Database database;
/*     */   final MVStore mvStore;
/*  55 */   private final AtomicLong nextLobId = new AtomicLong(0L);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final MVMap<Long, BlobMeta> lobMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final MVMap<Long, byte[]> tempLobMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final MVMap<BlobReference, Value> refMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final StreamStore streamStore;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MVMap<Long, BlobMeta> openLobMap(TransactionStore paramTransactionStore) {
/*  88 */     return paramTransactionStore.openMap("lobMap", (DataType)LongDataType.INSTANCE, (DataType)BlobMeta.Type.INSTANCE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static MVMap<Long, byte[]> openLobDataMap(TransactionStore paramTransactionStore) {
/*  97 */     return paramTransactionStore.openMap("lobData", (DataType)LongDataType.INSTANCE, (DataType)ByteArrayDataType.INSTANCE);
/*     */   }
/*     */   
/*     */   public LobStorageMap(Database paramDatabase) {
/* 101 */     this.database = paramDatabase;
/* 102 */     Store store = paramDatabase.getStore();
/* 103 */     TransactionStore transactionStore = store.getTransactionStore();
/* 104 */     this.mvStore = store.getMvStore();
/* 105 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage();
/*     */     try {
/* 107 */       this.lobMap = openLobMap(transactionStore);
/* 108 */       this.tempLobMap = transactionStore.openMap("tempLobMap", (DataType)LongDataType.INSTANCE, (DataType)ByteArrayDataType.INSTANCE);
/* 109 */       this.refMap = transactionStore.openMap("lobRef", (DataType)BlobReference.Type.INSTANCE, NullValueDataType.INSTANCE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 115 */       MVMap<Long, byte[]> mVMap = openLobDataMap(transactionStore);
/* 116 */       this.streamStore = new StreamStore((Map)mVMap);
/*     */       
/* 118 */       if (!paramDatabase.isReadOnly()) {
/*     */         
/* 120 */         Long long_1 = (Long)mVMap.lastKey();
/* 121 */         if (long_1 != null) {
/* 122 */           this.streamStore.setNextKey(long_1.longValue() + 1L);
/*     */         }
/*     */         
/* 125 */         Long long_2 = (Long)this.lobMap.lastKey();
/* 126 */         Long long_3 = (Long)this.tempLobMap.lastKey();
/* 127 */         long l = 1L;
/* 128 */         if (long_2 != null) {
/* 129 */           l = long_2.longValue() + 1L;
/*     */         }
/* 131 */         if (long_3 != null) {
/* 132 */           l = Math.max(l, long_3.longValue() + 1L);
/*     */         }
/* 134 */         this.nextLobId.set(l);
/*     */       } 
/*     */     } finally {
/* 137 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueBlob createBlob(InputStream paramInputStream, long paramLong) {
/* 143 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage(); try {
/*     */       RangeInputStream rangeInputStream;
/* 145 */       if (paramLong != -1L && paramLong <= this.database
/* 146 */         .getMaxLengthInplaceLob()) {
/* 147 */         byte[] arrayOfByte = new byte[(int)paramLong];
/* 148 */         int i = IOUtils.readFully(paramInputStream, arrayOfByte, (int)paramLong);
/* 149 */         if (i > paramLong) {
/* 150 */           throw new IllegalStateException("len > blobLength, " + i + " > " + paramLong);
/*     */         }
/*     */         
/* 153 */         if (i < arrayOfByte.length) {
/* 154 */           arrayOfByte = Arrays.copyOf(arrayOfByte, i);
/*     */         }
/* 156 */         return ValueBlob.createSmall(arrayOfByte);
/*     */       } 
/* 158 */       if (paramLong != -1L) {
/* 159 */         rangeInputStream = new RangeInputStream(paramInputStream, 0L, paramLong);
/*     */       }
/* 161 */       return createBlob((InputStream)rangeInputStream);
/* 162 */     } catch (IllegalStateException illegalStateException) {
/* 163 */       throw DbException.get(90007, illegalStateException, new String[0]);
/* 164 */     } catch (IOException iOException) {
/* 165 */       throw DbException.convertIOException(iOException, null);
/*     */     } finally {
/* 167 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueClob createClob(Reader paramReader, long paramLong) {
/* 173 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage();
/*     */     
/*     */     try {
/* 176 */       if (paramLong != -1L && paramLong * 3L <= this.database
/* 177 */         .getMaxLengthInplaceLob()) {
/* 178 */         char[] arrayOfChar = new char[(int)paramLong];
/* 179 */         int i = IOUtils.readFully(paramReader, arrayOfChar, (int)paramLong);
/* 180 */         if (i > paramLong) {
/* 181 */           throw new IllegalStateException("len > blobLength, " + i + " > " + paramLong);
/*     */         }
/*     */ 
/*     */         
/* 185 */         byte[] arrayOfByte = (new String(arrayOfChar, 0, i)).getBytes(StandardCharsets.UTF_8);
/* 186 */         if (arrayOfByte.length > this.database.getMaxLengthInplaceLob()) {
/* 187 */           throw new IllegalStateException("len > maxinplace, " + arrayOfByte.length + " > " + this.database
/*     */               
/* 189 */               .getMaxLengthInplaceLob());
/*     */         }
/* 191 */         return ValueClob.createSmall(arrayOfByte, i);
/*     */       } 
/* 193 */       if (paramLong < 0L) {
/* 194 */         paramLong = Long.MAX_VALUE;
/*     */       }
/* 196 */       CountingReaderInputStream countingReaderInputStream = new CountingReaderInputStream(paramReader, paramLong);
/* 197 */       ValueBlob valueBlob = createBlob((InputStream)countingReaderInputStream);
/* 198 */       LobData lobData = valueBlob.getLobData();
/* 199 */       return new ValueClob(lobData, valueBlob.octetLength(), countingReaderInputStream.getLength());
/* 200 */     } catch (IllegalStateException illegalStateException) {
/* 201 */       throw DbException.get(90007, illegalStateException, new String[0]);
/* 202 */     } catch (IOException iOException) {
/* 203 */       throw DbException.convertIOException(iOException, null);
/*     */     } finally {
/* 205 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */   
/*     */   private ValueBlob createBlob(InputStream paramInputStream) throws IOException {
/*     */     byte[] arrayOfByte;
/*     */     try {
/* 212 */       arrayOfByte = this.streamStore.put(paramInputStream);
/* 213 */     } catch (Exception exception) {
/* 214 */       throw DataUtils.convertToIOException(exception);
/*     */     } 
/* 216 */     long l1 = generateLobId();
/* 217 */     long l2 = this.streamStore.length(arrayOfByte);
/*     */     
/* 219 */     this.tempLobMap.put(Long.valueOf(l1), arrayOfByte);
/* 220 */     BlobReference blobReference = new BlobReference(arrayOfByte, l1);
/* 221 */     this.refMap.put(blobReference, ValueNull.INSTANCE);
/* 222 */     return new ValueBlob((LobData)new LobDataDatabase((DataHandler)this.database, -2, l1), l2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long generateLobId() {
/* 230 */     return this.nextLobId.getAndIncrement();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 235 */     return this.database.isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public ValueLob copyLob(ValueLob paramValueLob, int paramInt) {
/* 240 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage(); try {
/*     */       byte[] arrayOfByte;
/* 242 */       LobDataDatabase lobDataDatabase1 = (LobDataDatabase)paramValueLob.getLobData();
/* 243 */       int i = paramValueLob.getValueType();
/* 244 */       long l1 = lobDataDatabase1.getLobId();
/* 245 */       long l2 = paramValueLob.octetLength();
/*     */ 
/*     */       
/* 248 */       if (isTemporaryLob(lobDataDatabase1.getTableId())) {
/* 249 */         arrayOfByte = (byte[])this.tempLobMap.get(Long.valueOf(l1));
/*     */       } else {
/* 251 */         BlobMeta blobMeta = (BlobMeta)this.lobMap.get(Long.valueOf(l1));
/* 252 */         arrayOfByte = blobMeta.streamStoreId;
/*     */       } 
/*     */       
/* 255 */       long l3 = generateLobId();
/* 256 */       if (isTemporaryLob(paramInt)) {
/* 257 */         this.tempLobMap.put(Long.valueOf(l3), arrayOfByte);
/*     */       } else {
/*     */         
/* 260 */         BlobMeta blobMeta = new BlobMeta(arrayOfByte, paramInt, (i == 3) ? paramValueLob.charLength() : l2, 0L);
/* 261 */         this.lobMap.put(Long.valueOf(l3), blobMeta);
/*     */       } 
/* 263 */       BlobReference blobReference = new BlobReference(arrayOfByte, l3);
/* 264 */       this.refMap.put(blobReference, ValueNull.INSTANCE);
/* 265 */       LobDataDatabase lobDataDatabase2 = new LobDataDatabase((DataHandler)this.database, paramInt, l3);
/*     */       
/* 267 */       ValueLob valueLob = (ValueLob)((i == 7) ? new ValueBlob((LobData)lobDataDatabase2, l2) : new ValueClob((LobData)lobDataDatabase2, l2, paramValueLob.charLength()));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 272 */       return valueLob;
/*     */     } finally {
/* 274 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream(long paramLong1, long paramLong2) throws IOException {
/* 281 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage();
/*     */     try {
/* 283 */       byte[] arrayOfByte = (byte[])this.tempLobMap.get(Long.valueOf(paramLong1));
/* 284 */       if (arrayOfByte == null) {
/* 285 */         BlobMeta blobMeta = (BlobMeta)this.lobMap.get(Long.valueOf(paramLong1));
/* 286 */         arrayOfByte = blobMeta.streamStoreId;
/*     */       } 
/* 288 */       if (arrayOfByte == null) {
/* 289 */         throw DbException.get(90039, "" + paramLong1);
/*     */       }
/* 291 */       InputStream inputStream = this.streamStore.get(arrayOfByte);
/* 292 */       return new LobInputStream(inputStream);
/*     */     } finally {
/* 294 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream(long paramLong1, int paramInt, long paramLong2) throws IOException {
/* 301 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage();
/*     */     try {
/*     */       byte[] arrayOfByte;
/* 304 */       if (isTemporaryLob(paramInt)) {
/* 305 */         arrayOfByte = (byte[])this.tempLobMap.get(Long.valueOf(paramLong1));
/*     */       } else {
/* 307 */         BlobMeta blobMeta = (BlobMeta)this.lobMap.get(Long.valueOf(paramLong1));
/* 308 */         arrayOfByte = blobMeta.streamStoreId;
/*     */       } 
/* 310 */       if (arrayOfByte == null) {
/* 311 */         throw DbException.get(90039, "" + paramLong1);
/*     */       }
/* 313 */       InputStream inputStream = this.streamStore.get(arrayOfByte);
/* 314 */       return new LobInputStream(inputStream);
/*     */     } finally {
/* 316 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */   
/*     */   private final class LobInputStream
/*     */     extends FilterInputStream {
/*     */     public LobInputStream(InputStream param1InputStream) {
/* 323 */       super(param1InputStream);
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
/* 328 */       MVStore.TxCounter txCounter = LobStorageMap.this.mvStore.registerVersionUsage();
/*     */       try {
/* 330 */         return super.read(param1ArrayOfbyte, param1Int1, param1Int2);
/*     */       } finally {
/* 332 */         LobStorageMap.this.mvStore.deregisterVersionUsage(txCounter);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int read() throws IOException {
/* 338 */       MVStore.TxCounter txCounter = LobStorageMap.this.mvStore.registerVersionUsage();
/*     */       try {
/* 340 */         return super.read();
/*     */       } finally {
/* 342 */         LobStorageMap.this.mvStore.deregisterVersionUsage(txCounter);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeAllForTable(int paramInt) {
/* 349 */     if (this.mvStore.isClosed()) {
/*     */       return;
/*     */     }
/* 352 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage();
/*     */     try {
/* 354 */       if (isTemporaryLob(paramInt)) {
/* 355 */         Iterator<Long> iterator = this.tempLobMap.keyIterator(Long.valueOf(0L));
/* 356 */         while (iterator.hasNext()) {
/* 357 */           long l = ((Long)iterator.next()).longValue();
/* 358 */           removeLob(paramInt, l);
/*     */         } 
/* 360 */         this.tempLobMap.clear();
/*     */       } else {
/* 362 */         ArrayList arrayList = new ArrayList();
/*     */ 
/*     */ 
/*     */         
/* 366 */         for (Map.Entry entry : this.lobMap.entrySet()) {
/* 367 */           BlobMeta blobMeta = (BlobMeta)entry.getValue();
/* 368 */           if (blobMeta.tableId == paramInt) {
/* 369 */             arrayList.add(entry.getKey());
/*     */           }
/*     */         } 
/* 372 */         for (Iterator<Long> iterator = arrayList.iterator(); iterator.hasNext(); ) { long l = ((Long)iterator.next()).longValue();
/* 373 */           removeLob(paramInt, l); }
/*     */       
/*     */       } 
/*     */     } finally {
/* 377 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeLob(ValueLob paramValueLob) {
/* 383 */     MVStore.TxCounter txCounter = this.mvStore.registerVersionUsage();
/*     */     try {
/* 385 */       LobDataDatabase lobDataDatabase = (LobDataDatabase)paramValueLob.getLobData();
/* 386 */       int i = lobDataDatabase.getTableId();
/* 387 */       long l = lobDataDatabase.getLobId();
/* 388 */       removeLob(i, l);
/*     */     } finally {
/* 390 */       this.mvStore.deregisterVersionUsage(txCounter);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void removeLob(int paramInt, long paramLong) {
/*     */     byte[] arrayOfByte;
/* 399 */     if (isTemporaryLob(paramInt)) {
/* 400 */       arrayOfByte = (byte[])this.tempLobMap.remove(Long.valueOf(paramLong));
/* 401 */       if (arrayOfByte == null) {
/*     */         return;
/*     */       }
/*     */     } else {
/*     */       
/* 406 */       BlobMeta blobMeta = (BlobMeta)this.lobMap.remove(Long.valueOf(paramLong));
/* 407 */       if (blobMeta == null) {
/*     */         return;
/*     */       }
/*     */       
/* 411 */       arrayOfByte = blobMeta.streamStoreId;
/*     */     } 
/* 413 */     BlobReference blobReference1 = new BlobReference(arrayOfByte, paramLong);
/* 414 */     Value value = (Value)this.refMap.remove(blobReference1);
/* 415 */     assert value != null;
/*     */     
/* 417 */     blobReference1 = new BlobReference(arrayOfByte, 0L);
/* 418 */     BlobReference blobReference2 = (BlobReference)this.refMap.ceilingKey(blobReference1);
/* 419 */     boolean bool = false;
/* 420 */     if (blobReference2 != null) {
/* 421 */       byte[] arrayOfByte1 = blobReference2.streamStoreId;
/* 422 */       if (Arrays.equals(arrayOfByte, arrayOfByte1))
/*     */       {
/*     */ 
/*     */         
/* 426 */         bool = true;
/*     */       }
/*     */     } 
/* 429 */     if (!bool)
/*     */     {
/*     */ 
/*     */       
/* 433 */       this.streamStore.remove(arrayOfByte);
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean isTemporaryLob(int paramInt) {
/* 438 */     return (paramInt == -1 || paramInt == -2 || paramInt == -3);
/*     */   }
/*     */ 
/*     */   
/*     */   private static void trace(String paramString) {
/* 443 */     System.out.println("[" + Thread.currentThread().getName() + "] LOB " + paramString);
/*     */   }
/*     */   
/*     */   public static final class BlobReference
/*     */     implements Comparable<BlobReference>
/*     */   {
/*     */     public final byte[] streamStoreId;
/*     */     public final long lobId;
/*     */     
/*     */     public BlobReference(byte[] param1ArrayOfbyte, long param1Long) {
/* 453 */       this.streamStoreId = param1ArrayOfbyte;
/* 454 */       this.lobId = param1Long;
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(BlobReference param1BlobReference) {
/* 459 */       int i = Integer.compare(this.streamStoreId.length, param1BlobReference.streamStoreId.length);
/* 460 */       if (i == 0) {
/* 461 */         for (byte b = 0; i == 0 && b < this.streamStoreId.length; b++) {
/* 462 */           i = Byte.compare(this.streamStoreId[b], param1BlobReference.streamStoreId[b]);
/*     */         }
/* 464 */         if (i == 0) {
/* 465 */           i = Long.compare(this.lobId, param1BlobReference.lobId);
/*     */         }
/*     */       } 
/* 468 */       return i;
/*     */     }
/*     */     
/*     */     public static final class Type extends BasicDataType<BlobReference> {
/* 472 */       public static final Type INSTANCE = new Type();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public int getMemory(LobStorageMap.BlobReference param2BlobReference) {
/* 478 */         return param2BlobReference.streamStoreId.length + 8;
/*     */       }
/*     */ 
/*     */       
/*     */       public int compare(LobStorageMap.BlobReference param2BlobReference1, LobStorageMap.BlobReference param2BlobReference2) {
/* 483 */         return (param2BlobReference1 == param2BlobReference2) ? 0 : ((param2BlobReference1 == null) ? 1 : ((param2BlobReference2 == null) ? -1 : param2BlobReference1.compareTo(param2BlobReference2)));
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(WriteBuffer param2WriteBuffer, LobStorageMap.BlobReference param2BlobReference) {
/* 488 */         param2WriteBuffer.putVarInt(param2BlobReference.streamStoreId.length);
/* 489 */         param2WriteBuffer.put(param2BlobReference.streamStoreId);
/* 490 */         param2WriteBuffer.putVarLong(param2BlobReference.lobId);
/*     */       }
/*     */ 
/*     */       
/*     */       public LobStorageMap.BlobReference read(ByteBuffer param2ByteBuffer) {
/* 495 */         int i = DataUtils.readVarInt(param2ByteBuffer);
/* 496 */         byte[] arrayOfByte = new byte[i];
/* 497 */         param2ByteBuffer.get(arrayOfByte);
/* 498 */         long l = DataUtils.readVarLong(param2ByteBuffer);
/* 499 */         return new LobStorageMap.BlobReference(arrayOfByte, l);
/*     */       }
/*     */       
/*     */       public LobStorageMap.BlobReference[] createStorage(int param2Int)
/*     */       {
/* 504 */         return new LobStorageMap.BlobReference[param2Int]; } } } public static final class Type extends BasicDataType<BlobReference> { public static final Type INSTANCE = new Type(); public LobStorageMap.BlobReference[] createStorage(int param1Int) { return new LobStorageMap.BlobReference[param1Int]; }
/*     */      public int getMemory(LobStorageMap.BlobReference param1BlobReference) {
/*     */       return param1BlobReference.streamStoreId.length + 8;
/*     */     } public int compare(LobStorageMap.BlobReference param1BlobReference1, LobStorageMap.BlobReference param1BlobReference2) {
/*     */       return (param1BlobReference1 == param1BlobReference2) ? 0 : ((param1BlobReference1 == null) ? 1 : ((param1BlobReference2 == null) ? -1 : param1BlobReference1.compareTo(param1BlobReference2)));
/*     */     } public void write(WriteBuffer param1WriteBuffer, LobStorageMap.BlobReference param1BlobReference) {
/*     */       param1WriteBuffer.putVarInt(param1BlobReference.streamStoreId.length);
/*     */       param1WriteBuffer.put(param1BlobReference.streamStoreId);
/*     */       param1WriteBuffer.putVarLong(param1BlobReference.lobId);
/*     */     } public LobStorageMap.BlobReference read(ByteBuffer param1ByteBuffer) {
/*     */       int i = DataUtils.readVarInt(param1ByteBuffer);
/*     */       byte[] arrayOfByte = new byte[i];
/*     */       param1ByteBuffer.get(arrayOfByte);
/*     */       long l = DataUtils.readVarLong(param1ByteBuffer);
/*     */       return new LobStorageMap.BlobReference(arrayOfByte, l);
/*     */     } } public static final class BlobMeta { public final byte[] streamStoreId; final int tableId; final long byteCount; final long hash; public BlobMeta(byte[] param1ArrayOfbyte, int param1Int, long param1Long1, long param1Long2) {
/* 520 */       this.streamStoreId = param1ArrayOfbyte;
/* 521 */       this.tableId = param1Int;
/* 522 */       this.byteCount = param1Long1;
/* 523 */       this.hash = param1Long2;
/*     */     }
/*     */     
/*     */     public static final class Type extends BasicDataType<BlobMeta> {
/* 527 */       public static final Type INSTANCE = new Type();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public int getMemory(LobStorageMap.BlobMeta param2BlobMeta) {
/* 534 */         return param2BlobMeta.streamStoreId.length + 20;
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(WriteBuffer param2WriteBuffer, LobStorageMap.BlobMeta param2BlobMeta) {
/* 539 */         param2WriteBuffer.putVarInt(param2BlobMeta.streamStoreId.length);
/* 540 */         param2WriteBuffer.put(param2BlobMeta.streamStoreId);
/* 541 */         param2WriteBuffer.putVarInt(param2BlobMeta.tableId);
/* 542 */         param2WriteBuffer.putVarLong(param2BlobMeta.byteCount);
/* 543 */         param2WriteBuffer.putLong(param2BlobMeta.hash);
/*     */       }
/*     */ 
/*     */       
/*     */       public LobStorageMap.BlobMeta read(ByteBuffer param2ByteBuffer) {
/* 548 */         int i = DataUtils.readVarInt(param2ByteBuffer);
/* 549 */         byte[] arrayOfByte = new byte[i];
/* 550 */         param2ByteBuffer.get(arrayOfByte);
/* 551 */         int j = DataUtils.readVarInt(param2ByteBuffer);
/* 552 */         long l1 = DataUtils.readVarLong(param2ByteBuffer);
/* 553 */         long l2 = param2ByteBuffer.getLong();
/* 554 */         return new LobStorageMap.BlobMeta(arrayOfByte, j, l1, l2);
/*     */       }
/*     */       
/*     */       public LobStorageMap.BlobMeta[] createStorage(int param2Int)
/*     */       {
/* 559 */         return new LobStorageMap.BlobMeta[param2Int]; } } } public static final class Type extends BasicDataType<BlobMeta> { public static final Type INSTANCE = new Type(); public int getMemory(LobStorageMap.BlobMeta param1BlobMeta) { return param1BlobMeta.streamStoreId.length + 20; } public LobStorageMap.BlobMeta[] createStorage(int param1Int) { return new LobStorageMap.BlobMeta[param1Int]; }
/*     */ 
/*     */     
/*     */     public void write(WriteBuffer param1WriteBuffer, LobStorageMap.BlobMeta param1BlobMeta) {
/*     */       param1WriteBuffer.putVarInt(param1BlobMeta.streamStoreId.length);
/*     */       param1WriteBuffer.put(param1BlobMeta.streamStoreId);
/*     */       param1WriteBuffer.putVarInt(param1BlobMeta.tableId);
/*     */       param1WriteBuffer.putVarLong(param1BlobMeta.byteCount);
/*     */       param1WriteBuffer.putLong(param1BlobMeta.hash);
/*     */     }
/*     */     
/*     */     public LobStorageMap.BlobMeta read(ByteBuffer param1ByteBuffer) {
/*     */       int i = DataUtils.readVarInt(param1ByteBuffer);
/*     */       byte[] arrayOfByte = new byte[i];
/*     */       param1ByteBuffer.get(arrayOfByte);
/*     */       int j = DataUtils.readVarInt(param1ByteBuffer);
/*     */       long l1 = DataUtils.readVarLong(param1ByteBuffer);
/*     */       long l2 = param1ByteBuffer.getLong();
/*     */       return new LobStorageMap.BlobMeta(arrayOfByte, j, l1, l2);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\LobStorageMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */