package com.mysql.cj.admin;

import com.mysql.cj.Constants;
import com.mysql.cj.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class ServerController {
   public static final String BASEDIR_KEY = "basedir";
   public static final String DATADIR_KEY = "datadir";
   public static final String DEFAULTS_FILE_KEY = "defaults-file";
   public static final String EXECUTABLE_NAME_KEY = "executable";
   public static final String EXECUTABLE_PATH_KEY = "executablePath";
   private Process serverProcess = null;
   private Properties serverProps = null;

   public ServerController(String baseDir) {
      this.setBaseDir(baseDir);
   }

   public ServerController(String basedir, String datadir) {
   }

   public void setBaseDir(String baseDir) {
      this.getServerProps().setProperty("basedir", baseDir);
   }

   public void setDataDir(String dataDir) {
      this.getServerProps().setProperty("datadir", dataDir);
   }

   public Process start() throws IOException {
      if (this.serverProcess != null) {
         throw new IllegalArgumentException("Server already started");
      } else {
         this.serverProcess = Runtime.getRuntime().exec(this.getCommandLine());
         return this.serverProcess;
      }
   }

   public void stop(boolean forceIfNecessary) throws IOException {
      if (this.serverProcess != null) {
         String basedir = this.getServerProps().getProperty("basedir");
         StringBuilder pathBuf = new StringBuilder(basedir);
         if (!basedir.endsWith(File.separator)) {
            pathBuf.append(File.separator);
         }

         pathBuf.append("bin");
         pathBuf.append(File.separator);
         pathBuf.append("mysqladmin shutdown");
         System.out.println(pathBuf.toString());
         Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
         int exitStatus = -1;

         try {
            exitStatus = mysqladmin.waitFor();
         } catch (InterruptedException var7) {
         }

         if (exitStatus != 0 && forceIfNecessary) {
            this.forceStop();
         }
      }

   }

   public void forceStop() {
      if (this.serverProcess != null) {
         this.serverProcess.destroy();
         this.serverProcess = null;
      }

   }

   public synchronized Properties getServerProps() {
      if (this.serverProps == null) {
         this.serverProps = new Properties();
      }

      return this.serverProps;
   }

   private String getCommandLine() {
      StringBuilder commandLine = new StringBuilder(this.getFullExecutablePath());
      commandLine.append(this.buildOptionalCommandLine());
      return commandLine.toString();
   }

   private String getFullExecutablePath() {
      StringBuilder pathBuf = new StringBuilder();
      String optionalExecutablePath = this.getServerProps().getProperty("executablePath");
      String executableName;
      if (optionalExecutablePath == null) {
         executableName = this.getServerProps().getProperty("basedir");
         pathBuf.append(executableName);
         if (!executableName.endsWith(File.separator)) {
            pathBuf.append(File.separatorChar);
         }

         if (this.runningOnWindows()) {
            pathBuf.append("bin");
         } else {
            pathBuf.append("libexec");
         }

         pathBuf.append(File.separatorChar);
      } else {
         pathBuf.append(optionalExecutablePath);
         if (!optionalExecutablePath.endsWith(File.separator)) {
            pathBuf.append(File.separatorChar);
         }
      }

      executableName = this.getServerProps().getProperty("executable", "mysqld");
      pathBuf.append(executableName);
      return pathBuf.toString();
   }

   private String buildOptionalCommandLine() {
      StringBuilder commandLineBuf = new StringBuilder();
      if (this.serverProps != null) {
         Iterator<Object> iter = this.serverProps.keySet().iterator();

         while(true) {
            while(true) {
               String key;
               String value;
               do {
                  if (!iter.hasNext()) {
                     return commandLineBuf.toString();
                  }

                  key = (String)iter.next();
                  value = this.serverProps.getProperty(key);
               } while(this.isNonCommandLineArgument(key));

               if (value != null && value.length() > 0) {
                  commandLineBuf.append(" \"");
                  commandLineBuf.append("--");
                  commandLineBuf.append(key);
                  commandLineBuf.append("=");
                  commandLineBuf.append(value);
                  commandLineBuf.append("\"");
               } else {
                  commandLineBuf.append(" --");
                  commandLineBuf.append(key);
               }
            }
         }
      } else {
         return commandLineBuf.toString();
      }
   }

   private boolean isNonCommandLineArgument(String propName) {
      return propName.equals("executable") || propName.equals("executablePath");
   }

   private boolean runningOnWindows() {
      return StringUtils.indexOfIgnoreCase(Constants.OS_NAME, "WINDOWS") != -1;
   }
}
