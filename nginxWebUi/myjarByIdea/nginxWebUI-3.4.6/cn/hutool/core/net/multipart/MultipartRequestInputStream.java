package cn.hutool.core.net.multipart;

import cn.hutool.core.io.FastByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class MultipartRequestInputStream extends BufferedInputStream {
   protected byte[] boundary;
   protected UploadFileHeader lastHeader;

   public MultipartRequestInputStream(InputStream in) {
      super(in);
   }

   public byte readByte() throws IOException {
      int i = super.read();
      if (i == -1) {
         throw new IOException("End of HTTP request stream reached");
      } else {
         return (byte)i;
      }
   }

   public void skipBytes(long i) throws IOException {
      long len = super.skip(i);
      if (len != i) {
         throw new IOException("Unable to skip data in HTTP request");
      }
   }

   public byte[] readBoundary() throws IOException {
      ByteArrayOutputStream boundaryOutput = new ByteArrayOutputStream(1024);

      byte b;
      while((b = this.readByte()) <= 32) {
      }

      boundaryOutput.write(b);

      while((b = this.readByte()) != 13) {
         boundaryOutput.write(b);
      }

      if (boundaryOutput.size() == 0) {
         throw new IOException("Problems with parsing request: invalid boundary");
      } else {
         this.skipBytes(1L);
         this.boundary = new byte[boundaryOutput.size() + 2];
         System.arraycopy(boundaryOutput.toByteArray(), 0, this.boundary, 2, this.boundary.length - 2);
         this.boundary[0] = 13;
         this.boundary[1] = 10;
         return this.boundary;
      }
   }

   public UploadFileHeader getLastHeader() {
      return this.lastHeader;
   }

   public UploadFileHeader readDataHeader(Charset encoding) throws IOException {
      String dataHeader = this.readDataHeaderString(encoding);
      if (dataHeader != null) {
         this.lastHeader = new UploadFileHeader(dataHeader);
      } else {
         this.lastHeader = null;
      }

      return this.lastHeader;
   }

   protected String readDataHeaderString(Charset charset) throws IOException {
      ByteArrayOutputStream data = new ByteArrayOutputStream();

      while(true) {
         byte b;
         while((b = this.readByte()) == 13) {
            this.mark(4);
            this.skipBytes(1L);
            int i = this.read();
            if (i == -1) {
               return null;
            }

            if (i == 13) {
               this.reset();
               this.skipBytes(3L);
               return charset == null ? data.toString() : data.toString(charset.name());
            }

            this.reset();
            data.write(b);
         }

         data.write(b);
      }
   }

   public String readString(Charset charset) throws IOException {
      FastByteArrayOutputStream out = new FastByteArrayOutputStream();
      this.copy(out);
      return out.toString(charset);
   }

   public long copy(OutputStream out) throws IOException {
      long count = 0L;

      while(true) {
         byte b = this.readByte();
         if (this.isBoundary(b)) {
            return count;
         }

         out.write(b);
         ++count;
      }
   }

   public long copy(OutputStream out, long limit) throws IOException {
      long count = 0L;

      do {
         byte b = this.readByte();
         if (this.isBoundary(b)) {
            break;
         }

         out.write(b);
         ++count;
      } while(count <= limit);

      return count;
   }

   public long skipToBoundary() throws IOException {
      long count = 0L;

      byte b;
      do {
         b = this.readByte();
         ++count;
      } while(!this.isBoundary(b));

      return count;
   }

   public boolean isBoundary(byte b) throws IOException {
      int boundaryLen = this.boundary.length;
      this.mark(boundaryLen + 1);
      int bpos = 0;

      do {
         if (b != this.boundary[bpos]) {
            this.reset();
            return false;
         }

         b = this.readByte();
         ++bpos;
      } while(bpos != boundaryLen);

      return true;
   }
}
