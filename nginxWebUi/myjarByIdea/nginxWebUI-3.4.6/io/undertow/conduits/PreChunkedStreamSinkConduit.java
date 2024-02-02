package io.undertow.conduits;

import io.undertow.UndertowMessages;
import io.undertow.server.protocol.http.HttpAttachments;
import io.undertow.util.Attachable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.Bits;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public class PreChunkedStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final ConduitListener<? super PreChunkedStreamSinkConduit> finishListener;
   private static final int FLAG_WRITES_SHUTDOWN = 1;
   private static final int FLAG_FINISHED = 4;
   int state = 0;
   final ChunkReader<PreChunkedStreamSinkConduit> chunkReader;

   public PreChunkedStreamSinkConduit(StreamSinkConduit next, ConduitListener<? super PreChunkedStreamSinkConduit> finishListener, Attachable attachable) {
      super(next);
      this.chunkReader = new ChunkReader(attachable, HttpAttachments.RESPONSE_TRAILERS, this);
      this.finishListener = finishListener;
   }

   public int write(ByteBuffer src) throws IOException {
      return this.doWrite(src);
   }

   int doWrite(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else if (this.chunkReader.getChunkRemaining() == -1L) {
         throw UndertowMessages.MESSAGES.extraDataWrittenAfterChunkEnd();
      } else if (src.remaining() == 0) {
         return 0;
      } else {
         int oldPos = src.position();
         int oldLimit = src.limit();
         int ret = ((StreamSinkConduit)this.next).write(src);
         if (ret == 0) {
            return ret;
         } else {
            int newPos = src.position();
            src.position(oldPos);
            src.limit(oldPos + ret);

            try {
               do {
                  long chunkRemaining = this.chunkReader.readChunk(src);
                  int remaining;
                  if (chunkRemaining == -1L) {
                     if (src.remaining() == 0) {
                        remaining = ret;
                        return remaining;
                     }

                     throw UndertowMessages.MESSAGES.extraDataWrittenAfterChunkEnd();
                  }

                  if (chunkRemaining == 0L) {
                     remaining = ret;
                     return remaining;
                  }

                  if ((long)src.remaining() >= chunkRemaining) {
                     src.position((int)((long)src.position() + chunkRemaining));
                     remaining = 0;
                  } else {
                     remaining = (int)(chunkRemaining - (long)src.remaining());
                     src.position(src.limit());
                  }

                  this.chunkReader.setChunkRemaining((long)remaining);
               } while(src.hasRemaining());

               return ret;
            } finally {
               src.position(newPos);
               src.limit(oldLimit);
            }
         }
      }
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      for(int i = offset; i < length; ++i) {
         if (srcs[i].hasRemaining()) {
            return (long)this.write(srcs[i]);
         }
      }

      return 0L;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      if (!src.hasRemaining()) {
         this.terminateWrites();
         return 0;
      } else {
         int ret = this.doWrite(src);
         this.terminateWrites();
         return ret;
      }
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         return src.transferTo(position, count, new ConduitWritableByteChannel(this));
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         throw new ClosedChannelException();
      } else {
         return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
      }
   }

   public boolean flush() throws IOException {
      if (Bits.anyAreSet(this.state, 1)) {
         boolean val = ((StreamSinkConduit)this.next).flush();
         if (val && Bits.allAreClear(this.state, 4)) {
            this.invokeFinishListener();
         }

         return val;
      } else {
         return ((StreamSinkConduit)this.next).flush();
      }
   }

   private void invokeFinishListener() {
      this.state |= 4;
      if (this.finishListener != null) {
         this.finishListener.handleEvent(this);
      }

   }

   public void terminateWrites() throws IOException {
      if (!Bits.anyAreSet(this.state, 1)) {
         if (this.chunkReader.getChunkRemaining() != -1L) {
            throw UndertowMessages.MESSAGES.chunkedChannelClosedMidChunk();
         } else {
            this.state |= 1;
         }
      }
   }

   public void awaitWritable() throws IOException {
      ((StreamSinkConduit)this.next).awaitWritable();
   }

   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
      ((StreamSinkConduit)this.next).awaitWritable(time, timeUnit);
   }
}
