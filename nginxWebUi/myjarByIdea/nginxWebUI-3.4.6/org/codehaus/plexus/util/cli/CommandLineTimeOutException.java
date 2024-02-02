package org.codehaus.plexus.util.cli;

public class CommandLineTimeOutException extends CommandLineException {
   public CommandLineTimeOutException(String message) {
      super(message);
   }

   public CommandLineTimeOutException(String message, Throwable cause) {
      super(message, cause);
   }
}
