package cn.hutool.http;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

public class HttpResource implements Resource, Serializable {
   private static final long serialVersionUID = 1L;
   private final Resource resource;
   private final String contentType;

   public HttpResource(Resource resource, String contentType) {
      this.resource = (Resource)Assert.notNull(resource, "Resource must be not null !");
      this.contentType = contentType;
   }

   public String getName() {
      return this.resource.getName();
   }

   public URL getUrl() {
      return this.resource.getUrl();
   }

   public InputStream getStream() {
      return this.resource.getStream();
   }

   public String getContentType() {
      return this.contentType;
   }
}
