package ch.qos.logback.core.status;

import java.io.PrintStream;

public class OnErrorConsoleStatusListener extends OnPrintStreamStatusListenerBase {
   protected PrintStream getPrintStream() {
      return System.err;
   }
}
