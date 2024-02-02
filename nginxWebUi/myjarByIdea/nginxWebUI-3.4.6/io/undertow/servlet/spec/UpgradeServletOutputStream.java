package io.undertow.servlet.spec;

import io.undertow.servlet.UndertowServletMessages;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import org.xnio.Bits;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;

public class UpgradeServletOutputStream extends ServletOutputStream {
   private final StreamSinkChannel channel;
   private WriteListener listener;
   private final Executor ioExecutor;
   private static final int FLAG_READY = 1;
   private static final int FLAG_CLOSED = 2;
   private static final int FLAG_DELEGATE_SHUTDOWN = 4;
   private int state;
   private ByteBuffer buffer;

   protected UpgradeServletOutputStream(StreamSinkChannel channel, Executor ioExecutor) {
      this.channel = channel;
      this.ioExecutor = ioExecutor;
   }

   public void write(byte[] b) throws IOException {
      this.write(b, 0, b.length);
   }

   public void write(byte[] b, int off, int len) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      } else {
         if (this.listener == null) {
            Channels.writeBlocking(this.channel, ByteBuffer.wrap(b, off, len));
         } else {
            if (Bits.anyAreClear(this.state, 1)) {
               throw UndertowServletMessages.MESSAGES.streamNotReady();
            }

            ByteBuffer buffer = ByteBuffer.wrap(b);

            do {
               int res = this.channel.write(buffer);
               if (res == 0) {
                  ByteBuffer copy = ByteBuffer.allocate(buffer.remaining());
                  copy.put(buffer);
                  copy.flip();
                  this.buffer = copy;
                  this.state &= -2;
                  if (Thread.currentThread() == this.channel.getIoThread()) {
                     this.channel.resumeWrites();
                  } else {
                     this.ioExecutor.execute(new Runnable() {
                        public void run() {
                           UpgradeServletOutputStream.this.channel.resumeWrites();
                        }
                     });
                  }

                  return;
               }
            } while(buffer.hasRemaining());
         }

      }
   }

   public void write(int b) throws IOException {
      this.write(new byte[]{(byte)b}, 0, 1);
   }

   public void flush() throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw UndertowServletMessages.MESSAGES.streamIsClosed();
      } else {
         if (this.listener == null) {
            Channels.flushBlocking(this.channel);
         }

      }
   }

   public void close() throws IOException {
      this.state |= 2;
      this.state &= -2;
      if (this.listener == null) {
         this.channel.shutdownWrites();
         this.state |= 4;
         Channels.flushBlocking(this.channel);
      } else if (this.buffer == null) {
         this.channel.shutdownWrites();
         this.state |= 4;
         if (!this.channel.flush()) {
            if (Thread.currentThread() == this.channel.getIoThread()) {
               this.channel.resumeWrites();
            } else {
               this.ioExecutor.execute(new Runnable() {
                  public void run() {
                     UpgradeServletOutputStream.this.channel.resumeWrites();
                  }
               });
            }
         }
      }

   }

   void closeBlocking() throws IOException {
      this.state |= 2;

      try {
         if (this.buffer != null) {
            Channels.writeBlocking(this.channel, this.buffer);
         }

         this.channel.shutdownWrites();
         Channels.flushBlocking(this.channel);
      } catch (IOException var2) {
         this.channel.close();
         throw var2;
      }
   }

   public boolean isReady() {
      if (this.listener == null) {
         throw UndertowServletMessages.MESSAGES.streamNotInAsyncMode();
      } else {
         return Bits.anyAreSet(this.state, 1);
      }
   }

   public void setWriteListener(WriteListener writeListener) {
      if (writeListener == null) {
         throw UndertowServletMessages.MESSAGES.paramCannotBeNull("writeListener");
      } else if (this.listener != null) {
         throw UndertowServletMessages.MESSAGES.listenerAlreadySet();
      } else {
         this.listener = writeListener;
         this.channel.getWriteSetter().set(new WriteChannelListener());
         this.state |= 1;
         this.ioExecutor.execute(new Runnable() {
            public void run() {
               UpgradeServletOutputStream.this.channel.resumeWrites();
            }
         });
      }
   }

   private class WriteChannelListener implements ChannelListener<StreamSinkChannel> {
      private WriteChannelListener() {
      }

      public void handleEvent(StreamSinkChannel channel) {
         if (Bits.anyAreSet(UpgradeServletOutputStream.this.state, 4)) {
            try {
               channel.flush();
               return;
            } catch (IOException var7) {
               this.handleError(channel, var7);
            }
         }

         if (UpgradeServletOutputStream.this.buffer != null) {
            do {
               try {
                  int res = channel.write(UpgradeServletOutputStream.this.buffer);
                  if (res == 0) {
                     return;
                  }
               } catch (IOException var6) {
                  this.handleError(channel, var6);
               }
            } while(UpgradeServletOutputStream.this.buffer.hasRemaining());

            UpgradeServletOutputStream.this.buffer = null;
         }

         if (Bits.anyAreSet(UpgradeServletOutputStream.this.state, 2)) {
            try {
               channel.shutdownWrites();
               UpgradeServletOutputStream.this.state = UpgradeServletOutputStream.this.state | 4;
               channel.flush();
            } catch (IOException var5) {
               this.handleError(channel, var5);
            }
         } else {
            UpgradeServletOutputStream.this.state = UpgradeServletOutputStream.this.state | 1;
            channel.suspendWrites();

            try {
               UpgradeServletOutputStream.this.listener.onWritePossible();
            } catch (IOException var4) {
               UpgradeServletOutputStream.this.listener.onError(var4);
            }
         }

      }

      private void handleError(StreamSinkChannel channel, IOException e) {
         try {
            UpgradeServletOutputStream.this.listener.onError(e);
         } finally {
            IoUtils.safeClose((Closeable)channel);
         }

      }

      // $FF: synthetic method
      WriteChannelListener(Object x1) {
         this();
      }
   }
}
