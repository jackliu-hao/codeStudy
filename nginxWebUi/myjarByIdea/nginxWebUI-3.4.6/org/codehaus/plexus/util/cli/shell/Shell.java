package org.codehaus.plexus.util.cli.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;

public class Shell implements Cloneable {
   private static final char[] DEFAULT_QUOTING_TRIGGER_CHARS = new char[]{' '};
   private String shellCommand;
   private List shellArgs = new ArrayList();
   private boolean quotedArgumentsEnabled = true;
   private String executable;
   private String workingDir;
   private boolean quotedExecutableEnabled = true;
   private boolean doubleQuotedArgumentEscaped = false;
   private boolean singleQuotedArgumentEscaped = false;
   private boolean doubleQuotedExecutableEscaped = false;
   private boolean singleQuotedExecutableEscaped = false;
   private char argQuoteDelimiter = '"';
   private char exeQuoteDelimiter = '"';

   public void setShellCommand(String shellCommand) {
      this.shellCommand = shellCommand;
   }

   public String getShellCommand() {
      return this.shellCommand;
   }

   public void setShellArgs(String[] shellArgs) {
      this.shellArgs.clear();
      this.shellArgs.addAll(Arrays.asList(shellArgs));
   }

   public String[] getShellArgs() {
      return this.shellArgs != null && !this.shellArgs.isEmpty() ? (String[])this.shellArgs.toArray(new String[this.shellArgs.size()]) : null;
   }

   public List getCommandLine(String executable, String[] arguments) {
      return this.getRawCommandLine(executable, arguments);
   }

   protected List getRawCommandLine(String executable, String[] arguments) {
      List commandLine = new ArrayList();
      StringBuffer sb = new StringBuffer();
      char[] escapeChars;
      if (executable != null) {
         String preamble = this.getExecutionPreamble();
         if (preamble != null) {
            sb.append(preamble);
         }

         if (this.isQuotedExecutableEnabled()) {
            escapeChars = this.getEscapeChars(this.isSingleQuotedExecutableEscaped(), this.isDoubleQuotedExecutableEscaped());
            sb.append(StringUtils.quoteAndEscape(this.getExecutable(), this.getExecutableQuoteDelimiter(), escapeChars, this.getQuotingTriggerChars(), '\\', false));
         } else {
            sb.append(this.getExecutable());
         }
      }

      for(int i = 0; i < arguments.length; ++i) {
         if (sb.length() > 0) {
            sb.append(" ");
         }

         if (this.isQuotedArgumentsEnabled()) {
            escapeChars = this.getEscapeChars(this.isSingleQuotedExecutableEscaped(), this.isDoubleQuotedExecutableEscaped());
            sb.append(StringUtils.quoteAndEscape(arguments[i], this.getArgumentQuoteDelimiter(), escapeChars, this.getQuotingTriggerChars(), '\\', false));
         } else {
            sb.append(arguments[i]);
         }
      }

      commandLine.add(sb.toString());
      return commandLine;
   }

   protected char[] getQuotingTriggerChars() {
      return DEFAULT_QUOTING_TRIGGER_CHARS;
   }

   protected String getExecutionPreamble() {
      return null;
   }

   protected char[] getEscapeChars(boolean includeSingleQuote, boolean includeDoubleQuote) {
      StringBuffer buf = new StringBuffer(2);
      if (includeSingleQuote) {
         buf.append('\'');
      }

      if (includeDoubleQuote) {
         buf.append('"');
      }

      char[] result = new char[buf.length()];
      buf.getChars(0, buf.length(), result, 0);
      return result;
   }

   protected boolean isDoubleQuotedArgumentEscaped() {
      return this.doubleQuotedArgumentEscaped;
   }

   protected boolean isSingleQuotedArgumentEscaped() {
      return this.singleQuotedArgumentEscaped;
   }

   protected boolean isDoubleQuotedExecutableEscaped() {
      return this.doubleQuotedExecutableEscaped;
   }

   protected boolean isSingleQuotedExecutableEscaped() {
      return this.singleQuotedExecutableEscaped;
   }

   protected void setArgumentQuoteDelimiter(char argQuoteDelimiter) {
      this.argQuoteDelimiter = argQuoteDelimiter;
   }

   protected char getArgumentQuoteDelimiter() {
      return this.argQuoteDelimiter;
   }

   protected void setExecutableQuoteDelimiter(char exeQuoteDelimiter) {
      this.exeQuoteDelimiter = exeQuoteDelimiter;
   }

   protected char getExecutableQuoteDelimiter() {
      return this.exeQuoteDelimiter;
   }

   public List getShellCommandLine(String[] arguments) {
      List commandLine = new ArrayList();
      if (this.getShellCommand() != null) {
         commandLine.add(this.getShellCommand());
      }

      if (this.getShellArgs() != null) {
         commandLine.addAll(this.getShellArgsList());
      }

      commandLine.addAll(this.getCommandLine(this.getExecutable(), arguments));
      return commandLine;
   }

   public List getShellArgsList() {
      return this.shellArgs;
   }

   public void addShellArg(String arg) {
      this.shellArgs.add(arg);
   }

   public void setQuotedArgumentsEnabled(boolean quotedArgumentsEnabled) {
      this.quotedArgumentsEnabled = quotedArgumentsEnabled;
   }

   public boolean isQuotedArgumentsEnabled() {
      return this.quotedArgumentsEnabled;
   }

   public void setQuotedExecutableEnabled(boolean quotedExecutableEnabled) {
      this.quotedExecutableEnabled = quotedExecutableEnabled;
   }

   public boolean isQuotedExecutableEnabled() {
      return this.quotedExecutableEnabled;
   }

   public void setExecutable(String executable) {
      if (executable != null && executable.length() != 0) {
         this.executable = executable.replace('/', File.separatorChar).replace('\\', File.separatorChar);
      }
   }

   public String getExecutable() {
      return this.executable;
   }

   public void setWorkingDirectory(String path) {
      if (path != null) {
         this.workingDir = path;
      }

   }

   public void setWorkingDirectory(File workingDir) {
      if (workingDir != null) {
         this.workingDir = workingDir.getAbsolutePath();
      }

   }

   public File getWorkingDirectory() {
      return this.workingDir == null ? null : new File(this.workingDir);
   }

   public String getWorkingDirectoryAsString() {
      return this.workingDir;
   }

   public void clearArguments() {
      this.shellArgs.clear();
   }

   public Object clone() {
      Shell shell = new Shell();
      shell.setExecutable(this.getExecutable());
      shell.setWorkingDirectory(this.getWorkingDirectory());
      shell.setShellArgs(this.getShellArgs());
      return shell;
   }

   public String getOriginalExecutable() {
      return this.executable;
   }

   public List getOriginalCommandLine(String executable, String[] arguments) {
      return this.getRawCommandLine(executable, arguments);
   }

   protected void setDoubleQuotedArgumentEscaped(boolean doubleQuotedArgumentEscaped) {
      this.doubleQuotedArgumentEscaped = doubleQuotedArgumentEscaped;
   }

   protected void setDoubleQuotedExecutableEscaped(boolean doubleQuotedExecutableEscaped) {
      this.doubleQuotedExecutableEscaped = doubleQuotedExecutableEscaped;
   }

   protected void setSingleQuotedArgumentEscaped(boolean singleQuotedArgumentEscaped) {
      this.singleQuotedArgumentEscaped = singleQuotedArgumentEscaped;
   }

   protected void setSingleQuotedExecutableEscaped(boolean singleQuotedExecutableEscaped) {
      this.singleQuotedExecutableEscaped = singleQuotedExecutableEscaped;
   }
}
