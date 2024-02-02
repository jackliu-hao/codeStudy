/*     */ package com.sun.jna;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
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
/*     */ public class DefaultTypeMapper
/*     */   implements TypeMapper
/*     */ {
/*     */   private static class Entry
/*     */   {
/*     */     public Class<?> type;
/*     */     public Object converter;
/*     */     
/*     */     public Entry(Class<?> type, Object converter) {
/*  50 */       this.type = type;
/*  51 */       this.converter = converter;
/*     */     }
/*     */   }
/*     */   
/*  55 */   private List<Entry> toNativeConverters = new ArrayList<Entry>();
/*  56 */   private List<Entry> fromNativeConverters = new ArrayList<Entry>();
/*     */   
/*     */   private Class<?> getAltClass(Class<?> cls) {
/*  59 */     if (cls == Boolean.class)
/*  60 */       return boolean.class; 
/*  61 */     if (cls == boolean.class)
/*  62 */       return Boolean.class; 
/*  63 */     if (cls == Byte.class)
/*  64 */       return byte.class; 
/*  65 */     if (cls == byte.class)
/*  66 */       return Byte.class; 
/*  67 */     if (cls == Character.class)
/*  68 */       return char.class; 
/*  69 */     if (cls == char.class)
/*  70 */       return Character.class; 
/*  71 */     if (cls == Short.class)
/*  72 */       return short.class; 
/*  73 */     if (cls == short.class)
/*  74 */       return Short.class; 
/*  75 */     if (cls == Integer.class)
/*  76 */       return int.class; 
/*  77 */     if (cls == int.class)
/*  78 */       return Integer.class; 
/*  79 */     if (cls == Long.class)
/*  80 */       return long.class; 
/*  81 */     if (cls == long.class)
/*  82 */       return Long.class; 
/*  83 */     if (cls == Float.class)
/*  84 */       return float.class; 
/*  85 */     if (cls == float.class)
/*  86 */       return Float.class; 
/*  87 */     if (cls == Double.class)
/*  88 */       return double.class; 
/*  89 */     if (cls == double.class) {
/*  90 */       return Double.class;
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addToNativeConverter(Class<?> cls, ToNativeConverter converter) {
/* 102 */     this.toNativeConverters.add(new Entry(cls, converter));
/* 103 */     Class<?> alt = getAltClass(cls);
/* 104 */     if (alt != null) {
/* 105 */       this.toNativeConverters.add(new Entry(alt, converter));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFromNativeConverter(Class<?> cls, FromNativeConverter converter) {
/* 117 */     this.fromNativeConverters.add(new Entry(cls, converter));
/* 118 */     Class<?> alt = getAltClass(cls);
/* 119 */     if (alt != null) {
/* 120 */       this.fromNativeConverters.add(new Entry(alt, converter));
/*     */     }
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
/*     */   public void addTypeConverter(Class<?> cls, TypeConverter converter) {
/* 133 */     addFromNativeConverter(cls, converter);
/* 134 */     addToNativeConverter(cls, converter);
/*     */   }
/*     */   
/*     */   private Object lookupConverter(Class<?> javaClass, Collection<? extends Entry> converters) {
/* 138 */     for (Entry entry : converters) {
/* 139 */       if (entry.type.isAssignableFrom(javaClass)) {
/* 140 */         return entry.converter;
/*     */       }
/*     */     } 
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public FromNativeConverter getFromNativeConverter(Class<?> javaType) {
/* 148 */     return (FromNativeConverter)lookupConverter(javaType, this.fromNativeConverters);
/*     */   }
/*     */ 
/*     */   
/*     */   public ToNativeConverter getToNativeConverter(Class<?> javaType) {
/* 153 */     return (ToNativeConverter)lookupConverter(javaType, this.toNativeConverters);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\DefaultTypeMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */