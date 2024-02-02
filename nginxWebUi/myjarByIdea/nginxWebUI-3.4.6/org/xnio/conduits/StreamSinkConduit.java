package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;

public interface StreamSinkConduit extends SinkConduit {
   long transferFrom(FileChannel var1, long var2, long var4) throws IOException;

   long transferFrom(StreamSourceChannel var1, long var2, ByteBuffer var4) throws IOException;

   int write(ByteBuffer var1) throws IOException;

   long write(ByteBuffer[] var1, int var2, int var3) throws IOException;

   int writeFinal(ByteBuffer var1) throws IOException;

   long writeFinal(ByteBuffer[] var1, int var2, int var3) throws IOException;
}
