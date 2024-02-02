package io.undertow.conduits;

import io.undertow.UndertowLogger;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.NewInstanceObjectPool;
import io.undertow.util.ObjectPool;
import io.undertow.util.PooledObject;
import io.undertow.util.SimpleObjectPool;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.StreamSourceConduit;

public class InflatingStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   public static final ConduitWrapper<StreamSourceConduit> WRAPPER = new ConduitWrapper<StreamSourceConduit>() {
      public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
         return new InflatingStreamSourceConduit(exchange, (StreamSourceConduit)factory.create());
      }
   };
   private volatile Inflater inflater;
   private final PooledObject<Inflater> pooledObject;
   private final HttpServerExchange exchange;
   private PooledByteBuffer compressed;
   private PooledByteBuffer uncompressed;
   private boolean nextDone;
   private boolean headerDone;

   public InflatingStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next) {
      this(exchange, next, newInstanceInflaterPool());
   }

   public InflatingStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next, ObjectPool<Inflater> inflaterPool) {
      super(next);
      this.nextDone = false;
      this.headerDone = false;
      this.exchange = exchange;
      this.pooledObject = inflaterPool.allocate();
      this.inflater = (Inflater)this.pooledObject.getObject();
   }

   public static ObjectPool<Inflater> newInstanceInflaterPool() {
      return new NewInstanceObjectPool(() -> {
         return new Inflater(true);
      }, Inflater::end);
   }

   public static ObjectPool<Inflater> simpleInflaterPool(int poolSize) {
      return new SimpleObjectPool(poolSize, () -> {
         return new Inflater(true);
      }, Inflater::reset, Inflater::end);
   }

   public int read(ByteBuffer dst) throws IOException {
      if (this.isReadShutdown()) {
         throw new ClosedChannelException();
      } else {
         int read;
         if (this.uncompressed != null) {
            read = Buffers.copy(dst, this.uncompressed.getBuffer());
            if (!this.uncompressed.getBuffer().hasRemaining()) {
               this.uncompressed.close();
               this.uncompressed = null;
            }

            return read;
         } else {
            while(true) {
               int ret;
               if (this.compressed == null && !this.nextDone) {
                  this.compressed = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
                  ByteBuffer buf = this.compressed.getBuffer();
                  ret = ((StreamSourceConduit)this.next).read(buf);
                  if (ret == -1) {
                     this.nextDone = true;
                     this.compressed.close();
                     this.compressed = null;
                  } else {
                     if (ret == 0) {
                        this.compressed.close();
                        this.compressed = null;
                        return 0;
                     }

                     buf.flip();
                     if (!this.headerDone) {
                        this.headerDone = this.readHeader(buf);
                     }

                     this.inflater.setInput(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining());
                  }
               }

               if (this.nextDone && this.inflater.needsInput() && !this.inflater.finished()) {
                  throw UndertowLogger.ROOT_LOGGER.unexpectedEndOfCompressedInput();
               }

               if (this.nextDone && this.inflater.finished()) {
                  this.done();
                  return -1;
               }

               if (this.inflater.finished() && this.compressed != null) {
                  read = this.inflater.getRemaining();
                  ByteBuffer buf = this.compressed.getBuffer();
                  buf.position(buf.limit() - read);
                  this.readFooter(buf);

                  int res;
                  do {
                     buf.clear();
                     res = ((StreamSourceConduit)this.next).read(buf);
                     buf.flip();
                     if (res == -1) {
                        this.done();
                        this.nextDone = true;
                        return -1;
                     }

                     if (res > 0) {
                        this.readFooter(buf);
                     }
                  } while(res != 0);

                  this.compressed.close();
                  this.compressed = null;
                  return 0;
               }

               if (this.compressed == null) {
                  throw new RuntimeException();
               }

               this.uncompressed = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();

               try {
                  read = this.inflater.inflate(this.uncompressed.getBuffer().array(), this.uncompressed.getBuffer().arrayOffset(), this.uncompressed.getBuffer().limit());
                  this.uncompressed.getBuffer().limit(read);
                  this.dataDeflated(this.uncompressed.getBuffer().array(), this.uncompressed.getBuffer().arrayOffset(), read);
                  if (this.inflater.needsInput()) {
                     this.compressed.close();
                     this.compressed = null;
                  }

                  ret = Buffers.copy(dst, this.uncompressed.getBuffer());
                  if (!this.uncompressed.getBuffer().hasRemaining()) {
                     this.uncompressed.close();
                     this.uncompressed = null;
                  }

                  if (ret > 0) {
                     return ret;
                  }
               } catch (DataFormatException var5) {
                  this.done();
                  throw new IOException(var5);
               }
            }
         }
      }
   }

   protected void readFooter(ByteBuffer buf) throws IOException {
   }

   protected boolean readHeader(ByteBuffer byteBuffer) throws IOException {
      return true;
   }

   protected void dataDeflated(byte[] data, int off, int len) {
   }

   private void done() {
      if (this.compressed != null) {
         this.compressed.close();
      }

      if (this.uncompressed != null) {
         this.uncompressed.close();
      }

      if (this.inflater != null) {
         this.pooledObject.close();
         this.inflater = null;
      }

   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      try {
         return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
      } catch (RuntimeException | Error | IOException var7) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
         throw var7;
      }
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      try {
         return IoUtils.transfer(new ConduitReadableByteChannel(this), count, throughBuffer, target);
      } catch (RuntimeException | Error | IOException var6) {
         IoUtils.safeClose((Closeable)this.exchange.getConnection());
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
      this.done();
      ((StreamSourceConduit)this.next).terminateReads();
   }
}
