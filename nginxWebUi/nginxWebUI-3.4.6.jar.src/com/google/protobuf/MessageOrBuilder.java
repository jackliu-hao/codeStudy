package com.google.protobuf;

import java.util.List;
import java.util.Map;

public interface MessageOrBuilder extends MessageLiteOrBuilder {
  Message getDefaultInstanceForType();
  
  List<String> findInitializationErrors();
  
  String getInitializationErrorString();
  
  Descriptors.Descriptor getDescriptorForType();
  
  Map<Descriptors.FieldDescriptor, Object> getAllFields();
  
  boolean hasOneof(Descriptors.OneofDescriptor paramOneofDescriptor);
  
  Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor paramOneofDescriptor);
  
  boolean hasField(Descriptors.FieldDescriptor paramFieldDescriptor);
  
  Object getField(Descriptors.FieldDescriptor paramFieldDescriptor);
  
  int getRepeatedFieldCount(Descriptors.FieldDescriptor paramFieldDescriptor);
  
  Object getRepeatedField(Descriptors.FieldDescriptor paramFieldDescriptor, int paramInt);
  
  UnknownFieldSet getUnknownFields();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\MessageOrBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */