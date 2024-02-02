/*    */ package ch.qos.logback.classic.joran.action;
/*    */ 
/*    */ import ch.qos.logback.classic.Logger;
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import ch.qos.logback.classic.net.SocketAppender;
/*    */ import ch.qos.logback.core.Appender;
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.joran.action.Action;
/*    */ import ch.qos.logback.core.joran.spi.ActionException;
/*    */ import ch.qos.logback.core.joran.spi.InterpretationContext;
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
/*    */ public class ConsolePluginAction
/*    */   extends Action
/*    */ {
/*    */   private static final String PORT_ATTR = "port";
/* 28 */   private static final Integer DEFAULT_PORT = Integer.valueOf(4321);
/*    */ 
/*    */   
/*    */   public void begin(InterpretationContext ec, String name, Attributes attributes) throws ActionException {
/* 32 */     String portStr = attributes.getValue("port");
/* 33 */     Integer port = null;
/*    */     
/* 35 */     if (portStr == null) {
/* 36 */       port = DEFAULT_PORT;
/*    */     } else {
/*    */       try {
/* 39 */         port = Integer.valueOf(portStr);
/* 40 */       } catch (NumberFormatException ex) {
/* 41 */         addError("Port " + portStr + " in ConsolePlugin config is not a correct number");
/*    */       } 
/*    */     } 
/*    */     
/* 45 */     LoggerContext lc = (LoggerContext)ec.getContext();
/* 46 */     SocketAppender appender = new SocketAppender();
/* 47 */     appender.setContext((Context)lc);
/* 48 */     appender.setIncludeCallerData(true);
/* 49 */     appender.setRemoteHost("localhost");
/* 50 */     appender.setPort(port.intValue());
/* 51 */     appender.start();
/* 52 */     Logger root = lc.getLogger("ROOT");
/* 53 */     root.addAppender((Appender)appender);
/*    */     
/* 55 */     addInfo("Sending LoggingEvents to the plugin using port " + port);
/*    */   }
/*    */   
/*    */   public void end(InterpretationContext ec, String name) throws ActionException {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\joran\action\ConsolePluginAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */