package com.google.protobuf;

interface MessageInfoFactory {
  boolean isSupported(Class<?> paramClass);
  
  MessageInfo messageInfoFor(Class<?> paramClass);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageInfoFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */