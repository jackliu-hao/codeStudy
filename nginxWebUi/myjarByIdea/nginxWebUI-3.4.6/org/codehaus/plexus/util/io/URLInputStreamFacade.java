package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLInputStreamFacade implements InputStreamFacade {
   private final URL url;

   public URLInputStreamFacade(URL url) {
      this.url = url;
   }

   public InputStream getInputStream() throws IOException {
      return this.url.openStream();
   }
}
