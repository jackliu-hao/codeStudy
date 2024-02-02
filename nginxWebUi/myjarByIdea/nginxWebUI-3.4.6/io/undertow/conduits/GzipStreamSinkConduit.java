package io.undertow.conduits;

import io.undertow.server.Connectors;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.ObjectPool;
import java.util.zip.CRC32;
import org.xnio.conduits.StreamSinkConduit;

public class GzipStreamSinkConduit extends DeflatingStreamSinkConduit {
   private static final int GZIP_MAGIC = 35615;
   private static final byte[] HEADER = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
   protected CRC32 crc;

   public GzipStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange) {
      this(conduitFactory, exchange, -1);
   }

   public GzipStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, int deflateLevel) {
      this(conduitFactory, exchange, newInstanceDeflaterPool(deflateLevel));
   }

   public GzipStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, ObjectPool deflaterPool) {
      super(conduitFactory, exchange, deflaterPool);
      this.crc = new CRC32();
      this.writeHeader();
      Connectors.updateResponseBytesSent(exchange, (long)HEADER.length);
   }

   private void writeHeader() {
      this.currentBuffer.getBuffer().put(HEADER);
   }

   protected void preDeflate(byte[] data) {
      this.crc.update(data);
   }

   protected byte[] getTrailer() {
      byte[] ret = new byte[8];
      int checksum = (int)this.crc.getValue();
      int total = this.deflater.getTotalIn();
      ret[0] = (byte)(checksum & 255);
      ret[1] = (byte)(checksum >> 8 & 255);
      ret[2] = (byte)(checksum >> 16 & 255);
      ret[3] = (byte)(checksum >> 24 & 255);
      ret[4] = (byte)(total & 255);
      ret[5] = (byte)(total >> 8 & 255);
      ret[6] = (byte)(total >> 16 & 255);
      ret[7] = (byte)(total >> 24 & 255);
      return ret;
   }
}
