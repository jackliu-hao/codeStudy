/*    */ package com.sun.jna.win32;
/*    */ 
/*    */ import com.sun.jna.DefaultTypeMapper;
/*    */ import com.sun.jna.FromNativeContext;
/*    */ import com.sun.jna.StringArray;
/*    */ import com.sun.jna.ToNativeContext;
/*    */ import com.sun.jna.ToNativeConverter;
/*    */ import com.sun.jna.TypeConverter;
/*    */ import com.sun.jna.TypeMapper;
/*    */ import com.sun.jna.WString;
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
/*    */ public class W32APITypeMapper
/*    */   extends DefaultTypeMapper
/*    */ {
/* 44 */   public static final TypeMapper UNICODE = (TypeMapper)new W32APITypeMapper(true);
/*    */   
/* 46 */   public static final TypeMapper ASCII = (TypeMapper)new W32APITypeMapper(false);
/*    */   
/* 48 */   public static final TypeMapper DEFAULT = Boolean.getBoolean("w32.ascii") ? ASCII : UNICODE;
/*    */   
/*    */   protected W32APITypeMapper(boolean unicode) {
/* 51 */     if (unicode) {
/* 52 */       TypeConverter stringConverter = new TypeConverter()
/*    */         {
/*    */           public Object toNative(Object value, ToNativeContext context) {
/* 55 */             if (value == null)
/* 56 */               return null; 
/* 57 */             if (value instanceof String[]) {
/* 58 */               return new StringArray((String[])value, true);
/*    */             }
/* 60 */             return new WString(value.toString());
/*    */           }
/*    */           
/*    */           public Object fromNative(Object value, FromNativeContext context) {
/* 64 */             if (value == null)
/* 65 */               return null; 
/* 66 */             return value.toString();
/*    */           }
/*    */           
/*    */           public Class<?> nativeType() {
/* 70 */             return WString.class;
/*    */           }
/*    */         };
/* 73 */       addTypeConverter(String.class, stringConverter);
/* 74 */       addToNativeConverter(String[].class, (ToNativeConverter)stringConverter);
/*    */     } 
/* 76 */     TypeConverter booleanConverter = new TypeConverter()
/*    */       {
/*    */         public Object toNative(Object value, ToNativeContext context) {
/* 79 */           return Integer.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
/*    */         }
/*    */         
/*    */         public Object fromNative(Object value, FromNativeContext context) {
/* 83 */           return (((Integer)value).intValue() != 0) ? Boolean.TRUE : Boolean.FALSE;
/*    */         }
/*    */ 
/*    */         
/*    */         public Class<?> nativeType() {
/* 88 */           return Integer.class;
/*    */         }
/*    */       };
/* 91 */     addTypeConverter(Boolean.class, booleanConverter);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\win32\W32APITypeMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */