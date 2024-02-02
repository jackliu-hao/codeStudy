package com.mysql.cj.protocol;

import com.mysql.cj.log.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ReadAheadInputStream extends InputStream {
   private static final int DEFAULT_BUFFER_SIZE = 4096;
   private InputStream underlyingStream;
   private byte[] buf;
   protected int endOfCurrentData;
   protected int currentPosition;
   protected boolean doDebug;
   protected Log log;

   private void fill(int readAtLeastTheseManyBytes) throws IOException {
      this.checkClosed();
      this.currentPosition = 0;
      this.endOfCurrentData = this.currentPosition;
      int bytesToRead = Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes);
      int bytesAvailable = this.underlyingStream.available();
      if (bytesAvailable > bytesToRead) {
         bytesToRead = Math.min(this.buf.length - this.currentPosition, bytesAvailable);
      }

      if (this.doDebug) {
         StringBuilder debugBuf = new StringBuilder();
         debugBuf.append("  ReadAheadInputStream.fill(");
         debugBuf.append(readAtLeastTheseManyBytes);
         debugBuf.append("), buffer_size=");
         debugBuf.append(this.buf.length);
         debugBuf.append(", current_position=");
         debugBuf.append(this.currentPosition);
         debugBuf.append(", need to read ");
         debugBuf.append(Math.min(this.buf.length - this.currentPosition, readAtLeastTheseManyBytes));
         debugBuf.append(" bytes to fill request,");
         if (bytesAvailable > 0) {
            debugBuf.append(" underlying InputStream reports ");
            debugBuf.append(bytesAvailable);
            debugBuf.append(" total bytes available,");
         }

         debugBuf.append(" attempting to read ");
         debugBuf.append(bytesToRead);
         debugBuf.append(" bytes.");
         if (this.log != null) {
            this.log.logTrace(debugBuf.toString());
         } else {
            System.err.println(debugBuf.toString());
         }
      }

      int n = this.underlyingStream.read(this.buf, this.currentPosition, bytesToRead);
      if (n > 0) {
         this.endOfCurrentData = n + this.currentPosition;
      }

   }

   private int readFromUnderlyingStreamIfNecessary(byte[] b, int off, int len) throws IOException {
      this.checkClosed();
      int avail = this.endOfCurrentData - this.currentPosition;
      if (this.doDebug) {
         StringBuilder debugBuf = new StringBuilder();
         debugBuf.append("ReadAheadInputStream.readIfNecessary(");
         debugBuf.append(Arrays.toString(b));
         debugBuf.append(",");
         debugBuf.append(off);
         debugBuf.append(",");
         debugBuf.append(len);
         debugBuf.append(")");
         if (avail <= 0) {
            debugBuf.append(" not all data available in buffer, must read from stream");
            if (len >= this.buf.length) {
               debugBuf.append(", amount requested > buffer, returning direct read() from stream");
            }
         }

         if (this.log != null) {
            this.log.logTrace(debugBuf.toString());
         } else {
            System.err.println(debugBuf.toString());
         }
      }

      if (avail <= 0) {
         if (len >= this.buf.length) {
            return this.underlyingStream.read(b, off, len);
         }

         this.fill(len);
         avail = this.endOfCurrentData - this.currentPosition;
         if (avail <= 0) {
            return -1;
         }
      }

      int bytesActuallyRead = avail < len ? avail : len;
      System.arraycopy(this.buf, this.currentPosition, b, off, bytesActuallyRead);
      this.currentPosition += bytesActuallyRead;
      return bytesActuallyRead;
   }

   public synchronized int read(byte[] b, int off, int len) throws IOException {
      this.checkClosed();
      if ((off | len | off + len | b.length - (off + len)) < 0) {
         throw new IndexOutOfBoundsException();
      } else if (len == 0) {
         return 0;
      } else {
         int totalBytesRead = 0;

         do {
            int bytesReadThisRound = this.readFromUnderlyingStreamIfNecessary(b, off + totalBytesRead, len - totalBytesRead);
            if (bytesReadThisRound <= 0) {
               if (totalBytesRead == 0) {
                  totalBytesRead = bytesReadThisRound;
               }
               break;
            }

            totalBytesRead += bytesReadThisRound;
         } while(totalBytesRead < len && this.underlyingStream.available() > 0);

         return totalBytesRead;
      }
   }

   public int read() throws IOException {
      this.checkClosed();
      if (this.currentPosition >= this.endOfCurrentData) {
         this.fill(1);
         if (this.currentPosition >= this.endOfCurrentData) {
            return -1;
         }
      }

      return this.buf[this.currentPosition++] & 255;
   }

   public int available() throws IOException {
      this.checkClosed();
      return this.underlyingStream.available() + (this.endOfCurrentData - this.currentPosition);
   }

   private void checkClosed() throws IOException {
      if (this.buf == null) {
         throw new IOException("Stream closed");
      }
   }

   public ReadAheadInputStream(InputStream toBuffer, boolean debug, Log logTo) {
      this(toBuffer, 4096, debug, logTo);
   }

   public ReadAheadInputStream(InputStream toBuffer, int bufferSize, boolean debug, Log logTo) {
      this.doDebug = false;
      this.underlyingStream = toBuffer;
      this.buf = new byte[bufferSize];
      this.doDebug = debug;
      this.log = logTo;
   }

   public void close() throws IOException {
      if (this.underlyingStream != null) {
         try {
            this.underlyingStream.close();
         } finally {
            this.underlyingStream = null;
            this.buf = null;
            this.log = null;
         }
      }

   }

   public boolean markSupported() {
      return false;
   }

   public long skip(long n) throws IOException {
      this.checkClosed();
      if (n <= 0L) {
         return 0L;
      } else {
         long bytesAvailInBuffer = (long)(this.endOfCurrentData - this.currentPosition);
         if (bytesAvailInBuffer <= 0L) {
            this.fill((int)n);
            bytesAvailInBuffer = (long)(this.endOfCurrentData - this.currentPosition);
            if (bytesAvailInBuffer <= 0L) {
               return 0L;
            }
         }

         long bytesSkipped = bytesAvailInBuffer < n ? bytesAvailInBuffer : n;
         this.currentPosition = (int)((long)this.currentPosition + bytesSkipped);
         return bytesSkipped;
      }
   }
}
