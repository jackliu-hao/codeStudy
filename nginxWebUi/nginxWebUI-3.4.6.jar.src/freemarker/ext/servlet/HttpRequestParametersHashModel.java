/*     */ package freemarker.ext.servlet;
/*     */ 
/*     */ import freemarker.template.SimpleCollection;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpRequestParametersHashModel
/*     */   implements TemplateHashModelEx
/*     */ {
/*     */   private final HttpServletRequest request;
/*     */   private List keys;
/*     */   
/*     */   public HttpRequestParametersHashModel(HttpServletRequest request) {
/*  46 */     this.request = request;
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateModel get(String key) {
/*  51 */     String value = this.request.getParameter(key);
/*  52 */     return (value == null) ? null : (TemplateModel)new SimpleScalar(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/*  57 */     return !this.request.getParameterNames().hasMoreElements();
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  62 */     return getKeys().size();
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel keys() {
/*  67 */     return (TemplateCollectionModel)new SimpleCollection(getKeys().iterator());
/*     */   }
/*     */ 
/*     */   
/*     */   public TemplateCollectionModel values() {
/*  72 */     final Iterator iter = getKeys().iterator();
/*  73 */     return (TemplateCollectionModel)new SimpleCollection(new Iterator()
/*     */         {
/*     */           public boolean hasNext()
/*     */           {
/*  77 */             return iter.hasNext();
/*     */           }
/*     */           
/*     */           public Object next() {
/*  81 */             return HttpRequestParametersHashModel.this.request.getParameter(iter.next());
/*     */           }
/*     */           
/*     */           public void remove() {
/*  85 */             throw new UnsupportedOperationException();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   protected String transcode(String string) {
/*  91 */     return string;
/*     */   }
/*     */   
/*     */   private synchronized List getKeys() {
/*  95 */     if (this.keys == null) {
/*  96 */       this.keys = new ArrayList();
/*  97 */       for (Enumeration enumeration = this.request.getParameterNames(); enumeration.hasMoreElements();) {
/*  98 */         this.keys.add(enumeration.nextElement());
/*     */       }
/*     */     } 
/* 101 */     return this.keys;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\servlet\HttpRequestParametersHashModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */