package io.undertow.websockets.extensions;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import java.io.IOException;

public class NoopExtensionFunction implements ExtensionFunction {
   public static final ExtensionFunction INSTANCE = new NoopExtensionFunction();

   public boolean hasExtensionOpCode() {
      return false;
   }

   public int writeRsv(int rsv) {
      return 0;
   }

   public PooledByteBuffer transformForWrite(PooledByteBuffer pooledBuffer, StreamSinkFrameChannel channel, boolean lastFrame) throws IOException {
      return pooledBuffer;
   }

   public PooledByteBuffer transformForRead(PooledByteBuffer pooledBuffer, StreamSourceFrameChannel channel, boolean lastFragmentOfFrame) throws IOException {
      return pooledBuffer;
   }

   public void dispose() {
   }
}
