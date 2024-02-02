/*    */ package cn.hutool.core.annotation;
/*    */ 
/*    */ import cn.hutool.core.util.ReflectUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.Serializable;
/*    */ import java.lang.annotation.Annotation;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
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
/*    */ public class AnnotationProxy<T extends Annotation>
/*    */   implements Annotation, InvocationHandler, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final T annotation;
/*    */   private final Class<T> type;
/*    */   private final Map<String, Object> attributes;
/*    */   
/*    */   public AnnotationProxy(T annotation) {
/* 33 */     this.annotation = annotation;
/*    */     
/* 35 */     this.type = (Class)annotation.annotationType();
/* 36 */     this.attributes = initAttributes();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<? extends Annotation> annotationType() {
/* 42 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/* 49 */     Alias alias = method.<Alias>getAnnotation(Alias.class);
/* 50 */     if (null != alias) {
/* 51 */       String name = alias.value();
/* 52 */       if (StrUtil.isNotBlank(name)) {
/* 53 */         if (false == this.attributes.containsKey(name)) {
/* 54 */           throw new IllegalArgumentException(StrUtil.format("No method for alias: [{}]", new Object[] { name }));
/*    */         }
/* 56 */         return this.attributes.get(name);
/*    */       } 
/*    */     } 
/*    */     
/* 60 */     Object value = this.attributes.get(method.getName());
/* 61 */     if (value != null) {
/* 62 */       return value;
/*    */     }
/* 64 */     return method.invoke(this, args);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private Map<String, Object> initAttributes() {
/* 74 */     Method[] methods = ReflectUtil.getMethods(this.type);
/* 75 */     Map<String, Object> attributes = new HashMap<>(methods.length, 1.0F);
/*    */     
/* 77 */     for (Method method : methods) {
/*    */       
/* 79 */       if (!method.isSynthetic())
/*    */       {
/*    */ 
/*    */         
/* 83 */         attributes.put(method.getName(), ReflectUtil.invoke(this.annotation, method, new Object[0]));
/*    */       }
/*    */     } 
/* 86 */     return attributes;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\annotation\AnnotationProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */