package org.codehaus.plexus.util.cli;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.CommandShell;
import org.codehaus.plexus.util.cli.shell.Shell;

public class Commandline implements Cloneable {
   /** @deprecated */
   protected static final String OS_NAME = "os.name";
   /** @deprecated */
   protected static final String WINDOWS = "Windows";
   protected Vector arguments = new Vector();
   protected Map envVars = Collections.synchronizedMap(new LinkedHashMap());
   private long pid = -1L;
   private Shell shell;
   /** @deprecated */
   protected String executable;
   /** @deprecated */
   private File workingDir;

   public Commandline(String toProcess, Shell shell) {
      this.shell = shell;
      String[] tmp = new String[0];

      try {
         tmp = CommandLineUtils.translateCommandline(toProcess);
      } catch (Exception var5) {
         System.err.println("Error translating Commandline.");
      }

      if (tmp != null && tmp.length > 0) {
         this.setExecutable(tmp[0]);

         for(int i = 1; i < tmp.length; ++i) {
            this.createArgument().setValue(tmp[i]);
         }
      }

   }

   public Commandline(Shell shell) {
      this.shell = shell;
   }

   public Commandline(String toProcess) {
      this.setDefaultShell();
      String[] tmp = new String[0];

      try {
         tmp = CommandLineUtils.translateCommandline(toProcess);
      } catch (Exception var4) {
         System.err.println("Error translating Commandline.");
      }

      if (tmp != null && tmp.length > 0) {
         this.setExecutable(tmp[0]);

         for(int i = 1; i < tmp.length; ++i) {
            this.createArgument().setValue(tmp[i]);
         }
      }

   }

   public Commandline() {
      this.setDefaultShell();
   }

   public long getPid() {
      if (this.pid == -1L) {
         this.pid = Long.parseLong(String.valueOf(System.currentTimeMillis()));
      }

      return this.pid;
   }

   public void setPid(long pid) {
      this.pid = pid;
   }

   private void setDefaultShell() {
      if (Os.isFamily("windows")) {
         if (Os.isFamily("win9x")) {
            this.setShell(new CommandShell());
         } else {
            this.setShell(new CmdShell());
         }
      } else {
         this.setShell(new BourneShell());
      }

   }

   /** @deprecated */
   public Argument createArgument() {
      return this.createArgument(false);
   }

   /** @deprecated */
   public Argument createArgument(boolean insertAtStart) {
      Argument argument = new Argument();
      if (insertAtStart) {
         this.arguments.insertElementAt(argument, 0);
      } else {
         this.arguments.addElement(argument);
      }

      return argument;
   }

   public Arg createArg() {
      return this.createArg(false);
   }

   public Arg createArg(boolean insertAtStart) {
      Arg argument = new Argument();
      if (insertAtStart) {
         this.arguments.insertElementAt(argument, 0);
      } else {
         this.arguments.addElement(argument);
      }

      return argument;
   }

   public void addArg(Arg argument) {
      this.addArg(argument, false);
   }

   public void addArg(Arg argument, boolean insertAtStart) {
      if (insertAtStart) {
         this.arguments.insertElementAt(argument, 0);
      } else {
         this.arguments.addElement(argument);
      }

   }

   public void setExecutable(String executable) {
      this.shell.setExecutable(executable);
      this.executable = executable;
   }

   public String getExecutable() {
      String exec = this.shell.getExecutable();
      if (exec == null) {
         exec = this.executable;
      }

      return exec;
   }

   public void addArguments(String[] line) {
      for(int i = 0; i < line.length; ++i) {
         this.createArgument().setValue(line[i]);
      }

   }

   public void addEnvironment(String name, String value) {
      this.envVars.put(name, value);
   }

   public void addSystemEnvironment() throws Exception {
      Properties systemEnvVars = CommandLineUtils.getSystemEnvVars();
      Iterator i = systemEnvVars.keySet().iterator();

      while(i.hasNext()) {
         String key = (String)i.next();
         if (!this.envVars.containsKey(key)) {
            this.addEnvironment(key, systemEnvVars.getProperty(key));
         }
      }

   }

   public String[] getEnvironmentVariables() throws CommandLineException {
      try {
         this.addSystemEnvironment();
      } catch (Exception var6) {
         throw new CommandLineException("Error setting up environmental variables", var6);
      }

      String[] environmentVars = new String[this.envVars.size()];
      int i = 0;

      for(Iterator iterator = this.envVars.keySet().iterator(); iterator.hasNext(); ++i) {
         String name = (String)iterator.next();
         String value = (String)this.envVars.get(name);
         environmentVars[i] = name + "=" + value;
      }

      return environmentVars;
   }

   public String[] getCommandline() {
      String[] args = this.getArguments();
      String executable = this.getExecutable();
      if (executable == null) {
         return args;
      } else {
         String[] result = new String[args.length + 1];
         result[0] = executable;
         System.arraycopy(args, 0, result, 1, args.length);
         return result;
      }
   }

