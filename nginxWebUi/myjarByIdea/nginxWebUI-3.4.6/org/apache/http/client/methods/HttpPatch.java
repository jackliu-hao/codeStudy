package org.apache.http.client.methods;

import java.net.URI;

public class HttpPatch extends HttpEntityEnclosingRequestBase {
   public static final String METHOD_NAME = "PATCH";

   public HttpPatch() {
   }

   public HttpPatch(URI uri) {
      this.setURI(uri);
   }

   public HttpPatch(String uri) {
      this.setURI(URI.create(uri));
   }

   public String getMethod() {
      return "PATCH";
   }
}
