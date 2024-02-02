package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageSinkConduit extends SinkConduit {
  boolean send(ByteBuffer paramByteBuffer) throws IOException;
  
  boolean send(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  boolean sendFinal(ByteBuffer paramByteBuffer) throws IOException;
  
  boolean sendFinal(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\MessageSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */