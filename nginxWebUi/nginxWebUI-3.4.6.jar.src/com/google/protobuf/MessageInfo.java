package com.google.protobuf;

interface MessageInfo {
  ProtoSyntax getSyntax();
  
  boolean isMessageSetWireFormat();
  
  MessageLite getDefaultInstance();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */