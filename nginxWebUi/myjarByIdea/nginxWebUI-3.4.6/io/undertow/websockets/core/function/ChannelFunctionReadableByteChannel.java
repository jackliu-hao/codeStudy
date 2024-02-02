package io.undertow.websockets.core.function;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChannelFunctionReadableByteChannel implements ReadableByteChannel {
   private final ChannelFunction[] functions;
   private final ReadableByteChannel channel;

   public ChannelFunctionReadableByteChannel(ReadableByteChannel channel, ChannelFunction... functions) {
      this.channel = channel;
      this.functions = functions;
   }

   public int read(ByteBuffer dst) throws IOException {
      int pos = dst.position();
      int r = 0;
      boolean var15 = false;

      int var4;
      try {
         var15 = true;
         r = this.channel.read(dst);
         var4 = r;
         var15 = false;
      } finally {
         if (var15) {
            if (r > 0) {
               ChannelFunction[] var10 = this.functions;
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  ChannelFunction func = var10[var12];
                  func.afterRead(dst, pos, r);
               }
            }

         }
      }

      if (r > 0) {
         ChannelFunction[] var5 = this.functions;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            ChannelFunction func = var5[var7];
            func.afterRead(dst, pos, r);
         }
      }

      return var4;
   }

   public boolean isOpen() {
      return this.channel.isOpen();
   }

   public void close() throws IOException {
      this.channel.close();
   }
}
