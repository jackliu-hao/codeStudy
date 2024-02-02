package io.undertow.server.protocol.ajp;

import io.undertow.UndertowMessages;
import io.undertow.conduits.ConduitListener;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ImmediatePooledByteBuffer;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.StreamSourceConduit;

public class AjpServerRequestConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private static final ByteBuffer READ_BODY_CHUNK;
   private static final int HEADER_LENGTH = 6;
   private static final long STATE_READING = Long.MIN_VALUE;
   private static final long STATE_SEND_REQUIRED = 4611686018427387904L;
   private static final long STATE_FINISHED = 2305843009213693952L;
   private static final long STATE_MASK;
   private final HttpServerExchange exchange;
   private final AjpServerResponseConduit ajpResponseConduit;
   private final ByteBuffer headerBuffer = ByteBuffer.allocateDirect(6);
   private final ConduitListener<? super AjpServerRequestConduit> finishListener;
   private long remaining;
   private long state;
   private long totalRead;

   public AjpServerRequestConduit(StreamSourceConduit delegate, HttpServerExchange exchange, AjpServerResponseConduit ajpResponseConduit, Long size, ConduitListener<? super AjpServerRequestConduit> finishListener) {
      super(delegate);
      this.exchange = exchange;
      this.ajpResponseConduit = ajpResponseConduit;
      this.finishListener = finishListener;
      if (size == null) {
         this.state = 4611686018427387904L;
         this.remaining = -1L;
      } else if (size == 0L) {
         this.state = 2305843009213693952L;
         this.remaining = 0L;
      } else {
         this.state = Long.MIN_VALUE;
         this.remaining = size;
      }

   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      try {
         return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
      } catch (RuntimeException | IOException var7) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var7;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      try {
         return IoUtils.transfer(new ConduitReadableByteChannel(this), count, throughBuffer, target);
      } catch (RuntimeException | IOException var6) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var6;
      }
   }

   public void terminateReads() throws IOException {
      if (!this.exchange.isPersistent() || !Bits.anyAreSet(this.state, 2305843009213693952L)) {
         super.terminateReads();
      }
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      try {
         long total = 0L;

         for(int i = offset; i < length; ++i) {
            while(dsts[i].hasRemaining()) {
               int r = this.read(dsts[i]);
               if (r <= 0 && total > 0L) {
                  return total;
               }

               if (r <= 0) {
                  return (long)r;
               }

               total += (long)r;
            }
         }

         return total;
      } catch (RuntimeException | IOException var8) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var8;
      }
   }

   public int read(ByteBuffer dst) throws IOException {
      try {
         long state = this.state;
         if (Bits.anyAreSet(state, 2305843009213693952L)) {
            return -1;
         } else {
            if (Bits.anyAreSet(state, 4611686018427387904L)) {
               state = this.state = state & STATE_MASK | Long.MIN_VALUE;
               if (this.ajpResponseConduit.isWriteShutdown()) {
                  this.state = 2305843009213693952L;
                  if (this.finishListener != null) {
                     this.finishListener.handleEvent(this);
                  }

                  return -1;
               }

               if (!this.ajpResponseConduit.doGetRequestBodyChunk(READ_BODY_CHUNK.duplicate(), this)) {
                  return 0;
               }
            }

            if (Bits.anyAreSet(state, Long.MIN_VALUE)) {
               return this.doRead(dst, state);
            } else {
               assert 2305843009213693952L == state;

               return -1;
            }
         }
      } catch (RuntimeException | IOException var4) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var4;
      }
   }

   private int doRead(ByteBuffer dst, long state) throws IOException {
      ByteBuffer headerBuffer = this.headerBuffer;
      long headerRead = (long)(6 - headerBuffer.remaining());
      long remaining = this.remaining;
      if (remaining == 0L) {
         this.state = 2305843009213693952L;
         if (this.finishListener != null) {
            this.finishListener.handleEvent(this);
         }

         return -1;
      } else {
         long chunkRemaining;
         int limit;
         int returnValue;
         int read;
         if (headerRead != 6L) {
            limit = ((StreamSourceConduit)this.next).read(headerBuffer);
            if (limit == -1) {
               this.state = 2305843009213693952L;
               if (this.finishListener != null) {
                  this.finishListener.handleEvent(this);
               }

               throw new ClosedChannelException();
            }

            byte b1;
            byte[] data;
            ByteBuffer bb;
            if (headerBuffer.hasRemaining()) {
               if (headerBuffer.remaining() <= 2) {
                  b1 = headerBuffer.get(0);
                  returnValue = headerBuffer.get(1);
                  if (b1 != 18 || returnValue != 52) {
                     throw UndertowMessages.MESSAGES.wrongMagicNumber((b1 & 255) << 8 | returnValue & 255);
                  }

                  b1 = headerBuffer.get(2);
                  returnValue = headerBuffer.get(3);
                  read = (b1 & 255) << 8 | returnValue & 255;
                  if (read == 0) {
                     if (headerBuffer.remaining() < 2) {
                        data = new byte[1];
                        bb = ByteBuffer.wrap(data);
                        bb.put(headerBuffer.get(4));
                        bb.flip();
                        Connectors.ungetRequestBytes(this.exchange, new ImmediatePooledByteBuffer(bb));
                     }

                     this.remaining = 0L;
                     this.state = 2305843009213693952L;
                     if (this.finishListener != null) {
                        this.finishListener.handleEvent(this);
                     }

                     return -1;
                  }
               }

               return 0;
            }

            headerBuffer.flip();
            b1 = headerBuffer.get();
            returnValue = headerBuffer.get();
            if (b1 != 18 || returnValue != 52) {
               throw UndertowMessages.MESSAGES.wrongMagicNumber((b1 & 255) << 8 | returnValue & 255);
            }

            b1 = headerBuffer.get();
            returnValue = headerBuffer.get();
            read = (b1 & 255) << 8 | returnValue & 255;
            if (read == 0) {
               data = new byte[2];
               bb = ByteBuffer.wrap(data);
               bb.put(headerBuffer);
               bb.flip();
               Connectors.ungetRequestBytes(this.exchange, new ImmediatePooledByteBuffer(bb));
               this.remaining = 0L;
               this.state = 2305843009213693952L;
               if (this.finishListener != null) {
                  this.finishListener.handleEvent(this);
               }

               return -1;
            }

            b1 = headerBuffer.get();
            returnValue = headerBuffer.get();
            chunkRemaining = (long)((b1 & 255) << 8 | returnValue & 255);
            if (chunkRemaining == 0L) {
               this.remaining = 0L;
               this.state = 2305843009213693952L;
               if (this.finishListener != null) {
                  this.finishListener.handleEvent(this);
               }

               return -1;
            }
         } else {
            chunkRemaining = this.state & STATE_MASK;
         }

         limit = dst.limit();
         Throwable originalException = null;
         returnValue = 0;
         boolean var26 = false;

         Object suppressed;
         long maxEntitySize;
         label449: {
            try {
               var26 = true;
               if ((long)dst.remaining() > chunkRemaining) {
                  dst.limit((int)((long)dst.position() + chunkRemaining));
               }

               read = ((StreamSourceConduit)this.next).read(dst);
               chunkRemaining -= (long)read;
               if (remaining != -1L) {
                  remaining -= (long)read;
               }

               this.totalRead += (long)read;
               if (remaining != 0L) {
                  if (chunkRemaining == 0L) {
                     headerBuffer.clear();
                     this.state = 4611686018427387904L;
                  } else {
                     this.state = state & ~STATE_MASK | chunkRemaining;
                  }
               }

               returnValue = read;
               var26 = false;
               break label449;
            } catch (Throwable var30) {
               originalException = var30;
               var26 = false;
            } finally {
               if (var26) {
                  Throwable suppressed = originalException;

                  try {
                     this.remaining = remaining;
                     dst.limit(limit);
                     long maxEntitySize = this.exchange.getMaxEntitySize();
                     if (maxEntitySize > 0L && this.totalRead > maxEntitySize) {
                        this.terminateReads();
                        this.exchange.setPersistent(false);
                        suppressed = UndertowMessages.MESSAGES.requestEntityWasTooLarge(maxEntitySize);
                        if (originalException != null) {
                           originalException.addSuppressed((Throwable)suppressed);
                           suppressed = originalException;
                        }
                     }
                  } catch (Throwable var27) {
                     if (originalException != null) {
                        originalException.addSuppressed(var27);
                     } else {
                        suppressed = var27;
                     }
                  }

                  if (suppressed != null) {
                     if (suppressed instanceof RuntimeException) {
                        throw (RuntimeException)suppressed;
                     }

                     if (suppressed instanceof Error) {
                        throw (Error)suppressed;
                     }

                     if (suppressed instanceof IOException) {
                        throw (IOException)suppressed;
                     }
                  }

               }
            }

            suppressed = originalException;

            try {
               this.remaining = remaining;
               dst.limit(limit);
               maxEntitySize = this.exchange.getMaxEntitySize();
               if (maxEntitySize > 0L && this.totalRead > maxEntitySize) {
                  this.terminateReads();
                  this.exchange.setPersistent(false);
                  suppressed = UndertowMessages.MESSAGES.requestEntityWasTooLarge(maxEntitySize);
                  if (originalException != null) {
                     originalException.addSuppressed((Throwable)suppressed);
                     suppressed = originalException;
                  }
               }
            } catch (Throwable var29) {
               if (originalException != null) {
                  originalException.addSuppressed(var29);
               } else {
                  suppressed = var29;
               }
            }

            if (suppressed != null) {
               if (suppressed instanceof RuntimeException) {
                  throw (RuntimeException)suppressed;
               }

               if (suppressed instanceof Error) {
                  throw (Error)suppressed;
               }

               if (suppressed instanceof IOException) {
                  throw (IOException)suppressed;
               }
            }

            return returnValue;
         }

         suppressed = originalException;

         try {
            this.remaining = remaining;
            dst.limit(limit);
            maxEntitySize = this.exchange.getMaxEntitySize();
            if (maxEntitySize > 0L && this.totalRead > maxEntitySize) {
               this.terminateReads();
               this.exchange.setPersistent(false);
               suppressed = UndertowMessages.MESSAGES.requestEntityWasTooLarge(maxEntitySize);
               if (originalException != null) {
                  originalException.addSuppressed((Throwable)suppressed);
                  suppressed = originalException;
               }
            }
         } catch (Throwable var28) {
            if (originalException != null) {
               originalException.addSuppressed(var28);
            } else {
               suppressed = var28;
            }
         }

         if (suppressed != null) {
            if (suppressed instanceof RuntimeException) {
               throw (RuntimeException)suppressed;
            }

            if (suppressed instanceof Error) {
               throw (Error)suppressed;
            }

            if (suppressed instanceof IOException) {
               throw (IOException)suppressed;
            }
         }

         return returnValue;
      }
   }

   public void awaitReadable() throws IOException {
      try {
         if (Bits.anyAreSet(this.state, Long.MIN_VALUE)) {
            ((StreamSourceConduit)this.next).awaitReadable();
         }

      } catch (RuntimeException | IOException var2) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var2;
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      try {
         if (Bits.anyAreSet(this.state, Long.MIN_VALUE)) {
            ((StreamSourceConduit)this.next).awaitReadable(time, timeUnit);
         }

      } catch (RuntimeException | IOException var5) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var5;
      }
   }

   void setReadBodyChunkError(IOException e) {
      IoUtils.safeClose((Closeable)this.exchange.getConnection());
      if (this.isReadResumed()) {
         this.wakeupReads();
      }

   }

   static {
      ByteBuffer readBody = ByteBuffer.allocateDirect(7);
      readBody.put((byte)65);
      readBody.put((byte)66);
      readBody.put((byte)0);
      readBody.put((byte)3);
      readBody.put((byte)6);
      readBody.put((byte)31);
      readBody.put((byte)-6);
      readBody.flip();
      READ_BODY_CHUNK = readBody;
      STATE_MASK = Bits.longBitMask(0, 60);
   }
}
