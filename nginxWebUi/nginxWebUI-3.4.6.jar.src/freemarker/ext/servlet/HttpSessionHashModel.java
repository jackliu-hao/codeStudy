/*     */ package freemarker.ext.servlet;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.io.Serializable;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
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
/*     */ public final class HttpSessionHashModel
/*     */   implements TemplateHashModel, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private transient HttpSession session;
/*     */   private final transient ObjectWrapper wrapper;
/*     */   private final transient FreemarkerServlet servlet;
/*     */   private final transient HttpServletRequest request;
/*     */   private final transient HttpServletResponse response;
/*     */   
/*     */   public HttpSessionHashModel(HttpSession session, ObjectWrapper wrapper) {
/*  53 */     this.session = session;
/*  54 */     this.wrapper = wrapper;
/*     */     
/*  56 */     this.servlet = null;
/*  57 */     this.request = null;
/*  58 */     this.response = null;
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
/*     */   public HttpSessionHashModel(FreemarkerServlet servlet, HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
/*  73 */     this.wrapper = wrapper;
/*     */     
/*  75 */     this.servlet = servlet;
/*  76 */     this.request = request;
/*  77 */     this.response = response;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  82 */     checkSessionExistence();
/*  83 */     return this.wrapper.wrap((this.session != null) ? this.session.getAttribute(key) : null);
/*     */   }
/*     */   
/*     */   private void checkSessionExistence() throws TemplateModelException {
/*  87 */     if (this.session == null && this.request != null) {
/*  88 */       this.session = this.request.getSession(false);
/*  89 */       if (this.session != null && this.servlet != null) {
/*     */         try {
/*  91 */           this.servlet.initializeSessionAndInstallModel(this.request, this.response, this, this.session);
/*     */         }
/*  93 */         catch (RuntimeException e) {
/*  94 */           throw e;
/*  95 */         } catch (Exception e) {
/*  96 */           throw new TemplateModelException(e);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean isOrphaned(HttpSession currentSession) {
/* 103 */     return ((this.session != null && this.session != currentSession) || (this.session == null && this.request == null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() throws TemplateModelException {
/* 110 */     checkSessionExistence();
/* 111 */     return (this.session == null || !this.session.getAttributeNames().hasMoreElements());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\HttpSessionHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */