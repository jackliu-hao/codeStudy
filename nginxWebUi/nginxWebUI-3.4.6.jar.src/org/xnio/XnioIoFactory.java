package org.xnio;

import java.io.IOException;
import java.net.SocketAddress;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.StreamChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public interface XnioIoFactory {
  IoFuture<StreamConnection> openStreamConnection(SocketAddress paramSocketAddress, ChannelListener<? super StreamConnection> paramChannelListener, OptionMap paramOptionMap);
  
  IoFuture<StreamConnection> openStreamConnection(SocketAddress paramSocketAddress, ChannelListener<? super StreamConnection> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
  
  IoFuture<StreamConnection> openStreamConnection(SocketAddress paramSocketAddress1, SocketAddress paramSocketAddress2, ChannelListener<? super StreamConnection> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
  
  IoFuture<StreamConnection> acceptStreamConnection(SocketAddress paramSocketAddress, ChannelListener<? super StreamConnection> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
  
  IoFuture<MessageConnection> openMessageConnection(SocketAddress paramSocketAddress, ChannelListener<? super MessageConnection> paramChannelListener, OptionMap paramOptionMap);
  
  IoFuture<MessageConnection> acceptMessageConnection(SocketAddress paramSocketAddress, ChannelListener<? super MessageConnection> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1, OptionMap paramOptionMap);
  
  ChannelPipe<StreamChannel, StreamChannel> createFullDuplexPipe() throws IOException;
  
  ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection() throws IOException;
  
  ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe() throws IOException;
  
  ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory paramXnioIoFactory) throws IOException;
  
  ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory paramXnioIoFactory) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioIoFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */