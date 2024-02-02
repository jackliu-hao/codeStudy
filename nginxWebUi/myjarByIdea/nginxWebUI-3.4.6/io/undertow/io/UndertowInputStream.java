package io.undertow.io;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.xnio.Bits;
import org.xnio.channels.Channels;
import org.xnio.channels.EmptyStreamSourceChannel;
import org.xnio.channels.StreamSourceChannel;

public class UndertowInputStream extends InputStream {
   private final StreamSourceChannel channel;
   private final ByteBufferPool bufferPool;
   private static final int FLAG_CLOSED = 1;
   private static final int FLAG_FINISHED = 2;
   private int state;
   private PooledByteBuffer pooled;

   public UndertowInputStream(HttpServerExchange exchange) {
      if (exchange.isRequestChannelAvailable()) {
         this.channel = exchange.getRequestChannel();
      } else {
         this.channel = new EmptyStreamSourceChannel(exchange.getIoThread());
      }

      this.bufferPool = exchange.getConnection().getByteBufferPool();
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
      if (Thread.currentThread() == this.channel.getIoThread()) {
         throw UndertowMessages.MESSAGES.blockingIoFromIOThread();
      } else if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      } else {
         this.readIntoBuffer();
         if (Bits.anyAreSet(this.state, 2)) {
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
            }

            return copied;
         }
      }
   }

   private void readIntoBuffer() throws IOException {
      if (this.pooled == null && !Bits.anyAreSet(this.state, 2)) {
         this.pooled = this.bufferPool.allocate();
         int res = Channels.readBlocking(this.channel, this.pooled.getBuffer());
         this.pooled.getBuffer().flip();
         if (res == -1) {
            this.state |= 2;
            this.pooled.close();
            this.pooled = null;
         }
      }

   }

   private void readIntoBufferNonBlocking() throws IOException {
      if (this.pooled == null && !Bits.anyAreSet(this.state, 2)) {
         this.pooled = this.bufferPool.allocate();
         int res = this.channel.read(this.pooled.getBuffer());
         if (res == 0) {
            this.pooled.close();
            this.pooled = null;
            return;
         }

         this.pooled.getBuffer().flip();
         if (res == -1) {
            this.state |= 2;
            this.pooled.close();
            this.pooled = null;
         }
      }

   }

   public int available() throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.streamIsClosed();
      } else {
         this.readIntoBufferNonBlocking();
         if (Bits.anyAreSet(this.state, 2)) {
            return -1;
         } else {
            return this.pooled == null ? 0 : this.pooled.getBuffer().remaining();
         }
      }
   }

   public void close() throws IOException {
      if (!Bits.anyAreSet(this.state, 1)) {
         this.state |= 1;

         try {
            while(Bits.allAreClear(this.state, 2)) {
               this.readIntoBuffer();
               if (this.pooled != null) {
                  this.pooled.close();
                  this.pooled = null;
               }
            }
         } finally {
            if (this.pooled != null) {
               this.pooled.close();
               this.pooled = null;
            }

            this.channel.shutdownReads();
            this.state |= 2;
         }

      }
   }
}
