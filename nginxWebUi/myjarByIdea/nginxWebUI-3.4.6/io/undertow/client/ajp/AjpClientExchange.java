package io.undertow.client.ajp;

import io.undertow.channels.DetachableStreamSinkChannel;
import io.undertow.channels.DetachableStreamSourceChannel;
import io.undertow.client.ClientCallback;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.ContinueNotification;
import io.undertow.client.PushCallback;
import io.undertow.protocols.ajp.AjpClientRequestClientStreamSinkChannel;
import io.undertow.protocols.ajp.AjpClientResponseStreamSourceChannel;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.Headers;
import java.io.IOException;
import java.util.Iterator;
import org.xnio.Bits;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

class AjpClientExchange extends AbstractAttachable implements ClientExchange {
   private final ClientRequest request;
   private final boolean requiresContinue;
   private final AjpClientConnection clientConnection;
   private ClientCallback<ClientExchange> responseCallback;
   private ClientCallback<ClientExchange> readyCallback;
   private ContinueNotification continueNotification;
   private ClientResponse response;
   private ClientResponse continueResponse;
   private IOException failedReason;
   private AjpClientResponseStreamSourceChannel responseChannel;
   private AjpClientRequestClientStreamSinkChannel requestChannel;
   private int state = 0;
   private static final int REQUEST_TERMINATED = 1;
   private static final int RESPONSE_TERMINATED = 2;

   AjpClientExchange(ClientCallback<ClientExchange> readyCallback, ClientRequest request, AjpClientConnection clientConnection) {
      this.readyCallback = readyCallback;
      this.request = request;
      this.clientConnection = clientConnection;
      boolean reqContinue = false;
      if (request.getRequestHeaders().contains(Headers.EXPECT)) {
         Iterator var5 = request.getRequestHeaders().get(Headers.EXPECT).iterator();

         while(var5.hasNext()) {
            String header = (String)var5.next();
            if (header.equals("100-continue")) {
               reqContinue = true;
            }
         }
      }

      this.requiresContinue = reqContinue;
   }

   void terminateRequest() {
      this.state |= 1;
      if (!this.clientConnection.isOpen()) {
         this.state |= 2;
      }

      if (Bits.anyAreSet(this.state, 2)) {
         this.clientConnection.requestDone();
      }

   }

   void terminateResponse() {
      this.state |= 2;
      if (!this.clientConnection.isOpen()) {
         this.state |= 1;
      }

      if (Bits.anyAreSet(this.state, 1)) {
         this.clientConnection.requestDone();
      }

   }

   public boolean isRequiresContinue() {
      return this.requiresContinue;
   }

   void setContinueResponse(ClientResponse response) {
      this.continueResponse = response;
      if (this.continueNotification != null) {
         this.continueNotification.handleContinue(this);
      }

   }

   void setResponse(ClientResponse response) {
      this.response = response;
      if (this.responseCallback != null) {
         this.responseCallback.completed(this);
      }

   }

   public void setResponseListener(ClientCallback<ClientExchange> listener) {
      this.responseCallback = listener;
      if (listener != null) {
         if (this.failedReason != null) {
            listener.failed(this.failedReason);
         } else if (this.response != null) {
            listener.completed(this);
         }
      }

   }

   public void setContinueHandler(ContinueNotification continueHandler) {
      this.continueNotification = continueHandler;
   }

   public void setPushHandler(PushCallback pushCallback) {
   }

   void setFailed(IOException e) {
      this.failedReason = e;
      if (this.readyCallback != null) {
         this.readyCallback.failed(e);
         this.readyCallback = null;
      }

      if (this.responseCallback != null) {
         this.responseCallback.failed(e);
         this.responseCallback = null;
      }

   }

   public StreamSinkChannel getRequestChannel() {
      return new DetachableStreamSinkChannel(this.requestChannel) {
         protected boolean isFinished() {
            return Bits.anyAreSet(AjpClientExchange.this.state, 1);
         }
      };
   }

   public StreamSourceChannel getResponseChannel() {
      return new DetachableStreamSourceChannel(this.responseChannel) {
         protected boolean isFinished() {
            return Bits.anyAreSet(AjpClientExchange.this.state, 2);
         }
      };
   }

   public ClientRequest getRequest() {
      return this.request;
   }

   public ClientResponse getResponse() {
      return this.response;
   }

   public ClientResponse getContinueResponse() {
      return this.continueResponse;
   }

   public ClientConnection getConnection() {
      return this.clientConnection;
   }

   void setResponseChannel(AjpClientResponseStreamSourceChannel responseChannel) {
      this.responseChannel = responseChannel;
   }

   void setRequestChannel(AjpClientRequestClientStreamSinkChannel requestChannel) {
      this.requestChannel = requestChannel;
   }

   void invokeReadReadyCallback(ClientExchange result) {
      if (this.readyCallback != null) {
         this.readyCallback.completed(result);
         this.readyCallback = null;
      }

   }
}
