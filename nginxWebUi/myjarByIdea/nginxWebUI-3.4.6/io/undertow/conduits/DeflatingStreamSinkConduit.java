package io.undertow.conduits;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.Headers;
import io.undertow.util.NewInstanceObjectPool;
import io.undertow.util.ObjectPool;
import io.undertow.util.PooledObject;
import io.undertow.util.SimpleObjectPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import org.xnio.Bits;
import org.xnio.IoUtils;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;
import org.xnio.conduits.WriteReadyHandler;

public class DeflatingStreamSinkConduit implements StreamSinkConduit {
   protected volatile Deflater deflater;
   protected final PooledObject<Deflater> pooledObject;
   private final ConduitFactory<StreamSinkConduit> conduitFactory;
   private final HttpServerExchange exchange;
   private StreamSinkConduit next;
   private WriteReadyHandler writeReadyHandler;
   protected PooledByteBuffer currentBuffer;
   private ByteBuffer additionalBuffer;
   private int state;
   private static final int SHUTDOWN = 1;
   private static final int NEXT_SHUTDOWN = 2;
   private static final int FLUSHING_BUFFER = 4;
   private static final int WRITES_RESUMED = 8;
   private static final int CLOSED = 16;
   private static final int WRITTEN_TRAILER = 32;

   public DeflatingStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange) {
      this(conduitFactory, exchange, 8);
   }

   public DeflatingStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, int deflateLevel) {
      this(conduitFactory, exchange, newInstanceDeflaterPool(deflateLevel));
   }

   public DeflatingStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, ObjectPool<Deflater> deflaterPool) {
      this.state = 0;
      this.pooledObject = deflaterPool.allocate();
      this.deflater = (Deflater)this.pooledObject.getObject();
      this.currentBuffer = exchange.getConnection().getByteBufferPool().allocate();
      this.exchange = exchange;
      this.conduitFactory = conduitFactory;
      this.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler(Connectors.getConduitSinkChannel(exchange)));
   }

   public static ObjectPool<Deflater> newInstanceDeflaterPool(int deflateLevel) {
      return new NewInstanceObjectPool(() -> {
         return new Deflater(deflateLevel, true);
      }, Deflater::end);
   }

   public static ObjectPool<Deflater> simpleDeflaterPool(int poolSize, int deflateLevel) {
      return new SimpleObjectPool(poolSize, () -> {
         return new Deflater(deflateLevel, true);
      }, Deflater::reset, Deflater::end);
   }

   public int write(ByteBuffer src) throws IOException {
      if (!Bits.anyAreSet(this.state, 17) && this.currentBuffer != null) {
         try {
            if (!this.performFlushIfRequired()) {
               return 0;
            } else if (src.remaining() == 0) {
               return 0;
            } else {
               if (!this.deflater.needsInput()) {
                  this.deflateData(false);
                  if (!this.deflater.needsInput()) {
                     return 0;
                  }
               }

               byte[] data = new byte[src.remaining()];
               src.get(data);
               this.preDeflate(data);
               this.deflater.setInput(data);
               Connectors.updateResponseBytesSent(this.exchange, (long)(0 - data.length));
               this.deflateData(false);
               return data.length;
            }
         } catch (RuntimeException | Error | IOException var3) {
            this.freeBuffer();
            throw var3;
         }
      } else {
         throw new ClosedChannelException();
      }
   }

   protected void preDeflate(byte[] data) {
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (!Bits.anyAreSet(this.state, 17) && this.currentBuffer != null) {
         try {
            int total = 0;

            for(int i = offset; i < offset + length; ++i) {
               if (srcs[i].hasRemaining()) {
                  int ret = this.write(srcs[i]);
                  total += ret;
                  if (ret == 0) {
                     return (long)total;
                  }
               }
            }

            return (long)total;
         } catch (RuntimeException | Error | IOException var7) {
            this.freeBuffer();
            throw var7;
         }
      } else {
         throw new ClosedChannelException();
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.anyAreSet(this.state, 17)) {
         throw new ClosedChannelException();
      } else {
         return !this.performFlushIfRequired() ? 0L : src.transferTo(position, count, new ConduitWritableByteChannel(this));
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (Bits.anyAreSet(this.state, 17)) {
         throw new ClosedChannelException();
      } else {
         return !this.performFlushIfRequired() ? 0L : IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
      }
   }

   public XnioWorker getWorker() {
      return this.exchange.getConnection().getWorker();
   }

   public void suspendWrites() {
      if (this.next == null) {
         this.state &= -9;
      } else {
         this.next.suspendWrites();
      }

   }

   public boolean isWriteResumed() {
      return this.next == null ? Bits.anyAreSet(this.state, 8) : this.next.isWriteResumed();
   }

   public void wakeupWrites() {
      if (this.next == null) {
         this.resumeWrites();
      } else {
         this.next.wakeupWrites();
      }

   }

   public void resumeWrites() {
      if (this.next == null) {
         this.state |= 8;
         this.queueWriteListener();
      } else {
         this.next.resumeWrites();
      }

   }

   private void queueWriteListener() {
      this.exchange.getConnection().getIoThread().execute(new Runnable() {
         public void run() {
            if (DeflatingStreamSinkConduit.this.writeReadyHandler != null) {
               try {
                  DeflatingStreamSinkConduit.this.writeReadyHandler.writeReady();
               } finally {
                  if (DeflatingStreamSinkConduit.this.next == null && DeflatingStreamSinkConduit.this.isWriteResumed()) {
                     DeflatingStreamSinkConduit.this.queueWriteListener();
                  }

               }
            }

         }
      });
   }

   public void terminateWrites() throws IOException {
      if (this.deflater != null) {
         this.deflater.finish();
      }

      this.state |= 1;
   }

   public boolean isWriteShutdown() {
      return Bits.anyAreSet(this.state, 1);
   }

   public void awaitWritable() throws IOException {
      if (this.next != null) {
         this.next.awaitWritable();
      }
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      if (this.next != null) {
         this.next.awaitWritable(time, timeUnit);
      }
   }

   public XnioIoThread getWriteThread() {
      return this.exchange.getConnection().getIoThread();
   }

   public void setWriteReadyHandler(WriteReadyHandler handler) {
      this.writeReadyHandler = handler;
   }

   public boolean flush() throws IOException {
      if (this.currentBuffer == null) {
         return Bits.anyAreSet(this.state, 2) ? this.next.flush() : true;
      } else {
         try {
            boolean nextCreated = false;

            boolean var2;
            try {
               if (!Bits.anyAreSet(this.state, 1)) {
                  if (Bits.allAreClear(this.state, 4)) {
                     if (this.next == null) {
                        nextCreated = true;
                        this.next = this.createNextChannel();
                     }

                     this.deflateData(true);
                     if (Bits.allAreClear(this.state, 4)) {
                        this.currentBuffer.getBuffer().flip();
                        this.state |= 4;
                     }
                  }

                  if (!this.performFlushIfRequired()) {
                     var2 = false;
                     return var2;
                  }

                  var2 = this.next.flush();
                  return var2;
               }

               if (!Bits.anyAreSet(this.state, 2)) {
                  if (!this.performFlushIfRequired()) {
                     var2 = false;
                     return var2;
                  }

                  if (!this.deflater.finished()) {
                     this.deflateData(false);
                     if (!this.deflater.finished()) {
                        var2 = false;
                        return var2;
                     }
                  }

                  ByteBuffer buffer = this.currentBuffer.getBuffer();
                  if (Bits.allAreClear(this.state, 32)) {
                     this.state |= 32;
                     byte[] data = this.getTrailer();
                     if (data != null) {
                        Connectors.updateResponseBytesSent(this.exchange, (long)data.length);
                        if (this.additionalBuffer != null) {
                           byte[] newData = new byte[this.additionalBuffer.remaining() + data.length];

                           int pos;
                           for(pos = 0; this.additionalBuffer.hasRemaining(); newData[pos++] = this.additionalBuffer.get()) {
                           }

                           byte[] var6 = data;
                           int var7 = data.length;

                           for(int var8 = 0; var8 < var7; ++var8) {
                              byte aData = var6[var8];
                              newData[pos++] = aData;
                           }

                           this.additionalBuffer = ByteBuffer.wrap(newData);
                        } else if (Bits.anyAreSet(this.state, 4) && buffer.capacity() - buffer.remaining() >= data.length) {
                           buffer.compact();
                           buffer.put(data);
                           buffer.flip();
                        } else if (data.length <= buffer.remaining() && !Bits.anyAreSet(this.state, 4)) {
                           buffer.put(data);
                        } else {
                           this.additionalBuffer = ByteBuffer.wrap(data);
                        }
                     }
                  }

                  if (!Bits.anyAreSet(this.state, 4)) {
                     buffer.flip();
                     this.state |= 4;
                     if (this.next == null) {
                        nextCreated = true;
                        this.next = this.createNextChannel();
                     }
                  }

                  boolean var26;
                  if (this.performFlushIfRequired()) {
                     this.state |= 2;
                     this.freeBuffer();
                     this.next.terminateWrites();
                     var26 = this.next.flush();
                     return var26;
                  }

                  var26 = false;
                  return var26;
               }

               var2 = this.next.flush();
            } finally {
               if (nextCreated && Bits.anyAreSet(this.state, 8) && !Bits.anyAreSet(this.state, 2)) {
                  try {
                     this.next.resumeWrites();
                  } catch (Throwable var22) {
                     UndertowLogger.REQUEST_LOGGER.debug("Failed to resume", var22);
                  }
               }

            }

            return var2;
         } catch (RuntimeException | Error | IOException var24) {
            this.freeBuffer();
            throw var24;
         }
      }
   }

   protected byte[] getTrailer() {
      return null;
   }

   private boolean performFlushIfRequired() throws IOException {
      if (Bits.anyAreSet(this.state, 4)) {
         ByteBuffer[] bufs = new ByteBuffer[this.additionalBuffer == null ? 1 : 2];
         long totalLength = 0L;
         bufs[0] = this.currentBuffer.getBuffer();
         totalLength += (long)bufs[0].remaining();
         if (this.additionalBuffer != null) {
            bufs[1] = this.additionalBuffer;
            totalLength += (long)bufs[1].remaining();
         }

         if (totalLength > 0L) {
            long total = 0L;
            long res = 0L;

            do {
               res = this.next.write(bufs, 0, bufs.length);
               total += res;
               if (res == 0L) {
                  return false;
               }
            } while(total < totalLength);
         }

         this.additionalBuffer = null;
         this.currentBuffer.getBuffer().clear();
         this.state &= -5;
      }

      return true;
   }

   private StreamSinkConduit createNextChannel() {
      if (this.deflater.finished() && Bits.allAreSet(this.state, 32)) {
         int remaining = this.currentBuffer.getBuffer().remaining();
         if (this.additionalBuffer != null) {
            remaining += this.additionalBuffer.remaining();
         }

         if (!this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
            this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, Integer.toString(remaining));
         }
      } else {
         this.exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
      }

      return (StreamSinkConduit)this.conduitFactory.create();
   }

   private void deflateData(boolean force) throws IOException {
      boolean nextCreated = false;

      try {
         PooledByteBuffer arrayPooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
         Throwable var4 = null;

         try {
            PooledByteBuffer pooled = this.currentBuffer;
            ByteBuffer outputBuffer = pooled.getBuffer();
            boolean shutdown = Bits.anyAreSet(this.state, 1);
            ByteBuffer buf = arrayPooled.getBuffer();

            while(force || !this.deflater.needsInput() || shutdown && !this.deflater.finished()) {
               int count = this.deflater.deflate(buf.array(), buf.arrayOffset(), buf.remaining(), force ? 2 : 0);
               Connectors.updateResponseBytesSent(this.exchange, (long)count);
               if (count != 0) {
                  int remaining = outputBuffer.remaining();
                  if (remaining > count) {
                     outputBuffer.put(buf.array(), buf.arrayOffset(), count);
                  } else {
                     if (remaining == count) {
                        outputBuffer.put(buf.array(), buf.arrayOffset(), count);
                     } else {
                        outputBuffer.put(buf.array(), buf.arrayOffset(), remaining);
                        this.additionalBuffer = ByteBuffer.allocate(count - remaining);
                        this.additionalBuffer.put(buf.array(), buf.arrayOffset() + remaining, count - remaining);
                        this.additionalBuffer.flip();
                     }

                     outputBuffer.flip();
                     this.state |= 4;
                     if (this.next == null) {
                        nextCreated = true;
                        this.next = this.createNextChannel();
                     }

                     if (!this.performFlushIfRequired()) {
                        return;
                     }
                  }
               } else {
                  force = false;
               }
            }

         } catch (Throwable var28) {
            var4 = var28;
            throw var28;
         } finally {
            if (arrayPooled != null) {
               if (var4 != null) {
                  try {
                     arrayPooled.close();
                  } catch (Throwable var27) {
                     var4.addSuppressed(var27);
                  }
               } else {
                  arrayPooled.close();
               }
            }

         }
      } finally {
         if (nextCreated && Bits.anyAreSet(this.state, 8)) {
            this.next.resumeWrites();
         }

      }
   }

   public void truncateWrites() throws IOException {
      this.freeBuffer();
      this.state |= 16;
      this.next.truncateWrites();
   }

   private void freeBuffer() {
      if (this.currentBuffer != null) {
         this.currentBuffer.close();
         this.currentBuffer = null;
         this.state &= -5;
      }

      if (this.deflater != null) {
         this.deflater = null;
         this.pooledObject.close();
      }

   }
}
