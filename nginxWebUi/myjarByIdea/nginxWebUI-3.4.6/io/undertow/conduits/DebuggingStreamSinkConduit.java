package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class DebuggingStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private static final List<byte[]> data = new CopyOnWriteArrayList();

   public DebuggingStreamSinkConduit(StreamSinkConduit next) {
      super(next);
   }

   public int write(ByteBuffer src) throws IOException {
      int pos = src.position();
      int res = super.write(src);
      if (res > 0) {
         byte[] d = new byte[res];

         for(int i = 0; i < res; ++i) {
            d[i] = src.get(i + pos);
         }

         data.add(d);
      }

      return res;
   }

   public long write(ByteBuffer[] dsts, int offs, int len) throws IOException {
      for(int i = offs; i < len; ++i) {
         if (dsts[i].hasRemaining()) {
            return (long)this.write(dsts[i]);
         }
      }

      return 0L;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public static void dump() {
      for(int i = 0; i < data.size(); ++i) {
         System.out.println("Write Buffer " + i);
         StringBuilder sb = new StringBuilder();

         try {
            Buffers.dump((ByteBuffer)ByteBuffer.wrap((byte[])data.get(i)), sb, 0, 20);
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }

         System.out.println(sb);
         System.out.println();
      }

   }
}
