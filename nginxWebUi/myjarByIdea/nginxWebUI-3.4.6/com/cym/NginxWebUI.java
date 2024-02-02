package com.cym;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.cym.utils.JarUtil;
import com.cym.utils.SystemTool;
import freemarker.template.Configuration;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.noear.solon.Solon;
import org.noear.solon.schedule.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EnableScheduling
public class NginxWebUI {
   static Logger logger = LoggerFactory.getLogger(NginxWebUI.class);

   public static void main(String[] args) {
      try {
         killSelf(args);
         removeJar();
      } catch (Exception var2) {
         logger.error((String)var2.getMessage(), (Throwable)var2);
      }

      Solon.start(NginxWebUI.class, args, (app) -> {
         app.onError((e) -> {
            logger.error(e.getMessage(), e);
         });
         app.before((c) -> {
            String path;
            for(path = c.path(); path.contains("//"); path = path.replace("//", "/")) {
            }

            c.pathNew(path);
         });
         app.onEvent(Configuration.class, (cfg) -> {
            cfg.setSetting("classic_compatible", "true");
            cfg.setSetting("number_format", "0.##");
         });
      });
   }

   public static void killSelf(String[] args) {
      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      String myPid = runtimeMXBean.getName().split("@")[0];
      new ArrayList();
      List<String> list = RuntimeUtil.execForLines("jps");
      Iterator var4 = list.iterator();

      while(var4.hasNext()) {
         String line = (String)var4.next();
         if (line.contains("nginxWebUI") && line.contains(".jar")) {
            String pid = line.split("\\s+")[0].trim();
            if (!pid.equals(myPid)) {
               logger.info("杀掉旧进程:" + pid);
               if (SystemTool.isWindows()) {
                  RuntimeUtil.exec("taskkill /im " + pid + " /f");
               } else if (SystemTool.isLinux()) {
                  RuntimeUtil.exec("kill -9 " + pid);
               }
            }
         }
      }

   }

   private static void removeJar() {
      File[] list = (new File(JarUtil.getCurrentFilePath())).getParentFile().listFiles();
      File[] var1 = list;
      int var2 = list.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         File file = var1[var3];
         if (file.getName().startsWith("nginxWebUI") && file.getName().endsWith(".jar") && !file.getPath().equals(JarUtil.getCurrentFilePath())) {
            FileUtil.del(file);
            logger.info("删除文件:" + file);
         }
      }

   }
}
