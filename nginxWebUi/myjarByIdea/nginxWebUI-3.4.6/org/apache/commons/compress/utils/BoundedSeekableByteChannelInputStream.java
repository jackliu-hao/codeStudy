package org.apache.commons.compress.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class BoundedSeekableByteChannelInputStream extends BoundedArchiveInputStream {
   private final SeekableByteChannel channel;

   public BoundedSeekableByteChannelInputStream(long start, long remaining, SeekableByteChannel channel) {
      super(start, remaining);
      this.channel = channel;
   }

   protected int read(long pos, ByteBuffer buf) throws IOException {
      int read;
      synchronized(this.channel) {
         this.channel.position(pos);
         read = this.channel.read(buf);
      }

      buf.flip();
      return read;
   }
}
