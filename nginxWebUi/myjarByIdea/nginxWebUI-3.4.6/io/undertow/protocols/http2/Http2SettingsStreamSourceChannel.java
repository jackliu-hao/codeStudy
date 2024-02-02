package io.undertow.protocols.http2;

import io.undertow.connector.PooledByteBuffer;
import java.util.Collections;
import java.util.List;

public class Http2SettingsStreamSourceChannel extends AbstractHttp2StreamSourceChannel {
   private final List<Http2Setting> settings;

   Http2SettingsStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, List<Http2Setting> settings) {
      super(framedChannel, data, frameDataRemaining);
      this.settings = settings;
      this.lastFrame();
   }

   public List<Http2Setting> getSettings() {
      return Collections.unmodifiableList(this.settings);
   }
}
