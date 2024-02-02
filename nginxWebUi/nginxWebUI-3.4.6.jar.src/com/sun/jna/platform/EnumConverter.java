/*    */ package com.sun.jna.platform;
/*    */ 
/*    */ import com.sun.jna.FromNativeContext;
/*    */ import com.sun.jna.ToNativeContext;
/*    */ import com.sun.jna.TypeConverter;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EnumConverter<T extends Enum<T>>
/*    */   implements TypeConverter
/*    */ {
/*    */   private final Class<T> clazz;
/*    */   
/*    */   public EnumConverter(Class<T> clazz) {
/* 47 */     this.clazz = clazz;
/*    */   }
/*    */ 
/*    */   
/*    */   public T fromNative(Object input, FromNativeContext context) {
/* 52 */     Integer i = (Integer)input;
/*    */     
/* 54 */     Enum[] arrayOfEnum = (Enum[])this.clazz.getEnumConstants();
/* 55 */     return (T)arrayOfEnum[i.intValue()];
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer toNative(Object input, ToNativeContext context) {
/* 60 */     Enum enum_ = (Enum)this.clazz.cast(input);
/*    */     
/* 62 */     return Integer.valueOf(enum_.ordinal());
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<Integer> nativeType() {
/* 67 */     return Integer.class;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\EnumConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */