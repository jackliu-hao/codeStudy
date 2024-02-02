package org.noear.solon.logging.appender;

import java.io.Console;

public class ConsoleAppender extends OutputStreamAppender {
   public ConsoleAppender() {
      Console console = System.console();
      if (console != null) {
         this.setOutput(console.writer());
      } else {
         this.setOutput(System.out);
      }

   }
}
