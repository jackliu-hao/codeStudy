/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ final class StructuralMessageInfo
/*     */   implements MessageInfo
/*     */ {
/*     */   private final ProtoSyntax syntax;
/*     */   private final boolean messageSetWireFormat;
/*     */   private final int[] checkInitialized;
/*     */   private final FieldInfo[] fields;
/*     */   private final MessageLite defaultInstance;
/*     */   
/*     */   StructuralMessageInfo(ProtoSyntax syntax, boolean messageSetWireFormat, int[] checkInitialized, FieldInfo[] fields, Object defaultInstance) {
/*  63 */     this.syntax = syntax;
/*  64 */     this.messageSetWireFormat = messageSetWireFormat;
/*  65 */     this.checkInitialized = checkInitialized;
/*  66 */     this.fields = fields;
/*  67 */     this.defaultInstance = (MessageLite)Internal.<Object>checkNotNull(defaultInstance, "defaultInstance");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtoSyntax getSyntax() {
/*  73 */     return this.syntax;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isMessageSetWireFormat() {
/*  79 */     return this.messageSetWireFormat;
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getCheckInitialized() {
/*  84 */     return this.checkInitialized;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FieldInfo[] getFields() {
/*  92 */     return this.fields;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageLite getDefaultInstance() {
/*  97 */     return this.defaultInstance;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder newBuilder() {
/* 102 */     return new Builder();
/*     */   }
/*     */ 
/*     */   
/*     */   public static Builder newBuilder(int numFields) {
/* 107 */     return new Builder(numFields);
/*     */   }
/*     */   
/*     */   public static final class Builder
/*     */   {
/*     */     private final List<FieldInfo> fields;
/*     */     private ProtoSyntax syntax;
/*     */     private boolean wasBuilt;
/*     */     private boolean messageSetWireFormat;
/* 116 */     private int[] checkInitialized = null;
/*     */     private Object defaultInstance;
/*     */     
/*     */     public Builder() {
/* 120 */       this.fields = new ArrayList<>();
/*     */     }
/*     */     
/*     */     public Builder(int numFields) {
/* 124 */       this.fields = new ArrayList<>(numFields);
/*     */     }
/*     */     
/*     */     public void withDefaultInstance(Object defaultInstance) {
/* 128 */       this.defaultInstance = defaultInstance;
/*     */     }
/*     */     
/*     */     public void withSyntax(ProtoSyntax syntax) {
/* 132 */       this.syntax = Internal.<ProtoSyntax>checkNotNull(syntax, "syntax");
/*     */     }
/*     */     
/*     */     public void withMessageSetWireFormat(boolean messageSetWireFormat) {
/* 136 */       this.messageSetWireFormat = messageSetWireFormat;
/*     */     }
/*     */     
/*     */     public void withCheckInitialized(int[] checkInitialized) {
/* 140 */       this.checkInitialized = checkInitialized;
/*     */     }
/*     */     
/*     */     public void withField(FieldInfo field) {
/* 144 */       if (this.wasBuilt) {
/* 145 */         throw new IllegalStateException("Builder can only build once");
/*     */       }
/* 147 */       this.fields.add(field);
/*     */     }
/*     */     
/*     */     public StructuralMessageInfo build() {
/* 151 */       if (this.wasBuilt) {
/* 152 */         throw new IllegalStateException("Builder can only build once");
/*     */       }
/* 154 */       if (this.syntax == null) {
/* 155 */         throw new IllegalStateException("Must specify a proto syntax");
/*     */       }
/* 157 */       this.wasBuilt = true;
/* 158 */       Collections.sort(this.fields);
/* 159 */       return new StructuralMessageInfo(this.syntax, this.messageSetWireFormat, this.checkInitialized, this.fields
/*     */ 
/*     */ 
/*     */           
/* 163 */           .<FieldInfo>toArray(new FieldInfo[0]), this.defaultInstance);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\StructuralMessageInfo.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */