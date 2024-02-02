package org.xnio;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.EventListener;

public interface ChannelExceptionHandler<T extends Channel> extends EventListener {
   void handleException(T var1, IOException var2);
}
