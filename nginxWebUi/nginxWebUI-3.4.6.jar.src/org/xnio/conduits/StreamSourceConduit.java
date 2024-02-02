package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public interface StreamSourceConduit extends SourceConduit {
  long transferTo(long paramLong1, long paramLong2, FileChannel paramFileChannel) throws IOException;
  
  long transferTo(long paramLong, ByteBuffer paramByteBuffer, StreamSinkChannel paramStreamSinkChannel) throws IOException;
  
  int read(ByteBuffer paramByteBuffer) throws IOException;
  
  long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\StreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */