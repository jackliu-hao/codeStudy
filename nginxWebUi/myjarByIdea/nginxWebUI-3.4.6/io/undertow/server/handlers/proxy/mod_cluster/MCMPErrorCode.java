package io.undertow.server.handlers.proxy.mod_cluster;

enum MCMPErrorCode {
   CANT_READ_NODE("MEM", "MEM: Can't read node"),
   CANT_UPDATE_NODE("MEM", "MEM: Can't update or insert node"),
   CANT_UPDATE_CONTEXT("MEM", "MEM: Can't update or insert context"),
   NODE_STILL_EXISTS("SYNTAX", "MEM: Old node still exist");

   private final String type;
   private final String message;

   private MCMPErrorCode(String type, String message) {
      this.type = type;
      this.message = message;
   }

   public String getType() {
      return this.type;
   }

   public String getMessage() {
      return this.message;
   }
}
