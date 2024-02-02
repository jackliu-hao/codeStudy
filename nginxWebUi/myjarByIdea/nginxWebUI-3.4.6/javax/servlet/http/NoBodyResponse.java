package javax.servlet.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import javax.servlet.ServletOutputStream;

class NoBodyResponse extends HttpServletResponseWrapper {
   private static final ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
   private NoBodyOutputStream noBody = new NoBodyOutputStream();
   private PrintWriter writer;
   private boolean didSetContentLength;
   private boolean usingOutputStream;

   NoBodyResponse(HttpServletResponse r) {
      super(r);
   }

   void setContentLength() {
      if (!this.didSetContentLength) {
         if (this.writer != null) {
            this.writer.flush();
         }

         this.setContentLength(this.noBody.getContentLength());
      }

   }

   public void setContentLength(int len) {
      super.setContentLength(len);
      this.didSetContentLength = true;
   }

   public void setContentLengthLong(long len) {
      super.setContentLengthLong(len);
      this.didSetContentLength = true;
   }

   public void setHeader(String name, String value) {
      super.setHeader(name, value);
      this.checkHeader(name);
   }

   public void addHeader(String name, String value) {
      super.addHeader(name, value);
      this.checkHeader(name);
   }

   public void setIntHeader(String name, int value) {
      super.setIntHeader(name, value);
      this.checkHeader(name);
   }

   public void addIntHeader(String name, int value) {
      super.addIntHeader(name, value);
      this.checkHeader(name);
   }

   private void checkHeader(String name) {
      if ("content-length".equalsIgnoreCase(name)) {
         this.didSetContentLength = true;
      }

   }

   public ServletOutputStream getOutputStream() throws IOException {
      if (this.writer != null) {
         throw new IllegalStateException(lStrings.getString("err.ise.getOutputStream"));
      } else {
         this.usingOutputStream = true;
         return this.noBody;
      }
   }

   public PrintWriter getWriter() throws UnsupportedEncodingException {
      if (this.usingOutputStream) {
         throw new IllegalStateException(lStrings.getString("err.ise.getWriter"));
      } else {
         if (this.writer == null) {
            OutputStreamWriter w = new OutputStreamWriter(this.noBody, this.getCharacterEncoding());
            this.writer = new PrintWriter(w);
         }

         return this.writer;
      }
   }
}
