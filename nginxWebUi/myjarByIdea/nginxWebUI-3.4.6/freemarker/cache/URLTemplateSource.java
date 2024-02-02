package freemarker.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;

class URLTemplateSource {
   private final URL url;
   private URLConnection conn;
   private InputStream inputStream;
   private Boolean useCaches;

   URLTemplateSource(URL url, Boolean useCaches) throws IOException {
      this.url = url;
      this.conn = url.openConnection();
      this.useCaches = useCaches;
      if (useCaches != null) {
         this.conn.setUseCaches(useCaches);
      }

   }

   public boolean equals(Object o) {
      return o instanceof URLTemplateSource ? this.url.equals(((URLTemplateSource)o).url) : false;
   }

   public int hashCode() {
      return this.url.hashCode();
   }

   public String toString() {
      return this.url.toString();
   }

   long lastModified() {
      if (!(this.conn instanceof JarURLConnection)) {
         long lastModified = this.conn.getLastModified();
         return lastModified == -1L && this.url.getProtocol().equals("file") ? (new File(this.url.getFile())).lastModified() : lastModified;
      } else {
         URL jarURL = ((JarURLConnection)this.conn).getJarFileURL();
         if (jarURL.getProtocol().equals("file")) {
            return (new File(jarURL.getFile())).lastModified();
         } else {
            URLConnection jarConn = null;

            long var4;
            try {
               jarConn = jarURL.openConnection();
               long var3 = jarConn.getLastModified();
               return var3;
            } catch (IOException var15) {
               var4 = -1L;
            } finally {
               try {
                  if (jarConn != null) {
                     jarConn.getInputStream().close();
                  }
               } catch (IOException var14) {
               }

            }

            return var4;
         }
      }
   }

   InputStream getInputStream() throws IOException {
      if (this.inputStream != null) {
         try {
            this.inputStream.close();
         } catch (IOException var2) {
         }

         this.conn = this.url.openConnection();
      }

      this.inputStream = this.conn.getInputStream();
      return this.inputStream;
   }

   void close() throws IOException {
      try {
         if (this.inputStream != null) {
            this.inputStream.close();
         } else {
            this.conn.getInputStream().close();
         }
      } finally {
         this.inputStream = null;
         this.conn = null;
      }

   }

   Boolean getUseCaches() {
      return this.useCaches;
   }

   void setUseCaches(boolean useCaches) {
      if (this.conn != null) {
         this.conn.setUseCaches(useCaches);
         this.useCaches = useCaches;
      }

   }
}
