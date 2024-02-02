package javax.mail.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import javax.mail.internet.SharedInputStream;

public class SharedFileInputStream extends BufferedInputStream implements SharedInputStream {
   private static int defaultBufferSize = 2048;
   protected RandomAccessFile in;
   protected int bufsize;
   protected long bufpos;
   protected long start;
   protected long datalen;
   private boolean master;
   private SharedFile sf;

   private void ensureOpen() throws IOException {
      if (this.in == null) {
         throw new IOException("Stream closed");
      }
   }

   public SharedFileInputStream(File file) throws IOException {
      this(file, defaultBufferSize);
   }

   public SharedFileInputStream(String file) throws IOException {
      this(file, defaultBufferSize);
   }

   public SharedFileInputStream(File file, int size) throws IOException {
      super((InputStream)null);
      this.start = 0L;
      this.master = true;
      if (size <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.init(new SharedFile(file), size);
      }
   }

   public SharedFileInputStream(String file, int size) throws IOException {
      super((InputStream)null);
      this.start = 0L;
      this.master = true;
      if (size <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.init(new SharedFile(file), size);
      }
   }

   private void init(SharedFile sf, int size) throws IOException {
      this.sf = sf;
      this.in = sf.open();
      this.start = 0L;
      this.datalen = this.in.length();
      this.bufsize = size;
      this.buf = new byte[size];
   }

   private SharedFileInputStream(SharedFile sf, long start, long len, int bufsize) {
      super((InputStream)null);
      this.start = 0L;
      this.master = true;
      this.master = false;
      this.sf = sf;
      this.in = sf.open();
      this.start = start;
      this.bufpos = start;
      this.datalen = len;
      this.bufsize = bufsize;
      this.buf = new byte[bufsize];
   }

   private void fill() throws IOException {
      int nsz;
      if (this.markpos < 0) {
         this.pos = 0;
         this.bufpos += (long)this.count;
      } else if (this.pos >= this.buf.length) {
         if (this.markpos > 0) {
            nsz = this.pos - this.markpos;
            System.arraycopy(this.buf, this.markpos, this.buf, 0, nsz);
            this.pos = nsz;
            this.bufpos += (long)this.markpos;
            this.markpos = 0;
         } else if (this.buf.length >= this.marklimit) {
            this.markpos = -1;
            this.pos = 0;
            this.bufpos += (long)this.count;
         } else {
            nsz = this.pos * 2;
            if (nsz > this.marklimit) {
               nsz = this.marklimit;
            }

            byte[] nbuf = new byte[nsz];
            System.arraycopy(this.buf, 0, nbuf, 0, this.pos);
            this.buf = nbuf;
         }
      }

      this.count = this.pos;
      this.in.seek(this.bufpos + (long)this.pos);
      nsz = this.buf.length - this.pos;
      if (this.bufpos - this.start + (long)this.pos + (long)nsz > this.datalen) {
         nsz = (int)(this.datalen - (this.bufpos - this.start + (long)this.pos));
      }

      int n = this.in.read(this.buf, this.pos, nsz);
      if (n > 0) {
         this.count = n + this.pos;
      }

   }

   public synchronized int read() throws IOException {
      this.ensureOpen();
      if (this.pos >= this.count) {
         this.fill();
         if (this.pos >= this.count) {
            return -1;
         }
      }

      return this.buf[this.pos++] & 255;
   }

   private int read1(byte[] b, int off, int len) throws IOException {
      int avail = this.count - this.pos;
      if (avail <= 0) {
         this.fill();
         avail = this.count - this.pos;
         if (avail <= 0) {
            return -1;
         }
      }

      int cnt = avail < len ? avail : len;
      System.arraycopy(this.buf, this.pos, b, off, cnt);
      this.pos += cnt;
      return cnt;
   }

   public synchronized int read(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if ((off | len | off + len | b.length - (off + len)) < 0) {
         throw new IndexOutOfBoundsException();
      } else if (len == 0) {
         return 0;
      } else {
         int n = this.read1(b, off, len);
         if (n <= 0) {
            return n;
         } else {
            while(n < len) {
               int n1 = this.read1(b, off + n, len - n);
               if (n1 <= 0) {
                  break;
               }

               n += n1;
            }

            return n;
         }
      }
   }

   public synchronized long skip(long n) throws IOException {
      this.ensureOpen();
      if (n <= 0L) {
         return 0L;
      } else {
         long avail = (long)(this.count - this.pos);
         if (avail <= 0L) {
            this.fill();
            avail = (long)(this.count - this.pos);
            if (avail <= 0L) {
               return 0L;
            }
         }

         long skipped = avail < n ? avail : n;
         this.pos = (int)((long)this.pos + skipped);
         return skipped;
      }
   }

   public synchronized int available() throws IOException {
      this.ensureOpen();
      return this.count - this.pos + this.in_available();
   }

   private int in_available() throws IOException {
      return (int)(this.start + this.datalen - (this.bufpos + (long)this.count));
   }

   public synchronized void mark(int readlimit) {
      this.marklimit = readlimit;
      this.markpos = this.pos;
   }

   public synchronized void reset() throws IOException {
      this.ensureOpen();
      if (this.markpos < 0) {
         throw new IOException("Resetting to invalid mark");
      } else {
         this.pos = this.markpos;
      }
   }

   public boolean markSupported() {
      return true;
   }

   public void close() throws IOException {
      if (this.in != null) {
         try {
            if (this.master) {
               this.sf.forceClose();
            } else {
               this.sf.close();
            }
         } finally {
            this.sf = null;
            this.in = null;
            this.buf = null;
         }

      }
   }

   public long getPosition() {
      if (this.in == null) {
         throw new RuntimeException("Stream closed");
      } else {
         return this.bufpos + (long)this.pos - this.start;
      }
   }

   public synchronized InputStream newStream(long start, long end) {
      if (this.in == null) {
         throw new RuntimeException("Stream closed");
      } else if (start < 0L) {
         throw new IllegalArgumentException("start < 0");
      } else {
         if (end == -1L) {
            end = this.datalen;
         }

         return new SharedFileInputStream(this.sf, this.start + (long)((int)start), (long)((int)(end - start)), this.bufsize);
      }
   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.close();
   }

   static class SharedFile {
      private int cnt;
      private RandomAccessFile in;

      SharedFile(String file) throws IOException {
         this.in = new RandomAccessFile(file, "r");
      }

      SharedFile(File file) throws IOException {
         this.in = new RandomAccessFile(file, "r");
      }

      public synchronized RandomAccessFile open() {
         ++this.cnt;
         return this.in;
      }

      public synchronized void close() throws IOException {
         if (this.cnt > 0 && --this.cnt <= 0) {
            this.in.close();
         }

      }

      public synchronized void forceClose() throws IOException {
         if (this.cnt > 0) {
            this.cnt = 0;
            this.in.close();
         } else {
            try {
               this.in.close();
            } catch (IOException var2) {
            }
         }

      }

      protected void finalize() throws Throwable {
         super.finalize();
         this.in.close();
      }
   }
}
