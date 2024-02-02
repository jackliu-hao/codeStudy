/*    */ package io.undertow.servlet.sse;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.handlers.PathTemplateHandler;
/*    */ import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
/*    */ import io.undertow.server.handlers.sse.ServerSentEventHandler;
/*    */ import io.undertow.servlet.api.InstanceHandle;
/*    */ import io.undertow.servlet.spec.ServletContextImpl;
/*    */ import java.util.ArrayList;
/*    */ import java.util.EventListener;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import javax.servlet.ServletContainerInitializer;
/*    */ import javax.servlet.ServletContext;
/*    */ import javax.servlet.ServletContextEvent;
/*    */ import javax.servlet.ServletContextListener;
/*    */ import javax.servlet.ServletException;
/*    */ import javax.servlet.annotation.HandlesTypes;
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
/*    */ @HandlesTypes({ServerSentEvent.class})
/*    */ public class ServerSentEventSCI
/*    */   implements ServletContainerInitializer
/*    */ {
/*    */   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
/* 48 */     if (c == null || c.isEmpty()) {
/*    */       return;
/*    */     }
/*    */     try {
/* 52 */       final Map<String, ServerSentEventConnectionCallback> callbacks = new HashMap<>();
/* 53 */       ServletContextImpl servletContext = (ServletContextImpl)ctx;
/* 54 */       final List<InstanceHandle<?>> handles = new ArrayList<>();
/* 55 */       for (Class<?> clazz : c) {
/* 56 */         ServerSentEvent annotation = clazz.<ServerSentEvent>getAnnotation(ServerSentEvent.class);
/* 57 */         if (annotation == null) {
/*    */           continue;
/*    */         }
/* 60 */         String path = annotation.value();
/* 61 */         InstanceHandle<?> instance = servletContext.getDeployment().getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance();
/* 62 */         handles.add(instance);
/* 63 */         callbacks.put(path, (ServerSentEventConnectionCallback)instance.getInstance());
/*    */       } 
/*    */       
/* 66 */       if (callbacks.isEmpty()) {
/*    */         return;
/*    */       }
/*    */       
/* 70 */       servletContext.getDeployment().getDeploymentInfo().addInnerHandlerChainWrapper(new HandlerWrapper()
/*    */           {
/*    */             public HttpHandler wrap(HttpHandler handler) {
/* 73 */               PathTemplateHandler pathTemplateHandler = new PathTemplateHandler(handler, false);
/* 74 */               for (Map.Entry<String, ServerSentEventConnectionCallback> e : (Iterable<Map.Entry<String, ServerSentEventConnectionCallback>>)callbacks.entrySet()) {
/* 75 */                 pathTemplateHandler.add(e.getKey(), (HttpHandler)new ServerSentEventHandler(e.getValue()));
/*    */               }
/* 77 */               return (HttpHandler)pathTemplateHandler;
/*    */             }
/*    */           });
/* 80 */       servletContext.addListener((EventListener)new ServletContextListener()
/*    */           {
/*    */             public void contextInitialized(ServletContextEvent sce) {}
/*    */ 
/*    */ 
/*    */ 
/*    */             
/*    */             public void contextDestroyed(ServletContextEvent sce) {
/* 88 */               for (InstanceHandle<?> h : (Iterable<InstanceHandle<?>>)handles) {
/* 89 */                 h.release();
/*    */               }
/*    */             }
/*    */           });
/* 93 */     } catch (Exception e) {
/* 94 */       throw new ServletException(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\sse\ServerSentEventSCI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */