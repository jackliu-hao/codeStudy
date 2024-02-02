package com.google.protobuf;

public interface Service {
   Descriptors.ServiceDescriptor getDescriptorForType();

   void callMethod(Descriptors.MethodDescriptor var1, RpcController var2, Message var3, RpcCallback<Message> var4);

   Message getRequestPrototype(Descriptors.MethodDescriptor var1);

   Message getResponsePrototype(Descriptors.MethodDescriptor var1);
}
