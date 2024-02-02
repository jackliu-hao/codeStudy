package io.undertow.client.http;

import io.undertow.client.ClientResponse;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

final class HttpResponseBuilder {
   private final ResponseParseState parseState = new ResponseParseState();
   private int statusCode;
   private HttpString protocol;
   private String reasonPhrase;
   private final HeaderMap responseHeaders = new HeaderMap();

   public ResponseParseState getParseState() {
      return this.parseState;
   }

   HeaderMap getResponseHeaders() {
      return this.responseHeaders;
   }

   int getStatusCode() {
      return this.statusCode;
   }

   void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

   String getReasonPhrase() {
      return this.reasonPhrase;
   }

   void setReasonPhrase(String reasonPhrase) {
      this.reasonPhrase = reasonPhrase;
   }

   HttpString getProtocol() {
      return this.protocol;
   }

   void setProtocol(HttpString protocol) {
      this.protocol = protocol;
   }

   public ClientResponse build() {
      return new ClientResponse(this.statusCode, this.reasonPhrase, this.protocol, this.responseHeaders);
   }
}
