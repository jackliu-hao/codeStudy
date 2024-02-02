package io.undertow.servlet.spec;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.servlet.UndertowServletMessages;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.Channels;
import org.xnio.channels.EmptyStreamSourceChannel;
import org.xnio.channels.StreamSourceChannel;

public class ServletInputStreamImpl extends ServletInputStream {
   private final HttpServletRequestImpl request;
   private final StreamSourceChannel channel;
   private final ByteBufferPool bufferPool;
   private volatile ReadListener listener;
   private volatile ServletInputStreamChannelListener internalListener;
   private static final int FLAG_READY = 1;
   private static final int FLAG_CLOSED = 2;
   private static final int FLAG_FINISHED = 4;
   private static final int FLAG_ON_DATA_READ_CALLED = 8;
   private static final int FLAG_CALL_ON_ALL_DATA_READ = 16;
   private static final int FLAG_BEING_INVOKED_IN_IO_THREAD = 32;
   private static final int FLAG_IS_READY_CALLED = 64;
   private volatile int state;
   private volatile AsyncContextImpl asyncContext;
   private volatile PooledByteBuffer pooled;
   private volatile boolean asyncIoStarted;
   private static final AtomicIntegerFieldUpdater<ServletInputStreamImpl> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(ServletInputStreamImpl.class, "state");

   public ServletInputStreamImpl(HttpServletRequestImpl request) {
      this.request = request;
      if (request.getExchange().isRequestChannelAvailable()) {
         this.channel = request.getExchange().getRequestChannel();
      } else {
         this.channel = new EmptyStreamSourceChannel(request.getExchange().getIoThread());
      }

      this.bufferPool = request.getExchange().getConnection().getByteBufferPool();
   }

   public boolean isFinished() {
      return Bits.anyAreSet(this.state, 4);
   }

   public boolean isReady() {
      if (!this.asyncContext.isInitialRequestDone()) {
         return false;
      } else {
         boolean finished = Bits.anyAreSet(this.state, 4);
         if (finished && Bits.anyAreClear(this.state, 8)) {
            if (Bits.allAreClear(this.state, 32)) {
               this.setFlags(8);
               this.request.getServletContext().invokeOnAllDataRead(this.request.getExchange(), this.listener);
            } else {
               this.setFlags(16);
            }
         }

         if (!this.asyncIoStarted) {
            return false;
         } else {
            boolean ready = Bits.anyAreSet(this.state, 1) && !finished;
            if (!ready && this.listener != null && !finished) {
               this.channel.resumeReads();
            }

            if (ready) {
               this.setFlags(64);
            }

            return ready;
         }
      }
   }

   public void setReadListener(ReadListener readListener) {
      if (readListener == null) {
         throw UndertowServletMessages.MESSAGES.listenerCannotBeNull();
      } else if (this.listener != null) {
         throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
      } else if (!this.request.isAsyncStarted()) {
         throw UndertowServletMessages.MESSAGES.asyncNotStarted();
      } else {
         this.asyncContext = this.request.getAsyncContext();
         this.listener = readListener;
         this.channel.getReadSetter().set(this.internalListener = new ServletInputStreamChannelListener());
         this.asyncContext.addAsyncTask(new Runnable() {
            public void run() {
               ServletInputStreamImpl.this.channel.getIoThread().execute(new Runnable() {
                  public void run() {
                     ServletInputStreamImpl.this.asyncIoStarted = true;
                     ServletInputStreamImpl.this.internalListener.handleEvent(ServletInputStreamImpl.this.channel);
                  }
               });
            }
         });
      }
   }

   public int read() throws IOException {
      byte[] b = new byte[1];
      int read = this.read(b);
      return read == -1 ? -1 : b[0] & 255;
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      } else {
         if (this.listener != null) {
            if (Bits.anyAreClear(this.state, 65)) {
               throw UndertowServletMessages.MESSAGES.streamNotReady();
            }

            this.clearFlags(64);
         } else {
            this.readIntoBuffer();
         }

         if (Bits.anyAreSet(this.state, 4)) {
            return -1;
         } else if (len == 0) {
            return 0;
         } else {
            ByteBuffer buffer = this.pooled.getBuffer();
            int copied = Math.min(buffer.remaining(), len);
            buffer.get(b, off, copied);
            if (!buffer.hasRemaining()) {
               this.pooled.close();
               this.pooled = null;
               if (this.listener != null) {
                  this.readIntoBufferNonBlocking();
               }
            }

            return copied;
         }
      }
   }

   private void readIntoBuffer() throws IOException {
      if (this.pooled == null && !Bits.anyAreSet(this.state, 4)) {
         this.pooled = this.bufferPool.allocate();
         int res = Channels.readBlocking(this.channel, this.pooled.getBuffer());
         this.pooled.getBuffer().flip();
         if (res == -1) {
            this.setFlags(4);
            this.pooled.close();
            this.pooled = null;
         }
      }

   }

   private void readIntoBufferNonBlocking() throws IOException {
      if (this.pooled == null && !Bits.anyAreSet(this.state, 4)) {
         this.pooled = this.bufferPool.allocate();
         int res;
         if (this.listener == null) {
            res = this.channel.read(this.pooled.getBuffer());
            if (res == 0) {
               this.pooled.close();
               this.pooled = null;
               return;
            }

            this.pooled.getBuffer().flip();
            if (res == -1) {
               this.setFlags(4);
               this.pooled.close();
               this.pooled = null;
            }
         } else {
            res = this.channel.read(this.pooled.getBuffer());
            this.pooled.getBuffer().flip();
            if (res == -1) {
               this.setFlags(4);
               this.pooled.close();
               this.pooled = null;
            } else if (res == 0) {
               this.clearFlags(1);
               this.pooled.close();
               this.pooled = null;
            }
         }
      }

   }

   public int available() throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      } else {
         this.readIntoBufferNonBlocking();
         if (Bits.anyAreSet(this.state, 4)) {
            return 0;
         } else {
            return this.pooled == null ? 0 : this.pooled.getBuffer().remaining();
         }
      }
   }

   public void close() throws IOException {
      if (!Bits.anyAreSet(this.state, 2)) {
         this.setFlags(2);

         try {
            while(Bits.allAreClear(this.state, 4)) {
               this.readIntoBuffer();
               if (this.pooled != null) {
                  this.pooled.close();
                  this.pooled = null;
               }
            }
         } finally {
            this.setFlags(4);
            if (this.pooled != null) {
               this.pooled.close();
               this.pooled = null;
            }

            this.channel.shutdownReads();
         }

      }
   }

   private void setFlags(int flags) {
      int old;
      do {
         old = this.state;
      } while(!stateUpdater.compareAndSet(this, old, old | flags));

   }

   private void clearFlags(int flags) {
      int old;
      do {
         old = this.state;
      } while(!stateUpdater.compareAndSet(this, old, old & ~flags));

   }

   private class ServletInputStreamChannelListener implements ChannelListener<StreamSourceChannel> {
      private ServletInputStreamChannelListener() {
      }

      public void handleEvent(StreamSourceChannel channel) {
         try {
            if (ServletInputStreamImpl.this.asyncContext.isDispatched()) {
               channel.suspendReads();
               return;
            }

            if (Bits.anyAreSet(ServletInputStreamImpl.this.state, 4)) {
               channel.suspendReads();
               return;
            }

            ServletInputStreamImpl.this.readIntoBufferNonBlocking();
            if (ServletInputStreamImpl.this.pooled != null) {
               channel.suspendReads();
               ServletInputStreamImpl.this.setFlags(1);
               if (!Bits.anyAreSet(ServletInputStreamImpl.this.state, 4)) {
                  ServletInputStreamImpl.this.setFlags(32);

                  try {
                     ServletInputStreamImpl.this.request.getServletContext().invokeOnDataAvailable(ServletInputStreamImpl.this.request.getExchange(), ServletInputStreamImpl.this.listener);
                  } finally {
                     ServletInputStreamImpl.this.clearFlags(32);
                  }

                  if (Bits.anyAreSet(ServletInputStreamImpl.this.state, 16) && Bits.allAreClear(ServletInputStreamImpl.this.state, 8)) {
                     ServletInputStreamImpl.this.setFlags(8);
                     ServletInputStreamImpl.this.request.getServletContext().invokeOnAllDataRead(ServletInputStreamImpl.this.request.getExchange(), ServletInputStreamImpl.this.listener);
                  }
               }
            } else if (Bits.anyAreSet(ServletInputStreamImpl.this.state, 4)) {
               if (Bits.allAreClear(ServletInputStreamImpl.this.state, 8)) {
                  ServletInputStreamImpl.this.setFlags(8);
                  ServletInputStreamImpl.this.request.getServletContext().invokeOnAllDataRead(ServletInputStreamImpl.this.request.getExchange(), ServletInputStreamImpl.this.listener);
               }
            } else {
               channel.resumeReads();
            }
         } catch (Throwable var12) {
            final Throwable e = var12;

            try {
               ServletInputStreamImpl.this.request.getServletContext().invokeRunnable(ServletInputStreamImpl.this.request.getExchange(), new Runnable() {
                  public void run() {
                     ServletInputStreamImpl.this.listener.onError(e);
                  }
               });
            } finally {
               if (ServletInputStreamImpl.this.pooled != null) {
                  ServletInputStreamImpl.this.pooled.close();
                  ServletInputStreamImpl.this.pooled = null;
               }

               IoUtils.safeClose((Closeable)channel);
            }
         }

      }

      // $FF: synthetic method
      ServletInputStreamChannelListener(Object x1) {
         this();
      }
   }
}
