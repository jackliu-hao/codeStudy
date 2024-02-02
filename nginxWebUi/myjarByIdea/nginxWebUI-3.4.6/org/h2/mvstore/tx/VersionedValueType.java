package org.h2.mvstore.tx;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;
import org.h2.mvstore.type.DataType;
import org.h2.mvstore.type.MetaType;
import org.h2.mvstore.type.StatefulDataType;
import org.h2.value.VersionedValue;

public class VersionedValueType<T, D> extends BasicDataType<VersionedValue<T>> implements StatefulDataType<D> {
   private final DataType<T> valueType;
   private final Factory<D> factory = new Factory();

   public VersionedValueType(DataType<T> var1) {
      this.valueType = var1;
   }

   public VersionedValue<T>[] createStorage(int var1) {
      return new VersionedValue[var1];
   }

   public int getMemory(VersionedValue<T> var1) {
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 48 + this.getValMemory(var1.getCurrentValue());
         if (var1.getOperationId() != 0L) {
            var2 += this.getValMemory(var1.getCommittedValue());
         }

         return var2;
      }
   }

   private int getValMemory(T var1) {
      return var1 == null ? 0 : this.valueType.getMemory(var1);
   }

   public void read(ByteBuffer var1, Object var2, int var3) {
      int var4;
      if (var1.get() == 0) {
         for(var4 = 0; var4 < var3; ++var4) {
            ((VersionedValue[])this.cast(var2))[var4] = VersionedValueCommitted.getInstance(this.valueType.read(var1));
         }
      } else {
         for(var4 = 0; var4 < var3; ++var4) {
            ((VersionedValue[])this.cast(var2))[var4] = this.read(var1);
         }
      }

   }

   public VersionedValue<T> read(ByteBuffer var1) {
      long var2 = DataUtils.readVarLong(var1);
      if (var2 == 0L) {
         return VersionedValueCommitted.getInstance(this.valueType.read(var1));
      } else {
         byte var4 = var1.get();
         Object var5 = (var4 & 1) != 0 ? this.valueType.read(var1) : null;
         Object var6 = (var4 & 2) != 0 ? this.valueType.read(var1) : null;
         return VersionedValueUncommitted.getInstance(var2, var5, var6);
      }
   }

   public void write(WriteBuffer var1, Object var2, int var3) {
      boolean var4 = true;

      int var5;
      VersionedValue var6;
      for(var5 = 0; var5 < var3; ++var5) {
         var6 = ((VersionedValue[])this.cast(var2))[var5];
         if (var6.getOperationId() != 0L || var6.getCurrentValue() == null) {
            var4 = false;
         }
      }

      if (var4) {
         var1.put((byte)0);

         for(var5 = 0; var5 < var3; ++var5) {
            var6 = ((VersionedValue[])this.cast(var2))[var5];
            this.valueType.write(var1, var6.getCurrentValue());
         }
      } else {
         var1.put((byte)1);

         for(var5 = 0; var5 < var3; ++var5) {
            this.write(var1, ((VersionedValue[])this.cast(var2))[var5]);
         }
      }

   }

   public void write(WriteBuffer var1, VersionedValue<T> var2) {
      long var3 = var2.getOperationId();
      var1.putVarLong(var3);
      if (var3 == 0L) {
         this.valueType.write(var1, var2.getCurrentValue());
      } else {
         Object var5 = var2.getCommittedValue();
         int var6 = (var2.getCurrentValue() == null ? 0 : 1) | (var5 == null ? 0 : 2);
         var1.put((byte)var6);
         if (var2.getCurrentValue() != null) {
            this.valueType.write(var1, var2.getCurrentValue());
         }

         if (var5 != null) {
            this.valueType.write(var1, var5);
         }
      }

   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof VersionedValueType)) {
         return false;
      } else {
         VersionedValueType var2 = (VersionedValueType)var1;
         return this.valueType.equals(var2.valueType);
      }
   }

   public int hashCode() {
      return super.hashCode() ^ this.valueType.hashCode();
   }

   public void save(WriteBuffer var1, MetaType<D> var2) {
      var2.write(var1, this.valueType);
   }

   public int compare(VersionedValue<T> var1, VersionedValue<T> var2) {
      return this.valueType.compare(var1.getCurrentValue(), var2.getCurrentValue());
   }

   public Factory<D> getFactory() {
      return this.factory;
   }

   public static final class Factory<D> implements StatefulDataType.Factory<D> {
      public DataType<?> create(ByteBuffer var1, MetaType<D> var2, D var3) {
         DataType var4 = var2.read(var1);
         return new VersionedValueType(var4);
      }
   }
}
