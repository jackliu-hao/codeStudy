/*    */ package io.undertow.conduits;
/*    */ 
/*    */ import io.undertow.server.Connectors;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.ConduitFactory;
/*    */ import io.undertow.util.ObjectPool;
/*    */ import java.util.zip.CRC32;
/*    */ import java.util.zip.Deflater;
/*    */ import org.xnio.conduits.StreamSinkConduit;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class GzipStreamSinkConduit
/*    */   extends DeflatingStreamSinkConduit
/*    */ {
/*    */   private static final int GZIP_MAGIC = 35615;
/* 39 */   private static final byte[] HEADER = new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 55 */   protected CRC32 crc = new CRC32();
/*    */   
/*    */   public GzipStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange) {
/* 58 */     this(conduitFactory, exchange, -1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GzipStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, int deflateLevel) {
/* 65 */     this(conduitFactory, exchange, newInstanceDeflaterPool(deflateLevel));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GzipStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, ObjectPool<Deflater> deflaterPool) {
/* 72 */     super(conduitFactory, exchange, deflaterPool);
/* 73 */     writeHeader();
/* 74 */     Connectors.updateResponseBytesSent(exchange, HEADER.length);
/*    */   }
/*    */   
/*    */   private void writeHeader() {
/* 78 */     this.currentBuffer.getBuffer().put(HEADER);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void preDeflate(byte[] data) {
/* 83 */     this.crc.update(data);
/*    */   }
/*    */ 
/*    */   
/*    */   protected byte[] getTrailer() {
/* 88 */     byte[] ret = new byte[8];
/* 89 */     int checksum = (int)this.crc.getValue();
/* 90 */     int total = this.deflater.getTotalIn();
/* 91 */     ret[0] = (byte)(checksum & 0xFF);
/* 92 */     ret[1] = (byte)(checksum >> 8 & 0xFF);
/* 93 */     ret[2] = (byte)(checksum >> 16 & 0xFF);
/* 94 */     ret[3] = (byte)(checksum >> 24 & 0xFF);
/* 95 */     ret[4] = (byte)(total & 0xFF);
/* 96 */     ret[5] = (byte)(total >> 8 & 0xFF);
/* 97 */     ret[6] = (byte)(total >> 16 & 0xFF);
/* 98 */     ret[7] = (byte)(total >> 24 & 0xFF);
/* 99 */     return ret;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\GzipStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */