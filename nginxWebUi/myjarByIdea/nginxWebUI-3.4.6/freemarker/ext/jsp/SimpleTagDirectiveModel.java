package freemarker.ext.jsp;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;
import javax.servlet.jsp.tagext.Tag;

class SimpleTagDirectiveModel extends JspTagModelBase implements TemplateDirectiveModel {
   protected SimpleTagDirectiveModel(String tagName, Class tagClass) throws IntrospectionException {
      super(tagName, tagClass);
      if (!SimpleTag.class.isAssignableFrom(tagClass)) {
         throw new IllegalArgumentException(tagClass.getName() + " does not implement either the " + Tag.class.getName() + " interface or the " + SimpleTag.class.getName() + " interface.");
      }
   }

   public void execute(Environment env, Map args, TemplateModel[] outArgs, final TemplateDirectiveBody body) throws TemplateException, IOException {
      try {
         SimpleTag tag = (SimpleTag)this.getTagInstance();
         final FreeMarkerPageContext pageContext = PageContextFactory.getCurrentPageContext();
         pageContext.pushWriter(new JspWriterAdapter(env.getOut()));

         try {
            tag.setJspContext(pageContext);
            JspTag parentTag = (JspTag)pageContext.peekTopTag(JspTag.class);
            if (parentTag != null) {
               tag.setParent(parentTag);
            }

            this.setupTag(tag, args, pageContext.getObjectWrapper());
            if (body != null) {
               tag.setJspBody(new JspFragment() {
                  public JspContext getJspContext() {
                     return pageContext;
                  }

                  public void invoke(Writer out) throws JspException, IOException {
                     try {
                        body.render((Writer)(out == null ? pageContext.getOut() : out));
                     } catch (TemplateException var3) {
                        throw new TemplateExceptionWrapperJspException(var3);
                     }
                  }
               });
               pageContext.pushTopTag(tag);

               try {
                  tag.doTag();
               } finally {
                  pageContext.popTopTag();
               }
            } else {
               tag.doTag();
            }
         } finally {
            pageContext.popWriter();
         }

      } catch (TemplateException var20) {
         throw var20;
      } catch (Exception var21) {
         throw this.toTemplateModelExceptionOrRethrow(var21);
      }
   }

   static final class TemplateExceptionWrapperJspException extends JspException {
      public TemplateExceptionWrapperJspException(TemplateException cause) {
         super("Nested content has thrown template exception", cause);
      }

      public TemplateException getCause() {
         return (TemplateException)super.getCause();
      }
   }
}
