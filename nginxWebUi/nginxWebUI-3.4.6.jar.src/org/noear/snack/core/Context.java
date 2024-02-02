/*     */ package org.noear.snack.core;
/*     */ 
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import org.noear.snack.ONode;
/*     */ import org.noear.snack.exception.SnackException;
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
/*     */ public class Context
/*     */ {
/*     */   public final Options options;
/*     */   public Object source;
/*     */   public Object target;
/*     */   public Class<?> target_clz;
/*     */   public Type target_type;
/*     */   
/*     */   public Context(Options options, Object from) {
/*  34 */     this.options = options;
/*  35 */     this.source = from;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Context(Options options, ONode node, Type type0) {
/*  42 */     this.options = options;
/*  43 */     this.source = node;
/*     */     
/*  45 */     if (type0 == null) {
/*     */       return;
/*     */     }
/*     */     
/*  49 */     if (type0 instanceof Class) {
/*     */ 
/*     */       
/*  52 */       Class<?> clz = (Class)type0;
/*     */       
/*  54 */       if (TypeRef.class.isAssignableFrom(clz)) {
/*  55 */         Type superClass = clz.getGenericSuperclass();
/*  56 */         Type type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
/*     */         
/*  58 */         initType(type);
/*     */         
/*     */         return;
/*     */       } 
/*  62 */       if (clz.getName().indexOf("$") > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  67 */         if (clz.isMemberClass()) {
/*  68 */           initType(clz, clz);
/*     */         } else {
/*  70 */           initType(clz.getGenericSuperclass());
/*     */         } 
/*     */       } else {
/*  73 */         initType(clz, clz);
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/*  78 */       initType(type0);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initType(Type type) {
/*  83 */     if (type instanceof ParameterizedType) {
/*  84 */       ParameterizedType pType = (ParameterizedType)type;
/*     */       
/*  86 */       initType(type, (Class)pType.getRawType());
/*     */     } else {
/*  88 */       initType(type, (Class)type);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initType(Type type, Class<?> clz) {
/*  93 */     this.target_type = type;
/*  94 */     this.target_clz = clz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Context handle(Handler handler) {
/*     */     try {
/* 102 */       handler.handle(this);
/* 103 */       return this;
/* 104 */     } catch (RuntimeException e) {
/* 105 */       throw e;
/* 106 */     } catch (Throwable e) {
/* 107 */       throw new SnackException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */