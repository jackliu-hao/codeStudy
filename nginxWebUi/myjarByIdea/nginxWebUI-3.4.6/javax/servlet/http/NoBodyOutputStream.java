package javax.servlet.http;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

class NoBodyOutputStream extends ServletOutputStream {
   private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
   private static ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
   private int contentLength = 0;

   int getContentLength() {
      return this.contentLength;
   }

   public void write(int b) {
      ++this.contentLength;
   }

   public void write(byte[] buf, int offset, int len) throws IOException {
      if (buf == null) {
         throw new NullPointerException(lStrings.getString("err.io.nullArray"));
      } else if (offset >= 0 && len >= 0 && offset + len <= buf.length) {
         this.contentLength += len;
      } else {
         String msg = lStrings.getString("err.io.indexOutOfBounds");
         Object[] msgArgs = new Object[]{offset, len, buf.length};
         msg = MessageFormat.format(msg, msgArgs);
         throw new IndexOutOfBoundsException(msg);
      }
   }

   public boolean isReady() {
      return false;
   }

   public void setWriteListener(WriteListener writeListener) {
   }
}
