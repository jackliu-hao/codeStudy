package io.undertow.server.handlers.proxy.mod_cluster;

import io.undertow.client.ClientResponse;

public interface NodeHealthChecker {
   NodeHealthChecker NO_CHECK = new NodeHealthChecker() {
      public boolean checkResponse(ClientResponse response) {
         return true;
      }
   };
   NodeHealthChecker OK = new NodeHealthChecker() {
      public boolean checkResponse(ClientResponse response) {
         int code = response.getResponseCode();
         return code >= 200 && code < 400;
      }
   };

   boolean checkResponse(ClientResponse var1);
}
