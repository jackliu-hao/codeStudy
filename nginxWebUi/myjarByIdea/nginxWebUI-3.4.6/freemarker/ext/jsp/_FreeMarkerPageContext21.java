package freemarker.ext.jsp;

import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.el.ELContext;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.VariableResolver;

public class _FreeMarkerPageContext21 extends FreeMarkerPageContext {
   private static final Logger LOG = Logger.getLogger("freemarker.jsp");
   private ELContext elContext;

   public _FreeMarkerPageContext21() throws TemplateModelException {
   }

   public ExpressionEvaluator getExpressionEvaluator() {
      try {
         Class type = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return Thread.currentThread().getContextClassLoader();
            }
         })).loadClass("org.apache.commons.el.ExpressionEvaluatorImpl");
         return (ExpressionEvaluator)type.newInstance();
      } catch (Exception var2) {
         throw new UnsupportedOperationException("In order for the getExpressionEvaluator() method to work, you must have downloaded the apache commons-el jar and made it available in the classpath.");
      }
   }

   public VariableResolver getVariableResolver() {
      return new VariableResolver() {
         public Object resolveVariable(String name) throws ELException {
            return _FreeMarkerPageContext21.this.findAttribute(name);
         }
      };
   }

   public ELContext getELContext() {
      if (this.elContext == null) {
         JspApplicationContext jspctx = JspFactory.getDefaultFactory().getJspApplicationContext(this.getServletContext());
         if (!(jspctx instanceof FreeMarkerJspApplicationContext)) {
            throw new UnsupportedOperationException("Can not create an ELContext using a foreign JspApplicationContext (of class " + ClassUtil.getShortClassNameOfObject(jspctx) + ").\nHint: The cause of this is often that you are trying to use JSTL tags/functions in FTL. In that case, know that that's not really suppored, and you are supposed to use FTL constrcuts instead, like #list instead of JSTL's forEach, etc.");
         }

         this.elContext = ((FreeMarkerJspApplicationContext)jspctx).createNewELContext(this);
         this.elContext.putContext(JspContext.class, this);
      }

      return this.elContext;
   }

   static {
      if (JspFactory.getDefaultFactory() == null) {
         JspFactory.setDefaultFactory(new FreeMarkerJspFactory21());
      }

      LOG.debug("Using JspFactory implementation class " + JspFactory.getDefaultFactory().getClass().getName());
   }
}
