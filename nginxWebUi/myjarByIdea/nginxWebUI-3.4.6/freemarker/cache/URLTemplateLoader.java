package freemarker.cache;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public abstract class URLTemplateLoader implements TemplateLoader {
   private Boolean urlConnectionUsesCaches;

   public Object findTemplateSource(String name) throws IOException {
      URL url = this.getURL(name);
      return url == null ? null : new URLTemplateSource(url, this.getURLConnectionUsesCaches());
   }

   public long getLastModified(Object templateSource) {
      return ((URLTemplateSource)templateSource).lastModified();
   }

   public Reader getReader(Object templateSource, String encoding) throws IOException {
      return new InputStreamReader(((URLTemplateSource)templateSource).getInputStream(), encoding);
   }

   public void closeTemplateSource(Object templateSource) throws IOException {
      ((URLTemplateSource)templateSource).close();
   }

   public Boolean getURLConnectionUsesCaches() {
      return this.urlConnectionUsesCaches;
   }

   public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
      this.urlConnectionUsesCaches = urlConnectionUsesCaches;
   }

   protected abstract URL getURL(String var1);

   protected static String canonicalizePrefix(String prefix) {
      prefix = prefix.replace('\\', '/');
      if (prefix.length() > 0 && !prefix.endsWith("/")) {
         prefix = prefix + "/";
      }

      return prefix;
   }
}
