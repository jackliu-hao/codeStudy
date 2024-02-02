package org.xnio.channels;

import java.nio.channels.Channel;

public interface ProtectedWrappedChannel<C extends Channel> {
   C getChannel(Object var1);
}
