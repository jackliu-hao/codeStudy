/*    */ package freemarker.ext.jsp;
/*    */ 
/*    */ import freemarker.ext.beans.BeansWrapper;
/*    */ import freemarker.template.TemplateHashModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import javax.servlet.jsp.PageContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ class JspContextModel
/*    */   implements TemplateHashModel
/*    */ {
/*    */   public static final int ANY_SCOPE = -1;
/*    */   public static final int PAGE_SCOPE = 1;
/*    */   public static final int REQUEST_SCOPE = 2;
/*    */   public static final int SESSION_SCOPE = 3;
/*    */   public static final int APPLICATION_SCOPE = 4;
/*    */   private final PageContext pageContext;
/*    */   private final int scope;
/*    */   
/*    */   public JspContextModel(PageContext pageContext, int scope) {
/* 46 */     this.pageContext = pageContext;
/* 47 */     this.scope = scope;
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(String key) throws TemplateModelException {
/* 52 */     Object bean = (this.scope == -1) ? this.pageContext.findAttribute(key) : this.pageContext.getAttribute(key, this.scope);
/* 53 */     return BeansWrapper.getDefaultInstance().wrap(bean);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 58 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\JspContextModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */