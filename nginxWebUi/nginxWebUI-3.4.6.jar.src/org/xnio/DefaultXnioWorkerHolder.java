/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOError;
/*    */ import java.io.IOException;
/*    */ import java.security.AccessController;
/*    */ import java.util.Iterator;
/*    */ import java.util.ServiceConfigurationError;
/*    */ import java.util.ServiceLoader;
/*    */ import org.wildfly.client.config.ConfigXMLParseException;
/*    */ import org.xnio._private.Messages;
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
/*    */ final class DefaultXnioWorkerHolder
/*    */ {
/*    */   static final XnioWorker INSTANCE;
/*    */   
/*    */   static {
/* 37 */     INSTANCE = AccessController.<XnioWorker>doPrivileged(() -> {
/*    */           Xnio xnio = Xnio.getInstance();
/*    */           XnioWorker worker = null;
/*    */           try {
/*    */             worker = XnioXmlParser.parseWorker(xnio);
/* 42 */           } catch (ConfigXMLParseException|IOException e) {
/*    */             Messages.msg.trace("Failed to parse worker XML definition", e);
/*    */           }  if (worker == null) {
/*    */             Iterator<XnioWorkerConfigurator> iterator = ServiceLoader.<XnioWorkerConfigurator>load(XnioWorkerConfigurator.class, DefaultXnioWorkerHolder.class.getClassLoader()).iterator(); while (worker == null) {
/*    */               try {
/*    */                 if (!iterator.hasNext())
/*    */                   break;  XnioWorkerConfigurator configurator = iterator.next();
/*    */                 if (configurator != null)
/*    */                   try {
/*    */                     worker = configurator.createWorker();
/* 52 */                   } catch (IOException e) {
/*    */                     Messages.msg.trace("Failed to configure the default worker", e);
/*    */                   }  
/* 55 */               } catch (ServiceConfigurationError e) {
/*    */                 Messages.msg.trace("Failed to configure a service", e);
/*    */               } 
/*    */             } 
/*    */           }  if (worker == null)
/*    */             try {
/*    */               worker = xnio.createWorker(OptionMap.create(Options.THREAD_DAEMON, Boolean.TRUE));
/* 62 */             } catch (IOException e) {
/*    */               throw new IOError(e);
/*    */             }  
/*    */           return worker;
/*    */         });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\DefaultXnioWorkerHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */