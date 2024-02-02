package cn.hutool.log.dialect.logtube;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.github.logtube.Logtube;

public class LogTubeLogFactory extends LogFactory {
   public LogTubeLogFactory() {
      super("LogTube");
      this.checkLogExist(Logtube.class);
   }

   public Log createLog(String name) {
      return new LogTubeLog(name);
   }

   public Log createLog(Class<?> clazz) {
      return new LogTubeLog(clazz);
   }
}
