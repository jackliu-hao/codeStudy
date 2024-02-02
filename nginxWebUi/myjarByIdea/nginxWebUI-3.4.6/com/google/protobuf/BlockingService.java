package com.google.protobuf;

public interface BlockingService {
   Descriptors.ServiceDescriptor getDescriptorForType();

   Message callBlockingMethod(Descriptors.MethodDescriptor var1, RpcController var2, Message var3) throws ServiceException;

   Message getRequestPrototype(Descriptors.MethodDescriptor var1);

   Message getResponsePrototype(Descriptors.MethodDescriptor var1);
}
