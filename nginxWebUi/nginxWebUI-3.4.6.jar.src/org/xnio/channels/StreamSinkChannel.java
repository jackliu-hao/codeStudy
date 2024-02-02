package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import org.xnio.ChannelListener;

public interface StreamSinkChannel extends WritableByteChannel, GatheringByteChannel, SuspendableWriteChannel {
  long transferFrom(FileChannel paramFileChannel, long paramLong1, long paramLong2) throws IOException;
  
  long transferFrom(StreamSourceChannel paramStreamSourceChannel, long paramLong, ByteBuffer paramByteBuffer) throws IOException;
  
  ChannelListener.Setter<? extends StreamSinkChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends StreamSinkChannel> getCloseSetter();
  
  int writeFinal(ByteBuffer paramByteBuffer) throws IOException;
  
  long writeFinal(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  long writeFinal(ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\StreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */