package io.undertow.websockets.extensions;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import java.io.IOException;

public interface ExtensionFunction {
   int RSV1 = 4;
   int RSV2 = 2;
   int RSV3 = 1;

   boolean hasExtensionOpCode();

   int writeRsv(int var1);

   PooledByteBuffer transformForWrite(PooledByteBuffer var1, StreamSinkFrameChannel var2, boolean var3) throws IOException;

   PooledByteBuffer transformForRead(PooledByteBuffer var1, StreamSourceFrameChannel var2, boolean var3) throws IOException;

   void dispose();
}
