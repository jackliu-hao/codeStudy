package io.undertow.websockets.core;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.util.ImmediatePooledByteBuffer;
import io.undertow.util.WorkerUtils;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.XnioExecutor;

public class WebSockets {
   public static void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendText((String)message, wsChannel, callback, (Object)null);
   }

   public static <T> void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      ByteBuffer data = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
      sendInternal(data, WebSocketFrameType.TEXT, wsChannel, callback, context, -1L);
   }

   public static void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendText((String)message, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendText(String message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      ByteBuffer data = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
      sendInternal(data, WebSocketFrameType.TEXT, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)message, WebSocketFrameType.TEXT, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(message, WebSocketFrameType.TEXT, wsChannel, callback, context, -1L);
   }

   public static void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)message, WebSocketFrameType.TEXT, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendText(ByteBuffer message, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(message, WebSocketFrameType.TEXT, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.TEXT, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(pooledData, WebSocketFrameType.TEXT, wsChannel, callback, context, -1L);
   }

   public static void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.TEXT, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendText(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(pooledData, WebSocketFrameType.TEXT, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendTextBlocking(String message, WebSocketChannel wsChannel) throws IOException {
      ByteBuffer data = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
      sendBlockingInternal(data, WebSocketFrameType.TEXT, wsChannel);
   }

   public static void sendTextBlocking(ByteBuffer message, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(message, WebSocketFrameType.TEXT, wsChannel);
   }

   public static void sendTextBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(pooledData, WebSocketFrameType.TEXT, wsChannel);
   }

   public static void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)data, WebSocketFrameType.PING, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(data, WebSocketFrameType.PING, wsChannel, callback, context, -1L);
   }

   public static void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)data, WebSocketFrameType.PING, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendPing(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(data, WebSocketFrameType.PING, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, context, -1L);
   }

   public static void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendPing(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.PING, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(pooledData, WebSocketFrameType.PING, wsChannel, callback, context, -1L);
   }

   public static void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.PING, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendPing(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(pooledData, WebSocketFrameType.PING, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendPingBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(data, WebSocketFrameType.PING, wsChannel);
   }

   public static void sendPingBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(mergeBuffers(data), WebSocketFrameType.PING, wsChannel);
   }

   public static void sendPingBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(pooledData, WebSocketFrameType.PING, wsChannel);
   }

   public static void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)data, WebSocketFrameType.PONG, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(data, WebSocketFrameType.PONG, wsChannel, callback, context, -1L);
   }

   public static void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)data, WebSocketFrameType.PONG, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendPong(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(data, WebSocketFrameType.PONG, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, context, -1L);
   }

   public static void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendPong(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.PONG, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(pooledData, WebSocketFrameType.PONG, wsChannel, callback, context, -1L);
   }

   public static void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.PONG, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendPong(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(pooledData, WebSocketFrameType.PONG, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendPongBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(data, WebSocketFrameType.PONG, wsChannel);
   }

   public static void sendPongBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(mergeBuffers(data), WebSocketFrameType.PONG, wsChannel);
   }

   public static void sendPongBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(pooledData, WebSocketFrameType.PONG, wsChannel);
   }

   public static void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)data, WebSocketFrameType.BINARY, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(data, WebSocketFrameType.BINARY, wsChannel, callback, context, -1L);
   }

   public static void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)data, WebSocketFrameType.BINARY, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendBinary(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(data, WebSocketFrameType.BINARY, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((ByteBuffer)mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, context, -1L);
   }

   public static void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((ByteBuffer)mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendBinary(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.BINARY, wsChannel, callback, (Object)null, -1L);
   }

   public static <T> void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendInternal(pooledData, WebSocketFrameType.BINARY, wsChannel, callback, context, -1L);
   }

   public static void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<Void> callback, long timeoutmillis) {
      sendInternal((PooledByteBuffer)pooledData, WebSocketFrameType.BINARY, wsChannel, callback, (Object)null, timeoutmillis);
   }

   public static <T> void sendBinary(PooledByteBuffer pooledData, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal(pooledData, WebSocketFrameType.BINARY, wsChannel, callback, context, timeoutmillis);
   }

   public static void sendBinaryBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(data, WebSocketFrameType.BINARY, wsChannel);
   }

   public static void sendBinaryBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(mergeBuffers(data), WebSocketFrameType.BINARY, wsChannel);
   }

   public static void sendBinaryBlocking(PooledByteBuffer pooledData, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal(pooledData, WebSocketFrameType.BINARY, wsChannel);
   }

   public static void sendClose(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      CloseMessage sm = new CloseMessage(data);
      sendClose(sm, wsChannel, callback);
   }

   public static <T> void sendClose(ByteBuffer data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      CloseMessage sm = new CloseMessage(data);
      sendClose(sm, wsChannel, callback, context);
   }

   public static void sendClose(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      CloseMessage sm = new CloseMessage(data);
      sendClose(sm, wsChannel, callback);
   }

   public static <T> void sendClose(ByteBuffer[] data, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      CloseMessage sm = new CloseMessage(data);
      sendClose(sm, wsChannel, callback, context);
   }

   public static void sendClose(int code, String reason, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendClose(new CloseMessage(code, reason), wsChannel, callback);
   }

   public static <T> void sendClose(int code, String reason, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      sendClose(new CloseMessage(code, reason), wsChannel, callback, context);
   }

   public static void sendClose(CloseMessage closeMessage, WebSocketChannel wsChannel, WebSocketCallback<Void> callback) {
      sendClose((CloseMessage)closeMessage, wsChannel, callback, (Object)null);
   }

   public static <T> void sendClose(CloseMessage closeMessage, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context) {
      wsChannel.setCloseCode(closeMessage.getCode());
      wsChannel.setCloseReason(closeMessage.getReason());
      sendInternal(closeMessage.toByteBuffer(), WebSocketFrameType.CLOSE, wsChannel, callback, context, -1L);
   }

   public static void sendCloseBlocking(CloseMessage closeMessage, WebSocketChannel wsChannel) throws IOException {
      wsChannel.setCloseReason(closeMessage.getReason());
      wsChannel.setCloseCode(closeMessage.getCode());
      sendBlockingInternal(closeMessage.toByteBuffer(), WebSocketFrameType.CLOSE, wsChannel);
   }

   public static void sendCloseBlocking(int code, String reason, WebSocketChannel wsChannel) throws IOException {
      sendCloseBlocking(new CloseMessage(code, reason), wsChannel);
   }

   public static void sendCloseBlocking(ByteBuffer data, WebSocketChannel wsChannel) throws IOException {
      sendCloseBlocking(new CloseMessage(data), wsChannel);
   }

   public static void sendCloseBlocking(ByteBuffer[] data, WebSocketChannel wsChannel) throws IOException {
      sendCloseBlocking(new CloseMessage(data), wsChannel);
   }

   private static <T> void sendInternal(ByteBuffer data, WebSocketFrameType type, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      sendInternal((PooledByteBuffer)(new ImmediatePooledByteBuffer(data)), type, wsChannel, callback, context, timeoutmillis);
   }

   private static <T> void sendInternal(PooledByteBuffer pooledData, WebSocketFrameType type, WebSocketChannel wsChannel, WebSocketCallback<T> callback, T context, long timeoutmillis) {
      boolean closePooledData = true;

      try {
         StreamSinkFrameChannel channel = wsChannel.send(type);
         closePooledData = false;
         if (!channel.send(pooledData)) {
            throw WebSocketMessages.MESSAGES.unableToSendOnNewChannel();
         }

         flushChannelAsync(wsChannel, callback, channel, context, timeoutmillis);
      } catch (IOException var12) {
         if (callback != null) {
            callback.onError(wsChannel, context, var12);
         } else {
            IoUtils.safeClose((Closeable)wsChannel);
         }
      } finally {
         if (closePooledData) {
            pooledData.close();
         }

      }

   }

   private static <T> void flushChannelAsync(final WebSocketChannel wsChannel, final WebSocketCallback<T> callback, StreamSinkFrameChannel channel, final T context, long timeoutmillis) throws IOException {
      final WebSocketFrameType type = channel.getType();
      channel.shutdownWrites();
      if (!channel.flush()) {
         channel.getWriteSetter().set(ChannelListeners.flushingChannelListener(new ChannelListener<StreamSinkFrameChannel>() {
            public void handleEvent(StreamSinkFrameChannel channel) {
               if (callback != null) {
                  callback.complete(wsChannel, context);
               }

               if (type == WebSocketFrameType.CLOSE && wsChannel.isCloseFrameReceived()) {
                  IoUtils.safeClose((Closeable)wsChannel);
               }

               channel.getWriteSetter().set((ChannelListener)null);
            }
         }, new ChannelExceptionHandler<StreamSinkFrameChannel>() {
            public void handleException(StreamSinkFrameChannel channel, IOException exception) {
               if (callback != null) {
                  callback.onError(wsChannel, context, exception);
               }

               IoUtils.safeClose(channel, wsChannel);
               channel.getWriteSetter().set((ChannelListener)null);
            }
         }));
         if (timeoutmillis > 0L) {
            setupTimeout(channel, timeoutmillis);
         }

         channel.resumeWrites();
      } else {
         if (callback != null) {
            callback.complete(wsChannel, context);
         }

      }
   }

   private static void setupTimeout(final StreamSinkFrameChannel channel, long timeoutmillis) {
      final XnioExecutor.Key key = WorkerUtils.executeAfter(channel.getIoThread(), new Runnable() {
         public void run() {
            if (channel.isOpen()) {
               IoUtils.safeClose((Closeable)channel);
            }

         }
      }, timeoutmillis, TimeUnit.MILLISECONDS);
      channel.getCloseSetter().set(new ChannelListener<StreamSinkFrameChannel>() {
         public void handleEvent(StreamSinkFrameChannel channel) {
            key.remove();
         }
      });
   }

   private static void sendBlockingInternal(ByteBuffer data, WebSocketFrameType type, WebSocketChannel wsChannel) throws IOException {
      sendBlockingInternal((PooledByteBuffer)(new ImmediatePooledByteBuffer(data)), type, wsChannel);
   }

   private static void sendBlockingInternal(PooledByteBuffer pooledData, WebSocketFrameType type, WebSocketChannel wsChannel) throws IOException {
      boolean closePooledData = true;

      try {
         StreamSinkFrameChannel channel = wsChannel.send(type);
         closePooledData = false;
         if (!channel.send(pooledData)) {
            throw WebSocketMessages.MESSAGES.unableToSendOnNewChannel();
         }

         channel.shutdownWrites();

         while(!channel.flush()) {
            channel.awaitWritable();
         }

         if (type == WebSocketFrameType.CLOSE && wsChannel.isCloseFrameReceived()) {
            IoUtils.safeClose((Closeable)wsChannel);
         }
      } finally {
         if (closePooledData) {
            pooledData.close();
         }

      }

   }

   private WebSockets() {
   }

   public static ByteBuffer mergeBuffers(ByteBuffer... payload) {
      int size = (int)Buffers.remaining(payload);
      if (size == 0) {
         return Buffers.EMPTY_BYTE_BUFFER;
      } else {
         ByteBuffer buffer = ByteBuffer.allocate(size);
         ByteBuffer[] var3 = payload;
         int var4 = payload.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            ByteBuffer buf = var3[var5];
            buffer.put(buf);
         }

         buffer.flip();
         return buffer;
      }
   }
}
