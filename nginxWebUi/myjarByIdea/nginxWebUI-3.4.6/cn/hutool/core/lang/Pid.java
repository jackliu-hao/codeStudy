package cn.hutool.core.lang;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;
import java.lang.management.ManagementFactory;

public enum Pid {
   INSTANCE;

   private final int pid = getPid();

   public int get() {
      return this.pid;
   }

   private static int getPid() throws UtilException {
      String processName = ManagementFactory.getRuntimeMXBean().getName();
      if (StrUtil.isBlank(processName)) {
         throw new UtilException("Process name is blank!");
      } else {
         int atIndex = processName.indexOf(64);
         return atIndex > 0 ? Integer.parseInt(processName.substring(0, atIndex)) : processName.hashCode();
      }
   }
}
