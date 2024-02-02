package org.noear.solon.logging.model;

import java.util.Objects;
import org.noear.solon.logging.event.Level;

public class LoggerLevelEntity {
   private final String loggerExpr;
   private final Level level;

   public Level getLevel() {
      return this.level;
   }

   public String getLoggerExpr() {
      return this.loggerExpr;
   }

   public LoggerLevelEntity(String loggerExpr, Level level) {
      this.loggerExpr = loggerExpr;
      this.level = level;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof LoggerLevelEntity)) {
         return false;
      } else {
         LoggerLevelEntity that = (LoggerLevelEntity)o;
         return Objects.equals(this.loggerExpr, that.loggerExpr);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.loggerExpr});
   }
}
