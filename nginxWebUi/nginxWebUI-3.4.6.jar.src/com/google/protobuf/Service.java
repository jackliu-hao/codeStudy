package com.google.protobuf;

public interface Service {
  Descriptors.ServiceDescriptor getDescriptorForType();
  
  void callMethod(Descriptors.MethodDescriptor paramMethodDescriptor, RpcController paramRpcController, Message paramMessage, RpcCallback<Message> paramRpcCallback);
  
  Message getRequestPrototype(Descriptors.MethodDescriptor paramMethodDescriptor);
  
  Message getResponsePrototype(Descriptors.MethodDescriptor paramMethodDescriptor);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Service.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */