/*     */ package io.undertow.client.ajp;
/*     */ 
/*     */ import io.undertow.channels.DetachableStreamSinkChannel;
/*     */ import io.undertow.channels.DetachableStreamSourceChannel;
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.ClientResponse;
/*     */ import io.undertow.client.ContinueNotification;
/*     */ import io.undertow.client.PushCallback;
/*     */ import io.undertow.protocols.ajp.AjpClientRequestClientStreamSinkChannel;
/*     */ import io.undertow.protocols.ajp.AjpClientResponseStreamSourceChannel;
/*     */ import io.undertow.util.AbstractAttachable;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.IOException;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
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
/*     */ class AjpClientExchange
/*     */   extends AbstractAttachable
/*     */   implements ClientExchange
/*     */ {
/*     */   private final ClientRequest request;
/*     */   private final boolean requiresContinue;
/*     */   private final AjpClientConnection clientConnection;
/*     */   private ClientCallback<ClientExchange> responseCallback;
/*     */   private ClientCallback<ClientExchange> readyCallback;
/*     */   private ContinueNotification continueNotification;
/*     */   private ClientResponse response;
/*     */   private ClientResponse continueResponse;
/*     */   private IOException failedReason;
/*     */   private AjpClientResponseStreamSourceChannel responseChannel;
/*     */   private AjpClientRequestClientStreamSinkChannel requestChannel;
/*  61 */   private int state = 0;
/*     */   private static final int REQUEST_TERMINATED = 1;
/*     */   private static final int RESPONSE_TERMINATED = 2;
/*     */   
/*     */   AjpClientExchange(ClientCallback<ClientExchange> readyCallback, ClientRequest request, AjpClientConnection clientConnection) {
/*  66 */     this.readyCallback = readyCallback;
/*  67 */     this.request = request;
/*  68 */     this.clientConnection = clientConnection;
/*  69 */     boolean reqContinue = false;
/*  70 */     if (request.getRequestHeaders().contains(Headers.EXPECT)) {
/*  71 */       for (String header : request.getRequestHeaders().get(Headers.EXPECT)) {
/*  72 */         if (header.equals("100-continue")) {
/*  73 */           reqContinue = true;
/*     */         }
/*     */       } 
/*     */     }
/*  77 */     this.requiresContinue = reqContinue;
/*     */   }
/*     */   
/*     */   void terminateRequest() {
/*  81 */     this.state |= 0x1;
/*  82 */     if (!this.clientConnection.isOpen()) {
/*  83 */       this.state |= 0x2;
/*     */     }
/*  85 */     if (Bits.anyAreSet(this.state, 2)) {
/*  86 */       this.clientConnection.requestDone();
/*     */     }
/*     */   }
/*     */   
/*     */   void terminateResponse() {
/*  91 */     this.state |= 0x2;
/*  92 */     if (!this.clientConnection.isOpen()) {
/*  93 */       this.state |= 0x1;
/*     */     }
/*  95 */     if (Bits.anyAreSet(this.state, 1)) {
/*  96 */       this.clientConnection.requestDone();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isRequiresContinue() {
/* 101 */     return this.requiresContinue;
/*     */   }
/*     */ 
/*     */   
/*     */   void setContinueResponse(ClientResponse response) {
/* 106 */     this.continueResponse = response;
/* 107 */     if (this.continueNotification != null) {
/* 108 */       this.continueNotification.handleContinue(this);
/*     */     }
/*     */   }
/*     */   
/*     */   void setResponse(ClientResponse response) {
/* 113 */     this.response = response;
/* 114 */     if (this.responseCallback != null) {
/* 115 */       this.responseCallback.completed(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResponseListener(ClientCallback<ClientExchange> listener) {
/* 121 */     this.responseCallback = listener;
/* 122 */     if (listener != null) {
/* 123 */       if (this.failedReason != null) {
/* 124 */         listener.failed(this.failedReason);
/* 125 */       } else if (this.response != null) {
/* 126 */         listener.completed(this);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContinueHandler(ContinueNotification continueHandler) {
/* 133 */     this.continueNotification = continueHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPushHandler(PushCallback pushCallback) {}
/*     */ 
/*     */   
/*     */   void setFailed(IOException e) {
/* 142 */     this.failedReason = e;
/* 143 */     if (this.readyCallback != null) {
/* 144 */       this.readyCallback.failed(e);
/* 145 */       this.readyCallback = null;
/*     */     } 
/* 147 */     if (this.responseCallback != null) {
/* 148 */       this.responseCallback.failed(e);
/* 149 */       this.responseCallback = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSinkChannel getRequestChannel() {
/* 155 */     return (StreamSinkChannel)new DetachableStreamSinkChannel((StreamSinkChannel)this.requestChannel)
/*     */       {
/*     */         protected boolean isFinished() {
/* 158 */           return Bits.anyAreSet(AjpClientExchange.this.state, 1);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSourceChannel getResponseChannel() {
/* 165 */     return (StreamSourceChannel)new DetachableStreamSourceChannel((StreamSourceChannel)this.responseChannel)
/*     */       {
/*     */         protected boolean isFinished() {
/* 168 */           return Bits.anyAreSet(AjpClientExchange.this.state, 2);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientRequest getRequest() {
/* 175 */     return this.request;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientResponse getResponse() {
/* 180 */     return this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientResponse getContinueResponse() {
/* 185 */     return this.continueResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientConnection getConnection() {
/* 190 */     return this.clientConnection;
/*     */   }
/*     */   
/*     */   void setResponseChannel(AjpClientResponseStreamSourceChannel responseChannel) {
/* 194 */     this.responseChannel = responseChannel;
/*     */   }
/*     */   
/*     */   void setRequestChannel(AjpClientRequestClientStreamSinkChannel requestChannel) {
/* 198 */     this.requestChannel = requestChannel;
/*     */   }
/*     */   
/*     */   void invokeReadReadyCallback(ClientExchange result) {
/* 202 */     if (this.readyCallback != null) {
/* 203 */       this.readyCallback.completed(result);
/* 204 */       this.readyCallback = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ajp\AjpClientExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */