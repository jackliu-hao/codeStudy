package io.undertow.protocols.http2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class Http2SettingsParser extends Http2PushBackParser {
   private int count = 0;
   private final List<Http2Setting> settings = new ArrayList();

   Http2SettingsParser(int frameLength) {
      super(frameLength);
   }

   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser parser) {
      while(this.count < parser.length) {
         if (resource.remaining() < 6) {
            return;
         }

         int id = (resource.get() & 255) << 8;
         id += resource.get() & 255;
         long value = ((long)resource.get() & 255L) << 24;
         value += ((long)resource.get() & 255L) << 16;
         value += ((long)resource.get() & 255L) << 8;
         value += (long)resource.get() & 255L;
         this.settings.add(new Http2Setting(id, value));
         this.count += 6;
      }

   }

   public List<Http2Setting> getSettings() {
      return this.settings;
   }
}
