/*    */ package ch.qos.logback.classic.joran.action;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.jmx.JMXConfigurator;
/*    */ import ch.qos.logback.classic.jmx.MBeanUtil;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
/*    */ import ch.qos.logback.core.util.OptionHelper;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import javax.management.MBeanServer;
/*    */ import javax.management.ObjectName;
/*    */ import org.xml.sax.Attributes;
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
/*    */ public class JMXConfiguratorAction
/*    */   extends Action
/*    */ {
/*    */   static final String OBJECT_NAME_ATTRIBUTE_NAME = "objectName";
/*    */   static final String CONTEXT_NAME_ATTRIBUTE_NAME = "contextName";
/*    */   static final char JMX_NAME_SEPARATOR = ',';
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
/*    */     String objectNameAsStr;
/* 39 */     addInfo("begin");
/*    */     
/* 41 */     String contextName = this.context.getName();
/* 42 */     String contextNameAttributeVal = attributes.getValue("contextName");
/* 43 */     if (!OptionHelper.isEmpty(contextNameAttributeVal)) {
/* 44 */       contextName = contextNameAttributeVal;
/*    */     }
/*    */ 
/*    */     
/* 48 */     String objectNameAttributeVal = attributes.getValue("objectName");
/* 49 */     if (OptionHelper.isEmpty(objectNameAttributeVal)) {
/* 50 */       objectNameAsStr = MBeanUtil.getObjectNameFor(contextName, JMXConfigurator.class);
/*    */     } else {
/* 52 */       objectNameAsStr = objectNameAttributeVal;
/*    */     } 
/*    */     
/* 55 */     ObjectName objectName = MBeanUtil.string2ObjectName(this.context, this, objectNameAsStr);
/* 56 */     if (objectName == null) {
/* 57 */       addError("Failed construct ObjectName for [" + objectNameAsStr + "]");
/*    */       
/*    */       return;
/*    */     } 
/* 61 */     MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
/* 62 */     if (!MBeanUtil.isRegistered(mbs, objectName)) {
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 67 */       JMXConfigurator jmxConfigurator = new JMXConfigurator((LoggerContext)this.context, mbs, objectName);
/*    */       try {
/* 69 */         mbs.registerMBean(jmxConfigurator, objectName);
/* 70 */       } catch (Exception e) {
/* 71 */         addError("Failed to create mbean", e);
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String name) throws ActionException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\JMXConfiguratorAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */