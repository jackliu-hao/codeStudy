/*     */ package org.codehaus.plexus.util.cli.shell;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.codehaus.plexus.util.Os;
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
/*     */ public class BourneShell
/*     */   extends Shell
/*     */ {
/*  32 */   private static final char[] BASH_QUOTING_TRIGGER_CHARS = new char[] { ' ', '$', ';', '&', '|', '<', '>', '*', '?', '(', ')', '[', ']', '{', '}', '`' };
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
/*     */   public BourneShell() {
/*  52 */     this(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public BourneShell(boolean isLoginShell) {
/*  57 */     setShellCommand("/bin/sh");
/*  58 */     setArgumentQuoteDelimiter('\'');
/*  59 */     setExecutableQuoteDelimiter('"');
/*  60 */     setSingleQuotedArgumentEscaped(true);
/*  61 */     setSingleQuotedExecutableEscaped(false);
/*  62 */     setQuotedExecutableEnabled(true);
/*     */     
/*  64 */     if (isLoginShell)
/*     */     {
/*  66 */       addShellArg("-l");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExecutable() {
/*  73 */     if (Os.isFamily("windows"))
/*     */     {
/*  75 */       return super.getExecutable();
/*     */     }
/*     */     
/*  78 */     return unifyQuotes(super.getExecutable());
/*     */   }
/*     */ 
/*     */   
/*     */   public List getShellArgsList() {
/*  83 */     List shellArgs = new ArrayList();
/*  84 */     List existingShellArgs = super.getShellArgsList();
/*     */     
/*  86 */     if (existingShellArgs != null && !existingShellArgs.isEmpty())
/*     */     {
/*  88 */       shellArgs.addAll(existingShellArgs);
/*     */     }
/*     */     
/*  91 */     shellArgs.add("-c");
/*     */     
/*  93 */     return shellArgs;
/*     */   }
/*     */ 
/*     */   
/*     */   public String[] getShellArgs() {
/*  98 */     String[] shellArgs = super.getShellArgs();
/*  99 */     if (shellArgs == null)
/*     */     {
/* 101 */       shellArgs = new String[0];
/*     */     }
/*     */     
/* 104 */     if (shellArgs.length > 0 && !shellArgs[shellArgs.length - 1].equals("-c")) {
/*     */       
/* 106 */       String[] newArgs = new String[shellArgs.length + 1];
/*     */       
/* 108 */       System.arraycopy(shellArgs, 0, newArgs, 0, shellArgs.length);
/* 109 */       newArgs[shellArgs.length] = "-c";
/*     */       
/* 111 */       shellArgs = newArgs;
/*     */     } 
/*     */     
/* 114 */     return shellArgs;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getExecutionPreamble() {
/* 119 */     if (getWorkingDirectoryAsString() == null)
/*     */     {
/* 121 */       return null;
/*     */     }
/*     */     
/* 124 */     String dir = getWorkingDirectoryAsString();
/* 125 */     StringBuffer sb = new StringBuffer();
/* 126 */     sb.append("cd ");
/*     */     
/* 128 */     sb.append(unifyQuotes(dir));
/* 129 */     sb.append(" && ");
/*     */     
/* 131 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   protected char[] getQuotingTriggerChars() {
/* 136 */     return BASH_QUOTING_TRIGGER_CHARS;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static String unifyQuotes(String path) {
/* 158 */     if (path == null)
/*     */     {
/* 160 */       return null;
/*     */     }
/*     */     
/* 163 */     if (path.indexOf(" ") == -1 && path.indexOf("'") != -1 && path.indexOf("\"") == -1)
/*     */     {
/* 165 */       return StringUtils.escape(path);
/*     */     }
/*     */     
/* 168 */     return StringUtils.quoteAndEscape(path, '"', BASH_QUOTING_TRIGGER_CHARS);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\shell\BourneShell.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */