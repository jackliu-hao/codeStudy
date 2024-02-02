package javax.mail.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;

public class ByteArrayDataSource implements DataSource {
   private byte[] data;
   private int len = -1;
   private String type;
   private String name = "";

   public ByteArrayDataSource(InputStream is, String type) throws IOException {
      DSByteArrayOutputStream os = new DSByteArrayOutputStream();
      byte[] buf = new byte[8192];

      int len;
      while((len = is.read(buf)) > 0) {
         os.write(buf, 0, len);
      }

      this.data = os.getBuf();
      this.len = os.getCount();
      if (this.data.length - this.len > 262144) {
         this.data = os.toByteArray();
         this.len = this.data.length;
      }

      this.type = type;
   }

   public ByteArrayDataSource(byte[] data, String type) {
      this.data = data;
      this.type = type;
   }

   public ByteArrayDataSource(String data, String type) throws IOException {
      String charset = null;

      try {
         ContentType ct = new ContentType(type);
         charset = ct.getParameter("charset");
      } catch (ParseException var5) {
      }

      charset = MimeUtility.javaCharset(charset);
      if (charset == null) {
         charset = MimeUtility.getDefaultJavaCharset();
      }

      this.data = data.getBytes(charset);
      this.type = type;
   }

   public InputStream getInputStream() throws IOException {
      if (this.data == null) {
         throw new IOException("no data");
      } else {
         if (this.len < 0) {
            this.len = this.data.length;
         }

         return new SharedByteArrayInputStream(this.data, 0, this.len);
      }
   }

   public OutputStream getOutputStream() throws IOException {
      throw new IOException("cannot do this");
   }

   public String getContentType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   static class DSByteArrayOutputStream extends ByteArrayOutputStream {
      public byte[] getBuf() {
         return this.buf;
      }

      public int getCount() {
         return this.count;
      }
   }
}
