package io.undertow.conduits;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.server.protocol.http.HttpServerConnection;
import io.undertow.util.Attachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import io.undertow.util.PooledAdaptor;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.PushBackStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public class ChunkedStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   /** @deprecated */
   @Deprecated
   public static final AttachmentKey<HeaderMap> TRAILERS;
   private final BufferWrapper bufferWrapper;
   private final ConduitListener<? super ChunkedStreamSourceConduit> finishListener;
   private final HttpServerExchange exchange;
   private final Closeable closeable;
   private boolean closed;
   private boolean finishListenerInvoked;
   private long remainingAllowed;
   private final ChunkReader chunkReader;
   private final PushBackStreamSourceConduit channel;

   public ChunkedStreamSourceConduit(StreamSourceConduit next, final PushBackStreamSourceConduit channel, final ByteBufferPool pool, ConduitListener<? super ChunkedStreamSourceConduit> finishListener, Attachable attachable, Closeable closeable) {
      this(next, new BufferWrapper() {
         public PooledByteBuffer allocate() {
            return pool.allocate();
         }

         public void pushBack(PooledByteBuffer pooled) {
            channel.pushBack(new PooledAdaptor(pooled));
         }
      }, finishListener, attachable, (HttpServerExchange)null, closeable, channel);
   }

   public ChunkedStreamSourceConduit(StreamSourceConduit next, final HttpServerExchange exchange, ConduitListener<? super ChunkedStreamSourceConduit> finishListener) {
      this(next, new BufferWrapper() {
         public PooledByteBuffer allocate() {
            return exchange.getConnection().getByteBufferPool().allocate();
         }

         public void pushBack(PooledByteBuffer pooled) {
            ((HttpServerConnection)exchange.getConnection()).ungetRequestBytes(pooled);
         }
      }, finishListener, exchange, exchange, exchange.getConnection(), (PushBackStreamSourceConduit)null);
   }

   protected ChunkedStreamSourceConduit(StreamSourceConduit next, BufferWrapper bufferWrapper, ConduitListener<? super ChunkedStreamSourceConduit> finishListener, Attachable attachable, HttpServerExchange exchange, Closeable closeable, PushBackStreamSourceConduit channel) {
      super(next);
      this.bufferWrapper = bufferWrapper;
      this.finishListener = finishListener;
      this.remainingAllowed = Long.MIN_VALUE;
      this.chunkReader = new ChunkReader(attachable, HttpAttachments.REQUEST_TRAILERS, this);
      this.exchange = exchange;
      this.closeable = closeable;
      this.channel = channel;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      try {
         return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
      } catch (RuntimeException | Error | IOException var7) {
         IoUtils.safeClose(this.closeable);
         throw var7;
      }
   }

   private void updateRemainingAllowed(int written) throws IOException {
      if (this.remainingAllowed == Long.MIN_VALUE) {
         if (this.exchange == null) {
            return;
         }

         long maxEntitySize = this.exchange.getMaxEntitySize();
         if (maxEntitySize <= 0L) {
            return;
         }

         this.remainingAllowed = maxEntitySize;
      }

      this.remainingAllowed -= (long)written;
      if (this.remainingAllowed < 0L) {
         Connectors.terminateRequest(this.exchange);
         this.closed = true;
         this.exchange.setPersistent(false);
         this.finishListener.handleEvent(this);
         throw UndertowMessages.MESSAGES.requestEntityWasTooLarge(this.exchange.getMaxEntitySize());
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      try {
         return IoUtils.transfer(new ConduitReadableByteChannel(this), count, throughBuffer, target);
      } catch (RuntimeException | Error | IOException var6) {
         IoUtils.safeClose(this.closeable);
         throw var6;
      }
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      for(int i = offset; i < length; ++i) {
         if (dsts[i].hasRemaining()) {
            return (long)this.read(dsts[i]);
         }
      }

      return 0L;
   }

   public void terminateReads() throws IOException {
      super.terminateReads();
      if (this.channel != null) {
         this.channel.terminateReads();
      }

      if (!this.isFinished()) {
         this.exchange.setPersistent(false);
         super.terminateReads();
         throw UndertowMessages.MESSAGES.chunkedChannelClosedMidChunk();
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      boolean invokeFinishListener = false;

      int c;
      try {
         long chunkRemaining = this.chunkReader.getChunkRemaining();
         if (chunkRemaining == -1L) {
            if (!this.finishListenerInvoked) {
               invokeFinishListener = true;
            }

            byte var57 = -1;
            return var57;
         }

         if (this.closed) {
            throw new ClosedChannelException();
         }

         PooledByteBuffer pooled = this.bufferWrapper.allocate();
         ByteBuffer buf = pooled.getBuffer();
         boolean free = true;

         try {
            int r = ((StreamSourceConduit)this.next).read(buf);
            buf.flip();
            if (r == -1) {
               throw new ClosedChannelException();
            }

            int originalLimit;
            if (r == 0) {
               originalLimit = 0;
               return originalLimit;
            }

            if (chunkRemaining == 0L) {
               chunkRemaining = this.chunkReader.readChunk(buf);
               if (chunkRemaining <= 0L) {
                  if (buf.hasRemaining()) {
                     free = false;
                  }

                  if (!this.finishListenerInvoked && chunkRemaining < 0L) {
                     invokeFinishListener = true;
                  }

                  originalLimit = (int)chunkRemaining;
                  return originalLimit;
               }
            }

            originalLimit = dst.limit();

            try {
               int read = 0;
               long chunkInBuffer = Math.min((long)buf.remaining(), chunkRemaining);
               int remaining = dst.remaining();
               int old;
               if (chunkInBuffer <= (long)remaining) {
                  if (buf.hasRemaining()) {
                     old = buf.limit();
                     buf.limit((int)Math.min((long)old, (long)buf.position() + chunkInBuffer));

                     try {
                        dst.put(buf);
                     } finally {
                        buf.limit(old);
                     }

                     read = (int)((long)read + chunkInBuffer);
                     chunkRemaining -= chunkInBuffer;
                  }

                  if (chunkRemaining > 0L) {
                     old = dst.limit();

                     try {
                        if (chunkRemaining < (long)dst.remaining()) {
                           dst.limit((int)((long)dst.position() + chunkRemaining));
                        }

                        int c = false;

                        do {
                           c = ((StreamSourceConduit)this.next).read(dst);
                           if (c > 0) {
                              read += c;
                              chunkRemaining -= (long)c;
                           }
                        } while(c > 0 && chunkRemaining > 0L);

                        if (c == -1) {
                           throw new ClosedChannelException();
                        }
                     } finally {
                        dst.limit(old);
                     }
                  } else {
                     free = false;
                  }

                  this.updateRemainingAllowed(read);
                  old = read;
                  return old;
               }

               old = buf.limit();
               buf.limit(buf.position() + remaining);
               dst.put(buf);
               buf.limit(old);
               chunkRemaining -= (long)remaining;
               this.updateRemainingAllowed(remaining);
               free = false;
               c = remaining;
            } finally {
               dst.limit(originalLimit);
            }
         } finally {
            if (chunkRemaining >= 0L) {
               this.chunkReader.setChunkRemaining(chunkRemaining);
            }

            if (!free && buf.hasRemaining()) {
               this.bufferWrapper.pushBack(pooled);
            } else {
               pooled.close();
            }

         }
      } catch (RuntimeException | Error | IOException var55) {
         IoUtils.safeClose(this.closeable);
         throw var55;
      } finally {
         if (invokeFinishListener) {
            this.finishListenerInvoked = true;
            this.finishListener.handleEvent(this);
         }

      }

      return c;
   }

   public boolean isFinished() {
      return this.closed || this.chunkReader.getChunkRemaining() == -1L;
   }

   static {
      TRAILERS = HttpAttachments.REQUEST_TRAILERS;
   }

   interface BufferWrapper {
      PooledByteBuffer allocate();

      void pushBack(PooledByteBuffer var1);
   }
}
