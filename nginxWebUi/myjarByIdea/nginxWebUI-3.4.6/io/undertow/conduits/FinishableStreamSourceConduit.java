package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public final class FinishableStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final ConduitListener<? super FinishableStreamSourceConduit> finishListener;
   private boolean finishCalled = false;

   public FinishableStreamSourceConduit(StreamSourceConduit next, ConduitListener<? super FinishableStreamSourceConduit> finishListener) {
      super(next);
      this.finishListener = finishListener;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long res = 0L;

      long var8;
      try {
         var8 = res = ((StreamSourceConduit)this.next).transferTo(position, count, target);
      } finally {
         this.exitRead(res);
      }

      return var8;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      long res = 0L;

      long var7;
      try {
         var7 = res = ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target);
      } finally {
         this.exitRead(res);
      }

      return var7;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      long res = 0L;

      long var6;
      try {
         var6 = res = ((StreamSourceConduit)this.next).read(dsts, offset, length);
      } finally {
         this.exitRead(res);
      }

      return var6;
   }

   public int read(ByteBuffer dst) throws IOException {
      int res = 0;

      int var3;
      try {
         var3 = res = ((StreamSourceConduit)this.next).read(dst);
      } finally {
         this.exitRead((long)res);
      }

      return var3;
   }

   private void exitRead(long consumed) {
      if (consumed == -1L && !this.finishCalled) {
         this.finishCalled = true;
         this.finishListener.handleEvent(this);
      }

   }
}
