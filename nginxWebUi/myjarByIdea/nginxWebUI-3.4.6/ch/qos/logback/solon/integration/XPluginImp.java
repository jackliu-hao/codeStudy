package ch.qos.logback.solon.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.solon.SolonConfigurator;
import java.net.URL;
import java.util.Iterator;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.model.LoggerLevelEntity;
import org.slf4j.LoggerFactory;

public class XPluginImp implements Plugin {
   public void start(AopContext context) {
      URL url = Utils.getResource("logback.xml");
      if (url == null) {
         if (Utils.isNotEmpty(Solon.cfg().env())) {
            url = Utils.getResource("logback-solon-" + Solon.cfg().env() + ".xml");
         }

         if (url == null) {
            url = Utils.getResource("logback-solon.xml");
         }

         if (url == null) {
            url = Utils.getResource("META-INF/solon/logging/logback-def.xml");
         }

         if (url == null) {
            return;
         }

         this.initDo(url);
      }

   }

   private void initDo(URL url) {
      try {
         LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
         SolonConfigurator jc = new SolonConfigurator();
         jc.setContext(loggerContext);
         loggerContext.reset();
         jc.doConfigure(url);
         if (LogOptions.getLoggerLevels().size() > 0) {
            Iterator var4 = LogOptions.getLoggerLevels().iterator();

            while(var4.hasNext()) {
               LoggerLevelEntity lle = (LoggerLevelEntity)var4.next();
               Logger logger = loggerContext.getLogger(lle.getLoggerExpr());
               logger.setLevel(Level.valueOf(lle.getLevel().name()));
            }
         }

      } catch (JoranException var7) {
         throw new RuntimeException(var7);
      }
   }
}
