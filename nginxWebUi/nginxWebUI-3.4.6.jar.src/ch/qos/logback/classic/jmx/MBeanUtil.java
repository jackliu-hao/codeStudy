/*    */ package ch.qos.logback.classic.jmx;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.status.StatusUtil;
/*    */ import javax.management.InstanceNotFoundException;
/*    */ import javax.management.MBeanRegistrationException;
/*    */ import javax.management.MBeanServer;
/*    */ import javax.management.MalformedObjectNameException;
/*    */ import javax.management.ObjectName;
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
/*    */ public class MBeanUtil
/*    */ {
/*    */   static final String DOMAIN = "ch.qos.logback.classic";
/*    */   
/*    */   public static String getObjectNameFor(String contextName, Class type) {
/* 31 */     return "ch.qos.logback.classic:Name=" + contextName + ",Type=" + type.getName();
/*    */   }
/*    */   
/*    */   public static ObjectName string2ObjectName(Context context, Object caller, String objectNameAsStr) {
/* 35 */     String msg = "Failed to convert [" + objectNameAsStr + "] to ObjectName";
/*    */     
/* 37 */     StatusUtil statusUtil = new StatusUtil(context);
/*    */     try {
/* 39 */       return new ObjectName(objectNameAsStr);
/* 40 */     } catch (MalformedObjectNameException e) {
/* 41 */       statusUtil.addError(caller, msg, e);
/* 42 */       return null;
/* 43 */     } catch (NullPointerException e) {
/* 44 */       statusUtil.addError(caller, msg, e);
/* 45 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static boolean isRegistered(MBeanServer mbs, ObjectName objectName) {
/* 50 */     return mbs.isRegistered(objectName);
/*    */   }
/*    */ 
/*    */   
/*    */   public static void createAndRegisterJMXConfigurator(MBeanServer mbs, LoggerContext loggerContext, JMXConfigurator jmxConfigurator, ObjectName objectName, Object caller) {
/*    */     try {
/* 56 */       mbs.registerMBean(jmxConfigurator, objectName);
/* 57 */     } catch (Exception e) {
/* 58 */       StatusUtil statusUtil = new StatusUtil((Context)loggerContext);
/* 59 */       statusUtil.addError(caller, "Failed to create mbean", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static void unregister(LoggerContext loggerContext, MBeanServer mbs, ObjectName objectName, Object caller) {
/* 65 */     StatusUtil statusUtil = new StatusUtil((Context)loggerContext);
/* 66 */     if (mbs.isRegistered(objectName)) {
/*    */       try {
/* 68 */         statusUtil.addInfo(caller, "Unregistering mbean [" + objectName + "]");
/* 69 */         mbs.unregisterMBean(objectName);
/* 70 */       } catch (InstanceNotFoundException e) {
/*    */         
/* 72 */         statusUtil.addError(caller, "Failed to unregister mbean" + objectName, e);
/* 73 */       } catch (MBeanRegistrationException e) {
/*    */         
/* 75 */         statusUtil.addError(caller, "Failed to unregister mbean" + objectName, e);
/*    */       } 
/*    */     } else {
/* 78 */       statusUtil.addInfo(caller, "mbean [" + objectName + "] does not seem to be registered");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\jmx\MBeanUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */