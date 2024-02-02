package org.xnio.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.TimeUnit;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

final class JsseSslStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final JsseSslConduitEngine sslEngine;
   private volatile boolean tls;

   protected JsseSslStreamSinkConduit(StreamSinkConduit next, JsseSslConduitEngine sslEngine, boolean tls) {
      super(next);
      if (sslEngine == null) {
         throw Messages.msg.nullParameter("sslEngine");
      } else {
         this.sslEngine = sslEngine;
         this.tls = tls;
      }
   }

   public void enableTls() {
      this.tls = true;
      if (this.isWriteResumed()) {
         this.wakeupWrites();
      }

   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
   }

   public int write(ByteBuffer src) throws IOException {
      return this.write(src, false);
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return this.write(srcs, offs, len, false);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return this.write(src, true);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return this.write(srcs, offset, length, true);
   }

   private int write(ByteBuffer src, boolean writeFinal) throws IOException {
      if (!this.tls) {
         return writeFinal ? ((StreamSinkConduit)this.next).writeFinal(src) : ((StreamSinkConduit)this.next).write(src);
      } else {
         int wrappedBytes = this.sslEngine.wrap(src);
         if (wrappedBytes > 0) {
            this.writeWrappedBuffer(writeFinal);
         }

         return wrappedBytes;
      }
   }

   private long write(ByteBuffer[] srcs, int offs, int len, boolean writeFinal) throws IOException {
      if (!this.tls) {
         return writeFinal ? super.writeFinal(srcs, offs, len) : super.write(srcs, offs, len);
      } else {
         long wrappedBytes = this.sslEngine.wrap(srcs, offs, len);
         if (wrappedBytes > 0L) {
            this.writeWrappedBuffer(writeFinal);
         }

         return wrappedBytes;
      }
   }

   public void resumeWrites() {
      if (this.tls && this.sslEngine.isFirstHandshake()) {
         super.wakeupWrites();
      } else {
         super.resumeWrites();
      }

   }

   public void terminateWrites() throws IOException {
      if (!this.tls) {
         super.terminateWrites();
      } else {
         try {
            this.sslEngine.closeOutbound();
            this.flush();
         } catch (IOException var4) {
            try {
               super.truncateWrites();
            } catch (IOException var3) {
               var3.addSuppressed(var4);
               throw var3;
            }

            throw var4;
         }
      }
   }

   public void awaitWritable() throws IOException {
      if (this.tls) {
         this.sslEngine.awaitCanWrap();
      }

      super.awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      if (!this.tls) {
         super.awaitWritable(time, timeUnit);
      } else {
         long duration = timeUnit.toNanos(time);
         long awaited = System.nanoTime();
         this.sslEngine.awaitCanWrap(time, timeUnit);
         awaited = System.nanoTime() - awaited;
         if (awaited <= duration) {
            super.awaitWritable(duration - awaited, TimeUnit.NANOSECONDS);
         }
      }
   }

   public void truncateWrites() throws IOException {
      if (this.tls) {
         try {
            this.sslEngine.closeOutbound();
         } finally {
            try {
               super.truncateWrites();
            } catch (IOException var7) {
            }

         }
      }

      super.truncateWrites();
   }

   public boolean flush() throws IOException {
      if (!this.tls) {
         return super.flush();
      } else if (this.sslEngine.isOutboundClosed()) {
         if (this.sslEngine.flush() && this.writeWrappedBuffer(false) && super.flush()) {
            super.terminateWrites();
            return true;
         } else {
            return false;
         }
      } else {
         return this.sslEngine.flush() && this.writeWrappedBuffer(false) && super.flush();
      }
   }

   private boolean writeWrappedBuffer(boolean writeFinal) throws IOException {
      synchronized(this.sslEngine.getWrapLock()) {
         ByteBuffer wrapBuffer = this.sslEngine.getWrappedBuffer();

         while(true) {
            try {
               boolean var4;
               if (!wrapBuffer.flip().hasRemaining()) {
                  if (writeFinal) {
                     this.terminateWrites();
                  }

                  var4 = true;
                  return var4;
               }

               if (writeFinal) {
                  if (super.writeFinal(wrapBuffer) == 0) {
                     var4 = false;
                     return var4;
                  }
               } else if (super.write(wrapBuffer) == 0) {
                  var4 = false;
                  return var4;
               }
            } finally {
               wrapBuffer.compact();
            }
         }
      }
   }
}
