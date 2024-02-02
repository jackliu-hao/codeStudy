package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSinkChannel;

public final class InflatingStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
   private final Inflater inflater;
   private final ByteBuffer buffer;

   public InflatingStreamSourceConduit(StreamSourceConduit next, Inflater inflater) {
      super(next);
      this.inflater = inflater;
      this.buffer = ByteBuffer.allocate(16384);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return Conduits.transfer((StreamSourceConduit)this, count, throughBuffer, (WritableByteChannel)target);
   }

   public int read(ByteBuffer dst) throws IOException {
      int remaining = dst.remaining();
      int position = dst.position();
      Inflater inflater = this.inflater;
      int res;
      byte[] space;
      if (dst.hasArray()) {
         space = dst.array();
         int off = dst.arrayOffset();

         while(true) {
            try {
               res = inflater.inflate(space, off + position, remaining);
            } catch (DataFormatException var9) {
               throw new IOException(var9);
            }

            if (res > 0) {
               dst.position(position + res);
               return res;
            }

            if (inflater.needsDictionary()) {
               throw Messages.msg.inflaterNeedsDictionary();
            }

            ByteBuffer buffer = this.buffer;
            buffer.clear();
            res = ((StreamSourceConduit)this.next).read(buffer);
            if (res <= 0) {
               return res;
            }

            inflater.setInput(buffer.array(), buffer.arrayOffset(), res);
         }
      } else {
         space = new byte[remaining];

         while(true) {
            try {
               res = inflater.inflate(space);
            } catch (DataFormatException var10) {
               throw new IOException(var10);
            }

            if (res > 0) {
               dst.put(space, 0, res);
               return res;
            }

            if (inflater.needsDictionary()) {
               throw Messages.msg.inflaterNeedsDictionary();
            }

            ByteBuffer buffer = this.buffer;
            buffer.clear();
            res = ((StreamSourceConduit)this.next).read(buffer);
            if (res <= 0) {
               return res;
            }

            inflater.setInput(buffer.array(), buffer.arrayOffset(), res);
         }
      }
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      return this.read(dsts, 0, dsts.length);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      for(int i = 0; i < length; ++i) {
         ByteBuffer buffer = dsts[i + offset];
         if (buffer.hasRemaining()) {
            return (long)this.read(buffer);
         }
      }

      return 0L;
   }

   public void terminateReads() throws IOException {
      this.inflater.end();
      ((StreamSourceConduit)this.next).terminateReads();
   }

   public void awaitReadable() throws IOException {
      if (this.inflater.needsInput()) {
         ((StreamSourceConduit)this.next).awaitReadable();
      }
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      if (this.inflater.needsInput()) {
         ((StreamSourceConduit)this.next).awaitReadable(time, timeUnit);
      }
   }
}
