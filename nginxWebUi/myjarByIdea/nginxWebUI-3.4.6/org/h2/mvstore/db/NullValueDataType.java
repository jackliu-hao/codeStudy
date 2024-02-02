package org.h2.mvstore.db;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.DataType;
import org.h2.value.Value;
import org.h2.value.ValueNull;

public final class NullValueDataType implements DataType<Value> {
   public static final NullValueDataType INSTANCE = new NullValueDataType();

   private NullValueDataType() {
   }

   public int compare(Value var1, Value var2) {
      return 0;
   }

   public int binarySearch(Value var1, Object var2, int var3, int var4) {
      return 0;
   }

   public int getMemory(Value var1) {
      return 0;
   }

   public boolean isMemoryEstimationAllowed() {
      return true;
   }

   public void write(WriteBuffer var1, Value var2) {
   }

   public void write(WriteBuffer var1, Object var2, int var3) {
   }

   public Value read(ByteBuffer var1) {
      return ValueNull.INSTANCE;
   }

   public void read(ByteBuffer var1, Object var2, int var3) {
      Arrays.fill((Value[])((Value[])var2), 0, var3, ValueNull.INSTANCE);
   }

   public Value[] createStorage(int var1) {
      return new Value[var1];
   }
}
