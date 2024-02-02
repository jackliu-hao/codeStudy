package org.apache.http.client.methods;

import java.net.URI;

public class HttpPut extends HttpEntityEnclosingRequestBase {
   public static final String METHOD_NAME = "PUT";

   public HttpPut() {
   }

   public HttpPut(URI uri) {
      this.setURI(uri);
   }

   public HttpPut(String uri) {
      this.setURI(URI.create(uri));
   }

   public String getMethod() {
      return "PUT";
   }
}
