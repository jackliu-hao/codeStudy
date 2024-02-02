/*    */ package io.undertow.server.protocol.http;
/*    */ 
/*    */ import io.undertow.conduits.AbstractFixedLengthStreamSinkConduit;
/*    */ import io.undertow.server.Connectors;
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ 
/*    */ public class ServerFixedLengthStreamSinkConduit
/*    */   extends AbstractFixedLengthStreamSinkConduit
/*    */ {
/*    */   private HttpServerExchange exchange;
/*    */   
/*    */   public ServerFixedLengthStreamSinkConduit(StreamSinkConduit next, boolean configurable, boolean propagateClose) {
/* 41 */     super(next, 1L, configurable, propagateClose);
/*    */   }
/*    */   
/*    */   void reset(long contentLength, HttpServerExchange exchange) {
/* 45 */     this.exchange = exchange;
/* 46 */     reset(contentLength, !exchange.isPersistent());
/*    */   }
/*    */   
/*    */   void clearExchange() {
/* 50 */     channelFinished();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void channelFinished() {
/* 55 */     if (this.exchange != null) {
/* 56 */       HttpServerExchange exchange = this.exchange;
/* 57 */       this.exchange = null;
/* 58 */       Connectors.terminateResponse(exchange);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\ServerFixedLengthStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */