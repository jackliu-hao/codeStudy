package com.google.protobuf;

public interface BlockingRpcChannel {
  Message callBlockingMethod(Descriptors.MethodDescriptor paramMethodDescriptor, RpcController paramRpcController, Message paramMessage1, Message paramMessage2) throws ServiceException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\BlockingRpcChannel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */