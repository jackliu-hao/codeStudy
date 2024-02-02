package com.cym.utils;

import cn.hutool.core.util.RuntimeUtil;

public class NginxUtils {
   public static boolean isRun() {
      boolean isRun = false;
      String[] command;
      String rs;
      if (SystemTool.isWindows()) {
         command = new String[]{"tasklist"};
         rs = RuntimeUtil.execForStr(command);
         isRun = rs.toLowerCase().contains("nginx.exe");
      } else {
         command = new String[]{"/bin/sh", "-c", "ps -ef|grep nginx"};
         rs = RuntimeUtil.execForStr(command);
         isRun = rs.contains("nginx: master process") || rs.contains("nginx: worker process");
      }

      return isRun;
   }
}
