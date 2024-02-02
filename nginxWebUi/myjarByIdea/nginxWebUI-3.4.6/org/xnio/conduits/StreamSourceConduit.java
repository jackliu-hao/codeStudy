package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public interface StreamSourceConduit extends SourceConduit {
   long transferTo(long var1, long var3, FileChannel var5) throws IOException;

   long transferTo(long var1, ByteBuffer var3, StreamSinkChannel var4) throws IOException;

   int read(ByteBuffer var1) throws IOException;

   long read(ByteBuffer[] var1, int var2, int var3) throws IOException;
}
