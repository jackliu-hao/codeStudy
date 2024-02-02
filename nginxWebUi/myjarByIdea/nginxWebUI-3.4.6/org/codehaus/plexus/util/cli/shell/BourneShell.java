package org.codehaus.plexus.util.cli.shell;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

public class BourneShell extends Shell {
   private static final char[] BASH_QUOTING_TRIGGER_CHARS = new char[]{' ', '$', ';', '&', '|', '<', '>', '*', '?', '(', ')', '[', ']', '{', '}', '`'};

   public BourneShell() {
      this(false);
   }

   public BourneShell(boolean isLoginShell) {
      this.setShellCommand("/bin/sh");
      this.setArgumentQuoteDelimiter('\'');
      this.setExecutableQuoteDelimiter('"');
      this.setSingleQuotedArgumentEscaped(true);
      this.setSingleQuotedExecutableEscaped(false);
      this.setQuotedExecutableEnabled(true);
      if (isLoginShell) {
         this.addShellArg("-l");
      }

   }

   public String getExecutable() {
      return Os.isFamily("windows") ? super.getExecutable() : unifyQuotes(super.getExecutable());
   }

   public List getShellArgsList() {
      List shellArgs = new ArrayList();
      List existingShellArgs = super.getShellArgsList();
      if (existingShellArgs != null && !existingShellArgs.isEmpty()) {
         shellArgs.addAll(existingShellArgs);
      }

      shellArgs.add("-c");
      return shellArgs;
   }

   public String[] getShellArgs() {
      String[] shellArgs = super.getShellArgs();
      if (shellArgs == null) {
         shellArgs = new String[0];
      }

      if (shellArgs.length > 0 && !shellArgs[shellArgs.length - 1].equals("-c")) {
         String[] newArgs = new String[shellArgs.length + 1];
         System.arraycopy(shellArgs, 0, newArgs, 0, shellArgs.length);
         newArgs[shellArgs.length] = "-c";
         shellArgs = newArgs;
      }

      return shellArgs;
   }

   protected String getExecutionPreamble() {
      if (this.getWorkingDirectoryAsString() == null) {
         return null;
      } else {
         String dir = this.getWorkingDirectoryAsString();
         StringBuffer sb = new StringBuffer();
         sb.append("cd ");
         sb.append(unifyQuotes(dir));
         sb.append(" && ");
         return sb.toString();
      }
   }

   protected char[] getQuotingTriggerChars() {
      return BASH_QUOTING_TRIGGER_CHARS;
   }

   protected static String unifyQuotes(String path) {
      if (path == null) {
         return null;
      } else {
         return path.indexOf(" ") == -1 && path.indexOf("'") != -1 && path.indexOf("\"") == -1 ? StringUtils.escape(path) : StringUtils.quoteAndEscape(path, '"', BASH_QUOTING_TRIGGER_CHARS);
      }
   }
}
