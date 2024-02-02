package io.undertow.util;

import io.undertow.UndertowLogger;
import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.Channel;
import org.xnio.ChannelExceptionHandler;
import org.xnio.IoUtils;

public class ClosingChannelExceptionHandler<T extends Channel> implements ChannelExceptionHandler<T> {
   private final Closeable[] closable;

   public ClosingChannelExceptionHandler(Closeable... closable) {
      this.closable = closable;
   }

   public void handleException(T t, IOException e) {
      UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
      IoUtils.safeClose((Closeable)t);
      IoUtils.safeClose(this.closable);
   }
}
