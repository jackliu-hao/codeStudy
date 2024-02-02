package org.h2.mvstore.db;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.h2.engine.Database;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.StreamStore;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.tx.TransactionStore;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.ByteArrayDataType;
import org.h2.mvstore.type.LongDataType;
import org.h2.store.CountingReaderInputStream;
import org.h2.store.LobStorageInterface;
import org.h2.store.RangeInputStream;
import org.h2.util.IOUtils;
import org.h2.value.Value;
import org.h2.value.ValueBlob;
import org.h2.value.ValueClob;
import org.h2.value.ValueLob;
import org.h2.value.ValueNull;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;

public final class LobStorageMap implements LobStorageInterface {
   private static final boolean TRACE = false;
   private final Database database;
   final MVStore mvStore;
   private final AtomicLong nextLobId = new AtomicLong(0L);
   private final MVMap<Long, BlobMeta> lobMap;
   private final MVMap<Long, byte[]> tempLobMap;
   private final MVMap<BlobReference, Value> refMap;
   private final StreamStore streamStore;

   public static MVMap<Long, BlobMeta> openLobMap(TransactionStore var0) {
      return var0.openMap("lobMap", LongDataType.INSTANCE, LobStorageMap.BlobMeta.Type.INSTANCE);
   }

   public static MVMap<Long, byte[]> openLobDataMap(TransactionStore var0) {
      return var0.openMap("lobData", LongDataType.INSTANCE, ByteArrayDataType.INSTANCE);
   }

   public LobStorageMap(Database var1) {
      this.database = var1;
      Store var2 = var1.getStore();
      TransactionStore var3 = var2.getTransactionStore();
      this.mvStore = var2.getMvStore();
      MVStore.TxCounter var4 = this.mvStore.registerVersionUsage();

      try {
         this.lobMap = openLobMap(var3);
         this.tempLobMap = var3.openMap("tempLobMap", LongDataType.INSTANCE, ByteArrayDataType.INSTANCE);
         this.refMap = var3.openMap("lobRef", LobStorageMap.BlobReference.Type.INSTANCE, NullValueDataType.INSTANCE);
         MVMap var5 = openLobDataMap(var3);
         this.streamStore = new StreamStore(var5);
         if (!var1.isReadOnly()) {
            Long var6 = (Long)var5.lastKey();
            if (var6 != null) {
               this.streamStore.setNextKey(var6 + 1L);
            }

            Long var7 = (Long)this.lobMap.lastKey();
            Long var8 = (Long)this.tempLobMap.lastKey();
            long var9 = 1L;
            if (var7 != null) {
               var9 = var7 + 1L;
            }

            if (var8 != null) {
               var9 = Math.max(var9, var8 + 1L);
            }

            this.nextLobId.set(var9);
         }
      } finally {
         this.mvStore.deregisterVersionUsage(var4);
      }

   }

   public ValueBlob createBlob(InputStream var1, long var2) {
      MVStore.TxCounter var4 = this.mvStore.registerVersionUsage();

      ValueBlob var5;
      try {
         if (var2 != -1L && var2 <= (long)this.database.getMaxLengthInplaceLob()) {
            byte[] var15 = new byte[(int)var2];
            int var6 = IOUtils.readFully((InputStream)var1, (byte[])var15, (int)var2);
            if ((long)var6 > var2) {
               throw new IllegalStateException("len > blobLength, " + var6 + " > " + var2);
            }

            if (var6 < var15.length) {
               var15 = Arrays.copyOf(var15, var6);
            }

            ValueBlob var7 = ValueBlob.createSmall(var15);
            return var7;
         }

         if (var2 != -1L) {
            var1 = new RangeInputStream((InputStream)var1, 0L, var2);
         }

         var5 = this.createBlob((InputStream)var1);
      } catch (IllegalStateException var12) {
         throw DbException.get(90007, var12);
      } catch (IOException var13) {
         throw DbException.convertIOException(var13, (String)null);
      } finally {
         this.mvStore.deregisterVersionUsage(var4);
      }

      return var5;
   }

   public ValueClob createClob(Reader var1, long var2) {
      MVStore.TxCounter var4 = this.mvStore.registerVersionUsage();

      ValueClob var8;
      try {
         if (var2 != -1L && var2 * 3L <= (long)this.database.getMaxLengthInplaceLob()) {
            char[] var16 = new char[(int)var2];
            int var17 = IOUtils.readFully(var1, var16, (int)var2);
            if ((long)var17 > var2) {
               throw new IllegalStateException("len > blobLength, " + var17 + " > " + var2);
            }

            byte[] var18 = (new String(var16, 0, var17)).getBytes(StandardCharsets.UTF_8);
            if (var18.length > this.database.getMaxLengthInplaceLob()) {
               throw new IllegalStateException("len > maxinplace, " + var18.length + " > " + this.database.getMaxLengthInplaceLob());
            }

            var8 = ValueClob.createSmall(var18, (long)var17);
            return var8;
         }

         if (var2 < 0L) {
            var2 = Long.MAX_VALUE;
         }

         CountingReaderInputStream var5 = new CountingReaderInputStream(var1, var2);
         ValueBlob var6 = this.createBlob(var5);
         LobData var7 = var6.getLobData();
         var8 = new ValueClob(var7, var6.octetLength(), var5.getLength());
      } catch (IllegalStateException var13) {
         throw DbException.get(90007, var13);
      } catch (IOException var14) {
         throw DbException.convertIOException(var14, (String)null);
      } finally {
         this.mvStore.deregisterVersionUsage(var4);
      }

      return var8;
   }

   private ValueBlob createBlob(InputStream var1) throws IOException {
      byte[] var2;
      try {
         var2 = this.streamStore.put(var1);
      } catch (Exception var10) {
         throw DataUtils.convertToIOException(var10);
      }

      long var3 = this.generateLobId();
      long var5 = this.streamStore.length(var2);
      this.tempLobMap.put(var3, var2);
      BlobReference var8 = new BlobReference(var2, var3);
      this.refMap.put(var8, ValueNull.INSTANCE);
      ValueBlob var9 = new ValueBlob(new LobDataDatabase(this.database, -2, var3), var5);
      return var9;
   }

   private long generateLobId() {
      return this.nextLobId.getAndIncrement();
   }

   public boolean isReadOnly() {
      return this.database.isReadOnly();
   }

   public ValueLob copyLob(ValueLob var1, int var2) {
      MVStore.TxCounter var3 = this.mvStore.registerVersionUsage();

      Object var16;
      try {
         LobDataDatabase var4 = (LobDataDatabase)var1.getLobData();
         int var5 = var1.getValueType();
         long var6 = var4.getLobId();
         long var8 = var1.octetLength();
         byte[] var10;
         if (isTemporaryLob(var4.getTableId())) {
            var10 = (byte[])this.tempLobMap.get(var6);
         } else {
            BlobMeta var11 = (BlobMeta)this.lobMap.get(var6);
            var10 = var11.streamStoreId;
         }

         long var20 = this.generateLobId();
         if (isTemporaryLob(var2)) {
            this.tempLobMap.put(var20, var10);
         } else {
            BlobMeta var13 = new BlobMeta(var10, var2, var5 == 3 ? var1.charLength() : var8, 0L);
            this.lobMap.put(var20, var13);
         }

         BlobReference var21 = new BlobReference(var10, var20);
         this.refMap.put(var21, ValueNull.INSTANCE);
         LobDataDatabase var14 = new LobDataDatabase(this.database, var2, var20);
         Object var15 = var5 == 7 ? new ValueBlob(var14, var8) : new ValueClob(var14, var8, var1.charLength());
         var16 = var15;
      } finally {
         this.mvStore.deregisterVersionUsage(var3);
      }

      return (ValueLob)var16;
   }

   public InputStream getInputStream(long var1, long var3) throws IOException {
      MVStore.TxCounter var5 = this.mvStore.registerVersionUsage();

      LobInputStream var8;
      try {
         byte[] var6 = (byte[])this.tempLobMap.get(var1);
         if (var6 == null) {
            BlobMeta var7 = (BlobMeta)this.lobMap.get(var1);
            var6 = var7.streamStoreId;
         }

         if (var6 == null) {
            throw DbException.get(90039, "" + var1);
         }

         InputStream var12 = this.streamStore.get(var6);
         var8 = new LobInputStream(var12);
      } finally {
         this.mvStore.deregisterVersionUsage(var5);
      }

      return var8;
   }

   public InputStream getInputStream(long var1, int var3, long var4) throws IOException {
      MVStore.TxCounter var6 = this.mvStore.registerVersionUsage();

      LobInputStream var9;
      try {
         byte[] var7;
         if (isTemporaryLob(var3)) {
            var7 = (byte[])this.tempLobMap.get(var1);
         } else {
            BlobMeta var8 = (BlobMeta)this.lobMap.get(var1);
            var7 = var8.streamStoreId;
         }

         if (var7 == null) {
            throw DbException.get(90039, "" + var1);
         }

         InputStream var13 = this.streamStore.get(var7);
         var9 = new LobInputStream(var13);
      } finally {
         this.mvStore.deregisterVersionUsage(var6);
      }

      return var9;
   }

   public void removeAllForTable(int var1) {
      if (!this.mvStore.isClosed()) {
         MVStore.TxCounter var2 = this.mvStore.registerVersionUsage();

         try {
            if (isTemporaryLob(var1)) {
               Iterator var3 = this.tempLobMap.keyIterator(0L);

               while(var3.hasNext()) {
                  long var4 = (Long)var3.next();
                  this.removeLob(var1, var4);
               }

               this.tempLobMap.clear();
            } else {
               ArrayList var10 = new ArrayList();
               Iterator var11 = this.lobMap.entrySet().iterator();

               while(var11.hasNext()) {
                  Map.Entry var5 = (Map.Entry)var11.next();
                  BlobMeta var6 = (BlobMeta)var5.getValue();
                  if (var6.tableId == var1) {
                     var10.add(var5.getKey());
                  }
               }

               var11 = var10.iterator();

               while(var11.hasNext()) {
                  long var12 = (Long)var11.next();
                  this.removeLob(var1, var12);
               }
            }
         } finally {
            this.mvStore.deregisterVersionUsage(var2);
         }

      }
   }

   public void removeLob(ValueLob var1) {
      MVStore.TxCounter var2 = this.mvStore.registerVersionUsage();

      try {
         LobDataDatabase var3 = (LobDataDatabase)var1.getLobData();
         int var4 = var3.getTableId();
         long var5 = var3.getLobId();
         this.removeLob(var4, var5);
      } finally {
         this.mvStore.deregisterVersionUsage(var2);
      }

   }

   private void removeLob(int var1, long var2) {
      byte[] var4;
      if (isTemporaryLob(var1)) {
         var4 = (byte[])this.tempLobMap.remove(var2);
         if (var4 == null) {
            return;
         }
      } else {
         BlobMeta var5 = (BlobMeta)this.lobMap.remove(var2);
         if (var5 == null) {
            return;
         }

         var4 = var5.streamStoreId;
      }

      BlobReference var10 = new BlobReference(var4, var2);
      Value var6 = (Value)this.refMap.remove(var10);

      assert var6 != null;

      var10 = new BlobReference(var4, 0L);
      BlobReference var7 = (BlobReference)this.refMap.ceilingKey(var10);
      boolean var8 = false;
      if (var7 != null) {
         byte[] var9 = var7.streamStoreId;
         if (Arrays.equals(var4, var9)) {
            var8 = true;
         }
      }

      if (!var8) {
         this.streamStore.remove(var4);
      }

   }

   private static boolean isTemporaryLob(int var0) {
      return var0 == -1 || var0 == -2 || var0 == -3;
   }

   private static void trace(String var0) {
      System.out.println("[" + Thread.currentThread().getName() + "] LOB " + var0);
   }

