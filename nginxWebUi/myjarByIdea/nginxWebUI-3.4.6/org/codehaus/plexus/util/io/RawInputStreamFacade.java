package org.codehaus.plexus.util.io;

import java.io.IOException;
import java.io.InputStream;

public class RawInputStreamFacade implements InputStreamFacade {
   final InputStream stream;

   public RawInputStreamFacade(InputStream stream) {
      this.stream = stream;
   }

   public InputStream getInputStream() throws IOException {
      return this.stream;
   }
}
