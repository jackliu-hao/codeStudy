/*     */ package org.codehaus.plexus.util.cli;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Vector;
/*     */ import org.codehaus.plexus.util.Os;
/*     */ import org.codehaus.plexus.util.StringUtils;
/*     */ import org.codehaus.plexus.util.cli.shell.BourneShell;
/*     */ import org.codehaus.plexus.util.cli.shell.CmdShell;
/*     */ import org.codehaus.plexus.util.cli.shell.CommandShell;
/*     */ import org.codehaus.plexus.util.cli.shell.Shell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Commandline
/*     */   implements Cloneable
/*     */ {
/*     */   protected static final String OS_NAME = "os.name";
/*     */   protected static final String WINDOWS = "Windows";
/* 117 */   protected Vector arguments = new Vector();
/*     */ 
/*     */ 
/*     */   
/* 121 */   protected Map envVars = Collections.synchronizedMap(new LinkedHashMap());
/*     */   
/* 123 */   private long pid = -1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Shell shell;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String executable;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private File workingDir;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Commandline(String toProcess, Shell shell) {
/* 146 */     this.shell = shell;
/*     */     
/* 148 */     String[] tmp = new String[0];
/*     */     
/*     */     try {
/* 151 */       tmp = CommandLineUtils.translateCommandline(toProcess);
/*     */     }
/* 153 */     catch (Exception e) {
/*     */       
/* 155 */       System.err.println("Error translating Commandline.");
/*     */     } 
/* 157 */     if (tmp != null && tmp.length > 0) {
/*     */       
/* 159 */       setExecutable(tmp[0]);
/* 160 */       for (int i = 1; i < tmp.length; i++)
/*     */       {
/* 162 */         createArgument().setValue(tmp[i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Commandline(Shell shell) {
/* 173 */     this.shell = shell;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Commandline(String toProcess) {
/* 184 */     setDefaultShell();
/* 185 */     String[] tmp = new String[0];
/*     */     
/*     */     try {
/* 188 */       tmp = CommandLineUtils.translateCommandline(toProcess);
/*     */     }
/* 190 */     catch (Exception e) {
/*     */       
/* 192 */       System.err.println("Error translating Commandline.");
/*     */     } 
/* 194 */     if (tmp != null && tmp.length > 0) {
/*     */       
/* 196 */       setExecutable(tmp[0]);
/* 197 */       for (int i = 1; i < tmp.length; i++)
/*     */       {
/* 199 */         createArgument().setValue(tmp[i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Commandline() {
/* 210 */     setDefaultShell();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getPid() {
/* 215 */     if (this.pid == -1L)
/*     */     {
/* 217 */       this.pid = Long.parseLong(String.valueOf(System.currentTimeMillis()));
/*     */     }
/*     */     
/* 220 */     return this.pid;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPid(long pid) {
/* 225 */     this.pid = pid;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public class Marker
/*     */   {
/*     */     private int position;
/*     */ 
/*     */     
/*     */     private int realPos;
/*     */ 
/*     */     
/*     */     private final Commandline this$0;
/*     */ 
/*     */     
/*     */     Marker(Commandline this$0, int position) {
/* 242 */       this.this$0 = this$0; this.realPos = -1;
/* 243 */       this.position = position;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getPosition() {
/* 254 */       if (this.realPos == -1) {
/*     */         
/* 256 */         this.realPos = (this.this$0.getExecutable() == null) ? 0 : 1;
/* 257 */         for (int i = 0; i < this.position; i++) {
/*     */           
/* 259 */           Arg arg = this.this$0.arguments.elementAt(i);
/* 260 */           this.realPos += (arg.getParts()).length;
/*     */         } 
/*     */       } 
/* 263 */       return this.realPos;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setDefaultShell() {
/* 274 */     if (Os.isFamily("windows")) {
/*     */       
/* 276 */       if (Os.isFamily("win9x"))
/*     */       {
/* 278 */         setShell((Shell)new CommandShell());
/*     */       }
/*     */       else
/*     */       {
/* 282 */         setShell((Shell)new CmdShell());
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 287 */       setShell((Shell)new BourneShell());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Argument createArgument() {
/* 304 */     return createArgument(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Argument createArgument(boolean insertAtStart) {
/* 319 */     Argument argument = new Argument();
/* 320 */     if (insertAtStart) {
/*     */       
/* 322 */       this.arguments.insertElementAt(argument, 0);
/*     */     }
/*     */     else {
/*     */       
/* 326 */       this.arguments.addElement(argument);
/*     */     } 
/* 328 */     return argument;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Arg createArg() {
/* 343 */     return createArg(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Arg createArg(boolean insertAtStart) {
/* 357 */     Arg argument = new Argument();
/* 358 */     if (insertAtStart) {
/*     */       
/* 360 */       this.arguments.insertElementAt(argument, 0);
/*     */     }
/*     */     else {
/*     */       
/* 364 */       this.arguments.addElement(argument);
/*     */     } 
/* 366 */     return argument;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addArg(Arg argument) {
/* 377 */     addArg(argument, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addArg(Arg argument, boolean insertAtStart) {
/* 388 */     if (insertAtStart) {
/*     */       
/* 390 */       this.arguments.insertElementAt(argument, 0);
/*     */     }
/*     */     else {
/*     */       
/* 394 */       this.arguments.addElement(argument);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExecutable(String executable) {
/* 403 */     this.shell.setExecutable(executable);
/* 404 */     this.executable = executable;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getExecutable() {
/* 409 */     String exec = this.shell.getExecutable();
/*     */     
/* 411 */     if (exec == null)
/*     */     {
/* 413 */       exec = this.executable;
/*     */     }
/*     */     
/* 416 */     return exec;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addArguments(String[] line) {
/* 421 */     for (int i = 0; i < line.length; i++)
/*     */     {
/* 423 */       createArgument().setValue(line[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addEnvironment(String name, String value) {
/* 433 */     this.envVars.put(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addSystemEnvironment() throws Exception {
/* 442 */     Properties systemEnvVars = CommandLineUtils.getSystemEnvVars();
/*     */     
/* 444 */     for (Iterator i = systemEnvVars.keySet().iterator(); i.hasNext(); ) {
/*     */       
/* 446 */       String key = i.next();
/* 447 */       if (!this.envVars.containsKey(key))
/*     */       {
/* 449 */         addEnvironment(key, systemEnvVars.getProperty(key));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getEnvironmentVariables() throws CommandLineException {
/*     */     try {
/* 462 */       addSystemEnvironment();
/*     */     }
/* 464 */     catch (Exception e) {
/*     */       
/* 466 */       throw new CommandLineException("Error setting up environmental variables", e);
/*     */     } 
/* 468 */     String[] environmentVars = new String[this.envVars.size()];
/* 469 */     int i = 0;
/* 470 */     for (Iterator iterator = this.envVars.keySet().iterator(); iterator.hasNext(); ) {
/*     */       
/* 472 */       String name = iterator.next();
/* 473 */       String value = (String)this.envVars.get(name);
/* 474 */       environmentVars[i] = name + "=" + value;
/* 475 */       i++;
/*     */     } 
/* 477 */     return environmentVars;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getCommandline() {
/* 485 */     String[] args = getArguments();
/* 486 */     String executable = getExecutable();
/*     */     
/* 488 */     if (executable == null)
/*     */     {
/* 490 */       return args;
/*     */     }
/* 492 */     String[] result = new String[args.length + 1];
/* 493 */     result[0] = executable;
/* 494 */     System.arraycopy(args, 0, result, 1, args.length);
/* 495 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getShellCommandline() {
/* 504 */     verifyShellState();
/*     */     
/* 506 */     return (String[])getShell().getShellCommandLine(getArguments()).toArray((Object[])new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getArguments() {
/* 515 */     Vector result = new Vector(this.arguments.size() * 2);
/* 516 */     for (int i = 0; i < this.arguments.size(); i++) {
/*     */       
/* 518 */       Argument arg = this.arguments.elementAt(i);
/* 519 */       String[] s = arg.getParts();
/* 520 */       if (s != null)
/*     */       {
/* 522 */         for (int j = 0; j < s.length; j++)
/*     */         {
/* 524 */           result.addElement(s[j]);
/*     */         }
/*     */       }
/*     */     } 
/*     */     
/* 529 */     String[] res = new String[result.size()];
/* 530 */     result.copyInto((Object[])res);
/* 531 */     return res;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 536 */     return StringUtils.join((Object[])getShellCommandline(), " ");
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 541 */     return (getCommandline()).length;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 546 */     Commandline c = new Commandline((Shell)this.shell.clone());
/* 547 */     c.executable = this.executable;
/* 548 */     c.workingDir = this.workingDir;
/* 549 */     c.addArguments(getArguments());
/* 550 */     return c;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 558 */     this.executable = null;
/* 559 */     this.workingDir = null;
/* 560 */     this.shell.setExecutable(null);
/* 561 */     this.shell.clearArguments();
/* 562 */     this.arguments.removeAllElements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearArgs() {
/* 570 */     this.arguments.removeAllElements();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Marker createMarker() {
/* 582 */     return new Marker(this, this.arguments.size());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWorkingDirectory(String path) {
/* 590 */     this.shell.setWorkingDirectory(path);
/* 591 */     this.workingDir = new File(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWorkingDirectory(File workingDirectory) {
/* 599 */     this.shell.setWorkingDirectory(workingDirectory);
/* 600 */     this.workingDir = workingDirectory;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getWorkingDirectory() {
/* 605 */     File workDir = this.shell.getWorkingDirectory();
/*     */     
/* 607 */     if (workDir == null)
/*     */     {
/* 609 */       workDir = this.workingDir;
/*     */     }
/*     */     
/* 612 */     return workDir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Process execute() throws CommandLineException {
/*     */     Process process;
/* 622 */     verifyShellState();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 628 */     String[] environment = getEnvironmentVariables();
/*     */     
/* 630 */     File workingDir = this.shell.getWorkingDirectory();
/*     */ 
/*     */     
/*     */     try {
/* 634 */       if (workingDir == null)
/*     */       {
/* 636 */         process = Runtime.getRuntime().exec(getShellCommandline(), environment);
/*     */       }
/*     */       else
/*     */       {
/* 640 */         if (!workingDir.exists())
/*     */         {
/* 642 */           throw new CommandLineException("Working directory \"" + workingDir.getPath() + "\" does not exist!");
/*     */         }
/*     */         
/* 645 */         if (!workingDir.isDirectory())
/*     */         {
/* 647 */           throw new CommandLineException("Path \"" + workingDir.getPath() + "\" does not specify a directory.");
/*     */         }
/*     */ 
/*     */         
/* 651 */         process = Runtime.getRuntime().exec(getShellCommandline(), environment, workingDir);
/*     */       }
/*     */     
/* 654 */     } catch (IOException ex) {
/*     */       
/* 656 */       throw new CommandLineException("Error while executing process.", ex);
/*     */     } 
/*     */     
/* 659 */     return process;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void verifyShellState() {
/* 667 */     if (this.shell.getWorkingDirectory() == null)
/*     */     {
/* 669 */       this.shell.setWorkingDirectory(this.workingDir);
/*     */     }
/*     */     
/* 672 */     if (this.shell.getExecutable() == null)
/*     */     {
/* 674 */       this.shell.setExecutable(this.executable);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Properties getSystemEnvVars() throws Exception {
/* 681 */     return CommandLineUtils.getSystemEnvVars();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShell(Shell shell) {
/* 692 */     this.shell = shell;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Shell getShell() {
/* 702 */     return this.shell;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] translateCommandline(String toProcess) throws Exception {
/* 711 */     return CommandLineUtils.translateCommandline(toProcess);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String quoteArgument(String argument) throws CommandLineException {
/* 720 */     return CommandLineUtils.quote(argument);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(String[] line) {
/* 728 */     return CommandLineUtils.toString(line);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Argument
/*     */     implements Arg
/*     */   {
/*     */     private String[] parts;
/*     */ 
/*     */ 
/*     */     
/*     */     public void setValue(String value) {
/* 741 */       if (value != null)
/*     */       {
/* 743 */         this.parts = new String[] { value };
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setLine(String line) {
/* 752 */       if (line == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/*     */       try {
/* 758 */         this.parts = CommandLineUtils.translateCommandline(line);
/*     */       }
/* 760 */       catch (Exception e) {
/*     */         
/* 762 */         System.err.println("Error translating Commandline.");
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void setFile(File value) {
/* 771 */       this.parts = new String[] { value.getAbsolutePath() };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getParts() {
/* 779 */       return this.parts;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\Commandline.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */