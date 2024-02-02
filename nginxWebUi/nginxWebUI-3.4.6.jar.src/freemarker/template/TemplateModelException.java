/*     */ package freemarker.template;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.core._ErrorDescriptionBuilder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TemplateModelException
/*     */   extends TemplateException
/*     */ {
/*     */   private final boolean replaceWithCause;
/*     */   
/*     */   public TemplateModelException() {
/*  38 */     this((String)null, (Exception)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelException(String description) {
/*  48 */     this(description, (Exception)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelException(Exception cause) {
/*  56 */     this((String)null, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelException(Throwable cause) {
/*  67 */     this((String)null, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelException(String description, Exception cause) {
/*  75 */     this(description, cause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModelException(String description, Throwable cause) {
/*  83 */     this(description, false, cause);
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
/*     */   public TemplateModelException(String description, boolean replaceWithCause, Throwable cause) {
/* 100 */     super(description, cause, (Environment)null);
/* 101 */     this.replaceWithCause = replaceWithCause;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TemplateModelException(Throwable cause, Environment env, String description, boolean preventAmbiguity) {
/* 111 */     super(description, cause, env);
/* 112 */     this.replaceWithCause = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TemplateModelException(Throwable cause, Environment env, _ErrorDescriptionBuilder descriptionBuilder, boolean preventAmbiguity) {
/* 123 */     super(cause, env, null, descriptionBuilder);
/* 124 */     this.replaceWithCause = false;
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
/*     */   public boolean getReplaceWithCause() {
/* 137 */     return this.replaceWithCause;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\TemplateModelException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */