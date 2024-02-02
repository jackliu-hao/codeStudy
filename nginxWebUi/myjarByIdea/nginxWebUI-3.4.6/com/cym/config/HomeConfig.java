package com.cym.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cym.utils.FilePermissionUtil;
import com.cym.utils.JarUtil;
import com.cym.utils.SystemTool;
import com.cym.utils.ToolUtils;
import java.io.File;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class HomeConfig {
   @Inject("${project.home}")
   public String home;
   public String acmeShDir;
   public String acmeSh;
   Logger logger = LoggerFactory.getLogger(this.getClass());

   @Init
   public void init() {
      if (StrUtil.isEmpty(this.home)) {
         File file = new File(JarUtil.getCurrentFilePath());
         if (file.getPath().contains("target") && file.getPath().contains("classes")) {
            this.home = FileUtil.getUserHomePath() + File.separator + "svnWebUI";
         } else {
            this.home = file.getParent();
         }
      }

      if (SystemTool.isWindows() && !this.home.contains(":")) {
         this.home = JarUtil.getCurrentFilePath().split(":")[0] + ":" + this.home;
      }

      this.home = ToolUtils.endDir(ToolUtils.handlePath(this.home));
      if (!FilePermissionUtil.canWrite(new File(this.home))) {
         this.logger.error(this.home + " directory does not have writable permission. Please specify it again.");
         this.logger.error(this.home + " 目录没有可写权限,请重新指定.");
         System.exit(1);
      }

      this.acmeShDir = this.home + ".acme.sh/";
      this.acmeSh = this.home + ".acme.sh/acme.sh";
   }
}
