package org.apache.http.client.methods;

import java.net.URI;

public class HttpPost extends HttpEntityEnclosingRequestBase {
   public static final String METHOD_NAME = "POST";

   public HttpPost() {
   }

   public HttpPost(URI uri) {
      this.setURI(uri);
   }

   public HttpPost(String uri) {
      this.setURI(URI.create(uri));
   }

   public String getMethod() {
      return "POST";
   }
}