   public String[] getShellCommandline() {
      this.verifyShellState();
      return (String[])this.getShell().getShellCommandLine(this.getArguments()).toArray(new String[0]);
   }

   public String[] getArguments() {
      Vector result = new Vector(this.arguments.size() * 2);

      for(int i = 0; i < this.arguments.size(); ++i) {
         Argument arg = (Argument)this.arguments.elementAt(i);
         String[] s = arg.getParts();
         if (s != null) {
            for(int j = 0; j < s.length; ++j) {
               result.addElement(s[j]);
            }
         }
      }

      String[] res = new String[result.size()];
      result.copyInto(res);
      return res;
   }

   public String toString() {
      return StringUtils.join((Object[])this.getShellCommandline(), " ");
   }

   public int size() {
      return this.getCommandline().length;
   }

   public Object clone() {
      Commandline c = new Commandline((Shell)this.shell.clone());
      c.executable = this.executable;
      c.workingDir = this.workingDir;
      c.addArguments(this.getArguments());
      return c;
   }

   public void clear() {
      this.executable = null;
      this.workingDir = null;
      this.shell.setExecutable((String)null);
      this.shell.clearArguments();
      this.arguments.removeAllElements();
   }

   public void clearArgs() {
      this.arguments.removeAllElements();
   }

   public Marker createMarker() {
      return new Marker(this.arguments.size());
   }

   public void setWorkingDirectory(String path) {
      this.shell.setWorkingDirectory(path);
      this.workingDir = new File(path);
   }

   public void setWorkingDirectory(File workingDirectory) {
      this.shell.setWorkingDirectory(workingDirectory);
      this.workingDir = workingDirectory;
   }

   public File getWorkingDirectory() {
      File workDir = this.shell.getWorkingDirectory();
      if (workDir == null) {
         workDir = this.workingDir;
      }

      return workDir;
   }

   public Process execute() throws CommandLineException {
      this.verifyShellState();
      String[] environment = this.getEnvironmentVariables();
      File workingDir = this.shell.getWorkingDirectory();

      try {
         Process process;
         if (workingDir == null) {
            process = Runtime.getRuntime().exec(this.getShellCommandline(), environment);
         } else {
            if (!workingDir.exists()) {
               throw new CommandLineException("Working directory \"" + workingDir.getPath() + "\" does not exist!");
            }

            if (!workingDir.isDirectory()) {
               throw new CommandLineException("Path \"" + workingDir.getPath() + "\" does not specify a directory.");
            }

            process = Runtime.getRuntime().exec(this.getShellCommandline(), environment, workingDir);
         }

         return process;
      } catch (IOException var5) {
         throw new CommandLineException("Error while executing process.", var5);
      }
   }

   /** @deprecated */
   private void verifyShellState() {
      if (this.shell.getWorkingDirectory() == null) {
         this.shell.setWorkingDirectory(this.workingDir);
      }

      if (this.shell.getExecutable() == null) {
         this.shell.setExecutable(this.executable);
      }

   }

   public Properties getSystemEnvVars() throws Exception {
      return CommandLineUtils.getSystemEnvVars();
   }

   public void setShell(Shell shell) {
      this.shell = shell;
   }

   public Shell getShell() {
      return this.shell;
   }

   /** @deprecated */
   public static String[] translateCommandline(String toProcess) throws Exception {
      return CommandLineUtils.translateCommandline(toProcess);
   }

   /** @deprecated */
   public static String quoteArgument(String argument) throws CommandLineException {
      return CommandLineUtils.quote(argument);
   }

   /** @deprecated */
   public static String toString(String[] line) {
      return CommandLineUtils.toString(line);
   }

   public static class Argument implements Arg {
      private String[] parts;

      public void setValue(String value) {
         if (value != null) {
            this.parts = new String[]{value};
         }

      }

      public void setLine(String line) {
         if (line != null) {
            try {
               this.parts = CommandLineUtils.translateCommandline(line);
            } catch (Exception var3) {
               System.err.println("Error translating Commandline.");
            }

         }
      }

      public void setFile(File value) {
         this.parts = new String[]{value.getAbsolutePath()};
      }

      public String[] getParts() {
         return this.parts;
      }
   }

   public class Marker {
      private int position;
      private int realPos = -1;

      Marker(int position) {
         this.position = position;
      }

      public int getPosition() {
         if (this.realPos == -1) {
            this.realPos = Commandline.this.getExecutable() == null ? 0 : 1;

            for(int i = 0; i < this.position; ++i) {
               Arg arg = (Arg)Commandline.this.arguments.elementAt(i);
               this.realPos += arg.getParts().length;
            }
         }

         return this.realPos;
      }
   }
}
