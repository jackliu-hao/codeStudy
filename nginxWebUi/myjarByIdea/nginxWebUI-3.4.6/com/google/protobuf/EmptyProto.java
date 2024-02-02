package com.google.protobuf;

public final class EmptyProto {
   static final Descriptors.Descriptor internal_static_google_protobuf_Empty_descriptor;
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Empty_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private EmptyProto() {
   }

   public static void registerAllExtensions(ExtensionRegistryLite registry) {
   }

   public static void registerAllExtensions(ExtensionRegistry registry) {
      registerAllExtensions((ExtensionRegistryLite)registry);
   }

   public static Descriptors.FileDescriptor getDescriptor() {
      return descriptor;
   }

   static {
      String[] descriptorData = new String[]{"\n\u001bgoogle/protobuf/empty.proto\u0012\u000fgoogle.protobuf\"\u0007\n\u0005EmptyBv\n\u0013com.google.protobufB\nEmptyProtoP\u0001Z'github.com/golang/protobuf/ptypes/emptyø\u0001\u0001¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      internal_static_google_protobuf_Empty_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_google_protobuf_Empty_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Empty_descriptor, new String[0]);
   }
}
