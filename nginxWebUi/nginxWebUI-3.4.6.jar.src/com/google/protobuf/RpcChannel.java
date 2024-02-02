package com.google.protobuf;

public interface RpcChannel {
  void callMethod(Descriptors.MethodDescriptor paramMethodDescriptor, RpcController paramRpcController, Message paramMessage1, Message paramMessage2, RpcCallback<Message> paramRpcCallback);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\RpcChannel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */