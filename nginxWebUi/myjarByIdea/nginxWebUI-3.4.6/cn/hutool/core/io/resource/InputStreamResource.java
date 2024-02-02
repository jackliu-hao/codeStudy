package cn.hutool.core.io.resource;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class InputStreamResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;
   private final InputStream in;
   private final String name;

   public InputStreamResource(InputStream in) {
      this(in, (String)null);
   }

   public InputStreamResource(InputStream in, String name) {
      this.in = in;
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public URL getUrl() {
      return null;
   }

   public InputStream getStream() {
      return this.in;
   }
}
