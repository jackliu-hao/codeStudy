package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class RangeStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final long start;
   private final long end;
   private final long originalResponseLength;
   private long written;

   public RangeStreamSinkConduit(StreamSinkConduit next, long start, long end, long originalResponseLength) {
      super(next);
      this.start = start;
      this.end = end;
      this.originalResponseLength = originalResponseLength;
   }

   public int write(ByteBuffer src) throws IOException {
      boolean currentInclude = this.written >= this.start && this.written <= this.end;
      long bytesRemaining = this.written < this.start ? this.start - this.written : (this.written <= this.end ? this.end - this.written + 1L : Long.MAX_VALUE);
      int old;
      if (currentInclude) {
         old = src.limit();
         src.limit((int)Math.min((long)src.position() + bytesRemaining, (long)src.limit()));
         int toConsume = 0;

         int written;
         try {
            written = super.write(src);
            this.written += (long)written;
         } finally {
            if (!src.hasRemaining()) {
               src.limit(old);
               if (src.hasRemaining()) {
                  toConsume = src.remaining();
                  this.written += (long)toConsume;
                  src.position(src.limit());
               }
            } else {
               src.limit(old);
            }

         }

         return written + toConsume;
      } else if ((long)src.remaining() <= bytesRemaining) {
         old = src.remaining();
         this.written += (long)old;
         src.position(src.limit());
         return old;
      } else {
         this.written += bytesRemaining;
         src.position((int)((long)src.position() + bytesRemaining));
         return (int)bytesRemaining + this.write(src);
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      long ret = 0L;

      for(int i = offs; i < offs + len; ++i) {
         ByteBuffer buf = srcs[i];
         if (buf.remaining() > 0) {
            ret += (long)this.write(buf);
            if (buf.hasRemaining()) {
               return ret;
            }
         }
      }

      return ret;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }
}
