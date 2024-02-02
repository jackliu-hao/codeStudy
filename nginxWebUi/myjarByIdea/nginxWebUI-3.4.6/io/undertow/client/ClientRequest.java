package io.undertow.client;

import io.undertow.util.AbstractAttachable;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.Protocols;

public final class ClientRequest extends AbstractAttachable {
   private final HeaderMap requestHeaders = new HeaderMap();
   private String path = "/";
   private HttpString method;
   private HttpString protocol;

   public ClientRequest() {
      this.method = Methods.GET;
      this.protocol = Protocols.HTTP_1_1;
   }

   public HeaderMap getRequestHeaders() {
      return this.requestHeaders;
   }

   public String getPath() {
      return this.path;
   }

   public HttpString getMethod() {
      return this.method;
   }

   public HttpString getProtocol() {
      return this.protocol;
   }

   public ClientRequest setPath(String path) {
      this.path = path;
      return this;
   }

   public ClientRequest setMethod(HttpString method) {
      this.method = method;
      return this;
   }

   public ClientRequest setProtocol(HttpString protocol) {
      this.protocol = protocol;
      return this;
   }

   public String toString() {
      return "ClientRequest{path='" + this.path + '\'' + ", method=" + this.method + ", protocol=" + this.protocol + '}';
   }
}
