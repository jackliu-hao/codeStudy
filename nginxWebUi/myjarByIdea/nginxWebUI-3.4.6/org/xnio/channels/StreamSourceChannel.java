package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import org.xnio.ChannelListener;

public interface StreamSourceChannel extends ReadableByteChannel, ScatteringByteChannel, SuspendableReadChannel {
   long transferTo(long var1, long var3, FileChannel var5) throws IOException;

   long transferTo(long var1, ByteBuffer var3, StreamSinkChannel var4) throws IOException;

   ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter();

   ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter();
}
