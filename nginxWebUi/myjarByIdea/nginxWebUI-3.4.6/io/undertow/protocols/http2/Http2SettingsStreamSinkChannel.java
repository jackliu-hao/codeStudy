package io.undertow.protocols.http2;

import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.protocol.framed.SendFrameHeader;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

public class Http2SettingsStreamSinkChannel extends Http2StreamSinkChannel {
   private final List<Http2Setting> settings;

   Http2SettingsStreamSinkChannel(Http2Channel channel, List<Http2Setting> settings) {
      super(channel, 0);
      this.settings = settings;
   }

   Http2SettingsStreamSinkChannel(Http2Channel channel) {
      super(channel, 0);
      this.settings = null;
   }

   protected SendFrameHeader createFrameHeaderImpl() {
      PooledByteBuffer pooled = ((Http2Channel)this.getChannel()).getBufferPool().allocate();
      ByteBuffer currentBuffer = pooled.getBuffer();
      if (this.settings != null) {
         int size = this.settings.size() * 6;
         currentBuffer.put((byte)(size >> 16 & 255));
         currentBuffer.put((byte)(size >> 8 & 255));
         currentBuffer.put((byte)(size & 255));
         currentBuffer.put((byte)4);
         currentBuffer.put((byte)0);
         Http2ProtocolUtils.putInt(currentBuffer, this.getStreamId());
         Iterator var4 = this.settings.iterator();

         while(var4.hasNext()) {
            Http2Setting setting = (Http2Setting)var4.next();
            currentBuffer.put((byte)(setting.getId() >> 8 & 255));
            currentBuffer.put((byte)(setting.getId() & 255));
            currentBuffer.put((byte)((int)(setting.getValue() >> 24 & 255L)));
            currentBuffer.put((byte)((int)(setting.getValue() >> 16 & 255L)));
            currentBuffer.put((byte)((int)(setting.getValue() >> 8 & 255L)));
            currentBuffer.put((byte)((int)(setting.getValue() & 255L)));
         }
      } else {
         currentBuffer.put((byte)0);
         currentBuffer.put((byte)0);
         currentBuffer.put((byte)0);
         currentBuffer.put((byte)4);
         currentBuffer.put((byte)1);
         Http2ProtocolUtils.putInt(currentBuffer, this.getStreamId());
      }

      currentBuffer.flip();
      return new SendFrameHeader(pooled);
   }
}
