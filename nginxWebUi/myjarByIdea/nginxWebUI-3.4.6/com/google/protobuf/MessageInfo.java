package com.google.protobuf;

interface MessageInfo {
   ProtoSyntax getSyntax();

   boolean isMessageSetWireFormat();

   MessageLite getDefaultInstance();
}
