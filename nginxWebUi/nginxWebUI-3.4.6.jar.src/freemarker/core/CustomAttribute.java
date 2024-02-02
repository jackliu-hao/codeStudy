/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.Template;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CustomAttribute
/*     */ {
/*     */   public static final int SCOPE_ENVIRONMENT = 0;
/*     */   public static final int SCOPE_TEMPLATE = 1;
/*     */   public static final int SCOPE_CONFIGURATION = 2;
/*  64 */   private final Object key = new Object();
/*     */ 
/*     */   
/*     */   private final int scope;
/*     */ 
/*     */ 
/*     */   
/*     */   public CustomAttribute(int scope) {
/*  72 */     if (scope != 0 && scope != 1 && scope != 2)
/*     */     {
/*     */       
/*  75 */       throw new IllegalArgumentException();
/*     */     }
/*  77 */     this.scope = scope;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object create() {
/*  87 */     return null;
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
/*     */   public final Object get(Environment env) {
/* 105 */     return getScopeConfigurable(env).getCustomAttribute(this.key, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Object get() {
/* 116 */     return getScopeConfigurable(getRequiredCurrentEnvironment()).getCustomAttribute(this.key, this);
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
/*     */   public final Object get(Template template) {
/* 128 */     if (this.scope != 1) {
/* 129 */       throw new UnsupportedOperationException("This is not a template-scope attribute");
/*     */     }
/* 131 */     return template.getCustomAttribute(this.key, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object get(TemplateConfiguration templateConfiguration) {
/* 140 */     if (this.scope != 1) {
/* 141 */       throw new UnsupportedOperationException("This is not a template-scope attribute");
/*     */     }
/* 143 */     return templateConfiguration.getCustomAttribute(this.key, this);
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
/*     */   public final Object get(Configuration cfg) {
/* 157 */     if (this.scope != 2) {
/* 158 */       throw new UnsupportedOperationException("This is not a template-scope attribute");
/*     */     }
/* 160 */     return cfg.getCustomAttribute(this.key, this);
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
/*     */   public final void set(Object value, Environment env) {
/* 179 */     getScopeConfigurable(env).setCustomAttribute(this.key, value);
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
/*     */   public final void set(Object value) {
/* 191 */     getScopeConfigurable(getRequiredCurrentEnvironment()).setCustomAttribute(this.key, value);
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
/*     */   public final void set(Object value, Template template) {
/* 206 */     if (this.scope != 1) {
/* 207 */       throw new UnsupportedOperationException("This is not a template-scope attribute");
/*     */     }
/* 209 */     template.setCustomAttribute(this.key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void set(Object value, TemplateConfiguration templateConfiguration) {
/* 218 */     if (this.scope != 1) {
/* 219 */       throw new UnsupportedOperationException("This is not a template-scope attribute");
/*     */     }
/* 221 */     templateConfiguration.setCustomAttribute(this.key, value);
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
/*     */   public final void set(Object value, Configuration cfg) {
/* 238 */     if (this.scope != 2) {
/* 239 */       throw new UnsupportedOperationException("This is not a configuration-scope attribute");
/*     */     }
/* 241 */     cfg.setCustomAttribute(this.key, value);
/*     */   }
/*     */   
/*     */   private Environment getRequiredCurrentEnvironment() {
/* 245 */     Environment c = Environment.getCurrentEnvironment();
/* 246 */     if (c == null) {
/* 247 */       throw new IllegalStateException("No current environment");
/*     */     }
/* 249 */     return c;
/*     */   }
/*     */   
/*     */   private Configurable getScopeConfigurable(Environment env) throws Error {
/* 253 */     switch (this.scope) {
/*     */       case 0:
/* 255 */         return env;
/*     */       case 1:
/* 257 */         return env.getParent();
/*     */       case 2:
/* 259 */         return env.getParent().getParent();
/*     */     } 
/* 261 */     throw new BugException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\CustomAttribute.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */