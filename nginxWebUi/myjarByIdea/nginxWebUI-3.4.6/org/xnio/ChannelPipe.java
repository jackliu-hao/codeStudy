package org.xnio;

import org.xnio.channels.CloseableChannel;

public final class ChannelPipe<L extends CloseableChannel, R extends CloseableChannel> {
   private final L leftSide;
   private final R rightSide;

   public ChannelPipe(L leftSide, R rightSide) {
      this.rightSide = rightSide;
      this.leftSide = leftSide;
   }

   public L getLeftSide() {
      return this.leftSide;
   }

   public R getRightSide() {
      return this.rightSide;
   }
}
