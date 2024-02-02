/*    */ package io.undertow.client.http;
/*    */ 
/*    */ import io.undertow.conduits.AbstractFixedLengthStreamSinkConduit;
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
/*    */ 
/*    */ 
/*    */ class ClientFixedLengthStreamSinkConduit
/*    */   extends AbstractFixedLengthStreamSinkConduit
/*    */ {
/*    */   private final HttpClientExchange exchange;
/*    */   
/*    */   ClientFixedLengthStreamSinkConduit(StreamSinkConduit next, long contentLength, boolean configurable, boolean propagateClose, HttpClientExchange exchange) {
/* 38 */     super(next, contentLength, configurable, propagateClose);
/* 39 */     this.exchange = exchange;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void channelFinished() {
/* 46 */     this.exchange.terminateRequest();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\ClientFixedLengthStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */