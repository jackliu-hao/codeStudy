package ch.qos.logback.classic.selector;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.JNDIUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusPrinter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.Context;
import javax.naming.NamingException;

public class ContextJNDISelector implements ContextSelector {
   private final Map<String, LoggerContext> synchronizedContextMap = Collections.synchronizedMap(new HashMap());
   private final LoggerContext defaultContext;
   private static final ThreadLocal<LoggerContext> threadLocal = new ThreadLocal();

   public ContextJNDISelector(LoggerContext context) {
      this.defaultContext = context;
   }

   public LoggerContext getDefaultLoggerContext() {
      return this.defaultContext;
   }

   public LoggerContext detachLoggerContext(String loggerContextName) {
      return (LoggerContext)this.synchronizedContextMap.remove(loggerContextName);
   }

   public LoggerContext getLoggerContext() {
      String contextName = null;
      Context ctx = null;
      LoggerContext lc = (LoggerContext)threadLocal.get();
      if (lc != null) {
         return lc;
      } else {
         try {
            ctx = JNDIUtil.getInitialContext();
            contextName = JNDIUtil.lookupString(ctx, "java:comp/env/logback/context-name");
         } catch (NamingException var8) {
         }

         if (OptionHelper.isEmpty(contextName)) {
            return this.defaultContext;
         } else {
            LoggerContext loggerContext = (LoggerContext)this.synchronizedContextMap.get(contextName);
            if (loggerContext == null) {
               loggerContext = new LoggerContext();
               loggerContext.setName(contextName);
               this.synchronizedContextMap.put(contextName, loggerContext);
               URL url = this.findConfigFileURL(ctx, loggerContext);
               if (url != null) {
                  this.configureLoggerContextByURL(loggerContext, url);
               } else {
                  try {
                     (new ContextInitializer(loggerContext)).autoConfig();
                  } catch (JoranException var7) {
                  }
               }

               if (!StatusUtil.contextHasStatusListener(loggerContext)) {
                  StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
               }
            }

            return loggerContext;
         }
      }
   }

   private String conventionalConfigFileName(String contextName) {
      return "logback-" + contextName + ".xml";
   }

   private URL findConfigFileURL(Context ctx, LoggerContext loggerContext) {
      StatusManager sm = loggerContext.getStatusManager();
      String jndiEntryForConfigResource = null;

      try {
         jndiEntryForConfigResource = JNDIUtil.lookupString(ctx, "java:comp/env/logback/configuration-resource");
      } catch (NamingException var7) {
      }

      if (jndiEntryForConfigResource != null) {
         sm.add((Status)(new InfoStatus("Searching for [" + jndiEntryForConfigResource + "]", this)));
         URL url = this.urlByResourceName(sm, jndiEntryForConfigResource);
         if (url == null) {
            String msg = "The jndi resource [" + jndiEntryForConfigResource + "] for context [" + loggerContext.getName() + "] does not lead to a valid file";
            sm.add((Status)(new WarnStatus(msg, this)));
         }

         return url;
      } else {
         String resourceByConvention = this.conventionalConfigFileName(loggerContext.getName());
         return this.urlByResourceName(sm, resourceByConvention);
      }
   }

   private URL urlByResourceName(StatusManager sm, String resourceName) {
      sm.add((Status)(new InfoStatus("Searching for [" + resourceName + "]", this)));
      URL url = Loader.getResource(resourceName, Loader.getTCL());
      return url != null ? url : Loader.getResourceBySelfClassLoader(resourceName);
   }

   private void configureLoggerContextByURL(LoggerContext context, URL url) {
      try {
         JoranConfigurator configurator = new JoranConfigurator();
         context.reset();
         configurator.setContext(context);
         configurator.doConfigure(url);
      } catch (JoranException var4) {
      }

      StatusPrinter.printInCaseOfErrorsOrWarnings(context);
   }

   public List<String> getContextNames() {
      List<String> list = new ArrayList();
      list.addAll(this.synchronizedContextMap.keySet());
      return list;
   }

   public LoggerContext getLoggerContext(String name) {
      return (LoggerContext)this.synchronizedContextMap.get(name);
   }

   public int getCount() {
      return this.synchronizedContextMap.size();
   }

   public void setLocalContext(LoggerContext context) {
      threadLocal.set(context);
   }

   public void removeLocalContext() {
      threadLocal.remove();
   }
}
