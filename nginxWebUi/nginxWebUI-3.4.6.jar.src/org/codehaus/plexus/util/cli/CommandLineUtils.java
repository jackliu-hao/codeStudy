/*     */ package org.codehaus.plexus.util.cli;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ public abstract class CommandLineUtils
/*     */ {
/*  45 */   private static Map processes = Collections.synchronizedMap(new HashMap());
/*     */   
/*  47 */   private static Thread shutdownHook = new Thread("CommandlineUtil shutdown")
/*     */     {
/*     */       public void run()
/*     */       {
/*  51 */         if (CommandLineUtils.processes != null && CommandLineUtils.processes.size() > 0) {
/*     */           
/*  53 */           System.err.println("Destroying " + CommandLineUtils.processes.size() + " processes");
/*  54 */           for (Iterator it = CommandLineUtils.processes.values().iterator(); it.hasNext(); ) {
/*     */             
/*  56 */             System.err.println("Destroying process..");
/*  57 */             ((Process)it.next()).destroy();
/*     */           } 
/*     */           
/*  60 */           System.err.println("Destroyed " + CommandLineUtils.processes.size() + " processes");
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   static {
/*  67 */     shutdownHook.setContextClassLoader(null);
/*  68 */     addShutdownHook();
/*     */   }
/*     */ 
/*     */   
/*     */   public static void addShutdownHook() {
/*  73 */     Runtime.getRuntime().addShutdownHook(shutdownHook);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void removeShutdownHook(boolean execute) {
/*  78 */     Runtime.getRuntime().removeShutdownHook(shutdownHook);
/*     */     
/*  80 */     if (execute)
/*     */     {
/*  82 */       shutdownHook.run();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class StringStreamConsumer
/*     */     implements StreamConsumer
/*     */   {
/*  89 */     private StringBuffer string = new StringBuffer();
/*     */     
/*  91 */     private String ls = System.getProperty("line.separator");
/*     */ 
/*     */     
/*     */     public void consumeLine(String line) {
/*  95 */       this.string.append(line).append(this.ls);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getOutput() {
/* 100 */       return this.string.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int executeCommandLine(Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr) throws CommandLineException {
/* 107 */     return executeCommandLine(cl, null, systemOut, systemErr, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int executeCommandLine(Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr, int timeoutInSeconds) throws CommandLineException {
/* 114 */     return executeCommandLine(cl, null, systemOut, systemErr, timeoutInSeconds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int executeCommandLine(Commandline cl, InputStream systemIn, StreamConsumer systemOut, StreamConsumer systemErr) throws CommandLineException {
/* 121 */     return executeCommandLine(cl, systemIn, systemOut, systemErr, 0);
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
/*     */   public static int executeCommandLine(Commandline cl, InputStream systemIn, StreamConsumer systemOut, StreamConsumer systemErr, int timeoutInSeconds) throws CommandLineException {
/* 137 */     if (cl == null)
/*     */     {
/* 139 */       throw new IllegalArgumentException("cl cannot be null.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 144 */     Process p = cl.execute();
/*     */     
/* 146 */     processes.put(new Long(cl.getPid()), p);
/*     */     
/* 148 */     StreamFeeder inputFeeder = null;
/*     */     
/* 150 */     if (systemIn != null)
/*     */     {
/* 152 */       inputFeeder = new StreamFeeder(systemIn, p.getOutputStream());
/*     */     }
/*     */     
/* 155 */     StreamPumper outputPumper = new StreamPumper(p.getInputStream(), systemOut);
/*     */     
/* 157 */     StreamPumper errorPumper = new StreamPumper(p.getErrorStream(), systemErr);
/*     */     
/* 159 */     if (inputFeeder != null)
/*     */     {
/* 161 */       inputFeeder.start();
/*     */     }
/*     */     
/* 164 */     outputPumper.start();
/*     */     
/* 166 */     errorPumper.start();
/*     */     
/*     */     try {
/*     */       int returnValue;
/*     */       
/* 171 */       if (timeoutInSeconds <= 0) {
/*     */         
/* 173 */         returnValue = p.waitFor();
/*     */       }
/*     */       else {
/*     */         
/* 177 */         long now = System.currentTimeMillis();
/* 178 */         long timeoutInMillis = 1000L * timeoutInSeconds;
/* 179 */         long finish = now + timeoutInMillis;
/* 180 */         while (isAlive(p) && System.currentTimeMillis() < finish)
/*     */         {
/* 182 */           Thread.sleep(10L);
/*     */         }
/* 184 */         if (isAlive(p))
/*     */         {
/* 186 */           throw new InterruptedException("Process timeout out after " + timeoutInSeconds + " seconds");
/*     */         }
/* 188 */         returnValue = p.exitValue();
/*     */       } 
/*     */       
/* 191 */       if (inputFeeder != null)
/*     */       {
/* 193 */         synchronized (inputFeeder) {
/*     */           
/* 195 */           while (!inputFeeder.isDone())
/*     */           {
/* 197 */             inputFeeder.wait();
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 202 */       synchronized (outputPumper) {
/*     */         
/* 204 */         while (!outputPumper.isDone())
/*     */         {
/* 206 */           outputPumper.wait();
/*     */         }
/*     */       } 
/*     */       
/* 210 */       synchronized (errorPumper) {
/*     */         
/* 212 */         while (!errorPumper.isDone())
/*     */         {
/* 214 */           errorPumper.wait();
/*     */         }
/*     */       } 
/*     */       
/* 218 */       processes.remove(new Long(cl.getPid()));
/*     */       
/* 220 */       return returnValue;
/*     */     }
/* 222 */     catch (InterruptedException ex) {
/*     */       
/* 224 */       killProcess(cl.getPid());
/* 225 */       throw new CommandLineTimeOutException("Error while executing external command, process killed.", ex);
/*     */     }
/*     */     finally {
/*     */       
/* 229 */       if (inputFeeder != null)
/*     */       {
/* 231 */         inputFeeder.close();
/*     */       }
/*     */       
/* 234 */       outputPumper.close();
/*     */       
/* 236 */       errorPumper.close();
/*     */       
/* 238 */       p.destroy();
/*     */       
/* 240 */       if (processes.get(new Long(cl.getPid())) != null)
/*     */       {
/* 242 */         processes.remove(new Long(cl.getPid()));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Properties getSystemEnvVars() throws IOException {
/* 261 */     return getSystemEnvVars(!Os.isFamily("windows"));
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
/*     */   public static Properties getSystemEnvVars(boolean caseSensitive) throws IOException {
/* 280 */     Method getenvMethod = getEnvMethod();
/* 281 */     if (getenvMethod != null) {
/*     */       
/*     */       try {
/*     */         
/* 285 */         return getEnvFromSystem(getenvMethod, caseSensitive);
/*     */       }
/* 287 */       catch (IllegalAccessException e) {
/*     */         
/* 289 */         throw new IOException(e.getMessage());
/*     */       }
/* 291 */       catch (IllegalArgumentException e) {
/*     */         
/* 293 */         throw new IOException(e.getMessage());
/*     */       }
/* 295 */       catch (InvocationTargetException e) {
/*     */         
/* 297 */         throw new IOException(e.getMessage());
/*     */       } 
/*     */     }
/*     */     
/* 301 */     Process p = null;
/*     */ 
/*     */     
/*     */     try {
/* 305 */       Properties envVars = new Properties();
/*     */       
/* 307 */       Runtime r = Runtime.getRuntime();
/*     */ 
/*     */       
/* 310 */       boolean overriddenEncoding = false;
/* 311 */       if (Os.isFamily("windows")) {
/*     */         
/* 313 */         if (Os.isFamily("win9x"))
/*     */         {
/* 315 */           p = r.exec("command.com /c set");
/*     */         }
/*     */         else
/*     */         {
/* 319 */           overriddenEncoding = true;
/*     */ 
/*     */           
/* 322 */           p = r.exec("cmd.exe /U /c set");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 327 */         p = r.exec("env");
/*     */       } 
/*     */       
/* 330 */       Reader reader = overriddenEncoding ? new InputStreamReader(p.getInputStream(), "UTF-16LE") : new InputStreamReader(p.getInputStream());
/*     */ 
/*     */       
/* 333 */       BufferedReader br = new BufferedReader(reader);
/*     */ 
/*     */ 
/*     */       
/* 337 */       String lastKey = null;
/* 338 */       String lastVal = null;
/*     */       String line;
/* 340 */       while ((line = br.readLine()) != null) {
/*     */         
/* 342 */         int idx = line.indexOf('=');
/*     */         
/* 344 */         if (idx > 0) {
/*     */           
/* 346 */           lastKey = line.substring(0, idx);
/*     */           
/* 348 */           if (!caseSensitive)
/*     */           {
/* 350 */             lastKey = lastKey.toUpperCase(Locale.ENGLISH);
/*     */           }
/*     */           
/* 353 */           lastVal = line.substring(idx + 1);
/*     */           
/* 355 */           envVars.setProperty(lastKey, lastVal); continue;
/*     */         } 
/* 357 */         if (lastKey != null) {
/*     */           
/* 359 */           lastVal = lastVal + "\n" + line;
/*     */           
/* 361 */           envVars.setProperty(lastKey, lastVal);
/*     */         } 
/*     */       } 
/*     */       
/* 365 */       return envVars;
/*     */     }
/*     */     finally {
/*     */       
/* 369 */       if (p != null)
/*     */       {
/* 371 */         p.destroy();
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
/*     */   
/*     */   public static void killProcess(long pid) {
/* 384 */     Process p = (Process)processes.get(new Long(pid));
/*     */     
/* 386 */     if (p != null) {
/*     */       
/* 388 */       p.destroy();
/* 389 */       System.out.println("Process " + pid + " is killed.");
/* 390 */       processes.remove(new Long(pid));
/*     */     }
/*     */     else {
/*     */       
/* 394 */       System.out.println("don't exist.");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAlive(long pid) {
/* 400 */     return (processes.get(new Long(pid)) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public static boolean isAlive(Process p) {
/* 405 */     if (p == null)
/*     */     {
/* 407 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 412 */       p.exitValue();
/* 413 */       return false;
/*     */     }
/* 415 */     catch (IllegalThreadStateException e) {
/*     */       
/* 417 */       return true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String[] translateCommandline(String toProcess) throws Exception {
/* 424 */     if (toProcess == null || toProcess.length() == 0)
/*     */     {
/* 426 */       return new String[0];
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 431 */     int normal = 0;
/* 432 */     int inQuote = 1;
/* 433 */     int inDoubleQuote = 2;
/* 434 */     int state = 0;
/* 435 */     StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
/* 436 */     Vector v = new Vector();
/* 437 */     StringBuffer current = new StringBuffer();
/*     */     
/* 439 */     while (tok.hasMoreTokens()) {
/*     */       
/* 441 */       String nextTok = tok.nextToken();
/* 442 */       switch (state) {
/*     */         
/*     */         case 1:
/* 445 */           if ("'".equals(nextTok)) {
/*     */             
/* 447 */             state = 0;
/*     */             
/*     */             continue;
/*     */           } 
/* 451 */           current.append(nextTok);
/*     */           continue;
/*     */         
/*     */         case 2:
/* 455 */           if ("\"".equals(nextTok)) {
/*     */             
/* 457 */             state = 0;
/*     */             
/*     */             continue;
/*     */           } 
/* 461 */           current.append(nextTok);
/*     */           continue;
/*     */       } 
/*     */       
/* 465 */       if ("'".equals(nextTok)) {
/*     */         
/* 467 */         state = 1; continue;
/*     */       } 
/* 469 */       if ("\"".equals(nextTok)) {
/*     */         
/* 471 */         state = 2; continue;
/*     */       } 
/* 473 */       if (" ".equals(nextTok)) {
/*     */         
/* 475 */         if (current.length() != 0) {
/*     */           
/* 477 */           v.addElement(current.toString());
/* 478 */           current.setLength(0);
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 483 */       current.append(nextTok);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 489 */     if (current.length() != 0)
/*     */     {
/* 491 */       v.addElement(current.toString());
/*     */     }
/*     */     
/* 494 */     if (state == 1 || state == 2)
/*     */     {
/* 496 */       throw new CommandLineException("unbalanced quotes in " + toProcess);
/*     */     }
/*     */     
/* 499 */     String[] args = new String[v.size()];
/* 500 */     v.copyInto((Object[])args);
/* 501 */     return args;
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
/*     */   public static String quote(String argument) throws CommandLineException {
/* 519 */     return quote(argument, false, false, true);
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
/*     */   public static String quote(String argument, boolean wrapExistingQuotes) throws CommandLineException {
/* 537 */     return quote(argument, false, false, wrapExistingQuotes);
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
/*     */   public static String quote(String argument, boolean escapeSingleQuotes, boolean escapeDoubleQuotes, boolean wrapExistingQuotes) throws CommandLineException {
/* 549 */     if (argument.indexOf("\"") > -1) {
/*     */       
/* 551 */       if (argument.indexOf("'") > -1)
/*     */       {
/* 553 */         throw new CommandLineException("Can't handle single and double quotes in same argument");
/*     */       }
/*     */ 
/*     */       
/* 557 */       if (escapeSingleQuotes)
/*     */       {
/* 559 */         return "\\'" + argument + "\\'";
/*     */       }
/* 561 */       if (wrapExistingQuotes)
/*     */       {
/* 563 */         return '\'' + argument + '\'';
/*     */       
/*     */       }
/*     */     }
/* 567 */     else if (argument.indexOf("'") > -1) {
/*     */       
/* 569 */       if (escapeDoubleQuotes)
/*     */       {
/* 571 */         return "\\\"" + argument + "\\\"";
/*     */       }
/* 573 */       if (wrapExistingQuotes)
/*     */       {
/* 575 */         return '"' + argument + '"';
/*     */       }
/*     */     }
/* 578 */     else if (argument.indexOf(" ") > -1) {
/*     */       
/* 580 */       if (escapeDoubleQuotes)
/*     */       {
/* 582 */         return "\\\"" + argument + "\\\"";
/*     */       }
/*     */ 
/*     */       
/* 586 */       return '"' + argument + '"';
/*     */     } 
/*     */ 
/*     */     
/* 590 */     return argument;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(String[] line) {
/* 596 */     if (line == null || line.length == 0)
/*     */     {
/* 598 */       return "";
/*     */     }
/*     */ 
/*     */     
/* 602 */     StringBuffer result = new StringBuffer();
/* 603 */     for (int i = 0; i < line.length; i++) {
/*     */       
/* 605 */       if (i > 0)
/*     */       {
/* 607 */         result.append(' ');
/*     */       }
/*     */       
/*     */       try {
/* 611 */         result.append(StringUtils.quoteAndEscape(line[i], '"'));
/*     */       }
/* 613 */       catch (Exception e) {
/*     */         
/* 615 */         System.err.println("Error quoting argument: " + e.getMessage());
/*     */       } 
/*     */     } 
/* 618 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Method getEnvMethod() {
/*     */     try {
/* 625 */       return System.class.getMethod("getenv", null);
/*     */     }
/* 627 */     catch (NoSuchMethodException e) {
/*     */       
/* 629 */       return null;
/*     */     }
/* 631 */     catch (SecurityException e) {
/*     */       
/* 633 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Properties getEnvFromSystem(Method method, boolean caseSensitive) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
/* 640 */     Properties envVars = new Properties();
/* 641 */     Map envs = (Map)method.invoke(null, null);
/* 642 */     Iterator iterator = envs.keySet().iterator();
/* 643 */     while (iterator.hasNext()) {
/*     */       
/* 645 */       String key = iterator.next();
/* 646 */       String value = (String)envs.get(key);
/* 647 */       if (!caseSensitive)
/*     */       {
/* 649 */         key = key.toUpperCase(Locale.ENGLISH);
/*     */       }
/* 651 */       envVars.put(key, value);
/*     */     } 
/* 653 */     return envVars;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\cli\CommandLineUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */