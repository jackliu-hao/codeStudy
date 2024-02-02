/*     */ package freemarker.template.utility;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateTransformModel;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class CaptureOutput
/*     */   implements TemplateTransformModel
/*     */ {
/*     */   public Writer getWriter(final Writer out, Map args) throws TemplateModelException {
/*  66 */     String errmsg = "Must specify the name of the variable in which to capture the output with the 'var' or 'local' or 'global' parameter.";
/*     */     
/*  68 */     if (args == null) throw new TemplateModelException(errmsg);
/*     */     
/*  70 */     boolean local = false, global = false;
/*  71 */     final TemplateModel nsModel = (TemplateModel)args.get("namespace");
/*  72 */     Object varNameModel = args.get("var");
/*  73 */     if (varNameModel == null) {
/*  74 */       varNameModel = args.get("local");
/*  75 */       if (varNameModel == null) {
/*  76 */         varNameModel = args.get("global");
/*  77 */         global = true;
/*     */       } else {
/*  79 */         local = true;
/*     */       } 
/*  81 */       if (varNameModel == null) {
/*  82 */         throw new TemplateModelException(errmsg);
/*     */       }
/*     */     } 
/*  85 */     if (args.size() == 2)
/*  86 */     { if (nsModel == null) {
/*  87 */         throw new TemplateModelException("Second parameter can only be namespace");
/*     */       }
/*  89 */       if (local) {
/*  90 */         throw new TemplateModelException("Cannot specify namespace for a local assignment");
/*     */       }
/*  92 */       if (global) {
/*  93 */         throw new TemplateModelException("Cannot specify namespace for a global assignment");
/*     */       }
/*  95 */       if (!(nsModel instanceof Environment.Namespace)) {
/*  96 */         throw new TemplateModelException("namespace parameter does not specify a namespace. It is a " + nsModel.getClass().getName());
/*     */       } }
/*  98 */     else if (args.size() != 1) { throw new TemplateModelException("Bad parameters. Use only one of 'var' or 'local' or 'global' parameters."); }
/*     */ 
/*     */     
/* 101 */     if (!(varNameModel instanceof TemplateScalarModel)) {
/* 102 */       throw new TemplateModelException("'var' or 'local' or 'global' parameter doesn't evaluate to a string");
/*     */     }
/* 104 */     final String varName = ((TemplateScalarModel)varNameModel).getAsString();
/* 105 */     if (varName == null) {
/* 106 */       throw new TemplateModelException("'var' or 'local' or 'global' parameter evaluates to null string");
/*     */     }
/*     */     
/* 109 */     final StringBuilder buf = new StringBuilder();
/* 110 */     final Environment env = Environment.getCurrentEnvironment();
/* 111 */     final boolean localVar = local;
/* 112 */     final boolean globalVar = global;
/*     */     
/* 114 */     return new Writer()
/*     */       {
/*     */         public void write(char[] cbuf, int off, int len)
/*     */         {
/* 118 */           buf.append(cbuf, off, len);
/*     */         }
/*     */ 
/*     */         
/*     */         public void flush() throws IOException {
/* 123 */           out.flush();
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/* 128 */           SimpleScalar result = new SimpleScalar(buf.toString());
/*     */           try {
/* 130 */             if (localVar) {
/* 131 */               env.setLocalVariable(varName, (TemplateModel)result);
/* 132 */             } else if (globalVar) {
/* 133 */               env.setGlobalVariable(varName, (TemplateModel)result);
/*     */             }
/* 135 */             else if (nsModel == null) {
/* 136 */               env.setVariable(varName, (TemplateModel)result);
/*     */             } else {
/* 138 */               ((Environment.Namespace)nsModel).put(varName, result);
/*     */             }
/*     */           
/* 141 */           } catch (IllegalStateException ise) {
/* 142 */             throw new IOException("Could not set variable " + varName + ": " + ise.getMessage());
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\CaptureOutput.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */