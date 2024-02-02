/*     */ package io.undertow.client.http;
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
/*     */ import io.undertow.util.AbstractAttachable;
/*     */ import io.undertow.util.Headers;
/*     */ import java.io.IOException;
/*     */ import org.jboss.logging.Logger;
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
/*     */ class HttpClientExchange
/*     */   extends AbstractAttachable
/*     */   implements ClientExchange
/*     */ {
/*  45 */   private static final Logger log = Logger.getLogger(HttpClientExchange.class.getName());
/*     */   
/*     */   private final ClientRequest request;
/*     */   
/*     */   private final boolean requiresContinue;
/*     */   
/*     */   private final HttpClientConnection clientConnection;
/*     */   
/*     */   private ClientCallback<ClientExchange> responseCallback;
/*     */   private ClientCallback<ClientExchange> readyCallback;
/*     */   private ContinueNotification continueNotification;
/*     */   private ClientResponse response;
/*     */   private ClientResponse continueResponse;
/*     */   private IOException failedReason;
/*     */   private HttpRequestConduit requestConduit;
/*  60 */   private int state = 0;
/*     */   private static final int REQUEST_TERMINATED = 1;
/*     */   private static final int RESPONSE_TERMINATED = 2;
/*     */   
/*     */   HttpClientExchange(ClientCallback<ClientExchange> readyCallback, ClientRequest request, HttpClientConnection clientConnection) {
/*  65 */     this.readyCallback = readyCallback;
/*  66 */     this.request = request;
/*  67 */     this.clientConnection = clientConnection;
/*  68 */     boolean reqContinue = false;
/*  69 */     if (request.getRequestHeaders().contains(Headers.EXPECT)) {
/*  70 */       for (String header : request.getRequestHeaders().get(Headers.EXPECT)) {
/*  71 */         if (header.equals("100-continue")) {
/*  72 */           reqContinue = true;
/*     */         }
/*     */       } 
/*     */     }
/*  76 */     this.requiresContinue = reqContinue;
/*     */   }
/*     */   
/*     */   public void setRequestConduit(HttpRequestConduit requestConduit) {
/*  80 */     this.requestConduit = requestConduit;
/*     */   }
/*     */   
/*     */   void terminateRequest() {
/*  84 */     if (Bits.anyAreSet(this.state, 1)) {
/*     */       return;
/*     */     }
/*  87 */     log.debugf("request terminated for request to %s %s", this.clientConnection.getPeerAddress(), getRequest().getPath());
/*  88 */     this.state |= 0x1;
/*  89 */     this.clientConnection.requestDataSent();
/*  90 */     if (Bits.anyAreSet(this.state, 2)) {
/*  91 */       this.clientConnection.exchangeDone();
/*     */     }
/*     */   }
/*     */   
/*     */   boolean isRequestDataSent() {
/*  96 */     return Bits.anyAreSet(this.state, 1);
/*     */   }
/*     */   
/*     */   void terminateResponse() {
/* 100 */     if (Bits.anyAreSet(this.state, 2)) {
/*     */       return;
/*     */     }
/* 103 */     log.debugf("response terminated for request to %s %s", this.clientConnection.getPeerAddress(), getRequest().getPath());
/* 104 */     this.state |= 0x2;
/* 105 */     if (Bits.anyAreSet(this.state, 1)) {
/* 106 */       this.clientConnection.exchangeDone();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isRequiresContinue() {
/* 111 */     return this.requiresContinue;
/*     */   }
/*     */ 
/*     */   
/*     */   void setContinueResponse(ClientResponse response) {
/* 116 */     this.continueResponse = response;
/* 117 */     if (this.continueNotification != null) {
/* 118 */       this.continueNotification.handleContinue(this);
/*     */     }
/*     */   }
/*     */   
/*     */   void setResponse(ClientResponse response) {
/* 123 */     this.response = response;
/* 124 */     if (this.responseCallback != null) {
/* 125 */       this.responseCallback.completed(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setResponseListener(ClientCallback<ClientExchange> listener) {
/* 131 */     this.responseCallback = listener;
/* 132 */     if (listener != null) {
/* 133 */       if (this.failedReason != null) {
/* 134 */         listener.failed(this.failedReason);
/* 135 */       } else if (this.response != null) {
/* 136 */         listener.completed(this);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContinueHandler(ContinueNotification continueHandler) {
/* 143 */     this.continueNotification = continueHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPushHandler(PushCallback pushCallback) {}
/*     */ 
/*     */   
/*     */   void setFailed(IOException e) {
/* 152 */     this.failedReason = e;
/* 153 */     if (this.readyCallback != null) {
/* 154 */       this.readyCallback.failed(e);
/* 155 */       this.readyCallback = null;
/*     */     } 
/* 157 */     if (this.responseCallback != null) {
/* 158 */       this.responseCallback.failed(e);
/* 159 */       this.responseCallback = null;
/*     */     } 
/* 161 */     if (this.requestConduit != null) {
/* 162 */       this.requestConduit.freeBuffers();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSinkChannel getRequestChannel() {
/* 168 */     return (StreamSinkChannel)new DetachableStreamSinkChannel((StreamSinkChannel)this.clientConnection.getConnection().getSinkChannel())
/*     */       {
/*     */         protected boolean isFinished() {
/* 171 */           return Bits.anyAreSet(HttpClientExchange.this.state, 1);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSourceChannel getResponseChannel() {
/* 178 */     return (StreamSourceChannel)new DetachableStreamSourceChannel((StreamSourceChannel)this.clientConnection.getConnection().getSourceChannel())
/*     */       {
/*     */         protected boolean isFinished() {
/* 181 */           return Bits.anyAreSet(HttpClientExchange.this.state, 2);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientRequest getRequest() {
/* 188 */     return this.request;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientResponse getResponse() {
/* 193 */     return this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientResponse getContinueResponse() {
/* 198 */     return this.continueResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientConnection getConnection() {
/* 203 */     return this.clientConnection;
/*     */   }
/*     */   
/*     */   ClientCallback<ClientExchange> getResponseCallback() {
/* 207 */     return this.responseCallback;
/*     */   }
/*     */   
/*     */   void invokeReadReadyCallback() {
/* 211 */     if (this.readyCallback != null) {
/* 212 */       this.readyCallback.completed(this);
/* 213 */       this.readyCallback = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http\HttpClientExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */