package org.xnio;

import java.io.IOException;
import java.net.SocketAddress;
import org.xnio.channels.BoundChannel;
import org.xnio.channels.StreamChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public interface XnioIoFactory {
   IoFuture<StreamConnection> openStreamConnection(SocketAddress var1, ChannelListener<? super StreamConnection> var2, OptionMap var3);

   IoFuture<StreamConnection> openStreamConnection(SocketAddress var1, ChannelListener<? super StreamConnection> var2, ChannelListener<? super BoundChannel> var3, OptionMap var4);

   IoFuture<StreamConnection> openStreamConnection(SocketAddress var1, SocketAddress var2, ChannelListener<? super StreamConnection> var3, ChannelListener<? super BoundChannel> var4, OptionMap var5);

   IoFuture<StreamConnection> acceptStreamConnection(SocketAddress var1, ChannelListener<? super StreamConnection> var2, ChannelListener<? super BoundChannel> var3, OptionMap var4);

   IoFuture<MessageConnection> openMessageConnection(SocketAddress var1, ChannelListener<? super MessageConnection> var2, OptionMap var3);

   IoFuture<MessageConnection> acceptMessageConnection(SocketAddress var1, ChannelListener<? super MessageConnection> var2, ChannelListener<? super BoundChannel> var3, OptionMap var4);

   ChannelPipe<StreamChannel, StreamChannel> createFullDuplexPipe() throws IOException;

   ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection() throws IOException;

   ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe() throws IOException;

   ChannelPipe<StreamConnection, StreamConnection> createFullDuplexPipeConnection(XnioIoFactory var1) throws IOException;

   ChannelPipe<StreamSourceChannel, StreamSinkChannel> createHalfDuplexPipe(XnioIoFactory var1) throws IOException;
}