   public static final class BlobMeta {
      public final byte[] streamStoreId;
      final int tableId;
      final long byteCount;
      final long hash;

      public BlobMeta(byte[] var1, int var2, long var3, long var5) {
         this.streamStoreId = var1;
         this.tableId = var2;
         this.byteCount = var3;
         this.hash = var5;
      }

      public static final class Type extends BasicDataType<BlobMeta> {
         public static final Type INSTANCE = new Type();

         private Type() {
         }

         public int getMemory(BlobMeta var1) {
            return var1.streamStoreId.length + 20;
         }

         public void write(WriteBuffer var1, BlobMeta var2) {
            var1.putVarInt(var2.streamStoreId.length);
            var1.put(var2.streamStoreId);
            var1.putVarInt(var2.tableId);
            var1.putVarLong(var2.byteCount);
            var1.putLong(var2.hash);
         }

         public BlobMeta read(ByteBuffer var1) {
            int var2 = DataUtils.readVarInt(var1);
            byte[] var3 = new byte[var2];
            var1.get(var3);
            int var4 = DataUtils.readVarInt(var1);
            long var5 = DataUtils.readVarLong(var1);
            long var7 = var1.getLong();
            return new BlobMeta(var3, var4, var5, var7);
         }

         public BlobMeta[] createStorage(int var1) {
            return new BlobMeta[var1];
         }
      }
   }

   public static final class BlobReference implements Comparable<BlobReference> {
      public final byte[] streamStoreId;
      public final long lobId;

      public BlobReference(byte[] var1, long var2) {
         this.streamStoreId = var1;
         this.lobId = var2;
      }

      public int compareTo(BlobReference var1) {
         int var2 = Integer.compare(this.streamStoreId.length, var1.streamStoreId.length);
         if (var2 == 0) {
            for(int var3 = 0; var2 == 0 && var3 < this.streamStoreId.length; ++var3) {
               var2 = Byte.compare(this.streamStoreId[var3], var1.streamStoreId[var3]);
            }

            if (var2 == 0) {
               var2 = Long.compare(this.lobId, var1.lobId);
            }
         }

         return var2;
      }

      public static final class Type extends BasicDataType<BlobReference> {
         public static final Type INSTANCE = new Type();

         private Type() {
         }

         public int getMemory(BlobReference var1) {
            return var1.streamStoreId.length + 8;
         }

         public int compare(BlobReference var1, BlobReference var2) {
            return var1 == var2 ? 0 : (var1 == null ? 1 : (var2 == null ? -1 : var1.compareTo(var2)));
         }

         public void write(WriteBuffer var1, BlobReference var2) {
            var1.putVarInt(var2.streamStoreId.length);
            var1.put(var2.streamStoreId);
            var1.putVarLong(var2.lobId);
         }

         public BlobReference read(ByteBuffer var1) {
            int var2 = DataUtils.readVarInt(var1);
            byte[] var3 = new byte[var2];
            var1.get(var3);
            long var4 = DataUtils.readVarLong(var1);
            return new BlobReference(var3, var4);
         }

         public BlobReference[] createStorage(int var1) {
            return new BlobReference[var1];
         }
      }
   }

   private final class LobInputStream extends FilterInputStream {
      public LobInputStream(InputStream var2) {
         super(var2);
      }

      public int read(byte[] var1, int var2, int var3) throws IOException {
         MVStore.TxCounter var4 = LobStorageMap.this.mvStore.registerVersionUsage();

         int var5;
         try {
            var5 = super.read(var1, var2, var3);
         } finally {
            LobStorageMap.this.mvStore.deregisterVersionUsage(var4);
         }

         return var5;
      }

      public int read() throws IOException {
         MVStore.TxCounter var1 = LobStorageMap.this.mvStore.registerVersionUsage();

         int var2;
         try {
            var2 = super.read();
         } finally {
            LobStorageMap.this.mvStore.deregisterVersionUsage(var1);
         }

         return var2;
      }
   }
}
