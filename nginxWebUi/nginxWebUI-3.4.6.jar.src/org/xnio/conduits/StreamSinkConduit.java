package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;

public interface StreamSinkConduit extends SinkConduit {
  long transferFrom(FileChannel paramFileChannel, long paramLong1, long paramLong2) throws IOException;
  
  long transferFrom(StreamSourceChannel paramStreamSourceChannel, long paramLong, ByteBuffer paramByteBuffer) throws IOException;
  
  int write(ByteBuffer paramByteBuffer) throws IOException;
  
  long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  int writeFinal(ByteBuffer paramByteBuffer) throws IOException;
  
  long writeFinal(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\StreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */