/*     */ package com.sun.mail.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.mail.Session;
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
/*     */ public final class MailLogger
/*     */ {
/*     */   private final Logger logger;
/*     */   private final String prefix;
/*     */   private final boolean debug;
/*     */   private final PrintStream out;
/*     */   
/*     */   public MailLogger(String name, String prefix, boolean debug, PrintStream out) {
/*  85 */     this.logger = Logger.getLogger(name);
/*  86 */     this.prefix = prefix;
/*  87 */     this.debug = debug;
/*  88 */     this.out = (out != null) ? out : System.out;
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
/*     */   public MailLogger(Class clazz, String prefix, boolean debug, PrintStream out) {
/* 103 */     String name = packageOf(clazz);
/* 104 */     this.logger = Logger.getLogger(name);
/* 105 */     this.prefix = prefix;
/* 106 */     this.debug = debug;
/* 107 */     this.out = (out != null) ? out : System.out;
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
/*     */   public MailLogger(Class clazz, String subname, String prefix, boolean debug, PrintStream out) {
/* 123 */     String name = packageOf(clazz) + "." + subname;
/* 124 */     this.logger = Logger.getLogger(name);
/* 125 */     this.prefix = prefix;
/* 126 */     this.debug = debug;
/* 127 */     this.out = (out != null) ? out : System.out;
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
/*     */   public MailLogger(String name, String prefix, Session session) {
/* 140 */     this(name, prefix, session.getDebug(), session.getDebugOut());
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
/*     */   public MailLogger(Class clazz, String prefix, Session session) {
/* 154 */     this(clazz, prefix, session.getDebug(), session.getDebugOut());
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
/*     */   public MailLogger getLogger(String name, String prefix) {
/* 166 */     return new MailLogger(name, prefix, this.debug, this.out);
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
/*     */   public MailLogger getLogger(Class clazz, String prefix) {
/* 179 */     return new MailLogger(clazz, prefix, this.debug, this.out);
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
/*     */   public MailLogger getSubLogger(String subname, String prefix) {
/* 193 */     return new MailLogger(this.logger.getName() + "." + subname, prefix, this.debug, this.out);
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
/*     */   public MailLogger getSubLogger(String subname, String prefix, boolean debug) {
/* 210 */     return new MailLogger(this.logger.getName() + "." + subname, prefix, debug, this.out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(Level level, String msg) {
/* 218 */     ifDebugOut(msg);
/* 219 */     if (this.logger.isLoggable(level)) {
/* 220 */       String[] frame = inferCaller();
/* 221 */       this.logger.logp(level, frame[0], frame[1], msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(Level level, String msg, Object param1) {
/* 229 */     if (this.debug) {
/* 230 */       msg = MessageFormat.format(msg, new Object[] { param1 });
/* 231 */       debugOut(msg);
/*     */     } 
/*     */     
/* 234 */     if (this.logger.isLoggable(level)) {
/* 235 */       String[] frame = inferCaller();
/* 236 */       this.logger.logp(level, frame[0], frame[1], msg, param1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(Level level, String msg, Object[] params) {
/* 244 */     if (this.debug) {
/* 245 */       msg = MessageFormat.format(msg, params);
/* 246 */       debugOut(msg);
/*     */     } 
/*     */     
/* 249 */     if (this.logger.isLoggable(level)) {
/* 250 */       String[] frame = inferCaller();
/* 251 */       this.logger.logp(level, frame[0], frame[1], msg, params);
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
/*     */   public void log(Level level, String msg, Throwable thrown) {
/* 269 */     if (this.debug) {
/* 270 */       if (thrown != null) {
/* 271 */         debugOut(msg + ", THROW: ");
/* 272 */         thrown.printStackTrace(this.out);
/*     */       } else {
/* 274 */         debugOut(msg);
/*     */       } 
/*     */     }
/*     */     
/* 278 */     if (this.logger.isLoggable(level)) {
/* 279 */       String[] frame = inferCaller();
/* 280 */       this.logger.logp(level, frame[0], frame[1], msg, thrown);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void config(String msg) {
/* 288 */     log(Level.CONFIG, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fine(String msg) {
/* 295 */     log(Level.FINE, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finer(String msg) {
/* 302 */     log(Level.FINER, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void finest(String msg) {
/* 309 */     log(Level.FINEST, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLoggable(Level level) {
/* 317 */     return (this.debug || this.logger.isLoggable(level));
/*     */   }
/*     */   
/*     */   private final void ifDebugOut(String msg) {
/* 321 */     if (this.debug)
/* 322 */       debugOut(msg); 
/*     */   }
/*     */   
/*     */   private final void debugOut(String msg) {
/* 326 */     if (this.prefix != null) {
/* 327 */       this.out.println(this.prefix + ": " + msg);
/*     */     } else {
/* 329 */       this.out.println(msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String packageOf(Class clazz) {
/* 338 */     Package p = clazz.getPackage();
/* 339 */     if (p != null)
/* 340 */       return p.getName(); 
/* 341 */     String cname = clazz.getName();
/* 342 */     int i = cname.lastIndexOf('.');
/* 343 */     if (i > 0) {
/* 344 */       return cname.substring(0, i);
/*     */     }
/* 346 */     return "";
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
/*     */   private String[] inferCaller() {
/* 359 */     StackTraceElement[] stack = (new Throwable()).getStackTrace();
/*     */     
/* 361 */     int ix = 0;
/* 362 */     while (ix < stack.length) {
/* 363 */       StackTraceElement frame = stack[ix];
/* 364 */       String cname = frame.getClassName();
/* 365 */       if (isLoggerImplFrame(cname)) {
/*     */         break;
/*     */       }
/* 368 */       ix++;
/*     */     } 
/*     */     
/* 371 */     while (ix < stack.length) {
/* 372 */       StackTraceElement frame = stack[ix];
/* 373 */       String cname = frame.getClassName();
/* 374 */       if (!isLoggerImplFrame(cname))
/*     */       {
/* 376 */         return new String[] { cname, frame.getMethodName() };
/*     */       }
/* 378 */       ix++;
/*     */     } 
/*     */ 
/*     */     
/* 382 */     return new String[] { null, null };
/*     */   }
/*     */   
/*     */   private boolean isLoggerImplFrame(String cname) {
/* 386 */     return MailLogger.class.getName().equals(cname);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mai\\util\MailLogger.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */