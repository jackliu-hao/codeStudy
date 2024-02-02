package com.mysql.cj.protocol.a;

import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.StringUtils;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class CompressedInputStream extends InputStream {
   private byte[] buffer;
   private InputStream in;
   private Inflater inflater;
   private RuntimeProperty<Boolean> traceProtocol;
   private Log log;
   private byte[] packetHeaderBuffer = new byte[7];
   private int pos = 0;

   public CompressedInputStream(InputStream streamFromServer, RuntimeProperty<Boolean> traceProtocol, Log log) {
      this.traceProtocol = traceProtocol;
      this.log = log;
      this.in = streamFromServer;
      this.inflater = new Inflater();
   }

   public int available() throws IOException {
      return this.buffer == null ? this.in.available() : this.buffer.length - this.pos + this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
      this.buffer = null;
      this.inflater.end();
      this.inflater = null;
      this.traceProtocol = null;
      this.log = null;
   }

   private void getNextPacketFromServer() throws IOException {
      byte[] uncompressedData = null;
      int lengthRead = this.readFully(this.packetHeaderBuffer, 0, 7);
      if (lengthRead < 7) {
         throw new IOException("Unexpected end of input stream");
      } else {
         int compressedPacketLength = (this.packetHeaderBuffer[0] & 255) + ((this.packetHeaderBuffer[1] & 255) << 8) + ((this.packetHeaderBuffer[2] & 255) << 16);
         int uncompressedLength = (this.packetHeaderBuffer[4] & 255) + ((this.packetHeaderBuffer[5] & 255) << 8) + ((this.packetHeaderBuffer[6] & 255) << 16);
         boolean doTrace = (Boolean)this.traceProtocol.getValue();
         if (doTrace) {
            this.log.logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength);
         }

         byte[] tempData;
         byte[] uncompressedData;
         if (uncompressedLength > 0) {
            uncompressedData = new byte[uncompressedLength];
            tempData = new byte[compressedPacketLength];
            this.readFully(tempData, 0, compressedPacketLength);
            this.inflater.reset();
            this.inflater.setInput(tempData);

            try {
               this.inflater.inflate(uncompressedData);
            } catch (DataFormatException var8) {
               throw new IOException("Error while uncompressing packet from server.");
            }
         } else {
            if (doTrace) {
               this.log.logTrace("Packet didn't meet compression threshold, not uncompressing...");
            }

            uncompressedLength = compressedPacketLength;
            uncompressedData = new byte[compressedPacketLength];
            this.readFully(uncompressedData, 0, compressedPacketLength);
         }

         if (doTrace) {
            if (uncompressedLength > 1024) {
               this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, 256));
               tempData = new byte[256];
               System.arraycopy(uncompressedData, uncompressedLength - 256, tempData, 0, 256);
               this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(tempData, 256));
               this.log.logTrace("Large packet dump truncated. Showing first and last 256 bytes.");
            } else {
               this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, uncompressedLength));
            }
         }

         if (this.buffer != null && this.pos < this.buffer.length) {
            if (doTrace) {
               this.log.logTrace("Combining remaining packet with new: ");
            }

            int remaining = this.buffer.length - this.pos;
            byte[] newBuffer = new byte[remaining + uncompressedData.length];
            System.arraycopy(this.buffer, this.pos, newBuffer, 0, remaining);
            System.arraycopy(uncompressedData, 0, newBuffer, remaining, uncompressedData.length);
            uncompressedData = newBuffer;
         }

         this.pos = 0;
         this.buffer = uncompressedData;
      }
   }

   private void getNextPacketIfRequired(int numBytes) throws IOException {
      if (this.buffer == null || this.pos + numBytes > this.buffer.length) {
         this.getNextPacketFromServer();
      }

   }

   public int read() throws IOException {
      try {
         this.getNextPacketIfRequired(1);
      } catch (IOException var2) {
         return -1;
      }

      return this.buffer[this.pos++] & 255;
   }

   public int read(byte[] b) throws IOException {
      return this.read(b, 0, b.length);
   }

   public int read(byte[] b, int off, int len) throws IOException {
      if (b == null) {
         throw new NullPointerException();
      } else if (off >= 0 && off <= b.length && len >= 0 && off + len <= b.length && off + len >= 0) {
         if (len <= 0) {
            return 0;
         } else {
            try {
               this.getNextPacketIfRequired(len);
            } catch (IOException var6) {
               return -1;
            }

            int remainingBufferLength = this.buffer.length - this.pos;
            int consummedBytesLength = Math.min(remainingBufferLength, len);
            System.arraycopy(this.buffer, this.pos, b, off, consummedBytesLength);
            this.pos += consummedBytesLength;
            return consummedBytesLength;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private final int readFully(byte[] b, int off, int len) throws IOException {
      if (len < 0) {
         throw new IndexOutOfBoundsException();
      } else {
         int n;
         int count;
         for(n = 0; n < len; n += count) {
            count = this.in.read(b, off + n, len - n);
            if (count < 0) {
               throw new EOFException();
            }
         }

         return n;
      }
   }

   public long skip(long n) throws IOException {
      long count = 0L;

      for(long i = 0L; i < n; ++i) {
         int bytesRead = this.read();
         if (bytesRead == -1) {
            break;
         }

         ++count;
      }

      return count;
   }
}
