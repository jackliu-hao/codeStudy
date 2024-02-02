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
/*     */ public enum Syntax
/*     */   implements ProtocolMessageEnum
/*     */ {
/*  22 */   SYNTAX_PROTO2(0),
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  30 */   SYNTAX_PROTO3(1),
/*  31 */   UNRECOGNIZED(-1);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SYNTAX_PROTO2_VALUE = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SYNTAX_PROTO3_VALUE = 1;
/*     */ 
/*     */   
/*     */   private static final Internal.EnumLiteMap<Syntax> internalValueMap;
/*     */ 
/*     */   
/*     */   private static final Syntax[] VALUES;
/*     */ 
/*     */   
/*     */   private final int value;
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getNumber() {
/*  53 */     if (this == UNRECOGNIZED) {
/*  54 */       throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
/*     */     }
/*     */     
/*  57 */     return this.value;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Syntax forNumber(int value) {
/*  75 */     switch (value) { case 0:
/*  76 */         return SYNTAX_PROTO2;
/*  77 */       case 1: return SYNTAX_PROTO3; }
/*  78 */      return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Internal.EnumLiteMap<Syntax> internalGetValueMap() {
/*  84 */     return internalValueMap;
/*     */   }
/*     */   static {
/*  87 */     internalValueMap = new Internal.EnumLiteMap<Syntax>()
/*     */       {
/*     */         public Syntax findValueByNumber(int number) {
/*  90 */           return Syntax.forNumber(number);
/*     */         }
/*     */       };
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
/* 107 */     VALUES = values();
/*     */   }
/*     */ 
/*     */   
/*     */   public final Descriptors.EnumValueDescriptor getValueDescriptor() {
/*     */     return getDescriptor().getValues().get(ordinal());
/*     */   }
/*     */   
/*     */   public final Descriptors.EnumDescriptor getDescriptorForType() {
/*     */     return getDescriptor();
/*     */   }
/*     */   
/*     */   public static final Descriptors.EnumDescriptor getDescriptor() {
/*     */     return TypeProto.getDescriptor().getEnumTypes().get(0);
/*     */   }
/*     */   
/*     */   Syntax(int value) {
/* 124 */     this.value = value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\Syntax.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */