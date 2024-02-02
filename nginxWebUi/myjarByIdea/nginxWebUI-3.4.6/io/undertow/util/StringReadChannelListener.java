package io.undertow.util;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.XnioByteBufferPool;
import io.undertow.websockets.core.UTF8Output;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.Pool;
import org.xnio.channels.StreamSourceChannel;

public abstract class StringReadChannelListener implements ChannelListener<StreamSourceChannel> {
   private final UTF8Output string = new UTF8Output();
   private final ByteBufferPool bufferPool;

   public StringReadChannelListener(ByteBufferPool bufferPool) {
      this.bufferPool = bufferPool;
   }

   /** @deprecated */
   @Deprecated
   public StringReadChannelListener(Pool<ByteBuffer> bufferPool) {
      this.bufferPool = new XnioByteBufferPool(bufferPool);
   }

   public void setup(StreamSourceChannel channel) {
      PooledByteBuffer resource = this.bufferPool.allocate();
      ByteBuffer buffer = resource.getBuffer();

      try {
         int r = false;

         int r;
         do {
            r = channel.read(buffer);
            if (r == 0) {
               channel.getReadSetter().set(this);
               channel.resumeReads();
            } else if (r == -1) {
               this.stringDone(this.string.extract());
               IoUtils.safeClose((Closeable)channel);
            } else {
               buffer.flip();
               this.string.write(buffer);
            }
         } while(r > 0);
      } catch (IOException var8) {
         this.error(var8);
      } finally {
         resource.close();
      }

   }

   public void handleEvent(StreamSourceChannel channel) {
      PooledByteBuffer resource = this.bufferPool.allocate();
      ByteBuffer buffer = resource.getBuffer();

      try {
         int r = false;

         int r;
         do {
            r = channel.read(buffer);
            if (r == 0) {
               return;
            }

            if (r == -1) {
               this.stringDone(this.string.extract());
               IoUtils.safeClose((Closeable)channel);
            } else {
               buffer.flip();
               this.string.write(buffer);
            }
         } while(r > 0);
      } catch (IOException var8) {
         this.error(var8);
      } finally {
         resource.close();
      }

   }

   protected abstract void stringDone(String var1);

   protected abstract void error(IOException var1);
}
