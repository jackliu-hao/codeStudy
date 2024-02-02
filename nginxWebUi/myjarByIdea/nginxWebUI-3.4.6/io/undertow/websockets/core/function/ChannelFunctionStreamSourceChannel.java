package io.undertow.websockets.core.function;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;
import org.xnio.ChannelListener;
import org.xnio.Option;
import org.xnio.XnioExecutor;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public class ChannelFunctionStreamSourceChannel implements StreamSourceChannel {
   private final StreamSourceChannel channel;
   private final ChannelFunction[] functions;

   public ChannelFunctionStreamSourceChannel(StreamSourceChannel channel, ChannelFunction... functions) {
      this.channel = channel;
      this.functions = functions;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      return this.channel.transferTo(position, count, new ChannelFunctionFileChannel(target, this.functions));
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return target.transferFrom(this, count, throughBuffer);
   }

   public ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter() {
      return this.channel.getReadSetter();
   }

   public ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter() {
      return this.channel.getCloseSetter();
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      long r = 0L;

      for(int a = offset; a < length; ++a) {
         int i = this.read(dsts[a]);
         if (i < 1) {
            break;
         }

         r += (long)i;
      }

      return r;
   }

   public long read(ByteBuffer[] dsts) throws IOException {
      long r = 0L;
      ByteBuffer[] var4 = dsts;
      int var5 = dsts.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ByteBuffer buf = var4[var6];
         int i = this.read(buf);
         if (i < 1) {
            break;
         }

         r += (long)i;
      }

      return r;
   }

   public void suspendReads() {
      this.channel.suspendReads();
   }

   public void resumeReads() {
      this.channel.resumeReads();
   }

   public boolean isReadResumed() {
      return this.channel.isReadResumed();
   }

   public void wakeupReads() {
      this.channel.wakeupReads();
   }

   public void shutdownReads() throws IOException {
      this.channel.shutdownReads();
   }

   public void awaitReadable() throws IOException {
      this.channel.awaitReadable();
   }

   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
      this.channel.awaitReadable(time, timeUnit);
   }

   public XnioExecutor getReadThread() {
      return this.channel.getReadThread();
   }

   public int read(ByteBuffer dst) throws IOException {
      int position = dst.position();
      int r = this.channel.read(dst);
      if (r > 0) {
         this.afterReading(dst, position, r);
      }

      return r;
   }

   public XnioWorker getWorker() {
      return this.channel.getWorker();
   }

   public XnioIoThread getIoThread() {
      return this.channel.getIoThread();
   }

   public boolean supportsOption(Option<?> option) {
      return this.channel.supportsOption(option);
   }

   public <T> T getOption(Option<T> option) throws IOException {
      return this.channel.getOption(option);
   }

   public <T> T setOption(Option<T> option, T value) throws IOException {
      return this.channel.setOption(option, value);
   }

   private void afterReading(ByteBuffer buffer, int position, int length) throws IOException {
      ChannelFunction[] var4 = this.functions;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ChannelFunction func = var4[var6];
         func.afterRead(buffer, position, length);
      }

   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public void close() throws IOException {
      this.channel.close();
   }
}
