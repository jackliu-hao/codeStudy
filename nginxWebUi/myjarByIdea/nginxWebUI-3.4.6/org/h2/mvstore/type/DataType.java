package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import java.util.Comparator;
import org.h2.mvstore.WriteBuffer;

public interface DataType<T> extends Comparator<T> {
   int compare(T var1, T var2);

   int binarySearch(T var1, Object var2, int var3, int var4);

   int getMemory(T var1);

   boolean isMemoryEstimationAllowed();

   void write(WriteBuffer var1, T var2);

   void write(WriteBuffer var1, Object var2, int var3);

   T read(ByteBuffer var1);

   void read(ByteBuffer var1, Object var2, int var3);

   T[] createStorage(int var1);
}
