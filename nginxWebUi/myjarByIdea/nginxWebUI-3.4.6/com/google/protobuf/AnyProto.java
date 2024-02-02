package com.google.protobuf;

public final class AnyProto {
   static final Descriptors.Descriptor internal_static_google_protobuf_Any_descriptor;
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Any_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private AnyProto() {
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
      String[] descriptorData = new String[]{"\n\u0019google/protobuf/any.proto\u0012\u000fgoogle.protobuf\"&\n\u0003Any\u0012\u0010\n\btype_url\u0018\u0001 \u0001(\t\u0012\r\n\u0005value\u0018\u0002 \u0001(\fBo\n\u0013com.google.protobufB\bAnyProtoP\u0001Z%github.com/golang/protobuf/ptypes/any¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      internal_static_google_protobuf_Any_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_google_protobuf_Any_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Any_descriptor, new String[]{"TypeUrl", "Value"});
   }
}
