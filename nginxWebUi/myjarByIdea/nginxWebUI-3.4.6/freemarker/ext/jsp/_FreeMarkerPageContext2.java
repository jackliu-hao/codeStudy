package freemarker.ext.jsp;

import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

public class _FreeMarkerPageContext2 extends FreeMarkerPageContext {
   private static final Logger LOG = Logger.getLogger("freemarker.jsp");

   public _FreeMarkerPageContext2() throws TemplateModelException {
   }

   public ExpressionEvaluator getExpressionEvaluator() {
      try {
         Class type = Thread.currentThread().getContextClassLoader().loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
         return (ExpressionEvaluator)type.newInstance();
      } catch (Exception var2) {
         throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
      }
   }

   public VariableResolver getVariableResolver() {
      return new VariableResolver() {
         public Object resolveVariable(String name) throws ELException {
            return _FreeMarkerPageContext2.this.findAttribute(name);
         }
      };
   }

   public void include(String path, boolean flush) throws IOException, ServletException {
      super.include(path);
   }

   static {
      if (JspFactory.getDefaultFactory() == null) {
         JspFactory.setDefaultFactory(new FreeMarkerJspFactory2());
      }

      LOG.debug("Using JspFactory implementation class " + JspFactory.getDefaultFactory().getClass().getName());
   }
}
