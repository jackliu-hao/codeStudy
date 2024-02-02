package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import org.h2.mvstore.WriteBuffer;

public interface StatefulDataType<D> {
   void save(WriteBuffer var1, MetaType<D> var2);

   Factory<D> getFactory();

   public interface Factory<D> {
      DataType<?> create(ByteBuffer var1, MetaType<D> var2, D var3);
   }
}
