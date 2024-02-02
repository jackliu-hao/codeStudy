/*     */ package cn.hutool.core.lang;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
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
/*     */ public class ParameterizedTypeImpl
/*     */   implements ParameterizedType, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Type[] actualTypeArguments;
/*     */   private final Type ownerType;
/*     */   private final Type rawType;
/*     */   
/*     */   public ParameterizedTypeImpl(Type[] actualTypeArguments, Type ownerType, Type rawType) {
/*  31 */     this.actualTypeArguments = actualTypeArguments;
/*  32 */     this.ownerType = ownerType;
/*  33 */     this.rawType = rawType;
/*     */   }
/*     */ 
/*     */   
/*     */   public Type[] getActualTypeArguments() {
/*  38 */     return this.actualTypeArguments;
/*     */   }
/*     */ 
/*     */   
/*     */   public Type getOwnerType() {
/*  43 */     return this.ownerType;
/*     */   }
/*     */ 
/*     */   
/*     */   public Type getRawType() {
/*  48 */     return this.rawType;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  53 */     StringBuilder buf = new StringBuilder();
/*     */     
/*  55 */     Type useOwner = this.ownerType;
/*  56 */     Class<?> raw = (Class)this.rawType;
/*  57 */     if (useOwner == null) {
/*  58 */       buf.append(raw.getName());
/*     */     } else {
/*  60 */       if (useOwner instanceof Class) {
/*  61 */         buf.append(((Class)useOwner).getName());
/*     */       } else {
/*  63 */         buf.append(useOwner.toString());
/*     */       } 
/*  65 */       buf.append('.').append(raw.getSimpleName());
/*     */     } 
/*     */     
/*  68 */     appendAllTo(buf.append('<'), ", ", this.actualTypeArguments).append('>');
/*  69 */     return buf.toString();
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
/*     */   private static StringBuilder appendAllTo(StringBuilder buf, String sep, Type... types) {
/*  81 */     if (ArrayUtil.isNotEmpty((Object[])types)) {
/*  82 */       boolean isFirst = true;
/*  83 */       for (Type type : types) {
/*  84 */         String typeStr; if (isFirst) {
/*  85 */           isFirst = false;
/*     */         } else {
/*  87 */           buf.append(sep);
/*     */         } 
/*     */ 
/*     */         
/*  91 */         if (type instanceof Class) {
/*  92 */           typeStr = ((Class)type).getName();
/*     */         } else {
/*  94 */           typeStr = StrUtil.toString(type);
/*     */         } 
/*     */         
/*  97 */         buf.append(typeStr);
/*     */       } 
/*     */     } 
/* 100 */     return buf;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */