package io.undertow.server.handlers.sse;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Attachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.AttachmentList;
import io.undertow.util.HeaderMap;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.xnio.ChannelExceptionHandler;
import org.xnio.ChannelListener;
import org.xnio.ChannelListeners;
import org.xnio.IoUtils;
import org.xnio.XnioExecutor;
import org.xnio.channels.StreamSinkChannel;

public class ServerSentEventConnection implements Channel, Attachable {
   private final HttpServerExchange exchange;
   private final StreamSinkChannel sink;
   private final SseWriteListener writeListener = new SseWriteListener();
   private PooledByteBuffer pooled;
   private final Deque<SSEData> queue = new ConcurrentLinkedDeque();
   private final Queue<SSEData> buffered = new ConcurrentLinkedDeque();
   private final Queue<SSEData> flushingMessages = new ArrayDeque();
   private final List<ChannelListener<ServerSentEventConnection>> closeTasks = new CopyOnWriteArrayList();
   private Map<String, String> parameters;
   private Map<String, Object> properties = new HashMap();
   private static final AtomicIntegerFieldUpdater<ServerSentEventConnection> openUpdater = AtomicIntegerFieldUpdater.newUpdater(ServerSentEventConnection.class, "open");
   private volatile int open = 1;
   private volatile boolean shutdown = false;
   private volatile long keepAliveTime = -1L;
   private XnioExecutor.Key timerKey;

   public ServerSentEventConnection(HttpServerExchange exchange, StreamSinkChannel sink) {
      this.exchange = exchange;
      this.sink = sink;
      this.sink.getCloseSetter().set(new ChannelListener<StreamSinkChannel>() {
         public void handleEvent(StreamSinkChannel channel) {
            if (ServerSentEventConnection.this.timerKey != null) {
               ServerSentEventConnection.this.timerKey.remove();
            }

            Iterator var2 = ServerSentEventConnection.this.closeTasks.iterator();

            while(var2.hasNext()) {
               ChannelListener<ServerSentEventConnection> listener = (ChannelListener)var2.next();
               ChannelListeners.invokeChannelListener(ServerSentEventConnection.this, listener);
            }

            IoUtils.safeClose((Closeable)ServerSentEventConnection.this);
         }
      });
      this.sink.getWriteSetter().set(this.writeListener);
   }

   public synchronized void addCloseTask(ChannelListener<ServerSentEventConnection> listener) {
      this.closeTasks.add(listener);
   }

   public Principal getPrincipal() {
      Account account = this.getAccount();
      return account != null ? account.getPrincipal() : null;
   }

   public Account getAccount() {
      SecurityContext sc = this.exchange.getSecurityContext();
      return sc != null ? sc.getAuthenticatedAccount() : null;
   }

   public HeaderMap getRequestHeaders() {
      return this.exchange.getRequestHeaders();
   }

   public HeaderMap getResponseHeaders() {
      return this.exchange.getResponseHeaders();
   }

   public String getRequestURI() {
      return this.exchange.getRequestURI();
   }

   public Map<String, Deque<String>> getQueryParameters() {
      return this.exchange.getQueryParameters();
   }

   public String getQueryString() {
      return this.exchange.getQueryString();
   }

   public void send(String data) {
      this.send(data, (String)null, (String)null, (EventCallback)null);
   }

   public void send(String data, EventCallback callback) {
      this.send(data, (String)null, (String)null, callback);
   }

   public void sendRetry(long retry) {
      this.sendRetry(retry, (EventCallback)null);
   }

   public synchronized void sendRetry(long retry, EventCallback callback) {
      if (this.open != 0 && !this.shutdown) {
         this.queue.add(new SSEData(retry, callback));
         this.sink.getIoThread().execute(new Runnable() {
            public void run() {
               synchronized(ServerSentEventConnection.this) {
                  if (ServerSentEventConnection.this.pooled == null) {
                     ServerSentEventConnection.this.fillBuffer();
                     ServerSentEventConnection.this.writeListener.handleEvent(ServerSentEventConnection.this.sink);
                  }

               }
            }
         });
      } else {
         if (callback != null) {
            callback.failed(this, (String)null, (String)null, (String)null, new ClosedChannelException());
         }

      }
   }

