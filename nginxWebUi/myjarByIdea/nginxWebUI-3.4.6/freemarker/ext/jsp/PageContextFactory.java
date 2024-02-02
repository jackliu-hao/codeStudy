package freemarker.ext.jsp;

import freemarker.core.Environment;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.UndeclaredThrowableException;
import javax.servlet.jsp.PageContext;

class PageContextFactory {
   private static final Class pageContextImpl = getPageContextImpl();

   private static Class getPageContextImpl() {
      try {
         try {
            PageContext.class.getMethod("getELContext", (Class[])null);
            return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext21");
         } catch (NoSuchMethodException var3) {
            try {
               PageContext.class.getMethod("getExpressionEvaluator", (Class[])null);
               return Class.forName("freemarker.ext.jsp._FreeMarkerPageContext2");
            } catch (NoSuchMethodException var2) {
               throw new IllegalStateException("Since FreeMarker 2.3.24, JSP support requires at least JSP 2.0.");
            }
         }
      } catch (ClassNotFoundException var4) {
         throw new NoClassDefFoundError(var4.getMessage());
      }
   }

   static FreeMarkerPageContext getCurrentPageContext() throws TemplateModelException {
      Environment env = Environment.getCurrentEnvironment();
      TemplateModel pageContextModel = env.getGlobalVariable("javax.servlet.jsp.jspPageContext");
      if (pageContextModel instanceof FreeMarkerPageContext) {
         return (FreeMarkerPageContext)pageContextModel;
      } else {
         try {
            FreeMarkerPageContext pageContext = (FreeMarkerPageContext)pageContextImpl.newInstance();
            env.setGlobalVariable("javax.servlet.jsp.jspPageContext", pageContext);
            return pageContext;
         } catch (IllegalAccessException var3) {
            throw new IllegalAccessError(var3.getMessage());
         } catch (InstantiationException var4) {
            throw new UndeclaredThrowableException(var4);
         }
      }
   }
}
