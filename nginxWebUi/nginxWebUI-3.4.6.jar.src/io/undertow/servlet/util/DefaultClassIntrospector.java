/*    */ package io.undertow.servlet.util;
/*    */ 
/*    */ import io.undertow.servlet.api.ClassIntrospecter;
/*    */ import io.undertow.servlet.api.InstanceFactory;
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
/*    */ public class DefaultClassIntrospector
/*    */   implements ClassIntrospecter
/*    */ {
/* 29 */   public static final DefaultClassIntrospector INSTANCE = new DefaultClassIntrospector();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public <T> InstanceFactory<T> createInstanceFactory(Class<T> clazz) throws NoSuchMethodException {
/* 36 */     return new ConstructorInstanceFactory<>(clazz.getDeclaredConstructor(new Class[0]));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\DefaultClassIntrospector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */