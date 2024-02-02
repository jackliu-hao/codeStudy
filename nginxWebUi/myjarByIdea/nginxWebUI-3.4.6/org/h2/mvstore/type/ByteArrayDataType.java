package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

public final class ByteArrayDataType extends BasicDataType<byte[]> {
   public static final ByteArrayDataType INSTANCE = new ByteArrayDataType();

   private ByteArrayDataType() {
   }

   public int getMemory(byte[] var1) {
      return var1.length;
   }

   public void write(WriteBuffer var1, byte[] var2) {
      var1.putVarInt(var2.length);
      var1.put(var2);
   }

   public byte[] read(ByteBuffer var1) {
      int var2 = DataUtils.readVarInt(var1);
      byte[] var3 = new byte[var2];
      var1.get(var3);
      return var3;
   }

   public byte[][] createStorage(int var1) {
      return new byte[var1][];
   }
}
