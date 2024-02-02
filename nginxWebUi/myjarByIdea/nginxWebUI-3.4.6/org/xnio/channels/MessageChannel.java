package org.xnio.channels;

import org.xnio.ChannelListener;

public interface MessageChannel extends ReadableMessageChannel, WritableMessageChannel, SuspendableChannel {
   ChannelListener.Setter<? extends MessageChannel> getReadSetter();

   ChannelListener.Setter<? extends MessageChannel> getCloseSetter();

   ChannelListener.Setter<? extends MessageChannel> getWriteSetter();
}
