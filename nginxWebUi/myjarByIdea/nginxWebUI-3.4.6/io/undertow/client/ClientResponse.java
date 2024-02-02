package io.undertow.client;

import io.undertow.util.AbstractAttachable;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

public final class ClientResponse extends AbstractAttachable {
   private final HeaderMap responseHeaders;
   private final int responseCode;
   private final String status;
   private final HttpString protocol;

   public ClientResponse(int responseCode, String status, HttpString protocol) {
      this.responseCode = responseCode;
      this.status = status;
      this.protocol = protocol;
      this.responseHeaders = new HeaderMap();
   }

   public ClientResponse(int responseCode, String status, HttpString protocol, HeaderMap headers) {
      this.responseCode = responseCode;
      this.status = status;
      this.protocol = protocol;
      this.responseHeaders = headers;
   }

   public HeaderMap getResponseHeaders() {
      return this.responseHeaders;
   }

   public HttpString getProtocol() {
      return this.protocol;
   }

   public int getResponseCode() {
      return this.responseCode;
   }

   public String getStatus() {
      return this.status;
   }

   public String toString() {
      return "ClientResponse{responseHeaders=" + this.responseHeaders + ", responseCode=" + this.responseCode + ", status='" + this.status + '\'' + ", protocol=" + this.protocol + '}';
   }
}
