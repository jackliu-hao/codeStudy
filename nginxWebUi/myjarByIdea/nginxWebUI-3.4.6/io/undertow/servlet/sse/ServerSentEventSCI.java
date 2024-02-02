package io.undertow.servlet.sse;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathTemplateHandler;
import io.undertow.server.handlers.sse.ServerSentEventConnectionCallback;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.servlet.api.InstanceHandle;
import io.undertow.servlet.spec.ServletContextImpl;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

@HandlesTypes({ServerSentEvent.class})
public class ServerSentEventSCI implements ServletContainerInitializer {
   public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
      if (c != null && !c.isEmpty()) {
         try {
            final Map<String, ServerSentEventConnectionCallback> callbacks = new HashMap();
            ServletContextImpl servletContext = (ServletContextImpl)ctx;
            final List<InstanceHandle<?>> handles = new ArrayList();
            Iterator var6 = c.iterator();

            while(var6.hasNext()) {
               Class<?> clazz = (Class)var6.next();
               ServerSentEvent annotation = (ServerSentEvent)clazz.getAnnotation(ServerSentEvent.class);
               if (annotation != null) {
                  String path = annotation.value();
                  InstanceHandle<?> instance = servletContext.getDeployment().getDeploymentInfo().getClassIntrospecter().createInstanceFactory(clazz).createInstance();
                  handles.add(instance);
                  callbacks.put(path, (ServerSentEventConnectionCallback)instance.getInstance());
               }
            }

            if (!callbacks.isEmpty()) {
               servletContext.getDeployment().getDeploymentInfo().addInnerHandlerChainWrapper(new HandlerWrapper() {
                  public HttpHandler wrap(HttpHandler handler) {
                     PathTemplateHandler pathTemplateHandler = new PathTemplateHandler(handler, false);
                     Iterator var3 = callbacks.entrySet().iterator();

                     while(var3.hasNext()) {
                        Map.Entry<String, ServerSentEventConnectionCallback> e = (Map.Entry)var3.next();
                        pathTemplateHandler.add((String)e.getKey(), new ServerSentEventHandler((ServerSentEventConnectionCallback)e.getValue()));
                     }

                     return pathTemplateHandler;
                  }
               });
               servletContext.addListener((EventListener)(new ServletContextListener() {
                  public void contextInitialized(ServletContextEvent sce) {
                  }

                  public void contextDestroyed(ServletContextEvent sce) {
                     Iterator var2 = handles.iterator();

                     while(var2.hasNext()) {
                        InstanceHandle<?> h = (InstanceHandle)var2.next();
                        h.release();
                     }

                  }
               }));
            }
         } catch (Exception var11) {
            throw new ServletException(var11);
         }
      }
   }
}
