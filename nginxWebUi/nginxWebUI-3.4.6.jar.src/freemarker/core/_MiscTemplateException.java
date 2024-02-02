/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _MiscTemplateException
/*     */   extends TemplateException
/*     */ {
/*     */   public _MiscTemplateException(String description) {
/*  36 */     super(description, null);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Environment env, String description) {
/*  40 */     super(description, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _MiscTemplateException(Throwable cause, String description) {
/*  47 */     this(cause, (Environment)null, description);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Throwable cause, Environment env) {
/*  51 */     this(cause, env, (String)null);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Throwable cause) {
/*  55 */     this(cause, (Environment)null, (String)null);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Throwable cause, Environment env, String description) {
/*  59 */     super(description, cause, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _MiscTemplateException(_ErrorDescriptionBuilder description) {
/*  66 */     this((Environment)null, description);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Environment env, _ErrorDescriptionBuilder description) {
/*  70 */     this((Throwable)null, env, description);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
/*  74 */     super(cause, env, null, description);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _MiscTemplateException(Object... descriptionParts) {
/*  81 */     this((Environment)null, descriptionParts);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Environment env, Object... descriptionParts) {
/*  85 */     this((Throwable)null, env, descriptionParts);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Throwable cause, Object... descriptionParts) {
/*  89 */     this(cause, (Environment)null, descriptionParts);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Throwable cause, Environment env, Object... descriptionParts) {
/*  93 */     super(cause, env, null, new _ErrorDescriptionBuilder(descriptionParts));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _MiscTemplateException(Expression blamed, Object... descriptionParts) {
/* 100 */     this(blamed, (Environment)null, descriptionParts);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Expression blamed, Environment env, Object... descriptionParts) {
/* 104 */     this(blamed, (Throwable)null, env, descriptionParts);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, Object... descriptionParts) {
/* 108 */     super(cause, env, blamed, (new _ErrorDescriptionBuilder(descriptionParts)).blame(blamed));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _MiscTemplateException(Expression blamed, String description) {
/* 115 */     this(blamed, (Environment)null, description);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Expression blamed, Environment env, String description) {
/* 119 */     this(blamed, (Throwable)null, env, description);
/*     */   }
/*     */   
/*     */   public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, String description) {
/* 123 */     super(cause, env, blamed, (new _ErrorDescriptionBuilder(description)).blame(blamed));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_MiscTemplateException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */