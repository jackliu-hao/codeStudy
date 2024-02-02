package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.Deflater;
import org.xnio.Buffers;
import org.xnio._private.Messages;
import org.xnio.channels.StreamSourceChannel;

public final class DeflatingStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> implements StreamSinkConduit {
   private static final byte[] NO_BYTES = new byte[0];
   private final Deflater deflater;
   private final ByteBuffer outBuffer;

   public DeflatingStreamSinkConduit(StreamSinkConduit next, Deflater deflater) {
      super(next);
      this.deflater = deflater;
      this.outBuffer = ByteBuffer.allocate(16384);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, (StreamSinkConduit)this);
   }

   public int write(ByteBuffer src) throws IOException {
      ByteBuffer outBuffer = this.outBuffer;
      byte[] outArray = outBuffer.array();
      Deflater deflater = this.deflater;

      assert outBuffer.arrayOffset() == 0;

      int cnt = 0;

      int rem;
      while((rem = src.remaining()) > 0) {
         if (!outBuffer.hasRemaining()) {
            outBuffer.flip();

            try {
               if (((StreamSinkConduit)this.next).write(outBuffer) == 0) {
                  int var10 = cnt;
                  return var10;
               }
            } finally {
               outBuffer.compact();
            }
         }

         int pos = src.position();
         int c1;
         int t;
         int arrayOffset;
         byte[] array;
         if (src.hasArray()) {
            array = src.array();
            arrayOffset = src.arrayOffset();
            deflater.setInput(array, arrayOffset + pos, rem);
            c1 = deflater.getTotalIn();
            int dc = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
            outBuffer.position(outBuffer.position() + dc);
            t = deflater.getTotalIn() - c1;
            src.position(pos + t);
            cnt += t;
         } else {
            array = Buffers.take(src);
            deflater.setInput(array);
            c1 = deflater.getTotalIn();
            arrayOffset = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
            outBuffer.position(outBuffer.position() + arrayOffset);
            t = deflater.getTotalIn() - c1;
            src.position(pos + t);
            cnt += t;
         }
      }

      return cnt;
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      ByteBuffer outBuffer = this.outBuffer;
      byte[] outArray = outBuffer.array();
      Deflater deflater = this.deflater;

      assert outBuffer.arrayOffset() == 0;

      long cnt = 0L;

      for(int i = 0; i < length; ++i) {
         ByteBuffer src = srcs[i + offset];

         int rem;
         while((rem = src.remaining()) > 0) {
            if (!outBuffer.hasRemaining()) {
               outBuffer.flip();

               try {
                  if (((StreamSinkConduit)this.next).write(outBuffer) == 0) {
                     long var15 = cnt;
                     return var15;
                  }
               } finally {
                  outBuffer.compact();
               }
            }

            int pos = src.position();
            int c1;
            int t;
            int arrayOffset;
            byte[] array;
            if (src.hasArray()) {
               array = src.array();
               arrayOffset = src.arrayOffset();
               deflater.setInput(array, arrayOffset + pos, rem);
               c1 = deflater.getTotalIn();
               int dc = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
               outBuffer.position(outBuffer.position() + dc);
               t = deflater.getTotalIn() - c1;
               src.position(pos + t);
               cnt += (long)t;
            } else {
               array = Buffers.take(src);
               deflater.setInput(array);
               c1 = deflater.getTotalIn();
               arrayOffset = deflater.deflate(outArray, outBuffer.position(), outBuffer.remaining());
               outBuffer.position(outBuffer.position() + arrayOffset);
               t = deflater.getTotalIn() - c1;
               src.position(pos + t);
               cnt += (long)t;
            }
         }
      }

      return cnt;
   }

   public boolean flush() throws IOException {
      ByteBuffer outBuffer = this.outBuffer;
      byte[] outArray = outBuffer.array();
      Deflater deflater = this.deflater;

      assert outBuffer.arrayOffset() == 0;

      deflater.setInput(NO_BYTES);

      while(true) {
         int rem = outBuffer.remaining();
         int pos = outBuffer.position();
         int res = deflater.deflate(outArray, pos, rem, 2);
         if (pos == 0 && res == rem) {
            throw Messages.msg.flushSmallBuffer();
         }

         if (res > 0) {
            outBuffer.flip();

            boolean var7;
            try {
               if (((StreamSinkConduit)this.next).write(outBuffer) != 0) {
                  continue;
               }

               var7 = false;
            } finally {
               outBuffer.compact();
            }

            return var7;
         }

         if (deflater.needsInput() && pos == 0) {
            if (deflater.finished()) {
               ((StreamSinkConduit)this.next).terminateWrites();
            }

            return ((StreamSinkConduit)this.next).flush();
         }

         throw Messages.msg.deflaterState();
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public void terminateWrites() throws IOException {
      this.deflater.finish();
   }

   public void truncateWrites() throws IOException {
      this.deflater.finish();
      ((StreamSinkConduit)this.next).truncateWrites();
   }
}
