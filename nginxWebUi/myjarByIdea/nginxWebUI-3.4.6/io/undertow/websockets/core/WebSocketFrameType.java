package io.undertow.websockets.core;

public enum WebSocketFrameType {
   BINARY,
   TEXT,
   PING,
   PONG,
   CLOSE,
   CONTINUATION,
   UNKOWN;
}
