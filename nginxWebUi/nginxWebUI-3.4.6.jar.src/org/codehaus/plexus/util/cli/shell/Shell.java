/*     */ package org.codehaus.plexus.util.cli.shell;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.codehaus.plexus.util.StringUtils;
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
/*     */ public class Shell
/*     */   implements Cloneable
/*     */ {
/*  43 */   private static final char[] DEFAULT_QUOTING_TRIGGER_CHARS = new char[] { ' ' };
/*     */   
/*     */   private String shellCommand;
/*     */   
/*  47 */   private List shellArgs = new ArrayList();
/*     */   
/*     */   private boolean quotedArgumentsEnabled = true;
/*     */   
/*     */   private String executable;
/*     */   
/*     */   private String workingDir;
/*     */   
/*     */   private boolean quotedExecutableEnabled = true;
/*     */   
/*     */   private boolean doubleQuotedArgumentEscaped = false;
/*     */   
/*     */   private boolean singleQuotedArgumentEscaped = false;
/*     */   
/*     */   private boolean doubleQuotedExecutableEscaped = false;
/*     */   
/*     */   private boolean singleQuotedExecutableEscaped = false;
/*     */   
/*  65 */   private char argQuoteDelimiter = '"';
/*     */   
/*  67 */   private char exeQuoteDelimiter = '"';
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShellCommand(String shellCommand) {
/*  76 */     this.shellCommand = shellCommand;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getShellCommand() {
/*  86 */     return this.shellCommand;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setShellArgs(String[] shellArgs) {
/*  97 */     this.shellArgs.clear();
/*  98 */     this.shellArgs.addAll(Arrays.asList(shellArgs));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getShellArgs() {
/* 108 */     if (this.shellArgs == null || this.shellArgs.isEmpty())
/*     */     {
/* 110 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 114 */     return (String[])this.shellArgs.toArray((Object[])new String[this.shellArgs.size()]);
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
/*     */   public List getCommandLine(String executable, String[] arguments) {
/* 127 */     return getRawCommandLine(executable, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   protected List getRawCommandLine(String executable, String[] arguments) {
/* 132 */     List commandLine = new ArrayList();
/* 133 */     StringBuffer sb = new StringBuffer();
/*     */     
/* 135 */     if (executable != null) {
/*     */       
/* 137 */       String preamble = getExecutionPreamble();
/* 138 */       if (preamble != null)
/*     */       {
/* 140 */         sb.append(preamble);
/*     */       }
/*     */       
/* 143 */       if (isQuotedExecutableEnabled()) {
/*     */         
/* 145 */         char[] escapeChars = getEscapeChars(isSingleQuotedExecutableEscaped(), isDoubleQuotedExecutableEscaped());
/*     */         
/* 147 */         sb.append(StringUtils.quoteAndEscape(getExecutable(), getExecutableQuoteDelimiter(), escapeChars, getQuotingTriggerChars(), '\\', false));
/*     */       }
/*     */       else {
/*     */         
/* 151 */         sb.append(getExecutable());
/*     */       } 
/*     */     } 
/* 154 */     for (int i = 0; i < arguments.length; i++) {
/*     */       
/* 156 */       if (sb.length() > 0)
/*     */       {
/* 158 */         sb.append(" ");
/*     */       }
/*     */       
/* 161 */       if (isQuotedArgumentsEnabled()) {
/*     */         
/* 163 */         char[] escapeChars = getEscapeChars(isSingleQuotedExecutableEscaped(), isDoubleQuotedExecutableEscaped());
/*     */         
/* 165 */         sb.append(StringUtils.quoteAndEscape(arguments[i], getArgumentQuoteDelimiter(), escapeChars, getQuotingTriggerChars(), '\\', false));
/*     */       }
/*     */       else {
/*     */         
/* 169 */         sb.append(arguments[i]);
/*     */       } 
/*     */     } 
/*     */     
/* 173 */     commandLine.add(sb.toString());
/*     */     
/* 175 */     return commandLine;
/*     */   }
/*     */ 
/*     */   
/*     */   protected char[] getQuotingTriggerChars() {
/* 180 */     return DEFAULT_QUOTING_TRIGGER_CHARS;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getExecutionPreamble() {
/* 185 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected char[] getEscapeChars(boolean includeSingleQuote, boolean includeDoubleQuote) {
/* 190 */     StringBuffer buf = new StringBuffer(2);
/* 191 */     if (includeSingleQuote)
/*     */     {
/* 193 */       buf.append('\'');
/*     */     }
/*     */     
/* 196 */     if (includeDoubleQuote)
/*     */     {
/* 198 */       buf.append('"');
/*     */     }
/*     */     
/* 201 */     char[] result = new char[buf.length()];
/* 202 */     buf.getChars(0, buf.length(), result, 0);
/*     */     
/* 204 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isDoubleQuotedArgumentEscaped() {
/* 209 */     return this.doubleQuotedArgumentEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isSingleQuotedArgumentEscaped() {
/* 214 */     return this.singleQuotedArgumentEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isDoubleQuotedExecutableEscaped() {
/* 219 */     return this.doubleQuotedExecutableEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isSingleQuotedExecutableEscaped() {
/* 224 */     return this.singleQuotedExecutableEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setArgumentQuoteDelimiter(char argQuoteDelimiter) {
/* 229 */     this.argQuoteDelimiter = argQuoteDelimiter;
/*     */   }
/*     */ 
/*     */   
/*     */   protected char getArgumentQuoteDelimiter() {
/* 234 */     return this.argQuoteDelimiter;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setExecutableQuoteDelimiter(char exeQuoteDelimiter) {
/* 239 */     this.exeQuoteDelimiter = exeQuoteDelimiter;
/*     */   }
/*     */ 
/*     */   
/*     */   protected char getExecutableQuoteDelimiter() {
/* 244 */     return this.exeQuoteDelimiter;
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
/*     */   public List getShellCommandLine(String[] arguments) {
/* 258 */     List commandLine = new ArrayList();
/*     */     
/* 260 */     if (getShellCommand() != null)
/*     */     {
/* 262 */       commandLine.add(getShellCommand());
/*     */     }
/*     */     
/* 265 */     if (getShellArgs() != null)
/*     */     {
/* 267 */       commandLine.addAll(getShellArgsList());
/*     */     }
/*     */     
/* 270 */     commandLine.addAll(getCommandLine(getExecutable(), arguments));
/*     */     
/* 272 */     return commandLine;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List getShellArgsList() {
/* 278 */     return this.shellArgs;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addShellArg(String arg) {
/* 283 */     this.shellArgs.add(arg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setQuotedArgumentsEnabled(boolean quotedArgumentsEnabled) {
/* 288 */     this.quotedArgumentsEnabled = quotedArgumentsEnabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuotedArgumentsEnabled() {
/* 293 */     return this.quotedArgumentsEnabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setQuotedExecutableEnabled(boolean quotedExecutableEnabled) {
/* 298 */     this.quotedExecutableEnabled = quotedExecutableEnabled;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuotedExecutableEnabled() {
/* 303 */     return this.quotedExecutableEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExecutable(String executable) {
/* 311 */     if (executable == null || executable.length() == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 315 */     this.executable = executable.replace('/', File.separatorChar).replace('\\', File.separatorChar);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getExecutable() {
/* 320 */     return this.executable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWorkingDirectory(String path) {
/* 328 */     if (path != null)
/*     */     {
/* 330 */       this.workingDir = path;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWorkingDirectory(File workingDir) {
/* 339 */     if (workingDir != null)
/*     */     {
/* 341 */       this.workingDir = workingDir.getAbsolutePath();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public File getWorkingDirectory() {
/* 347 */     return (this.workingDir == null) ? null : new File(this.workingDir);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getWorkingDirectoryAsString() {
/* 352 */     return this.workingDir;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearArguments() {
/* 357 */     this.shellArgs.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object clone() {
/* 362 */     Shell shell = new Shell();
/* 363 */     shell.setExecutable(getExecutable());
/* 364 */     shell.setWorkingDirectory(getWorkingDirectory());
/* 365 */     shell.setShellArgs(getShellArgs());
/* 366 */     return shell;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getOriginalExecutable() {
/* 371 */     return this.executable;
/*     */   }
/*     */ 
/*     */   
/*     */   public List getOriginalCommandLine(String executable, String[] arguments) {
/* 376 */     return getRawCommandLine(executable, arguments);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setDoubleQuotedArgumentEscaped(boolean doubleQuotedArgumentEscaped) {
/* 381 */     this.doubleQuotedArgumentEscaped = doubleQuotedArgumentEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setDoubleQuotedExecutableEscaped(boolean doubleQuotedExecutableEscaped) {
/* 386 */     this.doubleQuotedExecutableEscaped = doubleQuotedExecutableEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setSingleQuotedArgumentEscaped(boolean singleQuotedArgumentEscaped) {
/* 391 */     this.singleQuotedArgumentEscaped = singleQuotedArgumentEscaped;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setSingleQuotedExecutableEscaped(boolean singleQuotedExecutableEscaped) {
/* 396 */     this.singleQuotedExecutableEscaped = singleQuotedExecutableEscaped;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\shell\Shell.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */