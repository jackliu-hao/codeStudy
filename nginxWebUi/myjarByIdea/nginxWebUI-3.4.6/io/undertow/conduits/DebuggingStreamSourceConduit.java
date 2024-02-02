package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.ConduitReadableByteChannel;
import org.xnio.conduits.StreamSourceConduit;

public class DebuggingStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private static final List<byte[]> data = new CopyOnWriteArrayList();

   public DebuggingStreamSourceConduit(StreamSourceConduit next) {
      super(next);
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return target.transferFrom(new ConduitReadableByteChannel(this), position, count);
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return IoUtils.transfer(new ConduitReadableByteChannel(this), count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      int pos = dst.position();
      int res = super.read(dst);
      if (res > 0) {
         byte[] d = new byte[res];

         for(int i = 0; i < res; ++i) {
            d[i] = dst.get(i + pos);
         }

         data.add(d);
      }

      return res;
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      for(int i = offs; i < len; ++i) {
         if (dsts[i].hasRemaining()) {
            return (long)this.read(dsts[i]);
         }
      }

      return 0L;
   }

   public static void dump() {
      for(int i = 0; i < data.size(); ++i) {
         System.out.println("Buffer " + i);
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
