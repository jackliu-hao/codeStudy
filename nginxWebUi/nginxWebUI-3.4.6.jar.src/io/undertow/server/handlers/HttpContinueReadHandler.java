/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.ResponseCommitListener;
/*     */ import io.undertow.server.protocol.http.HttpContinue;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSourceConduit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpContinueReadHandler
/*     */   implements HttpHandler
/*     */ {
/*  47 */   private static final ConduitWrapper<StreamSourceConduit> WRAPPER = new ConduitWrapper<StreamSourceConduit>()
/*     */     {
/*     */       public StreamSourceConduit wrap(ConduitFactory<StreamSourceConduit> factory, HttpServerExchange exchange) {
/*  50 */         if (exchange.isRequestChannelAvailable() && !exchange.isResponseStarted()) {
/*  51 */           return new HttpContinueReadHandler.ContinueConduit((StreamSourceConduit)factory.create(), exchange);
/*     */         }
/*  53 */         return (StreamSourceConduit)factory.create();
/*     */       }
/*     */     };
/*     */   
/*     */   private final HttpHandler handler;
/*     */   
/*     */   public HttpContinueReadHandler(HttpHandler handler) {
/*  60 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  65 */     if (HttpContinue.requiresContinueResponse(exchange)) {
/*  66 */       exchange.addRequestWrapper(WRAPPER);
/*  67 */       exchange.addResponseCommitListener(ContinueResponseCommitListener.INSTANCE);
/*     */     } 
/*  69 */     this.handler.handleRequest(exchange);
/*     */   }
/*     */   
/*     */   private enum ContinueResponseCommitListener implements ResponseCommitListener {
/*  73 */     INSTANCE;
/*     */ 
/*     */ 
/*     */     
/*     */     public void beforeCommit(HttpServerExchange exchange) {
/*  78 */       if (!HttpContinue.isContinueResponseSent(exchange)) {
/*  79 */         exchange.setPersistent(false);
/*     */         
/*  81 */         if (!exchange.isRequestComplete()) {
/*  82 */           exchange.getConnection().terminateRequestChannel(exchange);
/*     */         } else {
/*  84 */           Connectors.terminateRequest(exchange);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ContinueConduit
/*     */     extends AbstractStreamSourceConduit<StreamSourceConduit> implements StreamSourceConduit {
/*     */     private boolean sent = false;
/*  93 */     private HttpContinue.ContinueResponseSender response = null;
/*     */     
/*     */     private final HttpServerExchange exchange;
/*     */     
/*     */     protected ContinueConduit(StreamSourceConduit next, HttpServerExchange exchange) {
/*  98 */       super(next);
/*  99 */       this.exchange = exchange;
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 104 */       if (this.exchange.getStatusCode() == 417) {
/*     */         
/* 106 */         Connectors.terminateRequest(this.exchange);
/* 107 */         return -1L;
/*     */       } 
/* 109 */       if (!this.sent) {
/* 110 */         this.sent = true;
/* 111 */         this.response = HttpContinue.createResponseSender(this.exchange);
/*     */       } 
/* 113 */       if (this.response != null) {
/* 114 */         if (!this.response.send()) {
/* 115 */           return 0L;
/*     */         }
/* 117 */         this.response = null;
/*     */       } 
/* 119 */       return super.transferTo(position, count, target);
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 124 */       if (this.exchange.getStatusCode() == 417) {
/*     */         
/* 126 */         Connectors.terminateRequest(this.exchange);
/* 127 */         return -1L;
/*     */       } 
/* 129 */       if (!this.sent) {
/* 130 */         this.sent = true;
/* 131 */         this.response = HttpContinue.createResponseSender(this.exchange);
/*     */       } 
/* 133 */       if (this.response != null) {
/* 134 */         if (!this.response.send()) {
/* 135 */           return 0L;
/*     */         }
/* 137 */         this.response = null;
/*     */       } 
/* 139 */       return super.transferTo(count, throughBuffer, target);
/*     */     }
/*     */ 
/*     */     
/*     */     public int read(ByteBuffer dst) throws IOException {
/* 144 */       if (this.exchange.getStatusCode() == 417) {
/*     */         
/* 146 */         Connectors.terminateRequest(this.exchange);
/* 147 */         return -1;
/*     */       } 
/* 149 */       if (!this.sent) {
/* 150 */         this.sent = true;
/* 151 */         this.response = HttpContinue.createResponseSender(this.exchange);
/*     */       } 
/* 153 */       if (this.response != null) {
/* 154 */         if (!this.response.send()) {
/* 155 */           return 0;
/*     */         }
/* 157 */         this.response = null;
/*     */       } 
/* 159 */       return super.read(dst);
/*     */     }
/*     */ 
/*     */     
/*     */     public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 164 */       if (this.exchange.getStatusCode() == 417) {
/*     */         
/* 166 */         Connectors.terminateRequest(this.exchange);
/* 167 */         return -1L;
/*     */       } 
/* 169 */       if (!this.sent) {
/* 170 */         this.sent = true;
/* 171 */         this.response = HttpContinue.createResponseSender(this.exchange);
/*     */       } 
/* 173 */       if (this.response != null) {
/* 174 */         if (!this.response.send()) {
/* 175 */           return 0L;
/*     */         }
/* 177 */         this.response = null;
/*     */       } 
/* 179 */       return super.read(dsts, offs, len);
/*     */     }
/*     */ 
/*     */     
/*     */     public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 184 */       if (this.exchange.getStatusCode() == 417) {
/*     */         return;
/*     */       }
/*     */       
/* 188 */       if (!this.sent) {
/* 189 */         this.sent = true;
/* 190 */         this.response = HttpContinue.createResponseSender(this.exchange);
/*     */       } 
/* 192 */       long exitTime = System.currentTimeMillis() + timeUnit.toMillis(time);
/* 193 */       if (this.response != null) {
/* 194 */         while (!this.response.send()) {
/* 195 */           long l = System.currentTimeMillis();
/* 196 */           if (l > exitTime) {
/*     */             return;
/*     */           }
/* 199 */           this.response.awaitWritable(exitTime - l, TimeUnit.MILLISECONDS);
/*     */         } 
/* 201 */         this.response = null;
/*     */       } 
/*     */       
/* 204 */       long currentTime = System.currentTimeMillis();
/* 205 */       super.awaitReadable(exitTime - currentTime, TimeUnit.MILLISECONDS);
/*     */     }
/*     */ 
/*     */     
/*     */     public void awaitReadable() throws IOException {
/* 210 */       if (this.exchange.getStatusCode() == 417) {
/*     */         return;
/*     */       }
/*     */       
/* 214 */       if (!this.sent) {
/* 215 */         this.sent = true;
/* 216 */         this.response = HttpContinue.createResponseSender(this.exchange);
/*     */       } 
/* 218 */       if (this.response != null) {
/* 219 */         while (!this.response.send()) {
/* 220 */           this.response.awaitWritable();
/*     */         }
/* 222 */         this.response = null;
/*     */       } 
/* 224 */       super.awaitReadable();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\HttpContinueReadHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */