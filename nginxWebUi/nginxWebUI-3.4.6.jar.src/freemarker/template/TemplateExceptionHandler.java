/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface TemplateExceptionHandler
/*     */ {
/*  61 */   public static final TemplateExceptionHandler IGNORE_HANDLER = new TemplateExceptionHandler()
/*     */     {
/*     */       public void handleTemplateException(TemplateException te, Environment env, Writer out) {}
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   public static final TemplateExceptionHandler RETHROW_HANDLER = new TemplateExceptionHandler()
/*     */     {
/*     */       public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException
/*     */       {
/*  76 */         throw te;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   public static final TemplateExceptionHandler DEBUG_HANDLER = new TemplateExceptionHandler()
/*     */     {
/*     */       public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException
/*     */       {
/*  88 */         if (!env.isInAttemptBlock()) {
/*  89 */           PrintWriter pw = (out instanceof PrintWriter) ? (PrintWriter)out : new PrintWriter(out);
/*  90 */           pw.print("FreeMarker template error (DEBUG mode; use RETHROW in production!):\n");
/*  91 */           te.printStackTrace(pw, false, true, true);
/*     */           
/*  93 */           pw.flush();
/*     */         } 
/*  95 */         throw te;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static final TemplateExceptionHandler HTML_DEBUG_HANDLER = new TemplateExceptionHandler() {
/*     */       private static final String FONT_RESET_CSS = "color:#A80000; font-size:12px; font-style:normal; font-variant:normal; font-weight:normal; text-decoration:none; text-transform: none";
/*     */       
/*     */       public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
/* 108 */         if (!env.isInAttemptBlock()) {
/* 109 */           boolean externalPw = out instanceof PrintWriter;
/* 110 */           PrintWriter pw = externalPw ? (PrintWriter)out : new PrintWriter(out);
/*     */           try {
/* 112 */             pw.print("<!-- FREEMARKER ERROR MESSAGE STARTS HERE --><!-- ]]> --><script language=javascript>//\"></script><script language=javascript>//'></script><script language=javascript>//\"></script><script language=javascript>//'></script></title></xmp></script></noscript></style></object></head></pre></table></form></table></table></table></a></u></i></b><div align='left' style='background-color:#FFFF7C; display:block; border-top:double; padding:4px; margin:0; font-family:Arial,sans-serif; ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 125 */             pw.print("color:#A80000; font-size:12px; font-style:normal; font-variant:normal; font-weight:normal; text-decoration:none; text-transform: none");
/* 126 */             pw.print("'><b style='font-size:12px; font-style:normal; font-weight:bold; text-decoration:none; text-transform: none;'>FreeMarker template error  (HTML_DEBUG mode; use RETHROW in production!)</b><pre style='display:block; background: none; border: 0; margin:0; padding: 0;font-family:monospace; ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 132 */             pw.print("color:#A80000; font-size:12px; font-style:normal; font-variant:normal; font-weight:normal; text-decoration:none; text-transform: none");
/* 133 */             pw.println("; white-space: pre-wrap; white-space: -moz-pre-wrap; white-space: -pre-wrap; white-space: -o-pre-wrap; word-wrap: break-word;'>");
/*     */ 
/*     */             
/* 136 */             StringWriter stackTraceSW = new StringWriter();
/* 137 */             PrintWriter stackPW = new PrintWriter(stackTraceSW);
/* 138 */             te.printStackTrace(stackPW, false, true, true);
/* 139 */             stackPW.close();
/* 140 */             pw.println();
/* 141 */             pw.println(StringUtil.XMLEncNQG(stackTraceSW.toString()));
/*     */             
/* 143 */             pw.println("</pre></div></html>");
/* 144 */             pw.flush();
/*     */           } finally {
/* 146 */             if (!externalPw) pw.close();
/*     */           
/*     */           } 
/*     */         } 
/* 150 */         throw te;
/*     */       }
/*     */     };
/*     */   
/*     */   void handleTemplateException(TemplateException paramTemplateException, Environment paramEnvironment, Writer paramWriter) throws TemplateException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateExceptionHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */