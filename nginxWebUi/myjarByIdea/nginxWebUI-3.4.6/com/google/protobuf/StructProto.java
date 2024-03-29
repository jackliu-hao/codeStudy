package com.google.protobuf;

public final class StructProto {
   static final Descriptors.Descriptor internal_static_google_protobuf_Struct_descriptor;
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Struct_fieldAccessorTable;
   static final Descriptors.Descriptor internal_static_google_protobuf_Struct_FieldsEntry_descriptor;
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Struct_FieldsEntry_fieldAccessorTable;
   static final Descriptors.Descriptor internal_static_google_protobuf_Value_descriptor;
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Value_fieldAccessorTable;
   static final Descriptors.Descriptor internal_static_google_protobuf_ListValue_descriptor;
   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_ListValue_fieldAccessorTable;
   private static Descriptors.FileDescriptor descriptor;

   private StructProto() {
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
      String[] descriptorData = new String[]{"\n\u001cgoogle/protobuf/struct.proto\u0012\u000fgoogle.protobuf\"\u0084\u0001\n\u0006Struct\u00123\n\u0006fields\u0018\u0001 \u0003(\u000b2#.google.protobuf.Struct.FieldsEntry\u001aE\n\u000bFieldsEntry\u0012\u000b\n\u0003key\u0018\u0001 \u0001(\t\u0012%\n\u0005value\u0018\u0002 \u0001(\u000b2\u0016.google.protobuf.Value:\u00028\u0001\"ê\u0001\n\u0005Value\u00120\n\nnull_value\u0018\u0001 \u0001(\u000e2\u001a.google.protobuf.NullValueH\u0000\u0012\u0016\n\fnumber_value\u0018\u0002 \u0001(\u0001H\u0000\u0012\u0016\n\fstring_value\u0018\u0003 \u0001(\tH\u0000\u0012\u0014\n\nbool_value\u0018\u0004 \u0001(\bH\u0000\u0012/\n\fstruct_value\u0018\u0005 \u0001(\u000b2\u0017.google.protobuf.StructH\u0000\u00120\n\nlist_value\u0018\u0006 \u0001(\u000b2\u001a.google.protobuf.ListValueH\u0000B\u0006\n\u0004kind\"3\n\tListValue\u0012&\n\u0006values\u0018\u0001 \u0003(\u000b2\u0016.google.protobuf.Value*\u001b\n\tNullValue\u0012\u000e\n\nNULL_VALUE\u0010\u0000B\u0081\u0001\n\u0013com.google.protobufB\u000bStructProtoP\u0001Z1github.com/golang/protobuf/ptypes/struct;structpbø\u0001\u0001¢\u0002\u0003GPBª\u0002\u001eGoogle.Protobuf.WellKnownTypesb\u0006proto3"};
      descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
      internal_static_google_protobuf_Struct_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(0);
      internal_static_google_protobuf_Struct_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Struct_descriptor, new String[]{"Fields"});
      internal_static_google_protobuf_Struct_FieldsEntry_descriptor = (Descriptors.Descriptor)internal_static_google_protobuf_Struct_descriptor.getNestedTypes().get(0);
      internal_static_google_protobuf_Struct_FieldsEntry_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Struct_FieldsEntry_descriptor, new String[]{"Key", "Value"});
      internal_static_google_protobuf_Value_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(1);
      internal_static_google_protobuf_Value_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Value_descriptor, new String[]{"NullValue", "NumberValue", "StringValue", "BoolValue", "StructValue", "ListValue", "Kind"});
      internal_static_google_protobuf_ListValue_descriptor = (Descriptors.Descriptor)getDescriptor().getMessageTypes().get(2);
      internal_static_google_protobuf_ListValue_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_ListValue_descriptor, new String[]{"Values"});
   }
}
