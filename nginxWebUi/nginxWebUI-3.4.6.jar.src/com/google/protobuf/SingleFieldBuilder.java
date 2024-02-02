/*     */ package com.google.protobuf;
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
/*     */ public class SingleFieldBuilder<MType extends GeneratedMessage, BType extends GeneratedMessage.Builder, IType extends MessageOrBuilder>
/*     */   implements GeneratedMessage.BuilderParent
/*     */ {
/*     */   private GeneratedMessage.BuilderParent parent;
/*     */   private BType builder;
/*     */   private MType message;
/*     */   private boolean isClean;
/*     */   
/*     */   public SingleFieldBuilder(MType message, GeneratedMessage.BuilderParent parent, boolean isClean) {
/*  80 */     this.message = (MType)Internal.<GeneratedMessage>checkNotNull((GeneratedMessage)message);
/*  81 */     this.parent = parent;
/*  82 */     this.isClean = isClean;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispose() {
/*  87 */     this.parent = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MType getMessage() {
/*  99 */     if (this.message == null)
/*     */     {
/* 101 */       this.message = (MType)this.builder.buildPartial();
/*     */     }
/* 103 */     return this.message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MType build() {
/* 114 */     this.isClean = true;
/* 115 */     return getMessage();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BType getBuilder() {
/* 126 */     if (this.builder == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 131 */       this.builder = (BType)this.message.newBuilderForType(this);
/* 132 */       this.builder.mergeFrom((Message)this.message);
/* 133 */       this.builder.markClean();
/*     */     } 
/* 135 */     return this.builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IType getMessageOrBuilder() {
/* 146 */     if (this.builder != null) {
/* 147 */       return (IType)this.builder;
/*     */     }
/* 149 */     return (IType)this.message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SingleFieldBuilder<MType, BType, IType> setMessage(MType message) {
/* 160 */     this.message = (MType)Internal.<GeneratedMessage>checkNotNull((GeneratedMessage)message);
/* 161 */     if (this.builder != null) {
/* 162 */       this.builder.dispose();
/* 163 */       this.builder = null;
/*     */     } 
/* 165 */     onChanged();
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SingleFieldBuilder<MType, BType, IType> mergeFrom(MType value) {
/* 176 */     if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
/* 177 */       this.message = value;
/*     */     } else {
/* 179 */       getBuilder().mergeFrom((Message)value);
/*     */     } 
/* 181 */     onChanged();
/* 182 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SingleFieldBuilder<MType, BType, IType> clear() {
/* 192 */     this
/*     */ 
/*     */ 
/*     */       
/* 196 */       .message = (MType)((this.message != null) ? (GeneratedMessage)this.message.getDefaultInstanceForType() : (GeneratedMessage)this.builder.getDefaultInstanceForType());
/* 197 */     if (this.builder != null) {
/* 198 */       this.builder.dispose();
/* 199 */       this.builder = null;
/*     */     } 
/* 201 */     onChanged();
/* 202 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onChanged() {
/* 212 */     if (this.builder != null) {
/* 213 */       this.message = null;
/*     */     }
/* 215 */     if (this.isClean && this.parent != null) {
/* 216 */       this.parent.markDirty();
/*     */ 
/*     */       
/* 219 */       this.isClean = false;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void markDirty() {
/* 225 */     onChanged();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\SingleFieldBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */