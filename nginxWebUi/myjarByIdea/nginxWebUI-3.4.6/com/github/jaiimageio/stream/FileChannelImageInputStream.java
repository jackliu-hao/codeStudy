package com.github.jaiimageio.stream;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import javax.imageio.stream.ImageInputStreamImpl;

public class FileChannelImageInputStream extends ImageInputStreamImpl {
   private FileChannel channel;
   private MappedByteBuffer mappedBuffer;
   private long mappedPos;
   private long mappedUpperBound;

   public FileChannelImageInputStream(FileChannel channel) throws IOException {
      if (channel == null) {
         throw new IllegalArgumentException("channel == null");
      } else if (!channel.isOpen()) {
         throw new IllegalArgumentException("channel.isOpen() == false");
      } else {
         this.channel = channel;
         long channelPosition = channel.position();
         this.streamPos = this.flushedPos = channelPosition;
         long fullSize = channel.size() - channelPosition;
         long mappedSize = Math.min(fullSize, 2147483647L);
         this.mappedPos = 0L;
         this.mappedUpperBound = this.mappedPos + mappedSize;
         this.mappedBuffer = channel.map(MapMode.READ_ONLY, channelPosition, mappedSize);
      }
   }

   private MappedByteBuffer getMappedBuffer(int len) throws IOException {
      if (this.streamPos < this.mappedPos || this.streamPos + (long)len >= this.mappedUpperBound) {
         this.mappedPos = this.streamPos;
         long mappedSize = Math.min(this.channel.size() - this.mappedPos, 2147483647L);
         this.mappedUpperBound = this.mappedPos + mappedSize;
         this.mappedBuffer = this.channel.map(MapMode.READ_ONLY, this.mappedPos, mappedSize);
         this.mappedBuffer.order(super.getByteOrder());
      }

      return this.mappedBuffer;
   }

   public int read() throws IOException {
      this.checkClosed();
      this.bitOffset = 0;
      ByteBuffer byteBuffer = this.getMappedBuffer(1);
      if (byteBuffer.remaining() < 1) {
         return -1;
      } else {
         int value = byteBuffer.get() & 255;
         ++this.streamPos;
         return value;
      }
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= b.length) {
         if (len == 0) {
            return 0;
         } else {
            this.checkClosed();
            this.bitOffset = 0;
            ByteBuffer byteBuffer = this.getMappedBuffer(len);
            int numBytesRemaining = byteBuffer.remaining();
            if (numBytesRemaining < 1) {
               return -1;
            } else {
               if (len > numBytesRemaining) {
                  len = numBytesRemaining;
               }

               byteBuffer.get(b, off, len);
               this.streamPos += (long)len;
               return len;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > b.length");
      }
   }

   public void close() throws IOException {
      super.close();
      this.channel = null;
   }

   public void readFully(char[] c, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= c.length) {
         if (len != 0) {
            int byteLen = 2 * len;
            ByteBuffer byteBuffer = this.getMappedBuffer(byteLen);
            if (byteBuffer.remaining() < byteLen) {
               throw new EOFException();
            } else {
               CharBuffer viewBuffer = byteBuffer.asCharBuffer();
               viewBuffer.get(c, off, len);
               this.seek(this.streamPos + (long)byteLen);
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length");
      }
   }

   public void readFully(short[] s, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= s.length) {
         if (len != 0) {
            int byteLen = 2 * len;
            ByteBuffer byteBuffer = this.getMappedBuffer(byteLen);
            if (byteBuffer.remaining() < byteLen) {
               throw new EOFException();
            } else {
               ShortBuffer viewBuffer = byteBuffer.asShortBuffer();
               viewBuffer.get(s, off, len);
               this.seek(this.streamPos + (long)byteLen);
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length");
      }
   }

   public void readFully(int[] i, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= i.length) {
         if (len != 0) {
            int byteLen = 4 * len;
            ByteBuffer byteBuffer = this.getMappedBuffer(byteLen);
            if (byteBuffer.remaining() < byteLen) {
               throw new EOFException();
            } else {
               IntBuffer viewBuffer = byteBuffer.asIntBuffer();
               viewBuffer.get(i, off, len);
               this.seek(this.streamPos + (long)byteLen);
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length");
      }
   }

   public void readFully(long[] l, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= l.length) {
         if (len != 0) {
            int byteLen = 8 * len;
            ByteBuffer byteBuffer = this.getMappedBuffer(byteLen);
            if (byteBuffer.remaining() < byteLen) {
               throw new EOFException();
            } else {
               LongBuffer viewBuffer = byteBuffer.asLongBuffer();
               viewBuffer.get(l, off, len);
               this.seek(this.streamPos + (long)byteLen);
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length");
      }
   }

   public void readFully(float[] f, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= f.length) {
         if (len != 0) {
            int byteLen = 4 * len;
            ByteBuffer byteBuffer = this.getMappedBuffer(byteLen);
            if (byteBuffer.remaining() < byteLen) {
               throw new EOFException();
            } else {
               FloatBuffer viewBuffer = byteBuffer.asFloatBuffer();
               viewBuffer.get(f, off, len);
               this.seek(this.streamPos + (long)byteLen);
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length");
      }
   }

   public void readFully(double[] d, int off, int len) throws IOException {
      if (off >= 0 && len >= 0 && off + len <= d.length) {
         if (len != 0) {
            int byteLen = 8 * len;
            ByteBuffer byteBuffer = this.getMappedBuffer(byteLen);
            if (byteBuffer.remaining() < byteLen) {
               throw new EOFException();
            } else {
               DoubleBuffer viewBuffer = byteBuffer.asDoubleBuffer();
               viewBuffer.get(d, off, len);
               this.seek(this.streamPos + (long)byteLen);
            }
         }
      } else {
         throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length");
      }
   }

   public long length() {
      long length = -1L;

      try {
         length = this.channel.size();
      } catch (IOException var4) {
      }

      return length;
   }

   public void seek(long pos) throws IOException {
      super.seek(pos);
      if (pos >= this.mappedPos && pos < this.mappedUpperBound) {
         this.mappedBuffer.position((int)(pos - this.mappedPos));
      } else {
         int len = (int)Math.min(this.channel.size() - pos, 2147483647L);
         this.mappedBuffer = this.getMappedBuffer(len);
      }

   }

   public void setByteOrder(ByteOrder networkByteOrder) {
      super.setByteOrder(networkByteOrder);
      this.mappedBuffer.order(networkByteOrder);
   }
}
