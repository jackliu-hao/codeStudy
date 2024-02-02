package io.undertow.client.http2;

import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.ContinueNotification;
import io.undertow.client.PushCallback;
import io.undertow.protocols.http2.Http2Channel;
import io.undertow.protocols.http2.Http2StreamSinkChannel;
import io.undertow.protocols.http2.Http2StreamSourceChannel;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.HeaderMap;
import io.undertow.util.Protocols;
import java.io.IOException;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public class Http2ClientExchange extends AbstractAttachable implements ClientExchange {
   private ClientCallback<ClientExchange> responseListener;
   private ContinueNotification continueNotification;
   private Http2StreamSourceChannel response;
   private ClientResponse clientResponse;
   private ClientResponse continueResponse;
   private final ClientConnection clientConnection;
   private final Http2StreamSinkChannel request;
   private final ClientRequest clientRequest;
   private IOException failedReason;
   private PushCallback pushCallback;

   public Http2ClientExchange(ClientConnection clientConnection, Http2StreamSinkChannel request, ClientRequest clientRequest) {
      this.clientConnection = clientConnection;
      this.request = request;
      this.clientRequest = clientRequest;
   }

   public void setResponseListener(ClientCallback<ClientExchange> responseListener) {
      this.responseListener = responseListener;
      if (this.failedReason != null) {
         responseListener.failed(this.failedReason);
      }

   }

   public void setContinueHandler(ContinueNotification continueHandler) {
      this.continueNotification = continueHandler;
   }

   void setContinueResponse(ClientResponse response) {
      this.continueResponse = response;
      if (this.continueNotification != null) {
         this.continueNotification.handleContinue(this);
      }

   }

   public void setPushHandler(PushCallback pushCallback) {
      this.pushCallback = pushCallback;
   }

   PushCallback getPushCallback() {
      return this.pushCallback;
   }

   public StreamSinkChannel getRequestChannel() {
      return this.request;
   }

   public StreamSourceChannel getResponseChannel() {
      return this.response;
   }

   public ClientRequest getRequest() {
      return this.clientRequest;
   }

   public ClientResponse getResponse() {
      return this.clientResponse;
   }

   public ClientResponse getContinueResponse() {
      return this.continueResponse;
   }

   public ClientConnection getConnection() {
      return this.clientConnection;
   }

   void failed(IOException e) {
      this.failedReason = e;
      if (this.responseListener != null) {
         this.responseListener.failed(e);
      }

   }

   void responseReady(Http2StreamSourceChannel result) {
      this.response = result;
      ClientResponse clientResponse = this.createResponse(result);
      this.clientResponse = clientResponse;
      if (this.responseListener != null) {
         this.responseListener.completed(this);
      }

   }

   ClientResponse createResponse(Http2StreamSourceChannel result) {
      HeaderMap headers = result.getHeaders();
      String status = result.getHeaders().getFirst(Http2Channel.STATUS);
      int statusCode = Integer.parseInt(status);
      headers.remove(Http2Channel.STATUS);
      return new ClientResponse(statusCode, status.substring(3), Protocols.HTTP_2_0, headers);
   }
}
