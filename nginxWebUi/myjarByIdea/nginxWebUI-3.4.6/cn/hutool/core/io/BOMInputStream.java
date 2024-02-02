package cn.hutool.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class BOMInputStream extends InputStream {
   private final PushbackInputStream in;
   private boolean isInited;
   private final String defaultCharset;
   private String charset;
   private static final int BOM_SIZE = 4;

   public BOMInputStream(InputStream in) {
      this(in, "UTF-8");
   }

   public BOMInputStream(InputStream in, String defaultCharset) {
      this.isInited = false;
      this.in = new PushbackInputStream(in, 4);
      this.defaultCharset = defaultCharset;
   }

   public String getDefaultCharset() {
      return this.defaultCharset;
   }

   public String getCharset() {
      if (!this.isInited) {
         try {
            this.init();
         } catch (IOException var2) {
            throw new IORuntimeException(var2);
         }
      }

      return this.charset;
   }

   public void close() throws IOException {
      this.isInited = true;
      this.in.close();
   }

   public int read() throws IOException {
      this.isInited = true;
      return this.in.read();
   }

   protected void init() throws IOException {
      if (!this.isInited) {
         byte[] bom = new byte[4];
         int n = this.in.read(bom, 0, bom.length);
         int unread;
         if (bom[0] == 0 && bom[1] == 0 && bom[2] == -2 && bom[3] == -1) {
            this.charset = "UTF-32BE";
            unread = n - 4;
         } else if (bom[0] == -1 && bom[1] == -2 && bom[2] == 0 && bom[3] == 0) {
            this.charset = "UTF-32LE";
            unread = n - 4;
         } else if (bom[0] == -17 && bom[1] == -69 && bom[2] == -65) {
            this.charset = "UTF-8";
            unread = n - 3;
         } else if (bom[0] == -2 && bom[1] == -1) {
            this.charset = "UTF-16BE";
            unread = n - 2;
         } else if (bom[0] == -1 && bom[1] == -2) {
            this.charset = "UTF-16LE";
            unread = n - 2;
         } else {
            this.charset = this.defaultCharset;
            unread = n;
         }

         if (unread > 0) {
            this.in.unread(bom, n - unread, unread);
         }

         this.isInited = true;
      }
   }
}
