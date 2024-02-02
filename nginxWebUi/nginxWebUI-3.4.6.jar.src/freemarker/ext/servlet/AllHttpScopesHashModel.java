/*     */ package freemarker.ext.servlet;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.NullArgumentException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AllHttpScopesHashModel
/*     */   extends SimpleHash
/*     */ {
/*     */   private final ServletContext context;
/*     */   private final HttpServletRequest request;
/*  50 */   private final Map unlistedModels = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AllHttpScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request) {
/*  61 */     super(objectWrapper);
/*  62 */     NullArgumentException.check("wrapper", objectWrapper);
/*  63 */     this.context = context;
/*  64 */     this.request = request;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void putUnlistedModel(String key, TemplateModel model) {
/*  75 */     this.unlistedModels.put(key, model);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  81 */     TemplateModel model = super.get(key);
/*  82 */     if (model != null) {
/*  83 */       return model;
/*     */     }
/*     */ 
/*     */     
/*  87 */     model = (TemplateModel)this.unlistedModels.get(key);
/*  88 */     if (model != null) {
/*  89 */       return model;
/*     */     }
/*     */ 
/*     */     
/*  93 */     Object obj = this.request.getAttribute(key);
/*  94 */     if (obj != null) {
/*  95 */       return wrap(obj);
/*     */     }
/*     */ 
/*     */     
/*  99 */     HttpSession session = this.request.getSession(false);
/* 100 */     if (session != null) {
/* 101 */       obj = session.getAttribute(key);
/* 102 */       if (obj != null) {
/* 103 */         return wrap(obj);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 108 */     obj = this.context.getAttribute(key);
/* 109 */     if (obj != null) {
/* 110 */       return wrap(obj);
/*     */     }
/*     */ 
/*     */     
/* 114 */     return wrap(null);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\AllHttpScopesHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */