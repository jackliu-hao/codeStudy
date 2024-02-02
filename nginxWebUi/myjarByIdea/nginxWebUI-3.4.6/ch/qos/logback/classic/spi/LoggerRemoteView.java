package ch.qos.logback.classic.spi;

import ch.qos.logback.classic.LoggerContext;
import java.io.Serializable;

public class LoggerRemoteView implements Serializable {
   private static final long serialVersionUID = 5028223666108713696L;
   final LoggerContextVO loggerContextView;
   final String name;

   public LoggerRemoteView(String name, LoggerContext lc) {
      this.name = name;

      assert lc.getLoggerContextRemoteView() != null;

      this.loggerContextView = lc.getLoggerContextRemoteView();
   }

   public LoggerContextVO getLoggerContextView() {
      return this.loggerContextView;
   }

   public String getName() {
      return this.name;
   }
}
