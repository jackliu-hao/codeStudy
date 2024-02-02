/*    */ package ch.qos.logback.core.joran.spi;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultNestedComponentRegistry
/*    */ {
/* 27 */   Map<HostClassAndPropertyDouble, Class<?>> defaultComponentMap = new HashMap<HostClassAndPropertyDouble, Class<?>>();
/*    */   
/*    */   public void add(Class<?> hostClass, String propertyName, Class<?> componentClass) {
/* 30 */     HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName.toLowerCase());
/* 31 */     this.defaultComponentMap.put(hpDouble, componentClass);
/*    */   }
/*    */   
/*    */   public Class<?> findDefaultComponentType(Class<?> hostClass, String propertyName) {
/* 35 */     propertyName = propertyName.toLowerCase();
/* 36 */     while (hostClass != null) {
/* 37 */       Class<?> componentClass = oneShotFind(hostClass, propertyName);
/* 38 */       if (componentClass != null) {
/* 39 */         return componentClass;
/*    */       }
/* 41 */       hostClass = hostClass.getSuperclass();
/*    */     } 
/* 43 */     return null;
/*    */   }
/*    */   
/*    */   private Class<?> oneShotFind(Class<?> hostClass, String propertyName) {
/* 47 */     HostClassAndPropertyDouble hpDouble = new HostClassAndPropertyDouble(hostClass, propertyName);
/* 48 */     return this.defaultComponentMap.get(hpDouble);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\DefaultNestedComponentRegistry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */