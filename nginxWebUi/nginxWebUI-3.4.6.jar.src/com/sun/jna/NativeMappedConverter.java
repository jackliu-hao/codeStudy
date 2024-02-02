/*    */ package com.sun.jna;
/*    */ 
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.SoftReference;
/*    */ import java.util.Map;
/*    */ import java.util.WeakHashMap;
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
/*    */ public class NativeMappedConverter
/*    */   implements TypeConverter
/*    */ {
/* 34 */   private static final Map<Class<?>, Reference<NativeMappedConverter>> converters = new WeakHashMap<Class<?>, Reference<NativeMappedConverter>>();
/*    */   
/*    */   private final Class<?> type;
/*    */   private final Class<?> nativeType;
/*    */   private final NativeMapped instance;
/*    */   
/*    */   public static NativeMappedConverter getInstance(Class<?> cls) {
/* 41 */     synchronized (converters) {
/* 42 */       Reference<NativeMappedConverter> r = converters.get(cls);
/* 43 */       NativeMappedConverter nmc = (r != null) ? r.get() : null;
/* 44 */       if (nmc == null) {
/* 45 */         nmc = new NativeMappedConverter(cls);
/* 46 */         converters.put(cls, new SoftReference<NativeMappedConverter>(nmc));
/*    */       } 
/* 48 */       return nmc;
/*    */     } 
/*    */   }
/*    */   
/*    */   public NativeMappedConverter(Class<?> type) {
/* 53 */     if (!NativeMapped.class.isAssignableFrom(type))
/* 54 */       throw new IllegalArgumentException("Type must derive from " + NativeMapped.class); 
/* 55 */     this.type = type;
/* 56 */     this.instance = defaultValue();
/* 57 */     this.nativeType = this.instance.nativeType();
/*    */   }
/*    */   
/*    */   public NativeMapped defaultValue() {
/* 61 */     if (this.type.isEnum()) {
/* 62 */       return (NativeMapped)this.type.getEnumConstants()[0];
/*    */     }
/*    */     
/* 65 */     return (NativeMapped)Klass.newInstance(this.type);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object fromNative(Object nativeValue, FromNativeContext context) {
/* 70 */     return this.instance.fromNative(nativeValue, context);
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> nativeType() {
/* 75 */     return this.nativeType;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object toNative(Object value, ToNativeContext context) {
/* 80 */     if (value == null) {
/* 81 */       if (Pointer.class.isAssignableFrom(this.nativeType)) {
/* 82 */         return null;
/*    */       }
/* 84 */       value = defaultValue();
/*    */     } 
/* 86 */     return ((NativeMapped)value).toNative();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\NativeMappedConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */