/*     */ package io.undertow.client.http2;
/*     */ 
/*     */ import io.undertow.client.ClientCallback;
/*     */ import io.undertow.client.ClientConnection;
/*     */ import io.undertow.client.ClientExchange;
/*     */ import io.undertow.client.ClientRequest;
/*     */ import io.undertow.client.ClientResponse;
/*     */ import io.undertow.client.ContinueNotification;
/*     */ import io.undertow.client.PushCallback;
/*     */ import io.undertow.protocols.http2.Http2Channel;
/*     */ import io.undertow.protocols.http2.Http2StreamSinkChannel;
/*     */ import io.undertow.protocols.http2.Http2StreamSourceChannel;
/*     */ import io.undertow.util.AbstractAttachable;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Protocols;
/*     */ import java.io.IOException;
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
/*     */ public class Http2ClientExchange
/*     */   extends AbstractAttachable
/*     */   implements ClientExchange
/*     */ {
/*     */   private ClientCallback<ClientExchange> responseListener;
/*     */   private ContinueNotification continueNotification;
/*     */   private Http2StreamSourceChannel response;
/*     */   private ClientResponse clientResponse;
/*     */   private ClientResponse continueResponse;
/*     */   private final ClientConnection clientConnection;
/*     */   private final Http2StreamSinkChannel request;
/*     */   private final ClientRequest clientRequest;
/*     */   private IOException failedReason;
/*     */   private PushCallback pushCallback;
/*     */   
/*     */   public Http2ClientExchange(ClientConnection clientConnection, Http2StreamSinkChannel request, ClientRequest clientRequest) {
/*  57 */     this.clientConnection = clientConnection;
/*  58 */     this.request = request;
/*  59 */     this.clientRequest = clientRequest;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setResponseListener(ClientCallback<ClientExchange> responseListener) {
/*  65 */     this.responseListener = responseListener;
/*  66 */     if (this.failedReason != null) {
/*  67 */       responseListener.failed(this.failedReason);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContinueHandler(ContinueNotification continueHandler) {
/*  73 */     this.continueNotification = continueHandler;
/*     */   }
/*     */   
/*     */   void setContinueResponse(ClientResponse response) {
/*  77 */     this.continueResponse = response;
/*  78 */     if (this.continueNotification != null) {
/*  79 */       this.continueNotification.handleContinue(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPushHandler(PushCallback pushCallback) {
/*  85 */     this.pushCallback = pushCallback;
/*     */   }
/*     */   
/*     */   PushCallback getPushCallback() {
/*  89 */     return this.pushCallback;
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSinkChannel getRequestChannel() {
/*  94 */     return (StreamSinkChannel)this.request;
/*     */   }
/*     */ 
/*     */   
/*     */   public StreamSourceChannel getResponseChannel() {
/*  99 */     return (StreamSourceChannel)this.response;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientRequest getRequest() {
/* 104 */     return this.clientRequest;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientResponse getResponse() {
/* 109 */     return this.clientResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientResponse getContinueResponse() {
/* 114 */     return this.continueResponse;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClientConnection getConnection() {
/* 119 */     return this.clientConnection;
/*     */   }
/*     */   
/*     */   void failed(IOException e) {
/* 123 */     this.failedReason = e;
/* 124 */     if (this.responseListener != null) {
/* 125 */       this.responseListener.failed(e);
/*     */     }
/*     */   }
/*     */   
/*     */   void responseReady(Http2StreamSourceChannel result) {
/* 130 */     this.response = result;
/* 131 */     ClientResponse clientResponse = createResponse(result);
/* 132 */     this.clientResponse = clientResponse;
/* 133 */     if (this.responseListener != null) {
/* 134 */       this.responseListener.completed(this);
/*     */     }
/*     */   }
/*     */   
/*     */   ClientResponse createResponse(Http2StreamSourceChannel result) {
/* 139 */     HeaderMap headers = result.getHeaders();
/* 140 */     String status = result.getHeaders().getFirst(Http2Channel.STATUS);
/* 141 */     int statusCode = Integer.parseInt(status);
/* 142 */     headers.remove(Http2Channel.STATUS);
/* 143 */     return new ClientResponse(statusCode, status.substring(3), Protocols.HTTP_2_0, headers);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\http2\Http2ClientExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */