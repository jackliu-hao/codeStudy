/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class _TemplateModelException
/*     */   extends TemplateModelException
/*     */ {
/*     */   public _TemplateModelException(String description) {
/*  35 */     super(description);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _TemplateModelException(Throwable cause, String description) {
/*  42 */     this(cause, (Environment)null, description);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Environment env, String description) {
/*  46 */     this((Throwable)null, env, description);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Throwable cause, Environment env) {
/*  50 */     this(cause, env, (String)null);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Throwable cause) {
/*  54 */     this(cause, (Environment)null, (String)null);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Throwable cause, Environment env, String description) {
/*  58 */     super(cause, env, description, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _TemplateModelException(_ErrorDescriptionBuilder description) {
/*  65 */     this((Environment)null, description);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Environment env, _ErrorDescriptionBuilder description) {
/*  69 */     this((Throwable)null, env, description);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
/*  73 */     super(cause, env, description, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _TemplateModelException(Object... descriptionParts) {
/*  80 */     this((Environment)null, descriptionParts);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Environment env, Object... descriptionParts) {
/*  84 */     this((Throwable)null, env, descriptionParts);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Throwable cause, Object... descriptionParts) {
/*  88 */     this(cause, (Environment)null, descriptionParts);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Throwable cause, Environment env, Object... descriptionParts) {
/*  92 */     super(cause, env, new _ErrorDescriptionBuilder(descriptionParts), true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _TemplateModelException(Expression blamed, Object... descriptionParts) {
/*  99 */     this(blamed, (Environment)null, descriptionParts);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Expression blamed, Environment env, Object... descriptionParts) {
/* 103 */     this(blamed, (Throwable)null, env, descriptionParts);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Expression blamed, Throwable cause, Environment env, Object... descriptionParts) {
/* 107 */     super(cause, env, (new _ErrorDescriptionBuilder(descriptionParts)).blame(blamed), true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public _TemplateModelException(Expression blamed, String description) {
/* 114 */     this(blamed, (Environment)null, description);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Expression blamed, Environment env, String description) {
/* 118 */     this(blamed, (Throwable)null, env, description);
/*     */   }
/*     */   
/*     */   public _TemplateModelException(Expression blamed, Throwable cause, Environment env, String description) {
/* 122 */     super(cause, env, (new _ErrorDescriptionBuilder(description)).blame(blamed), true);
/*     */   }
/*     */   
/*     */   static Object[] modelHasStoredNullDescription(Class expected, TemplateModel model) {
/* 126 */     (new Object[5])[0] = "The FreeMarker value exists, but has nothing inside it; the TemplateModel object (class: "; (new Object[5])[1] = model
/*     */       
/* 128 */       .getClass().getName(); (new Object[5])[2] = ") has returned a null"; (new Object[2])[0] = " instead of a "; (new Object[2])[1] = 
/* 129 */       ClassUtil.getShortClassName(expected); return new Object[] { null, null, null, (expected != null) ? new Object[2] : "", ". This is possibly a bug in the non-FreeMarker code that builds the data-model." };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_TemplateModelException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */