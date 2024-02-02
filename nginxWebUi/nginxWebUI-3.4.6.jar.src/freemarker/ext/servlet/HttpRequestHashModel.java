/*     */ package freemarker.ext.servlet;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleCollection;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HttpRequestHashModel
/*     */   implements TemplateHashModelEx
/*     */ {
/*     */   private final HttpServletRequest request;
/*     */   private final HttpServletResponse response;
/*     */   private final ObjectWrapper wrapper;
/*     */   
/*     */   public HttpRequestHashModel(HttpServletRequest request, ObjectWrapper wrapper) {
/*  51 */     this(request, null, wrapper);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpRequestHashModel(HttpServletRequest request, HttpServletResponse response, ObjectWrapper wrapper) {
/*  57 */     this.request = request;
/*  58 */     this.response = response;
/*  59 */     this.wrapper = wrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) throws TemplateModelException {
/*  64 */     return this.wrapper.wrap(this.request.getAttribute(key));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  69 */     return !this.request.getAttributeNames().hasMoreElements();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  74 */     int result = 0;
/*  75 */     for (Enumeration enumeration = this.request.getAttributeNames(); enumeration.hasMoreElements(); ) {
/*  76 */       enumeration.nextElement();
/*  77 */       result++;
/*     */     } 
/*  79 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() {
/*  84 */     ArrayList keys = new ArrayList();
/*  85 */     for (Enumeration enumeration = this.request.getAttributeNames(); enumeration.hasMoreElements();) {
/*  86 */       keys.add(enumeration.nextElement());
/*     */     }
/*  88 */     return (TemplateCollectionModel)new SimpleCollection(keys.iterator());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() {
/*  93 */     ArrayList<Object> values = new ArrayList();
/*  94 */     for (Enumeration<String> enumeration = this.request.getAttributeNames(); enumeration.hasMoreElements();) {
/*  95 */       values.add(this.request.getAttribute(enumeration.nextElement()));
/*     */     }
/*  97 */     return (TemplateCollectionModel)new SimpleCollection(values.iterator(), this.wrapper);
/*     */   }
/*     */   
/*     */   public HttpServletRequest getRequest() {
/* 101 */     return this.request;
/*     */   }
/*     */   
/*     */   public HttpServletResponse getResponse() {
/* 105 */     return this.response;
/*     */   }
/*     */   
/*     */   public ObjectWrapper getObjectWrapper() {
/* 109 */     return this.wrapper;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\HttpRequestHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */