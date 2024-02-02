package io.undertow.conduits;

import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import org.xnio.Bits;
import org.xnio.Buffers;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class AbstractFramedStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final Deque<Frame> frameQueue = new ArrayDeque();
   private long queuedData = 0L;
   private int bufferCount = 0;
   private int state;
   private static final int FLAG_WRITES_TERMINATED = 1;
   private static final int FLAG_DELEGATE_SHUTDOWN = 2;

   protected AbstractFramedStreamSinkConduit(StreamSinkConduit next) {
      super(next);
   }

   protected void queueFrame(FrameCallBack callback, ByteBuffer... data) {
      this.queuedData += Buffers.remaining(data);
      this.bufferCount += data.length;
      this.frameQueue.add(new Frame(callback, data, 0, data.length));
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      return src.transferTo(position, count, new ConduitWritableByteChannel(this));
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
   }

   public int write(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return (int)this.doWrite(new ByteBuffer[]{src}, 0, 1);
      }
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw UndertowMessages.MESSAGES.channelIsClosed();
      } else {
         return this.doWrite(srcs, offs, len);
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offs, int len) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offs, len);
   }

   private long doWrite(ByteBuffer[] additionalData, int offs, int len) throws IOException {
      ByteBuffer[] buffers = new ByteBuffer[this.bufferCount + (additionalData == null ? 0 : len)];
      int count = 0;
      Iterator var6 = this.frameQueue.iterator();

      while(var6.hasNext()) {
         Frame frame = (Frame)var6.next();

         for(int i = frame.offs; i < frame.offs + frame.len; ++i) {
            buffers[count++] = frame.data[i];
         }
      }

      if (additionalData != null) {
         for(int i = offs; i < offs + len; ++i) {
            buffers[count++] = additionalData[i];
         }
      }

      try {
         long written = ((StreamSinkConduit)this.next).write(buffers, 0, buffers.length);
         if (written > this.queuedData) {
            this.queuedData = 0L;
         } else {
            this.queuedData -= written;
         }

         long toAllocate = written;

         for(Frame frame = (Frame)this.frameQueue.peek(); frame != null; frame = (Frame)this.frameQueue.peek()) {
            if (frame.remaining > toAllocate) {
               frame.remaining -= toAllocate;
               return 0L;
            }

            this.frameQueue.poll();
            FrameCallBack cb = frame.callback;
            if (cb != null) {
               cb.done();
            }

            this.bufferCount -= frame.len;
            toAllocate -= frame.remaining;
         }

         return toAllocate;
      } catch (RuntimeException | Error | IOException var16) {
         IOException ioe = var16 instanceof IOException ? (IOException)var16 : new IOException(var16);

         try {
            Iterator var20 = this.frameQueue.iterator();

            while(var20.hasNext()) {
               Frame frame = (Frame)var20.next();
               FrameCallBack cb = frame.callback;
               if (cb != null) {
                  cb.failed(ioe);
               }
            }

            this.frameQueue.clear();
            this.bufferCount = 0;
            this.queuedData = 0L;
         } finally {
            ;
         }

         throw var16;
      }
   }

   protected long queuedDataLength() {
      return this.queuedData;
   }

   public void terminateWrites() throws IOException {
      if (!Bits.anyAreSet(this.state, 1)) {
         this.queueCloseFrames();
         this.state |= 1;
         if (this.queuedData == 0L) {
            this.state |= 2;
            this.doTerminateWrites();
            this.finished();
         }

      }
   }

   protected void doTerminateWrites() throws IOException {
      ((StreamSinkConduit)this.next).terminateWrites();
   }

   protected boolean flushQueuedData() throws IOException {
      if (this.queuedData > 0L) {
         this.doWrite((ByteBuffer[])null, 0, 0);
      }

      if (this.queuedData > 0L) {
         return false;
      } else {
         if (Bits.anyAreSet(this.state, 1) && Bits.allAreClear(this.state, 2)) {
            this.doTerminateWrites();
            this.state |= 2;
            this.finished();
         }

         return ((StreamSinkConduit)this.next).flush();
      }
   }

   public void truncateWrites() throws IOException {
      Iterator var1 = this.frameQueue.iterator();

      while(var1.hasNext()) {
         Frame frame = (Frame)var1.next();
         FrameCallBack cb = frame.callback;
         if (cb != null) {
            cb.failed(UndertowMessages.MESSAGES.channelIsClosed());
         }
      }

   }

   protected boolean isWritesTerminated() {
      return Bits.anyAreSet(this.state, 1);
   }

   protected void queueCloseFrames() {
   }

   protected void finished() {
   }

   protected static class PooledBuffersFrameCallback implements FrameCallBack {
      private final PooledByteBuffer[] buffers;

      public PooledBuffersFrameCallback(PooledByteBuffer... buffers) {
         this.buffers = buffers;
      }

      public void done() {
         PooledByteBuffer[] var1 = this.buffers;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            PooledByteBuffer buffer = var1[var3];
            buffer.close();
         }

      }

      public void failed(IOException e) {
         this.done();
      }
   }

   protected static class PooledBufferFrameCallback implements FrameCallBack {
      private final PooledByteBuffer buffer;

      public PooledBufferFrameCallback(PooledByteBuffer buffer) {
         this.buffer = buffer;
      }

      public void done() {
         this.buffer.close();
      }

      public void failed(IOException e) {
         this.buffer.close();
      }
   }

   private static class Frame {
      final FrameCallBack callback;
      final ByteBuffer[] data;
      final int offs;
      final int len;
      long remaining;

      private Frame(FrameCallBack callback, ByteBuffer[] data, int offs, int len) {
         this.callback = callback;
         this.data = data;
         this.offs = offs;
         this.len = len;
         this.remaining = Buffers.remaining(data, offs, len);
      }

      // $FF: synthetic method
      Frame(FrameCallBack x0, ByteBuffer[] x1, int x2, int x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }

   public interface FrameCallBack {
      void done();

      void failed(IOException var1);
   }
}
