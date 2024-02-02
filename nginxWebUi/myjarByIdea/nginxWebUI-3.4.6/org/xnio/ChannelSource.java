package org.xnio;

import java.nio.channels.Channel;

public interface ChannelSource<T extends Channel> {
   IoFuture<T> open(ChannelListener<? super T> var1);
}
