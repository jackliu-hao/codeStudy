package com.google.protobuf;

public class SingleFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder> implements GeneratedMessage.BuilderParent {
   private GeneratedMessage.BuilderParent parent;
   private BType builder;
   private MType message;
   private boolean isClean;

   public SingleFieldBuilder(MType message, GeneratedMessage.BuilderParent parent, boolean isClean) {
      this.message = (GeneratedMessage)Internal.checkNotNull(message);
      this.parent = parent;
      this.isClean = isClean;
   }

   public void dispose() {
      this.parent = null;
   }

   public MType getMessage() {
      if (this.message == null) {
         this.message = (GeneratedMessage)this.builder.buildPartial();
      }

      return this.message;
   }

   public MType build() {
      this.isClean = true;
      return this.getMessage();
   }

   public BType getBuilder() {
      if (this.builder == null) {
         this.builder = (GeneratedMessage.Builder)this.message.newBuilderForType((GeneratedMessage.BuilderParent)this);
         this.builder.mergeFrom(this.message);
         this.builder.markClean();
      }

      return this.builder;
   }

   public IType getMessageOrBuilder() {
      return (MessageOrBuilder)(this.builder != null ? this.builder : this.message);
   }

   public SingleFieldBuilder<MType, BType, IType> setMessage(MType message) {
      this.message = (GeneratedMessage)Internal.checkNotNull(message);
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      return this;
   }

   public SingleFieldBuilder<MType, BType, IType> mergeFrom(MType value) {
      if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
         this.message = value;
      } else {
         this.getBuilder().mergeFrom(value);
      }

      this.onChanged();
      return this;
   }

   public SingleFieldBuilder<MType, BType, IType> clear() {
      this.message = (GeneratedMessage)((GeneratedMessage)(this.message != null ? this.message.getDefaultInstanceForType() : this.builder.getDefaultInstanceForType()));
      if (this.builder != null) {
         this.builder.dispose();
         this.builder = null;
      }

      this.onChanged();
      return this;
   }

   private void onChanged() {
      if (this.builder != null) {
         this.message = null;
      }

      if (this.isClean && this.parent != null) {
         this.parent.markDirty();
         this.isClean = false;
      }

   }

   public void markDirty() {
      this.onChanged();
   }
}
