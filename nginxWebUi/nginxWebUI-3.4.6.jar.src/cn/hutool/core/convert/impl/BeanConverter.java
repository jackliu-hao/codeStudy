/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
/*    */ import cn.hutool.core.bean.copier.BeanCopier;
/*    */ import cn.hutool.core.bean.copier.CopyOptions;
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.ConvertException;
/*    */ import cn.hutool.core.map.MapProxy;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import cn.hutool.core.util.TypeUtil;
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
/*    */ public class BeanConverter<T>
/*    */   extends AbstractConverter<T>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Type beanType;
/*    */   private final Class<T> beanClass;
/*    */   private final CopyOptions copyOptions;
/*    */   
/*    */   public BeanConverter(Type beanType) {
/* 42 */     this(beanType, CopyOptions.create().setIgnoreError(true));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanConverter(Class<T> beanClass) {
/* 51 */     this(beanClass, CopyOptions.create().setIgnoreError(true));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanConverter(Type beanType, CopyOptions copyOptions) {
/* 62 */     this.beanType = beanType;
/* 63 */     this.beanClass = TypeUtil.getClass(beanType);
/* 64 */     this.copyOptions = copyOptions;
/*    */   }
/*    */ 
/*    */   
/*    */   protected T convertInternal(Object value) {
/* 69 */     if (value instanceof Map || value instanceof cn.hutool.core.bean.copier.ValueProvider || 
/*    */       
/* 71 */       BeanUtil.isBean(value.getClass())) {
/* 72 */       if (value instanceof Map && this.beanClass.isInterface())
/*    */       {
/* 74 */         return (T)MapProxy.create((Map)value).toProxyBean(this.beanClass);
/*    */       }
/*    */ 
/*    */       
/* 78 */       return (T)BeanCopier.create(value, ReflectUtil.newInstanceIfPossible(this.beanClass), this.beanType, this.copyOptions).copy();
/* 79 */     }  if (value instanceof byte[])
/*    */     {
/* 81 */       return (T)ObjectUtil.deserialize((byte[])value);
/*    */     }
/*    */     
/* 84 */     throw new ConvertException("Unsupported source type: {}", new Object[] { value.getClass() });
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<T> getTargetType() {
/* 89 */     return this.beanClass;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\BeanConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */