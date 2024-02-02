package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class URLDataSource implements DataSource {
   private URL url = null;
   private URLConnection url_conn = null;

   public URLDataSource(URL url) {
      this.url = url;
   }

   public String getContentType() {
      String type = null;

      try {
         if (this.url_conn == null) {
            this.url_conn = this.url.openConnection();
         }
      } catch (IOException var3) {
      }

      if (this.url_conn != null) {
         type = this.url_conn.getContentType();
      }

      if (type == null) {
         type = "application/octet-stream";
      }

      return type;
   }

   public String getName() {
      return this.url.getFile();
   }

   public InputStream getInputStream() throws IOException {
      return this.url.openStream();
   }

   public OutputStream getOutputStream() throws IOException {
      this.url_conn = this.url.openConnection();
      if (this.url_conn != null) {
         this.url_conn.setDoOutput(true);
         return this.url_conn.getOutputStream();
      } else {
         return null;
      }
   }

   public URL getURL() {
      return this.url;
   }
}
