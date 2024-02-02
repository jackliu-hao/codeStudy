/*    */ package ch.qos.logback.core.joran.util.beans;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Collections;
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
/*    */ public class BeanDescription
/*    */ {
/*    */   private final Class<?> clazz;
/*    */   private final Map<String, Method> propertyNameToGetter;
/*    */   private final Map<String, Method> propertyNameToSetter;
/*    */   private final Map<String, Method> propertyNameToAdder;
/*    */   
/*    */   protected BeanDescription(Class<?> clazz, Map<String, Method> propertyNameToGetter, Map<String, Method> propertyNameToSetter, Map<String, Method> propertyNameToAdder) {
/* 37 */     this.clazz = clazz;
/* 38 */     this.propertyNameToGetter = Collections.unmodifiableMap(propertyNameToGetter);
/* 39 */     this.propertyNameToSetter = Collections.unmodifiableMap(propertyNameToSetter);
/* 40 */     this.propertyNameToAdder = Collections.unmodifiableMap(propertyNameToAdder);
/*    */   }
/*    */   
/*    */   public Class<?> getClazz() {
/* 44 */     return this.clazz;
/*    */   }
/*    */   
/*    */   public Map<String, Method> getPropertyNameToGetter() {
/* 48 */     return this.propertyNameToGetter;
/*    */   }
/*    */   
/*    */   public Map<String, Method> getPropertyNameToSetter() {
/* 52 */     return this.propertyNameToSetter;
/*    */   }
/*    */   
/*    */   public Method getGetter(String propertyName) {
/* 56 */     return this.propertyNameToGetter.get(propertyName);
/*    */   }
/*    */   
/*    */   public Method getSetter(String propertyName) {
/* 60 */     return this.propertyNameToSetter.get(propertyName);
/*    */   }
/*    */   
/*    */   public Map<String, Method> getPropertyNameToAdder() {
/* 64 */     return this.propertyNameToAdder;
/*    */   }
/*    */   
/*    */   public Method getAdder(String propertyName) {
/* 68 */     return this.propertyNameToAdder.get(propertyName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\jora\\util\beans\BeanDescription.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */