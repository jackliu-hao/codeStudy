/*    */ package io.undertow.server.handlers.encoding;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.conduits.DeflatingStreamSinkConduit;
/*    */ import io.undertow.server.ConduitWrapper;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.ConduitFactory;
/*    */ import io.undertow.util.ObjectPool;
/*    */ import java.util.zip.Deflater;
/*    */ import org.xnio.conduits.Conduit;
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
/*    */ public class DeflateEncodingProvider
/*    */   implements ContentEncodingProvider
/*    */ {
/*    */   private final ObjectPool<Deflater> deflaterPool;
/*    */   
/*    */   public DeflateEncodingProvider() {
/* 41 */     this(8);
/*    */   }
/*    */   
/*    */   public DeflateEncodingProvider(int deflateLevel) {
/* 45 */     this(DeflatingStreamSinkConduit.newInstanceDeflaterPool(deflateLevel));
/*    */   }
/*    */ 
/*    */   
/*    */   public DeflateEncodingProvider(ObjectPool<Deflater> deflaterPool) {
/* 50 */     this.deflaterPool = deflaterPool;
/*    */   }
/*    */ 
/*    */   
/*    */   public ConduitWrapper<StreamSinkConduit> getResponseWrapper() {
/* 55 */     return new ConduitWrapper<StreamSinkConduit>()
/*    */       {
/*    */         public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/* 58 */           UndertowLogger.REQUEST_LOGGER.tracef("Created DEFLATE response conduit for %s", exchange);
/* 59 */           return (StreamSinkConduit)new DeflatingStreamSinkConduit(factory, exchange, DeflateEncodingProvider.this.deflaterPool);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\DeflateEncodingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */