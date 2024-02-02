package io.undertow.protocols.http2;

import io.undertow.util.HeaderMap;

public class Http2HeadersStreamSinkChannel extends Http2DataStreamSinkChannel {
   public Http2HeadersStreamSinkChannel(Http2Channel channel, int streamId) {
      super(channel, streamId, 1);
   }

   public Http2HeadersStreamSinkChannel(Http2Channel channel, int streamId, HeaderMap headers) {
      super(channel, streamId, headers, 1);
   }
}
