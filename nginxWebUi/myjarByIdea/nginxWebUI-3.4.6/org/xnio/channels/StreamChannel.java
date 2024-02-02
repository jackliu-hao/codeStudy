package org.xnio.channels;

import org.xnio.ChannelListener;

public interface StreamChannel extends SuspendableChannel, StreamSinkChannel, StreamSourceChannel, ByteChannel {
   ChannelListener.Setter<? extends StreamChannel> getReadSetter();

   ChannelListener.Setter<? extends StreamChannel> getWriteSetter();

   ChannelListener.Setter<? extends StreamChannel> getCloseSetter();
}
