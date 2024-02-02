package com.cym.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import java.io.File;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UpdateUtils {
   @Inject("${server.port}")
   String port;
   @Inject("${project.home}")
   String home;
   @Inject("${spring.database.type:}")
   String type;
   @Inject("${spring.datasource.url:}")
   String url;
   @Inject("${spring.datasource.username:}")
   String username;
   @Inject("${spring.datasource.password:}")
   String password;
   private static final Logger LOG = LoggerFactory.getLogger(UpdateUtils.class);

   public void run(String path) {
      ThreadUtil.safeSleep(2000L);
      String newPath = path.replace(".update", "");
      FileUtil.rename(new File(path), newPath, true);
      String param = " --server.port=" + this.port + " --project.home=" + this.home;
      if ("mysql".equals(this.type.toLowerCase())) {
         param = param + " --spring.database.type=" + this.type + " --spring.datasource.url=" + this.url + " --spring.datasource.username=" + this.username + " --spring.datasource.password=" + this.password;
      }

      String cmd = null;
      if (SystemTool.isWindows()) {
         cmd = "java -jar -Dfile.encoding=UTF-8 " + newPath + param;
      } else {
         cmd = "nohup java -jar -Dfile.encoding=UTF-8 " + newPath + param + " > /dev/null &";
      }

      LOG.info(cmd);
      RuntimeUtil.exec(cmd);
   }
}
