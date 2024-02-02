package freemarker.ext.jsp;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

/** @deprecated */
@Deprecated
public class FreemarkerTag implements BodyTag {
   private Tag parent;
   private BodyContent bodyContent;
   private PageContext pageContext;
   private SimpleHash root;
   private Template template;
   private boolean caching = true;
   private String name = "";

   public boolean getCaching() {
      return this.caching;
   }

   public void setCaching(boolean caching) {
      this.caching = caching;
   }

   public void setName(String name) {
      this.name = name == null ? "" : name;
   }

   public Tag getParent() {
      return this.parent;
   }

   public void setParent(Tag parent) {
      this.parent = parent;
   }

   public int doStartTag() {
      return 2;
   }

   public void setBodyContent(BodyContent bodyContent) {
      this.bodyContent = bodyContent;
   }

   public void setPageContext(PageContext pageContext) {
      this.pageContext = pageContext;
      this.root = null;
   }

   public void doInitBody() {
   }

   public int doAfterBody() {
      return 0;
   }

   public void release() {
      this.root = null;
      this.template = null;
      this.name = "";
   }

   public int doEndTag() throws JspException {
      if (this.bodyContent == null) {
         return 6;
      } else {
         try {
            if (this.template == null) {
               this.template = new Template(this.name, this.bodyContent.getReader());
            }

            if (this.root == null) {
               this.root = new SimpleHash();
               this.root.put("page", new JspContextModel(this.pageContext, 1));
               this.root.put("request", new JspContextModel(this.pageContext, 2));
               this.root.put("session", new JspContextModel(this.pageContext, 3));
               this.root.put("application", new JspContextModel(this.pageContext, 4));
               this.root.put("any", new JspContextModel(this.pageContext, -1));
            }

            this.template.process(this.root, this.pageContext.getOut());
         } catch (Exception var8) {
            Exception e = var8;

            try {
               this.pageContext.handlePageException(e);
            } catch (IOException | ServletException var7) {
               throw new JspException(var7.getMessage());
            }
         } finally {
            if (!this.caching) {
               this.template = null;
            }

         }

         return 6;
      }
   }
}
