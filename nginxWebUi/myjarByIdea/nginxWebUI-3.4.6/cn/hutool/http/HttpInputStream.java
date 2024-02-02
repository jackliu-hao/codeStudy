package cn.hutool.http;

import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class HttpInputStream extends InputStream {
   private InputStream in;

   public HttpInputStream(HttpResponse response) {
      this.init(response);
   }

   public int read() throws IOException {
      return this.in.read();
   }

   public int read(byte[] b, int off, int len) throws IOException {
      return this.in.read(b, off, len);
   }

   public long skip(long n) throws IOException {
      return this.in.skip(n);
   }

   public int available() throws IOException {
      return this.in.available();
   }

   public void close() throws IOException {
      this.in.close();
   }

   public synchronized void mark(int readlimit) {
      this.in.mark(readlimit);
   }

   public synchronized void reset() throws IOException {
      this.in.reset();
   }

   public boolean markSupported() {
      return this.in.markSupported();
   }

   private void init(HttpResponse response) {
      try {
         this.in = response.status < 400 ? response.httpConnection.getInputStream() : response.httpConnection.getErrorStream();
      } catch (IOException var4) {
         if (!(var4 instanceof FileNotFoundException)) {
            throw new HttpException(var4);
         }
      }

      if (null == this.in) {
         this.in = new ByteArrayInputStream(StrUtil.format("Error request, response status: {}", new Object[]{response.status}).getBytes());
      } else {
         if (response.isGzip() && !(response.in instanceof GZIPInputStream)) {
            try {
               this.in = new GZIPInputStream(this.in);
            } catch (IOException var3) {
            }
         } else if (response.isDeflate() && !(this.in instanceof InflaterInputStream)) {
            this.in = new InflaterInputStream(this.in, new Inflater(true));
         }

      }
   }
}
