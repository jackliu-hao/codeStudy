package io.undertow.websockets.core;

import io.undertow.UndertowLogger;
import io.undertow.connector.ByteBufferPool;
import io.undertow.util.Transfer;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import org.xnio.Buffers;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public final class WebSocketUtils {
   private static final String EMPTY = "";

   public static ByteBuffer fromUtf8String(CharSequence utfString) {
      return utfString != null && utfString.length() != 0 ? ByteBuffer.wrap(utfString.toString().getBytes(StandardCharsets.UTF_8)) : Buffers.EMPTY_BYTE_BUFFER;
   }

   public static String toUtf8String(ByteBuffer buffer) {
      if (!buffer.hasRemaining()) {
         return "";
      } else if (buffer.hasArray()) {
         return new String(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining(), StandardCharsets.UTF_8);
      } else {
         byte[] content = new byte[buffer.remaining()];
         buffer.get(content);
         return new String(content, StandardCharsets.UTF_8);
      }
   }

   public static String toUtf8String(ByteBuffer... buffers) {
      int size = 0;
      ByteBuffer[] var2 = buffers;
      int var3 = buffers.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ByteBuffer buf = var2[var4];
         size += buf.remaining();
      }

      if (size == 0) {
         return "";
      } else {
         int index = 0;
         byte[] bytes = new byte[size];
         ByteBuffer[] var11 = buffers;
         int var12 = buffers.length;

         for(int var6 = 0; var6 < var12; ++var6) {
            ByteBuffer buf = var11[var6];
            int len;
            if (buf.hasArray()) {
               len = buf.remaining();
               System.arraycopy(buf.array(), buf.arrayOffset() + buf.position(), bytes, index, len);
               index += len;
            } else {
               len = buf.remaining();
               buf.get(bytes, index, len);
               index += len;
            }
         }

         return new String(bytes, StandardCharsets.UTF_8);
      }
   }

   public static long transfer(ReadableByteChannel source, long count, ByteBuffer throughBuffer, WritableByteChannel sink) throws IOException {
      long total = 0L;

      while(total < count) {
         throughBuffer.clear();
         if (count - total < (long)throughBuffer.remaining()) {
            throughBuffer.limit((int)(count - total));
         }

         long res;
         try {
            res = (long)source.read(throughBuffer);
            if (res <= 0L) {
               long var9 = total == 0L ? res : total;
               return var9;
            }
         } finally {
            throughBuffer.flip();
         }

         while(throughBuffer.hasRemaining()) {
            res = (long)sink.write(throughBuffer);
            if (res <= 0L) {
               return total;
            }

            total += res;
         }
      }

      return total;
   }

   public static void echoFrame(final WebSocketChannel channel, StreamSourceFrameChannel ws) throws IOException {
      final WebSocketFrameType type;
      switch (ws.getType()) {
         case PONG:
            ws.close();
            return;
         case PING:
            type = WebSocketFrameType.PONG;
            break;
         default:
            type = ws.getType();
      }

      StreamSinkFrameChannel sink = channel.send(type);
      sink.setRsv(ws.getRsv());
      initiateTransfer(ws, sink, new ChannelListener<StreamSourceFrameChannel>() {
         public void handleEvent(StreamSourceFrameChannel streamSourceFrameChannel) {
            IoUtils.safeClose((Closeable)streamSourceFrameChannel);
         }
      }, new ChannelListener<StreamSinkFrameChannel>() {
         public void handleEvent(StreamSinkFrameChannel streamSinkFrameChannel) {
            try {
               streamSinkFrameChannel.shutdownWrites();
            } catch (IOException var4) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var4);
               IoUtils.safeClose(streamSinkFrameChannel, channel);
               return;
            }

            try {
               if (!streamSinkFrameChannel.flush()) {
                  streamSinkFrameChannel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkFrameChannel>() {
                     public void handleEvent(StreamSinkFrameChannel streamSinkFrameChannel) {
                        streamSinkFrameChannel.getWriteSetter().set((ChannelListener)null);
                        IoUtils.safeClose((Closeable)streamSinkFrameChannel);
                        if (type == WebSocketFrameType.CLOSE) {
                           IoUtils.safeClose((Closeable)channel);
                        }

                     }
                  }, new ChannelExceptionHandler<StreamSinkFrameChannel>() {
                     public void handleException(StreamSinkFrameChannel streamSinkFrameChannel, IOException e) {
                        UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
                        IoUtils.safeClose(streamSinkFrameChannel, channel);
                     }
                  }));
                  streamSinkFrameChannel.resumeWrites();
               } else {
                  if (type == WebSocketFrameType.CLOSE) {
                     IoUtils.safeClose((Closeable)channel);
                  }

                  streamSinkFrameChannel.getWriteSetter().set((ChannelListener)null);
                  IoUtils.safeClose((Closeable)streamSinkFrameChannel);
               }
            } catch (IOException var3) {
               UndertowLogger.REQUEST_IO_LOGGER.ioException(var3);
               IoUtils.safeClose(streamSinkFrameChannel, channel);
            }

         }
      }, new ChannelExceptionHandler<StreamSourceFrameChannel>() {
         public void handleException(StreamSourceFrameChannel streamSourceFrameChannel, IOException e) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
            IoUtils.safeClose(streamSourceFrameChannel, channel);
         }
      }, new ChannelExceptionHandler<StreamSinkFrameChannel>() {
         public void handleException(StreamSinkFrameChannel streamSinkFrameChannel, IOException e) {
            UndertowLogger.REQUEST_IO_LOGGER.ioException(e);
            IoUtils.safeClose(streamSinkFrameChannel, channel);
         }
      }, channel.getBufferPool());
   }

   /** @deprecated */
   @Deprecated
   public static <I extends StreamSourceChannel, O extends StreamSinkChannel> void initiateTransfer(I source, O sink, ChannelListener<? super I> sourceListener, ChannelListener<? super O> sinkListener, ChannelExceptionHandler<? super I> readExceptionHandler, ChannelExceptionHandler<? super O> writeExceptionHandler, ByteBufferPool pool) {
      Transfer.initiateTransfer(source, sink, sourceListener, sinkListener, readExceptionHandler, writeExceptionHandler, pool);
   }

   private WebSocketUtils() {
   }
}
