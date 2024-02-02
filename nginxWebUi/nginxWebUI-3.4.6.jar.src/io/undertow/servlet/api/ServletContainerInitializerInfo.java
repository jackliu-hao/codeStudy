/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import io.undertow.servlet.UndertowServletMessages;
/*    */ import io.undertow.servlet.util.ConstructorInstanceFactory;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.util.Set;
/*    */ import javax.servlet.ServletContainerInitializer;
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
/*    */ public class ServletContainerInitializerInfo
/*    */ {
/*    */   private final Class<? extends ServletContainerInitializer> servletContainerInitializerClass;
/*    */   private final InstanceFactory<? extends ServletContainerInitializer> instanceFactory;
/*    */   private final Set<Class<?>> handlesTypes;
/*    */   
/*    */   public ServletContainerInitializerInfo(Class<? extends ServletContainerInitializer> servletContainerInitializerClass, InstanceFactory<? extends ServletContainerInitializer> instanceFactory, Set<Class<?>> handlesTypes) {
/* 39 */     this.servletContainerInitializerClass = servletContainerInitializerClass;
/* 40 */     this.instanceFactory = instanceFactory;
/* 41 */     this.handlesTypes = handlesTypes;
/*    */   }
/*    */   
/*    */   public ServletContainerInitializerInfo(Class<? extends ServletContainerInitializer> servletContainerInitializerClass, Set<Class<?>> handlesTypes) {
/* 45 */     this.servletContainerInitializerClass = servletContainerInitializerClass;
/* 46 */     this.handlesTypes = handlesTypes;
/*    */     
/*    */     try {
/* 49 */       Constructor<ServletContainerInitializer> ctor = (Constructor)servletContainerInitializerClass.getDeclaredConstructor(new Class[0]);
/* 50 */       ctor.setAccessible(true);
/* 51 */       this.instanceFactory = (InstanceFactory<? extends ServletContainerInitializer>)new ConstructorInstanceFactory(ctor);
/* 52 */     } catch (NoSuchMethodException e) {
/* 53 */       throw UndertowServletMessages.MESSAGES.componentMustHaveDefaultConstructor("ServletContainerInitializer", servletContainerInitializerClass);
/*    */     } 
/*    */   }
/*    */   
/*    */   public Class<? extends ServletContainerInitializer> getServletContainerInitializerClass() {
/* 58 */     return this.servletContainerInitializerClass;
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
/*    */   public Set<Class<?>> getHandlesTypes() {
/* 70 */     return this.handlesTypes;
/*    */   }
/*    */   
/*    */   public InstanceFactory<? extends ServletContainerInitializer> getInstanceFactory() {
/* 74 */     return this.instanceFactory;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletContainerInitializerInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */