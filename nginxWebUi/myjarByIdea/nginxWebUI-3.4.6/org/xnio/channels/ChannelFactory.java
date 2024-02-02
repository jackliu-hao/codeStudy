package org.xnio.channels;

import java.nio.channels.Channel;

public interface ChannelFactory<C extends Channel> {
   C create();
}
