package com.google.protobuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class StructuralMessageInfo implements MessageInfo {
   private final ProtoSyntax syntax;
   private final boolean messageSetWireFormat;
   private final int[] checkInitialized;
   private final FieldInfo[] fields;
   private final MessageLite defaultInstance;

   StructuralMessageInfo(ProtoSyntax syntax, boolean messageSetWireFormat, int[] checkInitialized, FieldInfo[] fields, Object defaultInstance) {
      this.syntax = syntax;
      this.messageSetWireFormat = messageSetWireFormat;
      this.checkInitialized = checkInitialized;
      this.fields = fields;
      this.defaultInstance = (MessageLite)Internal.checkNotNull(defaultInstance, "defaultInstance");
   }

   public ProtoSyntax getSyntax() {
      return this.syntax;
   }

   public boolean isMessageSetWireFormat() {
      return this.messageSetWireFormat;
   }

   public int[] getCheckInitialized() {
      return this.checkInitialized;
   }

   public FieldInfo[] getFields() {
      return this.fields;
   }

   public MessageLite getDefaultInstance() {
      return this.defaultInstance;
   }

   public static Builder newBuilder() {
      return new Builder();
   }

   public static Builder newBuilder(int numFields) {
      return new Builder(numFields);
   }

   public static final class Builder {
      private final List<FieldInfo> fields;
      private ProtoSyntax syntax;
      private boolean wasBuilt;
      private boolean messageSetWireFormat;
      private int[] checkInitialized = null;
      private Object defaultInstance;

      public Builder() {
         this.fields = new ArrayList();
      }

      public Builder(int numFields) {
         this.fields = new ArrayList(numFields);
      }

      public void withDefaultInstance(Object defaultInstance) {
         this.defaultInstance = defaultInstance;
      }

      public void withSyntax(ProtoSyntax syntax) {
         this.syntax = (ProtoSyntax)Internal.checkNotNull(syntax, "syntax");
      }

      public void withMessageSetWireFormat(boolean messageSetWireFormat) {
         this.messageSetWireFormat = messageSetWireFormat;
      }

      public void withCheckInitialized(int[] checkInitialized) {
         this.checkInitialized = checkInitialized;
      }

      public void withField(FieldInfo field) {
         if (this.wasBuilt) {
            throw new IllegalStateException("Builder can only build once");
         } else {
            this.fields.add(field);
         }
      }

      public StructuralMessageInfo build() {
         if (this.wasBuilt) {
            throw new IllegalStateException("Builder can only build once");
         } else if (this.syntax == null) {
            throw new IllegalStateException("Must specify a proto syntax");
         } else {
            this.wasBuilt = true;
            Collections.sort(this.fields);
            return new StructuralMessageInfo(this.syntax, this.messageSetWireFormat, this.checkInitialized, (FieldInfo[])this.fields.toArray(new FieldInfo[0]), this.defaultInstance);
         }
      }
   }
}
