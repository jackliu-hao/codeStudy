/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.TemplateDirectiveBody;
/*     */ import freemarker.template.TemplateDirectiveModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Map;
/*     */ import javax.servlet.jsp.JspContext;
/*     */ import javax.servlet.jsp.JspException;
/*     */ import javax.servlet.jsp.tagext.JspFragment;
/*     */ import javax.servlet.jsp.tagext.JspTag;
/*     */ import javax.servlet.jsp.tagext.SimpleTag;
/*     */ import javax.servlet.jsp.tagext.Tag;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SimpleTagDirectiveModel
/*     */   extends JspTagModelBase
/*     */   implements TemplateDirectiveModel
/*     */ {
/*     */   protected SimpleTagDirectiveModel(String tagName, Class<?> tagClass) throws IntrospectionException {
/*  46 */     super(tagName, tagClass);
/*  47 */     if (!SimpleTag.class.isAssignableFrom(tagClass)) {
/*  48 */       throw new IllegalArgumentException(tagClass.getName() + " does not implement either the " + Tag.class
/*  49 */           .getName() + " interface or the " + SimpleTag.class
/*  50 */           .getName() + " interface.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(Environment env, Map args, TemplateModel[] outArgs, final TemplateDirectiveBody body) throws TemplateException, IOException {
/*     */     try {
/*  60 */       SimpleTag tag = (SimpleTag)getTagInstance();
/*  61 */       final FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
/*  62 */       pageContext.pushWriter(new JspWriterAdapter(env.getOut()));
/*     */       try {
/*  64 */         tag.setJspContext((JspContext)pageContext);
/*  65 */         JspTag parentTag = (JspTag)pageContext.peekTopTag(JspTag.class);
/*  66 */         if (parentTag != null) {
/*  67 */           tag.setParent(parentTag);
/*     */         }
/*  69 */         setupTag(tag, args, pageContext.getObjectWrapper());
/*  70 */         if (body != null) {
/*  71 */           tag.setJspBody(new JspFragment()
/*     */               {
/*     */                 public JspContext getJspContext() {
/*  74 */                   return (JspContext)pageContext;
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public void invoke(Writer out) throws JspException, IOException {
/*     */                   try {
/*  80 */                     body.render((out == null) ? (Writer)pageContext.getOut() : out);
/*  81 */                   } catch (TemplateException e) {
/*  82 */                     throw new SimpleTagDirectiveModel.TemplateExceptionWrapperJspException(e);
/*     */                   } 
/*     */                 }
/*     */               });
/*  86 */           pageContext.pushTopTag(tag);
/*     */           try {
/*  88 */             tag.doTag();
/*     */           } finally {
/*  90 */             pageContext.popTopTag();
/*     */           } 
/*     */         } else {
/*  93 */           tag.doTag();
/*     */         } 
/*     */       } finally {
/*  96 */         pageContext.popWriter();
/*     */       } 
/*  98 */     } catch (TemplateException e) {
/*  99 */       throw e;
/* 100 */     } catch (Exception e) {
/* 101 */       throw toTemplateModelExceptionOrRethrow(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   static final class TemplateExceptionWrapperJspException
/*     */     extends JspException {
/*     */     public TemplateExceptionWrapperJspException(TemplateException cause) {
/* 108 */       super("Nested content has thrown template exception", (Throwable)cause);
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateException getCause() {
/* 113 */       return (TemplateException)super.getCause();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\SimpleTagDirectiveModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */