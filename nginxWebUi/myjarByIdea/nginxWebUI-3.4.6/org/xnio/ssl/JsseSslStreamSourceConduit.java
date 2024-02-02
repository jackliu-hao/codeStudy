package org.xnio.ssl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSourceConduit;

final class JsseSslStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final JsseSslConduitEngine sslEngine;
   private volatile boolean tls;

   protected JsseSslStreamSourceConduit(StreamSourceConduit next, JsseSslConduitEngine sslEngine, boolean tls) {
      super(next);
      if (sslEngine == null) {
         throw Messages.msg.nullParameter("sslEngine");
      } else {
         this.sslEngine = sslEngine;
         this.tls = tls;
      }
   }

   void enableTls() {
      this.tls = true;
      if (this.isReadResumed()) {
         this.wakeupReads();
      }

   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
   }

   public int read(ByteBuffer dst) throws IOException {
      if (!this.tls) {
         int res = super.read(dst);
         if (res == -1) {
            this.terminateReads();
         }

         return res;
      } else if ((this.sslEngine.isDataAvailable() || !this.sslEngine.isInboundClosed()) && !this.sslEngine.isClosed()) {
         boolean attemptToUnwrapFirst;
         synchronized(this.sslEngine.getUnwrapLock()) {
            attemptToUnwrapFirst = this.sslEngine.getUnwrapBuffer().remaining() > 0;
         }

         int readResult;
         if (attemptToUnwrapFirst) {
            readResult = this.sslEngine.unwrap(dst);
            if (readResult > 0) {
               return readResult;
            }
         }

         synchronized(this.sslEngine.getUnwrapLock()) {
            ByteBuffer unwrapBuffer = this.sslEngine.getUnwrapBuffer().compact();

            try {
               readResult = super.read(unwrapBuffer);
            } finally {
               unwrapBuffer.flip();
            }
         }

         int unwrapResult = this.sslEngine.unwrap(dst);
         if (unwrapResult == 0 && readResult == -1) {
            this.terminateReads();
            return -1;
         } else {
            return unwrapResult;
         }
      } else {
         return -1;
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      if (!this.tls) {
         long res = super.read(dsts, offs, len);
         if (res == -1L) {
            this.terminateReads();
         }

         return res;
      } else if (offs >= 0 && offs <= len && len >= 0 && offs + len <= dsts.length) {
         if (this.sslEngine.isClosed() || !this.sslEngine.isDataAvailable() && this.sslEngine.isInboundClosed()) {
            return -1L;
         } else {
            int readResult;
            synchronized(this.sslEngine.getUnwrapLock()) {
               ByteBuffer unwrapBuffer = this.sslEngine.getUnwrapBuffer().compact();

               try {
                  readResult = super.read(unwrapBuffer);
               } finally {
                  unwrapBuffer.flip();
               }
            }

            long unwrapResult = this.sslEngine.unwrap(dsts, offs, len);
            if (unwrapResult == 0L && readResult == -1) {
               this.terminateReads();
               return -1L;
            } else {
               return unwrapResult;
            }
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   public void resumeReads() {
      if (this.tls && this.sslEngine.isFirstHandshake()) {
         super.wakeupReads();
      } else {
         super.resumeReads();
      }

   }

   public void terminateReads() throws IOException {
      if (!this.tls) {
         super.terminateReads();
      } else {
         try {
            this.sslEngine.closeInbound();
         } catch (IOException var4) {
            try {
               super.terminateReads();
            } catch (IOException var3) {
               var3.addSuppressed(var4);
               throw var3;
            }

            throw var4;
         }
      }
   }

   public void awaitReadable() throws IOException {
      if (this.tls) {
         this.sslEngine.awaitCanUnwrap();
      }

      if (!this.sslEngine.isDataAvailable()) {
         super.awaitReadable();
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      if (!this.tls) {
         super.awaitReadable(time, timeUnit);
      } else {
         synchronized(this.sslEngine.getUnwrapLock()) {
            if (this.sslEngine.getUnwrapBuffer().hasRemaining()) {
               return;
            }
         }

         long duration = timeUnit.toNanos(time);
         long awaited = System.nanoTime();
         this.sslEngine.awaitCanUnwrap(time, timeUnit);
         awaited = System.nanoTime() - awaited;
         if (awaited <= duration) {
            super.awaitReadable(duration - awaited, TimeUnit.NANOSECONDS);
         }
      }
   }
}
