package cn.hutool.log.dialect.jdk;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import java.io.InputStream;
import java.util.logging.LogManager;

public class JdkLogFactory extends LogFactory {
   public JdkLogFactory() {
      super("JDK Logging");
      this.readConfig();
   }

   public Log createLog(String name) {
      return new JdkLog(name);
   }

   public Log createLog(Class<?> clazz) {
      return new JdkLog(clazz);
   }

   private void readConfig() {
      InputStream in = ResourceUtil.getStreamSafe("logging.properties");
      if (null == in) {
         System.err.println("[WARN] Can not find [logging.properties], use [%JRE_HOME%/lib/logging.properties] as default!");
      } else {
         try {
            LogManager.getLogManager().readConfiguration(in);
         } catch (Exception var9) {
            Console.error(var9, "Read [logging.properties] from classpath error!");

            try {
               LogManager.getLogManager().readConfiguration();
            } catch (Exception var8) {
               Console.error(var9, "Read [logging.properties] from [%JRE_HOME%/lib/logging.properties] error!");
            }
         } finally {
            IoUtil.close(in);
         }

      }
   }
}
