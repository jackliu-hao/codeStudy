package freemarker.template;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.core._CoreAPI;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.utility.CollectionUtils;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class TemplateException extends Exception {
   private static final String FTL_INSTRUCTION_STACK_TRACE_TITLE = "FTL stack trace (\"~\" means nesting-related):";
   private transient _ErrorDescriptionBuilder descriptionBuilder;
   private final transient Environment env;
   private final transient Expression blamedExpression;
   private transient TemplateElement[] ftlInstructionStackSnapshot;
   private String renderedFtlInstructionStackSnapshot;
   private String renderedFtlInstructionStackSnapshotTop;
   private String description;
   private transient String messageWithoutStackTop;
   private transient String message;
   private boolean blamedExpressionStringCalculated;
   private String blamedExpressionString;
   private boolean positionsCalculated;
   private String templateName;
   private String templateSourceName;
   private Integer lineNumber;
   private Integer columnNumber;
   private Integer endLineNumber;
   private Integer endColumnNumber;
   private transient Object lock;
   private transient ThreadLocal messageWasAlreadyPrintedForThisTrace;

   public TemplateException(Environment env) {
      this((String)null, (Exception)null, env);
   }

   public TemplateException(String description, Environment env) {
      this(description, (Exception)null, env);
   }

   public TemplateException(Exception cause, Environment env) {
      this((String)null, (Exception)cause, env);
   }

   public TemplateException(Throwable cause, Environment env) {
      this((String)null, (Throwable)cause, env);
   }

   public TemplateException(String description, Exception cause, Environment env) {
      this(description, cause, env, (Expression)null, (_ErrorDescriptionBuilder)null);
   }

   public TemplateException(String description, Throwable cause, Environment env) {
      this(description, cause, env, (Expression)null, (_ErrorDescriptionBuilder)null);
   }

   protected TemplateException(Throwable cause, Environment env, Expression blamedExpr, _ErrorDescriptionBuilder descriptionBuilder) {
      this((String)null, cause, env, blamedExpr, descriptionBuilder);
   }

   private TemplateException(String renderedDescription, Throwable cause, Environment env, Expression blamedExpression, _ErrorDescriptionBuilder descriptionBuilder) {
      super(cause);
      this.lock = new Object();
      if (env == null) {
         env = Environment.getCurrentEnvironment();
      }

      this.env = env;
      this.blamedExpression = blamedExpression;
      this.descriptionBuilder = descriptionBuilder;
      this.description = renderedDescription;
      if (env != null) {
         this.ftlInstructionStackSnapshot = _CoreAPI.getInstructionStackSnapshot(env);
      }

   }

   private void renderMessages() {
      String description = this.getDescription();
      if (description != null && description.length() != 0) {
         this.messageWithoutStackTop = description;
      } else if (this.getCause() != null) {
         this.messageWithoutStackTop = "No error description was specified for this error; low-level message: " + this.getCause().getClass().getName() + ": " + this.getCause().getMessage();
      } else {
         this.messageWithoutStackTop = "[No error description was available.]";
      }

      String stackTopFew = this.getFTLInstructionStackTopFew();
      if (stackTopFew != null) {
         this.message = this.messageWithoutStackTop + "\n\n" + "----" + "\n" + "FTL stack trace (\"~\" means nesting-related):" + "\n" + stackTopFew + "----";
         this.messageWithoutStackTop = this.message.substring(0, this.messageWithoutStackTop.length());
      } else {
         this.message = this.messageWithoutStackTop;
      }

   }

   private void calculatePosition() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            TemplateObject templateObject = this.blamedExpression != null ? this.blamedExpression : (this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length != 0 ? this.ftlInstructionStackSnapshot[0] : null);
            if (templateObject != null && ((TemplateObject)templateObject).getBeginLine() > 0) {
               Template template = ((TemplateObject)templateObject).getTemplate();
               this.templateName = template != null ? template.getName() : null;
               this.templateSourceName = template != null ? template.getSourceName() : null;
               this.lineNumber = ((TemplateObject)templateObject).getBeginLine();
               this.columnNumber = ((TemplateObject)templateObject).getBeginColumn();
               this.endLineNumber = ((TemplateObject)templateObject).getEndLine();
               this.endColumnNumber = ((TemplateObject)templateObject).getEndColumn();
            }

            this.positionsCalculated = true;
            this.deleteFTLInstructionStackSnapshotIfNotNeeded();
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public Exception getCauseException() {
      return this.getCause() instanceof Exception ? (Exception)this.getCause() : new Exception("Wrapped to Exception: " + this.getCause(), this.getCause());
   }

   public String getFTLInstructionStack() {
      synchronized(this.lock) {
         if (this.ftlInstructionStackSnapshot == null && this.renderedFtlInstructionStackSnapshot == null) {
            return null;
         } else {
            if (this.renderedFtlInstructionStackSnapshot == null) {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, false, pw);
               pw.close();
               if (this.renderedFtlInstructionStackSnapshot == null) {
                  this.renderedFtlInstructionStackSnapshot = sw.toString();
                  this.deleteFTLInstructionStackSnapshotIfNotNeeded();
               }
            }

            return this.renderedFtlInstructionStackSnapshot;
         }
      }
   }

   private String getFTLInstructionStackTopFew() {
      synchronized(this.lock) {
         if (this.ftlInstructionStackSnapshot == null && this.renderedFtlInstructionStackSnapshotTop == null) {
            return null;
         } else {
            if (this.renderedFtlInstructionStackSnapshotTop == null) {
               int stackSize = this.ftlInstructionStackSnapshot.length;
               String s;
               if (stackSize == 0) {
                  s = "";
               } else {
                  StringWriter sw = new StringWriter();
                  _CoreAPI.outputInstructionStack(this.ftlInstructionStackSnapshot, true, sw);
                  s = sw.toString();
               }

               if (this.renderedFtlInstructionStackSnapshotTop == null) {
                  this.renderedFtlInstructionStackSnapshotTop = s;
                  this.deleteFTLInstructionStackSnapshotIfNotNeeded();
               }
            }

            return this.renderedFtlInstructionStackSnapshotTop.length() != 0 ? this.renderedFtlInstructionStackSnapshotTop : null;
         }
      }
   }

   private void deleteFTLInstructionStackSnapshotIfNotNeeded() {
      if (this.renderedFtlInstructionStackSnapshot != null && this.renderedFtlInstructionStackSnapshotTop != null && (this.positionsCalculated || this.blamedExpression != null)) {
         this.ftlInstructionStackSnapshot = null;
      }

   }

   private String getDescription() {
      synchronized(this.lock) {
         if (this.description == null && this.descriptionBuilder != null) {
            this.description = this.descriptionBuilder.toString(this.getFailingInstruction(), this.env != null ? this.env.getShowErrorTips() : true);
            this.descriptionBuilder = null;
         }

         return this.description;
      }
   }

   private TemplateElement getFailingInstruction() {
      return this.ftlInstructionStackSnapshot != null && this.ftlInstructionStackSnapshot.length > 0 ? this.ftlInstructionStackSnapshot[0] : null;
   }

   public Environment getEnvironment() {
      return this.env;
   }

   public void printStackTrace(PrintStream out) {
      this.printStackTrace(out, true, true, true);
   }

   public void printStackTrace(PrintWriter out) {
      this.printStackTrace(out, true, true, true);
   }

   public void printStackTrace(PrintWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
      synchronized(out) {
         this.printStackTrace((StackTraceWriter)(new PrintWriterStackTraceWriter(out)), heading, ftlStackTrace, javaStackTrace);
      }
   }

   public void printStackTrace(PrintStream out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
      synchronized(out) {
         this.printStackTrace((StackTraceWriter)(new PrintStreamStackTraceWriter(out)), heading, ftlStackTrace, javaStackTrace);
      }
   }

   private void printStackTrace(StackTraceWriter out, boolean heading, boolean ftlStackTrace, boolean javaStackTrace) {
      synchronized(out) {
         if (heading) {
            out.println("FreeMarker template error:");
         }

         if (ftlStackTrace) {
            String stackTrace = this.getFTLInstructionStack();
            if (stackTrace != null) {
               out.println(this.getMessageWithoutStackTop());
               out.println();
               out.println("----");
               out.println("FTL stack trace (\"~\" means nesting-related):");
               out.print(stackTrace);
               out.println("----");
            } else {
               ftlStackTrace = false;
               javaStackTrace = true;
            }
         }

         if (javaStackTrace) {
            if (ftlStackTrace) {
               out.println();
               out.println("Java stack trace (for programmers):");
               out.println("----");
               synchronized(this.lock) {
                  if (this.messageWasAlreadyPrintedForThisTrace == null) {
                     this.messageWasAlreadyPrintedForThisTrace = new ThreadLocal();
                  }

                  this.messageWasAlreadyPrintedForThisTrace.set(Boolean.TRUE);
               }

               try {
                  out.printStandardStackTrace(this);
               } finally {
                  this.messageWasAlreadyPrintedForThisTrace.set(Boolean.FALSE);
               }
            } else {
               out.printStandardStackTrace(this);
            }

            if (this.getCause() != null) {
               Throwable causeCause = this.getCause().getCause();
               if (causeCause == null) {
                  try {
                     Method m = this.getCause().getClass().getMethod("getRootCause", CollectionUtils.EMPTY_CLASS_ARRAY);
                     Throwable rootCause = (Throwable)m.invoke(this.getCause(), CollectionUtils.EMPTY_OBJECT_ARRAY);
                     if (rootCause != null) {
                        out.println("ServletException root cause: ");
                        out.printStandardStackTrace(rootCause);
                     }
                  } catch (Throwable var14) {
                  }
               }
            }
         }

      }
   }

   public void printStandardStackTrace(PrintStream ps) {
      super.printStackTrace(ps);
   }

   public void printStandardStackTrace(PrintWriter pw) {
      super.printStackTrace(pw);
   }

   public String getMessage() {
      if (this.messageWasAlreadyPrintedForThisTrace != null && this.messageWasAlreadyPrintedForThisTrace.get() == Boolean.TRUE) {
         return "[... Exception message was already printed; see it above ...]";
      } else {
         synchronized(this.lock) {
            if (this.message == null) {
               this.renderMessages();
            }

            return this.message;
         }
      }
   }

   public String getMessageWithoutStackTop() {
      synchronized(this.lock) {
         if (this.messageWithoutStackTop == null) {
            this.renderMessages();
         }

         return this.messageWithoutStackTop;
      }
   }

   public Integer getLineNumber() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            this.calculatePosition();
         }

         return this.lineNumber;
      }
   }

   /** @deprecated */
   @Deprecated
   public String getTemplateName() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            this.calculatePosition();
         }

         return this.templateName;
      }
   }

   public String getTemplateSourceName() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            this.calculatePosition();
         }

         return this.templateSourceName;
      }
   }

   public Integer getColumnNumber() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            this.calculatePosition();
         }

         return this.columnNumber;
      }
   }

   public Integer getEndLineNumber() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            this.calculatePosition();
         }

         return this.endLineNumber;
      }
   }

   public Integer getEndColumnNumber() {
      synchronized(this.lock) {
         if (!this.positionsCalculated) {
            this.calculatePosition();
         }

         return this.endColumnNumber;
      }
   }

   public String getBlamedExpressionString() {
      synchronized(this.lock) {
         if (!this.blamedExpressionStringCalculated) {
            if (this.blamedExpression != null) {
               this.blamedExpressionString = this.blamedExpression.getCanonicalForm();
            }

            this.blamedExpressionStringCalculated = true;
         }

         return this.blamedExpressionString;
      }
   }

   Expression getBlamedExpression() {
      return this.blamedExpression;
   }

   private void writeObject(ObjectOutputStream out) throws IOException, ClassNotFoundException {
      this.getFTLInstructionStack();
      this.getFTLInstructionStackTopFew();
      this.getDescription();
      this.calculatePosition();
      this.getBlamedExpressionString();
      out.defaultWriteObject();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      this.lock = new Object();
      in.defaultReadObject();
   }

   private static class PrintWriterStackTraceWriter implements StackTraceWriter {
      private final PrintWriter out;

      PrintWriterStackTraceWriter(PrintWriter out) {
         this.out = out;
      }

      public void print(Object obj) {
         this.out.print(obj);
      }

      public void println(Object obj) {
         this.out.println(obj);
      }

      public void println() {
         this.out.println();
      }

      public void printStandardStackTrace(Throwable exception) {
         if (exception instanceof TemplateException) {
            ((TemplateException)exception).printStandardStackTrace(this.out);
         } else {
            exception.printStackTrace(this.out);
         }

      }
   }

   private static class PrintStreamStackTraceWriter implements StackTraceWriter {
      private final PrintStream out;

      PrintStreamStackTraceWriter(PrintStream out) {
         this.out = out;
      }

      public void print(Object obj) {
         this.out.print(obj);
      }

      public void println(Object obj) {
         this.out.println(obj);
      }

      public void println() {
         this.out.println();
      }

      public void printStandardStackTrace(Throwable exception) {
         if (exception instanceof TemplateException) {
            ((TemplateException)exception).printStandardStackTrace(this.out);
         } else {
            exception.printStackTrace(this.out);
         }

      }
   }

   private interface StackTraceWriter {
      void print(Object var1);

      void println(Object var1);

      void println();

      void printStandardStackTrace(Throwable var1);
   }
}
