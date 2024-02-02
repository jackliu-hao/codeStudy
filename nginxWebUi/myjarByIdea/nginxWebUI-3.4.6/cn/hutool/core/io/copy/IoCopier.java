package cn.hutool.core.io.copy;

import cn.hutool.core.io.StreamProgress;

public abstract class IoCopier<S, T> {
   protected final int bufferSize;
   protected final long count;
   protected StreamProgress progress;
   protected boolean flushEveryBuffer;

   public IoCopier(int bufferSize, long count, StreamProgress progress) {
      this.bufferSize = bufferSize > 0 ? bufferSize : 8192;
      this.count = count <= 0L ? Long.MAX_VALUE : count;
      this.progress = progress;
   }

   public abstract long copy(S var1, T var2);

   protected int bufferSize(long count) {
      return (int)Math.min((long)this.bufferSize, count);
   }

   public IoCopier<S, T> setFlushEveryBuffer(boolean flushEveryBuffer) {
      this.flushEveryBuffer = flushEveryBuffer;
      return this;
   }
}