   public synchronized void send(String data, String event, String id, EventCallback callback) {
      if (this.open != 0 && !this.shutdown) {
         this.queue.add(new SSEData(event, data, id, callback));
         this.sink.getIoThread().execute(new Runnable() {
            public void run() {
               synchronized(ServerSentEventConnection.this) {
                  if (ServerSentEventConnection.this.pooled == null) {
                     ServerSentEventConnection.this.fillBuffer();
                     ServerSentEventConnection.this.writeListener.handleEvent(ServerSentEventConnection.this.sink);
                  }

               }
            }
         });
      } else {
         if (callback != null) {
            callback.failed(this, data, event, id, new ClosedChannelException());
         }

      }
   }

   public String getParameter(String name) {
      return this.parameters == null ? null : (String)this.parameters.get(name);
   }

   public void setParameter(String name, String value) {
      if (this.parameters == null) {
         this.parameters = new HashMap();
      }

      this.parameters.put(name, value);
   }

   public Map<String, Object> getProperties() {
      return this.properties;
   }

   public long getKeepAliveTime() {
      return this.keepAliveTime;
   }

   public void setKeepAliveTime(long keepAliveTime) {
      this.keepAliveTime = keepAliveTime;
      if (this.timerKey != null) {
         this.timerKey.remove();
      }

      this.timerKey = this.sink.getIoThread().executeAtInterval(new Runnable() {
         public void run() {
            if (!ServerSentEventConnection.this.shutdown && ServerSentEventConnection.this.open != 0) {
               if (ServerSentEventConnection.this.pooled == null) {
                  ServerSentEventConnection.this.pooled = ServerSentEventConnection.this.exchange.getConnection().getByteBufferPool().allocate();
                  ServerSentEventConnection.this.pooled.getBuffer().put(":\n".getBytes(StandardCharsets.UTF_8));
                  ServerSentEventConnection.this.pooled.getBuffer().flip();
                  ServerSentEventConnection.this.writeListener.handleEvent(ServerSentEventConnection.this.sink);
               }

            } else {
               if (ServerSentEventConnection.this.timerKey != null) {
                  ServerSentEventConnection.this.timerKey.remove();
               }

            }
         }
      }, keepAliveTime, TimeUnit.MILLISECONDS);
   }

   private void fillBuffer() {
      if (this.queue.isEmpty()) {
         if (this.pooled != null) {
            this.pooled.close();
            this.pooled = null;
            this.sink.suspendWrites();
         }

      } else {
         if (this.pooled == null) {
            this.pooled = this.exchange.getConnection().getByteBufferPool().allocate();
         } else {
            this.pooled.getBuffer().clear();
         }

         ByteBuffer buffer = this.pooled.getBuffer();

         while(!this.queue.isEmpty() && buffer.hasRemaining()) {
            SSEData data = (SSEData)this.queue.poll();
            this.buffered.add(data);
            int i;
            if (data.leftOverData != null) {
               int remainingData = data.leftOverData.length - data.leftOverDataOffset;
               if (remainingData > buffer.remaining()) {
                  this.queue.addFirst(data);
                  i = buffer.remaining();
                  buffer.put(data.leftOverData, data.leftOverDataOffset, i);
                  data.leftOverDataOffset = data.leftOverDataOffset + i;
               } else {
                  buffer.put(data.leftOverData, data.leftOverDataOffset, remainingData);
                  data.endBufferPosition = buffer.position();
                  data.leftOverData = null;
               }
            } else {
               StringBuilder message = new StringBuilder();
               int c;
               if (data.retry > 0L) {
                  message.append("retry:");
                  message.append(data.retry);
                  message.append('\n');
               } else {
                  if (data.id != null) {
                     message.append("id:");
                     message.append(data.id);
                     message.append('\n');
                  }

                  if (data.event != null) {
                     message.append("event:");
                     message.append(data.event);
                     message.append('\n');
                  }

                  if (data.data != null) {
                     message.append("data:");

                     for(i = 0; i < data.data.length(); ++i) {
                        c = data.data.charAt(i);
                        if (c == 10) {
                           message.append("\ndata:");
                        } else {
                           message.append((char)c);
                        }
                     }

                     message.append('\n');
                  }
               }

               message.append('\n');
               byte[] messageBytes = message.toString().getBytes(StandardCharsets.UTF_8);
               if (messageBytes.length < buffer.remaining()) {
                  buffer.put(messageBytes);
                  data.endBufferPosition = buffer.position();
               } else {
                  this.queue.addFirst(data);
                  c = buffer.remaining();
                  buffer.put(messageBytes, 0, c);
                  data.leftOverData = messageBytes;
                  data.leftOverDataOffset = c;
               }
            }
         }

         buffer.flip();
         this.sink.resumeWrites();
      }
   }

   public void shutdown() {
      if (this.open != 0 && !this.shutdown) {
         this.shutdown = true;
         this.sink.getIoThread().execute(new Runnable() {
            public void run() {
               synchronized(ServerSentEventConnection.this) {
                  if (ServerSentEventConnection.this.queue.isEmpty() && ServerSentEventConnection.this.pooled == null) {
                     ServerSentEventConnection.this.exchange.endExchange();
                  }

               }
            }
         });
      }
   }

   public boolean isOpen() {
      return this.open != 0;
   }

   public void close() throws IOException {
      this.close(new ClosedChannelException());
   }

   private synchronized void close(IOException e) throws IOException {
      if (openUpdater.compareAndSet(this, 1, 0)) {
         if (this.pooled != null) {
            this.pooled.close();
            this.pooled = null;
         }

         List<SSEData> cb = new ArrayList(this.buffered.size() + this.queue.size() + this.flushingMessages.size());
         cb.addAll(this.buffered);
         cb.addAll(this.queue);
         cb.addAll(this.flushingMessages);
         this.queue.clear();
         this.buffered.clear();
         this.flushingMessages.clear();
         Iterator var3 = cb.iterator();

         while(var3.hasNext()) {
            SSEData i = (SSEData)var3.next();
            if (i.callback != null) {
               try {
                  i.callback.failed(this, i.data, i.event, i.id, e);
               } catch (Exception var6) {
                  UndertowLogger.REQUEST_LOGGER.failedToInvokeFailedCallback(i.callback, var6);
               }
            }
         }

         this.sink.shutdownWrites();
         if (!this.sink.flush()) {
            this.sink.getWriteSetter().set(ChannelListeners.flushingChannelListener((ChannelListener)null, new ChannelExceptionHandler<StreamSinkChannel>() {
               public void handleException(StreamSinkChannel channel, IOException exception) {
                  IoUtils.safeClose((Closeable)ServerSentEventConnection.this.sink);
               }
            }));
            this.sink.resumeWrites();
         }
      }

   }

   public <T> T getAttachment(AttachmentKey<T> key) {
      return this.exchange.getAttachment(key);
   }

   public <T> List<T> getAttachmentList(AttachmentKey<? extends List<T>> key) {
      return this.exchange.getAttachmentList(key);
   }

   public <T> T putAttachment(AttachmentKey<T> key, T value) {
      return this.exchange.putAttachment(key, value);
   }

   public <T> T removeAttachment(AttachmentKey<T> key) {
      return this.exchange.removeAttachment(key);
   }

   public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
      this.exchange.addToAttachmentList(key, value);
   }

   private void handleException(IOException e) {
      IoUtils.safeClose(this, this.sink, this.exchange.getConnection());
   }

   private class SseWriteListener implements ChannelListener<StreamSinkChannel> {
      private SseWriteListener() {
      }

      public void handleEvent(StreamSinkChannel channel) {
         synchronized(ServerSentEventConnection.this) {
            try {
               ByteBuffer buffer;
               if (ServerSentEventConnection.this.flushingMessages.isEmpty()) {
                  if (ServerSentEventConnection.this.pooled == null) {
                     if (channel.flush()) {
                        channel.suspendWrites();
                     }

                     return;
                  }
               } else {
                  if (!channel.flush()) {
                     return;
                  }

                  Iterator var3 = ServerSentEventConnection.this.flushingMessages.iterator();

                  while(var3.hasNext()) {
                     SSEData datax = (SSEData)var3.next();
                     if (datax.callback != null && datax.leftOverData == null) {
                        datax.callback.done(ServerSentEventConnection.this, datax.data, datax.event, datax.id);
                     }
                  }

                  ServerSentEventConnection.this.flushingMessages.clear();
                  buffer = ServerSentEventConnection.this.pooled.getBuffer();
                  if (!buffer.hasRemaining()) {
                     ServerSentEventConnection.this.fillBuffer();
                     if (ServerSentEventConnection.this.pooled == null) {
                        if (channel.flush()) {
                           channel.suspendWrites();
                        }

                        return;
                     }
                  }
               }

               buffer = ServerSentEventConnection.this.pooled.getBuffer();

               int res;
               do {
                  res = channel.write(buffer);
                  boolean flushed = channel.flush();

                  while(!ServerSentEventConnection.this.buffered.isEmpty()) {
                     SSEData data = (SSEData)ServerSentEventConnection.this.buffered.peek();
                     if (data.endBufferPosition <= 0 || buffer.position() < data.endBufferPosition) {
                        if (data.endBufferPosition <= 0) {
                           ServerSentEventConnection.this.buffered.poll();
                        }
                        break;
                     }

                     ServerSentEventConnection.this.buffered.poll();
                     if (flushed) {
                        if (data.callback != null && data.leftOverData == null) {
                           data.callback.done(ServerSentEventConnection.this, data.data, data.event, data.id);
                        }
                     } else {
                        ServerSentEventConnection.this.flushingMessages.add(data);
                     }
                  }

                  if (!flushed && !ServerSentEventConnection.this.flushingMessages.isEmpty()) {
                     ServerSentEventConnection.this.sink.resumeWrites();
                     return;
                  }

                  if (!buffer.hasRemaining()) {
                     ServerSentEventConnection.this.fillBuffer();
                     if (ServerSentEventConnection.this.pooled == null) {
                        return;
                     }
                  } else if (res == 0) {
                     ServerSentEventConnection.this.sink.resumeWrites();
                     return;
                  }
               } while(res > 0);

            } catch (IOException var8) {
               ServerSentEventConnection.this.handleException(var8);
            }
         }
      }

      // $FF: synthetic method
      SseWriteListener(Object x1) {
         this();
      }
   }

   private static class SSEData {
      final String event;
      final String data;
      final String id;
      final long retry;
      final EventCallback callback;
      private int endBufferPosition;
      private byte[] leftOverData;
      private int leftOverDataOffset;

      private SSEData(String event, String data, String id, EventCallback callback) {
         this.endBufferPosition = -1;
         this.event = event;
         this.data = data;
         this.id = id;
         this.callback = callback;
         this.retry = -1L;
      }

      private SSEData(long retry, EventCallback callback) {
         this.endBufferPosition = -1;
         this.event = null;
         this.data = null;
         this.id = null;
         this.callback = callback;
         this.retry = retry;
      }

      // $FF: synthetic method
      SSEData(long x0, EventCallback x1, Object x2) {
         this(x0, x1);
      }

      // $FF: synthetic method
      SSEData(String x0, String x1, String x2, EventCallback x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }

   public interface EventCallback {
      void done(ServerSentEventConnection var1, String var2, String var3, String var4);

      void failed(ServerSentEventConnection var1, String var2, String var3, String var4, IOException var5);
   }
}
