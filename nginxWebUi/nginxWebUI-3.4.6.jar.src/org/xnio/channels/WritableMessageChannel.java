package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface WritableMessageChannel extends SuspendableWriteChannel, Configurable {
  boolean send(ByteBuffer paramByteBuffer) throws IOException;
  
  boolean send(ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
  
  boolean send(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  boolean sendFinal(ByteBuffer paramByteBuffer) throws IOException;
  
  boolean sendFinal(ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
  
  boolean sendFinal(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  ChannelListener.Setter<? extends WritableMessageChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends WritableMessageChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\WritableMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */