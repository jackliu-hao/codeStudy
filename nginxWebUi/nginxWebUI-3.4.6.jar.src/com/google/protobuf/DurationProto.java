/*    */ package com.google.protobuf;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DurationProto
/*    */ {
/*    */   static final Descriptors.Descriptor internal_static_google_protobuf_Duration_descriptor;
/*    */   static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Duration_fieldAccessorTable;
/*    */   private static Descriptors.FileDescriptor descriptor;
/*    */   
/*    */   public static void registerAllExtensions(ExtensionRegistryLite registry) {}
/*    */   
/*    */   public static void registerAllExtensions(ExtensionRegistry registry) {
/* 14 */     registerAllExtensions(registry);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Descriptors.FileDescriptor getDescriptor() {
/* 25 */     return descriptor;
/*    */   }
/*    */ 
/*    */   
/*    */   static {
/* 30 */     String[] descriptorData = { "\n\036google/protobuf/duration.proto\022\017google.protobuf\"*\n\bDuration\022\017\n\007seconds\030\001 \001(\003\022\r\n\005nanos\030\002 \001(\005B|\n\023com.google.protobufB\rDurationProtoP\001Z*github.com/golang/protobuf/ptypes/durationø\001\001¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 39 */     descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
/*    */ 
/*    */ 
/*    */     
/* 43 */     internal_static_google_protobuf_Duration_descriptor = getDescriptor().getMessageTypes().get(0);
/* 44 */     internal_static_google_protobuf_Duration_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Duration_descriptor, new String[] { "Seconds", "Nanos" });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\DurationProto.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */