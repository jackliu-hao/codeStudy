package org.xnio.channels;

import java.nio.channels.Channel;

public interface WrappedChannel<C extends Channel> {
   C getChannel();
}
