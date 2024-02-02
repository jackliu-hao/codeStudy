package org.xnio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface WritableMultipointMessageChannel extends SuspendableWriteChannel {
  boolean sendTo(SocketAddress paramSocketAddress, ByteBuffer paramByteBuffer) throws IOException;
  
  boolean sendTo(SocketAddress paramSocketAddress, ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
  
  boolean sendTo(SocketAddress paramSocketAddress, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  ChannelListener.Setter<? extends WritableMultipointMessageChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends WritableMultipointMessageChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\WritableMultipointMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */