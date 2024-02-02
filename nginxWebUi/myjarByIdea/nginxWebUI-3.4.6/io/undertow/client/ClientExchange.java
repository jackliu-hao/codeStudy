package io.undertow.client;

import io.undertow.util.Attachable;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public interface ClientExchange extends Attachable {
   void setResponseListener(ClientCallback<ClientExchange> var1);

   void setContinueHandler(ContinueNotification var1);

   void setPushHandler(PushCallback var1);

   StreamSinkChannel getRequestChannel();

   StreamSourceChannel getResponseChannel();

   ClientRequest getRequest();

   ClientResponse getResponse();

   ClientResponse getContinueResponse();

   ClientConnection getConnection();
}
