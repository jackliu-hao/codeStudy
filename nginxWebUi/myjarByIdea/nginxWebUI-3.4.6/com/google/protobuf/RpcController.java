package com.google.protobuf;

public interface RpcController {
   void reset();

   boolean failed();

   String errorText();

   void startCancel();

   void setFailed(String var1);

   boolean isCanceled();

   void notifyOnCancel(RpcCallback<Object> var1);
}
