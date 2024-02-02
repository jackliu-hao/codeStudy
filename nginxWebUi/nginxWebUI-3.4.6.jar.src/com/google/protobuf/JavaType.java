/*    */ package com.google.protobuf;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum JavaType
/*    */ {
/* 36 */   VOID(Void.class, Void.class, null),
/* 37 */   INT(int.class, Integer.class, Integer.valueOf(0)),
/* 38 */   LONG(long.class, Long.class, Long.valueOf(0L)),
/* 39 */   FLOAT(float.class, Float.class, Float.valueOf(0.0F)),
/* 40 */   DOUBLE(double.class, Double.class, Double.valueOf(0.0D)),
/* 41 */   BOOLEAN(boolean.class, Boolean.class, Boolean.valueOf(false)),
/* 42 */   STRING(String.class, String.class, ""),
/* 43 */   BYTE_STRING(ByteString.class, ByteString.class, ByteString.EMPTY),
/* 44 */   ENUM(int.class, Integer.class, null),
/* 45 */   MESSAGE(Object.class, Object.class, null);
/*    */   
/*    */   private final Class<?> type;
/*    */   private final Class<?> boxedType;
/*    */   private final Object defaultDefault;
/*    */   
/*    */   JavaType(Class<?> type, Class<?> boxedType, Object defaultDefault) {
/* 52 */     this.type = type;
/* 53 */     this.boxedType = boxedType;
/* 54 */     this.defaultDefault = defaultDefault;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getDefaultDefault() {
/* 59 */     return this.defaultDefault;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getType() {
/* 64 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> getBoxedType() {
/* 69 */     return this.boxedType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isValidType(Class<?> t) {
/* 74 */     return this.type.isAssignableFrom(t);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\JavaType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */