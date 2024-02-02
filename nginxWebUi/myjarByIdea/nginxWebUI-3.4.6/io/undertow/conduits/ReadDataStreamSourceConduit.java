package io.undertow.conduits;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.AbstractServerConnection;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.StreamSourceConduit;

public class ReadDataStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final AbstractServerConnection connection;

   public ReadDataStreamSourceConduit(StreamSourceConduit next, AbstractServerConnection connection) {
      super(next);
      this.connection = connection;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return IoUtils.transfer(new ConduitReadableByteChannel(this), count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      PooledByteBuffer eb = this.connection.getExtraBytes();
      if (eb != null) {
         ByteBuffer buffer = eb.getBuffer();
         int result = Buffers.copy(dst, buffer);
         if (!buffer.hasRemaining()) {
            eb.close();
            this.connection.setExtraBytes((PooledByteBuffer)null);
         }

         return result;
      } else {
         return super.read(dst);
      }
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      PooledByteBuffer eb = this.connection.getExtraBytes();
      if (eb != null) {
         ByteBuffer buffer = eb.getBuffer();
         int result = Buffers.copy(dsts, offs, len, buffer);
         if (!buffer.hasRemaining()) {
            eb.close();
            this.connection.setExtraBytes((PooledByteBuffer)null);
         }

         return (long)result;
      } else {
         return super.read(dsts, offs, len);
      }
   }

   public void resumeReads() {
      if (this.connection.getExtraBytes() != null) {
         this.wakeupReads();
      } else {
         super.resumeReads();
      }

   }

   public void awaitReadable() throws IOException {
      if (this.connection.getExtraBytes() == null) {
         super.awaitReadable();
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      if (this.connection.getExtraBytes() == null) {
         super.awaitReadable(time, timeUnit);
      }
   }
}
