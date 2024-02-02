package io.undertow.websockets.extensions;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.websockets.core.StreamSinkFrameChannel;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import java.io.IOException;
import java.util.List;

public class CompositeExtensionFunction implements ExtensionFunction {
   private final ExtensionFunction[] delegates;

   private CompositeExtensionFunction(ExtensionFunction... delegates) {
      this.delegates = delegates;
   }

   public static ExtensionFunction compose(List<ExtensionFunction> functions) {
      return null == functions ? NoopExtensionFunction.INSTANCE : compose((ExtensionFunction[])functions.toArray(new ExtensionFunction[functions.size()]));
   }

   public static ExtensionFunction compose(ExtensionFunction... functions) {
      if (functions != null && functions.length != 0) {
         return (ExtensionFunction)(functions.length == 1 ? functions[0] : new CompositeExtensionFunction(functions));
      } else {
         return NoopExtensionFunction.INSTANCE;
      }
   }

   public boolean hasExtensionOpCode() {
      ExtensionFunction[] var1 = this.delegates;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ExtensionFunction delegate = var1[var3];
         if (delegate.hasExtensionOpCode()) {
            return true;
         }
      }

      return false;
   }

   public int writeRsv(int rsv) {
      ExtensionFunction[] var2 = this.delegates;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ExtensionFunction ext = var2[var4];
         rsv = ext.writeRsv(rsv);
      }

      return rsv;
   }

   public PooledByteBuffer transformForWrite(PooledByteBuffer pooledBuffer, StreamSinkFrameChannel channel, boolean lastFrame) throws IOException {
      PooledByteBuffer result = pooledBuffer;
      ExtensionFunction[] var5 = this.delegates;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         ExtensionFunction delegate = var5[var7];
         result = delegate.transformForWrite(result, channel, lastFrame);
      }

      return result;
   }

   public PooledByteBuffer transformForRead(PooledByteBuffer pooledBuffer, StreamSourceFrameChannel channel, boolean lastFragementOfMessage) throws IOException {
      PooledByteBuffer result = pooledBuffer;
      ExtensionFunction[] var5 = this.delegates;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         ExtensionFunction delegate = var5[var7];
         result = delegate.transformForRead(result, channel, lastFragementOfMessage);
      }

      return result;
   }

   public void dispose() {
      ExtensionFunction[] var1 = this.delegates;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ExtensionFunction delegate = var1[var3];
         delegate.dispose();
      }

   }
}
