package ch.qos.logback.core.status;

import java.io.PrintStream;

public class OnConsoleStatusListener extends OnPrintStreamStatusListenerBase {
   protected PrintStream getPrintStream() {
      return System.out;
   }
}
