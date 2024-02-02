package io.undertow.util;

import io.undertow.UndertowLogger;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.StreamConnection;
import org.xnio.XnioExecutor;
import org.xnio.conduits.ConduitStreamSinkChannel;
import org.xnio.conduits.ConduitStreamSourceChannel;

public class ConnectionUtils {
   private static final long MAX_DRAIN_TIME = Long.getLong("io.undertow.max-drain-time", 10000L);

   private ConnectionUtils() {
   }

   public static void cleanClose(final StreamConnection connection, final Closeable... additional) {
      try {
         connection.getSinkChannel().shutdownWrites();
         if (!connection.getSinkChannel().flush()) {
            connection.getSinkChannel().setWriteListener(ChannelListeners.flushingChannelListener(new ChannelListener<ConduitStreamSinkChannel>() {
               public void handleEvent(ConduitStreamSinkChannel channel) {
                  ConnectionUtils.doDrain(connection, additional);
               }
            }, new ChannelExceptionHandler<ConduitStreamSinkChannel>() {
               public void handleException(ConduitStreamSinkChannel channel, IOException exception) {
                  UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
                  IoUtils.safeClose((Closeable)connection);
                  IoUtils.safeClose(additional);
               }
            }));
            connection.getSinkChannel().resumeWrites();
         } else {
            doDrain(connection, additional);
         }
      } catch (Throwable var3) {
         if (var3 instanceof IOException) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var3);
         } else {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var3));
         }

         IoUtils.safeClose((Closeable)connection);
         IoUtils.safeClose(additional);
      }

   }

   private static void doDrain(final StreamConnection connection, final Closeable... additional) {
      if (!connection.getSourceChannel().isOpen()) {
         IoUtils.safeClose((Closeable)connection);
         IoUtils.safeClose(additional);
      } else {
         final ByteBuffer b = ByteBuffer.allocate(1);

         try {
            int res = connection.getSourceChannel().read(b);
            b.clear();
            if (res == 0) {
               final XnioExecutor.Key key = WorkerUtils.executeAfter(connection.getIoThread(), new Runnable() {
                  public void run() {
                     IoUtils.safeClose((Closeable)connection);
                     IoUtils.safeClose(additional);
                  }
               }, MAX_DRAIN_TIME, TimeUnit.MILLISECONDS);
               connection.getSourceChannel().setReadListener(new ChannelListener<ConduitStreamSourceChannel>() {
                  public void handleEvent(ConduitStreamSourceChannel channel) {
                     try {
                        int res = channel.read(b);
                        if (res != 0) {
                           IoUtils.safeClose((Closeable)connection);
                           IoUtils.safeClose(additional);
                           key.remove();
                        }
                     } catch (Exception var3) {
                        if (var3 instanceof IOException) {
                           UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var3);
                        } else {
                           UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var3));
                        }

                        IoUtils.safeClose((Closeable)connection);
                        IoUtils.safeClose(additional);
                        key.remove();
                     }

                  }
               });
               connection.getSourceChannel().resumeReads();
            } else {
               IoUtils.safeClose((Closeable)connection);
               IoUtils.safeClose(additional);
            }
         } catch (Throwable var5) {
            if (var5 instanceof IOException) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)var5);
            } else {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(new IOException(var5));
            }

            IoUtils.safeClose((Closeable)connection);
            IoUtils.safeClose(additional);
         }

      }
   }
}
