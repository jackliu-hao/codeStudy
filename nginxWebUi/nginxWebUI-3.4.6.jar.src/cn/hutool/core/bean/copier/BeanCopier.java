/*    */ package cn.hutool.core.bean.copier;
/*    */ 
/*    */ import cn.hutool.core.lang.Assert;
/*    */ import cn.hutool.core.lang.copier.Copier;
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Map;
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
/*    */ public class BeanCopier<T>
/*    */   implements Copier<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Copier<T> copier;
/*    */   
/*    */   public static <T> BeanCopier<T> create(Object source, T target, CopyOptions copyOptions) {
/* 40 */     return create(source, target, target.getClass(), copyOptions);
/*    */   }
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
/*    */   public static <T> BeanCopier<T> create(Object source, T target, Type destType, CopyOptions copyOptions) {
/* 54 */     return new BeanCopier<>(source, target, destType, copyOptions);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanCopier(Object source, T target, Type targetType, CopyOptions copyOptions) {
/*    */     Copier<T> copier;
/* 66 */     Assert.notNull(source, "Source bean must be not null!", new Object[0]);
/* 67 */     Assert.notNull(target, "Target bean must be not null!", new Object[0]);
/*    */     
/* 69 */     if (source instanceof Map) {
/* 70 */       if (target instanceof Map) {
/*    */         
/* 72 */         copier = new MapToMapCopier((Map)source, (Map)target, targetType, copyOptions);
/*    */       } else {
/* 74 */         copier = new MapToBeanCopier<>((Map<?, ?>)source, target, targetType, copyOptions);
/*    */       } 
/* 76 */     } else if (source instanceof ValueProvider) {
/*    */       
/* 78 */       copier = new ValueProviderToBeanCopier<>((ValueProvider<String>)source, target, targetType, copyOptions);
/*    */     }
/* 80 */     else if (target instanceof Map) {
/*    */       
/* 82 */       copier = new BeanToMapCopier(source, (Map)target, targetType, copyOptions);
/*    */     } else {
/* 84 */       copier = new BeanToBeanCopier<>(source, target, targetType, copyOptions);
/*    */     } 
/*    */     
/* 87 */     this.copier = copier;
/*    */   }
/*    */ 
/*    */   
/*    */   public T copy() {
/* 92 */     return (T)this.copier.copy();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\BeanCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */