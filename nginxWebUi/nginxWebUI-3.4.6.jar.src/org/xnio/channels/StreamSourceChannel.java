package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.ScatteringByteChannel;
import org.xnio.ChannelListener;

public interface StreamSourceChannel extends ReadableByteChannel, ScatteringByteChannel, SuspendableReadChannel {
  long transferTo(long paramLong1, long paramLong2, FileChannel paramFileChannel) throws IOException;
  
  long transferTo(long paramLong, ByteBuffer paramByteBuffer, StreamSinkChannel paramStreamSinkChannel) throws IOException;
  
  ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter();
  
  ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\StreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */