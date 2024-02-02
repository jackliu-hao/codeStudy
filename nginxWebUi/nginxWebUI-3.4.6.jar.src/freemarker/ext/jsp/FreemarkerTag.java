/*     */ package freemarker.ext.jsp;
/*     */ 
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.Template;
/*     */ import java.io.Writer;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.jsp.JspException;
/*     */ import javax.servlet.jsp.PageContext;
/*     */ import javax.servlet.jsp.tagext.BodyContent;
/*     */ import javax.servlet.jsp.tagext.BodyTag;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class FreemarkerTag
/*     */   implements BodyTag
/*     */ {
/*     */   private Tag parent;
/*     */   private BodyContent bodyContent;
/*     */   private PageContext pageContext;
/*     */   private SimpleHash root;
/*     */   private Template template;
/*     */   private boolean caching = true;
/*  50 */   private String name = "";
/*     */   
/*     */   public boolean getCaching() {
/*  53 */     return this.caching;
/*     */   }
/*     */   
/*     */   public void setCaching(boolean caching) {
/*  57 */     this.caching = caching;
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/*  61 */     this.name = (name == null) ? "" : name;
/*     */   }
/*     */ 
/*     */   
/*     */   public Tag getParent() {
/*  66 */     return this.parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setParent(Tag parent) {
/*  71 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   
/*     */   public int doStartTag() {
/*  76 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setBodyContent(BodyContent bodyContent) {
/*  81 */     this.bodyContent = bodyContent;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPageContext(PageContext pageContext) {
/*  86 */     this.pageContext = pageContext;
/*  87 */     this.root = null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void doInitBody() {}
/*     */ 
/*     */   
/*     */   public int doAfterBody() {
/*  96 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void release() {
/* 101 */     this.root = null;
/* 102 */     this.template = null;
/* 103 */     this.name = "";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int doEndTag() throws JspException {
/* 109 */     if (this.bodyContent == null) {
/* 110 */       return 6;
/*     */     }
/*     */     try {
/* 113 */       if (this.template == null) {
/* 114 */         this.template = new Template(this.name, this.bodyContent.getReader());
/*     */       }
/*     */       
/* 117 */       if (this.root == null) {
/* 118 */         this.root = new SimpleHash();
/* 119 */         this.root.put("page", new JspContextModel(this.pageContext, 1));
/* 120 */         this.root.put("request", new JspContextModel(this.pageContext, 2));
/* 121 */         this.root.put("session", new JspContextModel(this.pageContext, 3));
/* 122 */         this.root.put("application", new JspContextModel(this.pageContext, 4));
/* 123 */         this.root.put("any", new JspContextModel(this.pageContext, -1));
/*     */       } 
/* 125 */       this.template.process(this.root, (Writer)this.pageContext.getOut());
/* 126 */     } catch (Exception e) {
/*     */       try {
/* 128 */         this.pageContext.handlePageException(e);
/* 129 */       } catch (ServletException|java.io.IOException e2) {
/* 130 */         throw new JspException(e2.getMessage());
/*     */       } 
/*     */     } finally {
/* 133 */       if (!this.caching) {
/* 134 */         this.template = null;
/*     */       }
/*     */     } 
/*     */     
/* 138 */     return 6;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\jsp\FreemarkerTag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */