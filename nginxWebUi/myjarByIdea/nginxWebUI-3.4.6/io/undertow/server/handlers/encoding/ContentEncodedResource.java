package io.undertow.server.handlers.encoding;

import io.undertow.server.handlers.resource.Resource;

public class ContentEncodedResource {
   private final Resource resource;
   private final String contentEncoding;

   public ContentEncodedResource(Resource resource, String contentEncoding) {
      this.resource = resource;
      this.contentEncoding = contentEncoding;
   }

   public Resource getResource() {
      return this.resource;
   }

   public String getContentEncoding() {
      return this.contentEncoding;
   }
}
