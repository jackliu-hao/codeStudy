package org.h2.mvstore.tx;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.value.VersionedValue;

final class Record<K, V> {
   static final Record<?, ?> COMMIT_MARKER = new Record(-1, (Object)null, (VersionedValue)null);
   final int mapId;
   final K key;
   final VersionedValue<V> oldValue;

   Record(int var1, K var2, VersionedValue<V> var3) {
      this.mapId = var1;
      this.key = var2;
      this.oldValue = var3;
   }

   public String toString() {
      return "mapId=" + this.mapId + ", key=" + this.key + ", value=" + this.oldValue;
   }

   static final class Type<K, V> extends BasicDataType<Record<K, V>> {
      private final TransactionStore transactionStore;

      Type(TransactionStore var1) {
         this.transactionStore = var1;
      }

      public int getMemory(Record<K, V> var1) {
         int var2 = 52;
         if (var1.mapId >= 0) {
            MVMap var3 = this.transactionStore.getMap(var1.mapId);
            var2 += var3.getKeyType().getMemory(var1.key) + var3.getValueType().getMemory(var1.oldValue);
         }

         return var2;
      }

      public int compare(Record<K, V> var1, Record<K, V> var2) {
         throw new UnsupportedOperationException();
      }

      public void write(WriteBuffer var1, Record<K, V> var2) {
         var1.putVarInt(var2.mapId);
         if (var2.mapId >= 0) {
            MVMap var3 = this.transactionStore.getMap(var2.mapId);
            var3.getKeyType().write(var1, var2.key);
            VersionedValue var4 = var2.oldValue;
            if (var4 == null) {
               var1.put((byte)0);
            } else {
               var1.put((byte)1);
               var3.getValueType().write(var1, var4);
            }
         }

      }

      public Record<K, V> read(ByteBuffer var1) {
         int var2 = DataUtils.readVarInt(var1);
         if (var2 < 0) {
            return Record.COMMIT_MARKER;
         } else {
            MVMap var3 = this.transactionStore.getMap(var2);
            Object var4 = var3.getKeyType().read(var1);
            VersionedValue var5 = null;
            if (var1.get() == 1) {
               var5 = (VersionedValue)var3.getValueType().read(var1);
            }

            return new Record(var2, var4, var5);
         }
      }

      public Record<K, V>[] createStorage(int var1) {
         return new Record[var1];
      }
   }
}
