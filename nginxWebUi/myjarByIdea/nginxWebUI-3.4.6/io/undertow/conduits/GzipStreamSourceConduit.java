package io.undertow.conduits;

import io.undertow.UndertowMessages;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.ObjectPool;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import org.xnio.conduits.StreamSourceConduit;

public class GzipStreamSourceConduit extends InflatingStreamSourceConduit {
   public static final ConduitWrapper<StreamSourceConduit> WRAPPER = new ConduitWrapper<StreamSourceConduit>() {
      public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
         return new GzipStreamSourceConduit(exchange, (StreamSourceConduit)factory.create());
      }
   };
   private static final int GZIP_MAGIC = 35615;
   private static final byte[] HEADER = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
   private final CRC32 crc = new CRC32();
   private int totalOut;
   private int headerRead = 0;
   private int footerRead = 0;
   byte[] expectedFooter;

   public GzipStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next) {
      super(exchange, next);
   }

   public GzipStreamSourceConduit(HttpServerExchange exchange, StreamSourceConduit next, ObjectPool<Inflater> inflaterPool) {
      super(exchange, next, inflaterPool);
   }

   protected boolean readHeader(ByteBuffer headerData) throws IOException {
      while(this.headerRead < HEADER.length && headerData.hasRemaining()) {
         byte data = headerData.get();
         if (this.headerRead == 0 && data != HEADER[0]) {
            throw UndertowMessages.MESSAGES.invalidGzipHeader();
         }

         if (this.headerRead == 1 && data != HEADER[1]) {
            throw UndertowMessages.MESSAGES.invalidGzipHeader();
         }

         ++this.headerRead;
      }

      return this.headerRead == HEADER.length;
   }

   protected void readFooter(ByteBuffer buf) throws IOException {
      if (this.expectedFooter == null) {
         byte[] ret = new byte[8];
         int checksum = (int)this.crc.getValue();
         int total = this.totalOut;
         ret[0] = (byte)(checksum & 255);
         ret[1] = (byte)(checksum >> 8 & 255);
         ret[2] = (byte)(checksum >> 16 & 255);
         ret[3] = (byte)(checksum >> 24 & 255);
         ret[4] = (byte)(total & 255);
         ret[5] = (byte)(total >> 8 & 255);
         ret[6] = (byte)(total >> 16 & 255);
         ret[7] = (byte)(total >> 24 & 255);
         this.expectedFooter = ret;
      }

      while(buf.hasRemaining() && this.footerRead < this.expectedFooter.length) {
         byte data = buf.get();
         if (this.expectedFooter[this.footerRead++] != data) {
            throw UndertowMessages.MESSAGES.invalidGZIPFooter();
         }
      }

      if (buf.hasRemaining() && this.footerRead == this.expectedFooter.length) {
         throw UndertowMessages.MESSAGES.invalidGZIPFooter();
      }
   }

   protected void dataDeflated(byte[] data, int off, int len) {
      this.crc.update(data, off, len);
      this.totalOut += len;
   }
}
