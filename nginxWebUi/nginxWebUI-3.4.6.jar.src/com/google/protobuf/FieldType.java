/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
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
/*     */ public enum FieldType
/*     */ {
/*  42 */   DOUBLE(0, Collection.SCALAR, JavaType.DOUBLE),
/*  43 */   FLOAT(1, Collection.SCALAR, JavaType.FLOAT),
/*  44 */   INT64(2, Collection.SCALAR, JavaType.LONG),
/*  45 */   UINT64(3, Collection.SCALAR, JavaType.LONG),
/*  46 */   INT32(4, Collection.SCALAR, JavaType.INT),
/*  47 */   FIXED64(5, Collection.SCALAR, JavaType.LONG),
/*  48 */   FIXED32(6, Collection.SCALAR, JavaType.INT),
/*  49 */   BOOL(7, Collection.SCALAR, JavaType.BOOLEAN),
/*  50 */   STRING(8, Collection.SCALAR, JavaType.STRING),
/*  51 */   MESSAGE(9, Collection.SCALAR, JavaType.MESSAGE),
/*  52 */   BYTES(10, Collection.SCALAR, JavaType.BYTE_STRING),
/*  53 */   UINT32(11, Collection.SCALAR, JavaType.INT),
/*  54 */   ENUM(12, Collection.SCALAR, JavaType.ENUM),
/*  55 */   SFIXED32(13, Collection.SCALAR, JavaType.INT),
/*  56 */   SFIXED64(14, Collection.SCALAR, JavaType.LONG),
/*  57 */   SINT32(15, Collection.SCALAR, JavaType.INT),
/*  58 */   SINT64(16, Collection.SCALAR, JavaType.LONG),
/*  59 */   GROUP(17, Collection.SCALAR, JavaType.MESSAGE),
/*  60 */   DOUBLE_LIST(18, Collection.VECTOR, JavaType.DOUBLE),
/*  61 */   FLOAT_LIST(19, Collection.VECTOR, JavaType.FLOAT),
/*  62 */   INT64_LIST(20, Collection.VECTOR, JavaType.LONG),
/*  63 */   UINT64_LIST(21, Collection.VECTOR, JavaType.LONG),
/*  64 */   INT32_LIST(22, Collection.VECTOR, JavaType.INT),
/*  65 */   FIXED64_LIST(23, Collection.VECTOR, JavaType.LONG),
/*  66 */   FIXED32_LIST(24, Collection.VECTOR, JavaType.INT),
/*  67 */   BOOL_LIST(25, Collection.VECTOR, JavaType.BOOLEAN),
/*  68 */   STRING_LIST(26, Collection.VECTOR, JavaType.STRING),
/*  69 */   MESSAGE_LIST(27, Collection.VECTOR, JavaType.MESSAGE),
/*  70 */   BYTES_LIST(28, Collection.VECTOR, JavaType.BYTE_STRING),
/*  71 */   UINT32_LIST(29, Collection.VECTOR, JavaType.INT),
/*  72 */   ENUM_LIST(30, Collection.VECTOR, JavaType.ENUM),
/*  73 */   SFIXED32_LIST(31, Collection.VECTOR, JavaType.INT),
/*  74 */   SFIXED64_LIST(32, Collection.VECTOR, JavaType.LONG),
/*  75 */   SINT32_LIST(33, Collection.VECTOR, JavaType.INT),
/*  76 */   SINT64_LIST(34, Collection.VECTOR, JavaType.LONG),
/*  77 */   DOUBLE_LIST_PACKED(35, Collection.PACKED_VECTOR, JavaType.DOUBLE),
/*  78 */   FLOAT_LIST_PACKED(36, Collection.PACKED_VECTOR, JavaType.FLOAT),
/*  79 */   INT64_LIST_PACKED(37, Collection.PACKED_VECTOR, JavaType.LONG),
/*  80 */   UINT64_LIST_PACKED(38, Collection.PACKED_VECTOR, JavaType.LONG),
/*  81 */   INT32_LIST_PACKED(39, Collection.PACKED_VECTOR, JavaType.INT),
/*  82 */   FIXED64_LIST_PACKED(40, Collection.PACKED_VECTOR, JavaType.LONG),
/*  83 */   FIXED32_LIST_PACKED(41, Collection.PACKED_VECTOR, JavaType.INT),
/*  84 */   BOOL_LIST_PACKED(42, Collection.PACKED_VECTOR, JavaType.BOOLEAN),
/*  85 */   UINT32_LIST_PACKED(43, Collection.PACKED_VECTOR, JavaType.INT),
/*  86 */   ENUM_LIST_PACKED(44, Collection.PACKED_VECTOR, JavaType.ENUM),
/*  87 */   SFIXED32_LIST_PACKED(45, Collection.PACKED_VECTOR, JavaType.INT),
/*  88 */   SFIXED64_LIST_PACKED(46, Collection.PACKED_VECTOR, JavaType.LONG),
/*  89 */   SINT32_LIST_PACKED(47, Collection.PACKED_VECTOR, JavaType.INT),
/*  90 */   SINT64_LIST_PACKED(48, Collection.PACKED_VECTOR, JavaType.LONG),
/*  91 */   GROUP_LIST(49, Collection.VECTOR, JavaType.MESSAGE),
/*  92 */   MAP(50, Collection.MAP, JavaType.VOID);
/*     */   
/*     */   private final JavaType javaType;
/*     */   
/*     */   private final int id;
/*     */   
/*     */   private final Collection collection;
/*     */   
/*     */   FieldType(int id, Collection collection, JavaType javaType) {
/* 101 */     this.id = id;
/* 102 */     this.collection = collection;
/* 103 */     this.javaType = javaType;
/*     */     
/* 105 */     switch (collection) {
/*     */       case BYTE_STRING:
/* 107 */         this.elementType = javaType.getBoxedType();
/*     */         break;
/*     */       case MESSAGE:
/* 110 */         this.elementType = javaType.getBoxedType();
/*     */         break;
/*     */       
/*     */       default:
/* 114 */         this.elementType = null;
/*     */         break;
/*     */     } 
/*     */     
/* 118 */     boolean primitiveScalar = false;
/* 119 */     if (collection == Collection.SCALAR) {
/* 120 */       switch (javaType) {
/*     */         case BYTE_STRING:
/*     */         case MESSAGE:
/*     */         case STRING:
/*     */           break;
/*     */         default:
/* 126 */           primitiveScalar = true;
/*     */           break;
/*     */       } 
/*     */     }
/* 130 */     this.primitiveScalar = primitiveScalar;
/*     */   }
/*     */   private final Class<?> elementType; private final boolean primitiveScalar; private static final FieldType[] VALUES; private static final Type[] EMPTY_TYPES;
/*     */   
/*     */   public int id() {
/* 135 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JavaType getJavaType() {
/* 143 */     return this.javaType;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPacked() {
/* 148 */     return Collection.PACKED_VECTOR.equals(this.collection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPrimitiveScalar() {
/* 156 */     return this.primitiveScalar;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isScalar() {
/* 161 */     return (this.collection == Collection.SCALAR);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isList() {
/* 166 */     return this.collection.isList();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isMap() {
/* 171 */     return (this.collection == Collection.MAP);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValidForField(Field field) {
/* 176 */     if (Collection.VECTOR.equals(this.collection)) {
/* 177 */       return isValidForList(field);
/*     */     }
/* 179 */     return this.javaType.getType().isAssignableFrom(field.getType());
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isValidForList(Field field) {
/* 184 */     Class<?> clazz = field.getType();
/* 185 */     if (!this.javaType.getType().isAssignableFrom(clazz))
/*     */     {
/* 187 */       return false;
/*     */     }
/* 189 */     Type[] types = EMPTY_TYPES;
/* 190 */     Type genericType = field.getGenericType();
/* 191 */     if (genericType instanceof ParameterizedType) {
/* 192 */       types = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
/*     */     }
/* 194 */     Type listParameter = getListParameter(clazz, types);
/* 195 */     if (!(listParameter instanceof Class))
/*     */     {
/* 197 */       return true;
/*     */     }
/* 199 */     return this.elementType.isAssignableFrom((Class)listParameter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FieldType forId(int id) {
/* 209 */     if (id < 0 || id >= VALUES.length) {
/* 210 */       return null;
/*     */     }
/* 212 */     return VALUES[id];
/*     */   }
/*     */   
/*     */   static {
/* 216 */     EMPTY_TYPES = new Type[0];
/*     */ 
/*     */     
/* 219 */     FieldType[] values = values();
/* 220 */     VALUES = new FieldType[values.length];
/* 221 */     for (FieldType type : values) {
/* 222 */       VALUES[type.id] = type;
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
/*     */   private static Type getGenericSuperList(Class<?> clazz) {
/* 234 */     Type[] genericInterfaces = clazz.getGenericInterfaces();
/* 235 */     for (Type genericInterface : genericInterfaces) {
/* 236 */       if (genericInterface instanceof ParameterizedType) {
/* 237 */         ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
/* 238 */         Class<?> rawType = (Class)parameterizedType.getRawType();
/* 239 */         if (List.class.isAssignableFrom(rawType)) {
/* 240 */           return genericInterface;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 246 */     Type type = clazz.getGenericSuperclass();
/* 247 */     if (type instanceof ParameterizedType) {
/* 248 */       ParameterizedType parameterizedType = (ParameterizedType)type;
/* 249 */       Class<?> rawType = (Class)parameterizedType.getRawType();
/* 250 */       if (List.class.isAssignableFrom(rawType)) {
/* 251 */         return type;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 256 */     return null;
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
/*     */   private static Type getListParameter(Class<?> clazz, Type[] realTypes) {
/* 271 */     label41: while (clazz != List.class) {
/*     */       
/* 273 */       Type genericType = getGenericSuperList(clazz);
/* 274 */       if (genericType instanceof ParameterizedType) {
/*     */         
/* 276 */         ParameterizedType parameterizedType = (ParameterizedType)genericType;
/* 277 */         Type[] superArgs = parameterizedType.getActualTypeArguments();
/* 278 */         for (int i = 0; i < superArgs.length; i++) {
/* 279 */           Type superArg = superArgs[i];
/* 280 */           if (superArg instanceof TypeVariable) {
/*     */ 
/*     */             
/* 283 */             TypeVariable[] arrayOfTypeVariable = (TypeVariable[])clazz.getTypeParameters();
/* 284 */             if (realTypes.length != arrayOfTypeVariable.length) {
/* 285 */               throw new RuntimeException("Type array mismatch");
/*     */             }
/*     */ 
/*     */             
/* 289 */             boolean foundReplacement = false;
/* 290 */             for (int j = 0; j < arrayOfTypeVariable.length; j++) {
/* 291 */               if (superArg == arrayOfTypeVariable[j]) {
/* 292 */                 Type realType = realTypes[j];
/* 293 */                 superArgs[i] = realType;
/* 294 */                 foundReplacement = true;
/*     */                 break;
/*     */               } 
/*     */             } 
/* 298 */             if (!foundReplacement) {
/* 299 */               throw new RuntimeException("Unable to find replacement for " + superArg);
/*     */             }
/*     */           } 
/*     */         } 
/*     */         
/* 304 */         Class<?> parent = (Class)parameterizedType.getRawType();
/*     */         
/* 306 */         realTypes = superArgs;
/* 307 */         clazz = parent;
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 313 */       realTypes = EMPTY_TYPES;
/* 314 */       for (Class<?> iface : clazz.getInterfaces()) {
/* 315 */         if (List.class.isAssignableFrom(iface)) {
/* 316 */           clazz = iface;
/*     */           continue label41;
/*     */         } 
/*     */       } 
/* 320 */       clazz = clazz.getSuperclass();
/*     */     } 
/*     */     
/* 323 */     if (realTypes.length != 1) {
/* 324 */       throw new RuntimeException("Unable to identify parameter type for List<T>");
/*     */     }
/* 326 */     return realTypes[0];
/*     */   }
/*     */   
/*     */   enum Collection {
/* 330 */     SCALAR(false),
/* 331 */     VECTOR(true),
/* 332 */     PACKED_VECTOR(true),
/* 333 */     MAP(false);
/*     */     
/*     */     private final boolean isList;
/*     */     
/*     */     Collection(boolean isList) {
/* 338 */       this.isList = isList;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isList() {
/* 343 */       return this.isList;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\FieldType.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */