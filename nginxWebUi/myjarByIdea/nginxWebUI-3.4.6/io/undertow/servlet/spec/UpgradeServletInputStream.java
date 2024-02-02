package io.undertow.servlet.spec;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.servlet.UndertowServletMessages;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSourceChannel;

public class UpgradeServletInputStream extends ServletInputStream {
   private final StreamSourceChannel channel;
   private final ByteBufferPool bufferPool;
   private final Executor ioExecutor;
   private volatile ReadListener listener;
   private static final int FLAG_READY = 1;
   private static final int FLAG_CLOSED = 2;
   private static final int FLAG_FINISHED = 4;
   private static final int FLAG_ON_DATA_READ_CALLED = 8;
   private int state;
   private PooledByteBuffer pooled;

   public UpgradeServletInputStream(StreamSourceChannel channel, ByteBufferPool bufferPool, Executor ioExecutor) {
      this.channel = channel;
      this.bufferPool = bufferPool;
      this.ioExecutor = ioExecutor;
   }

   public boolean isFinished() {
      return Bits.anyAreSet(this.state, 4);
   }

   public boolean isReady() {
      return Bits.anyAreSet(this.state, 1) && !this.isFinished();
   }

   public void setReadListener(ReadListener readListener) {
      if (readListener == null) {
         throw UndertowServletMessages.MESSAGES.listenerCannotBeNull();
      } else if (this.listener != null) {
         throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
      } else {
         this.listener = readListener;
         this.channel.getReadSetter().set(new ServletInputStreamChannelListener());
         this.ioExecutor.execute(new Runnable() {
            public void run() {
               UpgradeServletInputStream.this.channel.wakeupReads();
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
            if (Bits.anyAreClear(this.state, 1)) {
               throw UndertowServletMessages.MESSAGES.streamNotReady();
            }
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
            this.state |= 4;
            this.pooled.close();
            this.pooled = null;
         }
      }

   }

   private void readIntoBufferNonBlocking() throws IOException {
      if (this.pooled == null && !Bits.anyAreSet(this.state, 6)) {
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
               this.state |= 4;
               this.pooled.close();
               this.pooled = null;
            }
         } else {
            if (Bits.anyAreClear(this.state, 1)) {
               throw UndertowServletMessages.MESSAGES.streamNotReady();
            }

            res = this.channel.read(this.pooled.getBuffer());
            this.pooled.getBuffer().flip();
            if (res == -1) {
               this.state |= 4;
               this.pooled.close();
               this.pooled = null;
            } else if (res == 0) {
               this.state &= -2;
               this.pooled.close();
               this.pooled = null;
               if (Thread.currentThread() == this.channel.getIoThread()) {
                  this.channel.resumeReads();
               } else {
                  this.ioExecutor.execute(new Runnable() {
                     public void run() {
                        UpgradeServletInputStream.this.channel.resumeReads();
                     }
                  });
               }
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
         this.state |= 6;
         if (this.pooled != null) {
            this.pooled.close();
            this.pooled = null;
         }

         this.channel.suspendReads();
         this.channel.shutdownReads();
      }
   }

   private class ServletInputStreamChannelListener implements ChannelListener<StreamSourceChannel> {
      private ServletInputStreamChannelListener() {
      }

      public void handleEvent(StreamSourceChannel channel) {
         if (!Bits.anyAreSet(UpgradeServletInputStream.this.state, 4)) {
            UpgradeServletInputStream.this.state = UpgradeServletInputStream.this.state | 1;

            try {
               UpgradeServletInputStream.this.readIntoBufferNonBlocking();
               if (UpgradeServletInputStream.this.pooled != null) {
                  UpgradeServletInputStream.this.state = UpgradeServletInputStream.this.state | 1;
                  if (!Bits.anyAreSet(UpgradeServletInputStream.this.state, 4)) {
                     UpgradeServletInputStream.this.listener.onDataAvailable();
                  }
               }
            } catch (Exception var4) {
               if (UpgradeServletInputStream.this.pooled != null) {
                  UpgradeServletInputStream.this.pooled.close();
                  UpgradeServletInputStream.this.pooled = null;
               }

               UpgradeServletInputStream.this.listener.onError(var4);
               IoUtils.safeClose((Closeable)channel);
            }

            if (Bits.anyAreSet(UpgradeServletInputStream.this.state, 4)) {
               if (Bits.anyAreClear(UpgradeServletInputStream.this.state, 8)) {
                  try {
                     UpgradeServletInputStream.this.state = UpgradeServletInputStream.this.state | 8;
                     channel.shutdownReads();
                     UpgradeServletInputStream.this.listener.onAllDataRead();
                  } catch (IOException var3) {
                     if (UpgradeServletInputStream.this.pooled != null) {
                        UpgradeServletInputStream.this.pooled.close();
                        UpgradeServletInputStream.this.pooled = null;
                     }

                     UpgradeServletInputStream.this.listener.onError(var3);
                     IoUtils.safeClose((Closeable)channel);
                  }
               }
            } else if (UpgradeServletInputStream.this.isReady()) {
               channel.suspendReads();
            }

         }
      }

      // $FF: synthetic method
      ServletInputStreamChannelListener(Object x1) {
         this();
      }
   }
}
