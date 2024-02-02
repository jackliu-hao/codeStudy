/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core.Expression;
/*     */ import freemarker.core.TemplateElement;
/*     */ import freemarker.core.TemplateObject;
/*     */ import freemarker.core._CoreAPI;
/*     */ import freemarker.core._ErrorDescriptionBuilder;
/*     */ import freemarker.template.utility.CollectionUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TemplateException
/*     */   extends Exception
/*     */ {
/*     */   private static final String FTL_INSTRUCTION_STACK_TRACE_TITLE = "FTL stack trace (\"~\" means nesting-related):";
/*     */   private transient _ErrorDescriptionBuilder descriptionBuilder;
/*     */   private final transient Environment env;
/*     */   private final transient Expression blamedExpression;
/*     */   private transient TemplateElement[] ftlInstructionStackSnapshot;
/*     */   private String renderedFtlInstructionStackSnapshot;
/*     */   private String renderedFtlInstructionStackSnapshotTop;
/*     */   private String description;
/*     */   private transient String messageWithoutStackTop;
/*     */   private transient String message;
/*     */   private boolean blamedExpressionStringCalculated;
/*     */   private String blamedExpressionString;
/*     */   private boolean positionsCalculated;
/*     */   private String templateName;
/*     */   private String templateSourceName;
/*     */   private Integer lineNumber;
/*     */   private Integer columnNumber;
/*     */   private Integer endLineNumber;
/*     */   private Integer endColumnNumber;
/*  72 */   private transient Object lock = new Object();
/*     */ 
/*     */   
/*     */   private transient ThreadLocal messageWasAlreadyPrintedForThisTrace;
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateException(Environment env) {
/*  80 */     this((String)null, (Exception)null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateException(String description, Environment env) {
/*  90 */     this(description, (Exception)null, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateException(Exception cause, Environment env) {
/*  98 */     this((String)null, cause, env);
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
/*     */   public TemplateException(Throwable cause, Environment env) {
/* 111 */     this((String)null, cause, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateException(String description, Exception cause, Environment env) {
/* 119 */     this(description, cause, env, null, null);
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
/*     */   public TemplateException(String description, Throwable cause, Environment env) {
/* 133 */     this(description, cause, env, null, null);
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
/*     */   protected TemplateException(Throwable cause, Environment env, Expression blamedExpr, _ErrorDescriptionBuilder descriptionBuilder) {
/* 145 */     this(null, cause, env, blamedExpr, descriptionBuilder);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateException(String renderedDescription, Throwable cause, Environment env, Expression blamedExpression, _ErrorDescriptionBuilder descriptionBuilder) {
/* 155 */     super(cause);
/*     */     
/* 157 */     if (env == null) env = Environment.getCurrentEnvironment(); 
/* 158 */     this.env = env;
/*     */     
/* 160 */     this.blamedExpression = blamedExpression;
/*     */     
/* 162 */     this.descriptionBuilder = descriptionBuilder;
/* 163 */     this.description = renderedDescription;
/*     */     
/* 165 */     if (env != null) this.ftlInstructionStackSnapshot = _CoreAPI.getInstructionStackSnapshot(env); 
/*     */   }
/*     */   
/*     */   private void renderMessages() {
/* 169 */     String description = getDescription();
/*     */     
/* 171 */     if (description != null && description.length() != 0) {
/* 172 */       this.messageWithoutStackTop = description;
/* 173 */     } else if (getCause() != null) {
/* 174 */       this
/* 175 */         .messageWithoutStackTop = "No error description was specified for this error; low-level message: " + getCause().getClass().getName() + ": " + getCause().getMessage();
/*     */     } else {
/* 177 */       this.messageWithoutStackTop = "[No error description was available.]";
/*     */     } 
/*     */     
/* 180 */     String stackTopFew = getFTLInstructionStackTopFew();
/* 181 */     if (stackTopFew != null) {
/* 182 */       this.message = this.messageWithoutStackTop + "\n\n" + "----" + "\n" + "FTL stack trace (\"~\" means nesting-related):" + "\n" + stackTopFew + "----";
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 187 */       this.messageWithoutStackTop = this.message.substring(0, this.messageWithoutStackTop.length());
/*     */     } else {
/* 189 */       this.message = this.messageWithoutStackTop;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void calculatePosition() {
/* 194 */     synchronized (this.lock) {
/* 195 */       if (!this.positionsCalculated) {
/*     */         
/* 197 */         TemplateObject templateObject = (this.blamedExpression != null) ? (TemplateObject)this.blamedExpression : ((this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length != 0) ? (TemplateObject)this.ftlInstructionStackSnapshot[0] : null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 203 */         if (templateObject != null && templateObject.getBeginLine() > 0) {
/* 204 */           Template template = templateObject.getTemplate();
/* 205 */           this.templateName = (template != null) ? template.getName() : null;
/* 206 */           this.templateSourceName = (template != null) ? template.getSourceName() : null;
/* 207 */           this.lineNumber = Integer.valueOf(templateObject.getBeginLine());
/* 208 */           this.columnNumber = Integer.valueOf(templateObject.getBeginColumn());
/* 209 */           this.endLineNumber = Integer.valueOf(templateObject.getEndLine());
/* 210 */           this.endColumnNumber = Integer.valueOf(templateObject.getEndColumn());
/*     */         } 
/* 212 */         this.positionsCalculated = true;
/* 213 */         deleteFTLInstructionStackSnapshotIfNotNeeded();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Exception getCauseException() {
/* 224 */     return (getCause() instanceof Exception) ? (Exception)
/* 225 */       getCause() : new Exception("Wrapped to Exception: " + 
/* 226 */         getCause(), getCause());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFTLInstructionStack() {
/* 233 */     synchronized (this.lock) {
/* 234 */       if (this.ftlInstructionStackSnapshot != null || this.renderedFtlInstructionStackSnapshot != null) {
/* 235 */         if (this.renderedFtlInstructionStackSnapshot == null) {
/* 236 */           StringWriter sw = new StringWriter();
/* 237 */           PrintWriter pw = new PrintWriter(sw);
/* 238 */           _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, false, pw);
/* 239 */           pw.close();
/* 240 */           if (this.renderedFtlInstructionStackSnapshot == null) {
/* 241 */             this.renderedFtlInstructionStackSnapshot = sw.toString();
/* 242 */             deleteFTLInstructionStackSnapshotIfNotNeeded();
/*     */           } 
/*     */         } 
/* 245 */         return this.renderedFtlInstructionStackSnapshot;
/*     */       } 
/* 247 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private String getFTLInstructionStackTopFew() {
/* 253 */     synchronized (this.lock) {
/* 254 */       if (this.ftlInstructionStackSnapshot != null || this.renderedFtlInstructionStackSnapshotTop != null) {
/* 255 */         if (this.renderedFtlInstructionStackSnapshotTop == null) {
/* 256 */           String s; int stackSize = this.ftlInstructionStackSnapshot.length;
/*     */           
/* 258 */           if (stackSize == 0) {
/* 259 */             s = "";
/*     */           } else {
/* 261 */             StringWriter sw = new StringWriter();
/* 262 */             _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, true, sw);
/* 263 */             s = sw.toString();
/*     */           } 
/* 265 */           if (this.renderedFtlInstructionStackSnapshotTop == null) {
/* 266 */             this.renderedFtlInstructionStackSnapshotTop = s;
/* 267 */             deleteFTLInstructionStackSnapshotIfNotNeeded();
/*     */           } 
/*     */         } 
/* 270 */         return (this.renderedFtlInstructionStackSnapshotTop.length() != 0) ? this.renderedFtlInstructionStackSnapshotTop : null;
/*     */       } 
/*     */       
/* 273 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void deleteFTLInstructionStackSnapshotIfNotNeeded() {
/* 279 */     if (this.renderedFtlInstructionStackSnapshot != null && this.renderedFtlInstructionStackSnapshotTop != null && (this.positionsCalculated || this.blamedExpression != null))
/*     */     {
/* 281 */       this.ftlInstructionStackSnapshot = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private String getDescription() {
/* 287 */     synchronized (this.lock) {
/* 288 */       if (this.description == null && this.descriptionBuilder != null) {
/* 289 */         this.description = this.descriptionBuilder.toString(
/* 290 */             getFailingInstruction(), (this.env != null) ? this.env
/* 291 */             .getShowErrorTips() : true);
/* 292 */         this.descriptionBuilder = null;
/*     */       } 
/* 294 */       return this.description;
/*     */     } 
/*     */   }
/*     */   
/*     */   private TemplateElement getFailingInstruction() {
/* 299 */     if (this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length > 0) {
/* 300 */       return this.ftlInstructionStackSnapshot[0];
/*     */     }
/* 302 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Environment getEnvironment() {
/* 311 */     return this.env;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintStream out) {
/* 319 */     printStackTrace(out, true, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintWriter out) {
/* 327 */     printStackTrace(out, true, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStackTrace(PrintWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
/* 338 */     synchronized (out) {
/* 339 */       printStackTrace(new PrintWriterStackTraceWriter(out), heading, ftlStackTrace, javaStackTrace);
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
/*     */   public void printStackTrace(PrintStream out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
/* 351 */     synchronized (out) {
/* 352 */       printStackTrace(new PrintStreamStackTraceWriter(out), heading, ftlStackTrace, javaStackTrace);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void printStackTrace(StackTraceWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
/* 357 */     synchronized (out) {
/* 358 */       if (heading) {
/* 359 */         out.println("FreeMarker template error:");
/*     */       }
/*     */       
/* 362 */       if (ftlStackTrace) {
/* 363 */         String stackTrace = getFTLInstructionStack();
/* 364 */         if (stackTrace != null) {
/* 365 */           out.println(getMessageWithoutStackTop());
/* 366 */           out.println();
/* 367 */           out.println("----");
/* 368 */           out.println("FTL stack trace (\"~\" means nesting-related):");
/* 369 */           out.print(stackTrace);
/* 370 */           out.println("----");
/*     */         } else {
/* 372 */           ftlStackTrace = false;
/* 373 */           javaStackTrace = true;
/*     */         } 
/*     */       } 
/*     */       
/* 377 */       if (javaStackTrace) {
/* 378 */         if (ftlStackTrace) {
/* 379 */           out.println();
/* 380 */           out.println("Java stack trace (for programmers):");
/* 381 */           out.println("----");
/* 382 */           synchronized (this.lock) {
/* 383 */             if (this.messageWasAlreadyPrintedForThisTrace == null) {
/* 384 */               this.messageWasAlreadyPrintedForThisTrace = new ThreadLocal();
/*     */             }
/* 386 */             this.messageWasAlreadyPrintedForThisTrace.set(Boolean.TRUE);
/*     */           } 
/*     */           
/*     */           try {
/* 390 */             out.printStandardStackTrace(this);
/*     */           } finally {
/* 392 */             this.messageWasAlreadyPrintedForThisTrace.set(Boolean.FALSE);
/*     */           } 
/*     */         } else {
/* 395 */           out.printStandardStackTrace(this);
/*     */         } 
/*     */         
/* 398 */         if (getCause() != null) {
/*     */           
/* 400 */           Throwable causeCause = getCause().getCause();
/* 401 */           if (causeCause == null) {
/*     */             
/*     */             try {
/* 404 */               Method m = getCause().getClass().getMethod("getRootCause", CollectionUtils.EMPTY_CLASS_ARRAY);
/* 405 */               Throwable rootCause = (Throwable)m.invoke(getCause(), CollectionUtils.EMPTY_OBJECT_ARRAY);
/* 406 */               if (rootCause != null) {
/* 407 */                 out.println("ServletException root cause: ");
/* 408 */                 out.printStandardStackTrace(rootCause);
/*     */               } 
/* 410 */             } catch (Throwable throwable) {}
/*     */           }
/*     */         } 
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
/*     */   public void printStandardStackTrace(PrintStream ps) {
/* 424 */     super.printStackTrace(ps);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void printStandardStackTrace(PrintWriter pw) {
/* 432 */     super.printStackTrace(pw);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 437 */     if (this.messageWasAlreadyPrintedForThisTrace != null && this.messageWasAlreadyPrintedForThisTrace
/* 438 */       .get() == Boolean.TRUE) {
/* 439 */       return "[... Exception message was already printed; see it above ...]";
/*     */     }
/* 441 */     synchronized (this.lock) {
/* 442 */       if (this.message == null) renderMessages(); 
/* 443 */       return this.message;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessageWithoutStackTop() {
/* 454 */     synchronized (this.lock) {
/* 455 */       if (this.messageWithoutStackTop == null) renderMessages(); 
/* 456 */       return this.messageWithoutStackTop;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getLineNumber() {
/* 466 */     synchronized (this.lock) {
/* 467 */       if (!this.positionsCalculated) {
/* 468 */         calculatePosition();
/*     */       }
/* 470 */       return this.lineNumber;
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
/*     */   @Deprecated
/*     */   public String getTemplateName() {
/* 486 */     synchronized (this.lock) {
/* 487 */       if (!this.positionsCalculated) {
/* 488 */         calculatePosition();
/*     */       }
/* 490 */       return this.templateName;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTemplateSourceName() {
/* 501 */     synchronized (this.lock) {
/* 502 */       if (!this.positionsCalculated) {
/* 503 */         calculatePosition();
/*     */       }
/* 505 */       return this.templateSourceName;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getColumnNumber() {
/* 515 */     synchronized (this.lock) {
/* 516 */       if (!this.positionsCalculated) {
/* 517 */         calculatePosition();
/*     */       }
/* 519 */       return this.columnNumber;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getEndLineNumber() {
/* 530 */     synchronized (this.lock) {
/* 531 */       if (!this.positionsCalculated) {
/* 532 */         calculatePosition();
/*     */       }
/* 534 */       return this.endLineNumber;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Integer getEndColumnNumber() {
/* 545 */     synchronized (this.lock) {
/* 546 */       if (!this.positionsCalculated) {
/* 547 */         calculatePosition();
/*     */       }
/* 549 */       return this.endColumnNumber;
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
/*     */   public String getBlamedExpressionString() {
/* 562 */     synchronized (this.lock) {
/* 563 */       if (!this.blamedExpressionStringCalculated) {
/* 564 */         if (this.blamedExpression != null) {
/* 565 */           this.blamedExpressionString = this.blamedExpression.getCanonicalForm();
/*     */         }
/* 567 */         this.blamedExpressionStringCalculated = true;
/*     */       } 
/* 569 */       return this.blamedExpressionString;
/*     */     } 
/*     */   }
/*     */   
/*     */   Expression getBlamedExpression() {
/* 574 */     return this.blamedExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
/* 579 */     getFTLInstructionStack();
/* 580 */     getFTLInstructionStackTopFew();
/* 581 */     getDescription();
/* 582 */     calculatePosition();
/* 583 */     getBlamedExpressionString();
/*     */     
/* 585 */     out.defaultWriteObject();
/*     */   }
/*     */   
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 589 */     this.lock = new Object();
/* 590 */     in.defaultReadObject();
/*     */   }
/*     */   
/*     */   private static interface StackTraceWriter {
/*     */     void print(Object param1Object);
/*     */     
/*     */     void println(Object param1Object);
/*     */     
/*     */     void println();
/*     */     
/*     */     void printStandardStackTrace(Throwable param1Throwable); }
/*     */   
/*     */   private static class PrintStreamStackTraceWriter implements StackTraceWriter {
/*     */     private final PrintStream out;
/*     */     
/*     */     PrintStreamStackTraceWriter(PrintStream out) {
/* 606 */       this.out = out;
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(Object obj) {
/* 611 */       this.out.print(obj);
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(Object obj) {
/* 616 */       this.out.println(obj);
/*     */     }
/*     */ 
/*     */     
/*     */     public void println() {
/* 621 */       this.out.println();
/*     */     }
/*     */ 
/*     */     
/*     */     public void printStandardStackTrace(Throwable exception) {
/* 626 */       if (exception instanceof TemplateException) {
/* 627 */         ((TemplateException)exception).printStandardStackTrace(this.out);
/*     */       } else {
/* 629 */         exception.printStackTrace(this.out);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static class PrintWriterStackTraceWriter
/*     */     implements StackTraceWriter
/*     */   {
/*     */     private final PrintWriter out;
/*     */     
/*     */     PrintWriterStackTraceWriter(PrintWriter out) {
/* 640 */       this.out = out;
/*     */     }
/*     */ 
/*     */     
/*     */     public void print(Object obj) {
/* 645 */       this.out.print(obj);
/*     */     }
/*     */ 
/*     */     
/*     */     public void println(Object obj) {
/* 650 */       this.out.println(obj);
/*     */     }
/*     */ 
/*     */     
/*     */     public void println() {
/* 655 */       this.out.println();
/*     */     }
/*     */ 
/*     */     
/*     */     public void printStandardStackTrace(Throwable exception) {
/* 660 */       if (exception instanceof TemplateException) {
/* 661 */         ((TemplateException)exception).printStandardStackTrace(this.out);
/*     */       } else {
/* 663 */         exception.printStackTrace(this.out);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */