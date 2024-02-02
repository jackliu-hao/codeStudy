package io.undertow.websockets.core;

import io.undertow.util.AttachmentKey;

public enum WebSocketVersion {
   UNKNOWN,
   V00,
   V07,
   V08,
   V13;

   public static final AttachmentKey<WebSocketVersion> ATTACHMENT_KEY = AttachmentKey.create(WebSocketVersion.class);

   public String toHttpHeaderValue() {
      if (this == V00) {
         return "0";
      } else if (this == V07) {
         return "7";
      } else if (this == V08) {
         return "8";
      } else if (this == V13) {
         return "13";
      } else {
         throw new IllegalStateException("Unknown WebSocket version: " + this);
      }
   }
}
