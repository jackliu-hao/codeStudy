package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface ReadableMessageChannel extends SuspendableReadChannel, Configurable {
  int receive(ByteBuffer paramByteBuffer) throws IOException;
  
  long receive(ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
  
  long receive(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  ChannelListener.Setter<? extends ReadableMessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends ReadableMessageChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ReadableMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */