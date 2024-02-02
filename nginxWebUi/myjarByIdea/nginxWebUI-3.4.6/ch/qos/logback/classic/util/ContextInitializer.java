package ch.qos.logback.classic.util;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

public class ContextInitializer {
   public static final String AUTOCONFIG_FILE = "logback.xml";
   public static final String TEST_AUTOCONFIG_FILE = "logback-test.xml";
   public static final String CONFIG_FILE_PROPERTY = "logback.configurationFile";
   final LoggerContext loggerContext;

   public ContextInitializer(LoggerContext loggerContext) {
      this.loggerContext = loggerContext;
   }

   public void configureByResource(URL url) throws JoranException {
      if (url == null) {
         throw new IllegalArgumentException("URL argument cannot be null");
      } else {
         String urlString = url.toString();
         if (urlString.endsWith("xml")) {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(this.loggerContext);
            configurator.doConfigure(url);
         } else {
            throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be .xml");
         }
      }
   }

   void joranConfigureByResource(URL url) throws JoranException {
      JoranConfigurator configurator = new JoranConfigurator();
      configurator.setContext(this.loggerContext);
      configurator.doConfigure(url);
   }

   private URL findConfigFileURLFromSystemProperties(ClassLoader classLoader, boolean updateStatus) {
      String logbackConfigFile = OptionHelper.getSystemProperty("logback.configurationFile");
      if (logbackConfigFile != null) {
         URL result = null;

         URL var7;
         try {
            result = new URL(logbackConfigFile);
            URL var5 = result;
            return var5;
         } catch (MalformedURLException var13) {
            result = Loader.getResource(logbackConfigFile, classLoader);
            if (result != null) {
               URL var15 = result;
               return var15;
            }

            File f = new File(logbackConfigFile);
            if (!f.exists() || !f.isFile()) {
               return null;
            }

            try {
               result = f.toURI().toURL();
               var7 = result;
            } catch (MalformedURLException var12) {
               return null;
            }
         } finally {
            if (updateStatus) {
               this.statusOnResourceSearch(logbackConfigFile, classLoader, result);
            }

         }

         return var7;
      } else {
         return null;
      }
   }

   public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {
      ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
      URL url = this.findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
      if (url != null) {
         return url;
      } else {
         url = this.getResource("logback-test.xml", myClassLoader, updateStatus);
         return url != null ? url : this.getResource("logback.xml", myClassLoader, updateStatus);
      }
   }

   private URL getResource(String filename, ClassLoader myClassLoader, boolean updateStatus) {
      URL url = Loader.getResource(filename, myClassLoader);
      if (updateStatus) {
         this.statusOnResourceSearch(filename, myClassLoader, url);
      }

      return url;
   }

   public void autoConfig() throws JoranException {
      StatusListenerConfigHelper.installIfAsked(this.loggerContext);
      URL url = this.findURLOfDefaultConfigurationFile(true);
      if (url != null) {
         this.configureByResource(url);
      } else {
         Configurator c = (Configurator)EnvUtil.loadFromServiceLoader(Configurator.class);
         if (c != null) {
            try {
               c.setContext(this.loggerContext);
               c.configure(this.loggerContext);
            } catch (Exception var4) {
               throw new LogbackException(String.format("Failed to initialize Configurator: %s using ServiceLoader", c != null ? c.getClass().getCanonicalName() : "null"), var4);
            }
         } else {
            BasicConfigurator basicConfigurator = new BasicConfigurator();
            basicConfigurator.setContext(this.loggerContext);
            basicConfigurator.configure(this.loggerContext);
         }
      }

   }

   private void statusOnResourceSearch(String resourceName, ClassLoader classLoader, URL url) {
      StatusManager sm = this.loggerContext.getStatusManager();
      if (url == null) {
         sm.add((Status)(new InfoStatus("Could NOT find resource [" + resourceName + "]", this.loggerContext)));
      } else {
         sm.add((Status)(new InfoStatus("Found resource [" + resourceName + "] at [" + url.toString() + "]", this.loggerContext)));
         this.multiplicityWarning(resourceName, classLoader);
      }

   }

   private void multiplicityWarning(String resourceName, ClassLoader classLoader) {
      Set<URL> urlSet = null;
      StatusManager sm = this.loggerContext.getStatusManager();

      try {
         urlSet = Loader.getResources(resourceName, classLoader);
      } catch (IOException var7) {
         sm.add((Status)(new ErrorStatus("Failed to get url list for resource [" + resourceName + "]", this.loggerContext, var7)));
      }

      if (urlSet != null && urlSet.size() > 1) {
         sm.add((Status)(new WarnStatus("Resource [" + resourceName + "] occurs multiple times on the classpath.", this.loggerContext)));
         Iterator var5 = urlSet.iterator();

         while(var5.hasNext()) {
            URL url = (URL)var5.next();
            sm.add((Status)(new WarnStatus("Resource [" + resourceName + "] occurs at [" + url.toString() + "]", this.loggerContext)));
         }
      }

   }
}
