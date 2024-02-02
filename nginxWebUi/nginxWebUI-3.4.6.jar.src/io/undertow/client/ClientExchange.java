package io.undertow.client;

import io.undertow.util.Attachable;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.channels.StreamSourceChannel;

public interface ClientExchange extends Attachable {
  void setResponseListener(ClientCallback<ClientExchange> paramClientCallback);
  
  void setContinueHandler(ContinueNotification paramContinueNotification);
  
  void setPushHandler(PushCallback paramPushCallback);
  
  StreamSinkChannel getRequestChannel();
  
  StreamSourceChannel getResponseChannel();
  
  ClientRequest getRequest();
  
  ClientResponse getResponse();
  
  ClientResponse getContinueResponse();
  
  ClientConnection getConnection();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ClientExchange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */