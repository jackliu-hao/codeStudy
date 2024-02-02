/*    */ package ch.qos.logback.classic.sift;
/*    */ 
/*    */ import ch.qos.logback.classic.spi.ILoggingEvent;
/*    */ import ch.qos.logback.core.joran.event.SaxEvent;
/*    */ import ch.qos.logback.core.sift.AbstractAppenderFactoryUsingJoran;
/*    */ import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
/*    */ import java.util.List;
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
/*    */ public class AppenderFactoryUsingJoran
/*    */   extends AbstractAppenderFactoryUsingJoran<ILoggingEvent>
/*    */ {
/*    */   AppenderFactoryUsingJoran(List<SaxEvent> eventList, String key, Map<String, String> parentPropertyMap) {
/* 30 */     super(eventList, key, parentPropertyMap);
/*    */   }
/*    */   
/*    */   public SiftingJoranConfiguratorBase<ILoggingEvent> getSiftingJoranConfigurator(String discriminatingValue) {
/* 34 */     return new SiftingJoranConfigurator(this.key, discriminatingValue, this.parentPropertyMap);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\sift\AppenderFactoryUsingJoran.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */