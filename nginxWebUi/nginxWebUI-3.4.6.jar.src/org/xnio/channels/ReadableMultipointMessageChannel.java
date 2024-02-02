package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface ReadableMultipointMessageChannel extends SuspendableReadChannel {
  int receiveFrom(SocketAddressBuffer paramSocketAddressBuffer, ByteBuffer paramByteBuffer) throws IOException;
  
  long receiveFrom(SocketAddressBuffer paramSocketAddressBuffer, ByteBuffer[] paramArrayOfByteBuffer) throws IOException;
  
  long receiveFrom(SocketAddressBuffer paramSocketAddressBuffer, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException;
  
  ChannelListener.Setter<? extends ReadableMultipointMessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends ReadableMultipointMessageChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ReadableMultipointMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */