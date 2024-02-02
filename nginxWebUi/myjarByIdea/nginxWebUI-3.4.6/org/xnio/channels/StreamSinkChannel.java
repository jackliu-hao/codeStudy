package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import org.xnio.ChannelListener;

public interface StreamSinkChannel extends WritableByteChannel, GatheringByteChannel, SuspendableWriteChannel {
   long transferFrom(FileChannel var1, long var2, long var4) throws IOException;

   long transferFrom(StreamSourceChannel var1, long var2, ByteBuffer var4) throws IOException;

   ChannelListener.Setter<? extends StreamSinkChannel> getWriteSetter();

   ChannelListener.Setter<? extends StreamSinkChannel> getCloseSetter();

   int writeFinal(ByteBuffer var1) throws IOException;

   long writeFinal(ByteBuffer[] var1, int var2, int var3) throws IOException;

   long writeFinal(ByteBuffer[] var1) throws IOException;
}
