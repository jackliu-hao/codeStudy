package org.xnio;

import java.nio.channels.Channel;
import org.xnio.channels.BoundChannel;

public interface ChannelDestination<T extends Channel> {
   IoFuture<? extends T> accept(ChannelListener<? super T> var1, ChannelListener<? super BoundChannel> var2);
}
