package io.undertow.websockets.core.function;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class ChannelFunctionWritableByteChannel implements WritableByteChannel {
   private final ChannelFunction[] functions;
   private final WritableByteChannel channel;

   public ChannelFunctionWritableByteChannel(WritableByteChannel channel, ChannelFunction... functions) {
      this.channel = channel;
      this.functions = functions;
   }

   public int write(ByteBuffer src) throws IOException {
      ChannelFunction[] var2 = this.functions;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChannelFunction func = var2[var4];
         int pos = src.position();
         func.beforeWrite(src, pos, src.limit() - pos);
      }

      return this.channel.write(src);
   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public void close() throws IOException {
      this.channel.close();
   }
}
