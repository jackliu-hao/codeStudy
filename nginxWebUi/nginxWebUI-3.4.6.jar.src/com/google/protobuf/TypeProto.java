/*     */ package com.google.protobuf;
/*     */ 
/*     */ 
/*     */ public final class TypeProto
/*     */ {
/*     */   static final Descriptors.Descriptor internal_static_google_protobuf_Type_descriptor;
/*     */   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Type_fieldAccessorTable;
/*     */   static final Descriptors.Descriptor internal_static_google_protobuf_Field_descriptor;
/*     */   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Field_fieldAccessorTable;
/*     */   static final Descriptors.Descriptor internal_static_google_protobuf_Enum_descriptor;
/*     */   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Enum_fieldAccessorTable;
/*     */   
/*     */   public static void registerAllExtensions(ExtensionRegistry registry) {
/*  14 */     registerAllExtensions(registry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final Descriptors.Descriptor internal_static_google_protobuf_EnumValue_descriptor;
/*     */ 
/*     */ 
/*     */   
/*     */   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_EnumValue_fieldAccessorTable;
/*     */ 
/*     */ 
/*     */   
/*     */   static final Descriptors.Descriptor internal_static_google_protobuf_Option_descriptor;
/*     */ 
/*     */ 
/*     */   
/*     */   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Option_fieldAccessorTable;
/*     */ 
/*     */ 
/*     */   
/*     */   private static Descriptors.FileDescriptor descriptor;
/*     */ 
/*     */ 
/*     */   
/*     */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public static Descriptors.FileDescriptor getDescriptor() {
/*  45 */     return descriptor;
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/*  50 */     String[] descriptorData = { "\n\032google/protobuf/type.proto\022\017google.protobuf\032\031google/protobuf/any.proto\032$google/protobuf/source_context.proto\"×\001\n\004Type\022\f\n\004name\030\001 \001(\t\022&\n\006fields\030\002 \003(\0132\026.google.protobuf.Field\022\016\n\006oneofs\030\003 \003(\t\022(\n\007options\030\004 \003(\0132\027.google.protobuf.Option\0226\n\016source_context\030\005 \001(\0132\036.google.protobuf.SourceContext\022'\n\006syntax\030\006 \001(\0162\027.google.protobuf.Syntax\"Õ\005\n\005Field\022)\n\004kind\030\001 \001(\0162\033.google.protobuf.Field.Kind\0227\n\013cardinality\030\002 \001(\0162\".google.protobuf.Field.Cardinality\022\016\n\006number\030\003 \001(\005\022\f\n\004name\030\004 \001(\t\022\020\n\btype_url\030\006 \001(\t\022\023\n\013oneof_index\030\007 \001(\005\022\016\n\006packed\030\b \001(\b\022(\n\007options\030\t \003(\0132\027.google.protobuf.Option\022\021\n\tjson_name\030\n \001(\t\022\025\n\rdefault_value\030\013 \001(\t\"È\002\n\004Kind\022\020\n\fTYPE_UNKNOWN\020\000\022\017\n\013TYPE_DOUBLE\020\001\022\016\n\nTYPE_FLOAT\020\002\022\016\n\nTYPE_INT64\020\003\022\017\n\013TYPE_UINT64\020\004\022\016\n\nTYPE_INT32\020\005\022\020\n\fTYPE_FIXED64\020\006\022\020\n\fTYPE_FIXED32\020\007\022\r\n\tTYPE_BOOL\020\b\022\017\n\013TYPE_STRING\020\t\022\016\n\nTYPE_GROUP\020\n\022\020\n\fTYPE_MESSAGE\020\013\022\016\n\nTYPE_BYTES\020\f\022\017\n\013TYPE_UINT32\020\r\022\r\n\tTYPE_ENUM\020\016\022\021\n\rTYPE_SFIXED32\020\017\022\021\n\rTYPE_SFIXED64\020\020\022\017\n\013TYPE_SINT32\020\021\022\017\n\013TYPE_SINT64\020\022\"t\n\013Cardinality\022\027\n\023CARDINALITY_UNKNOWN\020\000\022\030\n\024CARDINALITY_OPTIONAL\020\001\022\030\n\024CARDINALITY_REQUIRED\020\002\022\030\n\024CARDINALITY_REPEATED\020\003\"Î\001\n\004Enum\022\f\n\004name\030\001 \001(\t\022-\n\tenumvalue\030\002 \003(\0132\032.google.protobuf.EnumValue\022(\n\007options\030\003 \003(\0132\027.google.protobuf.Option\0226\n\016source_context\030\004 \001(\0132\036.google.protobuf.SourceContext\022'\n\006syntax\030\005 \001(\0162\027.google.protobuf.Syntax\"S\n\tEnumValue\022\f\n\004name\030\001 \001(\t\022\016\n\006number\030\002 \001(\005\022(\n\007options\030\003 \003(\0132\027.google.protobuf.Option\";\n\006Option\022\f\n\004name\030\001 \001(\t\022#\n\005value\030\002 \001(\0132\024.google.protobuf.Any*.\n\006Syntax\022\021\n\rSYNTAX_PROTO2\020\000\022\021\n\rSYNTAX_PROTO3\020\001B}\n\023com.google.protobufB\tTypeProtoP\001Z/google.golang.org/genproto/protobuf/ptype;ptypeø\001\001¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] {
/*     */           
/*  95 */           AnyProto.getDescriptor(), 
/*  96 */           SourceContextProto.getDescriptor()
/*     */         });
/*     */     
/*  99 */     internal_static_google_protobuf_Type_descriptor = getDescriptor().getMessageTypes().get(0);
/* 100 */     internal_static_google_protobuf_Type_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Type_descriptor, new String[] { "Name", "Fields", "Oneofs", "Options", "SourceContext", "Syntax" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 105 */     internal_static_google_protobuf_Field_descriptor = getDescriptor().getMessageTypes().get(1);
/* 106 */     internal_static_google_protobuf_Field_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Field_descriptor, new String[] { "Kind", "Cardinality", "Number", "Name", "TypeUrl", "OneofIndex", "Packed", "Options", "JsonName", "DefaultValue" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     internal_static_google_protobuf_Enum_descriptor = getDescriptor().getMessageTypes().get(2);
/* 112 */     internal_static_google_protobuf_Enum_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Enum_descriptor, new String[] { "Name", "Enumvalue", "Options", "SourceContext", "Syntax" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     internal_static_google_protobuf_EnumValue_descriptor = getDescriptor().getMessageTypes().get(3);
/* 118 */     internal_static_google_protobuf_EnumValue_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_EnumValue_descriptor, new String[] { "Name", "Number", "Options" });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 123 */     internal_static_google_protobuf_Option_descriptor = getDescriptor().getMessageTypes().get(4);
/* 124 */     internal_static_google_protobuf_Option_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Option_descriptor, new String[] { "Name", "Value" });
/*     */ 
/*     */ 
/*     */     
/* 128 */     AnyProto.getDescriptor();
/* 129 */     SourceContextProto.getDescriptor();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TypeProto.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */