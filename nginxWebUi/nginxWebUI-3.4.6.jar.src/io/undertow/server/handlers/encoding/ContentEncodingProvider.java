/*    */ package io.undertow.server.handlers.encoding;
/*    */ 
/*    */ import io.undertow.server.ConduitWrapper;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.ConduitFactory;
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
/*    */ public interface ContentEncodingProvider
/*    */ {
/* 32 */   public static final ContentEncodingProvider IDENTITY = new ContentEncodingProvider()
/*    */     {
/* 34 */       private final ConduitWrapper<StreamSinkConduit> CONDUIT_WRAPPER = new ConduitWrapper<StreamSinkConduit>()
/*    */         {
/*    */           public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/* 37 */             return (StreamSinkConduit)factory.create();
/*    */           }
/*    */         };
/*    */ 
/*    */       
/*    */       public ConduitWrapper<StreamSinkConduit> getResponseWrapper() {
/* 43 */         return this.CONDUIT_WRAPPER;
/*    */       }
/*    */     };
/*    */   
/*    */   ConduitWrapper<StreamSinkConduit> getResponseWrapper();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\ContentEncodingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */