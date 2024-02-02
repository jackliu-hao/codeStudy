package io.undertow.websockets.core;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
import io.undertow.server.protocol.framed.FrameHeaderData;
import io.undertow.websockets.core.function.ChannelFunction;
import io.undertow.websockets.core.function.ChannelFunctionFileChannel;
import io.undertow.websockets.core.protocol.version07.Masker;
import io.undertow.websockets.core.protocol.version07.UTF8Checker;
import io.undertow.websockets.extensions.ExtensionFunction;
import io.undertow.websockets.extensions.NoopExtensionFunction;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;

public abstract class StreamSourceFrameChannel extends AbstractFramedStreamSourceChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel> {
   protected final WebSocketFrameType type;
   private boolean finalFragment;
   private final int rsv;
   private final ChannelFunction[] functions;
   private final ExtensionFunction extensionFunction;
   private Masker masker;
   private UTF8Checker checker;

   protected StreamSourceFrameChannel(WebSocketChannel wsChannel, WebSocketFrameType type, PooledByteBuffer pooled, long frameLength) {
      this(wsChannel, type, 0, true, pooled, frameLength, (Masker)null);
   }

   protected StreamSourceFrameChannel(WebSocketChannel wsChannel, WebSocketFrameType type, int rsv, boolean finalFragment, PooledByteBuffer pooled, long frameLength, Masker masker, ChannelFunction... functions) {
      super(wsChannel, pooled, frameLength);
      this.type = type;
      this.finalFragment = finalFragment;
      this.rsv = rsv;
      this.functions = functions;
      this.masker = masker;
      this.checker = null;
      ChannelFunction[] var10 = functions;
      int var11 = functions.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         ChannelFunction func = var10[var12];
         if (func instanceof UTF8Checker) {
            this.checker = (UTF8Checker)func;
         }
      }

      if (rsv > 0) {
         this.extensionFunction = wsChannel.getExtensionFunction();
      } else {
         this.extensionFunction = NoopExtensionFunction.INSTANCE;
      }

   }

   public WebSocketFrameType getType() {
      return this.type;
   }

   public boolean isFinalFragment() {
      return this.finalFragment;
   }

   public int getRsv() {
      return this.rsv;
   }

   int getWebSocketFrameCount() {
      return this.getReadFrameCount();
   }

   protected WebSocketChannel getFramedChannel() {
      return (WebSocketChannel)super.getFramedChannel();
   }

   public WebSocketChannel getWebSocketChannel() {
      return this.getFramedChannel();
   }

   public void finalFrame() {
      this.lastFrame();
      this.finalFragment = true;
   }

   protected void handleHeaderData(FrameHeaderData headerData) {
      super.handleHeaderData(headerData);
      if (((WebSocketFrame)headerData).isFinalFragment()) {
         this.finalFrame();
      }

      if (this.masker != null) {
         this.masker.newFrame(headerData);
      }

      if (this.functions != null) {
         ChannelFunction[] var2 = this.functions;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ChannelFunction func = var2[var4];
            func.newFrame(headerData);
         }
      }

   }

   public final long transferTo(long position, long count, FileChannel target) throws IOException {
      long r;
      if (this.functions != null && this.functions.length > 0) {
         r = super.transferTo(position, count, new ChannelFunctionFileChannel(target, this.functions));
      } else {
         r = super.transferTo(position, count, target);
      }

      return r;
   }

   public final long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      return WebSocketUtils.transfer(this, count, throughBuffer, target);
   }

   public int read(ByteBuffer dst) throws IOException {
      int position = dst.position();
      int r = super.read(dst);
      if (r > 0) {
         this.checker(dst, position, dst.position() - position, false);
      } else if (r == -1) {
         this.checkComplete();
      }

      return r;
   }

   public final long read(ByteBuffer[] dsts) throws IOException {
      return this.read(dsts, 0, dsts.length);
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      Bounds[] old = new Bounds[length];

      for(int i = offset; i < length; ++i) {
         ByteBuffer dst = dsts[i];
         old[i - offset] = new Bounds(dst.position(), dst.limit());
      }

      long b = super.read(dsts, offset, length);
      if (b > 0L) {
         for(int i = offset; i < length; ++i) {
            ByteBuffer dst = dsts[i];
            int oldPos = old[i - offset].position;
            this.afterRead(dst, oldPos, dst.position() - oldPos);
         }
      } else if (b == -1L) {
         this.checkComplete();
      }

      return b;
   }

   private void checkComplete() throws IOException {
      try {
         ChannelFunction[] var1 = this.functions;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            ChannelFunction func = var1[var3];
            func.complete();
         }

      } catch (UnsupportedEncodingException var5) {
         this.getFramedChannel().markReadsBroken(var5);
         throw var5;
      }
   }

   protected void afterRead(ByteBuffer buffer, int position, int length) throws IOException {
      try {
         ChannelFunction[] var4 = this.functions;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ChannelFunction func = var4[var6];
            func.afterRead(buffer, position, length);
         }

         if (this.isComplete()) {
            this.checkComplete();
         }

      } catch (UnsupportedEncodingException var8) {
         this.getFramedChannel().markReadsBroken(var8);
         throw var8;
      }
   }

   protected void checker(ByteBuffer buffer, int position, int length, boolean complete) throws IOException {
      if (this.checker != null) {
         try {
            this.checker.afterRead(buffer, position, length);
            if (complete) {
               try {
                  this.checker.complete();
               } catch (UnsupportedEncodingException var6) {
                  this.getFramedChannel().markReadsBroken(var6);
                  throw var6;
               }
            }

         } catch (UnsupportedEncodingException var7) {
            this.getFramedChannel().markReadsBroken(var7);
            throw var7;
         }
      }
   }

   protected PooledByteBuffer processFrameData(PooledByteBuffer frameData, boolean lastFragmentOfFrame) throws IOException {
      if (this.masker != null) {
         this.masker.afterRead(frameData.getBuffer(), frameData.getBuffer().position(), frameData.getBuffer().remaining());
      }

      try {
         return this.extensionFunction.transformForRead(frameData, this, lastFragmentOfFrame && this.isFinalFragment());
      } catch (IOException var4) {
         this.getWebSocketChannel().markReadsBroken(new WebSocketFrameCorruptedException(var4));
         throw var4;
      } catch (Exception var5) {
         this.getWebSocketChannel().markReadsBroken(new WebSocketFrameCorruptedException(var5));
         throw new IOException(var5);
      }
   }

   private static class Bounds {
      final int position;
      final int limit;

      Bounds(int position, int limit) {
         this.position = position;
         this.limit = limit;
      }
   }
}
