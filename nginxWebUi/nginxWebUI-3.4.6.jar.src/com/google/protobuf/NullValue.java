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
/*     */ public enum NullValue
/*     */   implements ProtocolMessageEnum
/*     */ {
/*  24 */   NULL_VALUE(0),
/*  25 */   UNRECOGNIZED(-1);
/*     */ 
/*     */   
/*     */   public static final int NULL_VALUE_VALUE = 0;
/*     */ 
/*     */   
/*     */   private static final Internal.EnumLiteMap<NullValue> internalValueMap;
/*     */   
/*     */   private static final NullValue[] VALUES;
/*     */   
/*     */   private final int value;
/*     */ 
/*     */   
/*     */   public final int getNumber() {
/*  39 */     if (this == UNRECOGNIZED) {
/*  40 */       throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
/*     */     }
/*     */     
/*  43 */     return this.value;
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
/*     */   public static NullValue forNumber(int value) {
/*  61 */     switch (value) { case 0:
/*  62 */         return NULL_VALUE; }
/*  63 */      return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Internal.EnumLiteMap<NullValue> internalGetValueMap() {
/*  69 */     return internalValueMap;
/*     */   }
/*     */   static {
/*  72 */     internalValueMap = new Internal.EnumLiteMap<NullValue>()
/*     */       {
/*     */         public NullValue findValueByNumber(int number) {
/*  75 */           return NullValue.forNumber(number);
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
/*  92 */     VALUES = values();
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
/*     */     return StructProto.getDescriptor().getEnumTypes().get(0);
/*     */   }
/*     */   
/*     */   NullValue(int value) {
/* 109 */     this.value = value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\NullValue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */