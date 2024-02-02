package com.google.protobuf;

public interface ProtocolMessageEnum extends Internal.EnumLite {
   int getNumber();

   Descriptors.EnumValueDescriptor getValueDescriptor();

   Descriptors.EnumDescriptor getDescriptorForType();
}
